package com.gravadoracampista.model.entities.Avaliacao;

import com.gravadoracampista.model.entities.Musica;
import com.gravadoracampista.model.entities.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
//UMA NOTA POR USUARIO EM CADA MUSICA (O MYSQL ACEITA VARIAS LINHAS COM USUARIO NULO: AS AVALIACOES ANTIGAS)
@Table(name = "Avaliacao_Musica_Table",
        uniqueConstraints = @UniqueConstraint(name = "UK_Avaliacao_Musica_Usuario", columnNames = {"FK_Musica_id", "FK_Usuario_id"}))
public class Avaliacao_Musica_Table {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_Musica_id")
    private Musica musicaID;

    //QUEM AVALIOU; FICA NULO NAS AVALIACOES FEITAS ANTES DA REGRA DE UMA NOTA POR USUARIO
    @ManyToOne
    @JoinColumn(name = "FK_Usuario_id")
    private Usuario usuario;

    @Column(name = "Nota_Musica")
    private double nota;
}
