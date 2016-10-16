package com.merce.oscar.pressthebutton;


import java.util.Random;

/**
 * Created by Oscar Merce.
 */
public class Utils {

    /**
     * Generate a random number between a and b
     * @param a
     * @param b
     * @return random number between a and b
     */
    public static int getRandomNumber(int a, int b) {
        Random r = new Random();
        int randomNumber = r.nextInt(b - a) + a;
        return randomNumber;
    }

    public static float getGoodTextSize(String text) {
        switch (text.length()) {
            case 1: return 200f;
            case 2: return 150f;
            case 3: return 100f;
            case 4: return 75f;
            case 5: return 60f;
            case 6: return 50f;
            default: return 0f;
        }
    }
}
