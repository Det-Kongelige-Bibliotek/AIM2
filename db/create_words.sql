CREATE TABLE words (
  id SERIAL NOT NULL,
  text_en text,
  text_da text,
  category text,
  status text,
  PRIMARY KEY (id)
);

CREATE TABLE images (
  id SERIAL NOT NULL,
  path text,
  cumulus_id text,
  category text,
  color text,
  ocr text,
  status text,
  PRIMARY KEY (id)
);


CREATE TABLE image_word (
  id SERIAL NOT NULL,
  image_id INTEGER REFERENCES images (id),
  word_id INTEGER REFERENCES words (id),
  confidence INTEGER
);
