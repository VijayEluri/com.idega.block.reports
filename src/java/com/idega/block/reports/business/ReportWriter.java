package com.idega.block.reports.business;



import java.io.*;

import java.util.*;

import java.sql.*;

import com.lowagie.text.*;

import com.lowagie.text.pdf.PdfWriter;

import com.idega.block.reports.data.Report;

import com.idega.block.reports.data.ReportInfo;

import com.idega.util.database.ConnectionBroker;

import com.idega.idegaweb.IWMainApplication;
import com.idega.io.MemoryFileBuffer;

import com.idega.io.MemoryInputStream;

import com.idega.io.MemoryOutputStream;

import com.idega.io.MediaWritable;

import javax.servlet.http.HttpServletRequest;





/**

 * Title:

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:      idega multimedia

 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>

 * @version 1.0

 */



public class ReportWriter implements MediaWritable {



  private Report eReport;

  private ReportInfo eReportInfo;

  private String mimeType;

  private MemoryFileBuffer buffer = null;



  public final static String prmReportId = "repid";

  public final static String prmReportInfoId = "repifid";

  public final static String prmPrintType = "reptype";

  public final static String XLS = "xls";

  public final static String PDF = "pdf";

  public final static String TXT = "txt";



  public ReportWriter(){



  }



  public void init(HttpServletRequest req, IWMainApplication iwma){

    if(req.getParameter(prmReportId)!=null ){



      eReport = ReportFinder.getReport(Integer.parseInt(req.getParameter(prmReportId)));

      if(req.getParameter(prmReportInfoId)!=null){

        eReportInfo = ReportFinder.getReportInfo(Integer.parseInt(req.getParameter(prmReportInfoId)));

        if(eReportInfo.getType().equals("sticker"))

          buffer = StickerReport.writeStickerList(eReport,eReportInfo);

        else if(eReportInfo.getType().equals("columns"))

          System.err.println("not sticker could it be "+eReportInfo.getType());

        else

          System.err.println("not sticker could it be "+eReportInfo.getType());

      }

      else if(req.getParameter(prmPrintType)!=null){

        String type = req.getParameter(prmPrintType);

        if(type.equals(PDF)){

          buffer = writePDF(eReport);

        }

        else if(type.equals(XLS)){

          buffer = writeXLS(eReport);

        }

        else if(type.equals(TXT)){

          buffer = writeTXT(eReport);

        }



      }

    }

  }



  public String getMimeType(){

    if(buffer != null)

      return buffer.getMimeType();

    return "application/pdf";

  }



  public void writeTo(OutputStream out) throws IOException{

    if(buffer !=null){

      MemoryInputStream mis = new MemoryInputStream(buffer);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      // Read the entire contents of the file.



      while (mis.available() > 0)

      {

        baos.write(mis.read());

      }

      baos.writeTo(out);

    }

    else

      System.err.println("buffer is null");

  }



  public static boolean writeXLSReport(String[] Headers,String[][] Content, OutputStream out){

      boolean returner = false;

      try{

        OutputStreamWriter fout = new OutputStreamWriter(out);

        StringBuffer data;

        int len = Content.length;

        data = new StringBuffer();

        for (int j = 0; j < Headers.length; j++) {

            data.append(Headers[j]);

            data.append("\t");

        }

        data.append("\n");

        fout.write(data.toString());

        for(int i = 0; i < len; i++){

          data = new StringBuffer();

          for (int j = 0; j < Content[i].length; j++) {

            data.append(Content[i][j]);

            data.append("\t");

          }

          data.append("\n");

          fout.write(data.toString());

        }

        returner = true;

      }

      catch(Exception ex){

        ex.printStackTrace();

      }

      finally{

        try{

        out.close();

        }

        catch(IOException io){

          io.printStackTrace();

          returner = false;

        }

        return returner;

      }

  }



  public static boolean writePDFReport(String[] Headers,String[][] Content, OutputStream out){

    return false;

  }



  public static MemoryFileBuffer writeXLS(Report report){

    return writeTabDelimited(report,XLS);

  }



  public static MemoryFileBuffer writeTXT(Report report){

    return writeTabDelimited(report,TXT) ;

  }



