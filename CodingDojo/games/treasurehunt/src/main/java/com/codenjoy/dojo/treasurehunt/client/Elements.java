package com.codenjoy.dojo.treasurehunt.client;

import com.codenjoy.dojo.services.printer.CharElements;

public enum Elements implements CharElements {
    NONE(' ', 0), // например это пустое место, куда можно перейти герою
    WALL('☼', -1), // а это стенка, через которую я хочу чтобы проходить нельзя было
    HERO_UP('W', 1), // а это мой герой вверх
    HERO_RIGHT('D', 1), // а это мой герой вправо
    HERO_DOWN('S', 1), // а это мой герой вниз
    HERO_LEFT('A', 1), // а это мой герой влево
    OTHER_HERO('☻', -2), // это герои других игроков
    DEAD_HERO('X', -3), // а это временное явление - трупик моего героя, которое пропадет в следующем такте
    CHEST('$', 5), // сундук золота
    BAG('B', 3), // мешок золота
    ENEMY_UP('^', -4), // враг вверх
    ENEMY_RIGHT('>', -4), // враг вправо
    ENEMY_DOWN('v', -4), // враг вниз
    ENEMY_LEFT('<', -4), // враг влево
    ROCK('R', -1); // камень - препятствие

    final char ch;
    final int value;

    Elements(char ch, int value) {
        this.ch = ch;
        this.value = value;
    }

    @Override
    public char ch() {
        return ch;
    }

    @Override
    public String toString() {
        return String.valueOf(ch);
    }

    public static Elements valueOf(int number) {
        return Elements.valueOf("_" + String.valueOf(number));
    }

    public static Elements valueOf(char ch) {
        for (Elements el : Elements.values()) {
            if (el.ch == ch) {
                return el;
            }
        }
        throw new IllegalArgumentException("No such element for " + ch);
    }

    public int number() {
        return value;
    }

}
