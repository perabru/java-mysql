import java.sql.*;
import java.util.Scanner;

public class Exercicio09 {
    public static void main(String[] args) {
        try (Scanner teclado = new Scanner(System.in)) {
            while (true) {
                System.out.println("1 Cadastrar | 2 Excluir | 0 Sair");
                if (!teclado.hasNextLine()) break;
                String opcao = teclado.nextLine();
                if (opcao.equals("0")) break;
                try (Connection c = Conexao.conectar()) {
                    if (opcao.equals("1")) {
                        System.out.print("Nome: ");
                        String nome = teclado.nextLine().trim();
                        System.out.print("E-mail: ");
                        String email = teclado.nextLine().trim();
                        if (nome.length() < 2 || nome.length() > 100
                                || !email.contains("@") || email.contains(" ")
                                || email.length() > 150) {
                            System.out.println("Nome ou e-mail invalido.");
                            continue;
                        }
                        try (PreparedStatement ps = c.prepareStatement(
                                "INSERT INTO alunos (nome, email) VALUES (?, ?)")) {
                            ps.setString(1, nome);
                            ps.setString(2, email);
                            ps.executeUpdate();
                            System.out.println("Cadastrado!");
                        }
                    } else if (opcao.equals("2")) {
                        System.out.print("ID: ");
                        int id = Integer.parseInt(teclado.nextLine());
                        if (id <= 0) {
                            System.out.println("ID deve ser positivo.");
                            continue;
                        }
                        System.out.print("Excluir? S/N: ");
                        if (!teclado.nextLine().equalsIgnoreCase("S")) continue;
                        try (PreparedStatement ps = c.prepareStatement(
                                "DELETE FROM alunos WHERE id = ?")) {
                            ps.setInt(1, id);
                            System.out.println("Excluidos: " + ps.executeUpdate());
                        }
                    } else {
                        System.out.println("Opcao invalida.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Digite um numero inteiro.");
                } catch (SQLException e) {
                    System.out.println("Erro no banco: " + e.getMessage());
                }
            }
        }
    }
}
