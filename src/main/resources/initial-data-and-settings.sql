CREATE INDEX category_parent_category_id ON category USING btree (parent_category_id);
CREATE INDEX access_level_account_id ON access_level USING btree (account_id);
CREATE INDEX person_address_id ON person USING btree (address_id);
CREATE INDEX person_account_id ON person USING btree (account_id);
CREATE INDEX client_company_id ON client USING btree (company_id);
CREATE INDEX product_product_group_id ON product USING btree (product_group_id);
CREATE INDEX product_group_category_id ON product_group USING btree (category_id);
CREATE INDEX rate_person_id ON rate USING btree (person_id);
CREATE INDEX rate_product_group_id ON rate USING btree (product_group_id);
CREATE INDEX sales_order_account_id ON sales_order USING btree (account_id);
CREATE INDEX sales_order_delivery_address_id ON sales_order USING btree (delivery_address_id);
CREATE INDEX sales_order_delivery_person_id ON sales_order USING btree (delivery_person_id);
CREATE INDEX sales_order_product_order_id ON sales_order_product USING btree (order_id);
CREATE INDEX sales_order_product_product_id ON sales_order_product USING btree (product_id);

GRANT SELECT,INSERT,UPDATE,DELETE ON TABLE access_level TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE ON TABLE account TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE ON TABLE address TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE ON TABLE administrator TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE ON TABLE client TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE ON TABLE company TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE ON TABLE employee TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE ON TABLE person TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE ON TABLE sales_rep TO ssbd02mok;

GRANT SELECT,INSERT,UPDATE ON TABLE category TO ssbd02moz;
GRANT SELECT,INSERT,UPDATE ON TABLE product TO ssbd02moz;
GRANT SELECT,INSERT,UPDATE ON TABLE product_group TO ssbd02moz;
GRANT SELECT,INSERT,UPDATE ON TABLE rate TO ssbd02moz;
GRANT SELECT,INSERT,UPDATE ON TABLE sales_order TO ssbd02moz;
GRANT SELECT,INSERT,UPDATE ON TABLE sales_order_product TO ssbd02moz;

GRANT USAGE, SELECT ON SEQUENCE seq_gen_sequence TO ssbd02mok;
GRANT USAGE, SELECT ON SEQUENCE seq_gen_sequence TO ssbd02moz;

INSERT INTO category (id, version, category_name, parent_category_id) VALUES (1, 1, 'Bed', null),(2, 1, 'Case Furniture', null),(3, 1, 'Seat', null),(4, 1, 'Table', null),(5, 1, 'Single Bed', 1),(6, 1, 'Double Bed', 1),(7, 1, 'Kids', 1),(8, 1, 'Wardrobe', 2),(9, 1, 'Dresser', 2),(10, 1, 'Locker', 2),(11, 1, 'Desk', 2),(12, 1, 'Chair', 3),(13, 1, 'Stool', 3),(14, 1, 'Armchair', 3),(15, 1, 'Round Table', 4),(16, 1, 'Rectangular Table', 4);
INSERT INTO account (id, version, account_state, email, failed_login_counter, locale, login, password) VALUES (nextval('seq_gen_sequence'), 1, 'ACTIVE', 'admin@gmail.com', 0, 'pl', 'admin', 'kochamssbd');
INSERT INTO address (id, version, city, country, postal_code, street, street_number) VALUES (nextval('seq_gen_sequence'), 1, 'Lodz', 'Poland', '93-590', 'Aleje Testowe', '55');
INSERT INTO person (id, version, first_name, last_name, account_id, address_id) VALUES (nextval('seq_gen_sequence'), 1, 'Admin', 'Root', (SELECT id FROM account WHERE login = 'admin'), (SELECT id FROM address WHERE street = 'Aleje Testowe'));
INSERT INTO access_level (id, version, dtype, account_id) VALUES (nextval('seq_gen_sequence'), 1, 'administrator', (SELECT id FROM account WHERE login = 'admin'));
INSERT INTO administrator (id) VALUES ((SELECT id FROM access_level WHERE dtype = 'administrator'));
