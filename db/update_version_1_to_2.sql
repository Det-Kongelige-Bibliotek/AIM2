-- Update from version 1 to version 2, by adding the 'isFront' column to the images table.
ALTER TABLE images
ADD COLUMN isFront boolean default 'true';

