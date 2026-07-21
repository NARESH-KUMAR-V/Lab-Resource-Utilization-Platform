import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  ResponsiveContainer,
  Legend
} from "recharts";

const COLORS = [
  "#6C63FF",
  "#00C49F",
  "#FF8042"
];

function EquipmentPieChart({ stats }) {

  const data = [
    {
      name: "Available",
      value: stats.availableEquipment
    },
    {
      name: "Booked",
      value: stats.bookedEquipment
    },
    {
      name: "Maintenance",
      value: stats.maintenanceEquipment
    }
  ];

  return (
    <div className="chart-card">

      <h2>Equipment Status</h2>

      <ResponsiveContainer
        width="100%"
        height={320}
      >

        <PieChart>

          <Pie
            data={data}
            dataKey="value"
            nameKey="name"
            outerRadius={110}
            innerRadius={55}
            paddingAngle={5}
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

          <Tooltip />

          <Legend />

        </PieChart>

      </ResponsiveContainer>

    </div>
  );
}

export default EquipmentPieChart;