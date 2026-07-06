import { useEffect, useState } from "react";
import api from "../api/axios";
import { FaBell } from "react-icons/fa";

function NotificationPanel() {

  const [notifications, setNotifications] = useState([]);

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

  return (

    <div className="notification-card">

      <div className="notification-header">

        <h2>
          <FaBell />
          Recent Notifications
        </h2>

        <span className="notification-count">

          {notifications.length}

        </span>

      </div>

      {

        notifications.length === 0 ?

          <div className="notification-empty">

            🔔 No notifications available.

          </div>

          :

          notifications.map((notification) => (

            <div
              key={notification.id}
              className={`notification-item ${
                notification.read
                  ? ""
                  : "notification-unread"
              }`}
            >

              <div className="notification-content">

                <p>

                  {notification.message}

                </p>

                <small>

                  {notification.createdAt.replace("T", " ")}

                </small>

              </div>

              {

                !notification.read && (

                  <span className="notification-dot"></span>

                )

              }

            </div>

          ))

      }

    </div>

  );

}

export default NotificationPanel;