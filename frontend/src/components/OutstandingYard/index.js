import React from "react";
import { Link } from "react-router-dom";
import { Rating } from "react-simple-star-rating";
import { ToastContainer } from "react-toastify";

import yard1 from "../../assets/images/yard-1.jpg";
import yard2 from "../../assets/images/yard-2.jpg";
import yard3 from "../../assets/images/yard-3.jpg";
import { TIMELINE } from "../../constants/time";
import "./style.scss";

function OutstandingYard() {
  const yards = [
    {
      id: "c62f6d14-3114-435a-8c9a-c1c767eed9ca",
      name: "Sân Thủ Đức",
      address: "503 Lê Văn Việt, Thành phố Thủ Đức, Thành phố Hồ Chí Minh",
      openAt: 360,
      closeAt: 600,
      slotDuration: 60,
      images: [yard1, yard2, yard3],
      subYards: [
        {
          name: "Sân 1",
          type: "3 vs 3",
          slots: [
            {
              startTime: "06:00",
              endTime: "07:00",
              price: 50000,
            },
            {
              startTime: "07:00",
              endTime: "08:00",
              price: 60000,
            },
            {
              startTime: "08:00",
              endTime: "09:00",
              price: 60000,
            },
            {
              startTime: "09:00",
              endTime: "10:00",
              price: 50000,
            },
          ],
        },
        {
          name: "Sân 2",
          type: "5 vs 5",
          slots: [
            {
              startTime: "06:00",
              endTime: "07:00",
              price: 70000,
            },
            {
              startTime: "07:00",
              endTime: "08:00",
              price: 60000,
            },
            {
              startTime: "08:00",
              endTime: "09:00",
              price: 60000,
            },
            {
              startTime: "09:00",
              endTime: "10:00",
              price: 70000,
            },
          ],
        },
      ],
    },
    {
      id: "09effa30-d3d9-4d62-bb21-a795d0deba03",
      name: "Sân Quận 1",
      address: "2 Đinh Tiên Hoàng, Đa Kao, Quận 1, Thành phố Hồ Chí Minh",
      openAt: 360,
      closeAt: 600,
      slotDuration: 60,
      images: [yard2, yard1, yard3],
      subYards: [
        {
          name: "Sân 1",
          type: "3 vs 3",
          slots: [
            {
              startTime: "06:00",
              endTime: "07:00",
              price: 50000,
            },
            {
              startTime: "07:00",
              endTime: "08:00",
              price: 60000,
            },
            {
              startTime: "08:00",
              endTime: "09:00",
              price: 60000,
            },
            {
              startTime: "09:00",
              endTime: "10:00",
              price: 50000,
            },
          ],
        },
        {
          name: "Sân 2",
          type: "5 vs 5",
          slots: [
            {
              startTime: "06:00",
              endTime: "07:00",
              price: 70000,
            },
            {
              startTime: "07:00",
              endTime: "08:00",
              price: 60000,
            },
            {
              startTime: "08:00",
              endTime: "09:00",
              price: 60000,
            },
            {
              startTime: "09:00",
              endTime: "10:00",
              price: 70000,
            },
          ],
        },
        {
          name: "Sân 3",
          type: 2,
          slots: [
            {
              startTime: "06:00",
              endTime: "07:00",
              price: 70000,
            },
            {
              startTime: "07:00",
              endTime: "08:00",
              price: 70000,
            },
            {
              startTime: "08:00",
              endTime: "09:00",
              price: 60000,
            },
            {
              startTime: "09:00",
              endTime: "10:00",
              price: 50000,
            },
          ],
        },
      ],
    },
    {
      id: "970824f1-69b1-478e-9445-b3f8ca9a320c",
      name: "Sân Điện Biên Phủ",
      address: "186 Điện Biên Phủ, Quận 3, Thành phố Hồ Chí Minh",
      openAt: 360,
      closeAt: 600,
      slotDuration: 60,
      images: [yard3, yard1, yard2],
      subYards: [
        {
          name: "Sân 1",
          type: "3 vs 3",
          slots: [
            {
              startTime: "06:00",
              endTime: "07:00",
              price: 50000,
            },
            {
              startTime: "07:00",
              endTime: "08:00",
              price: 60000,
            },
            {
              startTime: "08:00",
              endTime: "09:00",
              price: 60000,
            },
            {
              startTime: "09:00",
              endTime: "10:00",
              price: 50000,
            },
          ],
        },
        {
          name: "Sân 2",
          type: "5 vs 5",
          slots: [
            {
              startTime: "06:00",
              endTime: "07:00",
              price: 70000,
            },
            {
              startTime: "07:00",
              endTime: "08:00",
              price: 60000,
            },
            {
              startTime: "08:00",
              endTime: "09:00",
              price: 60000,
            },
            {
              startTime: "09:00",
              endTime: "10:00",
              price: 70000,
            },
          ],
        },
      ],
    },
    {
      id: "b01b78ac-add8-4fbf-85a5-89666de6f3f6",
      name: "Sân Thủ Đức",
      address: "503 Lê Văn Việt, Thành phố Thủ Đức, Thành phố Hồ Chí Minh",
      openAt: 360,
      closeAt: 600,
      slotDuration: 60,
      images: [yard1, yard2, yard3],
      subYards: [
        {
          name: "Sân 1",
          type: "3 vs 3",
          slots: [
            {
              startTime: "06:00",
              endTime: "07:00",
              price: 50000,
            },
            {
              startTime: "07:00",
              endTime: "08:00",
              price: 60000,
            },
            {
              startTime: "08:00",
              endTime: "09:00",
              price: 60000,
            },
            {
              startTime: "09:00",
              endTime: "10:00",
              price: 50000,
            },
          ],
        },
        {
          name: "Sân 2",
          type: "5 vs 5",
          slots: [
            {
              startTime: "06:00",
              endTime: "07:00",
              price: 70000,
            },
            {
              startTime: "07:00",
              endTime: "08:00",
              price: 60000,
            },
            {
              startTime: "08:00",
              endTime: "09:00",
              price: 60000,
            },
            {
              startTime: "09:00",
              endTime: "10:00",
              price: 70000,
            },
          ],
        },
      ],
    },
  ]);

  return (
    <div>
      <h1 className="outstanding-title p-4 text-center">OUTSTANDING YARDS</h1>
      <div className="w-100 p-5">
        <div className="row mt-5 yard__result-container">
          {yards.map((yard) => (
            <div className="yard__result-wrapper col-3 p-3 pe-2" key={yard.id}>
              <Link className="yard-result" to={`/yard/${yard.id}`}>
                <img src={yard.images[0]} alt="basketball yard" />
                <div className="yard-details p-3">
                  <b className="d-block">{yard.name}</b>
                  <Rating
                    ratingValue={80}
                    allowHalfIcon={true}
                    readonly={true}
                    size={20}
                    className="mb-2"
                  />
                  <p className="row mb-1">
                    <i className="fas fa-clock col-1 pt-1"></i>
                    <span className="ps-2 color-blur col-10">
                      {
                        TIMELINE.find((time) => time.value === yard.openAt)
                          .label
                      }{" "}
                      -{" "}
                      {
                        TIMELINE.find((time) => time.value === yard.closeAt)
                          .label
                      }
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
        </div>
        {/* <Pagination /> */}
      </div>
      <ToastContainer />
    </div>
  );
}

export default OutstandingYard;
