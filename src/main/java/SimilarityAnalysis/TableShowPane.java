package SimilarityAnalysis;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * Created by Administrator on 2017/7/1.
 */
public class TableShowPane {
    private String[] columnNames;
    private Object[][] tableData;

    public TableShowPane(String[] columnNames, Object[][] tableData){
        this.columnNames = columnNames;
        this.tableData = tableData;

    }

    public JScrollPane initComponent(){
        JTable table = new JTable(tableData, columnNames);
        //设置table中数据居中
        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        tcr.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, tcr);

        /*
		 * 设置JTable的列默认的宽度和高度
		 */
        TableColumn column = null;
        int colunms = table.getColumnCount();
        for(int i = 0; i < colunms; i++)
        {
            column = table.getColumnModel().getColumn(i);
			/*将每一列的默认宽度设置为100*/
            column.setPreferredWidth(100);
        }
		/*
		 * 设置JTable自动调整列表的状态，此处设置为关闭
		 */
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);


        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setSize(576, 262);

        return scrollPane;
    }


}
