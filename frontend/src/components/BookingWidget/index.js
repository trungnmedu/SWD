import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Rating } from "react-simple-star-rating";
import { toast, ToastContainer } from "react-toastify";

import noData from "../../assets/images/no-data.jpg";
import yard1 from "../../assets/images/nodata.jpeg";
import { EMPTY, TOAST_CONFIG } from "../../constants/default";
import { INTERNAL_SERVER_ERROR } from "../../constants/error-message";
import { decrypt, encrypt, encryptKey } from "../../helpers/crypto.helper";
import {
  getAllProvinces,
  getDistrictsByProvinceId
} from "../../services/location.service";
import { searchYard } from "../../services/yard.service";
import DisableElement from "../DisableElement";
import Pagination from "../Pagination";
import "./style.scss";

function BookingWidget() {
  const ITEMS_PER_PAGE = 8;

  const [provinces, setProvinces] = useState([]);
  const [districts, setDistricts] = useState([]);
  const [selectedProvince, setSelectedProvince] = useState(EMPTY);
  const [selectedDistrict, setSelectedDistrict] = useState(EMPTY);
  const [yards, setYards] = useState([]);
  const [maxPage, setMaxPage] = useState(1);
  const [isLoadingProvinces, setIsLoadingProvinces] = useState(false);
  const [isLoadingDistricts, setIsLoadingDistricts] = useState(false);
  const [isLoadingYard, setIsLoadingYard] = useState(true);

  useEffect(() => {
    filterYard();
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

  const filterYard = (
    provinceId = EMPTY,
    districtId = EMPTY,
    page = 1,
    itemsPerPage = ITEMS_PER_PAGE
  ) => {
    if (!isLoadingYard) {
      setIsLoadingYard(true);
    }

    searchYard({ provinceId, districtId, page, itemsPerPage })
      .then((res) => {
        if (res) {
          setYards(res.yards);
          setMaxPage(
            res.maxResult % ITEMS_PER_PAGE === 0 && res.maxResult !== 0
              ? res.maxResult / ITEMS_PER_PAGE
              : Math.floor(res.maxResult / ITEMS_PER_PAGE) + 1
          );
        }
      })
      .catch((error) => {
        toast.error(
          error.response?.data.message || INTERNAL_SERVER_ERROR,
          TOAST_CONFIG
        );
      })
      .finally(() => {
        setIsLoadingYard(false);
      });
  };

  const onChangePage = (page) => {
    filterYard(selectedProvince, selectedDistrict, page);
  };

  return (
    <div>
      <h1 className="booking-title p-4 text-center">BOOKING NOW!</h1>
      <div className="w-100 p-5">
        <form className="row justify-content-center w-100">
          <div className="row p-2 col-3 justify-content-center px-0">
            <label htmlFor="province">Province</label>
            <span className="col-2 lh-44 signup__icon-wrapper booking__icon-wrapper">
              <i className="far fa-map"></i>
            </span>
            <select
              id="province"
              className="col-9 outline-none p-2 signup__input-border"
              style={{ backgroundColor: "white" }}
              onChange={(e) => {
                setSelectedProvince(e.target.value);
              }}
              disabled={isLoadingProvinces}
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
          <div className="row p-2 col-3 justify-content-center px-0">
            <label htmlFor="district">District</label>
            <span className="col-2 lh-44 signup__icon-wrapper booking__icon-wrapper">
              <i className="fas fa-map-marker-alt"></i>
            </span>
            <select
              id="district"
              className="col-9 outline-none p-2 signup__input-border"
              style={{ backgroundColor: "white" }}
              onChange={(e) => {
                setSelectedDistrict(e.target.value);
              }}
              disabled={isLoadingDistricts || !selectedProvince}
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
          <div className="d-flex flex-column p-2 col-1 justify-content-end ps-4">
            <button
              className="btn btn-primary w-100 p-2"
              style={{ height: "40px" }}
              onClick={(e) => {
                e.preventDefault();
                filterYard(selectedProvince, selectedDistrict);
              }}
              disabled={!selectedProvince}
            >
              <i className="fas fa-search"></i>
            </button>
          </div>
        </form>
        <div className="row mt-5 yard__result-container">
          {isLoadingYard && (
            <div className="w-100 d-flex justify-content-center">
              <DisableElement />
            </div>
          )}
          {!isLoadingYard &&
            yards?.map((yard) => (
              <div
                className="yard__result-wrapper col-3 p-3 pe-2"
                key={yard.id}
              >
                <Link className="yard-result" to={`/yard/${yard.id}`}>
                  <img src={yard.images[0] || yard1} alt="basketball yard" />
                  <div className="yard-details p-3">
                    <b className="d-block">{yard.name}</b>
                    <Rating
                      ratingValue={yard.score}
                      allowHalfIcon={true}
                      readonly={true}
                      size={20}
                      className="mb-2"
                    />
                    <p className="row mb-1">
                      <i className="fas fa-clock col-1 pt-1"></i>
                      <span className="ps-2 color-blur col-10">
                        {yard.openAt} - {yard.closeAt}
                      </span>
                    </p>
                    <p className="row mb-1">
                      <i className="fas fa-map-marker-alt col-1 pt-1"></i>
                      <span className="ps-2 color-blur col-10">
                        {yard.address}
                      </span>
                    </p>
                  </div>
                </Link>
              </div>
            ))}
          {!yards?.length && !isLoadingYard && (
            <div className=" row justify-content-center align-items-center">
              <img className="nodata-img" src={noData} alt="No data availble" />
              <p className="text-center nodata-text">No result available</p>
            </div>
          )}
        </div>
        <Pagination maxPage={maxPage} onChangePage={onChangePage} />
      </div>
      <ToastContainer />
    </div>
  );
}

export default BookingWidget;
