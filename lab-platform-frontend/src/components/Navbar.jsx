import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { jwtDecode } from "jwt-decode";
import "./Navbar.css";

function Navbar() {

  const { token, logout } = useAuth();
  const navigate = useNavigate();

  let role = "";

  if (token) {
    try {
      const decoded = jwtDecode(token);

      role =
        decoded.role ||
        decoded.authorities?.[0] ||
        "";
    } catch (error) {
      console.error(error);
    }
  }

  const hasRole = (...roles) => {

    return roles.some((r) =>
      role.includes(r)
    );

  };

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
          "INSTITUTION_ADMIN",
          "SYSTEM_ADMIN"
        ) && (
          <NavLink to="/bookings">
            Bookings
          </NavLink>
        )}

        {hasRole(
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
          "INSTITUTION_ADMIN",
          "SYSTEM_ADMIN"
        ) && (
          <NavLink to="/sharing-requests">
            Sharing
          </NavLink>
        )}

        <NavLink to="/notifications">
          Notifications
        </NavLink>

        <span
          style={{
            color: "#fff",
            fontWeight: "bold",
            marginLeft: "15px",
          }}
        >
          {role.replace("ROLE_", "")}
        </span>

        <button onClick={handleLogout}>
          Logout
        </button>

      </div>

    </nav>

  );

}

export default Navbar;