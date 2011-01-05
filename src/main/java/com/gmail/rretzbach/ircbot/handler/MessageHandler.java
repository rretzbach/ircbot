package com.gmail.rretzbach.ircbot.handler;

import org.schwering.irc.lib.IRCConnection;

public interface MessageHandler {

    public void handleMessage(IRCConnection conn, String target, String user,
            String message);

    public MessageHandler getNextMessageHandler();

    public void setNextMessageHandler(MessageHandler handler);

}