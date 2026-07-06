import "./Form.css";

function EquipmentForm({
  formData,
  handleChange,
  handleSubmit,
  editId,
}) {
  return (
    <div className="form-card">

      <h3>
        {editId ? "Update Equipment" : "Add Equipment"}
      </h3>

      <form onSubmit={handleSubmit}>

        <div className="form-grid">

          <div className="form-group">
            <label>Equipment Name</label>
            <input
              type="text"
              name="name"
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
              <option value="AVAILABLE">AVAILABLE</option>
              <option value="BOOKED">BOOKED</option>
              <option value="UNDER_MAINTENANCE">UNDER MAINTENANCE</option>
              <option value="OUT_OF_SERVICE">OUT OF SERVICE</option>
              <option value="RETIRED">RETIRED</option>
            </select>
          </div>

          <div className="form-group">
            <label>Specifications</label>
            <textarea
              name="specifications"
              value={formData.specifications}
              onChange={handleChange}
              required
            />
          </div>

        </div>

        <button
          className="submit-btn"
          type="submit"
        >
          {editId ? "Update Equipment" : "Add Equipment"}
        </button>

      </form>

    </div>
  );
}

export default EquipmentForm;