// ============================================
// PATRÓN SINGLETON EN REACT
// Siguiendo el modelo del profe (singleton-react-example.jsx)
// Una sola instancia gestiona la sesión en toda la app
// ============================================

class SessionManager {
  // 1. Atributo estático privado
  static instance = null

  // 2. Constructor — lanza error si intentan crear segunda instancia
  constructor() {
    if (SessionManager.instance) {
      throw new Error(
        'Ya existe una instancia de SessionManager. Usa getInstance()'
      )
    }
    this.token = null
    this.usuario = null
    console.log('✅ SessionManager — instancia única creada')
  }

  // 3. Método estático getInstance()
  static getInstance() {
    if (SessionManager.instance === null) {
      SessionManager.instance = new SessionManager()
    }
    return SessionManager.instance
  }

  // 4. Métodos de negocio
  guardarSesion(token, usuario) {
    this.token = token
    this.usuario = usuario
    localStorage.setItem('token', token)
    localStorage.setItem('usuario', JSON.stringify(usuario))
    console.log('🔐 Sesión guardada para:', usuario.email)
  }

  cerrarSesion() {
    console.log('🔓 Sesión cerrada para:', this.usuario?.email)
    this.token = null
    this.usuario = null
    localStorage.removeItem('token')
    localStorage.removeItem('usuario')
  }

  getToken() {
    return this.token || localStorage.getItem('token')
  }

  getUsuario() {
    return this.usuario || JSON.parse(localStorage.getItem('usuario'))
  }

  getSesionActiva() {
    return this.getToken() !== null
  }
}

export default SessionManager