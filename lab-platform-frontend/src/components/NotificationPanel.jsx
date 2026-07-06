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

            <h2>
                <FaBell /> Recent Notifications
            </h2>

            {
                notifications.length === 0 ?

                    <p>No notifications available.</p>

                    :

                    notifications.map(notification => (

                        <div
                            key={notification.id}
                            className="notification-item"
                        >

                            <p>{notification.message}</p>

                            <small>
                                {notification.createdAt.replace("T", " ")}
                            </small>

                        </div>

                    ))
            }

        </div>

    );

}

export default NotificationPanel;