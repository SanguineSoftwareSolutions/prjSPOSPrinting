package com.POSPrinting;

import com.POSPrinting.Jasper.KOT.clsKOTJasperFileGenerationForMakeKOT;
import com.POSPrinting.Jasper.KOT.clsKOTJasperFileGenerationForDirectBiller;
import com.POSPrinting.Text.KOT.clsConsolidatedKOTTextFileGenerationForDirectBiller;
import com.POSPrinting.Text.KOT.clsConsolidatedKOTTextFileGenerationForMakeKOT;
import com.POSPrinting.Text.KOT.clsKOTTextFileGenerationForMakeKOT;
import com.POSPrinting.Text.KOT.clsKOTTextFileGenerationForDirectBiller;
import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsUtility;
import com.POSPrinting.Jasper.KOT.clsKOTJasperFormat2FileGenerationForMakeKOT;
import com.POSPrinting.Text.KOT.clsCheckKOT;
import com.POSPrinting.Text.KOT.clsKitchenNote;
import com.POSPrinting.Utility.clsPrintingUtility;
import java.io.File;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
public class clsKOTGeneration
{

    private clsUtility objUtility;
    private clsKOTTextFileGenerationForMakeKOT objKOTTextFileGenerationForMakeKOT;
    private clsKOTJasperFileGenerationForMakeKOT objKOTJasperFileGenerationForMakeKOT;
    private clsKOTJasperFormat2FileGenerationForMakeKOT objJasperFormat2FileGenerationForMakeKOT;
    private clsConsolidatedKOTTextFileGenerationForMakeKOT objConsolidatedKOTTextFileGenerationForMakeKOT;

    private clsKOTTextFileGenerationForDirectBiller objKOTTextFileGenerationForDirectBiller;
    private clsKOTJasperFileGenerationForDirectBiller objKOTJasperFileGenerationForDirectBiller;
    private clsConsolidatedKOTTextFileGenerationForDirectBiller objConsolidatedKOTTextFileGenerationForDirectBiller;

    /**
     *
     */
    public clsKOTGeneration()
    {

    }

