# Trabalho Desenvolvimento Web Back-end

API REST desenvolvida em Java com Spring Boot para gerenciamento de clientes, livros e empréstimos, seguindo o padrão MVC e utilizando banco de dados em memória (H2).

## Tecnologias utilizadas
- Java 17
- Spring Boot 3
- Spring Data JPA
- Banco de dados H2 (em memória)
- JSON nos endpoints (REST)

## Estrutura de pacotes
- `br.com.biblioteca.model` – entidades JPA (Livro, Emprestimo, Cliente)
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
- `http://localhost:8080/api/clientes`
- `http://localhost:8080/api/emprestimos`

## Endpoints principais

### Livros
- `POST /api/livros` – cadastra livro
- `GET /api/livros` – retorna a lista de todos os livros cadastrados
- `GET /api/livros/{id}` – busca livro por ID
- `PUT /api/livros/{id}` – atualiza dados do livro
- `DELETE /api/livros/{id}` – remove livro

### Empréstimos
- `POST /api/emprestimos` – registra empréstimo de um livro disponível para um cliente
- `GET /api/emprestimos` – lista empréstimos
- `PUT /api/emprestimos/{id}/devolucao` – registra devolução

### Clientes
- `POST /api/clientes` – cadastra cliente
- `GET /api/clientes` – lista clientes
- `GET /api/clientes/{id}` – busca cliente por ID
- `PUT /api/clientes/{id}` – atualiza dados do cliente
- `DELETE /api/clientes/{id}` – remove cliente

## Regras de negócio

- Um livro só pode ser emprestado se estiver disponível.
- Ao registrar um empréstimo, o livro se torna indisponível.
- Ao registrar a devolução, o livro volta a ficar disponível.

## Autor
Luanna Esther Ribeiro Dias – RU: 5498149
