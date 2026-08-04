import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import Layout from "../components/Layout";
import api from "../api/axios";
import "../components/Table.css";
import "../components/WaitingQueueModal.css";
import { FaUserCheck, FaEye, FaCheckCircle, FaTimesCircle } from "react-icons/fa";

function UserApprovalPage() {
  const [pendingUsers, setPendingUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedUser, setSelectedUser] = useState(null);

  useEffect(() => {
    loadPendingUsers();
  }, []);

  const loadPendingUsers = async () => {
    try {
      setLoading(true);
      const res = await api.get("/users/pending");
      setPendingUsers(res.data);
    } catch (err) {
      console.error(err);
      toast.error("Failed to load pending user requests.");
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (userId) => {
    try {
      await api.put(`/users/${userId}/approve`);
      toast.success("User registration approved successfully.");
      setPendingUsers((prev) => prev.filter((u) => u.id !== userId));
      if (selectedUser?.id === userId) {
        setSelectedUser(null);
      }
    } catch (err) {
      console.error(err);
      toast.error("Failed to approve user.");
    }
  };

  const handleReject = async (userId) => {
    try {
      await api.put(`/users/${userId}/reject`);
      toast.info("User registration request rejected.");
      setPendingUsers((prev) => prev.filter((u) => u.id !== userId));
      if (selectedUser?.id === userId) {
        setSelectedUser(null);
      }
    } catch (err) {
      console.error(err);
      toast.error("Failed to reject user.");
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return "-";
    return new Date(dateStr).toLocaleString();
  };

  return (
    <Layout>
      <div className="equipment-page">

        <div className="page-header">
          <div>
            <h1>
              <FaUserCheck />
              User Registration Approvals
            </h1>
            <p>
              Review pending user account registrations, check institution credentials, and grant access.
            </p>
          </div>
        </div>

        <div className="table-card">
          <div className="table-header">
            <h2>Pending Registration Requests</h2>
            <span className="status-badge status-pending">
              Total Pending: {pendingUsers.length}
            </span>
          </div>

          <div className="table-container">
            {loading ? (
              <p style={{ padding: "30px", textAlign: "center", color: "var(--color-text-subtle)" }}>
                ⏳ Loading pending user registration requests...
              </p>
            ) : (
              <table className="data-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Full Name</th>
                    <th>Email Address</th>
                    <th>Requested Role</th>
                    <th>Institution</th>
                    <th>Department</th>
                    <th>Laboratory</th>
                    <th>Registered On</th>
                    <th>Actions</th>
                  </tr>
                </thead>

                <tbody>
                  {pendingUsers.length > 0 ? (
                    pendingUsers.map((user) => (
                      <tr key={user.id}>
                        <td><strong>#{user.id}</strong></td>
                        <td><strong>{user.name}</strong></td>
                        <td>{user.email}</td>
                        <td>
                          <span className="status-badge status-approved">
                            {user.requestedRole || user.role}
                          </span>
                        </td>
                        <td>{user.institution ? user.institution.name : "-"}</td>
                        <td>{user.department || "-"}</td>
                        <td>{user.laboratory ? user.laboratory.name : "-"}</td>
                        <td>{formatDate(user.createdAt)}</td>
                        <td>
                          <div style={{ display: "inline-flex", gap: "6px" }}>
                            <button
                              className="action-btn edit-btn"
                              onClick={() => setSelectedUser(user)}
                            >
                              <FaEye /> View
                            </button>

                            <button
                              className="action-btn approve-btn"
                              onClick={() => handleApprove(user.id)}
                            >
                              <FaCheckCircle /> Approve
                            </button>

                            <button
                              className="action-btn reject-btn"
                              onClick={() => handleReject(user.id)}
                            >
                              <FaTimesCircle /> Reject
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="9" className="empty-table">
                        👤 No pending user registration requests found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            )}
          </div>
        </div>

        {/* User Details Modal */}
        {selectedUser && (
          <div className="waiting-modal-overlay">
            <div className="waiting-modal" style={{ maxWidth: "560px" }}>
              <div className="waiting-header">
                <h2>👤 Applicant Details</h2>
                <button
                  className="close-btn"
                  onClick={() => setSelectedUser(null)}
                >
                  ✖
                </button>
              </div>

              <div style={{ padding: "16px 0", display: "grid", gap: "12px", fontSize: "14px" }}>
                <div style={{ display: "flex", justifyContent: "space-between", borderBottom: "1px solid var(--color-border)", paddingBottom: "8px" }}>
                  <span style={{ color: "var(--color-text-subtle)" }}>Full Name:</span>
                  <strong>{selectedUser.name}</strong>
                </div>

                <div style={{ display: "flex", justifyContent: "space-between", borderBottom: "1px solid var(--color-border)", paddingBottom: "8px" }}>
                  <span style={{ color: "var(--color-text-subtle)" }}>Email Address:</span>
                  <strong>{selectedUser.email}</strong>
                </div>

                <div style={{ display: "flex", justifyContent: "space-between", borderBottom: "1px solid var(--color-border)", paddingBottom: "8px" }}>
                  <span style={{ color: "var(--color-text-subtle)" }}>Requested Role:</span>
                  <span className="status-badge status-approved">
                    {selectedUser.requestedRole || selectedUser.role}
                  </span>
                </div>

                <div style={{ display: "flex", justifyContent: "space-between", borderBottom: "1px solid var(--color-border)", paddingBottom: "8px" }}>
                  <span style={{ color: "var(--color-text-subtle)" }}>Department:</span>
                  <strong>{selectedUser.department || "-"}</strong>
                </div>

                <div style={{ display: "flex", justifyContent: "space-between", borderBottom: "1px solid var(--color-border)", paddingBottom: "8px" }}>
                  <span style={{ color: "var(--color-text-subtle)" }}>Institution:</span>
                  <strong>{selectedUser.institution ? selectedUser.institution.name : "-"}</strong>
                </div>

                <div style={{ display: "flex", justifyContent: "space-between", borderBottom: "1px solid var(--color-border)", paddingBottom: "8px" }}>
                  <span style={{ color: "var(--color-text-subtle)" }}>Laboratory:</span>
                  <strong>{selectedUser.laboratory ? selectedUser.laboratory.name : "-"}</strong>
                </div>

                <div style={{ display: "flex", justifyContent: "space-between", borderBottom: "1px solid var(--color-border)", paddingBottom: "8px" }}>
                  <span style={{ color: "var(--color-text-subtle)" }}>Registration Date:</span>
                  <strong>{formatDate(selectedUser.createdAt)}</strong>
                </div>

                <div style={{ display: "flex", justifyContent: "space-between" }}>
                  <span style={{ color: "var(--color-text-subtle)" }}>Status:</span>
                  <span className="status-badge status-pending">
                    {selectedUser.status}
                  </span>
                </div>
              </div>

              <div className="modal-footer" style={{ display: "flex", gap: "8px", justifyContent: "flex-end" }}>
                <button
                  className="action-btn approve-btn"
                  onClick={() => handleApprove(selectedUser.id)}
                >
                  <FaCheckCircle /> Approve User
                </button>
                <button
                  className="action-btn reject-btn"
                  onClick={() => handleReject(selectedUser.id)}
                >
                  <FaTimesCircle /> Reject Request
                </button>
                <button
                  className="close-modal-btn"
                  onClick={() => setSelectedUser(null)}
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

export default UserApprovalPage;
