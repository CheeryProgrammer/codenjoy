using System;
using System.Collections.Generic;
using System.IO;
using System.Xml;
using log4net;
using log4net.Config;
using SharpNeat.Core;
using SharpNeat.EvolutionAlgorithms;
using SharpNeat.Genomes.Neat;

namespace neat_net_client
{
    class Program
    {
        static IGenomeFactory<NeatGenome> _genomeFactory;
        static List<NeatGenome> _genomeList;
        static NeatEvolutionAlgorithm<NeatGenome> _evolutionAlgo;

        static void Main(string[] args)
        {
            Console.SetWindowSize(Console.LargestWindowWidth - 3, Console.LargestWindowHeight - 3);
            //Задавайте здесь url сервера, который будете использовать для обучения. По умолчанию стоит локальный сервер
            //так как на боевом сервере есть задержка минимум в 500 мс, что затормозит обучение сильно
            string serverUrl = "http://localhost:8080/";
            var neuroSolver = new NeuroSolver(serverUrl);
            //Здесь активируется соединение по сокетам с выбранным сервером
            neuroSolver.Play();

            //Здесь происходит конфигурация обучения и проводится обучение. Это просто заглушка!!!
            //Там реализована просто совсем базовая логика для обучения, чтобы вам было проще разобраться.
            SetupNeuroNetwork(neuroSolver);

            // Hit return to quit.
            Console.ReadLine();
        }

        private static void SetupNeuroNetwork(NeuroSolver neuroSolver)
        {
            GameExperiment gameExperiment = new GameExperiment();

            XmlDocument log4netConfig = new XmlDocument();
            log4netConfig.Load(File.OpenRead("log4net.properties"));
            var repo = LogManager.CreateRepository(System.Reflection.Assembly.GetEntryAssembly(),
                       typeof(log4net.Repository.Hierarchy.Hierarchy));
            XmlConfigurator.Configure(repo, log4netConfig["log4net"]);

            XmlDocument xmlConfig = new XmlDocument();
            //Здесь читаются базовые настройки конфигурации обучения для NEAT пакета - тип активационной функции, размер популяции и т.д.
            xmlConfig.Load("Game.config");
            //Инициализация класса, который и будет проводить обучения
            //neuroSolver - объект, который инкапсулирует в себе логику по работе с сервером
            gameExperiment.Initialize("Game experiment", xmlConfig.DocumentElement, neuroSolver);

            _genomeFactory = gameExperiment.CreateGenomeFactory();

            _genomeList = _genomeFactory.CreateGenomeList(150, 0);

            // Создаём эволюционный алгоритм для обучения и привязываем событие для вывода результатов обучения
            _evolutionAlgo = gameExperiment.CreateEvolutionAlgorithm(_genomeFactory, _genomeList);
            _evolutionAlgo.UpdateEvent += new EventHandler(ea_UpdateEvent);

            // Запускаем работу алгоритма обучения
            _evolutionAlgo.StartContinue();
        }

        static void ea_UpdateEvent(object sender, EventArgs e)
        {
            Console.WriteLine(string.Format("gen={0:N0} bestFitness={1:N6}", _evolutionAlgo.CurrentGeneration, _evolutionAlgo.Statistics._maxFitness));
        }
    }
}
