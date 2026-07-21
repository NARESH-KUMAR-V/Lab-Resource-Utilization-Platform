import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Navbar.css";

function Navbar() {

  const { user, role, logout } = useAuth();

  const navigate = useNavigate();

  const hasRole = (...roles) => roles.includes(role);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="navbar">

      <h2 className="logo">
        Lab Resource Utilization Platform
      </h2>

      <div className="nav-links">

        <NavLink to="/dashboard">
          Dashboard
        </NavLink>

        <NavLink to="/equipment">
          Equipment
        </NavLink>

        {hasRole(
          "RESEARCHER",
          "LAB_MANAGER",
          "DEPARTMENT_HEAD",
          "INSTITUTION_ADMIN",
          "SYSTEM_ADMIN"
        ) && (
          <NavLink to="/bookings">
            Bookings
          </NavLink>
        )}

        {hasRole(
          "LAB_TECHNICIAN",
          "LAB_MANAGER",
          "INSTITUTION_ADMIN",
          "SYSTEM_ADMIN"
        ) && (
          <NavLink to="/maintenance">
            Maintenance
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
            Sharing
          </NavLink>
        )}

        {hasRole(
          "INSTITUTION_ADMIN",
          "SYSTEM_ADMIN",
          "DEPARTMENT_HEAD"
        ) && (
          <NavLink to="/users">
            Users
          </NavLink>
        )}

        {hasRole(
          "INSTITUTION_ADMIN",
          "SYSTEM_ADMIN"
        ) && (
          <NavLink to="/laboratories">
            Laboratories
          </NavLink>
        )}

        {hasRole("SYSTEM_ADMIN") && (
          <NavLink to="/institutions">
            Institutions
          </NavLink>
        )}

        <NavLink to="/notifications">
          Notifications
        </NavLink>

        <span
          style={{
            color: "#fff",
            marginLeft: "20px",
            textAlign: "right"
          }}
        >
          <div><strong>{user?.name}</strong></div>
          <div style={{ fontSize: "12px" }}>{role}</div>
        </span>

        <button onClick={handleLogout}>
          Logout
        </button>

      </div>

    </nav>
  );
}

export default Navbar;