    /**
     *
     * @param tableNo
     * @param KOTNo
     * @param billNo
     * @param reprint
     * @param type
     * @param printYN
     */
    public void funKOTGeneration(String tableNo, String KOTNo, String billNo, String reprint, String type, String printYN)
    {
	try
	{

	    objUtility = new clsUtility();
	    /**
	     * Text or jasper objects
	     */
	    objKOTTextFileGenerationForMakeKOT = new clsKOTTextFileGenerationForMakeKOT();
	    objKOTJasperFileGenerationForMakeKOT = new clsKOTJasperFileGenerationForMakeKOT();
	    objConsolidatedKOTTextFileGenerationForMakeKOT = new clsConsolidatedKOTTextFileGenerationForMakeKOT();

	    objKOTTextFileGenerationForDirectBiller = new clsKOTTextFileGenerationForDirectBiller();
	    objKOTJasperFileGenerationForDirectBiller = new clsKOTJasperFileGenerationForDirectBiller();
	    objConsolidatedKOTTextFileGenerationForDirectBiller = new clsConsolidatedKOTTextFileGenerationForDirectBiller();

	    String sql = "";
	    PreparedStatement pst = null;
	    String ncKOTYN = "N";
	    String pricingTable = "tblmenuitempricingdtl";
	    if (clsGlobalVarClass.gPlayZonePOS.equals("Y"))//Play Zone POS
	    {
		pricingTable = "tblplayzonepricinghd";
	    }

	    switch (type)
	    {
		case "Dina":

		    String areaCodeForAll = "";
		    String sql_AreaCode = "select strAreaCode from tblareamaster where strAreaName='All';";
		    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_AreaCode);
		    ResultSet rsAreaCode = pst.executeQuery();
		    if (rsAreaCode.next())
		    {
			areaCodeForAll = rsAreaCode.getString(1);
		    }
		    rsAreaCode.close();
		    sql = "select a.strItemName,a.strNCKotYN,d.strCostCenterCode,d.strPrimaryPrinterPort,d.strSecondaryPrinterPort,d.strCostCenterName "
			    + " ,ifnull(e.strLabelOnKOT,'KOT') strLabelOnKOT "
			    + " from tblitemrtemp a "
			    + " left outer join " + pricingTable + " c on a.strItemCode = c.strItemCode "
			    + " left outer join tblprintersetup d on c.strCostCenterCode=d.strCostCenterCode "
			    + " left outer join tblcostcentermaster e on c.strCostCenterCode=e.strCostCenterCode  "
			    + " where a.strKOTNo=? and a.strTableNo=? and (c.strPosCode=? or c.strPosCode='All') "
			    + " and (c.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo=? ) OR c.strAreaCode =?) "
			    + " group by d.strCostCenterCode";
		    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql);
		    pst.setString(1, KOTNo);
		    pst.setString(2, tableNo);
		    pst.setString(3, clsGlobalVarClass.gPOSCode);
		    pst.setString(4, tableNo);
		    pst.setString(5, areaCodeForAll);

		    ResultSet rsPrint = pst.executeQuery();
		    while (rsPrint.next())
		    {

			ncKOTYN = rsPrint.getString(2);//NC KOT YN

			// funGenerateTextFileForKOT("Dina", tableNo, rsPrint.getString(3), "", areaCodeForAll, KOTNo, reprint, rsPrint.getString(4), rsPrint.getString(5), rsPrint.getString(6), printYN, rsPrint.getString(2));
			if (clsGlobalVarClass.gPrintType.equals("Jasper"))
			{
			    if (clsGlobalVarClass.gClientCode.equalsIgnoreCase("239.001"))//urbo
			    {
				objJasperFormat2FileGenerationForMakeKOT=new clsKOTJasperFormat2FileGenerationForMakeKOT();
				objJasperFormat2FileGenerationForMakeKOT.funGenerateJasperForTableWiseKOT("Dina", tableNo, rsPrint.getString(3), "", areaCodeForAll, KOTNo, reprint, rsPrint.getString(4), rsPrint.getString(5), rsPrint.getString(6), printYN, rsPrint.getString(2), rsPrint.getString(7));
			    }
			    else
			    {
				objKOTJasperFileGenerationForMakeKOT.funGenerateJasperForTableWiseKOT("Dina", tableNo, rsPrint.getString(3), "", areaCodeForAll, KOTNo, reprint, rsPrint.getString(4), rsPrint.getString(5), rsPrint.getString(6), printYN, rsPrint.getString(2), rsPrint.getString(7));
			    }
			}
			else if (clsGlobalVarClass.gClientCode.equalsIgnoreCase("171.001") && clsGlobalVarClass.gPrintType.equals("Text File"))//china grill-pimpri menu head wise items kot format
			{
			    objKOTTextFileGenerationForMakeKOT.funGenerateTextKOTForMakeKOTForMenuHeadWise(tableNo, rsPrint.getString(3), areaCodeForAll, KOTNo, reprint, rsPrint.getString(4), rsPrint.getString(5), rsPrint.getString(6), printYN, rsPrint.getString(2), rsPrint.getString(7));
			}
			else //default kot format
			{
			    objKOTTextFileGenerationForMakeKOT.funGenerateTextFileForTableWiseKOT(tableNo, rsPrint.getString(3), areaCodeForAll, KOTNo, reprint, rsPrint.getString(4), rsPrint.getString(5), rsPrint.getString(6), printYN, rsPrint.getString(2), rsPrint.getString(7));
			}

		    }
		    rsPrint.close();
		    pst.close();

		    if (clsGlobalVarClass.gConsolidatedKOTPrinterPort.length() > 0)//print consolidated KOT only if it set
		    {
			if (printYN.equalsIgnoreCase("Y") && ncKOTYN.equalsIgnoreCase("N"))//print consolidated KOT only if it is not NC KOT
			{
			    objConsolidatedKOTTextFileGenerationForMakeKOT.funConsolidatedKOTForMakeKOTTextFileGeneration(tableNo, KOTNo);
			}
		    }
		    break;

		case "DirectBiller":

		    sql = "select a.strItemName,c.strCostCenterCode,c.strPrimaryPrinterPort "
			    + ",c.strSecondaryPrinterPort,c.strCostCenterName,d.strLabelOnKOT "
			    + " from tblbilldtl  a,tblmenuitempricingdtl b,tblprintersetup c,tblcostcentermaster d   "
			    + " where a.strBillNo=? "
			    + " and  a.strItemCode=b.strItemCode "
			    + " and b.strCostCenterCode=c.strCostCenterCode "
			    + " and b.strCostCenterCode=d.strCostCenterCode "
			    + " and (b.strPosCode=? or b.strPosCode='All') "
			    + " group by c.strCostCenterCode;";

		    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql);
		    pst.setString(1, billNo);
		    pst.setString(2, clsGlobalVarClass.gPOSCode);
		    ResultSet rsPrintDirect = pst.executeQuery();
		    while (rsPrintDirect.next())
		    {
			//funGenerateTextFileForKOTDirectBiller(rsPrintDirect.getString(2), "", clsGlobalVarClass.gDirectAreaCode, billNo, reprint, rsPrintDirect.getString(3), rsPrintDirect.getString(4), rsPrintDirect.getString(5));
			if (clsGlobalVarClass.gPrintType.equals("Jasper"))
			{
			    objKOTJasperFileGenerationForDirectBiller.funGenerateJasperForKOTDirectBiller(rsPrintDirect.getString(2), "", clsGlobalVarClass.gDineInAreaForDirectBiller, billNo, reprint, rsPrintDirect.getString(3), rsPrintDirect.getString(4), rsPrintDirect.getString(5), rsPrintDirect.getString(6));
			}
			else if (clsGlobalVarClass.gClientCode.equalsIgnoreCase("171.001") && clsGlobalVarClass.gPrintType.equals("Text File"))//menu head wise items kot format
			{
			    objKOTTextFileGenerationForDirectBiller.funGenerateTextFileForKOTDirectBiller(rsPrintDirect.getString(2), clsGlobalVarClass.gDineInAreaForDirectBiller, billNo, reprint, rsPrintDirect.getString(3), rsPrintDirect.getString(4), rsPrintDirect.getString(5), rsPrintDirect.getString(6));
			}
			else //if(clsGlobalVarClass.gPrintType.equals("Text File"))//default kot format
			{
			    objKOTTextFileGenerationForDirectBiller.funGenerateTextFileForKOTDirectBiller(rsPrintDirect.getString(2), clsGlobalVarClass.gDineInAreaForDirectBiller, billNo, reprint, rsPrintDirect.getString(3), rsPrintDirect.getString(4), rsPrintDirect.getString(5), rsPrintDirect.getString(6));
			}

		    }
		    rsPrintDirect.close();
		    pst.close();

