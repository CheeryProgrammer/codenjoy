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
/**
 * ConnectionGene Represents the connection(Axon) of the neuron
 * ConnectionGenes can completely represent the neuron as Nodes are generated while performing operation
 * Created by vishnughosh on 28/02/17.
 */
public class ConnectionGene {

    private int into,out, innovation;
    private float weight;
    private boolean enabled;

    public ConnectionGene(int into, int out, int innovation, float weight, boolean enabled) {
        this.into = into;
        this.out = out;
        this.innovation = innovation;
        this.weight = weight;
        this.enabled = enabled;
    }

    // Copy
    public ConnectionGene(ConnectionGene connectionGene){
        if(connectionGene!=null) {
            this.into = connectionGene.getInto();
            this.out = connectionGene.getOut();
            this.innovation = connectionGene.getInnovation();
            this.weight = connectionGene.getWeight();
            this.enabled = connectionGene.isEnabled();
        }
    }

    public int getInto() {
        return into;
    }

    public int getOut() {
        return out;
    }

    public int getInnovation() {
        return innovation;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    @Override
    public String toString() {
/*        return "ConnectionGene{" +
                "into=" + into +
                ", out=" + out +
                ", innovation=" + innovation +
                ", weight=" + weight +
                ", enabled=" + enabled +
                '}';*/
        return into+","+out+","+weight+","+enabled;
    }
}
