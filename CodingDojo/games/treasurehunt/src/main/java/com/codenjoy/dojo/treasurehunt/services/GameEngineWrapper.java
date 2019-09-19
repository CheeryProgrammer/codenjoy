package com.codenjoy.dojo.treasurehunt.services;

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

import com.codenjoy.dojo.treasurehunt.model.*;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.settings.SettingsImpl;

public class GameEngineWrapper {
    private final TreasureHuntGameEngine game;
    private final LevelImpl level;
    private final Dice dice;
    private final Player player;


    public GameEngineWrapper(){
        SettingsImpl s = new SettingsImpl();
        s.addEditBox("Chests count").type(Integer.class).def(2);
        s.addEditBox("Bags count").type(Integer.class).def(3);
        this.level = new LevelImpl(getMap(), s);
        this.dice = new RandomDice();
        this.game = new TreasureHuntGameEngine(level, dice);
        this.player = new Player(new EventListener() {
            @Override
            public void event(Object event) {
                // ignore
            }
        });
    }

    private String getMap() {
        return "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼" +
                "☼  R  B        ☼" +
                "☼  RS          ☼" +
                "☼  R           ☼" +
                "☼              ☼" +
                "☼  R   $       ☼" +
                "☼              ☼" +
                "☼       R      ☼" +
                "☼ C    R       ☼" +
                "☼              ☼" +
                "☼              ☼" +
                "☼              ☼" +
                "☼RRRR          ☼" +
                "☼     W        ☼" +
                "☼              ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼";
    }

    public void initNewGame(){
        this.game.newGame(this.player);
    }

    public void go(String direction){
        Hero hero = this.game.getHero();
        switch(direction){
            case "up": hero.up(); break;
            case "right": hero.right(); break;
            case "down": hero.down(); break;
            case "left": hero.left(); break;
            default: // do nothing
        }
        this.game.tick();
    }

    public String getBoard(){
        Iterable<? extends Point> elements = this.game.reader().elements();
        StringBuilder sb = new StringBuilder();
        for (Point el: elements) {
            sb.append(((State<Elements, Player>)el).state(null));
        }
        return sb.toString();
    }

    public boolean isAlive(){
        return this.game.getHero().isAlive();
    }

    public int getScore(){
        return this.player.getCurrentScore();
    }
}
