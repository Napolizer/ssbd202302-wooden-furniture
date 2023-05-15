GRANT SELECT,INSERT,DELETE ON TABLE access_level TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE account TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE ON TABLE address TO ssbd02mok;
GRANT SELECT,INSERT,DELETE ON TABLE administrator TO ssbd02mok;
GRANT SELECT,INSERT,DELETE ON TABLE client TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE company TO ssbd02mok;
GRANT SELECT,INSERT,DELETE ON TABLE employee TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE person TO ssbd02mok;
GRANT SELECT,INSERT,DELETE ON TABLE sales_rep TO ssbd02mok;

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

INSERT INTO address (id, version, archive, city, country, postal_code, street, street_number) VALUES (nextval('seq_gen_sequence'), 1, false, 'Lodz', 'Poland', '93-590', 'Aleje Testowe', '55');
INSERT INTO person (id, version, archive, first_name, last_name, address_id) VALUES (nextval('seq_gen_sequence'), 1, false, 'Admin', 'Root', (SELECT id FROM address WHERE street = 'Aleje Testowe'));
INSERT INTO category (id, version, archive, category_name, parent_category_id) VALUES (1, 1, false, 'Bed', null),(2, 1, false, 'Case Furniture', null),(3, 1, false, 'Seat', null),(4, 1, false, 'Table', null),(5, 1, false, 'Single Bed', 1),(6, 1, false, 'Double Bed', 1),(7, 1, false, 'Kids', 1),(8, 1, false, 'Wardrobe', 2),(9, 1, false, 'Dresser', 2),(10, 1, false, 'Locker', 2),(11, 1, false, 'Desk', 2),(12, 1, false, 'Chair', 3),(13, 1, false, 'Stool', 3),(14, 1, false, 'Armchair', 3),(15, 1, false, 'Round Table', 4),(16, 1, false, 'Rectangular Table', 4);
INSERT INTO account (id, version, archive, account_state, email, failed_login_counter, locale, login, password, person_id) VALUES (nextval('seq_gen_sequence'), 1, false, 'ACTIVE', 'admin@gmail.com', 0, 'pl', 'admin', '$2a$12$xnPLkhbqOn/obTmNJ1xL/u5bWs5huYfkyoSKeLJpK/jjZL0H031RW', (SELECT id FROM person WHERE first_name = 'Admin'));
INSERT INTO access_level (id, version, archive, dtype, account_id) VALUES (nextval('seq_gen_sequence'), 1, false, 'administrator', (SELECT id FROM account WHERE login = 'admin'));
INSERT INTO administrator (id) VALUES ((SELECT id FROM access_level WHERE dtype = 'administrator'));

INSERT INTO address (id, version, archive, city, country, postal_code, street, street_number) VALUES (nextval('seq_gen_sequence'), 1, false, 'Lodz', 'Poland', '93-590', 'Politechniki', '50');
INSERT INTO person (id, version, archive, first_name, last_name, address_id) VALUES (nextval('seq_gen_sequence'), 1, false, 'User1', 'User123', (SELECT id FROM address WHERE street = 'Politechniki'));
INSERT INTO account (id, version, archive, account_state, email, failed_login_counter, locale, login, password, person_id) VALUES (nextval('seq_gen_sequence'), 1, false, 'ACTIVE', 'user1@gmail.com', 0, 'pl', 'user1', '$2a$12$dandYnHSVGt/REAEm5Io3Ohrmhx6KLWiIiHGQ1ZPzAMB.fRyy69hK', (SELECT id FROM person WHERE first_name = 'User1'));
INSERT INTO access_level (id, version, archive, dtype, account_id) VALUES (nextval('seq_gen_sequence'), 1, false, 'employee', (SELECT id FROM account WHERE login = 'user1'));
INSERT INTO access_level (id, version, archive, dtype, account_id) VALUES (nextval('seq_gen_sequence'), 1, false, 'client', (SELECT id FROM account WHERE login = 'user1'));
INSERT INTO employee (id) VALUES ((SELECT id FROM access_level WHERE dtype = 'employee'));
INSERT INTO client (id, company_id) VALUES ((SELECT id FROM access_level WHERE dtype = 'client'), NULL);