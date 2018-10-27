package com.POSPrinting.Text.KOT;

import com.POSPrinting.Utility.clsPrintingUtility;
import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsPosConfigFile;
import com.POSGlobal.controller.clsUtility;
import com.POSGlobal.controller.clsUtility2;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates and open the template
 * in the editor.
 */
/**
 *
 * @author Ajim
 * @date Aug 11, 2017
 */
public class clsKOTTextFileGenerationForMakeKOT
{

    private DecimalFormat decimalFormat = new DecimalFormat("#.###");
    private SimpleDateFormat ddMMyyyyAMPMDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
    private clsUtility objUtility = new clsUtility();
    private clsUtility2 objUtility2 = new clsUtility2();
    private clsPrintingUtility objPrintingUtility = new clsPrintingUtility();
    private final String dashedLineFor40Chars = "  --------------------------------------";

    /**
     *
     * @param tableNo
     * @param costCenterCode
     * @param areaCode
     * @param KOTNO
     * @param Reprint
     * @param primaryPrinterName
     * @param secondaryPrinterName
     * @param costCenterName
     * @param printYN
     * @param NCKotYN
     * @param labelOnKOT
     */
    public void funGenerateTextKOTForMakeKOTForMenuHeadWise(String tableNo, String costCenterCode, String areaCode, String KOTNO, String Reprint, String primaryPrinterName, String secondaryPrinterName, String costCenterName, String printYN, String NCKotYN, String labelOnKOT)
    {
	try
	{
	    PreparedStatement pst = null;
	    objPrintingUtility.funCreateTempFolder();
	    String filePath = System.getProperty("user.dir");
	    File fileKOTPrint = new File(filePath + "/Temp/Temp_KOT.txt");
	    FileWriter fstream = new FileWriter(fileKOTPrint);
	    BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileKOTPrint), "UTF-8"));

	    boolean isReprint = false;
	    if ("Reprint".equalsIgnoreCase(Reprint))
	    {
		isReprint = true;
		objPrintingUtility.funPrintBlankSpace("[DUPLICATE]", KotOut);
		KotOut.write("[DUPLICATE]");
		KotOut.newLine();
	    }

	    if ("Y".equalsIgnoreCase(NCKotYN))
	    {
		objPrintingUtility.funPrintBlankSpace("NCKOT", KotOut);
		KotOut.write("NCKOT");
		KotOut.newLine();
	    }
	    else
	    {
		objPrintingUtility.funPrintBlankSpace(labelOnKOT, KotOut);//write KOT
		KotOut.write(labelOnKOT);//write KOT
		KotOut.newLine();
	    }
	    objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gPOSName, KotOut);
	    KotOut.write(clsGlobalVarClass.gPOSName);
	    KotOut.newLine();
	    objPrintingUtility.funPrintBlankSpace(costCenterName, KotOut);
	    KotOut.write(costCenterName);
	    KotOut.newLine();

	    String KOTType = "DINE";
	    if (null != clsGlobalVarClass.hmTakeAway.get(tableNo))
	    {
		KOTType = "Take Away";
	    }
	    objPrintingUtility.funPrintBlankSpace(KOTType, KotOut);
	    KotOut.write(KOTType);
	    KotOut.newLine();

	    if (clsGlobalVarClass.gCounterWise.equals("Yes"))
	    {
		objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gCounterName, KotOut);
		KotOut.write(clsGlobalVarClass.gCounterName);
		KotOut.newLine();
	    }

	    KotOut.write(dashedLineFor40Chars);
	    KotOut.newLine();
	    KotOut.write("  KOT NO     :");
	    KotOut.write(KOTNO + "  ");
	    KotOut.newLine();

	    String sqlKOTDtl = "select a.strWaiterNo,b.strTableName,b.intPaxNo,ifnull(c.strWShortName,''),a.dteDateCreated,TIME_FORMAT(TIME(a.dteDateCreated),'%h:%i')  "
		    + " from tblitemrtemp a "
		    + " left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
		    + " and b.strOperational='Y' "
		    + " left outer join tblwaitermaster c on a.strWaiterNo=c.strWaiterNo "
		    + " where a.strKOTNo=? and a.strTableNo=? group by a.strKOTNo ;";
	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlKOTDtl);
	    pst.setString(1, KOTNO);
	    pst.setString(2, tableNo);
	    ResultSet rsKOTDetails = pst.executeQuery();

	    String tableNoForConsolidatedKOT = tableNo;
	    String waiterNameForConsolidatedKOT = "";

	    if (rsKOTDetails.next())
	    {
		if (clsGlobalVarClass.gClientCode.equalsIgnoreCase("171.001") || clsGlobalVarClass.gClientCode.equalsIgnoreCase("136.001"))//"136.001",KINKI  "171.001","CHINA GRILL-PIMPRI"
		{
		    KotOut.write("  TABLE No   :");
		}
		else
		{
		    KotOut.write("  TABLE NAME :");
		}
		KotOut.write(rsKOTDetails.getString(2) + "  ");
		KotOut.write(" PAX   :");
		KotOut.write(rsKOTDetails.getString(3));
		KotOut.newLine();

		if (!rsKOTDetails.getString(4).trim().isEmpty())
		{
		    waiterNameForConsolidatedKOT = rsKOTDetails.getString(4);
		    KotOut.write("  WAITER NAME:" + "   " + rsKOTDetails.getString(4));
		    KotOut.newLine();
		}
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
		KotOut.write("  DATE & TIME:" + dateTimeFormat.format(rsKOTDetails.getObject(5)));
	    }
	    rsKOTDetails.close();

	    KotOut.newLine();
	    if ("Y".equalsIgnoreCase(NCKotYN))
	    {
		String sql = "select a.strRemark from tblnonchargablekot a where a.strKOTNo='" + KOTNO + "' "
			+ "group by a.strKOTNo ";
		ResultSet rsRemark = clsGlobalVarClass.dbMysql.executeResultSet(sql);
		if (rsRemark.next() && rsRemark.getString(1).trim().length() > 0)
		{
		    KotOut.write("  Remark     :" + rsRemark.getString(1));
		}
	    }

	    InetAddress ipAddress = InetAddress.getLocalHost();
	    String hostName = ipAddress.getHostName();
	    KotOut.newLine();

	    if (clsGlobalVarClass.gPrintDeviceAndUserDtlOnKOTYN)
	    {
		KotOut.write("  KOT From Computer:" + hostName);
		KotOut.newLine();
		KotOut.write("  KOT By User      :" + clsGlobalVarClass.gUserCode);
		KotOut.newLine();
	    }

	    KotOut.write(dashedLineFor40Chars);
	    KotOut.newLine();
	    KotOut.write("  QTY         ITEM NAME  ");
	    KotOut.newLine();
	    KotOut.write(dashedLineFor40Chars);

	    // Code to Print KOT Item details    
	    String sqlKOTItems = "";

	    if (clsGlobalVarClass.gAreaWisePricing.equals("Y"))
	    {
		sqlKOTItems = "SELECT LEFT(a.strItemCode,7),b.strItemName,a.dblItemQuantity,a.strKOTNo,a.strSerialNo,d.strShortName,e.strMenuName "
			+ ",ifnull(f.strSubMenuHeadName,'')strSubMenuHeadName "
			+ "FROM tblitemrtemp a "
			+ "left outer join tblmenuitempricingdtl b on a.strItemCode=b.strItemCode "
			+ "left outer join tblprintersetup c on b.strCostCenterCode=c.strCostCenterCode  "
			+ "left outer join tblitemmaster d on a.strItemCode=d.strItemCode "
			+ "left outer join tblmenuhd e on b.strMenuCode=e.strMenuCode  "
			+ "left outer join tblsubmenuhead f on b.strSubMenuHeadCode=f.strSubMenuHeadCode  "
			+ "WHERE a.strTableNo=?  "
			+ "AND a.strKOTNo=?  "
			+ "AND b.strCostCenterCode=?  "
			+ "and (b.strPOSCode=? OR b.strPOSCode='All')   "
			+ "AND b.strHourlyPricing='No' "
			+ "and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo=? )) "
			+ "and LEFT(a.strItemCode,7)=b.strItemCode and b.strHourlyPricing='No' "
			+ "ORDER BY f.strSubMenuHeadCode ";
	    }
	    else
	    {
		sqlKOTItems = "SELECT LEFT(a.strItemCode,7),b.strItemName,a.dblItemQuantity,a.strKOTNo,a.strSerialNo,d.strShortName,e.strMenuName "
			+ ",ifnull(f.strSubMenuHeadName,'')strSubMenuHeadName "
			+ "FROM tblitemrtemp a "
			+ "left outer join tblmenuitempricingdtl b on a.strItemCode=b.strItemCode "
			+ "left outer join tblprintersetup c on b.strCostCenterCode=c.strCostCenterCode  "
			+ "left outer join tblitemmaster d on a.strItemCode=d.strItemCode "
			+ "left outer join tblmenuhd e on b.strMenuCode=e.strMenuCode  "
			+ "left outer join tblsubmenuhead f on b.strSubMenuHeadCode=f.strSubMenuHeadCode  "
			+ "WHERE a.strTableNo=?  "
			+ "AND a.strKOTNo=?  "
			+ "AND b.strCostCenterCode=?  "
			+ "and (b.strPOSCode=? OR b.strPOSCode='All')   "
			+ "AND b.strHourlyPricing='No' "
			+ "and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo=? ) OR b.strAreaCode ='" + areaCode + "') "
			+ "and LEFT(a.strItemCode,7)=b.strItemCode and b.strHourlyPricing='No' "
			+ "ORDER BY f.strSubMenuHeadCode ";
	    }
	    //System.out.println(sqlKOTItems);
	    PreparedStatement pstKOTItems = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlKOTItems);
	    pstKOTItems.setString(1, tableNo);
	    pstKOTItems.setString(2, KOTNO);
	    pstKOTItems.setString(3, costCenterCode);
	    pstKOTItems.setString(4, clsGlobalVarClass.gPOSCode);
	    pstKOTItems.setString(5, tableNo);
	    //pst_KOT_Items.setString(5, AreaCode);

	    int noOfCharsToBePrinted = 30;
	    int noOfCharsToBePrintedX2 = noOfCharsToBePrinted * 2;
	    int qtyWidth = 6;
	    int qtyLeftSpace = 3;
	    String printSubMenuHeadName = "";

	    ResultSet rsKOTItems = pstKOTItems.executeQuery();
	    while (rsKOTItems.next())
	    {
		String rsSubMenuHead = rsKOTItems.getString(8).toUpperCase();
		if (rsSubMenuHead.isEmpty())
		{
		    rsSubMenuHead = "Not Define";
		}

		if (printSubMenuHeadName.isEmpty())
		{
		    printSubMenuHeadName = rsSubMenuHead.toUpperCase();

		    KotOut.newLine();
		    KotOut.write("   " + printSubMenuHeadName.trim());

		}
		else if (printSubMenuHeadName.toUpperCase().equalsIgnoreCase(rsSubMenuHead.toUpperCase()))
		{
		    //do nothing
		}
		else
		{
		    KotOut.newLine();
		    printSubMenuHeadName = rsSubMenuHead.toUpperCase();
		    KotOut.newLine();
		    KotOut.write("   " + printSubMenuHeadName.trim());
		}

		String kotItemName = rsKOTItems.getString(2);//full name
		if (clsGlobalVarClass.gPrintShortNameOnKOT && !rsKOTItems.getString(6).trim().isEmpty())
		{
		    kotItemName = rsKOTItems.getString(6);//short name
		}
		KotOut.newLine();

		String itemqty = decimalFormat.format(rsKOTItems.getDouble(3));

		KotOut.write("   " + String.format("%-" + qtyWidth + "s", itemqty) + "");

		if (kotItemName.length() <= noOfCharsToBePrinted)
		{
		    KotOut.write(kotItemName.trim());
		}
		else
		{
		    if (kotItemName.length() >= noOfCharsToBePrintedX2)
		    {
			KotOut.write(kotItemName.substring(0, noOfCharsToBePrinted).trim());
			KotOut.newLine();
			KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrinted, noOfCharsToBePrintedX2).trim());

			KotOut.newLine();
			KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrintedX2, kotItemName.length()).trim());
		    }
		    else
		    {
			KotOut.write(kotItemName.substring(0, noOfCharsToBePrinted).trim());
			KotOut.newLine();
			KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrinted, kotItemName.length()).trim());
		    }
		}

		boolean printDefaultModifier = true;

		//print no default modifiers
		String sqlModifier = "select a.strItemName,sum(a.dblItemQuantity) from tblitemrtemp a "
			+ " where a.strItemCode like'" + rsKOTItems.getString(1) + "M%' and a.strKOTNo='" + KOTNO + "' "
			+ " and strSerialNo like'" + rsKOTItems.getString(5) + ".%' "
			+ " group by a.strItemCode,a.strItemName ";
		//System.out.println(sqlModifier);
		ResultSet rsModifierItems = clsGlobalVarClass.dbMysql.executeResultSet(sqlModifier);
		while (rsModifierItems.next())
		{
		    printDefaultModifier = false;

		    String modQty = decimalFormat.format(rsModifierItems.getDouble(2));//rsModifierItems.getString(2);
		    int modQtyLength = modQty.length();

		    String modifierName = rsModifierItems.getString(1);
		    if (modifierName.startsWith("-->"))
		    {
			if (modifierName.length() <= noOfCharsToBePrinted)
			{
			    if (clsGlobalVarClass.gPrintModQtyOnKOT)
			    {
				KotOut.newLine();
				KotOut.write("   " + String.format("%-" + qtyWidth + "s", modQty) + "" + modifierName.trim());
			    }
			    else
			    {
				KotOut.newLine();
				KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.trim());
			    }
			}
			else
			{
			    if (clsGlobalVarClass.gPrintModQtyOnKOT)
			    {
				if (modifierName.length() >= noOfCharsToBePrintedX2)
				{
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", modQty) + "" + modifierName.substring(0, noOfCharsToBePrinted).trim());
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.substring(noOfCharsToBePrinted, noOfCharsToBePrintedX2).trim());
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.substring(noOfCharsToBePrintedX2, modifierName.length()).trim());
				}
				else
				{
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", modQty) + "" + modifierName.substring(0, noOfCharsToBePrinted).trim());
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.substring(noOfCharsToBePrinted, modifierName.length()).trim());
				}
			    }
			    else
			    {
				if (modifierName.length() >= noOfCharsToBePrintedX2)
				{
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.substring(0, noOfCharsToBePrinted).trim());
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.substring(noOfCharsToBePrinted, noOfCharsToBePrintedX2).trim());
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.substring(noOfCharsToBePrintedX2, modifierName.length()).trim());
				}
				else
				{
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.substring(0, noOfCharsToBePrinted).trim());
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.substring(noOfCharsToBePrinted, modifierName.length()).trim());
				}
			    }
			}

		    }
		}
		rsModifierItems.close();

		//print  default modifiers
		if (printDefaultModifier && !(printSubMenuHeadName.equalsIgnoreCase("Buffet") || printSubMenuHeadName.equalsIgnoreCase("Not Define")))
		{
		    KotOut.newLine();
		    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + "-->No Message");

		}

		//print  5 blank lines after buffet item
		if (printSubMenuHeadName.equalsIgnoreCase("Buffet") || printSubMenuHeadName.equalsIgnoreCase("Not Define"))
		{
		    KotOut.newLine();
		    KotOut.write("         " + String.format("%-15s", "Indian Starter") + "" + String.format("%-15s", "Chinese Starter"));

		    KotOut.newLine();
		    KotOut.write("         " + String.format("%-15s", "1") + String.format("%-15s", "1"));
		    KotOut.newLine();
		    KotOut.write("         " + String.format("%-15s", "2") + String.format("%-15s", "2"));
		    KotOut.newLine();
		    KotOut.write("         " + String.format("%-15s", "3") + String.format("%-15s", "3"));
		    KotOut.newLine();
		    KotOut.write("         " + String.format("%-15s", "4") + String.format("%-15s", "4"));
		    KotOut.newLine();
		    KotOut.write("         " + String.format("%-15s", "5") + String.format("%-15s", "5"));
		}

		//print  5 blank lines after buffet item
