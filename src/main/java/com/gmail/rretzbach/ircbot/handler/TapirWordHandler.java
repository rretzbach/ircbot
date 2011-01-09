package com.gmail.rretzbach.ircbot.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.schwering.irc.lib.IRCConnection;

public class TapirWordHandler extends ChainedMessageHandler implements
        MessageHandler {
    private static Logger log = Logger.getLogger(TapirWordHandler.class);

    private List<String> tapirFacts;

    @Override
    public void handleMessage(IRCConnection conn, String target, String nick,
            String message) {
        if (isHandlingRequired(conn.getNick(), target, nick, message)) {
            int factNumber = pickRandomNumber();
            String tapirFact = tapirFacts.get(factNumber);
            String finalMessage = buildMessage(factNumber, tapirFact);
            try {
                conn.doPrivmsg(target, finalMessage);
            } catch (Exception e) {
                log.error("Error while sending message", e);
                log.debug(String.format(
                        "Tried to send message %s to target %s", finalMessage,
                        target));
            }
        }
        MessageHandler handler = getNextMessageHandler();
        if (handler != null) {
            handler.handleMessage(conn, target, nick, message);
        }
    }

    private String buildMessage(int factNumber, String tapirFact) {
        return String.format("Tapir Fun Fact #%d: \"%s\"", factNumber,
                tapirFact);
    }

    private int pickRandomNumber() {
        return (int) (Math.random() * tapirFacts.size());
    }

    protected boolean isHandlingRequired(String myNick, String target,
            String nick, String message) {
        boolean containsTapirWord = containsTapirWord(message);
        boolean myMessage = myNick.equals(nick);
        return containsTapirWord && !myMessage;
    }

    private boolean containsTapirWord(String message) {
        Pattern tapirWordPattern = Pattern.compile("tapir",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = tapirWordPattern.matcher(message);
        return matcher.find();
    }

    public void loadFactsFromFile(String string) {
        tapirFacts = new ArrayList<String>();

        InputStream resourceAsStream = getClass().getResourceAsStream(
                "/" + string);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                resourceAsStream));

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                tapirFacts.add(line);
            }
        } catch (IOException e) {
            log.error(String.format("Error while loading file %s", string), e);
        }
    }

}
