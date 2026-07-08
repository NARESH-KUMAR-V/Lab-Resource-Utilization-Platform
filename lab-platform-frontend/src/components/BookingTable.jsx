import { useState } from "react";
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
  role === "ROLE_SYSTEM_ADMIN" ||
  role === "ROLE_LAB_MANAGER" ||
  role === "ROLE_INSTITUTION_ADMIN";

  const filteredBookings = bookings.filter((booking) => {

    if (search.trim() === "") return true;

    return booking.equipment?.name
      ?.toLowerCase()
      .includes(search.toLowerCase());

  });

  return (

    <div className="table-card">

      <div className="table-header">

        <h2>
          {isAdmin ? "All Bookings" : "My Bookings"}
        </h2>

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

            <th>User</th>

            <th>Booking Date</th>

            <th>Purpose</th>

            <th>Status</th>

            {isAdmin && <th>Actions</th>}

          </tr>

        </thead>

        <tbody>

          {filteredBookings.length > 0 ? (

            filteredBookings.map((booking) => (

              <tr key={booking.id}>

                <td>{booking.id}</td>

                <td>{booking.equipment?.name}</td>

                <td>{booking.user?.name}</td>

                <td>{booking.bookingDate}</td>

                <td>{booking.purpose}</td>

                <td>

                  <span
                    className={`status-badge ${
                      booking.status === "APPROVED"
                        ? "status-approved"
                        : booking.status === "REJECTED"
                        ? "status-rejected"
                        : booking.status === "COMPLETED"
                        ? "status-approved"
                        : "status-pending"
                    }`}
                  >
                    {booking.status}
                  </span>

                </td>

                {isAdmin && (

                  <td>

                    {booking.status === "PENDING" && (

                      <>

                        <button
                          className="action-btn edit-btn"
                          onClick={() =>
                            approveBooking(booking.id)
                          }
                        >
                          ✅ Approve
                        </button>

                        <button
                          className="action-btn delete-btn"
                          onClick={() =>
                            rejectBooking(booking.id)
                          }
                        >
                          ❌ Reject
                        </button>

                      </>

                    )}

                    {booking.status === "APPROVED" && (

                      <button
                        className="action-btn complete-btn"
                        onClick={() =>
                          completeBooking(booking.id)
                        }
                      >
                        ✔ Complete
                      </button>

                    )}

                    {(booking.status === "REJECTED" ||
                      booking.status === "COMPLETED") && (

                      <button
                        className="action-btn"
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
                colSpan={isAdmin ? 7 : 6}
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