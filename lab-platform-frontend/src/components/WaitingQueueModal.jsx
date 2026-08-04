import React from "react";
import { FaCheckCircle, FaTimesCircle } from "react-icons/fa";
import "./WaitingQueueModal.css";

function WaitingQueueModal({ bookings, allBookings, onClose, approveBooking, rejectBooking }) {

  const isEquipmentCurrentlyPendingOrApproved = (equipmentId) => {
    if (!allBookings || !equipmentId) return false;
    return allBookings.some(
      (b) =>
        b.equipment?.id === equipmentId &&
        (b.status === "PENDING" || b.status === "APPROVED")
    );
  };

  return (
    <div className="waiting-modal-overlay">

      <div className="waiting-modal">

        <div className="waiting-header">

          <h2>⏳ Waiting Queue Status</h2>

          <button
            className="close-btn"
            onClick={onClose}
          >
            ✖
          </button>

        </div>

        {bookings.length === 0 ? (

          <p className="empty-message">
            No users are currently in the waiting queue.
          </p>

        ) : (

          <div className="waiting-table-container">

            <table className="waiting-table">

              <thead>

                <tr>

                  <th>Queue Position</th>

                  <th>Researcher</th>

                  <th>Equipment</th>

                  <th>Start Date</th>

                  <th>End Date</th>

                  <th>Purpose</th>

                  {(approveBooking || rejectBooking) && <th>Actions</th>}

                </tr>

              </thead>

              <tbody>

                {bookings.map((booking) => {

                  const equipmentHasPendingOrApproved = isEquipmentCurrentlyPendingOrApproved(
                    booking.equipment?.id
                  );

                  const canAct = !equipmentHasPendingOrApproved && booking.waitingPosition === 1;

                  return (

                    <tr key={booking.id}>

                      <td>
                        <span className="queue-badge">
                          #{booking.waitingPosition}
                        </span>
                      </td>

                      <td><strong>{booking.user?.name}</strong></td>

                      <td>{booking.equipment?.name}</td>

                      <td>{booking.startDate}</td>

                      <td>{booking.endDate}</td>

                      <td title={booking.purpose}>{booking.purpose || "-"}</td>

                      {(approveBooking || rejectBooking) && (
                        <td>
                          {canAct ? (
                            <div style={{ display: "inline-flex", gap: "6px" }}>
                              {approveBooking && (
                                <button
                                  className="action-btn approve-btn"
                                  onClick={() => approveBooking(booking.id)}
                                >
                                  <FaCheckCircle /> Approve
                                </button>
                              )}

                              {rejectBooking && (
                                <button
                                  className="action-btn reject-btn"
                                  onClick={() => rejectBooking(booking.id)}
                                >
                                  <FaTimesCircle /> Reject
                                </button>
                              )}
                            </div>
                          ) : (
                            <span style={{ color: "var(--color-text-subtle)", fontSize: "12.5px", fontWeight: 600 }}>
                              In Queue
                            </span>
                          )}
                        </td>
                      )}

                    </tr>

                  );

                })}

              </tbody>

            </table>

          </div>

        )}

        <div className="modal-footer">

          <button
            className="close-modal-btn"
            onClick={onClose}
          >
            Close Window
          </button>

        </div>

      </div>

    </div>
  );

}

export default WaitingQueueModal;