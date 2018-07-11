package com.POSPrinting.Utility;

import com.POSGlobal.controller.clsBillDtl;
import com.POSGlobal.controller.clsBillDtlForMuscat;
import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsPosConfigFile;
import com.POSGlobal.controller.clsUtility;
import com.POSGlobal.controller.clsUtility2;
import com.POSGlobal.view.frmShowTextFile;
import com.POSPrinting.MyPrintJobListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrinterName;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates and open the template
 * in the editor.
 */
/**
 *
 * @author Ajim
 * @date Aug 24, 2017
 */
public class clsPrintingUtility
{

    private clsUtility objUtility = new clsUtility();
    private clsUtility2 objUtility2 = new clsUtility2();

    private final String Line = "  --------------------------------------";
    private DecimalFormat stdDecimalFormat = new DecimalFormat("######.##");
    private DecimalFormat decimalFormatFor2DecPoint = new DecimalFormat("0.00");
    private DecimalFormat decimalFormatFor3DecPoint = new DecimalFormat("0.000");

    /**
     *
     */
    public void funCreateTempFolder()
    {
	try
	{
	    String filePath = System.getProperty("user.dir");
	    File Text_KOT = new File(filePath + "/Temp");
	    if (!Text_KOT.exists())
	    {
		Text_KOT.mkdirs();
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    /**
     *
     * @param printWord
     * @param BWOut
     */
    public void funPrintBlankSpace(String printWord, BufferedWriter BWOut)
    {
	try
	{
	    int wordSize = printWord.length();
	    int actualPrintingSize = clsGlobalVarClass.gColumnSize;
	    int availableBlankSpace = actualPrintingSize - wordSize;

	    int leftSideSpace = availableBlankSpace / 2;
	    if (leftSideSpace > 0)
	    {
		for (int i = 0; i < leftSideSpace; i++)
		{
		    BWOut.write(" ");
		}
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    /**
     *
     * @param file
     * @param formName
     * @param printerInfo
     */
    public void funShowTextFile(File file, String formName, String printerInfo)
    {
	try
	{
	    String data = "";
	    FileReader fread = new FileReader(file);
	    //BufferedReader KOTIn = new BufferedReader(fread);
	    FileInputStream fis = new FileInputStream(file);
	    BufferedReader KOTIn = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
	    String line = "";
	    while ((line = KOTIn.readLine()) != null)
	    {
		data = data + line + "\n";
	    }
	    String fileName = file.getName();
	    String name = "";
	    if (formName.trim().length() > 0)
	    {
		name = formName;
	    }
	    if ("Temp_DayEndReport.txt".equalsIgnoreCase(fileName))
	    {
		name = "DayEnd";
	    }
	    fread.close();
	    KOTIn.close();
	    new frmShowTextFile(data, name, file, printerInfo).setVisible(true);
	    
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    /**
     *
     * @param primaryPrinterName
     * @param secPrinterName
     * @param type
     * @param printOnBothPrinters
     * @param isReprint
     */
    public void funPrintToPrinter(String primaryPrinterName, String secPrinterName, String type, String printOnBothPrinters, boolean isReprint)
    {
	try
	{
	    String reportname = "";
	    String fileName = "";
	    if (type.equalsIgnoreCase("kot") || type.equalsIgnoreCase("checkkot"))
	    {
		fileName = "Temp/Temp_KOT.txt";
		//fileName = "Temp/Temp_KOT.rtf";
	    }
	    else if (type.equalsIgnoreCase("ConsolidatedKOT"))
	    {
		fileName = "Temp/Temp_KOT.txt";
	    }
	    else if (type.equalsIgnoreCase("dayend"))
	    {
		fileName = "Temp/Temp_DayEndReport.txt";
		reportname = "dayend";
	    }
	    else if (type.equalsIgnoreCase("Adv Receipt"))
	    {
		reportname = "Adv Receipt";
	    }
	    else if (type.equalsIgnoreCase("ItemWiseKOT"))
	    {
		fileName = "/Temp/" + fileName + ".txt";
	    }
	    else
	    {
		fileName = "Temp/Temp_Bill.txt";
		reportname = "bill";
	    }

	    if ("windows".equalsIgnoreCase(clsPosConfigFile.gPrintOS))//&& clsGlobalVarClass.gPrintType.equalsIgnoreCase("Text File")
	    {
		if (type.equalsIgnoreCase("kot"))
		{
		    //System.out.println("G Print YN="+clsGlobalVarClass.gPrintKOTYN);
		    if (clsGlobalVarClass.gPrintKOTYN)
		    {
			funPrintKOTWindows(primaryPrinterName, secPrinterName, printOnBothPrinters);
			if (clsGlobalVarClass.gMultipleKOTPrint)
			{
			    if (!isReprint)
			    {
				funAppendDuplicate(fileName);
			    }
			    funPrintKOTWindows(primaryPrinterName, secPrinterName, printOnBothPrinters);
			}
		    }
		}
		else if (type.equalsIgnoreCase("checkkot"))
		{
		    funPrintCheckKOTWindows(primaryPrinterName);
		}
		else if (type.equalsIgnoreCase("ConsolidatedKOT"))
		{
		    funPrintConsolidatedKOTWindows(primaryPrinterName);
		}
		else if (type.equalsIgnoreCase("ItemWiseKOT"))
		{
		    funPrintItemWiseKOT(primaryPrinterName, secPrinterName, fileName);
		}
		else
		{
		    funPrintBillWindows(reportname);
		    //Avoid Muliple Bill Printing
		    if (!type.equalsIgnoreCase("dayend"))
		    {
			if (clsGlobalVarClass.gMultiBillPrint)
			{
			    if (!isReprint)
			    {
				funAppendDuplicate(fileName);
			    }
			    funPrintBillWindows(reportname);
			}
		    }
		}
	    }
	    else if ("linux".equalsIgnoreCase(clsPosConfigFile.gPrintOS) && clsGlobalVarClass.gPrintType.equalsIgnoreCase("Text File"))
	    {

		System.out.println("Linux Bill Printer->" + clsGlobalVarClass.gBillPrintPrinterPort);
		System.out.println("Linux primaryPrinterName->" + primaryPrinterName);

		if (type.equalsIgnoreCase("kot"))
		{
		    //System.out.println("G Print YN="+clsGlobalVarClass.gPrintKOTYN);
		    if (clsGlobalVarClass.gPrintKOTYN)
		    {
			Process process = Runtime.getRuntime().exec("lpr -P " + primaryPrinterName + " " + fileName, null);

			if (clsGlobalVarClass.gMultipleKOTPrint)
			{
			    if (!isReprint)
			    {
				funAppendDuplicate(fileName);
			    }
			    process = Runtime.getRuntime().exec("lpr -P " + primaryPrinterName + " " + fileName, null);
			}
		    }
		}
		else if (type.equalsIgnoreCase("checkkot"))
		{
		    Process process = Runtime.getRuntime().exec("lpr -P " + primaryPrinterName + " " + fileName, null);
		}
		else if (type.equalsIgnoreCase("ConsolidatedKOT"))
		{
		    Process process = Runtime.getRuntime().exec("lpr -P " + primaryPrinterName + " " + fileName, null);
		}
		else if (type.equalsIgnoreCase("ItemWiseKOT"))
		{
		    Process process = Runtime.getRuntime().exec("lpr -P " + primaryPrinterName + " " + fileName, null);
		}
		else
		{
		    //Process process = Runtime.getRuntime().exec("lpr -P " + primaryPrinterName + " " + fileName, null);
		    Process process = Runtime.getRuntime().exec("lpr -P " + clsGlobalVarClass.gBillPrintPrinterPort + " " + fileName, null);
		    if (!type.equalsIgnoreCase("dayend"))
		    {
			if (clsGlobalVarClass.gMultiBillPrint)
			{
			    if (!isReprint)
			    {
				funAppendDuplicate(fileName);
			    }
			    process = Runtime.getRuntime().exec("lpr -P " + clsGlobalVarClass.gBillPrintPrinterPort + " " + fileName, null);
			}
		    }
		}
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    private void funPrintKOTWindows(String primaryPrinterName, String secPrinterName, String printOnBothPrinters)
    {
	String filePath = System.getProperty("user.dir");
	String fileName = (filePath + "/Temp/Temp_KOT.txt");
	//String fileName = (filePath + "/Temp/Temp_KOT.rtf");
	try
	{
	    int printerIndex = 0;
	    String printerStatus = "Not Found";
	    System.out.println("Primary Name=" + primaryPrinterName);
	    System.out.println("Sec Name=" + secPrinterName);

	    PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
	    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
	    primaryPrinterName = primaryPrinterName.replaceAll("#", "\\\\");
	    secPrinterName = secPrinterName.replaceAll("#", "\\\\");

	    PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
	    for (int i = 0; i < printService.length; i++)
	    {
		System.out.println("Service=" + printService[i].getName() + "\tPrim P=" + primaryPrinterName);
		String printerServiceName = printService[i].getName();

		if (primaryPrinterName.equalsIgnoreCase(printerServiceName))
		{
		    System.out.println("Printer=" + primaryPrinterName);
		    printerIndex = i;
		    printerStatus = "Found";
		    break;
		}
	    }

	    if (printerStatus.equals("Found"))
	    {
		DocPrintJob job = printService[printerIndex].createPrintJob();
		FileInputStream fis = new FileInputStream(fileName);
		DocAttributeSet das = new HashDocAttributeSet();
		Doc doc = new SimpleDoc(fis, flavor, das);
		job.print(doc, pras);
		String printerInfo = "";

		PrintServiceAttributeSet att = printService[printerIndex].getAttributes();
		for (Attribute a : att.toArray())
		{
		    String attributeName;
		    String attributeValue;
		    attributeName = a.getName();
		    attributeValue = att.get(a.getClass()).toString();
		    if (attributeName.trim().equalsIgnoreCase("queued-job-count"))
		    {
			clsGlobalVarClass.gPrinterQueueStatus = attributeValue;
			printerInfo = primaryPrinterName + "!" + attributeValue;
			//System.out.println(attributeName + " : " + attributeValue);
		    }
		}
		if (printOnBothPrinters.equals("Y"))
		{
		    funPrintOnSecPrinter(secPrinterName, fileName);
		}
	    }
	    else
	    {
		funPrintOnSecPrinter(secPrinterName, fileName);
		//JOptionPane.showMessageDialog(null,primaryPrinterName+" Printer Not Found");
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    if (clsGlobalVarClass.gShowPrinterErrorMsg)
	    {
		try
		{
		    funPrintOnSecPrinter(secPrinterName, fileName);
		}
		catch (Exception ex)
		{
		    JOptionPane.showMessageDialog(null, "Secondary Printer Error= " + ex.getMessage());
		}
		JOptionPane.showMessageDialog(null, e.getMessage(), "Error Code - TFG 01", JOptionPane.ERROR_MESSAGE);
	    }
	}
    }

    private void funPrintOnSecPrinter(String secPrinterName, String fileName) throws Exception
    {
	String printerStatus = "Not Found";
	PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
	DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
	PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
	int printerIndex = 0;
	for (int i = 0; i < printService.length; i++)
	{
	    System.out.println("Service=" + printService[i].getName() + "\tSec P=" + secPrinterName);
	    String printerServiceName = printService[i].getName();

	    if (secPrinterName.equalsIgnoreCase(printerServiceName))
	    {
		System.out.println("Sec Printer=" + secPrinterName);
		printerIndex = i;
		printerStatus = "Found";
		break;
	    }
	}
	if (printerStatus.equals("Found"))
	{
	    String printerInfo = "";
	    DocPrintJob job = printService[printerIndex].createPrintJob();
	    FileInputStream fis = new FileInputStream(fileName);
	    DocAttributeSet das = new HashDocAttributeSet();
	    Doc doc = new SimpleDoc(fis, flavor, das);
	    job.addPrintJobListener(new MyPrintJobListener());
	    job.print(doc, pras);

	    PrintServiceAttributeSet att = printService[printerIndex].getAttributes();
	    for (Attribute a : att.toArray())
	    {
		String attributeName;
		String attributeValue;
		attributeName = a.getName();
		attributeValue = att.get(a.getClass()).toString();
		if (attributeName.trim().equalsIgnoreCase("queued-job-count"))
		{
		    clsGlobalVarClass.gPrinterQueueStatus = attributeValue;
		    printerInfo = secPrinterName + "!" + attributeValue;
		}
		System.out.println(attributeName + " : " + attributeValue);
	    }
	    if (clsGlobalVarClass.gShowBill)
	    {
		funShowTextFile(new File(fileName), "", printerInfo);
	    }
	}
	else
	{
	    if (clsGlobalVarClass.gShowPrinterErrorMsg)
	    {
		JOptionPane.showMessageDialog(null, secPrinterName + " Printer Not Found");
	    }
	}
    }

    private void funPrintCheckKOTWindows(String printerName)
    {
	try
	{
	    int printerIndex = 0;
	    String filePath = System.getProperty("user.dir");
	    String filename = (filePath + "/Temp/Temp_KOT.txt");
	    String billPrinterName = clsGlobalVarClass.gBillPrintPrinterPort;
	    billPrinterName = billPrinterName.replaceAll("#", "\\\\");
	    PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
	    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
	    PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
	    for (int i = 0; i < printService.length; i++)
	    {
		System.out.println("Sys=" + printService[i].getName() + "\tBill Printer=" + billPrinterName);
		if (billPrinterName.equalsIgnoreCase(printService[i].getName()))
		{
		    System.out.println("Bill Printer Sel=" + billPrinterName);
		    printerIndex = i;
		    break;
		}
	    }

	    DocPrintJob job = printService[printerIndex].createPrintJob();
	    FileInputStream fis = new FileInputStream(filename);
	    DocAttributeSet das = new HashDocAttributeSet();
	    Doc doc = new SimpleDoc(fis, flavor, das);
	    job.print(doc, pras);
	}
	catch (Exception e)
	{
	    if (clsGlobalVarClass.gShowPrinterErrorMsg)
	    {
		JOptionPane.showMessageDialog(null, e.getMessage(), "Error Code - TFG 01", JOptionPane.ERROR_MESSAGE);
	    }
	}
    }

    private void funPrintItemWiseKOT(String primaryPrinterName, String secPrinterName, String fileName)
    {
	String filePath = System.getProperty("user.dir");
	fileName = (filePath + "/Temp/" + fileName + ".txt");

	try
	{
	    String billPrinterName = clsGlobalVarClass.gBillPrintPrinterPort;

	    billPrinterName = billPrinterName.replaceAll("#", "\\\\");
	    int printerIndex = 0;
	    PrintRequestAttributeSet printerReqAtt = new HashPrintRequestAttributeSet();
	    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
	    PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, printerReqAtt);
	    for (int i = 0; i < printService.length; i++)
	    {
		System.out.println("Sys=" + printService[i].getName() + "\tBill Printer=" + billPrinterName);
		if (billPrinterName.equalsIgnoreCase(printService[i].getName()))
		{
		    System.out.println("ItemWise KOT Printer found=>" + billPrinterName);
		    printerIndex = i;
		    break;
		}
	    }
	    //PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
	    //DocPrintJob job = defaultService.createPrintJob();
	    DocPrintJob job = printService[printerIndex].createPrintJob();
	    FileInputStream fis = new FileInputStream(fileName);

	    DocAttributeSet das = new HashDocAttributeSet();
	    Doc doc = new SimpleDoc(fis, flavor, das);
	    job.print(doc, printerReqAtt);
	    System.out.println("Print Job Sent->" + fileName);

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    if (clsGlobalVarClass.gShowPrinterErrorMsg)
	    {
		JOptionPane.showMessageDialog(null, e.getMessage(), "Error Code - TFG 02", JOptionPane.ERROR_MESSAGE);
	    }
	}
    }

    private void funAppendDuplicate(String fileName)
    {
	try
	{
	    File fileKOTPrint = new File(fileName);
//            RandomAccessFile f = new RandomAccessFile(fileKOTPrint, "rw");
//            f.seek(0); // to the beginning                  
//            BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileKOTPrint), "UTF8"));            
//            funPrintBlankSpace("[DUPLICATE]", KotOut);            
//            KotOut.write("[DUPLICATE]");              
//            KotOut.newLine();            
//            KotOut.close();
//            f.close();                                    

	    String filePath = System.getProperty("user.dir");
	    filePath += "/Temp/Temp_KOT2.txt";
	    File fileKOTPrint2 = new File(filePath);
	    BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileKOTPrint2), "UTF8"));
	    funPrintBlankSpace("[DUPLICATE]", KotOut);
	    KotOut.write("[DUPLICATE]");
	    KotOut.newLine();

	    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileKOTPrint)));
	    String line = null;
	    while ((line = br.readLine()) != null)
	    {
		KotOut.write(line);
		KotOut.newLine();
	    }
	    br.close();
	    KotOut.close();

	    String content = new String(Files.readAllBytes(Paths.get(filePath)));
	    Files.write(Paths.get(fileName), content.getBytes(), StandardOpenOption.CREATE);

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    private void funPrintConsolidatedKOTWindows(String primaryPrinterName)
    {

	try
	{
	    int printerIndex = 0;
	    String filePath = System.getProperty("user.dir");
	    String filename = (filePath + "/Temp/Temp_KOT.txt");
	    String consolidatedPrinter = primaryPrinterName;
	    consolidatedPrinter = consolidatedPrinter.replaceAll("#", "\\\\");
	    PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
	    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
	    PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
	    for (int i = 0; i < printService.length; i++)
	    {
		System.out.println("Sys=" + printService[i].getName() + "\tConsolidated Printer=" + consolidatedPrinter);
		if (consolidatedPrinter.equalsIgnoreCase(printService[i].getName()))
		{
		    System.out.println("tConsolidated Printer Sel=" + consolidatedPrinter);
		    printerIndex = i;
		    break;
		}
	    }

	    DocPrintJob job = printService[printerIndex].createPrintJob();
	    FileInputStream fis = new FileInputStream(filename);
	    DocAttributeSet das = new HashDocAttributeSet();
	    Doc doc = new SimpleDoc(fis, flavor, das);
	    job.print(doc, pras);
	}
	catch (Exception e)
	{
	    if (clsGlobalVarClass.gShowPrinterErrorMsg)
	    {
		JOptionPane.showMessageDialog(null, e.getMessage(), "Error Code - TFG 01", JOptionPane.ERROR_MESSAGE);
	    }
	}

    }

    private void funPrintBillWindows(String type)
    {
	try
	{
	    //System.out.println("Print Bill");
	    String filePath = System.getProperty("user.dir");
	    String fileName = "";
	    String billPrinterNames[] = clsGlobalVarClass.gBillPrintPrinterPort.split(",");

	    for (int printer = 0; printer < billPrinterNames.length; printer++)
	    {
		String billPrinterName = billPrinterNames[printer];

		if (type.equalsIgnoreCase("bill"))
		{
		    fileName = (filePath + "/Temp/Temp_Bill.txt");
		}
		else if (type.equalsIgnoreCase("Adv Receipt"))
		{
		    fileName = (filePath + "/Temp/Temp_Bill.txt");
		    billPrinterName = clsGlobalVarClass.gAdvReceiptPrinterPort;
		}
		else if (type.equalsIgnoreCase("dayend"))
		{
		    fileName = (filePath + "/Temp/Temp_DayEndReport.txt");
		}

		billPrinterName = billPrinterName.replaceAll("#", "\\\\");
		int printerIndex = 0;
		PrintRequestAttributeSet printerReqAtt = new HashPrintRequestAttributeSet();
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, printerReqAtt);
		for (int i = 0; i < printService.length; i++)
		{
		    System.out.println("Sys=" + printService[i].getName() + "\tBill Printer=" + billPrinterName);
		    if (billPrinterName.equalsIgnoreCase(printService[i].getName()))
		    {
			System.out.println("Bill Printer Sel=" + billPrinterName);
			printerIndex = i;
			break;
		    }
		}
		PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
		//DocPrintJob job = defaultService.createPrintJob();
		DocPrintJob job = printService[printerIndex].createPrintJob();
		FileInputStream fis = new FileInputStream(fileName);
		DocAttributeSet das = new HashDocAttributeSet();
		Doc doc = new SimpleDoc(fis, flavor, das);
		job.print(doc, printerReqAtt);
	    }
	    if (clsGlobalVarClass.gOpenCashDrawerAfterBillPrintYN)
	    {
		objUtility.funInvokeSampleJasper();
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    if (clsGlobalVarClass.gShowPrinterErrorMsg)
	    {
		JOptionPane.showMessageDialog(null, e.getMessage(), "Error Code - TFG 02", JOptionPane.ERROR_MESSAGE);
	    }
	}
    }

    /**
     *
     * @param primaryPrinterName
     * @param secPrinterName
     * @param type
     * @param fileName
     */
    public void funPrintToPrinterForItemWise(String primaryPrinterName, String secPrinterName, String type, String fileName)
    {
	try
	{
	    if (type.equalsIgnoreCase("kot") || type.equalsIgnoreCase("checkkot"))
	    {
		fileName = "Temp/Temp_KOT.txt";
	    }
	    else if (type.equalsIgnoreCase("dayend"))
	    {
		fileName = "Temp/Temp_DayEndReport.txt";
	    }
	    else if (type.equalsIgnoreCase("ItemWiseKOT"))
	    {
		fileName = fileName;
	    }
	    else
	    {
		fileName = "Temp/Temp_Bill.txt";
	    }

	    if ("windows".equalsIgnoreCase(clsPosConfigFile.gPrintOS) && clsGlobalVarClass.gPrintType.equalsIgnoreCase("Text File"))
	    {
		if (type.equalsIgnoreCase("ItemWiseKOT"))
		{
		    if (clsGlobalVarClass.gPrintKOTYN)
		    {
			funPrintItemWiseKOT(primaryPrinterName, secPrinterName, fileName);
		    }
		}
	    }
	    else if ("linux".equalsIgnoreCase(clsPosConfigFile.gPrintOS) && clsGlobalVarClass.gPrintType.equalsIgnoreCase("Text File"))
	    {
		primaryPrinterName = clsGlobalVarClass.gBillPrintPrinterPort;
		Process process = Runtime.getRuntime().exec("lpr -P " + primaryPrinterName + " " + fileName, null);
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    /**
     *
     * @param billHdTableName
     * @param billNo
     * @param BillOut
     * @param billPrintSize
     * @return
     * @throws Exception
     */
    public int funPrintTakeAway(String billHdTableName, String billNo, BufferedWriter BillOut, int billPrintSize) throws Exception
    {
	String sqlTakeAway = "select strOperationType,strTakeAwayRemarks "
		+ " from " + billHdTableName + " "
		+ " where strBillNo='" + billNo + "'";
	ResultSet rsBill = clsGlobalVarClass.dbMysql.executeResultSet(sqlTakeAway);
	if (rsBill.next())
	{
	    if (rsBill.getString(1).equals("TakeAway"))
	    {
		if (billPrintSize == 4)
		{
		    BillOut.write("               Take Away ");
		    BillOut.newLine();
		    if (!rsBill.getString(1).isEmpty())
		    {
			BillOut.write("  " + rsBill.getString(2));
			BillOut.newLine();
		    }
		}
		else if (billPrintSize == 2)
		{
		    BillOut.write(new clsUtility().funPrintTextWithAlignment("Take Away", 20, "Center"));
		    BillOut.newLine();
		}
	    }
	}
	rsBill.close();
	return 1;
    }

    /**
     *
     * @param billNo
     * @param billhd
     * @return
     */
    public boolean funIsDirectBillerBill(String billNo, String billhd)
    {
	boolean flgIsDirectBillerBill = false;
	try
	{
	    String sql_checkDirectBillerBill = "select strTableNo,strOperationType "
		    + " from " + billhd + " where strBillNo=?  ";
	    PreparedStatement pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_checkDirectBillerBill);
	    //pst.setString(1, billhd);
	    pst.setString(1, billNo);
	    ResultSet rsIsDirectBillBill = pst.executeQuery();
	    if (rsIsDirectBillBill.next())
	    {
		if (rsIsDirectBillBill.getString(1) != null && rsIsDirectBillBill.getString(1).trim().isEmpty())
		{
		    flgIsDirectBillerBill = true;
		}
	    }
	    rsIsDirectBillBill.close();
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	return flgIsDirectBillerBill;
    }

    /**
     *
     * @param title
     * @param total
     * @param out
     * @param format
     */
    public void funWriteTotal(String title, String total, BufferedWriter out, String format)
    {
	try
	{
	    int counter = 0;
	    out.write("  ");
	    counter = counter + 2;
	    int length = title.length();
	    out.write(title);
	    counter = counter + length;
	    funWriteFormattedAmt(counter, total, out, format);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    /*
     * Please Do Not modify Space in this Function Ritesh 10 Sept 214
     *
     *
     */
    private void funWriteFormattedAmt(int counter, String Amount, BufferedWriter out, String format)
    {
	try
	{
	    int space = 30;

	    if (format.equals("Format1"))
	    {
		space = 29;
	    }
	    if (format.equals("Format3"))
	    {
		space = 29;
	    }
	    if (format.equals("Format4"))
	    {
		space = 34;
	    }
	    if (format.equals("Format5"))
	    {
		space = 29;
	    }
	    if (format.equals("Format6"))
	    {
		space = 30;
	    }
	    if (format.equals("Format11"))
	    {
		space = 12;
	    }
	    if (format.equals("Format13"))
	    {
		space = 29;
	    }
	    int usedSpace = space - counter;
	    for (int i = 0; i < usedSpace; i++)
	    {
		out.write(" ");
	    }
	    out.write("  ");
	    String tempAmount = Amount;

	    int length = tempAmount.length();
	    switch (length)
	    {
		case 1:
		    out.write("        " + tempAmount);//8
		    break;
		case 2:
		    out.write("       " + tempAmount);//7
		    break;
		case 3:
		    out.write("      " + tempAmount);//6
		    break;
		case 4:
		    out.write("     " + tempAmount);//5
		    break;
		case 5:
		    out.write("    " + tempAmount);//4
		    break;
		case 6:
		    out.write("   " + tempAmount);//3
		    break;
		case 7:
		    out.write("  " + tempAmount);//2
		    break;
		case 8:
		    out.write(" " + tempAmount);//1
		    break;
		case 9:
		    out.write(tempAmount);//0
		    break;
		default:
		    out.write(tempAmount);//0
		    break;
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    /**
     *
     * @param out
     * @param qyt
     * @param ItemName
     * @param Amount
     * @param format
     */
    public void funWriteToText(BufferedWriter out, String qyt, String ItemName, String Amount, String format)
    {
	try
	{
	    int counter = 0;
	    out.write("  ");
	    counter = counter + 2;
	    //Qty write 
	    String tempQty = qyt;
	    int length = tempQty.length();

	    switch (length)
	    {
		case 1:
		    out.write("   " + tempQty);//space
		    counter = counter + 3 + length;
		    break;
//                
//                case 2:
//                    out.write("     " + tempQty);//5space
//                    counter = counter + 3 + length;
//                    break;
//                
//                case 3:
//                    out.write("    " + tempQty);//4space
//                    counter = counter + 3 + length;
//                    break;

		case 4:
		    out.write("   " + tempQty);//3space
		    counter = counter + 3 + length;
		    break;

		case 5:
		    out.write("  " + tempQty);//2space
		    counter = counter + 2 + length;
		    break;

		case 6:
		    out.write(" " + tempQty);//1space
		    counter = counter + 1 + length;
		    break;

		case 7:
		    out.write(tempQty);
		    counter = counter + length;
		    break;
	    }

	    //End of Qty write 
	    out.write("  ");
	    counter = counter + 2;

	    //Item Write 
	    String tempItemName = ItemName;
	    length = tempItemName.length();
	    if (length < 20)
	    {
		out.write(tempItemName);
		counter = counter + length;
		funWriteFormattedAmt(counter, Amount, out, format);
	    }
	    else
	    {
		String partOne = tempItemName.substring(0, 19);
		out.write(partOne);
		counter = counter + partOne.length();
		String partTwo = tempItemName.substring(19, length);
		funWriteFormattedAmt(counter, Amount, out, format);
		out.newLine();
		out.write("           " + partTwo);
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    /**
     *
     * @param objBillOut
     * @param billPrinteSize
     * @param billNo
     * @param billDate
     * @param billTaxDtl
     * @throws IOException
     */
    public void funPrintServiceVatNo(BufferedWriter objBillOut, int billPrinteSize, String billNo, String billDate, String billTaxDtl) throws IOException
    {
	clsUtility objUtility = new clsUtility();
	Map<String, String> mapBillNote = new HashMap<>();

	try
	{
	    String billNote = "";
	    String sql = "select a.strTaxCode,a.strTaxDesc,a.strBillNote "
		    + "from tbltaxhd a," + billTaxDtl + " b "
		    + "where a.strTaxCode=b.strTaxCode "
		    + "and b.strBillNo='" + billNo + "' "
		    + "and date(b.dteBillDate)='" + billDate + "' "
		    + "order by a.strBillNote ";
	    ResultSet rsBillNote = clsGlobalVarClass.dbMysql.executeResultSet(sql);
	    while (rsBillNote.next())
	    {
		billNote = rsBillNote.getString(3).trim();
		if (!billNote.isEmpty())
		{
		    mapBillNote.put(billNote, billNote);
		}
	    }
	    rsBillNote.close();

	    sql = "select a.strPOSCode,a.strBillSeries,a.strHdBillNo,a.strDtlBillNos,a.dblGrandTotal,b.strBillNote "
		    + "from tblbillseriesbilldtl a,tblbillseries b "
		    + "where a.strBillSeries=b.strBillSeries "
		    + "and a.strHdBillNo='" + billNo + "' "
		    + "and date(a.dteBillDate)='" + billDate + "' ";
	    rsBillNote = clsGlobalVarClass.dbMysql.executeResultSet(sql);
	    if (rsBillNote.next())
	    {
		billNote = rsBillNote.getString(6).trim();
		if (!billNote.isEmpty())
		{
		    mapBillNote.put(billNote, billNote);
		}

	    }
	    rsBillNote.close();

	    for (String printBillNote : mapBillNote.values())
	    {
		objBillOut.write("  " + printBillNote);
		objBillOut.newLine();
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    /**
     *
     * @param kotNo
     * @param itemQty
     * @param itemRate
     * @param price
     * @param out
     * @param format
     * @param flgDirectBiller
     * @param discAmt
     * @throws Exception
     */
    public void funPrintItemDetails(String kotNo, String itemQty, String itemRate, String price, BufferedWriter out, String format, boolean flgDirectBiller, String discAmt) throws Exception
    {
	int len = 0;

	if (format.equalsIgnoreCase("Format6") || format.equalsIgnoreCase("Format13"))
	{
	    len = 10;

	    if (flgDirectBiller)
	    {
		int qtyLength = len - itemQty.trim().length();
		for (int i = 0; i < qtyLength; i++)
		{
		    out.write(" ");
		}
		out.write(itemQty);

		int itemRateLength = len - itemRate.trim().length();
		for (int i = 0; i < itemRateLength; i++)
		{
		    out.write(" ");
		}
		out.write(itemRate);

		int intDiscAmtLen = len - discAmt.trim().length();
		for (int i = 0; i < intDiscAmtLen; i++)
		{
		    out.write(" ");
		}
		out.write(discAmt);

		int priceLength = len - price.trim().length();
		for (int i = 0; i < priceLength; i++)
		{
		    out.write(" ");
		}
		out.write(price);
	    }
	    else
	    {
		if (kotNo.length() > 0)
		{
		    StringBuilder sbKOT = new StringBuilder(kotNo);
		    kotNo = sbKOT.delete(0, 2).toString();
		    long kot = Long.parseLong(kotNo);
		    int kotLength = 11 - String.valueOf(kot).trim().length();
		    for (int i = 0; i < kotLength; i++)
		    {
			out.write(" ");
		    }
		    out.write(String.valueOf(kot));
		}

		int qtyLength = len - itemQty.trim().length();
		for (int i = 0; i < qtyLength; i++)
		{
		    out.write(" ");
		}
		out.write(itemQty);

		int itemRateLength = len - itemRate.trim().length();
		for (int i = 0; i < itemRateLength; i++)
		{
		    out.write(" ");
		}
		out.write(itemRate);

		int priceLength = len - price.trim().length();
		for (int i = 0; i < priceLength; i++)
		{
		    out.write(" ");
		}
		out.write(price);

	    }
	}
	else
	{
	    len = 11;
	    if (kotNo.length() > 0)
	    {
		StringBuilder sbKOT = new StringBuilder(kotNo);
		kotNo = sbKOT.delete(0, 2).toString();
		long kot = Long.parseLong(kotNo);
		int kotLength = 11 - String.valueOf(kot).trim().length();
		for (int i = 0; i < kotLength; i++)
		{
		    out.write(" ");
		}
		out.write(String.valueOf(kot));
	    }

	    int qtyLength = len - itemQty.trim().length();
	    for (int i = 0; i < qtyLength; i++)
	    {
		out.write(" ");
	    }
	    out.write(itemQty);

	    int itemRateLength = len - itemRate.trim().length();
	    for (int i = 0; i < itemRateLength; i++)
	    {
		out.write(" ");
	    }
	    out.write(itemRate);

	    int intDiscAmtLen = len - discAmt.trim().length();
	    for (int i = 0; i < intDiscAmtLen; i++)
	    {
		out.write(" ");
	    }
	    out.write(discAmt);

	    int priceLength = len - price.trim().length();
	    for (int i = 0; i < priceLength; i++)
	    {
		out.write(" ");
	    }
	    out.write(price);
	}

	out.newLine();
	out.newLine();
    }

    /**
     *
     * @param kotNo
     * @param itemQty
     * @param itemRate
     * @param price
     * @param out
     * @param format
     * @param flgDirectBiller
     * @throws Exception
     */
    public void funPrintItemDetails(String kotNo, String itemQty, String itemRate, String price, BufferedWriter out, String format, boolean flgDirectBiller) throws Exception
    {
	int len = 0;
	if (kotNo.length() > 0)
	{
	    StringBuilder sbKOT = new StringBuilder(kotNo);
	    kotNo = sbKOT.delete(0, 2).toString();
	    long kot = Long.parseLong(kotNo);
	    int kotLength = 11 - String.valueOf(kot).trim().length();
	    for (int i = 0; i < kotLength; i++)
	    {
		out.write(" ");
	    }
	    out.write(String.valueOf(kot));
	}
	if (format.equalsIgnoreCase("Format6"))
	{
	    len = 10;
	}
	else
	{
	    len = 11;
	}

	if (flgDirectBiller)
	{
	    len = 15;
	}
	int qtyLength = len - itemQty.trim().length();
	for (int i = 0; i < qtyLength; i++)
	{
	    out.write(" ");
	}
	out.write(itemQty);

	if (flgDirectBiller)
	{
	    len = 15;
	}
	int itemRateLength = len - itemRate.trim().length();
	for (int i = 0; i < itemRateLength; i++)
	{
	    out.write(" ");
	}
	out.write(itemRate);

	if (flgDirectBiller)
	{
	    len = 15;
	}
	else
	{
	    len = 12;
	}
	if (format.equalsIgnoreCase("Format6"))
	{
	    len = 11;
	}
	int priceLength = len - price.trim().length();
	for (int i = 0; i < priceLength; i++)
	{
	    out.write(" ");
	}
	out.write(price);

	out.newLine();
	out.newLine();
    }

    /**
     *
     * @param out
     * @param memberName
     * @param format
     */
    public void funWriteToTextMemberNameForFormat5(BufferedWriter out, String memberName, String format)
    {
	try
	{
	    int counter = 0;
	    counter = counter + 2;
	    //Item Write 
	    String tempItemName = memberName;
	    int length = tempItemName.length();
	    if (length < 25)
	    {
		out.write(tempItemName);
		counter = counter + length;
	    }
	    else
	    {
		String partOne = tempItemName.substring(0, 24);
		out.write(partOne);
		counter = counter + partOne.length();
		String partTwo = tempItemName.substring(24, length - 1);
		out.newLine();
		out.write("                " + partTwo);
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    /**
     *
     * @param align
     * @param textToPrint
     * @param totalLength
     * @param pw
     * @return
     * @throws Exception
     */
    public int funPrintContentWithSpace(String align, String textToPrint, int totalLength, BufferedWriter pw) throws Exception
    {
	int len = totalLength - textToPrint.trim().length();
	if (align.equalsIgnoreCase("Left"))
	{
	    pw.write(textToPrint.trim());
	    for (int cnt = 0; cnt < len; cnt++)
	    {
		pw.write(" ");
	    }
	}
	else if (align.equalsIgnoreCase("Right"))
	{
	    for (int cnt = 0; cnt < len; cnt++)
	    {
		pw.write(" ");
	    }
	    pw.write(textToPrint.trim());
	}

	return 1;
    }

    /**
     *
     * @param out
     * @param qyt
     * @param ItemName
     * @param Amount
     * @param format
     */
    public void funWriteToTextformat5(BufferedWriter out, String qyt, String ItemName, String Amount, String format)
    {
	try
	{
	    int counter = 0;
	    out.write("  ");
	    counter = counter + 2;
	    //Qty write 
	    String tempQty = qyt;
	    int length = tempQty.length();

	    switch (length)
	    {
		case 4:
		    out.write("   " + tempQty);//3space
		    counter = counter + 3 + length;
		    break;

		case 5:
		    out.write(" " + tempQty);//2space
		    counter = counter + 1 + length;
		    break;

		case 6:
		    out.write(" " + tempQty);//1space
		    counter = counter + 1 + length;
		    break;

		case 7:
		    out.write(tempQty);
		    counter = counter + length;
		    break;
	    }

	    //End of Qty write 
	    out.write("  ");
	    counter = counter + 2;

	    //Item Write 
	    String tempItemName = ItemName;
	    length = tempItemName.length();
	    if (length < 19)
	    {
		out.write(tempItemName);
		counter = counter + length;
		funWriteFormattedAmt(counter, Amount, out, format);
	    }
	    else
	    {
		String partOne = tempItemName.substring(0, 18);
		out.write(partOne);
		counter = counter + partOne.length();
		String partTwo = tempItemName.substring(19, length - 1);
		funWriteFormattedAmt(counter, Amount, out, format);
		out.newLine();
		out.write("           " + partTwo);
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    /**
     *
     * @param billNo
     * @param objBillOut
     * @param billPrintSize
     * @param POSCode
     * @param billDate
     * @param sbZeroAmtItems
     * @return
     * @throws Exception
     */
    public int funPrintComplimentaryItemsInBill(String billNo, BufferedWriter objBillOut, int billPrintSize, String POSCode, String billDate, StringBuilder sbZeroAmtItems) throws Exception
    {
	if (sbZeroAmtItems.length() > 0)
	{
	    sbZeroAmtItems = sbZeroAmtItems.delete(0, 1);
	}
	clsUtility objUtility = new clsUtility();
	String sqlBillComplDtl = "select b.strItemName,b.dblQuantity "
		+ " from tblbillhd a,tblbillcomplementrydtl b "
		+ " where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
		+ " and date(a.dteBillDate)=date(b.dteBillDate) and a.strBillNo='" + billNo + "' "
		+ " and a.strPOSCode='" + POSCode + "'  and date(a.dteBillDate)='" + billDate + "' "
		+ " and a.strClientCode='" + clsGlobalVarClass.gClientCode + "' and b.strType='ItemComplimentary' ";
	if (clsGlobalVarClass.gPrintOpenItemsOnBill)
	{
	    if (sbZeroAmtItems.length() > 0)
	    {
		sqlBillComplDtl = sqlBillComplDtl + " and b.strItemCode not in (" + sbZeroAmtItems + ") ";
	    }
	}

	ResultSet rsBillComplItemDtl = clsGlobalVarClass.dbMysql.executeResultSet(sqlBillComplDtl);
	while (rsBillComplItemDtl.next())
	{
	    if (billPrintSize == 4)
	    {
		//funWriteToText(objBillOut, funReduceTo2DecimalPlaces(rsBillPromoItemDtl.getString(2)), rsBillPromoItemDtl.getString(1).toUpperCase(), "0", "Format1");
		String qty = rsBillComplItemDtl.getString(2);
		if (qty.contains("."))
		{
		    String decVal = qty.substring(qty.length() - 1, qty.length());
		    if (Double.parseDouble(decVal) == 0)
		    {
			qty = qty.substring(0, qty.length() - 3);
		    }
		}
		objBillOut.write(" ");
		objBillOut.write(objUtility.funPrintTextWithAlignment(qty, 7, "Right"));
		objBillOut.write(" ");
		objBillOut.write(objUtility.funPrintTextWithAlignment(rsBillComplItemDtl.getString(1).toUpperCase(), 20, "Left"));
		objBillOut.write(objUtility.funPrintTextWithAlignment("0.00", 11, "Right"));

		objBillOut.newLine();
	    }
	    else if (billPrintSize == 2)
	    {
		if (rsBillComplItemDtl.getString(1).toUpperCase().length() > 20)
		{
		    List listTextToPrint = funGetTextWithSpecifiedSize(rsBillComplItemDtl.getString(1).toUpperCase(), 2);
		    for (int cnt = 0; cnt < listTextToPrint.size(); cnt++)
		    {
			objBillOut.write(objUtility.funPrintTextWithAlignment(listTextToPrint.get(cnt).toString(), 20, "Left"));
			objBillOut.newLine();
		    }
		}
		else
		{
		    objBillOut.write(objUtility.funPrintTextWithAlignment(rsBillComplItemDtl.getString(1).toUpperCase(), 20, "Left"));
		    objBillOut.newLine();
		}
		//objBillOut.write(new clsUtility().funPrintTextWithAlignment(funReduceTo2DecimalPlaces(rsBillPromoItemDtl.getString(2)), 6, "Right"));
		objBillOut.write(objUtility.funPrintTextWithAlignment(rsBillComplItemDtl.getString(2), 6, "Right"));
		objBillOut.write(objUtility.funPrintTextWithAlignment("  ", 7, "Right"));
		objBillOut.write(objUtility.funPrintTextWithAlignment("0", 7, "Right"));
		objBillOut.newLine();
	    }
	}
	rsBillComplItemDtl.close();
	objUtility = null;
	return 1;
    }

    /**
     *
     * @param billNo
     * @param objBillOut
     * @param billPrintSize
     * @return
     * @throws Exception
     */
    public int funPrintPromoItemsInBill(String billNo, BufferedWriter objBillOut, int billPrintSize) throws Exception
    {
	clsUtility objUtility = new clsUtility();
	clsUtility2 objUtility2 = new clsUtility2();
	String sqlBillPromoDtl = "select b.strItemName,a.dblQuantity,'0',dblRate "
		+ " from tblbillpromotiondtl a,tblitemmaster b "
		+ " where a.strItemCode=b.strItemCode and a.strBillNo='" + billNo + "' and a.strPromoType!='Discount' ";
	ResultSet rsBillPromoItemDtl = clsGlobalVarClass.dbMysql.executeResultSet(sqlBillPromoDtl);
	while (rsBillPromoItemDtl.next())
	{
	    if (billPrintSize == 4)
	    {
		//funWriteToText(objBillOut, funReduceTo2DecimalPlaces(rsBillPromoItemDtl.getString(2)), rsBillPromoItemDtl.getString(1).toUpperCase(), "0", "Format1");
		String qty = rsBillPromoItemDtl.getString(2);
		if (qty.contains("."))
		{
		    String decVal = qty.substring(qty.length() - 1, qty.length());
		    if (Double.parseDouble(decVal) == 0)
		    {
			qty = qty.substring(0, qty.length() - 3);
		    }
		}
		objBillOut.write(" ");
		objBillOut.write(objUtility.funPrintTextWithAlignment(qty, 7, "Right"));
		objBillOut.write(" ");
		objBillOut.write(objUtility.funPrintTextWithAlignment(rsBillPromoItemDtl.getString(1).toUpperCase(), 20, "Left"));
		objBillOut.write(objUtility.funPrintTextWithAlignment("0.00", 11, "Right"));

		objBillOut.newLine();
	    }
	    else if (billPrintSize == 2)
	    {
		if (rsBillPromoItemDtl.getString(1).toUpperCase().length() > 20)
		{
		    List listTextToPrint = funGetTextWithSpecifiedSize(rsBillPromoItemDtl.getString(1).toUpperCase(), 2);
		    for (int cnt = 0; cnt < listTextToPrint.size(); cnt++)
		    {
			objBillOut.write(objUtility.funPrintTextWithAlignment(listTextToPrint.get(cnt).toString(), 20, "Left"));
			objBillOut.newLine();
		    }
		}
		else
		{
		    objBillOut.write(objUtility.funPrintTextWithAlignment(rsBillPromoItemDtl.getString(1).toUpperCase(), 20, "Left"));
		    objBillOut.newLine();
		}
		//objBillOut.write(new clsUtility().funPrintTextWithAlignment(funReduceTo2DecimalPlaces(rsBillPromoItemDtl.getString(2)), 6, "Right"));
		objBillOut.write(new clsUtility().funPrintTextWithAlignment(rsBillPromoItemDtl.getString(2), 6, "Right"));
		objBillOut.write(new clsUtility().funPrintTextWithAlignment("  ", 7, "Right"));
		objBillOut.write(new clsUtility().funPrintTextWithAlignment("0", 7, "Right"));
		objBillOut.newLine();
	    }
	}
	rsBillPromoItemDtl.close();
	objUtility = null;
	return 1;
    }

    /**
     *
     * @param billNo
     * @param objBillOut
     * @param billPrintSize
     * @param billPromoDtl
     * @return
     * @throws Exception
     */
    public int funPrintPromoItemsInBill(String billNo, BufferedWriter objBillOut, int billPrintSize, String billPromoDtl) throws Exception
    {
	clsUtility objUtility = new clsUtility();
	String sqlBillPromoDtl = "select b.strItemName,a.dblQuantity,'0',dblRate "
		+ " from " + billPromoDtl + " a,tblitemmaster b "
		+ " where a.strItemCode=b.strItemCode and a.strBillNo='" + billNo + "' and a.strPromoType!='Discount' ";
	ResultSet rsBillPromoItemDtl = clsGlobalVarClass.dbMysql.executeResultSet(sqlBillPromoDtl);
	while (rsBillPromoItemDtl.next())
	{
	    if (billPrintSize == 4)
	    {
		//funWriteToText(objBillOut, funReduceTo2DecimalPlaces(rsBillPromoItemDtl.getString(2)), rsBillPromoItemDtl.getString(1).toUpperCase(), "0", "Format1");
		String qty = rsBillPromoItemDtl.getString(2);
		if (qty.contains("."))
		{
		    String decVal = qty.substring(qty.length() - 1, qty.length());
		    if (Double.parseDouble(decVal) == 0)
		    {
			qty = qty.substring(0, qty.length() - 3);
		    }
		}
		objBillOut.write(" ");
		objBillOut.write(objUtility.funPrintTextWithAlignment(qty, 7, "Right"));
		objBillOut.write(" ");
		objBillOut.write(objUtility.funPrintTextWithAlignment(rsBillPromoItemDtl.getString(1).toUpperCase(), 20, "Left"));
		objBillOut.write(objUtility.funPrintTextWithAlignment("0.00", 11, "Right"));

		objBillOut.newLine();
	    }
	    else if (billPrintSize == 2)
	    {
		if (rsBillPromoItemDtl.getString(1).toUpperCase().length() > 20)
		{
		    List listTextToPrint = funGetTextWithSpecifiedSize(rsBillPromoItemDtl.getString(1).toUpperCase(), 2);
		    for (int cnt = 0; cnt < listTextToPrint.size(); cnt++)
		    {
			objBillOut.write(objUtility.funPrintTextWithAlignment(listTextToPrint.get(cnt).toString(), 20, "Left"));
			objBillOut.newLine();
		    }
		}
		else
		{
		    objBillOut.write(objUtility.funPrintTextWithAlignment(rsBillPromoItemDtl.getString(1).toUpperCase(), 20, "Left"));
		    objBillOut.newLine();
		}
		//objBillOut.write(new clsUtility().funPrintTextWithAlignment(funReduceTo2DecimalPlaces(rsBillPromoItemDtl.getString(2)), 6, "Right"));
		objBillOut.write(new clsUtility().funPrintTextWithAlignment(rsBillPromoItemDtl.getString(2), 6, "Right"));
		objBillOut.write(new clsUtility().funPrintTextWithAlignment("  ", 7, "Right"));
		objBillOut.write(new clsUtility().funPrintTextWithAlignment("0", 7, "Right"));
		objBillOut.newLine();
	    }
	}
	rsBillPromoItemDtl.close();
	objUtility = null;
	return 1;
    }

    /**
     *
     * @param billNo
     * @param billPrintSize
     * @param listOfBillDetail
     * @return
     * @throws Exception
     */
    public int funPrintPromoItemsInBill(String billNo, int billPrintSize, List<clsBillDtl> listOfBillDetail) throws Exception
    {
	String sqlBillPromoDtl = "select b.strItemName,a.dblQuantity,'0',dblRate "
		+ " from tblbillpromotiondtl a,tblitemmaster b "
		+ " where a.strItemCode=b.strItemCode and a.strBillNo='" + billNo + "' and a.strPromoType!='Discount' ";
	ResultSet rsBillPromoItemDtl = clsGlobalVarClass.dbMysql.executeResultSet(sqlBillPromoDtl);
	clsBillDtl objBillDtl = null;
	while (rsBillPromoItemDtl.next())
	{
	    objBillDtl = new clsBillDtl();
	    objBillDtl.setDblQuantity(rsBillPromoItemDtl.getDouble(2));
	    objBillDtl.setDblAmount(rsBillPromoItemDtl.getDouble(3));
	    objBillDtl.setStrItemName(rsBillPromoItemDtl.getString(1).toUpperCase());
	    listOfBillDetail.add(objBillDtl);
	}
	rsBillPromoItemDtl.close();
	return 1;
    }

    /**
     *
     * @param billNo
     * @param billPrintSize
     * @param listOfBillDetail
     * @return
     * @throws Exception
     */
    public int funPrintPromoItemsInBillMuscat(String billNo, int billPrintSize, List<clsBillDtlForMuscat> listOfBillDetail) throws Exception
    {
	DecimalFormat decimalFormat = new DecimalFormat("#.###");
	String sqlBillPromoDtl = "select b.strItemName,a.dblQuantity,'0',dblRate "
		+ " from tblbillpromotiondtl a,tblitemmaster b "
		+ " where a.strItemCode=b.strItemCode and a.strBillNo='" + billNo + "' and a.strPromoType!='Discount' ";
	ResultSet rsBillPromoItemDtl = clsGlobalVarClass.dbMysql.executeResultSet(sqlBillPromoDtl);
	clsBillDtlForMuscat objBillDtl = null;
	while (rsBillPromoItemDtl.next())
	{
	    objBillDtl = new clsBillDtlForMuscat();
	    objBillDtl.setDblQuantity(decimalFormat.format(Double.parseDouble(rsBillPromoItemDtl.getString(2))));
	    objBillDtl.setDblAmount(rsBillPromoItemDtl.getString(3));
	    objBillDtl.setStrItemName(rsBillPromoItemDtl.getString(1).toUpperCase());
	    listOfBillDetail.add(objBillDtl);
	}
	rsBillPromoItemDtl.close();
	return 1;
    }

    /**
     *
     * @param text
     * @param size
     * @return
     */
    public List funGetTextWithSpecifiedSize(String text, int size)
    {
	List listText = new ArrayList();
	try
	{
	    System.out.println(text);
	    if (size == 2)
	    {
		StringBuilder sbText = new StringBuilder();
		StringBuilder sbTempText = new StringBuilder();
		String[] arrTextToPrint = text.split(" ");
		for (int cnt = 0; cnt < arrTextToPrint.length; cnt++)
		{
		    sbTempText.append(arrTextToPrint[cnt] + " ");
		    if (sbTempText.length() > 20)
		    {
			String tempText = sbText.substring(0, sbText.lastIndexOf(" "));
			System.out.println("Add To List " + tempText);
			if (!tempText.isEmpty())
			{
			    listText.add(tempText);
			}
			sbText.setLength(0);
			sbTempText.setLength(0);

			sbTempText.append(arrTextToPrint[cnt] + " ");
			sbText.append(arrTextToPrint[cnt] + " ");
		    }
		    else
		    {
			sbText.append(arrTextToPrint[cnt] + " ");
		    }

		    if ((cnt == arrTextToPrint.length - 1) && sbTempText.length() > 0)
		    {
			listText.add(sbText);
		    }
		}
	    }
	    System.out.println("List= " + listText);

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	return listText;
    }

    /**
     *
     * @param billHdTableName
     * @param billNo
     * @return
     * @throws Exception
     */
    public int funPrintTakeAwayForJasper(String billHdTableName, String billNo) throws Exception
    {
	int res = 0;
	String sqlTakeAway = "select strOperationType from " + billHdTableName + " "
		+ " where strBillNo='" + billNo + "'";
	ResultSet rsBill = clsGlobalVarClass.dbMysql.executeResultSet(sqlTakeAway);
	if (rsBill.next())
	{
	    if (rsBill.getString(1).equals("TakeAway"))
	    {
		res = 1;
	    }
	}
	rsBill.close();
	return res;
    }

    /**
     *
     * @param billNo
     * @param billDate
     * @param billTaxDtl
     * @return
     * @throws IOException
     */
    public List<clsBillDtl> funPrintServiceVatNoForJasper(String billNo, String billDate, String billTaxDtl) throws IOException
    {
	List<clsBillDtl> listOfServiceVatDetail = new ArrayList<>();
	clsBillDtl objBillDtl = null;
	clsUtility objUtility = new clsUtility();
	Map<String, String> mapBillNote = new HashMap<>();

	try
	{
	    String billNote = "";
	    String sql = "select a.strTaxCode,a.strTaxDesc,a.strBillNote "
		    + "from tbltaxhd a," + billTaxDtl + " b "
		    + "where a.strTaxCode=b.strTaxCode "
		    + "and b.strBillNo='" + billNo + "' "
		    + "and date(b.dteBillDate)='" + billDate + "' "
		    + "order by a.strBillNote ";
	    ResultSet rsBillNote = clsGlobalVarClass.dbMysql.executeResultSet(sql);
	    while (rsBillNote.next())
	    {
		billNote = rsBillNote.getString(3).trim();
		if (!billNote.isEmpty())
		{
		    mapBillNote.put(billNote, billNote);
		}

	    }
	    rsBillNote.close();

	    sql = "select a.strPOSCode,a.strBillSeries,a.strHdBillNo,a.strDtlBillNos,a.dblGrandTotal,b.strBillNote "
		    + "from tblbillseriesbilldtl a,tblbillseries b "
		    + "where a.strBillSeries=b.strBillSeries "
		    + "and a.strHdBillNo='" + billNo + "' "
		    + "and date(a.dteBillDate)='" + billDate + "' ";
	    rsBillNote = clsGlobalVarClass.dbMysql.executeResultSet(sql);
	    if (rsBillNote.next())
	    {
		billNote = rsBillNote.getString(6).trim();
		if (!billNote.isEmpty())
		{
		    mapBillNote.put(billNote, billNote);
		}

	    }
	    rsBillNote.close();

	    for (String printBillNote : mapBillNote.values())
	    {
		objBillDtl = new clsBillDtl();
		objBillDtl.setStrItemName(printBillNote);
		listOfServiceVatDetail.add(objBillDtl);
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

	return listOfServiceVatDetail;
    }

    /**
     *
     * @param billNo
     * @param billDate
     * @param billTaxDtl
     * @return
     * @throws IOException
     */
    public List<clsBillDtlForMuscat> funPrintServiceVatNoForJasperMuscat(String billNo, String billDate, String billTaxDtl) throws IOException
    {
	List<clsBillDtlForMuscat> listOfServiceVatDetail = new ArrayList<>();
	clsBillDtlForMuscat objBillDtl = null;
	clsUtility objUtility = new clsUtility();
	Map<String, String> mapBillNote = new HashMap<>();

	try
	{
	    String billNote = "";
	    String sql = "select a.strTaxCode,a.strTaxDesc,a.strBillNote "
		    + "from tbltaxhd a," + billTaxDtl + " b "
		    + "where a.strTaxCode=b.strTaxCode "
		    + "and b.strBillNo='" + billNo + "' "
		    + "and date(b.dteBillDate)='" + billDate + "' "
		    + "order by a.strBillNote ";
	    ResultSet rsBillNote = clsGlobalVarClass.dbMysql.executeResultSet(sql);
	    while (rsBillNote.next())
	    {
		billNote = rsBillNote.getString(3).trim();
		if (!billNote.isEmpty())
		{
		    mapBillNote.put(billNote, billNote);
		}
//
//                if (billNote.length() > 0)
//                {
//                    objBillOut.write("  " + billNote);
//                    objBillOut.newLine();
//                }
	    }
	    rsBillNote.close();

	    for (String printBillNote : mapBillNote.values())
	    {
		objBillDtl = new clsBillDtlForMuscat();
		objBillDtl.setStrItemName(printBillNote);
		listOfServiceVatDetail.add(objBillDtl);
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

	return listOfServiceVatDetail;
    }

    /**
     *
     * @param print
     */
    public void funPrintJasperExporterInThread(JasperPrint print)
    {
	JRPrintServiceExporter exporter = new JRPrintServiceExporter();

	//--- Set print properties
	PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
	// printRequestAttributeSet.add(MediaSizeName.ISO_A6);
	printRequestAttributeSet.add(MediaSizeName.MONARCH_ENVELOPE);

	printRequestAttributeSet.add(new Copies(1));

	//----------------------------------------------------     
	//printRequestAttributeSet.add(new Destination(new java.net.URI("file:d:/output/report.ps")));
	//----------------------------------------------------     
	PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();

	String billPrinterName = clsGlobalVarClass.gBillPrintPrinterPort;

	billPrinterName = billPrinterName.replaceAll("#", "\\\\");
	printServiceAttributeSet.add(new PrinterName(billPrinterName, null));

	//--- Set print parameters      
	exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
	exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
	exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet);
	exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
	exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

	//--- Print the document
	try
	{
	    exporter.exportReport();
	}
	catch (JRException e)
	{
	    e.printStackTrace();
	}

    }

    /**
     *
     * @param title
     * @param total
     * @param out
     * @param format
     * @param space
     */
    public void funWriteTotalStationery(String title, String total, BufferedWriter out, String format, String space)
    {
	try
	{
	    int counter = 0;
	    out.write(space);
	    counter = counter + 2;
	    int length = title.length();
	    out.write(title);
	    counter = counter + length;
	    funWriteFormattedAmt(counter, total, out, format);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }
//for stationery bill to print blank lines
    // currentLineCount-- current line number, totalLineCount -- total number of line in single page , topSpaceLineCount --no of line from actual quantity & items printing start,
    //totalLinesForNextPage -- required blank line to contiouse print on next page ,

    /**
     *
     * @param currentLineCount
     * @param totalLineCount
     * @param topSpaceLineCount
     * @param totalLinesForNextPage
     * @param out
     * @return
     */
    public int funWriteBlankLines(int currentLineCount, int totalLineCount, int topSpaceLineCount, int totalLinesForNextPage, BufferedWriter out)
    {
	try
	{
	    if (currentLineCount == totalLineCount)
	    {
		for (int i = 0; i < totalLinesForNextPage; i++)
		{
		    out.newLine();
		}
		currentLineCount = topSpaceLineCount;
	    }
	}
	catch (Exception e)
	{

	}
	return currentLineCount;
    }

    /**
     *
     * @param text
     * @param len
     * @param out
     * @throws Exception
     */
    public void funWriteTextWithBlankLines(String text, int len, BufferedWriter out) throws Exception
    {
	int remLen = len - text.trim().length();
	out.write(text);
	for (int cn = 0; cn < remLen; cn++)
	{
	    out.write(" ");
	}
    }
}
