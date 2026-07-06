import "./Table.css";

function SharingRequestTable({
  requests,
  approveRequest,
  rejectRequest,
  deleteRequest,
}) {
  return (
    <>
      <h2
        style={{
          marginTop: "40px",
          marginBottom: "20px",
          color: "#1976d2",
          textAlign: "center",
        }}
      >
        Sharing Requests
      </h2>

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

          {requests.length > 0 ? (

            requests.map((request) => (

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

                  {request.status === "PENDING" && (
                    <>
                      <button
                        className="action-btn edit-btn"
                        onClick={() => approveRequest(request.id)}
                      >
                        Approve
                      </button>

                      <button
                        className="action-btn delete-btn"
                        onClick={() => rejectRequest(request.id)}
                      >
                        Reject
                      </button>
                    </>
                  )}

                  <button
                    className="action-btn"
                    style={{ background: "#616161" }}
                    onClick={() => deleteRequest(request.id)}
                  >
                    Delete
                  </button>

                </td>

              </tr>

            ))

          ) : (

            <tr>

              <td
                colSpan="7"
                style={{
                  textAlign: "center",
                  padding: "20px",
                }}
              >
                No sharing requests found.
              </td>

            </tr>

          )}

        </tbody>

      </table>
    </>
  );
}

export default SharingRequestTable;