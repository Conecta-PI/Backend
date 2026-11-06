-- Script de seed para MySQL do projeto Conecta
-- Objetivo: popular o banco com clientes, produtos e vendas reais para testes locais.
-- Observacao: as imagens sao armazenadas como URL (campo foto) para manter o banco leve e facil de navegar.
-- IMPORTANTE: rode apenas em base vazia. Fluxo: (1) inicie a API uma vez com APP_DEMO_DATA_ENABLED=false (spring.profiles.active=mysql) para criar as tabelas;
--   (2) pare a API e rode este script; (3) inicie a API normalmente (a carga automatica detecta a base populada e nao duplica nada).
-- Uso:
--   mysql -u root -p --default-character-set=utf8mb4 conecta < scripts\seed-mysql.sql
--   ou dentro do cliente MySQL: SOURCE scripts/seed-mysql.sql

CREATE DATABASE IF NOT EXISTS conecta CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE conecta;

-- Usuario responsavel para login do sistema
INSERT IGNORE INTO USUARIOS (id_usuario, nome, telefone, nome_negocio, email, senha_hash, data_criacao, cnpj, chave_pix, premium)
VALUES
  (1, 'Responsável Conecta', '(11) 99999-0001', 'Conecta Doces', 'responsavel@conecta.com', '$2a$10$QjT5SxZ5bCvdhVQwJ0ToEOGCkz0m2nM4oJ8l1Q2bzK7spmR6D1M6O', NOW(), '12.345.678/0001-90', 'responsavel@conecta.com', TRUE);

-- Clientes (acima de 20 registros)
INSERT IGNORE INTO CLIENTES (id_cliente, nome, telefone, email, observacoes)
VALUES
  (1, 'Maria Silva', '(11) 98765-4321', 'maria.silva@email.com', 'Alergia a amendoim'),
  (2, 'João Santos', '(11) 97654-3210', 'joao.santos@email.com', 'Prefere receber em horários comerciais'),
  (3, 'Ana Oliveira', '(11) 96543-2109', 'ana.oliveira@email.com', 'Prefere contato por mensagem'),
  (4, 'Carlos Lima', '(11) 95432-1098', 'carlos.lima@email.com', 'Confirmar endereço antes da entrega'),
  (5, 'Lúcia Mendes', '(11) 94321-0987', 'lucia.mendes@email.com', 'Sem observações especiais'),
  (6, 'Pedro Costa', '(11) 93210-9876', 'pedro.costa@email.com', 'Entrega após as 18h'),
  (7, 'Fernanda Rocha', '(11) 92109-8765', 'fernanda.rocha@email.com', 'Entregar após as 18h'),
  (8, 'Rafael Almeida', '(11) 91234-5561', 'rafael.almeida@email.com', 'Cliente recorrente'),
  (9, 'Beatriz Souza', '(11) 99876-1122', 'beatriz.souza@email.com', 'Pediu entrega pela manhã'),
  (10, 'Thiago Ferreira', '(11) 99123-3344', 'thiago.ferreira@email.com', 'Sem observações'),
  (11, 'Camila Santos', '(11) 98555-7788', 'camila.santos@email.com', 'Vai pagar por pix'),
  (12, 'Bruno Pereira', '(11) 97654-1188', 'bruno.pereira@email.com', 'Atenção ao endereço'),
  (13, 'Patrícia Rocha', '(11) 96541-5566', 'patricia.rocha@email.com', 'Prefere receber no fim de semana'),
  (14, 'Daniel Costa', '(11) 98771-4433', 'daniel.costa@email.com', 'Solicitou nota fiscal'),
  (15, 'Mariana Alves', '(11) 95432-8877', 'mariana.alves@email.com', 'Sem restrições'),
  (16, 'Leonardo Lima', '(11) 99888-3344', 'leonardo.lima@email.com', 'Cliente VIP'),
  (17, 'Tatiane Ribeiro', '(11) 97812-4422', 'tatiane.ribeiro@email.com', 'Entregar após 16h'),
  (18, 'Henrique Azevedo', '(11) 98666-3355', 'henrique.azevedo@email.com', 'Prefere pagamento em dinheiro'),
  (19, 'Gabriela Nunes', '(11) 93214-7766', 'gabriela.nunes@email.com', 'Sem observações especiais'),
  (20, 'Vinícius Mendes', '(11) 91122-3344', 'vinicius.mendes@email.com', 'Entrega em loja'),
  (21, 'Larissa Dias', '(11) 94455-7788', 'larissa.dias@email.com', 'Quero acompanhamento por WhatsApp'),
  (22, 'Eduardo Cardoso', '(11) 92345-6655', 'eduardo.cardoso@email.com', 'Sem observações'),
  (23, 'Sofia Martins', '(11) 93777-1122', 'sofia.martins@email.com', 'Pediu produto sem glúten'),
  (24, 'Mateus Rocha', '(11) 95544-8899', 'mateus.rocha@email.com', 'Cliente frequente'),
  (25, 'Isabela Ferreira', '(11) 90222-7766', 'isabela.ferreira@email.com', 'Destinatária preferencial');

