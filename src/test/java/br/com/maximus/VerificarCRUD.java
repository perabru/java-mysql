package br.com.maximus;

import java.sql.SQLException;
import java.util.UUID;

/** Teste de integracao: executa no MySQL real, sem mocks. */
public class VerificarCRUD {
    private static int verificacoes;

    public static void main(String[] args) throws Exception {
        String email = "teste-" + UUID.randomUUID() + "@example.com";
        int id = 0;
        try {
            String nome = "João D'Ávila'); DROP TABLE alunos; --";
            id = AlunoDAO.inserir(nome, email);
            conferir(id > 0, "ID gerado");
            conferir(AlunoDAO.listar().contains(id + " | " + nome + " | " + email),
                "Insercao, acentos e SQL tratado como dado");
            try {
                AlunoDAO.inserir("Duplicado", email);
                throw new AssertionError("Aceitou e-mail duplicado");
            } catch (SQLException e) {
                conferir(e.getErrorCode() == 1062, "E-mail unico");
            }
            conferir(AlunoDAO.atualizar(id, "Maria", email), "Atualizacao");
            conferir(AlunoDAO.listar().contains(id + " | Maria | " + email),
                "Dados atualizados persistidos");
            try {
                AlunoDAO.inserir(" ", email);
                throw new AssertionError("Aceitou nome vazio");
            } catch (IllegalArgumentException esperado) {
                conferir(true, "Nome vazio rejeitado");
            }
            try {
                AlunoDAO.inserir("Teste", "invalido");
                throw new AssertionError("Aceitou e-mail invalido");
            } catch (IllegalArgumentException esperado) {
                conferir(true, "E-mail invalido rejeitado");
            }
            conferir(AlunoDAO.excluir(id), "Exclusao");
            conferir(AlunoDAO.listar().stream().noneMatch(s -> s.endsWith(email)),
                "Exclusao persistida");
            conferir(!AlunoDAO.excluir(id), "Excluir ID inexistente");
            conferir(!AlunoDAO.atualizar(id, "Teste", email), "Alterar ID inexistente");
            try {
                AlunoDAO.excluir(0);
                throw new AssertionError("Aceitou ID zero");
            } catch (IllegalArgumentException esperado) {
                conferir(true, "ID invalido rejeitado");
            }
            System.out.println("APROVADO: " + verificacoes + " verificacoes.");
        } finally {
            if (id > 0) AlunoDAO.excluir(id);
        }
    }

    private static void conferir(boolean condicao, String descricao) {
        if (!condicao) throw new AssertionError(descricao);
        verificacoes++;
        System.out.println("OK: " + descricao);
    }
}
