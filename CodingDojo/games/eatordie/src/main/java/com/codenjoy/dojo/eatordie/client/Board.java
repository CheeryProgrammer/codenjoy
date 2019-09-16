package com.codenjoy.dojo.eatordie.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.client.AbstractBoard;
import com.codenjoy.dojo.client.ClientBoard;
import com.codenjoy.dojo.eatordie.model.Elements;
import com.codenjoy.dojo.services.Point;
import org.json.JSONObject;

/**
 * Класс, обрабатывающий строковое представление доски.
 * Содержит ряд унаследованных методов {@see AbstractBoard},
 * но ты можешь добавить сюда любые свои методы на их основе.
 */
public class Board extends AbstractBoard<Elements> {

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
