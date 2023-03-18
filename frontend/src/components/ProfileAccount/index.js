import { EMPTY, TOAST_CONFIG } from "../../constants/default";
import "./style.scss";

import { useFormik } from "formik";
import { useEffect, useState } from "react";
import { toast, ToastContainer } from "react-toastify";
import * as yup from "yup";
import user from "../../assets/images/user-1.png";
import { PHONE_PATTERN } from "../../constants/regex";
import { decrypt, encrypt, encryptKey } from "../../helpers/crypto.helper";
import { updateProfile } from "../../services/me.service";

const validation = yup.object({
  fullName: yup
    .string("Enter your full name")
    .required("Full name is required")
    .strict(true),
  phone: yup
    .string("Enter your phone")
    .matches(PHONE_PATTERN, "Phone number is not valid"),
});

function ProfileAccount() {
  const [currentUser] = useState(() => {
    return decrypt(localStorage.getItem(encryptKey("credential")));
  });

  const [avatarImage, setAvatarImage] = useState({
    file: null,
    url: null,
    preview: null,
  });

  useEffect(() => {
    if (currentUser) {
      setAvatarImage((previousAvatar) => ({
        ...previousAvatar,
        url: currentUser?.avatar,
      }));
    }
  }, [currentUser]);

  const handleUploadAvatarOnChange = (file) => {
    if (file) {
      setAvatarImage((previousAvatar) => {
        if (previousAvatar.preview) {
          URL.revokeObjectURL(previousAvatar.preview);
        }
        return {
          ...previousAvatar,
          file: file,
          preview: file ? URL.createObjectURL(file) : "",
        };
      });
    }
  };

  const handleUpdateProfile = async (values) => {
    try {
      const data = { phone: values.phone, fullName: values.fullName };
      const response = await updateProfile(avatarImage.file, JSON.stringify(data));
      toast.success("Update profile success!", TOAST_CONFIG)
      localStorage.removeItem(encryptKey("credential"));
      localStorage.setItem(encryptKey("credential"), encrypt(response));
    } catch (error) {
      toast.error(error.response.data.message, TOAST_CONFIG);
    }
  };

  const formik = useFormik({
    initialValues: {
      email: currentUser?.email ? currentUser.email : EMPTY,
      fullName: currentUser?.fullName ? currentUser.fullName : EMPTY,
      phone: currentUser?.phone ? currentUser.phone : EMPTY,
    },
    validateOnMount: true,
    validateOnChange: true,
    validationSchema: validation,
    onSubmit: handleUpdateProfile,
  });

  return (
    <div className="container profile-wrapper">
      <div className="d-flex w-100">
        <div className="col-4">
          <div className="profile-avatar-wrapper">
            <img
              className="mb-2 profile-avatar__img color-blur rounded-circle"
              src={avatarImage.preview || avatarImage.url || user}
              alt="Your avatar"
            />
            <div className="profile-avatar__upload-lable">
              <input
                className="outline-none custom-bg-input p-0 w-100"
                type="file"
                onChange={(e) => {
                  handleUploadAvatarOnChange(e.target.files[0]);
                }}
              />
            </div>
          </div>
        </div>
        <div className="col-8 p-4 ps-5">
          <div className="profile-info-wrapper">
            <h4 className="text-center">PROFILE USER</h4>
            <div>
              <form id="update-profile-form" onSubmit={formik.handleSubmit}>
                <div className="row p-1">
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
                    className="col-11 outline-none p-2 signup__input-border"
                    type="text"
                    placeholder="Email"
                    name="email"
                    onChange={formik.handleChange}
                    value={formik.values.email}
                    readOnly
                  />
                </div>
                <div className="row p-1">
                  <label
                    htmlFor="fullname"
                    className="text-start"
                    style={{ paddingLeft: 0 }}
                  >
                    Full Name
                  </label>
                  <span className="col-1 lh-44 signup__icon-wrapper">
                    <i className="fas fa-address-card"></i>
                  </span>
                  <input
                    className="col-11 outline-none p-2 signup__input-border"
                    type="text"
                    name="fullName"
                    value={formik.values.fullName}
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                    placeholder="Full name"
                  />
                  <span className="profile__filed--error">
                    {formik.touched.fullName && formik.errors.fullName
                      ? formik.errors.fullName
                      : EMPTY}
                    {""}
                  </span>
                </div>
                <div className="row p-1">
                  <label
                    htmlFor="phone"
                    className="text-start"
                    style={{ paddingLeft: 0 }}
                  >
                    Phone
                  </label>
                  <span className="col-1 lh-44 signup__icon-wrapper">
                    <i className="fas fa-phone-alt"></i>
                  </span>
                  <input
                    className="col-11 outline-none p-2 signup__input-border"
                    type="text"
                    name="phone"
                    value={formik.values.phone}
                    onChange={formik.handleChange}
                    onBlur={formik.handleBlur}
                    placeholder="Phone number"
                  />
                  <span className="profile__filed--error">
                    {formik.touched.phone && formik.errors.phone
                      ? formik.errors.phone
                      : EMPTY}{" "}
                  </span>
                </div>
                <div className="row mt-4 p-1">
                  <button
                    className="btn btn-primary w-25 mt-2"
                    disabled={!formik.isValid || formik.isSubmitting}
                    type="submit"
                    style={{ height: 46 }}
                  >
                    Save
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
      <ToastContainer />
    </div>
  );
}

export default ProfileAccount;
