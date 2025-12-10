package com.fastfood.service.Controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fastfood.service.model.HistorialOperacion;
import com.fastfood.service.model.Pedido;
import com.fastfood.service.Service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
public class Controller {

    @Autowired
    private PedidoService service;

    @PostMapping
    public ResponseEntity<?> register(@RequestBody Pedido pedido) {
        try {
            Pedido creado = service.registrarPedido(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al registrar el pedido.");
        }
    }

    @GetMapping
    public ResponseEntity<?> listAll() {
        try {
            return ResponseEntity.ok(service.listarPedidos());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener los pedidos.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> searchById(@PathVariable int id) {
        try {
            Pedido pedido = service.encontrarPedido(id);

            if (pedido == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Pedido no encontrado");
            }

            return ResponseEntity.ok(pedido);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al buscar el pedido.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelById(@PathVariable int id) {
        try {
            Pedido eliminado = service.cancelarPedido(id);

            if (eliminado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Pedido no encontrado");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Se eliminó el pedido con éxito.");
            response.put("pedido", eliminado);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cancelar el pedido.");
        }
    }

    @PostMapping("/despachar")
    public ResponseEntity<?> dispatch() {
        try {
            Pedido pedido = service.despacharPedido();

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Pedido despachado con éxito.");
            response.put("pedido", pedido);

            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al despachar el pedido.");
        }
    }

  
    @GetMapping("/estadisticas")
    public ResponseEntity<?> stats() {
        try {
            return ResponseEntity.ok(service.mostrarEstadisticas());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener estadísticas.");
        }
    }

    @GetMapping("/total-recursivo")
    public ResponseEntity<?> total() {
        try {
            return ResponseEntity.ok(service.calcularTotal());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al calcular el total.");
        }
    }

    
    @PostMapping("/rollback")
    public ResponseEntity<?> rollback() {
        try {
            HistorialOperacion evento = service.rollback();

            if (evento == null) {
                return ResponseEntity.badRequest().body("No hay operaciones para deshacer.");
            }

            Pedido afectado = (evento.getPedidoAntes() != null)
                    ? evento.getPedidoAntes()
                    : evento.getPedidoDespues();

            Map<String, Object> json = new HashMap<>();
            json.put("tipo", evento.getTipoOperacion());
            json.put("pedidoAfectado", afectado);

            return ResponseEntity.ok(json);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al ejecutar rollback.");
        }
    }
}
