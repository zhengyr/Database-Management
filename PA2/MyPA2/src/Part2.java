//Yiran Zheng
//zhengyr@brandeis.edu
//Part2
//graph the node and edge according to consumers in each region
import java.sql.*;
import edu.brandeis.cs127b.pa2.graphviz.*;
public class Part2 {
	static final String JDBC_DRIVER = "com.postgresql.jdbc.Driver";
	static final String DB_TYPE = "postgresql";
	static final String DB_DRIVER = "jdbc";
	static final String DB_NAME = System.getenv("PGDATABASE");
	static final String DB_HOST = System.getenv("PGHOST");
	static final String DB_URL = String.format("%s:%s://%s/%s",DB_DRIVER, DB_TYPE, DB_HOST, DB_NAME);
	static final String DB_USER = System.getenv("PGUSER");
	static final String DB_PASSWORD = System.getenv("PGPASSWORD");

	public static void main(String[] args) throws SQLException{
		DirectedGraph g = new DirectedGraph();
		try {
			Connection conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
			Statement st = conn.createStatement();
			String[] names= {"AFRICA", "AMERICA", "ASIA", "EUROPE", "MIDDLE EAST"};
			Node [] nodes = new Node[5];
			//create the node first
			for(int i = 0; i < 5; i++){
				nodes[i] = new Node(names[i]);
			}
			//nested loop go through each sales from one region to another
			for(int i = 0; i < 5; i++){
				for(int j = 0; j < 5; j++){
					String query = "SELECT SUM(l_extendedprice*(1-l_discount)*(1+l_tax)) "
									+ "FROM nation AS A, customer, orders, lineitem, supplier, nation AS B "
									+ "WHERE A.n_regionkey = "+ i + " AND A.n_nationkey = c_nationkey AND c_custkey = O_custkey "
									+ "AND O_orderkey = l_orderkey AND l_suppkey = s_suppkey AND s_nationkey = B.n_nationkey "
									+ "AND B.n_regionkey = " + j ;
					ResultSet rs = st.executeQuery(query);
					while(rs.next()){
						int a = rs.getInt(1);
						a = a/1000000;
						String weight = "$"+a+"M";
						DirectedEdge e = new DirectedEdge(nodes[i], nodes[j]);
						e.addLabel(weight);
						g.add(e);
					}
				}
			}
			System.out.println(g);
		} catch (SQLException s) {
			throw s;
		}

		

	}

}
