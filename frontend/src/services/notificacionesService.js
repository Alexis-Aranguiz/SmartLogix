import axios from 'axios'

const BASE_URL = 'http://localhost:8085/api/notificaciones'

const getHeaders = () => ({
  headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
})

export const listarNotificaciones = () =>
  axios.get(BASE_URL, getHeaders()).then(r => r.data)