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
  FaClock,
  FaUserCheck,
  FaExclamationTriangle,
  FaCertificate,
  FaChartBar,
  FaShieldAlt,
  FaLightbulb,
  FaReceipt,
  FaFileDownload
} from "react-icons/fa";

function DashboardPage() {

  const navigate = useNavigate();
  const { role, user } = useAuth();

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
    totalUtilizationHours: 0,
    totalBilledAmount: 0,
    pendingUserRequests: 0,

    recommendations: []
  });

  const [maintenanceDashboard, setMaintenanceDashboard] = useState(null);

  const isManager = ["LAB_MANAGER", "DEPARTMENT_HEAD", "INSTITUTION_ADMIN", "SYSTEM_ADMIN"].includes(role);

  useEffect(() => {
    loadDashboardData();
    if (isManager) {
      loadMaintenanceDashboard();
    }
  }, [role]);

  const loadDashboardData = async () => {
    try {
      let response;
      if (role === "RESEARCHER") {
        response = await api.get("/analytics/my-dashboard");
      } else {
        response = await api.get("/analytics/dashboard");
      }
      setStats(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  const loadMaintenanceDashboard = async () => {
    try {
      const response = await api.get("/maintenance/dashboard");
      setMaintenanceDashboard(response.data);
    } catch (error) {
      console.warn("Maintenance dashboard not available:", error);
    }
  };

  return (

    <Layout>

      <div className="dashboard">

        {/* Hero Header */}
        <div className="dashboard-header-hero">
          <div>
            <h1>Lab Resource Utilization Platform</h1>
            <p>Welcome back, <strong>{user?.name}</strong>. Here is your operational summary.</p>
          </div>
          <div className="role-badge-tag">
            <FaShieldAlt /> {role?.replaceAll("_", " ")}
          </div>
        </div>

        {/* Primary Operational & Financial Metrics */}
        <div className="dashboard-container">

          <DashboardCard
            title="Total Equipment"
            value={stats.totalEquipment}
            icon={<FaFlask />}
          />

          <DashboardCard
            title={role === "RESEARCHER" ? "My Bookings" : "Total Bookings"}
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
            title="Billed Utilization (₹)"
            value={`₹${Number(stats.totalBilledAmount || 0).toLocaleString()}`}
            icon={<FaReceipt />}
          />

          <DashboardCard
            title={
              role === "RESEARCHER"
                ? "Approved Bookings"
                : "Maintenance Tasks"
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
                ? "Unread Alerts"
                : "Utilization Hours"
            }
            value={
              role === "RESEARCHER"
                ? stats.unreadNotifications
                : Number(stats.totalUtilizationHours).toFixed(1)
            }
            icon={<FaClock />}
          />

          {role === "SYSTEM_ADMIN" && (
            <DashboardCard
              title="Pending Approvals"
              value={stats.pendingUserRequests || 0}
              icon={<FaUserCheck />}
            />
          )}

        </div>

        {/* Optimization Recommendations Widget */}
        {stats.recommendations && stats.recommendations.length > 0 && (
          <div
            style={{
              background: "var(--color-bg-card)",
              border: "1px solid var(--color-border)",
              borderRadius: "var(--radius-lg)",
              padding: "20px",
              boxShadow: "var(--shadow-sm)"
            }}
          >
            <h3 style={{ margin: "0 0 12px 0", fontSize: "15px", fontWeight: 700, color: "var(--color-primary)", display: "flex", alignItems: "center", gap: "8px" }}>
              <FaLightbulb style={{ color: "#f59e0b" }} /> Resource Optimization &amp; Scheduling Recommendations
            </h3>
            <div style={{ display: "flex", flexDirection: "column", gap: "8px" }}>
              {stats.recommendations.map((rec, index) => (
                <div
                  key={index}
                  style={{
                    padding: "10px 14px",
                    background: "var(--color-bg-subtle)",
                    borderLeft: "4px solid var(--color-primary)",
                    borderRadius: "0 var(--radius-md) var(--radius-md) 0",
                    fontSize: "13px",
                    fontWeight: 500,
                    color: "var(--color-text-main)"
                  }}
                >
                  {rec}
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Maintenance & Certificate Overview for Managers */}
        {isManager && maintenanceDashboard && (
          <>
            <h2 className="dashboard-section-title">
              <FaTools style={{ color: "#1e40af" }} /> Maintenance &amp; Calibration Intelligence
            </h2>

            <div className="dashboard-container">

              <DashboardCard
                title="Upcoming Maintenance"
                value={maintenanceDashboard.upcomingMaintenanceCount}
                icon={<FaTools />}
              />

              <DashboardCard
                title="Overdue Maintenance"
                value={maintenanceDashboard.overdueMaintenanceCount}
                icon={<FaExclamationTriangle />}
              />

              <DashboardCard
                title="Certs Expiring (30d)"
                value={maintenanceDashboard.certificatesExpiringCount}
                icon={<FaCertificate />}
              />

              <DashboardCard
                title="Expired Certs"
                value={maintenanceDashboard.expiredCertificatesCount}
                icon={<FaCertificate />}
              />

              <DashboardCard
                title="Total Maintenance Cost"
                value={`₹${Number(maintenanceDashboard.totalMaintenanceCost || 0).toFixed(0)}`}
                icon={<FaChartBar />}
              />

            </div>
          </>
        )}

        {/* Interactive Charts */}
        <div className="charts-section">

          <EquipmentPieChart stats={stats} />

          <BookingBarChart stats={stats} />

        </div>

        {/* Quick Action Navigation Grid */}
        <h2 className="dashboard-section-title">
          🚀 Workspace Navigation
        </h2>

        <div className="cards">

          <div className="card">

            <h2>💳 Cost &amp; Billing</h2>

            <p>
              Simulated academic chargebacks, department cost allocation, and invoices.
            </p>

            <button onClick={() => navigate("/billing")}>
              Open Cost &amp; Billing
            </button>

          </div>

          {isManager && (
            <div className="card">

              <h2>📊 Reports &amp; Export</h2>

              <p>
                Generate 1-click PDF &amp; Excel reports for utilization, maintenance, and sharing.
              </p>

              <button onClick={() => navigate("/reports")}>
                Open Reports
              </button>

            </div>
          )}

          <div className="card">

            <h2>📦 Equipment Inventory</h2>

            <p>
              Search, filter, view status, and manage all laboratory equipment and assets.
            </p>

            <button onClick={() => navigate("/equipment")}>
              Open Equipment
            </button>

          </div>

          <div className="card">

            <h2>📅 Booking Management</h2>

            <p>
              Schedule time slots, reserve equipment, and manage queue requests.
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