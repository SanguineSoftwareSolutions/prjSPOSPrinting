package com.POSPrinting.Jasper.KOT;

import com.POSPrinting.Utility.clsPrintingUtility;
import com.POSGlobal.controller.clsBillDtl;
import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsUtility;
import com.POSGlobal.controller.clsUtility2;
import java.awt.Dimension;
import java.io.InputStream;
import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

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
public class clsKOTJasperFileGenerationForMakeKOT
{

    private DecimalFormat decimalFormat = new DecimalFormat("#.###");
    private SimpleDateFormat ddMMyyyyAMPMDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
    private clsUtility objUtility = new clsUtility();
    private clsUtility2 objUtility2 = new clsUtility2();
    private clsPrintingUtility objPrintingUtility = new clsPrintingUtility();

    /**
     *
     * @param billingType
     * @param tableNo
     * @param CostCenterCode
     * @param ShowKOT
     * @param AreaCode
     * @param KOTNO
     * @param Reprint
     * @param primaryPrinterName
     * @param secondaryPrinterName
     * @param CostCenterName
     * @param printYN
     * @param NCKotYN
     * @param labelOnKOT
     */
    public void funGenerateJasperForTableWiseKOT(String billingType, String tableNo, String CostCenterCode, String ShowKOT, String AreaCode, String KOTNO, String Reprint, String primaryPrinterName, String secondaryPrinterName, String CostCenterName, String printYN, String NCKotYN, String labelOnKOT)
    {
	HashMap hm = new HashMap();
	List<List<clsBillDtl>> listData = new ArrayList<>();

	try
	{
	    PreparedStatement pst = null;
	    boolean isReprint = false;
	    if ("Reprint".equalsIgnoreCase(Reprint))
	    {
		isReprint = true;
		hm.put("dublicate", "[DUPLICATE]");
	    }
	    if ("Y".equalsIgnoreCase(NCKotYN))
	    {
		hm.put("KOTorNC", "NCKOT");
	    }
	    else
	    {
		hm.put("KOTorNC", labelOnKOT);
	    }
	    hm.put("POS", clsGlobalVarClass.gPOSName);
	    hm.put("costCenter", CostCenterName);

	    String tableName = "";
	    int pax = 0;
	    String SQL_KOT_Dina_tableName = "select strTableName,intPaxNo "
		    + " from tbltablemaster "
		    + " where strTableNo=? and strOperational='Y'";
	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_KOT_Dina_tableName);
	    pst.setString(1, tableNo);
	    ResultSet rs_Dina_Table = pst.executeQuery();
	    while (rs_Dina_Table.next())
	    {
		tableName = rs_Dina_Table.getString(1);
		pax = rs_Dina_Table.getInt(2);
	    }
	    rs_Dina_Table.close();

	    String sqlKOTItems = "";
	    List<clsBillDtl> listOfKOTDetail = new ArrayList<>();
	    if (clsGlobalVarClass.gAreaWisePricing.equals("Y"))
	    {
		sqlKOTItems = "select LEFT(a.strItemCode,7),b.strItemName,a.dblItemQuantity,a.strKOTNo,a.strSerialNo,d.strShortName "
			+ " from tblitemrtemp a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
			+ " where a.strTableNo=? and a.strKOTNo=? and b.strCostCenterCode=c.strCostCenterCode "
			+ " and b.strCostCenterCode=? and a.strItemCode=d.strItemCode "
			+ " and (b.strPOSCode=? or b.strPOSCode='All') "
			+ " and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo=? )) "
			+ " and LEFT(a.strItemCode,7)=b.strItemCode and b.strHourlyPricing='No' "
			+ " order by a.strSerialNo ";
	    }
	    else
	    {
		sqlKOTItems = "select LEFT(a.strItemCode,7),b.strItemName,a.dblItemQuantity,a.strKOTNo,a.strSerialNo,d.strShortName "
			+ " from tblitemrtemp a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
			+ " where a.strTableNo=? and a.strKOTNo=? and b.strCostCenterCode=c.strCostCenterCode "
			+ " and b.strCostCenterCode=? and a.strItemCode=d.strItemCode "
			+ " and (b.strPOSCode=? or b.strPOSCode='All') "
			+ " and (b.strAreaCode IN (SELECT strAreaCode FROM tbltablemaster where strTableNo=? ) "
			+ " OR b.strAreaCode ='" + AreaCode + "') "
			+ " and LEFT(a.strItemCode,7)=b.strItemCode and b.strHourlyPricing='No' "
			+ " order by a.strSerialNo ";
	    }
	    //System.out.println(sqlKOTItems);

	    PreparedStatement pst_KOT_Items = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlKOTItems);
	    pst_KOT_Items.setString(1, tableNo);
	    pst_KOT_Items.setString(2, KOTNO);
	    pst_KOT_Items.setString(3, CostCenterCode);
	    pst_KOT_Items.setString(4, clsGlobalVarClass.gPOSCode);
	    pst_KOT_Items.setString(5, tableNo);
	    //pst_KOT_Items.setString(5, AreaCode);
	    String KOTType = "DINE";
	    if (null != clsGlobalVarClass.hmTakeAway.get(tableNo))
	    {
		KOTType = "Take Away";
	    }
	    hm.put("KOTType", KOTType);
	    if (clsGlobalVarClass.gCounterWise.equals("Yes"))
	    {
		hm.put("CounterName", clsGlobalVarClass.gCounterName);
	    }
	    hm.put("KOT", KOTNO);
	    hm.put("tableNo", tableName);
	    if (clsGlobalVarClass.gClientCode.equals("124.001"))
	    {
		hm.put("124.001", tableName);
	    }
	    hm.put("PAX", String.valueOf(pax));

