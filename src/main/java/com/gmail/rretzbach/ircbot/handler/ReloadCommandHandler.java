package com.gmail.rretzbach.ircbot.handler;

import com.gmail.rretzbach.ircbot.IrcBot;
import com.gmail.rretzbach.ircbot.util.HandlerHelper;
import org.apache.log4j.Logger;
import org.schwering.irc.lib.IRCConnection;
import org.schwering.irc.lib.IRCEventAdapter;
import org.schwering.irc.lib.IRCUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReloadCommandHandler extends ChainedMessageHandler {
    private static Logger log = Logger.getLogger(ReloadCommandHandler.class);

    @Override
    public void handleMessage(final IRCConnection conn, final String target, final String nick,
                              String message) {

        if (message.startsWith("!reload")) {
            final IRCEventAdapter eventAdapter = new IRCEventAdapter() {
                private boolean active = true;

                @Override
                public void onReply(int num, String value, String msg) {
                    // 2 check if he is authorized

                    if (active && msg.equals(nick)) {
                        active = false;

                        Matcher m = Pattern.compile("(\\S+) (\\S+) \\S+ (\\S+) \\*").matcher(value);
                        if (!m.find()) {
                            return;
                        }

                        IRCUser actualUser = new IRCUser(nick, m.group(2), m.group(3));
                        if (HandlerHelper.isUser(actualUser, "baku") || HandlerHelper.isUser(actualUser, "Ostwind")) {
                            MessageHandler handler = IrcBot.getIrcBot().getMessageHandler();
                            do {
                                if (handler instanceof TapirWordHandler) {
                                    ((TapirWordHandler) handler).reload();
                                }
                            } while ((handler = handler.getNextMessageHandler()) != null);
                            conn.doPrivmsg(target, "done");
                            conn.removeIRCEventListener(this);
                        }
                    }
                }
            };

            conn.addIRCEventListener(eventAdapter);

            // 1 ask for nick's hostmask
            conn.doWhois(nick);
        }

        MessageHandler handler = getNextMessageHandler();
        if (handler != null) {
            handler.handleMessage(conn, target, nick, message);
        }
    }

}
