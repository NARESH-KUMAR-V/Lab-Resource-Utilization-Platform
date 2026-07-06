import "./Table.css";

function BookingTable({ bookings }) {
  return (
    <>
      <h2
        style={{
          marginTop: "40px",
          marginBottom: "20px",
          color: "#1976d2",
          textAlign: "center",
        }}
      >
        My Bookings
      </h2>

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
          {bookings.length > 0 ? (
            bookings.map((booking) => (
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
                style={{
                  textAlign: "center",
                  padding: "20px",
                }}
              >
                No bookings found.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </>
  );
}

export default BookingTable;