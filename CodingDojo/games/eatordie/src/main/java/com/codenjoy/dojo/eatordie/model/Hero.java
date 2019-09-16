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


import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.State;
import com.codenjoy.dojo.services.multiplayer.PlayerHero;

/**
 * Это реализация героя. Обрати внимание, что он имплементит {@see Joystick}, а значит может быть управляем фреймворком
 * Так же он имплементит {@see Tickable}, что значит - есть возможность его оповещать о каждом тике игры.
 * Ну и конечно же он имплементит {@see State}, а значит может быть отрисован на поле.
 * Часть этих интерфейсов объявлены в {@see PlayerHero}, а часть явно тут.
 */
public class Hero extends PlayerHero<Field> implements State<Elements, Player> {

    private boolean alive;
    private Direction direction;
    private Direction wentInDirection;

    public Hero(Point xy) {
        super(xy);
        direction = null;
        alive = true;
    }

    @Override
    public void init(Field field) {
        this.field = field;
    }

    @Override
    public void down() {
        if (!alive) return;

        direction = Direction.DOWN;
    }

    @Override
    public void up() {
        if (!alive) return;

        direction = Direction.UP;
    }

    @Override
    public void left() {
        if (!alive) return;

        direction = Direction.LEFT;
    }

    @Override
    public void right() {
        if (!alive) return;

        direction = Direction.RIGHT;
    }

    @Override
    public void act(int... p) {
        if (!alive) return;

        alive = false;
    }

    @Override
    public void tick() {
        if (!alive) return;

        if (field.isEnemy(this)) {
            alive = false;
            return;
        }

        if (direction != null) {
            Point to = direction.change(this.copy());

            if (field.isEnemy(to)) {
                alive = false;
            }

            if (!field.isBarrier(to)) {
                move(to);
            }
        }
        wentInDirection = direction;
        direction = null;
    }

    public boolean isAlive() {
        return alive;
    }

    @Override
    public Elements state(Player player, Object... alsoAtPoint) {
        if (!isAlive()) {
            return Elements.DEAD_HERO;
        }

        if(wentInDirection == null)
            return Elements.HERO_DOWN;

        switch (wentInDirection){
            case UP: return Elements.HERO_UP;
            case RIGHT: return Elements.HERO_RIGHT;
            case DOWN: return Elements.HERO_DOWN;
            default: return Elements.HERO_LEFT;
        }
        /* if (this == player.getHero()) {
            return Elements.HERO_DOWN;
        } else {
            return Elements.OTHER_HERO;
        }*/
    }
}
