package br.com.maximus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {
    public static int inserir(String nome, String email)
            throws SQLException {
        validar(nome, email);
        String sql = "INSERT INTO alunos (nome, email) VALUES (?, ?)";
        try (Connection c = Conexao.conectar();
             PreparedStatement ps = c.prepareStatement(
                 sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nome.trim());
            ps.setString(2, email.trim());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("O banco nao retornou o ID.");
            }
        }
    }

    public static List<String> listar() throws SQLException {
        List<String> alunos = new ArrayList<>();
        String sql = "SELECT id, nome, email FROM alunos ORDER BY id";
        try (Connection c = Conexao.conectar();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                alunos.add(rs.getInt("id") + " | "
                    + rs.getString("nome") + " | "
                    + rs.getString("email"));
            }
        }
        return alunos;
    }

    public static boolean atualizar(int id, String nome, String email)
            throws SQLException {
        validar(nome, email);
        validarId(id);
        String sql = "UPDATE alunos SET nome = ?, email = ? WHERE id = ?";
        try (Connection c = Conexao.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nome.trim());
            ps.setString(2, email.trim());
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean excluir(int id) throws SQLException {
        validarId(id);
        String sql = "DELETE FROM alunos WHERE id = ?";
        try (Connection c = Conexao.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private static void validar(String nome, String email) {
        if (nome == null || nome.isBlank() || nome.trim().length() > 100) {
            throw new IllegalArgumentException("Nome: de 1 a 100 caracteres.");
        }
        if (email == null || email.isBlank()
                || email.trim().length() > 150
                || !email.trim().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Informe um e-mail valido.");
        }
    }

    private static void validarId(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID deve ser positivo.");
    }
}
