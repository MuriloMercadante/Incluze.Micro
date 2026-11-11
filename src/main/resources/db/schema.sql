-- =====================================================================
-- Incluze - schema do banco (Microsoft SQL Server / Azure SQL)
-- Versionado no repositório e executado na inicialização da aplicação
-- (spring.sql.init). Idempotente: só cria o que ainda não existe.
-- O Hibernate apenas VALIDA o schema (spring.jpa.hibernate.ddl-auto=validate).
-- =====================================================================

IF OBJECT_ID(N'dbo.usuarios', N'U') IS NULL CREATE TABLE usuarios (id BIGINT IDENTITY NOT NULL, nome VARCHAR(100) NOT NULL, email VARCHAR(150) NOT NULL, tipo_deficiencia VARCHAR(30) CHECK (tipo_deficiencia IN ('FISICA','VISUAL','AUDITIVA','INTELECTUAL','NEURODIVERGENTE','MULTIPLA','NENHUMA')), cidade VARCHAR(80), pontos INT NOT NULL, data_cadastro DATETIME2(7) NOT NULL, PRIMARY KEY (id));

IF OBJECT_ID(N'dbo.locais', N'U') IS NULL CREATE TABLE locais (id BIGINT IDENTITY NOT NULL, nome VARCHAR(120) NOT NULL, endereco VARCHAR(200) NOT NULL, bairro VARCHAR(80), cidade VARCHAR(80) NOT NULL, descricao VARCHAR(500), imagem_url VARCHAR(500), categoria VARCHAR(30) NOT NULL, possui_rampa BIT NOT NULL, possui_banheiro_adaptado BIT NOT NULL CONSTRAINT DF_locais_banheiro DEFAULT 0, possui_elevador BIT NOT NULL CONSTRAINT DF_locais_elevador DEFAULT 0, possui_piso_tatil BIT NOT NULL CONSTRAINT DF_locais_piso_tatil DEFAULT 0, atendimento_libras BIT NOT NULL, cardapio_braille BIT NOT NULL, aceita_cao_guia BIT NOT NULL, ambiente_baixo_estimulo BIT NOT NULL, nota_media FLOAT(53), total_avaliacoes INT NOT NULL CONSTRAINT DF_locais_total_avaliacoes DEFAULT 0, verificado BIT NOT NULL CONSTRAINT DF_locais_verificado DEFAULT 0, data_cadastro DATETIME2(7) NOT NULL, PRIMARY KEY (id));

IF OBJECT_ID(N'dbo.avaliacoes', N'U') IS NULL CREATE TABLE avaliacoes (id BIGINT IDENTITY NOT NULL, nota INT NOT NULL, comentario VARCHAR(500), acessibilidade_confirmada BIT NOT NULL, data_avaliacao DATETIME2(7) NOT NULL, usuario_id BIGINT NOT NULL, local_id BIGINT NOT NULL, PRIMARY KEY (id));

IF OBJECT_ID(N'dbo.alertas', N'U') IS NULL CREATE TABLE alertas (id BIGINT IDENTITY NOT NULL, titulo VARCHAR(120) NOT NULL, descricao VARCHAR(500), tipo VARCHAR(40) NOT NULL CHECK (tipo IN ('ELEVADOR_QUEBRADO','OBRA_NA_CALCADA','PISO_TATIL_OBSTRUIDO','RAMPA_BLOQUEADA','BANHEIRO_ACESSIVEL_INDISPONIVEL','INTERPRETE_LIBRAS_AUSENTE','OUTRO')), ativo BIT NOT NULL, data_criacao DATETIME2(7) NOT NULL, data_expiracao DATETIME2(7) NOT NULL, usuario_id BIGINT NOT NULL, local_id BIGINT NOT NULL, PRIMARY KEY (id));

-- E-mail único por usuário
IF OBJECT_ID(N'dbo.UKkfsp0s1tflm1cwlj8idhqsad0') IS NULL ALTER TABLE usuarios ADD CONSTRAINT UKkfsp0s1tflm1cwlj8idhqsad0 UNIQUE (email);

-- Relacionamentos: avaliacoes/alertas N -> 1 usuarios e N -> 1 locais
IF OBJECT_ID(N'dbo.FKss4928rp8e299uvvhaf2yaxon') IS NULL ALTER TABLE avaliacoes ADD CONSTRAINT FKss4928rp8e299uvvhaf2yaxon FOREIGN KEY (usuario_id) REFERENCES usuarios (id);
IF OBJECT_ID(N'dbo.FKkc5366xrbtc1bwpe0j28wrkh3') IS NULL ALTER TABLE avaliacoes ADD CONSTRAINT FKkc5366xrbtc1bwpe0j28wrkh3 FOREIGN KEY (local_id) REFERENCES locais (id);
IF OBJECT_ID(N'dbo.FKp8jyskjm9vnd9pn5ghns87sm') IS NULL ALTER TABLE alertas ADD CONSTRAINT FKp8jyskjm9vnd9pn5ghns87sm FOREIGN KEY (usuario_id) REFERENCES usuarios (id);
IF OBJECT_ID(N'dbo.FKn0gqpqikn62fsfh7g79qxnx0a') IS NULL ALTER TABLE alertas ADD CONSTRAINT FKn0gqpqikn62fsfh7g79qxnx0a FOREIGN KEY (local_id) REFERENCES locais (id);

