package com.gmail.rretzbach.ircbot.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomUtil {

    static Random rng = new Random();

    public static List<Integer> createShuffledIndexes(int size) {
        Integer[] ary = new Integer[size];
        for (int i = 0; i < size; ++i) {
            ary[i] = i;
        }
        for (int i = size; i > 1; --i) {
            int j = rng.nextInt(i);
            int tmp = ary[j];
            ary[j] = ary[i - 1];
            ary[i - 1] = tmp;
        }
        return Arrays.asList(ary);
    }
}
