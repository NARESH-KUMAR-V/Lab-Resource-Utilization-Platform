import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import api from "../api/axios";
import "../components/Table.css";

function NotificationPage() {

  const [notifications, setNotifications] = useState([]);

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {

    try {

      const response = await api.get("/notifications");

      setNotifications(response.data);

    } catch (error) {

      console.error(error);

    }

  };

  const markAsRead = async (id) => {

    try {

      await api.put(`/notifications/${id}/read`);

      fetchNotifications();

    } catch (error) {

      console.error(error);

    }

  };

  const deleteNotification = async (id) => {

    if (!window.confirm("Delete this notification?")) return;

    try {

      await api.delete(`/notifications/${id}`);

      fetchNotifications();

    } catch (error) {

      console.error(error);

    }

  };

  return (
    <>
      <Navbar />

      <div style={{ padding: "20px" }}>

        <h2
          style={{
            textAlign: "center",
            color: "#1976d2",
            marginBottom: "20px",
          }}
        >
          Notifications
        </h2>

        <table className="data-table">

          <thead>

            <tr>

              <th>ID</th>

              <th>Message</th>

              <th>Date</th>

              <th>Status</th>

              <th>Actions</th>

            </tr>

          </thead>

          <tbody>

            {notifications.length > 0 ? (

              notifications.map((notification) => (

                <tr key={notification.id}>

                  <td>{notification.id}</td>

                  <td>{notification.message}</td>

                  <td>{notification.createdAt.replace("T"," ")}</td>

                  <td>

                    <span
                      className={`status-badge ${
                        notification.read
                          ? "status-approved"
                          : "status-pending"
                      }`}
                    >
                      {notification.read ? "READ" : "UNREAD"}
                    </span>

                  </td>

                  <td>

                    {!notification.read && (

                      <button
                        className="action-btn edit-btn"
                        onClick={() => markAsRead(notification.id)}
                      >
                        Mark Read
                      </button>

                    )}

                    <button
                      className="action-btn delete-btn"
                      onClick={() => deleteNotification(notification.id)}
                    >
                      Delete
                    </button>

                  </td>

                </tr>

              ))

            ) : (

              <tr>

                <td
                  colSpan="5"
                  style={{
                    textAlign: "center",
                    padding: "20px",
                  }}
                >
                  No notifications available.
                </td>

              </tr>

            )}

          </tbody>

        </table>

      </div>

    </>
  );

}

export default NotificationPage;