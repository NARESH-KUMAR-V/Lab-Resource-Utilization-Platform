import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { FaUniversity, FaPlus } from "react-icons/fa";
import api from "../api/axios";
import Layout from "../components/Layout";
import InstitutionForm from "../components/InstitutionForm";
import InstitutionTable from "../components/InstitutionTable";
import "./InstitutionPage.css";

function InstitutionPage() {

  const role = localStorage.getItem("role");

  const canManage = role === "SYSTEM_ADMIN";

  const [institutions, setInstitutions] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [editId, setEditId] = useState(null);

  const [formData, setFormData] = useState({
    name: "",
    address: "",
    email: "",
    phone: ""
  });

  useEffect(() => {
    fetchInstitutions();
  }, []);

  const fetchInstitutions = async () => {

    try {

      const response = await api.get("/institutions");

      setInstitutions(response.data);

    } catch (error) {

      console.error(error);

      toast.error("Failed to load institutions.");

    }

  };

  const handleChange = (e) => {

    const { name, value } = e.target;

    setFormData({
      ...formData,
      [name]: value
    });

  };

  const resetForm = () => {

    setFormData({
      name: "",
      address: "",
      email: "",
      phone: ""
    });

    setEditId(null);

  };

  const handleSubmit = async (e) => {

    e.preventDefault();

    try {

      if (editId) {

        await api.put(`/institutions/${editId}`, formData);

        toast.success("Institution updated successfully.");

      } else {

        await api.post("/institutions", formData);

        toast.success("Institution added successfully.");

      }

      fetchInstitutions();

      resetForm();

      setShowForm(false);

    } catch (error) {

      console.error(error);

      toast.error("Operation failed.");

    }

  };

  const handleDelete = async (id) => {

    if (!window.confirm("Delete this institution?")) return;

    try {

      await api.delete(`/institutions/${id}`);

      toast.success("Institution deleted.");

      fetchInstitutions();

    } catch (error) {

      console.error(error);

      toast.error("Delete failed.");

    }

  };

  const handleEdit = (institution) => {

    setEditId(institution.id);

    setShowForm(true);

    setFormData({
      name: institution.name,
      address: institution.address,
      email: institution.email,
      phone: institution.phone
    });

    window.scrollTo({
      top: 0,
      behavior: "smooth"
    });

  };

  return (

    <Layout>

      <div className="institution-page">

        <div className="page-header">

          <div>

            <h1>

              <FaUniversity />

              Institution Management

            </h1>

            <p>
              Manage all institutions from one place.
            </p>

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

              {showForm ? "Close Form" : "Add Institution"}

            </button>

          )}

        </div>

        {showForm && canManage && (

          <InstitutionForm
            formData={formData}
            handleChange={handleChange}
            handleSubmit={handleSubmit}
            editId={editId}
          />

        )}

        <InstitutionTable
          institutions={institutions}
          handleEdit={handleEdit}
          handleDelete={handleDelete}
        />

      </div>

    </Layout>

  );

}

export default InstitutionPage;