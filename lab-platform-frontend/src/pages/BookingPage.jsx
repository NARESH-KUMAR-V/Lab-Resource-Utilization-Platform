import { useEffect, useState } from "react";
import api from "../api/axios";
import BookingForm from "../components/BookingForm";
import BookingTable from "../components/BookingTable";
import Navbar from "../components/Navbar";

function BookingPage() {

  const [equipment, setEquipment] = useState([]);
  const [bookings, setBookings] = useState([]);

  const [bookingData, setBookingData] = useState({
    equipmentId: "",
    bookingDate: "",
    purpose: "",
  });

  useEffect(() => {
    fetchEquipment();
    fetchMyBookings();
  }, []);

  const fetchEquipment = async () => {
    try {
      const response = await api.get("/equipment");
      setEquipment(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  const fetchMyBookings = async () => {
    try {
      const response = await api.get("/bookings/my");
      setBookings(response.data);
    } catch (error) {
      console.error(error);
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

      alert("Booking created successfully!");

      setBookingData({
        equipmentId: "",
        bookingDate: "",
        purpose: "",
      });

      fetchMyBookings();

    } catch (error) {
      console.error(error);
      alert("Failed to create booking.");
    }
  };

  return (
  <>
    <Navbar />

    <div style={{ padding: "20px" }}>

      <h2>Book Equipment</h2>

      <BookingForm
        equipment={equipment}
        bookingData={bookingData}
        handleChange={handleChange}
        handleSubmit={handleSubmit}
      />

      <BookingTable
        bookings={bookings}
      />

    </div>
   </> 
  );
}

export default BookingPage;