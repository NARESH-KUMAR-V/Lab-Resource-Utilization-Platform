import { FaEdit, FaTrash } from "react-icons/fa";
import "./Table.css";

function InstitutionTable({
  institutions,
  handleEdit,
  handleDelete
}) {

  return (
    <div className="table-card">

      <div className="table-header">

        <h2>Institution List</h2>

        <span>
          Total : {institutions.length}
        </span>

      </div>

      <div className="table-container">

        <table className="data-table">

          <thead>

            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Address</th>
              <th>Email</th>
              <th>Phone</th>
              <th style={{ textAlign: "center" }}>Actions</th>
            </tr>

          </thead>

          <tbody>

            {institutions.length > 0 ? (

              institutions.map((institution) => (

                <tr key={institution.id}>

                  <td>{institution.id}</td>

                  <td>{institution.name}</td>

                  <td>{institution.address}</td>

                  <td>{institution.email}</td>

                  <td>{institution.phone}</td>

                  <td className="action-column">

                    <button
                      className="action-btn edit-btn"
                      onClick={() => handleEdit(institution)}
                    >
                      <FaEdit />
                    </button>

                    <button
                      className="action-btn delete-btn"
                      onClick={() => handleDelete(institution.id)}
                    >
                      <FaTrash />
                    </button>

                  </td>

                </tr>

              ))

            ) : (

              <tr>

                <td
                  colSpan="6"
                  className="no-data"
                >
                  No institutions found.
                </td>

              </tr>

            )}

          </tbody>

        </table>

      </div>

    </div>
  );

}

export default InstitutionTable;