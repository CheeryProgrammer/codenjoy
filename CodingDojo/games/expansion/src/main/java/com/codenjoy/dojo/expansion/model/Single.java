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


import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.hero.HeroData;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.printer.Printer;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.layeredview.PrinterData;
import com.codenjoy.dojo.utils.JsonUtils;
import com.codenjoy.dojo.expansion.model.lobby.PlayerLobby;
import com.codenjoy.dojo.expansion.services.SettingsWrapper;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Supplier;

/**
 * А вот тут немного хак :) Дело в том, что фреймворк изначально не поддерживал игры типа "все на однмо поле", а потому
 * пришлось сделать этот декоратор. Борда (@see Sample) - одна на всех, а игры (@see Single) у каждого своя. Кода тут не много.
 */
public abstract class Single implements Game {

    private static Logger logger = DLoggerFactory.getLogger(Single.class);

    private Ticker ticker;
    private ProgressBar progressBar;
    private Supplier<PlayerLobby> lobby;
    private Player player;
    private String save;

    public Single(GameFactory gameFactory, Supplier<PlayerLobby> lobby, EventListener listener,
                  PrinterFactory factory, Ticker ticker, Dice dice, String save, String playerName) {
        this.save = save;
        this.ticker = ticker;
        this.lobby = lobby;
        progressBar = new ProgressBar(gameFactory, lobby);
        progressBar.setGameOwner(this);
        player = new Player(listener, progressBar, playerName);
    }

    @Override
    public Joystick getJoystick() {
        return player.getHero();
    }

    @Override
    public boolean isGameOver() {
        return false;
    }

    @Override
    public void newGame() {
        if (logger.isDebugEnabled()) {
            logger.debug("Starts new game for {}", lg.id());
        }

        progressBar.start(save);
    }

    @Override
    public JSONObject getBoardAsString() {
        PrinterData data = getPrinter().print();

        JSONObject result = new JSONObject();
        List<String> layers = data.getLayers();
        String forces = layers.remove(2);
        result.put("layers", layers);
        result.put("forces", forces);
        result.put("myBase", new JSONObject(player.getBasePosition()));
        result.put("myColor", player.getForcesColor());
        result.put("tick", ticker.get());
        result.put("round", progressBar.getRoundTicks());
        result.put("rounds", SettingsWrapper.data.roundTicks());
        result.put("available", player.getForcesPerTick());
        result.put("offset", data.getOffset());
        JSONObject progress = progressBar.printProgress();
        result.put("showName", true);
        result.put("onlyMyName", !progress.getBoolean("multiple"));
        result.put("levelProgress", progress);
        result.put("inLobby", progressBar.inLobby());

        if (logger.isDebugEnabled()) {
            logger.debug("getBoardAsString for game {} prepare {}", lg.id(), JsonUtils.toStringSorted(result));
        }

        return result;
    }

    public void destroy() {
        lobby.get().remove(player);
        progressBar.remove(player);
        progressBar.setCurrent(null);
    }

    @Override
    public void clearScore() {
        // do nothing
    }

    @Override
    public HeroData getHero() {
        return new GameHeroData();
    }

    ProgressBar getProgressBar() {
        return progressBar;
    }

    public class GameHeroData implements HeroData {
        @Override
        public Point getCoordinate() {
            return new PointImpl(player.getHero().getBasePosition());
        }

        @Override
        public boolean isMultiplayer() {
            return progressBar.isMultiple();
        }

        @Override
        public int getLevel() {
            return 0;
        }

        @Override
        public Object getAdditionalData() {
            JSONObject result = new JSONObject();
            result.put("lastAction", player.getCurrentAction());

            if (logger.isDebugEnabled()) {
                logger.debug("getAdditionalData for game {} prepare {}", lg.id(), JsonUtils.toStringSorted(result));
            }

            return result;
        }

    };

    @Override
    public JSONObject getSave() {
        return progressBar.printProgress();
    }

    public void tick() {
        ticker.tick();

        if (logger.isDebugEnabled()) {
            logger.debug("----------------------------------------------------------------------------------------------------------------------");
            logger.debug("Game start tick {}", ticker.get(), toString());
        }

        progressBar.tick();

        if (logger.isDebugEnabled()) {
            logger.debug("Game finish tick {}", toString());
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Printer<PrinterData> getPrinter() {
        return progressBar.getPrinter();
    }

    public class LogState {
        public JSONObject json() {
            return new JSONObject(){{
                put("id", id());
                put("tick", ticker.get());
                put("progressBar", (progressBar != null) ? progressBar.lg.id() : "null");
                put("player", (player != null) ? player.lg.id() : "null");
                put("save", save);
            }};
        }

        public String id() {
            return "S@" + Integer.toHexString(Single.this.hashCode());
        }
    }

    LogState lg = new LogState();

    @Override
    public String toString() {
        return JsonUtils.toStringSorted(lg.json());
    }
}
