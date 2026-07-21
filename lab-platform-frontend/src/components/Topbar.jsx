import "./Topbar.css";
import {
  FaBell,
  FaSignOutAlt
} from "react-icons/fa";
import {
  useAuth
} from "../context/AuthContext";
import {
  useNavigate,
  useLocation
} from "react-router-dom";
import {
  useState,
  useEffect,
  useRef
} from "react";
import api from "../api/axios";
import NotificationPanel from "./NotificationPanel";

function Topbar() {

  const { user, logout } = useAuth();

  const navigate = useNavigate();
  const location = useLocation();

  const [showNotifications, setShowNotifications] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);

  const notificationRef = useRef(null);

  useEffect(() => {
    loadUnreadCount();
  }, []);

  useEffect(() => {

    const handleClickOutside = (event) => {

      if (
        notificationRef.current &&
        !notificationRef.current.contains(event.target)
      ) {
        setShowNotifications(false);
      }

    };

    document.addEventListener("mousedown", handleClickOutside);

    return () =>
      document.removeEventListener(
        "mousedown",
        handleClickOutside
      );

  }, []);

  const loadUnreadCount = async () => {

    try {

      const response = await api.get("/notifications");

      const unread = response.data.filter(
        notification =>
          !(notification.isRead ?? notification.read)
      ).length;

      setUnreadCount(unread);

    } catch (error) {

      console.error(error);

    }

  };

  const handleLogout = () => {

    const confirmLogout = window.confirm(
      "Are you sure you want to logout?"
    );

    if (!confirmLogout) return;

    logout();

    navigate("/login");

  };

  const getPageTitle = () => {

    switch (location.pathname) {

      case "/dashboard":
        return "Dashboard";

      case "/equipment":
        return "Equipment Management";

      case "/laboratories":
        return "Laboratory Management";

      case "/institutions":
        return "Institution Management";

      case "/bookings":
        return "Booking Management";

      case "/maintenance":
        return "Maintenance";

      case "/analytics":
        return "Analytics";

      case "/users":
        return "User Management";

      case "/notifications":
        return "Notifications";

      default:
        return "Lab Resource Utilization Platform";

    }

  };

  return (

    <header className="topbar">

      <div className="page-title">
        <h2>{getPageTitle()}</h2>
      </div>

      <div className="topbar-right">

        <div
          className="notification-wrapper"
          ref={notificationRef}
        >

          <button
            className="notification-btn"
            onClick={() =>
              setShowNotifications(
                !showNotifications
              )
            }
          >

            <FaBell />

            {unreadCount > 0 && (

              <span className="notification-badge">

                {unreadCount}

              </span>

            )}

          </button>

          {showNotifications && (

            <div className="notification-dropdown">

              <NotificationPanel />

            </div>

          )}

        </div>

        <div className="profile">

          <div className="profile-avatar">

            {user?.name?.charAt(0).toUpperCase()}

          </div>

          <div>

            <h4>{user?.name}</h4>

            <p>

              {user?.role?.replaceAll("_", " ")}

            </p>

          </div>

        </div>

        <button
          className="logout-btn"
          onClick={handleLogout}
        >

          <FaSignOutAlt />

          Logout

        </button>

      </div>

    </header>

  );

}

export default Topbar;