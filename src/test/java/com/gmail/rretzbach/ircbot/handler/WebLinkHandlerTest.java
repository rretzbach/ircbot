package com.gmail.rretzbach.ircbot.handler;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.schwering.irc.lib.IRCConnection;

public class WebLinkHandlerTest {

    private WebLinkHandler handler;

    @Before
    public void setup() {
        handler = new WebLinkHandler();
    }

    @Test
    public void shouldIgnoreMessagesWithoutLink() {
        IRCConnection mock = createMock(IRCConnection.class);
        replay(mock);
        handler.handleMessage(mock, "#channel", null,
                "Message without link in it.");
        verify(mock);
    }

    @Test
    public void shouldNotSendMessageWithoutTitles() {
        IRCConnection mockConnection = createMock(IRCConnection.class);
        WebLinkHandler mock = createNiceMock(WebLinkHandler.class);
        EasyMock.expect(mock.fetchTitles(null)).andStubReturn(
                new ArrayList<String>());
        replay(mock);
        replay(mockConnection);
        mock.handleMessage(mockConnection, "#channel", null,
                "Message with http://link.de in it.");
        verify(mock);
        verify(mockConnection);
    }

    @Test
    public void shouldSendPrivMsg() {
        IRCConnection mock = createStrictMock(IRCConnection.class);
        mock.doPrivmsg("#channel", "\"Google\"");
        replay(mock);
        handler.handleMessage(mock, "#channel", null,
                "\"Test with http://www.google.de \"");
        verify(mock);
    }

    @Test
    public void shouldBuildTitleMessage() {
        List<String> titles = new ArrayList<String>();
        titles.add("Google Search");
        titles.add("Yahoo Results");
        String message = handler.buildTitleMessage(titles);
        Assert.assertEquals("\"Google Search\", \"Yahoo Results\"", message);
    }

    @Test
    public void shouldFetchHTTPTitle() {
        List<String> links = new ArrayList<String>();
        links.add("http://www.google.de");
        List<String> titles = handler.fetchTitles(links);
        Assert.assertEquals(1, titles.size());
        Assert.assertEquals("Google", titles.get(0));
    }

    @Test
    public void shouldFetchTitle() {
        String page = "bla <title>Google</title> boof";
        String title = handler.extractTitle(page);
        Assert.assertEquals("Google", title);
    }

    @Test
    public void shouldFetchTitleWithLineBreaks() {
        String page = "bla <title>   \n Google\n    </title> boof";
        String title = handler.extractTitle(page);
        Assert.assertEquals("   \n Google\n    ", title);
    }

    @Test
    public void shouldExtractOneLink() {
        String message = "Hi, please check http://www.google.de";
        List<String> links = handler.extractLinks(message);
        Assert.assertEquals(1, links.size());
        Assert.assertEquals("http://www.google.de", links.get(0));
    }

    @Test
    public void shouldExtractTwoLinks() {
        String message = "Hi, please check http://www.google.de but you also might like www.yahoo.de";
        List<String> links = handler.extractLinks(message);
        Assert.assertEquals(2, links.size());
        Assert.assertEquals("http://www.google.de", links.get(0));
        Assert.assertEquals("http://www.yahoo.de", links.get(1));
    }

    @Test
    public void shouldRequireHandlingForHTTPLink() {
        String message = "Check out http://www.google.de";
        boolean containsLink = handler.isHandlingRequired(message);
        Assert.assertTrue(containsLink);
    }

    @Test
    public void shouldRequireHandlingForWWWLink() {
        String message = "Check out www.google.de";
        boolean containsLink = handler.isHandlingRequired(message);
        Assert.assertTrue(containsLink);
    }

    @Test
    public void shouldNotFetchPageWithIncorrectURL() {
        try {
            handler.fetchPage("http://");
            Assert.fail("Should throw error");
        } catch (Exception e) {}
    }
}
