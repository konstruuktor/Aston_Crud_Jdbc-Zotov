INSERT INTO manufacturer(manufacturer_name)
VALUES ('BMW'),
       ('MERCEDES'),
       ('AUDI');

INSERT INTO model(model_name)
VALUES ('5 series'),
       ('7 series'),
       ('E coupe'),
       ('A5');


INSERT INTO market(manufacturer_id, model_id, price, amount)
VALUES (1, 1, 100000, 137),
       (1, 2, 200000, 99),
       (2, 3, 150000, 22),
       (3, 4, 90000, 5);



