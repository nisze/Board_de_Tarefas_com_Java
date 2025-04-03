package models;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "board")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC")
    private List<Coluna> colunas = new ArrayList<>();

    // Método para adicionar coluna mantendo a integridade referencial
    public void adicionarColuna(Coluna coluna) {
        colunas.add(coluna);
        coluna.setBoard(this);
    }
}