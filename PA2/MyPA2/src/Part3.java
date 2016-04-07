//Yiran Zheng
//zhengyr@brandeis.edu
//Part3
//print out the pdf for orders
import java.sql.*;
import edu.brandeis.cs127b.pa2.latex.*;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Scanner;
import java.util.Set;
public class Part3 {
	static final String JDBC_DRIVER = "com.postgresql.jdbc.Driver";
	static final String DB_TYPE = "postgresql";
	static final String DB_DRIVER = "jdbc";
	static final String DB_NAME = System.getenv("PGDATABASE");
	static final String DB_HOST = System.getenv("PGHOST");
	static final String DB_URL = String.format("%s:%s://%s/%s",DB_DRIVER, DB_TYPE, DB_HOST, DB_NAME);
	static final String DB_USER = System.getenv("PGUSER");
	static final String DB_PASSWORD = System.getenv("PGPASSWORD");
	static Connection conn;

	public static void main(String[] args) throws SQLException{
		conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
		Scanner in = new Scanner(System.in);
		Document doc = new Document();
		Statement st = conn.createStatement();
		//go through the file for orders
		while (in.hasNextLine()){
			String[] arr = in.nextLine().split(":");
			String purchaseNumber = arr[0];
			String[] orders = arr[1].split(",");
			//no need for treeset
			//Set<Part> parts = new TreeSet<Part>();
			//integer as key so it's faster to check contains
			Map<Integer,Set<Part>> suppliers = new HashMap<Integer,Set<Part>>();
			//for loop go through orders for each part
			for(String o: orders){
				String[] os = o.split("x");
				int quantity = Integer.parseInt(os[0]);
				int partnumber = Integer.parseInt(os[1]);
				String query = "SELECT A.ps_suppkey, A.ps_supplycost " 
						+ "FROM partsupp AS A "
						+ "WHERE A.ps_partkey = "+ partnumber + " AND A.ps_availqty >= " + quantity  
						+ " AND A.ps_supplycost <= ALL (SELECT B.ps_supplycost "
						+ "FROM partsupp AS B "
						+ "WHERE B.ps_partkey = " + partnumber + " AND B.ps_availqty >= " + quantity +")";
				ResultSet rs = st.executeQuery(query);
				//get the supplier we picked, add the order to the hashmap
				while(rs.next()){
					int supplykey = rs.getInt(1);
					double supplycost = rs.getDouble(2);
					Part p = new Part(os[1], quantity);
					p.setCost(supplycost);
					if(!suppliers.keySet().contains(supplykey)){
						suppliers.put(supplykey, new HashSet<Part>());
					}
					suppliers.get(supplykey).add(p);
					
				}
				
			}
            Purchase p = new Purchase(purchaseNumber);
			for (int supp : suppliers.keySet()){
				Supplier s = new Supplier("" + supp);
				Suborder o = new Suborder(s);
				p.add(o);
				for (Part part : suppliers.get(supp)){
                    o.add(part);
				}
			}
			doc.add(p);
		}	
		System.out.println(doc);
	}

}
