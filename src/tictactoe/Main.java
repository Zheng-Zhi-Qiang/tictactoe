package tictactoe;

import java.io.Console;

public class Main {
    public static void main(String[] args) {
        Console cons = System.console();
        
        System.out.println("Welcome to TicTacToe! You make a move by indicating the position of the move you want to play (eg: 0, 1)");
        System.out.println("The possible positions are within the range of 0 and 2.");
        System.out.println("To start, make a move!");

        TicTacToe game = new TicTacToe();
        while (true){
            String move = cons.readLine("> ").trim();
            if (game.validMove(move)){
                System.out.println("Your move:");
                game.addPlayerMove(move);

                // can always move this functionality of checking game over into the class and call sys.exit from there if game is over
                if (game.gameOver()){
                    game.printGameWinner();
                    break;
                }
                else{
                    System.out.println("My move:");
                    game.makeMove(game.getNextMinValueMove());
                    if (game.gameOver()){
                        game.printGameWinner();
                        break;
                    }
                }
            }
            else{
                System.out.println("Invalid Move! Current game state");
                game.printGame();
            }
        }
    }
}
