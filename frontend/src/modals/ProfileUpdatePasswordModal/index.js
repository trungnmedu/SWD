import { useFormik } from "formik";
import { useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { toast, ToastContainer } from "react-toastify";
import * as yup from "yup";
import {
  EMPTY,
  REQUEST_PASSWORD,
  REQUIRED_PASSWORD, TOAST_CONFIG
} from "../../constants/default";
import { encryptKey } from "../../helpers/crypto.helper";
import { changePasswordRequest } from "../../services/me.service";
const ProfileUpdatePasswordModal = ({ toggleModal }) => {
  const navigate = useNavigate();

  const handleChangePasswordRequest = useCallback(async (values) => {
    try {
      const response = await changePasswordRequest(values);
      localStorage.setItem(encryptKey("temporaryToken"), response.token);
      toggleModal();
      navigate("reset-password");
    } catch (error) {
      toast.error(error?.response?.data?.message, {
        ...TOAST_CONFIG,
        containerId: "update-password-modal",
      });
    }
  }, []);

  const validation = yup.object({
    password: yup.string(REQUEST_PASSWORD).required(REQUIRED_PASSWORD),
  });

  const formik = useFormik({
    initialValues: { password: EMPTY },
    validateOnMount: true,
    validateOnChange: true,
    validationSchema: validation,
    onSubmit: async (values) => {
      formik.setSubmitting(true);
      await handleChangePasswordRequest(values);
      formik.setSubmitting(false);
    },
  });

  return (
    <div
      className="rounded border border-3 custom-confirm"
      style={{ width: 500 }}
    >
      <form className="my-3" onSubmit={formik.handleSubmit}>
        <div className="row p-1">
          <label
            htmlFor="old-password"
            className="text-start"
            style={{ paddingLeft: 0 }}
          >
            Current Password
          </label>
          <span className="col-1 lh-44 signup__icon-wrapper">
            <i className="fas fa-user-lock"></i>
          </span>
          <input
            className="col-11 outline-none p-2 signup__input-border"
            type="password"
            placeholder="Enter current password"
            name="password"
            value={formik.values.password}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
          />
          <span className="signup__filed--error">
            {formik.touched.password && formik.errors.password
              ? formik.errors.password
              : EMPTY}{" "}
          </span>
        </div>
        <div className="mt-3 row p-1">
          <button
            disabled={!formik.isValid | formik.isSubmitting}
            type="submit"
            className="btn btn-primary me-3 col-3"
          >
            Confirm
          </button>
          <div className="btn btn-light col-3" onClick={toggleModal}>
            Cancel
          </div>
        </div>
      </form>
      <ToastContainer containerId="update-password-modal" />
    </div>
  );
};

export default ProfileUpdatePasswordModal;
