--YIRAN ZHENG
--zhengyr@brandeis.edu
--primary keys

--table customer
ALTER TABLE customer ADD PRIMARY KEY (c_custkey);

--table lineitem
ALTER TABLE lineitem ADD PRIMARY KEY (l_orderkey, l_linenumber);

--table nation
ALTER TABLE nation ADD PRIMARY KEY (n_nationkey);

--table orders
ALTER TABLE orders ADD PRIMARY KEY (o_orderkey);

--table part
ALTER TABLE part ADD PRIMARY KEY (p_partkey);

--table partsupp
ALTER TABLE partsupp ADD PRIMARY KEY (ps_partkey, ps_suppkey);

--table region
ALTER TABLE region ADD PRIMARY KEY (r_regionkey);

--table supplier
ALTER TABLE supplier ADD PRIMARY KEY (s_suppkey);

