package com.codenjoy.dojo.a2048.services;

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


import com.codenjoy.dojo.a2048.client.Board;
import com.codenjoy.dojo.a2048.client.ai.AISolver;
import com.codenjoy.dojo.a2048.model.*;
import com.codenjoy.dojo.a2048.model.Number;
import com.codenjoy.dojo.client.ClientBoard;
import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.multiplayer.GameField;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.multiplayer.MultiplayerType;
import com.codenjoy.dojo.services.printer.BoardReader;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.Settings;
import org.json.JSONObject;

import static com.codenjoy.dojo.services.settings.SimpleParameter.v;

public class GameRunner extends AbstractGameType implements GameType {

    private Level level;

    public GameRunner() {
        level = new LevelImpl();
    }

    @Override
    public PlayerScores getPlayerScores(Object score) {
        return new Scores((Integer) score);
    }

    @Override
    public GameField createGame(int levelNumber) {
        return new A2048(level, getDice());
    }

    @Override
    public Parameter<Integer> getBoardSize() {
        return v(level.size());
    }

    @Override
    public String name() {
        return "a2048";
    }

    @Override
    public Enum[] getPlots() {
        return Elements.values();
    }

    @Override
    public Settings getSettings() {
        return level.getSettings();
    }

    @Override
    public MultiplayerType getMultiplayerType() {
        return MultiplayerType.SINGLE;
    }

    @Override
    public GamePlayer createPlayer(EventListener listener, String playerName) {
        return new Player(listener);
    }

    @Override
    public Class<? extends Solver> getAI() {
        return AISolver.class;
    }

    @Override
    public Class<? extends ClientBoard> getBoard() {
        return Board.class;
    }

    @Override
    public PrinterFactory getPrinterFactory() {
        return PrinterFactory.get((BoardReader reader, Player player) -> {
            JSONObject result = new JSONObject();
            StringBuilder sb = new StringBuilder();
            for (Point el: reader.elements() ) {
                sb.append(((State<Elements, Player>)el).state(null));
            }
            result.put("field", sb.toString());
            result.put("isGameOver", player.isGameOver());
            return result;
        });
    }
}
