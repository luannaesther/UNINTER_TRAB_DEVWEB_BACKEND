package br.com.biblioteca.controller;

import br.com.biblioteca.model.Cliente;
import br.com.biblioteca.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteRepository repository;

    public ClienteController(ClienteRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Cliente cliente) {
        if (cliente.getNome() == null || cliente.getEmail() == null) {
            return ResponseEntity.badRequest().body("Campos obrigatorios: nome, email");
        }

        if (repository.findByEmail(cliente.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email ja cadastrado");
        }

        Cliente salvo = repository.save(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public List<Cliente> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscar(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Cliente clienteAtualizado) {
        return repository.findById(id)
                .map(cliente -> {
                    if (clienteAtualizado.getNome() != null) {
                        cliente.setNome(clienteAtualizado.getNome());
                    }
                    if (clienteAtualizado.getEmail() != null) {
                        repository.findByEmail(clienteAtualizado.getEmail())
                                .filter(outro -> !outro.getId().equals(id))
                                .ifPresent(outro -> {
                                    throw new IllegalArgumentException("Email ja cadastrado");
                                });
                        cliente.setEmail(clienteAtualizado.getEmail());
                    }
                    if (clienteAtualizado.getTelefone() != null) {
                        cliente.setTelefone(clienteAtualizado.getTelefone());
                    }
                    return ResponseEntity.ok(repository.save(cliente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        return repository.findById(id)
                .map(cliente -> {
                    repository.delete(cliente);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
