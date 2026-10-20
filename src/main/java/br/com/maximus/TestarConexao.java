package br.com.maximus;

import java.sql.Connection;
import java.sql.SQLException;

public class TestarConexao {
    public static void main(String[] args) {
        try (Connection conexao = Conexao.conectar()) {
            System.out.println("Conectado ao banco: "
                + conexao.getCatalog());
        } catch (SQLException e) {
            System.err.println("Falha: " + e.getMessage());
            System.exit(1);
        }
    }
}
