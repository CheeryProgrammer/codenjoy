package com.codenjoy.dojo.eatordie.client.ai;

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


import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.eatordie.client.Board;
import com.codenjoy.dojo.eatordie.model.Elements;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.algs.DeikstraFindWay;

import java.util.Arrays;
import java.util.List;

/**
 * Это алгоритм твоего бота. Он будет запускаться в игру с первым
 * зарегистрировавшимся игроком, чтобы ему не было скучно играть самому.
 * Реализуй его как хочешь, хоть на Random (только используй для этого
 * {@see Dice} что приходит через конструктор).
 * Для его запуска воспользуйся методом {@see ApofigSolver#main}
 */
public class AISolver implements Solver<Board> {

    private DeikstraFindWay way;
    private Dice dice;

    public AISolver(Dice dice) {
        this.dice = dice;
        this.way = new DeikstraFindWay();
    }

    public DeikstraFindWay.Possible possible(final Board board) {
        return new DeikstraFindWay.Possible() {
            @Override
            public boolean possible(Point from, Direction where) {
                int x = from.getX();
                int y = from.getY();
                if (board.isBarrierAt(x, y)) return false;
                if (board.isEnemyAt(x, y)) return false;

                Point newPt = where.change(from);
                int nx = newPt.getX();
                int ny = newPt.getY();

                if (board.isOutOfField(nx, ny)) return false;

                if (board.isBarrierAt(nx, ny)) return false;
                if (board.isEnemyAt(nx, ny)) return false;

                return true;
            }

            @Override
            public boolean possible(Point atWay) {
                return true;
            }
        };
    }

    @Override
    public String get(final Board board) {
        if (!board.isAlive()) return "";
        List<Direction> result = getDirections(board);
        if (result.isEmpty()) return "";
        return result.get(0).toString();
    }

    private String getBombIfNeeded(Board board) {
        Point me = board.getMe();
        if (me.getX() % 2 == 0 && me.getY() % 2 == 0) {
            return ", ACT";
        } else {
            return "";
        }
    }

    public List<Direction> getDirections(Board board) {
        int size = board.size();
        if (enemiesNear(board)) {
            return Arrays.asList(Direction.random(dice));
        }

        Point from = board.getMe();
        List<Point> to = board.get(Elements.BAG);
        DeikstraFindWay.Possible map = possible(board);
        return way.getShortestWay(size, from, to, map);
    }

    // TODO fix Deikstra find way
    private boolean enemiesNear(Board board) {
        Point me = board.getMe();
        Point atLeft = Direction.LEFT.change(me);
        Point atRight = Direction.RIGHT.change(me);
        Point atUp = Direction.UP.change(me);
        Point atDown = Direction.DOWN.change(me);

        return board.isAt(atLeft.getX(), atLeft.getY(), Elements.ENEMY_UP, Elements.ENEMY_RIGHT, Elements.ENEMY_DOWN, Elements.ENEMY_LEFT, Elements.WALL, Elements.OTHER_HERO) &&
                board.isAt(atRight.getX(), atRight.getY(), Elements.ENEMY_UP, Elements.ENEMY_RIGHT, Elements.ENEMY_DOWN, Elements.ENEMY_LEFT, Elements.WALL, Elements.OTHER_HERO) &&
                board.isAt(atUp.getX(), atUp.getY(), Elements.ENEMY_UP, Elements.ENEMY_RIGHT, Elements.ENEMY_DOWN, Elements.ENEMY_LEFT, Elements.WALL, Elements.OTHER_HERO) &&
                board.isAt(atDown.getX(), atDown.getY(), Elements.ENEMY_UP, Elements.ENEMY_RIGHT, Elements.ENEMY_DOWN, Elements.ENEMY_LEFT, Elements.WALL, Elements.OTHER_HERO);
    }
}
