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
