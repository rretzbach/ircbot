package com.gmail.rretzbach.ircbot.handler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.schwering.irc.lib.IRCConnection;

public class WebLinkHandler extends ChainedMessageHandler {
    private static Logger log = Logger.getLogger(WebLinkHandler.class);

    private static final int FETCH_LIMIT_CHARS = 5000;

    @Override
    public void handleMessage(IRCConnection conn, String target, String nick,
            String message) {
        if (isHandlingRequired(conn, target, nick, message)) {
            List<String> links = extractLinks(message);
            List<String> titles = fetchTitles(links);
            if (!titles.isEmpty()) {
                String finalMessage = buildTitleMessage(titles);
                try {
                    conn.doPrivmsg(target, finalMessage);
                } catch (Exception e) {
                    log.error("Error while sending message", e);
                    log.debug(String.format(
                            "Tried to send message %s to target %s",
                            finalMessage, target));
                }
            }
        }

        MessageHandler handler = getNextMessageHandler();
        if (handler != null) {
            handler.handleMessage(conn, target, nick, message);
        }
    }

    protected List<String> fetchTitles(List<String> links) {
        List<String> titles = new ArrayList<String>();

        for (String link : links) {
            try {
                String page = fetchPage(link);
                String title = extractTitle(page);
                String decodedTitle = StringEscapeUtils.unescapeHtml(title);
                titles.add(decodedTitle);
            } catch (Exception e) {
                log.error("Error while fetching titles", e);
            }
        }

        return titles;
    }

    protected String extractTitle(String page) {
        if (page == null) {
            throw new NullPointerException(
                    "Cannot extract title from null page");
        }

        Pattern pattern = Pattern.compile("(?<=<title>).+?(?=</title>)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(page);
        String ret = null;
        if (matcher.find()) {
            ret = matcher.group();
        }
        return ret;
    }

    protected boolean isHandlingRequired(IRCConnection conn, String target,
            String nick, String message) {
        Pattern url = Pattern.compile("http://|www\\.\\w+\\.\\w+(?= |$)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = url.matcher(message);
        boolean containsLink = matcher.find();

        boolean myMessage = conn.getNick().equals(nick);

        return containsLink && !myMessage;
    }

    protected String buildTitleMessage(List<String> titles) {
        // strip empty titles
        List<String> nonEmptyTitles = new ArrayList<String>();
        for (String title : titles) {
            if (title != null && !title.isEmpty()) {
                // strip double quotes
                title = title.replaceAll("\"", "'");
                title = title.replaceAll("\\s+", " ");
                title = title.trim();
                nonEmptyTitles.add("\"" + title + "\"");
            }
        }

        return StringUtils.join(nonEmptyTitles, ", ");
    }

    protected List<String> extractLinks(String message) {
        List<String> links = new ArrayList<String>();
        Pattern url = Pattern.compile(
                "(?:http://.+?(?= |$))|(?:www\\.\\w+\\.\\w+(?= |$))",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = url.matcher(message);
        while (matcher.find()) {
            String rawLink = matcher.group();
            if (!rawLink.startsWith("http://")) {
                rawLink = "http://" + rawLink;
            }
            links.add(rawLink);
        }
        return links;
    }

    protected String fetchPage(String link) {
        try {
            URI.create(link);
            return fetchPageForCorrectLink(link);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: don't download the full content of the link
    protected String fetchPageForCorrectLink(String link) {

        StringBuilder sb = new StringBuilder();

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget;
        try {
            httpget = new HttpGet(link);
        } catch (Exception e) {
            throw new RuntimeException(String.format(
                    "Error while building uri from %s", link), e);
        }
        HttpResponse response;
        HttpEntity entity = null;
        try {
            response = httpclient.execute(httpget);
            entity = response.getEntity();
        } catch (Exception e) {
            throw new RuntimeException("Error while executing http get", e);
        }

        if (entity != null) {
            InputStream instream = null;
            try {
                if (!entity.getContentType().getValue().contains("text")) {
                    return null;
                }
                instream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(instream));

                String line = null;
                while ((line = reader.readLine()) != null) {
                    // stop if otherwise limit would be surpassed
                    if (sb.length() + line.length() >= FETCH_LIMIT_CHARS) {
                        break;
                    }
                    sb.append(line);
                    // stop if you can
                    if (line.matches("(?i).*</title>.*")) {
                        break;
                    }
                    sb.append("\n");
                }
            } catch (Exception e) {
                throw new RuntimeException(String.format(
                        "Error while fetching %s", link), e);
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        }

        return sb.toString();
    }
}
