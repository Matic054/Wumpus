import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
    static boolean foundWumpus = false;

    static int score = 0;
    static boolean lose = false;
    static boolean shotArrow = false;

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
                    costs[i][j] = Integer.MAX_VALUE;
                }
            costs[x][y] = Math.abs(goalX-x)+Math.abs(goalY-y);
            costs[goalX][goalY] = 0;
            explored[x][y] = true;
            for (String s: data) {
                if (s.length() < 4){
                    int X = Integer.parseInt(String.valueOf(s.charAt(1)));
                    int Y = Integer.parseInt(String.valueOf(s.charAt(2)));
                    if (s.charAt(0) != 'G') {
                        if (map[X-1][Y-1] == null) map[X-1][Y-1] = String.valueOf(s.charAt(0));
                        else map[X-1][Y-1] = map[X-1][Y-1]+","+s.charAt(0);
                    } else {
                        if (map[X-1][Y-1] == null) map[X-1][Y-1] = "Y";
                        else map[X-1][Y-1] = map[X-1][Y-1]+","+"Y";
                    }
                } else {
                    int X = Integer.parseInt(String.valueOf(s.charAt(2)));
                    int Y = Integer.parseInt(String.valueOf(s.charAt(3)));
                    map[X-1][Y-1] = "GO";
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
        Tell(x,y);
        W[x][y] = "no";
        P[x][y] = "no";
        System.out.println("Agent is at field ("+x+", "+y+")");
        traversal();
        System.out.println();
        System.out.println("The trace of the agent: "+trace);
        System.out.println("The players score was: "+score);

        for (int j = height-1; j >= 0; j--) {
            for (int i = 0; i < width; i++) {
                if (i==x && j == y)
                    System.out.print(" A ");
                else if (map[i][j] != null && map[i][j].contains("W"))
                    System.out.print(" W ");
                else if (map[i][j] != null && map[i][j].contains("P"))
                    System.out.print(" P ");
                else if (map[i][j] != null && map[i][j].contains("B"))
                    System.out.print(" B ");
                else if (i == goalX && j == goalY)
                    System.out.print(" G ");
                else if (map[i][j] != null && map[i][j].contains("Y"))
                    System.out.print(" Y ");
                else
                    System.out.print(" _ ");
            }
            System.out.println();
        }

        System.out.println(foundWumpus);

        for (int j = height-1; j >= 0; j--) {
            for (int i = 0; i < width; i++) {
                if (W[i][j] != null && W[i][j]=="yes")
                    System.out.print(" W ");
                else if (W[i][j] != null && W[i][j]=="no")
                    System.out.print(" X ");
                else if (W[i][j] == "uncertian")
                    System.out.print(" ? ");
                else
                    System.out.println(" _ ");
            }
            System.out.println();
        }

    }

    public static void traversal(){
        if (x == goalX && y == goalY) {
            System.out.println("The agent reached the exit.");
            return;
        } else if (lose){
            System.out.println("The agent died :(");
            return;
        }
        int costRight = Integer.MAX_VALUE;
        int costLeft = Integer.MAX_VALUE;
        int costUp = Integer.MAX_VALUE;
        int costDown = Integer.MAX_VALUE;
        if (x-1 >= 0) {
            switch (ASK(x-1,y)){
                case "yes":
                    costLeft = costs[x-1][y];
                    if (explored[x-1][y]) costLeft = Integer.MAX_VALUE;
                    break;
                case "no":
                    break;
                case "uncertian":
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
                    break;
            }
        }
        if (y-1 >= 0) {
            switch (ASK(x,y-1)){
                case "yes":
                    costDown = costs[x][y-1];
                    if (explored[x][y-1]) costDown = Integer.MAX_VALUE;
                    break;
                case "no":
                    break;
                case "uncertian":
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
                    break;
            }
        }

        if (costRight == costLeft && costLeft == costDown && costDown == costUp && (costRight == Integer.MAX_VALUE) && foundWumpus && !shotArrow) {
            int wX = 0;
            int wY = 0;
            for (int i = 0; i < width; i++)
                for (int j = 0; j < height; j++)
                    if (W[i][j] == "yes") {
                        wX = i;
                        wY = j;
                        break;
                    }
            if (wX == x) {
                int wDir = 2;
                if (wY > y) {
                    wDir = 2;
                } else if (wY < y) {
                    wDir = -2;
                }
                while (direction != wDir) actuator("RightTurn");
                actuator("Shoot");
                if (W[wX][wY] == "no") costs[wX][wY] = Math.abs(goalX - wX) + Math.abs(goalY - wY);
                if (wY == y + 1) costUp = costs[wX][wY];
                else if (wY == y - 1) costDown = costs[wX][wY];
            } else if (wY == y) {
                int wDir = 1;
                if (wX > x) {
                    wDir = 1;
                } else if (wX < x) {
                    wDir = -1;
                }
                while (direction != wDir) actuator("RightTurn");
                actuator("Shoot");
                if (W[wX][wY] == "no") costs[wX][wY] = Math.abs(goalX - wX) + Math.abs(goalY - wY);
                if (wX == x + 1) costRight = costs[wX][wY];
                else if (wX == x - 1) costLeft = costs[wX][wY];
            }
        }

        if (costRight == costLeft && costLeft == costDown && costDown == costUp && (costRight == Integer.MAX_VALUE)){
            System.out.println("Started backtracking");
            ArrayList<String> backtrack = new ArrayList<>(trace);
            Collections.reverse(backtrack);
            boolean firstForward = true;
            while (true) {
                String act = backtrack.remove(0);
                while (act != "Forward" && act != "LeftTurn" && act != "RightTurn") {
                    act = backtrack.remove(0);
                }
                if (act == "Forward"){
                    if (firstForward){
                        firstForward=false;
                        actuator("RightTurn");
                        actuator("RightTurn");
                        actuator("Forward");
                    } else {
                        actuator("Forward");
                    }
                } else if (act == "RightTurn"){
                    actuator("LeftTurn");
                } else if (act == "LeftTurn"){
                    actuator("RightTurn");
                }
                int cRight = Integer.MAX_VALUE;
                int cLeft = Integer.MAX_VALUE;
                int cUp = Integer.MAX_VALUE;
                int cDown = Integer.MAX_VALUE;
                if (x-1 >= 0) {
                    switch (ASK(x-1,y)){
                        case "yes":
                            cLeft = costs[x-1][y];
                            if (explored[x-1][y]) cLeft = Integer.MAX_VALUE;
                            break;
                        case "no":
                            break;
                        case "uncertian":
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
                            break;
                    }
                }
                if (y-1 >= 0) {
                    switch (ASK(x,y-1)){
                        case "yes":
                            cDown = costs[x][y-1];
                            if (explored[x][y-1]) cDown = Integer.MAX_VALUE;
                            break;
                        case "no":
                            break;
                        case "uncertian":
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
                            break;
                    }
                }
                if (!(cRight == cLeft &&
                        cLeft == cDown &&
                        cDown == cUp &&
                        cRight == Integer.MAX_VALUE)) {
                    System.out.println("Stoped backtracking");
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
                case "Up":
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
                    y++;
                    if (y > height){
                        y--;
                        return;
                    }
                } else {
                    y--;
                    if (y < 0){
                        y++;
                        return;
                    }
                }
                System.out.println("Move to field ("+x+", "+y+")");
                if (y > height-1) System.out.println(trace);
                for (int j = height-1; j >= 0; j--) {
                    for (int i = 0; i < width; i++) {
                        if (i==x && j == y)
                            System.out.print(" A ");
                        else if (map[i][j] != null && map[i][j].contains("W"))
                            System.out.print(" W ");
                        else if (map[i][j] != null && map[i][j].contains("P"))
                            System.out.print(" P ");
                        else if (map[i][j] != null && map[i][j].contains("B"))
                            System.out.print(" B ");
                        else if (i == goalX && j == goalY)
                            System.out.print(" G ");
                        else if (map[i][j] != null && map[i][j].contains("Y"))
                            System.out.print(" Y ");
                        else
                            System.out.print(" _ ");
                    }
                    System.out.println();
                }
                if (!explored[x][y]) explored[x][y] = true;
                Tell(x,y);
                score--;
                if (map[x][y] != null)
                if (map[x][y].contains("W") || map[x][y].contains("P")){
                    if (map[x][y].contains("P") || W[x][y]!="no"){
                        System.out.println("Game over!");
                        score -= 1000;
                        lose = true;
                    }
                } else if (map[x][y].contains("Y")){
                    actuator("Grab");
                    System.out.println("Found gold!");
                }
                break;
            case "Grab":
                if (map[x][y].contains("Y")){
                    score += 1000;
                    map[x][y] = map[x][y].replace(String.valueOf('Y'), "");
                }
                break;
            case "Release":
                break;
            case "Shoot":
                if (shotArrow) return;
                shotArrow = false;
                score -= 100;
                if (direction == 1){
                    System.out.println("Made a shot to the right");
                    int xArrow = x;
                    while (xArrow < width){
                        W[xArrow][y] = "no";
                        xArrow++;
                    }
                } else if (direction == 2){
                    System.out.println("Made a shot upwards");
                    int yArrow = y;
                    while (yArrow < height){
                        W[x][yArrow] = "no";
                        yArrow++;
                    }
                } else if (direction == -1) {
                    System.out.println("Made a shot to the left");
                    int xArrow = x;
                    while (xArrow >= 0){
                        W[xArrow][y] = "no";
                        xArrow--;
                    }
                } else {
                    System.out.println("Made a shot downwards");
                    int yArrow = y;
                    while (yArrow >= 0){
                        W[x][yArrow] = "no";
                        yArrow--;
                    }
                }
                for (int i = 0; i < width; i++)
                    for (int j = 0; j < height; j++) if (W[i][j] == "no" && P[i][j] == "no") costs[i][j] = Math.abs(goalX-i)+Math.abs(goalY-j);
                break;
        }
    }
    static void Tell(int x, int y){
        if (map[x][y] != null && map[x][y].contains("B")) {
            System.out.println("Breeze sensed");
            KB[x][y][0]="Breezy";
        } else {
            if (x-1 >= 0) P[x-1][y] = "no";
            if (x+1 < width) P[x+1][y] = "no";
            if (y-1 >= 0) P[x][y-1] = "no";
            if (y+1 < height) P[x][y+1] = "no";
        }
        if (!foundWumpus)
        if (map[x][y] != null && map[x][y].contains("S")) {
            System.out.println("Smell sensed");
            KB[x][y][1]="Smelly";
            int unCnt = 0;
            int unX = -1;
            int unY = -1;
            for (int i = 0; i < width; i++)
                for (int j = 0; j < height; j++)
                    if ((i==x+1 && j==y) || (i==x-1 && j==y) || (i==x && j==y+1) || (i==x && j==y-1)){
                        if (W[i][j] == "uncertian" || W[i][j]==null){
                            W[i][j] = "uncertian";
                            unCnt++;
                            unX=i;
                            unY=j;
                        }
                    } else {
                        W[i][j] = "no";
                    }
            if (unCnt==1){
                W[unX][unY]="yes";
                System.out.println("Found out Wumpus is on field ("+unX+", "+unY+")");
                foundWumpus=true;
            }
        } else {
            if (x-1 >= 0) W[x-1][y] = "no";
            if (x+1 < width) W[x+1][y] = "no";
            if (y-1 >= 0) W[x][y-1] = "no";
            if (y+1 < height) W[x][y+1] = "no";
        }
        if (map[x][y] == null || (!map[x][y].contains("S") && !map[x][y].contains("B"))){
            if (x-1 >= 0) costs[x-1][y] = Math.abs(goalX-x+1)+Math.abs(goalY-y);
            if (x+1 < width) costs[x+1][y] = Math.abs(goalX-x-1)+Math.abs(goalY-y);
            if (y-1 >= 0) costs[x][y-1] = Math.abs(goalX-x)+Math.abs(goalY-y+1);
            if (y+1 < height) costs[x][y+1] = Math.abs(goalX-x)+Math.abs(goalY-y-1);
        }
        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                if (!explored[i][j]) continue;
                if (KB[i][j][0]=="Breezy"){
                    int pitCount = 0;
                    int pitX=0;
                    int pitY=0;
                    if (i-1 >= 0){
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
                    if (j-1 >= 0){
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
                    if (pitCount==1 && P[pitX][pitY] != "yes"){
                        System.out.println("Found out pit is on field ("+pitX+", "+pitY+")");
                        P[pitX][pitY] = "yes";
                    }

                    if (foundWumpus) continue;
                    if (KB[i][j][1]=="Smelly") {
                        int wCount = 0;
                        int wX = 0;
                        int wY = 0;
                        if (i - 1 >= 0) {
                            if (W[i - 1][j] != "no") {
                                wCount++;
                                wX = i - 1;
                                wY = j;
                            }
                        }
                        if (i + 1 < width) {
                            if (W[i + 1][j] != "no") {
                                wCount++;
                                wX = i + 1;
                                wY = j;
                            }
                        }
                        if (j - 1 >= 0) {
                            if (W[i][j - 1] != "no") {
                                wCount++;
                                wX = i;
                                wY = j - 1;
                            }
                        }
                        if (j + 1 < height) {
                            if (W[i][j + 1] != "no") {
                                wCount++;
                                wX = i;
                                wY = j + 1;
                            }
                        }
                        if (wCount == 1 && W[wX][wY] != "yes"){
                            System.out.println("Found out Wumpus is on field ("+wX+", "+wY+")");
                            for (int X = 0; X < width; X++)
                                for (int Y = 0; Y < height; Y++)
                                    W[X][Y] = "no";
                            W[wX][wY] = "yes";
                            foundWumpus = true;
                            break;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                if (W[i][j] == "no" && P[i][j] == "no") costs[i][j] = Math.abs(goalX-i)+Math.abs(goalY-j);
    }
    static String ASK(int x,int y){
        if (P[x][y]=="yes" || W[x][y]=="yes") return "no";
        if (P[x][y]=="no" && W[x][y]=="no") return "yes";
        return "uncertian";
    }
}
