package com.gmail.rretzbach.ircbot.handler;

import com.gmail.rretzbach.ircbot.util.HandlerHelper;
import org.apache.log4j.Logger;
import org.schwering.irc.lib.IRCConnection;
import org.schwering.irc.lib.IRCEventAdapter;
import org.schwering.irc.lib.IRCUser;

public class WelcomeFryHandler {
    private static Logger log = Logger.getLogger(WelcomeFryHandler.class);

    public void onJoin(final IRCConnection conn, final String chan, IRCUser user) {
        if (isFry(user)) {
            // wait 10 - 60 seconds until greeting fry
            new Thread() {
                public void run() {
                    final Thread thisThread = Thread.currentThread();
                    double random = Math.random();
                    long waitAmount = (long) (10 + (random * 20));
                    final String greeting = generateGreeting(random);
                    log.info("Waiting for " + waitAmount + " seconds to say "
                            + greeting);
                    final IRCEventAdapter eventAdapter = new IRCEventAdapter() {
                        @Override
                        public void onPrivmsg(String target, IRCUser user, String msg) {
                            if (msg.startsWith("fry")) {
                                conn.doPrivmsg(chan, HandlerHelper.chooseOne("I don't blame you", "Why?", "I don't hate you", "No hard feelings"));
                                conn.removeIRCEventListener(this);
                                thisThread.interrupt();
                            }
                        }
                    };
                    conn.addIRCEventListener(eventAdapter);
                    try {
                        Thread.sleep(waitAmount * 1000);
                        conn.removeIRCEventListener(eventAdapter);
                        conn.doPrivmsg(chan, greeting);
                    } catch (InterruptedException e) {
                        log.error("Could not sleep current thread", e);
                    }
                }
            }.start();
        }
    }

    protected String generateGreeting() {
        return generateGreeting(Math.random());
    }

    protected String generateGreeting(double length) {
        String greetingBase = "fryyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy";
        return greetingBase = greetingBase.substring(0,
                3 + (int) (length * (greetingBase.length() - 3)));
    }

    protected boolean isFry(IRCUser user) {
        String userName = "fry";
        try {
            return HandlerHelper.isUser(user, userName);
        } catch (Exception e) {
            return false;
        }
    }
}
