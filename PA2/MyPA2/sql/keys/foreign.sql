--YIRAN ZHENG
--zhengyr@brandeis.edu
--foreign keys

--table customer
ALTER TABLE customer 
ADD CONSTRAINT fk_nation FOREIGN KEY (c_nationkey) REFERENCES nation;

--table lineitem
ALTER TABLE lineitem
ADD CONSTRAINT fk_partsupp FOREIGN KEY (l_partkey, l_suppkey) REFERENCES partsupp;

ALTER TABLE lineitem
ADD CONSTRAINT fk_orders FOREIGN KEY (l_orderkey) REFERENCES orders;

--table nation
ALTER TABLE nation 
ADD CONSTRAINT fk_region FOREIGN KEY (n_regionkey) REFERENCES region;

--table orders
ALTER TABLE orders
ADD CONSTRAINT fk_customer FOREIGN KEY (o_custkey) REFERENCES customer;

--table partsupp
ALTER TABLE partsupp
ADD CONSTRAINT fk_supplier FOREIGN KEY (ps_suppkey) REFERENCES supplier;

ALTER TABLE partsupp
ADD CONSTRAINT fk_part FOREIGN KEY (ps_partkey) REFERENCES part;

--table supplier
ALTER TABLE supplier
ADD CONSTRAINT fk_nation FOREIGN KEY (s_nationkey) REFERENCES nation;

