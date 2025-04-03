# Board-de-tarefas

# Diagrama de Classes

```mermaid
classDiagram
    class Board {
        +int id
        +String nome
        +List<Coluna> colunas
        +void adicionarColuna(Coluna coluna)
        +void removerColuna(Coluna coluna)
    }

    class Coluna {
        +int id
        +String nome
        +int ordem
        +String tipo
        +List<Card> cards
        +void adicionarCard(Card card)
        +void removerCard(Card card)
    }

    class Card {
        +int id
        +String titulo
        +String descricao
        +Date dataCriacao
        +boolean bloqueado
        +Block bloqueio
        +void moverPara(Coluna coluna)
        +void bloquear(String motivo)
        +void desbloquear(String motivo)
    }

    class Block {
        +int id
        +String motivo
        +Date dataBloqueio
        +Date dataDesbloqueio
        +void registrarBloqueio(String motivo)
        +void registrarDesbloqueio(String motivo)
    }

    Board "1" --> "3..*" Coluna
    Coluna "1" --> "*" Card
    Card "0..1" --> "1" Block

