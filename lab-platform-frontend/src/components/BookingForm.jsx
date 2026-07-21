import { FaCalendarCheck, FaFlask, FaRupeeSign } from "react-icons/fa";
import "./Form.css";

function BookingForm({
  equipment,
  bookingData,
  handleChange,
  handleSubmit,
  selectedEquipment,
}) {

  const bookableEquipment = equipment.filter(
    (item) =>
      item.status === "AVAILABLE" ||
      item.status === "BOOKED"
  );

  const today = new Date().toISOString().split("T")[0];

  let estimatedDays = 0;
  let estimatedCost = 0;

  if (
    selectedEquipment &&
    bookingData.startDate &&
    bookingData.endDate
  ) {

    const start = new Date(bookingData.startDate);
    const end = new Date(bookingData.endDate);

    estimatedDays =
      Math.floor(
        (end - start) /
          (1000 * 60 * 60 * 24)
      ) + 1;

    if (estimatedDays > 0) {
      estimatedCost =
        estimatedDays *
        selectedEquipment.costPerDay;
    }

  }

  return (

    <div className="form-card">

      <div className="form-header">

        <div>

          <h2>

            <FaCalendarCheck
              style={{
                marginRight: "10px",
                color: "#6c63ff",
              }}
            />

            Book Equipment

          </h2>

          <p>

            Reserve laboratory equipment for your
            experiment or research work.

          </p>

        </div>

      </div>

      <form onSubmit={handleSubmit}>

        <div className="form-grid">

          <div className="form-group">

            <label>

              <FaFlask
                style={{
                  marginRight: "8px",
                  color: "#6c63ff",
                }}
              />

              Equipment

            </label>

            <select
              name="equipmentId"
              value={bookingData.equipmentId}
              onChange={handleChange}
              required
            >

              <option value="">
                Select Equipment
              </option>

              {bookableEquipment.map((item) => (

                <option
                  key={item.id}
                  value={item.id}
                >

                  {item.name}

                  {item.laboratory
                    ? ` (${item.laboratory.name})`
                    : ""}

                  {item.status === "BOOKED"
                    ? " - Waiting List Available"
                    : " - Available"}

                </option>

              ))}

            </select>

            {bookableEquipment.length === 0 && (

              <small
                style={{
                  color: "#dc2626",
                  marginTop: "8px",
                }}
              >

                No equipment can currently be booked.

              </small>

            )}

          </div>

          {selectedEquipment && (

            <div
              className="full-width"
              style={{
                border: "1px solid #e5e7eb",
                borderRadius: "12px",
                padding: "20px",
                marginTop: "10px",
                display: "flex",
                gap: "20px",
                alignItems: "flex-start",
                background: "#f9fafb",
              }}
            >

              <img
                src={`http://localhost:8080${selectedEquipment.imageUrl}`}
                alt={selectedEquipment.name}
                style={{
                  width: "220px",
                  height: "160px",
                  objectFit: "cover",
                  borderRadius: "10px",
                  border: "1px solid #ddd",
                }}
              />

              <div style={{ flex: 1 }}>

                <h3
                  style={{
                    marginBottom: "12px",
                    color: "#374151",
                  }}
                >

                  {selectedEquipment.name}

                </h3>

                <p>

                  <strong>Description:</strong>

                </p>

                <p style={{ marginBottom: "15px" }}>

                  {selectedEquipment.description ||
                    "No description available."}

                </p>

                <p>

                  <strong>Specifications:</strong>

                </p>

                <p style={{ marginBottom: "15px" }}>

                  {selectedEquipment.specifications ||
                    "No specifications available."}

                </p>

                {selectedEquipment.laboratory && (

                  <p>

                    <strong>Laboratory:</strong>{" "}

                    {selectedEquipment.laboratory.name}

                  </p>

                )}

                <p>

                  <strong>Cost Per Day:</strong>{" "}

                  ₹
                  {selectedEquipment.costPerDay?.toLocaleString()}

                </p>

                <p>

                  <strong>Status:</strong>{" "}

                  {selectedEquipment.status === "BOOKED"
                    ? "Currently Booked (You will join the Waiting List)"
                    : "Available"}

                </p>

                              </div>

            </div>

          )}

          <div className="form-group">

            <label>

              Start Date

            </label>

            <input
              type="date"
              name="startDate"
              value={bookingData.startDate}
              onChange={handleChange}
              min={today}
              required
            />

          </div>

          <div className="form-group">

            <label>

              End Date

            </label>

            <input
              type="date"
              name="endDate"
              value={bookingData.endDate}
              onChange={handleChange}
              min={bookingData.startDate || today}
              required
            />

          </div>

          {selectedEquipment &&
            estimatedDays > 0 && (

            <div
              className="full-width"
              style={{
                marginTop: "15px",
                background: "#eef6ff",
                border: "1px solid #c7ddff",
                borderRadius: "12px",
                padding: "20px",
              }}
            >

              <h3
                style={{
                  marginBottom: "15px",
                  color: "#2563eb",
                }}
              >

                <FaRupeeSign
                  style={{
                    marginRight: "8px",
                  }}
                />

                Estimated Utilization Cost

              </h3>

              <p>

                <strong>Duration:</strong>{" "}

                {estimatedDays} day
                {estimatedDays > 1 ? "s" : ""}

              </p>

              <p>

                <strong>Cost Per Day:</strong>{" "}

                ₹
                {selectedEquipment.costPerDay?.toLocaleString()}

              </p>

              <h2
                style={{
                  marginTop: "15px",
                  color: "#16a34a",
                }}
              >

                Total Estimated Cost :
                ₹
                {estimatedCost.toLocaleString()}

              </h2>

              {selectedEquipment.status === "BOOKED" && (

                <div
                  style={{
                    marginTop: "18px",
                    padding: "12px",
                    borderRadius: "8px",
                    background: "#fff7ed",
                    border: "1px solid #fdba74",
                    color: "#9a3412",
                    fontWeight: "600",
                  }}
                >

                  ⚠ This equipment is currently booked.

                  <br />

                  Your request will automatically
                  be added to the waiting list.

                </div>

              )}

            </div>

          )}

          <div className="form-group full-width">

            <label>

              Purpose

            </label>

            <textarea
              name="purpose"
              value={bookingData.purpose}
              onChange={handleChange}
              placeholder="Example: AI research experiment, thesis work, hardware testing..."
              required
            />

          </div>

        </div>

        <button
          className="submit-btn"
          type="submit"
          disabled={bookableEquipment.length === 0}
        >

          <FaCalendarCheck
            style={{
              marginRight: "8px",
            }}
          />

          {selectedEquipment?.status === "BOOKED"
            ? "Join Waiting List"
            : "Book Equipment"}

        </button>

      </form>

    </div>

  );

}

export default BookingForm;