-- Produtos (acima de 20 registros)
INSERT IGNORE INTO PRODUTOS (id_produto, nome_produto, descricao, preco_custo, preco_venda, categoria, estoque, foto, preparo, rendimento, ingredientes, ativo)
VALUES
  (1, 'Bolo de Pote Chocolate', 'Bolo de pote com chocolate e cobertura cremosa.', 9.50, 18.90, 'Bolo de pote', 25, '/images/produtos/bolo-pote-chocolate.png', 'Levar à geladeira antes de servir.', '1 unidade', 'Chocolate, leite condensado, massa de bolo, cobertura.', TRUE),
  (2, 'Bolo de Pote Morango', 'Bolo de pote com morango e creme suave.', 9.20, 19.90, 'Bolo de pote', 18, '/images/produtos/bolo-pote-morango.png', 'Servir gelado.', '1 unidade', 'Morango, creme, massa de bolo, leite condensado.', TRUE),
  (3, 'Bolo de Pote Churros', 'Bolo de pote com sabor de churros.', 10.00, 21.00, 'Bolo de pote', 15, '/images/produtos/bolo-pote-churros.png', 'Servir gelado.', '1 unidade', 'Canela, açúcar, leite, chocolate.', TRUE),
  (4, 'Bolo de Pote Oreo', 'Bolo de pote com recheio de Oreo.', 10.50, 22.00, 'Bolo de pote', 12, '/images/produtos/Bolo de Pote Oreo.webp', 'Servir gelado.', '1 unidade', 'Oreo, creme, leite condensado.', TRUE),
  (5, 'Bolo de Pote Café', 'Bolo de pote com café e chantilly.', 9.80, 20.50, 'Bolo de pote', 16, '/images/produtos/Bolo de Pote Café.webp', 'Servir frio.', '1 unidade', 'Café, creme, massa de bolo.', TRUE),
  (6, 'Lanche Natural Frango', 'Lanche natural com frango, salada e molho especial.', 12.00, 28.00, 'Lanche', 22, '/images/produtos/Lanche Natural Frango.webp', 'Montar na hora e servir fresco.', '1 unidade', 'Frango desfiado, pão integral, alface, tomate, maionese.', TRUE),
  (7, 'Lanche Natural Atum', 'Lanche natural com atum, salada e molho leve.', 12.50, 29.00, 'Lanche', 20, '/images/produtos/lanche-natural-atum.png', 'Servir fresco.', '1 unidade', 'Atum, pão, rúcula, tomate, cebola roxa.', TRUE),
  (8, 'Lanche Natural Veggie', 'Lanche vegetariano com salada e molho.', 11.50, 26.00, 'Lanche', 19, '/images/produtos/Lanche Natural Veggie.webp', 'Servir fresco.', '1 unidade', 'Pão, alface, tomate, pepino, cream cheese.', TRUE),
  (9, 'Lanche Natural Presunto', 'Lanche natural com presunto e queijo.', 12.00, 27.50, 'Lanche', 18, '/images/produtos/Lanche Natural Presunto.webp', 'Servir fresco.', '1 unidade', 'Presunto, queijo, pão, alface, tomate.', TRUE),
  (10, 'Lanche Natural Peru', 'Lanche natural com peru e vegetais.', 13.00, 30.00, 'Lanche', 17, '/images/produtos/Lanche Natural Peru.webp', 'Servir fresco.', '1 unidade', 'Peru, pão integral, salada, molho.', TRUE),
  (11, 'Brigadeiro', 'Clássico brigadeiro com cacau.', 1.80, 6.50, 'Doce', 80, '/images/produtos/brigadeiro.png', 'Servir em embalagem individual.', '1 unidade', 'Leite condensado, chocolate em pó, manteiga.', TRUE),
  (12, 'Beijinho', 'Beijinho de coco.', 1.70, 6.00, 'Doce', 75, '/images/produtos/Beijinho.webp', 'Servir em embalagem individual.', '1 unidade', 'Leite condensado, coco ralado.', TRUE),
  (13, 'Cajuzinho', 'Cajuzinho com castanha.', 2.00, 7.00, 'Doce', 68, '/images/produtos/Cajuzinho.webp', 'Servir em embalagem individual.', '1 unidade', 'Leite condensado, castanha de caju.', TRUE),
  (14, 'Cookie de Chocolate', 'Cookie crocante por fora e macio por dentro.', 2.20, 8.50, 'Doce', 60, '/images/produtos/Cookie de Chocolate.webp', 'Servir em temperatura ambiente.', '1 unidade', 'Farinha, manteiga, chocolate, açúcar.', TRUE),
  (15, 'Cupcake de Morango', 'Cupcake com cobertura de morango.', 2.80, 10.00, 'Doce', 40, '/images/produtos/Cupcake de Morango.webp', 'Servir em embalagem individual.', '1 unidade', 'Massa de cupcake, morango, creme.', TRUE),
  (16, 'Cupcake de Baunilha', 'Cupcake clássico de baunilha.', 2.60, 9.50, 'Doce', 42, '/images/produtos/Cupcake de Baunilha.webp', 'Servir em temperatura ambiente.', '1 unidade', 'Massa de baunilha, creme, essência.', TRUE),
  (17, 'Brownie', 'Brownie de chocolate intenso.', 3.00, 11.00, 'Doce', 32, '/images/produtos/Brownie.webp', 'Servir em temperatura ambiente.', '1 unidade', 'Chocolate, farinha, ovos, manteiga.', TRUE),
  (18, 'Pudim', 'Pudim de leite condensado.', 3.20, 12.00, 'Doce', 30, '/images/produtos/Pudim.webp', 'Servir gelado.', '1 unidade', 'Leite, ovos, açúcar, baunilha.', TRUE),
  (19, 'Quindim', 'Quindim tradicional.', 2.90, 10.50, 'Doce', 35, '/images/produtos/Quindim.webp', 'Servir em temperatura ambiente.', '1 unidade', 'Gemas, coco, açúcar.', TRUE),
  (20, 'Torta de Limão', 'Torta com recheio de limão.', 3.40, 13.00, 'Doce', 28, '/images/produtos/Torta de Limão.webp', 'Servir gelado.', '1 unidade', 'Limão, leite condensado, massa.', TRUE),
  (21, 'Torta de Morango', 'Torta de morango com creme.', 3.60, 14.00, 'Doce', 26, '/images/produtos/Torta de Morango.webp', 'Servir gelado.', '1 unidade', 'Morango, creme, leite condensado, massa.', TRUE),
  (22, 'Salgado de Frango', 'Salgado clássico de frango.', 2.50, 9.00, 'Salgado', 55, '/images/produtos/Salgado de Frango.webp', 'Reaquecer antes de servir.', '1 unidade', 'Frango, massa, cebola, alho.', TRUE),
  (23, 'Esfiha de Carne', 'Esfiha de carne com tempero de alho.', 2.40, 8.50, 'Salgado', 57, '/images/produtos/Esfiha de Carne.webp', 'Servir quente.', '1 unidade', 'Carne, cebola, temperos, massa.', TRUE),
  (24, 'Coxinha', 'Coxinha de frango crocante.', 2.70, 9.50, 'Salgado', 52, '/images/produtos/Coxinha.webp', 'Reaquecer antes de servir.', '1 unidade', 'Frango, massa, caldo, temperos.', TRUE),
  (25, 'Pastel de Queijo', 'Pastel recheado com queijo.', 2.30, 8.00, 'Salgado', 48, '/images/produtos/Pastel de Queijo.webp', 'Fritar ou assar antes de servir.', '1 unidade', 'Queijo, massa, ovo, sal.', TRUE);

