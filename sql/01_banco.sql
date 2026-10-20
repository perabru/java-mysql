-- Execute como administrador do MySQL. Nao apaga registros existentes.
CREATE DATABASE IF NOT EXISTS escola_maximus
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE escola_maximus;
CREATE TABLE IF NOT EXISTS alunos (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nome VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE
);
