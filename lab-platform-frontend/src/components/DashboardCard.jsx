import "./DashboardCard.css";

function DashboardCard({ title, value, icon }) {
  return (
    <div className="dashboard-card">

      <div className="dashboard-card-icon">
        {icon}
      </div>

      <div className="dashboard-card-content">

        <p>{title}</p>

        <h2>{value}</h2>

      </div>

    </div>
  );
}

export default DashboardCard;