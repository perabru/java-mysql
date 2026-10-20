package br.com.maximus;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class CadastroAlunos {
    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in, "UTF-8")) {
            while (true) {
                System.out.println("\nMAXIMUS | CADASTRO DE ALUNOS");
                System.out.println("1 Cadastrar | 2 Listar | 3 Alterar");
                System.out.println("4 Excluir   | 0 Sair");
                if (!entrada.hasNextLine()) return;
                String opcao = entrada.nextLine().trim();
                if (opcao.equals("0")) return;
                try {
                    switch (opcao) {
                        case "1": {
                            String nome = ler(entrada, "Nome: ");
                            String email = ler(entrada, "E-mail: ");
                            int id = AlunoDAO.inserir(nome, email);
                            System.out.println("Cadastrado! ID: " + id);
                            break;
                        }
                        case "2": {
                            List<String> alunos = AlunoDAO.listar();
                            if (alunos.isEmpty()) {
                                System.out.println("Nenhum aluno cadastrado.");
                            } else {
                                alunos.forEach(System.out::println);
                            }
                            break;
                        }
                        case "3": {
                            int id = Integer.parseInt(ler(entrada, "ID: "));
                            String nome = ler(entrada, "Novo nome: ");
                            String email = ler(entrada, "Novo e-mail: ");
                            boolean ok = AlunoDAO.atualizar(id, nome, email);
                            System.out.println(ok ? "Atualizado!" : "ID inexistente.");
                            break;
                        }
                        case "4": {
                            int id = Integer.parseInt(ler(entrada, "ID: "));
                            String sim = ler(entrada, "Confirmar exclusao? (S/N): ");
                            if (sim.equalsIgnoreCase("S")) {
                                boolean ok = AlunoDAO.excluir(id);
                                System.out.println(ok ? "Excluido!" : "ID inexistente.");
                            } else {
                                System.out.println("Exclusao cancelada.");
                            }
                            break;
                        }
                        default: System.out.println("Escolha uma opcao do menu.");
                    }
                } catch (SQLException e) {
                    if (e.getErrorCode() == 1062) {
                        System.err.println("Este e-mail ja esta cadastrado.");
                    } else {
                        System.err.println("Erro no banco: " + e.getMessage());
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Entrada invalida: " + e.getMessage());
                }
            }
        }
    }

    private static String ler(Scanner entrada, String mensagem) {
        System.out.print(mensagem);
        if (!entrada.hasNextLine()) {
            throw new IllegalArgumentException("Entrada encerrada.");
        }
        return entrada.nextLine().trim();
    }
}