-- ---------------------------------------------------------------------
-- Migração v2 (app mobile): novos atributos de locais em bancos existentes
-- ---------------------------------------------------------------------
IF COL_LENGTH(N'dbo.locais', N'bairro') IS NULL ALTER TABLE locais ADD bairro VARCHAR(80);
IF COL_LENGTH(N'dbo.locais', N'descricao') IS NULL ALTER TABLE locais ADD descricao VARCHAR(500);
IF COL_LENGTH(N'dbo.locais', N'imagem_url') IS NULL ALTER TABLE locais ADD imagem_url VARCHAR(500);
IF COL_LENGTH(N'dbo.locais', N'possui_banheiro_adaptado') IS NULL ALTER TABLE locais ADD possui_banheiro_adaptado BIT NOT NULL CONSTRAINT DF_locais_banheiro DEFAULT 0;
IF COL_LENGTH(N'dbo.locais', N'possui_elevador') IS NULL ALTER TABLE locais ADD possui_elevador BIT NOT NULL CONSTRAINT DF_locais_elevador DEFAULT 0;
IF COL_LENGTH(N'dbo.locais', N'possui_piso_tatil') IS NULL ALTER TABLE locais ADD possui_piso_tatil BIT NOT NULL CONSTRAINT DF_locais_piso_tatil DEFAULT 0;
IF COL_LENGTH(N'dbo.locais', N'total_avaliacoes') IS NULL ALTER TABLE locais ADD total_avaliacoes INT NOT NULL CONSTRAINT DF_locais_total_avaliacoes DEFAULT 0;
IF COL_LENGTH(N'dbo.locais', N'verificado') IS NULL ALTER TABLE locais ADD verificado BIT NOT NULL CONSTRAINT DF_locais_verificado DEFAULT 0;

-- Categorias: remove a regra antiga (sem nome fixo), migra HOSPITAL -> SAUDE e cria a regra nova
WHILE EXISTS (SELECT 1 FROM sys.check_constraints cc JOIN sys.columns c ON c.object_id = cc.parent_object_id AND c.column_id = cc.parent_column_id WHERE cc.parent_object_id = OBJECT_ID(N'dbo.locais') AND c.name = N'categoria' AND cc.name <> N'CK_locais_categoria') BEGIN DECLARE @ck SYSNAME = (SELECT TOP 1 cc.name FROM sys.check_constraints cc JOIN sys.columns c ON c.object_id = cc.parent_object_id AND c.column_id = cc.parent_column_id WHERE cc.parent_object_id = OBJECT_ID(N'dbo.locais') AND c.name = N'categoria' AND cc.name <> N'CK_locais_categoria') EXEC (N'ALTER TABLE locais DROP CONSTRAINT [' + @ck + N']') END;
UPDATE locais SET categoria = 'SAUDE' WHERE categoria = 'HOSPITAL';
IF OBJECT_ID(N'dbo.CK_locais_categoria') IS NULL ALTER TABLE locais ADD CONSTRAINT CK_locais_categoria CHECK (categoria IN ('RESTAURANTE','SAUDE','ORGAO_PUBLICO','TRANSPORTE','COMERCIO','EDUCACAO','CULTURA','ESPORTE','LAZER','OUTRO'));

-- Indicadores calculados a partir das avaliações (mantém consistência com a regra da Service)
UPDATE l SET total_avaliacoes = (SELECT COUNT(*) FROM avaliacoes a WHERE a.local_id = l.id), verificado = CASE WHEN (SELECT COUNT(*) FROM avaliacoes a WHERE a.local_id = l.id AND a.acessibilidade_confirmada = 1) >= 3 THEN 1 ELSE 0 END FROM locais l;

-- Índices para os filtros e para as checagens de vínculo
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_avaliacoes_local' AND object_id = OBJECT_ID(N'dbo.avaliacoes')) CREATE INDEX IX_avaliacoes_local ON avaliacoes (local_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_avaliacoes_usuario' AND object_id = OBJECT_ID(N'dbo.avaliacoes')) CREATE INDEX IX_avaliacoes_usuario ON avaliacoes (usuario_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_alertas_local' AND object_id = OBJECT_ID(N'dbo.alertas')) CREATE INDEX IX_alertas_local ON alertas (local_id);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_alertas_usuario' AND object_id = OBJECT_ID(N'dbo.alertas')) CREATE INDEX IX_alertas_usuario ON alertas (usuario_id);
