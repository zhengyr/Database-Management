import java.sql.*;
import edu.brandeis.cs127b.pa2.latex.*;
import java.util.Map;
import java.util.HashMap;
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
		while (in.hasNextLine()){
			String[] arr = in.nextLine().split(":");
			String purchaseNumber = arr[0];
			Set<Part> parts = new TreeSet<Part>();
			Map<Supplier,Set<Part>> suppliers = new HashMap<Supplier,Set<Part>>();
            Purchase p = new Purchase(purchaseNumber);
			for (Supplier supp : suppliers.keySet()){
				Suborder o = new Suborder(supp);
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
