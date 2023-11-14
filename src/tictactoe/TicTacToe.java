package tictactoe;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TicTacToe {
    private String[][] game = new String[3][3];
    private List<String> movesMade = new ArrayList<>();
    private final SecureRandom RAND = new SecureRandom();

    public TicTacToe(){
        for (int i = 0; i < 3; i++){
            Arrays.fill(game[i], " ");
        }
    }


    public String getNextMinValueMove(){
        List<String> possibleMoves = getPossibleMoves();
        Map<String, Integer> minMoves = new HashMap<>();
        for (String move : possibleMoves){
            List<String> copyOfPossibleMoves = copy(possibleMoves);
            List<String> copyOfMovesMade = copy(movesMade);

            // psuedo make move
            copyOfMovesMade.add(move);
            copyOfPossibleMoves.remove(move);

            Integer value = 0;
            // for each following move, generate the tree and get the value
            for (int i = 0; i < copyOfPossibleMoves.size(); i++){
                GameState scenario = new GameState(copyOfPossibleMoves.get(i), copyOfMovesMade, copyOfPossibleMoves);
                if (i == 0){
                    value = scenario.getMoveValue();
                }
                else {
                    Integer newValue = scenario.getMoveValue();
                    value = newValue.compareTo(value) < 0 ? newValue : value;
                }
            }
            minMoves.put(move, value);
        }

        return getBestMove(minMoves);
    }

    private List<String> getPossibleMoves(){
        return getPossibleMoves(game);
    }

    private List<String> getPossibleMoves(String[][] array){
        List<String> possibleMoves = new ArrayList<>();
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                if (array[i][j].equals(" ")){
                    possibleMoves.add("%d, %d".formatted(i, j));
                }
            }
        }

        return possibleMoves;
    }

    public void addPlayerMove(String move){
        movesMade.add(move);
        changeGameState(move, "x");

        printGame();
    }

    public void makeMove(String move){
        movesMade.add(move);
        changeGameState(move, "o");

        printGame();
    }

    public boolean validMove(String move){
        List<Integer> position = returnIndices(move);
        boolean result = game[position.get(0)][position.get(1)].equals(" ") ? true : false;
        return result;
    }

    private void changeGameState(String move, String value){
        List<Integer> position = returnIndices(move);
        game[position.get(0)][position.get(1)] = value;
    }

    private List<Integer> returnIndices(String move){
        List<Integer> movesIndices = Stream.of(move.split(","))
                                                .flatMap(array -> Stream.of(array))
                                                .map(string -> string.trim())
                                                .map(number -> Integer.parseInt(number))
                                                .collect(Collectors.toList());
        return movesIndices;
    }
    
    public void printGame(){
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                System.out.print(game[i][j]);
            }
            System.out.println();
        }
    }

    private List<String> copy(List<String> list){
        List<String> copyOfList = new ArrayList<>();
        copyOfList.addAll(list);
        return copyOfList;
    }

    private String[][] copy(String[][] array){ // for 2d array, clone does not work as you are creating a copy of references where the references are still the same
        String[][] copy = new String[3][3];
        for (int i = 0; i < array.length; i++){
            copy[i] = array[i].clone();
        }

        return copy;
    }

    private String getBestMove(Map<String, Integer> moves){
        Integer value = 0;
        String bestMove = "";
        List<String> possibleMoves = new ArrayList<>();
        possibleMoves.addAll(moves.keySet());
        for (int i = 0; i < possibleMoves.size(); i++){
            int simulateResult = simulateMove(copy(game), possibleMoves.get(i));
            if (i == 0){
                bestMove = possibleMoves.get(i);
                value = moves.get(bestMove);
            }

            if (simulateResult == 1){
                bestMove = possibleMoves.get(i);
                value = moves.get(bestMove);
                break;
            }
            else if (simulateResult == -1){
                // Get best possible move that may also win
                if (simulateMove(copy(game), bestMove) >= 0){
                    continue;
                }
                else{
                    for (int j = 0; j < possibleMoves.size(); j++){
                        if (simulateMove(copy(game), possibleMoves.get(j)) >= 0){
                            bestMove = possibleMoves.get(j);
                            value = moves.get(bestMove);
                            break;
                        }
                    }
                    continue; // continue checking if there is a winning move;
                }
            }
            else if (moves.get(possibleMoves.get(i)).compareTo(value) == 0){
                int rand = RAND.nextInt(2);
                if (rand == 0){
                    continue;
                }
                else{
                    bestMove = possibleMoves.get(i);
                    value = moves.get(bestMove);
                }
            }
            else if (moves.get(possibleMoves.get(i)).compareTo(value) > 0){
                    bestMove = possibleMoves.get(i);
                    value = moves.get(bestMove);
            }
        }
        
        return bestMove;
    }

    public boolean gameOver(){
        boolean filled = true;

        // squares are filled
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                if (!game[i][j].equals(" ")){
                    continue;
                }
                else{
                    filled = false;
                    break;
                }
            }
        }

        boolean gameDecided = winner().equals("NA") ? false : true;
        
        boolean result = (gameDecided == false && filled == false) ? false : true;
        return result;

    }

    private String winner(){
        return winner(game);
    }

    private String winner(String[][] gameState){
        String winner = "NA";
        // horizontal and vertical wins
        for (int i = 0; i < 3; i++){
            if (gameState[i][0].equals(gameState[i][1]) && gameState[i][1].equals(gameState[i][2]) && !gameState[i][2].equals(" ")){
                winner = gameState[i][0];
                break;
            }
            else if (gameState[0][i].equals(gameState[1][i]) && gameState[1][i].equals(gameState[2][i]) && !gameState[2][i].equals(" ")){
                winner = gameState[0][i];
                break;
            }
        }
        
        // diagonal wins
        if ((gameState[0][0].equals(gameState[1][1]) && gameState[1][1].equals(gameState[2][2]) && !gameState[0][0].equals(" "))){
            winner = gameState[0][0];
        }
        else if ((gameState[0][2].equals(gameState[1][1]) && gameState[1][1].equals(gameState[2][0]) && !gameState[0][2].equals(" "))){
            winner = gameState[0][2];
        }

        return winner;
    }

    public void printGameWinner(){
        if (winner().equals("NA")){
            System.out.println("The game ended in a draw!");
        }
        else {
            if (winner().equals("x")){
                System.out.println("Good game! You win!");
            }
            else{
                System.out.println("I win! Well played!");
            }
        }
    }

    private int simulateMove(String[][] gameState, String move){
        List<Integer> position = returnIndices(move);
        gameState[position.get(0)][position.get(1)] = "o";
        int result;
        result = switch(winner(gameState)){
            case "o" -> 1;
            case "x" -> -1;
            default -> 0;
        };

        // check if move is winning move
        if (result == 1){
            return result;
        }

        // check if move has an immediate loss
        List<String> possibleMoves = getPossibleMoves(gameState);
        for (String possibleMove: possibleMoves){
            String[][] copyOfGameState = copy(gameState);
            List<Integer> possiblePosition = returnIndices(possibleMove);
            copyOfGameState[possiblePosition.get(0)][possiblePosition.get(1)] = "x";
            if (winner(copyOfGameState).equals("x")){
                result = -1;
            }
        }
        return result;
    }

    private void print(String[][] array){
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                System.out.print(array[i][j]);
            }
            System.out.println();
        }
    }
}