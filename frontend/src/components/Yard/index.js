import * as _ from "lodash";
import * as moment from "moment";
import React, { useEffect, useRef, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { Rating } from "react-simple-star-rating";
import { Slide } from "react-slideshow-image";

import { toast, ToastContainer } from "react-toastify";
import empty from "../../assets/images/empty.png";
import noData from "../../assets/images/no-data.jpg";
import { EMPTY, TOAST_CONFIG } from "../../constants/default";
import { INTERNAL_SERVER_ERROR } from "../../constants/error-message";
import { decrypt, encryptKey } from "../../helpers/crypto.helper";
import VoucherStorageModal from "../../modals/VoucherStorageModal";
import { calculateBookingList } from "../../services/voucher.service";
import {
  bookingYard,
  getSlots,
  getYardById
} from "../../services/yard.service";
import DisableElement from "../DisableElement";
import Modal, { useModal } from "../Modal";
import Reviews from "../Reviews";
import "./style.scss";

import ReportYardModal from "../../modals/ReportYardModal";

function Yard() {
  const { id } = useParams();
  const container = useRef(null);
  const [yard, setYard] = useState(null);
  const [booking, setBooking] = useState([]);
  const [total, setTotal] = useState(0);
  const [originalTotal, setOriginalTotal] = useState(0);
  const navigate = useNavigate();
  const location = useLocation();
  const [slideImages, setSlideImages] = useState([]);
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedSubYard, setSelectedSubYard] = useState(null);
  const [isLoadingSlots, setIsLoadingSlots] = useState(false);
  const [slots, setSlots] = useState([]);
  const [isLoadingVoucher, setIsLoadingVoucher] = useState(false);
  const [voucherCode, setVoucherCode] = useState(EMPTY);
  const [isBooking, setIsBooking] = useState(false);
  const [showVoucherStorageModal, toggleShowVoucherStorageModal] = useModal();
  const [showReportYardModal, toggleShowReportYardModal] = useModal();
  const credential = localStorage.getItem(encryptKey("credential"));

  useEffect(() => {
    getYardById(id).then((res) => {
      setYard(res.data);

      let images;
      if (res.data.images.length === 3) {
        setSlideImages(res.data.images);
      } else {
        images = _.cloneDeep(res.data.images) ?? [];
        while (images.length !== 3) {
          images.push(yard);
        }
        setSlideImages(images);
      }
    });
    container.current.scrollIntoView({ block: "start" });
  }, []);

  useEffect(() => {
    if (selectedDate && selectedSubYard) {
      setIsLoadingSlots(true);
      getSlots(selectedSubYard, selectedDate)
        .then((res) => {
          if (res) {
            res.data.forEach((slot) => {
              slot.isSelected = _.find(booking, { id: slot.id }) ? true : false;
            });
            setSlots(res.data);
          }
        })
        .finally(() => {
          setIsLoadingSlots(false);
        });
    } else {
      if (isLoadingSlots) {
        setIsLoadingSlots(false);
      }
    }
  }, [selectedDate, selectedSubYard]);

  useEffect(() => {
    if (voucherCode) {
      calculateBookingList(voucherCode, booking).then((res) => {
        let newTotal = 0;
        setOriginalTotal(total);
        setBooking(
          res.bookingList.map((item) => {
            newTotal += item.price;
            const found = _.find(booking, { slotId: item.slotId });
            return { ...found, ..._.pick(item, ["price", "originalPrice"]) };
          })
        );
        setTotal(newTotal);
      });
    } else {
      setTotal(originalTotal);
      setBooking(
        booking.map((item) => {
          const found = _.find(booking, { slotId: item.slotId });
          return {
            ...found,
            price: item.originalPrice || item.price,
            originalPrice: 0,
          };
        })
      );
      setOriginalTotal(0);
    }
  }, [voucherCode]);

  const onBooking = () => {
    if (!credential) {
      toast.error("Login to continue booking!", TOAST_CONFIG);
      localStorage.setItem(encryptKey("returnUrl"), location.pathname);
      navigate("/auth/login");
    } else {
      setIsBooking(true);
      bookingYard(
        yard.id,
        {
          voucherCode: voucherCode || null,
          bookingList: booking,
        },
        decrypt(credential).token
      )
        .then((res) => {
          if (res) {
            setIsLoadingSlots(true);
            getSlots(selectedSubYard, selectedDate)
              .then((res) => {
                if (res) {
                  setSlots(res.data);
                }
              })
              .finally(() => {
                setIsLoadingSlots(false);
              });
            setBooking([]);
            setTotal(0);
            setVoucherCode(EMPTY);
            setOriginalTotal(0);
            toast.success("Booking successfully.", TOAST_CONFIG);
          }
        })
        .catch((error) => {
          if (error.response.status === 409) {
            toast.error("Some of your booking slots are conflicted.", TOAST_CONFIG);
            navigate("/history");
          } else {
            toast.error(
              error.response.data.message || INTERNAL_SERVER_ERROR,
              TOAST_CONFIG
            );
          }
        })
        .finally(() => {
          setIsBooking(false);
        });
    }
  };

  const onRemoveBooking = (item) => {
    if (!voucherCode) {
      const slot = _.find(slots, { id: item.slotId });
      slot.isSelected = false;
      setBooking(booking.filter((b) => b.slotId !== item.slotId));
      setTotal(total - item.price);
    } else {
      toast.info("Remove voucher to change booking list!", TOAST_CONFIG);
    }
  };

  const onSelectSlot = (slot) => {
    if (!voucherCode && !slot.isBooked) {
      slot.isSelected = !slot.isSelected;
      if (slot.isSelected) {
        const subYard = _.find(yard.subYards, { id: slot.refSubYard });
        const newBooking = {
          ..._.omit(slot, ["isBooked", "isSelected", "id"]),
          date: moment(selectedDate).format("DD/MM/yyyy"),
          yardName: subYard.name,
          yardType: subYard.typeYard,
          slotId: slot.id,
          originalPrice: slot.price,
        };
        setBooking([newBooking, ...booking]);
        setTotal(total + slot.price);
      } else {
        const newBookingList = booking.filter(
          (item) => item.slotId !== slot.id
        );
        setBooking(newBookingList);
        setTotal(total - slot.price);
      }
    } else if (voucherCode) {
      toast.info("Remove voucher to change booking list!", TOAST_CONFIG);
    }
  };

  return (
    <div className="w-100 yard-container mt-4 container pb-5">
      <div className="mb-5" style={{ color: "white" }} ref={container}>
        .
      </div>
      {!yard && (
        <div className="w-100 d-flex justify-content-center align-items-center loading-height">
          <DisableElement />
        </div>
      )}
      {yard && (
        <>
          <div className="row flex-row">
            <div className="col-8">
              <Slide>
                {slideImages.map((slideImage, index) => (
                  <div className="each-slide" key={index}>
                    <div
                      style={{ backgroundImage: `url(${slideImage})` }}
                    ></div>
                  </div>
                ))}
              </Slide>
            </div>
            <div className="col-4 ps-4 pe-4 pt-5 flex-column position-relative">
              <div className="text-center mb-4">
                <b className="size-2 d-block mb-2">{yard.name}</b>
                <Rating
                  ratingValue={yard.score}
                  allowHalfIcon={true}
                  readonly={true}
                />
              </div>
              <div className="row mb-1 yard__details-field">
                <span className="col-3 fw-bolder">Address:</span>
                <span className="col-9">{yard.address}</span>
              </div>
              <div className="row mb-1 yard__details-field">
                <span className="col-3 fw-bolder">Open:</span>
                <span className="col-9">
                  {yard.openAt} - {yard.closeAt}
                </span>
              </div>
              {!!credential && (
                <div className="report-ic-box">
                  <i
                    className="fas fa-exclamation-circle report-icon"
                    title="Report this yard"
                    onClick={() => {
                      toggleShowReportYardModal();
                    }}
                  ></i>
                </div>
              )}
            </div>
          </div>
          <div className="row justify-content-center my-5">
            <div className="col-7 pt-4">
              <div className="text-center mb-4 row">
                <div className="row p-2 col-6 justify-content-end size-1 ps-3">
                  <label htmlFor="play-date" className="text-start">
                    Play Date
                  </label>
                  <span className="col-1 lh-44 signup__icon-wrapper booking__icon-wrapper">
                    <i className="far fa-clock"></i>
                  </span>
                  <input
                    id="play-date"
                    className="col-10 outline-none p-2 signup__input-border"
                    type="date"
                    placeholder="Date"
                    value={selectedDate}
                    onChange={(e) => {
                      setSelectedDate(e.target.value);
                    }}

                    required
                    disabled={voucherCode}
                  />
                </div>
                <div className="row p-2 col-6 justify-content-end size-1 ps-3">
                  <label htmlFor="play-date" className="text-start">
                    Pick Yard
                  </label>
                  <span className="col-1 lh-44 signup__icon-wrapper booking__icon-wrapper">
                    <i className="fas fa-columns"></i>
                  </span>
                  <select
                    id="sub-yard"
                    className="col-10 outline-none p-2 signup__input-border"
                    style={{ backgroundColor: "white" }}
                    onChange={(e) => {
                      setSelectedSubYard(e.target.value);
                    }}
                    disabled={voucherCode}
                  >
                    <option value="" disabled selected>
                      Select yard
                    </option>
                    {yard.subYards.map((sub) => (
                      <option
                        key={sub.id}
                        value={sub.id}
                      >{`${sub.name} - (${sub.typeYard})`}</option>
                    ))}
                  </select>
                </div>
                <div className="row d-flex align-items-center mt-1">
                  <div className="col-3 size-2">Slots</div>
                  <div className="col-3 map-color">
                    <div className="color"></div>
                    <span>Available</span>
                  </div>
                  <div className="col-3 map-color">
                    <div
                      className="color"
                      style={{ backgroundColor: "#444444" }}
                    ></div>
                    <span>Booked</span>
                  </div>
                  <div className="col-3 map-color">
                    <div
                      className="color"
                      style={{ backgroundColor: "#3E9834" }}
                    ></div>
                    <span>Selecting</span>
                  </div>
                </div>
              </div>
              <div
                className={!voucherCode ? "row ps-2" : "row ps-2 div-disabled"}
              >
                {slots &&
                  slots.map((slot) => (
                    <div key={slot.id} className="col-3 slot-details-container">
                      <div
                        className={
                          slot.isBooked
                            ? "slot-details flex-column booked-slot"
                            : slot.isSelected
                              ? "slot-details-clicked flex-column"
                              : "slot-details flex-column"
                        }
                        onClick={() => onSelectSlot(slot)}
                      >
                        <p>
                          <b>
                            {slot.startTime} - {slot.endTime}
                          </b>
                        </p>
                        <p>
                          <i>{slot.price} VND</i>
                        </p>
                      </div>
                    </div>
                  ))}
                {isLoadingSlots && !slots.length && (
                  <div className="w-100 d-flex justify-content-center pt-5">
                    <DisableElement />
                  </div>
                )}
                {(!selectedDate || !selectedSubYard) && !slots.length && (
                  <div className="w-100 pt-5 d-flex justify-content-center align-items-center flex-column">
                    <i className="fas fa-hand-pointer size-3"></i>
                    <p className="text-center nodata-text">
                      Please select date and sub yard
                    </p>
                  </div>
                )}
                {selectedDate &&
                  selectedSubYard &&
                  !isLoadingSlots &&
                  !slots.length && (
                    <div className="w-100 pt-5 d-flex justify-content-center align-items-center flex-column">
                      <img src={empty} style={{ width: 100 }} />
                      <p className="text-center nodata-text">
                        No result available
                      </p>
                    </div>
                  )}
              </div>
            </div>
            <div className="col-5 ps-4 pe-4 flex-column pt-4">
              <h4 className="text-center mb-3">Booking</h4>
              <div className="row p-2 mb-4">
                <label
                  htmlFor="voucher"
                  className="text-start"
                  style={{ paddingLeft: 0 }}
                >
                  Voucher Code
                </label>
                <span className="col-1 lh-44 signup__icon-wrapper">
                  <i className="fas fa-money-check-alt"></i>
                </span>
                <input
                  id="voucher"
                  name="voucher"
                  className="col-9 outline-none p-2 fg-pw__input-border"
                  type="text"
                  placeholder="Enter voucher code"
                  value={voucherCode}
                  readOnly
                />
                <button
                  id="voucher-btn"
                  className="col-2 lh-44 fg-pw__icon-wrapper"
                  onClick={() => {
                    if (!voucherCode) {
                      toggleShowVoucherStorageModal();
                    } else {
                      setVoucherCode("");
                    }
                  }}
                  disabled={!booking.length}
                >
                  {voucherCode ? "Remove" : "Select"}
                </button>
              </div>
              <div className="matches-container">
                {!booking.length && (
                  <div className=" row justify-content-center align-items-center">
                    <img
                      className="nodata-img"
                      src={noData}
                      alt="No data availble"
                    />
                    <p className="text-center nodata-text">
                      No booking available
                    </p>
                  </div>
                )}
                {booking.map((item, index) => (
                  <div key={index} className="match-container row mb-2">
                    <div className="col-1 basket__img-container d-flex justify-content-center align-items-center ps-3">
                      <p>
                        <i
                          className="far fa-trash-alt trash-icon"
                          title="Remove"
                          onClick={() => {
                            onRemoveBooking(item);
                          }}
                        ></i>
                      </p>
                    </div>
                    <div className="col-4 basket__img-container d-flex justify-content-center align-items-center">
                      <p className="text-center">
                        <b>
                          {item.startTime} - {item.endTime}
                        </b>
                        <br />
                        <span style={{ fontSize: "0.9rem" }}>{item.date}</span>
                      </p>
                    </div>
                    <div className="p-3 ps-0 pe-1 col-4 d-flex justify-content-center align-items-center">
                      <p>
                        <b>{item.yardName}</b> - ({item.yardType})
                      </p>
                    </div>
                    <div className="col-3 basket__img-container d-flex justify-content-center align-items-center  text-center">
                      <p>
                        <b>{item.price} VND</b>
                        <br />
                        {!!item.originalPrice && voucherCode && (
                          <span
                            className="original-price"
                            style={{ fontSize: "0.9rem" }}
                          >
                            {item.originalPrice} VND
                          </span>
                        )}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
              <div className="pt-3 pb-3 row">
                <div className="col-3">Total</div>
                <div className="col-9 text-end">
                  <b>{total} VND</b>
                  <br />
                  {!!originalTotal && voucherCode && (
                    <span
                      className="original-price"
                      style={{ fontSize: "0.9rem" }}
                    >
                      {originalTotal} VND
                    </span>
                  )}
                </div>
              </div>
              <div className="pt-3 pb-3 justify-content-around">
                <button
                  className="btn btn-primary p-2 w-100"
                  onClick={onBooking}
                  disabled={!booking.length || isLoadingVoucher || isBooking}
                >
                  {isBooking ? (
                    <div className="dots-loading">
                      <div></div>
                      <div></div>
                      <div></div>
                      <div></div>
                    </div>
                  ) : (
                    "Book"
                  )}
                </button>
              </div>
            </div>
          </div>
          <Reviews yardId={id} />
        </>
      )}
      <Modal
        isShowing={showVoucherStorageModal}
        hide={toggleShowVoucherStorageModal}
      >
        <VoucherStorageModal
          toggleModal={toggleShowVoucherStorageModal}
          ownerId={yard?.ownerId}
          onSelect={(code) => {
            setVoucherCode(code);
          }}
        />
      </Modal>
      <Modal isShowing={showReportYardModal} hide={toggleShowReportYardModal}>
        <ReportYardModal
          toggleModal={toggleShowReportYardModal}
          yardId={yard?.id}
        />
      </Modal>
      <ToastContainer />
    </div>
  );
}

export default Yard;
