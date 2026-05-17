import axios from 'axios'

const BASE_URL = 'http://localhost:8081/api/auth'

export const loginService = async (email, password) => {
  const response = await axios.post(`${BASE_URL}/login`, { email, password })
  const data = response.data
  return {
    token: data.token,
    usuario: { email: data.email, nombre: data.nombre, rol: data.rol }
  }
}

export const registroService = async (nombre, email, password) => {
  const response = await axios.post(`${BASE_URL}/registro`, {
    nombre, email, password
  })
  const data = response.data
  return {
    token: data.token,
    usuario: { email: data.email, nombre: data.nombre, rol: data.rol }
  }
}