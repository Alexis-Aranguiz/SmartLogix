// ============================================
// SINGLETON + OBSERVER combinados
// Singleton: SessionManager gestiona la sesión
// Observer: authStore notifica a los componentes
// ============================================

import { createContext, useContext } from 'react'
import SessionManager from '../singleton/SessionManager'
import useAuthStore from '../store/authStore'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const { setUsuario, clearUsuario } = useAuthStore()

  const login = (token, usuario) => {
    // SINGLETON — una sola instancia guarda la sesión
    SessionManager.getInstance().guardarSesion(token, usuario)
    // OBSERVER — notifica a todos los componentes suscritos
    setUsuario(usuario, token)
  }

  const logout = () => {
    // SINGLETON — cierra la sesión
    SessionManager.getInstance().cerrarSesion()
    // OBSERVER — notifica cierre de sesión
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