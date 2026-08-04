import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  ResponsiveContainer,
  Legend
} from "recharts";

const COLORS = [
  "#059669", // Emerald Green for Available
  "#1e40af", // Royal Blue for Booked
  "#d97706"  // Amber for Maintenance
];

const CustomTooltip = ({ active, payload }) => {
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
          {payload[0].name}: <span style={{ color: payload[0].color }}>{payload[0].value}</span>
        </p>
      </div>
    );
  }
  return null;
};

function EquipmentPieChart({ stats }) {

  const data = [
    {
      name: "Available",
      value: stats.availableEquipment || 0
    },
    {
      name: "Booked",
      value: stats.bookedEquipment || 0
    },
    {
      name: "Maintenance",
      value: stats.maintenanceEquipment || 0
    }
  ];

  return (
    <div className="chart-card">

      <h2>Equipment Status Distribution</h2>

      <ResponsiveContainer
        width="100%"
        height={280}
      >

        <PieChart>

          <Pie
            data={data}
            dataKey="value"
            nameKey="name"
            outerRadius={100}
            innerRadius={60}
            paddingAngle={4}
            stroke="#ffffff"
            strokeWidth={2}
          >

            {
              data.map((entry, index) => (
                <Cell
                  key={index}
                  fill={COLORS[index % COLORS.length]}
                />
              ))
            }

          </Pie>

          <Tooltip content={<CustomTooltip />} />

          <Legend
            verticalAlign="bottom"
            height={36}
            iconType="circle"
            formatter={(value) => (
              <span style={{ color: "#475569", fontSize: "13px", fontWeight: "500" }}>
                {value}
              </span>
            )}
          />

        </PieChart>

      </ResponsiveContainer>

    </div>
  );
}

export default EquipmentPieChart;