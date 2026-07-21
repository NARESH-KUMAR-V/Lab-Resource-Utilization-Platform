import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function OAuthSuccessPage() {

  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { login } = useAuth();

  useEffect(() => {

    const token = searchParams.get("token");

    if (!token) {
      navigate("/login", { replace: true });
      return;
    }

    login({
      token,
      id: searchParams.get("id"),
      name: searchParams.get("name"),
      email: searchParams.get("email"),
      role: searchParams.get("role"),
      institutionId: searchParams.get("institutionId") || null,
      laboratoryId: searchParams.get("laboratoryId") || null,
    });

    navigate("/dashboard", { replace: true });

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return <h2>Logging you in...</h2>;
}

export default OAuthSuccessPage;