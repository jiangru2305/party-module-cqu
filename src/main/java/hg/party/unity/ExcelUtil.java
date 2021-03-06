package hg.party.unity;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.contrib.HSSFCellUtil;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import hg.party.entity.partyMembers.PartyMembers;

public class ExcelUtil{
    public static String NO_DEFINE = "no_define";//未定义的字段
    public static String DEFAULT_DATE_PATTERN="yyyy年MM月dd日";//默认日期格式
    public static int DEFAULT_COLOUMN_WIDTH = 17;
   /**
     * 导出Excel 2007 OOXML (.xlsx)格式
     * @param title 标题行
     * @param headMap 属性-列头
     * @param jsonArray 数据集
     * @param datePattern 日期格式，传null值则默认 年月日
     * @param colWidth 列宽 默认 至少17个字节
     * @param out 输出流
     */
    public static  SXSSFWorkbook exportExcelX(String title,Map<String, String> headMap,JSONArray jsonArray,String datePattern,int colWidth, OutputStream out) {
        if(datePattern==null) datePattern = DEFAULT_DATE_PATTERN;
        // 声明一个工作薄
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);//缓存
        workbook.setCompressTempFiles(true);
        //表头样式
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 20);
        titleFont.setBold(true);
        titleStyle.setFont(titleFont);
        // 列头样式
        CellStyle headerStyle = workbook.createCellStyle();
//        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        // 单元格样式
        CellStyle cellStyle = workbook.createCellStyle();
