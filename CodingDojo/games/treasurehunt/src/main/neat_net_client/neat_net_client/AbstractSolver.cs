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
using System.Web;
using System.Linq;
using System.Threading;
using WebSocketSharp;
using Newtonsoft.Json;

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
                    _board = JsonConvert.DeserializeObject<Board>(boardString);

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
            return string.Format("ws://{0}/codenjoy-contest/ws?user={1}&code={2}",
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
