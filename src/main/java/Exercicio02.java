import java.sql.*;

public class Exercicio02 {
    public static void main(String[] args) throws SQLException {
        String[] nomes = {"Ana", "Bruno", "Carla"};
        String[] emails = {"ana@example.com", "bruno@example.com",
                           "carla@example.com"};
        String sql = "INSERT INTO alunos (nome, email) VALUES (?, ?)";
        try (Connection c = Conexao.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < nomes.length; i++) {
                ps.setString(1, nomes[i]);
                ps.setString(2, emails[i]);
                try {
                    ps.executeUpdate();
                    System.out.println("Cadastrado: " + nomes[i]);
                } catch (SQLException e) {
                    if (e.getErrorCode() != 1062) throw e;
                    System.out.println("E-mail ja cadastrado: " + emails[i]);
                }
            }
        }
    }
}
