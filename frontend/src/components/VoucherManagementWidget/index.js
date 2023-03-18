import React, { useEffect, useState } from "react";
import { confirmAlert } from "react-confirm-alert";

import { toast } from "react-toastify";
import coupon from "../../assets/images/coupon.png";
import empty from "../../assets/images/empty.png";
import { TOAST_CONFIG } from "../../constants/default";
import { INTERNAL_SERVER_ERROR } from "../../constants/error-message";
import { VOUCHER_STATUS, VOUCHER_TYPE } from "../../constants/voucher";
import VoucherDetailsModal from "../../modals/VoucherDetailsModal";
import { getAllVouchers, saveVoucherChanges } from "../../services/voucher.service";
import DisableElement from "../DisableElement";
import Modal, { useModal } from "../Modal";
import Pagination from "../Pagination";
import SearchBar from "../SearchBar";
import "./style.scss";

const sortableFields = [
  { value: "voucherCode", label: "Voucher Code" },
  { value: "reference", label: "Reference" },
  { value: "startDate", label: "Start Date" },
  { value: "endDate", label: "End Date" },
  { value: "maxQuantity", label: "Quantity" },
];

const filterableFields = [
  {
    label: "Status",
    options: [
      { value: "expired", label: "Expired" },
      { value: "active", label: "Active" },
      { value: "inactive", label: "Inactive" },
      { value: "full", label: "Full" }
    ],
    field: "status",
  },
  {
    label: "Type",
    options: [
      { value: "percent", label: "Percent (%)" },
      { value: "cash", label: "Cash (VND)" },
    ],
    field: "type",
  },
];

const messageKey = "MANAGE_VOUCHER_LIST";

