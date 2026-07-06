import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import DashboardPage from "./pages/DashboardPage";
import OAuthSuccessPage from "./pages/OAuthSuccessPage";
import EquipmentPage from "./pages/EquipmentPage";
import ProtectedRoute from "./components/ProtectedRoute";
import BookingPage from "./pages/BookingPage";
import MaintenancePage from "./pages/MaintenancePage";
import SharingRequestPage from "./pages/SharingRequestPage";
import NotificationPage from "./pages/NotificationPage";

function App() {
  return (
    <Routes>
      {/* Default route */}
      <Route path="/" element={<Navigate to="/login" replace />} />

      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/oauth-success" element={<OAuthSuccessPage />} />

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
    path="/maintenance"
    element={
        <ProtectedRoute>
            <MaintenancePage />
        </ProtectedRoute>
    }
/>

      <Route
        path="/bookings"
        element={
          <ProtectedRoute>
            <BookingPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/sharing-requests"
        element={
            <ProtectedRoute>
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

    </Routes>

  

  );
}

export default App;