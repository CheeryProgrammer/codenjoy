package com.codenjoy.dojo.treasurehunt.client;

import org.json.JSONObject;

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

import com.codenjoy.dojo.client.AbstractBoard;
import com.codenjoy.dojo.client.ClientBoard;
import com.codenjoy.dojo.services.Direction;

public class Board extends AbstractBoard<Elements> {

    Boolean isAlive;
    String boardString;
    int currentScore;

    public ClientBoard forString(String boardString) {
        this.boardString = boardString;
        source = new JSONObject(boardString);
        isAlive = source.getBoolean("isAlive");
        currentScore = source.getInt("currentScore");
        String field = source.getString("field");

        return forString(new String[] { field });
    }

    @Override
    public Elements valueOf(char ch) {
        return Elements.valueOf(ch);
    }

    @Override
    protected int inversionY(int y) { // TODO разобраться с этим чудом
        return size - 1 - y;
    }

    public int getSumCountFor(Direction direction) {
        int result = 0;

        for (int y = 0; y < size; y++) {
            int fromX = 0;
            int toX = 0;
            while (fromX < size && toX < size - 1) {
                toX++;

                Elements at = getAt(fromX, y);
                Elements at2 = getAt(toX, y);
                if (at == Elements.NONE) {
                    fromX++;
                    continue;
                }
                if (at2 == Elements.NONE) {
                    continue;
                }

                if (at != Elements.NONE && at == at2) {
                    result++;
                    fromX = toX + 1;
                    toX = fromX;
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{\n");
        result.append("  isAlive = ").append(getIsAlive()).append(", \n");
        result.append("  currentScore = ").append(getCurrentScore()).append(", \n");
        result.append("  field = {");
        result.append("\n");
        boardAsString(result, "    |", "|");
        result.append("  }\n");
        return result.append("}").toString();
    }

    protected StringBuilder boardAsString(StringBuilder result, String prefix, String postfix) {
        for (int y = 0; y < size; y++) {
            result.append(prefix);
            for (int x = 0; x < size; x++) {
                result.append(field[0][inversionX(x)][inversionY(y)]);
            }
            result.append(postfix).append("\n");
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getBoardString() == null) ? 0 : getBoardString().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Board other = (Board) obj;
        if (getBoardString() == null) {
            if (other.getBoardString() != null)
                return false;
        } else if (!getBoardString().equals(other.getBoardString()))
            return false;
        return true;
    }

    public String getBoardString() {
        return boardString;
    }

    public Boolean getIsAlive() {
        return isAlive;
    }

    public int getCurrentScore() {
        return currentScore;
    }

}
