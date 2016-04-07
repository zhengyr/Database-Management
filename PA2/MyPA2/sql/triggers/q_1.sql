--Yiran Zheng
--zhengyr@brandeis.edu
--question 1
--nation key update

--need to drop the old constraint and create new constraint
--notice that only customer and supllier have nationkey as foreign constraint
--these are the only two table we need to change
ALTER TABLE customer DROP CONSTRAINT fk_nation;
ALTER TABLE supplier DROP CONSTRAINT fk_nation;

--create new constraint here with predicate on update
ALTER TABLE customer 
ADD CONSTRAINT fk_nation FOREIGN KEY (c_nationkey) REFERENCES nation ON UPDATE CASCADE;
ALTER TABLE supplier 
ADD CONSTRAINT fk_nation FOREIGN KEY (s_nationkey) REFERENCES nation ON UPDATE CASCADE; 