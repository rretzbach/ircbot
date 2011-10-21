package com.gmail.rretzbach.ircbot.handler;

import java.util.ArrayList;

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
    public void shouldPickEquallyDistributedRandomTapirFactIndex()
            throws Exception {
        int n = 10;
        int factor = 10;
        for (int i = 0; i < n; i++) {
            handler.addTapirFact(String.valueOf(i));
        }
        // pick n random numbers
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < n * factor; i++) {
            int randomNumber = handler.pickRandomNumber();
            list.add(randomNumber);
        }
        // check if all 1 - n random numbers are retrieved
        int sum = 0;
        for (Integer integer : list) {
            sum += integer + 1;
        }
        int computedSum = factor * (n * n + n) / 2;
        Assert.assertEquals(computedSum, sum);
    }

    @Test
    public void shouldBuildMessageWithFactIndex() throws Exception {

    }

    @Test
    public void shouldReadFactsFile() throws Exception {
        // handler.loadFactsFromFile("tapirfacts.txt");
    }
}
