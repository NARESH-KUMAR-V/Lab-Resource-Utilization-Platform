import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { FaFlask, FaPlus, FaSearch, FaSyncAlt } from "react-icons/fa";
import api from "../api/axios";
import Layout from "../components/Layout";
import LaboratoryForm from "../components/LaboratoryForm";
import LaboratoryTable from "../components/LaboratoryTable";
import "./LaboratoryPage.css";

function LaboratoryPage() {

  const role = localStorage.getItem("role");
  const institutionId = localStorage.getItem("institutionId");

  const canManage =
    role === "SYSTEM_ADMIN" ||
    role === "INSTITUTION_ADMIN";

  const [laboratories, setLaboratories] = useState([]);
  const [searchText, setSearchText] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [editId, setEditId] = useState(null);

  const [formData, setFormData] = useState({
    name: "",
    department: "",
    location: "",
    hodName: "",
    institution: {
      id: role === "INSTITUTION_ADMIN" ? institutionId : ""
    }
  });

  useEffect(() => {
    fetchLaboratories();
  }, []);

  const fetchLaboratories = async () => {

    try {

      let response;

      if (role === "SYSTEM_ADMIN") {
        response = await api.get("/laboratories");
      } else {
        response = await api.get(
          `/laboratories/institution/${institutionId}`
        );
      }

      setLaboratories(response.data);

    } catch (error) {

      console.error(error);
      toast.error("Failed to load laboratories.");

    }

  };

  const searchLaboratory = () => {

    if (!searchText.trim()) {
      fetchLaboratories();
      return;
    }

    const filtered = laboratories.filter(lab =>
      lab.name.toLowerCase().includes(searchText.toLowerCase())
    );

    setLaboratories(filtered);

  };

  const handleChange = (e) => {

    const { name, value } = e.target;

    if (name === "institutionId") {

      setFormData({
        ...formData,
        institution: { id: value }
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
      department: "",
      location: "",
      hodName: "",
      institution: {
        id: role === "INSTITUTION_ADMIN" ? institutionId : ""
      }
    });

    setEditId(null);

  };

  const handleSubmit = async (e) => {

    e.preventDefault();

    try {

      if (editId) {

        await api.put(`/laboratories/${editId}`, formData);

        toast.success("Laboratory updated successfully.");

      } else {

        await api.post("/laboratories", formData);

        toast.success("Laboratory added successfully.");

      }

      fetchLaboratories();
      resetForm();
      setShowForm(false);

    } catch (error) {

      console.error(error);
      toast.error("Operation failed.");

    }

  };

  const handleDelete = async (id) => {

    if (!window.confirm("Delete this laboratory?")) return;

    try {

      await api.delete(`/laboratories/${id}`);

      toast.success("Laboratory deleted.");

      fetchLaboratories();

    } catch (error) {

      console.error(error);

      toast.error("Delete failed.");

    }

  };

  const handleEdit = (lab) => {

    setEditId(lab.id);

    setShowForm(true);

    setFormData({
      name: lab.name,
      department: lab.department,
      location: lab.location,
      hodName: lab.hodName,
      institution: lab.institution
    });

    window.scrollTo({
      top: 0,
      behavior: "smooth"
    });

  };

  return (

    <Layout>

      <div className="laboratory-page">

        <div className="page-header">

          <div>

            <h1>
              <FaFlask />
              Laboratory Management
            </h1>

            <p>Manage laboratories.</p>

          </div>

          {canManage && (

            <button
              className="add-equipment-btn"
              onClick={() => {

                resetForm();
                setShowForm(!showForm);

              }}
            >
              <FaPlus />
              {showForm ? "Close Form" : "Add Laboratory"}
            </button>

          )}

        </div>

        <div className="equipment-toolbar">

          <div className="search-wrapper">

            <FaSearch />

            <input
              type="text"
              placeholder="Search laboratory..."
              value={searchText}
              onChange={(e)=>setSearchText(e.target.value)}
            />

          </div>

          <button
            className="toolbar-btn"
            onClick={searchLaboratory}
          >
            Search
          </button>

          <button
            className="toolbar-btn secondary"
            onClick={()=>{
              setSearchText("");
              fetchLaboratories();
            }}
          >
            <FaSyncAlt />
            Refresh
          </button>

        </div>

        {showForm && canManage && (

          <LaboratoryForm
            formData={formData}
            handleChange={handleChange}
            handleSubmit={handleSubmit}
            editId={editId}
            role={role}
          />

        )}

        <LaboratoryTable
          laboratories={laboratories}
          handleEdit={handleEdit}
          handleDelete={handleDelete}
        />

      </div>

    </Layout>

  );

}

export default LaboratoryPage;