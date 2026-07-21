import { useState } from "react";
import "./Table.css";

function MaintenanceTable({
  maintenanceRecords,
  startMaintenance,
  completeMaintenance,
  cancelMaintenance,
}) {

  const [search, setSearch] = useState("");

  const filteredRecords = maintenanceRecords.filter((record) =>
    record.equipment?.name
      ?.toLowerCase()
      .includes(search.toLowerCase())
  );

  const getStatusClass = (status) => {
    switch (status) {
      case "COMPLETED":
        return "status-approved";
      case "CANCELLED":
        return "status-rejected";
      case "IN_PROGRESS":
        return "status-processing";
      default:
        return "status-pending";
    }
  };

  return (

    <div className="table-card">

      <div className="table-header">

        <h2>Maintenance Records</h2>

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

            <th>Laboratory</th>

            <th>Technician</th>

            <th>Date</th>

            <th>Description</th>

            <th>Status</th>

            <th>Actions</th>

          </tr>

        </thead>

        <tbody>

          {filteredRecords.length > 0 ? (

            filteredRecords.map((record) => (

              <tr key={record.id}>

                <td>{record.id}</td>

                <td>{record.equipment?.name}</td>

                <td>{record.equipment?.laboratory?.name}</td>

                <td>{record.technician?.name || "-"}</td>

                <td>{record.maintenanceDate}</td>

                <td>{record.description}</td>

                <td>

                  <span
                    className={`status-badge ${getStatusClass(record.status)}`}
                  >
                    {record.status}
                  </span>

                </td>

                <td>

                  {record.status === "PENDING" && (
                    <>
                      <button
                        className="action-btn approve-btn"
                        onClick={() => startMaintenance(record.id)}
                      >
                        ▶ Start
                      </button>

                      <button
                        className="action-btn reject-btn"
                        onClick={() => cancelMaintenance(record.id)}
                      >
                        ✖ Cancel
                      </button>
                    </>
                  )}

                  {record.status === "IN_PROGRESS" && (
                    <button
                      className="action-btn edit-btn"
                      onClick={() => completeMaintenance(record.id)}
                    >
                      ✔ Complete
                    </button>
                  )}

                  {(record.status === "COMPLETED" ||
                    record.status === "CANCELLED") && (
                    <button
                      className="action-btn complete-btn"
                      disabled
                    >
                      Processed
                    </button>
                  )}

                </td>

              </tr>

            ))

          ) : (

            <tr>

              <td
                colSpan="8"
                className="empty-table"
              >
                🔧 No maintenance records found.
              </td>

            </tr>

          )}

        </tbody>

      </table>

    </div>

  );

}

export default MaintenanceTable;