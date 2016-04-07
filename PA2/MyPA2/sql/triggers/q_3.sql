--Yiran Zheng
--zhengyr@brandeis.edu
--Problem 3
--if we want to enter an open order, we have to check if current open order number reach 14
CREATE OR REPLACE FUNCTION order_limit() 
RETURNS TRIGGER AS $$
BEGIN
IF (SELECT COUNT(O_orderstatus)
	FROM orders
	WHERE O_orderstatus = 'O' AND O_custkey = NEW.O_custkey AND NEW.O_orderstatus = 'O') 
	= 14 THEN
	RAISE EXCEPTION 'Customers cannot have more than 14 open orders.'; 
	RETURN NULL;
	END IF;
RETURN NEW;
END
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER order_limit_trigger
BEFORE INSERT ON orders 
FOR EACH ROW EXECUTE PROCEDURE order_limit();