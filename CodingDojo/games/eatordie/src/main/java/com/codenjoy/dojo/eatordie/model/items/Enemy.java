package com.codenjoy.dojo.eatordie.model.items;

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


import com.codenjoy.dojo.eatordie.model.Elements;
import com.codenjoy.dojo.eatordie.model.Field;
import com.codenjoy.dojo.eatordie.model.Player;
import com.codenjoy.dojo.eatordie.utils.Dijkstra;
import com.codenjoy.dojo.services.*;

import java.util.List;

/**
 * Артефакт: Бомба на поле
 */
public class Enemy extends PointImpl implements State<Elements, Player>, Tickable {

    private final Field field;
    private Direction direction;

    public Enemy(int x, int y, Field field) {
        super(x, y);
        this.field = field;
    }

    public Enemy(Point point, Field field) {
        super(point);
        this.field = field;
        direction = Direction.random();
    }

    @Override
    public Elements state(Player player, Object... alsoAtPoint) {
        switch(direction) {
            case UP:
                return Elements.ENEMY_UP;
            case DOWN:
                return Elements.ENEMY_DOWN;
            case RIGHT:
                return Elements.ENEMY_RIGHT;
            case LEFT:
                return Elements.ENEMY_LEFT;
            default:
                return Elements.ENEMY_DOWN;
        }
    }

    @Override
    public void tick() {
        Point goTo = null;
        do {
            this.direction = Direction.random();//CalculateNewDirection();
            goTo = this.direction.change(this.copy());
        } while (!field.isFree(goTo) && !goTo.itsMe(field.getHeroPosition()));

        move(goTo);
    }

    private Direction CalculateNewDirection() {
        List<Direction> way = new Dijkstra().findWay(this.copy(), field.getHeroPosition().copy(), field);
        System.out.println("Way length is: " + way.size());
        if(way.size() > 0)
            return way.get(way.size() - 1);
        return null;
    }
}
