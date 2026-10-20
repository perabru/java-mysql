import java.sql.*;
import java.util.Scanner;

public class Exercicio08 {
    public static void main(String[] args) throws SQLException {
        try (Scanner teclado = new Scanner(System.in);
             Connection c = Conexao.conectar()) {
            System.out.println("1 Cadastrar | 2 Listar | 3 Alterar");
            String opcao = teclado.nextLine();
            if (opcao.equals("2")) {
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT id, nome, email, turma FROM alunos ORDER BY id");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.println(rs.getInt("id") + " | "
                            + rs.getString("nome") + " | "
                            + rs.getString("email") + " | "
                            + rs.getString("turma"));
                    }
                }
                return;
            }
            if (!opcao.equals("1") && !opcao.equals("3")) {
                System.out.println("Opcao invalida.");
                return;
            }
            int id = 0;
            if (opcao.equals("3")) {
                System.out.print("ID: ");
                id = Integer.parseInt(teclado.nextLine());
            }
            System.out.print("Nome: ");
            String nome = teclado.nextLine().trim();
            System.out.print("E-mail: ");
            String email = teclado.nextLine().trim();
            System.out.print("Turma: ");
            String turma = teclado.nextLine().trim();
            if (nome.isEmpty() || nome.length() > 100
                    || !email.contains("@") || email.length() > 150
                    || turma.isEmpty() || turma.length() > 20) {
                System.out.println("Dados invalidos. Turma: ate 20 caracteres.");
                return;
            }
            String sql = opcao.equals("1")
                ? "INSERT INTO alunos (nome, email, turma) VALUES (?, ?, ?)"
                : "UPDATE alunos SET nome = ?, email = ?, turma = ? WHERE id = ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, nome);
                ps.setString(2, email);
                ps.setString(3, turma);
                if (opcao.equals("3")) ps.setInt(4, id);
                System.out.println("Registros gravados: " + ps.executeUpdate());
            }
        }
    }
}
