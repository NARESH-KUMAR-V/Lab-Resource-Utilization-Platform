import "./Table.css";

function EquipmentTable({
  equipment,
  handleEdit,
  handleDelete,
}) {
  return (
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
        {equipment.map((item) => (
          <tr key={item.id}>
            <td>{item.id}</td>
            <td>{item.name}</td>
            <td>{item.category}</td>
            <td>{item.status}</td>
            <td>{item.department}</td>
            <td>{item.institution}</td>

            <td>
              <button
                className="action-btn edit-btn"
                onClick={() => handleEdit(item)}
              >
                Edit
              </button>

              <button
                className="action-btn delete-btn"
                onClick={() => handleDelete(item.id)}
              >
                Delete
              </button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export default EquipmentTable;