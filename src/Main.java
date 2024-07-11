import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    static int width;
    static int height;
    static String [][] map;
    static  String [][] W;
    static  String [][] P;

    static String[][][] KB;
    static int direction = 1;

    static boolean [][] explored;

    static int score = 0;
    static boolean lose = false;
    static boolean win = false;

    static int x,y;
    static int goalX,goalY;
    public static void main(String[] args) {
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
                for (int j = 0; j < height; j++) explored[i][j] = false;
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

    static void makeMove(){
        if (x < goalX){
            String ask = ASK(x+1,y);
            switch (ask){
                case "yes":
                    break;
                case "no":
                    break;
                case "uncertian":
                    break;
            }
        } else if (x > goalX){
            String ask = ASK(x-1,y);
            switch (ask){
                case "yes":
                    break;
                case "no":
                    break;
                case "uncertian":
                    break;
            }
        } else if (y < goalY){
            String ask = ASK(x,y+1);
            switch (ask){
                case "yes":
                    break;
                case "no":
                    break;
                case "uncertian":
                    break;
            }
        } else if (y > goalY) {
            String ask = ASK(x,y-1);
            switch (ask){
                case "yes":
                    break;
                case "no":
                    break;
                case "uncertian":
                    break;
            }
        }
    }

    static void actuator(String action){
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
                explored[x][y] = true;
                Tell(x,y);
                score--;
                if (map[x][y] == null){
                    W[x][y] = "no";
                    P[x][y] = "no";
                } else if (map[x][y].contains("W") || map[x][y].contains("P")){
                    score -= 1000;
                    lose = true;
                } else if (map[x][y].contains("Y")){
                    score += 1000;
                }
                break;
            case "Grab":
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
                    if (pitCount==1) P[pitX][pitY] = "yes";

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
                        if (wCount == 1) W[pitX][pitY] = "yes";
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
