import React from "react";
import { Navigate, Outlet } from "react-router-dom";

import Header from "../../components/Header";
import { ADMIN, OWNER } from "../../constants/roles";
import { decrypt, encryptKey } from "../../helpers/crypto.helper";
import "./style.scss";

function HomePage() {
  const credential = localStorage.getItem(encryptKey("credential"));
  if (!credential) {
    return <Navigate to="/auth/login" />;
  }

  const role = decrypt(credential)?.role;
  if (role !== ADMIN) {
    return role === OWNER ? <Navigate to="/owner" /> : <Navigate to="/" />;
  }

  return (
    <div className="home">
      <Header auth={true} />
      <div className="px-5 d-flex justify-content-center features mt-5">
        <Outlet />
      </div>
    </div>
  );
}

export default HomePage;
