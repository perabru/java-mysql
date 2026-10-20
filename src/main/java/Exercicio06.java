import java.sql.*;
import java.util.Scanner;

public class Exercicio06 {
    public static void main(String[] args) throws SQLException {
        try (Scanner teclado = new Scanner(System.in);
             Connection c = Conexao.conectar();
             PreparedStatement buscar = c.prepareStatement(
                 "SELECT nome, email FROM alunos WHERE id = ?");
             PreparedStatement excluir = c.prepareStatement(
                 "DELETE FROM alunos WHERE id = ?")) {
            System.out.print("ID: ");
            int id = Integer.parseInt(teclado.nextLine());
            buscar.setInt(1, id);
            try (ResultSet rs = buscar.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Aluno nao encontrado.");
                    return;
                }
                System.out.println(rs.getString("nome") + " - "
                    + rs.getString("email"));
            }
            System.out.print("Excluir? S/N: ");
            if (!teclado.nextLine().equalsIgnoreCase("S")) {
                System.out.println("Cancelado.");
                return;
            }
            excluir.setInt(1, id);
            System.out.println("Excluidos: " + excluir.executeUpdate());
        }
    }
}
