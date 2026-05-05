import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { loginService } from '../services/authService'
import '../styles/LoginPage.css'

function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [cargando, setCargando] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleLogin = async () => {
    if (!email || !password) {
      setError('Completa todos los campos')
      return
    }

    try {
      setCargando(true)
      setError('')

      const datos = await loginService(email, password)
      login(datos.token, datos.usuario)
      navigate('/dashboard')

    } catch (err) {
      setError('Credenciales incorrectas')
    } finally {
      setCargando(false)
    }
  }

  return (
    <div className="login-contenedor">
      <div className="login-card">
        <h1 className="login-titulo">SmartLogix</h1>
        <p className="login-subtitulo">Ingresa a tu cuenta</p>

        <input
          className="login-input"
          type="email"
          placeholder="Email"
          value={email}
          onChange={e => setEmail(e.target.value)}
        />
        <input
          className="login-input"
          type="password"
          placeholder="Contraseña"
          value={password}
          onChange={e => setPassword(e.target.value)}
        />

        {error && <p className="login-error">{error}</p>}

        <button
          className="login-boton"
          onClick={handleLogin}
          disabled={cargando}
        >
          {cargando ? 'Ingresando...' : 'Ingresar'}
        </button>
      </div>
    </div>
  )
}

export default LoginPage