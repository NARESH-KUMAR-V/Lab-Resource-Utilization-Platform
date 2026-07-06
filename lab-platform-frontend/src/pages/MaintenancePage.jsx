import { useEffect, useState } from "react";
import { toast } from "react-toastify";
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

      toast.error("Failed to load maintenance records.");

    }

  };

  const loadEquipment = async () => {

    try {

      const response = await api.get("/equipment");

      setEquipmentList(response.data);

    } catch (error) {

      console.error(error);

      toast.error("Failed to load equipment.");

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

      toast.success("Maintenance record created successfully!");

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

      toast.error("Failed to create maintenance record.");

    }

  };

  const completeMaintenance = async (id) => {

    try {

      await api.put(`/maintenance/${id}/complete`);

      toast.success("Maintenance marked as completed.");

      loadMaintenance();

    } catch (error) {

      console.error(error);

      toast.error("Failed to complete maintenance.");

    }

  };

  const stats = {

    total: maintenanceRecords.length,

    pending: maintenanceRecords.filter(
      (m) => m.status === "PENDING"
    ).length,

    completed: maintenanceRecords.filter(
      (m) => m.status === "COMPLETED"
    ).length,

  };

  return (

    <>
      <Navbar />

      <div className="dashboard">

        <h1>Maintenance Management</h1>

        <p>
          Track, schedule and monitor equipment maintenance records.
        </p>

        <div className="dashboard-container">

          <div className="dashboard-card">
            <div className="card-content">
              <h4>Total Records</h4>
              <h2>{stats.total}</h2>
            </div>
          </div>

          <div className="dashboard-card">
            <div className="card-content">
              <h4>Pending</h4>
              <h2>{stats.pending}</h2>
            </div>
          </div>

          <div className="dashboard-card">
            <div className="card-content">
              <h4>Completed</h4>
              <h2>{stats.completed}</h2>
            </div>
          </div>

        </div>

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