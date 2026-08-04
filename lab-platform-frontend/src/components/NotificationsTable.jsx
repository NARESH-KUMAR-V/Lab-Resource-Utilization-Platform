import { FaCheckCircle, FaTrash } from "react-icons/fa";
import "./Table.css";

function NotificationsTable({
  notifications,
  markAsRead,
  deleteNotification
}) {

  return (

    <div className="table-card">

      <div className="table-header">

        <h2>Notification Center</h2>

        <span className="status-badge status-approved">
          Total Notifications: {notifications.length}
        </span>

      </div>

      <div className="table-container">

        <table className="data-table">

          <thead>

            <tr>
              <th>ID</th>
              <th>Notification Message</th>
              <th>Date &amp; Time</th>
              <th>Status</th>
              <th style={{ textAlign: "center" }}>Actions</th>
            </tr>

          </thead>

          <tbody>

            {notifications.length > 0 ? (

              notifications.map((notification) => {

                const isRead =
                  notification.isRead ?? notification.read ?? false;

                return (

                  <tr key={notification.id}>

                    <td><strong>#{notification.id}</strong></td>

                    <td>{notification.message}</td>

                    <td>
                      {notification.createdAt
                        ? new Date(notification.createdAt).toLocaleString()
                        : "-"}
                    </td>

                    <td>

                      <span
                        className={
                          isRead
                            ? "status-badge status-completed"
                            : "status-badge status-pending"
                        }
                      >
                        {isRead ? "Read" : "Unread"}
                      </span>

                    </td>

                    <td style={{ textAlign: "center" }}>

                      {!isRead && (

                        <button
                          className="action-btn edit-btn"
                          onClick={() => markAsRead(notification.id)}
                          title="Mark as Read"
                        >
                          <FaCheckCircle /> Mark Read
                        </button>

                      )}

                      <button
                        className="action-btn delete-btn"
                        onClick={() => deleteNotification(notification.id)}
                        title="Delete Notification"
                      >
                        <FaTrash /> Delete
                      </button>

                    </td>

                  </tr>

                );

              })

            ) : (

              <tr>

                <td
                  colSpan="5"
                  className="empty-table"
                >
                  🔔 No notifications found.
                </td>

              </tr>

            )}

          </tbody>

        </table>

      </div>

    </div>

  );

}

export default NotificationsTable;