package supernet.page;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Page {
	
	/*
	 * Web-Page class
	 * Useful for now
	 * May need to be changed for later
	 */
	
	static String A_TAG_MATCHING_GROUP = "(<a.*?>.*?</a>)";
	static String HREF_MATCHING_GROUP = "href=\"(.*?)\"";
	
	private String source;
	public URL url = null;
	public String strUrl;
	
	public Page(String url){
		
		try {
			strUrl = url;
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			System.out.println("[!]Error with url");
		}
		getHtml(true);
		
	}
	
	public String getHtml(boolean keep_lines){
		String html = "";
		 try {
	            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
	            String strTemp = "";
	            while(null != (strTemp = br.readLine())){
	            	if(keep_lines){
	            		strTemp+= "\n";
	            	}
	            	html += strTemp;
	            }
	        } catch (Exception ex) {
	        	//System.out.println("[!]Could not load " + strUrl);
	        }
		 
		this.source = html;
		return html;
	}
	
	public List<String> getLinks(){
		List<String> links = new ArrayList<String>();

		
		Matcher matcher = Pattern.compile(A_TAG_MATCHING_GROUP).matcher(this.source);
		while (matcher.find()) {
		        Matcher m2 = Pattern.compile(HREF_MATCHING_GROUP).matcher(matcher.group(1));
		        if(m2.find()){
		        	links.add(m2.group(1));
		        }
		}
		
	    return links;
		      
	}
	

}
