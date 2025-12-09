package com.fastfood.service.Controller;

import com.fastfood.service.model.Pedido;
import com.fastfood.service.model.PedidoRequest;
import com.fastfood.service.model.PedidoResponse;
import com.fastfood.service.model.HistorialOperacion;
import com.fastfood.service.Service.FastFoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors; // Necesario para mapear Listas

@RestController
@RequestMapping("/api/pedidos")
public class FastFoodController {

    @Autowired
    private FastFoodService fastFoodService;

    // 4.1. Registrar un nuevo pedido (POST /api/pedidos)
    @PostMapping
    public ResponseEntity<?> registrarPedido(@RequestBody PedidoRequest request) {
        // Validación DTO
        if (request.getNombreCliente() == null || request.getNombreCliente().trim().isEmpty() ||
            request.getDescripcion() == null || request.getDescripcion().trim().isEmpty() ||
            request.getMonto() <= 0) {
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Datos de entrada inválidos.");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); // 400
        }

        Pedido nuevoPedido = fastFoodService.registrarPedido(
            request.getNombreCliente(), 
            request.getDescripcion(), 
            request.getMonto());
        
        // Mapeo a PedidoResponse antes de devolver
        PedidoResponse responseDto = new PedidoResponse(nuevoPedido); 
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED); // 201
    }

    // 4.2. Listar todos los pedidos (GET /api/pedidos)
    @GetMapping
    public List<PedidoResponse> listarTodos() {
        // Mapeo List<Pedido> a List<PedidoResponse>
        return fastFoodService.listarTodos().stream()
                .map(PedidoResponse::new)
                .collect(Collectors.toList());
    }

    // 4.3. Consultar un pedido por id (GET /api/pedidos/{id})
    @GetMapping("/{id}")
    public ResponseEntity<?> consultarPorId(@PathVariable int id) {
        Pedido pedido = fastFoodService.buscarPorId(id);
        
        if (pedido == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Pedido no encontrado con ID: " + id);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // 404
        }
        
        PedidoResponse responseDto = new PedidoResponse(pedido);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 4.4. Cancelar un pedido por id (DELETE /api/pedidos/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarPedido(@PathVariable int id) {
        Pedido pedidoCancelado = fastFoodService.cancelarPedido(id);

        if (pedidoCancelado == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Pedido no encontrado con ID: " + id);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // 404
        }
        
        PedidoResponse responseDto = new PedidoResponse(pedidoCancelado);
        
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Pedido cancelado correctamente");
        response.put("pedido", responseDto);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 4.5. Despachar el siguiente pedido (POST /api/pedidos/despachar)
    @PostMapping("/despachar")
    public ResponseEntity<?> despacharSiguiente() {
        Pedido pedidoDespachado = fastFoodService.despacharSiguiente();

        if (pedidoDespachado == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "No hay pedidos pendientes para despachar en la cola.");
            return new ResponseEntity<>(error, HttpStatus.CONFLICT); // 409
        }

        PedidoResponse responseDto = new PedidoResponse(pedidoDespachado);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Pedido despachado correctamente");
        response.put("pedido", responseDto);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 4.6. Obtener estadísticas de pedidos (GET /api/pedidos/estadisticas)
    @GetMapping("/estadisticas")
    public Map<String, Object> obtenerEstadisticas() {
        return fastFoodService.obtenerEstadisticas();
    }

    // 4.7. Cálculo recursivo del monto total (GET /api/pedidos/total-recursivo)
    @GetMapping("/total-recursivo")
    public Map<String, Double> calcularTotalRecursivo() {
        double total = fastFoodService.calcularMontoTotalRecursivo();
        Map<String, Double> response = new HashMap<>();
        response.put("totalMontoRecursivo", total);
        return response;
    }

    // 4.8. Rollback de la última operación (POST /api/pedidos/rollback)
    @PostMapping("/rollback")
    public ResponseEntity<?> realizarRollback() {
        HistorialOperacion opRevertida = fastFoodService.rollback();

        if (opRevertida == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "La pila de historial está vacía. No hay operaciones para revertir.");
            return new ResponseEntity<>(error, HttpStatus.CONFLICT); // 409
        }

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Rollback realizado correctamente");
        response.put("operacionRevertida", opRevertida.getTipoOperacion());
        
        // El estado ANTES de la operación revertida es el estado ACTUAL después del rollback (excepto en CREAR)
        if (opRevertida.getTipoOperacion().equals("CREAR")) {
            response.put("pedido", "Pedido con ID " + opRevertida.getPedidoDespues().getId() + " eliminado.");
        } else {
            PedidoResponse responseDto = new PedidoResponse(opRevertida.getPedidoAntes());
            response.put("pedido", responseDto);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
