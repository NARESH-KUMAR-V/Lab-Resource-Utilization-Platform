import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import Navbar from "../components/Navbar";
import api from "../api/axios";
import SharingRequestForm from "../components/SharingRequestForm";
import SharingRequestTable from "../components/SharingRequestTable";

function SharingRequestPage() {

  const [equipment, setEquipment] = useState([]);
  const [requests, setRequests] = useState([]);

  const [formData, setFormData] = useState({
    equipmentId: "",
    purpose: "",
  });

  useEffect(() => {
    fetchEquipment();
    fetchRequests();
  }, []);

  const fetchEquipment = async () => {

    try {

      const response = await api.get("/equipment");

      setEquipment(response.data);

    } catch (error) {

      console.error(error);

      toast.error("Failed to load equipment.");

    }

  };

  const fetchRequests = async () => {

    try {

      const response = await api.get("/sharing-requests");

      setRequests(response.data);

    } catch (error) {

      console.error(error);

      toast.error("Failed to load sharing requests.");

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

      await api.post("/sharing-requests", {

        equipment: {
          id: formData.equipmentId,
        },

        purpose: formData.purpose,

      });

      toast.success("Sharing request submitted successfully!");

      setFormData({
        equipmentId: "",
        purpose: "",
      });

      fetchRequests();

    } catch (error) {

      console.error(error);

      toast.error(
        error.response?.data?.message ||
        "Failed to submit request."
      );

    }

  };

  const approveRequest = async (id) => {

    try {

      await api.put(`/sharing-requests/${id}/approve`);

      toast.success("Request approved.");

      fetchRequests();

    } catch (error) {

      console.error(error);

      toast.error("Failed to approve request.");

    }

  };

  const rejectRequest = async (id) => {

    try {

      await api.put(`/sharing-requests/${id}/reject`);

      toast.success("Request rejected.");

      fetchRequests();

    } catch (error) {

      console.error(error);

      toast.error("Failed to reject request.");

    }

  };

  const deleteRequest = async (id) => {

    if (!window.confirm("Delete this request?")) return;

    try {

      await api.delete(`/sharing-requests/${id}`);

      toast.success("Request deleted.");

      fetchRequests();

    } catch (error) {

      console.error(error);

      toast.error("Failed to delete request.");

    }

  };

  const stats = {

    total: requests.length,

    pending: requests.filter(
      (r) => r.status === "PENDING"
    ).length,

    approved: requests.filter(
      (r) => r.status === "APPROVED"
    ).length,

    rejected: requests.filter(
      (r) => r.status === "REJECTED"
    ).length,

  };

  return (

    <>
      <Navbar />

      <div className="dashboard">

        <h1>Sharing Request Management</h1>

        <p>
          Manage equipment sharing requests across departments and researchers.
        </p>

        <div className="dashboard-container">

          <div className="dashboard-card">
            <div className="card-content">
              <h4>Total Requests</h4>
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
              <h4>Approved</h4>
              <h2>{stats.approved}</h2>
            </div>
          </div>

          <div className="dashboard-card">
            <div className="card-content">
              <h4>Rejected</h4>
              <h2>{stats.rejected}</h2>
            </div>
          </div>

        </div>

        <SharingRequestForm
          equipment={equipment}
          formData={formData}
          handleChange={handleChange}
          handleSubmit={handleSubmit}
        />

        <SharingRequestTable
          requests={requests}
          approveRequest={approveRequest}
          rejectRequest={rejectRequest}
          deleteRequest={deleteRequest}
        />

      </div>

    </>

  );

}

export default SharingRequestPage;