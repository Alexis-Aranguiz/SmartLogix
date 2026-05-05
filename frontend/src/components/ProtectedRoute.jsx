import { Navigate } from 'react-router-dom'
import SessionManager from '../singleton/SessionManager'

function ProtectedRoute({ children }) {
  const sesionActiva = SessionManager.getInstance().getSesionActiva()

  if (!sesionActiva) {
    return <Navigate to="/" replace />
  }

  return children
}

export default ProtectedRoute