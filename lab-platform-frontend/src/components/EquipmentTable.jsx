import "./Table.css";
import { useAuth } from "../context/AuthContext";

function EquipmentTable({
  equipment,
  handleEdit,
  handleDelete,
}) {

  const { role } = useAuth();

  return (
    <div className="table-card">

      <div className="table-header">
        <h2>Equipment Inventory</h2>
      </div>

      <table className="data-table">

        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Category</th>
            <th>Status</th>
            <th>Department</th>
            <th>Institution</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>

          {equipment.length === 0 ? (
            <tr>
              <td colSpan="7" className="empty-table">
                No equipment found.
              </td>
            </tr>
          ) : (
            equipment.map((item) => (
              <tr key={item.id}>

                <td>{item.id}</td>

                <td>{item.name}</td>

                <td>{item.category}</td>

                <td>
                  <span
                    className={`status-badge ${item.status.toLowerCase()}`}
                  >
                    {item.status.replaceAll("_", " ")}
                  </span>
                </td>

                <td>{item.department}</td>

                <td>{item.institution}</td>

                <td>

                  {/* Edit Button */}
                  {(role === "LAB_MANAGER" ||
                    role === "INSTITUTION_ADMIN" ||
                    role === "SYSTEM_ADMIN") && (
                    <button
                      className="action-btn edit-btn"
                      onClick={() => handleEdit(item)}
                    >
                      ✏ Edit
                    </button>
                  )}

                  {/* Delete Button */}
                  {(role === "INSTITUTION_ADMIN" ||
                    role === "SYSTEM_ADMIN") && (
                    <button
                      className="action-btn delete-btn"
                      onClick={() => handleDelete(item.id)}
                    >
                      🗑 Delete
                    </button>
                  )}

                </td>

              </tr>
            ))
          )}

        </tbody>

      </table>

    </div>
  );
}

export default EquipmentTable;