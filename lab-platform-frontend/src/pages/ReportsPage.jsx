import { toast } from "react-toastify";
import {
  FaFileDownload,
  FaChartBar,
  FaTools,
  FaExchangeAlt,
  FaReceipt,
  FaFilePdf,
  FaFileExcel,
  FaCertificate,
  FaInfoCircle
} from "react-icons/fa";

import api from "../api/axios";
import Layout from "../components/Layout";
import "./ReportsPage.css";

function ReportsPage() {
  const downloadReport = async (endpoint, filename, type) => {
    try {
      toast.info(`Generating ${filename}... Please wait.`);
      const response = await api.get(endpoint, {
        responseType: "blob"
      });

      const blob = new Blob([response.data], {
        type: type === "pdf" ? "application/pdf" : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
      });

      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", filename);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);

      toast.success(`${filename} downloaded successfully!`);
    } catch (error) {
      console.error(error);
      toast.error(`Failed to download ${filename}.`);
    }
  };

  return (
    <Layout>
      <div className="reports-page">
        <div className="reports-header">
          <div>
            <h1>
              <FaFileDownload /> Reports &amp; Intelligence Export
            </h1>
            <p>
              Generate and download comprehensive PDF &amp; Excel reports for equipment utilization, maintenance workflows, inter-institution resource sharing, and academic cost allocation.
            </p>
          </div>
        </div>

        <div className="reports-grid">
          {/* Report 1: Equipment Utilization */}
          <div className="report-card">
            <div className="report-card-header">
              <div className="report-card-icon">
                <FaChartBar />
              </div>
              <div className="report-card-title">
                <h3>Equipment Utilization Report</h3>
                <p>
                  Comprehensive analytics covering usage hours, total bookings, utilization rates (%), idle days, and capacity tier classification.
                </p>
              </div>
            </div>

            <div className="report-card-actions">
              <button
                className="export-btn pdf"
                onClick={() => downloadReport("/reports/utilization/pdf", "utilization_report.pdf", "pdf")}
              >
                <FaFilePdf /> PDF Export
              </button>
              <button
                className="export-btn excel"
                onClick={() => downloadReport("/reports/utilization/excel", "utilization_report.xlsx", "excel")}
              >
                <FaFileExcel /> Excel Export
              </button>
            </div>
          </div>

          {/* Report 2: Maintenance & Service History */}
          <div className="report-card">
            <div className="report-card-header">
              <div className="report-card-icon" style={{ background: "var(--color-warning-bg)", color: "var(--color-warning)" }}>
                <FaTools />
              </div>
              <div className="report-card-title">
                <h3>Maintenance &amp; Service Report</h3>
                <p>
                  Complete service audit log detailing preventive/corrective work orders, technician assignments, maintenance costs, and completion notes.
                </p>
              </div>
            </div>

            <div className="report-card-actions">
              <button
                className="export-btn pdf"
                onClick={() => downloadReport("/reports/maintenance/pdf", "maintenance_report.pdf", "pdf")}
              >
                <FaFilePdf /> PDF Export
              </button>
              <button
                className="export-btn excel"
                onClick={() => downloadReport("/reports/maintenance/excel", "maintenance_report.xlsx", "excel")}
              >
                <FaFileExcel /> Excel Export
              </button>
            </div>
          </div>

          {/* Report 3: Inter-Institution Sharing */}
          <div className="report-card">
            <div className="report-card-header">
              <div className="report-card-icon" style={{ background: "var(--color-success-bg)", color: "var(--color-success)" }}>
                <FaExchangeAlt />
              </div>
              <div className="report-card-title">
                <h3>Inter-Institution Sharing Report</h3>
                <p>
                  Cross-institution resource sharing request history, requesting institution details, research scope, and approval decisions.
                </p>
              </div>
            </div>

            <div className="report-card-actions">
              <button
                className="export-btn pdf"
                onClick={() => downloadReport("/reports/sharing/pdf", "sharing_report.pdf", "pdf")}
              >
                <FaFilePdf /> PDF Export
              </button>
              <button
                className="export-btn excel"
                onClick={() => downloadReport("/reports/sharing/excel", "sharing_report.xlsx", "excel")}
              >
                <FaFileExcel /> Excel Export
              </button>
            </div>
          </div>

          {/* Report 4: Cost & Billing Allocation */}
          <div className="report-card">
            <div className="report-card-header">
              <div className="report-card-icon" style={{ background: "rgba(99, 102, 241, 0.1)", color: "#6366f1" }}>
                <FaReceipt />
              </div>
              <div className="report-card-title">
                <h3>Cost &amp; Billing Allocation Report</h3>
                <p>
                  Simulated academic chargeback report broken down by department cost allocation, inter-institution sharing surcharges, and total billed amounts.
                </p>
              </div>
            </div>

            <div className="report-card-actions">
              <button
                className="export-btn pdf"
                onClick={() => downloadReport("/reports/cost/pdf", "cost_billing_report.pdf", "pdf")}
              >
                <FaFilePdf /> PDF Export
              </button>
              <button
                className="export-btn excel"
                onClick={() => downloadReport("/reports/cost/excel", "cost_billing_report.xlsx", "excel")}
              >
                <FaFileExcel /> Excel Export
              </button>
            </div>
          </div>

          {/* Report 5: Equipment Calibration & Certification */}
          <div className="report-card">
            <div className="report-card-header">
              <div className="report-card-icon" style={{ background: "rgba(236, 72, 153, 0.1)", color: "#ec4899" }}>
                <FaCertificate />
              </div>
              <div className="report-card-title">
                <h3>Calibration &amp; Certificate Audit Report</h3>
                <p>
                  Equipment calibration compliance records, issuing authorities, expiration dates, and renewal status (VALID / EXPIRING / EXPIRED).
                </p>
              </div>
            </div>

            <div className="report-card-actions">
              <button
                className="export-btn pdf"
                onClick={() => downloadReport("/reports/certificates/pdf", "certificates_report.pdf", "pdf")}
              >
                <FaFilePdf /> PDF Export
              </button>
              <button
                className="export-btn excel"
                onClick={() => downloadReport("/reports/certificates/excel", "certificates_report.xlsx", "excel")}
              >
                <FaFileExcel /> Excel Export
              </button>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}

export default ReportsPage;
