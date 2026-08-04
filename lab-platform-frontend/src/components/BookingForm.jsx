import { useEffect, useState } from "react";
import { FaCalendarCheck, FaFlask, FaRupeeSign, FaInfoCircle, FaExclamationTriangle, FaCertificate, FaFilePdf, FaCheckCircle } from "react-icons/fa";
import api from "../api/axios";
import "./Form.css";

function BookingForm({
  equipment,
  bookingData,
  handleChange,
  handleSubmit,
  selectedEquipment,
}) {

  const [certificates, setCertificates] = useState([]);
  const [loadingCerts, setLoadingCerts] = useState(false);

  const [existingBookings, setExistingBookings] = useState([]);
  const [loadingBookings, setLoadingBookings] = useState(false);

  useEffect(() => {
    if (selectedEquipment?.id) {
      // Fetch equipment certificates
      setLoadingCerts(true);
      api
        .get(`/certificates/equipment/${selectedEquipment.id}`)
        .then((res) => {
          setCertificates(res.data || []);
        })
        .catch((err) => {
          console.error("Error loading equipment certificates:", err);
          setCertificates([]);
        })
        .finally(() => {
          setLoadingCerts(false);
        });

      // Fetch active bookings (APPROVED, PENDING, WAITING) for this equipment to perform date overlap checks
      setLoadingBookings(true);
      api
        .get(`/bookings/equipment/${selectedEquipment.id}`)
        .then((res) => {
          setExistingBookings(res.data || []);
        })
        .catch((err) => {
          console.error("Error loading existing equipment bookings:", err);
          setExistingBookings([]);
        })
        .finally(() => {
          setLoadingBookings(false);
        });
    } else {
      setCertificates([]);
      setExistingBookings([]);
    }
  }, [selectedEquipment?.id]);

  const bookableEquipment = equipment.filter(
    (item) =>
      item.status === "AVAILABLE" ||
      item.status === "BOOKED"
  );

  const today = new Date().toISOString().split("T")[0];

  let estimatedDays = 0;
  let estimatedCost = 0;

  const hasStartDate = Boolean(bookingData.startDate);
  const hasEndDate = Boolean(bookingData.endDate);
  const datesSelected = hasStartDate && hasEndDate;

  let isInvalidDateRange = false;
  let dateErrorMessage = "";

  if (datesSelected) {
    if (bookingData.endDate < bookingData.startDate) {
      isInvalidDateRange = true;
      dateErrorMessage = "End date cannot be before start date.";
    } else if (bookingData.startDate < today) {
      isInvalidDateRange = true;
      dateErrorMessage = "Start date cannot be in the past.";
    }
  }

  if (
    selectedEquipment &&
    datesSelected &&
    !isInvalidDateRange
  ) {
    const start = new Date(bookingData.startDate);
    const end = new Date(bookingData.endDate);

    estimatedDays =
      Math.floor(
        (end - start) /
          (1000 * 60 * 60 * 24)
      ) + 1;

    if (estimatedDays > 0) {
      estimatedCost =
        estimatedDays *
        selectedEquipment.costPerDay;
    }
  }

  // Date Overlap Detection logic: requestedStart <= existingEnd && requestedEnd >= existingStart
  let conflictingApproved = null;
  let conflictingWaiting = null;

  if (datesSelected && !isInvalidDateRange && selectedEquipment) {
    const reqStart = bookingData.startDate;
    const reqEnd = bookingData.endDate;

    const overlappingList = existingBookings.filter((b) => {
      const existStart = b.startDate;
      const existEnd = b.endDate;
      return reqStart <= existEnd && reqEnd >= existStart;
    });

    conflictingApproved = overlappingList.find((b) => b.status === "APPROVED");
    conflictingWaiting = overlappingList.find((b) => b.status === "WAITING" || b.status === "PENDING");
  }

  const hasApprovedConflict = Boolean(conflictingApproved);
  const hasWaitingConflict = Boolean(conflictingWaiting);
  const hasDateConflict = hasApprovedConflict || hasWaitingConflict;

  const expiredCertificates = certificates.filter(c => c.status === "EXPIRED");

  return (

    <div className="form-card">

      <div className="form-header">

        <div>

          <h2>
            📅 Reserve Equipment
          </h2>

          <p>
            Reserve laboratory equipment for your research or experimental workflow.
          </p>

        </div>

      </div>

      <form onSubmit={handleSubmit}>

        <div className="form-grid">

          <div className="form-group full-width">

            <label>
              Equipment *
            </label>

            <select
              name="equipmentId"
              value={bookingData.equipmentId}
              onChange={handleChange}
              required
            >

              <option value="">
                Select Equipment
              </option>

              {bookableEquipment.map((item) => (

                <option
                  key={item.id}
                  value={item.id}
                >

                  {item.name}

                  {item.laboratory
                    ? ` (${item.laboratory.name})`
                    : ""}

                  {item.status === "BOOKED"
                    ? " - Currently Booked (Check Available Dates)"
                    : " - Available"}

                </option>

              ))}

            </select>

            {bookableEquipment.length === 0 && (

              <small
                style={{
                  color: "var(--color-danger)",
                  marginTop: "6px",
                  fontSize: "12.5px"
                }}
              >
                No equipment is currently available for booking.
              </small>

            )}

          </div>

          {selectedEquipment && (

            <div
              className="full-width"
              style={{
                border: "1px solid var(--color-border)",
                borderRadius: "var(--radius-lg)",
                padding: "20px",
                display: "flex",
                flexDirection: "column",
                gap: "16px",
                background: "var(--color-bg-subtle)",
              }}
            >

              <div style={{ display: "flex", gap: "20px", alignItems: "flex-start" }}>

                <img
                  src={
                    selectedEquipment.imageUrl
                      ? `http://localhost:8080${selectedEquipment.imageUrl}`
                      : "/assets/no-image.png"
                  }
                  alt={selectedEquipment.name}
                  style={{
                    width: "180px",
                    height: "130px",
                    objectFit: "cover",
                    borderRadius: "var(--radius-md)",
                    border: "1px solid var(--color-border)",
                  }}
                />

                <div style={{ flex: 1, fontSize: "13.5px" }}>

                  <h3
                    style={{
                      margin: "0 0 8px 0",
                      color: "var(--color-text-main)",
                      fontSize: "16px",
                      fontWeight: 700
                    }}
                  >
                    {selectedEquipment.name}
                  </h3>

                  <p style={{ margin: "0 0 6px 0", color: "var(--color-text-muted)" }}>
                    {selectedEquipment.description || "No description available."}
                  </p>

                  <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(160px, 1fr))", gap: "8px", marginTop: "12px" }}>
                    <div>
                      <span style={{ color: "var(--color-text-subtle)", fontSize: "12px" }}>Laboratory</span>
                      <div style={{ fontWeight: 600 }}>{selectedEquipment.laboratory?.name || "-"}</div>
                    </div>
                    <div>
                      <span style={{ color: "var(--color-text-subtle)", fontSize: "12px" }}>Cost / Day</span>
                      <div style={{ fontWeight: 600 }}>₹{selectedEquipment.costPerDay?.toLocaleString()}</div>
                    </div>
                    <div>
                      <span style={{ color: "var(--color-text-subtle)", fontSize: "12px" }}>General Status</span>
                      <div>
                        <span className={`status-badge ${selectedEquipment.status.toLowerCase()}`}>
                          {selectedEquipment.status === "BOOKED" ? "Currently Booked" : "Available"}
                        </span>
                      </div>
                    </div>
                  </div>

                </div>

              </div>

              {/* Equipment Compliance & Certificate Inspection */}
              <div style={{ borderTop: "1px solid var(--color-border)", paddingTop: "14px" }}>

                <h4 style={{ margin: "0 0 10px 0", fontSize: "13.5px", fontWeight: 700, color: "var(--color-primary)", display: "flex", alignItems: "center", gap: "6px" }}>
                  <FaCertificate /> Equipment Compliance &amp; Certificates
                </h4>

                {/* EXPIRED CERTIFICATE WARNING BANNER */}
                {expiredCertificates.length > 0 && (
                  <div style={{
                    background: "var(--color-danger-bg)",
                    border: "1px solid rgba(220, 38, 38, 0.4)",
                    borderRadius: "var(--radius-md)",
                    padding: "12px 14px",
                    color: "var(--color-danger)",
                    fontSize: "13px",
                    fontWeight: 600,
                    marginBottom: "12px",
                    display: "flex",
                    alignItems: "center",
                    gap: "10px"
                  }}>
                    <FaExclamationTriangle style={{ fontSize: "18px", flexShrink: 0 }} />
                    <div>
                      <strong>⚠️ CERTIFICATE EXPIRED WARNING:</strong> This equipment has an EXPIRED calibration/safety certificate (
                      {expiredCertificates.map(c => c.certificateName).join(", ")}
                      ). Please verify calibration status with the laboratory manager before running sensitive experiments.
                    </div>
                  </div>
                )}

                {loadingCerts ? (

                  <div style={{ fontSize: "12.5px", color: "var(--color-text-subtle)" }}>
                    Checking equipment certificates...
                  </div>

                ) : certificates.length > 0 ? (

                  <div style={{ display: "flex", flexDirection: "column", gap: "8px" }}>

                    {certificates.map((cert) => (

                      <div
                        key={cert.id}
                        style={{
                          padding: "10px 14px",
                          background: "var(--color-bg-card)",
                          border: "1px solid var(--color-border)",
                          borderRadius: "var(--radius-md)",
                          display: "flex",
                          justifyContent: "space-between",
                          alignItems: "center",
                          fontSize: "12.5px"
                        }}
                      >

                        <div>
                          <div style={{ fontWeight: 700, color: "var(--color-text-main)" }}>
                            {cert.certificateName} ({cert.certificateNumber || "N/A"})
                          </div>
                          <div style={{ fontSize: "11.5px", color: "var(--color-text-subtle)", marginTop: "2px" }}>
                            Issued by {cert.issuedBy || "Official Authority"} • Valid through {cert.expiryDate}
                          </div>
                        </div>

                        <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>

                          <span className={`status-badge status-${cert.status === "VALID" ? "approved" : cert.status === "EXPIRING_SOON" ? "pending" : "rejected"}`}>
                            {cert.status ? cert.status.replaceAll("_", " ") : "VALID"}
                          </span>

                          {cert.certificateFileUrl && (
                            <a
                              href={`http://localhost:8080${cert.certificateFileUrl}`}
                              target="_blank"
                              rel="noreferrer"
                              style={{
                                color: "var(--color-primary)",
                                fontWeight: 600,
                                textDecoration: "none",
                                fontSize: "12px",
                                display: "inline-flex",
                                alignItems: "center",
                                gap: "4px",
                                padding: "4px 8px",
                                background: "var(--color-primary-light)",
                                border: "1px solid var(--color-primary-border)",
                                borderRadius: "var(--radius-sm)"
                              }}
                            >
                              <FaFilePdf /> View Document
                            </a>
                          )}

                        </div>

                      </div>

                    ))}

                  </div>

                ) : (

                  <div style={{ fontSize: "12.5px", color: "var(--color-text-muted)", fontStyle: "italic" }}>
                    📜 No certificate available for this equipment.
                  </div>

                )}

              </div>

            </div>

          )}

          <div className="form-group">

            <label>
              Start Date *
            </label>

            <input
              type="date"
              name="startDate"
              value={bookingData.startDate}
              onChange={handleChange}
              min={today}
              required
            />

          </div>

          <div className="form-group">

            <label>
              End Date *
            </label>

            <input
              type="date"
              name="endDate"
              value={bookingData.endDate}
              onChange={handleChange}
              min={bookingData.startDate || today}
              required
            />

          </div>

          {isInvalidDateRange && (

            <div
              className="full-width"
              style={{
                color: "var(--color-danger)",
                background: "var(--color-danger-bg)",
                border: "1px solid rgba(239, 68, 68, 0.3)",
                padding: "10px 14px",
                borderRadius: "var(--radius-md)",
                fontSize: "13px",
                fontWeight: 600,
                display: "flex",
                alignItems: "center",
                gap: "8px"
              }}
            >
              <FaExclamationTriangle />
              {dateErrorMessage}
            </div>

          )}

          {selectedEquipment && datesSelected && !isInvalidDateRange && estimatedDays > 0 && (

            <div
              className="full-width"
              style={{
                background: hasDateConflict ? "var(--color-warning-bg)" : "var(--color-primary-light)",
                border: `1px solid ${hasDateConflict ? "rgba(217, 119, 6, 0.4)" : "var(--color-primary-border)"}`,
                borderRadius: "var(--radius-lg)",
                padding: "20px",
              }}
            >

              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "12px" }}>
                <span style={{ fontWeight: 700, color: hasDateConflict ? "var(--color-warning)" : "var(--color-primary)", fontSize: "14px", display: "flex", alignItems: "center", gap: "6px" }}>
                  <FaRupeeSign /> Estimated Utilization Summary
                </span>
                <span style={{ fontSize: "13px", color: "var(--color-secondary)", fontWeight: 600 }}>
                  {estimatedDays} Day{estimatedDays > 1 ? "s" : ""} ({bookingData.startDate} to {bookingData.endDate})
                </span>
              </div>

              <div style={{ fontSize: "22px", fontWeight: 800, color: hasDateConflict ? "var(--color-warning)" : "var(--color-success)" }}>
                Total Cost: ₹{estimatedCost.toLocaleString()}
              </div>

              {hasDateConflict ? (

                <div
                  style={{
                    marginTop: "12px",
                    padding: "12px 14px",
                    borderRadius: "var(--radius-md)",
                    background: "rgba(217, 119, 6, 0.15)",
                    border: "1px solid rgba(217, 119, 6, 0.4)",
                    color: "var(--color-warning)",
                    fontSize: "13px",
                    fontWeight: 600,
                    display: "flex",
                    alignItems: "center",
                    gap: "10px"
                  }}
                >
                  <FaExclamationTriangle style={{ fontSize: "18px", flexShrink: 0 }} />
                  <div>
                    {hasApprovedConflict && hasWaitingConflict ? (
                      <span>
                        <strong>Approved Booking &amp; Waiting List Conflict:</strong> The selected period ({bookingData.startDate} to {bookingData.endDate}) overlaps with an approved booking ({conflictingApproved.startDate} to {conflictingApproved.endDate}) and existing waiting-list requests. Submitting will place your request into the <strong>Waiting List</strong>.
                      </span>
                    ) : hasApprovedConflict ? (
                      <span>
                        <strong>Equipment Already Booked:</strong> The equipment is already booked during part of the selected period ({conflictingApproved.startDate} to {conflictingApproved.endDate}). You can join the <strong>Waiting List</strong>.
                      </span>
                    ) : (
                      <span>
                        <strong>Waiting-List Requests Exist:</strong> There are already waiting-list requests for the selected period ({conflictingWaiting.startDate} to {conflictingWaiting.endDate}). You can join the <strong>Waiting List</strong>.
                      </span>
                    )}
                  </div>
                </div>

              ) : (

                <div
                  style={{
                    marginTop: "12px",
                    padding: "10px 14px",
                    borderRadius: "var(--radius-md)",
                    background: "rgba(16, 185, 129, 0.1)",
                    border: "1px solid rgba(16, 185, 129, 0.3)",
                    color: "#10b981",
                    fontSize: "13px",
                    fontWeight: 600,
                    display: "flex",
                    alignItems: "center",
                    gap: "8px"
                  }}
                >
                  <FaCheckCircle style={{ fontSize: "16px", flexShrink: 0 }} />
                  <strong>Dates Available:</strong> Equipment is available for your requested date range.
                </div>

              )}

            </div>

          )}

          <div className="form-group full-width">

            <label>
              Research Purpose *
            </label>

            <textarea
              name="purpose"
              value={bookingData.purpose}
              onChange={handleChange}
              placeholder="Describe your research experiment or testing scope..."
              required
            />

          </div>

        </div>

        <button
          className={`submit-btn ${hasDateConflict ? "waiting-btn" : ""}`}
          type="submit"
          disabled={bookableEquipment.length === 0 || isInvalidDateRange || !datesSelected || !bookingData.purpose.trim()}
          style={hasDateConflict ? { background: "var(--color-warning)", borderColor: "var(--color-warning)" } : {}}
        >

          {hasDateConflict
            ? "⏳ Join Waiting List"
            : "📅 Book Equipment"}

        </button>

      </form>

    </div>

  );

}

export default BookingForm;