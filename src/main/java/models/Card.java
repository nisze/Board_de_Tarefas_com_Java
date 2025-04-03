package models;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Lob
    private String descricao;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date dataCriacao = new Date();

    @Column(nullable = false)
    private boolean bloqueado = false;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "block_id")
    private Block bloqueio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coluna_id", nullable = false)
    private Coluna coluna;

    // Método para mover para outra coluna
    public void moverPara(Coluna novaColuna) {
        if (bloqueado) {
            throw new IllegalStateException("Não é possível mover um card bloqueado");
        }
        this.coluna = novaColuna;
    }

    // Método para bloquear o card
    public void bloquear(String motivo) {
        if (bloqueio == null) {
            bloqueio = new Block();
        }
        bloqueio.registrarBloqueio(motivo);
        bloqueado = true;
    }

    // Método para desbloquear o card
    public void desbloquear(String motivo) {
        if (bloqueio != null) {
            bloqueio.registrarDesbloqueio(motivo);
            bloqueado = false;
        }
    }
}