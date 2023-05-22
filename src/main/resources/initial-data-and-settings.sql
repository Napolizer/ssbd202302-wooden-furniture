GRANT SELECT,INSERT,DELETE ON TABLE access_level TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE ON TABLE password_history TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE account TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE ON TABLE address TO ssbd02mok;
GRANT SELECT,INSERT,DELETE ON TABLE administrator TO ssbd02mok;
GRANT SELECT,INSERT,DELETE ON TABLE client TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE company TO ssbd02mok;
GRANT SELECT,INSERT,DELETE ON TABLE employee TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE person TO ssbd02mok;
GRANT SELECT,INSERT,DELETE ON TABLE sales_rep TO ssbd02mok;
GRANT SELECT ON TABLE sales_order TO ssbd02mok;

GRANT SELECT ON TABLE category TO ssbd02moz;
GRANT SELECT,INSERT,UPDATE ON TABLE product TO ssbd02moz;
GRANT SELECT,INSERT,UPDATE ON TABLE product_group TO ssbd02moz;
GRANT SELECT,INSERT,UPDATE ON TABLE rate TO ssbd02moz;
GRANT SELECT,INSERT,UPDATE ON TABLE sales_order TO ssbd02moz;
GRANT SELECT,INSERT ON TABLE sales_order_product TO ssbd02moz;

GRANT SELECT ON account TO ssbd02auth;
GRANT SELECT ON access_level TO ssbd02auth;
GRANT SELECT ON client  TO ssbd02auth;
GRANT SELECT ON administrator TO ssbd02auth;
GRANT SELECT ON employee TO ssbd02auth;
GRANT SELECT ON sales_rep TO ssbd02auth;

GRANT USAGE, SELECT,UPDATE ON SEQUENCE seq_gen_sequence TO ssbd02mok;
GRANT USAGE, SELECT,UPDATE ON SEQUENCE seq_gen_sequence TO ssbd02moz;

