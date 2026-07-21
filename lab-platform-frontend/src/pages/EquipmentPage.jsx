import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { FaPlus, FaSearch, FaSyncAlt, FaBoxes } from "react-icons/fa";
import api from "../api/axios";
import Layout from "../components/Layout";
import EquipmentForm from "../components/EquipmentForm";
import EquipmentTable from "../components/EquipmentTable";
import "./EquipmentPage.css";

function EquipmentPage() {

  const [equipment, setEquipment] = useState([]);
  const [searchText, setSearchText] = useState("");
  const [editId, setEditId] = useState(null);
  const [showForm, setShowForm] = useState(false);

  const role = localStorage.getItem("role");

  const canManageEquipment =
    role === "LAB_MANAGER" ||
    role === "INSTITUTION_ADMIN" ||
    role === "SYSTEM_ADMIN";

  const [formData, setFormData] = useState({
    name: "",
    category: "",
    specifications: "",
    description: "",
    costPerDay: "",
    image: null,
    imageUrl: "",
    status: "AVAILABLE",
    laboratory: {
      id: ""
    }
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
    if (!searchText.trim()) {
      fetchEquipment();
      return;
    }

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

    const { name, value, files } = e.target;

    if (name === "image") {
      setFormData({
        ...formData,
        image: files[0]
      });
      return;
    }

    if (name === "laboratory") {
      setFormData({
        ...formData,
        laboratory: value
      });
      return;
    }

    setFormData({
      ...formData,
      [name]: value
    });

  };

  const resetForm = () => {

    setFormData({
      name: "",
      category: "",
      specifications: "",
      description: "",
      costPerDay: "",
      image: null,
      imageUrl: "",
      status: "AVAILABLE",
      laboratory: {
        id: ""
      }
    });

    setEditId(null);

  };

  const handleSubmit = async (e) => {

    e.preventDefault();

    try {

      let imageUrl = formData.imageUrl;

      if (formData.image) {

        const imageData = new FormData();
        imageData.append("file", formData.image);

        const uploadResponse = await api.post(
          "/equipment/upload",
          imageData,
          {
            headers: {
              "Content-Type": "multipart/form-data"
            }
          }
        );

        imageUrl = uploadResponse.data;
      }

      const equipmentData = {
        ...formData,
        imageUrl
      };

      delete equipmentData.image;

      if (editId) {

        await api.put(`/equipment/${editId}`, equipmentData);

        toast.success("Equipment updated successfully!");

      } else {

        await api.post("/equipment", equipmentData);

        toast.success("Equipment added successfully!");

      }

      fetchEquipment();

      resetForm();

      setShowForm(false);

    } catch (error) {

      console.error(error);

      toast.error("Operation failed.");

    }

  };

  const handleDelete = async (id) => {

    if (!window.confirm("Delete this equipment?")) return;

    try {

      await api.delete(`/equipment/${id}`);

      toast.success("Equipment deleted successfully!");

      fetchEquipment();

    } catch (error) {

      console.error(error);

      toast.error("Delete failed.");

    }

  };

  const handleEdit = (item) => {

    setEditId(item.id);

    setShowForm(true);

    setFormData({
      name: item.name,
      category: item.category,
      specifications: item.specifications,
      description: item.description || "",
      costPerDay: item.costPerDay,
      image: null,
      imageUrl: item.imageUrl || "",
      status: item.status,
      laboratory: item.laboratory
    });

    window.scrollTo({
      top: 0,
      behavior: "smooth"
    });

  };

  return (
    <Layout>

      <div className="equipment-page">

        <div className="page-header">

          <div>

            <h1>
              <FaBoxes />
              Equipment Management
            </h1>

            <p>
              Manage all laboratory equipment from one place.
            </p>

          </div>

          {canManageEquipment && (

            <button
              className="add-equipment-btn"
              onClick={() => {
                resetForm();
                setShowForm(!showForm);
              }}
            >
              <FaPlus />
              {showForm ? "Close Form" : "Add Equipment"}
            </button>

          )}

        </div>

        <div className="equipment-toolbar">

          <div className="search-wrapper">

            <FaSearch />

            <input
              type="text"
              placeholder="Search equipment..."
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
            />

          </div>

          <button
            className="toolbar-btn"
            onClick={searchEquipment}
          >
            Search
          </button>

          <button
            className="toolbar-btn secondary"
            onClick={() => {
              setSearchText("");
              fetchEquipment();
            }}
          >
            <FaSyncAlt />
            Refresh
          </button>

        </div>

        {showForm && canManageEquipment && (

          <EquipmentForm
            formData={formData}
            handleChange={handleChange}
            handleSubmit={handleSubmit}
            editId={editId}
          />

        )}

        <EquipmentTable
          equipment={equipment}
          handleEdit={handleEdit}
          handleDelete={handleDelete}
        />

      </div>

    </Layout>
  );

}

export default EquipmentPage;