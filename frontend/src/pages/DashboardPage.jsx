import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import useAuthStore from '../store/authStore'
import '../styles/DashboardPage.css'

function DashboardPage() {
  // OBSERVER — componente suscrito al store (attach() del GoF)
  // Cuando authStore cambia, este componente se actualiza solo
  const usuario = useAuthStore(state => state.usuario)
  const { logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <div className="dashboard-contenedor">
      <h1 className="dashboard-titulo">Bienvenido, {usuario?.nombre}</h1>
      <p className="dashboard-rol">Rol: {usuario?.rol}</p>
      <button className="dashboard-boton-logout" onClick={handleLogout}>
        Cerrar sesión
      </button>
    </div>
  )
}

export default DashboardPage