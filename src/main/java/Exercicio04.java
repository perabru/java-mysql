import java.sql.*;
import java.util.Scanner;

public class Exercicio04 {
    public static void main(String[] args) throws SQLException {
        try (Scanner teclado = new Scanner(System.in)) {
            System.out.print("Parte do nome: ");
            String nome = teclado.nextLine();
            String sql = "SELECT id, nome FROM alunos "
                + "WHERE nome LIKE ? ORDER BY nome";
            try (Connection c = Conexao.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, "%" + nome + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    boolean encontrou = false;
                    while (rs.next()) {
                        encontrou = true;
                        System.out.println(rs.getInt("id") + " - "
                            + rs.getString("nome"));
                    }
                    if (!encontrou) System.out.println("Nenhum aluno.");
                }
            }
        }
    }
}
