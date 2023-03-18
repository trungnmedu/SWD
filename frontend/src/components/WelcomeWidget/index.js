import { Slide } from "react-slideshow-image";

import { useState } from "react";
import { Navigate } from "react-router-dom";
import yard2 from "../../assets/images/3.jpg";
import yard1 from "../../assets/images/6.jpg";
import yard3 from "../../assets/images/9.jpg";
import BookingWidget from "../../components/BookingWidget";
import Footer from "../../components/Footer";
import { decrypt, encryptKey } from "../../helpers/crypto.helper";
import DashboardFeatures from "../DashboardFeatures";

const slideImages = [
  {
    url: yard1,
    caption:
      "“One man can be a crucial ingredient on a team, but one man cannot make a team”",
  },
  { url: yard2, caption: "“Eat, sleep and live basketball…”" },
  { url: yard3, caption: "“Booking faster, more convenience”" },
];

function WelcomeWidget() {
  const [auth, setAuth] = useState(false);
  const credential = localStorage.getItem(encryptKey("credential"));

  useState(() => {
    setAuth(() => (credential ? true : false));
  }, []);

  if (credential && !decrypt(credential).isConfirm) {
    return <Navigate to="/verification" />;
  }

  return (
    <>
      <Slide>
        {slideImages.map((slideImage, index) => (
          <div className="slide-container" key={index}>
            <div style={{ backgroundImage: `url(${slideImage.url})` }}>
              <span>{slideImage.caption}</span>
            </div>
          </div>
        ))}
      </Slide>
      {auth && <DashboardFeatures role="user" />}
      {/* <OutstandingYard /> */}
      <BookingWidget />
      <Footer />
    </>
  );
}

export default WelcomeWidget;
