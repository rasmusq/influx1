package com.ralleq.influx.rendertools;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

public class NumberRenderer {

    private static ArrayList<Integer> tempNumberArray = new ArrayList<>();
    public static ArrayList<Integer> stringToNumberArray(String string) {
        tempNumberArray.clear();
        for(int i = 0; i < string.length(); i++) {
            try {
                tempNumberArray.add(Integer.parseInt(string.substring(i, i+1)));
            } catch(Exception e) {
                e.printStackTrace();
                tempNumberArray.add(0);
            }
        }
        return tempNumberArray;
    }
    public static void renderNumbers(Canvas c, Paint p, ArrayList<Integer> numbers, float x, float y, float width, float whitespace) {
        for(int i = 0; i < numbers.size(); i++) {
            renderNumber(c, p, x + (width+whitespace)*i - (width+whitespace)*((numbers.size()-1)/2f), y, width, numbers.get(i));
        }
    }
    public static void renderBigNumber(Canvas c, Paint p, int number, float x, float y, float width, float whitespace) {
        renderNumbers(c, p, stringToNumberArray(Integer.toString(number)), x, y, width, whitespace);
    }

    public static void renderNumber(Canvas c, Paint p, float x, float y, float width, int number) {
        switch(number) {
            case 1:
                renderOne(c, p, x, y, width); break;
            case 2:
                renderTwo(c, p, x, y, width); break;
            case 3:
                renderThree(c, p, x, y, width); break;
            case 4:
                renderFour(c, p, x, y, width); break;
            case 5:
                renderFive(c, p, x, y, width); break;
            case 6:
                renderSix(c, p, x, y, width); break;
            case 7:
                renderSeven(c, p, x, y, width); break;
            case 8:
                renderEight(c, p, x, y, width); break;
            case 9:
                renderNine(c, p, x, y, width); break;
            case 0:
                renderZero(c, p, x, y, width); break;
        }
    }
    private static void renderOne(Canvas c, Paint p, float x, float y, float width) {
        c.drawLine(x, y - width*2, x, y, p);
        c.drawArc(x-width, y-width*2.5f, x, y-(width*1.5f), 0, 90, false, p);
    }
    private static void renderTwo(Canvas c, Paint p, float x, float y, float width) {
        c.drawLine(x-width/2, y, x+width/2, y, p);
        c.drawArc(x-width/2, y-width*2, x+width/2, y-width, -180, 216.86989764584402f, false, p);
        float arcOffsetX = (float)(width/2*Math.cos(Math.toRadians(36.86989764584402)));
        float arcOffsetY = (float)(width/2*Math.sin(Math.toRadians(36.86989764584402)));
        c.drawLine(x-width/2, y, x+arcOffsetX, y-width*1.5f+arcOffsetY, p);
    }
    private static void renderThree(Canvas c, Paint p, float x, float y, float width) {
        c.drawArc(x-width/2, y-width*2, x+width/2, y-width, -180, 270, false, p);
        c.drawArc(x-width/2, y-width, x+width/2, y, -90, 270, false, p);
    }
    private static void renderFour(Canvas c, Paint p, float x, float y, float width) {
        c.drawLine(x+width/2, y-width*2, x+width/2, y, p);
        c.drawLine(x-width/2, y-width, x+width/2, y-width*2, p);
        c.drawLine(x-width/2, y-width, x+width/2, y-width, p);
    }
    private static void renderFive(Canvas c, Paint p, float x, float y, float width) {
        float arcOffset = (float)(width/2*Math.cos(Math.toRadians(45)))/2;
        c.drawArc(x-width/2 - arcOffset, y-width - arcOffset, x+width/2, y, -135, 270, false, p);
        c.drawLine(x-width/2, y-width, x-width/2, y-width*2, p);
        c.drawLine(x-width/2, y-width*2, x+width/2, y-width*2, p);
    }
    private static void renderSix(Canvas c, Paint p, float x, float y, float width) {
        float arcOffset = (float)(((width/2+1) - p.getStrokeWidth()/2)*Math.cos(Math.toRadians(45)))/2;
        c.drawArc(x-width/2-arcOffset/2, y-width-arcOffset, x+width/2+arcOffset/2, y, 0, 360, false, p);
        c.drawLine(x-width/2 + arcOffset/2, y-width, x+width/2, y-width*2, p);
    }
    private static void renderSeven(Canvas c, Paint p, float x, float y, float width) {
        c.drawLine(x-width/2, y, x+width/2, y-width*2, p);
        c.drawLine(x-width/2, y-width*2, x+width/2, y-width*2, p);
    }
    private static void renderEight(Canvas c, Paint p, float x, float y, float width) {
        c.drawCircle(x, y-width*1.5f, width/2, p);
        c.drawCircle(x, y-width/2, width/2, p);
    }
    private static void renderNine(Canvas c, Paint p, float x, float y, float width) {
        float arcOffset = (float)(((width/2+1) - p.getStrokeWidth()/2)*Math.cos(Math.toRadians(45)))/2;
        c.drawArc(x-width/2-arcOffset/2, y-width*2, x+width/2+arcOffset/2, y-width+arcOffset, 0, 360, false, p);
        c.drawLine(x+width/2 - arcOffset/2, y-width, x-width/2, y, p);
    }
    private static void renderZero(Canvas c, Paint p, float x, float y, float width) {
        c.drawArc(x-width/2, y-width*2, x+width/2, y-width, 180, 180, false, p);
        c.drawArc(x-width/2, y-width, x+width/2, y, 0, 180, false, p);
        c.drawLine(x-width/2, y-width/2, x-width/2, y-width*1.5f, p);
        c.drawLine(x+width/2, y-width/2, x+width/2, y-width*1.5f, p);
    }

}
