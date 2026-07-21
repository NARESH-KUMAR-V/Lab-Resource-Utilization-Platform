import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

import {
  FaTachometerAlt,
  FaFlask,
  FaClipboardList,
  FaTools,
  FaExchangeAlt,
  FaBell,
  FaUsers,
  FaUniversity,
  FaBuilding,
  FaSignOutAlt
} from "react-icons/fa";

import "./Sidebar.css";

function Sidebar() {
  const { user, role, logout } = useAuth();
  const navigate = useNavigate();

  const hasRole = (...roles) => roles.includes(role);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <aside className="sidebar">

      <div className="sidebar-logo">
        <h2>LabSys</h2>
        <span>Resource Platform</span>
      </div>

      <div className="sidebar-user">
        <div className="avatar">
          {user?.name?.charAt(0).toUpperCase()}
        </div>

        <div>
          <h4>{user?.name}</h4>
          <p>{role}</p>
        </div>
      </div>

      <nav className="sidebar-menu">

        <NavLink to="/dashboard">
          <FaTachometerAlt />
          <span>Dashboard</span>
        </NavLink>

        <NavLink to="/equipment">
          <FaFlask />
          <span>Equipment</span>
        </NavLink>

        {hasRole(
          "RESEARCHER",
          "LAB_MANAGER",
          "DEPARTMENT_HEAD",
          "INSTITUTION_ADMIN",
          "SYSTEM_ADMIN"
        ) && (
          <NavLink to="/bookings">
            <FaClipboardList />
            <span>Bookings</span>
          </NavLink>
        )}

        {hasRole(
          "LAB_TECHNICIAN",
          "LAB_MANAGER",
          "INSTITUTION_ADMIN",
          "SYSTEM_ADMIN"
        ) && (
          <NavLink to="/maintenance">
            <FaTools />
            <span>Maintenance</span>
          </NavLink>
        )}

        {hasRole(
          "RESEARCHER",
          "LAB_MANAGER",
          "DEPARTMENT_HEAD",
          "INSTITUTION_ADMIN",
          "SYSTEM_ADMIN"
        ) && (
          <NavLink to="/sharing-requests">
            <FaExchangeAlt />
            <span>Sharing</span>
          </NavLink>
        )}

        <NavLink to="/notifications">
          <FaBell />
          <span>Notifications</span>
        </NavLink>

        {hasRole(
          "SYSTEM_ADMIN",
          "INSTITUTION_ADMIN",
          "DEPARTMENT_HEAD"
        ) && (
          <NavLink to="/users">
            <FaUsers />
            <span>Users</span>
          </NavLink>
        )}

        {hasRole(
          "SYSTEM_ADMIN",
          "INSTITUTION_ADMIN"
        ) && (
          <NavLink to="/laboratories">
            <FaUniversity />
            <span>Laboratories</span>
          </NavLink>
        )}

        {hasRole("SYSTEM_ADMIN") && (
          <NavLink to="/institutions">
            <FaBuilding />
            <span>Institutions</span>
          </NavLink>
        )}

      </nav>

      <div className="sidebar-footer">

        <button
          className="logout-btn"
          onClick={handleLogout}
        >
          <FaSignOutAlt />
          <span>Logout</span>
        </button>

      </div>

    </aside>
  );
}

export default Sidebar;