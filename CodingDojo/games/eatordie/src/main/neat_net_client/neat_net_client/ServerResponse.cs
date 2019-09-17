using Newtonsoft.Json;

namespace neat_net_client
{
    public class ServerResponse
    {
        public ServerResponse()
        {

        }

        [JsonProperty("score")]
        public int Score { get; set; }

        [JsonProperty("isGameOver")]
        public bool IsGameOver { get; set; }

        [JsonProperty("field")]
        public string Field { get; set; }

    }
}
