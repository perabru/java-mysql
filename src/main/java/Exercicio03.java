import java.sql.*;
import java.util.Scanner;

public class Exercicio03 {
    public static void main(String[] args) throws SQLException {
        try (Scanner teclado = new Scanner(System.in)) {
            System.out.print("ID do aluno: ");
            int id = Integer.parseInt(teclado.nextLine());
            String sql = "SELECT nome, email FROM alunos WHERE id = ?";
            try (Connection c = Conexao.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println(rs.getString("nome"));
                        System.out.println(rs.getString("email"));
                    } else {
                        System.out.println("Aluno nao encontrado.");
                    }
                }
            }
        }
    }
}
