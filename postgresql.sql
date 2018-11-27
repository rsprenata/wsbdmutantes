/***********************************
* CRIE UM BANCO CHAMADO wsmutantes *
************************************/

/***********************************
*        CRIAÇÃO DAS TABELAS       *
***********************************/

CREATE TABLE Usuario (
        id SERIAL,
        nome VARCHAR(128),
        login VARCHAR(128),
        senha VARCHAR(128),
        
        PRIMARY KEY (id),
	UNIQUE(login)
);

CREATE TABLE Mutante (
        nome VARCHAR(128),
        foto_path VARCHAR(128),
        usuario VARCHAR(128),
        
        PRIMARY KEY (nome)
);

CREATE TABLE Habilidades (
        mutante_nome VARCHAR(128),
        habilidade VARCHAR(128),
        
        FOREIGN KEY (mutante_nome) REFERENCES Mutante (nome)
);

/***********************************
*        INSERÇÃO DOS DADOS        *
***********************************/

INSERT INTO Usuario (nome, login, senha)
	VALUES ('Administrador', 'admin', '123');
