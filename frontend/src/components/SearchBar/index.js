import { useState } from "react";
import { EMPTY } from "../../constants/default";
import { messageService } from "../../services/message.service";
import "./style.scss";

function SearchBar({
  onSearch,
  filterableFields = [],
  sortableFields = [],
  messageKey = "DEFAULT",
}) {
  const [params, setParams] = useState({
    keyword: EMPTY,
    sort: { field: EMPTY, value: "asc" },
    filter: { field: EMPTY, value: EMPTY },
  });

  const handleSearch = () => {
    const criteria = { ...params };

    if (params.sort.field) {
      criteria.sort =
        (params.sort.value === "asc" ? "+" : "-") + params.sort.field;
    } else {
      delete criteria.sort;
    }

    onSearch(criteria);
    messageService.sendMessage(messageKey, { page: 1 });
  };

  const clearCriteria = () => {
    onSearch();
    messageService.sendMessage(messageKey, { page: 1 });
    setParams({
      keyword: EMPTY,
      sort: { field: EMPTY, value: EMPTY },
      filter: { field: EMPTY, value: EMPTY },
    });
  };

  return (
    <div className="search-sort mb-4 row">
      <div className="row p-2 col-3 px-4 pt-1">
        <input
          id="keyword"
          name="keyword"
          className="outline-none p-2 search-input"
          type="text"
          placeholder="Search by keyword"
          onChange={(e) => setParams({ ...params, keyword: e.target.value })}
          value={params.keyword}
        />
      </div>
      <div className="row p-2 col-2 px-4 pt-1">
        <select
          id="field"
          className="outline-none p-2 search-input"
          style={{ backgroundColor: "white" }}
          onChange={(e) =>
            setParams({
              ...params,
              filter: { field: e.target.value, value: EMPTY },
            })
          }
          value={params.filter.field}
        >
          <option value="" key="NO_FIELD" disabled>
            Filter by field
          </option>
          {filterableFields.map((field) => (
            <option value={field.field} key={field.field}>
              {field.label}
            </option>
          ))}
        </select>
      </div>
      <div className="row p-2 col-2 px-4 pt-1">
        <select
          id="field"
          className="outline-none p-2 search-input"
          style={{ backgroundColor: "white" }}
          disabled={!params.filter.field}
          onChange={(e) =>
            setParams({
              ...params,
              filter: { ...params.filter, value: e.target.value },
            })
          }
          value={params.filter.value}
        >
          <option value="" key="NO_VALUE" disabled>
            Select value
          </option>
          {filterableFields
            .find((field) => field.field === params.filter.field)
            ?.options.map((option) => (
              <option value={option.value} key={option.value}>
                {option.label}
              </option>
            ))}
        </select>
      </div>
      <div className="row p-2 col-2 px-4 pt-1">
        <select
          id="field"
          className="outline-none p-2 search-input"
          style={{ backgroundColor: "white" }}
          onChange={(e) =>
            setParams({
              ...params,
              sort: { ...params.sort, field: e.target.value },
            })
          }
          value={params.sort.field}
        >
          <option value="" key="NO_FIELD" disabled>
            Sort by field
          </option>
          {sortableFields.map((field) => (
            <option value={field.value} key={field.value}>
              {field.label}
            </option>
          ))}
        </select>
      </div>
      <div className="row p-2 col-2 px-4 pt-1">
        <select
          id="field"
          className="outline-none p-2 search-input"
          style={{ backgroundColor: "white" }}
          disabled={!params.sort.field}
          onChange={(e) =>
            setParams({
              ...params,
              sort: { ...params.sort, value: e.target.value },
            })
          }
          value={params.sort.value}
        >
          <option value="asc" key="asc">
            Ascending
          </option>
          <option value="des" key="des">
            Descending
          </option>
        </select>
      </div>
      <div className="row p-2 col-1 px-4 pe-2 pt-1">
        <button className="btn btn-primary p-2" onClick={() => handleSearch()}>
          Search
        </button>
      </div>
      <div className="row p-2 col-1 px-4 pe-2 pt-1">
        <button className="btn btn-danger p-2" onClick={() => clearCriteria()}>
          Clear
        </button>
      </div>
    </div>
  );
}

export default SearchBar;
