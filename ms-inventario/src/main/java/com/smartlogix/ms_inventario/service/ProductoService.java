package com.smartlogix.ms_inventario.service;

import com.smartlogix.ms_inventario.dto.ProductoRequest;
import com.smartlogix.ms_inventario.dto.ProductoResponse;
import com.smartlogix.ms_inventario.event.StockActualizadoEvent;
import com.smartlogix.ms_inventario.model.AlertaStock;
import com.smartlogix.ms_inventario.model.Producto;
import com.smartlogix.ms_inventario.repository.AlertaStockRepository;
import com.smartlogix.ms_inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    // ══════════════════════════════════════════════════════════════
    // PATRÓN OBSERVER — Publisher (GoF)
    // subscribers[] = lista de observers registrados
    // subscribe()   = registrar observer
    // notify()      = notificar a todos los observers
    // ══════════════════════════════════════════════════════════════

    // Lista de suscriptores — equivale a subscribers[] del diagrama
    private final List<StockObserver> subscribers = new ArrayList<>();

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private AlertaStockRepository alertaStockRepository;

    @Autowired
    private org.springframework.web.reactive.function.client.WebClient.Builder webClientBuilder;

    @org.springframework.beans.factory.annotation.Value("${notificaciones.url}")
    private String notificacionesUrl;

    // subscribe() del GoF — registrar un observer
    public void subscribe(StockObserver observer) {
        subscribers.add(observer);
        System.out.println("✅ Observer suscrito: " + observer.getClass().getSimpleName());
    }

    // unsubscribe() del GoF — eliminar un observer
    public void unsubscribe(StockObserver observer) {
        subscribers.remove(observer);
        System.out.println("🗑️ Observer desuscrito: " + observer.getClass().getSimpleName());
    }

    // notifySubscribers() del GoF — notificar a todos los observers
    private void notifySubscribers(Long idProducto, String nombreProducto,
                                    String tipoAlerta, Integer stockNuevo) {
        System.out.println("📣 Notificando a " + subscribers.size() + " observer(s)");
        subscribers.forEach(observer ->
            observer.update(idProducto, nombreProducto, tipoAlerta, stockNuevo)
        );
    }

    // Registrar observers al iniciar el servicio
    @Autowired
    public void registrarObservers(AlertaStockObserver alertaObserver) {
        subscribe(alertaObserver);
    }

    // ══════════════════════════════════════════════════════════════

    public List<ProductoResponse> listarProductos() {
        return productoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductoResponse obtenerProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return toResponse(producto);
    }

    @Transactional
    public ProductoResponse crearProducto(ProductoRequest request) {
        Producto producto = new Producto();
        producto.setSku(request.getSku());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setStockMinimo(request.getStockMinimo());
        producto.setStockMaximo(request.getStockMaximo());
        producto.setActivo(true);

        Producto guardado = productoRepository.save(producto);
        return toResponse(guardado);
    }

    @Transactional
    public ProductoResponse actualizarStock(Long id, Integer nuevoStock) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Integer stockAnterior = producto.getStock();
        producto.setStock(nuevoStock);
        productoRepository.save(producto);

        String tipoAlerta = determinarTipoAlerta(producto);

        // notifySubscribers() — notifica a observers locales (AlertaStockObserver)
        notifySubscribers(producto.getId(), producto.getNombre(),
                tipoAlerta, nuevoStock);

        // Publicar evento Spring (para otros microservicios)
        eventPublisher.publishEvent(new StockActualizadoEvent(
                this, producto.getId(), producto.getSku(),
                producto.getNombre(), stockAnterior, nuevoStock, tipoAlerta
        ));

        // Notificar a ms-notificaciones via REST
        notificarCambioStock(producto, stockAnterior, tipoAlerta);

        return toResponse(producto);
    }

    @Transactional
    public void descontarStock(Long productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));

        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente para: " + producto.getNombre());
        }

        Integer stockAnterior = producto.getStock();
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);

        String tipoAlerta = determinarTipoAlerta(producto);

        // notifySubscribers() — notifica observers
        notifySubscribers(producto.getId(), producto.getNombre(),
                tipoAlerta, producto.getStock());

        if (!tipoAlerta.equals("NORMAL")) {
            notificarCambioStock(producto, stockAnterior, tipoAlerta);
        }

        System.out.println("📦 Stock descontado: " + producto.getNombre() +
                " → " + producto.getStock());
    }

    @Transactional
    public ProductoResponse devolverStock(Long id, Integer cantidad) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Integer stockAnterior = producto.getStock();
        producto.setStock(producto.getStock() + cantidad);
        productoRepository.save(producto);

        System.out.println("🔄 SAGA Compensación: stock devuelto para " +
                producto.getNombre() + " +" + cantidad);

        return toResponse(producto);
    }

    public List<AlertaStock> obtenerAlertasPendientes() {
        return alertaStockRepository.findByLeidaFalse();
    }

    private void notificarCambioStock(Producto producto,
                                       Integer stockAnterior, String tipoAlerta) {
        if (tipoAlerta.equals("NORMAL")) return;
        try {
            java.util.Map<String, Object> evento = new java.util.HashMap<>();
            evento.put("idProducto", producto.getId());
            evento.put("nombreProducto", producto.getNombre());
            evento.put("tipoAlerta", tipoAlerta);
            evento.put("stockNuevo", producto.getStock());

            webClientBuilder.build()
                    .post()
                    .uri(notificacionesUrl + "/api/notificaciones/stock-evento")
                    .bodyValue(evento)
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(response ->
                            System.out.println("✅ ms-notificaciones notificado: " + response));
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo notificar: " + e.getMessage());
        }
    }

    private String determinarTipoAlerta(Producto producto) {
        if (producto.esAgotado()) return "AGOTADO";
        if (producto.esStockCritico()) return "BAJO";
        if (producto.esSobrestock()) return "SOBRESTOCK";
        return "NORMAL";
    }

    private String generarMensaje(String tipo, Producto producto) {
        return switch (tipo) {
            case "AGOTADO" -> "Producto AGOTADO: " + producto.getNombre();
            case "BAJO" -> "Stock bajo en: " + producto.getNombre() +
                    " (actual: " + producto.getStock() +
                    ", mínimo: " + producto.getStockMinimo() + ")";
            case "SOBRESTOCK" -> "Sobrestock en: " + producto.getNombre() +
                    " (actual: " + producto.getStock() +
                    ", máximo: " + producto.getStockMaximo() + ")";
            default -> "";
        };
    }

    private ProductoResponse toResponse(Producto p) {
        return new ProductoResponse(
                p.getId(), p.getSku(), p.getNombre(),
                p.getPrecio(), p.getStock(), p.getStockMinimo(),
                p.getStockMaximo(), determinarTipoAlerta(p), p.getActivo()
        );
    }
    @Transactional
public void eliminarProducto(Long id) {
    alertaStockRepository.deleteByIdProducto(id);
    productoRepository.deleteById(id);
}
}