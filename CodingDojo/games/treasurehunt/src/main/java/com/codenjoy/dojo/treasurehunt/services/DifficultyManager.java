package com.codenjoy.dojo.treasurehunt.services;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
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

import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.treasurehunt.model.Field;
import com.codenjoy.dojo.treasurehunt.model.items.Enemy;

import java.util.List;
import java.util.function.Supplier;

public class DifficultyManager {
    private final int minProbability = 30;
    private final int baseMaxProbability = 70;
    private final int probabilityIncrementPerEnemy = 10;
    private final int maxEnemiesCount = 3;

    private final Field field;
    private final Dice dice;
    private List<Enemy> enemies;
    private int maxProbability;

    // used in correctEnemyCount
    private int calculatedProbability;

    // used in StepProbabilityResolver
    private int actualProbability;

    public DifficultyManager(Field field, Dice dice){
        this.field = field;
        this.dice = dice;
    }

    public void prepare(List<Enemy> enemies, int currentScore){
        if(enemies.isEmpty())
            enemies.add(new Enemy(this.field));
        this.enemies = enemies;
        maxProbability = Math.min(100, baseMaxProbability + enemies.size() * probabilityIncrementPerEnemy);
        calculatedProbability = calculateProbability(currentScore, enemies.size());
        actualProbability = Math.min(maxProbability, Math.max(minProbability, calculatedProbability));
    }

    public StepProbabilityResolver getStepResolver(){
        return new StepProbabilityResolver(dice, actualProbability);
    }

    private int calculateProbability(int currentScore, int enemyCount) {
        return ((minProbability + (currentScore / 20)) / enemyCount) - (probabilityIncrementPerEnemy * (int) Math.pow(enemyCount - 1, 1.9));
    }

    public void correctEnemyCount() {
        if(calculatedProbability > maxProbability && enemies.size() < maxEnemiesCount)
            enemies.add(new Enemy(field));
    }
}
