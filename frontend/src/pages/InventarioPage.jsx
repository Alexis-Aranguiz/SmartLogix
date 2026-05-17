import { useEffect, useState } from 'react'
import Navbar from '../components/Navbar'
import { listarProductos, crearProducto, actualizarStock, obtenerAlertas } from '../services/inventarioService'

function InventarioPage() {
  const [productos, setProductos] = useState([])
  const [alertas, setAlertas] = useState([])
  const [form, setForm] = useState({ sku: '', nombre: '', descripcion: '', precio: '', stock: '', stockMinimo: '', stockMaximo: '' })
  const [nuevoStock, setNuevoStock] = useState({})
  const [error, setError] = useState('')
  const [mensaje, setMensaje] = useState('')

  const cargar = () => {
    listarProductos().then(setProductos).catch(() => setError('Error al cargar'))
    obtenerAlertas().then(setAlertas).catch(() => {})
  }

  useEffect(() => { cargar() }, [])

  const handleCrear = async () => {
    try {
      await crearProducto({ ...form, precio: parseFloat(form.precio), stock: parseInt(form.stock), stockMinimo: parseInt(form.stockMinimo), stockMaximo: parseInt(form.stockMaximo) })
      setMensaje('Producto creado')
      setForm({ sku: '', nombre: '', descripcion: '', precio: '', stock: '', stockMinimo: '', stockMaximo: '' })
      cargar()
      setTimeout(() => setMensaje(''), 3000)
    } catch (err) { setError('Error al crear producto') }
  }

  const handleActualizarStock = async (id) => {
    try {
      await actualizarStock(id, parseInt(nuevoStock[id]))
      setMensaje('Stock actualizado — Observer notificado')
      cargar()
      setTimeout(() => setMensaje(''), 3000)
    } catch (err) { setError('Error al actualizar stock') }
  }

  return (
    <>
      <Navbar />
      <div className="page">
        <h2 style={{ color: '#38bdf8', marginBottom: '1.5rem' }}>Gestión de Inventario</h2>

        {error && <div className="alert alert-error">{error}</div>}
        {mensaje && <div className="alert alert-success">{mensaje}</div>}

        {alertas.length > 0 && (
          <div className="card" style={{ marginBottom: '1.5rem' }}>
            <h3 style={{ color: '#fbbf24', marginBottom: '1rem' }}>⚠️ Alertas de Stock</h3>
            {alertas.map(a => (
              <div key={a.id} style={{ padding: '0.5rem', borderBottom: '1px solid #334155' }}>
                <span className={`badge ${a.tipo === 'BAJO' || a.tipo === 'AGOTADO' ? 'badge-danger' : 'badge-warning'}`}>
                  {a.tipo}
                </span>
                <span style={{ marginLeft: '0.8rem', color: '#94a3b8' }}>{a.mensaje}</span>
              </div>
            ))}
          </div>
        )}

        <div className="card" style={{ marginBottom: '1.5rem' }}>
          <h3 style={{ marginBottom: '1rem' }}>Nuevo Producto</h3>
          <div className="grid-2">
            <input className="input" placeholder="SKU" value={form.sku} onChange={e => setForm({ ...form, sku: e.target.value })} />
            <input className="input" placeholder="Nombre" value={form.nombre} onChange={e => setForm({ ...form, nombre: e.target.value })} />
            <input className="input" placeholder="Descripción" value={form.descripcion} onChange={e => setForm({ ...form, descripcion: e.target.value })} />
            <input className="input" placeholder="Precio" type="number" value={form.precio} onChange={e => setForm({ ...form, precio: e.target.value })} />
            <input className="input" placeholder="Stock inicial" type="number" value={form.stock} onChange={e => setForm({ ...form, stock: e.target.value })} />
            <input className="input" placeholder="Stock mínimo" type="number" value={form.stockMinimo} onChange={e => setForm({ ...form, stockMinimo: e.target.value })} />
            <input className="input" placeholder="Stock máximo" type="number" value={form.stockMaximo} onChange={e => setForm({ ...form, stockMaximo: e.target.value })} />
          </div>
          <button className="btn btn-primary" onClick={handleCrear}>Crear Producto</button>
        </div>

        <div className="card">
          <h3 style={{ marginBottom: '1rem' }}>Productos</h3>
          <table>
            <thead>
              <tr><th>SKU</th><th>Nombre</th><th>Precio</th><th>Stock</th><th>Estado</th><th>Actualizar Stock</th></tr>
            </thead>
            <tbody>
              {productos.map(p => (
                <tr key={p.id}>
                  <td>{p.sku}</td>
                  <td>{p.nombre}</td>
                  <td>${p.precio?.toLocaleString()}</td>
                  <td>{p.stock}</td>
                  <td><span className={`badge ${p.estadoStock === 'NORMAL' ? 'badge-success' : p.estadoStock === 'BAJO' ? 'badge-warning' : 'badge-danger'}`}>{p.estadoStock}</span></td>
                  <td style={{ display: 'flex', gap: '0.5rem' }}>
                    <input className="input" type="number" placeholder="Nuevo stock"
                      style={{ width: '100px', marginBottom: 0 }}
                      value={nuevoStock[p.id] || ''}
                      onChange={e => setNuevoStock({ ...nuevoStock, [p.id]: e.target.value })} />
                    <button className="btn btn-warning" onClick={() => handleActualizarStock(p.id)}>
                      Actualizar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </>
  )
}

export default InventarioPage