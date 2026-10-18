package com.gravadoracampista.service;

import com.gravadoracampista.service.exceptions.allExceptions.CreateEntitiesException;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
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

  //NOME DO MP3 GRAVADO E A DURACAO LIDA DELE, EM SEGUNDOS
  public record AudioSalvo(String nome, long duracao) {}

  private final Path pasta;
  private final DataSize limiteImagem;
  private final DataSize limiteAudio;

  public MidiaService(@Value("${api.midia.pasta}") String pasta,
                      @Value("${api.midia.limite-imagem}") DataSize limiteImagem,
                      @Value("${api.midia.limite-audio}") DataSize limiteAudio) throws IOException {
    this.pasta = Path.of(pasta).toAbsolutePath().normalize();
    this.limiteImagem = limiteImagem;
    this.limiteAudio = limiteAudio;
    Files.createDirectories(this.pasta);
  }

  public Path getPasta() {
    return pasta;
  }

  //GRAVA A IMAGEM COM UM NOME ALEATORIO E DEVOLVE ESSE NOME (O NOME QUE VEM DO NAVEGADOR NUNCA E USADO)
  public String salvarImagem(MultipartFile arquivo) {
    byte[] conteudo = lerConteudo(arquivo, "Envie um arquivo de imagem.", limiteImagem, "A imagem");
    String extensao = extensaoDaImagem(conteudo);
    if (extensao == null) {
      throw new CreateEntitiesException(List.of("Formato não aceito: envie uma imagem JPG, PNG ou WebP."));
    }
    return gravar(conteudo, extensao);
  }

  //GRAVA O MP3 E LE A DURACAO DELE. SE A JAUDIOTAGGER NAO CONSEGUE LER, NAO E UM MP3 DE VERDADE: O ARQUIVO E APAGADO
  public AudioSalvo salvarAudio(MultipartFile arquivo) {
    byte[] conteudo = lerConteudo(arquivo, "Envie um arquivo MP3.", limiteAudio, "O áudio");
    if (!pareceMp3(conteudo)) {
      throw new CreateEntitiesException(List.of("Formato não aceito: envie um arquivo MP3."));
    }
    String nome = gravar(conteudo, "mp3");
    try {
      AudioFile audio = AudioFileIO.read(pasta.resolve(nome).toFile());
      double segundos = audio.getAudioHeader().getPreciseTrackLength();
      if (segundos <= 0) {
        throw new IllegalStateException("duração zerada");
      }
      return new AudioSalvo(nome, Math.max(1, Math.round(segundos)));
    } catch (Exception e) {
      apagar(nome);
      log.warn("MP3 recusado: {}", e.getMessage());
      throw new CreateEntitiesException(List.of("Não foi possível ler o MP3. Confira se o arquivo não está corrompido."));
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

  //O LIMITE DO FORMULARIO (spring.servlet.multipart) E O DO MAIOR TIPO, O MP3; CADA TIPO CONFERE O SEU AQUI
  private static byte[] lerConteudo(MultipartFile arquivo, String mensagemVazio, DataSize limite, String tipo) {
    if (arquivo == null || arquivo.isEmpty()) {
      throw new CreateEntitiesException(List.of(mensagemVazio));
    }
    if (arquivo.getSize() > limite.toBytes()) {
      throw new CreateEntitiesException(List.of(tipo + " passa do limite de " + limite.toMegabytes() + " MB."));
    }
    try {
      return arquivo.getBytes();
    } catch (IOException e) {
      throw new UncheckedIOException("Não foi possível ler o arquivo enviado.", e);
    }
  }

  private String gravar(byte[] conteudo, String extensao) {
    String nome = UUID.randomUUID() + "." + extensao;
    try {
      Files.write(pasta.resolve(nome), conteudo);
    } catch (IOException e) {
      throw new UncheckedIOException("Não foi possível salvar o arquivo.", e);
    }
    return nome;
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

  //O MP3 COMECA PELA ETIQUETA ID3 OU DIRETO NO PRIMEIRO QUADRO DE AUDIO: 11 BITS EM 1 E A CAMADA III
  private static boolean pareceMp3(byte[] conteudo) {
    if (comecaCom(conteudo, 0, 'I', 'D', '3')) {
      return true;
    }
    return conteudo.length >= 2
            && (conteudo[0] & 0xFF) == 0xFF
            && (conteudo[1] & 0xE0) == 0xE0
            && (conteudo[1] & 0x06) == 0x02;
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
