package SimilarityAnalysis;

import javax.swing.*;

/**
 * Created by Administrator on 2017/6/29.
 */
public class MainFrame extends JFrame {
    private MainPanel mainPanel = new MainPanel();

    public void init(){
        this.setTitle("相似度分析");
        this.setSize(590, 580);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(400, 400);
        this.setResizable(false);
        this.add(mainPanel);
    }

    public static void main(String[] args) {
        new MainFrame().init();
    }
}
