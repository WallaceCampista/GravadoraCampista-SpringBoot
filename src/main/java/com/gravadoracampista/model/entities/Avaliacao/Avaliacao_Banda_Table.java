package com.gravadoracampista.model.entities.Avaliacao;

import com.gravadoracampista.model.entities.Banda;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "Avaliacao_Banda_Table")
public class Avaliacao_Banda_Table {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "FK_Banda_id")
    private Banda bandaID;

    @Column(name = "Nota_Banda")
    private double nota;
}
