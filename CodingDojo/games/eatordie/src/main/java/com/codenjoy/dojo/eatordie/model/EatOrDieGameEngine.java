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
import com.codenjoy.dojo.services.*;
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

    private Player player;

    private final int size;
    private Dice dice;

    private Level level;

    private boolean skipEnemyStep;

    public EatOrDieGameEngine(Level level, Dice dice) {
        this.dice = dice;
        walls = level.getWalls();
        bags = level.getBags();
        chests = level.getChests();
        rocks = level.getRocks();
        size = level.getSize();
        enemies = new LinkedList<>();
        this.level = level;
    }

    /**
     * @see Tickable#tick()
     */
    @Override
    public void tick() {
        skipEnemyStep = !skipEnemyStep;
        if(player.getCurrentScore() > 30 || skipEnemyStep )
            enemies.forEach(Enemy::tick);

        Hero hero = player.getHero();

        hero.tick();

        if (bags.contains(hero)) {
            bags.remove(hero);
            player.event(Events.GOT_BAG);
        }

        int bagsCount = this.level.getBagsCount();
        while(bags.size() != bagsCount) {
            if(bags.size() < bagsCount)
                bags.add(new Bag(getFreeRandom()));
            else
                bags.remove(bags.size() - 1);
        }

        if (chests.contains(hero)) {
            chests.remove(hero);
            player.event(Events.GOT_CHEST);
        }

        int chestsCount = this.level.getChestsCount();
        while(chests.size() != chestsCount) {
            if(chests.size() < chestsCount)
                chests.add(new Chest(getFreeRandom()));
            else
                chests.remove(chests.size() - 1);
        }

        if (!hero.isAlive()) {
            player.event(Events.DEAD);
        }

        correctDifficulty(player.getCurrentScore());
    }

    private void correctDifficulty(int currentScore) {
        if(currentScore < 50){
            correctEnemiesCount(1);
            return;
        }
        if(currentScore < 100){
            correctEnemiesCount(2);
            return;
        }
        correctEnemiesCount(3);
    }

    private void correctEnemiesCount(int desiredCount){
        while(enemies.size() < desiredCount) {
            enemies.add(new Enemy(getFreeRandom(), this));
        }
        while(enemies.size() > desiredCount) {
            enemies.remove(enemies.size() - 1);
        }
    }

    public int size() {
        return size;
    }

    @Override
    public Point getHeroPosition() {
        return getHero();
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
                || rocks.contains(pt);
    }

    @Override
    public Point getFreeRandom() {
        return BoardUtils.getFreeRandom(size, dice, pt -> isFree(pt));
    }

    @Override
    public boolean isFree(Point pt) {
        return !(bags.contains(pt)
                || chests.contains(pt)
                || enemies.contains(pt)
                || walls.contains(pt)
                || rocks.contains(pt)
                || (getHeroPosition() != null && getHeroPosition().itsMe(pt)));
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

    public Hero getHero() {
        return player.getHero();
    }

    @Override
    public void newGame(Player player) {
        if(this.player == null){
            this.player = player;
        }
        this.player.newHero(this);
    }

    @Override
    public void remove(Player player) {
        this.player = null;
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
                Point[][] field = new Point[size][size];

                fillField(field, EatOrDieGameEngine.this.getWalls());
                fillField(field, EatOrDieGameEngine.this.getBags());
                fillField(field, EatOrDieGameEngine.this.getChests());
                fillField(field, EatOrDieGameEngine.this.getEnemies());
                fillField(field, EatOrDieGameEngine.this.getRocks());

                Hero hero = EatOrDieGameEngine.this.getHero();
                field[hero.getX()][hero.getY()] = hero;

                List<Point> allElements = new LinkedList<Point>();
                for (int y = size - 1; y >= 0; y--) {
                    for (int x = 0; x < size; x++){
                        if(field[x][y] == null) {
                            allElements.add(new Empty(x,y));
                        }
                        else{
                            allElements.add(field[x][y]);
                        }
                    }
                }
                return allElements;
            }

            private void fillField(Point[][] field, Iterable<? extends Point> points){
                for (Point p: points)
                    field[p.getX()][p.getY()] = p;
            }
        };
    }
}
