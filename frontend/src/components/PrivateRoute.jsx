import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function PrivateRoute({ children }) {
  const { user } = useAuth();
  if (!user && !localStorage.getItem('bugforge_token')) {
    return <Navigate to="/login" replace />;
  }
  return children;
}
