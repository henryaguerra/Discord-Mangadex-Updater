import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class UrlHandler {

    String url = null;
    Document doc = null;
    String currentChapter = null;
    String updatedChapter = null;

    public UrlHandler(String url) throws IOException{

        this.url = url;
        currentChapter = getCurrentChapter();
    }

    private void getDocument() throws IOException{
        this.doc = Jsoup
                .connect(this.url)
                .userAgent(
                        "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36")
                .timeout(0).followRedirects(true).execute().parse();
    }

    private String getCurrentChapter() throws IOException{

        getDocument();
        Element rows = doc.select("div.container").get(1);
        Elements flag = rows.select("div.chapter-list-flag");

        int index = 1;

        // Checks all flags until it reaches an english one
        for(Element e: flag) {
            Element span = e.select("span").first();
            String classS = span.attr("class");

            if(classS.equals("rounded flag flag-gb")){
                break;
            }

            index++;

            System.out.println(classS);

        }

        // Get chapter using the col-lg-5 tag at the index given so we know its an english chapter
        Element chapter = rows.select("div.col-lg-5").get(index);
        System.out.println(chapter.text());

        return chapter.text();

    }

    public String getTitle() {

        try{
            getDocument();
            getCurrentChapter();
        } catch (Exception e){
            System.err.println("Problem getting document url stuff");
            return null;
        }

        String title = doc.select("title").text();

        // MangaDex title format is usally [Title Name] (Title) - MangaDex, so we splice at (Title)
        String[] splitTitle = title.split(" ");
        StringBuilder titleFinal = new StringBuilder();
        for(String splicedString: splitTitle){
            if(splicedString.toString().toLowerCase().equals("(title)")){
                titleFinal.deleteCharAt(titleFinal.length()-1);
                break;
            }
            else{
                titleFinal.append(splicedString + " ");
            }
        }

        return titleFinal.toString();
    }


    public boolean checkForNewChapter() throws IOException{

        updatedChapter = getCurrentChapter();
        if(!updatedChapter.equals(currentChapter)){
            currentChapter = updatedChapter;
            return true;
        }

        return false;

    }


}
