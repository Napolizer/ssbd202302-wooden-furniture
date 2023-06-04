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
GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE account_search_settings TO ssbd02mok;

GRANT SELECT ON TABLE account TO ssbd02moz;
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

INSERT INTO category (id, version, archive, category_name, parent_category_id, created_at) VALUES (1, 1, false, 'BED', null, now()),(2, 1, false, 'CASE_FURNITURE', null, now()),(3, 1, false, 'SEAT', null, now()),(4, 1, false, 'TABLE', null, now()),(5, 1, false, 'SINGLE_BED', 1, now()),(6, 1, false, 'DOUBLE_BED', 1, now()),(7, 1, false, 'KIDS', 1, now()),(8, 1, false, 'WARDROBE', 2, now()),(9, 1, false, 'DRESSER', 2, now()),(10, 1, false, 'LOCKER', 2, now()),(11, 1, false, 'DESK', 2, now()),(12, 1, false, 'CHAIR', 3, now()),(13, 1, false, 'STOOL', 3, now()),(14, 1, false, 'ARMCHAIR', 3, now()),(15, 1, false, 'ROUND_TABLE', 4, now()),(16, 1, false, 'RECTANGULAR_TABLE', 4, now());
INSERT INTO product_group (id, version, name, archive, average_rating, created_at, category_id) VALUES (1, 1, 'SLIPWEL 1',  false, 3.0, now(), (SELECT id FROM category WHERE category_name = 'SINGLE_BED'));
INSERT INTO product_group (id, version, name, archive, average_rating, created_at, category_id) VALUES (2, 1, 'SLIPWEL 2',  false, 4.9, now(), (SELECT id FROM category WHERE category_name = 'DOUBLE_BED'));
INSERT INTO product_group (id, version, name, archive, average_rating, created_at, category_id) VALUES (3, 1, 'CIRKEL',  false, 4.6, now(), (SELECT id FROM category WHERE category_name = 'ROUND_TABLE'));
INSERT INTO product_group (id, version, name, archive, average_rating, created_at, category_id) VALUES (4, 1, 'SITTA',  false, 1.2, now(), (SELECT id FROM category WHERE category_name = 'CHAIR'));
INSERT INTO product (id, version, amount, archive, available, color, created_at, image, price, weight, weight_in_package, wood_type, furniture_depth, furniture_height, furniture_width, package_depth, package_height, package_width, product_group_id) VALUES (6, 1, 259, false, true, 'BLACK', now(), decode('MTIzAAE=', 'base64'), 789.01, 5.0, 6.0, 'OAK', 6.1, 3.7, 4.4, 2.8, 8.5, 5.2, (SELECT id from product_group WHERE name = 'SLIPWEL 1'));
INSERT INTO product (id, version, amount, archive, available, color, created_at, image, price, weight, weight_in_package, wood_type, furniture_depth, furniture_height, furniture_width, package_depth, package_height, package_width, product_group_id) VALUES (7, 1, 259, false, true, 'BLACK', now(), decode('MTIzAAE=', 'base64'), 789.01, 5.0, 6.0, 'OAK', 6.1, 3.7, 4.4, 2.8, 8.5, 5.2, (SELECT id from product_group WHERE name = 'SLIPWEL 1'));
INSERT INTO product (id, version, amount, archive, available, color, created_at, image, price, weight, weight_in_package, wood_type, furniture_depth, furniture_height, furniture_width, package_depth, package_height, package_width, product_group_id) VALUES (8, 1, 259, false, true, 'BLACK', now(),decode('MTIzAAE=', 'base64'), 234.56, 5.0, 6.0, 'OAK', 5.9, 4.5, 9.2, 3.4, 1.8, 7.3, (SELECT id from product_group WHERE name = 'SLIPWEL 1'));
INSERT INTO product (id, version, amount, archive, available, color, created_at, image, price, weight, weight_in_package, wood_type, furniture_depth, furniture_height, furniture_width, package_depth, package_height, package_width, product_group_id) VALUES (9, 1, 259, false, true, 'BLACK', now(), null, 345.67, 5.0, 6.0, 'OAK', 8.3, 2.2, 7.1, 9.0, 2.7, 4.1, (SELECT id from product_group WHERE name = 'SLIPWEL 1'));
INSERT INTO product (id, version, amount, archive, available, color, created_at, image, price, weight, weight_in_package, wood_type, furniture_depth, furniture_height, furniture_width, package_depth, package_height, package_width, product_group_id) VALUES (10, 1, 259, false, true, 'BLACK', now(), decode('MTIzAAE=', 'base64'), 567.89, 5.0, 6.0, 'OAK', 2.6, 6.8, 8.7, 7.6, 5.3, 9.8, (SELECT id from product_group WHERE name = 'SLIPWEL 2'));
INSERT INTO product (id, version, amount, archive, available, color, created_at, image, price, weight, weight_in_package, wood_type, furniture_depth, furniture_height, furniture_width, package_depth, package_height, package_width, product_group_id) VALUES (11, 1, 259, false, true, 'BLACK', now(), null, 789.12, 5.0, 6.0, 'OAK', 1.7, 4.3, 2.8, 5.9, 6.7, 3.5, (SELECT id from product_group WHERE name = 'SLIPWEL 1'));
INSERT INTO product (id, version, amount, archive, available, color, created_at, image, price, weight, weight_in_package, wood_type, furniture_depth, furniture_height, furniture_width, package_depth, package_height, package_width, product_group_id) VALUES (12, 1, 259, false, true, 'BLACK', now(), decode('MTIzAAE=', 'base64'), 345.67, 5.0, 6.0, 'OAK', 6.5, 9.4, 1.5, 8.7, 3.2, 6.9, (SELECT id from product_group WHERE name = 'SLIPWEL 1'));
INSERT INTO product (id, version, amount, archive, available, color, created_at, image, price, weight, weight_in_package, wood_type, furniture_depth, furniture_height, furniture_width, package_depth, package_height, package_width, product_group_id) VALUES (13, 1, 259, false, true, 'BLACK', now(), null, 234.56, 5.0, 6.0, 'OAK', 3.9, 2.1, 7.6, 1.4, 5.6, 8.1, (SELECT id from product_group WHERE name = 'SLIPWEL 1'));
INSERT INTO product (id, version, amount, archive, available, color, created_at, image, price, weight, weight_in_package, wood_type, furniture_depth, furniture_height, furniture_width, package_depth, package_height, package_width, product_group_id) VALUES (14, 1, 259, false, true, 'BLACK', now(), null, 456.78, 5.0, 6.0, 'OAK', 2.3, 8.5, 4.6, 2.9, 7.4, 3.6, (SELECT id from product_group WHERE name = 'SLIPWEL 1'));
INSERT INTO product (id, version, amount, archive, available, color, created_at, image, price, weight, weight_in_package, wood_type, furniture_depth, furniture_height, furniture_width, package_depth, package_height, package_width, product_group_id) VALUES (15, 1, 259, false, true, 'BLACK', now(), decode('MTIzAAE=', 'base64'), 789.90, 5.0, 6.0, 'OAK', 4.2, 3.8, 6.3, 5.7, 3.9, 2.4, (SELECT id from product_group WHERE name = 'SLIPWEL 1'));
INSERT INTO product (id, version, amount, archive, available, color, created_at, image, price, weight, weight_in_package, wood_type, furniture_depth, furniture_height, furniture_width, package_depth, package_height, package_width, product_group_id) VALUES (16, 1, 2316, false, true, 'BROWN', now(), null, 2999.99, 5.0, 6.0, 'JUNGLE', 4.2, 3.8, 6.3, 5.7, 3.9, 2.4, (SELECT id from product_group WHERE name = 'SITTA'));

