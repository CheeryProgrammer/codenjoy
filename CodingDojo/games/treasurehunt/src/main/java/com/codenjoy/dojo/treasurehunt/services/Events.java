package com.codenjoy.dojo.treasurehunt.services;

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


/**
 * Ивенты, которые могут возникать в игре опиши тут. Что есть ивенты? ну убили твоего героя и ты хочешь ему очков начислить штрафных
 * или, быть может, наоборот - он поднял что-то ценное и ты хочешь ему дать бонус. Вот все все ивенты.
 */
public enum Events {
    GOT_BAG(3),
    GOT_CHEST(8),
    DEAD(0);

    private int currentScore;
    private int score;

    Events(int score){
        this.score = score;
    }

    public int getAddition(){
        int bonus = currentScore / 25;
        if(this==GOT_CHEST) {
            bonus *= 3;
        }
        return score + bonus;
    }

    public Events withCurrentScore(int currentScore){
        this.currentScore = currentScore;
        return this;
    }

    public int getCurrentScore() {
        return this.currentScore;
    }
}
