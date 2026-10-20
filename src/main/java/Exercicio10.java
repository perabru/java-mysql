import java.math.BigDecimal;
import java.sql.*;
import java.util.Scanner;

public class Exercicio10 {
    public static void main(String[] args) throws SQLException {
        try (Scanner teclado = new Scanner(System.in);
             Connection c = Conexao.conectar()) {
            System.out.println("1 Cadastrar | 2 Listar | 3 Alterar | 4 Excluir");
            String opcao = teclado.nextLine();
            if (opcao.equals("2")) {
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT id, nome, preco, estoque FROM produtos ORDER BY id");
                     ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.println(rs.getInt("id") + " | "
                            + rs.getString("nome") + " | R$ "
                            + rs.getBigDecimal("preco") + " | "
                            + rs.getInt("estoque"));
                    }
                }
                return;
            }
            if (!opcao.equals("1") && !opcao.equals("3") && !opcao.equals("4")) {
                System.out.println("Opcao invalida.");
                return;
            }
            int id = 0;
            if (!opcao.equals("1")) {
                System.out.print("ID: ");
                id = Integer.parseInt(teclado.nextLine());
                if (id <= 0) {
                    System.out.println("ID invalido.");
                    return;
                }
            }
            if (opcao.equals("4")) {
                System.out.print("Excluir? S/N: ");
                if (!teclado.nextLine().equalsIgnoreCase("S")) return;
                try (PreparedStatement ps = c.prepareStatement(
                        "DELETE FROM produtos WHERE id = ?")) {
                    ps.setInt(1, id);
                    int linhas = ps.executeUpdate();
                    System.out.println(linhas > 0 ? "Excluido!" : "ID inexistente.");
                }
                return;
            }
            System.out.print("Nome: ");
            String nome = teclado.nextLine().trim();
            System.out.print("Preco (ex.: 12.50): ");
            BigDecimal preco = new BigDecimal(teclado.nextLine().replace(",", "."));
            System.out.print("Estoque: ");
            int estoque = Integer.parseInt(teclado.nextLine());
            if (nome.isEmpty() || nome.length() > 100 || preco.signum() < 0
                    || preco.compareTo(new BigDecimal("99999999.99")) > 0
                    || preco.stripTrailingZeros().scale() > 2 || estoque < 0) {
                System.out.println("Dados invalidos; preco: ate 2 casas decimais.");
                return;
            }
            String sql = opcao.equals("1")
                ? "INSERT INTO produtos (nome, preco, estoque) VALUES (?, ?, ?)"
                : "UPDATE produtos SET nome = ?, preco = ?, estoque = ? WHERE id = ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, nome);
                ps.setBigDecimal(2, preco);
                ps.setInt(3, estoque);
                if (opcao.equals("3")) ps.setInt(4, id);
                int linhas = ps.executeUpdate();
                System.out.println(linhas > 0 ? "Gravado!" : "ID inexistente.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Numero invalido.");
        }
    }
}
