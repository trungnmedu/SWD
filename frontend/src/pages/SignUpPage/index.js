import { useFormik } from "formik";
import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import * as yup from "yup";

import {
  EMPTY,
  PHONE_PATTERN,
  REQUEST_EMAIL,
  REQUEST_PASSWORD,
  REQUIRED_EMAIL,
  REQUIRED_PASSWORD,
  TOAST_CONFIG
} from "../../constants/default";
import { INTERNAL_SERVER_ERROR } from "../../constants/error-message";
import { registerUser } from "../../services/auth.service";
import "./style.scss";

const validation = yup.object({
  email: yup
    .string(REQUEST_EMAIL)
    .email("Enter a valid email")
    .required(REQUIRED_EMAIL),
  password: yup
    .string(REQUEST_PASSWORD)
    .min(8, "Password should be of minimum 8 characters length")
    .required(REQUIRED_PASSWORD),
  confirmPassword: yup
    .string("Enter your confirm password")
    .required("Confirm password is required")
    .oneOf([yup.ref("password"), null], "Confirm password not matches"),
  fullName: yup.string("Enter your fullname").required("Fullname is required"),
  phone: yup
    .string("Enter your phone")
    .matches(PHONE_PATTERN, "Phone number is not valid"),
});

function SignUpPage() {
  const navigate = useNavigate();
  const formik = useFormik({
    initialValues: {
      email: EMPTY,
      password: EMPTY,
      confirmPassword: EMPTY,
      fullName: EMPTY,
      phone: EMPTY,
    },
    validationSchema: validation,
    onSubmit: async (values) => {
      const data = JSON.stringify(values);
      await registerUser(data)
        .then((res) => {
          if (res) {
            toast.success("Register account successfully.", TOAST_CONFIG);
            navigate("/auth/login");
          }
        })
        .catch((error) => {
          toast.error(
            error.response.status >= 500
              ? INTERNAL_SERVER_ERROR
              : error.response.data,
            TOAST_CONFIG
          );
        });
    },
  });

  return (
    <div className="row align-items-center justify-content-center text-center">
      <div className="col-4 pt-5">
        <h3 className=" bold size-4 pt-3">Register</h3>
        <form className="mt-5" onSubmit={formik.handleSubmit}>
          <div className="row p-2">
            <label
              htmlFor="signup-email"
              className="text-start"
              style={{ paddingLeft: 0 }}
            >
              Email*
            </label>
            <span className="col-1 lh-44 signup__icon-wrapper">
              <i className="fas fa-envelope"></i>
            </span>
            <input
              id="signup-email"
              onBlur={formik.handleBlur}
              value={formik.values.email}
              name="email"
              onChange={formik.handleChange}
              className="col-11 outline-none p-2 signup__input-border"
              type="text"
              placeholder="Email"
              required
            />
            <span className="signup__filed--error">
              {formik.touched.email && formik.errors.email
                ? formik.errors.email
                : EMPTY}{" "}
            </span>
          </div>
          <div className="row p-2 pt-0">
            <label
              htmlFor="signup-password"
              className="text-start"
              style={{ paddingLeft: 0 }}
            >
              Password*
            </label>
            <span className="col-1 lh-44 signup__icon-wrapper">
              <i className="fas fa-lock"></i>
            </span>
            <input
              id="signup-password"
              onBlur={formik.handleBlur}
              value={formik.values.password}
              name="password"
              onChange={formik.handleChange}
              className="col-11 outline-none p-2 signup__input-border"
              type="password"
              placeholder="Password"
              required
            />
            <span className="signup__filed--error">
              {formik.touched.password && formik.errors.password
                ? formik.errors.password
                : EMPTY}{" "}
            </span>
          </div>
          <div className="row p-2 pt-0">
            <label
              htmlFor="signup-password-confirm"
              className="text-start"
              style={{ paddingLeft: 0 }}
            >
              Confirm Password*
            </label>
            <span className="col-1 lh-44 signup__icon-wrapper">
              <i className="fas fa-lock"></i>
            </span>
            <input
              id="signup-password-confirm"
              onBlur={formik.handleBlur}
              value={formik.values.confirmPassword}
              name="confirmPassword"
              onChange={formik.handleChange}
              className="col-11 outline-none p-2 signup__input-border"
              type="password"
              placeholder="Input password again"
              required
            />
            <span className="signup__filed--error">
              {formik.touched.confirmPassword && formik.errors.confirmPassword
                ? formik.errors.confirmPassword
                : EMPTY}{" "}
            </span>
          </div>
          <div className="row p-2 pt-0">
            <label
              htmlFor="signup-fullname"
              className="text-start"
              style={{ paddingLeft: 0 }}
            >
              Fullname*
            </label>
            <span className="col-1 lh-44 signup__icon-wrapper">
              <i className="fas fa-address-card"></i>
            </span>
            <input
              id="signup-fullname"
              onBlur={formik.handleBlur}
              value={formik.values.fullName}
              name="fullName"
              onChange={formik.handleChange}
              className="col-11 outline-none p-2 signup__input-border"
              type="text"
              placeholder="Fullname"
            />
            <span className="signup__filed--error">
              {formik.touched.fullName && formik.errors.fullName
                ? formik.errors.fullName
                : EMPTY}{" "}
            </span>
          </div>
          <div className="row p-2 pt-0">
            <label
              htmlFor="signup-phone"
              className="text-start"
              style={{ paddingLeft: 0 }}
            >
              Phone
            </label>
            <span className="col-1 lh-44 signup__icon-wrapper">
              <i className="fas fa-phone-alt"></i>
            </span>
            <input
              id="signup-phone"
              onBlur={formik.handleBlur}
              value={formik.values.phone}
              name="phone"
              onChange={formik.handleChange}
              className="col-11 outline-none p-2 signup__input-border"
              type="text"
              placeholder="Phone number"
            />
            <span className="signup__filed--error">
              {formik.touched.phone && formik.errors.phone
                ? formik.errors.phone
                : EMPTY}{" "}
            </span>
          </div>
          <div className="pl-3 pr-3 mt-3">
            <p className="link">
              Already have an account? <Link to="/auth/login">Login</Link>
            </p>
          </div>
          <div className="pt-3 pb-3">
            <button
              type="submit"
              className="btn btn-primary w-100 p-2"
              disabled={
                formik.isSubmitting ||
                !formik.isValid ||
                formik.values.email === EMPTY ||
                formik.values.password === EMPTY ||
                formik.values.confirmPassword === EMPTY ||
                formik.values.fullName === EMPTY
              }
            >
              {!formik.isSubmitting ? (
                "Sign Up"
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

export default SignUpPage;
