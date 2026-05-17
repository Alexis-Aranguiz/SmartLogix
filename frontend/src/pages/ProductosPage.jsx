import { useEffect, useState } from 'react'
import Navbar from '../components/Navbar'
import useAuthStore from '../store/authStore'
import { listarProductos } from '../services/inventarioService'
import { crearPedido } from '../services/pedidosService'
import SessionManager from '../singleton/SessionManager'

function ProductosPage() {
  const [productos, setProductos] = useState([])
  const [carrito, setCarrito] = useState([])
  const [mensaje, setMensaje] = useState('')
  const [error, setError] = useState('')
  const [metodoPago, setMetodoPago] = useState('WEBPAY')
  const usuario = useAuthStore(state => state.usuario) || 
  SessionManager.getInstance().getUsuario()

  useEffect(() => {
    listarProductos()
      .then(setProductos)
      .catch(() => setError('Error al cargar productos'))
  }, [])

  const agregarAlCarrito = (producto) => {
    const existe = carrito.find(i => i.productoId === producto.id)
    if (existe) {
      setCarrito(carrito.map(i =>
        i.productoId === producto.id
          ? { ...i, cantidad: i.cantidad + 1 }
          : i
      ))
    } else {
      setCarrito([...carrito, {
        productoId: producto.id,
        nombreProducto: producto.nombre,
        cantidad: 1,
        precioUnitario: producto.precio
      }])
    }
  }

  const quitarDelCarrito = (productoId) => {
    setCarrito(carrito.filter(i => i.productoId !== productoId))
  }

  const realizarPedido = async () => {
  if (carrito.length === 0) { setError('El carrito está vacío'); return }
  try {
    setError('')
    const resultado = await crearPedido({
      clienteEmail: usuario.email,
      tipoCliente: usuario.rol === 'PREMIUM' ? 'PREMIUM' : 'NORMAL',
      metodoPago,
      datoPago: '',
      items: carrito
    })
    setMensaje('¡Pedido realizado exitosamente!')
    setCarrito([])
    setTimeout(() => setMensaje(''), 3000)
  } catch (err) {
    setError('Error: ' + (err.response?.data || err.message))
  }
}

  return (
    <>
      <Navbar />
      <div className="page">
        <h2 style={{ color: '#38bdf8', marginBottom: '1.5rem' }}>Catálogo de Productos</h2>

        {error && <div className="alert alert-error">{error}</div>}
        {mensaje && <div className="alert alert-success">{mensaje}</div>}

        <div className="grid-3" style={{ marginBottom: '2rem' }}>
          {productos.map(p => (
            <div className="card" key={p.id}>
              <h3>{p.nombre}</h3>
              <p style={{ color: '#94a3b8', margin: '0.5rem 0' }}>{p.descripcion}</p>
              <p style={{ color: '#4ade80', fontWeight: 'bold', fontSize: '1.1rem' }}>
                ${p.precio?.toLocaleString()}
              </p>
              <p style={{ color: '#94a3b8', fontSize: '0.85rem', marginBottom: '1rem' }}>
                Stock: {p.stock} |
                <span className={`badge ${p.estadoStock === 'NORMAL' ? 'badge-success' :
                  p.estadoStock === 'BAJO' ? 'badge-warning' : 'badge-danger'}`}
                  style={{ marginLeft: '0.5rem' }}>
                  {p.estadoStock}
                </span>
              </p>
              <button className="btn btn-primary" onClick={() => agregarAlCarrito(p)}
                disabled={p.stock === 0}>
                {p.stock === 0 ? 'Sin stock' : 'Agregar al carrito'}
              </button>
            </div>
          ))}
        </div>

        {carrito.length > 0 && (
          <div className="card">
            <h3 style={{ marginBottom: '1rem' }}>Carrito</h3>
            <table>
              <thead>
                <tr>
                  <th>Producto</th>
                  <th>Cantidad</th>
                  <th>Precio Unit.</th>
                  <th>Subtotal</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
  {carrito.map(item => (
    <tr key={item.productoId}>
      <td>{item.nombreProducto}</td>
      <td>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <button className="btn btn-warning"
            style={{ padding: '0.2rem 0.6rem' }}
            onClick={() => {
              if (item.cantidad === 1) {
                quitarDelCarrito(item.productoId)
              } else {
                setCarrito(carrito.map(i =>
                  i.productoId === item.productoId
                    ? { ...i, cantidad: i.cantidad - 1 }
                    : i
                ))
              }
            }}>−</button>
          <span>{item.cantidad}</span>
          <button className="btn btn-primary"
            style={{ padding: '0.2rem 0.6rem' }}
            onClick={() => setCarrito(carrito.map(i =>
              i.productoId === item.productoId
                ? { ...i, cantidad: i.cantidad + 1 }
                : i
            ))}>+</button>
        </div>
      </td>
      <td>${item.precioUnitario?.toLocaleString()}</td>
      <td>${(item.cantidad * item.precioUnitario)?.toLocaleString()}</td>
      <td>
        <button className="btn btn-danger"
          onClick={() => quitarDelCarrito(item.productoId)}
          style={{ padding: '0.3rem 0.6rem' }}>✕</button>
      </td>
    </tr>
  ))}
</tbody>
            </table>
           <div style={{ marginTop: '1rem', display: 'flex', gap: '1rem', alignItems: 'center', flexWrap: 'wrap' }}>
  <div>
    {usuario?.rol === 'PREMIUM' ? (
      <p>
        <span style={{ color: '#94a3b8', textDecoration: 'line-through', fontSize: '0.9rem' }}>
          ${carrito.reduce((acc, i) => acc + i.cantidad * i.precioUnitario, 0).toLocaleString()}
        </span>
        <span style={{ color: '#fbbf24', marginLeft: '0.5rem', fontSize: '0.85rem' }}>
          -10% PREMIUM
        </span>
        <br />
        <strong style={{ color: '#4ade80', fontSize: '1.1rem' }}>
          Total: ${(carrito.reduce((acc, i) => acc + i.cantidad * i.precioUnitario, 0) * 0.90).toLocaleString()}
        </strong>
      </p>
    ) : (
      <p>
        Total: <strong style={{ color: '#4ade80', fontSize: '1.1rem' }}>
          ${carrito.reduce((acc, i) => acc + i.cantidad * i.precioUnitario, 0).toLocaleString()}
        </strong>
      </p>
    )}
  </div>
  <select className="input" style={{ width: 'auto', marginBottom: 0 }}
    value={metodoPago} onChange={e => setMetodoPago(e.target.value)}>
    <option value="WEBPAY">WebPay</option>
    <option value="TARJETA">Tarjeta</option>
    <option value="TRANSFERENCIA">Transferencia</option>
  </select>
  <button className="btn btn-success" onClick={realizarPedido}>
    Confirmar Pedido
  </button>
</div>
          </div>
        )}
      </div>
    </>
  )
}

export default ProductosPage