import React from "react";
import { NavLink } from "react-router-dom";

import congratulation from "../../assets/images/fire-cracker.png";
import { ADMIN, OWNER, USER } from "../../constants/roles";
import "./style.scss";

const features = [
  {
    title: "Manage Yards",
    icon: "fas fa-columns",
    allowedRoles: [OWNER],
    path: "/owner/yards",
  },
  {
    title: "Statistics",
    icon: "fas fa-chart-bar",
    allowedRoles: [OWNER],
    path: "/owner/statistics",
  },
  {
    title: "Incoming Matches",
    icon: "far fa-calendar-check",
    allowedRoles: [USER],
    path: "/incoming-matches",
  },
  {
    title: "History",
    icon: "fas fa-history",
    allowedRoles: [USER],
    path: "/history",
  },
  {
    title: "Users",
    icon: "far fa-user",
    allowedRoles: [ADMIN],
    path: "/admin/users",
  },
  {
    title: "Reports",
    icon: "fas fa-exclamation-triangle",
    allowedRoles: [ADMIN],
    path: "/admin/reports",
  },
  {
    title: "Booking Management",
    icon: "fas fa-basketball-ball",
    allowedRoles: [OWNER],
    path: "/owner/booking-management",
  },
  {
    title: "History",
    icon: "fas fa-history",
    allowedRoles: [OWNER],
    path: "/owner/history",
  },
  {
    title: "Voucher Management",
    icon: "fas fa-wallet",
    allowedRoles: [OWNER],
    path: "/owner/voucher-management",
  },
  {
    title: "Rating",
    icon: "far fa-star",
    allowedRoles: [USER],
    path: "/rating",
  },
];

function DashboardFeatures({ role }) {
  return (
    <div className="p-5 w-100 height-mincontent mx-0">
      <h2 className="mb-5 text-center mt-5">
        {role !== USER ? "Dashboard" : "Welcome to Basketball Playground"}
        {role === USER && (
          <img src={congratulation} alt="congratulation" width="40" />
        )}
      </h2>
      <div className="row justify-content-around">
        {features
          .filter((feature) => feature.allowedRoles.includes(role))
          .map((feature) => {
            return (
              <div className="col-3 mb-4" key={feature.path}>
                <NavLink
                  key={feature.title}
                  to={feature.path}
                  className="feature-container d-flex align-items-center justify-content-center flex-column m-auto"
                >
                  <i className={"size-2 mb-3 " + feature.icon}></i>
                  <p>{feature.title}</p>
                </NavLink>
              </div>
            );
          })}
      </div>
    </div>
  );
}

export default DashboardFeatures;
