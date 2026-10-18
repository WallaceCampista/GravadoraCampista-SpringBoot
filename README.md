# API Gravadora Campista

API em Java Spring Boot que simula uma gravadora de música: cadastro de bandas, álbuns e músicas, avaliações de 0 a 10 e controle de usuários com login JWT. É consumida pelo frontend [GravadoraCampista-Angular](https://github.com/WallaceCampista/GravadoraCampista-Angular).

## Tecnologias

- Java 17 (compila e roda com JDK 17 a 21; o JDK 22+ quebra o Lombok desta versão)
- Spring Boot 3.2.5: Web, Data JPA, Security, Validation
- MySQL 8
- JWT (`com.auth0:java-jwt`)

## Como rodar

1. Suba o MySQL com Docker. Os dados batem com os valores padrão do `application-dev.yml`:
   ```bash
   docker run -d --name mysql-gravadora -p 3306:3306 \
     -e MYSQL_ROOT_PASSWORD=root \
     -e MYSQL_DATABASE=bancogravadora \
     -e MYSQL_USER=Wallace \
     -e 'MYSQL_PASSWORD=Senha123.' \
     mysql:8.4
   ```
   Nas próximas vezes: `docker start mysql-gravadora`.
2. Suba a API com o JDK 21 (ou 17):
   ```bash
   export JAVA_HOME=$(/usr/libexec/java_home -v 21)   # macOS
   ./mvnw spring-boot:run
   ```
   A API sobe em `http://localhost:8080` e as tabelas são criadas automaticamente.
3. Entre com o admin padrão: usuário `admin`, senha `Admin@123`. Ele é criado na inicialização sempre que não existe nenhum admin.

## Configuração

Todos os valores têm um padrão para desenvolvimento local e podem ser trocados por variáveis de ambiente:

| Variável | Padrão | Para que serve |
| --- | --- | --- |
| `DB_URL` | `jdbc:mysql://localhost:3306/bancogravadora` | Endereço do banco |
| `DB_USERNAME` / `DB_PASSWORD` | `Wallace` / `Senha123.` | Usuário do banco |
| `JWT_SECRET` | `my-secret-key` | Chave de assinatura dos tokens |
| `ADMIN_USERNAME` / `ADMIN_PASSWORD` / `ADMIN_EMAIL` | `admin` / `Admin@123` / `admin@gravadoracampista.com` | Admin criado na inicialização |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:4200` | Origens do frontend liberadas (separe por vírgula) |

## Autenticação

1. Faça login em `POST /usuarios/post/login/` com `{"username": "...", "password": "..."}`.
2. Envie o `token` da resposta em todas as outras rotas: `Authorization: Bearer <token>`. Ele vale 2 horas.

Sem token, ou com token inválido, a resposta é `403`. Rotas de admin chamadas por usuário comum respondem `401`.

Regras de cadastro de usuário: senha com 8+ caracteres, contendo maiúscula, minúscula, número e um destes símbolos: `@ # $ % ^ & + = .`. O e-mail precisa terminar em `com`.

## Rotas

Todas as rotas terminam com `/` (sem a barra final a resposta é `404`), exceto `/usuarios/all`.

| Método | Rota | Acesso | Corpo |
| --- | --- | --- | --- |
| POST | `/usuarios/post/registro/` | público | `username`, `password`, `email`, `primeiroNome`, `sobrenome` |
| POST | `/usuarios/post/login/` | público | `username`, `password` |
| GET | `/usuarios/all` | admin | — |
| PUT | `/usuarios/update/{id}/` | admin | qualquer campo do cadastro + `isAdmin` |
| DELETE | `/usuarios/delete/{id}/` | admin | — |
| POST | `/banda/novo-registro/` | logado | `nomeBanda`, `resumoBanda` |
| GET | `/banda/listar-bandas-simples/` | logado | — |
| GET | `/banda/listar-bandas-completos/` | logado | — |
| GET | `/banda/{nome}/` | logado | — (busca por parte do nome, paginada) |
| POST | `/banda/avaliar-banda/{id}/` | logado | `nota` (0 a 10) |
| PUT | `/banda/update/{id}/` | admin | `nomeBanda`, `resumoBanda` |
| DELETE | `/banda/delete/{id}/` | admin | — (apaga também os álbuns e músicas) |
| POST | `/album/novo-registro/` | logado | `idBanda`, `nomeAlbum`, `resumoAlbum` |
| GET | `/album/listar-albuns-simples/` | logado | — |
| GET | `/album/listar-albuns-completos/` | logado | — |
| GET | `/album/{nome}/` | logado | — (busca por parte do nome, paginada) |
| POST | `/album/avaliar-album/{id}/` | logado | `nota` (0 a 10) |
| PUT | `/album/update/{id}/` | admin | `idBanda`, `nomeAlbum`, `resumoAlbum` |
| DELETE | `/album/delete/{id}/` | admin | — (apaga também as músicas) |
| POST | `/musica/novo-registro/` | logado | `idAlbum`, `nomeMusica`, `resumoMusica`, `duracao` (segundos) |
| GET | `/musica/listar-musicas-simples/` | logado | — |
| GET | `/musica/listar-musicas-completas/` | logado | — |
| GET | `/musica/{nome}/` | logado | — (busca por parte do nome, paginada) |
| POST | `/musica/avaliar-musica/{id}/` | logado | `nota` (0 a 10) |
| PUT | `/musica/update/{id}/` | admin | `idAlbum`, `nomeMusica`, `resumoMusica`, `duracao` |
| DELETE | `/musica/delete/{id}/` | admin | — |

As exclusões são lógicas: o registro fica no banco marcado como excluído e some das listagens.
