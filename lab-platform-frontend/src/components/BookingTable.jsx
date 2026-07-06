import { useState } from "react";
import "./Table.css";

function BookingTable({ bookings }) {

  const [search, setSearch] = useState("");

  const filteredBookings = bookings.filter((booking) =>
    booking.equipment?.name
      ?.toLowerCase()
      .includes(search.toLowerCase())
  );

  return (

    <div className="table-card">

      <div className="table-header">

        <h2>My Bookings</h2>

        <input
          type="text"
          className="table-search"
          placeholder="🔍 Search equipment..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />

      </div>

      <table className="data-table">

        <thead>

          <tr>

            <th>ID</th>

            <th>Equipment</th>

            <th>Booking Date</th>

            <th>Purpose</th>

            <th>Status</th>

          </tr>

        </thead>

        <tbody>

          {filteredBookings.length > 0 ? (

            filteredBookings.map((booking) => (

              <tr key={booking.id}>

                <td>{booking.id}</td>

                <td>{booking.equipment?.name}</td>

                <td>{booking.bookingDate}</td>

                <td>{booking.purpose}</td>

                <td>

                  <span
                    className={`status-badge ${
                      booking.status === "APPROVED"
                        ? "status-approved"
                        : booking.status === "REJECTED"
                        ? "status-rejected"
                        : "status-pending"
                    }`}
                  >
                    {booking.status}
                  </span>

                </td>

              </tr>

            ))

          ) : (

            <tr>

              <td
                colSpan="5"
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