import { toast, ToastContainer } from "react-toastify";

import { useEffect, useState } from "react";
import empty from "../../assets/images/empty.png";
import voucher1 from "../../assets/images/voucher-1.png";
import voucherImg from "../../assets/images/voucher.png";
import DisableElement from "../../components/DisableElement";
import Pagination from "../../components/Pagination";
import { TOAST_CONFIG } from "../../constants/default";
import { VOUCHER_TYPE } from "../../constants/voucher";
import { searchVouchers } from "../../services/voucher.service";
import "./style.scss";

const VoucherStorageModal = ({ toggleModal, ownerId, onSelect }) => {
  const ITEMS_PER_PAGE = 6;
  const [isLoading, setIsLoading] = useState(false);
  const [vouchers, setVouchers] = useState([]);
  const [code, setCode] = useState("");
  const [maxPage, setMaxPage] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);

  const copyToClipBoard = (value) => {
    navigator.clipboard.writeText(value);
    toast.info("Copy to clipboard", TOAST_CONFIG);
  };

  const fetchVouchers = (page = 1, itemsPerPage = ITEMS_PER_PAGE) => {
    setIsLoading(true);
    searchVouchers({ page, itemsPerPage }, ownerId)
      .then((res) => {
        const { page, maxResult, vouchers } = res;
        setVouchers(vouchers);
        setMaxPage(
          maxResult % ITEMS_PER_PAGE === 0 && maxResult !== 0
            ? maxResult / ITEMS_PER_PAGE
            : Math.floor(maxResult / ITEMS_PER_PAGE) + 1
        );
      })
      .finally(() => setIsLoading(false));
  };

  const onApply = () => {
    onSelect(code);
  };

  useEffect(() => {
    fetchVouchers(1);
  }, []);

  const onChangePage = (page) => {
    setCurrentPage(page);
    fetchVouchers(page)
  };

  return (
    <div
      className="p-5 voucher-storage"
      style={{
        height: 700,
      }}
    >
      <h4 className="text-center mb-4 size-2">
        <img src={voucher1} alt="Transaction" className="width-60 pe-3" />
        Available Vouchers
      </h4>
      <div className="row mt-5 yard__result-container" style={{ height: 420, overflow: "auto" }}>
        {isLoading && (
          <div className="w-100 d-flex justify-content-center align-items-center">
            <DisableElement />
          </div>
        )}
        {!isLoading &&
          !!vouchers?.length &&
          vouchers?.map((voucher) =>
            voucher.type === VOUCHER_TYPE.CASH ? (
              <div
                className="voucher-wrapper col-4 p-3 pe-2"
                key={voucher.voucherCode}
              >
                <div
                  className={
                    voucher.voucherCode === code
                      ? "yard-result voucher-wrapper-selected"
                      : "yard-result"
                  }
                  onClick={() => {
                    setCode(voucher.voucherCode);
                  }}
                >
                  <img src={voucherImg} alt="basketball yard" />
                  <div className="yard-details p-3">
                    <b className="d-block mb-2">
                      Discount {voucher.discount} VND
                    </b>
                    <p
                      className="row mb-1"
                      title="Effective date"
                      style={{ fontSize: "0.9rem" }}
                    >
                      <i className="fas fa-clock col-1 pt-1"></i>
                      <span className="ps-2 color-blur col-10">
                        {voucher.startDate} - {voucher.endDate}
                      </span>
                    </p>
                    <p className="row mb-1">
                      <i
                        className="trash-icon fas fa-copy col-1 pt-1"
                        title="Copy"
                        onClick={() => copyToClipBoard(voucher.voucherCode)}
                      ></i>
                      <span
                        className="ps-2 bold col-10 voucher-code"
                        value={voucher.voucherCode}
                      >
                        {voucher.voucherCode}
                      </span>
                    </p>
                    <p
                      className="row mt-2 ps-2"
                      title={`${(voucher.usages * 100) / voucher.maxQuantity
                        }% already in use`}
                    >
                      <progress
                        id="file"
                        value={voucher.usages}
                        max={voucher.maxQuantity}
                      >
                        {" "}
                        {(voucher.usages * 100) / voucher.maxQuantity}%{" "}
                      </progress>
                    </p>
                    <p className="row mb-1" style={{ fontSize: "0.8rem" }}>
                      <span className="ps-2 color-blur col-10">
                        Already used{" "}
                        {(voucher.usages * 100) / voucher.maxQuantity}%
                      </span>
                    </p>
                  </div>
                </div>
              </div>
            ) : (
              <div
                className="voucher-wrapper col-4 p-3 pe-2"
                key={voucher.voucherCode}
              >
                <div
                  className={
                    voucher.voucherCode === code
                      ? "yard-result voucher-wrapper-selected"
                      : "yard-result"
                  }
                  onClick={() => {
                    setCode(voucher.voucherCode);
                  }}
                >
                  <img src={voucherImg} alt="basketball yard" />
                  <div className="yard-details p-3">
                    <b className="d-block mb-2">Discount {voucher.discount}%</b>
                    <p
                      className="row mb-1"
                      title="Effective date"
                      style={{ fontSize: "0.9rem" }}
                    >
                      <i className="fas fa-clock col-1 pt-1"></i>
                      <span className="ps-2 color-blur col-10">
                        {voucher.startDate} - {voucher.endDate}
                      </span>
                    </p>
                    <p className="row mb-1">
                      <i
                        className="trash-icon fas fa-copy col-1 pt-1"
                        title="Copy"
                        onClick={() => copyToClipBoard(voucher.voucherCode)}
                      ></i>
                      <span
                        className="ps-2 bold col-10 voucher-code"
                        value={voucher.voucherCode}
                      >
                        {voucher.voucherCode}
                      </span>
                    </p>
                    <p
                      className="row mt-2 ps-2"
                      title={`${(voucher.usages * 100) / voucher.maxQuantity
                        }% already in use`}
                    >
                      <progress
                        id="file"
                        value={voucher.usages}
                        max={voucher.maxQuantity}
                      >
                        {" "}
                        ${(voucher.usages * 100) / voucher.maxQuantity}%{" "}
                      </progress>
                    </p>
                    <p className="row mb-1" style={{ fontSize: "0.8rem" }}>
                      <span className="ps-2 color-blur col-10">
                        Already used{" "}
                        {(voucher.usages * 100) / voucher.maxQuantity}%
                      </span>
                    </p>
                  </div>
                </div>
              </div>
            )
          )}
        {!isLoading &&
          ITEMS_PER_PAGE - vouchers?.length > 0 &&
          !!vouchers?.length &&
          [...Array(ITEMS_PER_PAGE - vouchers?.length).keys()].map((item) => (
            <div className="voucher-wrapper col-4 p-3 pe-2" key={item}></div>
          ))}
        {!isLoading && !vouchers?.length && (
          <div className="w-100 d-flex justify-content-center align-items-center flex-column">
            <img src={empty} style={{ width: 80 }} />
            <p
              className="text-center nodata-text"
              style={{ fontSize: "0.9rem" }}
            >
              No voucher available
            </p>
          </div>
        )}
      </div>
      <Pagination maxPage={maxPage} onChangePage={onChangePage} />
      <button
        className="btn btn-primary me-3 px-4"
        disabled={!code}
        onClick={() => {
          onApply();
          toggleModal();
        }}
      >
        Apply
      </button>
      <button onClick={toggleModal} className="btn btn-light">
        Cancel
      </button>
      <ToastContainer />
    </div>
  );
};

export default VoucherStorageModal;
