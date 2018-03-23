insert into images (path,cumulus_id,category,status) VALUES ('four_legged_mammal_1.jpeg','qerfasdtrqfas','mammals','UNFINISHED');
insert into images (path,cumulus_id,category,status) VALUES ('four_legged_mammal_2.jpeg','qerty3dtrqfas','mammals','FINISHED');
insert into images (path,cumulus_id,category,status) VALUES ('hest.jpeg','qerty4dtrqfas','mammals','NEW');
INSERT INTO words (text_en,text_da,category,status) VALUES ('dog','hund','mammals','PENDING');
INSERT INTO words (text_en,text_da,category,status) VALUES ('cat','katt','mammals','PENDING');
INSERT INTO words (text_en,text_da,category,status) VALUES ('horse','hest','mammals','PENDING');
INSERT INTO words (text_en,text_da,category,status) VALUES ('camel','kamel','mammals','ACCEPTED');
INSERT INTO image_word (image_id, word_id, confidence) VALUES ( (SELECT id from images WHERE path='four_legged_mammal_2.jpeg'),(SELECT id from words  WHERE rext_en='camel' ),  75 );
INSERT INTO image_word (image_id, word_id, confidence) VALUES
    ( (SELECT id from images WHERE path='four_legged_mammal_1.jpeg'),
    (SELECT id from words  WHERE words.text_en='camel' ),  57 );
INSERT INTO image_word (image_id, word_id, confidence) VALUES
    ( (SELECT id from images WHERE path='four_legged_mammal_1.jpeg'),
    (SELECT id from words  WHERE words.text_en='horse' ),  57 );
INSERT INTO image_word (image_id, word_id, confidence) VALUES
    ( (SELECT id from images WHERE path='four_legged_mammal_1.jpeg'),
    (SELECT id from words  WHERE words.text_en='cat' ),  57 );
INSERT INTO image_word (image_id, word_id, confidence) VALUES
    ( (SELECT id from images WHERE path='hest.jpeg'),
    (SELECT id from words  WHERE words.text_en='horse' ),  95 );
INSERT INTO image_word (image_id, word_id, confidence) VALUES
    ( (SELECT id from images WHERE path='hest.jpeg'),
    (SELECT id from words  WHERE words.text_en='camel' ),  53 );
