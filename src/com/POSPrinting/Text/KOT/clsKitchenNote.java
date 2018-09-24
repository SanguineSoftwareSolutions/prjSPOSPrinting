/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting.Text.KOT;

import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsUtility;
import com.POSGlobal.controller.clsUtility2;
import com.POSPrinting.Utility.clsPrintingUtility;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

/**
 *
 * @author Ajim
 */
public class clsKitchenNote
{

    private clsUtility objUtility;
    private clsUtility2 objUtility2;

    public clsKitchenNote()
    {
	objUtility = new clsUtility();
	objUtility2 = new clsUtility2();
    }

    public void funPrintKOTTextMessage(String costCenterCode, String costCenterName, String kitchenNote)
    {
	try
	{
	    funCreateTempFolder();

	    String sqlCostCenters = "select a.strPrinterPort,a.strSecondaryPrinterPort,a.strPrintOnBothPrinters from tblcostcentermaster a where a.strCostCenterCode='" + costCenterCode + "' ";
	    ResultSet rsCostCenters = clsGlobalVarClass.dbMysql.executeResultSet(sqlCostCenters);
	    if (rsCostCenters.next())
	    {
		String printerName = rsCostCenters.getString(1);
		String secondaryPrinterName = rsCostCenters.getString(1);
		String filePath = System.getProperty("user.dir");
		String filename = (filePath + "/Temp/KitchenNote.txt");
		try
		{
		    File file = new File(filename);

		    funCreateTestTextFile(file, costCenterCode, costCenterName, printerName, secondaryPrinterName, kitchenNote);

		    clsPrintingUtility objPrintingUtility = new clsPrintingUtility();
		    objPrintingUtility.funShowTextFile(file, "", "");

		    int printerIndex = 0;
		    String printerStatus = "Not Found";
		    PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		    printerName = printerName.replaceAll("#", "\\\\");
		    PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
		    for (int i = 0; i < printService.length; i++)
		    {
			String printerServiceName = printService[i].getName();
			if (printerName.equalsIgnoreCase(printerServiceName))
			{
			    System.out.println("Printer=" + printerName);
			    printerIndex = i;
			    printerStatus = "Found";
			    break;
			}
		    }

		    if (printerStatus.equals("Found"))
		    {
			DocPrintJob job = printService[printerIndex].createPrintJob();
			FileInputStream fis = new FileInputStream(filename);
			DocAttributeSet das = new HashDocAttributeSet();
			Doc doc = new SimpleDoc(fis, flavor, das);
			job.print(doc, pras);

			PrintServiceAttributeSet att = printService[printerIndex].getAttributes();
			for (Attribute a : att.toArray())
			{
			    String attributeName;
			    String attributeValue;
			    attributeName = a.getName();
			    attributeValue = att.get(a.getClass()).toString();
			}
		    }
		    else
		    {
			JOptionPane.showMessageDialog(null, printerName + " Printer Not Found");
		    }

		}
		catch (Exception e)
		{

		    objUtility.funWriteErrorLog(e);
		    e.printStackTrace();
		    JOptionPane.showMessageDialog(null, e.getMessage(), "Error Code - TFG 01", JOptionPane.ERROR_MESSAGE);
		}
	    }
	}
	catch (Exception e)
	{
	    objUtility.funWriteErrorLog(e);
	    e.printStackTrace();
	}
    }

    private void funCreateTempFolder()
    {
	try
	{
	    String filePath = System.getProperty("user.dir");
	    File PrintText = new File(filePath + "/Temp");
	    if (!PrintText.exists())
	    {
		PrintText.mkdirs();
	    }
	}
	catch (Exception e)
	{
	    objUtility.funWriteErrorLog(e);
	    e.printStackTrace();
	}
    }

    private void funCreateTestTextFile(File file, String costcenterCode, String costcenterName, String primaryPrinter, String secondaryPrinter, String kitchenNote)
    {
	BufferedWriter fileWriter = null;
	try
	{
	    //File file=new File(filename);
	    fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));

//	    String fileHeader = "----------Kitchen Note------------";
	    String dottedLine = "----------------------------------";
//	    String newLine = "\n";
//	    String blankLine = "                                   ";
//
//	    fileWriter.write(fileHeader);
//	    fileWriter.newLine();
//	    fileWriter.write(dottedLine);
//	    fileWriter.newLine();
//	    fileWriter.write("User Name : " + clsGlobalVarClass.gUserName);
//	    fileWriter.newLine();
//	    fileWriter.write("POS Name : " + clsGlobalVarClass.gPOSName);
//	    fileWriter.newLine();
//	    fileWriter.write("Cost Center Name : " + costcenterName);
//	    fileWriter.newLine();
	    //message

	    fileWriter.write("Note : ");
	    fileWriter.write(kitchenNote);
	    fileWriter.newLine();
	    fileWriter.write(dottedLine);
	}
	catch (FileNotFoundException ex)
	{
	    objUtility.funWriteErrorLog(ex);
	    ex.printStackTrace();
	}
	catch (UnsupportedEncodingException ex)
	{
	    objUtility.funWriteErrorLog(ex);
	    ex.printStackTrace();
	}
	catch (IOException ex)
	{
	    objUtility.funWriteErrorLog(ex);
	    ex.printStackTrace();
	}
	finally
	{
	    try
	    {
		fileWriter.close();
	    }
	    catch (IOException ex)
	    {
		objUtility.funWriteErrorLog(ex);
		ex.printStackTrace();
	    }
	}

    }

    public void funPrintKOTJasperMessage(String costCenterCode, String costCenterName, String kitchenNote)
    {
	try
	{
	    

	    String sqlCostCenters = "select a.strPrinterPort,a.strSecondaryPrinterPort,a.strPrintOnBothPrinters from tblcostcentermaster a where a.strCostCenterCode='" + costCenterCode + "' ";
	    ResultSet rsCostCenters = clsGlobalVarClass.dbMysql.executeResultSet(sqlCostCenters);
	    if (rsCostCenters.next())
	    {
		String printerName = rsCostCenters.getString(1);
		String secondaryPrinterName = rsCostCenters.getString(1);
		String reportName = "com/POSGlobal/reports/rptKitchenNote.jasper";
		try
		{
		    clsUtility2 objUtility2 = new clsUtility2();

		    HashMap hm = new HashMap();
		    hm.put("note", kitchenNote);
		    hm.put("DATE_TIME", clsGlobalVarClass.getCurrentDateTime());

		    ArrayList listData = new ArrayList();
		    listData.add(1);

		    JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(listData);
		    InputStream is = this.getClass().getClassLoader().getResourceAsStream(reportName);

		    JasperPrint print = JasperFillManager.fillReport(is, hm, beanColDataSource);
		    JRViewer viewer = new JRViewer(print);
		    JFrame jf = new JFrame();
		    jf.getContentPane().add(viewer);
		    jf.validate();
		    if (clsGlobalVarClass.gShowBill)
		    {
			jf.setVisible(true);
			jf.setSize(new Dimension(500, 900));
			jf.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		    }

		    //--- Print the document
		    objUtility2.funPrintJasperKOT(printerName, print);

		}
		catch (Exception e)
		{

		    objUtility.funWriteErrorLog(e);
		    e.printStackTrace();
		    JOptionPane.showMessageDialog(null, e.getMessage(), "Error Code - TFG 01", JOptionPane.ERROR_MESSAGE);
		}
	    }
	}
	catch (Exception e)
	{
	    objUtility.funWriteErrorLog(e);
	    e.printStackTrace();
	}
    }
}
