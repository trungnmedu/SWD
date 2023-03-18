import React from "react";
import notFound from "../../assets/images/notfound.jpg";

function NotFoundPage() {
  return (
    <div className="row align-items-center justify-content-center">
      <img className="w-50" src={notFound} alt="not found page" />
    </div>
  );
}

export default NotFoundPage;
