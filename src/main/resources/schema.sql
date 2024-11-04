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
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

ALTER TABLE user_details
ADD COLUMN gender VARCHAR(15);

