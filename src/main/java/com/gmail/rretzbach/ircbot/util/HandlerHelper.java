package com.gmail.rretzbach.ircbot.util;

import org.apache.log4j.Logger;
import org.schwering.irc.lib.*;

import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rretzbach
 * Date: 27.05.12
 * Time: 15:22
 */
public class HandlerHelper {
    private static Logger log = Logger.getLogger(HandlerHelper.class);

    public static boolean isUser(IRCUser user, String userName) {
        int nextPoint = user.getHost().indexOf(".");
        if (nextPoint != -1) {
            String authUser = user.getHost().substring(0, nextPoint);
            return authUser.equals(userName);
        }
        return false;
    }

    public static void sendMessage(IRCConnection conn, String channel, String message) {
        try {
            conn.doPrivmsg(channel, message);
        } catch (Exception e) {
            log.error("Error while sending message", e);
            log.debug(String.format(
                    "Tried to send message %s to target %s", message,
                    channel));
        }
    }
    
    public static String chooseOne(String... strings) {
        return strings[(int) (Math.random() * strings.length)];
    }

    public static String chooseOne(List<String> strings) {
        return strings.get((int) (Math.random() * strings.size()));
    }


}