function VoucherManagementWidget() {
  const ITEMS_PER_PAGE = 10;
  const [isLoading, setIsLoading] = useState(false);
  const [maxPage, setMaxPage] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);
  const [criteria, setCriteria] = useState({});

  const [vouchers, setVouchers] = useState([]);
  const [voucherSelected, setVoucherSelected] = useState();
  const [voucherTypeCreate, setVoucherTypeCreate] = useState();
  const [toggleCreateVoucherModal, setToggleCreateVoucherModal] = useModal(false);

  const handleToggleVoucherModal = () => {
    setToggleCreateVoucherModal(state => !state);
  }

  const fetchVouchers = async (
    page = 1,
    itemsPerPage = ITEMS_PER_PAGE,
    criteria = {}
  ) => {
    try {
      setIsLoading(true);
      const response = await getAllVouchers({ page, itemsPerPage, ...criteria });

      const { maxResult, vouchers } = response;
      setMaxPage(
        maxResult % ITEMS_PER_PAGE === 0 && maxResult !== 0
          ? maxResult / ITEMS_PER_PAGE
          : Math.floor(maxResult / ITEMS_PER_PAGE) + 1
      );
      setVouchers(vouchers);
      setIsLoading(false)

    } catch (error) {
      toast.error(INTERNAL_SERVER_ERROR, TOAST_CONFIG)
      setIsLoading(false)
    }
  }

  useEffect(() => {
    fetchVouchers()
  }, [])

  const onChangePage = (page) => {
    fetchVouchers(page, ITEMS_PER_PAGE, criteria);
    setCurrentPage(page);
  };

  const handleSearchBar = (criteria) => {
    fetchVouchers(1, ITEMS_PER_PAGE, criteria);
    setCriteria(criteria || {});
  };

  const onSimpleClick = (title, question, voucher, callback) => {
    confirmAlert({
      customUI: ({ onClose }) => {
        return (
          <div className="custom-confirm">
            <h4>{title}</h4>
            <p className="mb-3">{question}</p>
            <button
              className="btn btn-primary me-3"
              onClick={async () => {
                await callback(voucher);
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

  const showVoucherDetailsModal = (voucher) => {
    setVoucherTypeCreate(undefined)
    setVoucherSelected(voucher)
    handleToggleVoucherModal()
  }

  const handleDisableVoucher = async (voucher) => {
    const voucherValues = {
      ...voucher,
      "isActive": false,
      "status": VOUCHER_STATUS.INACTIVE
    }

    try {
      const response = await saveVoucherChanges(voucherValues);
      toast.success(response.message, TOAST_CONFIG)
      fetchVouchers()
    } catch (error) {
      toast.error("Disable voucher failed!", TOAST_CONFIG)
    }
  };

  const handleEnableVoucher = async (voucher) => {
    const voucherValues = {
      ...voucher,
      "isActive": true,
      "status": VOUCHER_STATUS.ACTIVE
    }

    try {
      const response = await saveVoucherChanges(voucherValues);
      toast.success(response.message, TOAST_CONFIG)
      fetchVouchers()
    } catch (error) {
      toast.error("Enable voucher failed!", TOAST_CONFIG)
    }
  };

  const handleDeleteVoucher = async (voucher) => {
    const voucherValues = {
      ...voucher,
      "isActive": false,
      "status": VOUCHER_STATUS.DELETED
    }

    try {
      const response = await saveVoucherChanges(voucherValues);
      toast.success(response.message, TOAST_CONFIG)
      fetchVouchers()
    } catch (error) {
      toast.error("Deleted voucher failed!", TOAST_CONFIG)
    }
  };

  return (
    <div className="pt-4 mt-5 w-100">
      <h4 className="mb-4 d-inline-block">
        <img src={coupon} alt="Yard" className="width-60 pe-3" />
        Voucher Management
      </h4>
      <button
        className="btn btn-primary px-4 ms-5"
        onClick={() => {
          setVoucherTypeCreate(VOUCHER_TYPE.PERCENT)
          setVoucherSelected(undefined);
          handleToggleVoucherModal();
        }}
      >
        <i className="fas fa-plus me-2" style={{ fontSize: "0.8rem" }}></i>
        <b>Voucher %</b>
      </button>
      <button
        className="btn btn-primary px-4 ms-3"
        onClick={() => {
          setVoucherTypeCreate(VOUCHER_TYPE.CASH)
          setVoucherSelected(undefined);
          handleToggleVoucherModal();
        }}
      >
        <i className="fas fa-plus me-2" style={{ fontSize: "0.8rem" }}></i>
        <b>Voucher $</b>
      </button>
      <SearchBar
        sortableFields={sortableFields}
        filterableFields={filterableFields}
        onSearch={handleSearchBar}
        messageKey={messageKey}
      />
      {isLoading ? (
        <div
          className="w-100 d-flex justify-content-center align-items-center"
          style={{ height: "50vh" }}
        >
          <DisableElement />
        </div>
      ) : !vouchers.length ? (
        <div className="w-100 pt-5 d-flex justify-content-center align-items-center flex-column h-300">
          <img src={empty} alt="No result." style={{ width: 80 }} />
          <p className="text-center nodata-text" style={{ fontSize: "0.9rem" }}>
            No result available
          </p>
        </div>
      ) : (
        <table className="table table-striped">
          <thead>
            <tr>
              <th scope="col" style={{ width: "8%" }}>
                Actions
              </th>
              <th scope="col" style={{ width: "10%" }}>Reference</th>
              <th scope="col">Title</th>
              <th scope="col">Code</th>
              <th scope="col" style={{ width: "8%" }}>Amount</th>
              <th scope="col" style={{ width: "6%" }}>Quantity</th>
              <th scope="col" style={{ width: "8%" }}>Usages</th>
              <th scope="col" style={{ width: "22%" }}>
                Effective Date
              </th>
              <th scope="col" style={{ width: "8%" }}>Status</th>
            </tr>
          </thead>
          <tbody>
            {
              vouchers.map(voucher => (
                <tr key={voucher.id}>
                  <td>
                    <i
                      className="trash-icon fas fa-trash-alt col-4"
                      title="Delete"
                      onClick={() =>
                        onSimpleClick(
                          "Delete",
                          "Are you sure to delete this voucher permanently?",
                          voucher,
                          handleDeleteVoucher
                        )
                      }
                    ></i>
                    {voucher.status === 'active' && <i
                      className="trash-icon fas fa-ban col-4"
                      title="Deactive"
                      onClick={() =>
                        onSimpleClick(
                          "Disable",
                          "Are you sure to disable this voucher?",
                          voucher,
                          handleDisableVoucher
                        )
                      }
                    ></i>}
                    {voucher.status === 'inactive' && <i
                      className="trash-icon fas fa-check-circle col-4"
                      title="Active"
                      onClick={() =>
                        onSimpleClick(
                          "Enable",
                          "Are you sure to activate this voucher?",
                          voucher,
                          handleEnableVoucher
                        )
                      }
                    ></i>}
                  </td>

                  <td>
                    <b className="trash-icon" onClick={() => showVoucherDetailsModal(voucher)}>{voucher.reference}</b>
                  </td>
                  <td>
                    <p className="text-truncate" title={voucher.title} onClick={() => showVoucherDetailsModal(voucher)}>{voucher.title}</p>
                  </td>
                  <td className="text-truncate">{voucher.voucherCode}</td>
                  <td>
                    <span>{`${voucher.discount}${voucher.type === VOUCHER_TYPE.CASH ? " VND" : "%"}`}</span>
                  </td>
                  <td>{voucher.maxQuantity}</td>
                  <td>{voucher.usages}</td>
                  <td className="text-truncate">
                    {`${voucher.startDate} - ${voucher.endDate}`}
                  </td>
                  <td className={`bold text-uppercase ${voucher.status === 'active' ? "green" : voucher.status === 'inactive' ? "red" : voucher.status === 'full' ? "blue" : "yellow"}`}>{voucher.status}</td>
                </tr>
              ))}
          </tbody>
        </table>
      )}
      <div className="yard-pagination mt-4">
        <Pagination maxPage={maxPage} messageKey={messageKey} onChangePage={onChangePage} />
      </div>
      <Modal isShowing={toggleCreateVoucherModal} hide={handleToggleVoucherModal}>
        <VoucherDetailsModal
          voucherTypeCreate={voucherTypeCreate}
          toggleModal={handleToggleVoucherModal}
          voucher={voucherSelected}
          fetchVouchers={fetchVouchers}
        />
      </Modal>
    </div>
  );
}

export default VoucherManagementWidget;
