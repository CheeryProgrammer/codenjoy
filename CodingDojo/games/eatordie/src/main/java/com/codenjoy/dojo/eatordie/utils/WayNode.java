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

import com.codenjoy.dojo.services.Point;

public class WayNode {
    private final Point _point;
    private WayNode _cameFrom;
    private int _stepCount;
    private boolean _isChecked;

    public WayNode(Point point) {
        _point = point;
        _isChecked = false;
    }

    public void setCameFrom(WayNode node, int stepCount){
        _cameFrom = node;
        _stepCount = stepCount;
    }

    public WayNode getCameFrom(){
        return _cameFrom;
    }

    public Point getPosition() {
        return _point;
    }

    public int getStepCount() {
        return _stepCount;
    }

    public void markChecked(){
        _isChecked = true;
    }

    public boolean getIsChecked(){
        return _isChecked;
    }
}
