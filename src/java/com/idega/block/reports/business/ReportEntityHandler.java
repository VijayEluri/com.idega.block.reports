package com.idega.block.reports.business;

import java.io.*;
import java.util.*;
import java.sql.SQLException;
import com.idega.data.EntityFinder;
import com.idega.block.reports.data.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

public class ReportEntityHandler {

  public ReportEntityHandler(){}

  public static ReportCategory[] findCategorys(int iCatId){
    try {
      if(iCatId > 0){
        return (ReportCategory[]) new ReportCategory().findAllByColumn(ReportItem.getColumnNameCategory(),iCatId);
      }
      else{
        return new ReportCategory[0];
      }
    }
    catch (SQLException ex) {
      return new ReportCategory[0];
    }
  }

  public static List listOfReportItems(int iCatId){
    List L = null;
    try {
      L = EntityFinder.findAllByColumn(new ReportItem(),ReportItem.getColumnNameCategory(),iCatId);
    }
    catch (SQLException ex) {
      L = null;
    }
    return L;
  }

   public static ReportItem[] findReportItems(int iCatId){
    try {
      if(iCatId > 0){
        return (ReportItem[]) new ReportItem().findAllByColumnOrdered(ReportItem.getColumnNameCategory(),String.valueOf(iCatId),ReportItem.getColumnNameDisplayOrder());
      }
      else{
        return new ReportItem[0];
      }
    }
    catch (SQLException ex) {
      return new ReportItem[0];
    }
  }

  public static List listOfReports(int iCategoryId){
    List L = null;
    try {
      L = EntityFinder.findAllByColumn(new Report(),Report.getColumnNameCategory(),iCategoryId );
    }
    catch (SQLException ex) {
      ex.printStackTrace();
      L = null;
    }
    return L;
  }

  public static Report[] findReports(int iCatId){
    try {
      if(iCatId > 0){
        return (Report[]) new Report().findAllByColumn(ReportItem.getColumnNameCategory(),iCatId);
      }
      else{
        return new Report[0];
      }
    }
    catch (SQLException ex) {
      return new Report[0];
    }
  }

  public static ReportCondition[] getConditions(int iCategory){
    ReportItem[] RI = findReportItems(iCategory);
    ReportCondition[] RC = new ReportCondition[RI.length];
    for (int i = 0; i < RI.length; i++) {
      RC[i] = new ReportCondition(RI[i]);
    }
    return RC;
  }

  public static List listOfReportConditions(int iCategory){
    ReportItem[] RI = findReportItems(iCategory);
    Vector V = new Vector(RI.length);
    for (int i = 0; i < RI.length; i++) {
      V.add(new ReportCondition(RI[i]));
    }
    return V;
  }

  public static boolean saveReportItem(int catid,String name,String field,String table,
                  String joins,String jointables,String condtype,
                  String conddata,String condop,String entity,String info){
    try {
      if(catid > 0){
        ReportItem ri = new ReportItem();
        ri.setCategory(catid);
        ri.setName(name);
        ri.setField(field);
        ri.setMainTable(table);
        ri.setJoin(joins);
        ri.setJoinTables(jointables);
        ri.setConditionType(condtype);
        ri.setConditionData(conddata);
        ri.setConditionOperator(condop);
        ri.setEntity(entity);
        ri.setInfo(info);
        ri.insert();

        return true;
      }
      else
        return false;
    }
    catch (Exception ex) {
      return false;
    }
  }
   public static boolean updateReportItem(int id,int catid,String name,String field,String table,
                  String joins,String jointables,String condtype,
                  String conddata,String condop,String entity,String info){
     try {
      if(id > 0){
      ReportItem ri = new ReportItem(id);
      ri.setCategory(catid);
      ri.setName(name);
      ri.setField(field);
      ri.setMainTable(table);
      ri.setJoin(joins);
      ri.setJoinTables(jointables);
      ri.setConditionType(condtype);
      ri.setConditionData(conddata);
      ri.setConditionOperator(condop);
      ri.setEntity(entity);
      ri.setInfo(info);
      ri.update();
      return true;
      }
      else
        return false;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }
  public static boolean saveCategory(String name,String info){
    try {
      ReportCategory rc = new ReportCategory();
      rc.setName(name);
      rc.setInfo(info);
      rc.insert();
      return true;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  public static boolean updateCategory(int id,String name,String info){
     try {
      if(id != -1){
      ReportCategory rc = new ReportCategory(id);
      rc.setName(name);
      rc.setInfo(info);
      rc.update();
      return true;
      }
      else
        return false;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  public static boolean saveReport(String name,String info,String[] headers,String sql,int Category){
    try {
      Report r = new Report();
      r.setName(name);
      r.setInfo(info);
      r.setHeaders(headers );
      r.setSQL(sql);
      r.setCategory(Category);
      r.insert();
      return true;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }
  public static boolean updateReport(int id,String name,String info,String[] headers,String sql,int Category){
     try {
      if(id != -1){
        Report r = new Report(id);
        r.setName(name);
        r.setInfo(info);
        r.setHeaders(headers );
        r.setSQL(sql);
        r.setCategory(Category);
        r.update();
        return true;
      }
      else
        return false;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  public static boolean deleteReport(int id){
    try {
      Report r = new Report(id);
      r.delete();
      return true;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }

  public static boolean deleteCategory(int id){
    try {
      ReportCategory rc = new ReportCategory(id);
      rc.delete();
      return true;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }
}
