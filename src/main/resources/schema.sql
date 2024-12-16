drop table IF EXISTS note;
drop table IF EXISTS user_role;
drop table IF EXISTS roles;
drop table IF EXISTS user_customer;
drop table IF EXISTS user_employee;
drop table IF EXISTS users;
drop table IF EXISTS customer;
drop table IF EXISTS employee;
drop table IF EXISTS inactive_employee;

--

Create Table inactive_employee (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    department VARCHAR(50),
    original_employee_id INTEGER
);

-- Create Employee Table
create TABLE employee (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    department VARCHAR(50)
);

-- Create Customer Table
create TABLE customer (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    address VARCHAR(255),
    last_interaction_date DATE,
    employee_id INTEGER,
    FOREIGN KEY (employee_id) REFERENCES employee(id)
);

-- Create Note Table
create TABLE note (
    id SERIAL PRIMARY KEY,
    interaction_type VARCHAR(50),
    content TEXT,
    date DATE,
    customer_id INTEGER,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

create TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create TABLE roles (
    id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) UNIQUE NOT NULL
);

create TABLE user_role (
    user_id BIGINT REFERENCES users(id),
    role_id BIGINT REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

create TABLE user_customer (
    user_id BIGINT REFERENCES users(id),
    customer_id BIGINT REFERENCES customer(id),
    PRIMARY KEY (user_id, customer_id)
);

create TABLE user_employee (
    user_id BIGINT REFERENCES users(id),
    employee_id BIGINT REFERENCES employee(id),
    PRIMARY KEY (user_id, employee_id)
);


