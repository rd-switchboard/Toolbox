import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;


public class App {
	
   public static void main(final String[] args) throws IOException,SAXException, TikaException {

	   	  long startTime = System.currentTimeMillis();
	   	  
	      //detecting the file type
	      BodyContentHandler handler = new BodyContentHandler();
	      Metadata metadata = new Metadata();
	      FileInputStream inputstream = new FileInputStream(new File("data/sample.html"));
	      ParseContext pcontext = new ParseContext();
	      
	     
	      
	      //Html parser 
	      HtmlParser htmlparser = new HtmlParser();
	      htmlparser.parse(inputstream, handler, metadata,pcontext);
	      String s= handler.toString();
	      System.out.println("Contents of the document:" + s);
	      System.out.println("Metadata of the document:");
	      String[] metadataNames = metadata.names();
	      
	      for(String name : metadataNames) {
	         System.out.println(name + ":   " + metadata.get(name));  
	      }
	      
	  	long endTime = System.currentTimeMillis() - startTime;		
		System.out.println("TIme Spent: " + endTime + " ms");
	   }

}
