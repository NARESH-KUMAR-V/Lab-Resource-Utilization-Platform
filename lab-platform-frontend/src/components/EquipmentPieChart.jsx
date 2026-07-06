import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

const COLORS = ["#4CAF50", "#2196F3", "#FF9800"];

function EquipmentPieChart({ stats }) {
  const data = [
    {
      name: "Available",
      value: stats.availableEquipment,
    },
    {
      name: "Booked",
      value: stats.bookedEquipment,
    },
    {
      name: "Maintenance",
      value: stats.maintenanceEquipment,
    },
  ];

  return (
    <div className="chart-card">
      <h2>Equipment Status</h2>

      <ResponsiveContainer width="100%" height={320}>
        <PieChart>
          <Pie
            data={data}
            dataKey="value"
            outerRadius={110}
            label
          >
            {data.map((entry, index) => (
              <Cell
                key={index}
                fill={COLORS[index]}
              />
            ))}
          </Pie>

          <Tooltip />

          <Legend />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
}

export default EquipmentPieChart;