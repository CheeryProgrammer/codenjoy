package com.evo.NEAT;

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
import java.util.ArrayList;

/**
 * NodeGene represents the nodes of the neural network
 * Created by vishnughosh on 28/02/17.
 */
public class NodeGene {

    private float value;

    private ArrayList<ConnectionGene> incomingCon = new ArrayList<>();

    public NodeGene(float value) {
        super();
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public ArrayList<ConnectionGene> getIncomingCon() {
        return incomingCon;
    }

    public void setIncomingCon(ArrayList<ConnectionGene> incomingCon) {
        this.incomingCon = incomingCon;
    }

}
