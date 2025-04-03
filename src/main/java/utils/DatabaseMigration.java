package utils;

import jakarta.persistence.EntityManager;

public class DatabaseMigration {
    public static void runMigrations(EntityManager em) {
        em.getTransaction().begin();

        // Verifica e cria tabelas se não existirem
        em.createNativeQuery(
                "CREATE TABLE IF NOT EXISTS Board (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "nome VARCHAR(255) NOT NULL)"
        ).executeUpdate();

        // (Adicione migrações para outras tabelas aqui)

        em.getTransaction().commit();
    }
}