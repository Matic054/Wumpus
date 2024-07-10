import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    static int width;
    static int height;
    static String [][] map;
    static  boolean [][] W;
    static  boolean [][] P;

    static String[][][] KB;
    static int direction = 1;

    static int score = 0;
    static boolean lose = false;
    static boolean win = false;

    static int x,y;
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
                    W = new boolean[width][height];
                    KB = new String[width][height][2];
                } else {
                    data.add(line);
                    if (line.charAt(0) == 'A'){
                        x = Integer.parseInt(String.valueOf(line.charAt(1)))-1;
                        y = Integer.parseInt(String.valueOf(line.charAt(2)))-1;
                    }
                }
            }
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
                Tell(x,y);
                score--;
                if (map[x][y] == null){
                    W[x][y] = false;
                    P[x][y] = false;
                } else if (map[x][y].contains("W") || map[x][y].contains("P")){
                    score -= 1000;
                    lose = true;
                } else if (map[x][y].contains("Y")){
                    score += 1000;
                    win = true;
                }
                break;
            case "Grab":
                break;
            case "Release":
                break;
            case "Shoot":
                break;
        }
    }
    static void Tell(int x, int y){
        if (map[x][y].contains("B")) KB[x][y][1]="Breezy";
        else {
            if (x-1 > 0) P[x-1][y] = false;
            if (x+1 > 0) P[x+1][y] = false;
            if (y-1 > 0) P[x][y-1] = false;
            if (y+1 > 0) P[x][y+1] = false;
        }
        if (map[x][y].contains("S")) KB[x][y][1]="Smelly";
        else {
            if (x-1 > 0) W[x-1][y] = false;
            if (x+1 > 0) W[x+1][y] = false;
            if (y-1 > 0) W[x][y-1] = false;
            if (y+1 > 0) W[x][y+1] = false;
        }
    }

    static String ASK(int x,int y){
        String action = "";
        
        return action;
    }
}
