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
GRANT SELECT,INSERT,UPDATE ON TABLE image TO ssbd02moz;
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

INSERT INTO image (id, version, url, archive, created_at) VALUES (1, 1, 'https://storage.googleapis.com/furniture-store-images/ee85e2fb-54b5-4616-8fd6-82988ba1b366', false, now()),(2, 1, 'https://storage.googleapis.com/furniture-store-images/a48f1cb0-21f7-4a92-8509-c7ebdec82d37', false, now()),(3, 1, 'https://storage.googleapis.com/furniture-store-images/a98b5675-2dc6-41d3-80f4-3e0d74180cc9', false, now()),(4, 1, 'https://storage.googleapis.com/furniture-store-images/22c6665f-78c1-4f1c-94d9-41a9f843f9c8', false, now()),(5, 1, 'https://storage.googleapis.com/furniture-store-images/ee85e2fb-54b5-4616-8fd6-82988ba1b366', false, now()),(6, 1, 'https://storage.googleapis.com/furniture-store-images/4e114977-f553-4ebe-aca9-9aee1a8eea04', false, now()),(7, 1, 'https://storage.googleapis.com/furniture-store-images/4ab36b9a-cd75-47d6-ba95-3b982bf33f12', false, now()),(8, 1, 'https://storage.googleapis.com/furniture-store-images/b6d11ea1-2121-4029-a272-517901ed24e4', false, now()),(9, 1, 'https://storage.googleapis.com/furniture-store-images/fd685006-3d7f-45a6-af6c-acc6363ad7cc', false, now()),(10, 1, 'https://storage.googleapis.com/furniture-store-images/5ae7b72c-10b6-45aa-ba0f-daef25a02828', false, now()),(11, 1, 'https://storage.googleapis.com/furniture-store-images/71ef816a-9ff6-4eb5-b7e9-b38bdf98dd87', false, now()),(12, 1, 'https://storage.googleapis.com/furniture-store-images/9f3154ba-f18e-451e-a42b-3db47ba7eb2f', false, now()),(13, 1, 'https://storage.googleapis.com/furniture-store-images/3ec0bfc3-1eb9-440e-804c-502a8de75687', false, now()),(14, 1, 'https://storage.googleapis.com/furniture-store-images/b198ac5e-fc0c-4388-95d0-bf40417f43cd', false, now()),(15, 1, 'https://storage.googleapis.com/furniture-store-images/b8d1e057-4ca2-4ebe-bbb9-8cfe9a083c31', false, now()),(16, 1, 'https://storage.googleapis.com/furniture-store-images/d527ac89-dcb3-48d4-ad14-44d2959f1c3c', false, now());

INSERT INTO category (id, version, archive, category_name, parent_category_id, created_at, image_id) VALUES (1, 1, false, 'BED', null, now(), 1),(2, 1, false, 'CASE_FURNITURE', null, now(), 2),(3, 1, false, 'SEAT', null, now(), 3),(4, 1, false, 'TABLE', null, now(), 4),(5, 1, false, 'SINGLE_BED', 1, now(), 5),(6, 1, false, 'DOUBLE_BED', 1, now(), 6),(7, 1, false, 'KIDS', 1, now(), 7),(8, 1, false, 'WARDROBE', 2, now(), 8),(9, 1, false, 'DRESSER', 2, now(), 9),(10, 1, false, 'LOCKER', 2, now(), 10),(11, 1, false, 'DESK', 2, now(), 11),(12, 1, false, 'CHAIR', 3, now(), 12),(13, 1, false, 'STOOL', 3, now(), 13),(14, 1, false, 'ARMCHAIR', 3, now(), 14),(15, 1, false, 'ROUND_TABLE', 4, now(), 15),(16, 1, false, 'RECTANGULAR_TABLE', 4, now(), 16);
INSERT INTO product_group (id, version, name, archive, average_rating, created_at, category_id) VALUES (1, 1, 'Single SLIPWEL 1',  false, 3.0, now(), (SELECT id FROM category WHERE category_name = 'SINGLE_BED'));
INSERT INTO product_group (id, version, name, archive, average_rating, created_at, category_id) VALUES (2, 1, 'Double SLIPWEL 1',  false, 4.9, now(), (SELECT id FROM category WHERE category_name = 'DOUBLE_BED'));
INSERT INTO product (id, version, amount, archive, available, color, created_at, image_id, price, weight, weight_in_package, wood_type, furniture_depth, furniture_height, furniture_width, package_depth, package_height, package_width, product_group_id) VALUES (1, 1, 10, false, true, 'RED', now(), 1, 99.99, 2.5, 3.0, 'OAK', 2.2, 1.8, 1.5, 2.5, 1.9, 1.6, (SELECT id from product_group WHERE name = 'Single SLIPWEL 1'));
INSERT INTO product (id, version, amount, archive, available, color, created_at, image_id, price, weight, weight_in_package, wood_type, furniture_depth, furniture_height, furniture_width, package_depth, package_height, package_width, product_group_id) VALUES (2, 1, 15, false, true, 'BLACK', now(), 1, 99.99, 2.5, 3.0, 'OAK', 2.2, 1.8, 1.5, 2.5, 1.9, 1.6, (SELECT id from product_group WHERE name = 'Single SLIPWEL 1'));
INSERT INTO product (id, version, amount, archive, available, color, created_at, image_id, price, weight, weight_in_package, wood_type, furniture_depth, furniture_height, furniture_width, package_depth, package_height, package_width, product_group_id) VALUES (3, 1, 259, false, true, 'BLACK', now(), 1, 199.99, 5.0, 6.0, 'OAK', 2.2, 1.8, 3.0, 2.5, 1.9, 3.1, (SELECT id from product_group WHERE name = 'Double SLIPWEL 1'));
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
