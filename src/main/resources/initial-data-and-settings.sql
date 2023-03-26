CREATE INDEX category_parent_category_id ON category USING btree (parent_category_id);
CREATE INDEX access_level_account_id ON access_level USING btree (account_id);
CREATE INDEX person_address_id ON person USING btree (adress_id);
CREATE INDEX person_company_id ON person USING btree (company_id);
CREATE INDEX person_account_id ON person USING btree (account_id);
CREATE INDEX product_product_group_id ON product USING btree (product_group_id);
CREATE INDEX product_group_category_id ON product_group USING btree (category_id);
CREATE INDEX rate_person_id ON rate USING btree (person_id);
CREATE INDEX rate_product_group_id ON rate USING btree (product_group_id);
CREATE INDEX sales_order_account_id ON sales_order USING btree (account_id);
CREATE INDEX sales_order_delivery_address_id ON sales_order USING btree (delivery_address_id);
CREATE INDEX sales_order_delivery_person_id ON sales_order USING btree (delivery_person_id);
CREATE INDEX sales_order_product_order_id ON sales_order_product USING btree (order_id);
CREATE INDEX sales_order_product_product_id ON sales_order_product USING btree (product_id);

GRANT SELECT,INSERT,UPDATE ON TABLE account TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE ON TABLE address TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE ON TABLE company TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE ON TABLE person TO ssbd02mok;
GRANT SELECT,INSERT,UPDATE ON TABLE access_level TO ssbd02mok;

GRANT SELECT,INSERT,UPDATE ON TABLE category TO ssbd02moz;
GRANT SELECT,INSERT,UPDATE ON TABLE product TO ssbd02moz;
GRANT SELECT,INSERT,UPDATE ON TABLE product_group TO ssbd02moz;
GRANT SELECT,INSERT,UPDATE ON TABLE sales_order TO ssbd02moz;
GRANT SELECT,INSERT,UPDATE ON TABLE sales_order_product TO ssbd02moz;