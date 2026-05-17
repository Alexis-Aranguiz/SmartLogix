import { useEffect, useState } from 'react'
import Navbar from '../components/Navbar'
import { listarPedidos, actualizarEstadoPedido, cancelarPedido } from '../services/pedidosService'
import { crearEnvio } from '../services/enviosService'

function PedidosAdminPage() {
  const [pedidos, setPedidos] = useState([])
  const [error, setError] = useState('')
  const [mensaje, setMensaje] = useState('')

  const cargar = () => listarPedidos().then(setPedidos).catch(() => setError('Error al cargar'))

  useEffect(() => { cargar() }, [])

  const handleEstado = async (id, estado) => {
  try {
    await actualizarEstadoPedido(id, estado)

    if (estado === 'CONFIRMADO') {
      const pedido = pedidos.find(p => p.id === id)
      try {
        await crearEnvio({
          pedidoId: id,
          direccionDestino: pedido?.clienteEmail
            ? `Envío para ${pedido.clienteEmail}`
            : 'Dirección por confirmar',
          transportistaId: null
        })
        setMensaje('Pedido confirmado — envío creado automáticamente')
      } catch (e) {
        setMensaje('Pedido confirmado — error al crear envío')
      }
    } else {
      setMensaje('Estado actualizado')
    }

    cargar()
    setTimeout(() => setMensaje(''), 3000)
  } catch (err) {
    setError('Error al actualizar')
  }
}

  const handleCancelar = async (id) => {
    if (window.confirm('¿Cancelar este pedido? Se devolverá el stock.')) {
      try {
        await cancelarPedido(id)
        setMensaje('Pedido cancelado — stock devuelto')
        cargar()
        setTimeout(() => setMensaje(''), 3000)
      } catch (err) { setError('Error al cancelar') }
    }
  }

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
        <h2 style={{ color: '#38bdf8', marginBottom: '1.5rem' }}>Gestión de Pedidos</h2>
        {error && <div className="alert alert-error">{error}</div>}
        {mensaje && <div className="alert alert-success">{mensaje}</div>}
        <div className="card">
          <table>
            <thead>
              <tr>
                <th>#Pedido</th>
                <th>Cliente</th>
                <th>Productos</th>
                <th>Total</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {pedidos.map(p => (
                <tr key={p.id}>
                  <td style={{ fontFamily: 'monospace', color: '#38bdf8' }}>
                    {p.numeroPedido}
                  </td>
                  <td style={{ color: '#94a3b8', fontSize: '0.85rem' }}>
                    {p.clienteEmail}
                  </td>
                  <td>
                    {p.items?.map((item, i) => (
                      <div key={i} style={{ fontSize: '0.85rem', color: '#f1f5f9' }}>
                        {item.nombreProducto} x{item.cantidad}
                        <span style={{ color: '#94a3b8', marginLeft: '0.3rem' }}>
                          (${item.precioUnitario?.toLocaleString()})
                        </span>
                      </div>
                    )) || <span style={{ color: '#94a3b8' }}>Sin items</span>}
                  </td>
                  <td style={{ color: '#4ade80', fontWeight: 'bold' }}>
                    ${p.total?.toLocaleString()}
                  </td>
                  <td>
                    <span className={`badge ${getBadge(p.estado)}`}>{p.estado}</span>
                  </td>
                  <td style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                    <button className="btn btn-success"
                      style={{ padding: '0.3rem 0.6rem', fontSize: '0.8rem' }}
                      onClick={() => handleEstado(p.id, 'CONFIRMADO')}>
                      Confirmar
                    </button>
                    <button className="btn btn-warning"
                      style={{ padding: '0.3rem 0.6rem', fontSize: '0.8rem' }}
                      onClick={() => handleEstado(p.id, 'ENVIADO')}>
                      Enviar
                    </button>
                    <button className="btn btn-danger"
                      style={{ padding: '0.3rem 0.6rem', fontSize: '0.8rem' }}
                      onClick={() => handleCancelar(p.id)}>
                      Cancelar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </>
  )
}

export default PedidosAdminPage