INSERT INTO address (id, version, archive, city, country, postal_code, street, street_number, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Lodz', 'Poland', '93-590', 'Aleje Testowe', '55', now());
INSERT INTO person (id, version, archive, first_name, last_name, address_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Jan', 'Kowalski', (SELECT id FROM address WHERE street = 'Aleje Testowe'), now());
INSERT INTO category (id, version, archive, category_name, parent_category_id, created_at) VALUES (1, 1, false, 'Bed', null, now()),(2, 1, false, 'Case Furniture', null, now()),(3, 1, false, 'Seat', null, now()),(4, 1, false, 'Table', null, now()),(5, 1, false, 'Single Bed', 1, now()),(6, 1, false, 'Double Bed', 1, now()),(7, 1, false, 'Kids', 1, now()),(8, 1, false, 'Wardrobe', 2, now()),(9, 1, false, 'Dresser', 2, now()),(10, 1, false, 'Locker', 2, now()),(11, 1, false, 'Desk', 2, now()),(12, 1, false, 'Chair', 3, now()),(13, 1, false, 'Stool', 3, now()),(14, 1, false, 'Armchair', 3, now()),(15, 1, false, 'Round Table', 4, now()),(16, 1, false, 'Rectangular Table', 4, now());
INSERT INTO account (id, version, time_zone, type, archive, account_state, email, failed_login_counter, locale, login, password, person_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, 'EUROPE_WARSAW', 'NORMAL', false, 'ACTIVE', 'admin@gmail.com', 0, 'pl', 'administrator', '$2a$12$X7QVm.XkCx3l97z0/LbxzewopH6ift/IU9kDPfq834MYPfV7w27pe', (SELECT id FROM person WHERE first_name = 'Jan'), now());
INSERT INTO access_level (id, version, archive, dtype, account_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'administrator', (SELECT id FROM account WHERE login = 'administrator'), now());
INSERT INTO administrator (id) VALUES ((SELECT id FROM access_level WHERE dtype = 'administrator'));

INSERT INTO address (id, version, archive, city, country, postal_code, street, street_number, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Lodz', 'Poland', '93-590', 'Politechniki', '50', now());
INSERT INTO person (id, version, archive, first_name, last_name, address_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Steve', 'Jobs', (SELECT id FROM address WHERE street = 'Politechniki'), now());
INSERT INTO account (id, version, time_zone,type, archive, account_state, email, failed_login_counter, locale, login, password, person_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, 'EUROPE_WARSAW', 'NORMAL', false, 'ACTIVE', 'steve.jobs@gmail.com', 0, 'pl', 'clientemployee', '$2a$12$X7QVm.XkCx3l97z0/LbxzewopH6ift/IU9kDPfq834MYPfV7w27pe', (SELECT id FROM person WHERE first_name = 'Steve'), now());
INSERT INTO access_level (id, version, archive, dtype, account_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'employee', (SELECT id FROM account WHERE login = 'clientemployee'), now());
INSERT INTO access_level (id, version, archive, dtype, account_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'client', (SELECT id FROM account WHERE login = 'clientemployee'), now());
INSERT INTO employee (id) VALUES ((SELECT id FROM access_level WHERE dtype = 'employee'));
INSERT INTO client (id, company_id) VALUES ((SELECT id FROM access_level WHERE dtype = 'client'), NULL);

INSERT INTO address (id, version, archive, city, country, postal_code, street, street_number, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Lodz', 'Poland', '93-116', 'Przybyszewskiego', '13', now());
INSERT INTO person (id, version, archive, first_name, last_name, address_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Adam', 'Mickiewicz', (SELECT id FROM address WHERE street = 'Przybyszewskiego'), now());
INSERT INTO account (id, version, time_zone, type, archive, account_state, email, failed_login_counter, locale, login, password, person_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, 'EUROPE_WARSAW', 'NORMAL', false, 'ACTIVE', 'adam.mickiewicz@gmail.com', 0, 'pl', 'client', '$2a$12$X7QVm.XkCx3l97z0/LbxzewopH6ift/IU9kDPfq834MYPfV7w27pe', (SELECT id FROM person WHERE first_name = 'Adam'), now());
INSERT INTO access_level (id, version, archive, dtype, account_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'client', (SELECT id FROM account WHERE login = 'client'), now());
INSERT INTO client (id, company_id) VALUES ((SELECT id FROM access_level WHERE account_id = (SELECT id FROM account WHERE login = 'client')), NULL);

INSERT INTO address (id, version, archive, city, country, postal_code, street, street_number, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Warszawa', 'Poland', '22-192', 'Piłsudskiego', '21', now());
INSERT INTO person (id, version, archive, first_name, last_name, address_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Juliusz', 'Słowacki', (SELECT id FROM address WHERE street = 'Piłsudskiego'), now());
INSERT INTO account (id, version, time_zone, type, archive, account_state, email, failed_login_counter, locale, login, password, person_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, 'EUROPE_WARSAW', 'NORMAL', false, 'ACTIVE', 'juliusz.slowacki@gmail.com', 0, 'pl', 'employee', '$2a$12$X7QVm.XkCx3l97z0/LbxzewopH6ift/IU9kDPfq834MYPfV7w27pe', (SELECT id FROM person WHERE first_name = 'Juliusz'), now());
INSERT INTO access_level (id, version, archive, dtype, account_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'employee', (SELECT id FROM account WHERE login = 'employee'), now());
INSERT INTO employee (id) VALUES ((SELECT id FROM access_level WHERE account_id = (SELECT id FROM account WHERE login = 'employee')));

INSERT INTO address (id, version, archive, city, country, postal_code, street, street_number, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Szczecin', 'Poland', '12-121', 'Paprykarza', '12', now());
INSERT INTO person (id, version, archive, first_name, last_name, address_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Cyprian', 'Norwid', (SELECT id FROM address WHERE street = 'Paprykarza'), now());
INSERT INTO account (id, version, time_zone, type, archive, account_state, email, failed_login_counter, locale, login, password, person_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, 'EUROPE_WARSAW', 'NORMAL', false, 'ACTIVE', 'cyprian.norwid@gmail.com', 0, 'pl', 'salesrep', '$2a$12$X7QVm.XkCx3l97z0/LbxzewopH6ift/IU9kDPfq834MYPfV7w27pe', (SELECT id FROM person WHERE first_name = 'Cyprian'), now());
INSERT INTO access_level (id, version, archive, dtype, account_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'sales_rep', (SELECT id FROM account WHERE login = 'salesrep'), now());
INSERT INTO sales_rep (id) VALUES ((SELECT id FROM access_level WHERE account_id = (SELECT id FROM account WHERE login = 'salesrep')));
