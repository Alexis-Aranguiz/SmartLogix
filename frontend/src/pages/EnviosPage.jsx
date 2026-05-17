import { useEffect, useState } from 'react'
import Navbar from '../components/Navbar'
import { listarEnvios, actualizarEstadoEnvio } from '../services/enviosService'
import { obtenerPedido } from '../services/pedidosService'

function EnviosPage() {
  const [envios, setEnvios] = useState([])
  const [pedidosMap, setPedidosMap] = useState({})
  const [error, setError] = useState('')
  const [mensaje, setMensaje] = useState('')

  const cargar = async () => {
    try {
      const data = await listarEnvios()
      setEnvios(data)

      // Obtener detalles de cada pedido
      const pedidosInfo = {}
      await Promise.all(data.map(async (envio) => {
        try {
          const pedido = await obtenerPedido(envio.pedidoId)
          pedidosInfo[envio.pedidoId] = pedido
        } catch (e) {
          pedidosInfo[envio.pedidoId] = null
        }
      }))
      setPedidosMap(pedidosInfo)
    } catch (err) {
      setError('Error al cargar envíos')
    }
  }

  useEffect(() => { cargar() }, [])

  const handleEstado = async (id, estado) => {
    try {
      await actualizarEstadoEnvio(id, estado)
      setMensaje('Estado actualizado')
      cargar()
      setTimeout(() => setMensaje(''), 3000)
    } catch (err) { setError('Error al actualizar') }
  }

  const getBadge = (estado) => {
    const map = {
      PENDIENTE: 'badge-info', PROCESANDO: 'badge-warning',
      DESPACHADO: 'badge-info', EN_CAMINO: 'badge-warning',
      ENTREGADO: 'badge-success', FALLIDO: 'badge-danger',
      CANCELADO: 'badge-danger'
    }
    return map[estado] || 'badge-info'
  }

  return (
    <>
      <Navbar />
      <div className="page">
        <h2 style={{ color: '#38bdf8', marginBottom: '1.5rem' }}>Gestión de Envíos</h2>
        {error && <div className="alert alert-error">{error}</div>}
        {mensaje && <div className="alert alert-success">{mensaje}</div>}
        <div className="card">
          <table>
            <thead>
              <tr>
                <th>Tracking</th>
                <th>#Pedido</th>
                <th>Cliente</th>
                <th>Productos</th>
                <th>Total</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {envios.map(e => {
                const pedido = pedidosMap[e.pedidoId]
                return (
                  <tr key={e.id}>
                    <td style={{ fontFamily: 'monospace', color: '#38bdf8' }}>
                      {e.trackingNumber}
                    </td>
                    <td style={{ color: '#94a3b8', fontSize: '0.85rem' }}>
                      {pedido?.numeroPedido || e.pedidoId}
                    </td>
                    <td style={{ color: '#94a3b8', fontSize: '0.85rem' }}>
                      {pedido?.clienteEmail || 'N/A'}
                    </td>
                    <td>
                      {pedido?.items?.map((item, i) => (
                        <div key={i} style={{ fontSize: '0.85rem', color: '#f1f5f9' }}>
                          {item.nombreProducto} x{item.cantidad}
                        </div>
                      )) || <span style={{ color: '#94a3b8' }}>N/A</span>}
                    </td>
                    <td style={{ color: '#4ade80' }}>
                      ${pedido?.total?.toLocaleString() || 'N/A'}
                    </td>
                    <td>
                      <span className={`badge ${getBadge(e.estado)}`}>
                        {e.estado}
                      </span>
                    </td>
                    <td style={{ display: 'flex', gap: '0.5rem' }}>
                      <button className="btn btn-warning"
                        style={{ padding: '0.3rem 0.6rem', fontSize: '0.8rem' }}
                        onClick={() => handleEstado(e.id, 'EN_CAMINO')}>
                        En camino
                      </button>
                      <button className="btn btn-success"
                        style={{ padding: '0.3rem 0.6rem', fontSize: '0.8rem' }}
                        onClick={() => handleEstado(e.id, 'ENTREGADO')}>
                        Entregado
                      </button>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      </div>
    </>
  )
}

export default EnviosPage