from player import Player
from CodenjoyConnection import CodenjoyConnection

try:
    name = 'vb180x6lc2sf62cfp112'     # player id (which you can get from board page url after registration)
    code = '8843976861658600249'      # player code (-- " --")
    port = '8080'                       # game port
    host = 'epruryaw0537.moscow.epam.com'             # game host
    game_url = 'codenjoy-contest/ws?' # game url

    url = "ws://{0}:{1}/{2}user={3}&code={4}".format(host, port, game_url, name, code)
    player = Player()
    ws = CodenjoyConnection(url, player)
    ws.connect()
    ws.run_forever()
except KeyboardInterrupt:
    ws.close()