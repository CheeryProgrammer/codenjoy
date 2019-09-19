import neat
import jnius_config
jnius_config.set_classpath('.', './*')
from jnius import autoclass
import os
import numpy as np

GameEngine = autoclass('com.codenjoy.dojo.treasurehunt.services.GameEngineWrapper')

def create_game():
    game = GameEngine()
    game.initNewGame()
    return game

def play_game(net, game):
    score = game.getScore()
    stuck_count = 0

    while game.isAlive() and stuck_count < 100:
        inputs = np.array(game.getBoard()).flatten()
        inputs = convertBoardToNumbers(inputs)

        result = net.activate(inputs)
        direction = get_direction(result)
        game.go(direction)
        
        if game.isAlive():
            new_score = game.getScore()
            if new_score == score:
                stuck_count += 1

            score = new_score

    return float(score)

def convertBoardToNumbers(inputs):
    response = [0]*len(inputs[0])
    for i, inputSymbol in enumerate(inputs[0]):
        if inputSymbol == 'R' or inputSymbol == '☼':
            response[i] = -10
        if inputSymbol == '$':
            response[i] = 3
        if inputSymbol == 'B':
            response[i] = 5
        if inputSymbol == 'S':
            response[i] = 1
        if inputSymbol == ' ':
            response[i] = 0
        if inputSymbol == 'v' or inputSymbol == '>' or inputSymbol == '<' or inputSymbol == '^':
            response[i] = -5
    return response

def get_direction(prediction):
    max_index = np.argmax(prediction)
    if max_index == 0: return "up"
    if max_index == 1: return "right"
    if max_index == 2: return "down"
    return "left"

def update_genomes(genomes, config):
    for _, genome in genomes:
        game = create_game()
        net = neat.nn.FeedForwardNetwork.create(genome, config)
        result = play_game(net, game)
        genome.fitness = result
        
local_dir = os.path.dirname(__file__)
config_file = os.path.join(local_dir, 'config') # use config file in current directory

# Load configuration.
config = neat.Config(neat.DefaultGenome, neat.DefaultReproduction, neat.DefaultSpeciesSet, neat.DefaultStagnation, config_file)

# Create the population, which is the top-level object for a NEAT run.
p = neat.Population(config)

# From checkpoint
#p = neat.Checkpointer.restore_checkpoint('neat-checkpoint-30595')

# Add a stdout reporter to show progress in the terminal.
p.add_reporter(neat.StdOutReporter(True))
stats = neat.StatisticsReporter()
p.add_reporter(stats)
p.add_reporter(neat.Checkpointer(100))

# Run for up to 10000 generations.
winner = p.run(update_genomes, 10000)
# Display the winning genome.
print('\nBest genome:\n{!s}'.format(winner))

# Create winner network and play the game
winner_net = neat.nn.FeedForwardNetwork.create(winner, config)
best_result = play_game(winner_net, create_game())

print('\nBest result: {}'.format(best_result))
