--Yiran Zheng
--zhengyr@brandeis.edu
--Problem 5
--for insert, we could only change a state from F or O to P
CREATE FUNCTION order_status()
RETURNS TRIGGER AS $$
BEGIN
IF (EXISTS (SELECT A.l_linestatus 
	FROM lineitem AS A 
	WHERE A.l_linestatus != NEW.l_linestatus 
	AND A.l_orderkey = NEW.l_orderkey)) THEN
	UPDATE orders SET O_orderstatus = 'P' WHERE O_orderkey = NEW.l_orderkey;
	END IF;
RETURN NULL;
END
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER order_status_trigger
AFTER INSERT ON lineitem
FOR EACH ROW EXECUTE PROCEDURE order_status();