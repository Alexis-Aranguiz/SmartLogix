import { createContext, useContext, useEffect } from 'react'
import SessionManager from '../singleton/SessionManager'
import useAuthStore from '../store/authStore'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const { setUsuario, clearUsuario } = useAuthStore()

  // Restaurar sesión al recargar la página
  useEffect(() => {
    const token = SessionManager.getInstance().getToken()
    const usuario = SessionManager.getInstance().getUsuario()
    if (token && usuario) {
      setUsuario(usuario, token)
    }
  }, [])

  const login = (token, usuario) => {
    SessionManager.getInstance().guardarSesion(token, usuario)
    setUsuario(usuario, token)
  }

  const logout = () => {
    SessionManager.getInstance().cerrarSesion()
    clearUsuario()
  }

  return (
    <AuthContext.Provider value={{ login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}