import { useState } from "react";
import { FaSearch } from "react-icons/fa";
import "./Table.css";

function UsersTable({ users }) {
  const [search, setSearch] = useState("");

  const filteredUsers = users.filter((u) =>
    u.name?.toLowerCase().includes(search.toLowerCase()) ||
    u.email?.toLowerCase().includes(search.toLowerCase()) ||
    u.role?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="table-card">

      <div className="table-header">
        <h2>User Directory</h2>
        
        <div className="search-wrapper">
          <FaSearch />
          <input
            type="text"
            placeholder="Search by name, email, or role..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
      </div>

      <div className="table-container">

        <table className="data-table">

          <thead>
            <tr>
              <th>ID</th>
              <th>Full Name</th>
              <th>Email Address</th>
              <th>Role</th>
              <th>Auth Provider</th>
              <th>Institution</th>
              <th>Laboratory</th>
            </tr>
          </thead>

          <tbody>

            {filteredUsers.length > 0 ? (

              filteredUsers.map((user) => (

                <tr key={user.id}>

                  <td><strong>#{user.id}</strong></td>

                  <td><strong>{user.name}</strong></td>

                  <td>{user.email}</td>

                  <td>
                    <span className="role-badge">
                      {user.role}
                    </span>
                  </td>

                  <td>
                    <span className="provider-badge">
                      {user.authProvider || "LOCAL"}
                    </span>
                  </td>

                  <td>
                    {user.institution
                      ? user.institution.name
                      : "-"}
                  </td>

                  <td>
                    {user.laboratory
                      ? user.laboratory.name
                      : "-"}
                  </td>

                </tr>

              ))

            ) : (

              <tr>
                <td colSpan="7" className="empty-table">
                  👥 No users found matching your search.
                </td>
              </tr>

            )}

          </tbody>

        </table>

      </div>

    </div>
  );
}

export default UsersTable;