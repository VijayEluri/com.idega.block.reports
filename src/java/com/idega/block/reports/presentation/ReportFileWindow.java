package com.idega.block.reports.presentation;

import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import java.io.File;
import java.sql.SQLException;
import com.idega.block.reports.data.Report;
import com.idega.block.reports.business.ReportWriter;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWBundle;
import com.idega.io.*;



/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */

public class ReportFileWindow extends IWAdminWindow {

  private final static String IW_BUNDLE_IDENTIFIER="com.idega.block.reports";
  protected IWResourceBundle iwrb;
  protected IWBundle iwb;

  public final static String prmReportId = ReportContentViewer.prmReportId;

  public ReportFileWindow() {
    setWidth(800);
    setHeight(600);
    setResizable( true);
    setMenubar( true);
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  public void main(IWContext iwc) throws Exception{

    iwrb = getResourceBundle(iwc);
    String title = iwrb.getLocalizedString("report_filer","Report Filer");
    setTitle(title);
    addTitle(title);
    addHeaderObject(getLinkTable());

    String prefix = "";
    String fileSeperator = System.getProperty("file.separator");
    String filepath = iwc.getServletContext().getRealPath(fileSeperator+"reports/temp"+fileSeperator);
    String filename = prefix+"temp.";
    String sReportId = iwc.getParameter(prmReportId );
    if(sReportId!=null){
      Report R = null;
      try{
        R = new Report(Integer.parseInt(sReportId));
      }
      catch(SQLException ex){
        R = null;
        ex.printStackTrace();
      }
      if(R!= null){
        if(iwc.getParameter("type")!=null){
          String type = iwc.getParameter("type");
          if(type.equalsIgnoreCase("xls")){
            filename = filename+type;
            String path = filepath+filename;
            MemoryFileBuffer buf = ReportWriter.writeXLS(R);
            if(buf!=null)
              setToRedirect("/servlet/excel?&dir="+path,1);
          }
          else if(type.equalsIgnoreCase("pdf")){
            filename = filename+type;
            String path = filepath+filename;
            MemoryFileBuffer buf = ReportWriter.writePDF(R);
            if(buf !=null)
              setToRedirect("/servlet/pdf?&dir="+path,1);
          }
        }
      }
      else
        add(formatText(iwrb.getLocalizedString("no_report","No Report")));
    }
    else
       add(formatText(iwrb.getLocalizedString("no_report","No Report")));
  }

  private PresentationObject getLinkTable(){
    Link L = new Link(iwrb.getLocalizedString("close","Close"));
    L.setFontStyle("text-decoration: none");
    L.setFontColor("#FFFFFF");
    L.setBold();
    L.setOnClick("window.close()");
    return L;
  }
}