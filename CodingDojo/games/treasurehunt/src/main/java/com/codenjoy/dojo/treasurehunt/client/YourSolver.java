package com.codenjoy.dojo.treasurehunt.client;

import static com.codenjoy.dojo.services.Direction.DOWN;
import static com.codenjoy.dojo.services.Direction.RIGHT;

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;

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

import com.codenjoy.dojo.services.Direction;

/**
 * User: your name
 */
public class YourSolver implements Solver<ClientBoard> {

    boolean b;
    boolean isFinished = false;

    @Override
    public String get(ClientBoard board) {
        //Direction.ACT;

        b = !b;
        Direction direction = b ? DOWN : RIGHT;
        return direction.toString();
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                "http://localhost:8080/codenjoy-contest/board/player/quriyqkxsa6923cqr6li?code=1345206823804532213",
                //"http://epruryaw0537.moscow.epam.com:23345/codenjoy-contest/board/player/quriyqkxsa6923cqr6li?code=1345206823804532213",
                new YourSolver(),
                new ClientBoard());
    }



}
