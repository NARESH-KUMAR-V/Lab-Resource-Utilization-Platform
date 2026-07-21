import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Layout from "../components/Layout";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";

import DashboardCard from "../components/DashboardCard";
import EquipmentPieChart from "../components/EquipmentPieChart";
import BookingBarChart from "../components/BookingBarChart";

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
  const { role } = useAuth();

  const [stats, setStats] = useState({
    totalEquipment: 0,
    availableEquipment: 0,
    bookedEquipment: 0,
    maintenanceEquipment: 0,

    totalBookings: 0,
    approvedBookings: 0,
    pendingBookings: 0,
    rejectedBookings: 0,

    totalSharingRequests: 0,

    totalMaintenanceRecords: 0,

    unreadNotifications: 0,

    totalUtilizationHours: 0
  });

  useEffect(() => {
    loadDashboardData();
  }, [role]);

  const loadDashboardData = async () => {

    try {

      let response;

      if (role === "RESEARCHER") {

        response = await api.get("/analytics/my-dashboard");

      } else {

        response = await api.get("/analytics/dashboard");

      }

      console.log("Analytics Response:", response.data);

      setStats(response.data);

    } catch (error) {

      console.error(error);

    }

  };

  return (

    <Layout>

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
            title={role === "RESEARCHER" ? "My Bookings" : "Bookings"}
            value={stats.totalBookings}
            icon={<FaClipboardList />}
          />

          <DashboardCard
            title={
              role === "RESEARCHER"
                ? "Pending Bookings"
                : "Sharing Requests"
            }
            value={
              role === "RESEARCHER"
                ? stats.pendingBookings
                : stats.totalSharingRequests
            }
            icon={<FaShareAlt />}
          />

          <DashboardCard
            title="Notifications"
            value={stats.unreadNotifications}
            icon={<FaBell />}
          />

          <DashboardCard
            title={
              role === "RESEARCHER"
                ? "Approved Bookings"
                : "Maintenance"
            }
            value={
              role === "RESEARCHER"
                ? stats.approvedBookings
                : stats.totalMaintenanceRecords
            }
            icon={<FaTools />}
          />

          <DashboardCard
            title={
              role === "RESEARCHER"
                ? "Rejected Bookings"
                : "Utilization Hours"
            }
            value={
              role === "RESEARCHER"
                ? stats.rejectedBookings
                : Number(stats.totalUtilizationHours).toFixed(2)
            }
            icon={<FaClock />}
          />

        </div>

        <div className="charts-section">

          <EquipmentPieChart stats={stats} />

          <BookingBarChart stats={stats} />

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

    </Layout>

  );

}

export default DashboardPage;