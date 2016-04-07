--Yiran Zheng
--zhengyr@brandeis.edu
--Problem 4
--drop original table first and then make changes
ALTER TABLE orders DROP CONSTRAINT fk_customer;
ALTER TABLE lineitem DROP CONSTRAINT fk_orders;
--delete cascade
ALTER TABLE orders
ADD CONSTRAINT fk_customer FOREIGN KEY (o_custkey) REFERENCES customer ON DELETE CASCADE;
ALTER TABLE lineitem
ADD CONSTRAINT fk_orders FOREIGN KEY (l_orderkey) REFERENCES orders ON DELETE CASCADE;