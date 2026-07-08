import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { jwtDecode } from "jwt-decode";
import api from "../api/axios";
import BookingForm from "../components/BookingForm";
import BookingTable from "../components/BookingTable";
import BookingBarChart from "../components/BookingBarChart";
import Navbar from "../components/Navbar";

function BookingPage() {

  const [equipment, setEquipment] = useState([]);
  const [bookings, setBookings] = useState([]);

  const [bookingData, setBookingData] = useState({
    equipmentId: "",
    bookingDate: "",
    purpose: "",
  });

  // ==========================
  // Get role from JWT token
  // ==========================
  const token = localStorage.getItem("token");

  let role = "";

  if (token) {
    try {
      const decoded = jwtDecode(token);
      role = decoded.role || "";
    } catch (err) {
      console.error(err);
    }
  }

  const isResearcher = role === "ROLE_RESEARCHER";

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

      let response;

      if (isResearcher) {
        response = await api.get("/bookings/my");
      } else {
        response = await api.get("/bookings");
      }

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
        bookingDate: "",
        purpose: "",
      });

      fetchBookings();

    } catch (error) {

      console.error(error);

      toast.error("Failed to create booking.");

    }

  };

  const approveBooking = async (id) => {

    try {

      await api.put(`/bookings/${id}/approve`);

      toast.success("Booking approved.");

      fetchBookings();

    } catch (error) {

      console.error(error);

      toast.error("Failed to approve booking.");

    }

  };

  const rejectBooking = async (id) => {

    try {

      await api.put(`/bookings/${id}/reject`);

      toast.success("Booking rejected.");

      fetchBookings();

    } catch (error) {

      console.error(error);

      toast.error("Failed to reject booking.");

    }

  };

  const completeBooking = async (id) => {

    try {

      await api.put(`/bookings/${id}/complete`);

      toast.success("Booking completed.");

      fetchBookings();

    } catch (error) {

      console.error(error);

      toast.error("Failed to complete booking.");

    }

  };

  const bookingStats = {

    totalBookings: bookings.length,

    pendingBookings: bookings.filter(
      (b) => b.status === "PENDING"
    ).length,

    approvedBookings: bookings.filter(
      (b) => b.status === "APPROVED"
    ).length,

    rejectedBookings: bookings.filter(
      (b) => b.status === "REJECTED"
    ).length,

  };

  return (

    <>
      <Navbar />

      <div className="dashboard">

        <h1>Book Equipment</h1>

        <p>
          Schedule laboratory equipment for research and monitor booking requests.
        </p>

        <div className="dashboard-container">

          <div className="dashboard-card">
            <div className="card-content">
              <h4>Total Bookings</h4>
              <h2>{bookingStats.totalBookings}</h2>
            </div>
          </div>

          <div className="dashboard-card">
            <div className="card-content">
              <h4>Pending</h4>
              <h2>{bookingStats.pendingBookings}</h2>
            </div>
          </div>

          <div className="dashboard-card">
            <div className="card-content">
              <h4>Approved</h4>
              <h2>{bookingStats.approvedBookings}</h2>
            </div>
          </div>

          <div className="dashboard-card">
            <div className="card-content">
              <h4>Rejected</h4>
              <h2>{bookingStats.rejectedBookings}</h2>
            </div>
          </div>

        </div>

        {isResearcher && (
          <BookingForm
            equipment={equipment}
            bookingData={bookingData}
            handleChange={handleChange}
            handleSubmit={handleSubmit}
          />
        )}

        <div className="charts-section">
          <BookingBarChart stats={bookingStats} />
        </div>

        <BookingTable
          bookings={bookings}
          approveBooking={approveBooking}
          rejectBooking={rejectBooking}
          completeBooking={completeBooking}
          role={role}
        />

      </div>

    </>

  );

}

export default BookingPage;