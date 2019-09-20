package com.codenjoy.dojo.treasurehunt.neat.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.evo.NEAT.Environment;
import com.evo.NEAT.Genome;
import com.evo.NEAT.Pool;


public class NeatTeacher implements Environment {

    SolverPool sp = new SolverPool();

    @Override
    public void evaluateFitness(ArrayList<Genome> population) {
        List<Float> x = population.stream() //
                .parallel() //
                .map(e -> extracted(e)).collect(Collectors.toList());
        x.size();
    }

    public float extracted(Genome gene) {
        gene.setFitness(0);
        float fitness = calculateFitness(gene);
        System.out.println("fitness:" + fitness);
        gene.setFitness(fitness);
        return fitness;
    }

    public float calculateFitness(Genome gene) {
        float result = 0;
        try {
            NeuroSolver solver = sp.get(gene);
            result = solver.getScoreAsync();
            sp.returnItem(solver);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        float expected = 1000;
        if (result > expected) {
            System.err.println(result);
        }
        float fitness = (result - expected) / expected;
        return fitness;
    }

    public static void main(String arg0[]) {
        NeatTeacher xor = new NeatTeacher();

        Pool pool = new Pool();
        pool.initializePool();

        Genome topGenome = new Genome();
        int generation = 0;
        while (true) {
            //pool.evaluateFitness();
            pool.evaluateFitness(xor);
            topGenome = pool.getTopGenome();
            float points = topGenome.getPoints();
            System.out.println("TopFitness : " + points);

            if (points > 15) {
                break;
            }
            //            System.out.println("Population : " + pool.getCurrentPopulation() );
            System.out.println("Generation : " + generation);
            //           System.out.println("Total number of matches played : "+TicTacToe.matches);
            //           pool.calculateGenomeAdjustedFitness();

            pool.breedNewGeneration();
            generation++;

        }
        //System.out.println(topGenome.evaluateNetwork(new float[] { 1, 0 })[0]);
        System.exit(1);
    }
}
