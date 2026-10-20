-- Sprint 3 - MOTIVA
-- dados de teste (rodar depois do script-criacao.sql)

INSERT INTO EquipeManutencao (nome) VALUES ('Alpha-1');
INSERT INTO EquipeManutencao (nome) VALUES ('Bravo-2');

-- os tres primeiros trechos sao os mesmos do Main da Sprint 2
-- (BR-116 km 10-15 umido 5.0 / km 15-20 seco 28.0 / km 20-25 umido com sensor 14.0)
-- possui_sensor: 1 = trecho inteligente
-- equipe so pode ficar em trecho critico (vegetacao >= 30)
INSERT INTO TrechoRodovia (nome_rodovia, km_inicial, km_final, tipo_clima, possui_sensor, nivel_vegetacao, id_equipe)
    VALUES ('BR-116', 10, 15, 'UMIDO', 0, 5.0, NULL);
INSERT INTO TrechoRodovia (nome_rodovia, km_inicial, km_final, tipo_clima, possui_sensor, nivel_vegetacao, id_equipe)
    VALUES ('BR-116', 15, 20, 'SECO', 0, 28.0, NULL);
INSERT INTO TrechoRodovia (nome_rodovia, km_inicial, km_final, tipo_clima, possui_sensor, nivel_vegetacao, id_equipe)
    VALUES ('BR-116', 20, 25, 'UMIDO', 1, 14.0, NULL);
INSERT INTO TrechoRodovia (nome_rodovia, km_inicial, km_final, tipo_clima, possui_sensor, nivel_vegetacao, id_equipe)
    VALUES ('BR-116', 25, 30, 'SECO', 0, 35.0, (SELECT id FROM EquipeManutencao WHERE nome = 'Alpha-1'));
INSERT INTO TrechoRodovia (nome_rodovia, km_inicial, km_final, tipo_clima, possui_sensor, nivel_vegetacao, id_equipe)
    VALUES ('BR-116', 30, 35, 'UMIDO', 1, 31.5, (SELECT id FROM EquipeManutencao WHERE nome = 'Bravo-2'));
INSERT INTO TrechoRodovia (nome_rodovia, km_inicial, km_final, tipo_clima, possui_sensor, nivel_vegetacao, id_equipe)
    VALUES ('BR-116', 35, 40, 'SECO', 1, 16.0, NULL);

-- intervencoes: roçada mecanizada nos criticos e pulverizacao no trecho de atencao
INSERT INTO IntervencaoOperacional (id_trecho, tipo) VALUES (
    (SELECT id FROM TrechoRodovia WHERE nome_rodovia = 'BR-116' AND km_inicial = 25), 'ROCADA_MECANIZADA');
INSERT INTO IntervencaoOperacional (id_trecho, tipo) VALUES (
    (SELECT id FROM TrechoRodovia WHERE nome_rodovia = 'BR-116' AND km_inicial = 30), 'ROCADA_MECANIZADA');
INSERT INTO IntervencaoOperacional (id_trecho, tipo) VALUES (
    (SELECT id FROM TrechoRodovia WHERE nome_rodovia = 'BR-116' AND km_inicial = 15), 'PULVERIZACAO');

COMMIT;
