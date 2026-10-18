package com.gravadoracampista.model.entities.Avaliacao;

import com.gravadoracampista.model.entities.Album;
import com.gravadoracampista.model.entities.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
//UMA NOTA POR USUARIO EM CADA ALBUM (O MYSQL ACEITA VARIAS LINHAS COM USUARIO NULO: AS AVALIACOES ANTIGAS)
@Table(name = "Avaliacao_Album_Table",
        uniqueConstraints = @UniqueConstraint(name = "UK_Avaliacao_Album_Usuario", columnNames = {"FK_Album_id", "FK_Usuario_id"}))
public class Avaliacao_Album_Table {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_Album_id")
    private Album albumID;

    //QUEM AVALIOU; FICA NULO NAS AVALIACOES FEITAS ANTES DA REGRA DE UMA NOTA POR USUARIO
    @ManyToOne
    @JoinColumn(name = "FK_Usuario_id")
    private Usuario usuario;

    @Column(name = "Nota_Album")
    private double nota;
}
