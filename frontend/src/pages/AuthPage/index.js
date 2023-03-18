import React from "react";
import { Navigate, Outlet } from "react-router-dom";

import Header from "../../components/Header";
import { ADMIN, OWNER } from "../../constants/roles";
import { decrypt, encryptKey } from "../../helpers/crypto.helper";
import "./style.scss";

function AuthPage() {
  const credential = localStorage.getItem(encryptKey("credential"));
  if (credential) {
    const role = decrypt(credential).role;
    return role === ADMIN ? (
      <Navigate to="/admin" />
    ) : role === OWNER ? (
      <Navigate to="/owner" />
    ) : (
      <Navigate to="/" />
    );
  }

  return (
    <div className="home">
      <Header auth={false} />
      <div className="features mt-5">
        <Outlet />
      </div>
    </div>
  );
}

export default AuthPage;
