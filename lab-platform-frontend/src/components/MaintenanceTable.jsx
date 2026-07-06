import { useState } from "react";
import "./Table.css";

function MaintenanceTable({
  maintenanceRecords,
  completeMaintenance,
}) {

  const [search, setSearch] = useState("");

  const filteredRecords = maintenanceRecords.filter((record) =>
    record.equipment?.name
      ?.toLowerCase()
      .includes(search.toLowerCase())
  );

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

            <th>Date</th>

            <th>Performed By</th>

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

                  {record.status === "COMPLETED" ? (

                    <button
                      className="action-btn complete-btn"
                      disabled
                    >
                      ✔ Completed
                    </button>

                  ) : (

                    <button
                      className="action-btn edit-btn"
                      onClick={() => completeMaintenance(record.id)}
                    >
                      🔧 Complete
                    </button>

                  )}

                </td>

              </tr>

            ))

          ) : (

            <tr>

              <td
                colSpan="7"
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