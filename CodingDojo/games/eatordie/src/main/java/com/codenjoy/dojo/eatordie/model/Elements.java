package com.codenjoy.dojo.eatordie.model;

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


import com.codenjoy.dojo.services.printer.CharElements;

/**
 * Тут указана легенда всех возможных объектов на поле и их состояний.
 * Важно помнить, что для каждой енумной константы надо создать спрайт в папке \src\main\webapp\resources\sprite.
 */
public enum Elements implements CharElements {

    NONE(' '),       // например это пустое место, куда можно перейти герою
    WALL('☼'),       // а это стенка, через которую я хочу чтобы проходить нельзя было
    HERO_UP('W'),       // а это мой герой вверх
    HERO_RIGHT('D'),       // а это мой герой вправо
    HERO_DOWN('S'),       // а это мой герой вниз
    HERO_LEFT('A'),       // а это мой герой влево
    OTHER_HERO('☻'), // это герои других игроков
    DEAD_HERO('X'),  // а это временное явление - трупик моего героя, которое пропадет в следующем такте
    CHEST('$'),       // сундук золота
    BAG('B'),       // мешок золота
    ENEMY_UP('^'),       // враг вверх
    ENEMY_RIGHT('>'),       // враг вправо
    ENEMY_DOWN('v'),       // враг вниз
    ENEMY_LEFT('<'),       // враг влево
    ROCK('R');       // камень - препятствие

    final char ch;

    Elements(char ch) {
        this.ch = ch;
    }

    @Override
    public char ch() {
        return ch;
    }

    @Override
    public String toString() {
        return String.valueOf(ch);
    }

    public static Elements valueOf(char ch) {
        for (Elements el : Elements.values()) {
            if (el.ch == ch) {
                return el;
            }
        }
        throw new IllegalArgumentException("No such element for " + ch);
    }

}
