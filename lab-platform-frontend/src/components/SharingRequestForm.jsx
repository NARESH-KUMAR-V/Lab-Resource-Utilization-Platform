import "./Form.css";

function SharingRequestForm({
  equipment,
  formData,
  handleChange,
  handleSubmit,
}) {
  return (
    <div className="form-card">

      <h3>Create Sharing Request</h3>

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
              placeholder="Purpose of requesting this equipment..."
              required
            />
          </div>

        </div>

        <button
          className="submit-btn"
          type="submit"
        >
          Submit Request
        </button>

      </form>

    </div>
  );
}

export default SharingRequestForm;