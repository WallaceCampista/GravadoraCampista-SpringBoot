package com.gravadoracampista.infra;

import com.gravadoracampista.dtos.request.AlbumRequestDto;
import com.gravadoracampista.dtos.request.AvaliacaoRequestDto;
import com.gravadoracampista.dtos.request.BandaRequestDto;
import com.gravadoracampista.dtos.request.MusicaRequestDto;
import com.gravadoracampista.model.entities.Album;
import com.gravadoracampista.model.entities.Banda;
import com.gravadoracampista.model.entities.Musica;
import com.gravadoracampista.repository.AlbumRepository;
import com.gravadoracampista.repository.BandaRepository;
import com.gravadoracampista.repository.MusicaRepository;
import com.gravadoracampista.service.AlbumService;
import com.gravadoracampista.service.AvaliacaoService;
import com.gravadoracampista.service.BandaService;
import com.gravadoracampista.service.MusicaService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CriaDadosExemplo implements CommandLineRunner {

  private final BandaRepository bandaRepository;
  private final AlbumRepository albumRepository;
  private final MusicaRepository musicaRepository;
  private final BandaService bandaService;
  private final AlbumService albumService;
  private final MusicaService musicaService;
  private final AvaliacaoService avaliacaoService;
  private final EntityManager entityManager;

  @Value("${api.dados-exemplo}")
  private boolean dadosExemplo;

  //SO POPULA UM BANCO NOVO (NENHUMA BANDA, NEM EXCLUIDA), PARA A HOME "TOP 3" NAO ABRIR VAZIA
  //PASSA PELOS SERVICES PARA SEGUIR AS MESMAS REGRAS DO CADASTRO PELA TELA
  //UMA TRANSACAO SO: SE A API PARAR NO MEIO DA CARGA, NADA FICA GRAVADO PELA METADE
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void run(String... args) {
    if (!dadosExemplo || bandaRepository.count() > 0) {
      return;
    }

    Banda mareAlta = banda("Maré Alta", "Rock alternativo nascido às margens do Paraíba do Sul.", 9, 8, 10);
    Album planicie = album(mareAlta, "Planície", "Disco de estreia, gravado ao vivo em estúdio.", 9, 8);
    musica(planicie, "Paraíba do Sul", "Faixa de abertura, guitarras longas e coro no refrão.", 245, 9, 10);
    musica(planicie, "Farol de São Thomé", "Balada sobre as férias na praia.", 212, 8);
    musica(planicie, "Lagoa Feia", "A mais pesada do disco.", 198, 7, 8);
    Album canavial = album(mareAlta, "Canavial Elétrico", "Segundo disco, com sintetizadores e letras sobre a cidade.", 8);
    musica(canavial, "Usina", "Single que abriu a turnê.", 230, 9);
    musica(canavial, "Trem de Ferro", "Instrumental com percussão de sucata.", 187, 7);

    Banda goytacazes = banda("Os Goytacazes", "Samba-rock com metais e muita percussão.", 8, 7);
    Album terraPlana = album(goytacazes, "Terra Plana", "Coletânea de sambas gravados nos bailes da cidade.", 8, 7);
    musica(terraPlana, "Avenida Pelinca", "Samba-rock para dançar em dupla.", 201, 8);
    musica(terraPlana, "Calçadão", "Partido-alto com participação do público.", 176, 7, 9);
    musica(terraPlana, "Jardim São Benedito", "Encerramento dos shows da banda.", 223, 8);

    Banda lunaCampos = banda("Luna Campos", "Cantora e compositora de MPB, voz e violão.", 10, 9);
    Album noturno = album(lunaCampos, "Noturno", "Álbum acústico gravado em uma única noite.", 10, 9);
    musica(noturno, "Lua de Atafona", "Canção que dá nome à turnê.", 254, 10);
    musica(noturno, "Areia Branca", "Dueto de voz e violoncelo.", 199, 9);

    Banda asfaltoQuente = banda("Asfalto Quente", "Punk rock rápido, de garagem.", 6, 5);
    Album tresAcordes = album(asfaltoQuente, "Três Acordes", "EP gravado em um fim de semana.", 6);
    musica(tresAcordes, "Ônibus Lotado", "Dois minutos de barulho.", 132, 6);
    musica(tresAcordes, "Sinal Fechado", "A mais curta do EP.", 118, 5);
    musica(tresAcordes, "Hora Extra", "Faixa escondida no fim do EP.", 141, 7);

    log.info("Banco vazio: criados dados de exemplo ({} bandas). Desligue com DADOS_EXEMPLO=false.", bandaRepository.count());
  }

  private Banda banda(String nome, String resumo, double... notas) {
    novoContexto();
    bandaService.registroDeBanda(new BandaRequestDto(nome, resumo));
    Banda banda = bandaRepository.findByNomeBandaAndExcluidoFalse(nome).orElseThrow();
    for (double nota : notas) {
      avaliacaoService.avaliarBanda(banda.getBandaId(), new AvaliacaoRequestDto(nota));
    }
    return banda;
  }

  private Album album(Banda banda, String nome, String resumo, double... notas) {
    novoContexto();
    albumService.registroDeAlbum(new AlbumRequestDto(banda.getBandaId(), nome, resumo));
    Album album = albumRepository.findByNomeAlbumAndExcluidoFalse(nome).orElseThrow();
    for (double nota : notas) {
      avaliacaoService.avaliarAlbum(album.getAlbumId(), new AvaliacaoRequestDto(nota));
    }
    return album;
  }

  private void musica(Album album, String nome, String resumo, double duracao, double... notas) {
    novoContexto();
    musicaService.registroDeMusica(new MusicaRequestDto(album.getAlbumId(), nome, resumo, duracao));
    Musica musica = musicaRepository.findByNomeMusicaAndExcluidoFalse(nome).orElseThrow();
    for (double nota : notas) {
      avaliacaoService.avaliarMusica(musica.getMusicaId(), new AvaliacaoRequestDto(nota));
    }
  }

  //NUMA TRANSACAO SO, UMA LISTA JA CARREGADA (EX.: MUSICAS DO ALBUM, SOMADAS NA DURACAO TOTAL) NAO VE OS
  //REGISTROS NOVOS; LIMPAR O CONTEXTO FAZ CADA PASSO LER DO BANCO, COMO NUMA CHAMADA FEITA PELA TELA
  private void novoContexto() {
    entityManager.flush();
    entityManager.clear();
  }
}
