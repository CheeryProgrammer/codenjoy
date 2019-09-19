using SharpNeat.Core;
using SharpNeat.Phenomes;

namespace neat_net_client
{
    public class GameEvaluator : IPhenomeEvaluator<IBlackBox>
    {
        const double _stopFitness = 200.0;
        const int _stuckCounter = 200;
        ulong _evalCount;
        bool _stopConditionSatisfied;
        NeuroSolver _solver;

        /// <summary>
        /// Здесь доска с сервера транслируется во входные сигналы для сети
        /// </summary>
        /// <param name="inputArr"></param>
        /// <param name="board"></param>
        private void SetUpInputsForNeuroNetwork(ISignalArray inputArr, Board board)
        {
            var wholeBoard = board.WholeBoard;
            for (int i = 0; i < wholeBoard.Length; i++)
            {
                inputArr[i] = wholeBoard[i];
            }
        }

        /// <summary>
        /// Здесь описана логика трансляции результатов работы нейронной сети в команду для отправления на сервер и отправление этой команды
        /// </summary>
        /// <param name="outputArr"></param>
        private void ReadOutputSignalAndSendMessage(ISignalArray outputArr)
        {
            int maxIndex = 0;
            for (int i=1;i<outputArr.Length;i++)
            {
                if (outputArr[i] > outputArr[maxIndex])
                    maxIndex = i;
            }

            string action;

            switch (outputArr[maxIndex])
            {
                case 0:
                    {
                        action = "DOWN";
                        break;
                    }
                case 1:
                    {
                        action = "LEFT";
                        break;
                    }
                case 2:
                    {
                        action = "UP";
                        break;
                    }
                default:
                    {
                        action = "RIGHT";
                        break;
                    }
            }

            _solver.SendAction(action);
        }

        public GameEvaluator(NeuroSolver solver)
        {
            _solver = solver;
        }

        #region IPhenomeEvaluator<IBlackBox> Members

        /// <summary>
        /// Gets the total number of evaluations that have been performed.
        /// </summary>
        public ulong EvaluationCount
        {
            get { return _evalCount; }
        }

        /// <summary>
        /// Gets a value indicating whether some goal fitness has been achieved and that
        /// the evolutionary algorithm/search should stop. This property's value can remain false
        /// to allow the algorithm to run indefinitely.
        /// </summary>
        public bool StopConditionSatisfied
        {
            get { return _stopConditionSatisfied; }
        }

        /// <summary>
        /// Evaluate the provided IBlackBox against the XOR problem domain and return its fitness score.
        /// </summary>
        public FitnessInfo Evaluate(IBlackBox box)
        {
            int fitness = 0;
            double output;
            double pass = 1.0;

            _solver.SendAction("ACT");
            Board board = _solver.CurrentBoard;

            var prevScore = board.Score;
            var stuckCount = 0;

            //Пока можем вести игру
            while (board.IsAlive)
            {
                ISignalArray inputArr = box.InputSignalArray;
                ISignalArray outputArr = box.OutputSignalArray;

                _evalCount++;

                //Сбросим состояние
                box.ResetState();

                //Зададим значения на входах нейронной сети
                SetUpInputsForNeuroNetwork(inputArr, board);

                //Выполним одну итерацию работы нейронной сети
                box.Activate();

                if (!box.IsStateValid)
                {  
                    return FitnessInfo.Zero;
                }

                //Считаем результаты работы и отправим сообщение
                ReadOutputSignalAndSendMessage(outputArr);

                //Получим обновленное состояние доски
                board = _solver.CurrentBoard;

                //Считаем новое количество очков
                fitness = board.Score;

                //Если ничего не произошло - тогда увеличим счётчик застревания сети, то есть она не сделал ничего хорошего
                //Это просто заглушка, для нормальной работы придумайте свой алгоритм распознования того, что сетка не делает ничего полезного!
                if (fitness == prevScore)
                    stuckCount++;
                else
                    prevScore = fitness;

                if (stuckCount == _stuckCounter)
                    break;
            }

            //Если цель обучения нашей сети выполнена, то есть сетка набирает заданное минимальное количество очков - тогда выставим флаг об успешном обучении
            if (fitness > _stopFitness)
            {
                _stopConditionSatisfied = true;
            }

            return new FitnessInfo(fitness, fitness);
        }

        

        /// <summary>
        /// Reset the internal state of the evaluation scheme if any exists.
        /// Note. The XOR problem domain has no internal state. This method does nothing.
        /// </summary>
        public void Reset()
        {
        }

        #endregion
    }

}
