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


import com.codenjoy.dojo.eatordie.model.items.*;
import com.codenjoy.dojo.eatordie.services.Events;
import com.codenjoy.dojo.services.BoardUtils;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.Tickable;
import com.codenjoy.dojo.services.printer.BoardReader;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * О! Это самое сердце игры - борда, на которой все происходит.
 * Если какой-то из жителей борды вдруг захочет узнать что-то у нее, то лучше ему дать интефейс {@see Field}
 * Борда реализует интерфейс {@see Tickable} чтобы быть уведомленной о каждом тике игры. Обрати внимание на {Sample#tick()}
 */
public class EatOrDieGameEngine implements Field {

    private List<Wall> walls;
    private List<Bag> bags;
    private List<Chest> chests;
    private List<Enemy> enemies;
    private List<Rock> rocks;

    private List<Player> players;

    private final int size;
    private Dice dice;

    public EatOrDieGameEngine(Level level, Dice dice) {
        this.dice = dice;
        walls = level.getWalls();
        bags = level.getBags();
        chests = level.getChests();
        rocks = level.getRocks();
        size = level.getSize();
        players = new LinkedList<>();
        enemies = new LinkedList<>();
    }

    /**
     * @see Tickable#tick()
     */
    @Override
    public void tick() {
        for (Player player : players) {
            Hero hero = player.getHero();

            hero.tick();

            if(enemies.isEmpty()){
                enemies.add(new Enemy(getFreeRandom(), this));
            }
            else{
                enemies.get(0).tick();
            }

            if (bags.contains(hero)) {
                bags.remove(hero);
                player.event(Events.WIN);

                Point pos = getFreeRandom();
                bags.add(new Bag(pos));
            }

            if (chests.contains(hero)) {
                chests.remove(hero);
                player.event(Events.WIN);

                Point pos = getFreeRandom();
                chests.add(new Chest(pos));
            }
        }

        for (Player player : players) {
            Hero hero = player.getHero();

            if (!hero.isAlive()) {
                player.event(Events.LOOSE);
            }
        }
    }

    public int size() {
        return size;
    }

    @Override
    public Point getHeroPosition() {
        return getHeroes().get(0);
    }

    @Override
    public boolean isBarrier(Point pt) {
        int x = pt.getX();
        int y = pt.getY();

        return x > size - 1
                || x < 0
                || y < 0
                || y > size - 1
                || walls.contains(pt)
                || getHeroes().contains(pt);
    }

    @Override
    public Point getFreeRandom() {
        return BoardUtils.getFreeRandom(size, dice, pt -> isFree(pt));
    }

    @Override
    public boolean isFree(Point pt) {
        return !(bags.contains(pt)
                || enemies.contains(pt)
                || walls.contains(pt)
                || rocks.contains(pt)
                || getHeroes().contains(pt));
    }

    @Override
    public boolean isEnemy(Point pt) {
        return enemies.contains(pt);
    }

    public List<Bag> getBags() {
        return bags;
    }

    public List<Chest> getChests() {
        return chests;
    }

    public List<Hero> getHeroes() {
        return players.stream()
                .map(Player::getHero)
                .collect(toList());
    }

    @Override
    public void newGame(Player player) {
        if (!players.contains(player)) {
            players.add(player);
        }
        player.newHero(this);
    }

    @Override
    public void remove(Player player) {
        players.remove(player);
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Rock> getRocks() {
        return rocks;
    }

    @Override
    public BoardReader reader() {
        return new BoardReader() {
            private int size = EatOrDieGameEngine.this.size;

            @Override
            public int size() {
                return size;
            }

            @Override
            public Iterable<? extends Point> elements() {
                return new LinkedList<Point>(){{
                    addAll(EatOrDieGameEngine.this.getWalls());
                    addAll(EatOrDieGameEngine.this.getHeroes());
                    addAll(EatOrDieGameEngine.this.getBags());
                    addAll(EatOrDieGameEngine.this.getChests());
                    addAll(EatOrDieGameEngine.this.getEnemies());
                    addAll(EatOrDieGameEngine.this.getRocks());
                }};
            }
        };
    }
}
