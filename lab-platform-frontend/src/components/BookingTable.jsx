import { useState } from "react";
import {
  FaSearch,
  FaCheckCircle,
  FaTimesCircle,
  FaClipboardCheck
} from "react-icons/fa";
import "./Table.css";

function BookingTable({
  bookings,
  approveBooking,
  rejectBooking,
  completeBooking,
  role,
}) {

  const [search, setSearch] = useState("");

  const isAdmin =
    role === "SYSTEM_ADMIN" ||
    role === "LAB_MANAGER" ||
    role === "INSTITUTION_ADMIN";

  const filteredBookings = bookings.filter((booking) => {

    if (search.trim() === "") return true;

    return (
      booking.equipment?.name
        ?.toLowerCase()
        .includes(search.toLowerCase()) ||
      booking.user?.name
        ?.toLowerCase()
        .includes(search.toLowerCase())
    );

  });

  const getStatusClass = (status) => {

  switch (status) {

    case "APPROVED":
      return "status-approved";

    case "WAITING":
      return "status-waiting";

    case "COMPLETED":
      return "status-completed";

    case "REJECTED":
      return "status-rejected";

    case "CANCELLED":
      return "status-cancelled";

    default:
      return "status-pending";

  }

};

  const formatDate = (date) => {

    if (!date) return "-";

    return new Date(date).toLocaleDateString("en-GB");

  };

  return (

    <div className="table-card">

      <div className="table-header">

        <h2>

          {isAdmin ? "All Bookings" : "My Bookings"}

        </h2>

        <div className="search-wrapper">

          <FaSearch />

          <input
            type="text"
            placeholder="Search equipment or user..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />

        </div>

      </div>

      <table className="data-table">

        <thead>

          <tr>

            <th>ID</th>

            <th>Equipment</th>

            <th>Laboratory</th>

            <th>Institution</th>

            <th>User</th>

            <th>Start</th>

            <th>End</th>

            <th>Purpose</th>

<th>Cost (₹)</th>

<th>Status</th>

<th>Waiting No.</th>

            {isAdmin && <th>Actions</th>}

          </tr>

        </thead>

        <tbody>

          {filteredBookings.length > 0 ? (

            filteredBookings.map((booking) => (

              <tr key={booking.id}>

                <td>{booking.id}</td>

                <td>{booking.equipment?.name || "-"}</td>

                <td>{booking.equipment?.laboratory?.name || "-"}</td>

                <td>{booking.equipment?.laboratory?.institution?.name || "-"}</td>

                <td>{booking.user?.name || "-"}</td>

                <td>{formatDate(booking.startDate)}</td>

                <td>{formatDate(booking.endDate)}</td>

                <td>{booking.purpose || "-"}</td>

<td>

  ₹{booking.utilizationCost?.toLocaleString() || 0}

</td>

<td>

  <span className={`status-badge ${getStatusClass(booking.status)}`}>

    {booking.status.replaceAll("_", " ")}

  </span>

</td>

<td>

  {booking.status === "WAITING"
    ? booking.waitingPosition
    : "-"}

</td>

                {isAdmin && (

  <td>

    {booking.status === "PENDING" && (

      <>

        <button
          className="action-btn edit-btn"
          onClick={() => approveBooking(booking.id)}
        >
          <FaCheckCircle />
          Approve
        </button>

        <button
          className="action-btn delete-btn"
          onClick={() => rejectBooking(booking.id)}
        >
          <FaTimesCircle />
          Reject
        </button>

      </>

    )}

    {booking.status === "WAITING" && (

      <button
        className="action-btn secondary"
        disabled
      >
        Waiting Queue
      </button>

    )}

    {booking.status === "APPROVED" && (

      <button
        className="action-btn complete-btn"
        onClick={() => completeBooking(booking.id)}
      >
        <FaClipboardCheck />
        Complete
      </button>

    )}

    {(booking.status === "REJECTED" ||
      booking.status === "COMPLETED" ||
      booking.status === "CANCELLED") && (

      <button
        className="action-btn secondary"
        disabled
      >
        Processed
      </button>

    )}

  </td>

)}

              </tr>

            ))

          ) : (

            <tr>

              <td
                colSpan={isAdmin ? 12 : 11}
                className="empty-table"
              >

                📅 No bookings found.

              </td>

            </tr>

          )}

        </tbody>

      </table>

    </div>

  );

}

export default BookingTable;