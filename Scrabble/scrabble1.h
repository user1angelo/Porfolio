#ifndef IS_DEFINED
 #define IS_DEFINED (1)
 #define MAX_ELEMENTS 130
 #define MAX_RECORDS 10
 #define WORD_MIN 4
 #define WORD_MAX 12
 #define WIN_WORDS 11
 #define WIN_POINTS 88
 typedef struct PlayerRecord {
 char username[20];
 char longestWord[10]; // longest word formed
 char mostScoringWord[10]; // most scoring word formed
 int nGames; // number of games
 int averageScore; // average score
 } PlayerRecord;
 typedef struct Player {
 int index; // player number
 char username[20]; // alias of the player
 char tileRack[7]; // contains the tiles the player can use
 int score; // player score
 int wordsCast; // number of words applied on the board
 PlayerRecord *record;
 } Player;
 typedef struct TileBag {
 char tiles[28]; // the usable characters or tiles
 int quantity[28]; // stores the quantity of each tile in the bag
 int points[28]; // stores the point equivalent of each tile
 } TileBag;
 typedef struct Scrabble{
 char Board[MAX_ELEMENTS]; // the state of the board
 char PrevBoard[MAX_ELEMENTS]; // the preserved state of the board before the current turn
 struct TileBag tbag; // the bag of tiles
 int noOfPlayers; // number of players
 int nPlayers; // number of players
 int turns; // number of turns
 int words; // number of words cast
 PlayerRecord records[MAX_RECORDS];
 int nRecords;
 }Scrabble;
 
 // unsorted
 // 
 int isLetValid(char userinput, Player *p);
 void RetrieveTile(int index, Scrabble *g);
 int isVacant(int index, Scrabble *g);
 int isBorder(int index);
 void isValidScrabble(Scrabble *g, Player *p);
 void isValidBoardInput(int idx, char player_word[], Scrabble *g, Player *p);
 void CharInput(char player_word[], Scrabble *g, Player *p);
 void InputWord(Scrabble *g, Player *p);
 
 void drawFirstSevenTiles(TileBag *tbag, Player *p);
 Player * setCurrentPlayer(int index, Player *p1, Player *p2, Player *p3, Player
*p4);
 int getTileIndexFromBag(Scrabble *g, char tile);
 void drawTile(Scrabble *g, Player *p, char tile, int rackIndex);
 void replenishRack(Scrabble *g, Player *p);
 void removeTileFromRack(char tile, Player *p);
 void replaceTiles(Scrabble *g, Player *p);
 int didPlayerWin(Player *p);
 // initializers
 int getNumberOfPlayers();
 void getPlayerNames(Scrabble *g, Player *p1, Player *p2, Player *p3, Player 
*p4);
 void createPlayer(Player *p, int index);
 void initializePlayers(Player *p1, Player *p2, Player *p3, Player *p4);
 void initializeBoard(char Board[]);
 void initializeTileBag(TileBag *tbag);
 void initializeTileRacks(int nPlayers, TileBag *tbag, Player *p1, Player *p2, 
Player *p3, Player *p4);
 void initializeGame(Scrabble *g, TileBag *tbag, Player *p1, Player *p2, Player 
*p3, Player *p4, int persist);
 // display
 void displayMenu();
 void displayBoard(char Board[]);
 void displayTileRack(Player *p);
 void displayScoreboard(int nPlayers, Player *p1, Player *p2, Player *p3, Player
*p4);
 void displayTilesOnBag(TileBag *tbag);
 // utils
 void selectOption(Scrabble *g, Player *p, int *option);
 int isWordValid(char* inputWord);
 void copyIntegerArray(int dest[], int src[], int length);
 int generateRandomNumber();
 void getFreqDist(char *word, char *letters, int *frequency);
 PlayerRecord createPlayerRecord(Player *p);
 // scoring
 int getTileScoreEquivalent(char tile, Scrabble *g);
 void updateScore(char tile, Player *p, Scrabble *g);
#endif
