import { BrowserRouter as Router, Route, Routes } from "react-router-dom";

import "./App.scss";
import AccountVerification from "./components/AccountVerification";
import BookingWidget from "./components/BookingWidget";
import DashboardFeatures from "./components/DashboardFeatures";
import HistoryWidget from "./components/HistoryWidget";
import IncomingMatchesWidget from "./components/IncomingMatchesWidget";
import ManageUsersWidget from "./components/ManageUsersWidget";
import ManageYardsWidget from "./components/ManageYardsWidget";
import MatchManagementWidget from "./components/MatchManagementWidget";
import ProfileAccount from "./components/ProfileAccount";
import VoucherManagementWidget from "./components/VoucherManagementWidget";
import WelcomeWidget from "./components/WelcomeWidget";
import Yard from "./components/Yard";
import YardDetails from "./components/YardDetails";
import YardRatingWidget from "./components/YardRatingWidget";
import AuthPage from "./pages/AuthPage";
import ForgotPassword from "./pages/ForgotPassword";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import NotFoundPage from "./pages/NotFound";
import OwnerPage from "./pages/OwnerPage";
import ResetPassword from "./pages/ResetPassword";
import SignUpPage from "./pages/SignUpPage";
import WelcomePage from "./pages/WelcomePage";
import Statistics from "./components/Statistics";
import ManageReportsWidget from "./components/ManageReportsWidget";

function App() {
  return (
    <Router>
      <div className="app-container">
        <Routes>
          <Route exact path="/" element={<WelcomePage />}>
            <Route exact path="/" element={<WelcomeWidget />}></Route>
            <Route path="/yard/:id" element={<Yard />}></Route>
            <Route
              path="/incoming-matches"
              element={<IncomingMatchesWidget />}
            ></Route>
            <Route path="/rating" element={<YardRatingWidget />}></Route>
            <Route
              path="/verification"
              element={<AccountVerification />}
            ></Route>
            <Route path="/history" element={<HistoryWidget />}></Route>
            <Route path="/me" element={<ProfileAccount />}></Route>
            <Route
              exact
              path="reset-password"
              element={<ResetPassword />}
            ></Route>
          </Route>

          <Route exact path="/auth" element={<AuthPage />}>
            <Route exact path="/auth/login" element={<LoginPage />}></Route>
            <Route exact path="/auth/signup" element={<SignUpPage />}></Route>
            <Route
              exact
              path="/auth/forgot-password"
              element={<ForgotPassword />}
            ></Route>
            <Route
              exact
              path="/auth/reset-password"
              element={<ResetPassword />}
            ></Route>
          </Route>

          <Route exact path="/owner" element={<OwnerPage />}>
            <Route
              exact
              path="/owner"
              element={<DashboardFeatures role="owner" />}
            ></Route>
            <Route path="/owner/yards" element={<ManageYardsWidget />}></Route>
            <Route path="/owner/yards/:id" element={<YardDetails />}></Route>
            <Route
              path="/owner/voucher-management"
              element={<VoucherManagementWidget />}
            ></Route>
            <Route path="/owner/history" element={<HistoryWidget />}></Route>
            <Route
              path="/owner/booking-management"
              element={<MatchManagementWidget />}
            ></Route>

            <Route path="/owner/statistics" element={<Statistics />}></Route>
            <Route path="/owner/me" element={<ProfileAccount />}></Route>
            <Route
              exact
              path="reset-password"
              element={<ResetPassword />}
            ></Route>
          </Route>

          <Route exact path="/admin" element={<HomePage />}>
            <Route
              exact
              path="/admin"
              element={<DashboardFeatures role="admin" />}
            ></Route>
            <Route path="/admin/booking" element={<BookingWidget />}></Route>
            <Route
              path="/admin/incoming-matches"
              element={<IncomingMatchesWidget />}
            ></Route>
            <Route path="/admin/users" element={<ManageUsersWidget />}></Route>
            <Route
              path="/admin/reports"
              element={<ManageReportsWidget />}
            ></Route>
            <Route path="/admin/me" element={<ProfileAccount />}></Route>
            <Route
              exact
              path="reset-password"
              element={<ResetPassword />}
            ></Route>
          </Route>
          <Route path="*" element={<NotFoundPage />}></Route>
        </Routes>
      </div>
    </Router>
  );
}

export default App;
