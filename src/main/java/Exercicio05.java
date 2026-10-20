import java.sql.*;
import java.util.Scanner;

public class Exercicio05 {
    public static void main(String[] args) throws SQLException {
        try (Scanner teclado = new Scanner(System.in)) {
            System.out.print("ID: ");
            int id = Integer.parseInt(teclado.nextLine());
            System.out.print("Novo e-mail: ");
            String email = teclado.nextLine().trim();
            if (!email.contains("@") || email.contains(" ")
                    || email.length() > 150) {
                System.out.println("E-mail invalido.");
                return;
            }
            String sql = "UPDATE alunos SET email = ? WHERE id = ?";
            try (Connection c = Conexao.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setInt(2, id);
                int linhas = ps.executeUpdate();
                System.out.println(linhas > 0 ? "Atualizado!" : "ID inexistente.");
            } catch (SQLException e) {
                if (e.getErrorCode() != 1062) throw e;
                System.out.println("E-mail ja cadastrado.");
            }
        }
    }
}
