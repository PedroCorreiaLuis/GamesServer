## Game Server

Game server built using Play framework and Actors. This server is composed by the following services.  

##### App Manager
- Account Manager
- Game Manager
- Game Engine
- Player Service

##### Account Manager
- Player balance
- Player transactions

##### Game Manager
- Creating game sessions
- Pairing players

##### Game Engine
- Controls the actions inside the games
    - Fold
    - Play

##### TODO
- Game Controller implementation
- Testing (a lot)

##### Known limitations
- The same player can't do transactions in parallel

##### Future Changes
- Adding balance validation
- Add authentication
- Add persistence (DB)