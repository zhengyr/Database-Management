//Yiran Zheng
//zhengyr@brandeis.edu
//Part1
//graph the total sales of suppliers according to region
import java.sql.*;
import java.text.SimpleDateFormat;

import edu.brandeis.cs127b.pa2.gnuplot.*;
public class Part1 {
	static final String JDBC_DRIVER = "com.postgresql.jdbc.Driver";
	static final String DB_TYPE = "postgresql";
	static final String DB_DRIVER = "jdbc";
	static final String DB_NAME = System.getenv("PGDATABASE");
	static final String DB_HOST = System.getenv("PGHOST");
	static final String DB_URL = String.format("%s:%s://%s/%s",DB_DRIVER, DB_TYPE, DB_HOST, DB_NAME);
	static final String DB_USER = System.getenv("PGUSER");
	static final String DB_PASSWORD = System.getenv("PGPASSWORD");
	static Connection conn;

	public static void main(String[] args) throws SQLException {
		conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
        final String title = "Monthly TPC-H Order Sales Total By Region";
        final String xlabel = "Year";
        final String ylabel = "Order Total (Thousands)";
		TimeSeriesPlot plot = new TimeSeriesPlot(title, xlabel, ylabel);
		String[] names= {"AFRICA", "AMERICA", "ASIA", "EUROPE", "MIDDLE EAST"};
		//for loop will go through each eagion and then use the query to output the result
		for(int i = 0; i < 5; i++){
			//calculate the price for each lineitem and then sum them together by month
			String query = "SELECT EXTRACT(YEAR from l_shipdate), EXTRACT(MONTH from l_shipdate), SUM(l_extendedprice*(1-l_discount)*(1+l_tax)) "
							+ "FROM supplier, nation, lineitem "
							+ "WHERE s_nationkey = n_nationkey AND s_suppkey = l_suppkey AND n_regionkey = " + i + " "
							+ "GROUP BY EXTRACT(YEAR from l_shipdate), EXTRACT(MONTH from l_shipdate) "
							+ "ORDER BY EXTRACT(YEAR from l_shipdate), EXTRACT(MONTH from l_shipdate) ";
			try{
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(query);
		        DateLine l = new DateLine(names[i]);
		        //get data from query result and plot them into graph
				while ( rs.next() ) {
					String date = rs.getInt(1) + "/" + rs.getInt(2) + "/15";
					java.util.Date utilDate = new SimpleDateFormat("yyyy/MM/dd").parse(date);
					java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
					l.add(new DatePoint(sqlDate, rs.getDouble(3)/1000));

				}

				plot.add(l);
			}catch(Exception e){

			}
		}
		System.out.println(plot);
	}

}
