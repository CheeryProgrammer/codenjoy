package com.codenjoy.dojo.a2048.client;

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

import com.codenjoy.dojo.a2048.model.Elements;
import com.codenjoy.dojo.client.AbstractBoard;
import com.codenjoy.dojo.client.ClientBoard;
import com.codenjoy.dojo.services.Direction;

public class Board extends AbstractBoard<Elements> {

    Boolean isGameOver;

    public ClientBoard forString(String boardString) {
            source = new JSONObject(boardString);
            isGameOver = source.getBoolean("isGameOver");
            String field = source.getString("field");

            return forString(new String[]{field});
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
        result.append("  isGameOver = ").append(isGameOver).append(", \n");
        result.append("  field = {");
        result.append("\n").append(boardAsString(result, "    |","|"));
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

}
