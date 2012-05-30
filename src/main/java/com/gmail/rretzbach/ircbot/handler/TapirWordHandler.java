package com.gmail.rretzbach.ircbot.handler;

import com.gmail.rretzbach.ircbot.util.HandlerHelper;
import com.gmail.rretzbach.ircbot.util.RandomUtil;
import org.apache.log4j.Logger;
import org.schwering.irc.lib.IRCConnection;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TapirWordHandler extends ChainedMessageHandler implements
        MessageHandler {
    private static Logger log = Logger.getLogger(TapirWordHandler.class);

    private List<String> tapirFacts;
    private Queue<Integer> shuffledFactIndexes;
    private String tapirFactsFile;

    @Override
    public void handleMessage(IRCConnection conn, String target, String nick,
                              String message) {
        if (isHandlingRequired(conn.getNick(), target, nick, message)) {
            int factIndex = pickRandomNumber();
            String finalMessage = getTapirFact(factIndex);
            HandlerHelper.sendMessage(conn, target, finalMessage);
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
        boolean myMessage = myNick.equals(nick);
        return containsTapirWord(message) && !myMessage;
    }

    private boolean containsTapirWord(String message) {
        Pattern tapirWordPattern = Pattern.compile("tapir",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = tapirWordPattern.matcher(message);
        return matcher.find();
    }

    public void loadFactsFromFile(String fileName) {
        tapirFactsFile = fileName;

        String home = System.getProperty("user.home");

        InputStream resourceAsStream = null;
        try {
            resourceAsStream = new FileInputStream(new File(new File(home, "Shared"), tapirFactsFile));

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    resourceAsStream, Charset.forName("UTF-8")));

            shuffledFactIndexes = null;
            tapirFacts = null;

            String line = null;
            while ((line = reader.readLine()) != null) {
                addTapirFact(line);
            }
        } catch (IOException e) {
            log.error(String.format("Error while loading file %s", tapirFactsFile), e);
        }
    }

    protected void addTapirFact(String line) {
        if (tapirFacts == null) {
            tapirFacts = new ArrayList<String>();
        }

        tapirFacts.add(line);
    }

    public void reload() {
        loadFactsFromFile(tapirFactsFile);
    }
}
