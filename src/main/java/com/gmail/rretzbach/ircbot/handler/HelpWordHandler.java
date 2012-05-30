package com.gmail.rretzbach.ircbot.handler;

import org.schwering.irc.lib.IRCConnection;

import com.gmail.rretzbach.ircbot.handler.ChainedMessageHandler;

public class HelpWordHandler extends ChainedMessageHandler {

    @Override
    public void handleMessage(IRCConnection conn, String target, String nick,
            String message) {
        if (target.matches("(?i)scirocco") && message.endsWith("help")) {
            conn.doPrivmsg(nick, "I am Ostwind's and baku's bot. github is my home: "
                    + "https://github.com/rretzbach/ircbot");
        }

        MessageHandler handler = getNextMessageHandler();
        if (handler != null) {
            handler.handleMessage(conn, target, nick, message);
        }
    }

}
