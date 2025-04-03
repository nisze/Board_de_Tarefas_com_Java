import models.*;
import utils.*;
import jakarta.persistence.*;
import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static EntityManager em;

    public static void main(String[] args) {
        em = HibernateUtil.getEntityManager();
        DatabaseMigration.runMigrations(em);

        while (true) {
            printMainMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> createBoard();
                case 2 -> listAllBoards();
                case 3 -> manageBoard();
                case 4 -> {
                    HibernateUtil.close();
                    System.exit(0);
                }
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n=== GERENCIADOR DE BOARDS ===");
        System.out.println("1. Criar novo Board");
        System.out.println("2. Listar todos os Boards");
        System.out.println("3. Gerenciar Board existente");
        System.out.println("4. Sair");
        System.out.print("Escolha: ");
    }

    private static void createBoard() {
        System.out.print("\nNome do Board: ");
        String nome = scanner.nextLine();

        Board board = new Board();
        board.setNome(nome);

        // Cria colunas padrão
        createDefaultColumns(board);

        em.getTransaction().begin();
        em.persist(board);
        em.getTransaction().commit();

        System.out.println("Board criado com sucesso!");
    }

    private static void createDefaultColumns(Board board) {
        String[] names = {"To Do", "Doing", "Done", "Canceled"};
        String[] types = {"inicial", "pendente", "final", "cancelamento"};

        for (int i = 0; i < names.length; i++) {
            Coluna coluna = new Coluna();
            coluna.setNome(names[i]);
            coluna.setOrdem(i + 1);
            coluna.setBoard(board);
            board.getColunas().add(coluna);
        }
    }

    private static void listAllBoards() {
        List<Board> boards = em.createQuery("FROM Board", Board.class).getResultList();

        if (boards.isEmpty()) {
            System.out.println("\nNenhum board encontrado!");
            return;
        }

        System.out.println("\n=== LISTA DE BOARDS ===");
        boards.forEach(board -> {
            System.out.printf("\n%d. %s\n", board.getId(), board.getNome());
            board.getColunas().forEach(col ->
                    System.out.printf("   - %s \n", col.getNome()));
        });
    }

    private static void manageBoard() {
        listAllBoards();
        System.out.print("\nID do Board a gerenciar (0 para cancelar): ");
        Long boardId = scanner.nextLong();
        scanner.nextLine();

        if (boardId == 0) return;

        Board board = em.find(Board.class, boardId);
        if (board == null) {
            System.out.println("Board não encontrado!");
            return;
        }

        boolean managing = true;
        while (managing) {
            printBoardManagementMenu(board);
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addCardToBoard(board);
                case 2 -> listBoardCards(board);
                case 3 -> moveCard(board);
                case 4 -> toggleCardBlock(board);
                case 5 -> managing = false;
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private static void printBoardManagementMenu(Board board) {
        System.out.printf("\n=== GERENCIANDO: %s ===\n", board.getNome());
        System.out.println("1. Adicionar Card");
        System.out.println("2. Listar Cards");
        System.out.println("3. Mover Card");
        System.out.println("4. Bloquear/Desbloquear Card");
        System.out.println("5. Voltar");
        System.out.print("Escolha: ");
    }

    private static void addCardToBoard(Board board) {
        System.out.print("\nTítulo do Card: ");
        String titulo = scanner.nextLine();

        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();

        // Encontra coluna "To Do" (inicial)
        Coluna colunaInicial = board.getColunas().stream()
                .findFirst()
                .orElseThrow();

        Card card = new Card();
        card.setTitulo(titulo);
        card.setDescricao(descricao);
        card.setColuna(colunaInicial);

        em.getTransaction().begin();
        em.persist(card);
        colunaInicial.getCards().add(card);
        em.getTransaction().commit();

        System.out.println("Card adicionado com sucesso!");
    }

    private static void listBoardCards(Board board) {
        System.out.printf("\n=== CARDS NO BOARD: %s ===\n", board.getNome());
        board.getColunas().forEach(coluna -> {
            System.out.printf("\n[%s]\n", coluna.getNome());
            if (coluna.getCards().isEmpty()) {
                System.out.println("  (vazio)");
            } else {
                coluna.getCards().forEach(card -> {
                    String status = card.isBloqueado() ? " (BLOQUEADO)" : "";
                    System.out.printf("  %d. %s%s\n",
                            card.getId(), card.getTitulo(), status);
                });
            }
        });
    }

    private static void moveCard(Board board) {
        listBoardCards(board);
        System.out.print("\nID do Card a mover: ");
        Long cardId = scanner.nextLong();
        scanner.nextLine();

        Card card = em.find(Card.class, cardId);
        if (card == null || !card.getColuna().getBoard().equals(board)) {
            System.out.println("Card não encontrado neste board!");
            return;
        }

        if (card.isBloqueado()) {
            System.out.println("Card está bloqueado e não pode ser movido!");
            return;
        }

        System.out.println("\nPara qual coluna mover?");
        board.getColunas().forEach(col ->
                System.out.printf("%d. %s\n", col.getOrdem(), col.getNome()));

        System.out.print("Escolha: ");
        int colunaDestino = scanner.nextInt();
        scanner.nextLine();

        Coluna novaColuna = board.getColunas().get(colunaDestino - 1);

        em.getTransaction().begin();
        card.getColuna().getCards().remove(card);
        card.setColuna(novaColuna);
        novaColuna.getCards().add(card);
        em.getTransaction().commit();

        System.out.println("Card movido com sucesso!");
    }

    private static void toggleCardBlock(Board board) {
        listBoardCards(board);
        System.out.print("\nID do Card: ");
        Long cardId = scanner.nextLong();
        scanner.nextLine();

        Card card = em.find(Card.class, cardId);
        if (card == null || !card.getColuna().getBoard().equals(board)) {
            System.out.println("Card não encontrado neste board!");
            return;
        }

        if (card.isBloqueado()) {
            System.out.print("Motivo do desbloqueio: ");
            String motivo = scanner.nextLine();

            em.getTransaction().begin();
            card.getBloqueio().setDataDesbloqueio(new Date());
            card.getBloqueio().setMotivoDesbloqueio(motivo);
            card.setBloqueado(false);
            em.getTransaction().commit();

            System.out.println("Card desbloqueado!");
        } else {
            System.out.print("Motivo do bloqueio: ");
            String motivo = scanner.nextLine();

            Block bloqueio = new Block();
            bloqueio.setMotivo(motivo);
            bloqueio.setDataBloqueio(new Date());

            em.getTransaction().begin();
            em.persist(bloqueio);
            card.setBloqueio(bloqueio);
            card.setBloqueado(true);
            em.getTransaction().commit();

            System.out.println("Card bloqueado!");
        }
    }
}