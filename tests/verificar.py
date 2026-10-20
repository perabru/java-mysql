"""Integracao das 10 respostas. Use somente banco descartavel e vazio."""
import os
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MYSQL = [os.environ.get('MYSQL_BIN', 'mysql'), '--no-defaults', '--protocol=TCP',
         '-h127.0.0.1', '-u' + os.environ.get('MYSQL_TEST_USER', 'root'),
         '--batch', '--skip-column-names', 'escola_maximus']
CP = os.pathsep.join([str(ROOT / 'target/classes'), str(ROOT / 'target/dependency/*')])
checks = 0


def sql(query):
    return subprocess.run(MYSQL, input=query, text=True, capture_output=True,
                          check=True).stdout.strip()


def check(value, label):
    global checks
    if not value:
        raise AssertionError(label)
    checks += 1
    print('OK:', label, flush=True)


def run(number, data='', expected=None):
    result = subprocess.run(['java', '-cp', CP, f'Exercicio{number:02}'],
                            input=data, text=True, capture_output=True,
                            cwd=ROOT, timeout=20)
    if result.returncode:
        raise AssertionError(result.stdout + result.stderr)
    if expected is not None:
        check(expected in result.stdout, f'Exercicio {number:02}: {expected}')
    return result.stdout


check(sql('SELECT COUNT(*) FROM alunos') == '0', 'Base de testes vazia')
run(1, expected='Conectado: escola_maximus')
run(7, expected='Total: 0')
run(2, expected='Cadastrado: Ana')
check(sql('SELECT COUNT(*) FROM alunos') == '3', 'Tres cadastros')
run(2, expected='E-mail ja cadastrado:')
check(sql('SELECT COUNT(*) FROM alunos') == '3', 'Sem duplicar e-mails')
ana = sql("SELECT id FROM alunos WHERE nome='Ana'")
run(3, ana + '\n', 'ana@example.com')
run(3, '999999\n', 'Aluno nao encontrado.')
run(4, 'a\n', 'Carla')
run(4, 'zzzz\n', 'Nenhum aluno.')
run(4, "'); DROP TABLE alunos; --\n", 'Nenhum aluno.')
run(5, ana + '\nnovo@example.com\n', 'Atualizado!')
check(sql(f'SELECT nome FROM alunos WHERE id={ana}') == 'Ana', 'Nome preservado')
run(5, ana + '\nbruno@example.com\n', 'E-mail ja cadastrado.')
run(5, ana + '\nemail-invalido\n', 'E-mail invalido.')
run(5, '999999\na@exemplo.com\n', 'ID inexistente.')
run(6, ana + '\nN\n', 'Cancelado.')
check(sql(f'SELECT COUNT(*) FROM alunos WHERE id={ana}') == '1', 'Cancelamento preserva aluno')
run(6, '999999\n', 'Aluno nao encontrado.')
run(6, ana + '\nS\n', 'Excluidos: 1')
run(7, expected='Total: 2')
run(2)
run(7, expected='Total: 3')
sql((ROOT / 'sql/03_turma.sql').read_text())
check(sql('SELECT COUNT(*) FROM alunos WHERE turma IS NULL') == '3', 'Migracao preserva alunos antigos')
run(8, '1\nJoao\njoao@example.com\n1A\n', 'Registros gravados: 1')
joao = sql("SELECT id FROM alunos WHERE email='joao@example.com'")
run(8, '3\n' + joao + '\nJoao Novo\njoao@example.com\n2B\n', 'Registros gravados: 1')
run(8, '2\n', '2B')
check(sql(f'SELECT turma FROM alunos WHERE id={joao}') == '2B', 'Turma atualizada')
run(8, '1\nTeste\nt@example.com\n' + 'x' * 21 + '\n', 'Dados invalidos.')
# Cinco erros, depois um cadastro valido, no mesmo processo/menu.
data = ('1\nA\na@example.com\n1\nAna\n\n2\n-1\n2\nabc\n'
        '1\nDuplicado\nbruno@example.com\n'
        '1\nValido\nvalido@example.com\n0\n')
out = run(9, data, 'Cadastrado!')
check(out.count('Nome ou e-mail invalido.') == 2, 'Nome curto e e-mail vazio')
check('ID deve ser positivo.' in out, 'ID negativo rejeitado')
check('Digite um numero inteiro.' in out, 'ID com texto tratado')
check('Erro no banco:' in out, 'Duplicidade tratada sem fechar menu')
check(sql("SELECT COUNT(*) FROM alunos WHERE email='valido@example.com'") == '1', 'Cadastro apos os erros')
sql((ROOT / 'sql/04_produtos.sql').read_text())
run(10, '1\nCaderno\n12.50\n3\n', 'Gravado!')
produto = sql("SELECT id FROM produtos WHERE nome='Caderno'")
run(10, '2\n', '12.50')
run(10, '3\n' + produto + '\nCaderno Novo\n15,90\n5\n', 'Gravado!')
check(sql(f'SELECT preco FROM produtos WHERE id={produto}') == '15.90', 'BigDecimal preserva centavos')
run(10, '1\nTeste\n-1\n2\n', 'Dados invalidos;')
run(10, '1\nTeste\n1\n-2\n', 'Dados invalidos;')
run(10, '1\nTeste\n1.234\n2\n', 'Dados invalidos;')
run(10, '1\nTeste\nabc\n', 'Numero invalido.')
run(10, '3\n999999\nTeste\n1\n1\n', 'ID inexistente.')
run(10, '4\n999999\nS\n', 'ID inexistente.')
run(10, '4\n' + produto + '\nN\n')
check(sql('SELECT COUNT(*) FROM produtos') == '1', 'Cancelar exclusao de produto')
run(10, '4\n' + produto + '\nS\n', 'Excluido!')
check(sql('SELECT COUNT(*) FROM produtos') == '0', 'Produto excluido')
print(f'APROVADO: {checks} verificacoes nas 10 respostas.', flush=True)
