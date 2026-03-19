package br.com.biblioteca.controller;

import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.repository.EmprestimoRepository;
import br.com.biblioteca.repository.LivroRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/emprestimos")
public class EmprestimoController {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;

    public EmprestimoController(EmprestimoRepository emprestimoRepository, LivroRepository livroRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Emprestimo emprestimo) {
        if (emprestimo.getLivro() == null || emprestimo.getLivro().getId() == null || emprestimo.getNomeUsuario() == null) {
            return ResponseEntity.badRequest().body("Campos obrigatórios: livro.id, nomeUsuario");
        }

        Livro livro = livroRepository.findById(emprestimo.getLivro().getId()).orElse(null);
        if (livro == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Livro não encontrado");
        }

        if (!livro.isDisponivel()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Livro não está disponível para empréstimo");
        }

        livro.setDisponivel(false);
        livroRepository.save(livro);

        emprestimo.setLivro(livro);
        if (emprestimo.getDataEmprestimo() == null) {
            emprestimo.setDataEmprestimo(LocalDate.now());
        }

        Emprestimo salvo = emprestimoRepository.save(emprestimo);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public List<Emprestimo> listar() {
        return emprestimoRepository.findAll();
    }

    @PutMapping("/{id}/devolucao")
    public ResponseEntity<?> registrarDevolucao(@PathVariable Long id) {
        return emprestimoRepository.findById(id)
                .map(emprestimo -> {
                    if (emprestimo.isDevolvido()) {
                        return ResponseEntity.badRequest().body("Empréstimo já foi devolvido");
                    }
                    emprestimo.setDevolvido(true);
                    emprestimoRepository.save(emprestimo);

                    Livro livro = emprestimo.getLivro();
                    livro.setDisponivel(true);
                    livroRepository.save(livro);

                    return ResponseEntity.ok(emprestimo);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empréstimo não encontrado"));
    }
}
