package com.gmail.rretzbach.ircbot.handler;

import org.apache.log4j.Logger;
import org.schwering.irc.lib.IRCConnection;
import org.schwering.irc.lib.IRCUser;

public class WelcomeFryHandler {
    private static Logger log = Logger.getLogger(WelcomeFryHandler.class);

    public void onJoin(final IRCConnection conn, final String chan, IRCUser user) {
        if (isFry(user)) {
            // wait 10 - 60 seconds until greeting fry
            new Thread() {
                public void run() {
                    double random = Math.random();
                    long waitAmount = (long) (10 + (random * 20));
                    final String greeting = generateGreeting(random);
                    log.info("Waiting for " + waitAmount + " seconds to say "
                            + greeting);
                    try {
                        Thread.sleep(waitAmount * 1000);
                    } catch (InterruptedException e) {
                        log.error("Could not sleep current thread", e);
                    }
                    conn.doPrivmsg(chan, greeting);
                };
            }.start();
        }
    }

    protected String generateGreeting() {
        String greetingBase = "fryyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy";
        return greetingBase = greetingBase.substring(0,
                3 + (int) (Math.random() * (greetingBase.length() - 3)));
    }

    protected String generateGreeting(double random) {
        String greetingBase = "fryyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy";
        return greetingBase = greetingBase.substring(0,
                3 + (int) (random * (greetingBase.length() - 3)));
    }

    protected boolean isFry(IRCUser user) {
        try {
            int nextPoint = user.getHost().indexOf(".");
            String authUser = user.getHost().substring(0, nextPoint);
            return authUser.equals("fry");
        } catch (Exception e) {
            return false;
        }
    }
}
