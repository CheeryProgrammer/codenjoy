package com.codenjoy.dojo.treasurehunt.client.ai;

import com.codenjoy.dojo.client.AbstractBoard;
import com.codenjoy.dojo.client.ClientBoard;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.treasurehunt.model.Elements;
import org.json.JSONObject;

public class AiBoard extends AbstractBoard<Elements> {

    private boolean isAlive;
    private int currentScore;

    @Override
    public Elements valueOf(char ch) {
        return Elements.valueOf(ch);
    }

    public Point getMe() {
        return get(Elements.DEAD_HERO,
                Elements.HERO_UP, Elements.HERO_RIGHT, Elements.HERO_DOWN, Elements.HERO_LEFT).get(0);
    }

    public boolean isBarrierAt(int x, int y) {
        return isAt(x, y, Elements.WALL, Elements.ROCK);
    }

    public boolean isEnemyAt(int x, int y) {
        return isAt(x, y, Elements.ENEMY_UP, Elements.ENEMY_RIGHT, Elements.ENEMY_DOWN, Elements.ENEMY_LEFT);
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int getCurrentScore(){
        return currentScore;
    }

    @Override
    public ClientBoard forString(String boardString) {
        source = new JSONObject(boardString);
        isAlive = source.getBoolean("isAlive");
        currentScore = source.getInt("currentScore");
        String field = source.getString("field");

        return forString(new String[]{field});
    }
}
