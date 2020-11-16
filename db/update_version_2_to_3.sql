-- Update from version 2 to version 3, by adding the unique constaint for the text_en and category to the word table.
ALTER TABLE words
ADD CONSTRAINT duplicate_words UNIQUE (text_en, category);
