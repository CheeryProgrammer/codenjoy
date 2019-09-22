package com.codenjoy.dojo.treasurehunt.model;

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


import com.codenjoy.dojo.treasurehunt.services.Events;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;

/**
 * Класс игрока. Тут кроме героя может подсчитываться очки.
 * Тут же ивенты передабтся лиснеру фреймворка.
 */
public class Player extends GamePlayer<Hero, Field> {

    Hero hero;
    private int currentScore;

    public Player(EventListener listener) {
        super(listener);
        currentScore = 0;
    }

    public Hero getHero() {
        return hero;
    }

    public int getCurrentScore(){
        return currentScore;
    }

    @Override
    public void newHero(Field field) {
        currentScore = 0;
        hero = new Hero(field.getFreeRandom());
        hero.init(field);
    }

    @Override
    public boolean isAlive() {
        return hero != null && hero.isAlive();
    }

    @Override
    public void event(Object event) {
        Events ev = ((Events)event).withCurrentScore(currentScore);
        if(event.equals(Events.DEAD)) {
            currentScore = 0;
        } else {
            currentScore += ev.getAddition();
        }
        super.event(ev.withCurrentScore(currentScore));
    }

    public void clearScore(){
        currentScore = 0;
    }
}
