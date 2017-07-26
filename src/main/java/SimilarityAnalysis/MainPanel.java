/*
 * Created by JFormDesigner on Thu Jun 29 12:33:39 CST 2017
 */

package SimilarityAnalysis;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author MccreeFei
 */
public class MainPanel extends JPanel{
    private WordSplit wordSplit = new WordSplit();
    private TfIdfHelper tfIdfHelper;
    private String[] filePaths;
    String[] similarityTableTitle;
    String[][] similarityTableData;

    public MainPanel() {
        initComponents();
    }

    //浏览按钮触发
    private void searchButtonActionPerformed(ActionEvent e) {
        //将文件选择器设置为windows样式
        if(UIManager.getLookAndFeel().isSupportedLookAndFeel()){
            final String platform = UIManager.getSystemLookAndFeelClassName();
            // If the current Look & Feel does not match the platform Look & Feel,
            // change it so it does.
            if (!UIManager.getLookAndFeel().getName().equals(platform)) {
                try {
                    UIManager.setLookAndFeel(platform);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
        JFileChooser fileChooser = new JFileChooser("." + File.separator + "testDoc");
        //设置文件过滤器过滤TXT文本
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                if (f.getName().endsWith(".txt")) return true;
                return false;
            }

            @Override
            public String getDescription() {
                return null;
            }
        });

        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.showOpenDialog(fileChooser);
        docLocationTextArea.setText("");

        File[] selectedFiles = fileChooser.getSelectedFiles();
        for (File f : selectedFiles){
            docLocationTextArea.append(f.getAbsolutePath() + "\n");
        }

        String docLocationText = docLocationTextArea.getText();
        if (docLocationText.trim().equals("")){
            JOptionPane.showMessageDialog(this, "请选择要分词的文件！");
            return;
        }

        //从文本输入框提取需要分词的文件路径
        filePaths = docLocationText.split("\\n");
        //需要分词的文件名
        String[] fileNames = WordSplit.getFileNamesFromFilePaths(filePaths);

        //执行分词，保存到splitWordFilePaths
        wordSplit.doSplitFiles(filePaths);


        //计算分词文件中每个单词的个数
        wordSplit.doWordCountFiles(fileNames);

        //去除停用词
        wordSplit.removeStopWords();

        tfIdfHelper = new TfIdfHelper(wordSplit.wordMap);

        //生成所有单词并计算tf_idf
        tfIdfHelper.generateAllWords();

    }

    //分词按钮触发
    private void splitWordButtonActionPerformed(ActionEvent e) {
        if (filePaths == null){
            JOptionPane.showMessageDialog(this, "请先选择要分词的文件！");
            return;
        }
        //需要分词的文件名
        String[] fileNames = WordSplit.getFileNamesFromFilePaths(filePaths);
        //分词后的保存路径
        String[] splitWordFilePaths = WordSplit.getSplitWordFilePaths(filePaths);


        //将分词文件合并成一个文件保存
        JFileChooser fileChooser = new JFileChooser("." + File.separator + "testDoc");
        fileChooser.showSaveDialog(fileChooser);

        File saveFile = fileChooser.getSelectedFile();
        try {
            FileWriter fw = new FileWriter(saveFile);
            for (String filePath : splitWordFilePaths){
                String fileText = WordSplit.readFile(filePath);
                fw.append(fileText + "\r\n\r\n-----------------\r\n\r\n");
            }
            fw.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        JOptionPane.showMessageDialog(this, "分词文本导出成功！");

    }

    //词频权重分析触发
    private void wordFrequencyButtonActionPerformed(ActionEvent e) {
        if (tfIdfHelper == null) {
            JOptionPane.showMessageDialog(this, "请先选择要分词的文件！");
            return;
        }
        String[] wordFrequencyTableTitle = tfIdfHelper.getWordFrequencyTableTitle();
        String[][] wordFrequencyTableData = tfIdfHelper.getWordFrequencyTableData();

        TableShowPane tableShowPane = new TableShowPane(wordFrequencyTableTitle, wordFrequencyTableData);

        tableShowJPanel.removeAll();
        tableShowJPanel.add(tableShowPane.initComponent());
        tableShowJPanel.repaint();
    }

    //相似度比较按钮触发
    private void similarityButtonActionPerformed(ActionEvent e) {
        if (tfIdfHelper == null) {
            JOptionPane.showMessageDialog(this, "请先选择要分词的文件！");
            return;
        }
        similarityTableTitle = tfIdfHelper.getSimilarityTableTitle();
        similarityTableData = tfIdfHelper.getSimilarityTableData();

        TableShowPane tableShowPane = new TableShowPane(similarityTableTitle, similarityTableData);

        tableShowJPanel.removeAll();
        tableShowJPanel.add(tableShowPane.initComponent());
        tableShowJPanel.repaint();
    }

    private void exportXlsButtonActionPerformed(ActionEvent e) {
        if (similarityTableTitle == null || similarityTableData == null){
            JOptionPane.showMessageDialog(this, "请先进行相似度比较！");
            return;
        }
        JFileChooser fileChooser = new JFileChooser("." + File.separator + "testDoc");
        fileChooser.showSaveDialog(fileChooser);

        File saveFile = fileChooser.getSelectedFile();
        PoiHelper.exportXlsToFile(similarityTableTitle, similarityTableData, saveFile);
        JOptionPane.showMessageDialog(this, "导出成功！");

    }

    private void exportTxtButtonActionPerformed(ActionEvent e) {
        if (similarityTableTitle == null || similarityTableData == null){
            JOptionPane.showMessageDialog(this, "请先进行相似度比较！");
            return;
        }
        JFileChooser fileChooser = new JFileChooser("." + File.separator + "testDoc");
        fileChooser.showSaveDialog(fileChooser);

        File saveFile = fileChooser.getSelectedFile();
        PoiHelper.exportTxtToFile(similarityTableTitle, similarityTableData, saveFile);
        JOptionPane.showMessageDialog(this, "导出成功！");
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - mccreefei mccreefei
        panel1 = new JPanel();
        scrollPane1 = new JScrollPane();
        docLocationTextArea = new JTextArea();
        panel2 = new JPanel();
        searchButton = new JButton();
        splitWordButton = new JButton();
        wordFrequencyButton = new JButton();
        similarityButton = new JButton();
        exportTxtButton = new JButton();
        exportXlsButton = new JButton();
        label1 = new JLabel();
        tableShowJPanel = new JPanel();

        //======== this ========

        // JFormDesigner evaluation mark
        setBorder(new javax.swing.border.CompoundBorder(
            new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

        setLayout(new GridLayout(2, 1, 0, 5));

        //======== panel1 ========
        {
            panel1.setLayout(null);

            //======== scrollPane1 ========
            {

                //---- docLocationTextArea ----
                docLocationTextArea.setEditable(false);
                scrollPane1.setViewportView(docLocationTextArea);
            }
            panel1.add(scrollPane1);
            scrollPane1.setBounds(10, 30, 415, 160);

            //======== panel2 ========
            {
                panel2.setLayout(new GridLayout(6, 1, 0, 3));

                //---- searchButton ----
                searchButton.setText("\u6d4f\u89c8");
                searchButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        searchButtonActionPerformed(e);
                    }
                });
                panel2.add(searchButton);

                //---- splitWordButton ----
                splitWordButton.setText("\u5206\u8bcd");
                splitWordButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        splitWordButtonActionPerformed(e);
                    }
                });
                panel2.add(splitWordButton);

