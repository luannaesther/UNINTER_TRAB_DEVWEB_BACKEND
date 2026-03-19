# Trabalho Desenvolvimento Web Back-end - Biblioteca

Projeto em Java com Spring Boot, Spring Data JPA e banco H2 para gerenciamento de livros e empréstimos.

## Tecnologias utilizadas
- Java 17
- Spring Boot 3
- Spring Data JPA
- Banco de dados H2 (em memória)
- JSON nos endpoints (REST)

## Estrutura de pacotes
- `br.com.biblioteca.model` – entidades JPA (Livro, Emprestimo)
- `br.com.biblioteca.repository` – interfaces de acesso a dados (JpaRepository)
- `br.com.biblioteca.controller` – controladores REST

## Como executar

1. Certifique-se de ter **Java 17** e **Maven** instalados.
2. No diretório do projeto, execute:

```powershell
mvn spring-boot:run
```

A API ficará disponível em:

- `http://localhost:8080/api/livros`
- `http://localhost:8080/api/emprestimos`

## Endpoints principais

### Livros
- `POST /api/livros` – cadastra livro
- `GET /api/livros` – lista todos os livros
- `GET /api/livros/{id}` – busca livro por ID
- `PUT /api/livros/{id}` – atualiza dados do livro
- `DELETE /api/livros/{id}` – remove livro

### Empréstimos
- `POST /api/emprestimos` – registra empréstimo de um livro disponível
- `GET /api/emprestimos` – lista empréstimos
- `PUT /api/emprestimos/{id}/devolucao` – registra devolução
