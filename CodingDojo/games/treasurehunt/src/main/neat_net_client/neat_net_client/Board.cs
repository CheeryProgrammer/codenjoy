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
﻿using System;
using Newtonsoft.Json;
using Json.Net;
using System.Runtime.Serialization;

namespace neat_net_client
{
    public class Board
    {
        [JsonProperty(PropertyName = "field")]
        private String _boardString;
        [JsonProperty(PropertyName = "currentScore")]
        private int _score;
        [JsonProperty(PropertyName = "isAlive")]
        private bool _isAlive;
        private int[] _board;

        [OnDeserialized]
        internal void TranslateFieldToIntArray(StreamingContext ctx)
        {
            if (!_isAlive)
                return;

            _board = new int[_boardString.Length];
            for (int i = 0; i < _boardString.Length; i++)
            {
                _board[i] = GetIntValueByChar(_boardString[i]);
            }
        }

        /// <summary>
        /// В данном методе описана логика трансляции доски с сервера в сигналы для нейронной сети
        /// </summary>
        /// <param name="symbol"></param>
        /// <returns></returns>
        private int GetIntValueByChar(char symbol)
        {
            switch (symbol)
            {
                case '☼':
                case 'R': { return -10; } break;
                case 'v':
                case '>':
                case '<':
                case '^': { return -5; } break;
                case ' ': { return 0; } break;
                case '$': { return 3; } break;
                case 'B': { return 5; } break;
                case 'S': { return 1; } break;
            }

            return 0;
        }

        public int Score
        {
            get
            {
                return _score;
            }
        }

        public bool IsAlive
        {
            get
            {
                return _isAlive;
            }
        }

        public int[] WholeBoard
        {
            get
            {
                return _board;
            }
        }

        public int BoardLength
        {
            get
            {
                return (int)Math.Sqrt(_board.Length);
            }
        }

        public Point GetHeroPosition
        {
            get
            {
                return new Point { X = 0, Y = 0 } ;
            }
        }

        public override string ToString()
        {
            return Score.ToString() + " " + IsAlive.ToString();
        }
    }
}
