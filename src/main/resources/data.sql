-- Create Employees
INSERT INTO employee (first_name, last_name, email, department) VALUES
('John', 'Doe', 'john.doe@example.com', 'Sales'),
('Jane', 'Smith', 'jane.smith@example.com', 'Support'),
('Alice', 'Johnson', 'alice.johnson@example.com', 'Marketing');

-- Zusätzliche Mitarbeiter
--INSERT INTO employee (first_name, last_name, email, department) VALUES
--('TestVorname1', 'TestNachname1', 'test1@example.com', 'TestAbteilung1'),
--('TestVorname2', 'TestNachname2', 'test2@example.com', 'TestAbteilung2'),
--('TestVorname3', 'TestNachname3', 'test3@example.com', 'TestAbteilung3'),
--('TestVorname4', 'TestNachname4', 'test4@example.com', 'TestAbteilung4'),
--('TestVorname5', 'TestNachname5', 'test5@example.com', 'TestAbteilung5'),
--('TestVorname6', 'TestNachname6', 'test6@example.com', 'TestAbteilung1'),
--('TestVorname7', 'TestNachname7', 'test7@example.com', 'TestAbteilung2'),
--('TestVorname8', 'TestNachname8', 'test8@example.com', 'TestAbteilung3'),
--('TestVorname9', 'TestNachname9', 'test9@example.com', 'TestAbteilung4'),
--('TestVorname10', 'TestNachname10', 'test10@example.com', 'TestAbteilung5'),
--('TestVorname11', 'TestNachname11', 'test11@example.com', 'TestAbteilung1'),
--('TestVorname12', 'TestNachname12', 'test12@example.com', 'TestAbteilung2'),
--('TestVorname13', 'TestNachname13', 'test13@example.com', 'TestAbteilung3'),
--('TestVorname14', 'TestNachname14', 'test14@example.com', 'TestAbteilung4'),
--('TestVorname15', 'TestNachname15', 'test15@example.com', 'TestAbteilung5'),
--('TestVorname16', 'TestNachname16', 'test16@example.com', 'TestAbteilung1'),
--('TestVorname17', 'TestNachname17', 'test17@example.com', 'TestAbteilung2'),
--('TestVorname18', 'TestNachname18', 'test18@example.com', 'TestAbteilung3'),
--('TestVorname19', 'TestNachname19', 'test19@example.com', 'TestAbteilung4'),
--('TestVorname20', 'TestNachname20', 'test20@example.com', 'TestAbteilung5'),
--('TestVorname21', 'TestNachname21', 'test21@example.com', 'TestAbteilung1'),
--('TestVorname22', 'TestNachname22', 'test22@example.com', 'TestAbteilung2'),
--('TestVorname23', 'TestNachname23', 'test23@example.com', 'TestAbteilung3'),
--('TestVorname24', 'TestNachname24', 'test24@example.com', 'TestAbteilung4'),
--('TestVorname25', 'TestNachname25', 'test25@example.com', 'TestAbteilung5'),
--('TestVorname26', 'TestNachname26', 'test26@example.com', 'TestAbteilung1'),
--('TestVorname27', 'TestNachname27', 'test27@example.com', 'TestAbteilung2');

-- Create Customers
INSERT INTO customer (first_name, last_name, email, phone, address, last_interaction_date, employee_id) VALUES
('Max', 'Mustermann', 'max.mustermann@example.com', '1234567890', '123 Main St', '2024-10-22', 1),
('Erika', 'Musterfrau', 'erika.musterfrau@example.com', '2345678901', '456 Oak St', '2024-10-22', 1),
('Hans', 'Hansen', 'hans.hansen@example.com', '3456789012', '789 Pine St', '2024-10-22', 1),
('Petra', 'Petersen', 'petra.petersen@example.com', '4567890123', '101 Maple St', '2024-10-22', 2),
('Klaus', 'Klaussen', 'klaus.klaussen@example.com', '5678901234', '202 Birch St', '2024-10-22', 2),
('Lisa', 'Larsen', 'lisa.larsen@example.com', '6789012345', '303 Cedar St', '2024-10-22', 2),
('Frank', 'Franken', 'frank.franken@example.com', '7890123456', '404 Spruce St', '2024-10-22', 3),
('Sandra', 'Sanders', 'sandra.sanders@example.com', '8901234567', '505 Elm St', '2024-10-22', 3),
('Gerd', 'Gerdsen', 'gerd.gerdsen@example.com', '9012345678', '606 Fir St', '2024-10-22', 3),
('Ute', 'Utens', 'ute.utens@example.com', '0123456789', '707 Walnut St', '2024-10-22', 3);

-- Create Notes
INSERT INTO note (interaction_type, content, date, customer_id) VALUES
('MEETING', 'Meeting with Max', '2024-10-22', 1),
('PHONE_CALL', 'Call with Erika', '2024-10-22', 2),
('EMAIL', 'Email to Hans', '2024-10-22', 3),
('MEETING', 'Meeting with Petra', '2024-10-22', 4),
('PHONE_CALL', 'Call with Klaus', '2024-10-22', 5),
('EMAIL', 'Email to Lisa', '2024-10-22', 6),
('MEETING', 'Meeting with Frank', '2024-10-22', 7),
('PHONE_CALL', 'Call with Sandra', '2024-10-22', 8),
('EMAIL', 'Email to Gerd', '2024-10-22', 9),
('MEETING', 'Meeting with Ute', '2024-10-22', 10);
