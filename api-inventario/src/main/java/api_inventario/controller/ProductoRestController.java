package api_inventario.controller;

import api_inventario.entity.Producto;
import api_inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(productoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HashMap<String, Object>> obtenerProductoPorId(@PathVariable("id") String idStr) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            int id = Integer.parseInt(idStr);
            Optional<Producto> optProducto = productoRepository.findById(id);

            if (optProducto.isPresent()) {
                response.put("estado", "encontrado");
                response.put("producto", optProducto.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Producto no encontrado con el ID: " + id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (NumberFormatException e) {
            response.put("error", "El ID debe ser un valor numérico válido");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<HashMap<String, Object>> crearProducto(
            @RequestBody Producto producto,
            @RequestParam(required = false) boolean fetchId) {
        
        HashMap<String, Object> response = new HashMap<>();
        Producto productoGuardado = productoRepository.save(producto);

        response.put("estado", "creado");
        if (fetchId) {
            response.put("id", productoGuardado.getIdproducto());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<HashMap<String, Object>> actualizarProducto(@RequestBody Producto producto) {
        HashMap<String, Object> response = new HashMap<>();

        if (producto.getIdproducto() == null || producto.getIdproducto() <= 0) {
            response.put("error", "Debe proporcionar un idproducto válido mayor a 0");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Optional<Producto> optExistente = productoRepository.findById(producto.getIdproducto());
        if (optExistente.isEmpty()) {
            response.put("error", "El producto con ID " + producto.getIdproducto() + " no existe");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Producto productoDb = optExistente.get();

        if (producto.getNombre() != null) {
            productoDb.setNombre(producto.getNombre());
        }
        if (producto.getPrecio() != null) {
            productoDb.setPrecio(producto.getPrecio());
        }
        if (producto.getStock() != null) {
            productoDb.setStock(producto.getStock());
        }
        if (producto.getProveedor() != null) {
            productoDb.setProveedor(producto.getProveedor());
        }

        productoRepository.save(productoDb);
        response.put("estado", "actualizado");
        response.put("producto", productoDb);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HashMap<String, Object>> eliminarProducto(@PathVariable("id") String idStr) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            int id = Integer.parseInt(idStr);
            if (productoRepository.existsById(id)) {
                productoRepository.deleteById(id);
                response.put("estado", "eliminado");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "No existe un producto con el ID: " + id);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (NumberFormatException e) {
            response.put("error", "El ID proporcionado no es numérico");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HashMap<String, Object>> handleJsonError(HttpMessageNotReadableException ex) {
        HashMap<String, Object> response = new HashMap<>();
        response.put("error", "El cuerpo de la petición JSON está vacío o tiene una sintaxis inválida");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
