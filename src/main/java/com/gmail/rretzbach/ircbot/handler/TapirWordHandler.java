package com.gmail.rretzbach.ircbot.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.schwering.irc.lib.IRCConnection;

import com.gmail.rretzbach.ircbot.util.RandomUtil;

public class TapirWordHandler extends ChainedMessageHandler implements
        MessageHandler {
    private static Logger log = Logger.getLogger(TapirWordHandler.class);

    private List<String> tapirFacts;
    private Queue<Integer> shuffledFactIndexes;

    @Override
    public void handleMessage(IRCConnection conn, String target, String nick,
            String message) {
        if (isHandlingRequired(conn.getNick(), target, nick, message)) {
            int factIndex = pickRandomNumber();
            String finalMessage = getTapirFact(factIndex);
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

    public String getTapirFact(int index) {
        String tapirFact = tapirFacts.get(index);
        if (tapirFact == null) {
            return "No fact found";
        }
        return buildMessage(index, tapirFact);
    }

    private String buildMessage(int factIndex, String tapirFact) {
        return String.format("Tapir Fun Fact #%d: \"%s\"", factIndex + 1,
                tapirFact);
    }

    protected int pickRandomNumber() {
        if (shuffledFactIndexes == null || shuffledFactIndexes.isEmpty()) {
            List<Integer> shuffledIndexes = RandomUtil
                    .createShuffledIndexes(tapirFacts.size());
            shuffledFactIndexes = new LinkedBlockingDeque<Integer>(
                    shuffledIndexes);
        }

        return shuffledFactIndexes.remove();
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
        InputStream resourceAsStream = getClass().getResourceAsStream(
                "/" + string);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                resourceAsStream, Charset.forName(System
                        .getProperty("file.encoding"))));

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                addTapirFact(line);
            }
        } catch (IOException e) {
            log.error(String.format("Error while loading file %s", string), e);
        }
    }

    protected void addTapirFact(String line) {
        if (tapirFacts == null) {
            tapirFacts = new ArrayList<String>();
        }

        tapirFacts.add(line);
    }

}
