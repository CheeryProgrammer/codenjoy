using System;
using Newtonsoft.Json;
using Json.Net;

namespace neat_net_client
{
    public class Board
    {
        private String _boardString;
        private int _score;
        private bool _isAlive;
        private int[] _board;

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

        public Board(String boardString)
        {
            try
            {
                int indexOfIsGameOver = boardString.IndexOf("isAlive");
                int indexOfScore = boardString.IndexOf("currentScore"); 
                int indexOfField = boardString.IndexOf("field");
                _isAlive = Boolean.Parse(
                    boardString.Substring(
                        indexOfIsGameOver + 8, 
                        boardString.IndexOf(",", indexOfIsGameOver) - (indexOfIsGameOver + 8)
                    )
                );
                _score = Int32.Parse(
                    boardString.Substring(
                        indexOfScore + 13,
                        boardString.IndexOf(",", indexOfScore) - (indexOfScore + 13)
                    )
                );

                _boardString = boardString.Substring(
                        indexOfField + 7,
                        boardString.IndexOf("}", indexOfField + 7) - (indexOfField + 8)
                    );
            }
            catch (Exception exc)
            {
                throw;
            }
            

            if (!_isAlive)
                return;

            _board = new int[_boardString.Length];
            for (int i = 0; i < _boardString.Length; i++)
            {
                _board[i] = GetIntValueByChar(_boardString[i]);
            }
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
