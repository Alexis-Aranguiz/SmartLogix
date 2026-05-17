import { useEffect, useState } from 'react'
import Navbar from '../components/Navbar'
import { listarNotificaciones } from '../services/notificacionesService'

function NotificacionesPage() {
  const [notificaciones, setNotificaciones] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    listarNotificaciones().then(setNotificaciones).catch(() => setError('Error al cargar'))
  }, [])

  const getBadge = (tipo) => {
    const map = { EMAIL: 'badge-info', SMS: 'badge-warning', PUSH: 'badge-success' }
    return map[tipo] || 'badge-info'
  }

  return (
    <>
      <Navbar />
      <div className="page">
        <h2 style={{ color: '#38bdf8', marginBottom: '1.5rem' }}>Notificaciones Enviadas</h2>
        {error && <div className="alert alert-error">{error}</div>}
        <div className="card">
          <table>
            <thead>
              <tr><th>Tipo</th><th>Destinatario</th><th>Asunto</th><th>Estado</th><th>Fecha</th></tr>
            </thead>
            <tbody>
              {notificaciones.map(n => (
                <tr key={n.id}>
                  <td><span className={`badge ${getBadge(n.tipo)}`}>{n.tipo}</span></td>
                  <td>{n.destinatario}</td>
                  <td>{n.asunto}</td>
                  <td><span className={`badge ${n.estado === 'ENVIADO' ? 'badge-success' : 'badge-danger'}`}>{n.estado}</span></td>
                  <td style={{ color: '#94a3b8', fontSize: '0.85rem' }}>
                    {new Date(n.fechaCreacion).toLocaleString()}
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

export default NotificacionesPage