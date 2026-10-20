package br.com.maximus;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Conexao {
    public static Connection conectar() throws SQLException {
        Properties config = new Properties();
        Path arquivo = Path.of("db.properties");
        try (Reader leitor = Files.newBufferedReader(
                arquivo, StandardCharsets.UTF_8)) {
            config.load(leitor);
        } catch (IOException e) {
            throw new SQLException(
                "Copie db.properties.example para db.properties "
                + "na raiz e confira o diretorio de trabalho.", e);
        }
        String url = config.getProperty("db.url");
        String usuario = config.getProperty("db.usuario");
        String senha = config.getProperty("db.senha");
        if (url == null || usuario == null || senha == null) {
            throw new SQLException(
                "Preencha db.url, db.usuario e db.senha.");
        }
        return DriverManager.getConnection(url, usuario, senha);
    }
}
