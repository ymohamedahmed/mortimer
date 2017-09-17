# Mortimer
Mortimer is a chess playing program (commonly referred to as a chess engine) which I wrote as a part of my Computer Science A-Level Coursework. However, this project was primarily completed as a means of pursuing my wider interest in Artificial Intelligence. The full write up can be found [here](https://drive.google.com/open?id=0B-q1OPqj3iCmN0ZXcTJlUG1lYjA). A video outlining some of the features can be found [here](https://www.youtube.com/watch?v=LOsC6-85fvk).

![](https://github.com/ymohamedahmed/mortimer/blob/bitboard/res/gif/mortimer.gif). 
## Basic Description
Mortimer uses bitboards as its internal board representation this leads to it having very fast move generation speeds since binary number manipulation is very fast on modern computers. As a result, when searching the game tree it can search to relatively high depths (for an amateur project). This helps to make up somewhat for the simple evaluation function. In future, to improve the strength of the AI, the evaluation function would have to consider additional factors such as king safety etc.

## Features
* Alpha-beta pruning
* Perft testing
* Magic bitboards for sliding piece move generation
* Material evaluation
* Positional evaluation
* Negamax
* MTD-f
* Transposition tables
* Zobrist hashing
* PGN
* Saving and loading games
* Changing move speed of the AI
* Different board themes
* Loading and exporting FEN
* Undoing moves
* Playing against another human locally

## Resources Used
* [Tables and algorithms for magic bitboards](http://www.rivalchess.com/magic-bitboards/)
* Chess Programming WikiSpace for understanding the algorithms used in chess engines
* [Positional evaluation](https://chessprogramming.wikispaces.com/Simplified+evaluation+function)

## Usage
A jar is included and can be downloaded [here](https://github.com/ymohamedahmed/mortimer/raw/bitboard/morty.jar). Alternatively the repo can be cloned and then the code executed as follows (requires JUnit to be compiled):
```
git clone https://github.com/ymohamedahmed/mortimer.git
cd mortimer/src/
javac */*.java
java core.main
```

