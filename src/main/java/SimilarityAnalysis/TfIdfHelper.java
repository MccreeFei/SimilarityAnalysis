package SimilarityAnalysis;

import org.omg.CORBA.INTERNAL;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/6/30.
 */
public class TfIdfHelper {
    //词语map，存放文件名和每个文件词语个数的映射
    private Map<String, Map<String, Integer>> wordMap = new LinkedHashMap<String, Map<String, Integer>>();

    private Map<String, Map<String, Double>> tfMap = new LinkedHashMap<String, Map<String, Double>>();

    //idf=所有文件数/包含该词的文件数再取log
    private Map<String, Double> idfMap = new LinkedHashMap<String, Double>();

    private Map<String, Map<String, Double>> tfIdfMap = new LinkedHashMap<String, Map<String, Double>>();

    //按照allWords顺序重新组织wordMap
    private Map<String, Map<String, Integer>> allWordCountMap = new LinkedHashMap<String, Map<String, Integer>>();

    private Set<String> allWords = new LinkedHashSet<String>();

    public TfIdfHelper(Map<String, Map<String, Integer>> wordMap) {
        this.wordMap = wordMap;
    }

    public void generateAllWords() {
        Iterator<Map<String, Integer>> mapIterator = wordMap.values().iterator();
        while (mapIterator.hasNext()) {
            Map<String, Integer> wordCountMap = mapIterator.next();
            Set<String> keySet = wordCountMap.keySet();
            for (String key : keySet) {
                allWords.add(key);
            }
        }
        generateTfMap();
        generateIdfMap();
        generateTfIdfMap();
    }

    /**
     * 生成tfMap，过程中会生成allWordCountMap用于统计词频
     */
    public void generateTfMap() {
        Iterator<Map.Entry<String, Map<String, Integer>>> fileIterator = wordMap.entrySet().iterator();
        while (fileIterator.hasNext()) {
            Map.Entry<String, Map<String, Integer>> fileEntry = fileIterator.next();
            String fileName = fileEntry.getKey();
            Map<String, Integer> oldMap = fileEntry.getValue();

            int wordNumber = getWordNumber(oldMap);
            Map<String, Integer> allWordMap = new LinkedHashMap<String, Integer>();
            Map<String, Double> tfInsertMap = new LinkedHashMap<String, Double>();

            Iterator<String> allWordsIterator = allWords.iterator();
            while (allWordsIterator.hasNext()) {
                String word = allWordsIterator.next();
                if (oldMap.containsKey(word)) {
                    allWordMap.put(word, oldMap.get(word));
                    tfInsertMap.put(word, (double) oldMap.get(word) / (double) wordNumber);
                } else {
                    allWordMap.put(word, 0);
                    tfInsertMap.put(word, 0.0);
                }
            }

            allWordCountMap.put(fileName, allWordMap);
            tfMap.put(fileName, tfInsertMap);

        }
    }

    public void generateIdfMap(){
        Iterator<Map.Entry<String, Map<String, Double>>> fileIterator = tfMap.entrySet().iterator();
        int fileNumber = getFileNumber();
        while (fileIterator.hasNext()){
            Map.Entry<String, Map<String, Double>> fileEntry = fileIterator.next();
            String fileName = fileEntry.getKey();
            Map<String, Double> tfInsertMap = fileEntry.getValue();
            Map<String, Double> idfInsertMap = new LinkedHashMap<String, Double>();
            Iterator<String> keyIterator = tfInsertMap.keySet().iterator();
            while (keyIterator.hasNext()){
                String word = keyIterator.next();
                int containFileNumber = getContainTheWordFileNumber(word);
                idfMap.put(word, Math.log((double)fileNumber / (double)containFileNumber));
            }
        }
    }

    public void generateTfIdfMap(){
        Iterator<Map.Entry<String, Map<String, Double>>> fileIterator = tfMap.entrySet().iterator();
        while (fileIterator.hasNext()){
            Map.Entry<String, Map<String, Double>> fileEntry = fileIterator.next();
            String fileName = fileEntry.getKey();
            Map<String, Double> tfInsertMap = fileEntry.getValue();
            Map<String, Double> tfIdfInsertMap = new LinkedHashMap<String, Double>();

            Iterator<String> wordKeyIterator = tfInsertMap.keySet().iterator();
            while (wordKeyIterator.hasNext()){
                String word = wordKeyIterator.next();
                double tfIdf = tfInsertMap.get(word) * idfMap.get(word);
                tfIdfInsertMap.put(word, tfIdf);
            }

            tfIdfMap.put(fileName, tfIdfInsertMap);
        }
    }

    /**
     * 获取词频表格的表头
     * @return
     */
    public String[] getWordFrequencyTableTitle(){
        int size = getFileNumber() * 2 + 1;
        String[] res = new String[size];
        res[0] = "Word";
        int index = 1;

        Iterator<String> fileNameIterator = tfIdfMap.keySet().iterator();
        while (fileNameIterator.hasNext()){
            String fileName = fileNameIterator.next();
            res[index++] = fileName + "词频";
            res[index++] = fileName + "加权";
        }

        return res;
    }

