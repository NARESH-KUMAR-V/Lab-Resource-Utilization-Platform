import "./Form.css";

function MaintenanceForm({
  equipmentList,
  technicians,
  formData,
  handleChange,
  handleSubmit,
}) {

  const selectedEquipment = equipmentList.find(
    (item) => String(item.id) === String(formData.equipmentId)
  );

  const filteredTechnicians = selectedEquipment
    ? technicians.filter((tech) => {
        const eqLabId = selectedEquipment.laboratory?.id;
        const eqInstId =
          selectedEquipment.laboratory?.institution?.id ||
          selectedEquipment.institution?.id;

        const techLabId = tech.laboratory?.id;
        const techInstId = tech.institution?.id;

        if (eqLabId && techLabId && String(eqLabId) === String(techLabId)) return true;
        if (eqInstId && techInstId && String(eqInstId) === String(techInstId)) return true;

        return false;
      })
    : technicians;

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
            <label>Maintenance Type</label>

            <select
              name="maintenanceType"
              value={formData.maintenanceType}
              onChange={handleChange}
              required
            >
              <option value="PREVENTIVE">🛡️ Preventive</option>
              <option value="CORRECTIVE">🔧 Corrective</option>
              <option value="EMERGENCY">🚨 Emergency</option>
              <option value="CALIBRATION">📐 Calibration</option>
              <option value="SOFTWARE_UPDATE">💻 Software Update</option>
              <option value="HARDWARE_INSPECTION">🔍 Hardware Inspection</option>
            </select>
          </div>

          <div className="form-group">
            <label>Assign Technician</label>

            <select
              name="technicianId"
              value={formData.technicianId}
              onChange={handleChange}
            >
              <option value="">
                {selectedEquipment
                  ? `Select Technician (${selectedEquipment.laboratory?.name || "Institution"})`
                  : "Select Equipment First"}
              </option>

              {filteredTechnicians.map((tech) => (
                <option key={tech.id} value={tech.id}>
                  {tech.name}
                  {tech.laboratory
                    ? ` (${tech.laboratory.name})`
                    : tech.institution
                    ? ` (${tech.institution.name})`
                    : ""}
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
            <label>Estimated Cost (₹)</label>

            <input
              type="number"
              name="maintenanceCost"
              value={formData.maintenanceCost}
              onChange={handleChange}
              placeholder="0.00"
              min="0"
              step="0.01"
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