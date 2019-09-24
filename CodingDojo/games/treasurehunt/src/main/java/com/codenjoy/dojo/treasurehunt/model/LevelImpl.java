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


import com.codenjoy.dojo.treasurehunt.model.items.Bag;
import com.codenjoy.dojo.treasurehunt.model.items.Chest;
import com.codenjoy.dojo.treasurehunt.model.items.Rock;
import com.codenjoy.dojo.treasurehunt.model.items.Wall;
import com.codenjoy.dojo.services.LengthToXY;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.Settings;

import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.treasurehunt.model.Elements.*;
import static java.util.stream.Collectors.toList;

/**
 * Полезный утилитный класс для получения объектов на поле из текстового вида.
 */
public class LevelImpl implements Level {
    private final LengthToXY xy;
    private final Settings settings;
    private final Parameter<Integer> bagsCount;
    private final Parameter<Integer> chestsCount;

    private String map;

    public LevelImpl(String map, Settings settings) {
        this.map = map;
        xy = new LengthToXY(getSize());
        this.settings = settings;
        bagsCount = settings.getParameter("Bags count").type(Integer.class);
        chestsCount = settings.getParameter("Chests count").type(Integer.class);
    }

    @Override
    public int getSize() {
        return (int) Math.sqrt(map.length());
    }

    @Override
    public List<Hero> getHero() {
        return pointsOf(HERO_DOWN).stream()
                .map(Hero::new)
                .collect(toList());

    }

    @Override
    public List<Bag> getBags() {
        return pointsOf(BAG).stream()
                .map(Bag::new)
                .collect(toList());
    }

    @Override
    public List<Chest> getChests() {
        return pointsOf(CHEST).stream()
                .map(Chest::new)
                .collect(toList());
    }

    @Override
    public List<Wall> getWalls() {
        return pointsOf(WALL).stream()
                .map(Wall::new)
                .collect(toList());
    }

    @Override
    public List<Rock> getRocks() {
        return pointsOf(ROCK).stream()
                .map(Rock::new)
                .collect(toList());
    }

    @Override
    public int getChestsCount() {
        return chestsCount.getValue();
    }

    @Override
    public int getBagsCount() {
        return bagsCount.getValue();
    }

    private List<Point> pointsOf(Elements element) {
        List<Point> result = new LinkedList<>();
        for (int index = 0; index < map.length(); index++) {
            if (map.charAt(index) == element.ch) {
                result.add(xy.getXY(index));
            }
        }
        return result;
    }
}