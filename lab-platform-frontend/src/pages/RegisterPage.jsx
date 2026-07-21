import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { toast } from "react-toastify";
import api from "../api/axios";
import "./LoginPage.css";

function RegisterPage() {
  const navigate = useNavigate();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [role, setRole] = useState("RESEARCHER");

  const [institutions, setInstitutions] = useState([]);
  const [laboratories, setLaboratories] = useState([]);

  const [institutionId, setInstitutionId] = useState("");
  const [laboratoryId, setLaboratoryId] = useState("");

  const [error, setError] = useState("");

  useEffect(() => {
    loadInstitutions();
  }, []);

  useEffect(() => {
    if (!institutionId) {
      setLaboratories([]);
      return;
    }

    api
      .get(`/laboratories/institution/${institutionId}`)
      .then((res) => setLaboratories(res.data))
      .catch(console.error);
  }, [institutionId]);

  // Reset selections whenever role changes
  useEffect(() => {
    if (role === "SYSTEM_ADMIN") {
      setInstitutionId("");
      setLaboratoryId("");
    } else if (role === "INSTITUTION_ADMIN") {
      setLaboratoryId("");
    }
  }, [role]);

  const loadInstitutions = async () => {
    try {
      const res = await api.get("/institutions");
      setInstitutions(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");

    try {
      await api.post("/auth/register", {
        name,
        email,
        password,
        role,
        institutionId: role === "SYSTEM_ADMIN" ? null : institutionId,
        laboratoryId:
          role === "SYSTEM_ADMIN" || role === "INSTITUTION_ADMIN"
            ? null
            : laboratoryId,
      });

      toast.success("Registration successful!");
      navigate("/login");
    } catch (err) {
      console.error(err);
      setError("Registration failed.");
      toast.error("Registration failed.");
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
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Password</label>
            <input
              type="password"
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

          {/* Institution - Hidden only for System Admin */}
          {role !== "SYSTEM_ADMIN" && (
            <div className="form-group">
              <label>Institution</label>

              <select
                value={institutionId}
                onChange={(e) => setInstitutionId(e.target.value)}
                required={role !== "SYSTEM_ADMIN"}
              >
                <option value="">Select Institution</option>

                {institutions.map((inst) => (
                  <option key={inst.id} value={inst.id}>
                    {inst.name}
                  </option>
                ))}
              </select>
            </div>
          )}

          {/* Laboratory - Only for roles below Institution Admin */}
          {role !== "SYSTEM_ADMIN" &&
            role !== "INSTITUTION_ADMIN" && (
              <div className="form-group">
                <label>Laboratory</label>

                <select
                  value={laboratoryId}
                  onChange={(e) => setLaboratoryId(e.target.value)}
                  required
                >
                  <option value="">Select Laboratory</option>

                  {laboratories.map((lab) => (
                    <option key={lab.id} value={lab.id}>
                      {lab.name}
                    </option>
                  ))}
                </select>
              </div>
            )}

          {error && <p className="error-message">{error}</p>}

          <button
            type="submit"
            className="login-btn"
          >
            Register
          </button>

          <div className="auth-link">
            <p>
              Already have an account? <Link to="/login">Login</Link>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
}

export default RegisterPage;