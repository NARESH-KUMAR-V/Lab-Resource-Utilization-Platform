import { useEffect, useState } from "react";
import { FaFlask } from "react-icons/fa";
import api from "../api/axios";
import "./Form.css";

function LaboratoryForm({
  formData,
  handleChange,
  handleSubmit,
  editId,
  role,
}) {

  const [institutions, setInstitutions] = useState([]);

  useEffect(() => {

    if (role === "SYSTEM_ADMIN") {

      api
        .get("/institutions")
        .then((response) => {
          setInstitutions(response.data);
        })
        .catch((error) => {
          console.error("Failed to load institutions:", error);
        });

    }

  }, [role]);

  return (

    <div className="form-card">

      <div className="form-header">

        <div>

          <h2>

            <FaFlask
              style={{
                marginRight: "10px",
                color: "#6c63ff",
              }}
            />

            {editId ? "Edit Laboratory" : "Add Laboratory"}

          </h2>

          <p>
            Create and manage laboratories under institutions.
          </p>

        </div>

      </div>

      <form onSubmit={handleSubmit}>

        <div className="form-grid">

          {role === "SYSTEM_ADMIN" && (

            <div className="form-group">

              <label>Institution</label>

              <select
                name="institutionId"
                value={formData.institution?.id || ""}
                onChange={handleChange}
                required
              >

                <option value="">
                  Select Institution
                </option>

                {institutions.map((item) => (

                  <option
                    key={item.id}
                    value={item.id}
                  >
                    {item.name}
                  </option>

                ))}

              </select>

            </div>

          )}

          <div className="form-group">

            <label>Laboratory Name</label>

            <input
              type="text"
              name="name"
              value={formData.name}
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

            <label>Location</label>

            <input
              type="text"
              name="location"
              value={formData.location}
              onChange={handleChange}
              required
            />

          </div>

          <div className="form-group">

            <label>Department Head</label>

            <input
              type="text"
              name="hodName"
              value={formData.hodName}
              onChange={handleChange}
              required
            />

          </div>

        </div>

        <button
          className="submit-btn"
          type="submit"
        >
          {editId ? "Update Laboratory" : "Add Laboratory"}
        </button>

      </form>

    </div>

  );

}

export default LaboratoryForm;