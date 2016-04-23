
import java.io.IOException;
import org.apache.lucene.queryparser.classic.ParseException;


public class App {
	public static void main(String[] args) throws IOException, ParseException {

		//Test search in an array of strings
		SingleSearch singleSearchObj= new SingleSearch();
		singleSearchObj.test("Manage Gigabyte");
		
    }


}
