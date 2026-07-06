import "./Form.css";

function MaintenanceForm({
  equipmentList,
  formData,
  handleChange,
  handleSubmit,
}) {
  return (
    <div className="form-card">

      <h3>Add Maintenance Record</h3>

      <form onSubmit={handleSubmit}>

        <div className="form-grid">

          <div className="form-group">
            <label>Equipment</label>

            <select
              name="equipmentId"
              value={formData.equipmentId}
              onChange={handleChange}
              required
            >
              <option value="">Select Equipment</option>

              {equipmentList.map((item) => (
                <option
                  key={item.id}
                  value={item.id}
                >
                  {item.name}
                </option>
              ))}

            </select>
          </div>

          <div className="form-group">
            <label>Maintenance Date</label>

            <input
              type="date"
              name="maintenanceDate"
              value={formData.maintenanceDate}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Performed By</label>

            <input
              type="text"
              name="performedBy"
              value={formData.performedBy}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Status</label>

            <select
              name="status"
              value={formData.status}
              onChange={handleChange}
            >
              <option value="PENDING">PENDING</option>
              <option value="COMPLETED">COMPLETED</option>
            </select>
          </div>

          <div
            className="form-group"
            style={{ gridColumn: "1 / -1" }}
          >
            <label>Description</label>

            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              required
            />

          </div>

        </div>

        <button
          className="submit-btn"
          type="submit"
        >
          Save Maintenance
        </button>

      </form>

    </div>
  );
}

export default MaintenanceForm;