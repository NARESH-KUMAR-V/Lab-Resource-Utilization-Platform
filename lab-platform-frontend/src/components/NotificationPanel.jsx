import { useEffect, useState } from "react";
import { FaBell, FaExternalLinkAlt } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import "./NotificationPanel.css";

function NotificationPanel() {
  const [notifications, setNotifications] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    loadNotifications();
  }, []);

  const loadNotifications = async () => {
    try {
      const response = await api.get("/notifications");

      setNotifications(response.data.slice(0, 5));
    } catch (error) {
      console.error("Error loading notifications:", error);
    }
  };

  const markAsRead = async (id) => {
    try {
      await api.put(`/notifications/${id}/read`);

      setNotifications((prev) =>
        prev.map((notification) =>
          notification.id === id
            ? {
                ...notification,
                isRead: true,
                read: true,
              }
            : notification
        )
      );
    } catch (error) {
      console.error(error);
    }
  };

  const unreadCount = notifications.filter(
    (notification) =>
      !(notification.isRead ?? notification.read)
  ).length;

  return (
    <div className="notification-card">

      <div className="notification-header">

        <h2>
          <FaBell />
          Notifications
        </h2>

        <span className="notification-count">
          {unreadCount}
        </span>

      </div>

      {notifications.length === 0 ? (

        <div className="notification-empty">
          🔔 No notifications available.
        </div>

      ) : (

        notifications.map((notification) => {

          const isRead =
            notification.isRead ??
            notification.read ??
            false;

          return (

            <div
              key={notification.id}
              className={`notification-item ${
                isRead ? "" : "notification-unread"
              }`}
              onClick={() => {
                if (!isRead) {
                  markAsRead(notification.id);
                }
              }}
            >

              <div className="notification-content">

                <p>{notification.message}</p>

                <small>
                  {notification.createdAt
                    ? new Date(
                        notification.createdAt
                      ).toLocaleString()
                    : "-"}
                </small>

              </div>

              {!isRead && (
                <span className="notification-dot"></span>
              )}

            </div>

          );

        })

      )}

      <div className="notification-footer">

        <button
          className="view-all-btn"
          onClick={() => navigate("/notifications")}
        >
          View All Notifications
          <FaExternalLinkAlt />
        </button>

      </div>

    </div>
  );
}

export default NotificationPanel;