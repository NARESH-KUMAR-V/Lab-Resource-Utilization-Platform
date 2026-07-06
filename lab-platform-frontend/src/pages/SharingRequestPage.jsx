import { useEffect, useState } from "react";
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
    }
  };

  const fetchRequests = async () => {
    try {
      const response = await api.get("/sharing-requests");
      setRequests(response.data);
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

      await api.post("/sharing-requests", {
        equipment: {
          id: formData.equipmentId,
        },
        purpose: formData.purpose,
      });

      alert("Sharing request submitted successfully!");

      setFormData({
        equipmentId: "",
        purpose: "",
      });

      fetchRequests();

    } catch (error) {

      console.error(error);

      alert(
        error.response?.data?.message ||
        "Failed to submit request."
      );

    }

  };

  const approveRequest = async (id) => {

    try {

      await api.put(`/sharing-requests/${id}/approve`);

      fetchRequests();

    } catch (error) {

      console.error(error);

    }

  };

  const rejectRequest = async (id) => {

    try {

      await api.put(`/sharing-requests/${id}/reject`);

      fetchRequests();

    } catch (error) {

      console.error(error);

    }

  };

  const deleteRequest = async (id) => {

    if (!window.confirm("Delete this request?")) return;

    try {

      await api.delete(`/sharing-requests/${id}`);

      fetchRequests();

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
          Sharing Request Management
        </h2>

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