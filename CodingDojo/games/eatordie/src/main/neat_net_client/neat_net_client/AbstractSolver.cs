using System;
using System.Web;
using System.Linq;
using System.Threading;
using WebSocketSharp;

namespace neat_net_client
{
    public abstract class AbstractSolver
    {
        private const string ResponsePrefix = "board=";
        private Board _board;
        private WebSocket _socket;
        private AutoResetEvent _semaphore;

        /// <summary>
        /// constructor
        /// </summary>
        /// <param name="server">server http address including email and code</param>
        public AbstractSolver(string server)
        {
            // Console.OutputEncoding = Encoding.UTF8;
            _semaphore = new AutoResetEvent(false);
            ServerUrl = server;
        }

        public string ServerUrl { get; private set; }


        /// <summary>
        /// Set this property to true to finish playing
        /// </summary>
        public bool ShouldExit { get; protected set; }

        public void Play()
        {
            string url = GetWebSocketUrl(this.ServerUrl);

            _socket = new WebSocket(url);

            _socket.OnMessage += Socket_OnMessage;
            _socket.Connect();

        }


        private void Socket_OnMessage(object sender, MessageEventArgs e)
        {
            if (!ShouldExit)
            {
                var response = e.Data;

                if (!response.StartsWith(ResponsePrefix))
                {
                    Console.WriteLine("Something strange is happening on the server... Response:\n{0}", response);
                    ShouldExit = true;
                }
                else
                {
                    var boardString = response.Substring(ResponsePrefix.Length);
                    _board = new Board(boardString);

                    _semaphore.Set();
                }
            }
        }

        public Board CurrentBoard
        {
            get
            {
                _semaphore.WaitOne();
                return _board;
            }
        }

        public void SendAction(string action)
        {
            _socket.Send(action);
        }

        public static string GetWebSocketUrl(string serverUrl)
        {
            Uri uri = new Uri(serverUrl);

            var server = $"{uri.Host}:{uri.Port}";
            var userName = uri.Segments.Last();
            var code = HttpUtility.ParseQueryString(uri.Query).Get("code");

            return GetWebSocketUrl(userName, code, server);
        }

        private static string GetWebSocketUrl(string userName, string code, string server)
        {
            return string.Format("ws://{0}/codenjoy-contest/ws?user=vrvm40aetmndbdzyl7uo&code=1",
                            server,
                            Uri.EscapeDataString(userName),
                            code);
        }

        protected internal abstract string Get(Board gameBoard);

        /// <summary>
        /// Starts client shutdown.
        /// </summary>
        public void InitiateExit()
        {
            ShouldExit = true;
        }

        ~AbstractSolver()
        {
            if (_socket.ReadyState == WebSocketState.Open)
                _socket.Close();
        }
    }
}
