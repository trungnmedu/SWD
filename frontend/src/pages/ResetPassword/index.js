import { useFormik } from "formik";
import React from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import * as yup from "yup";

import "./style.scss";

import {
  EMPTY,
  REQUEST_PASSWORD,
  REQUIRED_PASSWORD,
  TOAST_CONFIG
} from "../../constants/default";
import { INTERNAL_SERVER_ERROR } from "../../constants/error-message";
import { encryptKey } from "../../helpers/crypto.helper";
import { updatePassword } from "../../services/auth.service";

const validation = yup.object({
  re_password: yup.string(REQUEST_PASSWORD).required(REQUIRED_PASSWORD),
  confirm_re_password: yup
    .string(REQUEST_PASSWORD)
    .oneOf([yup.ref("re_password"), null], "Confirm password not matches")
    .required(REQUIRED_PASSWORD),
});

function ResetPassword() {
  const temporaryToken = localStorage.getItem(encryptKey("temporaryToken"));
  const credential = localStorage.getItem(encryptKey("credential"));
  const navigate = useNavigate();

  const formik = useFormik({
    initialValues: {
      re_password: EMPTY,
      confirm_re_password: EMPTY,
    },
    validationSchema: validation,
    onSubmit: async (values) => {
      const data = JSON.stringify({
        password: values.confirm_re_password,
      });

      if (temporaryToken) {
        updatePassword(data, temporaryToken)
          .then((res) => {
            toast.success("Reset password successfully.", TOAST_CONFIG);
            navigate("/auth/login");
            localStorage.removeItem(encryptKey("temporaryToken"));
          })
          .catch((error) => {
            toast.error(
              error.response.data.message || INTERNAL_SERVER_ERROR,
              TOAST_CONFIG
            );
          });
      } else {
        toast.error("Missing token!", TOAST_CONFIG);
      }
    },
  });

  if (!temporaryToken) {
    return <Navigate to="/not-found" />;
  }

  return (
    <div className="row align-items-center justify-content-center text-center w-100">
      <div className={`col-4 ${!credential && "pt-5"}`}>
        <h3 className={`bold size-4 ${!credential && "pt-5"}`}>Reset Password</h3>
        <i className="fas fa-lock size-5 mt-4"></i>
        <form className="mt-5" onSubmit={formik.handleSubmit}>
          <div className="row p-2">
            <label
              htmlFor="rePassword"
              className="text-start"
              style={{ paddingLeft: 0 }}
            >
              New Password
            </label>
            <span className="col-1 lh-44 signup__icon-wrapper">
              <i className="fas fa-lock"></i>
            </span>
            <input
              id="password"
              name="re_password"
              value={formik.values.re_password}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              className="col-11 outline-none p-2 signup__input-border"
              type="password"
              placeholder="Enter new password"
              required
            />
            <span className="signup__filed--error">
              {formik.touched.re_password && formik.errors.re_password
                ? formik.errors.re_password
                : EMPTY}{" "}
            </span>
          </div>
          <div className="row p-2">
            <label
              htmlFor="confirmRePassword"
              className="text-start"
              style={{ paddingLeft: 0 }}
            >
              Confirm New Password
            </label>
            <span className="col-1 lh-44 signup__icon-wrapper">
              <i className="fas fa-lock"></i>
            </span>
            <input
              id="confirmRePassword "
              name="confirm_re_password"
              value={formik.values.confirm_re_password}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              className="col-11 outline-none p-2 signup__input-border"
              type="password"
              placeholder="Confirm new password"
              required
            />
            <span className="signup__filed--error">
              {formik.touched.confirm_re_password &&
                formik.errors.confirm_re_password
                ? formik.errors.confirm_re_password
                : EMPTY}{" "}
            </span>
          </div>
          <div className="pl-3 pr-3 mt-3">
            <p className="link">
              {!credential && (
                <Link to="/auth/login">
                  <i className="fas fa-long-arrow-alt-left"></i> Back to login
                </Link>
              )}
            </p>
          </div>
          <div className="pt-3 pb-3 row p-2">
            <button
              disabled={
                formik.isSubmitting ||
                !formik.isValid ||
                formik.values.re_password === EMPTY ||
                formik.values.confirm_re_password === EMPTY
              }
              type="submit"
              className="btn btn-primary w-100 p-2"
            >
              {!formik.isSubmitting ? (
                "Confirm"
              ) : (
                <div className="dots-loading">
                  <div></div>
                  <div></div>
                  <div></div>
                  <div></div>
                </div>
              )}
            </button>
            <ToastContainer />
          </div>
        </form>
      </div>
    </div>
  );
}

export default ResetPassword;
