import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import Navbar from "../components/Navbar";
import api from "../api/axios";
import MaintenanceForm from "../components/MaintenanceForm";
import MaintenanceTable from "../components/MaintenanceTable";

function MaintenancePage() {

  const [maintenanceRecords, setMaintenanceRecords] = useState([]);
  const [equipmentList, setEquipmentList] = useState([]);
  const [technicians, setTechnicians] = useState([]);

  const [formData, setFormData] = useState({
    equipmentId: "",
    technicianId: "",
    maintenanceDate: "",
    description: ""
  });

  useEffect(() => {
    loadMaintenance();
    loadEquipment();
    loadTechnicians();
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
    }
  };

  const loadTechnicians = async () => {
    try {
      const response = await api.get("/users");

      const techs = response.data.filter(
        user => user.role === "LAB_TECHNICIAN"
      );

      setTechnicians(techs);

    } catch (error) {
      console.error(error);
    }
  };

  const handleChange = e => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async e => {

    e.preventDefault();

    try {

      await api.post("/maintenance", {

        equipment: {
          id: formData.equipmentId
        },

        technician: formData.technicianId
          ? { id: formData.technicianId }
          : null,

        maintenanceDate: formData.maintenanceDate,

        description: formData.description

      });

      toast.success("Maintenance request created.");

      setFormData({
        equipmentId: "",
        technicianId: "",
        maintenanceDate: "",
        description: ""
      });

      loadMaintenance();

    } catch (error) {

      console.error(error);

      toast.error("Failed to create maintenance.");

    }

  };

  const startMaintenance = async id => {

    try {

      await api.put(`/maintenance/${id}/start`);

      toast.success("Maintenance started.");

      loadMaintenance();

    } catch (error) {

      console.error(error);

      toast.error("Failed to start maintenance.");

    }

  };

  const completeMaintenance = async id => {

    try {

      await api.put(`/maintenance/${id}/complete`);

      toast.success("Maintenance completed.");

      loadMaintenance();

    } catch (error) {

      console.error(error);

      toast.error("Failed to complete maintenance.");

    }

  };

  const cancelMaintenance = async id => {

    try {

      await api.put(`/maintenance/${id}/cancel`);

      toast.success("Maintenance cancelled.");

      loadMaintenance();

    } catch (error) {

      console.error(error);

      toast.error("Failed to cancel maintenance.");

    }

  };

  const stats = {

    total: maintenanceRecords.length,

    pending: maintenanceRecords.filter(
      m => m.status === "PENDING"
    ).length,

    progress: maintenanceRecords.filter(
      m => m.status === "IN_PROGRESS"
    ).length,

    completed: maintenanceRecords.filter(
      m => m.status === "COMPLETED"
    ).length

  };

  return (

    <>
      <Navbar />

      <div className="dashboard">

        <h1>Maintenance Management</h1>

        <p>
          Track and manage equipment maintenance.
        </p>

        <div className="dashboard-container">

          <div className="dashboard-card">
            <div className="card-content">
              <h4>Total</h4>
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
              <h4>In Progress</h4>
              <h2>{stats.progress}</h2>
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
          technicians={technicians}
          formData={formData}
          handleChange={handleChange}
          handleSubmit={handleSubmit}
        />

        <MaintenanceTable
          maintenanceRecords={maintenanceRecords}
          startMaintenance={startMaintenance}
          completeMaintenance={completeMaintenance}
          cancelMaintenance={cancelMaintenance}
        />

      </div>

    </>

  );

}

export default MaintenancePage;