-- Vendas e itens
INSERT IGNORE INTO VENDAS (id_venda, id_cliente, data_venda, valor_total, status_venda, forma_pagamento)
VALUES
  (1, 1, '2026-10-01 15:00:00', 39.80, 1, 0),
  (2, 2, '2026-10-02 15:07:00', 45.00, 0, 1),
  (3, 3, '2026-10-03 15:14:00', 52.80, 1, 2),
  (4, 4, '2026-10-04 15:21:00', 33.50, 0, 3),
  (5, 5, '2026-10-05 15:28:00', 41.20, 1, 4),
  (6, 6, '2026-10-06 15:35:00', 48.90, 0, 0),
  (7, 7, '2026-10-07 15:42:00', 38.40, 1, 1),
  (8, 8, '2026-10-08 15:49:00', 44.60, 0, 2),
  (9, 9, '2026-10-09 15:56:00', 50.00, 1, 3),
  (10, 10, '2026-10-10 16:03:00', 36.70, 0, 4),
  (11, 11, '2026-10-11 16:10:00', 43.00, 1, 0),
  (12, 12, '2026-10-12 16:17:00', 58.20, 1, 1),
  (13, 13, '2026-10-13 16:24:00', 31.80, 0, 2),
  (14, 14, '2026-10-14 16:31:00', 46.20, 1, 3),
  (15, 15, '2026-10-15 16:38:00', 60.30, 1, 4),
  (16, 16, '2026-10-16 16:45:00', 40.00, 0, 0),
  (17, 17, '2026-10-17 16:52:00', 54.10, 1, 1),
  (18, 18, '2026-10-18 16:59:00', 37.50, 0, 2),
  (19, 19, '2026-10-19 17:06:00', 49.90, 1, 3),
  (20, 20, '2026-10-20 17:13:00', 29.40, 1, 4),
  (21, 21, '2026-10-21 17:20:00', 35.65, 0, 0),
  (22, 22, '2026-10-22 17:27:00', 53.70, 1, 1),
  (23, 23, '2026-10-23 17:34:00', 42.10, 0, 2),
  (24, 24, '2026-10-24 17:41:00', 47.25, 1, 3),
  (25, 25, '2026-10-25 17:48:00', 62.80, 1, 4),
  (26, 1, '2026-10-26 17:55:00', 28.80, 0, 0),
  (27, 2, '2026-10-27 18:02:00', 61.40, 1, 1),
  (28, 3, '2026-10-28 18:09:00', 39.20, 0, 2),
  (29, 4, '2026-10-29 18:16:00', 44.90, 1, 3),
  (30, 5, '2026-10-30 18:23:00', 56.00, 1, 4);

