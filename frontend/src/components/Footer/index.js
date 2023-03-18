import React from "react";
import "./style.scss";

function Header() {
  return (
    <div className="footer row">
      <div className="col-8 bold d-flex align-items-center justify-content-center">
        <span className="p-2 size-2 ps-4 pe-3">
          <i className="fas fa-basketball-ball"></i>
        </span>
        Copyright 2023, Inc Basketball Playground @ 2023
      </div>
      <div className="col-4 d-flex justify-content-center flex-column">
        <p className="mb-3 bold">Contact Us</p>
        <div className="contact-item mb-2">
          <i className="fas fa-envelope pe-3" style={{ fontSize: 22 }}></i>
          <span>fuswp391@gmail.com</span>
        </div>
        <div className="contact-item">
          <i className="fas fa-phone-alt pe-3" style={{ fontSize: 22 }}></i>
          <span>0919 956 689</span>
        </div>
      </div>
    </div>
  );
}

export default Header;
