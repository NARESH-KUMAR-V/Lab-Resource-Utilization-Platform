import { useEffect, useMemo, useState } from "react";
import Navbar from "../components/Navbar";
import api from "../api/axios";
import "../components/Table.css";

function NotificationPage() {

  const [notifications, setNotifications] = useState([]);
  const [search, setSearch] = useState("");

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

  const filteredNotifications = useMemo(() => {

    return notifications.filter((notification) =>

      notification.message
        .toLowerCase()
        .includes(search.toLowerCase())

    );

  }, [notifications, search]);

  const stats = {

    total: notifications.length,

    unread: notifications.filter(n => !n.read).length,

    read: notifications.filter(n => n.read).length,

  };

  return (

    <>
      <Navbar />

      <div className="dashboard">

        <h1>Notification Center</h1>

        <p>

          Stay informed about bookings, maintenance,
          approvals and system activities.

        </p>

        <div className="dashboard-container">

          <div className="dashboard-card">
            <div className="card-content">
              <h4>Total Notifications</h4>
              <h2>{stats.total}</h2>
            </div>
          </div>

          <div className="dashboard-card">
            <div className="card-content">
              <h4>Unread</h4>
              <h2>{stats.unread}</h2>
            </div>
          </div>

          <div className="dashboard-card">
            <div className="card-content">
              <h4>Read</h4>
              <h2>{stats.read}</h2>
            </div>
          </div>

        </div>

        <div className="table-card">

          <div className="table-header">

            <h2>All Notifications</h2>

            <input
              type="text"
              className="table-search"
              placeholder="🔍 Search notifications..."
              value={search}
              onChange={(e)=>setSearch(e.target.value)}
            />

          </div>

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

            {filteredNotifications.length>0 ?

              filteredNotifications.map(notification=>(

                <tr key={notification.id}>

                  <td>{notification.id}</td>

                  <td>{notification.message}</td>

                  <td>

                    {notification.createdAt.replace("T"," ")}

                  </td>

                  <td>

                    <span
                      className={`status-badge ${
                        notification.read
                          ? "status-approved"
                          : "status-pending"
                      }`}
                    >

                      {notification.read
                        ? "Read"
                        : "Unread"}

                    </span>

                  </td>

                  <td>

                    {!notification.read && (

                      <button
                        className="action-btn edit-btn"
                        onClick={()=>markAsRead(notification.id)}
                      >
                        ✓ Mark Read
                      </button>

                    )}

                    <button
                      className="action-btn delete-btn"
                      onClick={()=>deleteNotification(notification.id)}
                    >
                      🗑 Delete
                    </button>

                  </td>

                </tr>

              ))

              :

              <tr>

                <td
                  colSpan="5"
                  className="empty-table"
                >

                  🔔 No notifications found.

                </td>

              </tr>

            }

            </tbody>

          </table>

        </div>

      </div>

    </>

  );

}

export default NotificationPage;