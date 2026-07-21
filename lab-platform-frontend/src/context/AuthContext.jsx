import { createContext, useContext, useState } from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {

  const [token, setToken] = useState(localStorage.getItem("token"));

  const [user, setUser] = useState(
    localStorage.getItem("user")
      ? JSON.parse(localStorage.getItem("user"))
      : null
  );

  const login = (loginResponse) => {

  localStorage.setItem("token", loginResponse.token);
  localStorage.setItem("user", JSON.stringify(loginResponse));

  localStorage.setItem("role", loginResponse.role);

  if (loginResponse.institutionId) {
    localStorage.setItem(
      "institutionId",
      loginResponse.institutionId
    );
  } else {
    localStorage.removeItem("institutionId");
  }

  setToken(loginResponse.token);
  setUser(loginResponse);

};

  const logout = () => {

  localStorage.removeItem("token");
  localStorage.removeItem("user");
  localStorage.removeItem("role");
  localStorage.removeItem("institutionId");

  setToken(null);
  setUser(null);

};

  return (

    <AuthContext.Provider
      value={{
        token,
        user,
        role: user?.role,
        isAuthenticated: !!token,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>

  );

}

export function useAuth() {
  return useContext(AuthContext);
}