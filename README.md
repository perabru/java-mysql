# Java + MySQL: versão simples | Escola Máximu’s

Uma classe de conexão e dez programas independentes. Abra um arquivo, execute e observe o resultado. Material do Prof. Bruno Michel Pera.

[Baixar PDF](docs/Java_MySQL_Eclipse_Maximus.pdf) · [Baixar códigos em ZIP](https://github.com/perabru/java-mysql/archive/refs/heads/main.zip) · [Respostas dos 10 exercícios](#respostas-dos-10-exercícios)

[![Testes](https://github.com/perabru/java-mysql/actions/workflows/verificar.yml/badge.svg)](https://github.com/perabru/java-mysql/actions/workflows/verificar.yml)

## 1. Baixe e instale

| Programa | Link oficial | Para que serve |
|---|---|---|
| JDK 21 LTS | https://adoptium.net/temurin/releases/?version=21 | Compilar e executar Java; o projeto aceita Java 17+ |
| Eclipse IDE for Java Developers | https://www.eclipse.org/downloads/packages/ | Escrever e executar os exemplos |
| MySQL Server 8.4 LTS | https://dev.mysql.com/downloads/mysql/8.4.html | Guardar os dados |
| MySQL Workbench | https://dev.mysql.com/downloads/workbench/ | Executar SQL e visualizar o banco |
| Connector/J | https://dev.mysql.com/downloads/connector/j/ | Driver de conexão; Maven baixa automaticamente |
| Connector/J 9.7.0 (arquivo) | https://downloads.mysql.com/archives/c-j/ | Para o caminho manual: 9.7.0, Platform Independent, ZIP |

Instale o JDK. No terminal, confira `java -version` e `javac -version`. Instale o Eclipse e mantenha o runtime exigido pelo instalador da IDE. Instale e configure o MySQL Server na porta **3306**, guarde a senha de `root` e mantenha a autenticação e TLS padrão. O Workbench sozinho não instala nem liga o servidor.

## 2. Prepare o banco

No Workbench, conecte como `root`, host `localhost`, porta `3306`. Use **File → Open SQL Script** e execute o script inteiro com o raio:

1. `sql/01_banco.sql`: cria `escola_maximus` e a tabela `alunos`.
2. `sql/02_usuario_local.sql`: cria o usuário da aula e suas permissões.

Os scripts não apagam registros existentes. Confira:

```sql
USE escola_maximus;
SHOW TABLES;
SELECT * FROM alunos;
```

Para o exercício 8, execute **uma vez**, como administrador, `sql/03_turma.sql`. Se disser `Duplicate column name`, a coluna já existe: não precisa executar de novo. Para o exercício 10, execute `sql/04_produtos.sql`.

## 3. Abra no Eclipse

1. Baixe o ZIP acima e extraia.
2. **File → Import → Maven → Existing Maven Projects**.
3. Selecione a pasta que contém `pom.xml` e clique em **Finish**.
4. Aguarde as dependências. Se necessário: botão direito no projeto → **Maven → Update Project**.
5. Abra `src/main/java → (default package) → Conexao.java`.
6. Confira as três variáveis: URL, usuário e senha.
7. Abra `Exercicio01.java` → botão direito → **Run As → Java Application**.

Não crie `module-info.java`. Os arquivos ficam no pacote padrão para reduzir os passos iniciais. O projeto não usa DAO, arquivo de propriedades, framework ou camadas adicionais.

### Conexão completa

```java
import java.sql.*;

public class Conexao {
    public static Connection conectar() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/escola_maximus"
            + "?sslMode=REQUIRED";
        String usuario = "aluno_java";
        String senha = "MaximusLocal_2026!"; // Senha publica da aula.
        return DriverManager.getConnection(url, usuario, senha);
    }
}
```

A senha acima é pública e didática, para o MySQL instalado no próprio computador. Para a aula, ela é a mesma do script 02. Se o usuário já existia, o script preserva a senha antiga; ajuste `senha` para o valor correto. Não coloque senhas reais no GitHub.

A URL exige TLS. Para outro computador/servidor use uma configuração adequada com `sslMode=VERIFY_IDENTITY` e certificado confiável; `REQUIRED` criptografa, mas não verifica a identidade do servidor.

### Entenda os cinco comandos principais

| Código | O que faz |
|---|---|
| `Conexao.conectar()` | Abre a conexão |
| `prepareStatement(sql)` | Prepara o SQL |
| `setString(1, valor)` | Preenche o primeiro `?` com texto |
| `executeUpdate()` | Executa INSERT, UPDATE ou DELETE |
| `executeQuery()` | Executa SELECT e retorna um ResultSet |

`try (...)` fecha os recursos automaticamente. `rs.next()` vai para a próxima linha. `throws SQLException` deixa o Java mostrar erros do banco; os primeiros exemplos priorizam a leitura do SQL. O tratamento com mensagens e repetição está no exercício 9. A validação de e-mail é propositalmente básica: presença de `@`, sem espaços e limite de tamanho quando indicado; não verifica domínio ou existência da caixa postal.

## 4. Se a aula pedir um Java Project sem Maven

1. **File → New → Java Project**. Use JDK 17+ e não crie `module-info.java`.
2. Copie `Conexao.java` e os exercícios de `src/main/java` para a pasta `src`, sem criar pacote.
3. Baixe e extraia o ZIP oficial do Connector/J 9.7.0. Copie o `.jar` para uma pasta `lib` do projeto.
4. Botão direito no projeto → **Build Path → Configure Build Path → Libraries → Classpath → Add JARs** → selecione o JAR → **Apply and Close**.
5. Execute `Exercicio01` como Java Application. Não misture as duas formas no mesmo projeto.

## 5. Ordem de execução

Primeiro 01, depois 02 para criar os três alunos. Rode 04 com busca vazia para visualizar os nomes e IDs antes de alterar ou excluir. Digite **um número por linha** nos campos ID. Nos exemplos curtos, texto no lugar de um número gera `NumberFormatException`; o exercício 9 ensina a tratar isso.

Cada execução de 08 e 10 faz uma operação. Execute novamente a classe para escolher outra. Só o exercício 9 mantém o menu aberto. Todos compartilham `Conexao.java`.

## Respostas dos 10 exercícios

As respostas abaixo são completas e idênticas aos arquivos `.java` do projeto. Cada bloco deve ser salvo no arquivo indicado. Não cole todas as classes no mesmo arquivo.

### 01. Primeira conexão

**Enunciado:** Instale o ambiente e conecte ao banco.

**Resposta:** Conectado: escola_maximus. Host é o computador do banco; porta é o ponto de acesso (3306); escola_maximus é o nome do banco.

Arquivo: [src/main/java/Exercicio01.java](src/main/java/Exercicio01.java)

```java
import java.sql.*;

public class Exercicio01 {
    public static void main(String[] args) throws SQLException {
        try (Connection c = Conexao.conectar()) {
            System.out.println("Conectado: " + c.getCatalog());
        }
    }
}
```

### 02. Três cadastros

**Enunciado:** Cadastre três alunos e confira os registros no Java e no Workbench.

**Resposta:** Execute 02 e depois 04 com uma linha vazia para listar todos. No Workbench, SELECT * FROM alunos mostra IDs distintos. Repetir 02 informa os e-mails já cadastrados sem duplicar registros.

Arquivo: [src/main/java/Exercicio02.java](src/main/java/Exercicio02.java)

```java
import java.sql.*;

public class Exercicio02 {
    public static void main(String[] args) throws SQLException {
        String[] nomes = {"Ana", "Bruno", "Carla"};
        String[] emails = {"ana@example.com", "bruno@example.com",
                           "carla@example.com"};
        String sql = "INSERT INTO alunos (nome, email) VALUES (?, ?)";
        try (Connection c = Conexao.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < nomes.length; i++) {
                ps.setString(1, nomes[i]);
                ps.setString(2, emails[i]);
                try {
                    ps.executeUpdate();
                    System.out.println("Cadastrado: " + nomes[i]);
                } catch (SQLException e) {
                    if (e.getErrorCode() != 1062) throw e;
                    System.out.println("E-mail ja cadastrado: " + emails[i]);
                }
            }
        }
    }
}
```

### 03. Buscar por ID

**Enunciado:** Mostre nome/e-mail de um aluno ou informe que o ID não existe.

**Resposta:** Digite o ID obtido no exercício 2. Depois teste 999999 para conferir a mensagem de aluno não encontrado.

Arquivo: [src/main/java/Exercicio03.java](src/main/java/Exercicio03.java)

```java
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
```

### 04. Pesquisar por nome

**Enunciado:** Busque parte do nome com LIKE e ordene por nome.

**Resposta:** Digite a para vários nomes, zzz para nenhum e deixe vazio para listar todos. O termo é passado ao parâmetro, não concatenado ao SQL. % e _ mantêm o significado de curingas de LIKE.

Arquivo: [src/main/java/Exercicio04.java](src/main/java/Exercicio04.java)

```java
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
```

### 05. Atualizar e-mail

**Enunciado:** Altere somente o e-mail, preserve o nome e trate e-mail inválido e repetido.

**Resposta:** Digite um ID existente e outro e-mail fictício. Teste sem @ e com um e-mail já cadastrado. O UPDATE altera apenas a coluna email.

Arquivo: [src/main/java/Exercicio05.java](src/main/java/Exercicio05.java)

```java
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
```

### 06. Exclusão segura

**Enunciado:** Mostre os dados, peça S/N e trate o ID ausente.

**Resposta:** Com N, o registro permanece. Com S, ele é excluído. Com 999999, aparece a mensagem de aluno não encontrado.

Arquivo: [src/main/java/Exercicio06.java](src/main/java/Exercicio06.java)

```java
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
```

### 07. Contar alunos

**Enunciado:** Use COUNT(*) e compare antes/depois de um cadastro e em banco de testes vazio.

**Resposta:** No banco novo, o total começa em zero. Depois de executar 02 pela primeira vez, deve ser três. Se você já alterou a base, o resultado corresponde aos registros atuais.

Arquivo: [src/main/java/Exercicio07.java](src/main/java/Exercicio07.java)

```java
import java.sql.*;

public class Exercicio07 {
    public static void main(String[] args) throws SQLException {
        try (Connection c = Conexao.conectar();
             PreparedStatement ps = c.prepareStatement(
                 "SELECT COUNT(*) AS total FROM alunos");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            System.out.println("Total: " + rs.getInt("total"));
        }
    }
}
```

### 08. Adicionar turma

**Enunciado:** Adicione turma VARCHAR(20) sem perder alunos e adapte cadastrar/listar/alterar.

**Resposta:** Execute o SQL abaixo uma vez como root. O programa tem as opções 1 cadastrar, 2 listar e 3 alterar. Até 20 caracteres são aceitos. Nos alunos antigos, turma fica NULL até ser preenchida.

```sql
-- Execute como administrador, uma vez, antes do exercicio 8.
-- Preserva os alunos existentes: turma fica NULL nos registros antigos.
USE escola_maximus;
ALTER TABLE alunos ADD COLUMN turma VARCHAR(20);
```

Arquivo: [src/main/java/Exercicio08.java](src/main/java/Exercicio08.java)

```java
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
```

### 09. Validações e erros

**Enunciado:** Rejeite nome com um caractere, e-mail vazio e ID negativo; teste cinco erros mantendo o menu aberto.

**Resposta:** O menu usa while e continue. A tabela após o código mostra cinco casos de teste. A entrada correta pode ser cadastrada logo depois de um erro.

Arquivo: [src/main/java/Exercicio09.java](src/main/java/Exercicio09.java)

```java
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
```

| Teste | O que digitar | Resultado |
|---|---|---|
| Nome curto | 1 → A → a@example.com | Nome ou e-mail inválido; volta ao menu |
| E-mail vazio | 1 → Ana → linha vazia | Nome ou e-mail inválido; volta ao menu |
| ID negativo | 2 → -1 | ID deve ser positivo; volta ao menu |
| ID com texto | 2 → abc | Digite um número inteiro; volta ao menu |
| E-mail duplicado | 1 → Outro Nome → e-mail já existente | Erro no banco pela chave única; volta ao menu |

### 10. Mini-sistema de produtos

**Enunciado:** Faça CRUD de produtos com BigDecimal, preço e estoque não negativos e confirmação de exclusão.

**Resposta:** Execute o SQL abaixo como root. Rode a classe para cadastrar, listar, alterar ou excluir. Teste preço negativo, estoque negativo e ID 999999 nas opções de alterar/excluir.

```sql
-- Execute como administrador antes do exercicio 10.
USE escola_maximus;
CREATE TABLE IF NOT EXISTS produtos (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nome VARCHAR(100) NOT NULL,
  preco DECIMAL(10,2) NOT NULL CHECK (preco >= 0),
  estoque INT NOT NULL CHECK (estoque >= 0)
);
```

Arquivo: [src/main/java/Exercicio10.java](src/main/java/Exercicio10.java)

```java
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
```

## 6. Executar pelo terminal (opcional)

Com Maven instalado, na raiz:

```sh
mvn compile
mvn exec:java -Dexec.mainClass=Exercicio01
mvn exec:java -Dexec.mainClass=Exercicio02
```

Troque o número para executar outra resposta. No PowerShell, se necessário, coloque `"-Dexec.mainClass=Exercicio01"` entre aspas.

## 7. Erros comuns

| Erro | Confira |
|---|---|
| No suitable driver | Maven Dependencies ou JAR no Classpath |
| Access denied | Usuário e senha da classe Conexao e script 02 |
| Unknown database | Execute sql/01_banco.sql |
| Coluna turma ausente | Execute sql/03_turma.sql uma vez como administrador |
| Tabela produtos ausente | Execute sql/04_produtos.sql |
| Communications link failure | Serviço MySQL ligado, host localhost e porta 3306 |
| Erro TLS | Mantenha TLS habilitado no servidor e use a URL fornecida |
| NumberFormatException | Digite um número no campo numérico; o exercício 9 mostra como tratar |

## 8. Verificação automática

O GitHub Actions compila todas as classes e testa as dez respostas contra MySQL 8.4. O arquivo `tests/verificar.py` verifica os dados reais no banco, as mensagens, as confirmações, as validações e a preservação dos alunos na migração de turma. Ele usa uma base descartável de testes: não execute esse verificador em um banco com dados reais.

A versão curta concentra cada operação em um único `main`. Os exercícios 8, 9 e 10 têm mais linhas porque incluem menus e validações pedidos no enunciado.

Referências: [JDBC](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/DriverManager.html) · [PreparedStatement](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/PreparedStatement.html) · [Connector/J](https://dev.mysql.com/doc/connector-j/en/connector-j-installing.html).
