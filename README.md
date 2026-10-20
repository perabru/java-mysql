# Java + MySQL no Eclipse | Escola Máximu’s

Tutorial para começar do zero e criar um cadastro de alunos no console, com código Java simples, JDBC e MySQL. Material organizado pelo Prof. Bruno Michel Pera.

[![Verificação Java e MySQL](https://github.com/perabru/java-mysql/actions/workflows/verificar.yml/badge.svg)](https://github.com/perabru/java-mysql/actions/workflows/verificar.yml)

**[Baixar o PDF ilustrado](docs/Java_MySQL_Eclipse_Maximus.pdf)** · **[Baixar todos os códigos em ZIP](https://github.com/perabru/java-mysql/archive/refs/heads/main.zip)** · **[Ver os 10 exercícios](#10-exercícios-para-praticar)**

## O que está pronto

- Conexão com fechamento automático e configuração em arquivo local.
- Cadastro, listagem, alteração e exclusão com confirmação.
- `PreparedStatement`, e-mail único, validação básica e tratamento de erros.
- Scripts SQL que preservam os registros existentes.
- Teste de integração com 12 verificações no MySQL real e GitHub Actions.

## 1. Downloads oficiais

| Ferramenta | Download | Escolha / função |
|---|---|---|
| JDK | https://adoptium.net/temurin/releases/?version=21 | JDK 21 LTS para seu sistema; o código exige Java 17 ou superior |
| Eclipse | https://www.eclipse.org/downloads/packages/ | Eclipse IDE for Java Developers; mantenha o runtime exigido pelo instalador |
| MySQL Server | https://dev.mysql.com/downloads/mysql/8.4.html | MySQL Community Server 8.4 LTS |
| MySQL Workbench | https://dev.mysql.com/downloads/workbench/ | Editor SQL opcional; não substitui o servidor |
| Connector/J | https://dev.mysql.com/downloads/connector/j/ | Driver JDBC; download manual apenas no caminho sem Maven |
| Connector/J 9.7.0 | https://downloads.mysql.com/archives/c-j/ | Versão fixada no projeto; escolha 9.7.0 e Platform Independent |
| Maven | https://maven.apache.org/download.cgi | Opcional no terminal; o Eclipse já inclui integração Maven |

As páginas de download mudam. Este projeto fixa Connector/J **9.7.0** para permitir reproduzir a aula; não significa que seja a versão mais recente. Nunca baixe o driver de sites desconhecidos.

## 2. Instalar e verificar (Windows)

1. Instale o **JDK**, marque a opção de adicionar ao PATH e, se oferecido, JAVA_HOME. Abra um novo terminal e execute `java -version` e `javac -version`. Ambos devem indicar Java 17 ou superior.
2. Instale o Eclipse IDE for Java Developers e escolha uma pasta de workspace. Se a versão atual do Eclipse exigir outro Java para a própria IDE, utilize o runtime fornecido pelo instalador; o projeto pode continuar no JDK 21.
3. Instale o **MySQL Server**, conclua o MySQL Configurator, mantenha a porta **3306**, guarde a senha de `root` e configure o serviço para iniciar com o Windows. Deixe a autenticação padrão e TLS habilitado.
4. Opcionalmente instale o Workbench. Crie/abra a conexão local: hostname `localhost`, porta `3306`, usuário `root`, senha definida na instalação. Use **Test Connection**.
5. No Windows, `Win + R` → `services.msc` permite conferir se o serviço MySQL está em execução. O nome pode variar, como `MySQL84`.

**Servidor x ferramentas:** o servidor guarda os dados; Workbench edita SQL; Eclipse executa Java; Connector/J permite ao Java conversar com o servidor.

## 3. Criar o banco e o usuário

1. Baixe o ZIP deste repositório pelo botão acima e extraia em uma pasta permanente.
2. Abra o Workbench como `root`. Em **File → Open SQL Script**, abra `sql/01_banco.sql` e execute o script inteiro com o raio de execução. Ele cria `escola_maximus` e a tabela `alunos`.
3. Abra `sql/02_usuario_local.sql` e execute. Ele cria `aluno_java`, com permissão somente para consultar e modificar dados nesse banco.
4. Clique com o botão direito em **Schemas → Refresh All**. Abra `escola_maximus → Tables → alunos`.
5. Verifique com:

```sql
USE escola_maximus;
SHOW TABLES;
SELECT * FROM alunos;
```

Uma tabela vazia nesta etapa é normal. Campos: `id` automático, `nome` obrigatório (100 caracteres), `email` obrigatório e único (150 caracteres).

A senha `MaximusLocal_2026!` é **didática e pública**, somente para laboratório local. Não reutilize em servidores reais. Se o usuário já existir, o script preserva a senha anterior. Para redefini-la conscientemente como administrador:

```sql
ALTER USER 'aluno_java'@'localhost' IDENTIFIED BY 'SUA_NOVA_SENHA';
```

Depois ajuste a mesma senha no `db.properties`.

## 4. Abrir no Eclipse (caminho recomendado: Maven)

1. **File → Import → Maven → Existing Maven Projects → Next**.
2. Em **Root Directory**, selecione a pasta extraída que contém o `pom.xml`.
3. Marque o projeto e clique em **Finish**. Aguarde o download das dependências; esta etapa precisa de internet.
4. Em **Window → Preferences → Java → Installed JREs**, adicione/selecione seu JDK. Em **Project → Properties → Java Compiler**, use Java 17 ou superior; o POM compila para Java 17.
5. Se aparecer erro de dependência: botão direito no projeto → **Maven → Update Project → Force Update → OK**. Confira a internet e o proxy da instituição.
6. Na raiz do projeto, copie `db.properties.example` e renomeie a cópia para **db.properties**, sem `.txt` no final. Ajuste usuário/senha se você mudou o SQL.
7. O arquivo deve ficar ao lado do `pom.xml`, e não dentro de `src`. Ele é ignorado pelo Git para não publicar sua senha.

```properties
db.url=jdbc:mysql://localhost:3306/escola_maximus?sslMode=REQUIRED&connectTimeout=5000&socketTimeout=10000
db.usuario=aluno_java
db.senha=MaximusLocal_2026!
```

`sslMode=REQUIRED` exige conexão criptografada. No laboratório local funciona com o TLS padrão do MySQL. Em servidor remoto use `sslMode=VERIFY_IDENTITY`, hostname correto e certificado/CA confiável; REQUIRED não valida a identidade do servidor. Não desative TLS para contornar erros.

## 5. Testar a conexão

1. Abra `src/main/java → br.com.maximus → TestarConexao.java`.
2. Botão direito → **Run As → Java Application**.
3. No Console deve aparecer:

```text
Conectado ao banco: escola_maximus
```

Se faltar o arquivo de configuração, abra **Run → Run Configurations → Java Application → TestarConexao → Arguments → Working directory** e selecione a raiz deste projeto. Em **Common → Encoding**, use UTF-8 se necessário.

A classe `Conexao` lê os três valores do arquivo e usa `DriverManager.getConnection`. O driver é descoberto automaticamente; não é necessário `Class.forName`. `try (...)` fecha a conexão até quando ocorre erro.

## 6. Executar o cadastro

Abra `CadastroAlunos.java` → **Run As → Java Application**. Clique no Console para digitar.

```text
MAXIMUS | CADASTRO DE ALUNOS
1 Cadastrar | 2 Listar | 3 Alterar
4 Excluir   | 0 Sair
```

1. Digite `1`, depois um nome e um e-mail fictício; anote o ID retornado.
2. Digite `2` e confira o aluno na lista.
3. Digite `3`, o ID e novos dados; liste novamente.
4. Digite `4`, o ID e `S` para confirmar; `N` cancela.
5. Digite `0` para encerrar. Fechar o programa não apaga o banco.
6. No Workbench, execute novamente `SELECT * FROM escola_maximus.alunos;` para conferir a persistência.

Use apenas dados fictícios nos exercícios. Repetir um e-mail mostra uma mensagem sem encerrar o menu. IDs inexistentes também recebem uma mensagem. A validação de e-mail é básica e não verifica se a caixa postal existe.

## 7. Entender os arquivos

| Arquivo | Responsabilidade |
|---|---|
| `Conexao.java` | Ler configuração e abrir conexão |
| `TestarConexao.java` | Primeiro teste, sem CRUD |
| `AlunoDAO.java` | SQL de inserir, listar, alterar e excluir |
| `CadastroAlunos.java` | Menu e leitura do teclado |
| `sql/01_banco.sql` | Criar banco e tabela sem apagar dados |
| `sql/02_usuario_local.sql` | Criar usuário restrito para laboratório |
| `src/test/.../VerificarCRUD.java` | Verificação automatizada no MySQL real |

**DAO** significa Data Access Object: uma classe que reúne o acesso aos dados. No cadastro, `?` representa um valor. `setString`/`setInt` preenchem esses valores sem concatenar a entrada do usuário ao SQL. Os índices começam em 1. `executeUpdate()` executa INSERT/UPDATE/DELETE; `executeQuery()` retorna as linhas de SELECT. `ResultSet.next()` avança uma linha.

## 8. Alternativa sem Maven (JAR manual)

Use este caminho **em vez** da importação Maven, se a aula pedir um Java Project tradicional.

1. **File → New → Java Project**, nome `JavaMySQL`; use JDK 17 ou superior e não crie `module-info.java`.
2. Crie o pacote `br.com.maximus` dentro de `src` e copie para ele as quatro classes de `src/main/java/br/com/maximus` deste repositório.
3. Baixe o Connector/J 9.7.0 no arquivo oficial, opção **Platform Independent**, ZIP; extraia o arquivo `mysql-connector-j-9.7.0.jar`.
4. Crie uma pasta `lib` na raiz do projeto e copie o JAR para ela.
5. Botão direito no projeto → **Build Path → Configure Build Path → Libraries → Classpath → Add JARs**. Selecione o arquivo de `lib` → **Apply and Close**.
6. Copie `db.properties.example` para a raiz deste novo projeto e renomeie para `db.properties`.
7. Execute primeiro `TestarConexao`, depois `CadastroAlunos`. Não adicione o mesmo driver duas vezes.

## 9. Terminal e verificação automatizada (opcional)

Com JDK e Maven instalados, na raiz do projeto, depois dos scripts SQL e do `db.properties`:

```sh
mvn clean compile
mvn exec:java -Dexec.mainClass=br.com.maximus.TestarConexao
mvn exec:java
```

Teste de integração, que cria dados temporários e remove o aluno criado no final:

```sh
mvn test-compile
mvn exec:java -Dexec.mainClass=br.com.maximus.VerificarCRUD -Dexec.classpathScope=test
```

No PowerShell, coloque cada argumento `-D...` entre aspas se o shell alterar a interpretação. O resultado esperado é **APROVADO: 12 verificacoes.** Os testes cobrem CRUD, Unicode/apóstrofo, texto parecido com SQL, duplicidade, dados inválidos e IDs ausentes. `mvn test` sozinho não executa esse teste: rode o comando explícito acima. O GitHub Actions prepara um MySQL 8.4 e executa os mesmos comandos em cada push/PR.

## 10. Resolver problemas

| Mensagem / sintoma | O que conferir |
|---|---|
| `No suitable driver` | Maven Dependencies ou JAR em Classpath; URL deve começar com `jdbc:mysql:` |
| `Access denied` | Usuário, senha, host e execução de `02_usuario_local.sql`; usuário existente preserva a senha anterior |
| `Unknown database` | Executar `01_banco.sql`; nome deve ser `escola_maximus` |
| `Table ... doesn't exist` | Executar o script inteiro no banco correto |
| `Communications link failure` | Serviço MySQL ativo, hostname e porta 3306; não liberar a porta na internet |
| Erro de TLS | Habilitar TLS no servidor e usar Connector/J compatível; manter `sslMode=REQUIRED` local |
| `Public Key Retrieval is not allowed` | Conferir a URL fornecida e o TLS do servidor; não adicionar parâmetros inseguros automaticamente |
| `Copie db.properties...` | Arquivo na raiz, sem `.txt`, e Working directory correto |
| E-mail já cadastrado | Escolher outro e-mail fictício ou alterar o cadastro existente |
| Acentos incorretos | Projeto, arquivos e Console em UTF-8; tabela em utf8mb4 |
| `UnsupportedClassVersionError` | Runtime Java antigo; escolher JDK 17+ na execução |
| `module ... does not read java.sql` | Usar o exemplo sem `module-info.java` e driver no Classpath |

## 10 exercícios para praticar

Faça os exercícios em ordem, usando dados fictícios. Entregue o código, o SQL adicional quando houver e evidências de execução. Não há gabarito: o projeto base é o ponto de partida.

1. **Primeira conexão.** Instale as ferramentas, prepare banco/usuário e execute `TestarConexao`. Entregue uma captura do Console e explique host, porta e nome do banco sem mostrar sua senha.
2. **Três cadastros.** Cadastre três alunos pelo menu e liste todos. Confirme os mesmos registros com SELECT no Workbench. Os IDs devem ser diferentes.
3. **Buscar por ID.** Adicione a opção 5, que recebe um ID e mostra nome/e-mail. Use `WHERE id = ?`. Teste um ID existente e um inexistente.
4. **Pesquisar por nome.** Adicione busca parcial com `LIKE ?` e `ORDER BY nome`. Teste um termo que retorne vários alunos e outro que não retorne nenhum; não concatene o termo ao SQL.
5. **Atualizar e-mail.** Crie uma opção que altere apenas o e-mail de um aluno por ID. Preserve o nome. Teste sucesso, e-mail inválido e duplicado.
6. **Exclusão segura.** Antes de excluir, mostre os dados do aluno e peça confirmação. Com `N`, os dados devem permanecer; com `S`, o aluno deve desaparecer; trate ID ausente.
7. **Contar alunos.** Exiba o total usando `SELECT COUNT(*)`. Verifique o resultado antes e depois de cadastrar um aluno e no banco vazio de testes.
8. **Campo turma.** Acrescente `turma VARCHAR(20)` à tabela com ALTER TABLE e adapte cadastrar/listar/alterar. Preserve registros antigos e valide até 20 caracteres.
9. **Validação e erros.** Rejeite nome com apenas um caractere, e-mail vazio e IDs negativos. Mostre mensagens claras e mantenha o menu aberto após cada erro. Registre cinco casos de teste e seus resultados.
10. **Mini-sistema de produtos.** Crie uma tabela `produtos` com id, nome, preco DECIMAL(10,2) e estoque INT. Implemente CRUD com PreparedStatement, BigDecimal para dinheiro, proibição de valores negativos e confirmação para excluir. Entregue também o SQL e teste produto inexistente.

**Critérios de conclusão:** código compila, conexão usa configuração local, consultas recebem parâmetros, recursos são fechados e todos os casos solicitados foram demonstrados.

## Referências oficiais

- [JDBC e DriverManager](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/DriverManager.html)
- [PreparedStatement](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/PreparedStatement.html)
- [Connector/J: instalação](https://dev.mysql.com/doc/connector-j/en/connector-j-installing.html)
- [Connector/J: TLS](https://dev.mysql.com/doc/connector-j/en/connector-j-reference-using-ssl.html)
- [Downloads MySQL 8.4](https://dev.mysql.com/downloads/mysql/8.4.html)
- [Downloads Eclipse](https://www.eclipse.org/downloads/packages/)

Material didático para uso em aula. Senhas de exemplo são públicas; mantenha credenciais reais fora do Git. O PDF apresenta a conexão completa e trechos identificados do DAO; o repositório contém todas as classes completas.