                //---- wordFrequencyButton ----
                wordFrequencyButton.setText("\u8bcd\u9891\u6743\u91cd\u5206\u6790");
                wordFrequencyButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        wordFrequencyButtonActionPerformed(e);
                    }
                });
                panel2.add(wordFrequencyButton);

                //---- similarityButton ----
                similarityButton.setText("\u76f8\u4f3c\u5ea6\u6bd4\u8f83");
                similarityButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        similarityButtonActionPerformed(e);
                    }
                });
                panel2.add(similarityButton);

                //---- exportTxtButton ----
                exportTxtButton.setText("\u7ed3\u679c\u5bfc\u51fa(TXT)");
                exportTxtButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        exportTxtButtonActionPerformed(e);
                    }
                });
                panel2.add(exportTxtButton);

                //---- exportXlsButton ----
                exportXlsButton.setText("\u7ed3\u679c\u5bfc\u51fa(XLS)");
                exportXlsButton.setActionCommand("\u7ed3\u679c\u5bfc\u51fa(XLS)");
                exportXlsButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        exportXlsButtonActionPerformed(e);
                    }
                });
                panel2.add(exportXlsButton);
            }
            panel1.add(panel2);
            panel2.setBounds(445, 15, 125, 185);

            //---- label1 ----
            label1.setText("\u6587\u4ef6\u5217\u8868");
            panel1.add(label1);
            label1.setBounds(10, 5, 415, 25);

            { // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel1.getComponentCount(); i++) {
                    Rectangle bounds = panel1.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel1.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel1.setMinimumSize(preferredSize);
                panel1.setPreferredSize(preferredSize);
            }
        }
        add(panel1);

        //======== tableShowJPanel ========
        {
            tableShowJPanel.setLayout(null);

            { // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < tableShowJPanel.getComponentCount(); i++) {
                    Rectangle bounds = tableShowJPanel.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = tableShowJPanel.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                tableShowJPanel.setMinimumSize(preferredSize);
                tableShowJPanel.setPreferredSize(preferredSize);
            }
        }
        add(tableShowJPanel);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

        //add listener
//        searchButton.addActionListener(this);
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - mccreefei mccreefei
    private JPanel panel1;
    private JScrollPane scrollPane1;
    private JTextArea docLocationTextArea;
    private JPanel panel2;
    private JButton searchButton;
    private JButton splitWordButton;
    private JButton wordFrequencyButton;
    private JButton similarityButton;
    private JButton exportTxtButton;
    private JButton exportXlsButton;
    private JLabel label1;
    private JPanel tableShowJPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
