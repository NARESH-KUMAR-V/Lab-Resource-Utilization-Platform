import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import {
  FaReceipt,
  FaRupeeSign,
  FaBuilding,
  FaUniversity,
  FaFileInvoiceDollar,
  FaPrint,
  FaTimes,
  FaCheckCircle,
  FaInfoCircle,
  FaExchangeAlt,
  FaHandHoldingUsd
} from "react-icons/fa";

import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import Layout from "../components/Layout";
import "./BillingPage.css";

function BillingPage() {
  const { role } = useAuth();
  const [billings, setBillings] = useState([]);
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedInvoice, setSelectedInvoice] = useState(null);

  const isManagerOrAdmin = ["LAB_MANAGER", "DEPARTMENT_HEAD", "INSTITUTION_ADMIN", "SYSTEM_ADMIN"].includes(role);

  useEffect(() => {
    fetchBillingData();
  }, []);

  const fetchBillingData = async () => {
    try {
      setLoading(true);
      const [recordsRes, summaryRes] = await Promise.all([
        isManagerOrAdmin ? api.get("/billing") : api.get("/billing/my"),
        api.get("/billing/summary")
      ]);

      setBillings(recordsRes.data || []);
      setSummary(summaryRes.data || null);
    } catch (error) {
      console.error(error);
      toast.error("Failed to load cost & billing records.");
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = async (id, newStatus) => {
    try {
      await api.put(`/billing/${id}/status?status=${newStatus}`);
      toast.success(`Invoice status updated to ${newStatus}`);
      fetchBillingData();
    } catch (error) {
      console.error(error);
      toast.error("Failed to update invoice status.");
    }
  };

  return (
    <Layout>
      <div className="billing-page">
        <div className="billing-header">
          <div>
            <h1>
              <FaReceipt /> Cost &amp; Billing Management
            </h1>
            <p>
              Simulated academic chargeback, usage-based cost tracking, and department/institution billing summaries.
            </p>
          </div>
        </div>

        {/* Academic Notice Banner */}
        <div className="invoice-notice">
          <FaInfoCircle style={{ marginRight: "8px" }} />
          <strong>Simulated Academic Chargeback:</strong> This module estimates resource utilization costs for university budget allocation. No real financial transaction or payment gateway integration occurs.
        </div>

        {/* Summary Statistics Cards */}
        {summary && (
          <div className="billing-summary-grid">
            <div className="billing-summary-card">
              <div className="billing-summary-icon">
                <FaRupeeSign />
              </div>
              <div className="billing-summary-info">
                <h3>Total Billed Amount</h3>
                <div className="value">₹{summary.totalBilledAmount?.toLocaleString()}</div>
              </div>
            </div>

            <div className="billing-summary-card">
              <div className="billing-summary-icon" style={{ background: "rgba(59, 130, 246, 0.15)", color: "var(--color-primary)" }}>
                <FaExchangeAlt />
              </div>
              <div className="billing-summary-info">
                <h3>Outgoing Sharing Cost</h3>
                <div className="value">₹{summary.outgoingSharingCost?.toLocaleString() || 0}</div>
                <small style={{ fontSize: "11px", color: "var(--color-text-subtle)" }}>Simulated usage cost</small>
              </div>
            </div>

            <div className="billing-summary-card">
              <div className="billing-summary-icon" style={{ background: "rgba(16, 185, 129, 0.15)", color: "var(--color-success)" }}>
                <FaHandHoldingUsd />
              </div>
              <div className="billing-summary-info">
                <h3>Resource Sharing Value</h3>
                <div className="value">₹{summary.resourceSharingValue?.toLocaleString() || 0}</div>
                <small style={{ fontSize: "11px", color: "var(--color-text-subtle)" }}>Simulated resource revenue</small>
              </div>
            </div>

            <div className="billing-summary-card">
              <div className="billing-summary-icon" style={{ background: "var(--color-warning-bg)", color: "var(--color-warning)" }}>
                <FaRupeeSign />
              </div>
              <div className="billing-summary-info">
                <h3>Inter-Institution Fees</h3>
                <div className="value">₹{summary.totalInterInstitutionFees?.toLocaleString()}</div>
              </div>
            </div>
          </div>
        )}

        {/* Department & Institution Cost Allocation Tables */}
        {summary && isManagerOrAdmin && (
          <div className="summaries-two-col">
            <div className="summary-table-card">
              <h3>
                <FaBuilding /> Department Cost Allocation
              </h3>
              <table>
                <thead>
                  <tr>
                    <th>Department</th>
                    <th style={{ textAlign: "right" }}>Accumulated Cost</th>
                  </tr>
                </thead>
                <tbody>
                  {Object.entries(summary.departmentCostSummary || {}).map(([dept, cost]) => (
                    <tr key={dept}>
                      <td style={{ fontWeight: 600 }}>{dept}</td>
                      <td style={{ color: "var(--color-success)", fontWeight: 700, textAlign: "right" }}>
                        ₹{cost.toLocaleString()}
                      </td>
                    </tr>
                  ))}
                  {Object.keys(summary.departmentCostSummary || {}).length === 0 && (
                    <tr>
                      <td colSpan="2" style={{ textAlign: "center", color: "var(--color-text-subtle)" }}>No department costs calculated yet.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>

            <div className="summary-table-card">
              <h3>
                <FaUniversity /> Institution Cost Summary
              </h3>
              <table>
                <thead>
                  <tr>
                    <th>Institution</th>
                    <th style={{ textAlign: "right" }}>Total Resource Usage</th>
                  </tr>
                </thead>
                <tbody>
                  {Object.entries(summary.institutionCostSummary || {}).map(([inst, cost]) => (
                    <tr key={inst}>
                      <td style={{ fontWeight: 600 }}>{inst}</td>
                      <td style={{ color: "var(--color-primary)", fontWeight: 700, textAlign: "right" }}>
                        ₹{cost.toLocaleString()}
                      </td>
                    </tr>
                  ))}
                  {Object.keys(summary.institutionCostSummary || {}).length === 0 && (
                    <tr>
                      <td colSpan="2" style={{ textAlign: "center", color: "var(--color-text-subtle)" }}>No institution costs calculated yet.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Invoices & Chargeback Records Data Grid */}
        <div className="table-card-wrapper">
          <h3>
            <FaReceipt /> Invoices &amp; Chargeback Records
          </h3>

          <table className="data-table">
            <thead>
              <tr>
                <th style={{ width: "160px" }}>Invoice No</th>
                <th style={{ width: "220px" }}>Equipment</th>
                <th style={{ width: "200px" }}>Type / Partner</th>
                <th style={{ width: "120px" }}>Usage</th>
                <th style={{ width: "130px" }}>Base Cost (₹)</th>
                <th style={{ width: "130px" }}>Fee (₹)</th>
                <th style={{ width: "130px" }}>Total (₹)</th>
                <th style={{ width: "120px" }}>Status</th>
                <th style={{ width: "160px" }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {billings.map((b) => (
                <tr key={b.id}>
                  <td style={{ fontWeight: 700, color: "var(--color-primary)" }}>
                    {b.invoiceNumber}
                  </td>
                  <td style={{ fontWeight: 600 }}>
                    {b.equipment?.name || "-"}
                  </td>
                  <td>
                    <div style={{ fontWeight: 600, color: "var(--color-text-main)" }}>
                      {b.sharingRequest ? "🤝 Inter-Inst Sharing" : "Normal Booking"}
                    </div>
                    <div style={{ fontSize: "11.5px", color: "var(--color-text-subtle)", marginTop: "2px" }}>
                      {b.institution?.name || "Institution"}
                    </div>
                  </td>
                  <td>
                    {b.usageDays} Day(s)
                  </td>
                  <td>
                    ₹{b.estimatedCost?.toLocaleString()}
                  </td>
                  <td style={{ color: b.interInstitutionFee > 0 ? "var(--color-warning)" : "inherit", fontWeight: b.interInstitutionFee > 0 ? 600 : 400 }}>
                    ₹{b.interInstitutionFee?.toLocaleString()}
                  </td>
                  <td style={{ fontWeight: 800, color: "var(--color-success)" }}>
                    ₹{b.totalAmount?.toLocaleString()}
                  </td>
                  <td>
                    <span className={`status-badge status-${b.billingStatus?.toLowerCase()}`}>
                      {b.billingStatus}
                    </span>
                  </td>
                  <td>
                    <div style={{ display: "flex", gap: "8px" }}>
                      <button
                        className="action-btn view-btn"
                        onClick={() => setSelectedInvoice(b)}
                        title="View Invoice"
                      >
                        <FaFileInvoiceDollar /> Invoice
                      </button>

                      {isManagerOrAdmin && b.billingStatus !== "CLOSED" && (
                        <button
                          className="action-btn approve-btn"
                          onClick={() => handleStatusChange(b.id, b.billingStatus === "ESTIMATED" ? "GENERATED" : "CLOSED")}
                        >
                          {b.billingStatus === "ESTIMATED" ? "Generate" : "Close"}
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}

              {billings.length === 0 && !loading && (
                <tr>
                  <td colSpan="9" style={{ textAlign: "center", padding: "40px", color: "var(--color-text-subtle)" }}>
                    No billing records available. Create and approve equipment bookings or sharing requests to generate billing statements.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Invoice Modal */}
        {selectedInvoice && (
          <div className="invoice-modal-overlay">
            <div className="invoice-modal">
              <div className="invoice-header">
                <div>
                  <h2>LAB RESOURCE UTILIZATION PLATFORM</h2>
                  <p style={{ margin: "4px 0 0 0", color: "var(--color-text-subtle)", fontSize: "12px" }}>
                    Academic Cost Allocation &amp; Inter-Institution Simulated Invoice
                  </p>
                </div>
                <button
                  style={{ background: "none", border: "none", fontSize: "18px", cursor: "pointer", color: "var(--color-text-subtle)" }}
                  onClick={() => setSelectedInvoice(null)}
                >
                  <FaTimes />
                </button>
              </div>

              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px", fontSize: "13px" }}>
                <div>
                  <strong>Invoice Number:</strong> {selectedInvoice.invoiceNumber}<br />
                  <strong>Date:</strong> {selectedInvoice.invoiceDate}<br />
                  <strong>Status:</strong> <span className={`status-badge status-${selectedInvoice.billingStatus?.toLowerCase()}`}>{selectedInvoice.billingStatus}</span>
                </div>
                <div style={{ textAlign: "right" }}>
                  <strong>Billed To (Requesting Inst):</strong> {selectedInvoice.institution?.name || selectedInvoice.user?.name}<br />
                  <strong>Owning Institution:</strong> {selectedInvoice.owningInstitution?.name || "Partner Inst"}<br />
                  <strong>Type:</strong> {selectedInvoice.sharingRequest ? "Inter-Institution Sharing" : "Normal Equipment Booking"}
                </div>
              </div>

              <table className="custom-table" style={{ fontSize: "13px", marginTop: "10px" }}>
                <thead>
                  <tr>
                    <th>Item Description</th>
                    <th>Rate / Day</th>
                    <th>Duration</th>
                    <th>Amount</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>{selectedInvoice.equipment?.name} Usage</td>
                    <td>₹{selectedInvoice.costPerDay?.toLocaleString()}</td>
                    <td>{selectedInvoice.usageDays} Days</td>
                    <td>₹{selectedInvoice.estimatedCost?.toLocaleString()}</td>
                  </tr>
                  {selectedInvoice.interInstitutionFee > 0 && (
                    <tr>
                      <td>Inter-Institution Sharing Surcharge Fee (10%)</td>
                      <td>-</td>
                      <td>-</td>
                      <td>₹{selectedInvoice.interInstitutionFee?.toLocaleString()}</td>
                    </tr>
                  )}
                </tbody>
              </table>

              <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", borderTop: "2px solid var(--color-border)", paddingTop: "14px" }}>
                <div style={{ fontSize: "12px", color: "var(--color-text-muted)" }}>
                  * Simulated academic cost calculation only. No real payment required.
                </div>
                <div style={{ fontSize: "18px", fontWeight: 800, color: "var(--color-success)" }}>
                  Total Amount: ₹{selectedInvoice.totalAmount?.toLocaleString()}
                </div>
              </div>

              <div style={{ display: "flex", justifyContent: "flex-end", gap: "10px" }}>
                <button
                  className="toolbar-btn secondary"
                  onClick={() => window.print()}
                >
                  <FaPrint /> Print Statement
                </button>
                <button
                  className="toolbar-btn"
                  onClick={() => setSelectedInvoice(null)}
                >
                  Close
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </Layout>
  );
}

export default BillingPage;
