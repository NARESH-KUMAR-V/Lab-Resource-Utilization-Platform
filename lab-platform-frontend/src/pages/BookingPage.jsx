import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import {
  FaCalendarCheck,
  FaPlus,
  FaSyncAlt,
  FaListOl
} from "react-icons/fa";

import api from "../api/axios";
import { useAuth } from "../context/AuthContext";

import Layout from "../components/Layout";
import DashboardCard from "../components/DashboardCard";
import BookingForm from "../components/BookingForm";
import BookingTable from "../components/BookingTable";
import BookingBarChart from "../components/BookingBarChart";
import WaitingQueueModal from "../components/WaitingQueueModal";

import "./EquipmentPage.css";

function BookingPage() {

  const { role } = useAuth();

  const [equipment, setEquipment] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [showForm, setShowForm] = useState(false);

  const [showWaitingModal, setShowWaitingModal] = useState(false);
  const [waitingBookings, setWaitingBookings] = useState([]);

  const [bookingData, setBookingData] = useState({
    equipmentId: "",
    startDate: "",
    endDate: "",
    purpose: "",
  });

  const isResearcher = role === "RESEARCHER";

  useEffect(() => {
    fetchEquipment();
    fetchBookings();
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

  const fetchBookings = async () => {

    try {

      const response = isResearcher
        ? await api.get("/bookings/my")
        : await api.get("/bookings");

      setBookings(response.data);

    } catch (error) {

      console.error(error);

      toast.error("Failed to load bookings.");

    }

  };

  const handleChange = (e) => {

    setBookingData({
      ...bookingData,
      [e.target.name]: e.target.value,
    });

  };

  const handleSubmit = async (e) => {

    e.preventDefault();

    try {

      await api.post("/bookings", bookingData);

      toast.success("Booking created successfully!");

      setBookingData({
        equipmentId: "",
        startDate: "",
        endDate: "",
        purpose: "",
      });

      setShowForm(false);

      fetchBookings();

    } catch (error) {

      console.error(error);

      toast.error(
        error.response?.data?.message ||
        "Failed to create booking."
      );

    }

  };

  const approveBooking = async (id) => {

    try {

      await api.put(`/bookings/${id}/approve`);

      toast.success("Booking approved.");

      fetchBookings();
      if (showWaitingModal) {
        fetchWaitingBookings();
      }

    } catch (error) {

      toast.error(
        error.response?.data?.message ||
        "Failed to approve booking."
      );

    }

  };

  const rejectBooking = async (id) => {

    try {

      await api.put(`/bookings/${id}/reject`);

      toast.success("Booking rejected.");

      fetchBookings();
      if (showWaitingModal) {
        fetchWaitingBookings();
      }

    } catch (error) {

      toast.error(
        error.response?.data?.message ||
        "Failed to reject booking."
      );

    }

  };

  const completeBooking = async (id) => {

    try {

      await api.put(`/bookings/${id}/complete`);

      toast.success("Booking completed.");

      fetchBookings();
      if (showWaitingModal) {
        fetchWaitingBookings();
      }

    } catch (error) {

      toast.error(
        error.response?.data?.message ||
        "Failed to complete booking."
      );

    }

  };

  const fetchWaitingBookings = async () => {

    try {

      const response = await api.get("/bookings/waiting");

      setWaitingBookings(response.data);

      setShowWaitingModal(true);

    } catch (error) {

      toast.error("Failed to load waiting queue.");

    }

  };

  const bookingStats = {

    totalBookings: bookings.length,

    pendingBookings:
      bookings.filter(
        b => b.status === "PENDING"
      ).length,

    waitingBookings:
      bookings.filter(
        b => b.status === "WAITING"
      ).length,

    approvedBookings:
      bookings.filter(
        b => b.status === "APPROVED"
      ).length,

    rejectedBookings:
      bookings.filter(
        b => b.status === "REJECTED"
      ).length,

    completedBookings:
      bookings.filter(
        b => b.status === "COMPLETED"
      ).length

  };

  return (

    <Layout>

      <div className="equipment-page">

        <div className="page-header">

          <div>

            <h1>
              <FaCalendarCheck />
              Booking &amp; Queue Management
            </h1>

            <p>
              Schedule equipment reservations, review pending requests, and manage queue positions.
            </p>

          </div>

          {isResearcher && (

            <button
              className="add-equipment-btn"
              onClick={() => setShowForm(!showForm)}
            >

              <FaPlus />

              {showForm ? "Close Form" : "New Booking"}

            </button>

          )}

        </div>

        {/* Operational Stats Grid */}
        <div className="dashboard-container">

          <DashboardCard
            title="Total Bookings"
            value={bookingStats.totalBookings}
            icon={<FaCalendarCheck />}
          />

          <DashboardCard
            title="Pending Requests"
            value={bookingStats.pendingBookings}
            icon={<FaCalendarCheck />}
          />

          <DashboardCard
            title="In Waiting Queue"
            value={bookingStats.waitingBookings}
            icon={<FaCalendarCheck />}
          />

          <DashboardCard
            title="Approved"
            value={bookingStats.approvedBookings}
            icon={<FaCalendarCheck />}
          />

          <DashboardCard
            title="Rejected"
            value={bookingStats.rejectedBookings}
            icon={<FaCalendarCheck />}
          />

          <DashboardCard
            title="Completed"
            value={bookingStats.completedBookings}
            icon={<FaCalendarCheck />}
          />

        </div>

        {showForm && isResearcher && (

          <BookingForm
            equipment={equipment}
            bookingData={bookingData}
            handleChange={handleChange}
            handleSubmit={handleSubmit}
            selectedEquipment={
              equipment.find(
                item => item.id === Number(bookingData.equipmentId)
              )
            }
          />

        )}

        <div className="charts-section">

          <BookingBarChart
            stats={bookingStats}
          />

        </div>

        <div
          style={{
            display: "flex",
            justifyContent: "flex-end",
            gap: "12px",
            marginBottom: "16px"
          }}
        >

          <button
            className="toolbar-btn secondary"
            onClick={fetchBookings}
          >

            <FaSyncAlt />

            Refresh Bookings

          </button>

          {!isResearcher && (
            <button
              className="toolbar-btn"
              onClick={fetchWaitingBookings}
            >
              <FaListOl />
              View Waiting Queue
            </button>
          )}

        </div>

        <BookingTable
          bookings={bookings}
          approveBooking={approveBooking}
          rejectBooking={rejectBooking}
          completeBooking={completeBooking}
          role={role}
        />

        {showWaitingModal && (
          <WaitingQueueModal
            bookings={waitingBookings}
            allBookings={bookings}
            onClose={() => setShowWaitingModal(false)}
            approveBooking={approveBooking}
            rejectBooking={rejectBooking}
          />
        )}

      </div>

    </Layout>

  );

}

export default BookingPage;