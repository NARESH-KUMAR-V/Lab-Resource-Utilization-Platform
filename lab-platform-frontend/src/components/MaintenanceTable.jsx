import "./Table.css";

function MaintenanceTable({
  maintenanceRecords,
  completeMaintenance,
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
        Maintenance Records
      </h2>

      <table className="data-table">

        <thead>

          <tr>

            <th>ID</th>

            <th>Equipment</th>

            <th>Maintenance Date</th>

            <th>Performed By</th>

            <th>Description</th>

            <th>Status</th>

            <th>Actions</th>

          </tr>

        </thead>

        <tbody>

          {maintenanceRecords.length > 0 ? (

            maintenanceRecords.map((record) => (

              <tr key={record.id}>

                <td>{record.id}</td>

                <td>{record.equipment?.name}</td>

                <td>{record.maintenanceDate}</td>

                <td>{record.performedBy}</td>

                <td>{record.description}</td>

                <td>

                  <span
                    className={`status-badge ${
                      record.status === "COMPLETED"
                        ? "status-approved"
                        : "status-pending"
                    }`}
                  >
                    {record.status}
                  </span>

                </td>

                <td>

                  <button
                    className="action-btn edit-btn"
                    onClick={() => completeMaintenance(record.id)}
                    disabled={record.status === "COMPLETED"}
                  >
                    {record.status === "COMPLETED"
                      ? "Completed"
                      : "Complete"}
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
                No maintenance records found.
              </td>

            </tr>

          )}

        </tbody>

      </table>

    </>
  );
}

export default MaintenanceTable;