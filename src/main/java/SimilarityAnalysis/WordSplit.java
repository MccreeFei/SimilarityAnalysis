package SimilarityAnalysis;

import com.sun.jna.Library;
import com.sun.jna.Native;

import java.io.*;
import java.util.*;

/**
 * Created by Administrator on 2017/6/29.
 */
public class WordSplit {
    //NLPIR.dll和NLPIR.lib的存放路径
    private final static String NLPIR_SOURCE_PATH = "." + File.separator + "source" + File.separator + "NLPIR";

    //每个文本的分词文件存放位置
    private final static String EACH_SPLIT_WORD_FILE_BASE_PATH = "." + File.separator + "splitWordFile";

    //停用词存放路径
    private final static String STOP_WORDS_FILE_PATH = "." + File.separator + "StopWord.txt";

    //词语map，存放文件名和每个文件词语个数的映射
    public Map<String, Map<String, Integer>> wordMap = new LinkedHashMap<String, Map<String, Integer>>();


    public interface CLibrary extends Library {
        CLibrary Instance = (CLibrary) Native.loadLibrary(NLPIR_SOURCE_PATH, CLibrary.class);

        //初始化
        public boolean NLPIR_Init(byte[] sDataPath, int encoding, byte[] sLicenceCode);

        //对字符串进行分词
        public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);

        //对TXT文件内容进行分词
        public double NLPIR_FileProcess(String sSourceFilename, String sResultFilename, int bPOStagged);

        //从字符串中提取关键词
        public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);

        //从TXT文件中提取关键词
        public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);

        //添加单条用户词典
        public int NLPIR_AddUserWord(String sWord);

        //删除单条用户词典
        public int NLPIR_DelUsrWord(String sWord);

        //从TXT文件中导入用户词典
        public int NLPIR_ImportUserDict(String sFilename);

        //将用户词典保存至硬盘
        public int NLPIR_SaveTheUsrDic();

        //从字符串中获取新词
        public String NLPIR_GetNewWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);

        //从TXT文件中获取新词
        public String NLPIR_GetFileNewWords(String sTextFile, int nMaxKeyLimit, boolean bWeightOut);

        //获取一个字符串的指纹值
        public long NLPIR_FingerPrint(String sLine);

        //设置要使用的POS map
        public int NLPIR_SetPOSmap(int nPOSmap);

        //获取报错日志
        public String NLPIR_GetLastErrorMsg();

        //退出
        public void NLPIR_Exit();
    }



    public WordSplit(){
        String argu = "";
        String system_charset = "GBK";//GBK----0
//        String system_charset = "UTF-8";
//        int charset_type = 1;
        int charset_type = 0;
        // 调用printf打印信息
        try {
            if (!CLibrary.Instance.NLPIR_Init(argu.getBytes(system_charset),
                    charset_type, "0".getBytes(system_charset))) {
                System.err.println("初始化失败！");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void doWordCountFiles(String[] fileNames){
        for (String fileName : fileNames){
            String filePath = EACH_SPLIT_WORD_FILE_BASE_PATH + File.separator + fileName;
            Map<String, Integer> wordCountMap = doWordCountOneFile(filePath);
            wordMap.put(fileName, wordCountMap);
        }
    }

    public Map<String, Integer> doWordCountOneFile(String filePath){
        String fileText = readFile(filePath);
        Map<String, Integer> fileWordCount = new HashMap<String, Integer>();

        doWordCount(fileText, fileWordCount);
        return fileWordCount;

    }

    /**
     * 读取filePath文件，转化String文本
     * @param filePath
     * @return
     */
    public static String readFile(String filePath){
        File file = new File(filePath);
        StringBuilder sb = new StringBuilder();

        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "GBK");
             BufferedReader br = new BufferedReader(isr)) {
            String line = null;
            while ((line = br.readLine()) != null){
                sb.append(line + "\n");
            }

        } catch (FileNotFoundException e) {
            System.out.println(filePath + "文件不存在！");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private void doWordCount(String fileText, Map<String, Integer> fileWordCount){
        String regex = "/\\w+";
        String[] words = fileText.split(regex);
        for (String word : words){
            word = word.trim();
            if (!fileWordCount.containsKey(word)){
                fileWordCount.put(word, 1);
            }else {
                fileWordCount.put(word, fileWordCount.get(word) + 1);
            }
        }

    }

    /**
     * 对每个文件路径进行分词
     * @param filePaths
     */
    public void doSplitFiles(String[] filePaths){
        for (String filePath : filePaths){
            doSplitOneFile(filePath);
        }
    }

    /**
     * 给filePath文件分词，并存放到指定目录下
     * @param filePath
     */
    public void doSplitOneFile(String filePath){
        String fileName = getFileNameFromFilePath(filePath);
        String outPutFilePath = EACH_SPLIT_WORD_FILE_BASE_PATH + File.separator + fileName;
        //执行分词
        CLibrary.Instance.NLPIR_FileProcess(filePath, outPutFilePath, 1);
    }

    public static String getFileNameFromFilePath(String filePath){
        int index = filePath.lastIndexOf("\\");
        String fileName = filePath.substring(index + 1);
        return fileName;
    }

    /**
     * 对于每一个文件，找出它的分词文件存放路径
     * @param filePaths
     * @return
     */
    public static String[] getSplitWordFilePaths(String[] filePaths){
        String[] splitWordFilePaths = new String[filePaths.length];
        for (int i = 0; i < filePaths.length; i++) {
            String fileName = getFileNameFromFilePath(filePaths[i]);
            splitWordFilePaths[i] = EACH_SPLIT_WORD_FILE_BASE_PATH + File.separator + fileName;
        }
        return splitWordFilePaths;
    }

    /**
     * 去除停用词
     */
    public void removeStopWords(){
        String stopFileText = readFile(STOP_WORDS_FILE_PATH);
        String[] stopWords = stopFileText.split("\\n");
        Iterator<Map<String, Integer>> mapIterator = wordMap.values().iterator();
        while (mapIterator.hasNext()){
            Map<String, Integer> wordCountMap = mapIterator.next();
            Set<String> keySet = wordCountMap.keySet();
            for (String stopWord : stopWords){
                stopWord = stopWord.trim();
                if (keySet.contains(stopWord))
                    wordCountMap.remove(stopWord);

            }
        }
    }

    public static String[] getFileNamesFromFilePaths(String[] filePaths){
        String[] fileNames = new String[filePaths.length];
        for (int i = 0; i < fileNames.length; i++) {
            fileNames[i] = WordSplit.getFileNameFromFilePath(filePaths[i]);
        }
        return fileNames;
    }

    public static void main(String[] args) {
        String inputFile1 = "E:\\myProject\\SimilarityAnalysis\\testDoc\\11.txt";
        String inputFile2 = "E:\\myProject\\SimilarityAnalysis\\testDoc\\12.txt";
        String inputFile3 = "E:\\myProject\\SimilarityAnalysis\\testDoc\\13.txt";
        String inputFile4 = "E:\\myProject\\SimilarityAnalysis\\testDoc\\14.txt";

        String[] filePaths = new String[]{inputFile1, inputFile2, inputFile3, inputFile4};
        String[] fileNames = WordSplit.getFileNamesFromFilePaths(filePaths);

        WordSplit wordSplit = new WordSplit();

        wordSplit.doSplitFiles(filePaths);

        wordSplit.doWordCountFiles(fileNames);

        wordSplit.removeStopWords();

        TfIdfHelper tfIdfHelper = new TfIdfHelper(wordSplit.wordMap);

        tfIdfHelper.generateAllWords();
    }

}
