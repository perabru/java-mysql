import java.sql.*;

public class Exercicio01 {
    public static void main(String[] args) throws SQLException {
        try (Connection c = Conexao.conectar()) {
            System.out.println("Conectado: " + c.getCatalog());
        }
    }
}