    /**
     * 获取词频表格表数据
     * @return
     */
    public String[][] getWordFrequencyTableData(){
        int row = allWords.size();
        int column = getFileNumber() * 2 + 1;
        String[][] res = new String[row][column];
        //第一列添加分词名称
        int i = 0;
        Iterator<String> wordIterator = allWords.iterator();
        while (wordIterator.hasNext()){
            res[i++][0] = wordIterator.next();
        }
        //单数列添加词频
        int j = 1;
        Iterator<Map.Entry<String, Map<String, Integer>>> entryIterator = allWordCountMap.entrySet().iterator();
        while (entryIterator.hasNext()){
            Iterator<Integer> wordNumberIterator = entryIterator.next().getValue().values().iterator();
            int wordIndex = 0;
            while (wordNumberIterator.hasNext()){
                Integer number = wordNumberIterator.next();
                res[wordIndex++][j] = String.valueOf(number);
            }
            j += 2;
        }
        //偶数列添加加权值
        int k = 2;
        Iterator<Map.Entry<String, Map<String, Double>>> tfIdfEntryIterator = tfIdfMap.entrySet().iterator();
        while (tfIdfEntryIterator.hasNext()){
            Iterator<Double> tfIdfIterator = tfIdfEntryIterator.next().getValue().values().iterator();
            int tfIdfWordIndex = 0;
            while (tfIdfIterator.hasNext()){
                Double tfIdfValue = tfIdfIterator.next();
                DecimalFormat df = new DecimalFormat("0.0000");
                res[tfIdfWordIndex++][k] = df.format(tfIdfValue);
            }
            k += 2;
        }
        return res;

    }

    /**
     * 获取相似度表格的表头
     * @return
     */
    public String[] getSimilarityTableTitle(){
        int size = getFileNumber() + 1;
        String[] res = new String[size];
        res[0] = "Article";
        int i = 1;
        Iterator<String> fileNameIterator = tfIdfMap.keySet().iterator();
        while (fileNameIterator.hasNext()){
            String fileName = fileNameIterator.next();
            res[i++] = fileName;
        }

        return res;
    }

    /**
     * 获取相似度表格的表数据
     * @return
     */
    public String[][] getSimilarityTableData(){
        int size = getFileNumber();
        String[][] res = new String[size][size+1];
        String[] fileNameArray = new String[size];
        //第一列显示文件名
        int i = 0;
        Iterator<String> fileNameIterator = tfIdfMap.keySet().iterator();
        while (fileNameIterator.hasNext()){
            String fileName = fileNameIterator.next();
            fileNameArray[i] = fileName;
            res[i++][0] = fileName;
        }

        for (int j = 0; j < fileNameArray.length; j++){
            for (int k = 0; k < fileNameArray.length; k++){
                double temp = countCos(fileNameArray[j], fileNameArray[k]);
                DecimalFormat df = new DecimalFormat("0.0000");
                String data = df.format(temp);
                res[j][k+1] = data;
            }
        }
        return res;
    }

    private double countCos(String aFileName, String bFileName){
        Map<String, Double> aFileNameWordMap = tfIdfMap.get(aFileName);
        Map<String, Double> bFileNameWordMap = tfIdfMap.get(bFileName);

        double aVectorLength = countVectorLength(aFileNameWordMap.values());
        double bVectorLength = countVectorLength(bFileNameWordMap.values());

        //向量乘积
        double vectorProduct = countVectorProduct(aFileNameWordMap.values(), bFileNameWordMap.values());

        double res = vectorProduct / (aVectorLength * bVectorLength);

        return res;
    }

    /**
     * 计算向量的模
     * @param vector
     * @return
     */
    private double countVectorLength(Collection<Double> vector){
        Iterator<Double> iterator = vector.iterator();
        double res = 0.0;
        while (iterator.hasNext()){
            double value = iterator.next();
            res += value * value;
        }
        return Math.sqrt(res);
    }

    /**
     * 计算两个向量的乘积
     * @param aVector
     * @param bVector
     * @return
     */
    private double countVectorProduct(Collection<Double> aVector, Collection<Double> bVector){
        Iterator<Double> aIterator = aVector.iterator();
        Iterator<Double> bIterator = bVector.iterator();
        double res = 0.0;

        while (aIterator.hasNext()){
            res += aIterator.next() * bIterator.next();
        }

        return res;
    }



    public int getFileNumber() {
        return wordMap.keySet().size();
    }

    /**
     * 得到包含该词语的文件数量
     * @param word
     * @return
     */
    public int getContainTheWordFileNumber(String word){
        int res = 0;
        Iterator<Map<String, Integer>> mapIterator = wordMap.values().iterator();
        while (mapIterator.hasNext()){
            Map<String, Integer> wordCountMap = mapIterator.next();
            if (wordCountMap.containsKey(word)) res++;
        }
        return res;
    }

    /**
     * 得到所有单词数量
     * @param fileWordMap
     * @return
     */
    public int getWordNumber(Map<String, Integer> fileWordMap) {
        int res = 0;
        Iterator<Integer> numberIterator = fileWordMap.values().iterator();
        while (numberIterator.hasNext()) {
            res += numberIterator.next();
        }
        return res;
    }
}
