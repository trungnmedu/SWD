import React, { useEffect, useState } from "react";
import empty from "../../assets/images/empty.png";

import transaction from "../../assets/images/transaction.png";
import "./style.scss";

import { CANCELED, FAILED, SUCCESS } from "../../constants/bookingStatus";
import { USER } from "../../constants/roles";
import { decrypt, encryptKey } from "../../helpers/crypto.helper";
import { getBookingHistory } from "../../services/me.service";
import DisableElement from "../DisableElement";
import Pagination from "../Pagination";
import SearchBar from "../SearchBar";

const sortableFields = [
  { value: "bigYardName", label: "Yard" },
  { value: "price", label: "Price" },
  { value: "createdAt", label: "Created Time" },
];

const filterableFields = [
  {
    label: "Status",
    options: [
      { value: "SUCCESS", label: "Success" },
      { value: "FAILED", label: "Failed" },
      { value: "CANCELED", label: "Canceled" },
    ],
    field: "bookingStatus",
  },
];

const messageKey = "MANAGE_HISTORY_LIST";

function HistoryWidget() {
  const ITEMS_PER_PAGE = 10;
  const [historyBookingModels, setHistoryBookingModels] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [maxPage, setMaxPage] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);
  const [criteria, setCriteria] = useState({});

  useEffect(() => {
    getHistoryBooking();
  }, []);

  const getHistoryBooking = (
    page = 1,
    itemsPerPage = ITEMS_PER_PAGE,
    criteria = {}
  ) => {
    setIsLoading(true);
    const credential = localStorage.getItem(encryptKey("credential"));
    if (credential) {
      const role = decrypt(credential).role;
      getBookingHistory(
        { page, itemsPerPage, ...criteria },
        role === USER ? "me" : "owners/me"
      )
        .then((res) => {
          setHistoryBookingModels(res.data);
          setMaxPage(
            res.maxResult % ITEMS_PER_PAGE === 0 && res.maxResult !== 0
              ? res.maxResult / ITEMS_PER_PAGE
              : Math.floor(res.maxResult / ITEMS_PER_PAGE) + 1
          );
        })
        .finally(() => {
          setIsLoading(false);
        });
    }
  };

  const onChangePage = (page) => {
    getHistoryBooking(page, ITEMS_PER_PAGE, criteria);
    setCurrentPage(page);
  };

  const getColor = (status) => {
    if (status === SUCCESS) return "green bold";
    else if (status === FAILED) return "red bold";
    else if (status === CANCELED) return "yellow bold";
  };

  const handleSearchBar = (criteria) => {
    getHistoryBooking(1, ITEMS_PER_PAGE, criteria);
    setCriteria(criteria || {});
  };

  return (
    <div className="pt-4 mt-5 w-100 px-5">
      <h4 className="mb-4 d-inline-block">
        <img src={transaction} alt="Transaction" className="width-60 pe-3" />
        Booking History
      </h4>
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
      ) : !historyBookingModels.length ? (
        <div className="w-100 pt-5 d-flex justify-content-center align-items-center flex-column h-300">
          <img src={empty} style={{ width: 80 }} />
          <p className="text-center nodata-text" style={{ fontSize: "0.9rem" }}>
            No result available
          </p>
        </div>
      ) : (
        <>
          <table className="table table-striped">
            <thead>
              <tr>
                <th scope="col" style={{ width: "15%" }}>
                  Yard
                </th>
                <th scope="col" style={{ width: "10%" }}>
                  Sub-yard
                </th>
                <th scope="col" style={{ width: "5%" }}>
                  Type
                </th>
                <th scope="col" style={{ width: "10%" }}>
                  Address
                </th>
                <th scope="col" style={{ width: "8%" }}>
                  Price
                </th>
                <th scope="col" style={{ width: "8%" }}>
                  Status
                </th>
                <th scope="col" style={{ width: "15%" }}>
                  Note
                </th>
                <th scope="col" style={{ width: "8%" }}>
                  Created By
                </th>
                <th scope="col" style={{ width: "15%" }}>
                  Created At
                </th>
              </tr>
            </thead>
            <tbody>
              <>
                {historyBookingModels.map((historyBooking) => (
                  <tr>
                    <td
                      className="text-truncate"
                      title={historyBooking.bigYardName}
                    >
                      {historyBooking.bigYardName}
                    </td>
                    <td>{historyBooking.subYardName}</td>
                    <td>{historyBooking.type}</td>
                    <td
                      className="text-truncate"
                      title={historyBooking.address}
                    >
                      {historyBooking.address}
                    </td>
                    <td>{historyBooking.price} VND</td>
                    <td className={getColor(historyBooking.bookingStatus)}>
                      {historyBooking.bookingStatus}
                    </td>
                    <td
                      className="text-truncate"
                      title={historyBooking.note || "N/A"}
                    >
                      {historyBooking.note || "N/A"}
                    </td>
                    <td>{historyBooking.createdBy}</td>
                    <td>{historyBooking.createdAt}</td>
                  </tr>
                ))}
              </>
            </tbody>
          </table>
        </>
      )}
      <Pagination maxPage={maxPage} onChangePage={onChangePage} messageKey={messageKey} />
    </div>
  );
}

export default HistoryWidget;
