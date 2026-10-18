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

Com o banco vazio (nenhuma banda cadastrada, nem excluída), a API também cria dados de exemplo: 4 bandas fictícias, com álbuns, músicas e avaliações, para a home "TOP 3" já abrir preenchida. As notas de exemplo são de 3 usuários comuns, os ouvintes `ana.lima`, `bruno.costa` e `carla.souza`, todos com a senha `Ouvinte@123`; dá para entrar com eles e ver a própria nota no popup de avaliar. Para começar com o banco vazio, suba com `DADOS_EXEMPLO=false`. A carga é gravada numa transação só: se a API for parada no meio dela, nada fica gravado e a carga roda de novo na próxima subida.

Para voltar o banco ao estado inicial (só o admin e os dados de exemplo), recrie o banco e suba a API de novo:

```bash
docker exec mysql-gravadora mysql -uroot -proot -e "DROP DATABASE bancogravadora; CREATE DATABASE bancogravadora;"
```

## Configuração

Todos os valores têm um padrão para desenvolvimento local e podem ser trocados por variáveis de ambiente:

| Variável | Padrão | Para que serve |
| --- | --- | --- |
| `DB_URL` | `jdbc:mysql://localhost:3306/bancogravadora` | Endereço do banco |
| `DB_USERNAME` / `DB_PASSWORD` | `Wallace` / `Senha123.` | Usuário do banco |
| `JWT_SECRET` | `my-secret-key` | Chave de assinatura dos tokens |
| `ADMIN_USERNAME` / `ADMIN_PASSWORD` / `ADMIN_EMAIL` | `admin` / `Admin@123` / `admin@gravadoracampista.com` | Admin criado na inicialização |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:4200` | Origens do frontend liberadas (separe por vírgula) |
| `DADOS_EXEMPLO` | `true` | Cria bandas, álbuns e músicas de exemplo quando o banco está vazio |
| `MIDIA_PASTA` | `uploads` | Pasta das imagens e dos MP3 enviados, relativa à pasta em que a API roda (fica fora do Git) |

## Autenticação

1. Faça login em `POST /usuarios/post/login/` com `{"username": "...", "password": "..."}`.
2. Envie o `token` da resposta em todas as outras rotas: `Authorization: Bearer <token>`. Ele vale 2 horas.

Sem token, ou com token inválido, a resposta é `403`. Rotas de admin chamadas por usuário comum respondem `401`.

As rotas de admin ficam listadas, por método, no `SecurityConfiguration`, e quem confere é o próprio Spring Security. O perfil de admin é lido do banco a cada chamada, não do token: promover ou rebaixar um usuário vale na hora, sem precisar entrar de novo.

Regras de cadastro de usuário: senha com 8+ caracteres, contendo maiúscula, minúscula, número e um destes símbolos: `@ # $ % ^ & + = .`. O e-mail precisa terminar em `com`.

## Rotas

Todas as rotas terminam com `/` (sem a barra final a resposta é `404`), exceto `/usuarios/all` e `/midia/{arquivo}`.

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
| POST | `/banda/avaliar-banda/{id}/` | logado | `nota` (0 a 10); avaliar de novo substitui a nota anterior |
| GET | `/banda/minha-avaliacao/{id}/` | logado | — (devolve `{"nota": …}` com a nota do usuário logado, ou `null`) |
| PUT | `/banda/update/{id}/` | admin | `nomeBanda`, `resumoBanda` |
| DELETE | `/banda/delete/{id}/` | admin | — (apaga também os álbuns e músicas) |
| POST | `/banda/foto/{id}/` | admin | multipart, campo `arquivo` (imagem) |
| DELETE | `/banda/foto/{id}/` | admin | — (remove a foto) |
| POST | `/album/novo-registro/` | logado | `idBanda`, `nomeAlbum`, `resumoAlbum` |
| GET | `/album/listar-albuns-simples/` | logado | — |
| GET | `/album/listar-albuns-completos/` | logado | — |
| GET | `/album/{nome}/` | logado | — (busca por parte do nome, paginada) |
| POST | `/album/avaliar-album/{id}/` | logado | `nota` (0 a 10); avaliar de novo substitui a nota anterior |
| GET | `/album/minha-avaliacao/{id}/` | logado | — (devolve `{"nota": …}` com a nota do usuário logado, ou `null`) |
| PUT | `/album/update/{id}/` | admin | `idBanda`, `nomeAlbum`, `resumoAlbum` |
| DELETE | `/album/delete/{id}/` | admin | — (apaga também as músicas) |
| POST | `/album/capa/{id}/` | admin | multipart, campo `arquivo` (imagem) |
| DELETE | `/album/capa/{id}/` | admin | — (remove a capa) |
| POST | `/musica/novo-registro/` | logado | `idAlbum`, `nomeMusica`, `resumoMusica`, `duracao` (segundos, opcional) |
| GET | `/musica/listar-musicas-simples/` | logado | — |
| GET | `/musica/listar-musicas-completas/` | logado | — |
| GET | `/musica/{nome}/` | logado | — (busca por parte do nome, paginada) |
| POST | `/musica/avaliar-musica/{id}/` | logado | `nota` (0 a 10); avaliar de novo substitui a nota anterior |
| GET | `/musica/minha-avaliacao/{id}/` | logado | — (devolve `{"nota": …}` com a nota do usuário logado, ou `null`) |
| PUT | `/musica/update/{id}/` | admin | `idAlbum`, `nomeMusica`, `resumoMusica`, `duracao` (opcional; ignorada se a música tem MP3) |
| POST | `/musica/audio/{id}/` | admin | multipart, campo `arquivo` (MP3) |
| DELETE | `/musica/audio/{id}/` | admin | — (remove o MP3) |
| DELETE | `/musica/delete/{id}/` | admin | — |
| GET | `/midia/{arquivo}` | público | — (imagem ou MP3 enviado; aceita `Range`) |

