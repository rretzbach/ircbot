package com.gmail.rretzbach.ircbot.handler;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class TapirWordHandlerTest {
    private TapirWordHandler handler;

    @Before
    public void setup() {
        handler = new TapirWordHandler();
    }

    @Test
    public void shouldRequireHandlingForTapirWord() throws Exception {
        boolean handlingRequired = handler.isHandlingRequired("mynick", null,
                "othernick", "what exactly is a tapir?");
        Assert.assertTrue(handlingRequired);
    }

    @Test
    public void shouldRequireHandlingForTapirWordCaseInsensitive()
            throws Exception {
        boolean handlingRequired = handler.isHandlingRequired("mynick", null,
                "othernick", "what exactly is a TAPIR?");
        Assert.assertTrue(handlingRequired);
    }

    @Test
    public void shouldNotRequireHandlingOwnMessages() throws Exception {
        boolean handlingRequired = handler.isHandlingRequired("mynick", null,
                "mynick", "");
        Assert.assertFalse(handlingRequired);
    }

    @Test
    public void shouldPickRandomTapirFact() throws Exception {

    }

    @Test
    public void shouldBuildMessageWithFactNumber() throws Exception {

    }

    @Test
    public void shouldReadFactsFile() throws Exception {
        // handler.loadFactsFromFile("tapirfacts.txt");
    }
}
