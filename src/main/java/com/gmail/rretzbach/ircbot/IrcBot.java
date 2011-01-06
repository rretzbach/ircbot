package com.gmail.rretzbach.ircbot;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.schwering.irc.lib.IRCConnection;
import org.schwering.irc.lib.IRCEventAdapter;
import org.schwering.irc.lib.IRCUser;

import com.gmail.rretzbach.ircbot.handler.MessageHandler;
import com.gmail.rretzbach.ircbot.handler.WebLinkHandler;

public class IrcBot extends IRCEventAdapter {

    private static Logger log = Logger.getLogger(IrcBot.class);

    private static final int RECONNECT_DELAY_SECS = 5000;
    private final IrcBotConfig config;
    private MessageHandler messageHandler;
    private IRCConnection connection;

    public static void main(String[] args) {
        log.info("Program started");
        IrcBotConfig connInfo = loadIrcConnectionInfo();
        IrcBot ircBot = new IrcBot(connInfo);
        ircBot.setMessageHandler(new WebLinkHandler());
        ircBot.connectAndStayConnected();
        log.info("Program exited");
    }

    // TODO read in properties file
    public static IrcBotConfig loadIrcConnectionInfo() {
        IrcBotConfig connInfo = new IrcBotConfig();
        connInfo.setHost("irc.iz-smart.net");
        connInfo.setNick("Scirocco");
        connInfo.setEmail("Ostwind's Bot");
        connInfo.setRealName("Ostwind's Bot");
        connInfo.setPortMaximum(6669);
        connInfo.setPortMinimum(6667);
        // connInfo.setChannel("#vegan");
        connInfo.setChannel("#vegan-test");
        return connInfo;
    }

    private void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
    }

    public IrcBot(IrcBotConfig config) {
        this.config = config;
    }

    /**
     * Reconnects if error occurs or thread terminates
     */
    protected void connectAndStayConnected() {
        try {
            this.connection = createIrcConnection();
            this.connection.connect();
        } catch (Exception ioexc) {
            log.error("error while connecting", ioexc);
            reconnect();
        }
    }

    /**
     * Waits 5 seconds before trying to reconnect
     */
    protected void reconnect() {
        log.info("reconnecting");
        try {
            Thread.sleep(RECONNECT_DELAY_SECS);
        } catch (InterruptedException e) {}
        connectAndStayConnected();
    }

    protected IRCConnection createIrcConnection() throws IOException {
        IRCConnection conn = new IRCConnection(config.getHost(),
                config.getPortMinimum(), config.getPortMaximum(),
                config.getPassword(), config.getNick(), config.getEmail(),
                config.getRealName());

        conn.addIRCEventListener(this);
        conn.setEncoding("UTF-8");
        conn.setDaemon(false);
        conn.setColors(false);
        conn.setPong(true);

        return conn;
    }

    @Override
    public void onRegistered() {
        log.info("connected");
        connection.doJoin(config.getChannel());
    }

    @Override
    public void onDisconnected() {
        log.error("disconnected");
        reconnect();
    }

    @Override
    public void onPrivmsg(String target, IRCUser user, String msg) {
        if (messageHandler == null) {
            return;
        }

        log.debug(String.format("incoming message: [target=%s;nick=%s;msg=%s]",
                target, user.getNick(), msg));
        messageHandler.handleMessage(connection, target, user.getNick(), msg);
    }
}
