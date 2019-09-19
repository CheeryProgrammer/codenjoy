package com.codenjoy.dojo.treasurehunt.model.items;

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


import com.codenjoy.dojo.treasurehunt.model.Elements;
import com.codenjoy.dojo.treasurehunt.model.Field;
import com.codenjoy.dojo.treasurehunt.model.Player;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.algs.DeikstraFindWay;

import java.util.ArrayList;
import java.util.List;

/**
 * Артефакт: Бомба на поле
 */
public class Enemy extends PointImpl implements State<Elements, Player>, Tickable {

    private final Field field;
    private final DeikstraFindWay way;
    private Direction direction;

    public Enemy(int x, int y, Field field) {
        super(x, y);
        this.field = field;
        this.way = new DeikstraFindWay();
    }

    public Enemy(Point point, Field field) {
        super(point);
        this.field = field;
        direction = Direction.random();
        this.way = new DeikstraFindWay();
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
        List<Direction> directions = getDirections();
        if(directions != null && directions.size() > 0) {
            this.direction = directions.get(0);
            goTo = this.direction.change(this.copy());
            move(goTo);
        }
    }

    public List<Direction> getDirections() {
        int size = field.size();

        Point from = this;
        List<Point> to = new ArrayList<>();
        to.add(field.getHeroPosition());
        DeikstraFindWay.Possible map = possible(field);
        return way.getShortestWay(size, from, to, map);
    }

    private DeikstraFindWay.Possible possible(Field field) {
        return new DeikstraFindWay.Possible() {
            @Override
            public boolean possible(Point from, final Direction where) {
                if (field.isBarrier(from)) return false;

                Point newPt = where.change(from);
                int nx = newPt.getX();
                int ny = newPt.getY();

                if(nx < 0 || nx >= field.size() || ny < 0 || ny >= field.size())
                    return false;

                if (field.isBarrier(newPt)) return false;

                return true;
            }

            @Override
            public boolean possible(Point atWay) {
                return true;
            }
        };
    }
}
