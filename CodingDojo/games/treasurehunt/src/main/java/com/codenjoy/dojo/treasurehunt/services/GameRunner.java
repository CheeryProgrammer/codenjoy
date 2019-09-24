package com.codenjoy.dojo.treasurehunt.services;

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
import com.codenjoy.dojo.treasurehunt.client.ai.AISolver;
import com.codenjoy.dojo.treasurehunt.client.ai.Board;
import com.codenjoy.dojo.treasurehunt.model.*;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.multiplayer.GameField;
import com.codenjoy.dojo.services.multiplayer.GamePlayer;
import com.codenjoy.dojo.services.multiplayer.MultiplayerType;
import com.codenjoy.dojo.services.printer.BoardReader;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.SettingsImpl;
import org.json.JSONObject;

import static com.codenjoy.dojo.services.settings.SimpleParameter.v;

/**
 * Генератор игор - реализация {@see GameType}
 */
public class GameRunner extends AbstractGameType implements GameType {

    private final Level level;

    public GameRunner() {
        new Scores(0, settings);
        level = new LevelImpl(getMap(), settings);
    }

    protected String getMap() {
        return "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼" +
                "☼  R  B        ☼" +
                "☼  RS          ☼" +
                "☼  R           ☼" +
                "☼              ☼" +
                "☼  R   $       ☼" +
                "☼              ☼" +
                "☼       R      ☼" +
                "☼ C    R       ☼" +
                "☼              ☼" +
                "☼              ☼" +
                "☼              ☼" +
                "☼RRRR          ☼" +
                "☼     W        ☼" +
                "☼              ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼";


        /*
        return "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼" +
                "☼          $                 ☼" +
                "☼                            ☼" +
                "☼   $              $         ☼" +
                "☼                       $    ☼" +
                "☼  $                         ☼" +
                "☼                            ☼" +
                "☼                            ☼" +
                "☼              S             ☼" +
                "☼        $                   ☼" +
                "☼                            ☼" +
                "☼                            ☼" +
                "☼ C                         $☼" +
                "☼                            ☼" +
                "☼              $             ☼" +
                "☼                            ☼" +
                "☼    $                       ☼" +
                "☼                            ☼" +
                "☼                       $    ☼" +
                "☼                            ☼" +
                "☼                            ☼" +
                "☼                            ☼" +
                "☼            $               ☼" +
                "☼                            ☼" +
                "☼                            ☼" +
                "☼       S                $   ☼" +
                "☼                            ☼" +
                "☼       ☺        $           ☼" +
                "☼                            ☼" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼";*/
    }

    @Override
    public PlayerScores getPlayerScores(Object score) {
        return new Scores((Integer)score, settings);
    }

    @Override
    public GameField createGame(int levelNumber) {
        return new TreasureHuntGameEngine(level, getDice());
    }

    @Override
    public Parameter<Integer> getBoardSize() {
        return v(level.getSize());
    }

    @Override
    public String name() {
        return "treasurehunt";
    }

    @Override
    public Enum[] getPlots() {
        return Elements.values();
    }

    @Override
    public Class<? extends Solver> getAI() {
        return AISolver.class;
    }

    @Override
    public Class<? extends Board> getBoard() {
        return Board.class;
    }

    @Override
    public MultiplayerType getMultiplayerType() {
        return MultiplayerType.SINGLE;
    }

    @Override
    protected SettingsImpl createSettings() {
        SettingsImpl s = new SettingsImpl();
        s.addEditBox("Chests count").type(Integer.class).def(2);
        s.addEditBox("Bags count").type(Integer.class).def(3);
        return s;
    }

    @Override
    public GamePlayer createPlayer(EventListener listener, String playerName) {
        return new Player(listener);
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
            result.put("isAlive", player.isAlive());
            result.put("currentScore", player.getCurrentScore());
            return result;
        });
    }
}