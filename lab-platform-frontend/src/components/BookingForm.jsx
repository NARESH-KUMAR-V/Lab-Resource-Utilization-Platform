import "./Form.css";

function BookingForm({
  equipment,
  bookingData,
  handleChange,
  handleSubmit,
}) {
  return (
    <div className="form-card">

      <div className="form-header">
        <h2>Book Equipment</h2>
        <p>Fill in the details below to create a laboratory booking.</p>
      </div>

      <form onSubmit={handleSubmit}>

        <div className="form-grid">

          <div className="form-group">
            <label>Equipment</label>

            <select
              name="equipmentId"
              value={bookingData.equipmentId}
              onChange={handleChange}
              required
            >
              <option value="">Select Equipment</option>

              {equipment.map((item) => (
                <option
                  key={item.id}
                  value={item.id}
                >
                  {item.name}
                </option>
              ))}

            </select>
          </div>

          <div className="form-group">
            <label>Booking Date</label>

            <input
              type="date"
              name="bookingDate"
              value={bookingData.bookingDate}
              onChange={handleChange}
              required
            />
          </div>

          <div
            className="form-group"
            style={{ gridColumn: "1 / -1" }}
          >
            <label>Purpose</label>

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
        >
          📅 Book Equipment
        </button>

      </form>

    </div>
  );
}

export default BookingForm;