package com.codenjoy.dojo.treasurehunt.neat.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import com.evo.NEAT.Genome;

public class SolverPool {
    List<NeuroSolver> all = new LinkedList<>();
    Queue<NeuroSolver> free = new LinkedList<>();

    public synchronized NeuroSolver get(Genome gene) throws InterruptedException, ExecutionException {

        NeuroSolver result = free.poll();
        if (result == null) {
            result = NeuroSolver.create();
            all.add(result);
        }
        result.setGene(gene);
        return result;
    }

    public synchronized void returnItem(NeuroSolver ns) {
        free.add(ns);
    }
}
