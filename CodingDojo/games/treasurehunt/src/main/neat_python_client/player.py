import json
import neat
import os
import numpy as np
import jnius_config
jnius_config.set_classpath('..', '../*')
from jnius import autoclass

GameEngine = autoclass('com.codenjoy.dojo.treasurehunt.services.GameEngineWrapper')

class Player:
    def __init__(self):
        self.isGameOver = False
        self.board = []
        self.net = self.get_net()

    def get_net(self):
        local_dir = os.path.dirname(__file__)
        config_file = os.path.join(local_dir, '../config')

        config = neat.Config(neat.DefaultGenome, neat.DefaultReproduction, neat.DefaultSpeciesSet, neat.DefaultStagnation, config_file)
        p = neat.Checkpointer.restore_checkpoint('../neat-checkpoint-10')
        winner = p.run(Player.update_genomes, 1)
        return neat.nn.FeedForwardNetwork.create(winner, config)

    @staticmethod
    def update_genomes(genomes, config):
        for _, genome in genomes:
            game = Player.create_game()
            net = neat.nn.FeedForwardNetwork.create(genome, config)
            result = Player.play_game(net, game)
            genome.fitness = result

    @staticmethod
    def create_game():
        game = GameEngine()
        game.initNewGame()
        return game

    @staticmethod
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

    @staticmethod
    def get_direction(prediction):
        max_index = np.argmax(prediction)
        if max_index == 0: return "up"
        if max_index == 1: return "right"
        if max_index == 2: return "down"
        return "left"

    @staticmethod
    def play_game(net, game):
        score = game.getScore()
        stuck_count = 0

        while game.isAlive() and stuck_count < 100:
            inputs = np.array(game.getBoard()).flatten()
            inputs = Player.convertBoardToNumbers(inputs)

            result = net.activate(inputs)
            direction = Player.get_direction(result)
            game.go(direction)
            
            if game.isAlive():
                new_score = game.getScore()
                if new_score == score:
                    stuck_count += 1

                score = new_score

        return float(score)

    @staticmethod
    def get_score(data):
        return 10 * np.max(data) + np.sum(data)

    def process_data(self, data):
        # process data here
        self.isGameOver, self.board = self.parse_data(data)

    def make_step(self):
        # prepare response here
        #return 'ACT'
        inputs = np.array(self.board).flatten()
        inputs = np.log2(inputs, where=(inputs >= 2))

        result = self.net.activate(inputs)
        direction = Player.get_direction(result)
        print(direction)
        return direction

    def parse_data(self, data):
        decoded = data.data.decode("utf-8")
        json_string = decoded.split('=', 1)[-1]
        parsed = json.loads(json_string)
        isGameOver = parsed["isGameOver"]
        board = self.get_board(parsed["field"])
        return isGameOver, board

    def get_board(self, string_board):
        dictionary = {
            " ": 0,
            "2": 2,
            "4": 4,
            "8": 8,
            "A": 16,
            "B": 32,
            "C": 64,
            "D": 128,
            "E": 256,
            "F": 512,
            "G": 1024,
            "H": 2048,
            "I": 4096,
            "J": 8192,
            "K": 16384,
            "L": 32768,
            "M": 65536,
            "N": 131072,
            "O": 262144,
            "P": 524288,
            "Q": 1048576,
            "R": 2097152,
            "S": 4194304,
        }

        result = [[], [], [], [], []]
        for i, c in enumerate(string_board):
            row = i // 5
            result[row].append(dictionary[c])

        return result