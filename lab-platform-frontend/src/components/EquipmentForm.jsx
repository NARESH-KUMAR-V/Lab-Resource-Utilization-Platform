import "./Form.css";

function EquipmentForm({
  formData,
  handleChange,
  handleSubmit,
  editId,
}) {
  return (
    <div className="form-card">

      <div className="form-header">
        <div>
          <h2>{editId ? "Edit Equipment" : "Add New Equipment"}</h2>
        </div>
      </div>

      <form onSubmit={handleSubmit}>

        <div className="form-grid">

          <div className="form-group">
            <label>Equipment Name</label>
            <input
              type="text"
              name="name"
              placeholder="Enter equipment name"
              value={formData.name}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Category</label>
            <input
              type="text"
              name="category"
              placeholder="Enter category"
              value={formData.category}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Department</label>
            <input
              type="text"
              name="department"
              placeholder="Enter department name"
              value={formData.department}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Institution</label>
            <input
              type="text"
              name="institution"
              placeholder="Enter institution name"
              value={formData.institution}
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
              <option value="AVAILABLE">Available</option>
              <option value="BOOKED">Booked</option>
              <option value="UNDER_MAINTENANCE">
                Under Maintenance
              </option>
              <option value="OUT_OF_SERVICE">
                Out Of Service
              </option>
              <option value="RETIRED">
                Retired
              </option>
            </select>
          </div>

          <div className="form-group full-width">
            <label>Specifications</label>

            <textarea
              rows="5"
              name="specifications"
              placeholder="Enter equipment specifications..."
              value={formData.specifications}
              onChange={handleChange}
              required
            />
          </div>

        </div>

        <button className="submit-btn" type="submit">
          {editId ? "Update Equipment" : "Add Equipment"}
        </button>

      </form>

    </div>
  );
}

export default EquipmentForm;