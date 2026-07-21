import { FaSave } from "react-icons/fa";
import "./Form.css";

function InstitutionForm({
  formData,
  handleChange,
  handleSubmit,
  editId
}) {

  return (

    <div className="form-card">

      <h2>

        {editId ? "Edit Institution" : "Add Institution"}

      </h2>

      <form
        className="modern-form"
        onSubmit={handleSubmit}
      >

        <div className="form-group">

          <label>Institution Name</label>

          <input
            type="text"
            name="name"
            placeholder="Enter institution name"
            value={formData.name}
            onChange={handleChange}
            required
          />

        </div>

        <div className="form-group">

          <label>Address</label>

          <textarea
            name="address"
            placeholder="Enter institution address"
            value={formData.address}
            onChange={handleChange}
            rows="3"
          />

        </div>

        <div className="form-group">

          <label>Email</label>

          <input
            type="email"
            name="email"
            placeholder="Enter institution email"
            value={formData.email}
            onChange={handleChange}
          />

        </div>

        <div className="form-group">

          <label>Phone</label>

          <input
            type="text"
            name="phone"
            placeholder="Enter phone number"
            value={formData.phone}
            onChange={handleChange}
          />

        </div>

        <button
          type="submit"
          className="submit-btn"
        >

          <FaSave />

          {editId ? "Update Institution" : "Save Institution"}

        </button>

      </form>

    </div>

  );

}

export default InstitutionForm;