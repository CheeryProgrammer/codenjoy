namespace neat_net_client
{
    public class NeuroSolver : AbstractSolver
    {
        public NeuroSolver(string serverUrl) : base(serverUrl)
        {

        }

        protected internal override string Get(Board gameBoard)
        {
            return "DOWN";
        }

        public NeuroSolver CreateNewSolver()
        {
            return new NeuroSolver(ServerUrl);
        }
    }
}
