package com.gmail.rretzbach.ircbot;

import org.schwering.irc.lib.IRCConnection;

import com.gmail.rretzbach.ircbot.handler.ChainedMessageHandler;

public class HelpWordHandler extends ChainedMessageHandler {

    @Override
    public void handleMessage(IRCConnection conn, String target, String nick,
            String message) {
        if (target.matches("(?i)scirocco") && message.endsWith("help")) {
            conn.doPrivmsg(nick, "I am Ostwind's bot. github is my home: "
                    + "https://github.com/rretzbach/ircbot");
        }
    }

}
