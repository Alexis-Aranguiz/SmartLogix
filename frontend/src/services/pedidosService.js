import axios from 'axios'

const BASE_URL = 'http://localhost:8083/api/pedidos'

const getHeaders = () => ({
  headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
})

export const crearPedido = async (data) => {
  try {
    const response = await axios.post(BASE_URL, data, getHeaders())
    return response.data
  } catch (err) {
    // Si el pedido se creó pero hubo error en la respuesta, no lanzar error
    if (err.response?.status === 200 || err.response?.status === 201) {
      return err.response.data
    }
    console.error('Error al crear pedido:', err.response?.data || err.message)
    throw err
  }
}

export const listarPedidos = () =>
  axios.get(BASE_URL, getHeaders()).then(r => r.data)

export const obtenerPedido = (id) =>
  axios.get(`${BASE_URL}/${id}`, getHeaders()).then(r => r.data)

export const actualizarEstadoPedido = (id, nuevoEstado) =>
  axios.patch(`${BASE_URL}/${id}/estado?nuevoEstado=${nuevoEstado}`, {}, getHeaders()).then(r => r.data)
export const cancelarPedido = (id, motivo = 'Cancelado por administrador') =>
  axios.patch(`${BASE_URL}/${id}/cancelar?motivo=${motivo}`, {}, getHeaders())
    .then(r => r.data)