	    String sqlWaiterDtl = "select strWaiterNo from tblitemrtemp where strKOTNo=?  and strTableNo=? group by strKOTNo ;";
	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlWaiterDtl);
	    pst.setString(1, KOTNO);
	    pst.setString(2, tableNo);
	    ResultSet rsWaiterDtl = pst.executeQuery();
	    if (rsWaiterDtl.next())
	    {
		if (!"null".equalsIgnoreCase(rsWaiterDtl.getString(1)) && rsWaiterDtl.getString(1).trim().length() > 0)
		{
		    sqlWaiterDtl = "select strWShortName from tblwaitermaster where strWaiterNo=? ;";
		    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlWaiterDtl);
		    pst.setString(1, rsWaiterDtl.getString(1));
		    ResultSet rs = pst.executeQuery();
		    rs.next();
		    hm.put("waiterName", rs.getString(1));
		    rs.close();
		}
	    }
	    rsWaiterDtl.close();
	    String sql_KOTDate = "select dteDateCreated from tblitemrtemp where strKOTNo=?  and strTableNo=? group by strKOTNo ;";
	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_KOTDate);
	    pst.setString(1, KOTNO);
	    pst.setString(2, tableNo);
	    ResultSet rs_KOTDate = pst.executeQuery();
	    rs_KOTDate.next();

	    hm.put("DATE_TIME", ddMMyyyyAMPMDateFormat.format(rs_KOTDate.getObject(1)));
	    rs_KOTDate.close();
	    InetAddress ipAddress = InetAddress.getLocalHost();
	    String hostName = ipAddress.getHostName();

	    if (clsGlobalVarClass.gPrintDeviceAndUserDtlOnKOTYN)
	    {
		hm.put("KOT From", hostName);
		hm.put("kotByUser", clsGlobalVarClass.gUserCode);
	    }

	    ResultSet rs_KOT_Items = pst_KOT_Items.executeQuery();
	    while (rs_KOT_Items.next())
	    {

		String itemName = rs_KOT_Items.getString(2);
		if (clsGlobalVarClass.gPrintShortNameOnKOT && !rs_KOT_Items.getString(6).trim().isEmpty())
		{
		    itemName = rs_KOT_Items.getString(6);
		}

		clsBillDtl objBillDtl = new clsBillDtl();
		objBillDtl.setDblQuantity(Double.parseDouble(rs_KOT_Items.getString(3)));
		objBillDtl.setStrItemName(itemName);
		listOfKOTDetail.add(objBillDtl);
		String sql_Modifier = "select a.strItemName,sum(a.dblItemQuantity) from tblitemrtemp a "
			+ " where a.strItemCode like'" + rs_KOT_Items.getString(1) + "M%' and a.strKOTNo='" + KOTNO + "' "
			+ " and strSerialNo like'" + rs_KOT_Items.getString(5) + ".%' "
			+ " group by a.strItemCode,a.strItemName ";
		//System.out.println(sql_Modifier);
		ResultSet rsModifierItems = clsGlobalVarClass.dbMysql.executeResultSet(sql_Modifier);
		while (rsModifierItems.next())
		{
		    objBillDtl = new clsBillDtl();
		    String modifierName = rsModifierItems.getString(1);
		    if (modifierName.startsWith("-->"))
		    {
			if (clsGlobalVarClass.gPrintModQtyOnKOT)
			{
			    objBillDtl.setDblQuantity(Double.parseDouble(rsModifierItems.getString(2)));
			    objBillDtl.setStrItemName(rsModifierItems.getString(1));
			}
			else
			{
			    objBillDtl.setDblQuantity(0);
			    objBillDtl.setStrItemName(rsModifierItems.getString(1));
			}
		    }
		    listOfKOTDetail.add(objBillDtl);
		}
	    }
	    rs_KOT_Items.close();
	    pst_KOT_Items.close();
	    pst.close();

	    for (int cntLines = 0; cntLines < Integer.parseInt(clsGlobalVarClass.gNoOfLinesInKOTPrint); cntLines++)
	    {
		clsBillDtl objBillDtl = new clsBillDtl();
		objBillDtl.setDblQuantity(0);
		objBillDtl.setStrItemName("  ");
		listOfKOTDetail.add(objBillDtl);
	    }

	    hm.put("listOfItemDtl", listOfKOTDetail);
	    listData.add(listOfKOTDetail);
	    String reportName = "com/POSGlobal/reports/rptGenrateKOTJasperReport.jasper";
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
	    clsUtility2 objUtility2 = new clsUtility2();

	    ResultSet rsPrinter = clsGlobalVarClass.dbMysql.executeResultSet("select a.strPrinterPort,a.strSecondaryPrinterPort,a.strPrintOnBothPrinters from tblcostcentermaster  a where a.strCostCenterCode='" + CostCenterCode + "' ");
	    if (rsPrinter.next())
	    {
		String primary = rsPrinter.getString(1);
		String secondary = rsPrinter.getString(2);
		String printOnBothPrinters = rsPrinter.getString(3);

		//primary = primary.replaceAll("#", "\\\\");
		//printServiceAttributeSet.add(new PrinterName(primary, null));
		primary = primary.replaceAll("#", "\\\\");
		secondary = secondary.replaceAll("#", "\\\\");

		if (printOnBothPrinters.equalsIgnoreCase("Y"))
		{
		    objUtility2.funPrintJasperKOT(primary, print);
		    objUtility2.funPrintJasperKOT(secondary, print);
//		    if (clsGlobalVarClass.gMultipleKOTPrint)
//		    {
//			int noOfCopies = 1;
//			String sql = "select a.intCostCenterWiseNoOfCopies from tblcostcentermaster a where a.strCostCenterCode='"+CostCenterCode+"'";
//			ResultSet rsNoOfCopies = clsGlobalVarClass.dbMysql.executeResultSet(sql);
//			if (rsNoOfCopies.next())
//			{
//			    noOfCopies = rsNoOfCopies.getInt(1);
//			}
//			if(noOfCopies>1)
//			{	
//			    for(int i=0;i<noOfCopies-1;i++)
//			    {	
//			    objUtility2.funPrintJasperKOT(primary, print);
//			    objUtility2.funPrintJasperKOT(secondary, print);
//			    }
//			}
//		     }	
//			objUtility2.funPrintJasperKOT(primary, print);
//			objUtility2.funPrintJasperKOT(primary, print);
//
//			objUtility2.funPrintJasperKOT(secondary, print);
//			objUtility2.funPrintJasperKOT(secondary, print);
		   
//		    else
//		    {
//			
//			objUtility2.funPrintJasperKOT(primary, print);
//			objUtility2.funPrintJasperKOT(secondary, print);
//		    }
		}
		else
		{

		    if (clsGlobalVarClass.gMultipleKOTPrint)
		    {
			if (!objUtility2.funPrintJasperKOT(primary, print))
			{
			    objUtility2.funPrintJasperKOT(secondary, print);
			}
			  
//			int noOfCopies = 1;
//			String sql = "select a.intCostCenterWiseNoOfCopies from tblcostcentermaster a where a.strCostCenterCode='"+CostCenterCode+"'";
//			ResultSet rsNoOfCopies = clsGlobalVarClass.dbMysql.executeResultSet(sql);
//			if (rsNoOfCopies.next())
//			{
//			    noOfCopies = rsNoOfCopies.getInt(1);
//			}
//			if(noOfCopies>1)
//			{	
//			    for(int i=0;i<noOfCopies-1;i++)
//			    {	
//				if (!objUtility2.funPrintJasperKOT(primary, print))
//				{
//				    objUtility2.funPrintJasperKOT(secondary, print);
//				}
//				   
//			    }
//			}	
//			if (!objUtility2.funPrintJasperKOT(primary, print))
//			{
//			    objUtility2.funPrintJasperKOT(secondary, print);
//			}
//			if (!objUtility2.funPrintJasperKOT(primary, print))
//			{
//			    objUtility2.funPrintJasperKOT(secondary, print);
//			}
			
		    }
//		    else
//		    {
//			if (!objUtility2.funPrintJasperKOT(primary, print))
//			{
//			    objUtility2.funPrintJasperKOT(secondary, print);
//			}
//		    }
		}
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }
}
