--Yiran Zheng
--zhengyr@brandeis.edu
--Problem 7
--each if statement checks for one condition, could have nested if statement to check the conditions
--but this looks more clear
CREATE OR REPLACE FUNCTION supplier_changes()
RETURNS TRIGGER AS $$
BEGIN
IF (NEW.s_nationkey != OLD.s_nationkey) THEN
	IF((SELECT n_regionkey FROM nation WHERE n_nationkey = OLD.s_nationkey) = 1 AND 
		(SELECT n_regionkey FROM nation WHERE n_nationkey = NEW.s_nationkey) = 2) THEN
		UPDATE partsupp
		SET ps_supplycost = ps_supplycost * 0.8
		WHERE ps_suppkey = NEW.s_suppkey;
	ELSEIF((SELECT n_regionkey FROM nation WHERE n_nationkey = OLD.s_nationkey) = 1 AND 
		(SELECT n_regionkey FROM nation WHERE n_nationkey = NEW.s_nationkey) = 3) THEN
		UPDATE partsupp
		SET ps_supplycost = ps_supplycost * 1.05
		WHERE ps_suppkey = NEW.s_suppkey;
	ELSEIF((SELECT n_regionkey FROM nation WHERE n_nationkey = OLD.s_nationkey) = 2 AND 
		(SELECT n_regionkey FROM nation WHERE n_nationkey = NEW.s_nationkey) = 1) THEN
		UPDATE partsupp
		SET ps_supplycost = ps_supplycost * 1.2
		WHERE ps_suppkey = NEW.s_suppkey;
	ELSEIF((SELECT n_regionkey FROM nation WHERE n_nationkey = OLD.s_nationkey) = 2 AND 
		(SELECT n_regionkey FROM nation WHERE n_nationkey = NEW.s_nationkey) = 3) THEN
		UPDATE partsupp
		SET ps_supplycost = ps_supplycost * 1.1
		WHERE ps_suppkey = NEW.s_suppkey;
	ELSEIF((SELECT n_regionkey FROM nation WHERE n_nationkey = OLD.s_nationkey) = 3 AND 
		(SELECT n_regionkey FROM nation WHERE n_nationkey = NEW.s_nationkey) = 1) THEN
		UPDATE partsupp
		SET ps_supplycost = ps_supplycost * 0.95
		WHERE ps_suppkey = NEW.s_suppkey;
	ELSEIF((SELECT n_regionkey FROM nation WHERE n_nationkey = OLD.s_nationkey) = 3 AND 
		(SELECT n_regionkey FROM nation WHERE n_nationkey = NEW.s_nationkey) = 2) THEN
		UPDATE partsupp
		SET ps_supplycost = ps_supplycost * 0.9
		WHERE ps_suppkey = NEW.s_suppkey;
	END IF;
END IF;
RETURN NULL;
END
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER supplier_changes_trigger
AFTER UPDATE ON supplier
FOR EACH ROW EXECUTE PROCEDURE supplier_changes();