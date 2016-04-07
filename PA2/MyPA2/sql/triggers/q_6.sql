--Yiran Zheng
--zhengyr@brandeis.edu
--Problem 6
--drop the original constraint
ALTER TABLE nation DROP CONSTRAINT fk_region;
ALTER TABLE supplier DROP CONSTRAINT fk_nation;
ALTER TABLE customer DROP CONSTRAINT fk_nation;
ALTER TABLE partsupp DROP CONSTRAINT fk_supplier;
ALTER TABLE lineitem DROP CONSTRAINT fk_partsupp;

--declear new constraint with predicate
ALTER TABLE nation 
ADD CONSTRAINT fk_region FOREIGN KEY (n_regionkey) REFERENCES region ON DELETE CASCADE;
ALTER TABLE supplier
ADD CONSTRAINT fk_nation FOREIGN KEY (s_nationkey) REFERENCES nation ON DELETE CASCADE;
ALTER TABLE customer 
ADD CONSTRAINT fk_nation FOREIGN KEY (c_nationkey) REFERENCES nation ON DELETE SET NULL;
ALTER TABLE partsupp
ADD CONSTRAINT fk_supplier FOREIGN KEY (ps_suppkey) REFERENCES supplier ON DELETE CASCADE;
ALTER TABLE lineitem
ADD CONSTRAINT fk_partsupp FOREIGN KEY (l_partkey, l_suppkey) REFERENCES partsupp ON DELETE SET NULL;