CREATE TABLE manufacturer
(
    manufacturer_id  IDENTITY NOT NULL,
    manufacturer_name TEXT,
    PRIMARY KEY (manufacturer_id)
);

CREATE TABLE model
(
    model_id   IDENTITY NOT NULL,
    model_name TEXT,
    PRIMARY KEY (model_id)
);

CREATE TABLE market
(
    market_id       IDENTITY NOT NULL,
    manufacturer_id INTEGER,
    model_id       INTEGER,
    price         DOUBLE PRECISION,
    amount        INTEGER,
    PRIMARY KEY (market_id),
    FOREIGN KEY (manufacturer_id) REFERENCES manufacturer (manufacturer_id) ON DELETE CASCADE,
    FOREIGN KEY (model_id) REFERENCES model (model_id) ON DELETE CASCADE

);

