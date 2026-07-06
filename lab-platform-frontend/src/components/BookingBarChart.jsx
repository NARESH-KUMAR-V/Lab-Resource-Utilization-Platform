import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer
} from "recharts";

function BookingBarChart({ stats }) {

    const data = [

        {
            status: "Approved",
            count: stats.approvedBookings
        },

        {
            status: "Pending",
            count: stats.pendingBookings
        },

        {
            status: "Rejected",
            count: stats.rejectedBookings
        }

    ];

    return (

        <div className="chart-card">

            <h2>Booking Status</h2>

            <ResponsiveContainer width="100%" height={320}>

                <BarChart data={data}>

                    <CartesianGrid strokeDasharray="3 3"/>

                    <XAxis dataKey="status"/>

                    <YAxis/>

                    <Tooltip/>

                    <Bar
                        dataKey="count"
                        fill="#1976d2"
                    />

                </BarChart>

            </ResponsiveContainer>

        </div>

    );

}

export default BookingBarChart;