package com.haniokasai.nukkit.MyAuth;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;


public class Main extends PluginBase implements Listener{
	static Map<String, Boolean> lged = new HashMap<String, Boolean>();
	static Map<String, Boolean> prerg = new HashMap<String, Boolean>();
	static Map<String, Integer> ct = new HashMap<String, Integer>();
	static int ctime =(int) (System.currentTimeMillis()/1000);
	public static File path;

	public void onEnable (){
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new events(this), this);

		String name = "kkK!!!";
		prerg.put(name,true);
		lged.put(name,true);
		ct.remove(name,true);
		getDataFolder().mkdir();
		path  = getDataFolder();

		@SuppressWarnings("deprecation")
		Config config = new Config(
                new File(this.getDataFolder(), "config.yml"),Config.YAML,
                new LinkedHashMap<String, Object>() {
                    {
                    	put("//mysql///", "");
                    	put("ip", "localhost");
                    	put("port", "3306");
                    	put("user", "root");
                    	put("passwd", "passwd");
                    	put("dbname", "myauth");
                    }
                });
        config.save();
config.get("ip");
 mysql.load();
    }

	public void onDisable() {
		mysql.shutdown();
		Map<UUID, Player>  onlinePlayers = Server.getInstance().getOnlinePlayers();
		for (Entry<UUID, Player> online : onlinePlayers.entrySet()) {
			online.getValue().sendMessage("[MyAuth]Please Login to server /login <passwd>");
		}
	}

	@EventHandler
	public void join(PlayerJoinEvent event){
				Player player = event.getPlayer();
				String name = player.getName();
				String ip = player.getAddress();
				String cid = player.getUniqueId().toString();
				HashMap<String,String> map = mysql.get(name);
				if (!map.containsValue(name)) {
					player.sendMessage("[MyAuth]Please Register your account /register <passwd>");
					prerg.put(name,true);
				}else{
			        Date date = new Date(((long)Integer.parseInt(map.get("llogin")) * 1000));
			        player.sendMessage(TextFormat.RED +"[MyAuth] last login:"+date);
			        ctime = (int) (System.currentTimeMillis()/1000);
			        mysql.settime(name,ctime);
					if (map.get("ip").equals(ip) & map.get("cid").equals(cid)) {
						player.sendMessage("[MyAuth]Logined!");
						lged.put(name,true);


					}else{
						player.sendMessage("[MyAuth]Please Login to server /login <passwd>");
					}
				}
    	}
	@EventHandler
	public void quit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		String name = player.getName();
		ct.remove(name);
		lged.remove(name);
		prerg.remove(name);
	}


     @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
     	switch (command.getName()) {
     	case "unregister":
             if (sender instanceof ConsoleCommandSender) {
         		if(args.length != 1){
         			sender.sendMessage("[MyAuth]/unregister <playername>");
         			break;
         		}
         		if(mysql.remove(args[0])){
         			sender.sendMessage("[MyAuth]Deleted : "+args[0]);
         		}else{
         			sender.sendMessage("[MyAuth]there isnt such a player : "+args[0]);
         		}


             }else{
             		sender.sendMessage("[MyAuth]You can from only console");
             }

     	break;
     	case "changepasswd":
     		if(args.length != 3){
     			sender.sendMessage("[MyAuth]/changepasswd <old passwd> <new passwd> <new passwd>");
     			break;
     		}
     		if(!args[1].equals(args[2])){
     			sender.sendMessage("[MyAuth]Please enter correct new-passwd.");
     			break;
     		}
     		String name = sender.getName();

			if(mysql.login(name,toEn(args[0]))){
				mysql.remove(name);
				mysql.regi((Player)sender, toEn(args[2]));
        		sender.sendMessage("[MyAuth]Everything Ok, Your Now Passwd is : "+args[1]);
			}else{
				sender.sendMessage("[MyAuth]Please enter correct old-passwd.");
			}
     		break;
     	}
     	Player player;
     	String name;
     	String ip;


        switch (command.getName()) {

        	case "register":
        		 player = (Player) sender;
        		 name = player.getName();
        		 ip = player.getAddress();
        		if (player instanceof Player) {
        			if(lged.containsKey(name)){
        				player.sendMessage("[MyAuth]You dont have to do it.");
        				break;
        			}
        			if(args.length != 1){
        				player.sendMessage("[MyAuth]/login <passwd> or /register <passwd>");
        				break;
        			}
                	if (prerg.containsKey(name)) {
                		String hashed = toEn(args[0]);
                		mysql.regi(player,hashed);
                		lged.put(name,true);
                		prerg.remove(name);
                		player.sendMessage("[MyAuth]Registered! Passwd: "+args[0]);
                	}
                }
            break;

        	case "login":
        		player = (Player) sender;
        		name = player.getName();
        		ip = player.getAddress();
        		String cid = player.getUniqueId().toString();
        		if (player instanceof Player) {
        			if(lged.containsKey(name)){
        				player.sendMessage("[MyAuth]You dont have to do it.");
        				break;
        			}
        			if(args.length != 1){
        				player.sendMessage("[MyAuth]/login <passwd> or /register <passwd>");
        				break;
        			}
        			String hashed = toEn(args[0]);
        			if(mysql.login(name,hashed)){
                		lged.put(name,true);
                		prerg.remove(name);
						mysql.setcid(name, cid,ip);
                		player.sendMessage("[MyAuth]Logined!");

        			}else{
        				if(ct.containsKey(name)){
        					ct.put(name, ct.get(name)-1);
        				}else{
        					ct.put(name, 10);
        				}

        				player.sendMessage("[MyAuth]Please enter correct passwd. Remaining : "+ct.get(name));
        				if(ct.get(name) <=0 ){
        					Server.getInstance().getIPBans().addBan(ip,"[MyAuth] 10 times passwd missing.", null,"[MyAuth]");
        					Server.getInstance().broadcastMessage("[MyAuth] "+name+" :missed passwd 10 times.");
        					ct.remove(name);
        				}

        			}

                }else{
                	player.sendMessage("[MyAuth]You can from only in-game");
                }
        		break;



        }
		return true;
     }




     private String toEn(String value) {
    	    MessageDigest md = null;
    	    StringBuilder sb = null;
    	    try {
    	        md = MessageDigest.getInstance("SHA-512");
    	    } catch (NoSuchAlgorithmException e) {
    	        e.printStackTrace();
    	    }
    	    md.update(value.getBytes());
    	    sb = new StringBuilder();
    	    for (byte b : md.digest()) {
    	        String hex = String.format("%02x", b);
    	        sb.append(hex);
    	    }
    	    return sb.toString();
    	}





}
