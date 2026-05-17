import axios from 'axios'

const BASE_URL = 'http://localhost:8084/api/envios'

const getHeaders = () => ({
  headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
})

export const listarEnvios = () =>
  axios.get(BASE_URL, getHeaders()).then(r => r.data)

export const crearEnvio = (data) =>
  axios.post(BASE_URL, data, getHeaders()).then(r => r.data)

export const actualizarEstadoEnvio = (id, nuevoEstado) =>
  axios.put(`${BASE_URL}/${id}/estado?nuevoEstado=${nuevoEstado}`, {}, getHeaders()).then(r => r.data)