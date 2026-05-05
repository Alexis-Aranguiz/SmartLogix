
import { create } from 'zustand'

const useInventarioStore = create((set) => ({
  productos: [],
  alertasStock: [],

  setProductos: (productos) => {
    console.log('Observer notificado — productos actualizados')
    set({ productos })
  },

  agregarAlerta: (alerta) => {
    console.log('Observer notificado — alerta de stock:', alerta)
    set((state) => ({
      alertasStock: [...state.alertasStock, alerta]
    }))
  },

  limpiarAlertas: () => set({ alertasStock: [] })
}))

export default useInventarioStore