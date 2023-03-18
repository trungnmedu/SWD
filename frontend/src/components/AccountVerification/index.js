import React, { useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import { toast, ToastContainer } from "react-toastify";
import {
  EMPTY, INTERNAL_SERVER_ERROR, TOAST_CONFIG
} from "../../constants/default";
import { ADMIN, OWNER } from "../../constants/roles";
import { decrypt, encrypt, encryptKey } from "../../helpers/crypto.helper";
import { receiveVerifyCode, verifyAccount } from "../../services/auth.service";
import "./style.scss";

function AccountVerification() {
  const credential = localStorage.getItem(encryptKey("credential"));
  const navigate = useNavigate();
  const [code, setCode] = useState(EMPTY);

  if (!credential) {
    return <Navigate to="/auth/login" />;
  } else {
    const role = decrypt(credential).role;
    if (role === ADMIN) {
      return <Navigate to="/admin" />;
    } else if (role === OWNER) {
      return <Navigate to="/owner" />;
    }
  }

  const handleVerifyCode = () => {
    const decoded = decrypt(credential);
    verifyAccount(decoded.token, code)
      .then((res) => {
        toast.success("Verify account successfully.", TOAST_CONFIG);
        decoded.isConfirm = true;
        localStorage.setItem(encryptKey("credential"), encrypt(decoded));
        navigate("/");
      })
      .catch((error) => {
        toast.error(
          error.response.data.message || INTERNAL_SERVER_ERROR,
          TOAST_CONFIG
        );
      });
  };

  const handleRequestCode = () => {
    setCode(EMPTY);
    toast.info("Code will be sent soon.", TOAST_CONFIG);

    receiveVerifyCode(decrypt(credential).token)
      .then(() => {
        toast.success("Code has been sent.", TOAST_CONFIG);
      })
      .catch((error) => {
        toast.error(
          error.response.data.message || INTERNAL_SERVER_ERROR,
          TOAST_CONFIG
        );
      });
  };

  return (
    <div className="row align-items-center justify-content-center text-center loading-height">
      <div className="col-4">
        <h3 className=" bold size-3">Verify Your Account</h3>
        <form className="mt-5">
          <div className="row p-2">
            <label
              htmlFor="verificationCode"
              className="text-start"
              style={{ paddingLeft: 0 }}
            >
              Verification Code
            </label>
            <span className="col-1 lh-44 signup__icon-wrapper">
              <i className="fas fa-lock"></i>
            </span>
            <input
              id="verificationCode"
              name="verificationCode"
              className="col-8 outline-none p-2 fg-pw__input-border"
              type="text"
              placeholder="Enter verification code"
              required
              value={code}
              onChange={(e) => {
                setCode(e.target.value);
              }}
            />
            <button
              className="col-3 lh-44 fg-pw__icon-wrapper"
              onClick={(e) => {
                e.preventDefault();
                handleRequestCode();
              }}
            >
              Resend mail
            </button>
          </div>
          <div className="pt-3 pb-3">
            <button
              type="submit"
              className="btn btn-primary w-100 p-2"
              onClick={(e) => {
                e.preventDefault();
                handleVerifyCode();
              }}
              disabled={!code}
            >
              Verify
            </button>
            <ToastContainer />
          </div>
        </form>
      </div>
    </div>
  );
}

export default AccountVerification;
