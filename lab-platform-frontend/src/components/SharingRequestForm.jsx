import { useState, useEffect } from "react";
import api from "../api/axios";
import { FaExclamationTriangle, FaTimesCircle, FaBan } from "react-icons/fa";
import "./Form.css";

function SharingRequestForm({
  equipment,
  formData,
  handleChange,
  handleSubmit,
  loadingEquipment,
}) {
  const todayStr = new Date().toISOString().split("T")[0];
  const [estimate, setEstimate] = useState(null);
  const [loadingEstimate, setLoadingEstimate] = useState(false);
  const [showConflictModal, setShowConflictModal] = useState(false);

  useEffect(() => {
    if (formData.equipmentId && formData.startDate && formData.endDate) {
      if (new Date(formData.endDate) >= new Date(formData.startDate)) {
        fetchEstimate();
      } else {
        setEstimate(null);
        setShowConflictModal(false);
      }
    } else {
      setEstimate(null);
      setShowConflictModal(false);
    }
  }, [formData.equipmentId, formData.startDate, formData.endDate]);

  const fetchEstimate = async () => {
    try {
      setLoadingEstimate(true);
      const res = await api.get("/sharing-requests/estimate-cost", {
        params: {
          equipmentId: formData.equipmentId,
          startDate: formData.startDate,
          endDate: formData.endDate,
        },
      });
      setEstimate(res.data);
      if (res.data && res.data.isAvailable === false) {
        setShowConflictModal(true);
      } else {
        setShowConflictModal(false);
      }
    } catch (err) {
      console.error("Estimate fetch error:", err);
      setEstimate(null);
    } finally {
      setLoadingEstimate(false);
    }
  };

  const isConflict = estimate && estimate.isAvailable === false;

  return (
    <div className="form-card">

      <div className="form-header">
        <div>
          <h2>🤝 Create Inter-Institution Sharing Request</h2>
          <p>
            Request shared laboratory assets owned by external partner institutions for collaborative research.
          </p>
        </div>
      </div>

      <form onSubmit={handleSubmit}>

        <div className="form-grid">

          <div className="form-group full-width">
            <label>External Shared Equipment Asset *</label>

            <select
              name="equipmentId"
              value={formData.equipmentId}
              onChange={handleChange}
              required
            >
              <option value="">Select Partner Institution Equipment Asset</option>

              {loadingEquipment ? (
                <option value="" disabled>Loading external shared equipment...</option>
              ) : equipment && equipment.length > 0 ? (
                equipment.map((item) => (
                  <option
                    key={item.id}
                    value={item.id}
                  >
                    {item.name} — {item.laboratory?.name || "Lab"} — {item.laboratory?.institution?.name || "External Institution"} (₹{item.costPerDay?.toLocaleString()}/day)
                  </option>
                ))
              ) : (
                <option value="" disabled>
                  No external shared equipment available from other institutions
                </option>
              )}
            </select>
            {equipment && equipment.length > 0 && (
              <small style={{ color: "var(--color-text-subtle)", marginTop: "4px", display: "block" }}>
                🔒 Displays only equipment marked for sharing owned by external partner institutions.
              </small>
            )}
          </div>

          <div className="form-group">
            <label>Sharing Start Date *</label>
            <input
              type="date"
              name="startDate"
              value={formData.startDate || ""}
              onChange={handleChange}
              min={todayStr}
              required
            />
          </div>

          <div className="form-group">
            <label>Sharing End Date *</label>
            <input
              type="date"
              name="endDate"
              value={formData.endDate || ""}
              onChange={handleChange}
              min={formData.startDate || todayStr}
              required
            />
          </div>

          {/* Date Conflict Warning Banner */}
          {isConflict && (
            <div className="form-group full-width" style={{
              background: "#fef2f2",
              border: "1px solid #fca5a5",
              borderRadius: "var(--radius-md)",
              padding: "16px",
              marginTop: "8px"
            }}>
              <div style={{ display: "flex", alignItems: "flex-start", gap: "10px" }}>
                <FaExclamationTriangle style={{ color: "#dc2626", fontSize: "20px", marginTop: "2px" }} />
                <div>
                  <h4 style={{ margin: "0 0 4px 0", color: "#991b1b", fontSize: "14px", fontWeight: 700 }}>
                    🚫 Requested Dates Already Reserved / Booked
                  </h4>
                  <p style={{ margin: 0, color: "#7f1d1d", fontSize: "13px" }}>
                    {estimate.conflictMessage || "The requested dates collide with an existing approved sharing request or booking. Please select different dates."}
                  </p>
                </div>
              </div>
            </div>
          )}

          {/* Live Estimated Cost Breakdown Box */}
          {estimate && estimate.isAvailable && (
            <div className="form-group full-width" style={{
              background: "rgba(59, 130, 246, 0.05)",
              border: "1px dashed var(--color-primary)",
              borderRadius: "var(--radius-md)",
              padding: "16px",
              marginTop: "8px"
            }}>
              <h4 style={{ margin: "0 0 10px 0", color: "var(--color-primary)", fontSize: "14px", display: "flex", alignItems: "center", gap: "6px" }}>
                💰 Estimated Sharing Cost Breakdown (Simulated)
              </h4>
              <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))", gap: "12px", fontSize: "13px" }}>
                <div>
                  <span style={{ color: "var(--color-text-subtle)" }}>Equipment Rate:</span>
                  <br /><strong>₹{estimate.costPerDay?.toLocaleString()}/day</strong>
                </div>
                <div>
                  <span style={{ color: "var(--color-text-subtle)" }}>Sharing Duration:</span>
                  <br /><strong>{estimate.durationDays} day(s)</strong>
                </div>
                <div>
                  <span style={{ color: "var(--color-text-subtle)" }}>Base Usage Cost:</span>
                  <br /><strong>₹{estimate.baseCost?.toLocaleString()}</strong>
                </div>
                <div>
                  <span style={{ color: "var(--color-text-subtle)" }}>Inter-Institution Fee ({estimate.feePercentage}%):</span>
                  <br /><strong>₹{estimate.interInstitutionFee?.toLocaleString()}</strong>
                </div>
              </div>
              <div style={{ marginTop: "12px", paddingTop: "10px", borderTop: "1px solid rgba(0,0,0,0.08)", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <span style={{ fontWeight: 700, fontSize: "15px", color: "var(--color-text-main)" }}>Estimated Total:</span>
                <span style={{ fontWeight: 800, fontSize: "18px", color: "var(--color-success)" }}>₹{estimate.totalAmount?.toLocaleString()}</span>
              </div>
              <small style={{ color: "var(--color-text-subtle)", fontSize: "11px", display: "block", marginTop: "6px" }}>
                ℹ️ Academic demonstration estimate. Authoritative calculation enforced by Spring Boot backend. No real payment required.
              </small>
            </div>
          )}

          <div className="form-group full-width">
            <label>Collaborative Purpose *</label>

            <textarea
              name="purpose"
              value={formData.purpose}
              onChange={handleChange}
              placeholder="Describe joint research goals, academic projects, or testing objectives..."
              required
            />

          </div>

        </div>

        <button
          className="submit-btn"
          type="submit"
          disabled={!formData.equipmentId || !formData.startDate || !formData.endDate || !formData.purpose.trim() || isConflict}
          style={isConflict ? { background: "#9ca3af", cursor: "not-allowed" } : {}}
        >
          {isConflict ? "🚫 Selected Dates Collide / Unavailable" : "🤝 Submit Inter-Institution Request"}
        </button>

      </form>

      {/* Date Conflict Pop-up Modal */}
      {showConflictModal && (
        <div style={{
          position: "fixed",
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: "rgba(0, 0, 0, 0.5)",
          backdropFilter: "blur(4px)",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          zIndex: 9999
        }}>
          <div style={{
            background: "#ffffff",
            borderRadius: "16px",
            padding: "24px",
            maxWidth: "480px",
            width: "90%",
            boxShadow: "0 20px 25px -5px rgba(0,0,0,0.1), 0 8px 10px -6px rgba(0,0,0,0.1)",
            border: "1px solid #fee2e2"
          }}>
            <div style={{ display: "flex", alignItems: "center", gap: "12px", color: "#dc2626", marginBottom: "14px" }}>
              <FaBan style={{ fontSize: "24px" }} />
              <h3 style={{ margin: 0, fontSize: "18px", fontWeight: 700 }}>Requested Dates Already Booked!</h3>
            </div>
            
            <p style={{ color: "var(--color-text-main)", fontSize: "14px", lineHeight: "1.5", margin: "0 0 16px 0" }}>
              {estimate?.conflictMessage || `The requested period (${formData.startDate} to ${formData.endDate}) overlaps with an existing approved sharing request or booking for this equipment.`}
            </p>

            <div style={{
              background: "#fff5f5",
              borderLeft: "4px solid #ef4444",
              padding: "10px 14px",
              borderRadius: "4px",
              fontSize: "13px",
              color: "#991b1b",
              marginBottom: "20px"
            }}>
              <strong>Action Required:</strong> Please select a different date range that does not collide with active reservations.
            </div>

            <div style={{ display: "flex", justifyContent: "flex-end" }}>
              <button
                onClick={() => setShowConflictModal(false)}
                style={{
                  background: "#dc2626",
                  color: "#ffffff",
                  border: "none",
                  padding: "10px 20px",
                  borderRadius: "8px",
                  fontWeight: 600,
                  fontSize: "14px",
                  cursor: "pointer"
                }}
              >
                Got It, Change Dates
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}

export default SharingRequestForm;