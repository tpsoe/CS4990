
import java.util.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;




public class Crawler {
	
	private static final int MAX_PAGES = 10;
    private Set<String> pagesVisited = new HashSet<String>();
    private List<String> Links = new LinkedList<String>();
	private Queue<String> qList = new LinkedList<String>();

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
	
	private String getNextUrl()
    {
        String nextUrl;
        nextUrl = this.qList.poll();
        if(this.pagesVisited.contains(nextUrl)){
        	nextUrl = this.qList.poll();
        }
        this.pagesVisited.add(nextUrl);
        return nextUrl;
    }
	
	public boolean connect(String url){
		try
		{
			Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
			Document htmlDocument = connection.get();
			String html = Jsoup.connect(url).get().html();

			url = url.replace("http://", "").replace("https://","").replaceAll("/", "-") ;
			File repository = new File("repository");
			repository.mkdir();
			
            File htmlFile = new File(repository + "/" + url + ".html") ;
            
            File txtFile = new File("text.txt");
            FileWriter txtFileWriter = new FileWriter(txtFile,true);
            
//            Document htmlBodyDocument = Jsoup.parseBodyFragment(html);
//            Element body = htmlBodyDocument.body();
//            Elements paragraphs = body.getElementsByTag("p");
//            for (Element paragraph : paragraphs) {
//            	txtFileWriter.write(paragraphs.text());
//            	}
//            
//            
//            
//            txtFileWriter.close();
            Elements allSmallSpan = htmlDocument.select("span.devsite-nav-text");
          
            
            System.out.println(allSmallSpan);
            
            try{
            BufferedWriter htmlWriter = new BufferedWriter(new FileWriter(htmlFile));
            htmlWriter.write(html);
            htmlWriter.close();
            }catch (IOException e){
            	e.printStackTrace();
            }

            
			Elements linksOnPage = htmlDocument.select("a[href]");
			int countLinks = linksOnPage.size();
			
			File csvFile = new File("report.csv");
			FileWriter fw = new FileWriter(csvFile,true);
			BufferedWriter csvWriter = new BufferedWriter(fw);
			csvWriter.write(url + " , " + countLinks + " number of outlinks" + "\n");
			csvWriter.close();

			System.out.println(linksOnPage.size() + " links");
			
			for(Element link : linksOnPage){
				this.Links.add(link.absUrl("href"));
			}
			return true;
		}
		catch(IOException ioe){
			return false;
		}
		
	}
	
	public List<String> getLinks(){
		return this.Links;
		
	}
	
	public void crawl(String url){
		
		
		while(this.pagesVisited.size() <= MAX_PAGES){
			String currentUrl;
			Crawler crawl = new Crawler();
			if(this.qList.isEmpty()){
				currentUrl = url;
				this.pagesVisited.add(url);
			}
			else {
				currentUrl = this.getNextUrl();
			}
			crawl.connect(currentUrl);
			
			this.qList.addAll(crawl.getLinks());
		}
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Crawler test = new Crawler();
		test.crawl("https://developer.android.com/reference/android/Manifest.permission");
	}

}
