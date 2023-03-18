import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { toast, ToastContainer } from "react-toastify";

import { TOAST_CONFIG } from "../../constants/default";
import { INTERNAL_SERVER_ERROR } from "../../constants/error-message";
import { decrypt, encryptKey } from "../../helpers/crypto.helper";
import ProfileUpdatePasswordModal from "../../modals/ProfileUpdatePasswordModal";
import { logout } from "../../services/auth.service";
import DisableScreen from "../DisableScreen";
import Modal, { useModal } from "../Modal";
import "./style.scss";

function Header({ auth }) {
  const [isLoggingOut, setIsLoggingOut] = useState(false);
  const [showUpdatePasswordModal, setShowUpdatePasswordModal] = useModal(false);
  const popupFeatures = [
    {
      title: "Profile",
      icon: "fas fa-info-circle",
      click: () => {
        const account = decrypt(localStorage.getItem(encryptKey("credential")));
        const role = account?.role;
        switch (role) {
          case "admin":
            navigate("/admin/me");
            break;
          case "owner":
            navigate("/owner/me");
            break;
          default:
            navigate("/me");
        }
      },
    },
    {
      title: "Change Password",
      icon: "fas fa-lock",
      click: () => {
        setShowUpdatePasswordModal();
      },
    },
    {
      title: "Log Out",
      icon: "fas fa-sign-out-alt",
      click: () => {
        setIsLoggingOut(true);
        logout()
          .then((res) => {
            if (res) {
              toast.success("Logout successfully.", TOAST_CONFIG);
              localStorage.clear();
              navigate("/auth/login");
            }
          })
          .catch((error) => {
            toast.error(INTERNAL_SERVER_ERROR, TOAST_CONFIG);
          })
          .finally(() => {
            setIsLoggingOut(false);
          });
      },
    },
  ];
  const [showPopup, setShowPopup] = useState(false);
  const navigate = useNavigate();

  const hidePopup = (e) => {
    if (
      !document.getElementById("header-popup")?.contains(e.target) &&
      !document.getElementById("header-avatar")?.contains(e.target)
    ) {
      setShowPopup(false);
    }
  };

  useEffect(() => {
    window.addEventListener("click", hidePopup);
    return () => window.removeEventListener("click", hidePopup);
  }, []);

  return (
    <div className="header">
      <Modal
        isShowing={showUpdatePasswordModal}
        hide={setShowUpdatePasswordModal}
      >
        <ProfileUpdatePasswordModal toggleModal={setShowUpdatePasswordModal} />
      </Modal>
      {isLoggingOut && <DisableScreen />}
      <Link to="/" className="d-flex align-content-center nav-brand">
        <span className="p-2 size-2 ps-4 pe-3">
          <i className="fas fa-basketball-ball"></i>
        </span>
        <b className="home-title">Basketball Playground</b>
      </Link>
      {auth ? (
        <div
          id="header-avatar"
          className="size-2 position-fixed top-1 right-1 profile-icon"
          onClick={() => setShowPopup(() => !showPopup)}
        >
          <i className="far fa-user-circle"></i>
        </div>
      ) : (
        <div className="size-2 position-fixed top-1 right-1 unauth-group">
          <Link to="/auth/login">Login</Link>
          <Link to="/auth/signup" style={{ marginLeft: "1.5rem" }}>
            Sign Up
          </Link>
        </div>
      )}
      {showPopup && (
        <div id="header-popup" className="profile-popup">
          {popupFeatures.map((feature) => {
            return (
              <div
                className="profile__popup-feature"
                onClick={() => {
                  feature.click();
                  setShowPopup(false);
                }}
                key={feature.title}
              >
                <i className={feature.icon + " p-2"}></i>
                <span>{feature.title}</span>
              </div>
            );
          })}
        </div>
      )}

      <ToastContainer />
    </div>
  );
}

export default Header;
