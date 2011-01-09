package com.gmail.rretzbach.ircbot.handler;

import org.schwering.irc.lib.IRCConnection;

public abstract class ChainedMessageHandler implements MessageHandler {
    protected MessageHandler nextMessageHandler;

    @Override
    public abstract void handleMessage(IRCConnection conn, String target,
            String nick, String message);

    @Override
    public MessageHandler getNextMessageHandler() {
        return nextMessageHandler;
    }

    @Override
    public void setNextMessageHandler(MessageHandler handler) {
        this.nextMessageHandler = handler;
    }
}
