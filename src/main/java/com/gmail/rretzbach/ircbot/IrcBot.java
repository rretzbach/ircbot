package com.gmail.rretzbach.ircbot;

import java.io.IOException;

import com.gmail.rretzbach.ircbot.handler.*;
import org.apache.log4j.Logger;
import org.schwering.irc.lib.IRCConnection;
import org.schwering.irc.lib.IRCEventAdapter;
import org.schwering.irc.lib.IRCUser;

public class IrcBot extends IRCEventAdapter {

    private static Logger log = Logger.getLogger(IrcBot.class);

    private static final int RECONNECT_DELAY_SECS = 5000;
    private final IrcBotConfig config;
    private static IrcBot ircBot;

    public static IrcBot getIrcBot() {
        return ircBot;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    private MessageHandler messageHandler;
    private IRCConnection connection;

    public WelcomeFryHandler getJoinHandler() {
        return joinHandler;
    }

    private WelcomeFryHandler joinHandler;

    public static void main(String[] args) {
        log.info("Program started");
        IrcBotConfig connInfo = loadIrcConnectionInfo();
        ircBot = new IrcBot(connInfo);

        MessageHandler handler = linkMessageHandlers();

        ircBot.setMessageHandler(handler);
        ircBot.setJoinHandler(new WelcomeFryHandler());
        ircBot.connectAndStayConnected();
    }

    private static MessageHandler linkMessageHandlers() {
        MessageHandler reloadHandler = new ReloadCommandHandler();
        MessageHandler frownHandler = new TurnYourFrownUpsideDownHandler();
        MessageHandler webLinkHandler = new WebLinkHandler();
        HelpWordHandler helpWordHandler = new HelpWordHandler();
        TapirWordHandler tapirWordHandler = new TapirWordHandler();
        tapirWordHandler.loadFactsFromFile("tapirfacts.txt");

        webLinkHandler.setNextMessageHandler(tapirWordHandler);
        tapirWordHandler.setNextMessageHandler(helpWordHandler);
        helpWordHandler.setNextMessageHandler(frownHandler);
        frownHandler.setNextMessageHandler(reloadHandler);

        return webLinkHandler;
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
        connInfo.setChannel("#vegan");
        return connInfo;
    }

    public IrcBot(IrcBotConfig config) {
        this.config = config;
    }

    protected void connectAndStayConnected() {
        try {
            this.connection = createIrcConnection();
            this.connection.connect();
        } catch (Exception ioexc) {
            log.error("Error while connecting", ioexc);
            reconnect();
        }
    }

    protected void reconnect() {
        log.info("Reconnecting");
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
        log.info("Connected");
        getConnection().doJoin(getConfiguredChannel());
    }

    public IRCConnection getConnection() {
        return connection;
    }

    public String getConfiguredChannel() {
        return config.getChannel();
    }

    @Override
    public void onDisconnected() {
        log.info("Disconnected");
        reconnect();
    }

    @Override
    public void onPrivmsg(String target, IRCUser user, String msg) {
        if (messageHandler == null) {
            return;
        }

        log.debug(String.format("Incoming message: [target=%s;nick=%s;msg=%s]",
                target, user.getNick(), msg));
        messageHandler.handleMessage(connection, target, user.getNick(), msg);
    }

    @Override
    public void onJoin(String chan, IRCUser user) {
        if (joinHandler == null) {
            return;
        }

        log.debug(String.format("Joined #%s: nick=%s,username=%s,host=%s",
                chan, user.getNick(), user.getUsername(), user.getHost()));
        joinHandler.onJoin(connection, chan, user);
    }

    private void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
    }

    private void setJoinHandler(WelcomeFryHandler handler) {
        this.joinHandler = handler;
    }
}
