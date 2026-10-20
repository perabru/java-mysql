-- Somente laboratorio local; esta senha e publica e didatica.
-- Execute como administrador. Se o usuario ja existir, mantenha sua senha
-- ou ajuste-a conscientemente com ALTER USER (ver README).
CREATE USER IF NOT EXISTS 'aluno_java'@'localhost'
  IDENTIFIED BY 'MaximusLocal_2026!';
GRANT SELECT, INSERT, UPDATE, DELETE ON escola_maximus.*
  TO 'aluno_java'@'localhost';
