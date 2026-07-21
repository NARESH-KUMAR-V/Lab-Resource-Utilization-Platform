import "./Table.css";
import { useAuth } from "../context/AuthContext";
import noImage from "../assets/no-image.png";

function EquipmentTable({
  equipment,
  handleEdit,
  handleDelete,
}) {

  const { role } = useAuth();

  const canEdit =
    role === "LAB_MANAGER" ||
    role === "INSTITUTION_ADMIN" ||
    role === "SYSTEM_ADMIN";

  const canDelete =
    role === "INSTITUTION_ADMIN" ||
    role === "SYSTEM_ADMIN";

  return (
    <div className="table-card">

      <div className="table-header">
        <h2>Equipment Inventory</h2>
      </div>

      <table className="data-table">

        <thead>
          <tr>
            <th>ID</th>
            <th>Image</th>
            <th>Name</th>
            <th>Category</th>
            <th>Cost/Day</th>
            <th>Description</th>
            <th>Status</th>
            <th>Laboratory</th>
            <th>Institution</th>

            {(canEdit || canDelete) && (
              <th>Actions</th>
            )}
          </tr>
        </thead>

        <tbody>

          {equipment.length === 0 ? (

            <tr>
              <td
                colSpan={canEdit || canDelete ? 10 : 9}
                className="empty-table"
              >
                No equipment found.
              </td>
            </tr>

          ) : (

            equipment.map((item) => (

              <tr key={item.id}>

                <td>{item.id}</td>

                <td style={{ textAlign: "center" }}>
                  <img
                    src={
                      item.imageUrl
                        ? `http://localhost:8080${item.imageUrl}`
                        : noImage
                    }
                    alt={item.name}
                    style={{
                      width: "80px",
                      height: "80px",
                      objectFit: "cover",
                      borderRadius: "10px",
                      border: "1px solid #ddd",
                      transition: "0.3s",
                      cursor: "pointer"
                    }}
                    onMouseOver={(e) => {
                      e.target.style.transform = "scale(1.1)";
                    }}
                    onMouseOut={(e) => {
                      e.target.style.transform = "scale(1)";
                    }}
                  />
                </td>

                <td>{item.name}</td>

                <td>{item.category}</td>

                <td>
                  ₹{item.costPerDay?.toLocaleString() || 0}
                </td>

                <td title={item.description || ""}>
                  {item.description
                    ? item.description.length > 50
                      ? item.description.substring(0, 50) + "..."
                      : item.description
                    : "-"}
                </td>

                <td>
                  <span
                    className={`status-badge ${item.status.toLowerCase()}`}
                  >
                    {item.status.replaceAll("_", " ")}
                  </span>
                </td>

                <td>{item.laboratory?.name || "-"}</td>

                <td>{item.laboratory?.institution?.name || "-"}</td>

                {(canEdit || canDelete) && (

                  <td>

                    {canEdit && (
                      <button
                        className="action-btn edit-btn"
                        onClick={() => handleEdit(item)}
                      >
                        ✏ Edit
                      </button>
                    )}

                    {canDelete && (
                      <button
                        className="action-btn delete-btn"
                        onClick={() => handleDelete(item.id)}
                      >
                        🗑 Delete
                      </button>
                    )}

                  </td>

                )}

              </tr>

            ))

          )}

        </tbody>

      </table>

    </div>
  );
}

export default EquipmentTable;