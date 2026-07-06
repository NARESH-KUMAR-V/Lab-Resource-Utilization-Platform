import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import "./LoginPage.css";
import { Link } from "react-router-dom";

function RegisterPage() {

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("RESEARCHER");
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const handleSubmit = async (e) => {

    e.preventDefault();

    setError("");

    try {

      await api.post("/auth/register", {
        name,
        email,
        password,
        role,
      });

      alert("Registration Successful!");

      navigate("/login");

    } catch (err) {

      setError("Registration failed. Email may already exist.");

    }

  };

  return (

    <div className="login-container">

      <div className="login-card">

        <h1>Lab Resource Utilization Platform</h1>

        <h2>Create Account</h2>

        <form onSubmit={handleSubmit}>

          <div className="form-group">

            <label>Full Name</label>

            <input
              type="text"
              placeholder="Enter your name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />

          </div>

          <div className="form-group">

            <label>Email</label>

            <input
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />

          </div>

          <div className="form-group">

            <label>Password</label>

            <input
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />

          </div>

          <div className="form-group">

            <label>Role</label>

            <select
              value={role}
              onChange={(e) => setRole(e.target.value)}
            >

              <option value="RESEARCHER">Researcher</option>
              <option value="LAB_TECHNICIAN">Lab Technician</option>
              <option value="LAB_MANAGER">Lab Manager</option>
              <option value="DEPARTMENT_HEAD">Department Head</option>
              <option value="INSTITUTION_ADMIN">Institution Admin</option>
              <option value="SYSTEM_ADMIN">System Admin</option>

            </select>

          </div>

          {error && (
            <p className="error-message">
              {error}
            </p>
          )}

          <button
            type="submit"
            className="login-btn"
          >
            Register
          </button>

          <div className="auth-link">
            <p>
              Already have an account?{" "}
              <Link to="/login">Login</Link>
            </p>
          </div>

        </form>

      </div>

    </div>

  );

}

export default RegisterPage;