package com.gravadoracampista.model.entities.Avaliacao;

import com.gravadoracampista.model.entities.Album;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "Avaliacao_Album_Table")
public class Avaliacao_Album_Table {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_Album_id")
    private Album albumID;

    @Column(name = "Nota_Album")
    private double nota;
}