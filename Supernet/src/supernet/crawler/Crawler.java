package supernet.crawler;

import java.util.ArrayList;
import java.util.List;

import supernet.page.Page;

public class Crawler {

	//private final int MAX_TRYS = 2;
	//private final int TIMEOUT = 1337;
	//private final int CHECK_INTERVAL = 150000;
	//private final int MAX_THREADS = 3;
	//private final int DEEP = 3;
	
	
	List<String> que = new ArrayList<String>();
	List<String> que2 = new ArrayList<String>();
	List<String> visited = new ArrayList<String>();
	List<String> links = new ArrayList<String>();

	public void crawl(String regex, Page start){
		
		
	}
	
	public void crawl(Page start){
		
		//Load first page and parse links
		links = start.getLinks();
        for (String link : links){
        	que.add(makeAbsolute(start.strUrl,link));
        }
        while(true){
        	que2.clear();
        	for(String page : que){
        		if(!(visited.contains(page)) && page.split("/").length <= 6 && page.contains(".asp") == false && page.contains(".bml") == false && page.contains("mailto:") == false){
    	        	start = new Page(page);
    	        	if(start.getHtml(true) != ""){
    	        		System.out.println("[+]"+page);
    	        		links = start.getLinks();
        	            for (String link : links){
        	            	que2.add(makeAbsolute(start.strUrl,link));
        	            }
    	        	}
    	        	visited.add(page);
            	}
            }
        	que.clear();
        	if(que2.size() > 1){
	        	for(String page : que2){
	            	if(!(visited.contains(page))){
	    	        	start = new Page(page);
	    	        	if(start.getHtml(true) != ""){
	    	        		System.out.println("[+]"+page);
	    	        		links = start.getLinks();
	        	            for (String link : links){
	        	            	que.add(makeAbsolute(start.strUrl,link));
	        	            }
	    	        	}
	    	        	visited.add(page);
	            	}
	            }
        	}else{
        		System.out.println("[!]No links to follow, check starting site");
        		System.exit(0);
        	}
        }  
	}
	
	private static String makeAbsolute(String url, String link) {
	    if (link.matches("http://.*")) {
	      return link;
	    }
	    else if (link.matches("https://.*")) {
		      return link;
		    }
	    else if (link.matches("/.*") && url.matches(".*$[^/]")) {
	      return url + "/" + link;
	    }
	    else if (link.matches("[^/].*") && url.matches(".*[^/]")) {
	      return url + "/" + link;
	    }
	    else if (link.matches("/.*") && url.matches(".*[/]")) {
	      return url + link;
	    }
	    else if (link.matches("/.*") && url.matches(".*[^/]")) {
	      return url + link;
	    }
	    return link;
	  }
	
}
