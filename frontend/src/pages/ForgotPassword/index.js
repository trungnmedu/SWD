import { useFormik } from "formik";
import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import * as yup from "yup";

import {
  EMPTY,
  REQUEST_EMAIL,
  REQUEST_PASSWORD, REQUIRED_CODE, REQUIRED_EMAIL, TOAST_CONFIG
} from "../../constants/default";
import { INTERNAL_SERVER_ERROR } from "../../constants/error-message";
import { encryptKey } from "../../helpers/crypto.helper";
import { sendForgotPassword, verifyForgotPassword } from "../../services/auth.service";
import "./style.scss";

const validation = yup.object({
  re_email: yup
    .string(REQUEST_EMAIL)
    .email("Enter a valid email")
    .required(REQUIRED_EMAIL),
  code: yup
    .string(REQUEST_PASSWORD)
    .length(6, "Code should be 6 characters length")
    .required(REQUIRED_CODE),
});

function ForgotPassword() {
  const navigate = useNavigate();

  const formik = useFormik({
    initialValues: {
      re_email: EMPTY,
      code: EMPTY,
    },
    validationSchema: validation,
    onSubmit: async (values) => {
      const data = JSON.stringify({
        email: values.re_email,
        otpCode: values.code,
      });
      await verifyForgotPassword(data)
        .then((res) => {
          localStorage.setItem(encryptKey("temporaryToken"), res.token);
          navigate("/auth/reset-password");
        })
        .catch((error) => {
          toast.error(
            error.response.data.message || INTERNAL_SERVER_ERROR,
            TOAST_CONFIG
          );
        });
    },
  });

  const requestCode = async () => {
    const data = JSON.stringify({
      email: formik.values.re_email,
    });

    toast.info("Code will be sent soon.", TOAST_CONFIG);

    await sendForgotPassword(data)
      .then((res) => {
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
    <div className="row align-items-center justify-content-center text-center">
      <div className="col-4 pt-5">
        <h3 className=" bold size-4 pt-5">Forgot Password</h3>
        <i className="fas fa-lock size-5 mt-4"></i>
        <form className="mt-5" onSubmit={formik.handleSubmit}>
          <div className="row p-2">
            <label
              htmlFor="email"
              className="text-start"
              style={{ paddingLeft: 0 }}
            >
              Email
            </label>
            <span className="col-1 lh-44 signup__icon-wrapper">
              <i className="fas fa-envelope"></i>
            </span>
            <input
              id="email"
              name="re_email"
              value={formik.values.re_email}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              className="col-8 outline-none p-2 fg-pw__input-border"
              type="text"
              placeholder="Enter your registered mail to receive code"
              required
            />
            <button
              onClick={requestCode}
              className="col-3 lh-44 fg-pw__icon-wrapper"
              disabled={!formik.values.re_email}
            >
              Receive code
            </button>
            <span className="signup__filed--error">
              {formik.touched.re_email && formik.errors.re_email
                ? formik.errors.re_email
                : EMPTY}{" "}
            </span>
          </div>
          <div className="row p-2" title="Resend code">
            <label
              htmlFor="code"
              className="text-start"
              style={{ paddingLeft: 0 }}
            >
              Code
            </label>
            <span className="col-1 lh-44 signup__icon-wrapper">
              <i className="fas fa-lock"></i>
            </span>
            <input
              id="code"
              name="code"
              value={formik.values.code}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              className="col-11 outline-none p-2 signup__input-border"
              type="text"
              placeholder="Enter 6-digits code"
            />
            <span className="signup__filed--error">
              {formik.touched.code && formik.errors.code
                ? formik.errors.code
                : EMPTY}{" "}
            </span>
          </div>
          <div className="pl-3 pr-3 mt-3">
            <p className="link">
              <Link to="/auth/login">
                <i classname="fas fa-long-arrow-alt-left"></i> Back to login
              </Link>
            </p>
          </div>
          <div className="pt-3 pb-3">
            <button
              type="submit"
              className="btn btn-primary w-100 p-2"
              disabled={
                formik.isSubmitting ||
                !formik.isValid ||
                formik.values.re_email === EMPTY ||
                formik.values.code === EMPTY
              }
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

export default ForgotPassword;
