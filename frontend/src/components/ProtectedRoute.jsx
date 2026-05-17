import { Navigate } from 'react-router-dom'
import SessionManager from '../singleton/SessionManager'
import useAuthStore from '../store/authStore'

function ProtectedRoute({ children, roles }) {
  const token = SessionManager.getInstance().getToken()
  const usuario = useAuthStore(state => state.usuario) ||
    SessionManager.getInstance().getUsuario()

  if (!token) return <Navigate to="/" replace />

  if (roles && !roles.includes(usuario?.rol)) {
    return <Navigate to="/productos" replace />
  }

  return children
}

export default ProtectedRoute