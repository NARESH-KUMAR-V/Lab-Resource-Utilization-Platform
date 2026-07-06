import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Navbar.css";

function Navbar() {
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <h2 className="logo">Lab Resource Utilization Platform</h2>

      <div className="nav-links">
        <Link to="/dashboard">Dashboard</Link>

        <Link to="/equipment">Equipment</Link>

        <Link to="/bookings">Bookings</Link>

        <Link to="/maintenance">Maintenance</Link>

        <Link to="/sharing-requests">Sharing</Link>

        <Link to="/notifications">Notifications</Link>

        <button onClick={handleLogout}>
          Logout
        </button>
      </div>
    </nav>
  );
}

export default Navbar;