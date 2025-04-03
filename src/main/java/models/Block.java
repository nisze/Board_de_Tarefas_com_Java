package models;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "blocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String motivo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date dataBloqueio;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataDesbloqueio;

    @Lob
    private String motivoDesbloqueio;

    // Método para registrar bloqueio
    public void registrarBloqueio(String motivo) {
        this.motivo = motivo;
        this.dataBloqueio = new Date();
        this.dataDesbloqueio = null;
        this.motivoDesbloqueio = null;
    }

    // Método para registrar desbloqueio
    public void registrarDesbloqueio(String motivo) {
        this.dataDesbloqueio = new Date();
        this.motivoDesbloqueio = motivo;
    }
}