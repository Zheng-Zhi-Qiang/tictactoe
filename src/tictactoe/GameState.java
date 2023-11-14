package tictactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameState {
    private String[][] game = new String[3][3];
    private String move;
    private Integer value;
    private Map<String, GameState> nextMoves = new HashMap<>();

    public GameState(String move, List<String> movesMade, List<String> remainingMoves){
        this.move = move;
        movesMade.add(move);
        remainingMoves.remove(move);
        createCurrentGameState(movesMade);

        // check if game is over
        if (!gameOver()){
            for (String remainingMove : remainingMoves){
                GameState nextMove = new GameState(remainingMove, copy(movesMade), copy(remainingMoves));
                nextMoves.put(move, nextMove);
            }
        }
    }

    public String getMove(){
        return this.move;
    }

    public Integer getMoveValue(){
        Integer moveValue;
        if (value == null && !gameOver()){
            moveValue = 0;
            for (String possibleMove : nextMoves.keySet()){
                moveValue +=  nextMoves.get(possibleMove).getMoveValue();
            }
        }
        else{
            moveValue = checkGameResult();
        }

        return moveValue;
    }

    private List<String> copy(List<String> list){
        List<String> copyOfList = new ArrayList<>();
        copyOfList.addAll(list);
        return copyOfList;
    }

    private List<Integer> returnIndices(String move){
        List<Integer> movesIndices = Stream.of(move.split(","))
                                                .flatMap(array -> Stream.of(array))
                                                .map(string -> string.trim())
                                                .map(number -> Integer.parseInt(number))
                                                .collect(Collectors.toList());
        return movesIndices;
    }

    private void createCurrentGameState(List<String> movesMade){
        for (int i = 0; i < 3; i++){
            Arrays.fill(game[i], " ");
        }

        for (int i = 0; i < movesMade.size(); i++){
            String player = isEven(i) ? "o" : "x"; // assuming that right now the user is always the first player
            List<Integer> movePosition = returnIndices(movesMade.get(i));
            game[movePosition.get(0)][movePosition.get(1)] = player;
        }
    }

    private boolean isEven(int index){
        if (index % 2 == 0){
            return true;
        }
        else {
            return false;
        }
    }

    private Integer checkGameResult(){
        String winner = winner();
        Integer result;
        result = switch (winner){
            case "x" -> -1;
            case "o" -> 1;
            default -> 0;
        };

        return result;
    }

    private boolean gameOver(){
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

    public String winner(){
        String winner = "NA";
        // horizontal and vertical wins
        for (int i = 0; i < 3; i++){
            if (game[i][0].equals(game[i][1]) && game[i][1].equals(game[i][2]) && !game[i][2].equals(" ")){
                winner = game[i][0];
                break;
            }
            else if (game[0][i].equals(game[1][i]) && game[1][i].equals(game[2][i]) && !game[2][i].equals(" ")){
                winner = game[0][i];
                break;
            }
        }
        
        // diagonal wins
        if ((game[0][0].equals(game[1][1]) && game[1][1].equals(game[2][2]) && !game[0][0].equals(" "))){
            winner = game[0][0];
        }
        else if ((game[0][2].equals(game[1][1]) && game[1][1].equals(game[2][0]) && !game[0][2].equals(" "))){
            winner = game[0][2];
        }

        return winner;
    }

    public void print(){
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                System.out.print(game[i][j]);
            }
            System.out.println();
        }
    }
}