		    if (clsGlobalVarClass.gConsolidatedKOTPrinterPort.length() > 0 && printYN.equalsIgnoreCase("Y"))//print consolidated KOT 
		    {
			objConsolidatedKOTTextFileGenerationForDirectBiller.funConsolidatedKOTForDirectBillerTextFileGeneration(billNo);
		    }

		    break;
	    }
	}
	catch (Exception e)
	{
	    objUtility.funShowErrorMessage(e);
	    objUtility = null;
	    e.printStackTrace();
	}
    }

    /**
     *
     * @param TableNo
     * @param WaiterName
     * @param printYN
     */
    public void funCkeckKotTextFile(String TableNo, String WaiterName, String printYN)
    {
	clsCheckKOT objCheckKOT = new clsCheckKOT();
	objCheckKOT.funCkeckKotTextFile(TableNo, WaiterName, printYN);
    }
    
     /**
     *
     * @param TableNo
     * @param WaiterName
     * @param printYN
     */
    public void funCkeckKotForJasper(String TableNo, String WaiterName, String printYN)
    {
	clsCheckKOT objCheckKOT = new clsCheckKOT();
	objCheckKOT.funCkeckKotForJasper(TableNo, WaiterName, printYN);
    }
    
    /**
     * Kitchen note
     */
    
    public void funPrintKOTMessage(String costCenterCode, String costCenterName,String kitchenNote)
    {
	clsKitchenNote objKitchenNote=new clsKitchenNote();
	objKitchenNote.funPrintKOTMessage(costCenterCode, costCenterName,kitchenNote);
    }

}
