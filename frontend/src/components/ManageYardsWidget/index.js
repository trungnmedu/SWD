import * as moment from "moment";
import { useEffect } from "react";
import { confirmAlert } from "react-confirm-alert";

import { useState } from "react";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";
import empty from "../../assets/images/empty.png";
import playground from "../../assets/images/playground.png";
import { TOAST_CONFIG } from "../../constants/default";
import { INTERNAL_SERVER_ERROR } from "../../constants/error-message";
import {
  activateYard,
  deactivateYard,
  deleteYard,
  searchOwnerYard
} from "../../services/yard.service";
import DisableElement from "../DisableElement";
import Pagination from "../Pagination";
import SearchBar from "../SearchBar";
import "./style.scss";

const sortableFields = [
  { value: "reference", label: "Reference" },
  { value: "createdAt", label: "Created Time" },
  { value: "name", label: "Name" },
  { value: "address", label: "Address" },
  { value: "openAt", label: "Open Time" },
  { value: "closeAt", label: "Close Time" }
];

const filterableFields = [
  {
    label: "Status",
    options: [
      { value: true, label: "Active" },
      { value: false, label: "Inactive" },
    ],
    field: "isActive",
  },
];

const messageKey = "MANAGE_YARD_LIST";

function ManageYardsWidget() {
  const ITEMS_PER_PAGE = 10;
  const [yards, setYards] = useState([]);
  const [maxPage, setMaxPage] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const [criteria, setCriteria] = useState({});

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

  const handleDisableYard = (yard) => {
    deactivateYard(yard.id)
      .then(() => {
        toast.success(
          `Deactivate yard "${yard.name}" successfully.`,
          TOAST_CONFIG
        );
        getYards(currentPage);
      })
      .catch((error) => {
        toast.error(
          error.response.data.message || INTERNAL_SERVER_ERROR,
          TOAST_CONFIG
        );
      });
  };

  const handleEnableYard = (yard) => {
    activateYard(yard.id)
      .then(() => {
        toast.success(
          `Activate yard "${yard.name}" successfully.`,
          TOAST_CONFIG
        );
        getYards(currentPage);
      })
      .catch((error) => {
        toast.error(
          error.response.data.message || INTERNAL_SERVER_ERROR,
          TOAST_CONFIG
        );
      });
  };

  const handleDeleteClick = (yard) => {
    deleteYard(yard.id)
      .then(() => {
        toast.success(`Delete yard ${yard.name} successfully.`, TOAST_CONFIG);
        getYards(currentPage);
      })
      .catch((error) => {
        toast.error(
          error.response.data.message || INTERNAL_SERVER_ERROR,
          TOAST_CONFIG
        );
      });
  };

  const onChangePage = (page) => {
    setCurrentPage(page);
    getYards(page, ITEMS_PER_PAGE, criteria);
  };

  const getYards = (page = 1, itemsPerPage = ITEMS_PER_PAGE, criteria = {}) => {
    setIsLoading(true);
    searchOwnerYard({ page, itemsPerPage, ...criteria })
      .then((res) => {
        setYards(res.listYard);
        setMaxPage(
          res.maxResult % ITEMS_PER_PAGE === 0 && res.maxResult !== 0
            ? res.maxResult / ITEMS_PER_PAGE
            : Math.floor(res.maxResult / ITEMS_PER_PAGE) + 1
        );
      })
      .catch((error) => {
        toast.error(INTERNAL_SERVER_ERROR, TOAST_CONFIG);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const handleSearchBar = (criteria) => {
    getYards(1, ITEMS_PER_PAGE, criteria);
    setCriteria(criteria || {});
  };

  useEffect(() => {
    getYards(1, ITEMS_PER_PAGE);
  }, []);

  return (
    <div className="pt-4 mt-5 w-100">
      <div>
        <h4 className="mb-4 d-inline-block">
          <img src={playground} alt="Yard" className="width-60 pe-3" />
          Yard Management
        </h4>
        <Link to="/owner/yards/draft">
          <button className="btn btn-primary px-4 ms-5">
            <i className="fas fa-plus me-2" style={{ fontSize: "0.8rem" }}></i>
            <b>Add</b>
          </button>
        </Link>
        <SearchBar
          sortableFields={sortableFields}
          filterableFields={filterableFields}
          onSearch={handleSearchBar}
          messageKey={messageKey}
        />
      </div>
      {isLoading && (
        <div className="w-100 d-flex justify-content-center pt-5 h-300 align-items-center">
          <DisableElement />
        </div>
      )}
      {!isLoading && !!yards.length && (
        <table className="table table-striped">
          <thead>
            <tr>
              <th scope="col" style={{ width: "9%" }}>
                Actions
              </th>
              <th scope="col" style={{ width: "10%" }}>
                Reference
              </th>
              <th scope="col">Name</th>
              <th scope="col" style={{ width: "22%" }}>
                Address
              </th>
              <th scope="col" style={{ width: "12%" }}>
                Open Time
              </th>
              <th scope="col" style={{ width: "12%" }}>
                Status
              </th>
              <th scope="col" style={{ width: "12%" }}>
                Created At
              </th>
            </tr>
          </thead>
          <tbody>
            {yards.map((yard) => (
              <tr key={yard.id}>
                <td>
                  <i
                    className="trash-icon fas fa-trash-alt col-4"
                    title="Delete"
                    onClick={() =>
                      onSimpleClick(
                        "Delete",
                        "Are you sure to delete this yard permanently?",
                        () => handleDeleteClick(yard)
                      )
                    }
                  ></i>
                  {yard.isActive ? (
                    <i
                      className="trash-icon fas fa-ban col-4"
                      title="Deactive"
                      onClick={() =>
                        onSimpleClick(
                          "Disable",
                          "Are you sure to disable this yard?",
                          () => handleDisableYard(yard)
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
                          () => handleEnableYard(yard)
                        )
                      }
                    ></i>
                  )}
                </td>
                <td>
                  <Link to={`/owner/yards/${yard.id}`}>
                    <b className="trash-icon">{yard.reference}</b>
                  </Link>
                </td>
                <td className="text-truncate" title={yard.name}>
                  {yard.name}
                </td>
                <td className="text-truncate" title={yard.address}>
                  {yard.address}
                </td>
                <td>
                  {yard.openAt} - {yard.closeAt}
                </td>
                <td className={yard.isActive ? "green bold" : "red bold"}>
                  {yard.isActive ? "ACTIVE" : "INACTIVE"}
                </td>
                <td>{moment(yard.createdAt).format("DD/MM/yyyy")}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      {!isLoading && !yards.length && (
        <div className="w-100 pt-5 d-flex justify-content-center align-items-center flex-column">
          <img src={empty} alt="No result" style={{ width: 80 }} />
          <p className="text-center nodata-text" style={{ fontSize: "0.9rem" }}>
            No yard available
          </p>
        </div>
      )}
      <Pagination
        maxPage={maxPage}
        onChangePage={onChangePage}
        messageKey={messageKey}
      />
    </div>
  );
}

export default ManageYardsWidget;
