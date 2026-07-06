import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import api from "../api/axios";
import "./Dashboard.css";
import DashboardCard from "../components/DashboardCard";
import EquipmentPieChart from "../components/EquipmentPieChart";
import BookingBarChart from "../components/BookingBarChart";
import NotificationPanel from "../components/NotificationPanel";

import {
  FaFlask,
  FaClipboardList,
  FaShareAlt,
  FaBell,
  FaTools,
  FaClock
} from "react-icons/fa";

function DashboardPage() {
  const navigate = useNavigate();

  const [stats, setStats] = useState({
    totalEquipment: 0,
    availableEquipment: 0,
    bookedEquipment: 0,
    maintenanceEquipment: 0,

    totalBookings: 0,
    approvedBookings: 0,
    pendingBookings: 0,

    totalSharingRequests: 0,

    totalMaintenanceRecords: 0,

    unreadNotifications: 0,

    totalUtilizationHours: 0
  });

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {

        const response = await api.get("/analytics/dashboard");

        console.log("Analytics Response:", response.data);

        setStats(response.data);

    } catch (error) {

        console.log(error.response);

        console.error("Error loading dashboard data:", error);

    }
};

  return (
    <>
      <Navbar />

      <div className="dashboard">

        <h1>Lab Resource Utilization Platform</h1>

        <p>Welcome to the Dashboard</p>

        <div className="dashboard-container">

          <DashboardCard
            title="Equipment"
            value={stats.totalEquipment}
            icon={<FaFlask />}
          />

          <DashboardCard
            title="Bookings"
            value={stats.totalBookings}
            icon={<FaClipboardList />}
          />

          <DashboardCard
            title="Sharing Requests"
            value={stats.totalSharingRequests}
            icon={<FaShareAlt />}
          />

          <DashboardCard
            title="Notifications"
            value={stats.unreadNotifications}
            icon={<FaBell />}
          />

          <DashboardCard
            title="Maintenance"
            value={stats.totalMaintenanceRecords}
            icon={<FaTools />}
          />

          <DashboardCard
            title="Utilization Hours"
            value={Number(stats.totalUtilizationHours).toFixed(2)}
            icon={<FaClock />}
          />

        </div>

        <div className="charts-section">

          <EquipmentPieChart stats={stats} />

          <BookingBarChart stats={stats} />

        </div>

          <div className="notification-section">

          <NotificationPanel />

        </div>

        <div className="cards">

          <div className="card">
            <h2>📦 Equipment Management</h2>

            <p>
              Add, update, search and manage laboratory equipment.
            </p>

            <button onClick={() => navigate("/equipment")}>
              Open Equipment
            </button>
          </div>

          <div className="card">
            <h2>📅 Booking Management</h2>

            <p>
              Book laboratory equipment and manage your bookings.
            </p>

            <button onClick={() => navigate("/bookings")}>
              Open Bookings
            </button>
          </div>

        </div>

      </div>
    </>
  );
}

export default DashboardPage;