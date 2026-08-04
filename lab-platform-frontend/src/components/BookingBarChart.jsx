import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
  Cell
} from "recharts";

const BAR_COLORS = {
  Pending: "#d97706",  // Amber
  Approved: "#059669", // Emerald Green
  Rejected: "#dc2626"  // Danger Red
};

const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    return (
      <div style={{
        background: "#0f172a",
        border: "1px solid #334155",
        borderRadius: "8px",
        padding: "8px 14px",
        color: "#ffffff",
        fontSize: "12px",
        fontWeight: "600",
        boxShadow: "0 4px 12px rgba(0,0,0,0.15)"
      }}>
        <p style={{ margin: 0 }}>
          {label}: <span style={{ color: payload[0].payload.fill }}>{payload[0].value}</span>
        </p>
      </div>
    );
  }
  return null;
};

function BookingBarChart({ stats }) {

  const data = [
    {
      status: "Pending",
      value: stats.pendingBookings || 0,
      fill: BAR_COLORS.Pending
    },
    {
      status: "Approved",
      value: stats.approvedBookings || 0,
      fill: BAR_COLORS.Approved
    },
    {
      status: "Rejected",
      value: stats.rejectedBookings || 0,
      fill: BAR_COLORS.Rejected
    }
  ];

  return (

    <div className="chart-card">

      <h2>Booking Status Overview</h2>

      <ResponsiveContainer
        width="100%"
        height={280}
      >

        <BarChart data={data} margin={{ top: 15, right: 15, left: -20, bottom: 0 }}>

          <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" vertical={false} />

          <XAxis
            dataKey="status"
            tick={{ fill: "#64748b", fontSize: 12, fontWeight: 500 }}
            axisLine={{ stroke: "#cbd5e1" }}
            tickLine={false}
          />

          <YAxis
            tick={{ fill: "#64748b", fontSize: 12 }}
            axisLine={false}
            tickLine={false}
            allowDecimals={false}
          />

          <Tooltip content={<CustomTooltip />} />

          <Bar
            dataKey="value"
            radius={[6, 6, 0, 0]}
            barSize={48}
          >
            {data.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={entry.fill} />
            ))}
          </Bar>

        </BarChart>

      </ResponsiveContainer>

    </div>

  );

}

export default BookingBarChart;