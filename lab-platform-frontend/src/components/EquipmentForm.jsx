import { useEffect, useState } from "react";
import api from "../api/axios";
import "./Form.css";

function EquipmentForm({
  formData,
  handleChange,
  handleSubmit,
  editId,
}) {

  const [laboratories, setLaboratories] = useState([]);

  useEffect(() => {
    loadLaboratories();
  }, []);

  const loadLaboratories = async () => {
    try {
      const response = await api.get("/laboratories");
      setLaboratories(response.data);
    } catch (error) {
      console.error(error);
    }
  };

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
            <label>Cost Per Day (₹)</label>
            <input
              type="number"
              name="costPerDay"
              placeholder="Enter daily utilization cost"
              value={formData.costPerDay}
              onChange={handleChange}
              min="0"
              step="0.01"
              required
            />
          </div>

          <div className="form-group">
            <label>Laboratory</label>

            <select
              name="laboratory"
              value={formData.laboratory?.id || ""}
              onChange={(e) =>
                handleChange({
                  target: {
                    name: "laboratory",
                    value: {
                      id: Number(e.target.value),
                    },
                  },
                })
              }
              required
            >
              <option value="">Select Laboratory</option>

              {laboratories.map((lab) => (
                <option key={lab.id} value={lab.id}>
                  {lab.name}
                </option>
              ))}
            </select>
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

          <div className="form-group full-width">
            <label>Description</label>

            <textarea
              rows="4"
              name="description"
              placeholder="Enter equipment description..."
              value={formData.description || ""}
              onChange={handleChange}
            />
          </div>

          <div className="form-group full-width">
            <label>Equipment Image</label>

            <input
              type="file"
              name="image"
              accept="image/*"
              onChange={handleChange}
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