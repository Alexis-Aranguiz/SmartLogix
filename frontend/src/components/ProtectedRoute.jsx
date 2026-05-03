import { Navigate } from 'react-router-dom'
import SessionManager from '../singleton/SessionManager'

function ProtectedRoute({ children }) {
  // SINGLETON — verifica si hay sesión activa
  const sesionActiva = SessionManager.getInstance().getSesionActiva()

  if (!sesionActiva) {
    return <Navigate to="/" replace />
  }

  return children
}

export default ProtectedRoute