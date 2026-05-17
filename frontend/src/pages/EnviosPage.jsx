import { useEffect, useState } from 'react'
import Navbar from '../components/Navbar'
import { listarEnvios, actualizarEstadoEnvio } from '../services/enviosService'

function EnviosPage() {
  const [envios, setEnvios] = useState([])
  const [error, setError] = useState('')
  const [mensaje, setMensaje] = useState('')

  const cargar = () => listarEnvios().then(setEnvios).catch(() => setError('Error al cargar envíos'))

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
      ENTREGADO: 'badge-success', FALLIDO: 'badge-danger', CANCELADO: 'badge-danger'
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
              <tr><th>Tracking</th><th>Pedido ID</th><th>Dirección</th><th>Estado</th><th>Acciones</th></tr>
            </thead>
            <tbody>
              {envios.map(e => (
                <tr key={e.id}>
                  <td>{e.trackingNumber}</td>
                  <td>{e.pedidoId}</td>
                  <td>{e.direccionDestino || 'N/A'}</td>
                  <td><span className={`badge ${getBadge(e.estado)}`}>{e.estado}</span></td>
                  <td style={{ display: 'flex', gap: '0.5rem' }}>
                    <button className="btn btn-warning" style={{ padding: '0.3rem 0.6rem', fontSize: '0.8rem' }}
                      onClick={() => handleEstado(e.id, 'EN_CAMINO')}>En camino</button>
                    <button className="btn btn-success" style={{ padding: '0.3rem 0.6rem', fontSize: '0.8rem' }}
                      onClick={() => handleEstado(e.id, 'ENTREGADO')}>Entregado</button>
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

export default EnviosPage