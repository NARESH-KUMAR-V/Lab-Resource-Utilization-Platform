import "./Form.css";

function SharingRequestForm({
  equipment,
  formData,
  handleChange,
  handleSubmit,
}) {
  return (
    <div className="form-card">

      <div className="form-header">
        <h2>Create Sharing Request</h2>
        <p>
          Request shared laboratory equipment for collaborative research and academic activities.
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
              <option value="">Select Shared Equipment</option>

              {equipment
                .filter((item) => item.shared)
                .map((item) => (
                  <option
                    key={item.id}
                    value={item.id}
                  >
                    {item.name}
                  </option>
                ))}
            </select>
          </div>

          <div
            className="form-group"
            style={{ gridColumn: "1 / -1" }}
          >
            <label>Purpose</label>

            <textarea
              name="purpose"
              value={formData.purpose}
              onChange={handleChange}
              placeholder="Example: Cross-department AI research, Final year project, Laboratory collaboration..."
              required
            />

          </div>

        </div>

        <button
          className="submit-btn"
          type="submit"
        >
          🤝 Submit Sharing Request
        </button>

      </form>

    </div>
  );
}

export default SharingRequestForm;