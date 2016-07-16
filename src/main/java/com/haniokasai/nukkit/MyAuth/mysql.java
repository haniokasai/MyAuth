package com.haniokasai.nukkit.MyAuth;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Listener;
import cn.nukkit.utils.Config;

public class mysql implements Listener{

    static Main plugin;

    public mysql(Main plugin){
        mysql.plugin = plugin;
    }
    static Connection conn =null;

	public static void load(){

		 try {
			  Class.forName("com.mysql.jdbc.Driver");


			  Config config = new Config(new File(Main.path,"config.yml"));
			 conn =DriverManager.getConnection("jdbc:mysql://"+config.getString("ip")+":"+config.getString("port")+"/"+config.getString("dbname")+"?useUnicode=true&characterEncoding=UTF-8&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",config.getString("user"),config.getString("passwd"));
			 Statement stmt = conn.createStatement();
			 stmt.setQueryTimeout(10);
			 stmt.executeUpdate("CREATE TABLE IF NOT EXISTS player (name  VARCHAR(25) PRIMARY KEY , passwd TEXT, ip TEXT, cid TEXT, flogin TEXT, llogin TEXT)");
			 stmt.close();
			 Server.getInstance().getLogger().info("[MyAuth]Loaded");
			 Server.getInstance().getScheduler().scheduleRepeatingTask(new Runnable() {
				@Override
				public void run() {
					mysql.reload();
				}
			},20*30);
		}catch (ClassNotFoundException e){
			Server.getInstance().getLogger().info("[MyAuth] ClassNotFoundException"+e);
		}catch (Exception e){
			Server.getInstance().getLogger().info("[MyAuth] Exception"+e);
		}
	}

    public static boolean reload(){
        try {
			conn.close();
			Config config = new Config(new File(Main.path,"config.yml"));
			 conn =DriverManager.getConnection("jdbc:mysql://"+config.getString("ip")+":"+config.getString("port")+"/"+config.getString("dbname")+"?useUnicode=true&characterEncoding=UTF-8&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",config.getString("user"),config.getString("passwd"));
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
        return true;
   }

	public static void shutdown(){
        try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static HashMap<String, String> get(String name){
		HashMap<String,String> map = new HashMap<String,String>();
		 try {
			 Statement stmt = conn.createStatement();
			 stmt.setQueryTimeout(10);
			 ResultSet rs = stmt.executeQuery("SELECT * FROM player WHERE name = '"+name+"'");
		      while(rs.next()){
		         map.put("name", rs.getString("name"));
		         map.put("passwd", rs.getString("passwd"));
		         map.put("ip", rs.getString("ip"));
		         map.put("cid", rs.getString("cid"));
		         map.put("flogin", rs.getString("flogin"));
		         map.put("llogin", rs.getString("llogin"));
		      }
		      rs.close();
		      stmt.close();


		} catch (SQLException e) {
			e.printStackTrace();
		}

	      return map;
	}

	public static boolean settime(String name,int ctime) {
		try {
			Statement stmt = conn.createStatement();
		      stmt.executeUpdate("UPDATE player SET  llogin = '"+ctime+"'  WHERE name = '"+name+"'");
		      stmt.close();
		      return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public static boolean setcid(String name,String cid,String ip) {
		try {
			Statement stmt = conn.createStatement();
		      stmt.executeUpdate("UPDATE player SET  cid = '"+cid+"' ,ip = '"+ip+"'   WHERE name = '"+name+"'");
		      stmt.close();
		      return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}
	public static void regi(Player player, String hashed) {
		Statement stmt;
		try {
			stmt = conn.createStatement();
			 String sql;
		      sql = "SELECT name FROM player WHERE name = '"+player.getName()+"'";
		      ResultSet rs = stmt.executeQuery(sql);
		      String r = null;
		      while(rs.next()){
		         r = rs.getString("name");
		      }
		      if(r == null){
		    	  stmt.executeUpdate("INSERT INTO player (name,passwd,ip,cid,flogin,llogin) VALUES ('"+player.getName()+"','"+hashed+"','"+player.getAddress()+"','"+ player.getUniqueId().toString()+"','"+Main.ctime+"','"+Main.ctime+"');");
		      }
		      rs.close();

		    	  stmt.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	public static boolean remove(String name) {
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement("SELECT name FROM player WHERE name = ?");
			pstmt.setString(1, name);
			ResultSet rs = pstmt.executeQuery();
		      String r = null;
		      while(rs.next()){
		         r = rs.getString("name");
		      }
		      pstmt.close();
		      rs.close();
		      if(r.equals(name)){
		    	  pstmt = conn.prepareStatement("DELETE * FROM player WHERE name = ?");
		    	  pstmt.setString(1, name);
		    	  pstmt.executeUpdate();
		    	  rs.close();
		    	  pstmt.close();
		    	  return true;
		      }else{
		    	  return false;
		      }
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return false;
	}

	public static boolean login(String name, String hashed) {
		Statement stmt;
		String p = null;
		try {
			stmt = conn.createStatement();
			 String sql;
		      sql = "SELECT passwd FROM player WHERE name = '"+name+"'";
		      ResultSet rs = stmt.executeQuery(sql);

		      while(rs.next()){
		         p = rs.getString("passwd");
		      }
	    	  rs.close();
	    	  stmt.close();


		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		if(p.equals(hashed)){
	    	  return true;
	      }else{
	    	  return false;
	      }
	}

}
