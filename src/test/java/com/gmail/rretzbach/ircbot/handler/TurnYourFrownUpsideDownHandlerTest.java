package com.gmail.rretzbach.ircbot.handler;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rretzbach
 * Date: 27.05.12
 * Time: 16:18
 * To change this template use File | Settings | File Templates.
 */
public class TurnYourFrownUpsideDownHandlerTest {
    private TurnYourFrownUpsideDownHandler handler;

    @Before
    public void setup() {
        handler = new TurnYourFrownUpsideDownHandler();
    }

    @Test
    public void shouldFetchRedditAww() {
        //List<String> json = handler.getRedditImgurURLs("aww");
        //assertNotNull(json);
    }

    @Test
    public void shouldFetchRandomAwwURL() {
        //String url = handler.fetchRandomAwwURL();
        //assertNotNull(url);
    }

    @Test
    public void shouldDetectFrown() {
        {
            boolean result = handler.isHandlingRequired(null, null, null, ":(");
            assertTrue(result);
        }
        {
            boolean result = handler.isHandlingRequired(null, null, null, ":-(");
            assertTrue(result);
        }
        {
            boolean result = handler.isHandlingRequired(null, null, null, ":'(");
            assertTrue(result);
        }
        {
            boolean result = handler.isHandlingRequired(null, null, null, ":´(");
            assertTrue(result);
        }
        {
            boolean result = handler.isHandlingRequired(null, null, null, ":`(");
            assertTrue(result);
        }
        {
            boolean result = handler.isHandlingRequired(null, null, null, ";(");
            assertTrue(result);
        }

        {
            boolean result = handler.isHandlingRequired(null, null, null, "):");
            assertTrue(result);
        }
        {
            boolean result = handler.isHandlingRequired(null, null, null, ")-:");
            assertTrue(result);
        }
        {
            boolean result = handler.isHandlingRequired(null, null, null, ")':");
            assertTrue(result);
        }
        {
            boolean result = handler.isHandlingRequired(null, null, null, ")´:");
            assertTrue(result);
        }
        {
            boolean result = handler.isHandlingRequired(null, null, null, ")`:");
            assertTrue(result);
        }
        {
            boolean result = handler.isHandlingRequired(null, null, null, ");");
            assertTrue(result);
        }
        {
            boolean result = handler.isHandlingRequired(null, null, null, ":<");
            assertTrue(result);
        }
        {
            boolean result = handler.isHandlingRequired(null, null, null, "():-(");
            assertTrue(result);
        }
        // negative cases
        {
            boolean result = handler.isHandlingRequired(null, null, null, ">:)");
            assertFalse(result);
        }
        {
            boolean result = handler.isHandlingRequired(null, null, null, "(:<");
            assertFalse(result);
        }
        {
            boolean result = handler.isHandlingRequired(null, null, null, ">:-D");
            assertFalse(result);
        }
        {
            boolean result = handler.isHandlingRequired(null, null, null, "():-)");
            assertFalse(result);
        }
        {
            boolean result = handler.isHandlingRequired(null, null, null, "(-:()");
            assertFalse(result);
        }
    }


}
