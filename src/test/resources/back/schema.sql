DROP TABLE IF EXISTS note;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS user_customer;
DROP TABLE IF EXISTS user_employee;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS inactive_employee;

CREATE TABLE inactive_employee (
    id BIGINT PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    department VARCHAR(50),
    original_employee_id INTEGER
);

CREATE TABLE employee (
    id BIGINT PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    department VARCHAR(50)
);

CREATE TABLE customer (
    id BIGINT PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    address VARCHAR(255),
    last_interaction_date DATE,
    employee_id BIGINT,
    FOREIGN KEY (employee_id) REFERENCES employee(id)
);

CREATE TABLE note (
    id BIGINT PRIMARY KEY,
    interaction_type VARCHAR(50),
    content TEXT,
    date DATE,
    customer_id BIGINT,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id BIGINT PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE user_role (
    user_id BIGINT REFERENCES users(id),
    role_id BIGINT REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE user_customer (
    user_id BIGINT REFERENCES users(id),
    customer_id BIGINT REFERENCES customer(id),
    PRIMARY KEY (user_id, customer_id)
);

CREATE TABLE user_employee (
    user_id BIGINT REFERENCES users(id),
    employee_id BIGINT REFERENCES employee(id),
    PRIMARY KEY (user_id, employee_id)
);
