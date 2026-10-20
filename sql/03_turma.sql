-- Execute como administrador, uma vez, antes do exercicio 8.
-- Preserva os alunos existentes: turma fica NULL nos registros antigos.
USE escola_maximus;
ALTER TABLE alunos ADD COLUMN turma VARCHAR(20);
