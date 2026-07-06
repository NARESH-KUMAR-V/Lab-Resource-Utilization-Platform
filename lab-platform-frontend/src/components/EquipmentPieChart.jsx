import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

const COLORS = [
  "#4CAF50",
  "#2196F3",
  "#FF9800",
];

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

  const total =
    stats.availableEquipment +
    stats.bookedEquipment +
    stats.maintenanceEquipment;

  return (

    <div className="chart-card">

      <div className="chart-header">
        <h2>Equipment Status</h2>
        <p>Total Equipment: {total}</p>
      </div>

      <ResponsiveContainer width="100%" height={300}>

        <PieChart>

          <Pie
            data={data}
            dataKey="value"
            nameKey="name"
            cx="50%"
            cy="45%"
            innerRadius={55}
            outerRadius={95}
            paddingAngle={3}
            label={({ percent }) =>
              `${(percent * 100).toFixed(0)}%`
            }
          >

            {data.map((entry, index) => (

              <Cell
                key={index}
                fill={COLORS[index]}
              />

            ))}

          </Pie>

          <Tooltip />

          <Legend
            verticalAlign="bottom"
            height={35}
          />

        </PieChart>

      </ResponsiveContainer>

    </div>

  );

}

export default EquipmentPieChart;