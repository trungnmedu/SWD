import { useEffect, useState } from "react";
import "react-confirm-alert/src/react-confirm-alert.css";

import { ToastContainer } from "react-toastify";
import basketball from "../../assets/images/basketball-2.png";
import empty from "../../assets/images/empty.png";
import player1 from "../../assets/images/player-1.gif";
import player2 from "../../assets/images/player-2.gif";
import { EMPTY } from "../../constants/default";
import CancelBookingModal from "../../modals/CancelBookingModal";
import { getMyIncomingMatches } from "../../services/me.service";
import DisableElement from "../DisableElement";
import Modal, { useModal } from "../Modal";
import Pagination from "../Pagination";
import "./style.scss";

function IncomingMatchesWidget() {
  const ITEMS_PER_PAGE = 5;
  const [showCancelBookingModal, toggleShowCancelBookingModal] = useModal();
  const [isLoading, setIsLoading] = useState(false);
  const [matches, setMatches] = useState([]);
  const [maxPage, setMaxPage] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);
  const [bookingId, setBookingId] = useState(EMPTY);

  useEffect(() => {
    getIncomingMatches();
  }, []);

  const getIncomingMatches = (page = 1, itemsPerPage = ITEMS_PER_PAGE) => {
    setIsLoading(true);
    getMyIncomingMatches({ page, itemsPerPage })
      .then((res) => {
        setMatches(res.data);
        setMaxPage(
          res.maxResult % ITEMS_PER_PAGE === 0 && res.maxResult !== 0
            ? res.maxResult / ITEMS_PER_PAGE
            : Math.floor(res.maxResult / ITEMS_PER_PAGE) + 1
        );
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const onChangePage = (page) => {
    getIncomingMatches(page);
    setCurrentPage(page);
  };

  return (
    <div className="col-6 ps-4 pe-4 flex-column pt-4 m-auto mt-5">
      <Modal
        isShowing={showCancelBookingModal}
        hide={toggleShowCancelBookingModal}
      >
        <CancelBookingModal
          toggleModal={toggleShowCancelBookingModal}
          bookingId={bookingId}
          onSave={() => getIncomingMatches(currentPage)}
        />
      </Modal>
      <img src={player1} alt="Player" className="incoming-player-1" />
      <img src={player2} alt="Player" className="incoming-player-2" />
      <h4 className="text-center mb-4">
        <img src={basketball} alt="basketball" className="width-60 pe-3" />
        Incoming Matches
      </h4>
      <div className="overflow-auto h-75 p-3">
        {isLoading ? (
          <div
            className="w-100 d-flex justify-content-center align-items-center"
            style={{ height: "50vh" }}
          >
            <DisableElement />
          </div>
        ) : (
          <>
            {matches.map((match) => (
              <div className="match-container row mb-2">
                <div className="col-3 basket__img-container d-flex justify-content-center align-items-center">
                  <p className="text-center">
                    <b>
                      {match.startTime} - {match.endTime}
                    </b>
                    <br />
                    <span style={{ fontSize: "0.9rem" }}>{match.date}</span>
                  </p>
                </div>
                <div className="p-3 ps-0 pe-1 col-7 d-flex align-items-center flex-column">
                  <p className="text-center">
                    <b>{match.bigYardName}</b>
                    <p>
                      {match.subYardName} - ({match.type})
                    </p>
                  </p>
                  <p className="color-blur">{match.bigYardAddress}</p>
                </div>
                <div
                  className="col-2 basket__img-container d-flex justify-content-center align-items-center"
                  style={{ fontSize: "1.5rem" }}
                >
                  <i
                    className="far fa-trash-alt trash-icon red"
                    title="Cancel booking"
                    onClick={() => {
                      setBookingId(match.bookingId);
                      toggleShowCancelBookingModal();
                    }}
                  ></i>
                </div>
              </div>
            ))}
            {!matches.length && (
              <div className="w-100 pt-5 d-flex justify-content-center align-items-center flex-column h-300">
                <img src={empty} style={{ width: 80 }} />
                <p
                  className="text-center nodata-text"
                  style={{ fontSize: "0.9rem" }}
                >
                  No incoming match available
                </p>
              </div>
            )}
          </>
        )}
      </div>
      <Pagination maxPage={maxPage} onChangePage={onChangePage} />
      <ToastContainer />
    </div>
  );
}

export default IncomingMatchesWidget;