INSERT IGNORE INTO ITENS_VENDA (id_item, id_venda, id_produto, quantidade, preco_unitario)
VALUES
  (1, 1, 1, 2, 18.90),
  (2, 1, 11, 1, 6.50),
  (3, 2, 6, 1, 28.00),
  (4, 2, 12, 1, 6.00),
  (5, 3, 2, 2, 19.90),
  (6, 3, 15, 1, 10.00),
  (7, 4, 22, 2, 9.00),
  (8, 4, 11, 1, 6.50),
  (9, 5, 7, 1, 29.00),
  (10, 5, 13, 1, 7.00),
  (11, 6, 3, 1, 21.00),
  (12, 6, 14, 2, 8.50),
  (13, 7, 9, 1, 27.50),
  (14, 7, 17, 1, 11.00),
  (15, 8, 5, 1, 20.50),
  (16, 8, 18, 1, 12.00),
  (17, 9, 10, 1, 30.00),
  (18, 9, 19, 1, 10.50),
  (19, 10, 4, 1, 22.00),
  (20, 10, 20, 1, 13.00),
  (21, 11, 8, 1, 26.00),
  (22, 11, 23, 1, 8.50),
  (23, 12, 1, 2, 18.90),
  (24, 12, 24, 1, 9.50),
  (25, 13, 21, 1, 14.00),
  (26, 13, 12, 1, 6.00),
  (27, 14, 7, 1, 29.00),
  (28, 14, 17, 1, 11.00),
  (29, 15, 6, 1, 28.00),
  (30, 15, 25, 1, 8.00),
  (31, 16, 10, 1, 30.00),
  (32, 16, 13, 1, 7.00),
  (33, 17, 3, 2, 21.00),
  (34, 17, 16, 1, 9.50),
  (35, 18, 23, 2, 8.50),
  (36, 18, 11, 1, 6.50),
  (37, 19, 5, 1, 20.50),
  (38, 19, 18, 1, 12.00),
  (39, 20, 2, 1, 19.90),
  (40, 20, 14, 1, 8.50),
  (41, 21, 22, 1, 9.00),
  (42, 21, 12, 1, 6.00),
  (43, 22, 9, 1, 27.50),
  (44, 22, 18, 1, 12.00),
  (45, 23, 8, 1, 26.00),
  (46, 23, 15, 1, 10.00),
  (47, 24, 7, 1, 29.00),
  (48, 24, 24, 1, 9.50),
  (49, 25, 10, 1, 30.00),
  (50, 25, 21, 1, 14.00),
  (51, 26, 1, 1, 18.90),
  (52, 26, 11, 1, 6.50),
  (53, 27, 6, 1, 28.00),
  (54, 27, 22, 2, 9.00),
  (55, 28, 3, 1, 21.00),
  (56, 28, 17, 1, 11.00),
  (57, 29, 2, 1, 19.90),
  (58, 29, 20, 1, 13.00),
  (59, 30, 6, 1, 28.00),
  (60, 30, 18, 1, 12.00);