  private static MemoryFileBuffer writeTabDelimited(Report report,String type){

      Connection Conn = null;



			MemoryFileBuffer buffer = new MemoryFileBuffer();

			MemoryOutputStream mos = new MemoryOutputStream(buffer);

      try{

        String[] Headers = report.getHeaders();

        String sql = report.getSQL();



        //String file = realpath;

        //FileWriter out = new FileWriter(file);

        Conn = com.idega.util.database.ConnectionBroker.getConnection();

        Statement stmt = Conn.createStatement();

        ResultSet RS  = stmt.executeQuery(sql);

        ResultSetMetaData MD = RS.getMetaData();

        int count = MD.getColumnCount();

        String temp;

        StringBuffer data = new StringBuffer();

        for (int i = 0; i < Headers.length; i++) {

          data.append(Headers[i]);

          data.append("\t");

        }

        data.append("\n");

        mos.write(data.toString().getBytes());



        while(RS.next()){

           data = new StringBuffer();

          for(int i = 1; i <= count; i++){

            temp = RS.getString(i);

            temp = temp!=null?temp:"";

            data.append(temp);

            data.append("\t");

          }

          data.append("\n");



          mos.write(data.toString().getBytes());

        }

        RS.close();

        stmt.close();

        mos.close();




      }

      catch(Exception ex){

        ex.printStackTrace();

      }

      finally {

        ConnectionBroker.freeConnection(Conn);

      }

      if(type.equals(XLS))

        buffer.setMimeType("application/x-msexcel");

      else

        buffer.setMimeType("text/plain");

      return buffer;

  }



  public static MemoryFileBuffer  writePDF(Report report){

    Connection Conn = null;



    MemoryFileBuffer buffer = new MemoryFileBuffer();

    MemoryOutputStream mos = new MemoryOutputStream(buffer);



    try {

        String[] Headers = report.getHeaders();

        int Hlen = Headers.length;

        String sql = report.getSQL();

        String info = report.getColInfo();

        String columnWidths = null;

        int[] sizes = null;



        if(info!=null){

          int first = info.indexOf("#");



          if(first != -1){

            if(info.length()>first){

            int second = info.indexOf("#",first+1);

              if(second!=-1){

                columnWidths = info.substring(first+1,second);

                StringTokenizer tok = new StringTokenizer(columnWidths,";");

                int size = tok.countTokens();

                if(size > 0){

                  sizes = new int[size];

                  int i = 0;

                  while (tok.hasMoreTokens()) {

                    sizes[i++] = Integer.parseInt(tok.nextToken());

                  }

                }

              }

            }

          }

        }



        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        PdfWriter writer = PdfWriter.getInstance(document, mos);

        document.addTitle(report.getName());

        document.addAuthor("Idega Reports");

        document.addSubject(report.getInfo());

        document.open();



        Conn = com.idega.util.database.ConnectionBroker.getConnection();

        Statement stmt = Conn.createStatement();

        ResultSet RS  = stmt.executeQuery(sql);



        String temp = null;

        Table datatable = getTable(Headers,sizes);

        while(RS.next()){

          for(int i = 1; i <= Hlen; i++){

            temp = RS.getString(i);

            temp = temp!=null?temp:"";



            Cell cell = new Cell(new Phrase(temp, new Font(Font.HELVETICA, 10, Font.BOLD)));

            cell.setBorder(Rectangle.NO_BORDER);

            datatable.addCell(cell);

          }

          if (!writer.fitsPage(datatable)) {

            datatable.deleteLastRow();

            document.add(datatable);

            document.newPage();

            datatable = getTable(Headers,sizes);

          }

        }



        RS.close();

        stmt.close();



        document.add(datatable);

        document.close();

    }

    catch (Exception ex) {

      ex.printStackTrace();

    }

    finally {

      if(Conn!=null)

      ConnectionBroker.freeConnection(Conn);

    }

    buffer.setMimeType("application/pdf");

    return buffer;

  }



  private static Table getTable(String[] headers,int[] sizes) throws BadElementException, DocumentException {



    Table datatable = new Table(headers.length);



    datatable.setPadding(0.0f);
	
    datatable.setSpacing(0.0f);

    datatable.setBorder(Rectangle.NO_BORDER);

    datatable.setWidth(100);

    if(sizes!=null)

      datatable.setWidths(sizes);



    for (int i = 0; i < headers.length; i++) {

      //datatable.addCell(Headers[i]);

      Cell cell = new Cell(new Phrase(headers[i], new Font(Font.HELVETICA, 12, Font.BOLD)));

      cell.setBorder(Rectangle.BOTTOM);

      datatable.addCell(cell);

    }

    // the first cell spans 10 columns



    datatable.setDefaultCellBorderWidth(0);

    datatable.setDefaultCellBorder(Rectangle.NO_BORDER);

    datatable.setDefaultRowspan(1);



    return datatable;

  }





}