As exclusões são lógicas: o registro fica no banco marcado como excluído e some das listagens.

## Regras de avaliação e de nomes

- Cada usuário tem uma nota por banda, álbum ou música. Avaliar de novo substitui a nota anterior, e a média é recalculada.
- Avaliações feitas antes dessa regra não têm usuário. Elas continuam contando na média, mas ninguém consegue trocá-las.
- O nome da banda é único na gravadora. O nome do álbum só precisa ser único dentro da banda, e o da música dentro do álbum: duas bandas podem ter uma faixa "Intro".
- A comparação de nomes segue o banco (MySQL): maiúsculas e acentos não contam, então "Planície" e "planicie" são o mesmo nome.

## Imagens

A foto da banda e a capa do álbum são enviadas como formulário multipart, no campo `arquivo`. A API aceita JPG, PNG e WebP de até 5 MB (acima disso responde `400` com a mensagem) e confere o formato pelo conteúdo do arquivo, não pela extensão. Cada envio grava o arquivo com um nome aleatório na pasta `MIDIA_PASTA` e apaga a imagem anterior.

As listas trazem o nome do arquivo em `fotoArquivo` (banda) e `capaArquivo` (álbum), ou `null` quando não há imagem. A imagem fica em `GET /midia/{arquivo}`, que é a única rota de leitura sem token, porque a tag `<img>` do navegador não envia o token.

## Áudio

- O MP3 da música é enviado como formulário multipart, no campo `arquivo`, em `POST /musica/audio/{id}/`. A API aceita só MP3, de até 20 MB.
  - O formato é conferido pelos primeiros bytes (etiqueta ID3 ou quadro MPEG camada III). Depois, a biblioteca `jaudiotagger` lê o arquivo: se ela não consegue, o arquivo é apagado e a resposta é `400`.
  - Qualquer envio acima de 20 MB responde `413`. Esse é o limite do formulário (`spring.servlet.multipart`); o de cada tipo fica em `api.midia.limite-imagem` e `api.midia.limite-audio`.
- A duração da música passa a ser a do MP3, arredondada em segundos, e a duração total do álbum é recalculada.
  - Enquanto a música tem MP3, a `duracao` enviada no cadastro ou na edição é ignorada. Sem MP3 ela é opcional (fica `0`).
  - Remover o MP3 mantém a última duração, que volta a ser editável.
- As listas trazem o nome do arquivo em `audioArquivo` (ou `null`), inclusive nas músicas dentro dos álbuns.
- O MP3 fica em `GET /midia/{arquivo}`, que aceita `Range`: o navegador pede só o pedaço que precisa (resposta `206`), e dá para avançar e voltar na faixa sem baixar tudo.
- Use só músicas que você pode publicar (por exemplo, de [Pixabay Music](https://pixabay.com/music/) ou [Free Music Archive](https://freemusicarchive.org/) com licença Creative Commons). A rota de mídia é pública, e a pasta `uploads/` nunca vai para o Git.
