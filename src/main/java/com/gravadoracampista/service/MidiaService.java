package com.gravadoracampista.service;

import com.gravadoracampista.service.exceptions.allExceptions.CreateEntitiesException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MidiaService {

  private final Path pasta;

  public MidiaService(@Value("${api.midia.pasta}") String pasta) throws IOException {
    this.pasta = Path.of(pasta).toAbsolutePath().normalize();
    Files.createDirectories(this.pasta);
  }

  public Path getPasta() {
    return pasta;
  }

  //GRAVA A IMAGEM COM UM NOME ALEATORIO E DEVOLVE ESSE NOME (O NOME QUE VEM DO NAVEGADOR NUNCA E USADO)
  public String salvarImagem(MultipartFile arquivo) {
    if (arquivo == null || arquivo.isEmpty()) {
      throw new CreateEntitiesException(List.of("Envie um arquivo de imagem."));
    }
    try {
      byte[] conteudo = arquivo.getBytes();
      String extensao = extensaoDaImagem(conteudo);
      if (extensao == null) {
        throw new CreateEntitiesException(List.of("Formato não aceito: envie uma imagem JPG, PNG ou WebP."));
      }
      String nome = UUID.randomUUID() + "." + extensao;
      Files.write(pasta.resolve(nome), conteudo);
      return nome;
    } catch (IOException e) {
      throw new UncheckedIOException("Não foi possível salvar a imagem.", e);
    }
  }

  public void apagar(String nome) {
    if (nome == null || nome.isBlank()) {
      return;
    }
    Path arquivo = pasta.resolve(nome).normalize();
    //SO APAGA ARQUIVOS DE DENTRO DA PASTA DE MIDIA
    if (!arquivo.startsWith(pasta)) {
      return;
    }
    try {
      Files.deleteIfExists(arquivo);
    } catch (IOException e) {
      log.warn("Não foi possível apagar o arquivo {}", arquivo, e);
    }
  }

  //A EXTENSAO E O TIPO INFORMADOS PELO NAVEGADOR PODEM MENTIR; OS PRIMEIROS BYTES DIZEM O FORMATO REAL
  private static String extensaoDaImagem(byte[] conteudo) {
    if (comecaCom(conteudo, 0, 0xFF, 0xD8, 0xFF)) {
      return "jpg";
    }
    if (comecaCom(conteudo, 0, 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A)) {
      return "png";
    }
    if (comecaCom(conteudo, 0, 'R', 'I', 'F', 'F') && comecaCom(conteudo, 8, 'W', 'E', 'B', 'P')) {
      return "webp";
    }
    return null;
  }

  private static boolean comecaCom(byte[] conteudo, int inicio, int... assinatura) {
    if (conteudo.length < inicio + assinatura.length) {
      return false;
    }
    for (int i = 0; i < assinatura.length; i++) {
      if ((conteudo[inicio + i] & 0xFF) != assinatura[i]) {
        return false;
      }
    }
    return true;
  }
}