-- Dados extras de encomendas e lista de compra para telas internas
INSERT IGNORE INTO ENCOMENDAS (id_encomenda, codigo, cliente_nome, status, data_pedido, data_entrega, valor_total, observacao)
VALUES
  (1, 'EC-2026-001', 'Maria Silva', 'pendente', '2026-09-27 18:00:00', '2026-09-30 18:00:00', 96.00, 'Entrega no bairro Centro.'),
  (2, 'EC-2026-002', 'João Santos', 'em_rota', '2026-09-26 15:00:00', '2026-09-28 14:00:00', 72.50, 'Entregar em horário comercial.'),
  (3, 'EC-2026-003', 'Ana Oliveira', 'entregue', '2026-09-25 10:30:00', '2026-09-26 12:00:00', 58.00, 'Pedido entregue com sucesso.');

INSERT IGNORE INTO ENCOMENDA_ITENS (id_encomenda, nome_produto, quantidade, preco_unitario)
VALUES
  (1, 'Bolo de Pote Chocolate', 2, 18.90),
  (1, 'Brigadeiro', 6, 6.50),
  (2, 'Lanche Natural Frango', 2, 28.00),
  (2, 'Cookie de Chocolate', 3, 8.50),
  (3, 'Bolo de Pote Morango', 2, 19.90),
  (3, 'Cupcake de Morango', 2, 10.00);

INSERT IGNORE INTO LISTA_COMPRAS (id_lista_compra, titulo, status, prioridade, data_criacao, data_conclusao, total_estimado)
VALUES
  (1, 'Compras da semana', 'aberta', 'alta', NOW(), NULL, 340.00),
  (2, 'Itens para festa', 'concluida', 'media', '2026-09-20 09:00:00', '2026-09-21 18:00:00', 210.00);

INSERT IGNORE INTO LISTA_COMPRA_ITENS (id_lista_compra, nome_produto, quantidade, preco_unitario, concluido)
VALUES
  (1, 'Chocolate em pó', 5, 12.50, FALSE),
  (1, 'Leite condensado', 10, 8.00, FALSE),
  (1, 'Massa para bolo', 4, 20.00, TRUE),
  (2, 'Copo descartável', 50, 1.20, TRUE),
  (2, 'Fita para presente', 8, 4.00, TRUE);

SELECT 'SEED_OK' AS status,
       (SELECT COUNT(*) FROM CLIENTES) AS total_clientes,
       (SELECT COUNT(*) FROM PRODUTOS) AS total_produtos,
       (SELECT COUNT(*) FROM VENDAS) AS total_vendas,
       (SELECT COUNT(*) FROM ITENS_VENDA) AS total_itens_venda;
