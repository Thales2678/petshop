CREATE TABLE IF NOT EXISTS usuario (
    id INTEGER PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL,
    senha VARCHAR(200) NOT NULL,
    ativo BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS cliente (
    id INTEGER PRIMARY KEY REFERENCES usuario (id),
    telefone VARCHAR(30) NOT NULL,
    endereco VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS funcionario (
    id INTEGER PRIMARY KEY REFERENCES usuario (id),
    matricula VARCHAR(30) NOT NULL,
    cargo VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS servico (
    id INTEGER PRIMARY KEY,
    nome VARCHAR(80) NOT NULL,
    descricao VARCHAR(300) NOT NULL,
    preco NUMERIC(10, 2) NOT NULL,
    duracao_minutos INTEGER NOT NULL,
    ativo BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS animal (
    id INTEGER PRIMARY KEY,
    nome VARCHAR(80) NOT NULL,
    especie VARCHAR(60) NOT NULL,
    raca VARCHAR(60) NOT NULL,
    idade INTEGER NOT NULL,
    ativo BOOLEAN NOT NULL,
    cliente_id INTEGER NOT NULL REFERENCES cliente (id)
);

CREATE TABLE IF NOT EXISTS agendamento (
    id INTEGER PRIMARY KEY,
    data DATE NOT NULL,
    horario TIME NOT NULL,
    status VARCHAR(10) NOT NULL,
    observacoes VARCHAR(300) NOT NULL,
    animal_id INTEGER NOT NULL REFERENCES animal (id),
    servico_id INTEGER NOT NULL REFERENCES servico (id),
    funcionario_id INTEGER NOT NULL REFERENCES funcionario (id)
);
