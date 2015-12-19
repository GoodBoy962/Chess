package board;

import enums.Color;
import enums.Figure;
import enums.FigureColor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Board extends JFrame {

    private final Integer SIZE = 8;
    private int buttonSize = 65;
    private FigureColor[][] field;
    private boolean whiteKingInCheck;
    private boolean blackKingInCheck;
    //o - white, 1 - black
    private byte turn;
    private JButton selected;
    private int selectedI;
    private int selectedJ;
    private boolean whiteKingMotion;
    private boolean blackKingMotion;
    private boolean whiteLeftCastleMotion;
    private boolean whiteRightCastleMotion;
    private boolean blackLeftCastleMotion;
    private boolean blackRightCastleMotion;
    private BufferedReader is;
    private PrintWriter pw;
    List term = new ArrayList();
    private int yourTurn;

    public void addMessage(String s) {
        term.add(s + "\n");
    }

    public Board(PrintWriter pw, BufferedReader is, int yourTurn) throws IOException {
        this.yourTurn = yourTurn;
        this.is = is;
        this.pw = pw;
        setBounds(50, 50, buttonSize * SIZE + 100, buttonSize * SIZE + 100);
        setLayout(new GroupLayout(getContentPane()));
        generateField();
        whiteKingInCheck = false;
        blackKingInCheck = false;
        whiteLeftCastleMotion = false;
        whiteRightCastleMotion = false;
        blackLeftCastleMotion = false;
        blackRightCastleMotion = false;
        turn = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JButton jb = new JButton();
                jb.setBounds(j * buttonSize, i * buttonSize, buttonSize, buttonSize);
                if ((i + j) % 2 == 1) {
                    jb.setBackground(java.awt.Color.lightGray);
                } else {
                    jb.setBackground(java.awt.Color.white);
                }
                jb.setBorderPainted(true);
                jb.repaint();
                setImageForButton(jb, i, j);
                jb.setContentAreaFilled(false);
                jb.setOpaque(true);
                jb.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton button = (JButton) e.getSource();
                        int i = button.getY() / buttonSize;
                        int j = button.getX() / buttonSize;
                        if (selected == null) {
                            if (field[i][j].color == Color.WHITE && turn == 0 || field[i][j].color == Color.BLACK && turn == 1) {
                                selected = button;
                                selectedI = i;
                                selectedJ = j;
                                selected.setFocusPainted(true);
                            }
                        } else {
                            // TODO Логика пешки
                            if (field[selectedI][selectedJ].figure == Figure.PONE) {
                                if (field[selectedI][selectedJ].color == Color.WHITE) {
                                    if (j == selectedJ && i == selectedI - 1 && field[i][j].figure == Figure.NON ||
                                            j == selectedJ && selectedI == 6 && i == selectedI - 2 && field[i][j].figure == Figure.NON && field[i + 1][j].figure == Figure.NON ||
                                            ((j == selectedJ + 1 || j == selectedJ - 1) && i == selectedI - 1 && field[i][j].figure != Figure.NON &&
                                                    field[i][j].color == Color.BLACK)) {
                                        try {
                                            doChanges(i, j, button, Figure.PONE, 0);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                } else if (j == selectedJ && i == selectedI + 1 && field[i][j].figure == Figure.NON ||
                                        j == selectedJ && selectedI == 1 && i == selectedI + 2 && field[i][j].figure == Figure.NON && field[i - 1][j].figure == Figure.NON ||
                                        ((j == selectedJ + 1 || j == selectedJ - 1) && i == selectedI + 1 && field[i][j].figure != Figure.NON &&
                                                field[i][j].color == Color.WHITE)) {
                                    try {
                                        doChanges(i, j, button, Figure.PONE, 0);
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }

                            }

                            //TODO логика ладьи
                            else if (field[selectedI][selectedJ].figure == Figure.CASTLE) {
                                if (field[selectedI][selectedJ].color == Color.WHITE) {
                                    if (((j == selectedJ && i != selectedI && noObstacleForCastleI(i, j)) || (j != selectedJ && i == selectedI && noObstacleForCastleJ(i, j)))
                                            && field[i][j].color != Color.WHITE) {
                                        try {
                                            doChanges(i, j, button, Figure.CASTLE, 0);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                        if (i == 0 && j == 7) {
                                            whiteLeftCastleMotion = true;
                                        } else if (i == 7 && j == 7) {
                                            whiteRightCastleMotion = true;
                                        }
                                    }
                                } else {
                                    if (((j == selectedJ && i != selectedI && noObstacleForCastleI(i, j)) || (j != selectedJ && i == selectedI && noObstacleForCastleJ(i, j)))
                                            && field[i][j].color != Color.BLACK) {
                                        try {
                                            doChanges(i, j, button, Figure.CASTLE, 0);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                        if (i == 0 && j == 0) {
                                            blackLeftCastleMotion = true;
                                        } else if (i == 7 && j == 0) {
                                            blackRightCastleMotion = true;
                                        }
                                    }

                                }

                            }

                            //TODO логика коня
                            else if (field[selectedI][selectedJ].figure == Figure.KNIGHT) {
                                if (field[selectedI][selectedJ].color == Color.WHITE) {
                                    if ((j == selectedJ + 1 || j == selectedJ - 1) && (i == selectedI + 2 || i == selectedI - 2) ||
                                            (j == selectedJ + 2 || j == selectedJ - 2) && (i == selectedI + 1 || i == selectedI - 1) &&
                                                    field[i][j].color != Color.WHITE) {
                                        try {
                                            doChanges(i, j, button, Figure.KNIGHT, 0);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                } else if ((j == selectedJ + 1 || j == selectedJ - 1) && (i == selectedI + 2 || i == selectedI - 2) ||
                                        (j == selectedJ + 2 || j == selectedJ - 2) && (i == selectedI + 1 || i == selectedI - 1) &&
                                                field[i][j].color != Color.BLACK) {
                                    try {
                                        doChanges(i, j, button, Figure.KNIGHT, 0);
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }

                            //TODO логика офицера
                            else if (field[selectedI][selectedJ].figure == Figure.BISHOP) {
                                if (field[selectedI][selectedJ].color == Color.WHITE) {
                                    if (Math.abs(selectedI - i) == Math.abs(selectedJ - j) && field[i][j].color != Color.WHITE && noObstacleForBishop(i, j)) {
                                        try {
                                            doChanges(i, j, button, Figure.BISHOP, 0);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                } else if (Math.abs(selectedI - i) == Math.abs(selectedJ - j) && field[i][j].color != Color.BLACK && noObstacleForBishop(i, j)) {
                                    try {
                                        doChanges(i, j, button, Figure.BISHOP, 0);
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                            //TODO логика ферзя
                            else if (field[selectedI][selectedJ].figure == Figure.QUEEN) {
                                if (field[selectedI][selectedJ].color == Color.WHITE) {
                                    if (((j == selectedJ && i != selectedI && noObstacleForCastleI(i, j)) || (j != selectedJ && i == selectedI && noObstacleForCastleJ(i, j)))
                                            && field[i][j].color != Color.WHITE) {
                                        try {
                                            doChanges(i, j, button, Figure.QUEEN, 0);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    } else if (Math.abs(selectedI - i) == Math.abs(selectedJ - j) && field[i][j].color != Color.WHITE && noObstacleForBishop(i, j)) {
                                        try {
                                            doChanges(i, j, button, Figure.QUEEN, 0);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                } else if (((j == selectedJ && i != selectedI && noObstacleForCastleI(i, j)) || (j != selectedJ && i == selectedI && noObstacleForCastleJ(i, j)))
                                        && field[i][j].color != Color.BLACK) {
                                    try {
                                        doChanges(i, j, button, Figure.QUEEN, 0);
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                } else if (Math.abs(selectedI - i) == Math.abs(selectedJ - j) && field[i][j].color != Color.BLACK && noObstacleForBishop(i, j)) {
                                    try {
                                        doChanges(i, j, button, Figure.QUEEN, 0);
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }

                            //TODO локига короля
                            else if (field[selectedI][selectedJ].figure == Figure.KING) {
                                if (field[selectedI][selectedJ].color == Color.WHITE) {
                                    if (!whiteKingMotion && i == selectedI && j == selectedJ + 2 && !whiteRightCastleMotion && field[i][j].figure == Figure.NON && selectedI == 7 && selectedJ == 4) {
//                                        doRockerovka(i, j, button, -1, 1);
                                        try {
                                            doChanges(i, j, button, Figure.NON, -1);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                        whiteKingMotion = true;
                                        whiteRightCastleMotion = true;
                                    } else if (!whiteKingMotion && i == selectedI && j == selectedJ - 2 && !whiteLeftCastleMotion && field[i][j].figure == Figure.NON && selectedI == 7 && selectedJ == 4) {
//                                        doRockerovka(i, j, button, 2, 0);
                                        try {
                                            doChanges(i, j, button, Figure.NON, 2);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                        whiteKingMotion = true;
                                        whiteLeftCastleMotion = true;
                                    } else if ((Math.abs(i - selectedI) == 1 && Math.abs(j - selectedJ) == 1 || Math.abs(i - selectedI) == 0 && Math.abs(j - selectedJ) == 1 || Math.abs(i - selectedI) == 1 && Math.abs(j - selectedJ) == 0)) {
                                        if (((j == selectedJ && i != selectedI && noObstacleForCastleI(i, j)) || (j != selectedJ && i == selectedI && noObstacleForCastleJ(i, j)))
                                                && field[i][j].color != Color.WHITE) {
                                            whiteKingMotion = true;
                                            try {
                                                doChanges(i, j, button, Figure.KING, 0);
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }
                                        } else if (Math.abs(selectedI - i) == Math.abs(selectedJ - j) && field[i][j].color != Color.WHITE && noObstacleForBishop(i, j)) {
                                            whiteKingMotion = true;
                                            try {
                                                doChanges(i, j, button, Figure.KING, 0);
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                    }
                                } else if (!blackKingMotion && i == selectedI && j == selectedJ + 2 && !blackRightCastleMotion && field[i][j].figure == Figure.NON && selectedI == 0 && selectedJ == 4) {
//                                    doRockerovka(i, j, button, -1, 1);
                                    try {
                                        doChanges(i, j, button, Figure.NON, -1);
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                    blackKingMotion = true;
                                    blackRightCastleMotion = true;
                                } else if (!blackKingMotion && i == selectedI && j == selectedJ - 2 && !blackLeftCastleMotion && field[i][j].figure == Figure.NON && selectedI == 0 && selectedJ == 4) {
//                                    doRockerovka(i, j, button, 2, 0);
                                    try {
                                        doChanges(i, j, button, Figure.NON, 2);
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                    blackKingMotion = true;
                                    blackLeftCastleMotion = true;
                                } else if ((Math.abs(i - selectedI) == 1 && Math.abs(j - selectedJ) == 1 || Math.abs(i - selectedI) == 0 && Math.abs(j - selectedJ) == 1 || Math.abs(i - selectedI) == 1 && Math.abs(j - selectedJ) == 0))
                                    if (((j == selectedJ && i != selectedI && noObstacleForCastleI(i, j)) || (j != selectedJ && i == selectedI && noObstacleForCastleJ(i, j)))
                                            && field[i][j].color != Color.BLACK) {
                                        blackKingMotion = true;
                                        try {
                                            doChanges(i, j, button, Figure.KING, 0);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    } else if (Math.abs(selectedI - i) == Math.abs(selectedJ - j) && field[i][j].color != Color.BLACK && noObstacleForBishop(i, j)) {
                                        blackKingMotion = true;
                                        try {
                                            doChanges(i, j, button, Figure.KING, 0);
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                            }
//                            if (turn!=yourTurn) {
//                                pw.write(i * 1000 + j * 100 + selectedI * 10 + selectedJ);
//                                pw.flush();
////                                turn ^= 1;
//                                Integer res = null;
//                                try {
//                                    res = is.read();
//                                } catch (IOException e1) {
//                                    e1.printStackTrace();
//                                }
//                                String s = res.toString();
//                                int i1 = Character.getNumericValue(s.charAt(0));
//                                int j1 = Character.getNumericValue(s.charAt(1));
//                                int selectedI1 = Character.getNumericValue(s.charAt(2));
//                                int selectedJ1 = Character.getNumericValue(s.charAt(3));
//                                field[i1][j1].figure = field[selectedI1][selectedJ1].figure;
//                                field[selectedI1][selectedJ1].figure = Figure.NON;
//                                field[i1][j1].color = field[selectedI1][selectedJ1].color;
//                                field[selectedI1][selectedJ1].color = Color.NON;
//                                setImageForButton(getButton(selectedI1, selectedJ1), selectedI1, selectedJ1);
//                                setImageForButton(getButton(i1, j1), i1, j1);
//                                turn ^= 1;
//                            }
                            selected = null;
                        }

                    }
                });
                jb.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) {

                    }

                    @Override
                    public void mousePressed(MouseEvent e) {

                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {

                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                    }
                });

                add(jb);

            }
        }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        if (yourTurn != turn) {
            Integer a = is.read();
            String s = a.toString();
            int i1 = Character.getNumericValue(s.charAt(0));
            if (i1 == 9) {
                i1 = 0;
            }
            int j1 = Character.getNumericValue(s.charAt(1));
            int selectedI1 = Character.getNumericValue(s.charAt(2));
            int selectedJ1 = Character.getNumericValue(s.charAt(3));
            field[i1][j1].figure = field[selectedI1][selectedJ1].figure;
            field[selectedI1][selectedJ1].figure = Figure.NON;
            field[i1][j1].color = field[selectedI1][selectedJ1].color;
            field[selectedI1][selectedJ1].color = Color.NON;
            setImageForButton(getButton(selectedI1, selectedJ1), selectedI1, selectedJ1);
            setImageForButton(getButton(i1, j1), i1, j1);
            turn ^= 1;
        }
    }

    private boolean yourTurn() {
        return yourTurn == (int) turn;
    }

    private void generateField() {
        field = new FigureColor[SIZE][SIZE];
        for (int j = 0; j < SIZE; j++) {
            field[1][j] = new FigureColor(Figure.PONE, Color.BLACK);
            field[6][j] = new FigureColor(Figure.PONE, Color.WHITE);
        }
        field[0][0] = new FigureColor(Figure.CASTLE, Color.BLACK);
        field[0][7] = new FigureColor(Figure.CASTLE, Color.BLACK);
        field[7][0] = new FigureColor(Figure.CASTLE, Color.WHITE);
        field[7][7] = new FigureColor(Figure.CASTLE, Color.WHITE);
        field[0][1] = new FigureColor(Figure.KNIGHT, Color.BLACK);
        field[0][6] = new FigureColor(Figure.KNIGHT, Color.BLACK);
        field[7][1] = new FigureColor(Figure.KNIGHT, Color.WHITE);
        field[7][6] = new FigureColor(Figure.KNIGHT, Color.WHITE);
        field[0][2] = new FigureColor(Figure.BISHOP, Color.BLACK);
        field[0][5] = new FigureColor(Figure.BISHOP, Color.BLACK);
        field[7][2] = new FigureColor(Figure.BISHOP, Color.WHITE);
        field[7][5] = new FigureColor(Figure.BISHOP, Color.WHITE);
        field[0][3] = new FigureColor(Figure.QUEEN, Color.BLACK);
        field[7][3] = new FigureColor(Figure.QUEEN, Color.WHITE);
        field[0][4] = new FigureColor(Figure.KING, Color.BLACK);
        field[7][4] = new FigureColor(Figure.KING, Color.WHITE);
        for (int i = 2; i < SIZE - 2; i++) {
            for (int j = 0; j < SIZE; j++) {
                field[i][j] = new FigureColor(Figure.NON, Color.NON);
            }
        }
    }

    private void setImageForButton(JButton jb, int i, int j) {
        String figure = "/icons/";
        if (field[i][j].figure == Figure.PONE) {
            figure += "Pone";
        } else if (field[i][j].figure == Figure.KNIGHT) {
            figure += "Knight";
        } else if (field[i][j].figure == Figure.CASTLE) {
            figure += "Castle";
        } else if (field[i][j].figure == Figure.BISHOP) {
            figure += "Bishop";
        } else if (field[i][j].figure == Figure.QUEEN) {
            figure += "Queen";
        } else if (field[i][j].figure == Figure.KING) {
            figure += "King";
        }
        if (field[i][j].color == Color.WHITE) {
            figure += "-white.png";
        } else {
            figure += "-black.png";
        }
        if (figure.length() >= 20) {
            jb.setIcon(new ImageIcon(this.getClass().getResource(figure)));
        } else {
            jb.setIcon(null);
        }
    }

    private String sendAndGet(Integer s) throws IOException {
        pw.write(s);
        pw.flush();
        turn ^= 1;
        Integer res = is.read();
        String s1 = res.toString();
        return s1;
    }

    private void doChanges(int i, int j, JButton button, Figure figure, int side) throws IOException {
//        field[i][j].figure = figure;
//        field[selectedI][selectedJ].figure = Figure.NON;
//        field[i][j].color = field[selectedI][selectedJ].color;
//        field[selectedI][selectedJ].color = Color.NON;
//        setImageForButton(selected, selectedI, selectedJ);
//        setImageForButton(button, i, j);
//        Integer s1 = i * 1000 + j * 100 + selectedI * 10 + selectedJ;
//                        turn^=1;
//        pw.write(s1);
//        pw.flush();
//        turn ^= 1;
//        Integer res = is.read();
        Integer s1;
        if (side == 0) {
            s1 = doStep(i, j, button, figure);
        } else {
            s1 = doRockerovka(i, j, button, side);
        }
        System.out.println("here" + s1);
        System.out.println(yourTurn + " waiting opponet");
        String s = sendAndGet(s1);
        if (s.length() < 5) {
            int i1 = Character.getNumericValue(s.charAt(0));
            if (i1 == 9) {
                i1 = 0;
            }
            int j1 = Character.getNumericValue(s.charAt(1));
            selectedI = Character.getNumericValue(s.charAt(2));
            selectedJ = Character.getNumericValue(s.charAt(3));
            selected = getButton(selectedI, selectedJ);
            doStep(i1, j1, getButton(i1, j1), field[selectedI][selectedJ].figure);
        } else {
            int i1 = Character.getNumericValue(s.charAt(1));
            if (i1 == 9) {
                i1 = 0;
            }
            int j1 = Character.getNumericValue(s.charAt(2));
            selectedI = Character.getNumericValue(s.charAt(3));
            selectedJ = Character.getNumericValue(s.charAt(4));
            selected = getButton(selectedI, selectedJ);
            int sideG = Character.getNumericValue(s.charAt(0));
            if (sideG == 1) {
                sideG *= -1;
            }
            System.out.println("получил и делаю рокеровочку " + j1 + "  " + sideG);
            doRockerovka(i1, j1, getButton(i1, j1), sideG);
        }
//        int i1 = Character.getNumericValue(s.charAt(0));
//        int j1 = Character.getNumericValue(s.charAt(1));
//        int selectedI1 = Character.getNumericValue(s.charAt(2));
//        int selectedJ1 = Character.getNumericValue(s.charAt(3));
//        field[i1][j1].figure = field[selectedI1][selectedJ1].figure;
//        field[selectedI1][selectedJ1].figure = Figure.NON;
//        field[i1][j1].color = field[selectedI1][selectedJ1].color;
//        field[selectedI1][selectedJ1].color = Color.NON;
//        setImageForButton(getButton(selectedI1, selectedJ1), selectedI1, selectedJ1);
//        setImageForButton(getButton(i1, j1), i1, j1);
        turn ^= 1;

    }

    private JButton getButton(int i, int j) {
        return (JButton) getContentPane().getComponent((i) * 8 + j);
    }

    private Boolean noObstacleForCastleI(int i, int j) {
        Boolean res = true;
        if (i < selectedI) {
            for (int k = i + 1; k < selectedI; k++) {
                if (field[k][j].figure != Figure.NON) {
                    res = false;
                }
            }
        } else {
            for (int k = selectedI + 1; k < i; k++) {
                if (field[k][j].figure != Figure.NON) {
                    res = false;
                }
            }
        }
        return res;
    }

    private Boolean noObstacleForCastleJ(int i, int j) {
        Boolean res = true;
        if (j < selectedJ) {
            for (int k = j + 1; k < selectedJ; k++) {
                if (field[i][k].figure != Figure.NON) {
                    res = false;
                }
            }
        } else {
            for (int k = selectedJ + 1; k < j; k++) {
                if (field[i][k].figure != Figure.NON) {
                    res = false;
                }
            }
        }
        return res;
    }

    private Boolean noObstacleForBishop(int i, int j) {
        Boolean res = true;
        if (i - selectedI == j - selectedJ) {
            if (i > selectedI) {
                for (int k = 1; k < Math.abs(selectedI - i); k++) {
                    if (field[(i - k)][(j - k)].figure != Figure.NON) {
                        res = false;
                    }
                }
            } else {
                for (int k = 1; k < Math.abs(i - selectedI); k++) {
                    if (field[(i + k)][(j + k)].figure != Figure.NON) {
                        res = false;
                    }
                }
            }
        } else {
            if (i > selectedI) {
                for (int k = 1; k < Math.abs(selectedI - i); k++) {
                    if (field[(i - k)][(j + k)].figure != Figure.NON) {
                        res = false;
                    }
                }
            } else {
                for (int k = 1; k < Math.abs(i - selectedI); k++) {
                    if (field[(i + k)][(j - k)].figure != Figure.NON) {
                        res = false;
                    }
                }
            }
        }
        return res;
    }

    private Integer doRockerovka(int i, int j, JButton button, int side) {
        System.out.println("рокеровка бля");
        System.out.println(i + " " + j + " " + side);
        field[selectedI][selectedJ].figure = Figure.NON;
        field[i][j].color = field[selectedI][selectedJ].color;
        field[selectedI][selectedJ].color = Color.NON;
        field[i][j].figure = Figure.KING;
        setImageForButton(selected, selectedI, selectedJ);
        setImageForButton(button, i, j);
        field[i][j - side].figure = Figure.NON;
        field[i][j + (int) Math.signum(side)].color = field[i][j - side].color;
        field[i][j + (int) Math.signum(side)].figure = Figure.CASTLE;
        field[i][j - side].color = Color.NON;
//        setImageForButton(((JButton) getContentPane().getComponent((i) * 8 + (j + side))), i, j + side);
        setImageForButton(((JButton) getContentPane().getComponent((i) * 8 + (j + (int) Math.signum(side)))), i, j + (int) Math.signum(side));
        setImageForButton(((JButton) getContentPane().getComponent((i) * 8 + j - side)), i, j - side);
//        turn ^= 1;
        Integer s1 = Math.abs(side) * 10000 + (i * 1000 + j * 100 + selectedI * 10 + selectedJ);
        System.out.println(s1);
        return s1;
    }

    private Integer doStep(int i, int j, JButton button, Figure figure) {
        field[i][j].figure = figure;
        field[selectedI][selectedJ].figure = Figure.NON;
        field[i][j].color = field[selectedI][selectedJ].color;
        field[selectedI][selectedJ].color = Color.NON;
        setImageForButton(selected, selectedI, selectedJ);
        setImageForButton(button, i, j);
        if (i == 0) {
            i = 9;
        }
        Integer s1 = i * 1000 + j * 100 + selectedI * 10 + selectedJ;
        return s1;
    }

    public static void main(String[] args) throws IOException {
        int port = 3456;
        String host = "localhost";
        Socket s = new Socket(host, port);
        OutputStream os = s.getOutputStream();
        PrintWriter pw = new PrintWriter(os, true);
        InputStream is = s.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        Integer tern = br.read();
        System.out.println(tern);
        if (tern == 48) {
            tern = 0;
        } else {
            tern = 1;
        }
//        boolean flag = true;
//        while (flag) {
//            Integer when = br.read();
//            System.out.println(when);
//            if (when == 50) {
//                flag = false;
//            }
//        }
        System.out.println(tern);
        System.out.println("game starts");
        new Board(pw, br, tern);
    }
}
