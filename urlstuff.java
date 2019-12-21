import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import okhttp3.OkHttpClient;
import org.apache.commons.codec.digest.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.*;
import java.util.*;



public class urlstuff {

    static class onTerminate extends Thread{
        @Override
        public void run() {
            System.out.println("This ran");
        }
    }

    // List should hold urls as the first value, and a list of usernames for the second value
    private static Map<UrlHandler, ArrayList<User>> usersList = new HashMap<UrlHandler, ArrayList<User>>();
    static JDA bot;

    public static void main(String[] args) throws IOException, InterruptedException {

//        String url = "https://mangadex.org/title/20679/5toubun-no-hanayome";
        String url = "https://mangadex.org/title/350/vinland-saga";



        Runtime.getRuntime().addShutdownHook(new onTerminate());
        // Scan file for token
        File file = new File("token.txt");
        Scanner scanner = new Scanner(file);
        String token = scanner.nextLine();

        // Bot instantiation stuff
        try {
            bot = new JDABuilder(token)
                    .addEventListeners(new MessageListener())
                    .build();
        } catch (Exception e){
            System.err.println("Login exception");
        }

        bot.awaitReady();

        // This will continuously check for chapters (every 20 seconds)
        while(true){

            for(HashMap.Entry element: usersList.entrySet()){

                UrlHandler urlHandler = (UrlHandler) element.getKey();
                if(urlHandler.checkForNewChapter()){
                    sendMessages((ArrayList<User>)element.getValue(), bot, urlHandler.getTitle());
                    break;
                }


            }
            Thread.sleep(20000);
        }
    }

//    private static void urlthings(String spec) throws IOException {
//
//        Document doc = Jsoup
//                .connect(spec)
//                .userAgent(
//                        "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.110 Safari/537.36")
//                .timeout(0).followRedirects(true).execute().parse();
//
////        System.out.println(doc.outerHtml().toString());
////        Elements div = doc.select("div.row no-gutters");
////        for (Element e: div){
////            System.out.println("text: " + e.text());
////        }
//
//
//        Element rows = doc.select("div.container").get(1);
//        Element col = rows.select("div.col").get(4);
//        Elements flag = rows.select("div.chapter-list-flag");
//        System.out.println(col.text());
//
//        for(Element e: flag) {
//            Element span = e.select("span").first();
//            String classS = span.attr("class");
//            System.out.println(classS);
//        }
//
//        System.out.println(doc.select("title").text());
//    }

    // updates the global usersObjectList, its in here so we can update it from MessageListener
    public static void updateUsersObject(UrlHandler url, User user){

        // If url doesnt exist in map then add it
        if(!usersList.containsKey(url)){
            ArrayList<User> userList =  new ArrayList<User>();
            userList.add(user);
            usersList.put(url, userList);
        }

        // If url exists already then add it to the list
        else{
            usersList.get(url).add(user);
        }

        updateTextFile();
    }

    private static void sendMessages(ArrayList<User> users, JDA bot, String title){

        String content = "A new chapter of \"" + title + "\" has been released!";
        for(User user: users){
            user.openPrivateChannel().queue( (channel) -> channel.sendMessage(content).queue());
        }
    }

    private static void updateTextFile(){

    }

}





