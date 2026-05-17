import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import useAuthStore from '../store/authStore'
import SessionManager from '../singleton/SessionManager'

function Navbar() {
  const { logout } = useAuth()
  const usuarioStore = useAuthStore(state => state.usuario)
  const usuario = usuarioStore || SessionManager.getInstance().getUsuario()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  const esAdmin = usuario?.rol === 'ADMIN_PYME'

  return (
    <nav className="navbar">
      <span className="navbar-brand">SmartLogix</span>
      <div className="navbar-links">
        {esAdmin ? (
          <>
            <Link className="nav-link" to="/dashboard">Dashboard</Link>
            <Link className="nav-link" to="/inventario">Inventario</Link>
            <Link className="nav-link" to="/pedidos-admin">Pedidos</Link>
            <Link className="nav-link" to="/envios">Envíos</Link>
            <Link className="nav-link" to="/notificaciones">Notificaciones</Link>
          </>
        ) : (
          <>
            <Link className="nav-link" to="/productos">Productos</Link>
            <Link className="nav-link" to="/mis-pedidos">Mis Pedidos</Link>
          </>
        )}
        <span style={{ color: '#94a3b8', fontSize: '0.85rem' }}>
          {usuario?.nombre} ({usuario?.rol})
        </span>
        <button className="btn btn-danger" onClick={handleLogout}
          style={{ padding: '0.4rem 0.8rem', fontSize: '0.85rem' }}>
          Salir
        </button>
      </div>
    </nav>
  )
}

export default Navbar