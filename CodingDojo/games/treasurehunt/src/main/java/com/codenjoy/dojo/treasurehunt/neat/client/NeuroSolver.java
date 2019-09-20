package com.codenjoy.dojo.treasurehunt.neat.client;

import java.util.concurrent.ExecutionException;

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.treasurehunt.client.Board;
import com.codenjoy.dojo.treasurehunt.client.Elements;

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
import com.evo.NEAT.Genome;

/**
 * User: your name
 */
public class NeuroSolver implements Solver<Board> {
    private static final int MAX_LIFE = 50;

    private Genome gene;
    //    private Board board;

    private boolean isFinished = false;
    private String boardString;
    int steps;
    private int score;
    private int life;

    private final Object geneLock = new Object();

    public NeuroSolver() {
    }

    @Override
    public String get(Board board) {

        if (!board.getIsAlive()) {
            setFinish(board);
            return Direction.ACT.toString();
        }

        if ((score - board.getCurrentScore()) == 0) {
            life--;
            if (life == 0) {
                System.out.println(steps + " -> " + " life:" + life + " -> " + this.boardString);
                life = MAX_LIFE;
                this.boardString = board.getBoardString() + " - ACT";
                setFinish(board);
                return Direction.ACT.toString();
            }
        } else {
            life = MAX_LIFE;
        }

        float[] inputs = toInputs(board);
        float[] r = getGene().evaluateNetwork(inputs);

        score = board.getCurrentScore();
        Direction direction = getDirection(r);
        this.boardString = board.getBoardString() + " - " + direction;
        System.out.println(steps + " -> " + " life:" + life + " -> " + this.boardString);

        steps++;
        return direction.toString();
    }

    private Genome getGene() {
        Genome g = gene;
        if (g == null) {
            synchronized (geneLock) {
                while (gene == null) {
                    try {
                        geneLock.wait(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return gene;
            }
        }
        return g;
    }

    public void setGene(Genome gene) {
        synchronized (geneLock) {
            this.isFinished = false;
            steps = 0;
            life = MAX_LIFE;
            this.gene = gene;
            geneLock.notifyAll();
        }
    }

    private float[] toInputs(Board board) {

        char[][] field = board.getField();
        if (field != null) {
            int size = field.length;
            float[] inputs = new float[size * size];
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    inputs[x * size + y] = toNumber(field[x][y]);
                }
            }
            return inputs;
        }
        return new float[0];
    }

    private float toNumber(char c) {
        return Elements.valueOf(c).number();
    }

    private Direction getDirection(float[] r) {
        float max = -1f;
        int d = 0;
        for (int i = 0; i < r.length; i++) {
            max = Math.max(max, r[i]);
            if (max == r[i]) {
                d = i;
            }
        }

        return Direction.valueOf(d);
    }

    public static com.codenjoy.dojo.treasurehunt.neat.client.NeuroSolver create() throws InterruptedException, ExecutionException {
        com.codenjoy.dojo.treasurehunt.neat.client.NeuroSolver solver = new com.codenjoy.dojo.treasurehunt.neat.client.NeuroSolver();
        WebSocketRunner.runClient(
                "http://localhost:8080/codenjoy-contest/board/player/vrvm40aetmndbdzyl7uo?code=1",
                //"http://epruryaw0537.moscow.epam.com:23345/codenjoy-contest/board/player/quriyqkxsa6923cqr6li?code=1345206823804532213",

                solver,
                new Board());
        return solver;
    }

    public synchronized int getScoreAsync() throws InterruptedException {
        while (!isFinished) {
            this.wait(5000);
        }
        return score;
    }

    public synchronized void setFinish(Board board) {
        score = board.getCurrentScore();
        setGene(null);
        isFinished = true;
        this.notifyAll();
    }

}
