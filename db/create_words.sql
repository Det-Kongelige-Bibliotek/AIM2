CREATE TABLE words (
  id SERIAL NOT NULL,
  text_en text NOT NULL,
  text_da text,
  category text NOT NULL,
  status text,
  PRIMARY KEY (id),
  CONSTRAINT duplicate_words UNIQUE (text_en, category)
);

CREATE TABLE images (
  id SERIAL NOT NULL,
  path text,
  cumulus_id text,
  category text,
  color text,
  ocr text,
  status text,
  isFront boolean default 'true',
  PRIMARY KEY (id)
);


CREATE TABLE image_word (
  id SERIAL NOT NULL,
  image_id INTEGER REFERENCES images (id),
  word_id INTEGER REFERENCES words (id),
  confidence INTEGER
);
