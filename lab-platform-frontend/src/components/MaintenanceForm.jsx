import "./Form.css";

function MaintenanceForm({
  equipmentList,
  formData,
  handleChange,
  handleSubmit,
}) {
  return (
    <div className="form-card">

      <div className="form-header">
        <h2>Add Maintenance Record</h2>
        <p>
          Record maintenance activities and keep laboratory equipment in
          excellent condition.
        </p>
      </div>

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
              placeholder="Technician or Engineer Name"
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
              <option value="PENDING">Pending</option>
              <option value="COMPLETED">Completed</option>
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
              placeholder="Example: Replaced damaged power supply, cleaned cooling fan, calibrated microscope lenses..."
              required
            />

          </div>

        </div>

        <button
          className="submit-btn"
          type="submit"
        >
          🔧 Save Maintenance Record
        </button>

      </form>

    </div>
  );
}

export default MaintenanceForm;