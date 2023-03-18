import * as _ from "lodash";
import { useEffect, useState } from "react";
import { confirmAlert } from "react-confirm-alert";
import { Link, useNavigate, useParams } from "react-router-dom";
import { toast, ToastContainer } from "react-toastify";

import empty from "../../assets/images/empty.png";
import yard from "../../assets/images/nodata.jpeg";
import { EMPTY, TOAST_CONFIG } from "../../constants/default";
import { INTERNAL_SERVER_ERROR } from "../../constants/error-message";
import {
  END,
  MIN_PERIOD,
  PERIODS,
  START,
  TIMELINE
} from "../../constants/time";
import { YARD_TYPES } from "../../constants/type";
import { decrypt, encrypt, encryptKey } from "../../helpers/crypto.helper";
import UpdateSubYardModal from "../../modals/UpdateSubYardModal";
import {
  getAllProvinces,
  getDistrictsByProvinceId
} from "../../services/location.service";
import {
  activateSubYard,
  addNewYard,
  deactivateSubYard,
  deleteSubYard,
  getYardDetailById,
  updateYard
} from "../../services/yard.service";
import DisableScreen from "../DisableScreen";
import Modal, { useModal } from "../Modal";
import "./style.scss";

const _URL = window.URL || window.webkitURL;

