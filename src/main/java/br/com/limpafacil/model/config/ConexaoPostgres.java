package br.com.limpafacil.model.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public final class ConexaoPostgres {
    private static final String URL = lerAmbiente("LIMPAFACIL_DB_URL", "jdbc:postgresql://localhost:5432/limpafacil");
    private static final String USUARIO = lerAmbiente("LIMPAFACIL_DB_USER", "postgres");
    private static final String SENHA = lerAmbiente("LIMPAFACIL_DB_PASSWORD", "postgres");

    private ConexaoPostgres() {
    }

    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    public static void inicializarSchema() {
        try (Connection conexao = obterConexao();
             Statement stmt = conexao.createStatement()) {
            String schema = lerSchema();
            for (String comando : schema.split(";")) {
                if (!comando.isBlank()) {
                    stmt.execute(comando);
                }
            }
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Erro ao inicializar schema do banco.", e);
        }
    }

    private static String lerAmbiente(String nome, String padrao) {
        String valor = System.getenv(nome);
        return valor == null || valor.isBlank() ? padrao : valor;
    }

    private static String lerSchema() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                ConexaoPostgres.class.getResourceAsStream("/db/schema.sql")))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
