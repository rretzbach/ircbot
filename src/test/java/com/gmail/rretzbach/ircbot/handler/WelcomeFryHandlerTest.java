package com.gmail.rretzbach.ircbot.handler;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.schwering.irc.lib.IRCConnection;
import org.schwering.irc.lib.IRCUser;

public class WelcomeFryHandlerTest {
    private WelcomeFryHandler handler;
    private IRCUser mockFry;

    @Before
    public void setup() {
        handler = new WelcomeFryHandler();
        mockFry = new IRCUser("fry", "username", "fry.users.iZ-smart.net");
    }

    @Test
    @Ignore(value = "Don't know how to handle thread sleep with easymock")
    public void shouldGreetFry() throws Exception {
        IRCConnection mock = createNiceMock(IRCConnection.class);
        mock.doPrivmsg((String) org.easymock.EasyMock.anyObject(),
                (String) org.easymock.EasyMock.startsWith("fryy"));
        replay(mock);
        handler.onJoin(mock, "#vegan", mockFry);

        verify(mock);
    }

    @Test
    @Ignore(value = "Long running test")
    public void shouldGreetFryWithDelay() throws Exception {
        final Date[] measures = new Date[2];

        IRCConnection mock = new IRCConnection("", 0, 1, "pass", "nick",
                "username", "realname") {
            @Override
            public void doPrivmsg(String target, String msg) {
                measures[1] = new Date();
                long duration = (measures[1].getTime() - measures[0].getTime()) / 1000;
                assertTrue("Welcome delay was not between 10 and 30 seconds",
                        duration >= 10 && duration <= 30);
                assertTrue("Welcome name didn't match fryy+, but was " + msg,
                        msg.matches("fryy+"));

                synchronized (this) {
                    this.notifyAll();
                }
            }
        };

        measures[0] = new Date();
        handler.onJoin(mock, "#vegan", mockFry);
        synchronized (mock) {
            mock.wait();
        }
    }

    @Test
    public void shouldBeFry() throws Exception {
        assertTrue(handler.isFry(mockFry));
    }

    @Test
    public void shouldShoutFry() throws Exception {
        String greeting = handler.generateGreeting();
        assertTrue(greeting.matches("fryy+"));
    }
}
