import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import api from "../api/axios";
import MaintenanceForm from "../components/MaintenanceForm";
import MaintenanceTable from "../components/MaintenanceTable";

function MaintenancePage() {

  const [maintenanceRecords, setMaintenanceRecords] = useState([]);
  const [equipmentList, setEquipmentList] = useState([]);

  const [formData, setFormData] = useState({
    equipmentId: "",
    maintenanceDate: "",
    description: "",
    performedBy: "",
    status: "PENDING",
  });

  useEffect(() => {
    loadMaintenance();
    loadEquipment();
  }, []);

  const loadMaintenance = async () => {
    try {
      const response = await api.get("/maintenance");
      setMaintenanceRecords(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  const loadEquipment = async () => {
    try {
      const response = await api.get("/equipment");
      setEquipmentList(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {

    e.preventDefault();

    try {

      await api.post("/maintenance", {
        equipment: {
          id: formData.equipmentId,
        },
        maintenanceDate: formData.maintenanceDate,
        description: formData.description,
        performedBy: formData.performedBy,
        status: formData.status,
      });

      alert("Maintenance record created successfully!");

      setFormData({
        equipmentId: "",
        maintenanceDate: "",
        description: "",
        performedBy: "",
        status: "PENDING",
      });

      loadMaintenance();

    } catch (error) {

      console.error(error);

      alert("Failed to create maintenance record.");

    }

  };

  const completeMaintenance = async (id) => {

    try {

      await api.put(`/maintenance/${id}/complete`);

      loadMaintenance();

    } catch (error) {

      console.error(error);

    }

  };

  return (
    <>
      <Navbar />

      <div style={{ padding: "20px" }}>

        <h2
          style={{
            textAlign: "center",
            color: "#1976d2",
            marginBottom: "25px",
          }}
        >
          Maintenance Management
        </h2>

        <MaintenanceForm
          equipmentList={equipmentList}
          formData={formData}
          handleChange={handleChange}
          handleSubmit={handleSubmit}
        />

        <MaintenanceTable
          maintenanceRecords={maintenanceRecords}
          completeMaintenance={completeMaintenance}
        />

      </div>
    </>
  );
}

export default MaintenancePage;