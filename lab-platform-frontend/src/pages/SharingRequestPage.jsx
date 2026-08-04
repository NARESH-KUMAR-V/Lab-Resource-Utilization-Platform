import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import Layout from "../components/Layout";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import SharingRequestForm from "../components/SharingRequestForm";
import SharingRequestTable from "../components/SharingRequestTable";
import DashboardCard from "../components/DashboardCard";
import { FaShareAlt, FaCalendarCheck, FaClock, FaTimesCircle, FaInbox, FaPaperPlane, FaExclamationCircle } from "react-icons/fa";

function SharingRequestPage() {
  const { role } = useAuth();
  const [equipment, setEquipment] = useState([]);
  const [loadingEquipment, setLoadingEquipment] = useState(false);
  
  const [incomingRequests, setIncomingRequests] = useState([]);
  const [outgoingRequests, setOutgoingRequests] = useState([]);
  const [allRequests, setAllRequests] = useState([]);
  
  const [activeTab, setActiveTab] = useState("incoming");

  const [formData, setFormData] = useState({
    equipmentId: "",
    startDate: "",
    endDate: "",
    purpose: "",
  });

  const [modalMessage, setModalMessage] = useState(null);

  const isSystemAdmin = role === "SYSTEM_ADMIN";

  useEffect(() => {
    fetchExternalEquipment();
    fetchRequests();
  }, []);

  const fetchExternalEquipment = async () => {
    try {
      setLoadingEquipment(true);
      const response = await api.get("/equipment/external-shared");
      setEquipment(Array.isArray(response.data) ? response.data : []);
    } catch (error) {
      console.error(error);
      toast.error("Failed to load external shared equipment.");
      setEquipment([]);
    } finally {
      setLoadingEquipment(false);
    }
  };

  const fetchRequests = async () => {
    try {
      if (isSystemAdmin) {
        const response = await api.get("/sharing-requests");
        setAllRequests(Array.isArray(response.data) ? response.data : []);
      } else {
        const [incRes, outRes] = await Promise.all([
          api.get("/sharing-requests/incoming"),
          api.get("/sharing-requests/outgoing"),
        ]);
        setIncomingRequests(Array.isArray(incRes.data) ? incRes.data : []);
        setOutgoingRequests(Array.isArray(outRes.data) ? outRes.data : []);
      }
    } catch (error) {
      console.error(error);
      toast.error("Failed to load sharing requests.");
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.startDate || !formData.endDate) {
      toast.error("Start date and end date are required.");
      return;
    }
    if (new Date(formData.endDate) < new Date(formData.startDate)) {
      toast.error("End date cannot be before start date.");
      return;
    }

    try {
      await api.post("/sharing-requests", {
        equipment: {
          id: formData.equipmentId,
        },
        startDate: formData.startDate,
        endDate: formData.endDate,
        purpose: formData.purpose,
      });

      toast.success("Inter-institution sharing request submitted successfully!");

      setFormData({
        equipmentId: "",
        startDate: "",
        endDate: "",
        purpose: "",
      });

      fetchRequests();
      setActiveTab("outgoing");
    } catch (error) {
      console.error(error);
      const errMsg = error.response?.data?.message || "Failed to submit sharing request.";
      if (errMsg.includes("overlaps") || errMsg.includes("collides") || errMsg.includes("reserved") || errMsg.includes("booked")) {
        setModalMessage(errMsg);
      } else {
        toast.error(errMsg);
      }
    }
  };

  const approveRequest = async (id) => {
    try {
      await api.put(`/sharing-requests/${id}/approve`);
      toast.success("Sharing request approved.");
      fetchRequests();
    } catch (error) {
      console.error(error);
      const errMsg = error.response?.data?.message || "Failed to approve request.";
      if (errMsg.includes("overlaps") || errMsg.includes("collides") || errMsg.includes("reserved") || errMsg.includes("booked")) {
        setModalMessage(errMsg);
      } else {
        toast.error(errMsg);
      }
    }
  };

  const rejectRequest = async (id) => {
    try {
      await api.put(`/sharing-requests/${id}/reject`);
      toast.success("Sharing request rejected.");
      fetchRequests();
    } catch (error) {
      console.error(error);
      toast.error(error.response?.data?.message || "Failed to reject request.");
    }
  };

  const deleteRequest = async (id) => {
    if (!window.confirm("Delete this sharing request?")) return;
    try {
      await api.delete(`/sharing-requests/${id}`);
      toast.success("Sharing request deleted.");
      setAllRequests((prev) => prev.filter((r) => r.id !== id));
      setIncomingRequests((prev) => prev.filter((r) => r.id !== id));
      setOutgoingRequests((prev) => prev.filter((r) => r.id !== id));
      fetchRequests();
    } catch (error) {
      console.error(error);
      toast.error(error.response?.data?.message || "Failed to delete request.");
    }
  };

  const currentRequestsList = isSystemAdmin
    ? allRequests
    : activeTab === "incoming"
    ? incomingRequests
    : outgoingRequests;

  const stats = {
    total: isSystemAdmin ? allRequests.length : incomingRequests.length + outgoingRequests.length,
    incoming: incomingRequests.length,
    outgoing: outgoingRequests.length,
    pending: currentRequestsList.filter((r) => r.status === "PENDING").length,
    active: currentRequestsList.filter((r) => r.status === "ACTIVE").length,
    approved: currentRequestsList.filter((r) => r.status === "APPROVED" || r.status === "ACTIVE").length,
  };

  return (
    <Layout>
      <div className="equipment-page">
        <div className="page-header">
          <div>
            <h1>
              <FaShareAlt />
              Inter-Institution Equipment Sharing
            </h1>
            <p>
              {isSystemAdmin
                ? "System Administrator Audit & Control Panel for cross-institution shared equipment requests."
                : "Collaborative resource sharing workflow across academic & research partner institutions."}
            </p>
          </div>
        </div>

        {/* Operational Stats Grid */}
        <div className="dashboard-container">
          <DashboardCard
            title="Total Requests"
            value={stats.total}
            icon={<FaShareAlt />}
          />

          {!isSystemAdmin && (
            <DashboardCard
              title="Incoming Requests"
              value={stats.incoming}
              icon={<FaInbox />}
            />
          )}

          {!isSystemAdmin && (
            <DashboardCard
              title="Outgoing Requests"
              value={stats.outgoing}
              icon={<FaPaperPlane />}
            />
          )}

          <DashboardCard
            title="Active Sharing"
            value={stats.active}
            icon={<FaClock />}
          />

          <DashboardCard
            title="Approved / Reserved"
            value={stats.approved}
            icon={<FaCalendarCheck />}
          />
        </div>

        {/* Create Inter-Institution Sharing Request Form */}
        {!isSystemAdmin && (
          <SharingRequestForm
            equipment={equipment}
            formData={formData}
            handleChange={handleChange}
            handleSubmit={handleSubmit}
            loadingEquipment={loadingEquipment}
          />
        )}

        {/* Tab Navigation for Incoming vs Outgoing Requests */}
        {!isSystemAdmin && (
          <div className="cert-filters" style={{ marginBottom: "16px" }}>
            <button
              className={`tab-btn ${activeTab === "incoming" ? "active" : ""}`}
              onClick={() => setActiveTab("incoming")}
            >
              <FaInbox style={{ marginRight: "6px" }} /> 📥 Incoming Requests (For My Equipment)
              <span className="filter-count">{incomingRequests.length}</span>
            </button>

            <button
              className={`tab-btn ${activeTab === "outgoing" ? "active" : ""}`}
              onClick={() => setActiveTab("outgoing")}
            >
              <FaPaperPlane style={{ marginRight: "6px" }} /> 📤 Outgoing Requests (My Requests)
              <span className="filter-count">{outgoingRequests.length}</span>
            </button>
          </div>
        )}

        <SharingRequestTable
          requests={currentRequestsList}
          approveRequest={approveRequest}
          rejectRequest={rejectRequest}
          deleteRequest={deleteRequest}
          type={isSystemAdmin ? "all" : activeTab}
        />

        {/* General Error / Overlap Pop-up Modal */}
        {modalMessage && (
          <div style={{
            position: "fixed",
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: "rgba(0, 0, 0, 0.55)",
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
              maxWidth: "500px",
              width: "90%",
              boxShadow: "0 20px 25px -5px rgba(0,0,0,0.15)",
              border: "1px solid #fee2e2"
            }}>
              <div style={{ display: "flex", alignItems: "center", gap: "12px", color: "#dc2626", marginBottom: "14px" }}>
                <FaExclamationCircle style={{ fontSize: "26px" }} />
                <h3 style={{ margin: 0, fontSize: "18px", fontWeight: 700 }}>Requested Dates Already Booked / Reserved</h3>
              </div>
              
              <p style={{ color: "var(--color-text-main)", fontSize: "14.5px", lineHeight: "1.5", margin: "0 0 16px 0" }}>
                {modalMessage}
              </p>

              <div style={{
                background: "#fef2f2",
                borderLeft: "4px solid #ef4444",
                padding: "12px 14px",
                borderRadius: "6px",
                fontSize: "13px",
                color: "#991b1b",
                marginBottom: "20px"
              }}>
                <strong>Recommendation:</strong> Select non-overlapping dates or inspect existing active reservations in the sharing schedule.
              </div>

              <div style={{ display: "flex", justifyContent: "flex-end" }}>
                <button
                  onClick={() => setModalMessage(null)}
                  style={{
                    background: "#dc2626",
                    color: "#ffffff",
                    border: "none",
                    padding: "10px 22px",
                    borderRadius: "8px",
                    fontWeight: 600,
                    fontSize: "14px",
                    cursor: "pointer"
                  }}
                >
                  OK, Close
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </Layout>
  );
}

export default SharingRequestPage;