function YardDetails() {
  const [provinces, setProvinces] = useState([]);
  const [districts, setDistricts] = useState([]);
  const [selectedProvince, setSelectedProvince] = useState(EMPTY);
  const [selectedDistrict, setSelectedDistrict] = useState(EMPTY);
  const [isLoadingProvinces, setIsLoadingProvinces] = useState(false);
  const [isLoadingDistricts, setIsLoadingDistricts] = useState(false);
  const [firstChangeTimeSlot, setFirstChangeTimeSlot] = useState(false);
  const [timeSlot, setTimeSlot] = useState({
    start: START,
    end: END,
    period: MIN_PERIOD,
  });
  const [yardPictures, setYardPictures] = useState([
    { file: null, src: EMPTY },
    { file: null, src: EMPTY },
    { file: null, src: EMPTY },
  ]);
  const [basicData, setBasicData] = useState({ name: EMPTY, address: EMPTY });
  const [subYards, setSubYards] = useState([]);
  const [defaultPrice, setDefaultPrice] = useState(0);
  const [defaultSlots, setDefaultSlots] = useState([]);
  const [isAppliedDefaultPrice, setIsAppliedDefaultPrice] = useState(false);
  const [showUpdateSubYardModal, toggleShowUpdateSubYardModal] = useModal();
  const [selectedSubYard, setSelectedSubYard] = useState(null);
  const [isAddingYard, setIsAddingYard] = useState(false);
  const navigate = useNavigate();
  const { id } = useParams();

  useEffect(() => {
    if (isNaN(defaultPrice) || defaultPrice - 1 < 0) {
      setDefaultPrice("");
    }
  }, [defaultPrice]);

  useEffect(() => {
    updateDefaulSlots();
  }, [timeSlot]);

  const updateDefaulSlots = () => {
    const slots = [];
    let start = Number(timeSlot.start),
      end = Number(timeSlot.end),
      period = Number(timeSlot.period);

    while (start < end) {
      slots.push({
        startTime: TIMELINE.find((t) => t.value === start).label,
        endTime: TIMELINE.find((t) => t.value === start + period).label,
        price: isAppliedDefaultPrice ? defaultPrice : 0,
      });
      start += period;
    }

    if (firstChangeTimeSlot) {
      setSubYards(
        subYards.map((sub) => {
          return { ...sub, slots: _.cloneDeep(slots) };
        })
      );
    }

    setDefaultSlots(slots);
  };

  const onUpdateSubYard = (yard) => {
    setSelectedSubYard(yard ? yard : null);
    toggleShowUpdateSubYardModal();
  };

  const onUpdateSubYardList = (yard) => {
    if (!yard.id) {
      if (yard.isUpdate) {
        setSubYards([
          yard,
          ...subYards.filter((item) => item.ref !== yard.ref),
        ]);
      } else {
        setSubYards([yard, ...subYards]);
      }
    } else {
      setSubYards([yard, ...subYards.filter((item) => item.id !== yard.id)]);
    }
  };

  const fetchYard = () => {
    setIsAddingYard(true);
    getYardDetailById(id)
      .then((res) => {
        const addressDetail = res.address.split(", ");
        setBasicData({
          name: res.name,
          address: addressDetail.splice(0, addressDetail.length - 2).join(", "),
        });
        setSelectedProvince(res.provinceId);
        setSelectedDistrict(res.districtId);
        setTimeSlot({
          start: TIMELINE.find((t) => t.label === res.openTime).value,
          end: TIMELINE.find((t) => t.label === res.closeTime).value,
          period: TIMELINE.find((t) => t.label === res.duration).value,
        });
        res.subYards = res.subYards.map((sub) => {
          return {
            ...sub,
            type: YARD_TYPES.find((t) => t.lable === sub.typeYard).value,
          };
        });
        setSubYards(res.subYards);
        setYardPictures(
          res.images.map((url) => {
            return { newImage: null, src: url, currentImage: url };
          })
        );
      })
      .catch((error) => {
        navigate("/not-found");
      })
      .finally(() => {
        setIsAddingYard(false);
      });
  };

  useEffect(() => {
    if (id !== "draft") {
      fetchYard();
    }

    setIsLoadingProvinces(true);
    const storedProvinces = localStorage.getItem(encryptKey("provinces"));
    if (!storedProvinces) {
      return async () => {
        await getAllProvinces()
          .then((res) => {
            if (res) {
              localStorage.setItem(encryptKey("provinces"), encrypt(res));
              setProvinces(res);
            }
          })
          .catch((error) => {
            toast.error(INTERNAL_SERVER_ERROR, TOAST_CONFIG);
          })
          .finally(() => {
            setIsLoadingProvinces(false);
          });
      };
    } else {
      setProvinces(decrypt(storedProvinces));
      setIsLoadingProvinces(false);
    }
    setTimeSlot({ ...timeSlot, start: START });
  }, []);

  useEffect(() => {
    setIsLoadingDistricts(true);
    if (selectedProvince) {
      const storedDistricts = localStorage.getItem(encryptKey("districts"));
      if (storedDistricts) {
        const districtsSet = decrypt(storedDistricts);
        if (districtsSet.hasOwnProperty(selectedProvince)) {
          setIsLoadingDistricts(false);
          return setDistricts(districtsSet[selectedProvince]);
        }
      }
      fetchDistricts();
    } else {
      setDistricts([]);
      setIsLoadingDistricts(false);
    }
  }, [selectedProvince]);

  useEffect(() => {
    setDefaultSlots(
      defaultSlots.map((slot) => {
        return { ...slot, price: isAppliedDefaultPrice ? defaultPrice : 0 };
      })
    );
  }, [isAppliedDefaultPrice]);

  const fetchDistricts = () => {
    const storedDistricts = localStorage.getItem(encryptKey("districts"));
    getDistrictsByProvinceId(selectedProvince)
      .then((res) => {
        if (res) {
          if (storedDistricts) {
            localStorage.setItem(
              encryptKey("districts"),
              encrypt({
                ...decrypt(storedDistricts),
                [selectedProvince]: res,
              })
            );
          } else {
            localStorage.setItem(
              encryptKey("districts"),
              encrypt({
                [selectedProvince]: res,
              })
            );
          }
          setDistricts(res);
        }
      })
      .catch((error) => {
        toast.error(INTERNAL_SERVER_ERROR, TOAST_CONFIG);
      })
      .finally(() => {
        setIsLoadingDistricts(false);
      });
  };

  const handleDisableYard = (yard) => {
    deactivateSubYard(yard.id)
      .then(() => {
        toast.success(
          `Deactivate yard "${yard.name}" successfully.`,
          TOAST_CONFIG
        );
        fetchYard();
      })
      .catch((error) => {
        toast.error(
          error.response.data.message || INTERNAL_SERVER_ERROR,
          TOAST_CONFIG
        );
      });
  };

  const handleEnableYard = (yard) => {
    activateSubYard(yard.id)
      .then(() => {
        toast.success(
          `Activate yard "${yard.name}" successfully.`,
          TOAST_CONFIG
        );
        fetchYard();
      })
      .catch((error) => {
        toast.error(
          error.response.data.message || INTERNAL_SERVER_ERROR,
          TOAST_CONFIG
        );
      });
  };

  const handleDeleteClick = (yard) => {
    deleteSubYard(yard.id)
      .then(() => {
        toast.success(`Delete yard "${yard.name}" successfully.`, TOAST_CONFIG);
        fetchYard();
      })
      .catch((error) => {
        toast.error(
          error.response.data.message || INTERNAL_SERVER_ERROR,
          TOAST_CONFIG
        );
      });
  };

  const onSimpleClick = (title, question, callback) => {
    confirmAlert({
      customUI: ({ onClose }) => {
        return (
          <div className="custom-confirm">
            <h4>{title}</h4>
            <p className="mb-3">{question}</p>
            <button
              className="btn btn-primary me-3"
              onClick={() => {
                callback();
                onClose();
              }}
            >
              Confirm
            </button>
            <button onClick={onClose} className="btn btn-light">
              Cancel
            </button>
          </div>
        );
      },
      closeOnEscape: true,
      closeOnClickOutside: true,
    });
  };

  const saveYard = async () => {
    const data = {
      name: basicData.name,
      address:
        basicData.address +
        `, ${districts.find((d) => d.id === Number(selectedDistrict)).districtName
        }, ${provinces.find((p) => p.id === Number(selectedProvince)).provinceName
        }`,
      districtId: selectedDistrict,
      openAt: TIMELINE.find((item) => item.value === Number(timeSlot.start))
        .label,
      closeAt: TIMELINE.find((item) => item.value === Number(timeSlot.end))
        .label,
      slotDuration: PERIODS.find(
        (item) => item.value === Number(timeSlot.period)
      ).label,
      subYards: subYards.map((sub) => {
        return {
          ..._.pick(sub, ["id", "name", "type", "slots"]),
        };
      }),
    };

    for (const sub of data.subYards) {
      if (checkSlotOfSubYard(sub)) {
        toast.error(
          `Sub yard "${sub.name}" has one or more zero-price slots!`,
          TOAST_CONFIG
        );
        return;
      }
    }

    setIsAddingYard(true);
    try {
      if (id === "draft") {
        await addNewYard({
          images: yardPictures.map((picture) => picture.file),
          yard: data,
        });
        navigate("/owner/yards");
      } else {
        await updateYard(
          {
            images: yardPictures
              .filter((picture) => picture.isUpdate)
              .map((picture) => picture.currentImage),
            newImages: yardPictures
              .filter((picture) => picture.isUpdate)
              .map((picture) => picture.newImage),
            yard: data,
          },
          id
        );
        fetchYard();
        setIsAppliedDefaultPrice(false);
        setDefaultPrice("");
      }
      toast.success("Save yard successfully.", TOAST_CONFIG);
    } catch (error) {
      toast.error(
        error.response.data.message || INTERNAL_SERVER_ERROR,
        TOAST_CONFIG
      );
    } finally {
      setIsAddingYard(false);
    }
  };

  const checkSlotOfSubYard = (subYard) => {
    return subYard.slots.some((slot) => Number(slot.price) === 0);
  };

  const uploadImage = (position, e) => {
    const cloned = _.cloneDeep(yardPictures);
    const file = e.target.files[0];
    cloned[position] = {
      ...cloned[position],
      file,
      src: _URL.createObjectURL(file),
      newImage: file,
    };

    if (id !== "draft") {
      cloned[position] = { ...cloned[position], isUpdate: true };
    }
    setYardPictures(cloned);
  };

  return (
    <div className="mt-5">
      {isAddingYard && <DisableScreen />}
      <h4>{id !== "draft" ? "Yard Details" : "Create Yard"}</h4>
      <div className="d-flex">
        <form className="my-3 col-3 mw-410">
          <div className="row p-2 py-1">
            <label htmlFor="name" style={{ paddingLeft: 0 }}>
              Name
            </label>
            <span className="col-1 lh-44 signup__icon-wrapper" title="Name">
              <i className="fas fa-address-card"></i>
            </span>
            <input
              id="name"
              className="col-11 outline-none p-2 signup__input-border"
              type="text"
              placeholder="Name"
              name="name"
              value={basicData.name}
              onChange={(e) =>
                setBasicData({ ...basicData, [e.target.name]: e.target.value })
              }
            />
          </div>
          <div className="row p-2 py-1">
            <label htmlFor="provinceId" style={{ paddingLeft: 0 }}>
              Province
            </label>
            <span className="col-1 lh-44 signup__icon-wrapper" title="Province">
              <i className="far fa-map"></i>
            </span>
            <select
              id="provinceId"
              className="col-11 outline-none p-2 signup__input-border"
              style={{ backgroundColor: "white" }}
              onChange={(e) => setSelectedProvince(() => e.target.value)}
              disabled={isLoadingProvinces}
              name="provinceId"
              value={selectedProvince}
            >
              <option value="" key="NO_PROVINCE">
                {isLoadingProvinces ? "Loading..." : "Select province"}
              </option>
              {provinces.map((province) => (
                <option value={province.id} key={province.id}>
                  {province.provinceName}
                </option>
              ))}
            </select>
          </div>
          <div className="row p-2 py-1">
            <label htmlFor="districtId" style={{ paddingLeft: 0 }}>
              District
            </label>
            <span className="col-1 lh-44 signup__icon-wrapper" title="District">
              <i className="fas fa-map-marker-alt"></i>
            </span>
            <select
              id="districtId"
              className="col-11 outline-none p-2 signup__input-border"
              style={{ backgroundColor: "white" }}
              onChange={(e) => {
                setSelectedDistrict(e.target.value);
              }}
              disabled={isLoadingDistricts || !selectedProvince}
              name="districtId"
              value={selectedDistrict}
            >
              <option value="" key="NO_DISTRICT">
                {isLoadingDistricts ? "Loading..." : "Select district"}
              </option>
              {districts.map((district) => (
                <option value={district.id} key={district.id}>
                  {district.districtName}
                </option>
              ))}
            </select>
          </div>
          <div className="row p-2 py-1">
            <label htmlFor="address" style={{ paddingLeft: 0 }}>
              Address Details
            </label>
            <span className="col-1 lh-44 signup__icon-wrapper" title="Address">
              <i className="fas fa-map-pin"></i>
            </span>
            <input
              id="address"
              name="address"
              className="col-11 outline-none p-2 signup__input-border"
              type="text"
              placeholder="Address details"
              value={basicData.address}
              onChange={(e) =>
                setBasicData({ ...basicData, [e.target.name]: e.target.value })
              }
            />
          </div>
          <div className="row p-2 py-1">
            <label htmlFor="open" style={{ paddingLeft: 0 }}>
              Open Time
            </label>
            <span
              className="col-1 lh-44 signup__icon-wrapper"
              title="Open Time"
            >
              <i className="fas fa-clock"></i>
            </span>
            <select
              id="open"
              name="open"
              className="col-11 outline-none p-2 signup__input-border"
              style={{ backgroundColor: "white" }}
              onChange={(e) => {
                setTimeSlot({ ...timeSlot, start: e.target.value });
                if (!firstChangeTimeSlot) {
                  setFirstChangeTimeSlot(true);
                }
              }}
              value={timeSlot.start}
            >
              {TIMELINE.map((time) => (
                <option value={time.value} key={time.value}>
                  {time.label}
                </option>
              ))}
            </select>
          </div>
          <div className="row p-2 py-1">
            <label htmlFor="close" style={{ paddingLeft: 0 }}>
              Close Time
            </label>
            <span
              className="col-1 lh-44 signup__icon-wrapper"
              title="Close Time"
            >
              <i className="fas fa-clock"></i>
            </span>
            <select
              id="close"
              name="close"
              className="col-11 outline-none p-2 signup__input-border"
              style={{ backgroundColor: "white" }}
              onChange={(e) => {
                setTimeSlot({ ...timeSlot, end: e.target.value });
                if (!firstChangeTimeSlot) {
                  setFirstChangeTimeSlot(true);
                }
              }}
              value={timeSlot.end}
            >
              {TIMELINE.map((time) => (
                <option value={time.value} key={time.value}>
                  {time.label}
                </option>
              ))}
            </select>
          </div>
          <div className="row p-2 py-1">
            <label htmlFor="duration" style={{ paddingLeft: 0 }}>
              Duration
            </label>
            <span className="col-1 lh-44 signup__icon-wrapper" title="Duration">
              <i className="fas fa-hourglass-half"></i>
            </span>
            <select
              id="duration"
              name="duration"
              className="col-11 outline-none p-2 signup__input-border"
              style={{ backgroundColor: "white" }}
              onChange={(e) => {
                setTimeSlot({ ...timeSlot, period: e.target.value });
                if (!firstChangeTimeSlot) {
                  setFirstChangeTimeSlot(true);
                }
              }}
              value={timeSlot.period}
            >
              {PERIODS.filter(
                (value) => (timeSlot.end - timeSlot.start) % value.value === 0
              ).map((time) => (
                <option value={time.value} key={time.value}>
                  {time.label}
                </option>
              ))}
            </select>
          </div>
          <div className="row p-2">
            <label
              htmlFor="defaultPrice"
              className="text-start"
              style={{ paddingLeft: 0 }}
            >
              Default Price
            </label>
            <span className="col-1 signup__icon-wrapper">
              <i className="fas fa-dollar-sign"></i>
            </span>
            <input
              id="defaultPrice"
              name="defaultPrice"
              className="col-9 outline-none p-2 fg-pw__input-border"
              type="text"
              placeholder="Default price"
              value={defaultPrice}
              onChange={(e) => setDefaultPrice(Number(e.target.value))}
              disabled={isAppliedDefaultPrice}
            />
            <span
              className="col-2 fg-pw__icon-wrapper"
              onClick={() => {
                if (isAppliedDefaultPrice) {
                  setDefaultPrice("");
                }
                setIsAppliedDefaultPrice(!isAppliedDefaultPrice);
              }}
            >
              {isAppliedDefaultPrice ? "Remove" : "Apply"}
            </span>
          </div>
        </form>
        <div className="flex-1 ps-4">
          <div className="row h-50">
            <div className="col-4 p-1">
              <div
                className="upload__img-wrapper mb-2 color-blur"
                style={{
                  backgroundImage: `url(${yardPictures[0]?.["src"] ?? yard})`,
                  backgroundSize: "cover",
                }}
              >
                {!yardPictures[0]?.["src"] && id === "draft" && "Intro image"}
              </div>
              <input
                className="outline-none custom-bg-input p-0 w-100"
                type="file"
                onChange={(e) => {
                  uploadImage(0, e);
                }}
              />
            </div>
            <div className="col-4 p-1">
              <div
                className="upload__img-wrapper mb-2 color-blur"
                style={{
                  backgroundImage: `url(${yardPictures[1]?.["src"] ?? yard})`,
                  backgroundSize: "cover",
                }}
              >
                {!yardPictures[1]?.["src"] && id === "draft" && "Intro image"}
              </div>
              <input
                className="outline-none custom-bg-input p-0 w-100"
                type="file"
                onChange={(e) => {
                  uploadImage(1, e);
                }}
              />
            </div>
            <div className="col-4 p-1">
              <div
                className="upload__img-wrapper mb-2 color-blur"
                style={{
                  backgroundImage: `url(${yardPictures[2]?.["src"] ?? yard})`,
                  backgroundSize: "cover",
                }}
              >
                {!yardPictures[2]?.["src"] && id === "draft" && "Intro image"}
              </div>
              <input
                className="outline-none custom-bg-input p-0 w-100"
                type="file"
                onChange={(e) => {
                  uploadImage(2, e);
                }}
              />
            </div>
          </div>
          <div className="pt-4">
            <h4 className="d-inline-block">Sub Yards</h4>
            <button
              className="btn btn-primary px-4 ms-5"
              onClick={() => onUpdateSubYard()}
            >
              <i
                className="fas fa-plus me-2"
                style={{ fontSize: "0.8rem" }}
              ></i>
              <b>Add</b>
            </button>
            <div className="overflow-y-auto mt-3 h-250">
              {!subYards.length && (
                <div className="w-100 pt-5 d-flex justify-content-center align-items-center flex-column">
                  <img src={empty} style={{ width: 80 }} />
                  <p
                    className="text-center nodata-text"
                    style={{ fontSize: "0.9rem" }}
                  >
                    No sub yard available
                  </p>
                </div>
              )}
              {!!subYards.length && (
                <table className="table table-striped mt-1">
                  <thead>
                    <tr>
                      <th scope="col" style={{ width: "12%" }}>
                        Actions
                      </th>
                      <th scope="col">Reference</th>
                      <th scope="col">Name</th>
                      <th scope="col">Type</th>
                      <th scope="col">Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {subYards.map((item, index) => {
                      return item.id ? (
                        <tr key={item.id}>
                          <td>
                            <i
                              className="trash-icon fas fa-trash-alt col-4"
                              title="Delete"
                              onClick={() =>
                                onSimpleClick(
                                  "Delete",
                                  "Are you sure to delete this yard permanently?",
                                  () => handleDeleteClick(item)
                                )
                              }
                            ></i>
                            {item.isActive ? (
                              <i
                                className="trash-icon fas fa-ban col-4"
                                title="Deactive"
                                onClick={() =>
                                  onSimpleClick(
                                    "Disable",
                                    "Are you sure to disable this yard?",
                                    () => handleDisableYard(item)
                                  )
                                }
                              ></i>
                            ) : (
                              <i
                                className="trash-icon fas fa-check-circle col-4"
                                title="Active"
                                onClick={() =>
                                  onSimpleClick(
                                    "Enable",
                                    "Are you sure to activate this yard?",
                                    () => handleEnableYard(item)
                                  )
                                }
                              ></i>
                            )}
                          </td>
                          <td>
                            <b
                              className="trash-icon"
                              onClick={() => onUpdateSubYard(item)}
                            >
                              {item.reference}
                            </b>
                          </td>
                          <td className="text-truncate" title="Sân quận 9">
                            {item.name}
                          </td>
                          <td>{item.typeYard}</td>
                          <td
                            className={
                              item.isActive ? "green bold" : "red bold"
                            }
                          >
                            {item.isActive ? "ACTIVE" : "INACTIVE"}
                          </td>
                        </tr>
                      ) : (
                        <tr key={index}>
                          <td>
                            <i
                              className="trash-icon fas fa-times col-4"
                              title="Delete"
                              onClick={() => {
                                setSubYards([
                                  ...subYards.filter(
                                    (yard) => yard.ref !== item.ref
                                  ),
                                ]);
                              }}
                            ></i>
                            <i
                              className="trash-icon fas fa-edit col-4"
                              title="Edit"
                              onClick={() => onUpdateSubYard(item)}
                            ></i>
                          </td>
                          <td>
                            <b className="fsi fwl">Draft</b>
                          </td>
                          <td
                            className="text-truncate fsi fwl"
                            title="Sân quận 9"
                          >
                            {item.name || "N/A"}
                          </td>
                          <td className="fsi fwl">
                            {
                              YARD_TYPES.find(
                                (type) => type.value === item.type
                              ).lable
                            }
                          </td>
                          <td className="fsi fwl">N/A</td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              )}
            </div>
          </div>
        </div>
      </div>
      <button className="btn btn-primary me-3 px-4" onClick={saveYard}>
        Save
      </button>
      <Link to="/owner/yards">
        <button className="btn btn-light">Back</button>
      </Link>
      <Modal
        isShowing={showUpdateSubYardModal}
        hide={toggleShowUpdateSubYardModal}
      >
        <UpdateSubYardModal
          toggleModal={toggleShowUpdateSubYardModal}
          slots={defaultSlots}
          yard={selectedSubYard}
          onUpdateSubYardList={onUpdateSubYardList}
          firstChangeTimeSlot={firstChangeTimeSlot}
        />
      </Modal>
      <ToastContainer />
    </div>
  );
}

export default YardDetails;
