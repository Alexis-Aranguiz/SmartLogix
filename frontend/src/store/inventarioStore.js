// ============================================
// PATRÓN OBSERVER — inventarioStore
// Equivale a productoStore.js del profe (TiendaMax)
// Cuando el stock cambia, todos los componentes
// suscritos se actualizan automáticamente

import { create } from 'zustand'

const useInventarioStore = create((set) => ({
  productos: [],
  alertasStock: [],

  // notify() — actualiza productos
  setProductos: (productos) => {
    console.log('📣 Observer notificado — productos actualizados')
    set({ productos })
  },

  // notify() — agrega alerta de stock
  agregarAlerta: (alerta) => {
    console.log('📣 Observer notificado — alerta de stock:', alerta)
    set((state) => ({
      alertasStock: [...state.alertasStock, alerta]
    }))
  },

  limpiarAlertas: () => set({ alertasStock: [] })
}))

export default useInventarioStore