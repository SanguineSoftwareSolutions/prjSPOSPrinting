/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting.Text.KOT;

import com.POSPrinting.Utility.clsPrintingUtility;
import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsPosConfigFile;
import com.POSGlobal.controller.clsUtility;
import com.POSGlobal.controller.clsUtility2;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JOptionPane;

/**
 *
 * @author Ajim
 * @date Aug 26, 2017
 */
public class clsKOTTextFileGenerationForDirectBiller
{

    private DecimalFormat decimalFormat = new DecimalFormat("#.###");
    private SimpleDateFormat ddMMyyyyAMPMDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
    private clsUtility objUtility = new clsUtility();
    private clsUtility2 objUtility2 = new clsUtility2();
    private clsPrintingUtility objPrintingUtility = new clsPrintingUtility();
    private final String dashedLineFor40Chars = "  --------------------------------------";

    /**
     *
     * @param costCenterCode
     * @param areaCode
     * @param billNo
     * @param reprint
     * @param primaryPrinterName
     * @param secondaryPrinterName
     * @param costCenterName
     * @param labelOnKOT
     */
    public void funGenerateTextFileForKOTDirectBiller(String costCenterCode, String areaCode, String billNo, String reprint, String primaryPrinterName, String secondaryPrinterName, String costCenterName, String labelOnKOT)
    {
	try
	{
	    PreparedStatement pst = null;
	    objPrintingUtility.funCreateTempFolder();
	    String filePath = System.getProperty("user.dir");
	    File Text_KOT = new File(filePath + "/Temp/Temp_KOT.txt");
	    FileWriter fstream = new FileWriter(Text_KOT);
	    //BufferedWriter KotOut = new BufferedWriter(fstream);
	    BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Text_KOT), "UTF8"));
	    boolean isReprint = false;
	    String isWas = "Is";
	    if ("Reprint".equalsIgnoreCase(reprint))
	    {
		isReprint = true;
		objPrintingUtility.funPrintBlankSpace("[DUPLICATE]", KotOut);
		KotOut.write("[DUPLICATE]");
		KotOut.newLine();

		isWas = "Was";
	    }

	    String sql_PrintHomeDelivery = "select strOperationType,intOrderNo from tblbillhd where strBillNo=? ";
	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_PrintHomeDelivery);
	    pst.setString(1, billNo);
	    ResultSet rs_PrintHomeDelivery = pst.executeQuery();
	    String operationType = "";
	    if (rs_PrintHomeDelivery.next())
	    {
		operationType = rs_PrintHomeDelivery.getString(1);
		if (clsGlobalVarClass.gBillFormatType.equalsIgnoreCase("Text 19") || clsGlobalVarClass.gBillFormatType.equalsIgnoreCase("Jasper 4"))//for only "MING YANG", Raju Ki Chai("206.001","Gaurika Enterprises Pvt. Ltd.")
		{
		    String orderNo = rs_PrintHomeDelivery.getString(2);
		    KotOut.write("  Your order no is " + orderNo);
		    KotOut.newLine();
		    KotOut.newLine();
		    KotOut.newLine();
		    KotOut.newLine();
		}
	    }
	    rs_PrintHomeDelivery.close();
	    if (operationType.equalsIgnoreCase("HomeDelivery"))
	    {
		if (clsGlobalVarClass.gPrintHomeDeliveryYN)
		{
		    objPrintingUtility.funPrintBlankSpace("Home Delivery", KotOut);
		    KotOut.write("Home Delivery");
		    KotOut.newLine();
		}

	    }
	    else if (operationType.equalsIgnoreCase("TakeAway"))
	    {
		objPrintingUtility.funPrintBlankSpace("Take Away", KotOut);
		KotOut.write("Take Away");
		KotOut.newLine();
	    }

	    objPrintingUtility.funPrintBlankSpace(labelOnKOT, KotOut);
	    KotOut.write(labelOnKOT);
	    KotOut.newLine();
	    objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gPOSName, KotOut);
	    KotOut.write(clsGlobalVarClass.gPOSName);
	    KotOut.newLine();
	    objPrintingUtility.funPrintBlankSpace(costCenterName, KotOut);
	    KotOut.write(costCenterName);
	    KotOut.newLine();

	    objPrintingUtility.funPrintBlankSpace("DIRECT BILLER", KotOut);
	    KotOut.write("DIRECT BILLER");
	    KotOut.newLine();
	    KotOut.write(dashedLineFor40Chars);
	    KotOut.newLine();
	    KotOut.write("  BILL No: " + billNo);
	    KotOut.newLine();
	    KotOut.write(dashedLineFor40Chars);

	    String sql_DirectKOT_Date = "select dteBillDate from tblbilldtl where strBillNo=? ";
	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_DirectKOT_Date);
	    pst.setString(1, billNo);
	    ResultSet rs_DirectKOT_Date = pst.executeQuery();
	    if (rs_DirectKOT_Date.next())
	    {
		KotOut.newLine();
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
		KotOut.write("  DATE & TIME: " + dateTimeFormat.format(rs_DirectKOT_Date.getObject(1)));
	    }
	    rs_DirectKOT_Date.close();

	    KotOut.newLine();
	    KotOut.write(dashedLineFor40Chars);
	    KotOut.newLine();
	    KotOut.write("  QTY        ITEM NAME  ");
	    KotOut.newLine();
	    KotOut.write(dashedLineFor40Chars);

	    String areaCodeForTransaction = clsGlobalVarClass.gAreaCodeForTrans;
	    if (operationType.equalsIgnoreCase("HomeDelivery"))
	    {
		areaCodeForTransaction = clsGlobalVarClass.gHomeDeliveryAreaForDirectBiller;
	    }
	    else if (operationType.equalsIgnoreCase("TakeAway"))
	    {
		areaCodeForTransaction = clsGlobalVarClass.gTakeAwayAreaForDirectBiller;
	    }
	    else
	    {
		areaCodeForTransaction = clsGlobalVarClass.gDineInAreaForDirectBiller;
	    }

	    String sql_DirectKOT_Items = "select a.strItemCode,a.strItemName,a.dblQuantity,d.strShortName,a.strSequenceNo "
		    + "from tblbilldtl a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
		    + "where  a.strBillNo=? and  b.strCostCenterCode=c.strCostCenterCode "
		    + "and a.strItemCode=d.strItemCode "
		    + "and b.strCostCenterCode=? and (b.strAreaCode=? or b.strAreaCode='" + areaCodeForTransaction + "') "
		    + "and a.strItemCode=b.strItemCode "
		    + "group by a.strItemCode,a.strSequenceNo "
		    + "ORDER BY a.strSequenceNo;;";
	    //System.out.println(sql_DirectKOT_Items);

	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_DirectKOT_Items);
	    pst.setString(1, billNo);
	    pst.setString(2, costCenterCode);
	    pst.setString(3, areaCode);
	    ResultSet rs_DirectKOT_Items = pst.executeQuery();
	    while (rs_DirectKOT_Items.next())
	    {
		String kotItemName = rs_DirectKOT_Items.getString(2).toUpperCase();
		if (clsGlobalVarClass.gPrintShortNameOnKOT && !rs_DirectKOT_Items.getString(4).trim().isEmpty())
		{
		    kotItemName = rs_DirectKOT_Items.getString(4).toUpperCase();
		}

		KotOut.newLine();

		String itemQty = String.valueOf(decimalFormat.format(rs_DirectKOT_Items.getDouble(3)));

		KotOut.write("  " + itemQty + "      ");
		if (kotItemName.length() <= 25)
		{
		    KotOut.write(kotItemName);
		}
		else
		{
		    KotOut.write(kotItemName.substring(0, 25));
		    KotOut.newLine();
		    KotOut.write("            " + kotItemName.substring(25, kotItemName.length()));
		}
		//following code called for modifier

		String sql_Modifier = " select a.strModifierName,a.dblQuantity,ifnull(b.strDefaultModifier,'N'),a.strDefaultModifierDeselectedYN "
			+ "from tblbillmodifierdtl a "
			+ "left outer join tblitemmodofier b on left(a.strItemCode,7)=if(b.strItemCode='',a.strItemCode,b.strItemCode) "
			+ "and a.strModifierCode=if(a.strModifierCode=null,'',b.strModifierCode) "
			+ "where a.strBillNo=? "
			+ "and left(a.strItemCode,7)=? "
			+ "and left(a.strSequenceNo,1)='" + rs_DirectKOT_Items.getString(5) + "' ";
		//System.out.println(sql_Modifier);

		pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_Modifier);
		pst.setString(1, billNo);
		pst.setString(2, rs_DirectKOT_Items.getString(1));
		ResultSet rs_Modifier = pst.executeQuery();
		while (rs_Modifier.next())
		{
		    String modiQty = String.valueOf(decimalFormat.format(rs_Modifier.getDouble(2)));

		    if (!clsGlobalVarClass.gPrintModQtyOnKOT)//dont't print modifier qty
		    {
			if (rs_Modifier.getString(3).equalsIgnoreCase("Y") && rs_Modifier.getString(4).equalsIgnoreCase("Y"))
			{
			    KotOut.newLine();
			    KotOut.write("        " + "No " + rs_Modifier.getString(1).toUpperCase());
			}
			else if (!rs_Modifier.getString(3).equalsIgnoreCase("Y"))
			{
			    KotOut.newLine();
			    KotOut.write("        " + rs_Modifier.getString(1).toUpperCase());
			}
		    }
		    else
		    {
			if (rs_Modifier.getString(3).equalsIgnoreCase("Y") && rs_Modifier.getString(4).equalsIgnoreCase("Y"))
			{
			    KotOut.newLine();
			    KotOut.write("  " + modiQty + "      " + "No " + rs_Modifier.getString(1).toUpperCase());
			}
			else if (!rs_Modifier.getString(3).equalsIgnoreCase("Y"))
			{
			    KotOut.newLine();
			    KotOut.write("  " + modiQty + "      " + rs_Modifier.getString(1).toUpperCase());
			}
		    }
		}
		rs_Modifier.close();
	    }

	    rs_DirectKOT_Items.close();
	    KotOut.newLine();
	    KotOut.write(dashedLineFor40Chars);
	    KotOut.newLine();
	    KotOut.newLine();
	    KotOut.newLine();
	    KotOut.newLine();
	    KotOut.newLine();
	    KotOut.newLine();
	    if ("linux".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
	    {
		KotOut.write("V");//Linux
	    }
	    else if ("windows".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
	    {
		if ("Inbuild".equalsIgnoreCase(clsPosConfigFile.gPrinterType))
		{
		    KotOut.write("V");
		}
		else
		{
		    KotOut.write("m");//windows
		}
	    }
	    // KotOut.write("m");
	    KotOut.close();
	    fstream.close();
	    pst.close();

	    if (clsGlobalVarClass.gShowBill)
	    {
		objPrintingUtility.funShowTextFile(Text_KOT, "", "Printer Info!2");
	    }

	    if (clsGlobalVarClass.gItemWiseKOTPrintYN)
	    {
		funGenerateTextFileItemWiseKOTForDirectBiller(costCenterCode, areaCode, billNo, reprint, primaryPrinterName, secondaryPrinterName, costCenterName);
	    }

	    String sql = "select strPrintOnBothPrinters from tblcostcentermaster where strCostCenterCode='" + costCenterCode + "'";
	    ResultSet rsCostCenter = clsGlobalVarClass.dbMysql.executeResultSet(sql);
	    if (rsCostCenter.next())
	    {
		objPrintingUtility.funPrintToPrinter(primaryPrinterName, secondaryPrinterName, "kot", rsCostCenter.getString(1), isReprint);
	    }
	    rsCostCenter.close();

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    /*
     * Item wise KOT print for direct biller
     */
    private void funGenerateTextFileItemWiseKOTForDirectBiller(String costCenterCode, String areaCode, String billNo, String reprint, String primaryPrinterName, String secondaryPrinterName, String costCenterName)
    {
	try
	{
	    PreparedStatement pst = null;

	    String sql_DirectKOT_Items = "select a.strItemCode,d.strItemName,a.dblQuantity,strItemWiseKOTYN "
		    + ",left(a.strSequenceNo,1),d.strShortName "
		    + "from tblbilldtl a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
		    + "where  a.strBillNo=? and  b.strCostCenterCode=c.strCostCenterCode "
		    + "and a.strItemCode=d.strItemCode "
		    + "and b.strCostCenterCode=? and (b.strAreaCode=? or b.strAreaCode='" + clsGlobalVarClass.gAreaCodeForTrans + "') "
		    + "and a.strItemCode=b.strItemCode "
		    + "GROUP BY left(a.strSequenceNo,1),a.strItemCode; ";

	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_DirectKOT_Items);
	    pst.setString(1, billNo);
	    pst.setString(2, costCenterCode);
	    pst.setString(3, areaCode);
	    ResultSet rs_DirectKOT_Items = pst.executeQuery();
	    int i = 0;
	    while (rs_DirectKOT_Items.next())
	    {

		String itemName = rs_DirectKOT_Items.getString(2).toUpperCase();
		if (clsGlobalVarClass.gPrintShortNameOnKOT && !rs_DirectKOT_Items.getString(6).trim().isEmpty())
		{
		    itemName = rs_DirectKOT_Items.getString(6).toUpperCase();
		}

		if (rs_DirectKOT_Items.getString(4).equalsIgnoreCase("Y"))
		{
		    String itemCode = rs_DirectKOT_Items.getString(1);
		    String fileName = "KOTFOR" + rs_DirectKOT_Items.getString(2).toUpperCase() + "" + (++i);
		    BufferedWriter KotOut = funGenerateItemWiseKOTHeaderForDirectBiller(billNo, fileName, reprint, costCenterName);

		    KotOut.newLine();
		    KotOut.write("  " + rs_DirectKOT_Items.getString(3) + "      " + itemName);
		    //following code called for modifier
		    String sql_Modifier = " select b.strModifierName ,b.dblQuantity,a.strDefaultModifier,b.strDefaultModifierDeselectedYN "
			    + " from tblitemmodofier a,tblbillmodifierdtl b "
			    + " where a.strItemCode=left(b.strItemCode,7) and a.strModifierCode=b.strModifierCode "
			    + " and b.strBillNo=? and left(b.strItemCode,7)=? AND left(b.strSequenceNo,1)=? ";
		    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_Modifier);
		    pst.setString(1, billNo);
		    pst.setString(2, rs_DirectKOT_Items.getString(1));
		    pst.setString(3, rs_DirectKOT_Items.getString(5));
		    ResultSet rs_Modifier = pst.executeQuery();
		    while (rs_Modifier.next())
		    {
			if (rs_Modifier.getString(3).equalsIgnoreCase("Y") && rs_Modifier.getString(4).equalsIgnoreCase("Y"))
			{
			    KotOut.newLine();
			    KotOut.write("  " + rs_Modifier.getString(2) + "      " + "No " + rs_Modifier.getString(1).toUpperCase());
			}
			else if (!rs_Modifier.getString(3).equalsIgnoreCase("Y"))
			{
			    KotOut.newLine();
			    KotOut.write("  " + rs_Modifier.getString(2) + "      " + rs_Modifier.getString(1).toUpperCase());
			}
		    }
		    rs_Modifier.close();

		    //seperate items
		    KotOut.newLine();
		    KotOut.write(dashedLineFor40Chars);
		    KotOut.newLine();
		    KotOut.newLine();
		    KotOut.newLine();
		    KotOut.newLine();
		    KotOut.newLine();
		    KotOut.newLine();
		    if ("linux".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
		    {
			KotOut.write("V");//Linux
		    }
		    else if ("windows".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
		    {
			if ("Inbuild".equalsIgnoreCase(clsPosConfigFile.gPrinterType))
			{
			    KotOut.write("V");
			}
			else
			{
			    KotOut.write("m");//windows
			}
		    }
		    KotOut.close();
		    pst.close();
		    objPrintingUtility.funPrintToPrinterForItemWise(primaryPrinterName, secondaryPrinterName, "ItemWiseKOT", fileName);
		}
	    }
	    rs_DirectKOT_Items.close();
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    /*
     * generate Itemwise KOT text file
     */
    private BufferedWriter funGenerateItemWiseKOTHeaderForDirectBiller(String BillNo, String fileName, String Reprint, String CostCenterName)
    {
	BufferedWriter KotOut = null;

	try
	{
	    PreparedStatement pst = null;
	    objPrintingUtility.funCreateTempFolder();

	    String filePath = System.getProperty("user.dir");
	    File Text_KOT = new File(filePath + "/Temp/" + fileName + ".txt");
	    KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Text_KOT), "UTF8"));
	    boolean isReprint = false;
	    if ("Reprint".equalsIgnoreCase(Reprint))
	    {
		isReprint = true;
		objPrintingUtility.funPrintBlankSpace("[DUPLICATE]", KotOut);
		KotOut.write("[DUPLICATE]");
		KotOut.newLine();
	    }

	    String sql_PrintHomeDelivery = "select strOperationType from tblbillhd where strBillNo=? ";
	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_PrintHomeDelivery);
	    pst.setString(1, BillNo);
	    ResultSet rs_PrintHomeDelivery = pst.executeQuery();
	    String operationType = "";
	    if (rs_PrintHomeDelivery.next())
	    {
		operationType = rs_PrintHomeDelivery.getString(1);
	    }
	    rs_PrintHomeDelivery.close();
	    if (operationType.equalsIgnoreCase("HomeDelivery"))
	    {
		if (clsGlobalVarClass.gPrintHomeDeliveryYN)
		{
		    objPrintingUtility.funPrintBlankSpace(operationType, KotOut);
		    KotOut.write(operationType);
		    KotOut.newLine();
		}
	    }
	    else if (operationType.equalsIgnoreCase("TakeAway"))
	    {
		objPrintingUtility.funPrintBlankSpace(operationType, KotOut);
		KotOut.write(operationType);
		KotOut.newLine();
	    }

	    objPrintingUtility.funPrintBlankSpace("KOT", KotOut);
	    KotOut.write("KOT");
	    KotOut.newLine();
	    objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gPOSName, KotOut);
	    KotOut.write(clsGlobalVarClass.gPOSName);
	    KotOut.newLine();
	    objPrintingUtility.funPrintBlankSpace(CostCenterName, KotOut);
	    KotOut.write(CostCenterName);
	    KotOut.newLine();

	    //item will pickup from tblbilldtl           
	    objPrintingUtility.funPrintBlankSpace("DIRECT BILLER", KotOut);
	    KotOut.write("DIRECT BILLER");
	    KotOut.newLine();
	    KotOut.write(dashedLineFor40Chars);
	    KotOut.newLine();
	    KotOut.write("  BILL No: " + BillNo);
	    KotOut.newLine();
	    KotOut.write(dashedLineFor40Chars);

	    String sql_DirectKOT_Date = "select date(dteBillDate), time(dteBillDate) from tblbilldtl where strBillNo=? ";
	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_DirectKOT_Date);
	    pst.setString(1, BillNo);
	    ResultSet rs_DirectKOT_Date = pst.executeQuery();
	    if (rs_DirectKOT_Date.next())
	    {
		KotOut.newLine();
		KotOut.write("  DATE & TIME: " + rs_DirectKOT_Date.getString(1) + " " + rs_DirectKOT_Date.getString(2));
	    }
	    rs_DirectKOT_Date.close();
	    KotOut.newLine();
	    KotOut.write(dashedLineFor40Chars);
	    KotOut.newLine();
	    KotOut.write("  QTY        ITEM NAME  ");
	    KotOut.newLine();
	    KotOut.write(dashedLineFor40Chars);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
	finally
	{
	    return KotOut;
	}
    }

}
