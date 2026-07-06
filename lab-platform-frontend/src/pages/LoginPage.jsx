import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./LoginPage.css";
import { Link } from "react-router-dom";

function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");

    try {

      const response = await api.post("/auth/login", {
        email,
        password,
      });

      login(response.data.token);

      navigate("/dashboard");

    } catch (err) {

      setError("Invalid email or password");

    }
  };

  return (
    <div className="login-container">

      <div className="login-card">

        <h1>Lab Resource Utilization Platform</h1>

        <h2>Login</h2>

        <form onSubmit={handleSubmit}>

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

          {error && <p className="error-message">{error}</p>}

          <button
            type="submit"
            className="login-btn"
          >
            Login
          </button>

        </form>

        <a
          href="http://localhost:8080/oauth2/authorization/google"
          className="google-btn"
        >
          Continue with Google
        </a>

        <div className="auth-link">
          <p>
            Don't have an account?{" "}
            <Link to="/register">Register</Link>
          </p>
        </div>

      </div>

    </div>
  );
}

export default LoginPage;