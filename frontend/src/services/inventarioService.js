import axios from 'axios'

const BASE_URL = 'http://localhost:8082/api/inventario'

const getHeaders = () => ({
  headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
})

export const listarProductos = () =>
  axios.get(`${BASE_URL}/productos`, getHeaders()).then(r => r.data)

export const crearProducto = (data) =>
  axios.post(`${BASE_URL}/productos`, data, getHeaders()).then(r => r.data)

export const actualizarStock = (id, stock) =>
  axios.patch(`${BASE_URL}/productos/${id}/stock`, { stock }, getHeaders()).then(r => r.data)

export const obtenerAlertas = () =>
  axios.get(`${BASE_URL}/alertas`, getHeaders()).then(r => r.data)

export const eliminarProducto = (id) =>
  axios.delete(`${BASE_URL}/productos/${id}`, getHeaders()).then(r => r.data)