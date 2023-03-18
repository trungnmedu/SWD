import React from "react";
import { Navigate, Outlet } from "react-router-dom";

import Header from "../../components/Header";
import { ADMIN, OWNER } from "../../constants/roles";
import { decrypt, encryptKey } from "../../helpers/crypto.helper";
import "./style.scss";

function OwnerPage() {
  const credential = localStorage.getItem(encryptKey("credential"));
  if (!credential) {
    return <Navigate to="/auth/login" />;
  }

  const role = decrypt(credential)?.role;
  if (role !== OWNER) {
    return role === ADMIN ? <Navigate to="/admin" /> : <Navigate to="/" />;
  }

  return (
    <div className="home">
      <Header auth={true} />
      <div className="d-flex justify-content-center features mt-5 px-5">
        <Outlet />
      </div>
    </div>
  );
}

export default OwnerPage;
