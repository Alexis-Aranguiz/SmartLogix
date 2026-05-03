// ============================================
// PATRÓN OBSERVER EN REACT — Zustand
// ============================================
// set() = notify() del GoF
// useAuthStore(selector) = attach() del GoF
// Cuando set() cambia el estado, TODOS los componentes
// suscritos se actualizan automáticamente (Observer)

import { create } from 'zustand'

const useAuthStore = create((set) => ({
  // Estado compartido — equivale al estado del Subject en GoF
  usuario: null,
  token: null,
  sesionActiva: false,

  // notify() — actualiza a todos los Observers suscritos
  setUsuario: (usuario, token) => {
    console.log('📣 Observer notificado — usuario autenticado:', usuario.email)
    set({ usuario, token, sesionActiva: true })
  },

  clearUsuario: () => {
    console.log('📣 Observer notificado — sesión cerrada')
    set({ usuario: null, token: null, sesionActiva: false })
  }
}))

export default useAuthStore