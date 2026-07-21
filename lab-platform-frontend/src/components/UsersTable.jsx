import "./Table.css";

function UsersTable({ users }) {
  return (
    <div className="table-card">

      <div className="table-header">
        <h2>User List</h2>
        <span>Total : {users.length}</span>
      </div>

      <div className="table-container">

        <table className="data-table">

          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Auth Provider</th>
              <th>Institution</th>
              <th>Laboratory</th>
            </tr>
          </thead>

          <tbody>

            {users.length > 0 ? (

              users.map((user) => (

                <tr key={user.id}>

                  <td>{user.id}</td>

                  <td>{user.name}</td>

                  <td>{user.email}</td>

                  <td>
                    <span className="role-badge">
                      {user.role}
                    </span>
                  </td>

                  <td>
                    <span className="provider-badge">
                      {user.authProvider}
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
                <td colSpan="7" className="no-data">
                  No users found.
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