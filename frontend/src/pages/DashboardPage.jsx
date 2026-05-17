import Navbar from '../components/Navbar'
import useAuthStore from '../store/authStore'

function DashboardPage() {
  const usuario = useAuthStore(state => state.usuario)

  return (
    <>
      <Navbar />
      <div className="page">
        <h1 style={{ color: '#38bdf8', marginBottom: '1.5rem' }}>
          Bienvenido, {usuario?.nombre}
        </h1>
        <div className="grid-3">
          <div className="card">
            <h3 style={{ color: '#38bdf8' }}>Inventario</h3>
            <p style={{ color: '#94a3b8', marginTop: '0.5rem' }}>
              Gestiona productos y stock
            </p>
          </div>
          <div className="card">
            <h3 style={{ color: '#4ade80' }}>Pedidos</h3>
            <p style={{ color: '#94a3b8', marginTop: '0.5rem' }}>
              Administra los pedidos
            </p>
          </div>
          <div className="card">
            <h3 style={{ color: '#fbbf24' }}>Envíos</h3>
            <p style={{ color: '#94a3b8', marginTop: '0.5rem' }}>
              Seguimiento de envíos
            </p>
          </div>
        </div>
      </div>
    </>
  )
}

export default DashboardPage