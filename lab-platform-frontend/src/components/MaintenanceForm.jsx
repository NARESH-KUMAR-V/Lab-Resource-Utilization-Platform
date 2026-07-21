import "./Form.css";

function MaintenanceForm({
  equipmentList,
  technicians,
  formData,
  handleChange,
  handleSubmit,
}) {
  return (
    <div className="form-card">

      <div className="form-header">
        <h2>Create Maintenance Request</h2>
        <p>
          Schedule maintenance, assign a technician, and keep laboratory
          equipment in good condition.
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
                <option key={item.id} value={item.id}>
                  {item.name}
                  {item.laboratory ? ` (${item.laboratory.name})` : ""}
                </option>
              ))}

            </select>
          </div>

          <div className="form-group">
            <label>Assign Technician</label>

            <select
              name="technicianId"
              value={formData.technicianId}
              onChange={handleChange}
            >
              <option value="">Select Technician</option>

              {technicians.map((tech) => (
                <option key={tech.id} value={tech.id}>
                  {tech.name}
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

          <div
            className="form-group"
            style={{ gridColumn: "1 / -1" }}
          >
            <label>Description</label>

            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              placeholder="Describe the maintenance work to be performed..."
              required
            />

          </div>

        </div>

        <button
          className="submit-btn"
          type="submit"
        >
          🔧 Create Maintenance Request
        </button>

      </form>

    </div>
  );
}

export default MaintenanceForm;