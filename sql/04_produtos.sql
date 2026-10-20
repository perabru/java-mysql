-- Execute como administrador antes do exercicio 10.
USE escola_maximus;
CREATE TABLE IF NOT EXISTS produtos (
  id INT PRIMARY KEY AUTO_INCREMENT,
  nome VARCHAR(100) NOT NULL,
  preco DECIMAL(10,2) NOT NULL CHECK (preco >= 0),
  estoque INT NOT NULL CHECK (estoque >= 0)
);
