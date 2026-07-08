import { useState } from "react";
import { jwtDecode } from "jwt-decode";
import "./Table.css";

function SharingRequestTable({
  requests,
  approveRequest,
  rejectRequest,
  deleteRequest,
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

  const isAdmin =
    userRole === "ROLE_SYSTEM_ADMIN" ||
    userRole === "ROLE_INSTITUTION_ADMIN" ||
    userRole === "ROLE_LAB_MANAGER";

  const filteredRequests = requests.filter((request) =>
    request.equipment?.name
      ?.toLowerCase()
      .includes(search.toLowerCase())
  );

  return (
    <div className="table-card">
      <div className="table-header">
        <h2>Sharing Requests</h2>

        <input
          type="text"
          className="table-search"
          placeholder="🔍 Search equipment..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Equipment</th>
            <th>Requester</th>
            <th>Purpose</th>
            <th>Request Date</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          {filteredRequests.length > 0 ? (
            filteredRequests.map((request) => (
              <tr key={request.id}>
                <td>{request.id}</td>
                <td>{request.equipment?.name}</td>
                <td>{request.requester?.name}</td>
                <td>{request.purpose}</td>
                <td>{request.requestDate}</td>

                <td>
                  <span
                    className={`status-badge ${
                      request.status === "APPROVED"
                        ? "status-approved"
                        : request.status === "REJECTED"
                        ? "status-rejected"
                        : "status-pending"
                    }`}
                  >
                    {request.status}
                  </span>
                </td>

                <td>
                  {isAdmin ? (
                    <>
                      {request.status === "PENDING" ? (
                        <>
                          <button
                            className="action-btn edit-btn"
                            onClick={() => approveRequest(request.id)}
                          >
                            ✅ Approve
                          </button>

                          <button
                            className="action-btn delete-btn"
                            onClick={() => rejectRequest(request.id)}
                          >
                            ❌ Reject
                          </button>
                        </>
                      ) : (
                        <button
                          className="action-btn complete-btn"
                          disabled
                        >
                          ✔ Processed
                        </button>
                      )}

                      <button
                        className="action-btn"
                        style={{
                          background: "#616161",
                          marginTop: "8px",
                        }}
                        onClick={() => deleteRequest(request.id)}
                      >
                        🗑 Delete
                      </button>
                    </>
                  ) : (
                    <span style={{ color: "#777", fontWeight: "bold" }}>
                      No Actions
                    </span>
                  )}
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="7" className="empty-table">
                🤝 No sharing requests found.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default SharingRequestTable;