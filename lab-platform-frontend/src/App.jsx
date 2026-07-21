import { Routes, Route, Navigate } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import DashboardPage from "./pages/DashboardPage";
import OAuthSuccessPage from "./pages/OAuthSuccessPage";
import EquipmentPage from "./pages/EquipmentPage";
import BookingPage from "./pages/BookingPage";
import MaintenancePage from "./pages/MaintenancePage";
import SharingRequestPage from "./pages/SharingRequestPage";
import NotificationPage from "./pages/NotificationsPage";
import UsersPage from "./pages/UsersPage";
import LaboratoryPage from "./pages/LaboratoryPage";
import InstitutionPage from "./pages/InstitutionPage";

import ProtectedRoute from "./components/ProtectedRoute";

function App() {
  return (
    <>
      <Routes>

        <Route
          path="/"
          element={<Navigate to="/login" replace />}
        />

        <Route
          path="/login"
          element={<LoginPage />}
        />

        <Route
          path="/register"
          element={<RegisterPage />}
        />

        <Route
          path="/oauth-success"
          element={<OAuthSuccessPage />}
        />

        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/equipment"
          element={
            <ProtectedRoute>
              <EquipmentPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/bookings"
          element={
            <ProtectedRoute
              allowedRoles={[
                "RESEARCHER",
                "LAB_MANAGER",
                "DEPARTMENT_HEAD",
                "INSTITUTION_ADMIN",
                "SYSTEM_ADMIN"
              ]}
            >
              <BookingPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/maintenance"
          element={
            <ProtectedRoute
              allowedRoles={[
                "LAB_TECHNICIAN",
                "LAB_MANAGER",
                "INSTITUTION_ADMIN",
                "SYSTEM_ADMIN"
              ]}
            >
              <MaintenancePage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/sharing-requests"
          element={
            <ProtectedRoute
              allowedRoles={[
                "RESEARCHER",
                "LAB_MANAGER",
                "DEPARTMENT_HEAD",
                "INSTITUTION_ADMIN",
                "SYSTEM_ADMIN"
              ]}
            >
              <SharingRequestPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/notifications"
          element={
            <ProtectedRoute>
              <NotificationPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/users"
          element={
            <ProtectedRoute
              allowedRoles={[
                "SYSTEM_ADMIN",
                "INSTITUTION_ADMIN",
                "DEPARTMENT_HEAD"
              ]}
            >
              <UsersPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/laboratories"
          element={
            <ProtectedRoute
              allowedRoles={[
                "SYSTEM_ADMIN",
                "INSTITUTION_ADMIN"
              ]}
            >
              <LaboratoryPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/institutions"
          element={
            <ProtectedRoute
              allowedRoles={[
                "SYSTEM_ADMIN"
              ]}
            >
              <InstitutionPage />
            </ProtectedRoute>
          }
        />

      </Routes>

      <ToastContainer
        position="top-right"
        autoClose={2500}
        hideProgressBar={false}
        newestOnTop
        closeOnClick
        pauseOnHover
        draggable
        theme="colored"
      />
    </>
  );
}

export default App;