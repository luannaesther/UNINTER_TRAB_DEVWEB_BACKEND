package br.com.biblioteca.controller;

import br.com.biblioteca.model.Livro;
import br.com.biblioteca.repository.LivroRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
public class LivroController {

    private final LivroRepository repository;

    public LivroController(LivroRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Livro livro) {
        if (livro.getTitulo() == null || livro.getAutor() == null || livro.getIsbn() == null) {
            return ResponseEntity.badRequest().body("Campos obrigatórios: titulo, autor, isbn");
        }

        if (repository.findByIsbn(livro.getIsbn()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("ISBN já cadastrado");
        }

        Livro salvo = repository.save(livro);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public List<Livro> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscar(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Livro> atualizar(@PathVariable Long id, @RequestBody Livro livroAtualizado) {
        return repository.findById(id)
                .map(livro -> {
                    if (livroAtualizado.getTitulo() != null) {
                        livro.setTitulo(livroAtualizado.getTitulo());
                    }
                    if (livroAtualizado.getAutor() != null) {
                        livro.setAutor(livroAtualizado.getAutor());
                    }
                    if (livroAtualizado.getAno() != null) {
                        livro.setAno(livroAtualizado.getAno());
                    }
                    return ResponseEntity.ok(repository.save(livro));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        return repository.findById(id)
                .map(livro -> {
                    repository.delete(livro);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
