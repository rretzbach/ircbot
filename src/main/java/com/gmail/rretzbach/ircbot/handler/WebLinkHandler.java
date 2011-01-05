package com.gmail.rretzbach.ircbot.handler;

import java.io.BufferedReader;
import java.io.IOException;
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

    @Override
    public void handleMessage(IRCConnection conn, String target, String user,
            String message) {
        if (isHandlingRequired(message)) {
            List<String> links = extractLinks(message);
            List<String> titles = fetchTitles(links);
            if (!titles.isEmpty()) {
                String finalMessage = buildTitleMessage(titles);
                try {
                    conn.doPrivmsg(target, finalMessage);
                } catch (Exception e) {
                    log.error("error while sending message", e);
                    log.debug(String.format(
                            "tried to send message %s to target %s",
                            finalMessage, target));
                }
            }
        } else {
            MessageHandler handler = getNextMessageHandler();
            if (handler != null) {
                handler.handleMessage(conn, target, user, message);
            }
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
                continue;
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

    protected boolean isHandlingRequired(String message) {
        Pattern url = Pattern.compile("http://|www\\.\\w+\\.\\w+(?= |$)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = url.matcher(message);

        return matcher.find();
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

    protected String fetchPageForCorrectLink(String link) {

        StringBuilder sb = new StringBuilder();

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget;
        try {
            httpget = new HttpGet(link);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        HttpResponse response;
        HttpEntity entity = null;
        try {
            response = httpclient.execute(httpget);
            entity = response.getEntity();
        } catch (Exception e) {
            throw new RuntimeException(e);
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
                    sb.append(line);
                    sb.append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (instream != null) {
                    try {
                        instream.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        httpclient.getConnectionManager().shutdown();

        return sb.toString();
    }
}
