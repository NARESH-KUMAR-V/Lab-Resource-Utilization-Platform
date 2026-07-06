import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import api from "../api/axios";
import EquipmentForm from "../components/EquipmentForm";
import EquipmentTable from "../components/EquipmentTable";
import Navbar from "../components/Navbar";
import "./EquipmentPage.css";

function EquipmentPage() {

  const [equipment, setEquipment] = useState([]);
  const [searchText, setSearchText] = useState("");
  const [editId, setEditId] = useState(null);

  const [formData, setFormData] = useState({
    name: "",
    category: "",
    specifications: "",
    status: "AVAILABLE",
    department: "",
    institution: "",
  });

  useEffect(() => {
    fetchEquipment();
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

  const searchEquipment = async () => {

    try {

      const response = await api.get(
        `/equipment/search?name=${searchText}`
      );

      setEquipment(response.data);

    } catch (error) {

      console.error(error);

      toast.error("Search failed.");

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

      if (editId) {

        await api.put(`/equipment/${editId}`, formData);

        toast.success("Equipment updated successfully!");

      } else {

        await api.post("/equipment", formData);

        toast.success("Equipment added successfully!");

      }

      setFormData({
        name: "",
        category: "",
        specifications: "",
        status: "AVAILABLE",
        department: "",
        institution: "",
      });

      setEditId(null);

      fetchEquipment();

    } catch (error) {

      console.error(error);

      toast.error("Operation failed.");

    }

  };

  const handleDelete = async (id) => {

    const confirmDelete = window.confirm(
      "Are you sure you want to delete this equipment?"
    );

    if (!confirmDelete) return;

    try {

      await api.delete(`/equipment/${id}`);

      toast.success("Equipment deleted successfully!");

      fetchEquipment();

    } catch (error) {

      console.error(error);

      toast.error("Failed to delete equipment.");

    }

  };

  const handleEdit = (item) => {

    setEditId(item.id);

    setFormData({

      name: item.name,
      category: item.category,
      specifications: item.specifications,
      status: item.status,
      department: item.department,
      institution: item.institution,

    });

  };

  return (

    <>
      <Navbar />

      <div className="equipment-page">

        <h2>Equipment Inventory</h2>

        <div className="search-container">

          <input
            type="text"
            placeholder="🔍 Search equipment..."
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            className="search-input"
          />

          <button
            className="search-btn"
            onClick={searchEquipment}
          >
            Search
          </button>

          <button
            className="show-btn"
            onClick={fetchEquipment}
          >
            Show All
          </button>

        </div>

        <EquipmentForm
          formData={formData}
          handleChange={handleChange}
          handleSubmit={handleSubmit}
          editId={editId}
        />

        <EquipmentTable
          equipment={equipment}
          handleEdit={handleEdit}
          handleDelete={handleDelete}
        />

      </div>

    </>

  );

}

export default EquipmentPage;