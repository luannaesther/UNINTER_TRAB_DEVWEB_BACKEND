package br.com.biblioteca.controller;

import br.com.biblioteca.model.Emprestimo;
import br.com.biblioteca.model.Livro;
import br.com.biblioteca.model.Cliente;
import br.com.biblioteca.repository.ClienteRepository;
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
    private final ClienteRepository clienteRepository;

    public EmprestimoController(EmprestimoRepository emprestimoRepository,
                                LivroRepository livroRepository,
                                ClienteRepository clienteRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
        this.clienteRepository = clienteRepository;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Emprestimo emprestimo) {
        if (emprestimo.getLivro() == null || emprestimo.getLivro().getId() == null
                || emprestimo.getCliente() == null || emprestimo.getCliente().getId() == null) {
            return ResponseEntity.badRequest().body("Campos obrigatorios: livro.id, cliente.id");
        }

        Livro livro = livroRepository.findById(emprestimo.getLivro().getId()).orElse(null);
        if (livro == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Livro não encontrado");
        }

        Cliente cliente = clienteRepository.findById(emprestimo.getCliente().getId()).orElse(null);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente nao encontrado");
        }

        if (!livro.isDisponivel()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Livro não está disponível para empréstimo");
        }

        livro.setDisponivel(false);
        livroRepository.save(livro);

        emprestimo.setLivro(livro);
        emprestimo.setCliente(cliente);
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
