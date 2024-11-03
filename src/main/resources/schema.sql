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