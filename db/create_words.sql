DROP TABLE IF EXISTS words;
CREATE TABLE words (
  id SERIAL NOT NULL,
  text_en VARCHAR(256),
  text_da VARCHAR(256),
  status VARCHAR(50),
  PRIMARY KEY (id)
);