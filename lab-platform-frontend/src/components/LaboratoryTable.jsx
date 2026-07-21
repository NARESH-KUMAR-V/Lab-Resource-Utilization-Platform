import { FaEdit, FaTrash } from "react-icons/fa";
import "./Table.css";

function LaboratoryTable({
  laboratories,
  handleEdit,
  handleDelete,
}) {

  const role = localStorage.getItem("role");

  const canDelete = role === "SYSTEM_ADMIN";

  const canEdit =
    role === "SYSTEM_ADMIN" ||
    role === "INSTITUTION_ADMIN";

  return (

    <div className="table-card">

      <table className="data-table">

        <thead>

          <tr>

            <th>Name</th>
            <th>Department</th>
            <th>Location</th>
            <th>Department Head</th>
            <th>Institution</th>

            {canEdit && (
              <th style={{ textAlign: "center" }}>
                Actions
              </th>
            )}

          </tr>

        </thead>

        <tbody>

          {laboratories.length === 0 ? (

            <tr>

              <td
                colSpan={canEdit ? 6 : 5}
                className="empty-table"
              >
                No laboratories found.
              </td>

            </tr>

          ) : (

            laboratories.map((lab) => (

              <tr key={lab.id}>

                <td>{lab.name}</td>

                <td>{lab.department}</td>

                <td>{lab.location}</td>

                <td>{lab.hodName}</td>

                <td>{lab.institution?.name || "-"}</td>

                {canEdit && (

                  <td
                    style={{
                      display: "flex",
                      justifyContent: "center",
                      gap: "10px",
                    }}
                  >

                    <button
                      className="action-btn edit-btn"
                      onClick={() => handleEdit(lab)}
                    >
                      <FaEdit />
                    </button>

                    {canDelete && (

                      <button
                        className="action-btn delete-btn"
                        onClick={() => handleDelete(lab.id)}
                      >
                        <FaTrash />
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

export default LaboratoryTable;