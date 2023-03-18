import { useEffect, useState } from "react";
import { EMPTY } from "rxjs";

import Modal, { useModal } from "../Modal";

import { toast } from "react-toastify";
import empty from "../../assets/images/empty.png";
import { TOAST_CONFIG } from "../../constants/default";
import CancelBookingModal from "../../modals/CancelBookingModal";
import {
  getOwnerSlots,
  getSimpleYardDetails,
  getSlotDetails
} from "../../services/owner.service";
import DisableElement from "../DisableElement";
import "./style.scss";

function MatchManagementWidget() {
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedSubYard, setSelectedSubYard] = useState(EMPTY);
  const [selectedYard, setSelectedYard] = useState(null);
  const [isLoadingSlots, setIsLoadingSlots] = useState(false);
  const [isLoadingInfo, setIsLoadingInfo] = useState(false);
  const [slots, setSlots] = useState([]);
  const [yards, setYards] = useState([]);
  const [subYards, setSubYards] = useState([]);
  const [data, setData] = useState(null);
  const [showCancelBookingModal, toggleShowCancelBookingModal] = useModal();
  const [bookingId, setBookingId] = useState(EMPTY);

  useEffect(() => {
    getSimpleYardDetails().then((res) => {
      setYards(res);
    });
  }, []);

  useEffect(() => {
    if (selectedYard) {
      const yard = yards.find((y) => y.yardId === selectedYard);
      if (yard) {
        setSelectedSubYard(yard.subYards[0]?.subYardId);
        setSubYards(yard.subYards);
      }
    } else {
      setSelectedSubYard(EMPTY);
    }
  }, [selectedYard]);

  useEffect(() => {
    if (
      selectedDate &&
      selectedSubYard &&
      typeof selectedSubYard === "string"
    ) {
      setIsLoadingSlots(true);
      getOwnerSlots(selectedSubYard, selectedDate)
        .then((res) => {
          if (res) {
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

  const getSlots = () => {
    setIsLoadingSlots(true);
    getOwnerSlots(selectedSubYard, selectedDate)
      .then((res) => {
        if (res) {
          setSlots(res.data);
        }
      })
      .finally(() => {
        setIsLoadingSlots(false);
      });
  };

  const onSelectSlot = (slot) => {
    setIsLoadingInfo(true);
    getSlotDetails(selectedSubYard, slot.id, selectedDate)
      .then((res) => {
        setData(res.userId ? res : null);
        if (!res.userId) {
          toast.info("This slot has not been booked yet!", TOAST_CONFIG);
        }
      })
      .finally(() => {
        setIsLoadingInfo(false);
      });
  };

  const isLaterThanCurrentTime = (dataTime) => {
    const now = new Date();
    const toStringCurrentTime = now.toLocaleTimeString("en-US", {
      hour12: false,
      hour: "numeric",
      minute: "numeric",
      timeZone: "ASIA/Ho_Chi_Minh",
    });
    return dataTime.localeCompare(toStringCurrentTime) > 0;
  };

  return (
    <div className="pt-4 mt-5 w-100 row justify-content-center">
      <Modal
        isShowing={showCancelBookingModal}
        hide={toggleShowCancelBookingModal}
      >
        <CancelBookingModal
          toggleModal={toggleShowCancelBookingModal}
          bookingId={bookingId}
          onSave={() => getSlots()}
        />
      </Modal>
      <div className="col-7">
        <div className="text-center mb-4 row">
          <div className="row p-2 col-4 justify-content-end size-1 ps-3">
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
            />
          </div>
          <div className="row p-2 col-4 justify-content-end size-1 ps-3">
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
                setSelectedYard(e.target.value);
              }}
              value={selectedYard}
            >
              <option value="" disabled selected>
                Select yard
              </option>
              {yards.map((yard) => (
                <option key={yard.yardId} value={yard.yardId}>
                  {yard.yardName}
                </option>
              ))}
            </select>
          </div>
          <div className="row p-2 col-4 justify-content-end size-1 ps-3">
            <label htmlFor="play-date" className="text-start">
              Pick Sub Yard
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
              disabled={!selectedYard}
              value={selectedSubYard}
            >
              <option value="" disabled selected>
                Select sub yard
              </option>
              {subYards.map((sub) => (
                <option
                  key={sub.subYardId}
                  value={sub.subYardId}
                >{`${sub.subYardName} - (${sub.typeYard})`}</option>
              ))}
            </select>
          </div>
        </div>
        <div className="row d-flex align-items-center mb-3">
          <div className="col-6 size-2">Slots</div>
          <div className="col-3 map-color">
            <div className="color"></div>
            <span>Available</span>
          </div>
          <div className="col-3 map-color">
            <div className="color" style={{ backgroundColor: "#444444" }}></div>
            <span>Booked</span>
          </div>
        </div>
        <div className="row ps-2">
          {slots &&
            slots.map((slot) => (
              <div key={slot.id} className="col-3 slot-details-container">
                <div
                  className={
                    slot.isBooked
                      ? "slot-details flex-column booked-bg"
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
          {(!selectedDate ||
            !selectedSubYard ||
            typeof selectedSubYard !== "string") &&
            !slots.length && (
              <div className="w-100 pt-5 d-flex justify-content-center align-items-center flex-column">
                <i className="fas fa-hand-pointer size-3"></i>
                <p className="text-center nodata-text">
                  Please select date, yard and sub yard
                </p>
              </div>
            )}
          {selectedDate &&
            selectedSubYard &&
            !isLoadingSlots &&
            !slots.length &&
            typeof selectedSubYard === "string" && (
              <div className="w-100 pt-5 d-flex justify-content-center align-items-center flex-column">
                <img src={empty} style={{ width: 100 }} />
                <p className="text-center nodata-text">No result available</p>
              </div>
            )}
        </div>
      </div>
      <div className="col-5 ps-4 pe-4 flex-column">
        <h4 className="text-center mb-4">Booking Information</h4>
        {data && !isLoadingInfo && (
          <div className="px-3">
            <div className="row mb-1 yard__details-field">
              <span className="col-3 fw-bolder">Yard:</span>
              <span className="col-9">{data.yardName}</span>
            </div>
            <div className="row mb-1 yard__details-field">
              <span className="col-3 fw-bolder">Play Time:</span>
              <span className="col-9">
                {data.startTime} - {data.endTime}
              </span>
            </div>
            <div className="row mb-1 yard__details-field">
              <span className="col-3 fw-bolder">Address:</span>
              <span className="col-9">{data.address}</span>
            </div>
            <div className="row mb-1 yard__details-field">
              <span className="col-3 fw-bolder">Price:</span>
              <span className="col-9">{data.price} VND</span>
            </div>
            <div className="row mb-1 yard__details-field">
              <span className="col-3 fw-bolder">Sub Yard:</span>
              <span className="col-9">{data.subYardName}</span>
            </div>
            <div className="row mb-1 yard__details-field">
              <span className="col-3 fw-bolder">Yard Type:</span>
              <span className="col-9">{data.typeYard}</span>
            </div>
            <div className="row mb-1 yard__details-field">
              <span className="col-3 fw-bolder">User:</span>
              <span className="col-9">{data.userName}</span>
            </div>
            <div className="row mb-1 yard__details-field">
              <span className="col-3 fw-bolder">Email:</span>
              <span className="col-9">{data.email}</span>
            </div>
            <div className="row mb-1 yard__details-field">
              <span className="col-3 fw-bolder">Booked At:</span>
              <span className="col-9">{data.bookedTime}</span>
            </div>

            <div className="row mb-1">
              <button
                type="button"
                className="btn btn-danger py-2 mt-2"
                style={{ borderRadius: 10 }}
                onClick={() => {
                  setBookingId(data.bookingId);
                  toggleShowCancelBookingModal();
                }}
              >
                Cancel Booking
              </button>
            </div>

          </div>
        )}
        {!data && !isLoadingInfo && (
          <div className="w-100 pt-5 d-flex justify-content-center align-items-center flex-column mt-5">
            <img src={empty} style={{ width: 70 }} />
            <p className="text-center nodata-text">No booking yet!</p>
          </div>
        )}
        {isLoadingInfo && (
          <div className="d-flex justify-content-center align-content-center h-300 mt-5 pt-5">
            <DisableElement />
          </div>
        )}
      </div>
    </div>
  );
}

export default MatchManagementWidget;
