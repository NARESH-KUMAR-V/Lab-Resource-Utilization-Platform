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

  const isEquipmentCurrentlyPendingOrApproved = (equipmentId) => {
    if (!equipmentId) return false;
    return bookings.some(
      (b) =>
        b.equipment?.id === equipmentId &&
        (b.status === "PENDING" || b.status === "APPROVED")
    );
  };

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
          {isAdmin ? "All Booking Records" : "My Booking Requests"}
        </h2>

        <div className="search-wrapper">

          <FaSearch />

          <input
            type="text"
            placeholder="Search by equipment or researcher..."
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

            <th>Researcher</th>

            <th>Start Date</th>

            <th>End Date</th>

            <th>Purpose</th>

            <th>Cost (₹)</th>

            <th>Status</th>

            <th>Queue No.</th>

            {isAdmin && <th>Actions</th>}

          </tr>

        </thead>

        <tbody>

          {filteredBookings.length > 0 ? (

            filteredBookings.map((booking) => {

              const equipmentHasPendingOrApproved = isEquipmentCurrentlyPendingOrApproved(
                booking.equipment?.id
              );

              return (

                <tr key={booking.id}>

                  <td><strong>#{booking.id}</strong></td>

                  <td><strong>{booking.equipment?.name || "-"}</strong></td>

                  <td>{booking.equipment?.laboratory?.name || "-"}</td>

                  <td>{booking.equipment?.laboratory?.institution?.name || "-"}</td>

                  <td>{booking.user?.name || "-"}</td>

                  <td>{formatDate(booking.startDate)}</td>

                  <td>{formatDate(booking.endDate)}</td>

                  <td
                    title={booking.purpose || ""}
                    style={{ maxWidth: "160px", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}
                  >
                    {booking.purpose || "-"}
                  </td>

                  <td>
                    ₹{booking.utilizationCost?.toLocaleString() || 0}
                  </td>

                  <td>

                    <span className={`status-badge ${getStatusClass(booking.status)}`}>

                      {booking.status.replaceAll("_", " ")}

                    </span>

                  </td>

                  <td>

                    {booking.status === "WAITING" ? (
                      <span className="status-badge status-waiting">
                        #{booking.waitingPosition}
                      </span>
                    ) : "-"}

                  </td>

                  {isAdmin && (

                    <td>

                      {booking.status === "PENDING" && (

                        <>

                          <button
                            className="action-btn approve-btn"
                            onClick={() => approveBooking(booking.id)}
                            title="Approve Booking Request"
                          >
                            <FaCheckCircle />
                            Approve
                          </button>

                          <button
                            className="action-btn reject-btn"
                            onClick={() => rejectBooking(booking.id)}
                            title="Reject Booking Request"
                          >
                            <FaTimesCircle />
                            Reject
                          </button>

                        </>

                      )}

                      {booking.status === "WAITING" && (

                        (!equipmentHasPendingOrApproved && booking.waitingPosition === 1) ? (

                          <>

                            <button
                              className="action-btn approve-btn"
                              onClick={() => approveBooking(booking.id)}
                              title="Approve Waiting Request"
                            >
                              <FaCheckCircle />
                              Approve
                            </button>

                            <button
                              className="action-btn reject-btn"
                              onClick={() => rejectBooking(booking.id)}
                              title="Reject Waiting Request"
                            >
                              <FaTimesCircle />
                              Reject
                            </button>

                          </>

                        ) : (

                          <button
                            className="action-btn secondary"
                            disabled
                          >
                            In Queue
                          </button>

                        )

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

              );

            })

          ) : (

            <tr>

              <td
                colSpan={isAdmin ? 12 : 11}
                className="empty-table"
              >

                📅 No booking records found.

              </td>

            </tr>

          )}

        </tbody>

      </table>

    </div>

  );

}

export default BookingTable;