package com.codenjoy.dojo.eatordie.utils;

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

import com.codenjoy.dojo.eatordie.model.Field;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;

import java.util.ArrayList;
import java.util.List;

public class Dijkstra {

    private WayNode[][] _checked;

    public List<Direction> findWay(Point source, Point dest, Field field){
        List<Direction> way = new ArrayList<>();
        System.out.println("Destination " + dest == null ? "is null" : dest.toString());
        if(source.itsMe(dest))
            return way;
        _checked = new WayNode[field.size()][field.size()];
        WayNode root = new WayNode(source.copy());
        root.setCameFrom(null, 0);
        WayNode destination = findDestination(root, dest, field, 1);

        if(destination != null)
            way = BuildWay(destination);

        return way;
    }

    private WayNode findDestination(WayNode node, Point destination, Field field, int stepCount) {
        WayNode foundInChildren = null;
        int checkedX = node.getPosition().getX();
        int checkedY = node.getPosition().getY();

        if(node.getPosition().itsMe(destination))
            foundInChildren = node;

        if(_checked[checkedX][checkedY] != null)
            return foundInChildren;

        _checked[checkedX][checkedY] = node;

        List<WayNode> possibleSteps = getPossibleSteps(node, destination, field);
        for (WayNode step : possibleSteps) {
            step.setCameFrom(node, stepCount);
            if(_checked[step.getPosition().getX()][step.getPosition().getY()] != null){
                if(_checked[step.getPosition().getX()][step.getPosition().getY()].getStepCount() > stepCount)
                    _checked[step.getPosition().getX()][step.getPosition().getY()].setCameFrom(node, stepCount);
            }
            foundInChildren = findDestination(step, destination, field, stepCount + 1);
        }
        return foundInChildren;
        /*
        int checkedX = node.getPosition().getX();
        int checkedY = node.getPosition().getY();
        if(_checked[checkedX][checkedY] == null)
        {
            _checked[checkedX][checkedY] = node;
            WayNode foundDestinationNode = null;
            List<WayNode> possibleSteps = getPossibleSteps(node, destination, field);

            for (WayNode step : possibleSteps) {
                WayNode foundInChildren = findDestination(step, destination, field, stepCount + 1);
                if(step.getCameFrom() == null || step.getStepCount() > stepCount)
                    step.setCameFrom(node, stepCount);
                if (_checked[step.getPosition().getX()][step.getPosition().getY()] != null) {
                    WayNode checked = _checked[step.getPosition().getX()][step.getPosition().getY()];
                    if (checked.getStepCount() > stepCount) {
                        checked.setCameFrom(node, stepCount);
                    }
                }
                if (foundInChildren != null)
                    foundDestinationNode = foundInChildren;
                if (foundDestinationNode == null && step.getPosition().itsMe(destination))
                    foundDestinationNode = step;
            }
            return foundDestinationNode;
        }
        else{
            if(_checked[checkedX][checkedY].getStepCount() > stepCount)
                _checked[checkedX][checkedY].setCameFrom(node.getCameFrom(), stepCount);
        }
        return null;*/
    }

    private List<WayNode> getPossibleSteps(WayNode currentPlace, Point dest, Field field) {
        List<WayNode> possibleWays = new ArrayList<>();
        tryToGo(possibleWays, currentPlace.getPosition(), Direction.UP, dest, field);
        tryToGo(possibleWays, currentPlace.getPosition(), Direction.DOWN, dest, field);
        tryToGo(possibleWays, currentPlace.getPosition(), Direction.LEFT, dest, field);
        tryToGo(possibleWays, currentPlace.getPosition(), Direction.RIGHT, dest, field);
        return possibleWays;
    }

    private void tryToGo(List<WayNode> possibleWays, Point currentPosition, Direction direction, Point destination, Field field) {
        Point whereToGo = direction.change(currentPosition.copy());
        int x = whereToGo.getX();
        int y = whereToGo.getY();
        // if were here, skip
        if(x < 0 || x >= _checked.length || y < 0 || y >= _checked.length)
            return;

        if (field.isFree(whereToGo) || destination.itsMe(whereToGo))
        {
            WayNode newWay = new WayNode(whereToGo);
            possibleWays.add(newWay);
        }
    }

    private List<Direction> BuildWay(WayNode destination) {
        List<Direction> way = new ArrayList<>();
        WayNode currentNode = destination;
        WayNode cameFrom;
        while ((cameFrom = currentNode.getCameFrom()) != null){
            Direction direction = getDirection(cameFrom, currentNode);
            System.out.println("From " + cameFrom.getPosition().toString() + " to " + currentNode.getPosition().toString() + " is " + direction.toString() + " " + currentNode.getStepCount());
            way.add(direction);
            currentNode = cameFrom;
        }
        System.out.println();
        return way;
    }

    private static Direction getDirection(WayNode from, WayNode to) {
        Point fromPt = from.getPosition().copy();
        Point toPt = to.getPosition();

        if(Direction.UP.change(fromPt).itsMe(toPt))
            return Direction.UP;
        if(Direction.RIGHT.change(fromPt).itsMe(toPt))
            return Direction.RIGHT;
        if(Direction.DOWN.change(fromPt).itsMe(toPt))
            return Direction.DOWN;

        return Direction.LEFT;
    }
}
