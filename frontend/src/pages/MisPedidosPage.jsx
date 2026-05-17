import { useEffect, useState } from 'react'
import Navbar from '../components/Navbar'
import useAuthStore from '../store/authStore'
import SessionManager from '../singleton/SessionManager'
import { listarPedidos } from '../services/pedidosService'

function MisPedidosPage() {
  const [pedidos, setPedidos] = useState([])
  const [error, setError] = useState('')
  const usuarioStore = useAuthStore(state => state.usuario)
  const usuario = usuarioStore || SessionManager.getInstance().getUsuario()

  useEffect(() => {
    listarPedidos()
      .then(data => {
        const misPedidos = data.filter(p => p.clienteEmail === usuario?.email)
        setPedidos(misPedidos)
      })
      .catch(() => setError('Error al cargar pedidos'))
  }, [])

  const getBadge = (estado) => {
    const map = {
      CREADO: 'badge-info', CONFIRMADO: 'badge-success',
      CANCELADO: 'badge-danger', ENVIADO: 'badge-warning'
    }
    return map[estado] || 'badge-info'
  }

  return (
    <>
      <Navbar />
      <div className="page">
        <h2 style={{ color: '#38bdf8', marginBottom: '1.5rem' }}>Mis Pedidos</h2>
        {error && <div className="alert alert-error">{error}</div>}
        {pedidos.length === 0
          ? <div className="card"><p style={{ color: '#94a3b8' }}>No tienes pedidos aún.</p></div>
          : pedidos.map(p => (
            <div className="card" key={p.id}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <h3>Pedido #{p.numeroPedido}</h3>
                  <p style={{ color: '#94a3b8', marginTop: '0.3rem' }}>
                    Total: <strong style={{ color: '#4ade80' }}>${p.total?.toLocaleString()}</strong>
                  </p>
                  <p style={{ color: '#94a3b8', fontSize: '0.85rem' }}>
                    Pago: {p.codigoTransaccion || 'N/A'}
                  </p>
                </div>
                <span className={`badge ${getBadge(p.estado)}`}>{p.estado}</span>
              </div>
            </div>
          ))
        }
      </div>
    </>
  )
}

export default MisPedidosPage