import * as moment from "moment";
import React, { useEffect, useState } from "react";
import { Bar, Line, Pie } from "react-chartjs-2";
import { getStatistic } from "../../services/me.service";
import DisableElement from "../DisableElement";
import "./style.scss";

function Statistics() {
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [yardStatisticModel, setYardStatisticModel] = useState([]);
  const [numberOfBookingsByHours, setNumberOfBookingsByHours] = useState([]);
  const [maxBookings, setMaxBookings] = useState(0);
  const [hasData, setHasData] = useState(false);
  const [totalIncome, setTotalIncome] = useState(0);
  let numOfColor = 0;

  useEffect(() => {
    if (startDate && endDate) {
      getStatisticObject();
      setHasData(true);
    }
  }, [startDate, endDate]);

  const selectColor = (number) => {
    const approximation = number * 137.508;
    return `hsl(${approximation},50%,50%)`;
  };

  const getStatisticObject = () => {
    setIsLoading(true);
    getStatistic({
      startTime: moment(startDate).format("yyyy-MM-DD"),
      endTime: moment(endDate).format("yyyy-MM-DD"),
    })
      .then((res) => {
        setYardStatisticModel(res.yardStatistic);
        setNumberOfBookingsByHours(res.bookingStatisticByTime);
        setMaxBookings(Math.round(res.maxOfBooking + 0.2 * res.maxOfBooking));
        setTotalIncome(res.totalIncome);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const numberWithCommas = function (x) {
    return "abc";
  };

  return (
    <div className="pt-4 w-100 mt-5">
      <div className="d-flex justify-content-around">
        <div className="page-title">
          <h3>Statistics</h3>
        </div>
        <div className="start-date d-flex justify-content-center mb-4 row">
          <label htmlFor="play-date" className="text-start">
            Start Date
          </label>
          <span className="col-1 lh-44 signup__icon-wrapper booking__icon-wrapper">
            <i className="far fa-clock"></i>
          </span>
          <input
            id="play-date"
            className="col-10 outline-none p-2 signup__input-border"
            type="date"
            placeholder="Date"
            value={startDate}
            onChange={(e) => {
              setStartDate(e.target.value);
            }}
            required
          />
        </div>
        <div className="end-date d-flex justify-content-center mb-4 row">
          <label htmlFor="play-date" className="text-start">
            End Date
          </label>
          <span className="col-1 lh-44 signup__icon-wrapper booking__icon-wrapper">
            <i className="far fa-clock"></i>
          </span>
          <input
            id="play-date"
            className="col-10 outline-none p-2 signup__input-border"
            type="date"
            placeholder="Date"
            value={endDate}
            onChange={(e) => {
              setEndDate(e.target.value);
            }}
            required
          />
        </div>
      </div>

      {isLoading ? (
        <div
          className="w-100 d-flex justify-content-center align-items-center"
          style={{ height: "50vh" }}
        >
          <DisableElement />
        </div>
      ) : (
        <div className="pt-4 w-100">
          <div className=" pt-4 w-100">
            {hasData ? (
              <>
                {" "}
                <div className="d-flex padding-right-20">
                  <div className="doughnut-chart col-4 text-center border border-1">
                    <h4 className="pie-title w-100">Yard Turnover</h4>
                    <div className="total-income">
                      Total Income: {totalIncome} VND
                    </div>
                    <div className="mg-top-20">
                      <Pie
                        data={{
                          labels: yardStatisticModel.map(
                            (yardModel) => yardModel.yardName
                          ),
                          datasets: [
                            {
                              label: "Booking",
                              data: yardStatisticModel.map(
                                (yardModel) => yardModel.totalIncome
                              ),
                              backgroundColor: yardStatisticModel.map(
                                (yardModel) => selectColor(numOfColor++)
                              ),
                              borderColor: "white",
                              borderWidth: 1,
                              spacing: 0.5,
                            },
                          ],
                        }}
                        height={600}
                        width={900}
                        options={{
                          plugins: {
                            legend: {
                              display: false,
                            },
                            tooltip: {
                              callbacks: {
                                label: function (context) {
                                  return (
                                    context.label +
                                    ": " +
                                    context.dataset.data[context.dataIndex] +
                                    " VND"
                                  );
                                },
                              },
                            },
                          },
                        }}
                      />
                    </div>
                  </div>

                  <div className="bar-chart col-8 border border-1 chart-padding">
                    <div>
                      <h4 class="chart-title">Booking Statistic</h4>
                      <div className="text-center">
                        <Bar
                          data={{
                            labels: yardStatisticModel.map((yardModel) =>
                              yardModel.yardName.split(" ")
                            ),
                            datasets: [
                              {
                                label: "Number of bookings",
                                data: yardStatisticModel.map(
                                  (yardModel) => yardModel.numberOfBookings
                                ),
                                backgroundColor: "#0000FF",
                                borderColor: "white",
                                borderWidth: 1,
                              },
                              {
                                label: "Number of cancellations",
                                data: yardStatisticModel.map(
                                  (yardModel) =>
                                    yardModel.numberOfBookingCanceled
                                ),
                                backgroundColor: "#FF0000",
                                borderColor: "white",
                              },
                              {
                                label: "Number of turns played",
                                data: yardStatisticModel.map(
                                  (yardModel) => yardModel.numberOfBookingPlayed
                                ),
                                backgroundColor: "#00FF00",
                                borderColor: "white",
                              },
                            ],
                          }}
                          height={400}
                          width={600}
                          options={{
                            maintainAspectRatio: false,
                            scales: {
                              y: {
                                max: maxBookings,
                                beginAtZero: true,
                                ticks: {
                                  precision: 0,
                                },
                              },
                            },
                            legend: {
                              labels: {
                                fontSize: 25,
                              },
                              display: true,
                            },
                            plugins: {
                              tooltip: {
                                callbacks: {
                                  title: function (context) {
                                    return context[0].label.replaceAll(
                                      ",",
                                      " "
                                    );
                                  },
                                },
                              },
                            },
                          }}
                        />
                      </div>
                    </div>
                  </div>
                </div>
                <div className="d-flex row">
                  <div className="pt-4 w-100">
                    <div className="border border-1 chart-padding">
                      <h4 class="chart-title">Booking In Time</h4>
                      <div className="line-chart text-center">
                        <Line
                          data={{
                            labels: [
                              "00:00",
                              "00:30",
                              "01:00",
                              "01:30",
                              "02:00",
                              "02:30",
                              "03:00",
                              "03:30",
                              "04:00",
                              "04:30",
                              "05:00",
                              "05:30",
                              "06:00",
                              "06:30",
                              "07:00",
                              "07:30",
                              "08:00",
                              "08:30",
                              "09:00",
                              "09:30",
                              "10:00",
                              "10:30",
                              "11:00",
                              "11:30",
                              "12:00",
                              "12:30",
                              "13:00",
                              "13:30",
                              "14:00",
                              "14:30",
                              "15:00",
                              "15:30",
                              "16:00",
                              "16:30",
                              "17:00",
                              "17:30",
                              "18:00",
                              "18:30",
                              "19:00",
                              "19:30",
                              "20:00",
                              "20:30",
                              "21:00",
                              "21:30",
                              "22:00",
                              "22:30",
                              "23:00",
                              "23:30",
                            ],
                            datasets: [
                              {
                                label: "Number of bookings",
                                data: numberOfBookingsByHours,
                                backgroundColor: "blue",
                                borderColor: "#008cea",
                                borderWidth: 1,
                                pointRadius: 0.4,
                                lineTension: 0.3,
                              },
                            ],
                          }}
                          height={400}
                          width={600}
                          options={{
                            maintainAspectRatio: false,
                            scales: {
                              y: {
                                ticks: {
                                  beginAtZero: true,
                                  precision: 0,
                                },
                              },
                              x: {
                                ticks: {
                                  autoSkip: false,
                                },
                              },
                            },
                            legend: {
                              labels: {
                                fontSize: 25,
                              },
                            },
                          }}
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </>
            ) : (
              <div className="w-100 pt-5 d-flex justify-content-center align-items-center flex-column mt-5">
                <i className="fas fa-hand-pointer size-3"></i>
                <p className="text-center nodata-text">
                  Please select start date and end date
                </p>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
export default Statistics;