//        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font cellFont = workbook.createFont();
        cellFont.setBold(false);
        cellStyle.setFont(cellFont);
        // 生成一个(带标题)表格
        SXSSFSheet sheet = workbook.createSheet();
        //设置列宽
        int minBytes = colWidth<DEFAULT_COLOUMN_WIDTH?DEFAULT_COLOUMN_WIDTH:colWidth;//至少字节数
        int[] arrColWidth = new int[headMap.size()];
        // 产生表格标题行,以及设置列宽
        String[] properties = new String[headMap.size()];
        String[] headers = new String[headMap.size()];
        int ii = 0;
        for (Iterator<String> iter = headMap.keySet().iterator(); iter
                .hasNext();) {
            String fieldName = iter.next();

            properties[ii] = fieldName;
            headers[ii] = headMap.get(fieldName);

            int bytes = fieldName.getBytes().length;
            arrColWidth[ii] =  bytes < minBytes ? minBytes : bytes;
            sheet.setColumnWidth(ii,arrColWidth[ii]*256);
            ii++;
        }
        // 遍历集合数据，产生数据行
        int rowIndex = 0;
        for (Object obj : jsonArray) {
            if(rowIndex == 65535 || rowIndex == 0){
                if ( rowIndex != 0 ) sheet = workbook.createSheet();//如果数据超过了，则在第二页显示
				if (!"root".equals(title)) {
	                SXSSFRow titleRow = sheet.createRow(0);//表头 rowIndex=0
	                titleRow.createCell(0).setCellValue(title);
	                titleRow.getCell(0).setCellStyle(titleStyle);
	                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headMap.size() - 1));
	                rowIndex = 1;
				}
                SXSSFRow headerRow = sheet.createRow(rowIndex); //列头 rowIndex =1
                for(int i=0;i<headers.length;i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                    headerRow.getCell(i).setCellStyle(headerStyle);

                }
                rowIndex += 1;//数据内容从 rowIndex=2开始
            }
            JSONObject jo = (JSONObject) JSONObject.toJSON(obj);
            SXSSFRow dataRow = sheet.createRow(rowIndex);
            for (int i = 0; i < properties.length; i++) {
                SXSSFCell newCell = dataRow.createCell(i);

                Object o =  jo.get(properties[i]);
                String cellValue = "";
                if(o==null) cellValue = "";
                else if(o instanceof Date) cellValue = new SimpleDateFormat(datePattern).format(o);
                else if(o instanceof Float || o instanceof Double)
                    cellValue= new BigDecimal(o.toString()).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
                else cellValue = o.toString();

                newCell.setCellValue(cellValue);
                newCell.setCellStyle(cellStyle);
            }
            rowIndex++;
        }
        // 自动调整宽度
        /*for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }*/
       /* try {
           // workbook.write(out);
           
          //  workbook.close();
          //  workbook.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return workbook;
    }
    
    public static void test(OutputStream outXlsx) throws IOException {
    	int count = 100000;
        JSONArray ja = new JSONArray();
        for(int i=0;i<100000;i++){
            Student s = new Student();
            s.setName("POI"+i);
            s.setAge(i);
            s.setBirthday(new Date());
            s.setHeight(i);
            s.setWeight(i);
            s.setSex(i/2==0?false:true);
            ja.add(s);
            
        }
        Map<String,String> headMap = new LinkedHashMap<String,String>();
        headMap.put("name","姓名");
        headMap.put("age","年龄");
        headMap.put("birthday","生日");
        headMap.put("height","身高");
        headMap.put("weight","体重");
        headMap.put("sex","性别");

        String title = "测试";
        /*
        OutputStream outXls = new FileOutputStream("E://a.xls");
        System.out.println("正在导出xls....");
        Date d = new Date();
        ExcelUtil.exportExcel(title,headMap,ja,null,outXls);
        System.out.println("共"+count+"条数据,执行"+(new Date().getTime()-d.getTime())+"ms");
        outXls.close();*/
        //
        System.out.println("正在导出xlsx....");
        Date d2 = new Date();
        ExcelUtil.exportExcelX(title,headMap,ja,null,0,outXlsx);
        System.out.println("共"+count+"条数据,执行"+(new Date().getTime()-d2.getTime())+"ms");
        outXlsx.close();
	}

    
    /** 
     * 读取Excel表格表头的内容 
     *  
     * @param InputStream 
     * @return String 表头内容的数组 
     * @author  
     */  
    private static String[] readExcelTitle(Workbook wb) throws Exception{  
        if(wb==null){  
            throw new Exception("Workbook对象为空！");  
        }  
        Sheet sheet = wb.getSheetAt(0);  
        Row row = sheet.getRow(0);  
        // 标题总列数  
        int colNum = row.getPhysicalNumberOfCells();  
        System.out.println("colNum:" + colNum);  
        String[] title = new String[colNum];  
        for (int i = 0; i < colNum; i++) {  
            title[i] = (String) getCellFormatValue(row.getCell(i));  
        }  
        return title;  
    }  
  
    /** 
     * 读取Excel数据内容 
     *  
     * @param InputStream 
     * @return Map 包含单元格数据内容的Map对象 
     * @author  
     */  
    private static Map<Integer, Map<Object, Object>> readExcelContent(Workbook wb) throws Exception{  
        if(wb==null){  
            throw new Exception("Workbook对象为空！");  
        }  
        Map<Integer, Map<Object, Object>> content = new HashMap<Integer, Map<Object,Object>>();  
          
        Sheet sheet = wb.getSheetAt(0);
        // 得到总行数  
        int rowNum = sheet.getLastRowNum();
        Row row = sheet.getRow(0);
        int colNum = row.getPhysicalNumberOfCells();
        // 正文内容应该从第二行开始,第一行为表头的标题  
        for (int i = 1; i <= rowNum; i++) {
            row = sheet.getRow(i);  
            int j = 0;  
            Map<Object, Object> cellValue = new HashMap<Object, Object>();  
            while (j < colNum) {  
                Object obj = getCellFormatValue(row.getCell(j));  
                cellValue.put(j, obj);  
                j++;  
            }  
            content.put(i, cellValue);  
        }  
        return content;  
    }  
   
    /** 
     *  
     * 根据Cell类型设置数据 
     *  
     * @param cell 
     * @return 
     * @author 
     */  
	private static Object getCellFormatValue(Cell cell) {  
       Object cellvalue = "";  
       if (cell != null) {  
           // 判断当前Cell的Type  
           switch (cell.getCellTypeEnum()) {  
           case NUMERIC: // 如果当前Cell的Type为NUMERIC
        	 //设置单元格类型
//        	   cell.setCellType(CellType.NUMERIC);
        	   if(HSSFDateUtil.isCellDateFormatted(cell)){
            	   cellvalue = cell.getDateCellValue();
            	   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            	   cellvalue = dateFormat.format(((Date)cellvalue));
        	   }else{
        		   cell.setCellType(CellType.STRING);
        		   cellvalue = cell.getStringCellValue();
        	   }
        	   break;
           case FORMULA: {  
               // 判断当前的cell是否为Date  
               if (DateUtil.isCellDateFormatted(cell)) {  
                   // 如果是Date类型则，转化为Data格式  
                   // data格式是带时分秒的：2013-7-10 0:00:00  
                   // cellvalue = cell.getDateCellValue().toLocaleString();  
                   // data格式是不带带时分秒的：2013-7-10  
                   Date date = cell.getDateCellValue();  
                   cellvalue = date;  
               } else {// 如果是纯数字  
                   // 取得当前Cell的数值,不按科学计数法输出
            	  // DecimalFormat df = new DecimalFormat("0");    
            	  /// cellvalue = df.format(cell.getNumericCellValue()); 
            	   cell.setCellType(CellType.STRING);
            	   cellvalue = cell.getStringCellValue();
               }  
               break;  
           }  
           case STRING:// 如果当前Cell的Type为STRING  
               // 取得当前的Cell字符串  
               cellvalue = cell.getRichStringCellValue().getString();  
               break;  
           default:// 默认的Cell值  
               cellvalue = "";  
           }  
       } else {  
           cellvalue = "";  
       }  
       return cellvalue;  
   }  

	public static List<Map<Object, Object>> importExcel(File file) {
		Workbook wb;  
		try{
			String filepath = file.getPath();
	        String ext = filepath.substring(filepath.lastIndexOf(".")); 
	        InputStream is = new FileInputStream(filepath); 
	        if(".xls".equals(ext)){  
	            wb = new HSSFWorkbook(is);
	        }else if(".xlsx".equals(ext)){  
	            wb = new XSSFWorkbook(is);  
	        }else{  
	            wb=null;  
	        }  
	        // 对读取Excel表格标题测试  
	        String[] title = readExcelTitle(wb);  
	        // 对读取Excel表格内容测试  
	        Map<Integer, Map<Object,Object>> map = readExcelContent(wb); 
	        //把表标题设置为每条数据对应的key值
	        Map<Object, Object> map1=new HashMap<Object,Object>();
	        List<Map<Object, Object>> list=new ArrayList<Map<Object, Object>>();
	        for(int i=1;i<=map.size();i++){
	        	map1=map.get(i);
	        	for(int j=0;j<map1.size();j++){
	        		map1.put(title[j], map1.get(j));
	        		map1.remove(j);
	        	}
	        	list.add(map1);
	        }
	        return list;
	    } catch (Exception e) {  
	        System.out.println("未找到指定路径的文件!");  
	        e.printStackTrace();  
	        return null;
	    }
	}
    
}
