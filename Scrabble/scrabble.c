/*
Description: This program aims to capture a simplified version of 
the Word Game Scrabble.
Programmed by: <Juan Pablo C. Bernardo> <S14> <Angelo Yanto 
Quinones> <S14>
Last modified: <12/5/2022>
Version: <4>
*/
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>
#include "scrabble1.h"
typedef char String[12];
/*****************************************************************
**********************************/
void printChoice()
{
 printf("\n**************\n%7s", "SCRABBLE CHOICE");
 printf("\n**************\n");
 printf("1 - Input\n"); // 1 time replacement
 printf("2 - Retrieve\n");
 printf("3 - Submit\n");
 // input skip option?
} // every time a player is done menu will be shown
void displayMenu()
{
 printf("\n**************\n%7s", "SCRABBLE MENU");
 printf("\n**************\n");
 printf("1 - Start Game\n");
 printf("2 - Input\n"); // 1 time replacement
 printf("3 - Skip\n");
 printf("4 - New Game\n");
 printf("5 - End Game\n");
} // every time a player is done menu will be shown
/**
* This function displays the entire contents of the scrabble 
board.
* Precondition: Board[] must be a pointer referring to 
Scrabble.Board[]
* @param Board pointer referring to the variable that stores the 
Scrabble Board's current state
*/
void displayBoard(char Board[])
{
 int i = 0; // counter for the array index of Board[]
 int j; // counter for the row number
 int k; // variable to track the index of each cell
 printf("\n 1: 2: 3: 4: 5: 6: 7: 8: 9: 10: 11:\n");
 for (j = 1; j < 12; j++)
 {
 printf("%d: ", j); // print the row number
 if (j < 10)
 {
 printf(" ");
 }
 for (k = 0; k < 11; k++)
 {
 printf("[%c] ", Board[i]);
 i++;
 if (k == 10)
 {
 printf("\n");
 }
 }
 }
}
/**
* This function displays the current tiles on a player's tile 
rack.
* Precondition: p must be a pointer referring to a player 
instance
* @param p pointer referring to a player instance
*/
void displayTileRack(Player *p)
{
 int i;
 printf("=====TILE RACK (PLAYER %d)=====\n%*c", p->index, 8, ' ');
 printf("[");
 for (i = 0; i < strlen(p->tileRack); i++)
 {
 printf("%c", p->tileRack[i]);
 if (i < strlen(p->tileRack) - 1)
 {
 printf(",");
 }
 }
 printf("]");
 printf("\n==============================\n");
}
void displayScoreboard(int nPlayers, Player *p1, Player *p2, 
Player *p3, Player *p4)
{
 int i;
 printf("\n==========SCOREBOARD==========");
 for (i = 1; i <= nPlayers; i++)
 {
 if (i == 1)
 {
 printf("\n1. %s (%d pts)", p1->username, p1->score);
 }
 if (i == 2)
 {
 printf("\n2. %s (%d pts)", p2->username, p2->score);
 }
 if (i == 3)
 {
 printf("\n3. %s (%d pts)", p3->username, p3->score);
 }
 if (i == 4)
 {
 printf("\n4. %s (%d pts)", p4->username, p4->score);
 }
 }
 printf("\n==============================\n");
}
/**
* This function displays all the remaining tiles on the bag and 
their
* respective quantity.
* Precondition: tbag must be a pointer referring to 
Scrabble.TileBag[]
* @param tbag pointer referring to the variable which stores the 
current tilebag state
*/
void displayTilesOnBag(TileBag *tbag)
{
 int i;
 printf("============TILE BAG============\n");
 printf("FORMAT: [tile, quantity]\n\n");
 for (i = 0; i < 27; i++)
 {
 printf("[%c, %d]", tbag->tiles[i], tbag->quantity[i]);
 // if there are already 9 elements printed, display the next elements on the next line
 if ((i + 1) % 9 == 0)
 {
 printf("\n");
 }
 }
 printf("==============================\n");
}
/***********************************************INITIAL***********
*****************************************/
/**
* Function which gets and returns the number of players at the 
start of the game.
* Precondition: noOfPlayers must only be within the 2-4 interval.
* @param
* @return number of players
*/
int getNumberOfPlayers()
{
 int noOfPlayers = 0;
 while (noOfPlayers > 4 || noOfPlayers < 2)
 {
 printf("Enter No. of Players (2-4): ");
 scanf("%d", &noOfPlayers);
 }
 return noOfPlayers;
}
/**
* Function which gets data of returning player.
* Precondition: called when game loop has started
* @param recordIndex index of returning player
* @param g pointer referring to scrabble data
* @param p pointer referring to player data
*/
void restorePlayerData(Scrabble *g, Player *p, int recordIndex)
{
 p->record = &g->records[recordIndex];
 // printing the username from p->record->username, since this will
 // validate if the player's previous record has actually been retrieved
 printf("Restoring data for %s...\n", p->record->username);
}
/**
* Function which saves data of player.
* Precondition: called when game loop has started
* @param g pointer referring to scrabble data
* @param p pointer referring to player data
*/
int isPlayerSaved(Scrabble *g, Player *p)
{
 int index = -1;
 int i;
 for (i = 0; i < g->nRecords; i++)
 {
 if (!strcmp(g->records[i].username, p->username) && index 
< 0)
 {
 index = i;
 }
 }
 return index;
}
/**
* Function which saves data of player.
* Precondition: called when game loop has started
* @param index index of player.
* @param p pointer referring to player data
*/
void createPlayer(Player *p, int index)
{
 char emptyRack[7] = {};
 p->index = index;
 p->score = 0;
 p->wordsCast = 0;
 strcpy(p->tileRack, emptyRack);
}
/**
* Function which gets the name of each player.
* Precondition: numberOfPlayers must be an integer within 2-4, 
and p1-p4 must be pointers referring to the
* four possible players in the game.
* @param numberOfPlayers number of players
* @param p1 pointer referring to player 1's data
* @param p2 pointer referring to player 2's data
* @param p3 pointer referring to player 3's data
* @param p4 pointer referring to player 4's data
*/
void getPlayerNames(Scrabble *g, Player *p1, Player *p2, Player 
*p3, Player *p4)
{
 int isSavedIdx, i;
 for (i = 1; i <= g->noOfPlayers; i++)
 {
 char name[12] = "";
 isSavedIdx = -1;
 while (strlen(name) == 0)
 {
 printf("Please enter username (Player %d): ", i);
 scanf("%s", name);
 }
 switch (i)
 {
 case 1:
 strcpy(p1->username, name);
 isSavedIdx = isPlayerSaved(g, p1);
 if (isSavedIdx >= 0)
 {
 restorePlayerData(g, p1, isSavedIdx);
 }
 break;
 case 2:
 strcpy(p2->username, name);
 isSavedIdx = isPlayerSaved(g, p2);
 if (isSavedIdx >= 0)
 {
 restorePlayerData(g, p2, isSavedIdx);
 }
 break;
 case 3:
 strcpy(p3->username, name);
 isSavedIdx = isPlayerSaved(g, p3);
 if (isSavedIdx >= 0)
 {
 restorePlayerData(g, p3, isSavedIdx);
 }
 break;
 case 4:
 strcpy(p4->username, name);
 isSavedIdx = isPlayerSaved(g, p4);
 if (isSavedIdx >= 0)
 {
 restorePlayerData(g, p4, isSavedIdx);
 }
 break;
 }
 }
}
/**
* This function initializes the contents of a Player instance 
(i.e. player index, tile rack).
* Precondition: p1, p2, p3, and p4 are all pointers to a Player 
instance struct
* @param p1 pointer referencing to player one
* @param p2 pointer referencing to player two
* @param p3 pointer referencing to player three
* @param p4 pointer referencing to player four
* @return
*/
void initializePlayers(Player *p1, Player *p2, Player *p3, Player
*p4)
{
 createPlayer(p1, 1);
 createPlayer(p2, 2);
 createPlayer(p3, 3);
 createPlayer(p4, 4);
}
/**
* This function initializes the board by filling up empty cells 
with underscores (_) to
* indicate fillable cells.
* Precondition: Board[] must be a pointer referencing 
Scrabble.Board[]
* @param Board[] pointer which refers to the current board state 
(Scrabble.Board[])
* @return
*/
void initializeBoard(char Board[])
{
 int i;
 for (i = 0; i < MAX_ELEMENTS; i++)
 {
 Board[i] = '_';
 }
}
/**
* This function initializes the contents of the tilebag, which 
will be used throughout the game.
* Precondition: tbag must be a pointer to a TileBag instance, 
which is found in Scrabble.TileBag[]
* @param tbag pointer which refers to the game's tilebag
* @return
*/
void initializeTileBag(TileBag *tbag)
{
 char tiles[28] = {
 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'B', '\0'};
 int quantity[27] = {
 9, 2, 2, 4, 12, 2, 3, 2, 9,
 1, 1, 4, 2, 6, 8, 2, 1, 6,
 4, 6, 4, 2, 2, 1, 2, 1};
 int points[27] = {
 1, 3, 3, 2, 1, 4, 2, 4, 1,
 8, 5, 1, 3, 1, 1, 3, 10, 1,
 1, 1, 1, 4, 4, 8, 4, 10};
 strcpy(tbag->tiles, tiles);
 copyIntegerArray(tbag->quantity, quantity, 27);
 copyIntegerArray(tbag->points, points, 27);
}
/**
* Initializes all the tile racks of each player at the start of 
the game. This will call
* drawFirstSevenTiles() for each player instance, issuing 7 
random tiles for each player in
* accordance to the supply of tiles remaining in the tilebag.
*
* Precondition: tbag, p1, p2, p3, and p4 are pointers
* @param nPlayers the number of players in the game
* @param tbag pointer referring to the game's tilebag
* @param p1 pointer referring to player 1's data
* @param p2 pointer referring to player 2's data
* @param p3 pointer referring to player 3's data
* @param p4 pointer referring to player 4's data
*/
void initializeTileRacks(int nPlayers, TileBag *tbag, Player *p1,
Player *p2, Player *p3, Player *p4)
{
 int i;
 for (i = 1; i <= nPlayers; i++)
 {
 if (i == 1)
 drawFirstSevenTiles(tbag, p1);
 if (i == 2)
 drawFirstSevenTiles(tbag, p2);
 if (i == 3)
 drawFirstSevenTiles(tbag, p3);
 if (i == 4)
 drawFirstSevenTiles(tbag, p4);
 }
}
/**
* Function which initializes the game by setting up initial 
player data, game data, board data, and tile economy.
* Preconditions: all arguments must be pointers of their 
respective instances.
* @param g pointer referring to the game instance
* @param tbag pointer referring to the tile bag (tile econoomy) 
instance
* @param p1 pointer referring to player 1's data
* @param p2 pointer referring to player 2's data
* @param p3 pointer referring to player 3's data
* @param p4 pointer referring to player 4's data
*/
void initializeGame(Scrabble *g, TileBag *tbag, Player *p1, 
Player *p2, Player *p3, Player *p4, int persist)
{
 int lastRecordIdx = 0;
 int i;
 printf("\nLet's Start!\n");
 initializePlayers(p1, p2, p3, p4);
 g->turns = 0;
 g->words = 0;
 // get the number of players who will be playing the game
 g->noOfPlayers = getNumberOfPlayers();
 getPlayerNames(g, p1, p2, p3, p4);
 // initialize the board and the tile bag
 initializeBoard(g->Board);
 initializeTileBag(tbag);
 initializeTileRacks(g->noOfPlayers, tbag, p1, p2, p3, p4);
 if (persist == 0)
 {
 g->nRecords = 0;
 lastRecordIdx = 0;
 }
 else
 {
 lastRecordIdx = g->nRecords - 1;
 }
 for (i = 0; i < g->noOfPlayers; i++)
 {
 if (i == 0)
 {
 g->records[lastRecordIdx + i] = 
createPlayerRecord(p1);
 p1->record = &g->records[lastRecordIdx + i];
 g->nRecords++;
 }
 if (i == 1)
 {
 g->records[lastRecordIdx + i] = 
createPlayerRecord(p2);
 p2->record = &g->records[lastRecordIdx + i];
 g->nRecords++;
 }
 if (i == 2)
 {
 g->records[lastRecordIdx + i] = 
createPlayerRecord(p3);
 p3->record = &g->records[lastRecordIdx + i];
 g->nRecords++;
 }
 if (i == 3)
 {
 g->records[lastRecordIdx + i] = 
createPlayerRecord(p4);
 p4->record = &g->records[lastRecordIdx + i];
 g->nRecords++;
 }
 }
}
/******************************************UTILS******************
***************************************/
/**
* Function which prompts the player to choose an item on the 
menu.
* Preconditions: g, p, and option must be pointers referring to 
the game, player, and the option variable, respectively.
* @param g pointer referring to the game/Scrabble instance
* @param p pointer referring to the target player's data or 
instance
* @param option pointer referring to the option variable
*/
void selectOption(Scrabble *g, Player *p, int *option)
{
 int selected;
 while (selected > 5 || selected < 1)
 {
 displayMenu();
 printf("Select an option (Player %d [%s]): ", p->index, p->username);
 scanf("%d", &selected);
 }
 *option = selected;
}
/**
* This function checks if an input word exists in the wordlist.
* Precondition: inputWord must be a string containing only 
lowercase characters
* @param inputWord the word placed by the user as input
* @return 1 if the word exists in the wordlist, and 0 otherwise.
*/
int isWordValid(char *inputWord)
{
 FILE *fp;
 char word[WORD_MAX];
 int isValid = 0;
 int result;
 // open the file
 fp = fopen("wordlist1.txt", "r");
 // if the wordlist file does not exist, return an error
 if (fp == NULL)
 {
 printf("ERROR: Wordlist not found!\n");
 exit(1);
 }
 else
 {
 // compare each line from the text file against the input word
 while (fgets(word, WORD_MAX, fp) != NULL)
 {
 // since our wordlist contains a newline (\n) at the end of each word,
 // we want to use strncmp() instead of strcmp()
 result = strncmp(word, inputWord, strlen(inputWord));
 if (result == 0)
 {
 isValid = 1;
 }
 }
 }
 // close the file
 fclose(fp);
 return isValid;
}
/**
* Helper function which copies the contents of an integer array 
to another
* integer array BY VALUE.
* Precondition: dest[] and src[] must be integer arrays, and 
length must be determined ahead of
* time (size of the source array)
*
* @param dest[] destination integer array
* @param src[] source destination array
* @param length the length of the source array, which must be 
determined ahead of time
* @return
*/
void copyIntegerArray(int dest[], int src[], int length)
{
 int i;
 for (i = 0; i < length; i++)
 {
 dest[i] = src[i];
 }
}
/**
* Function which generates a random number from 0-26. 0 refers to
'blank' (b), while 1-26 refer
* to letters A-Z.
* @param
* @return a randomly-generated number or seed
*/
int generateRandomNumber()
{
 int num = 0;
 num = rand() % 27;
 return num;
}
PlayerRecord createPlayerRecord(Player *p)
{
 PlayerRecord pr;
 strcpy(pr.username, p->username);
 pr.nGames = 1;
 pr.averageScore = 0;
 return pr;
}
/******************************************SCORING****************
*****************************************/
int getTileScoreEquivalent(char tile, Scrabble *g)
{
 int equivalent = 0;
 int found = 0, i;
 for (i = 0; i < strlen(g->tbag.tiles); i++)
 {
 if (g->tbag.tiles[i] == tile && found == 0)
 {
 found = 1;
 equivalent = g->tbag.points[i];
 }
 }
 return equivalent;
}
void updateScore(char tile, Player *p, Scrabble *g)
{
 int score;
 score = getTileScoreEquivalent(tile, g);
 p->score += score;
}
/*******************************************MAIN******************
**************************************/
/**
* This function draws the 7 initial tiles that a specific player 
can use at the start of the game.
* Precondition: tbag and p must be pointers
* @param tbag pointer which refers to the game's tilebag
* @param p pointer which refers to the target player instance
*/
void drawFirstSevenTiles(TileBag *tbag, Player *p)
{
 int i;
 int seed;
 int valid;
 for (i = 0; i < 7; i++)
 {
 valid = 0;
 // generate a random number from 0-26 and make sure the random number
 // maps to a tile whose quantity is still >0 (greater than0)
 while (valid == 0)
 {
 seed = generateRandomNumber();
 if (tbag->quantity[seed] > 0)
 {
 valid = 1;
 }
 }
 // once a valid seed and tile has been found, transfer thetile
 // to the player's tile rack
 p->tileRack[i] = tbag->tiles[seed];
 // update the quantity of the selected tile in the bag
 tbag->quantity[seed] = tbag->quantity[seed] - 1;
 }
}
/**
* Function for allowing the program to switch between players for
each turn.
* Preconditions: index must be a number between 1-4 and p1, p2, 
p3, and p4 are instances of the Player struct.
* @param index the number of the target player whose pointer will
be returned
* @param p1 pointer referring to player 1's data
* @param p2 pointer referring to player 2's data
* @param p3 pointer referring to player 3's data
* @param p4 pointer referring to player 4's data
* @return pointer referring to the target player's data
*/
Player *setCurrentPlayer(int index, Player *p1, Player *p2, 
Player *p3, Player *p4)
{
 switch (index)
 {
 case 1:
 return p1;
 case 2:
 return p2;
 case 3:
 return p3;
 case 4:
 return p4;
 default:
 return p1;
 }
}
int didPlayerWin(Player *p)
{
 int didWin = 0;
 if (p->score >= WIN_POINTS || p->wordsCast >= WIN_WORDS)
 {
 didWin = 1;
 printf("====================================\n");
 printf("%12sPLAYER %d WINS%12s\n", "", p->index, "");
 printf("====================================\n");
 }
 return didWin;
}
/**
* Function gets tile index from quantity tile array
* Preconditions:
* @param g pointer referring to Scrabble struct data
* @param tile pointer referring to player char
*/
int getTileIndexFromBag(Scrabble *g, char tile)
{
 int bagIndex = -1;
 int i;
 for (i = 0; i < strlen(g->tbag.tiles); i++)
 {
 if (g->tbag.tiles[i] == tile)
 {
 bagIndex = i;
 }
 }
 return bagIndex;
}
/**
* Function gets tile index from bag and subtracts quantity of 
letters from tbag.
* Preconditions:
* @param g pointer referring to Scrabble struct data
* @param tile pointer referring to player char
*/
void subTileIndexFromBag(Scrabble *g, char tile)
{
 int bagIndex = -1;
 int i;
 for (i = 0; i < strlen(g->tbag.tiles); i++)
 {
 if (g->tbag.tiles[i] == tile)
 {
 bagIndex = i;
 }
 }
 g->tbag.tiles[bagIndex] -= 1;
}
/**
* Function creates and designates a random tile.
* Preconditions:
* @param g pointer referring to Scrabble struct data
* @param p pointer referring to player struct data
* @param tile pointer referring to player char
* @param rackIndex pointer referring to player index of inputted 
char on the rack.
*/
void drawTile(Scrabble *g, Player *p, char tile, int rackIndex)
{
 int seed;
 int isDrawValid = 0;
 int bagIndex;
 while (isDrawValid == 0)
 {
 seed = generateRandomNumber();
 if (g->tbag.quantity[seed] > 0)
 {
 isDrawValid = 1;
 }
 }
 // replace the tile on the rack with the drawn tile from the bag
 p->tileRack[rackIndex] = g->tbag.tiles[seed];
 // reduce the quantity of the tile that has been drawn from the bag
 g->tbag.quantity[seed] = g->tbag.quantity[seed] - 1;
 // increase the quantity of the tile that has been returned tothe bag,
 // assuming that rackIndex contained a tile
 if (tile != '_')
 {
 bagIndex = getTileIndexFromBag(g, tile);
 g->tbag.quantity[bagIndex] = g->tbag.quantity[bagIndex] + 
1;
 }
}
/**
* Function to replace tiles with more random tiles generated by 
the program
* Preconditions:
* @param g pointer referring to Scrabble struct data
* @param p pointer referring to player struct data
*/
void replenishRack(Scrabble *g, Player *p)
{
 int i;
 // count the number of emptied slots on the player's rack
 for (i = 0; i < strlen(p->tileRack); i++)
 {
 if (p->tileRack[i] == '_')
 {
 drawTile(g, p, p->tileRack[i], i);
 }
 }
}
/**
* Function to replace tiles within the players rack
* Preconditions: must be within player tile rack
* @param g pointer referring to Scrabble struct data
* @param p pointer referring to player struct data
*/
void replaceTiles(Scrabble *g, Player *p)
{
 struct TileBag *ptr_tbag = &g->tbag;
 int timesDraw = 0;
 char tile;
 int rackIndex = 0;
 int bagIndex = 0;
 int isTileValid;
 int isDrawValid;
 int previouslyReplaced;
 // a variable for storing the indices of tiles that have been .replaced from the rack
 int replaced[7] = {-1, -1, -1, -1, -1, -1, -1};
 // start drawing a tile from the tile bag.
 int seed;
 int i, j, k, n;
 displayTileRack(p);
 // specify how many times a tile will be drawn from the bag
 while (timesDraw <= 0 || timesDraw > 7)
 {
 printf("How many times do you want to draw a tile? (1-7): ");
 scanf("%d", &timesDraw);
 }
 for (i = 0; i < timesDraw; i++)
 {
 isTileValid = 0;
 isDrawValid = 0;
 // variable(s) for distinguishing the error
 previouslyReplaced = 0;
 // re-display the tile rack
 displayTilesOnBag(ptr_tbag);
 displayTileRack(p);
 // prompt the player to select a tile from the rack
 while (isTileValid == 0)
 {
 printf("Please specify a tile from the rack you want to replace (#%d): ", i + 1);
 scanf(" %c", &tile);
 // verify that the tile can be found in the player's rack
 for (j = 0; j < strlen(p->tileRack); j++)
 {
 if (p->tileRack[j] == tile)
 {
 rackIndex = j;
 isTileValid = 1;
 }
 }
 // make sure the player is not replacing a tile that has been replaced during the current player's turn
 for (n = 0; n < 7; n++)
 {
 if (replaced[n] >= 0)
 {
 if (replaced[n] == rackIndex)
 {
 previouslyReplaced = 1;
 isTileValid = 0;
 }
 }
 }
 if (isTileValid == 0)
 {
 if (previouslyReplaced == 1)
 {
 printf("ERROR: You are replacing the tile you have just drawn from the bag during this turn. Please try again.\n");
 }
 else
 {
 printf("ERROR: The specified tile is not in your tile rack. Try again.\n");
 }
 }
 }
 // find the index of the selected tile on the game's tile bag (tile economy)
 for (k = 0; k < strlen(g->tbag.tiles); k++)
 {
 if (g->tbag.tiles[k] == tile)
 {
 bagIndex = k;
 }
 }
 // if it turns out that the randomly-selected tile's quantity is 0, repeat the selection process.
 while (isDrawValid == 0)
 {
 seed = generateRandomNumber();
 if (g->tbag.quantity[seed] > 0)
 {
 isDrawValid = 1;
 }
 }
 // replace the tile on the rack with the randomly-selected tile from the bag
 p->tileRack[rackIndex] = g->tbag.tiles[seed];
 // manage the saved tile quantity of the traded tiles
 g->tbag.quantity[seed] = g->tbag.quantity[seed] - 1;
 g->tbag.quantity[bagIndex] = g->tbag.quantity[bagIndex] + 
1;
 // record the index of the tile that has been added in theplayer's tile rack
 replaced[i] = rackIndex;
 }
 displayTileRack(p);
}
/**
* Function to check if letter is within the player rack
* Preconditions: must be within player tile rack
* @param userinput char to be checked if within random tiles
* @param p pointer referring to player struct data
* @return falg that signifies presence of letter.
*/
int isLetValid(char userinput, Player *p)
{
 int j;
 int flag = 0;
 j = 0;
 while (j < strlen(p->tileRack))
 {
 if (p->tileRack[j] == userinput)
 {
 flag = 1;
 }
 else
 {
 flag = 0; // 0 if not in list
 }
 j++;
 }
 return flag;
}
/**
* Function to remove a char from board.
* Preconditions: must be a char on the board.
* @param tile char to be removed y player
* @param g pointer referring to player struct data
* @return pointer referring to the target player's data
*/
void RetrieveTile(int index, Scrabble *g)
{
 int p;
 for (p = 0; p < MAX_ELEMENTS; p++)
 {
 if (p == index)
 {
 g->Board[p] = '_';
 }
 }
 displayBoard(g->Board);
}
/**
* Function checks if index contains a char
* Preconditions:
* @param idx index being passed by the user
* @param g pointer referring to scrabble struct data
* @return pointer referring to the target player's data
*/
int isVacant(int index, Scrabble *g)
{
 int clearFlag = 0;
 int p;
 p = 0;
 while (p < MAX_ELEMENTS)
 {
 if (p == index)
 {
 if (g->Board[p] == '_')
 {
 clearFlag = 1; // clear
 p = MAX_ELEMENTS;
 }
 else
 {
 clearFlag = 0; // occupied
 }
 }
 p++;
 }
 return clearFlag;
}
/**
* Function that checks if index meets index 60
* Preconditions:
* @param idx passed by
* @return flag that signifies when index meets border or not
*/
int isBorder(int index)
{
 int borFlag = 0;
 if (index == 10 || index == 21 || index == 32 || index == 43 
|| index == 54 || index == 65 || index == 76 ||
 index == 87 || index == 98 || index == 109 || index == 
120)
 {
 borFlag = 1; // 1 is border tile
 }
 else
 {
 borFlag = 0; // 0 is not border tile
 }
 return borFlag;
}
/**
* Function to remove a char from player rack
* Preconditions: must be a char on the board.
* @param tile char to be removed from player tile rack
* @param p pointer referring to player struct data
* @return pointer referring to the target player's data
*/
void removeFromTileRack(char tile, Player *p)
{
 int removed = 0;
 int i;
 for (i = 0; i < strlen(p->tileRack); i++)
 {
 if (tile == p->tileRack[i])
 {
 if (removed != 1)
 {
 p->tileRack[i] = '_';
 removed = 1;
 }
 }
 }
}
/**
* Function to receive multiple chars, retrieve tiles from board, 
and check if word submitted is valid.
* Preconditions:
* @param g pointer referring to scrabble struct data
* @param up pointer referring to player struct data
*/
void isValidScrabble(Scrabble *g, Player *p)
{
 int nRow = 0, nCol = 0;
 char userinput;
 int index = 0;
 int vacantFlag = 0;
 int option = 0;
 int j, i, k, w;
 int orient = 0;
 char scrWord[10];
 int clrFlag = 0;
 int tFlag = 0;
 int bordFlag = 0; // flag to check if border.
 int subFlag = 0; // flag to submit given input !
 // if next index is not pluss 11 or 1 flag
 j = 0;
 do
 {
 displayBoard(g->Board);
 printChoice();
 printf("Scrabble Choice: ");
 scanf("%d", &option);
 switch (option)
 {
 case 1:
 do
 {
 displayTileRack(p);
 printf("\nEnter letter: ");
 scanf(" %c", &userinput);
 tFlag = isLetValid(userinput, p); // check is letter is within the string of letters.
 if (tFlag == 0)
 {
 printf("Enter a letter within tile rack!\n");
 }
 else
 {
 removeFromTileRack(userinput, p);
 j++;
 }
 } while (tFlag == 0);
 printf("\nEnter Row:");
 scanf("%d", &nRow);
 printf("\nEnter Col:");
 scanf("%d", &nCol);
 // keep passing index and check if its in a straight line
 index = (nRow * 11) - (12 - nCol);
 vacantFlag = isVacant(index, g);
 if (vacantFlag == 1) // if it is vacant
 {
 j++;
 g->Board[index] = userinput;
 displayBoard(g->Board);
 }
 else
 {
printf("INVALID: Tile is occupied. Choose anotherlocation");
 break;
 }
 break;
 case 2:
 printf("\nEnter Row:");
 scanf("%d", &nRow);
 printf("\nEnter Col:");
 scanf("%d", &nCol);
 index = (nRow * 11) - (12 - nCol);
 vacantFlag = isVacant(index, g);
 if (vacantFlag == 1)
 {
 printf("Tile is vacant, Choose Another Tile to Retrieve");
 break;
 }
 // loop to check if index is occupied
 RetrieveTile(index, g);
 displayTileRack(p);
 break;
 case 3:
 // get starting index.
 printf("\nEnter Starting Index:");
 printf("\nEnter Row:");
 scanf("%d", &nRow);
 printf("\nEnter Col:");
 scanf("%d", &nCol);
 index = (nRow * 11) - (12 - nCol);
 printf("\nEnter Orientation 1[H] 2[V]:");
 scanf("%d", &orient);
 bordFlag = isBorder(index);
 clrFlag = isVacant(index, g);
 i = 0;
 if (bordFlag != 1 || clrFlag != 1)
 {
 while (subFlag != 1)
 {
 scrWord[i] = g->Board[index];
 i++;
 if (orient == 1)
 {
 index++; // for horizontal input. Fix forvertical input
 }
 else
 {
 index += 11;
 }
 bordFlag = isBorder(index);
 clrFlag = isVacant(index, g);
 if (bordFlag == 1 || clrFlag == 1)
 {
 subFlag = 1;
 scrWord[i] = '\0';
 }
 }
 }
 else
 {
 scrWord[i] = g->Board[index];
 }
 k = isWordValid(scrWord);
 if (k != 1)
 {
 printf("INVALID: Enter a Valid word within the Dictionary!\n");
 printf("INVALID WORD is: %s", scrWord);
 break;
 }
 else if (strlen(scrWord) == i - 1) // if it intersects a word
 {
 printf("INVALID: Did not Intersect!\n");
 printf("Retrieve and input in valid location\n");
 printf("INVALID WORD is: %s", scrWord);
 break;
 }
 else
 {
 printf("Word is: %s\n", scrWord);
 for (w = 0; w < strlen(scrWord); w++)
 {
 updateScore(scrWord[w], p, g);
 subTileIndexFromBag(g, scrWord[w]);
 }
 g->words++;
 p->wordsCast++;
 replenishRack(g, p);
 option = 4;
 }
 break;
 default:
 printf("Invalid input. Please enter 1, 2, 3 or 4 only.\n");
 }
 } while (option != 4);
}
/**
* Function checking if word at first input hits index 60 and if 
correct order of letters are displayed.
* Preconditions: char player_word[] must be the second letter of 
the inputted word before starting first loop.
* @param idx index of the first letter at the board
* @param player_word first word inputted by the player
* @param g pointer referring to scrabble struct data
* @param up pointer referring to player struct data
*/
void isValidBoardInput(int idx, char player_word[], Scrabble *g, 
Player *up)
{
 char userinput; // tile input by the user
 int index;
 int Usertile[9]; // index variable of the corresponding letter
 int j, p, i, nRow = 0, nCol = 0, x;
 int stFlag = 0;
 int wordLen = strlen(player_word);
 index = idx;
 Usertile[0] = idx;
 i = 0;
 p = 1;
 // check if continuous right index and letter.
 while (j != 1)
 {
 printf("\nEnter A char:");
 scanf(" %c", &userinput);
 printf("\nEnter Row:");
 scanf("%d", &nRow);
 printf("\nEnter Col:");
 scanf("%d", &nCol);
 index = (nRow * 11) - (12 - nCol);
 Usertile[p] = index;
 if (index == 60)
 {
 stFlag = 1;
 }
 if (Usertile[0] + 1 == Usertile[1])
 {
 if (Usertile[p] != Usertile[i] + 1 || player_word[p] != userinput)
 {
 j = 0;
 printf("Invalid: Input Correct Letter.\n");
 printf("Invalid: Input Horizontallly or Vertically.\n");
 }
 else
 {
 i++;
 p++;
 g->Board[index] = userinput;
 displayBoard(g->Board);
 updateScore(userinput, up, g);
 if (p == wordLen)
 {
 j = 1;
 g->words++; // all words submitted
 up->wordsCast++; // words submitted by the players
 replenishRack(g, up);
 }
 }
 }
 else if (Usertile[0] + 11 == Usertile[1])
 {
 if (Usertile[p] != Usertile[i] + 11 || 
player_word[p] != userinput)
 {
 j = 0;
 printf("Invalid: Input Correct Letter.\n");
 printf("Invalid: Input Horizontallly or Vertically.\n");
 }
 else
 {
 i++;
 p++;
 g->Board[index] = userinput;
 displayBoard(g->Board);
 updateScore(userinput, up, g);
 if (p == wordLen)
 {
 j = 1;
 g->words++;
 up->wordsCast++;
 replenishRack(g, up);
 }
 }
 }
 else
 {
 printf("INVALID: Input can only be done horizontally or vertically.");
 }
 }
 // IF DID NOT INPUT AT INDEX 60 RETRIEVE TILES.
 if (stFlag != 1)
 {
 printf("INVALID: To start match word must hit location ROW: 6 COL: 6"); 
 x = 0;
 do
 {
 printf("\nRETRIEVE TILES");
 printf("\nEnter A char:");
 scanf(" %c", &userinput);
 printf("\nEnter Row:");
 scanf("%d", &nRow);
 printf("\nEnter Col:");
 scanf("%d", &nCol);
 index = (nRow * 11) - (12 - nCol);
 if (Usertile[x] == index && userinput == 
player_word[x])
 {
 RetrieveTile(index, g);
 x++;
 }
 else
 {
 printf("INVALID: FOLLOW INITIAL INDEX");
 }
 if (x == wordLen)
 {
 printf("INPUT TILES AGAIN");
 printf("\nEnter A char:");
 scanf(" %c", &userinput);
 printf("\nEnter Row:");
 scanf("%d", &nRow);
 printf("\nEnter Col:");
 scanf("%d", &nCol);
 idx = (nRow * 11) - (12 - nCol);
 g->Board[idx] = userinput;
 displayBoard(g->Board);
 isValidBoardInput(idx, player_word, g, up);
 }
 // figure out how to reset the index array to store newchar location on the board.
 } while (x != wordLen);
 }
}
void CharInput(char player_word[], Scrabble *g, Player *p)
{
 char userinput;
 int idx = 0;
 int nRow = 0, nCol = 0;
 do
 {
 displayBoard(g->Board);
 printf("\nEnter A char:");
 scanf(" %c", &userinput);
 printf("\nEnter Row:");
 scanf("%d", &nRow);
 printf("\nEnter Col:");
 scanf("%d", &nCol);
 if (userinput != player_word[0])
 {
 printf("Input first letter of the Inputted word");
 }
 } while (userinput != player_word[0]);
 idx = (nRow * 11) - (12 - nCol);
 g->Board[idx] = userinput;
 displayBoard(g->Board);
 isValidBoardInput(idx, player_word, g, p);
}
void InputWord(Scrabble *g, Player *p)
{
 String userString;
 int k = 0;
 int i;
 do
 {
 printf("Input A Word: ");
 scanf("%s", userString);
 // input skip option?
 k = isWordValid(userString);
 if (k != 1)
 {
 printf("Enter a Valid word within the Dictionary!\n USE ALL CAPITAL LETTERS");
 }
 } while (k != 1);
 for (i = 0; i < strlen(userString); i++)
 {
 subTileIndexFromBag(g, userString[i]);
 }
 updateScore(userString[0], p, g);
 CharInput(userString, g, p);
}
void PlayerRecFile(PlayerRecord *PlayerRecord)
{
 int i;
 FILE *fp;
 fp = fopen("Player_Record.txt", "w");
 if (fp == NULL)
 {
 printf("FILE (Player_Record.txt) NOT FOUND");
 }
 else
 {
 for (i = 0; i < getNumberOfPlayers(); i++)
 {
 fprintf(fp, "Name of Player: %s \nLongest Word Formed:%s, Highest Score Word: %s, Number of Games Played: %d, Average Score: %d", PlayerRecord[i].username,PlayerRecord[i].longestWord, PlayerRecord[i].mostScoringWord, PlayerRecord[i].nGames, layerRecord[i].averageScore);
 }
 }
 fclose(fp);
}
/**
*
* MAIN FUNCTION
*
*/
int main()
{
 // this function is required for our RNG (random number generator) to
 // generate 'totally random' numbers
 srand(time(NULL));
 Player *ptr_p1, *ptr_p2, *ptr_p3, *ptr_p4, p1, p2, p3, p4;
 Scrabble *ptr_game, game;
 TileBag *ptr_tbag;
 // initialize a pointer for the current player
 Player *currentP;
 // initialize pointers
 // PlayerRecord *PlayerRecord;
 ptr_game = &game;
 ptr_tbag = &game.tbag;
 ptr_p1 = &p1;
 ptr_p2 = &p2;
 ptr_p3 = &p3;
 ptr_p4 = &p4;
 int RESET_FLAG;
 int persist, i;
 // initialize a variable for identifying the player's specified option
 int option;
 while (1)
 {
 // signals the game to reset
 RESET_FLAG = 0;
 // signals the game to retain player records
 persist = 0;
 // if the while-loop in this block has been interrupted, the program will handle the
 // RESET_FLAG=1 signal that interrupted the while-loop
 if (RESET_FLAG == 1)
 {
 /**
 * If RESET_FLAG == 1, then this means the game cycle 
has been reset, but not the program itself.
 * We have a 'persist' variable to tell 
initializeGame() to keep the player records intact, instead
 * of resetting the entire game data.
 */
 persist = RESET_FLAG;
 // revert the RESET_FLAG to its original state
 RESET_FLAG = 0;
 }
 initializeGame(ptr_game, ptr_tbag, ptr_p1, ptr_p2, ptr_p3,
ptr_p4, persist);
 while (RESET_FLAG == 0)
 {
 // as long as RESET_FLAG=0 (there are no game reset calls) or exit(1) calls,
 // this for-loop will run infinitely
 for (i = 1; i <= game.noOfPlayers; i++)
 {
 // initialize a variable for identifying the player's specified option
 option = 0;
 // save the current state of the board to PrevBoard. for retrieve checking.
 memcpy(ptr_game->PrevBoard, ptr_game->Board, 
MAX_ELEMENTS);
 // set the current player according to the index
 currentP = setCurrentPlayer(i, ptr_p1, ptr_p2, 
ptr_p3, ptr_p4);
 selectOption(ptr_game, currentP, &option);
 displayScoreboard(ptr_game->noOfPlayers, ptr_p1, 
ptr_p2, ptr_p3, ptr_p4);
 displayTileRack(currentP);
 // Start Game (FIRST MOVE)
 if (option == 1)
 {
 InputWord(ptr_game, currentP);
 displayScoreboard(ptr_game->noOfPlayers, 
ptr_p1, ptr_p2, ptr_p3, ptr_p4);
 ptr_game->turns++;
 }
 // input more tiles
 if (option == 2)
 {
 isValidScrabble(ptr_game, currentP);
 displayScoreboard(ptr_game->noOfPlayers, 
ptr_p1, ptr_p2, ptr_p3, ptr_p4);
 ptr_game->turns++;
 }
 // SKIP
 if (option == 3)
 {
 replaceTiles(ptr_game, currentP);
 printf("Ending turn...");
 ptr_game->turns++;
 }
 // NEW GAME
 if (option == 4)
 {
 RESET_FLAG = 1;
 printf("Resetting the game...\n");
 }
 // EXIT GAME
 if (option == 5)
 {
 printf("\nThank you! Goodbye!\n");
 exit(1);
 }
 // EXIT GAME (Due to Win)
 if (didPlayerWin(currentP))
 {
 // PlayerRecFile(PlayerRecord);
 printf("\nThank you! Goodbye!\n");
 exit(1);
 }
 }
 }
 }
 return 0;
}
/*
This is to certify that this project is my/our own work, based on
my/our personal efforts in studying and applying
the concepts learned. We have constructed the functions and their
respective algorithms and corresponding codes
either individually or with my group mate. The program was run, 
tested, and debugged by my/our own efforts. I/We
further certify that I/we have not copied in part or whole or 
otherwise plagiarized the work of other students/groups
and/or persons.
<Juan Pablo C. Bernardo> <Angelo Yanto Quinones>
DLSU ID DLSU ID 
*/
