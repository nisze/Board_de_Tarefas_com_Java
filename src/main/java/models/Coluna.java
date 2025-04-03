package models;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "colunas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coluna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer ordem;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @OneToMany(mappedBy = "coluna", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dataCriacao ASC")
    private List<Card> cards = new ArrayList<>();


    // Método para adicionar card mantendo a integridade referencial
    public void adicionarCard(Card card) {
        cards.add(card);
        card.setColuna(this);
    }
}