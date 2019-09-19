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
        static GameExperiment _gameExperiment;

        static void Main(string[] args)
        {
            Console.SetWindowSize(Console.LargestWindowWidth - 3, Console.LargestWindowHeight - 3);
            //Задавайте здесь url сервера, который будете использовать для обучения. По умолчанию стоит локальный сервер
            //так как на боевом сервере есть задержка минимум в 500 мс, что затормозит обучение сильно
            string serverUrl = "http://localhost:8080/";
            var neuroSolver = new NeuroSolver(serverUrl);
            //Здесь активируется соединение по сокетам с выбранным сервером
            neuroSolver.Play();

            _gameExperiment = new GameExperiment();

            XmlDocument log4netConfig = new XmlDocument();
            log4netConfig.Load(File.OpenRead("log4net.properties"));
            var repo = LogManager.CreateRepository(System.Reflection.Assembly.GetEntryAssembly(),
                       typeof(log4net.Repository.Hierarchy.Hierarchy));
            XmlConfigurator.Configure(repo, log4netConfig["log4net"]);

            XmlDocument xmlConfig = new XmlDocument();
            //Здесь читаются базовые настройки конфигурации обучения для NEAT пакета - тип активационной функции, размер популяции и т.д.
            xmlConfig.Load("Game.config");
            //Инициализация класса, который и будет проводить обучения. Это просто заглушка!!!
            //neuroSolver - объект, который инкапсулирует в себе логику по работе с сервером
            //Там реализована просто совсем базовая логика для обучения, чтобы вам было проще разобраться.
            _gameExperiment.Initialize("Game experiment", xmlConfig.DocumentElement, neuroSolver);

            if (args.Length == 0)
            {
                //Здесь происходит конфигурация обучения.                
                _genomeFactory = _gameExperiment.CreateGenomeFactory();
                _genomeList = _genomeFactory.CreateGenomeList(150, 0);
            }
            else
            {
                // Ожидаем только один параметр на вход
                if (args.Length > 1)
                {
                    throw new ArgumentException("Error. Wrong input arguments.");
                }

                // Open and load population XML file.
                using (XmlReader xr = XmlReader.Create(args[0]))
                {
                    _genomeList = _gameExperiment.LoadPopulation(xr);
                }
                _genomeFactory = _genomeList[0].GenomeFactory;
                Console.WriteLine($"Loaded [{_genomeList.Count}] genomes.");
            }
            // Создаём эволюционный алгоритм для обучения и привязываем событие для вывода результатов обучения
            _evolutionAlgo = _gameExperiment.CreateEvolutionAlgorithm(_genomeFactory, _genomeList);
            _evolutionAlgo.UpdateEvent += new EventHandler(ea_UpdateEvent);
            // Запускаем работу алгоритма обучения
            _evolutionAlgo.StartContinue();

            // Hit return to quit.
            Console.ReadLine();
        }

        static void ea_UpdateEvent(object sender, EventArgs e)
        {
            Console.WriteLine(string.Format("gen={0:N0} bestFitness={1:N6}", _evolutionAlgo.CurrentGeneration, _evolutionAlgo.Statistics._maxFitness));
            //Сохраняем в файл только каждую 40-ю версию
            if (_evolutionAlgo.CurrentGeneration % 40 == 0 && _evolutionAlgo.CurrentGeneration > 0)
            {
                XmlWriterSettings xwSettings = new XmlWriterSettings();
                xwSettings.Indent = true;
                using (XmlWriter xw = XmlWriter.Create("genomeNumber " + _evolutionAlgo.CurrentGeneration + ".xml", xwSettings))
                {
                    _gameExperiment.SavePopulation(xw, _genomeList);
                }
            }
        }
    }
}
