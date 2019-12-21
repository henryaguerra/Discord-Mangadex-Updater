import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;

class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        // If message is from bot ignore, checks for prefix, getsTitle and tells the user it is tracking new chapters
        String message = event.getMessage().getContentRaw();
        if(!event.getMessage().getAuthor().isBot() && message.contains(">")) {

            try {
                UrlHandler urlHandler = new UrlHandler(message.substring(1));
                String title = urlHandler.getTitle();
                urlstuff.updateUsersObject(urlHandler, event.getMessage().getAuthor());

                event.getChannel().sendMessage("You'll now get " +
                        "updated whenever a new chapter of \"" + title + "\" is uploaded to MangaDex!").queue();
            }
            catch (Exception e){
                System.err.println(e.toString());
            }
        }
        // WHen a user sends a message, if url does not exist, add it to a map that holds users and urls that are being
        // tracked. at intervals, iterate through the list, check for updates, if updates exist then send a message to
        // the users in the map, telling them that a new chapter has been uploaded.
    }
}