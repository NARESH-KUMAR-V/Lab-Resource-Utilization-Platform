import { useState } from "react";
import { jwtDecode } from "jwt-decode";
import { FaSearch, FaCheckCircle, FaTimesCircle, FaTrash, FaInbox, FaPaperPlane, FaCoins } from "react-icons/fa";
import "./Table.css";

function SharingRequestTable({
  requests,
  approveRequest,
  rejectRequest,
  deleteRequest,
  type = "all",
}) {
  const [search, setSearch] = useState("");

  const token = localStorage.getItem("token");
  let userRole = "";

  if (token) {
    try {
      const decoded = jwtDecode(token);
      userRole = decoded.role || "";
    } catch (error) {
      console.error(error);
    }
  }

  const isSystemAdmin = userRole === "ROLE_SYSTEM_ADMIN";
  const canManage =
    isSystemAdmin ||
    userRole === "ROLE_INSTITUTION_ADMIN" ||
    userRole === "ROLE_LAB_MANAGER";

  const filteredRequests = (requests || []).filter((request) =>
    request.equipment?.name?.toLowerCase().includes(search.toLowerCase()) ||
    request.requestingInstitution?.toLowerCase().includes(search.toLowerCase()) ||
    request.requester?.name?.toLowerCase().includes(search.toLowerCase())
  );

  const getStatusBadgeClass = (status) => {
    switch (status) {
      case "ACTIVE":
        return "status-approved";
      case "APPROVED":
        return "status-pending";
      case "COMPLETED":
        return "status-secondary";
      case "REJECTED":
        return "status-rejected";
      default:
        return "status-pending";
    }
  };

  return (
    <div className="table-card">
      <div className="table-header">
        <h2 style={{ display: "flex", alignItems: "center", gap: "8px" }}>
          {type === "incoming" ? (
            <>
              <FaInbox style={{ color: "var(--color-primary)" }} /> Incoming Sharing Requests (For My Equipment)
            </>
          ) : type === "outgoing" ? (
            <>
              <FaPaperPlane style={{ color: "var(--color-secondary)" }} /> Outgoing Sharing Requests (My Institution Requests)
            </>
          ) : (
            "Sharing Request Records"
          )}
        </h2>

        <div className="search-wrapper">
          <FaSearch />
          <input
            type="text"
            placeholder="Search requests..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
      </div>

      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Equipment Asset</th>
            <th>Owning Institution</th>
            <th>Requesting Institution</th>
            <th>Requester</th>
            <th>Purpose</th>
            <th>Sharing Period</th>
            <th>Estimated Cost</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          {filteredRequests.length > 0 ? (
            filteredRequests.map((request) => {
              const owningInstName = request.equipment?.laboratory?.institution?.name || "Owning Institution";
              const isIncoming = type === "incoming";
              const canApproveThis = (isIncoming || isSystemAdmin) && canManage;

              const totalAmount = request.totalAmount || 0;
              const baseCost = request.estimatedCost || 0;
              const fee = request.interInstitutionFee || 0;

              return (
                <tr key={request.id}>
                  <td><strong>#{request.id}</strong></td>
                  <td>
                    <strong>{request.equipment?.name || "Equipment"}</strong>
                    <div style={{ fontSize: "11.5px", color: "var(--color-text-subtle)" }}>
                      {request.equipment?.laboratory?.name || "Lab"}
                    </div>
                  </td>
                  <td><span className="inst-badge">{owningInstName}</span></td>
                  <td><span className="inst-badge">{request.requestingInstitution || "External"}</span></td>
                  <td>{request.requester?.name || "-"}</td>
                  <td title={request.purpose}>{request.purpose || "-"}</td>
                  <td>
                    <div style={{ fontWeight: 600, fontSize: "12.5px" }}>
                      {request.startDate || "-"} to {request.endDate || "-"}
                    </div>
                  </td>

                  <td>
                    <div style={{ fontWeight: 700, color: "var(--color-text-main)", fontSize: "13.5px" }}>
                      ₹{totalAmount.toLocaleString()}
                    </div>
                    {fee > 0 && (
                      <div style={{ fontSize: "11px", color: "var(--color-text-subtle)" }} title={`Base: ₹${baseCost} + Fee (10%): ₹${fee}`}>
                        Base ₹{baseCost.toLocaleString()} + Fee ₹{fee.toLocaleString()}
                      </div>
                    )}
                  </td>

                  <td>
                    <span className={`status-badge ${getStatusBadgeClass(request.status)}`}>
                      {request.status === "ACTIVE" ? "🤝 ACTIVE" : request.status}
                    </span>
                  </td>

                  <td>
                    {canApproveThis ? (
                      <div style={{ display: "inline-flex", gap: "6px" }}>
                        {request.status === "PENDING" ? (
                          <>
                            <button
                              className="action-btn approve-btn"
                              onClick={() => approveRequest(request.id)}
                            >
                              <FaCheckCircle /> Approve
                            </button>

                            <button
                              className="action-btn reject-btn"
                              onClick={() => rejectRequest(request.id)}
                            >
                              <FaTimesCircle /> Reject
                            </button>
                          </>
                        ) : (
                          <button
                            className="action-btn secondary"
                            disabled
                          >
                            Processed
                          </button>
                        )}

                        {isSystemAdmin && (
                          <button
                            className="action-btn delete-btn"
                            onClick={() => deleteRequest(request.id)}
                          >
                            <FaTrash /> Delete
                          </button>
                        )}
                      </div>
                    ) : type === "outgoing" ? (
                      <span style={{ color: "var(--color-text-subtle)", fontSize: "12px", fontStyle: "italic" }}>
                        {request.status === "PENDING" ? "Awaiting Partner Approval" : "Processed"}
                      </span>
                    ) : (
                      <span style={{ color: "var(--color-text-subtle)", fontSize: "12.5px" }}>
                        View Only
                      </span>
                    )}
                  </td>
                </tr>
              );
            })
          ) : (
            <tr>
              <td colSpan="10" className="empty-table">
                🤝 No {type !== "all" ? type : ""} sharing requests found.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default SharingRequestTable;