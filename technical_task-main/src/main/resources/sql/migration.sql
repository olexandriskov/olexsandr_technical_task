
CREATE TABLE fish_images (
    id int PRIMARY KEY NOT NULL AUTO_INCREMENT,
    file_name varchar(255) DEFAULT NULL,
    fish_id int DEFAULT NULL,
    CONSTRAINT fk_fish_instance FOREIGN KEY (fish_id) REFERENCES fish (id) ON DELETE CASCADE
);

INSERT INTO fish_images (file_name, fish_id)
SELECT image_file_name, id
FROM fish
WHERE image_file_name IS NOT NULL AND image_file_name != '';

ALTER TABLE fish CHANGE COLUMN image_file_name deprecated_image_file_name VARCHAR(255);