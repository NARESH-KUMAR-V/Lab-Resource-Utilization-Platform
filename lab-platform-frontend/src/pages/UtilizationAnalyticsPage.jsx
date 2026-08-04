import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import Layout from "../components/Layout";
import api from "../api/axios";
import "./Dashboard.css";
import "./UtilizationAnalytics.css";

import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  Cell,
} from "recharts";

function UtilizationAnalyticsPage() {
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState("overview");
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      setLoading(true);
      const response = await api.get("/utilization/stats/dashboard");
      setDashboard(response.data);
    } catch (error) {
      console.error(error);
      toast.error("Failed to load utilization analytics.");
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = async () => {
    try {
      setRefreshing(true);
      await api.post("/utilization/stats/refresh");
      toast.success("Statistics refreshed.");
      await loadDashboard();
    } catch (error) {
      toast.error("Refresh failed. Make sure you have SYSTEM_ADMIN role.");
    } finally {
      setRefreshing(false);
    }
  };

  const getTierColor = (tier) => {
    if (tier === "HIGH") return "#16a34a";
    if (tier === "MEDIUM") return "#d97706";
    return "#dc2626";
  };

  const getTierClass = (tier) => {
    if (tier === "HIGH") return "tier-high";
    if (tier === "MEDIUM") return "tier-medium";
    return "tier-low";
  };

  if (loading) {
    return (
      <Layout>
        <div className="dashboard">
          <div className="loading-state">⏳ Loading utilization analytics...</div>
        </div>
      </Layout>
    );
  }

  if (!dashboard) return null;

  const rankingChartData = (dashboard.utilizationRanking || [])
    .slice(0, 10)
    .map((item) => ({
      name: item.equipmentName.length > 14
        ? item.equipmentName.slice(0, 14) + "…"
        : item.equipmentName,
      "Utilization %": item.utilizationPercentage,
      Bookings: item.totalBookings,
      tier: item.utilizationTier,
    }));

  return (
    <Layout>
      <div className="dashboard utilization-page">

        {/* Header */}
        <div className="util-header">
          <div>
            <h1>📊 Utilization Analytics</h1>
            <p>Real-time equipment usage insights and performance rankings.</p>
          </div>
          <button
            className="refresh-btn"
            onClick={handleRefresh}
            disabled={refreshing}
          >
            {refreshing ? "⏳ Refreshing..." : "🔄 Refresh Stats"}
          </button>
        </div>

        {/* KPI Cards */}
        <div className="dashboard-container">

          <div className="dashboard-card kpi-card">
            <div className="card-content">
              <h4>Total Equipment</h4>
              <h2>{dashboard.totalEquipmentCount}</h2>
            </div>
            <div className="kpi-icon">🏭</div>
          </div>

          <div className="dashboard-card kpi-card kpi-green">
            <div className="card-content">
              <h4>Avg Utilization</h4>
              <h2>{dashboard.averageUtilizationPercentage?.toFixed(1)}%</h2>
            </div>
            <div className="kpi-icon">📈</div>
          </div>

          <div className="dashboard-card kpi-card kpi-blue">
            <div className="card-content">
              <h4>Total Usage Hours</h4>
              <h2>{dashboard.totalUsageHours?.toFixed(1)}</h2>
            </div>
            <div className="kpi-icon">⏱️</div>
          </div>

          <div className="dashboard-card kpi-card kpi-red">
            <div className="card-content">
              <h4>Idle Equipment</h4>
              <h2>{dashboard.idleEquipmentCount}</h2>
              <small>idle &gt; 30 days</small>
            </div>
            <div className="kpi-icon">💤</div>
          </div>

          <div className="dashboard-card kpi-card kpi-purple">
            <div className="card-content">
              <h4>High Utilization</h4>
              <h2>{dashboard.highlyUtilizedCount}</h2>
              <small>≥ 70%</small>
            </div>
            <div className="kpi-icon">🔥</div>
          </div>

          <div className="dashboard-card kpi-card kpi-orange">
            <div className="card-content">
              <h4>Low Utilization</h4>
              <h2>{dashboard.lowUtilizedCount}</h2>
              <small>&lt; 30%</small>
            </div>
            <div className="kpi-icon">🌡️</div>
          </div>

        </div>

        {/* Tabs */}
        <div className="util-tabs">
          <button
            className={`tab-btn ${activeTab === "overview" ? "active" : ""}`}
            onClick={() => setActiveTab("overview")}
          >
            📊 Chart View
          </button>
          <button
            className={`tab-btn ${activeTab === "ranking" ? "active" : ""}`}
            onClick={() => setActiveTab("ranking")}
          >
            🏆 Rankings
          </button>
          <button
            className={`tab-btn ${activeTab === "most" ? "active" : ""}`}
            onClick={() => setActiveTab("most")}
          >
            🔝 Most Used
          </button>
          <button
            className={`tab-btn ${activeTab === "least" ? "active" : ""}`}
            onClick={() => setActiveTab("least")}
          >
            🔻 Least Used
          </button>
        </div>

        {/* Chart View */}
        {activeTab === "overview" && (
          <div className="util-chart-section">

            <div className="chart-card">
              <h3>Equipment Utilization % (Top 10)</h3>
              <ResponsiveContainer width="100%" height={360}>
                <BarChart data={rankingChartData} margin={{ top: 15, right: 20, left: -10, bottom: 95 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" vertical={false} />
                  <XAxis
                    dataKey="name"
                    tick={{ fill: "#475569", fontSize: 11, fontWeight: 600 }}
                    angle={-45}
                    textAnchor="end"
                    interval={0}
                    dy={8}
                    axisLine={{ stroke: "#cbd5e1" }}
                    tickLine={false}
                  />
                  <YAxis
                    tick={{ fill: "#475569", fontSize: 12 }}
                    domain={[0, 100]}
                    axisLine={false}
                    tickLine={false}
                  />
                  <Tooltip
                    contentStyle={{ background: "#0f172a", border: "1px solid #334155", borderRadius: "8px", color: "#ffffff", fontSize: "12px", boxShadow: "0 4px 12px rgba(0,0,0,0.15)" }}
                  />
                  <Bar dataKey="Utilization %" radius={[6, 6, 0, 0]} barSize={32}>
                    {rankingChartData.map((entry, index) => (
                      <Cell key={index} fill={getTierColor(entry.tier)} />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>

            <div className="chart-card">
              <h3>Total Bookings per Equipment (Top 10)</h3>
              <ResponsiveContainer width="100%" height={360}>
                <BarChart data={rankingChartData} margin={{ top: 15, right: 20, left: -10, bottom: 95 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" vertical={false} />
                  <XAxis
                    dataKey="name"
                    tick={{ fill: "#475569", fontSize: 11, fontWeight: 600 }}
                    angle={-45}
                    textAnchor="end"
                    interval={0}
                    dy={8}
                    axisLine={{ stroke: "#cbd5e1" }}
                    tickLine={false}
                  />
                  <YAxis
                    tick={{ fill: "#475569", fontSize: 12 }}
                    axisLine={false}
                    tickLine={false}
                    allowDecimals={false}
                  />
                  <Tooltip
                    contentStyle={{ background: "#0f172a", border: "1px solid #334155", borderRadius: "8px", color: "#ffffff", fontSize: "12px", boxShadow: "0 4px 12px rgba(0,0,0,0.15)" }}
                  />
                  <Legend verticalAlign="top" align="right" wrapperStyle={{ paddingBottom: "12px", fontSize: "12px", color: "var(--color-text-main)" }} />
                  <Bar dataKey="Bookings" fill="#6366f1" radius={[6, 6, 0, 0]} barSize={32} />
                </BarChart>
              </ResponsiveContainer>
            </div>

          </div>
        )}

        {/* Rankings Table */}
        {activeTab === "ranking" && (
          <div className="util-table-section">
            <h3>📊 Equipment Utilization Ranking</h3>
            <div className="table-card">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Equipment</th>
                    <th>Laboratory</th>
                    <th>Bookings</th>
                    <th>Usage Hours</th>
                    <th>Usage Days</th>
                    <th>Utilization %</th>
                    <th>Last Used</th>
                    <th>Idle Days</th>
                    <th>Avg/Month</th>
                    <th>Tier</th>
                  </tr>
                </thead>
                <tbody>
                  {(dashboard.utilizationRanking || []).map((item, i) => (
                    <tr key={item.equipmentId}>
                      <td>{i + 1}</td>
                      <td><strong>{item.equipmentName}</strong></td>
                      <td>{item.laboratoryName || "-"}</td>
                      <td>{item.totalBookings}</td>
                      <td>{item.totalUsageHours?.toFixed(1)}</td>
                      <td>{item.totalUsageDays}</td>
                      <td>
                        <div className="util-bar-wrapper">
                          <div
                            className="util-bar"
                            style={{
                              width: `${Math.min(item.utilizationPercentage, 100)}%`,
                              background: getTierColor(item.utilizationTier),
                            }}
                          />
                          <span>{item.utilizationPercentage?.toFixed(1)}%</span>
                        </div>
                      </td>
                      <td>{item.lastUsedDate || "Never"}</td>
                      <td>{item.idleDays}</td>
                      <td>{item.avgUsagePerMonth?.toFixed(1)} days</td>
                      <td>
                        <span className={`tier-badge ${getTierClass(item.utilizationTier)}`}>
                          {item.utilizationTier}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Most Used */}
        {activeTab === "most" && (
          <div className="util-table-section">
            <h3>🔝 Most Used Equipment (Top 5 by Bookings)</h3>
            <div className="util-card-grid">
              {(dashboard.mostUsedEquipment || []).map((item, i) => (
                <div className="util-stat-card" key={item.equipmentId}>
                  <div className="util-rank">#{i + 1}</div>
                  <h4>{item.equipmentName}</h4>
                  <p className="util-lab">{item.laboratoryName || "-"}</p>
                  <div className="util-metrics">
                    <div><span>📅 Bookings</span><strong>{item.totalBookings}</strong></div>
                    <div><span>⏱ Hours</span><strong>{item.totalUsageHours?.toFixed(1)}</strong></div>
                    <div><span>📆 Days</span><strong>{item.totalUsageDays}</strong></div>
                    <div><span>📊 Util%</span><strong>{item.utilizationPercentage?.toFixed(1)}%</strong></div>
                  </div>
                  <span className={`tier-badge ${getTierClass(item.utilizationTier)}`}>
                    {item.utilizationTier}
                  </span>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Least Used */}
        {activeTab === "least" && (
          <div className="util-table-section">
            <h3>🔻 Least Utilized Equipment (Bottom 5)</h3>
            <div className="util-card-grid">
              {(dashboard.leastUsedEquipment || []).map((item, i) => (
                <div className="util-stat-card util-idle" key={item.equipmentId}>
                  <div className="util-rank">#{i + 1}</div>
                  <h4>{item.equipmentName}</h4>
                  <p className="util-lab">{item.laboratoryName || "-"}</p>
                  <div className="util-metrics">
                    <div><span>📅 Bookings</span><strong>{item.totalBookings}</strong></div>
                    <div><span>⏱ Hours</span><strong>{item.totalUsageHours?.toFixed(1)}</strong></div>
                    <div><span>💤 Idle Days</span><strong style={{ color: "var(--color-danger)" }}>{item.idleDays}</strong></div>
                    <div><span>📊 Util%</span><strong>{item.utilizationPercentage?.toFixed(1)}%</strong></div>
                  </div>
                  <span className={`tier-badge ${getTierClass(item.utilizationTier)}`}>
                    {item.utilizationTier}
                  </span>
                </div>
              ))}
            </div>
          </div>
        )}

      </div>
    </Layout>
  );
}

export default UtilizationAnalyticsPage;
