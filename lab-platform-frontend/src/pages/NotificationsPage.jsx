import { useEffect, useState } from "react";
import { FaBell } from "react-icons/fa";
import { toast } from "react-toastify";
import api from "../api/axios";
import Layout from "../components/Layout";
import NotificationsTable from "../components/NotificationsTable";
import "./NotificationsPage.css";

function NotificationsPage() {

  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {

    try {

      setLoading(true);

      const response = await api.get("/notifications");

      setNotifications(response.data);

    } catch (error) {

      console.error(error);

      toast.error("Failed to load notifications.");

    } finally {

      setLoading(false);

    }

  };

  const markAsRead = async (id) => {

    try {

      await api.put(`/notifications/${id}/read`);

      toast.success("Notification marked as read.");

      fetchNotifications();

    } catch (error) {

      console.error(error);

      toast.error("Unable to mark notification as read.");

    }

  };

  const deleteNotification = async (id) => {

    if (!window.confirm("Delete this notification?")) return;

    try {

      await api.delete(`/notifications/${id}`);

      toast.success("Notification deleted.");

      fetchNotifications();

    } catch (error) {

      console.error(error);

      toast.error("Unable to delete notification.");

    }

  };

  const unreadCount = notifications.filter(
    notification => !notification.read && !notification.isRead
  ).length;

  return (

    <Layout>

      <div className="notifications-page">

        <div className="page-header">

          <div>

            <h1>

              <FaBell />

              Notifications

            </h1>

            <p>
              View and manage all your notifications.
            </p>

          </div>

          <div className="notification-count-card">

            <span>Unread</span>

            <h2>{unreadCount}</h2>

          </div>

        </div>

        {loading ? (

          <div className="loading-card">

            Loading notifications...

          </div>

        ) : (

          <NotificationsTable
            notifications={notifications}
            markAsRead={markAsRead}
            deleteNotification={deleteNotification}
          />

        )}

      </div>

    </Layout>

  );

}

export default NotificationsPage;