import { create } from 'zustand'
const useAuthStore = create((set) => ({

  usuario: null,
  token: null,
  sesionActiva: false,

  setUsuario: (usuario, token) => {
    console.log('Observer notificado — usuario autenticado:', usuario.email)
    set({ usuario, token, sesionActiva: true })
  },

  clearUsuario: () => {
    console.log('Observer notificado — sesión cerrada')
    set({ usuario: null, token: null, sesionActiva: false })
  }
}))

export default useAuthStore