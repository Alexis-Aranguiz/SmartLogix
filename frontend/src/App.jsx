import { BrowserRouter, Routes, Route } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import RegistroPage from './pages/RegistroPage'
import DashboardPage from './pages/DashboardPage'
import ProductosPage from './pages/ProductosPage'
import MisPedidosPage from './pages/MisPedidosPage'
import InventarioPage from './pages/InventarioPage'
import PedidosAdminPage from './pages/PedidosAdminPage'
import EnviosPage from './pages/EnviosPage'
import NotificacionesPage from './pages/NotificacionesPage'
import ProtectedRoute from './components/ProtectedRoute'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/registro" element={<RegistroPage />} />

        {/* Rutas CLIENTE */}
        <Route path="/productos" element={
          <ProtectedRoute><ProductosPage /></ProtectedRoute>
        } />
        <Route path="/mis-pedidos" element={
          <ProtectedRoute><MisPedidosPage /></ProtectedRoute>
        } />

        {/* Rutas ADMIN */}
        <Route path="/dashboard" element={
          <ProtectedRoute roles={['ADMIN_PYME']}><DashboardPage /></ProtectedRoute>
        } />
        <Route path="/inventario" element={
          <ProtectedRoute roles={['ADMIN_PYME']}><InventarioPage /></ProtectedRoute>
        } />
        <Route path="/pedidos-admin" element={
          <ProtectedRoute roles={['ADMIN_PYME']}><PedidosAdminPage /></ProtectedRoute>
        } />
        <Route path="/envios" element={
          <ProtectedRoute roles={['ADMIN_PYME']}><EnviosPage /></ProtectedRoute>
        } />
        <Route path="/notificaciones" element={
          <ProtectedRoute roles={['ADMIN_PYME']}><NotificacionesPage /></ProtectedRoute>
        } />
      </Routes>
    </BrowserRouter>
  )
}

export default App