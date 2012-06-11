package com.gmail.rretzbach.ircbot.handler;

import com.gmail.rretzbach.ircbot.util.HandlerHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.schwering.irc.lib.IRCConnection;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: rretzbach
 * Date: 27.05.12
 * Time: 15:59
 */
public class TurnYourFrownUpsideDownHandler extends ChainedMessageHandler implements
        MessageHandler {
    private static Logger log = Logger.getLogger(TurnYourFrownUpsideDownHandler.class);

    @Override
    public void handleMessage(IRCConnection conn, String target, String nick, String message) {
        if (isHandlingRequired(conn.getNick(), target, nick, message)) {
            try {
                String bridge = HandlerHelper.chooseOne("I hope this can make you feel a little better", "here is something to cheer you up:", "turn your frown upside down!", "why so sad?", "there, there.", "please don't be so sad.");
                String url = fetchRandomAwwURL();
                String finalMessage = String.format("Hey %s, %s %s", nick, bridge, url);
                HandlerHelper.sendMessage(conn, target, finalMessage);
            } catch (Exception e) {
                log.error("Error while trying to cheer someone up :/", e);
            }
        }

        MessageHandler handler = getNextMessageHandler();
        if (handler != null) {
            handler.handleMessage(conn, target, nick, message);
        }
    }

    protected String fetchRandomAwwURL() {
        List<String> urlPool = new ArrayList<String>();
        urlPool.addAll(getRedditImgurURLs("aww"));
        urlPool.addAll(getRedditImgurURLs("animalporn"));
        urlPool.addAll(getRedditImgurURLs("rabbits"));
        urlPool.addAll(getRedditImgurURLs("foxes"));
        urlPool.addAll(getRedditImgurURLs("hedgehogs"));
        urlPool.addAll(getRedditImgurURLs("ferrets"));
        return HandlerHelper.chooseOne(urlPool);
    }

    protected List<String> getRedditImgurURLs(String subreddit) {
        List<String> urls = null;

        try {
            URI url = new URI("http://www.reddit.com/r/"+subreddit+".json");
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);
            InputStream in = response.getEntity().getContent();
            JsonParser parser = new JsonParser();
            JsonElement elem = parser.parse(new InputStreamReader(in));
            JsonObject obj = elem.getAsJsonObject();
            JsonArray foo = obj.get("data").getAsJsonObject().get("children").getAsJsonArray();
            urls = new ArrayList<String>();
            for (Iterator<JsonElement> it = foo.iterator(); it.hasNext(); ) {
                JsonElement post = it.next();
                String link = post.getAsJsonObject().get("data").getAsJsonObject().get("url").getAsString();
                if (link.contains("imgur.com")) {
                    urls.add(link);
                }
            }

        } catch (Exception e) {
            log.error("Error while fetching /r/aww ", e);
        }

        return urls;
    }

    protected boolean isHandlingRequired(String myNick, String target,
                                         String nick, String message) {
        Pattern frownPattern = Pattern.compile("(?<![->(])[:;][-'´`]?[(<Cc]|[)>D][-'´`]?[:;](?![)<-])");
        return frownPattern.matcher(message).find();
    }
}
