package com.codenjoy.dojo.a2048.services;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
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

import com.codenjoy.dojo.a2048.model.*;
import com.codenjoy.dojo.a2048.model.Number;
import com.codenjoy.dojo.services.RandomDice;
import com.codenjoy.dojo.services.settings.Settings;

public class GameEngine {
    private A2048 game;
    private Hero hero;

    public GameEngine() {
    }

    public void init(){
        init(1);
    }

    public void init(int newNumbersCount) {
        Level level = new LevelImpl();
        Settings settings = level.getSettings();
        //settings.getParameter("Mode").type(Integer.class).update(1);
        settings.getParameter("New numbers").type(Integer.class).update(newNumbersCount);
        game = new A2048(level, new RandomDice());
    }

    public void newGame() {
        Player player = new Player(null);
        game.newGame(player);
        hero = player.getHero();
    }

    public void go(String direction) {
        switch (direction) {
            case "up":
                hero.up();
                break;
            case "down":
                hero.down();
                break;
            case "left":
                hero.left();
                break;
            case "right":
                hero.right();
                break;
            case "act":
                hero.clear();
                break;
        }
        game.tick();
    }

    public int getScore(){
        return game.getNumbers().getSum();
    }

    public boolean isGameOver(){
        return game.isGameOver();
    }

    public int[][] getData() {
        if(game.isGameOver())
            return null;

        int size = game.size();
        int[][] result = new int[size][size];

        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y++) {
                result[size - x - 1][y] = game.getNumbers().get(y, x).get();
            }
        return result;
    }
}
