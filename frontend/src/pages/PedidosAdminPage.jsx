import { useEffect, useState } from 'react'
import Navbar from '../components/Navbar'
import { listarPedidos, actualizarEstadoPedido } from '../services/pedidosService'

function PedidosAdminPage() {
  const [pedidos, setPedidos] = useState([])
  const [error, setError] = useState('')
  const [mensaje, setMensaje] = useState('')

  const cargar = () => listarPedidos().then(setPedidos).catch(() => setError('Error al cargar'))

  useEffect(() => { cargar() }, [])

  const handleEstado = async (id, estado) => {
    try {
      await actualizarEstadoPedido(id, estado)
      setMensaje('Estado actualizado')
      cargar()
      setTimeout(() => setMensaje(''), 3000)
    } catch (err) { setError('Error al actualizar') }
  }

  const getBadge = (estado) => {
    const map = { CREADO: 'badge-info', CONFIRMADO: 'badge-success', CANCELADO: 'badge-danger', ENVIADO: 'badge-warning' }
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
              <tr><th>#Pedido</th><th>Cliente</th><th>Total</th><th>Estado</th><th>Acciones</th></tr>
            </thead>
            <tbody>
              {pedidos.map(p => (
                <tr key={p.id}>
                  <td>{p.numeroPedido}</td>
                  <td>{p.clienteEmail}</td>
                  <td>${p.total?.toLocaleString()}</td>
                  <td><span className={`badge ${getBadge(p.estado)}`}>{p.estado}</span></td>
                  <td style={{ display: 'flex', gap: '0.5rem' }}>
                    <button className="btn btn-success" style={{ padding: '0.3rem 0.6rem', fontSize: '0.8rem' }}
                      onClick={() => handleEstado(p.id, 'CONFIRMADO')}>Confirmar</button>
                    <button className="btn btn-warning" style={{ padding: '0.3rem 0.6rem', fontSize: '0.8rem' }}
                      onClick={() => handleEstado(p.id, 'ENVIADO')}>Enviar</button>
                    <button className="btn btn-danger" style={{ padding: '0.3rem 0.6rem', fontSize: '0.8rem' }}
                      onClick={() => handleEstado(p.id, 'CANCELADO')}>Cancelar</button>
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