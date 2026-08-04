import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import Layout from "../components/Layout";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";
import "./CertificateManagement.css";

function CertificateManagementPage() {
  const { role } = useAuth();
  const [certificates, setCertificates] = useState([]);
  const [equipment, setEquipment] = useState([]);
  const [filter, setFilter] = useState("ALL");
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [isRenewing, setIsRenewing] = useState(false);
  const [uploading, setUploading] = useState(false);

  const [formData, setFormData] = useState({
    equipmentId: "",
    certificateName: "",
    certificateNumber: "",
    issueDate: "",
    expiryDate: "",
    issuedBy: "",
    remarks: "",
    certificateFileUrl: "",
  });

  useEffect(() => {
    loadCertificates();
    loadEquipment();
  }, []);

  const loadCertificates = async () => {
    try {
      const response = await api.get("/certificates");
      setCertificates(Array.isArray(response.data) ? response.data : []);
    } catch (error) {
      console.error(error);
      toast.error("Failed to load certificates.");
      setCertificates([]);
    }
  };

  const loadEquipment = async () => {
    try {
      const response = await api.get("/equipment");
      setEquipment(Array.isArray(response.data) ? response.data : []);
    } catch (error) {
      console.error(error);
      setEquipment([]);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleFileUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const data = new FormData();
    data.append("file", file);

    try {
      setUploading(true);
      const response = await api.post("/certificates/upload", data, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      setFormData((prev) => ({ ...prev, certificateFileUrl: response.data }));
      toast.success("File uploaded.");
    } catch (error) {
      toast.error("File upload failed.");
    } finally {
      setUploading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const payload = {
      equipment: { id: formData.equipmentId },
      certificateName: formData.certificateName,
      certificateNumber: formData.certificateNumber,
      issueDate: formData.issueDate || null,
      expiryDate: formData.expiryDate,
      issuedBy: formData.issuedBy,
      remarks: formData.remarks,
      certificateFileUrl: formData.certificateFileUrl,
    };

    try {
      if (editingId) {
        await api.put(`/certificates/${editingId}`, payload);
        toast.success(isRenewing ? "🎉 Certificate successfully renewed! Status is now VALID." : "Certificate updated.");
      } else {
        await api.post("/certificates", payload);
        toast.success("Certificate created.");
      }

      resetForm();
      loadCertificates();
    } catch (error) {
      toast.error("Failed to save certificate.");
    }
  };

  const handleEdit = (cert) => {
    setIsRenewing(false);
    setFormData({
      equipmentId: cert.equipment?.id || "",
      certificateName: cert.certificateName || "",
      certificateNumber: cert.certificateNumber || "",
      issueDate: cert.issueDate || "",
      expiryDate: cert.expiryDate || "",
      issuedBy: cert.issuedBy || "",
      remarks: cert.remarks || "",
      certificateFileUrl: cert.certificateFileUrl || "",
    });
    setEditingId(cert.id);
    setShowForm(true);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleRenew = (cert) => {
    const today = new Date();
    const todayStr = today.toISOString().split("T")[0];
    
    // Set default renewal expiry date to +1 year
    const nextYear = new Date(today);
    nextYear.setFullYear(nextYear.getFullYear() + 1);
    const nextYearStr = nextYear.toISOString().split("T")[0];

    setIsRenewing(true);
    setEditingId(cert.id);
    setFormData({
      equipmentId: cert.equipment?.id || "",
      certificateName: cert.certificateName || "",
      certificateNumber: cert.certificateNumber ? `${cert.certificateNumber}-R1` : "CERT-RENEWED",
      issueDate: todayStr,
      expiryDate: nextYearStr,
      issuedBy: cert.issuedBy || "Calibration Standards Board",
      remarks: `Renewed on ${todayStr}. Previous Expiry: ${cert.expiryDate || "N/A"}`,
      certificateFileUrl: cert.certificateFileUrl || "",
    });

    setShowForm(true);
    toast.info(`Pre-filled renewal for ${cert.certificateName}. Please review new expiry date and submit.`);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this certificate?")) return;
    try {
      await api.delete(`/certificates/${id}`);
      toast.success("Certificate deleted.");
      loadCertificates();
    } catch (error) {
      toast.error("Delete failed.");
    }
  };

  const resetForm = () => {
    setFormData({
      equipmentId: "",
      certificateName: "",
      certificateNumber: "",
      issueDate: "",
      expiryDate: "",
      issuedBy: "",
      remarks: "",
      certificateFileUrl: "",
    });
    setEditingId(null);
    setIsRenewing(false);
    setShowForm(false);
  };

  const getStatusClass = (status) => {
    if (status === "VALID") return "cert-valid";
    if (status === "EXPIRING_SOON") return "cert-expiring";
    return "cert-expired";
  };

  const getStatusIcon = (status) => {
    if (status === "VALID") return "✅";
    if (status === "EXPIRING_SOON") return "⚠️";
    return "🚨";
  };

  const safeCertificates = Array.isArray(certificates) ? certificates : [];

  const filtered = filter === "ALL"
    ? safeCertificates
    : safeCertificates.filter((c) => (c?.status || "VALID") === filter);

  const stats = {
    valid: safeCertificates.filter((c) => (c?.status || "VALID") === "VALID").length,
    expiring: safeCertificates.filter((c) => c?.status === "EXPIRING_SOON").length,
    expired: safeCertificates.filter((c) => c?.status === "EXPIRED").length,
  };

  const canManage = role === "LAB_MANAGER" || role === "INSTITUTION_ADMIN" || role === "SYSTEM_ADMIN";
  const canDelete = role === "INSTITUTION_ADMIN" || role === "SYSTEM_ADMIN";

  return (
    <Layout>
      <div className="dashboard cert-page">

        {/* Header */}
        <div className="cert-header">
          <div>
            <h1>🏅 Certificate Management</h1>
            <p>Manage equipment certifications, track expiry dates, and perform certificate renewals.</p>
          </div>
          {canManage && (
            <button
              className="cert-add-btn"
              onClick={() => { resetForm(); setShowForm(!showForm); }}
            >
              {showForm ? "✖ Cancel" : "+ Add Certificate"}
            </button>
          )}
        </div>

        {/* Stats Row */}
        <div className="dashboard-container">
          <div className="dashboard-card cert-stat-card cert-stat-valid">
            <div className="card-content">
              <h4>✅ Valid</h4>
              <h2>{stats.valid}</h2>
            </div>
          </div>
          <div className="dashboard-card cert-stat-card cert-stat-expiring">
            <div className="card-content">
              <h4>⚠️ Expiring Soon</h4>
              <h2>{stats.expiring}</h2>
            </div>
          </div>
          <div className="dashboard-card cert-stat-card cert-stat-expired">
            <div className="card-content">
              <h4>🚨 Expired</h4>
              <h2>{stats.expired}</h2>
            </div>
          </div>
          <div className="dashboard-card cert-stat-card">
            <div className="card-content">
              <h4>📋 Total</h4>
              <h2>{safeCertificates.length}</h2>
            </div>
          </div>
        </div>

        {/* Form */}
        {showForm && (
          <div className="cert-form-card" style={isRenewing ? { borderLeft: "5px solid var(--color-success)" } : {}}>
            <div className="form-header">
              <h2>
                {isRenewing
                  ? "🔄 Renew Certificate"
                  : editingId
                  ? "✏️ Edit Certificate"
                  : "➕ Add Certificate"}
              </h2>
              <p>
                {isRenewing
                  ? "Update issue date, enter new valid-through expiry date, and attach updated certificate document."
                  : "Fill in the certificate details for the selected equipment."}
              </p>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-grid">

                <div className="form-group">
                  <label>Equipment *</label>
                  <select
                    name="equipmentId"
                    value={formData.equipmentId}
                    onChange={handleChange}
                    required
                    disabled={isRenewing}
                  >
                    <option value="">Select Equipment</option>
                    {equipment.map((eq) => (
                      <option key={eq.id} value={eq.id}>
                        {eq.name}{eq.laboratory ? ` (${eq.laboratory.name})` : ""}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-group">
                  <label>Certificate Name *</label>
                  <input
                    type="text"
                    name="certificateName"
                    value={formData.certificateName}
                    onChange={handleChange}
                    placeholder="e.g. ISO Compliance, Calibration Certificate"
                    required
                  />
                </div>

                <div className="form-group">
                  <label>New Certificate Number</label>
                  <input
                    type="text"
                    name="certificateNumber"
                    value={formData.certificateNumber}
                    onChange={handleChange}
                    placeholder="Certificate / Ref number"
                  />
                </div>

                <div className="form-group">
                  <label>Issued By Authority</label>
                  <input
                    type="text"
                    name="issuedBy"
                    value={formData.issuedBy}
                    onChange={handleChange}
                    placeholder="Issuing authority"
                  />
                </div>

                <div className="form-group">
                  <label>Issue Date *</label>
                  <input
                    type="date"
                    name="issueDate"
                    value={formData.issueDate}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>New Expiry Date * (Valid Through)</label>
                  <input
                    type="date"
                    name="expiryDate"
                    value={formData.expiryDate}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="form-group" style={{ gridColumn: "1 / -1" }}>
                  <label>Renewal Remarks / Audit Notes</label>
                  <textarea
                    name="remarks"
                    value={formData.remarks}
                    onChange={handleChange}
                    placeholder="Notes regarding renewal, calibration results, or testing metrics..."
                  />
                </div>

                <div className="form-group" style={{ gridColumn: "1 / -1" }}>
                  <label>New Certificate Document (PDF / Image)</label>
                  <input type="file" onChange={handleFileUpload} accept=".pdf,.jpg,.jpeg,.png" />
                  {uploading && <small style={{ color: "#94a3b8" }}>⏳ Uploading...</small>}
                  {formData.certificateFileUrl && (
                    <small style={{ color: "#22c55e" }}>
                      ✅ Attached document: {formData.certificateFileUrl.split("/").pop()}
                    </small>
                  )}
                </div>

              </div>

              <div className="cert-form-actions">
                <button type="submit" className="submit-btn" style={isRenewing ? { background: "var(--color-success)" } : {}}>
                  {isRenewing
                    ? "🔄 Submit Renewal (Sets Status to VALID)"
                    : editingId
                    ? "💾 Update Certificate"
                    : "🏅 Save Certificate"}
                </button>
                <button type="button" className="cancel-btn" onClick={resetForm}>
                  Cancel
                </button>
              </div>
            </form>
          </div>
        )}

        {/* Filter Tabs */}
        <div className="cert-filters">
          {["ALL", "VALID", "EXPIRING_SOON", "EXPIRED"].map((f) => (
            <button
              key={f}
              className={`tab-btn ${filter === f ? "active" : ""}`}
              onClick={() => setFilter(f)}
            >
              {f === "ALL" && "📋 All"}
              {f === "VALID" && "✅ Valid"}
              {f === "EXPIRING_SOON" && "⚠️ Expiring Soon"}
              {f === "EXPIRED" && "🚨 Expired"}
              <span className="filter-count">
                {f === "ALL" ? safeCertificates.length
                  : safeCertificates.filter((c) => (c?.status || "VALID") === f).length}
              </span>
            </button>
          ))}
        </div>

        {/* Certificates Table */}
        <div className="table-card">
          <div className="table-header">
            <h2>Certificate Records</h2>
          </div>
          <table className="data-table">
            <thead>
              <tr>
                <th>Equipment</th>
                <th>Certificate Name</th>
                <th>Certificate No.</th>
                <th>Issued By</th>
                <th>Issue Date</th>
                <th>Expiry Date</th>
                <th>Status</th>
                <th>File</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filtered.length > 0 ? (
                filtered.map((cert) => {
                  const certStatus = cert.status || "VALID";
                  return (
                    <tr key={cert.id}>
                      <td><strong>{cert.equipment?.name || "N/A"}</strong></td>
                      <td>{cert.certificateName}</td>
                      <td>{cert.certificateNumber || "-"}</td>
                      <td>{cert.issuedBy || "-"}</td>
                      <td>{cert.issueDate || "-"}</td>
                      <td>
                        <span className={certStatus !== "VALID" ? "expiry-alert" : ""}>
                          {cert.expiryDate}
                        </span>
                      </td>
                      <td>
                        <span className={`status-badge ${getStatusClass(certStatus)}`}>
                          {getStatusIcon(certStatus)} {certStatus.replace("_", " ")}
                        </span>
                      </td>
                      <td>
                        {cert.certificateFileUrl ? (
                          <a
                            href={`http://localhost:8080${cert.certificateFileUrl}`}
                            target="_blank"
                            rel="noreferrer"
                            className="cert-file-link"
                          >
                            📄 View
                          </a>
                        ) : "-"}
                      </td>
                      <td>
                        <div style={{ display: "flex", gap: "6px", alignItems: "center" }}>
                          {canManage && (
                            <button
                              className="action-btn approve-btn"
                              onClick={() => handleRenew(cert)}
                              title="Renew Certificate Expiry Date"
                              style={{ background: "#10b981", borderColor: "#10b981", color: "#ffffff" }}
                            >
                              🔄 Renew
                            </button>
                          )}
                          {canManage && (
                            <button
                              className="action-btn edit-btn"
                              onClick={() => handleEdit(cert)}
                            >
                              ✏️ Edit
                            </button>
                          )}
                          {canDelete && (
                            <button
                              className="action-btn reject-btn"
                              onClick={() => handleDelete(cert.id)}
                            >
                              🗑️ Delete
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  );
                })
              ) : (
                <tr>
                  <td colSpan="9" className="empty-table">
                    🏅 No certificates found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

      </div>
    </Layout>
  );
}

export default CertificateManagementPage;
