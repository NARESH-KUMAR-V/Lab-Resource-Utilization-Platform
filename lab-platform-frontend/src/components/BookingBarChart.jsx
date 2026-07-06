import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Cell,
} from "recharts";

const COLORS = [
  "#2E7D32",
  "#FB8C00",
  "#D32F2F",
];

function BookingBarChart({ stats }) {

  const data = [

    {
      status: "Approved",
      count: stats.approvedBookings,
    },

    {
      status: "Pending",
      count: stats.pendingBookings,
    },

    {
      status: "Rejected",
      count: stats.rejectedBookings,
    },

  ];

  return (

    <div className="chart-card">

      <div className="chart-header">

        <h2>Booking Status</h2>

        <p>Overview of booking requests</p>

      </div>

      <ResponsiveContainer
        width="100%"
        height={300}
      >

        <BarChart
          data={data}
          margin={{
            top: 10,
            right: 20,
            left: 0,
            bottom: 10,
          }}
        >

          <CartesianGrid
            strokeDasharray="3 3"
            vertical={false}
          />

          <XAxis dataKey="status" />

          <YAxis allowDecimals={false} />

          <Tooltip />

          <Bar
            dataKey="count"
            radius={[8, 8, 0, 0]}
          >

            {data.map((entry, index) => (

              <Cell
                key={index}
                fill={COLORS[index]}
              />

            ))}

          </Bar>

        </BarChart>

      </ResponsiveContainer>

    </div>

  );

}

export default BookingBarChart;