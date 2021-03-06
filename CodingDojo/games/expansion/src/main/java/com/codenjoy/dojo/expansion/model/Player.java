package com.codenjoy.dojo.expansion.model;

/*-
 * #%L
 * iCanCode - it's a dojo-like platform from developers to developers.
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


import com.codenjoy.dojo.services.DLoggerFactory;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.utils.JsonUtils;
import com.codenjoy.dojo.expansion.model.levels.items.Hero;
import com.codenjoy.dojo.expansion.services.Events;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class Player extends GamePlayer<Hero, Field> {

    private static Logger logger = DLoggerFactory.getLogger(Player.class);

    Hero hero;
    private ProgressBar progressBar;
    private String name;

    public Player(EventListener listener, ProgressBar progressBar, String name) {
        super(listener);
        this.progressBar = progressBar;
        this.name = name;
        progressBar.setPlayer(this);
    }

    public void event(Events event) {
        if (logger.isDebugEnabled()) {
            logger.debug("Player {} fired event {}", lg.id(), event);
        }

        if (listener != null && progressBar.enableWinScore()) {
            listener.event(event);
        }
    }

    public Hero getHero() {
        return hero;
    }

    public void newHero(Field field) {
        if (hero == null) {
            hero = new Hero();
        }

        hero.setField(field);
    }

    @Override
    public boolean isAlive() {
        return hero.isAlive();
    }

    public void tick() {
        progressBar.checkLevel();
        hero.tick();
    }

    public void setNextLevel() {
        progressBar.setNextLevel();
    }

    public int getForcesColor() {
        return hero.getBase().element().getIndex();
    }

    public Game getGame() {
        return progressBar.getGameOwner();
    }

    public Point getBasePosition() {
        return hero.getBasePosition();
    }

    public JSONObject getCurrentAction() {
        return hero.getCurrentAction();
    }

    public int getForcesPerTick() {
        return hero.getForcesPerTick();
    }

    public void destroyHero() {
        if (hero != null) {
            hero.destroy();
        }
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public void setPlayerBoard(PlayerBoard current) {
        progressBar.setCurrent(current);
    }

    public PlayerBoard getCurrent() {
        return progressBar.getCurrent();
    }

    public String getName() {
        return name;
    }

    public class LogState {
        public JSONObject json() {
            return new JSONObject(){{
                put("id", id());
                put("name", name);
                put("hero", (hero != null) ? hero.lg.json() : "null");
                put("progressBar", (progressBar != null) ? progressBar.lg.json() : "null");
            }};
        }

        public String id() {
            return "P@" + Integer.toHexString(Player.this.hashCode());
        }
    }

    public static List<String> lg(List<Player> players) {
        return players.stream().map(p -> (p != null) ? p.lg.id() : "null").collect(toList());
    }

    public LogState lg = new LogState();

    @Override
    public String toString() {
        return JsonUtils.toStringSorted(lg.json());
    }
}