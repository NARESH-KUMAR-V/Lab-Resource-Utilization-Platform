import { useEffect, useState } from "react";
import { FaUsers } from "react-icons/fa";
import { toast } from "react-toastify";
import api from "../api/axios";
import Layout from "../components/Layout";
import UsersTable from "../components/UsersTable";
import DashboardCard from "../components/DashboardCard";
import "./UsersPage.css";

function UsersPage() {

  const role = localStorage.getItem("role");
  const institutionId = localStorage.getItem("institutionId");

  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {

    try {

      setLoading(true);

      let response;

      if (role === "SYSTEM_ADMIN") {

        response = await api.get("/users");

      } else if (role === "INSTITUTION_ADMIN") {

        response = await api.get(
          `/users/institution/${institutionId}`
        );

      } else {

        response = await api.get("/users");

      }

      setUsers(response.data);

    } catch (error) {

      console.error(error);

      toast.error("Failed to load users.");

    } finally {

      setLoading(false);

    }

  };

  return (

    <Layout>

      <div className="users-page">

        <div className="page-header">

          <div>

            <h1>
              <FaUsers />
              User Directory &amp; Roles
            </h1>

            <p>
              View all registered platform accounts across institutions, departments, and roles.
            </p>

          </div>

          <div className="user-count-card">

            <span>Active Platform Users</span>

            <h2>{users.length}</h2>

          </div>

        </div>

        {loading ? (

          <div className="loading-card">

            <h3>Loading user directory...</h3>

          </div>

        ) : (

          <UsersTable users={users} />

        )}

      </div>

    </Layout>

  );

}

export default UsersPage;