//                if (printSubMenuHeadName.equalsIgnoreCase("Buffet") || printSubMenuHeadName.equalsIgnoreCase("Not Define"))
//                {
//                    KotOut.newLine();
//                    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + "1");
//                    KotOut.newLine();
//                    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + "2");
//                    KotOut.newLine();
//                    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + "3");
//                    KotOut.newLine();
//                    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + "4");
//                    KotOut.newLine();
//                    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + "5");
//                }
	    }
	    rsKOTItems.close();
	    pstKOTItems.close();

	    KotOut.newLine();
	    KotOut.write(dashedLineFor40Chars);
	    for (int cntLines = 0; cntLines < Integer.parseInt(clsGlobalVarClass.gNoOfLinesInKOTPrint); cntLines++)
	    {
		KotOut.newLine();
	    }
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
	    fstream.close();
	    pst.close();
	    //System.out.println("PP="+primaryPrinterName);
	    //System.out.println("SP="+secondaryPrinterName);
	    //System.out.println("PRINT="+printYN);

	    if ("Reprint".equalsIgnoreCase(Reprint))
	    {
		objPrintingUtility.funShowTextFile(fileKOTPrint, "", clsGlobalVarClass.gBillPrintPrinterPort);
	    }

	    if (clsGlobalVarClass.gShowBill)
	    {
		objPrintingUtility.funShowTextFile(fileKOTPrint, "", clsGlobalVarClass.gBillPrintPrinterPort);
	    }

	    if (printYN.equals("Y"))
	    {
		String sql = "select strPrintOnBothPrinters,intPrimaryPrinterNoOfCopies,intSecondaryPrinterNoOfCopies from tblcostcentermaster where strCostCenterCode='" + costCenterCode + "' ";
		ResultSet rsCostCenter = clsGlobalVarClass.dbMysql.executeResultSet(sql);
		if (rsCostCenter.next())
		{
		    objPrintingUtility.funPrintToPrinter(primaryPrinterName, secondaryPrinterName, "kot", rsCostCenter.getString(1), isReprint,costCenterCode);
		    
		}
		rsCostCenter.close();
	    }
	}
	catch (Exception e)
	{
	    JOptionPane.showMessageDialog(null, "KOT Printing Error:" + e.getMessage());
	    e.printStackTrace();
	}
    }

    /**
     *
     * @param tableNo
     * @param costCenterCode
     * @param areaCode
     * @param KOTNO
     * @param Reprint
     * @param primaryPrinterName
     * @param secondaryPrinterName
     * @param costCenterName
     * @param printYN
     * @param NCKotYN
     * @param labelOnKOT
     */
    public void funGenerateTextFileForTableWiseKOT(String tableNo, String costCenterCode, String areaCode, String KOTNO, String Reprint, String primaryPrinterName, String secondaryPrinterName, String costCenterName, String printYN, String NCKotYN, String labelOnKOT)
    {
	try
	{
	    PreparedStatement pst = null;
	    objPrintingUtility.funCreateTempFolder();
	    String filePath = System.getProperty("user.dir");
	    File fileKOTPrint = new File(filePath + "/Temp/Temp_KOT.txt");
	    FileWriter fstream = new FileWriter(fileKOTPrint);
	    BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileKOTPrint), "UTF-8"));

	    DecimalFormat decimalFormat = new DecimalFormat("#.###");

	    boolean isReprint = false;
	    if ("Reprint".equalsIgnoreCase(Reprint))
	    {
		isReprint = true;
		objPrintingUtility.funPrintBlankSpace("[DUPLICATE]", KotOut);
		KotOut.write("[DUPLICATE]");
		KotOut.newLine();
	    }

	    if (clsGlobalVarClass.gPrintDeviceAndUserDtlOnKOTYN)
	    {
		if ("Y".equalsIgnoreCase(NCKotYN))
		{
		    objPrintingUtility.funPrintBlankSpace("NCKOT", KotOut);
		    KotOut.write("NCKOT");
		    KotOut.newLine();
		}
		else
		{
		    objPrintingUtility.funPrintBlankSpace(labelOnKOT, KotOut);//write KOT
		    KotOut.write(labelOnKOT);//write KOT
		    KotOut.newLine();
		}
		objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gPOSName, KotOut);
		KotOut.write(clsGlobalVarClass.gPOSName);
	    }

	    KotOut.newLine();
	    objPrintingUtility.funPrintBlankSpace(costCenterName, KotOut);
	    KotOut.write(costCenterName);

	    if (clsGlobalVarClass.gPrintDeviceAndUserDtlOnKOTYN)
	    {

		String KOTType = "DINE";
		if (null != clsGlobalVarClass.hmTakeAway.get(tableNo))
		{
		    KOTType = "Take Away";
		}
		KotOut.newLine();
		objPrintingUtility.funPrintBlankSpace(KOTType, KotOut);
		KotOut.write(KOTType);
		KotOut.newLine();

		if (clsGlobalVarClass.gCounterWise.equals("Yes"))
		{
		    objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gCounterName, KotOut);
		    KotOut.write(clsGlobalVarClass.gCounterName);
		    KotOut.newLine();
		}

		KotOut.write(dashedLineFor40Chars);
	    }

	    String areaCodeOfTable = "";
	    String sqlArea = "select strTableName,intPaxNo,strAreaCode "
		    + " from tbltablemaster "
		    + " where strTableNo='" + tableNo + "' "
		    + " and strOperational='Y' ";
	    ResultSet rsArea = clsGlobalVarClass.dbMysql.executeResultSet(sqlArea);
	    if (rsArea.next())
	    {
		areaCodeOfTable = rsArea.getString(3);
	    }
	    rsArea.close();

	    String sqlKOTDtl = "select a.strWaiterNo,b.strTableName,b.intPaxNo,ifnull(c.strWShortName,'')"
		    + ",a.dteDateCreated,TIME_FORMAT(TIME(a.dteDateCreated),'%h:%i')  "
		    + " from tblitemrtemp a "
		    + " left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
		    + " and b.strOperational='Y' "
		    + " left outer join tblwaitermaster c on a.strWaiterNo=c.strWaiterNo "
		    + " where a.strKOTNo=? and a.strTableNo=? group by a.strKOTNo ;";
	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlKOTDtl);
	    pst.setString(1, KOTNO);
	    pst.setString(2, tableNo);
	    ResultSet rsKOTDetails = pst.executeQuery();

	    String tableNoForConsolidatedKOT = tableNo;
	    String waiterNameForConsolidatedKOT = "";

	    if (rsKOTDetails.next())
	    {
		KotOut.newLine();
		KotOut.write("  TABLE No         :");
		KotOut.write(rsKOTDetails.getString(2) + "  ");

		if (clsGlobalVarClass.gPrintDeviceAndUserDtlOnKOTYN)
		{
		    KotOut.write(" PAX : ");
		    KotOut.write(rsKOTDetails.getString(3));

		    KotOut.newLine();
		    KotOut.write("  KOT NO           :");
		    KotOut.write(KOTNO + "  ");
		}

		if (!rsKOTDetails.getString(4).trim().isEmpty())
		{
		    if (clsGlobalVarClass.gPrintDeviceAndUserDtlOnKOTYN)
		    {
			waiterNameForConsolidatedKOT = rsKOTDetails.getString(4);
			KotOut.newLine();
			KotOut.write("  WAITER NAME      :" + "" + rsKOTDetails.getString(4));

		    }

		}
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
		KotOut.newLine();
		KotOut.write("  DATE & TIME      :" + dateTimeFormat.format(rsKOTDetails.getObject(5)));
	    }
	    rsKOTDetails.close();

	    InetAddress ipAddress = InetAddress.getLocalHost();
	    String hostName = ipAddress.getHostName();

	    if (clsGlobalVarClass.gPrintDeviceAndUserDtlOnKOTYN)
	    {
		KotOut.newLine();
		KotOut.write("  KOT From Computer:" + hostName);
		KotOut.newLine();
		KotOut.write("  KOT By User      :" + clsGlobalVarClass.gUserCode);
	    }

	    if ("Y".equalsIgnoreCase(NCKotYN))
	    {
		String sql = "select a.strRemark from tblnonchargablekot a where a.strKOTNo='" + KOTNO + "' "
			+ "group by a.strKOTNo ";
		ResultSet rsRemark = clsGlobalVarClass.dbMysql.executeResultSet(sql);
		if (rsRemark.next() && rsRemark.getString(1).trim().length() > 0)
		{
		    KotOut.newLine();
		    KotOut.write("  Remark : " + rsRemark.getString(1));
		}
	    }

	    StringBuilder sqlBuilder = new StringBuilder();
	    StringBuilder billNoteBuilder = new StringBuilder();

	    sqlBuilder.setLength(0);
	    sqlBuilder.append("select a.strBillNote "
		    + "from tblitemrtemp a "
		    + "where a.strTableNo='" + tableNo + "' "
		    + "and a.strPOSCode='" + clsGlobalVarClass.gPOSCode + "' "
		    + "and length(a.strBillNote)>0 "
		    + "and a.strKOTNo='" + KOTNO + "' "
		    + "group by a.strBillNote ");
	    billNoteBuilder.setLength(0);
	    ResultSet rsBillNoteBuilder = clsGlobalVarClass.dbMysql.executeResultSet(sqlBuilder.toString());
	    for (int i = 0; rsBillNoteBuilder.next(); i++)
	    {
		if (i == 0)
		{
		    billNoteBuilder.append(rsBillNoteBuilder.getString(1));
		}
		else
		{
		    billNoteBuilder.append("," + rsBillNoteBuilder.getString(1));
		}
	    }
	    rsBillNoteBuilder.close();
	    if (billNoteBuilder.toString().trim().length() > 0)
	    {
		KotOut.newLine();
		KotOut.write("  Zomato Code      :" + billNoteBuilder.toString().trim());
	    }

	    if (clsGlobalVarClass.gPrintDeviceAndUserDtlOnKOTYN)
	    {
		KotOut.newLine();
		KotOut.write(dashedLineFor40Chars);
		KotOut.newLine();
		KotOut.write("  QTY         ITEM NAME  ");
	    }
	    KotOut.newLine();
	    KotOut.write(dashedLineFor40Chars);

	    // Code to Print KOT Item details    
	    String sqlKOTItems = "", filter = "";

	    String printItemQty = "a.dblItemQuantity";
	    if (clsGlobalVarClass.gFireCommunication)
	    {
		printItemQty = "a.dblPrintQty";
		filter = " and a.dblPrintQty>0 ";
	    }

	    if (clsGlobalVarClass.gAreaWisePricing.equals("Y"))
	    {
		sqlKOTItems = "select LEFT(a.strItemCode,7),b.strItemName," + printItemQty + ",a.strKOTNo,a.strSerialNo,d.strShortName "
			+ " from tblitemrtemp a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
			+ " where a.strTableNo=? and a.strKOTNo=? and b.strCostCenterCode=c.strCostCenterCode "
			+ " and b.strCostCenterCode=? and a.strItemCode=d.strItemCode "
			+ " and (b.strPOSCode=? or b.strPOSCode='All') "
			+ " and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo=? )) "
			+ " and LEFT(a.strItemCode,7)=b.strItemCode and b.strHourlyPricing='No' "
			+ " " + filter
			+ " order by a.strSerialNo ";
	    }
	    else
	    {
		sqlKOTItems = "select LEFT(a.strItemCode,7),d.strItemName," + printItemQty + ",a.strKOTNo,a.strSerialNo,d.strShortName "
			+ " from tblitemrtemp a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
			+ " where a.strTableNo=? and a.strKOTNo=? and b.strCostCenterCode=c.strCostCenterCode "
			+ " and b.strCostCenterCode=? and a.strItemCode=d.strItemCode "
			+ " and (b.strPOSCode=? or b.strPOSCode='All') "
			+ " and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo=? ) "
			+ " OR b.strAreaCode ='" + areaCode + "') "
			+ " and LEFT(a.strItemCode,7)=b.strItemCode and b.strHourlyPricing='No' "
			+ " " + filter
			+ " order by a.strSerialNo ";
	    }

	    if (clsGlobalVarClass.gPlayZonePOS.equals("Y"))
	    {
		if (clsGlobalVarClass.gAreaWisePricing.equals("Y"))
		{
		    sqlKOTItems = "select LEFT(a.strItemCode,7),d.strItemName," + printItemQty + ",a.strKOTNo,a.strSerialNo,d.strShortName "
			    + " from tblitemrtemp a,tblplayzonepricinghd b,tblprintersetup c,tblitemmaster d "
			    + " where a.strTableNo=? and a.strKOTNo=? and b.strCostCenterCode=c.strCostCenterCode "
			    + " and b.strCostCenterCode=? and a.strItemCode=d.strItemCode "
			    + " and (b.strPOSCode=? or b.strPOSCode='All') "
			    + " and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo=? )) "
			    + " and LEFT(a.strItemCode,7)=b.strItemCode "
			    + " " + filter
			    + " order by a.strSerialNo ";
		}
		else
		{
		    sqlKOTItems = "select LEFT(a.strItemCode,7),d.strItemName," + printItemQty + ",a.strKOTNo,a.strSerialNo,d.strShortName "
			    + " from tblitemrtemp a,tblplayzonepricinghd b,tblprintersetup c,tblitemmaster d "
			    + " where a.strTableNo=? and a.strKOTNo=? and b.strCostCenterCode=c.strCostCenterCode "
			    + " and b.strCostCenterCode=? and a.strItemCode=d.strItemCode "
			    + " and (b.strPOSCode=? or b.strPOSCode='All') "
			    + " and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo=? ) "
			    + " OR b.strAreaCode ='" + areaCode + "') "
			    + " and LEFT(a.strItemCode,7)=b.strItemCode "
			    + " " + filter
			    + " order by a.strSerialNo ";
		}
	    }

	    //System.out.println(sqlKOTItems);
	    PreparedStatement pstKOTItems = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlKOTItems);
	    pstKOTItems.setString(1, tableNo);
	    pstKOTItems.setString(2, KOTNO);
	    pstKOTItems.setString(3, costCenterCode);
	    pstKOTItems.setString(4, clsGlobalVarClass.gPOSCode);
	    pstKOTItems.setString(5, tableNo);
	    //pst_KOT_Items.setString(5, AreaCode);

	    int noOfCharsToBePrinted = 30;
	    int noOfCharsToBePrintedX2 = noOfCharsToBePrinted * 2;
	    int qtyWidth = 6;
	    int qtyLeftSpace = 3;

	    ResultSet rsKOTItems = pstKOTItems.executeQuery();
	    while (rsKOTItems.next())
	    {

		String kotItemName = rsKOTItems.getString(2);//full name
		if (clsGlobalVarClass.gPrintShortNameOnKOT && !rsKOTItems.getString(6).trim().isEmpty())
		{
		    kotItemName = rsKOTItems.getString(6);//short name
		}

		KotOut.newLine();

		String itemqty = decimalFormat.format(rsKOTItems.getDouble(3));

		KotOut.write("   " + String.format("%-" + qtyWidth + "s", itemqty) + "");

		if (kotItemName.length() <= noOfCharsToBePrinted)
		{
		    KotOut.write(kotItemName.trim());
		}
		else
		{
		    if (kotItemName.length() >= noOfCharsToBePrintedX2)
		    {
			KotOut.write(kotItemName.substring(0, noOfCharsToBePrinted).trim());
			KotOut.newLine();
			KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrinted, noOfCharsToBePrintedX2).trim());

			KotOut.newLine();
			KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrintedX2, kotItemName.length()).trim());
		    }
		    else
		    {
			KotOut.write(kotItemName.substring(0, noOfCharsToBePrinted).trim());
			KotOut.newLine();
			KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrinted, kotItemName.length()).trim());
		    }
		}

		String printModifierQty = "a.dblItemQuantity";
		if (clsGlobalVarClass.gFireCommunication)
		{
		    printModifierQty = "a.dblPrintQty";
		}

		String sqlModifier = "select a.strItemName," + printModifierQty + " "
			+ " from tblitemrtemp a "
			+ " where a.strItemCode like'" + rsKOTItems.getString(1) + "M%' and a.strKOTNo='" + KOTNO + "' "
			+ " and strSerialNo like'" + rsKOTItems.getString(5) + ".%' "
			+ " " + filter
			+ " group by a.strItemCode,a.strItemName ";
		//System.out.println(sqlModifier);
		ResultSet rsModifierItems = clsGlobalVarClass.dbMysql.executeResultSet(sqlModifier);
		while (rsModifierItems.next())
		{

		    String modQty = decimalFormat.format(rsModifierItems.getDouble(2));//rsModifierItems.getString(2);
		    int modQtyLength = modQty.length();

		    String modifierName = rsModifierItems.getString(1);
		    if (modifierName.startsWith("-->"))
		    {
			if (modifierName.length() <= noOfCharsToBePrinted)
			{
			    if (clsGlobalVarClass.gPrintModQtyOnKOT)
			    {
				KotOut.newLine();
				KotOut.write("   " + String.format("%-" + qtyWidth + "s", modQty) + "" + modifierName.trim());
			    }
			    else
			    {
				KotOut.newLine();
				KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.trim());
			    }
			}
			else
			{
			    if (clsGlobalVarClass.gPrintModQtyOnKOT)
			    {
				if (modifierName.length() >= noOfCharsToBePrintedX2)
				{
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", modQty) + "" + modifierName.substring(0, noOfCharsToBePrinted).trim());
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.substring(noOfCharsToBePrinted, noOfCharsToBePrintedX2).trim());
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.substring(noOfCharsToBePrintedX2, modifierName.length()).trim());
				}
				else
				{
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", modQty) + "" + modifierName.substring(0, noOfCharsToBePrinted).trim());
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.substring(noOfCharsToBePrinted, modifierName.length()).trim());
				}
			    }
			    else
			    {
				if (modifierName.length() >= noOfCharsToBePrintedX2)
				{
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.substring(0, noOfCharsToBePrinted).trim());
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.substring(noOfCharsToBePrinted, noOfCharsToBePrintedX2).trim());
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.substring(noOfCharsToBePrintedX2, modifierName.length()).trim());
				}
				else
				{
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.substring(0, noOfCharsToBePrinted).trim());
				    KotOut.newLine();
				    KotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + modifierName.substring(noOfCharsToBePrinted, modifierName.length()).trim());
				}
			    }
			}

		    }
		}
	    }
	    rsKOTItems.close();
	    pstKOTItems.close();

	    if (clsGlobalVarClass.gPrintDeviceAndUserDtlOnKOTYN)
	    {
		KotOut.newLine();
		KotOut.write(dashedLineFor40Chars);
	    }

	    for (int cntLines = 0; cntLines < Integer.parseInt(clsGlobalVarClass.gNoOfLinesInKOTPrint); cntLines++)
	    {
		KotOut.newLine();
	    }
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
	    fstream.close();
	    pst.close();
	    //System.out.println("PP="+primaryPrinterName);
	    //System.out.println("SP="+secondaryPrinterName);
	    //System.out.println("PRINT="+printYN);

	    if ("Reprint".equalsIgnoreCase(Reprint))
	    {
		objPrintingUtility.funShowTextFile(fileKOTPrint, "", "");
	    }

	    if (clsGlobalVarClass.gShowBill)
	    {
		objPrintingUtility.funShowTextFile(fileKOTPrint, "", "Printer Info!2");
	    }

	    if (printYN.equals("Y"))
	    {
		if (clsGlobalVarClass.gAreaWiseCostCenterKOTPrinting)
		{
		    String sqlAreaWiseCostCenterKOTPrinting = "select a.strPrimaryPrinterPort,a.strSecondaryPrinterPort,a.strPrintOnBothPrintersYN "
			    + "from tblprintersetupmaster a "
			    + "where (a.strPOSCode='" + clsGlobalVarClass.gPOSCode + "' or a.strPOSCode='All') "
			    + "and a.strAreaCode='" + areaCodeOfTable + "' "
			    + "and a.strCostCenterCode='" + costCenterCode + "' "
			    + "and a.strPrinterType='Cost Center' ";
		    ResultSet rsPrinter = clsGlobalVarClass.dbMysql.executeResultSet(sqlAreaWiseCostCenterKOTPrinting);
		    if (rsPrinter.next())
		    {
			String primary = rsPrinter.getString(1);
			String secondary = rsPrinter.getString(2);
			String printOnBothPrinters = rsPrinter.getString(3);

			objPrintingUtility.funPrintToPrinter(primary, secondary, "kot", printOnBothPrinters, isReprint,costCenterCode);
		    }
		    rsPrinter.close();
		}
		else
		{
		    String sql = "select strPrintOnBothPrinters from tblcostcentermaster where strCostCenterCode='" + costCenterCode + "' ";
		    ResultSet rsCostCenter = clsGlobalVarClass.dbMysql.executeResultSet(sql);
		    if (rsCostCenter.next())
		    {
			
			objPrintingUtility.funPrintToPrinter(primaryPrinterName, secondaryPrinterName, "kot", rsCostCenter.getString(1), isReprint,costCenterCode);
			
			
		    }
		    rsCostCenter.close();
		}
	    }
	}
	catch (Exception e)
	{
	    JOptionPane.showMessageDialog(null, "KOT Printing Error:" + e.getMessage());
	    e.printStackTrace();
	}
    }
}
