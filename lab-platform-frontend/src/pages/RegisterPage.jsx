import { useEffect, useState } from "react";
import { useNavigate, Link, useSearchParams } from "react-router-dom";
import { toast } from "react-toastify";
import api from "../api/axios";
import "./LoginPage.css";

function RegisterPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const googleEmail = searchParams.get("email") || "";
  const googleName = searchParams.get("name") || "";
  const isFromGoogle = searchParams.get("fromGoogle") === "true";
  const isReapply = searchParams.get("reapply") === "true";

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [department, setDepartment] = useState("");

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
    if (googleName) setName(googleName);
    if (googleEmail) setEmail(googleEmail);
  }, [googleName, googleEmail]);

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
        password: password || (isFromGoogle ? "GoogleOAuthUserSecuredPass123!" : ""),
        role,
        department,
        institutionId: role === "SYSTEM_ADMIN" ? null : institutionId,
        laboratoryId:
          role === "SYSTEM_ADMIN" || role === "INSTITUTION_ADMIN"
            ? null
            : laboratoryId,
      });

      toast.success(
        "Registration re-submitted successfully! Your account is awaiting System Admin approval."
      );
      navigate("/login");
    } catch (err) {
      console.error(err);
      const msg = err.response?.data?.message || "Registration failed.";
      setError(msg);
      toast.error(msg);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h1>Lab Resource Utilization Platform</h1>

        <h2>{isReapply ? "Re-Submit Registration" : "Create Account"}</h2>

        {isReapply ? (
          <div
            style={{
              background: "#fff3cd",
              color: "#856404",
              padding: "12px",
              borderRadius: "8px",
              marginBottom: "16px",
              fontSize: "13.5px",
              border: "1px solid #ffeeba",
              textAlign: "left",
              lineHeight: "1.4"
            }}
          >
            ℹ️ <strong>Re-submitting for Google Account ({googleEmail})</strong><br />
            Your previous registration was not approved. Please select your <strong>Institution</strong>, <strong>Laboratory</strong>, <strong>Department</strong>, and <strong>Requested Role</strong> below to re-submit your registration for System Admin review.
          </div>
        ) : isFromGoogle ? (
          <div
            style={{
              background: "#e8f0fe",
              color: "#1a73e8",
              padding: "12px",
              borderRadius: "8px",
              marginBottom: "16px",
              fontSize: "13.5px",
              border: "1px solid #aecbfa",
              textAlign: "left",
              lineHeight: "1.4"
            }}
          >
            ✨ <strong>Google Sign-Up ({googleEmail})</strong><br />
            Please complete your profile by selecting your <strong>Requested Role</strong>, <strong>Institution</strong>, and <strong>Laboratory</strong>. Your registration will be sent to the System Admin for approval.
          </div>
        ) : null}

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
              readOnly={isFromGoogle}
              style={isFromGoogle ? { background: "#f1f3f4", cursor: "not-allowed" } : {}}
              required
            />
          </div>

          {!isFromGoogle && (
            <div className="form-group">
              <label>Password</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required={!isFromGoogle}
              />
            </div>
          )}

          <div className="form-group">
            <label>Requested Role</label>
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

          {/* Department */}
          <div className="form-group">
            <label>Department</label>
            <input
              value={department}
              placeholder="e.g. Computer Science, Biotechnology"
              onChange={(e) => setDepartment(e.target.value)}
            />
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
                <label>Laboratory (Optional depending on role)</label>

                <select
                  value={laboratoryId}
                  onChange={(e) => setLaboratoryId(e.target.value)}
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
            {isReapply ? "Re-Submit Registration" : "Submit Registration"}
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