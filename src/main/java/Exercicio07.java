import java.sql.*;

public class Exercicio07 {
    public static void main(String[] args) throws SQLException {
        try (Connection c = Conexao.conectar();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT COUNT(*) AS total FROM alunos");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            System.out.println("Total: " + rs.getInt("total"));
        }
    }
}