INSERT INTO address (id, version, archive, city, country, postal_code, street, street_number, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Lodz', 'Poland', '93-590', 'Aleje Testowe', '55', now());
INSERT INTO person (id, version, archive, first_name, last_name, address_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Jan', 'Kowalski', (SELECT id FROM address WHERE street = 'Aleje Testowe'), now());
INSERT INTO account (id, version, time_zone, type, archive, account_state, email, failed_login_counter, locale, login, password, person_id, created_at, mode) VALUES (nextval('seq_gen_sequence'), 1, 'EUROPE_WARSAW', 'NORMAL', false, 'ACTIVE', 'admin@gmail.com', 0, 'pl', 'administrator', '$2a$12$X7QVm.XkCx3l97z0/LbxzewopH6ift/IU9kDPfq834MYPfV7w27pe', (SELECT id FROM person WHERE first_name = 'Jan'), now(), 'DARK');
INSERT INTO access_level (id, version, archive, dtype, account_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'administrator', (SELECT id FROM account WHERE login = 'administrator'), now());
INSERT INTO administrator (id) VALUES ((SELECT id FROM access_level WHERE dtype = 'administrator'));

INSERT INTO address (id, version, archive, city, country, postal_code, street, street_number, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Lodz', 'Poland', '93-590', 'Politechniki', '50', now());
INSERT INTO person (id, version, archive, first_name, last_name, address_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Steve', 'Jobs', (SELECT id FROM address WHERE street = 'Politechniki'), now());
INSERT INTO account (id, version, time_zone,type, archive, account_state, email, failed_login_counter, locale, login, password, person_id, created_at, mode) VALUES (nextval('seq_gen_sequence'), 1, 'EUROPE_WARSAW', 'NORMAL', false, 'ACTIVE', 'steve.jobs@gmail.com', 0, 'pl', 'clientemployee', '$2a$12$X7QVm.XkCx3l97z0/LbxzewopH6ift/IU9kDPfq834MYPfV7w27pe', (SELECT id FROM person WHERE first_name = 'Steve'), now(), 'LIGHT');
INSERT INTO access_level (id, version, archive, dtype, account_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'employee', (SELECT id FROM account WHERE login = 'clientemployee'), now());
INSERT INTO access_level (id, version, archive, dtype, account_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'client', (SELECT id FROM account WHERE login = 'clientemployee'), now());
INSERT INTO employee (id) VALUES ((SELECT id FROM access_level WHERE dtype = 'employee'));
INSERT INTO client (id, company_id) VALUES ((SELECT id FROM access_level WHERE dtype = 'client'), NULL);

INSERT INTO address (id, version, archive, city, country, postal_code, street, street_number, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Lodz', 'Poland', '93-116', 'Przybyszewskiego', '13', now());
INSERT INTO person (id, version, archive, first_name, last_name, address_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Adam', 'Mickiewicz', (SELECT id FROM address WHERE street = 'Przybyszewskiego'), now());
INSERT INTO account (id, version, time_zone, type, archive, account_state, email, failed_login_counter, locale, login, password, person_id, created_at, mode) VALUES (nextval('seq_gen_sequence'), 1, 'EUROPE_WARSAW', 'NORMAL', false, 'ACTIVE', 'adam.mickiewicz@gmail.com', 0, 'pl', 'client', '$2a$12$X7QVm.XkCx3l97z0/LbxzewopH6ift/IU9kDPfq834MYPfV7w27pe', (SELECT id FROM person WHERE first_name = 'Adam'), now(), 'LIGHT');
INSERT INTO access_level (id, version, archive, dtype, account_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'client', (SELECT id FROM account WHERE login = 'client'), now());
INSERT INTO client (id, company_id) VALUES ((SELECT id FROM access_level WHERE account_id = (SELECT id FROM account WHERE login = 'client')), NULL);

INSERT INTO address (id, version, archive, city, country, postal_code, street, street_number, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Warszawa', 'Poland', '22-192', 'Piłsudskiego', '21', now());
INSERT INTO person (id, version, archive, first_name, last_name, address_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Juliusz', 'Słowacki', (SELECT id FROM address WHERE street = 'Piłsudskiego'), now());
INSERT INTO account (id, version, time_zone, type, archive, account_state, email, failed_login_counter, locale, login, password, person_id, created_at, mode) VALUES (nextval('seq_gen_sequence'), 1, 'EUROPE_WARSAW', 'NORMAL', false, 'ACTIVE', 'juliusz.slowacki@gmail.com', 0, 'pl', 'employee', '$2a$12$X7QVm.XkCx3l97z0/LbxzewopH6ift/IU9kDPfq834MYPfV7w27pe', (SELECT id FROM person WHERE first_name = 'Juliusz'), now(), 'LIGHT');
INSERT INTO access_level (id, version, archive, dtype, account_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'employee', (SELECT id FROM account WHERE login = 'employee'), now());
INSERT INTO employee (id) VALUES ((SELECT id FROM access_level WHERE account_id = (SELECT id FROM account WHERE login = 'employee')));

INSERT INTO address (id, version, archive, city, country, postal_code, street, street_number, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Szczecin', 'Poland', '12-121', 'Paprykarza', '12', now());
INSERT INTO person (id, version, archive, first_name, last_name, address_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'Cyprian', 'Norwid', (SELECT id FROM address WHERE street = 'Paprykarza'), now());
INSERT INTO account (id, version, time_zone, type, archive, account_state, email, failed_login_counter, locale, login, password, person_id, created_at, mode) VALUES (nextval('seq_gen_sequence'), 1, 'EUROPE_WARSAW', 'NORMAL', false, 'ACTIVE', 'cyprian.norwid@gmail.com', 0, 'pl', 'salesrep', '$2a$12$X7QVm.XkCx3l97z0/LbxzewopH6ift/IU9kDPfq834MYPfV7w27pe', (SELECT id FROM person WHERE first_name = 'Cyprian'), now(), 'LIGHT');
INSERT INTO access_level (id, version, archive, dtype, account_id, created_at) VALUES (nextval('seq_gen_sequence'), 1, false, 'sales_rep', (SELECT id FROM account WHERE login = 'salesrep'), now());
INSERT INTO sales_rep (id) VALUES ((SELECT id FROM access_level WHERE account_id = (SELECT id FROM account WHERE login = 'salesrep')));
