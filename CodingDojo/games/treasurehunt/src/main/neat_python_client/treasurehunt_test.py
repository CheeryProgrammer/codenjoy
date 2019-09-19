import jnius_config
jnius_config.set_classpath('.', './*')
import math
from jnius import autoclass
from os import system
import msvcrt
import numpy as np

def makeStep(engine, direction):
    engine.go(direction)
    if engine.isAlive():
        board = engine.getBoard()
        printBoard(board)

def printBoard(board):
    length = int(math.sqrt(len(board)))
    for x in range(length):
        for y in range(length):
            print(board[x * length + y], end='')
        print()

def inputToCommand(x):
    return {
        b'w': "up",
        b'a': "left",
        b's': "down",
        b'd': "right",
    }[x]

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

board = '';
try:
    GameEngine = autoclass('com.codenjoy.dojo.treasurehunt.services.GameEngineWrapper')
    engine = GameEngine()
    engine.initNewGame()
    input = b'w';
    while input == b'w' or input == b'a' or input == b's' or input == b'd':
        makeStep(engine, inputToCommand(input))
        print("Is alive: " + str(engine.isAlive()))
        if not engine.isAlive():
            engine.initNewGame()
        print("Score: " + str(engine.getScore()))
        inputs = np.array(engine.getBoard()).flatten()
        inputs = convertBoardToNumbers(inputs)
        print(inputs)
        input = msvcrt.getch()
        system('cls')
except Exception as e:
    print(str(e));
