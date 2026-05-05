class SessionManager {
  static instance = null
  constructor() {
    if (SessionManager.instance) {
      throw new Error(
        'Ya existe una instancia de SessionManager. Usa getInstance()'
      )
    }
    this.token = null
    this.usuario = null
    console.log('SessionManager — instancia única creada')
  }

  static getInstance() {
    if (SessionManager.instance === null) {
      SessionManager.instance = new SessionManager()
    }
    return SessionManager.instance
  }

  guardarSesion(token, usuario) {
    this.token = token
    this.usuario = usuario
    localStorage.setItem('token', token)
    localStorage.setItem('usuario', JSON.stringify(usuario))
    console.log('Sesión guardada para:', usuario.email)
  }

  cerrarSesion() {
    console.log('Sesión cerrada para:', this.usuario?.email)
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