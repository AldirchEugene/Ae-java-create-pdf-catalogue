package com.thingsgod.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.*;
import com.thingsgod.entity.Bookmark;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JavaCreatePdfCatalogue {

    /**
     * 读取书签文本
     * @param txt
     * @param skip
     * @return
     * @throws Exception
     */
    public static List<Bookmark> readTxt(String txt, Integer skip) throws Exception{
        ArrayList<String> lines=new ArrayList<>();
        BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(txt),"UTF-8"));
        String line = null;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        br.close();

        //判断分别的级别
        ArrayList<Bookmark> Bookmarks = new ArrayList<>();
        for(int i =0;i<lines.size();i++){
            try {
                String[] r = lines.get(i).split(" ");
                Bookmark temp = new Bookmark();
                int len = emptyStrlen(r);

                //一个tab开头为二级标题【一个tab是4个空格，截取出来就会产生4个空字符，就是要空字符来判断】
                if(len == 4){
                    temp.setLevel(2);
                }

                //两个tab开头为三级标题【一个tab是4个空格，截取出来就会产生8个空字符】
                else if(len == 8){
                    temp.setLevel(3);
                }
                //无tab开头为一级标题
                else{
                    temp.setLevel(1);
                }
                temp.setId(i);
                temp.setTitle(r[r.length-2].replace("\t",""));
                temp.setPage(Integer.valueOf(r[r.length-1])+skip);
                temp.setPid(-1);
                Bookmarks.add(temp);
            } catch (Exception e){
                System.out.println("数据错误: 行数="+i+",数据:"+lines.get(i));
                e.printStackTrace();
                throw new RuntimeException();
            }
        }

        //建立pid关联关系
        int lastLevel1 = 0;//最近一次level1标签的下标
        int lastLevel2 = 0;//最近一次level2标签下标
        for(int i =0;i<Bookmarks.size() ;i++){
            Bookmark bt = Bookmarks.get(i);
            if(bt.getLevel() == 1){
                lastLevel1 = i;
                continue;
            }
            if(bt.getLevel() == 2){
                lastLevel2 = i;
                bt.setPid(lastLevel1);
                continue;
            }
            if(bt.getLevel() == 3){
                bt.setPid(lastLevel2);
            }
        }
        Bookmarks.stream().forEach(n->{
            System.out.println(n.toString());
        });
        return Bookmarks;
    }

    /**
     * 添加多级书签
     * @param sPdf pdf的文件所在目录，绝对路径
     * @param bookTxt 书籍的目录结构，可以从当当网上去获取
     * @param skip 跳过书籍前面的广告页数
     * @throws Exception
     */
    public static void addMultiLevelBookmark(String sPdf,String bookTxt,Integer skip) throws Exception{
        //打开文档
        Document document = new Document();

        //新pdf名称
        String[] a = sPdf.split("\\.pdf");
        String dPdf = a[0]+"_new.pdf";
        PdfCopy writer = new PdfCopy(document, new FileOutputStream(dPdf));
        writer.setViewerPreferences(PdfWriter.PageModeUseOutlines);
        document.open();

        //打开源pdf复制到新pdf
        PdfReader reader = new PdfReader(sPdf);
        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {
            writer.addPage(writer.getImportedPage(reader, i));
        }

        //添加书签
        writer.freeReader(reader);
        PdfOutline root = writer.getRootOutline();

        //书签文本转书签对象
        List<Bookmark> bookTemps = readTxt(bookTxt,skip);
        bookTemps.stream().forEach(filter1->{
            if(filter1.getLevel().equals(1)){
                //建立一级标签
                PdfAction actionLevel1 = PdfAction.gotoLocalPage(filter1.getPage(),new PdfDestination(PdfDestination.FIT), writer);
                PdfOutline outlineLevel1 = new PdfOutline(root, actionLevel1, filter1.getTitle(), false);
                System.out.println("一级:"+filter1.getTitle()+" "+filter1.getPage());
                bookTemps.stream().forEach(filter2->{

                    //建立二级标签,要挂载一级标签下面
                    if(filter2.getLevel().equals(2)&&filter1.getId().equals(filter2.getPid())){
                        System.out.println("二级:"+filter2.getTitle()+" "+filter2.getPage());
                        PdfAction action2 = PdfAction.gotoLocalPage(filter2.getPage(),new PdfDestination(PdfDestination.FIT), writer);
                        PdfOutline outlineLevel2 = new PdfOutline(outlineLevel1, action2, filter2.getTitle(), false);
                        bookTemps.stream().forEach(filter3->{

                            //建立三级标签,要挂载二级标签下面
                            if(filter3.getLevel().equals(3)&&filter2.getId().equals(filter3.getPid())){
                                PdfAction action3 = PdfAction.gotoLocalPage(filter3.getPage(),new PdfDestination(PdfDestination.FIT), writer);
                                PdfOutline outlineLevel3 = new PdfOutline(outlineLevel2, action3, filter3.getTitle(), false);
                                System.out.println("三级:"+filter3.getTitle()+" "+filter3.getPage());
                            }
                        });
                    }
                });
            }
        });

        document.close();
    }

    public static int emptyStrlen(String[] strArr){
        int len = 0;
        if(strArr != null && strArr.length > 0){
            for (String str : strArr) {
                if("".equals(str)){
                    len += 1;
                }
            }
        }
        return len;
    }
    
    public static void main(String[] args) throws Exception {


        addMultiLevelBookmark("D:\\yuxinwei\\AE\\Ae-java-create-pdf-catalogue\\pdf\\Kafka权威指南.pdf",
                "D:\\yuxinwei\\AE\\Ae-java-create-pdf-catalogue\\bookmark\\目录.txt",
                19);
    }
}
