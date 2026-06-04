package br.com.limpafacil.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexaoPostgres {
    private static final String URL = lerAmbiente("LIMPAFACIL_DB_URL", "jdbc:postgresql://localhost:5432/limpafacil");
    private static final String USUARIO = lerAmbiente("LIMPAFACIL_DB_USER", "postgres");
    private static final String SENHA = lerAmbiente("LIMPAFACIL_DB_PASSWORD", "postgres");

    private ConexaoPostgres() {
    }

    public static Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    private static String lerAmbiente(String nome, String padrao) {
        String valor = System.getenv(nome);
        return valor == null || valor.isBlank() ? padrao : valor;
    }
}
