import _ from "lodash";
import { useState } from "react";
import { v4 as uuidv4 } from "uuid";

import { EMPTY } from "../../constants/default";
import { YARD_TYPES } from "../../constants/type";

const UpdateSubYardModal = ({
  toggleModal,
  slots,
  yard,
  onUpdateSubYardList,
}) => {
  const [subYard, setSubYard] = useState({
    name: EMPTY,
    type: yard
      ? yard.id
        ? YARD_TYPES.find((t) => t.lable === yard.typeYard).value
        : yard.type
      : 3,
    ...yard,
    slots: !yard ? _.cloneDeep(slots) : yard.slots,
  });

  const onChangeSlotPrice = (e, slot) => {
    const slotList = _.clone(subYard.slots);
    const index = slotList.findIndex(
      (item) => item.startTime === slot.startTime
    );
    slotList[index] = {
      ...slot,
      price: Number(e.target.value),
    };

    console.log(isNaN(e.target.value));
    if (isNaN(e.target.value) || slotList[index].price - 1 < 0) {
      slotList[index].price = "";
    }

    setSubYard({
      ...subYard,
      slots: slotList,
    });
  };

  return (
    <div className="custom-confirm" style={{ width: "90vw" }}>
      <h4>{yard ? yard.name : "Create New Sub Yard"}</h4>
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
              name="name"
              className="col-11 outline-none p-2 signup__input-border"
              type="text"
              placeholder="Name"
              value={subYard.name}
              onChange={(e) => setSubYard({ ...subYard, name: e.target.value })}
            />
          </div>
          <div className="row p-2 py-1">
            <label htmlFor="type" style={{ paddingLeft: 0 }}>
              Type
            </label>
            <span className="col-1 lh-44 signup__icon-wrapper" title="Size">
              <i className="fas fa-expand-arrows-alt"></i>
            </span>
            <select
              className="col-11 outline-none p-2 signup__input-border"
              style={{ backgroundColor: "white" }}
              name="type"
              onChange={(e) =>
                setSubYard({ ...subYard, type: Number(e.target.value) })
              }
              value={subYard.type}
            >
              <option value="3">3 vs 3</option>
              <option value="5">5 vs 5</option>
            </select>
          </div>
        </form>
        <div className="flex-1 ps-3">
          <div className="row p-3 overflow-y-auto pt-0 mh-550">
            {subYard.slots.map((slot) => {
              return (
                <div
                  className="col-3 slot-create-container"
                  key={slot.startTime}
                >
                  <div className="slot-details flex-column">
                    <p>
                      <b>
                        {slot.startTime} - {slot.endTime}
                      </b>
                    </p>
                    <p className="mt-2">
                      <input
                        className="w-75 text-center border-none price-input py-2"
                        type="text"
                        value={slot.price}
                        onChange={(e) => onChangeSlotPrice(e, slot)}
                      />
                      {""}
                      VND
                    </p>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>
      <button
        className="btn btn-primary me-3 px-4"
        onClick={() => {
          if (!yard) {
            subYard.ref = uuidv4();
          }
          onUpdateSubYardList({
            ...subYard,
            isUpdate: yard ? true : false,
          });
          toggleModal();
        }}
      >
        {yard ? "Save" : "Create"}
      </button>
      <button onClick={toggleModal} className="btn btn-light">
        Cancel
      </button>
    </div>
  );
};

export default UpdateSubYardModal;
