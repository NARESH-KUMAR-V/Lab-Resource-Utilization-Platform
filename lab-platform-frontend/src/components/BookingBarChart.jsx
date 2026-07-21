import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid
} from "recharts";

function BookingBarChart({ stats }) {

  const data = [

    {
      status: "Pending",
      value: stats.pendingBookings
    },

    {
      status: "Approved",
      value: stats.approvedBookings
    },

    {
      status: "Rejected",
      value: stats.rejectedBookings
    }

  ];

  return (

    <div className="chart-card">

      <h2>Booking Status</h2>

      <ResponsiveContainer
        width="100%"
        height={320}
      >

        <BarChart data={data}>

          <CartesianGrid strokeDasharray="3 3" />

          <XAxis dataKey="status" />

          <YAxis />

          <Tooltip />

          <Bar
            dataKey="value"
            radius={[10,10,0,0]}
            fill="#6C63FF"
          />

        </BarChart>

      </ResponsiveContainer>

    </div>

  );

}

export default BookingBarChart;