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

        <h2>My Notifications</h2>

        <span>Total : {notifications.length}</span>

      </div>

      <div className="table-container">

        <table className="data-table">

          <thead>

            <tr>
              <th>ID</th>
              <th>Message</th>
              <th>Date & Time</th>
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

                    <td>{notification.id}</td>

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
                            ? "status-badge completed"
                            : "status-badge pending"
                        }
                      >
                        {isRead ? "Read" : "Unread"}
                      </span>

                    </td>

                    <td className="action-column">

                      {!isRead && (

                        <button
                          className="action-btn edit-btn"
                          onClick={() => markAsRead(notification.id)}
                          title="Mark as Read"
                        >
                          <FaCheckCircle />
                        </button>

                      )}

                      <button
                        className="action-btn delete-btn"
                        onClick={() => deleteNotification(notification.id)}
                        title="Delete Notification"
                      >
                        <FaTrash />
                      </button>

                    </td>

                  </tr>

                );

              })

            ) : (

              <tr>

                <td
                  colSpan="5"
                  className="no-data"
                >
                  No notifications found.
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