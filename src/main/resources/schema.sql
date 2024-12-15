CREATE DATABASE `wandat`;

CREATE TABLE users (
    user_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    exp INT DEFAULT(0)
);

CREATE TABLE credentials (
    credential_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNSIGNED NOT NULL,
    achievement_name VARCHAR(255) NOT NULL,
    description TEXT,
    category ENUM('personal', 'local', 'national', 'international') NOT NULL,
    date_achieved DATE,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE user_details (
    user_id BIGINT UNSIGNED PRIMARY KEY,
    full_name VARCHAR(100),
    birthday DATE,
    contact_number VARCHAR(15),
    email VARCHAR(100),
    age INT,
    height FLOAT,
    weight FLOAT,
    gender VARCHAR(15),
    profession VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE user_questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    question_text VARCHAR(255),
    question_type ENUM('scale', 'multiple_choice'),
    max_scale INT,
    choice_options VARCHAR(255)
);

CREATE TABLE job_experience (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    job_title VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    description TEXT,
    job_type VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE daily_routines (
    id INT AUTO_INCREMENT PRIMARY KEY,
    routine VARCHAR(255) NOT NULL
);

CREATE TABLE user_daily_routines (
    user_id BIGINT UNSIGNED,
    routine_id INT,
    date DATE,
    completed BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (user_id, routine_id, date),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (routine_id) REFERENCES daily_routines(id)
);

SET sql_mode = 'STRICT_ALL_TABLES';

ALTER TABLE user_details
ADD CONSTRAINT chk_age CHECK (age >= 0 AND age <= 120),
ADD CONSTRAINT chk_height CHECK (height >= 0.5 AND height <= 3.0),
ADD CONSTRAINT chk_weight CHECK (weight >= 10 AND weight <= 300);


ALTER TABLE users
    MODIFY COLUMN username VARCHAR(50) NOT NULL CHECK (CHAR_LENGTH(username) >= 5);

ALTER TABLE users
    MODIFY COLUMN password VARCHAR(100) NOT NULL CHECK (CHAR_LENGTH(password) >= 8);

ALTER TABLE credentials
    ADD CONSTRAINT chk_date_achieved CHECK (date_achieved <= CURRENT_DATE);

ALTER TABLE user_details
    ADD CONSTRAINT chk_age CHECK (age >= 0 AND age <= 120),
    ADD CONSTRAINT chk_height CHECK (height >= 0.5 AND height <= 3.0),
    ADD CONSTRAINT chk_weight CHECK (weight >= 10 AND weight <= 300);

ALTER TABLE user_details
    MODIFY COLUMN contact_number VARCHAR(15) NOT NULL,
    MODIFY COLUMN email VARCHAR(100) NOT NULL;

ALTER TABLE job_experience
    ADD CONSTRAINT chk_job_dates CHECK (end_date IS NULL OR end_date >= start_date);

ALTER TABLE user_questions
    ADD CONSTRAINT chk_max_scale CHECK (max_scale >= 1 AND max_scale <= 10);

ALTER TABLE user_questions
    ADD CONSTRAINT chk_choice_options CHECK (
        question_type = 'multiple_choice' AND choice_options IS NOT NULL
            OR question_type = 'scale' AND choice_options IS NULL
        );

ALTER TABLE user_daily_routines
    ADD CONSTRAINT chk_date CHECK (date <= CURRENT_DATE);


DELIMITER //

CREATE TRIGGER after_exp_update
    AFTER UPDATE ON users
    FOR EACH ROW
BEGIN
    IF NEW.exp != OLD.exp THEN
        INSERT INTO credentials (user_id, achievement_name, description, category, date_achieved)
        VALUES (NEW.user_id, 'EXP Update', CONCAT('EXP changed from ', OLD.exp, ' to ', NEW.exp), 'personal', CURRENT_DATE);
    END IF;
END;

//

CREATE TRIGGER after_birthday_update
    AFTER UPDATE ON user_details
    FOR EACH ROW
BEGIN
    IF NEW.birthday != OLD.birthday THEN
        UPDATE user_details
        SET age = TIMESTAMPDIFF(YEAR, NEW.birthday, CURDATE())
        WHERE user_id = NEW.user_id;
    END IF;
END;


CREATE TRIGGER after_user_delete
    AFTER DELETE ON users
    FOR EACH ROW
BEGIN
    UPDATE user_daily_routines
    SET completed = FALSE
    WHERE user_id = OLD.user_id;
END;
//
DELIMITER ;

INSERT INTO users (username, password) VALUES
    ('test_user', 'password123'),
    ('admin_user', 'securePassword!');

INSERT INTO daily_routines (routine) VALUES
    ('Morning Walk'), ('Study Session'), ('Evening Run');

UPDATE users
SET username = 'updated_user', password = 'newPassword456'
WHERE user_id = 1;

DROP TABLE users;