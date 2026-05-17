import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { registroService } from '../services/authService'
import '../styles/LoginPage.css'

function RegistroPage() {
  const [nombre, setNombre] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [cargando, setCargando] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleRegistro = async () => {
    if (!nombre || !email || !password) {
      setError('Completa todos los campos')
      return
    }
    try {
      setCargando(true)
      setError('')
      const datos = await registroService(nombre, email, password)
      login(datos.token, datos.usuario)
      navigate('/productos')
    } catch (err) {
      setError(err.response?.data || 'Error al registrarse')
    } finally {
      setCargando(false)
    }
  }

  return (
    <div className="login-contenedor">
      <div className="login-card">
        <h1 className="login-titulo">SmartLogix</h1>
        <p className="login-subtitulo">Crear cuenta</p>
        <input className="login-input" type="text" placeholder="Nombre completo"
          value={nombre} onChange={e => setNombre(e.target.value)} />
        <input className="login-input" type="email" placeholder="Email"
          value={email} onChange={e => setEmail(e.target.value)} />
        <input className="login-input" type="password" placeholder="Contraseña"
          value={password} onChange={e => setPassword(e.target.value)} />
        {error && <p className="login-error">{error}</p>}
        <button className="login-boton" onClick={handleRegistro} disabled={cargando}>
          {cargando ? 'Registrando...' : 'Registrarse'}
        </button>
        <p style={{ textAlign: 'center', color: '#94a3b8', fontSize: '0.9rem' }}>
          ¿Ya tienes cuenta? <Link to="/" style={{ color: '#38bdf8' }}>Inicia sesión</Link>
        </p>
      </div>
    </div>
  )
}

export default RegistroPage