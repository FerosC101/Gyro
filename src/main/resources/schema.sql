CREATE DATABASE `wandat`;

CREATE TABLE users (
    user_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
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
    exp_points INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE daily_routines (
    id INT AUTO_INCREMENT PRIMARY KEY,
    routine VARCHAR(255) NOT NULL
);

CREATE TABLE user_daily_routines (
    user_id BIGINT,
    routine_id INT,
    date DATE,
    completed BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (user_id, routine_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (routine_id) REFERENCES daily_routines(id)
);
