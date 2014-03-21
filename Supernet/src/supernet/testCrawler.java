package supernet;
 
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import supernet.crawler.Crawler;
import supernet.page.Page;


public class testCrawler {

	
	private final static int PROXY_TIMEOUT = 500;
	private final static int MAX_TRIES = 2;
	private final static String STARTING_URL = "http://google.com";
	
	public static void main(String[] args){

		        
		        //Attempt to connect proxy
		        System.out.println("[-]Attempting to connect to proxy...");
		        setProxy("173.201.95.24","80");
		        
		        //If it works load Page and start Crawling
		        if(checkProxy(true)){
		        	Page current = new Page(STARTING_URL);
		        	Crawler crawler = new Crawler();
		        	crawler.crawl(current);
		        }

		        
	}
	
	public static List<String> getProxies(){
		
		List<String> proxies = new ArrayList<String>();
		//String ip_regex = "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
		String source = null;
		int count = 0;
		
		try{
			source = getHtml("http://www.rmccurdy.com/scripts/proxy/good.txt",true);
		}catch(Exception e){
			System.out.println("[!]Could not load proxy list");
			return null;
		}
		
		for(String addr : source.split("\n")){
			if( (addr.split(":")[1].equals("80")) ){
				proxies.add(addr);
				count++;
			}
		}	
		
		System.out.println("[+]" + count + " proxies found.");
		return proxies;
	}
	
	public static boolean checkProxy(boolean infob){
		
		String city_regex = "(<label for=\"chkRegionCity\">.*?</label>)";
		String country_regex = "(<label for=\"chkCountry\">.*?</label>)";
		String isp_regex = "(<label for=\"chkISP\">.*?</label>)";
		
		String info = "";
		
		try {
				String source = getHtml("http://www.ip2location.com",false);
				
				Matcher matcher = Pattern.compile(isp_regex).matcher(source);
				while(matcher.find()){
					String found = matcher.group(1);
					if(!found.contains("ISP<")){
						info += found.replace("<label for=\"chkISP\">", "").replace("</label>", "") + "\t";
					}  
				}
				
				matcher = Pattern.compile(city_regex).matcher(source);
				while(matcher.find()) {
					String found = matcher.group(1);
					if(!found.contains("Region & City")){
						info += found.replace("<label for=\"chkRegionCity\">", "").replace("</label>", "") + "\t";
					}    
				}
				
				matcher = Pattern.compile(country_regex).matcher(source);
				while(matcher.find()){
					String found = matcher.group(1);
					if(!found.contains("Country<")){
						info += found.replace("<label for=\"chkCountry\">", "").replace("</label>", "") + "\t";
					}  
				}
				if(infob){
					System.out.println(info);
				}
				return true;
		} catch (Exception e) {
			System.out.println("[!]Error with proxy check");
			return false;
		}
	}
	
	public static void bruteProxy(List<String> list){
		String ip = "";
        for (String proxy : list){
        	try{
        		ip = proxy.split(":")[0];
        		String port = proxy.split(":")[1];
        			if(setProxy(ip, port)){
        				break;
        			}
        	}catch(Exception e){
        		System.out.println("[-]" + ip + " not working");
        	}
        }
	}
	
	public static boolean setProxy(String ip, String port){
		
		//Get old ip
		String old_ip = getHtml("http://bot.whatismyipaddress.com/",false);
		
		
		//Set system properties
		try{
			System.setProperty("http.proxyHost", ip); 
		    System.setProperty("http.proxyPort", port);
		    System.setProperty("http.proxySet", "true");
		    System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1"); 
		}catch(Exception e){
			System.out.println("[!]Unable to set proxy ip and/or port");
			return false;
		}
		
		
		
		//Check to see if ip is changed to that of the proxy
		for(int i = 0; i<MAX_TRIES;i++){
			
			//Give it a few seconds to connect
			try {
			    Thread.sleep(PROXY_TIMEOUT);
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			
			try {
				String p_ip = getHtml("http://bot.whatismyipaddress.com/",false);
				if(!(p_ip.contains(old_ip)) && p_ip != null){
					System.out.println("[+]Connecting to proxy: " + ip + ":" + port);
					System.out.println("[+]Success!");
					return true;
				}else{
					System.out.println("[-]" + ip + ":" + port + " not working trying again");
				}
			} catch (Exception e) {
				System.out.println("[!]Error with proxy, trying again...");
			}
		}
		return false;
	}
	
	public static String getHtml(String ref, boolean keep_lines){
			
		String html = null;
		 try {
			 	URL url = new URL(ref);
			 	URLConnection uc = url.openConnection();
			 	uc.setRequestProperty("User-Agent", "Mozilla/5.0 Gecko/20100316 Firefox/3.6.2");
	            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
	            String strTemp = "";
	            while(null != (strTemp = br.readLine())){
	            	if(keep_lines){
	            		strTemp+= "\n";
	            	}
	            	html += strTemp;
	            }
	            br.close();
	            
	        } catch (Exception ex) {
	            System.out.println("[!]Error with loading page");
	        }
		
		return html;
	}

	
}
