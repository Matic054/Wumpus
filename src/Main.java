import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Main {
    static int width;
    static int height;
    static String [][] map;
    static  String [][] W;
    static  String [][] P;

    static String[][][] KB;
    static int direction = 1;

    static boolean [][] explored;
    static int [][] costs;

    static int score = 0;
    static boolean lose = false;
    static boolean win = false;

    static int x,y;
    static int goalX,goalY;

    static ArrayList<String> trace = new ArrayList<>();
    public static void main(String[] args) {
        ArrayList<String> t = new ArrayList<>();
        String filePath = "wumpus_world.txt";
        BufferedReader reader = null;
        try {
            FileReader fileReader = new FileReader(filePath);
            reader = new BufferedReader(fileReader);
            String line;
            ArrayList<String> data = new ArrayList<String>();
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0)
                if (line.charAt(0) == 'M'){
                    width = Integer.parseInt(String.valueOf(line.charAt(1)));
                    height = Integer.parseInt(String.valueOf(line.charAt(2)));
                    map = new String[width][height];
                    W = new String[width][height];
                    P = new String[width][height];
                    KB = new String[width][height][2];
                    explored = new boolean[width][height];
                    costs = new int[width][height];
                } else {
                    data.add(line);
                    if (line.charAt(0) == 'A'){
                        x = Integer.parseInt(String.valueOf(line.charAt(1)))-1;
                        y = Integer.parseInt(String.valueOf(line.charAt(2)))-1;
                    }
                    if (line.charAt(0) == 'G' && line.charAt(1) == 'O'){
                        goalX = Integer.parseInt(String.valueOf(line.charAt(2)))-1;
                        goalY = Integer.parseInt(String.valueOf(line.charAt(3)))-1;
                    }
                }
            }
            for (int i = 0; i < width; i++)
                for (int j = 0; j < height; j++) {
                    explored[i][j] = false;
                    costs[i][j] = Math.abs(i-goalX)+Math.abs(j-goalY);
                }
            explored[x][y] = true;
            for (String s: data) {
                if (s.length() < 4){
                    int X = Integer.parseInt(String.valueOf(s.charAt(1)));
                    int Y = Integer.parseInt(String.valueOf(s.charAt(2)));
                    if (map[X-1][Y-1] == null) map[X-1][Y-1] = String.valueOf(s.charAt(0));
                    else map[X-1][Y-1] = map[X-1][Y-1]+","+s.charAt(0);
                } else {
                    int X = Integer.parseInt(String.valueOf(s.charAt(2)));
                    int Y = Integer.parseInt(String.valueOf(s.charAt(3)));
                    map[X-1][Y-1] = "Y";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                System.out.print(map[i][j] + ", ");
            }
            System.out.println();
        }
        Tell(x,y);
    }

    public void traversal(){
        if (x == goalX && y == goalY) return;
        int costRight = Integer.MAX_VALUE;
        int costLeft = Integer.MAX_VALUE;
        int costUp = Integer.MAX_VALUE;
        int costDown = Integer.MAX_VALUE;
        if (x-1 > 0) {
            switch (ASK(x-1,y)){
                case "yes":
                    costLeft = costs[x-1][y];
                    if (explored[x-1][y]) costLeft = Integer.MAX_VALUE;
                    break;
                case "no":
                    break;
                case "uncertian":
                    costLeft = costLeft/2;
                    break;
            }
        }
        if (x+1 < width) {
            switch (ASK(x+1,y)){
                case "yes":
                    costRight = costs[x+1][y];
                    if (explored[x+1][y]) costRight = Integer.MAX_VALUE;
                    break;
                case "no":
                    break;
                case "uncertian":
                    costRight = costRight/2;
                    break;
            }
        }
        if (y-1 > 0) {
            switch (ASK(x,y-1)){
                case "yes":
                    costDown = costs[x][y-1];
                    if (explored[x][y-1]) costDown = Integer.MAX_VALUE;
                    break;
                case "no":
                    break;
                case "uncertian":
                    costDown = costDown/2;
                    break;
            }
        }
        if (y+1 < height) {
            switch (ASK(x,y+1)){
                case "yes":
                    costUp = costs[x][y+1];
                    if (explored[x][y+1]) costUp = Integer.MAX_VALUE;
                    break;
                case "no":
                    break;
                case "uncertian":
                    costUp = costUp/2;
                    break;
            }
        }
        if (costRight == costLeft &&
                costLeft == costDown &&
                costDown == costUp &&
                (costRight == Integer.MAX_VALUE  || costRight == Integer.MAX_VALUE/2)){
            ArrayList<String> backtrack = new ArrayList<>(trace);
            Collections.reverse(backtrack);
            while (true) {
                actuator(backtrack.remove(0));
                int cRight = Integer.MAX_VALUE;
                int cLeft = Integer.MAX_VALUE;
                int cUp = Integer.MAX_VALUE;
                int cDown = Integer.MAX_VALUE;
                if (x-1 > 0) {
                    switch (ASK(x-1,y)){
                        case "yes":
                            cLeft = costs[x-1][y];
                            if (explored[x-1][y]) cLeft = Integer.MAX_VALUE;
                            break;
                        case "no":
                            break;
                        case "uncertian":
                            cLeft = cLeft/2;
                            break;
                    }
                }
                if (x+1 < width) {
                    switch (ASK(x+1,y)){
                        case "yes":
                            cRight = costs[x+1][y];
                            if (explored[x+1][y]) cRight = Integer.MAX_VALUE;
                            break;
                        case "no":
                            break;
                        case "uncertian":
                            cRight = cRight/2;
                            break;
                    }
                }
                if (y-1 > 0) {
                    switch (ASK(x,y-1)){
                        case "yes":
                            cDown = costs[x][y-1];
                            if (explored[x][y-1]) cDown = Integer.MAX_VALUE;
                            break;
                        case "no":
                            break;
                        case "uncertian":
                            cDown = cDown/2;
                            break;
                    }
                }
                if (y+1 < height) {
                    switch (ASK(x,y+1)){
                        case "yes":
                            cUp = costs[x][y+1];
                            if (explored[x][y+1]) cUp = Integer.MAX_VALUE;
                            break;
                        case "no":
                            break;
                        case "uncertian":
                            cUp = cUp/2;
                            break;
                    }
                }
                if (!(cRight == cLeft &&
                        cLeft == cDown &&
                        cDown == cUp &&
                        (cRight == Integer.MAX_VALUE  || cRight == Integer.MAX_VALUE/2))) {
                    String action;
                    if (cRight <= cLeft && cRight <= cUp && cRight <= cDown) action = "Right";
                    else if (cLeft <= cRight && cLeft <= cUp && cLeft <= cDown) action = "Left";
                    else if (cUp <= cRight && cUp <= cLeft && cUp <= cDown) action = "Up";
                    else action = "Down";
                    switch (action){
                        case "Right":
                            if (direction == 1){
                                actuator("Forward");
                            } else if (direction == 2){
                                actuator("RightTurn");
                                actuator("Forward");
                            } else if (direction == -1){
                                actuator("RightTurn");
                                actuator("RightTurn");
                                actuator("Forward");
                            } else {
                                actuator("LeftTurn");
                                actuator("Forward");
                            }
                            break;
                        case "Left":
                            if (direction == 1){
                                actuator("RightTurn");
                                actuator("RightTurn");
                                actuator("Forward");
                            } else if (direction == 2){
                                actuator("LeftTurn");
                                actuator("Forward");
                            } else if (direction == -1){
                                actuator("Forward");
                            } else {
                                actuator("RightTurn");
                                actuator("Forward");
                            }
                            break;
                        case "up":
                            if (direction == 1){
                                actuator("LeftTurn");
                                actuator("Forward");
                            } else if (direction == 2){
                                actuator("Forward");
                            } else if (direction == -1){
                                actuator("RightTurn");
                                actuator("Forward");
                            } else {
                                actuator("RightTurn");
                                actuator("RightTurn");
                                actuator("Forward");
                            }
                            break;
                        case "Down":
                            if (direction == 1){
                                actuator("RightTurn");
                                actuator("Forward");
                            } else if (direction == 2){
                                actuator("RightTurn");
                                actuator("RightTurn");
                                actuator("Forward");
                            } else if (direction == -1){
                                actuator("LeftTurn");
                                actuator("Forward");
                            } else {
                                actuator("Forward");
                            }
                            break;
                    }
                    break;
                }
                if (backtrack.size() == 0) {
                    System.out.println("Unsolvable");
                    return;
                }
            }
        } else {
            String action;
            if (costRight <= costLeft && costRight <= costUp && costRight <= costDown) action = "Right";
            else if (costLeft <= costRight && costLeft <= costUp && costLeft <= costDown) action = "Left";
            else if (costUp <= costRight && costUp <= costLeft && costUp <= costDown) action = "Up";
            else action = "Down";
            switch (action){
                case "Right":
                    if (direction == 1){
                        actuator("Forward");
                    } else if (direction == 2){
                        actuator("RightTurn");
                        actuator("Forward");
                    } else if (direction == -1){
                        actuator("RightTurn");
                        actuator("RightTurn");
                        actuator("Forward");
                    } else {
                        actuator("LeftTurn");
                        actuator("Forward");
                    }
                    break;
                case "Left":
                    if (direction == 1){
                        actuator("RightTurn");
                        actuator("RightTurn");
                        actuator("Forward");
                    } else if (direction == 2){
                        actuator("LeftTurn");
                        actuator("Forward");
                    } else if (direction == -1){
                        actuator("Forward");
                    } else {
                        actuator("RightTurn");
                        actuator("Forward");
                    }
                    break;
                case "up":
                    if (direction == 1){
                        actuator("LeftTurn");
                        actuator("Forward");
                    } else if (direction == 2){
                        actuator("Forward");
                    } else if (direction == -1){
                        actuator("RightTurn");
                        actuator("Forward");
                    } else {
                        actuator("RightTurn");
                        actuator("RightTurn");
                        actuator("Forward");
                    }
                    break;
                case "Down":
                    if (direction == 1){
                        actuator("RightTurn");
                        actuator("Forward");
                    } else if (direction == 2){
                        actuator("RightTurn");
                        actuator("RightTurn");
                        actuator("Forward");
                    } else if (direction == -1){
                        actuator("LeftTurn");
                        actuator("Forward");
                    } else {
                        actuator("Forward");
                    }
                    break;
            }
        }
        traversal();
    }

    static void actuator(String action){
        trace.add(action);
        switch (action){
            case "LeftTurn":
                if (direction==1) direction = 2;
                else if (direction==2) direction = -1;
                else if (direction==-1) direction = -2;
                else direction = 1;
                break;
            case "RightTurn":
                if (direction==1) direction = -2;
                else if (direction==2) direction = 1;
                else if (direction==-1) direction = 2;
                else direction = -1;
                break;
            case "Forward":
                if (direction == 1){
                    x++;
                    if (x >= width){
                        x--;
                        return;
                    }
                } else if (direction == -1) {
                    x--;
                    if (x < 0){
                        x++;
                        return;
                    }
                } else if (direction == 2){
                    y--;
                    if (y < 0){
                        y++;
                        return;
                    }
                } else {
                    y++;
                    if (y >= height){
                        y--;
                        return;
                    }
                }
                if (!explored[x][y]){
                    explored[x][y] = true;
                    Tell(x,y);
                }
                score--;
                if (map[x][y] == null){
                    W[x][y] = "no";
                    P[x][y] = "no";
                } else if (map[x][y].contains("W") || map[x][y].contains("P")){
                    score -= 1000;
                    lose = true;
                } else if (map[x][y].contains("Y")){
                    actuator("Grab");
                }
                break;
            case "Grab":
                if (map[x][y].contains("Y")){
                    score += 1000;
                    map[x][y].replace(String.valueOf('Y'), "");
                }
                break;
            case "Release":
                break;
            case "Shoot":
                score -= 100;
                break;
        }
    }
    static void Tell(int x, int y){
        if (map[x][y].contains("B")) KB[x][y][0]="Breezy";
        else {
            if (x-1 > 0) P[x-1][y] = "no";
            if (x+1 > 0) P[x+1][y] = "no";
            if (y-1 > 0) P[x][y-1] = "no";
            if (y+1 > 0) P[x][y+1] = "no";
        }
        if (map[x][y].contains("S")) KB[x][y][1]="Smelly";
        else {
            if (x-1 > 0) W[x-1][y] = "no";
            if (x+1 > 0) W[x+1][y] = "no";
            if (y-1 > 0) W[x][y-1] = "no";
            if (y+1 > 0) W[x][y+1] = "no";
        }
        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                if (!explored[i][j]) continue;
                if (KB[i][j][0]=="Breezy"){
                    int pitCount = 0;
                    int pitX=0;
                    int pitY=0;
                    if (i-1 > 0){
                        if (P[i-1][j]=="yes") pitCount = 2;
                        if (P[i-1][j]==null) {
                            pitCount++;
                            pitX = i-1;
                            pitY = j;
                        }
                    }
                    if (i+1 < width){
                        if (P[i+1][j]=="yes") pitCount = 2;;
                        if (P[i+1][j]==null) {
                            pitCount++;
                            pitX = i+1;
                            pitY = j;
                        }
                    }
                    if (j-1 > 0){
                        if (P[i][j-1]=="yes") pitCount = 2;;
                        if (P[i][j-1]==null) {
                            pitCount++;
                            pitX = i;
                            pitY = j-1;
                        }
                    }
                    if (j+1 < height){
                        if (P[i][j+1]=="yes") pitCount = 2;;
                        if (P[i][j+1]==null) {
                            pitCount++;
                            pitX = i;
                            pitY = j+1;
                        }
                    }
                    if (pitCount==1){
                        P[pitX][pitY] = "yes";
                        costs[pitX][pitY] = Integer.MAX_VALUE;
                    }

                    if (KB[i][j][0]=="Smelly") {
                        int wCount = 0;
                        int wX = 0;
                        int wY = 0;
                        if (i - 1 > 0) {
                            if (W[i - 1][j] == "yes") wCount = 2;
                            if (W[i - 1][j] == null) {
                                wCount++;
                                wX = i - 1;
                                wY = j;
                            }
                        }
                        if (i + 1 < width) {
                            if (W[i + 1][j] == "yes") wCount = 2;
                            ;
                            if (W[i + 1][j] == null) {
                                wCount++;
                                wX = i + 1;
                                wY = j;
                            }
                        }
                        if (j - 1 > 0) {
                            if (W[i][j - 1] == "yes") wCount = 2;
                            ;
                            if (W[i][j - 1] == null) {
                                wCount++;
                                wX = i;
                                wY = j - 1;
                            }
                        }
                        if (j + 1 < height) {
                            if (W[i][j + 1] == "yes") wCount = 2;
                            ;
                            if (W[i][j + 1] == null) {
                                wCount++;
                                wX = i;
                                wY = j + 1;
                            }
                        }
                        if (wCount == 1){
                            W[wX][wY] = "yes";
                            costs[wX][wY] = Integer.MAX_VALUE;
                        }
                    }
                }
            }
        }
    }
    static String ASK(int x,int y){
        if (P[x][y]=="yes" || W[x][y]=="yes") return "no";
        if (P[x][y]=="no" && W[x][y]=="no") return "yes";
        return "uncertian";
    }
}
