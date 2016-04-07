--Yiran Zheng
--zhengyr@brandeis.edu
--Problem 2
--if retail price changes, then change supply price by the same amount
CREATE FUNCTION retail_supply_price() 
RETURNS TRIGGER AS $$
BEGIN
UPDATE partsupp 
SET ps_supplycost = NEW.p_retailprice - OLD.p_retailprice + ps_supplycost
WHERE ps_partkey = NEW.p_partkey AND OLD.p_retailprice <> NEW.p_retailprice;
RETURN NEW;
END
$$ LANGUAGE 'plpgsql';
--everytime a new update 
CREATE TRIGGER retail_supply_price_trigger
AFTER UPDATE ON part
FOR EACH ROW EXECUTE PROCEDURE retail_supply_price();