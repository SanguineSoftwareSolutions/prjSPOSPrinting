/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting.Jasper.Bill;

import com.POSPrinting.Utility.clsPrintingUtility;
import com.POSGlobal.controller.clsBillDtl;
import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsPosConfigFile;
import com.POSGlobal.controller.clsUtility;
import com.POSGlobal.controller.clsUtility2;
import com.POSPrinting.Interfaces.clsBillGenerationFormat;
import java.awt.Dimension;
import java.io.File;
import java.io.InputStream;
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

/**
 *
 * @author Ajim
 * @date Aug 26, 2017
 */
public class clsJasperFormat7ForBill implements clsBillGenerationFormat
{

    private SimpleDateFormat ddMMyyyyAMPMDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
    private clsUtility objUtility = new clsUtility();
    private clsUtility2 objUtility2 = new clsUtility2();
    private clsPrintingUtility objPrintingUtility = new clsPrintingUtility();
    private DecimalFormat stdDecimalFormat = new DecimalFormat("######.##");
    private final String dashedLineFor40Chars = "  --------------------------------------";

    private final DecimalFormat gDecimalFormat = clsGlobalVarClass.funGetGlobalDecimalFormatter();
    private final String gDecimalFormatString = clsGlobalVarClass.funGetGlobalDecimalFormatString();

    /**
     *
     * @param billNo
     * @param reprint
     * @param formName
     * @param transType
     * @param billDate
     * @param posCode
     * @param viewORprint
     */
    @Override
    public void funGenerateBill(String billNo, String reprint, String formName, String transType, String billDate, String posCode, String viewORprint)
    {

	billDate = billDate.split(" ")[0];

	HashMap hm = new HashMap();

	clsUtility objUtility = new clsUtility();
	DecimalFormat decimalFormat = new DecimalFormat("#.###");
	String Linefor5 = "  --------------------------------------";
	try
	{

	    String user = "";
	    String billhd;
	    String billdtl;
	    String billModifierdtl;
	    String billSettlementdtl;
	    String billtaxdtl;
	    String billDscFrom = "tblbilldiscdtl";
	    String billPromoDtl = "tblbillpromotiondtl";
	    String billType = " ";

	    if (clsGlobalVarClass.gHOPOSType.equalsIgnoreCase("HOPOS"))
	    {
		billhd = "tblqbillhd";
		billdtl = "tblqbilldtl";
		billModifierdtl = "tblqbillmodifierdtl";
		billSettlementdtl = "tblqbillsettlementdtl";
		billtaxdtl = "tblqbilltaxdtl";
		billDscFrom = "tblqbilldiscdtl";
		billPromoDtl = "tblqbillpromotiondtl";
	    }
	    else
	    {
		if ("sales report".equalsIgnoreCase(formName))
		{
		    billhd = "tblbillhd";
		    billdtl = "tblbilldtl";
		    billModifierdtl = "tblbillmodifierdtl";
		    billSettlementdtl = "tblbillsettlementdtl";
		    billtaxdtl = "tblbilltaxdtl";
		    billDscFrom = "tblbilldiscdtl";
		    billPromoDtl = "tblbillpromotiondtl";

		    long dateDiff = new clsUtility().funCompareDate(billDate, objUtility.funGetPOSDateForTransaction());
		    if (dateDiff > 0)
		    {
			billhd = "tblqbillhd";
			billdtl = "tblqbilldtl";
			billModifierdtl = "tblqbillmodifierdtl";
			billSettlementdtl = "tblqbillsettlementdtl";
			billtaxdtl = "tblqbilltaxdtl";
			billDscFrom = "tblqbilldiscdtl";
			billPromoDtl = "tblqbillpromotiondtl";
		    }

		    String sql = "select count(strBillNo) from tblbillhd where strBillNo='" + billNo + "' ";
		    ResultSet rsBillTable = clsGlobalVarClass.dbMysql.executeResultSet(sql);
		    rsBillTable.next();
		    int billCnt = rsBillTable.getInt(1);
		    if (billCnt == 0)
		    {
			billhd = "tblqbillhd";
			billdtl = "tblqbilldtl";
			billModifierdtl = "tblqbillmodifierdtl";
			billSettlementdtl = "tblqbillsettlementdtl";
			billtaxdtl = "tblqbilltaxdtl";
			billDscFrom = "tblqbilldiscdtl";
			billPromoDtl = "tblqbillpromotiondtl";
		    }
		}
		else
		{
		    billhd = "tblbillhd";
		    billdtl = "tblbilldtl";
		    billModifierdtl = "tblbillmodifierdtl";
		    billSettlementdtl = "tblbillsettlementdtl";
		    billtaxdtl = "tblbilltaxdtl";
		    billPromoDtl = "tblbillpromotiondtl";
		}
	    }
	    PreparedStatement pst = null;
	    String subTotal = "";
	    String grandTotal = "";
	    String advAmount = "";
	    String deliveryCharge = "";
	    String customerCode = "";
	    boolean flag_DirectBiller = false;

	    if (clsGlobalVarClass.gClientCode.equals("117.001"))
	    {
		if (clsGlobalVarClass.gPOSCode.equals("P01"))
		{
		    hm.put("posWiseHeading", "THE PREM'S HOTEL");
		}
		else if (clsGlobalVarClass.gPOSCode.equals("P02"))
		{
		    hm.put("posWiseHeading", "SWIG");
		}
	    }

	    if (clsGlobalVarClass.gClientCode.equals("239.001"))//URBO
	    {
		hm.put("brandName", "URBO");
	    }

	    boolean isReprint = false;
	    if ("reprint".equalsIgnoreCase(reprint))
	    {
		isReprint = true;
		hm.put("duplicate", "[DUPLICATE]");
	    }
	    if (transType.equals("Void"))
	    {
		hm.put("voidedBill", "VOIDED BILL");
	    }

	    boolean flag_isHomeDelvBill = false;
	    String SQL_HomeDelivery = "select strBillNo,strCustomerCode,strDPCode,tmeTime,strCustAddressLine1 "
		    + "from tblhomedelivery where strBillNo=? ;";
	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_HomeDelivery);
	    pst.setString(1, billNo);
	    ResultSet rs_HomeDelivery = pst.executeQuery();

	    List<clsBillDtl> listOfHomeDeliveryDtl = new ArrayList<>();
	    clsBillDtl objBillDtl = new clsBillDtl();

	    if (rs_HomeDelivery.next())
	    {
		flag_isHomeDelvBill = true;

		if (clsGlobalVarClass.gPrintHomeDeliveryYN)
		{
		    billType = "HOME DELIVERY";
		}
		customerCode = rs_HomeDelivery.getString(2);

		String SQL_CustomerDtl = "";

		if (rs_HomeDelivery.getString(5).equals("Temporary"))
		{
		    SQL_CustomerDtl = "select a.strCustomerName,a.strTempAddress,a.strTempStreet"
			    + " ,a.strTempLandmark,a.strBuildingName,a.strCity,a.intPinCode,a.longMobileNo "
			    + " from tblcustomermaster a left outer join tblbuildingmaster b "
			    + " on a.strBuldingCode=b.strBuildingCode "
			    + " where a.strCustomerCode=? ;";
		}
		else if (rs_HomeDelivery.getString(5).equals("Office"))
		{
		    SQL_CustomerDtl = "select a.strCustomerName,a.strOfficeBuildingName,a.strOfficeStreetName"
			    + ",a.strOfficeLandmark,a.strOfficeArea,a.strOfficeCity,a.strOfficePinCode,a.longMobileNo "
			    + " from tblcustomermaster a "
			    + " where a.strCustomerCode=? ";
		}
		else
		{
		    SQL_CustomerDtl = "select a.strCustomerName,a.strCustAddress,a.strStreetName"
			    + " ,a.strLandmark,a.strBuildingName,a.strCity,a.intPinCode,a.longMobileNo "
			    + " from tblcustomermaster a left outer join tblbuildingmaster b "
			    + " on a.strBuldingCode=b.strBuildingCode "
			    + " where a.strCustomerCode=? ;";
		}
		pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_CustomerDtl);
		pst.setString(1, rs_HomeDelivery.getString(2));
		ResultSet rs_CustomerDtl = pst.executeQuery();
		while (rs_CustomerDtl.next())
		{
		    StringBuilder fullAddress = new StringBuilder();

		    hm.put("NAME", rs_CustomerDtl.getString(1));
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName("Name         : " + rs_CustomerDtl.getString(1).toUpperCase());
		    fullAddress.append(objBillDtl.getStrItemName());
		    listOfHomeDeliveryDtl.add(objBillDtl);

		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName("ADDRESS    :" + rs_CustomerDtl.getString(2).toUpperCase());
		    fullAddress.append(objBillDtl.getStrItemName());
		    listOfHomeDeliveryDtl.add(objBillDtl);

		    if (rs_CustomerDtl.getString(3).trim().length() > 0)
		    {
			objBillDtl = new clsBillDtl();
			objBillDtl.setStrItemName(rs_CustomerDtl.getString(3).toUpperCase());//"Street    :" +
			fullAddress.append(objBillDtl.getStrItemName());
			listOfHomeDeliveryDtl.add(objBillDtl);
		    }

		    if (rs_CustomerDtl.getString(4).trim().length() > 0)
		    {
			objBillDtl = new clsBillDtl();
			objBillDtl.setStrItemName(rs_CustomerDtl.getString(4).toUpperCase());//"Landmark    :" +
			fullAddress.append(objBillDtl.getStrItemName());
			listOfHomeDeliveryDtl.add(objBillDtl);
		    }

		    if (rs_CustomerDtl.getString(6).trim().length() > 0)
		    {
			objBillDtl = new clsBillDtl();
			objBillDtl.setStrItemName(rs_CustomerDtl.getString(6).toUpperCase());//"City    :" +
			fullAddress.append(objBillDtl.getStrItemName());
			listOfHomeDeliveryDtl.add(objBillDtl);
		    }

		    if (rs_CustomerDtl.getString(7).trim().length() > 0)
		    {
			objBillDtl = new clsBillDtl();
			objBillDtl.setStrItemName(rs_CustomerDtl.getString(7).toUpperCase());//"Pin    :" +
			fullAddress.append(objBillDtl.getStrItemName());
			listOfHomeDeliveryDtl.add(objBillDtl);
		    }

		    hm.put("FullAddress", fullAddress);

		    if (rs_CustomerDtl.getString(8).isEmpty())
		    {
			hm.put("MOBILE_NO", "");
			objBillDtl = new clsBillDtl();
			objBillDtl.setStrItemName("MOBILE_NO  :" + " ");
			listOfHomeDeliveryDtl.add(objBillDtl);
		    }
		    else
		    {
			hm.put("MOBILE_NO", rs_CustomerDtl.getString(8));
			objBillDtl = new clsBillDtl();
			objBillDtl.setStrItemName("Mobile No    : " + rs_CustomerDtl.getString(8));
			listOfHomeDeliveryDtl.add(objBillDtl);
		    }
		}
		rs_CustomerDtl.close();

		if (null != rs_HomeDelivery.getString(3) && rs_HomeDelivery.getString(3).trim().length() > 0)
		{
		    String[] delBoys = rs_HomeDelivery.getString(3).split(",");
		    StringBuilder strIN = new StringBuilder("(");
		    for (int i = 0; i < delBoys.length; i++)
		    {
			if (i == 0)
			{
			    strIN.append("'" + delBoys[i] + "'");
			}
			else
			{
			    strIN.append(",'" + delBoys[i] + "'");
			}
		    }
		    strIN.append(")");
		    String SQL_DeliveryBoyDtl = "select strDPName from tbldeliverypersonmaster where strDPCode IN " + strIN + " ;";
		    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_DeliveryBoyDtl);

		    ResultSet rs_DeliveryBoyDtl = pst.executeQuery();
		    strIN.setLength(0);
		    for (int i = 0; rs_DeliveryBoyDtl.next(); i++)
		    {
			if (i == 0)
			{
			    strIN.append(rs_DeliveryBoyDtl.getString(1).toUpperCase());
			}
			else
			{
			    strIN.append("," + rs_DeliveryBoyDtl.getString(1).toUpperCase());
			}
		    }

		    if (strIN.toString().isEmpty())
		    {
			hm.put("DELV BOY", "");
		    }
		    else
		    {
			hm.put("DELV BOY", "Delivery Boy : " + strIN);
			objBillDtl = new clsBillDtl();
			objBillDtl.setStrItemName("Delivery Boy : " + strIN);
			listOfHomeDeliveryDtl.add(objBillDtl);
		    }
		    rs_DeliveryBoyDtl.close();
		}
		else
		{
		    hm.put("DELV BOY", "");
		}
	    }
	    rs_HomeDelivery.close();
	    int result = objPrintingUtility.funPrintTakeAwayForJasper(billhd, billNo);
	    if (result == 1)
	    {
		billType = "Take Away";
		String sql = "select a.strBillNo,a.dteBillDate,a.strCustomerCode,b.strCustomerName,b.longMobileNo "
			+ "from " + billhd + " a,tblcustomermaster b "
			+ "where a.strCustomerCode=b.strCustomerCode "
			+ "and a.strBillNo='" + billNo + "' "
			+ "and date(a.dteBillDate)='" + billDate + "' ";
		ResultSet rsCustomer = clsGlobalVarClass.dbMysql.executeResultSet(sql);
		if (rsCustomer.next())
		{
		    hm.put("NAME", rsCustomer.getString(4));
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName("Name         : " + rsCustomer.getString(4).toUpperCase());
		    listOfHomeDeliveryDtl.add(objBillDtl);

		    hm.put("MOBILE_NO", rsCustomer.getString(5));
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName("Mobile No    : " + rsCustomer.getString(5));
		    listOfHomeDeliveryDtl.add(objBillDtl);
		}
		rsCustomer.close();
	    }
	    if (clsGlobalVarClass.gPrintTaxInvoice.equalsIgnoreCase("Y"))
	    {
		hm.put("TAX_INVOICE", "TAX INVOICE");
	    }
	    if (clsGlobalVarClass.gClientCode.equals("047.001") && clsGlobalVarClass.gPOSCode.equals("P03"))
	    {
		hm.put("ClientName", "SHRI SHAM CATERERS");
		String cAddr1 = "Flat No.7, Mon Amour,";
		String cAddr2 = "Thorat Colony,Prabhat Road,";
		String cAddr3 = " Erandwane, Pune 411 004.";
		String cAddr4 = "Approved Caterers of";
		String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
		hm.put("ClientAddress1", cAddr1 + cAddr2);
		hm.put("ClientAddress2", cAddr3 + cAddr4);
		hm.put("ClientAddress3", cAddr5);
	    }
	    else if (clsGlobalVarClass.gClientCode.equals("047.001") && clsGlobalVarClass.gPOSCode.equals("P02"))
	    {
		hm.put("ClientName", "SHRI SHAM CATERERS");
		String cAddr1 = "Flat No.7, Mon Amour,";
		String cAddr2 = "Thorat Colony,Prabhat Road,";
		String cAddr3 = " Erandwane, Pune 411 004.";
		String cAddr4 = "Approved Caterers of";
		String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
		hm.put("ClientAddress1", cAddr1 + cAddr2);
		hm.put("ClientAddress2", cAddr3 + cAddr4);
		hm.put("ClientAddress3", cAddr5);
	    }
	    else if (clsGlobalVarClass.gClientCode.equals("092.001") || clsGlobalVarClass.gClientCode.equals("092.002") || clsGlobalVarClass.gClientCode.equals("092.003"))//Shree Sound Pvt. Ltd.
	    {
		hm.put("ClientName", "SSPL");
		hm.put("ClientAddress1", clsGlobalVarClass.gClientAddress1);
		hm.put("ClientAddress2", clsGlobalVarClass.gClientAddress2);
		hm.put("ClientAddress3", clsGlobalVarClass.gClientAddress3);

		if (clsGlobalVarClass.gCityName.trim().length() > 0)
		{
		    hm.put("ClientCity", clsGlobalVarClass.gCityName);
		}
	    }
	    else if (clsGlobalVarClass.gClientCode.equals("190.001"))
	    {
		String licenseName = clsGlobalVarClass.gClientName;
		if (billNo.startsWith("L"))//liqour bill license name
		{
		    licenseName = "STEP-IN-AGENCIES & ESTATE PVT LTD";
		}
		else
		{
		    licenseName = clsGlobalVarClass.gClientName;
		}

		hm.put("ClientName", licenseName);
		hm.put("ClientAddress1", clsGlobalVarClass.gClientAddress1);
		hm.put("ClientAddress2", clsGlobalVarClass.gClientAddress2);
		hm.put("ClientAddress3", clsGlobalVarClass.gClientAddress3);

		if (clsGlobalVarClass.gCityName.trim().length() > 0)
		{
		    hm.put("ClientCity", clsGlobalVarClass.gCityName);
		}
	    }
	    else
	    {
		hm.put("ClientName", clsGlobalVarClass.gClientName);
		hm.put("ClientAddress1", clsGlobalVarClass.gClientAddress1);
		hm.put("ClientAddress2", clsGlobalVarClass.gClientAddress2);
		hm.put("ClientAddress3", clsGlobalVarClass.gClientAddress3);

		if (clsGlobalVarClass.gCityName.trim().length() > 0)
		{
		    hm.put("ClientCity", clsGlobalVarClass.gCityName);
		}
	    }

	    hm.put("TEL NO", String.valueOf(clsGlobalVarClass.gClientTelNo));
	    hm.put("EMAIL ID", clsGlobalVarClass.gClientEmail);
	    hm.put("Line", Linefor5);

	    String query = "";
	    String SQL_BillHD = "";
	    String waiterName = "";
	    String waiterNo = "";
	    String tblName = "";
	    ResultSet rsQuery = null;
	    ResultSet rs_BillHD = null;
	    ResultSet rsTblName = null;
	    String sqlTblName = "";
	    String tabNo = "";
	    boolean flag_DirectBillerBlill = false;
	    boolean flgComplimentaryBill = false;
	    String sql = "select b.strSettelmentType from " + billSettlementdtl + " a,tblsettelmenthd b "
		    + " where a.strSettlementCode=b.strSettelmentCode and a.strBillNo='" + billNo + "' and b.strSettelmentType='Complementary' "
		    + "and date(a.dteBillDate)='" + billDate + "' ";
	    ResultSet rsSettlementType = clsGlobalVarClass.dbMysql.executeResultSet(sql);
	    if (rsSettlementType.next())
	    {
		flgComplimentaryBill = true;
	    }
	    rsSettlementType.close();

	    if (objPrintingUtility.funIsDirectBillerBill(billNo, billhd))
	    {
		flag_DirectBillerBlill = true;
		SQL_BillHD = "select a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
			+ "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
			+ ",ifnull(dblDeliveryCharges,0.00),ifnull(b.dblAdvDeposite,0.00),a.dblDiscountPer,c.strPOSName,a.intOrderNo "
			+ ",a.strKOTToBillNote "
			+ "from " + billhd + " a left outer join tbladvancereceipthd b on a.strAdvBookingNo=b.strAdvBookingNo "
			+ "left outer join tblposmaster c on a.strPOSCode=c.strPOSCode "
			+ "where a.strBillNo=?  "
			+ "and date(a.dteBillDate)=? ";
		flag_DirectBiller = true;
		pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_BillHD);
		pst.setString(1, billNo);
		pst.setString(2, billDate);
		rs_BillHD = pst.executeQuery();
		rs_BillHD.next();
	    }
	    else
	    {
		SQL_BillHD = "select a.strTableNo,a.strWaiterNo,a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
			+ "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
			+ ",dblDeliveryCharges,ifnull(c.dblAdvDeposite,0.00),a.dblDiscountPer,d.strPOSName,a.intPaxNo,a.intOrderNo "
			+ ",a.strKOTToBillNote "
			+ "from " + billhd + " a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
			+ "left outer join tbladvancereceipthd c on a.strAdvBookingNo=c.strAdvBookingNo "
			+ "left outer join tblposmaster d on a.strPOSCode=d.strPOSCode "
			+ "where a.strBillNo=? and b.strOperational='Y' and date(a.dteBillDate)=? ";
		pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_BillHD);
		pst.setString(1, billNo);
		pst.setString(2, billDate);
		rs_BillHD = pst.executeQuery();
		if (rs_BillHD.next())
		{
		    tabNo = rs_BillHD.getString(1);
		    if (rs_BillHD.getString(2).equalsIgnoreCase("null") || rs_BillHD.getString(2).equalsIgnoreCase(""))
		    {
			waiterNo = "";
		    }
		    else
		    {
			waiterNo = rs_BillHD.getString(2);
			query = "select strWShortName from tblwaitermaster where strWaiterNo=? ;";
			pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(query);
			pst.setString(1, waiterNo);
			rsQuery = pst.executeQuery();
			if (rsQuery.next())
			{
			    waiterName = rsQuery.getString(1);
			}
			rsQuery.close();
			pst.close();
		    }
		}

		sqlTblName = "select strTableName from tbltablemaster where strTableNo=? ;";
		pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlTblName);
		pst.setString(1, tabNo);
		rsTblName = pst.executeQuery();
		if (rsTblName.next())
		{
		    tblName = rsTblName.getString(1);
		}
		rsTblName.close();
		pst.close();
	    }

	    // funPrintTakeAway(billhd, billNo, BillOut);
	    if (flag_DirectBillerBlill)
	    {
		hm.put("POS", rs_BillHD.getString(14));
		hm.put("BillNo", billNo);

		String orderNo = rs_BillHD.getString(15);
		if (clsGlobalVarClass.gPrintOrderNoOnBillYN)
		{
		    hm.put("orderNo", "Your order no is " + orderNo);
		}
		
		String billNote = rs_BillHD.getString(16);
		hm.put("strBillNote", billNote);
		

		if (clsGlobalVarClass.gPrintTimeOnBillYN)
		{
		    SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
		    hm.put("DATE_TIME", ft.format(rs_BillHD.getObject(1)));
		}
		else
		{
		    SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
		    hm.put("DATE_TIME", ft.format(rs_BillHD.getObject(1)));
		}

		subTotal = rs_BillHD.getString(4);
		grandTotal = rs_BillHD.getString(6);
		user = rs_BillHD.getString(10);
		deliveryCharge = rs_BillHD.getString(11);
		advAmount = rs_BillHD.getString(12);
	    }
	    else
	    {
		hm.put("TABLE NAME", tblName);

		if (waiterName.trim().length() > 0)
		{
		    hm.put("waiterName", waiterName);
		}
		hm.put("POS", rs_BillHD.getString(16));
		hm.put("BillNo", billNo);
		hm.put("PaxNo", rs_BillHD.getString(17));

		String orderNo = rs_BillHD.getString(18);
		if (clsGlobalVarClass.gPrintOrderNoOnBillYN)
		{
		    hm.put("orderNo", "Your Order No. Is:" + orderNo);
		}
		
		String billNote = rs_BillHD.getString(19);
		hm.put("strBillNote", billNote);

		if (clsGlobalVarClass.gPrintTimeOnBillYN)
		{
		    SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
		    hm.put("DATE_TIME", ft.format(rs_BillHD.getObject(3)));
		}
		else
		{
		    SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
		    hm.put("DATE_TIME", ft.format(rs_BillHD.getObject(3)));
		}

		subTotal = rs_BillHD.getString(6);
		grandTotal = rs_BillHD.getString(8);
		user = rs_BillHD.getString(12);
		deliveryCharge = rs_BillHD.getString(13);
		advAmount = rs_BillHD.getString(14);
	    }

	    List<clsBillDtl> listOfBillDetail = new ArrayList<>();
	    String SQL_BillDtl = "select sum(a.dblQuantity),a.strItemName as ItemLine1"
		    + " ,MID(a.strItemName,23,LENGTH(a.strItemName)) as ItemLine2"
		    + " ,sum(a.dblAmount),a.strItemCode,a.strKOTNo "
		    + " from " + billdtl + " a "
		    + " where a.strBillNo=? and a.tdhYN='N' and date(a.dteBillDate)=?";
	    if (!clsGlobalVarClass.gPrintOpenItemsOnBill)
	    {
		SQL_BillDtl += "and a.dblAmount>0 ";
	    }
	    SQL_BillDtl += " group by a.strItemCode ;";
	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_BillDtl);
	    pst.setString(1, billNo);
	    pst.setString(2, billDate);
	    ResultSet rs_BillDtl = pst.executeQuery();
	    while (rs_BillDtl.next())
	    {
		double saleQty = rs_BillDtl.getDouble(1);
		String sqlPromoBills = "select dblQuantity from " + billPromoDtl + " "
			+ " where strBillNo='" + billNo + "' and strItemCode='" + rs_BillDtl.getString(5) + "' "
			+ " and strPromoType='ItemWise' and date(dteBillDate)='" + billDate + "'";
		ResultSet rsPromoItems = clsGlobalVarClass.dbMysql.executeResultSet(sqlPromoBills);
		if (rsPromoItems.next())
		{
		    saleQty -= rsPromoItems.getDouble(1);
		}
		rsPromoItems.close();

		String qty = String.valueOf(saleQty);
		if (qty.contains("."))
		{
		    String decVal = qty.substring(qty.length() - 2, qty.length());
		    if (Double.parseDouble(decVal) == 0)
		    {
			qty = qty.substring(0, qty.length() - 2);
		    }
		}

		if (saleQty > 0)
		{
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setDblQuantity(Double.parseDouble(decimalFormat.format(Double.parseDouble(qty))));
		    objBillDtl.setDblAmount(rs_BillDtl.getDouble(4));
		    objBillDtl.setStrItemName(rs_BillDtl.getString(2));
		    listOfBillDetail.add(objBillDtl);

		    String sqlModifier = "select count(*) "
			    + "from " + billModifierdtl + " where strBillNo=? and left(strItemCode,7)=? and date(dteBillDate)=?";
		    if (!clsGlobalVarClass.gPrintZeroAmtModifierOnBill)
		    {
			sqlModifier += " and  dblAmount !=0.00 ";
		    }
		    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlModifier);

		    pst.setString(1, billNo);
		    pst.setString(2, rs_BillDtl.getString(5));
		    pst.setString(3, billDate);
		    ResultSet rs_count = pst.executeQuery();
		    rs_count.next();
		    int cntRecord = rs_count.getInt(1);
		    rs_count.close();
		    if (cntRecord > 0)
		    {
			sqlModifier = "select strModifierName,dblQuantity,dblAmount "
				+ " from " + billModifierdtl + " "
				+ " where strBillNo=? and left(strItemCode,7)=? and date(dteBillDate)=?";
			if (!clsGlobalVarClass.gPrintZeroAmtModifierOnBill)
			{
			    sqlModifier += " and  dblAmount !=0.00 ";
			}
			pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlModifier);

			pst.setString(1, billNo);
			pst.setString(2, rs_BillDtl.getString(5));
			pst.setString(3, billDate);
			ResultSet rs_modifierRecord = pst.executeQuery();
			while (rs_modifierRecord.next())
			{
			    if (flgComplimentaryBill)
			    {
				objBillDtl = new clsBillDtl();
				objBillDtl.setDblQuantity(Double.parseDouble(decimalFormat.format(rs_modifierRecord.getDouble(2))));
				objBillDtl.setDblAmount(0);
				objBillDtl.setStrItemName(rs_modifierRecord.getString(1).toUpperCase());
				listOfBillDetail.add(objBillDtl);
			    }
			    else
			    {
				objBillDtl = new clsBillDtl();
				objBillDtl.setDblQuantity(Double.parseDouble(decimalFormat.format(rs_modifierRecord.getDouble(2))));
				objBillDtl.setDblAmount(rs_modifierRecord.getDouble(3));
				objBillDtl.setStrItemName(rs_modifierRecord.getString(1).toUpperCase());
				listOfBillDetail.add(objBillDtl);
			    }
			}
			rs_modifierRecord.close();
		    }
		}
	    }
	    rs_BillDtl.close();

	    objPrintingUtility.funPrintPromoItemsInBill(billNo, 4, listOfBillDetail);  // Print Promotion Items in Bill for this billno.

	    List<clsBillDtl> listOfDiscountDtl = new ArrayList<>();
	    sql = "select a.dblDiscPer,a.dblDiscAmt,a.strDiscOnType,a.strDiscOnValue,b.strReasonName,a.strDiscRemarks "
		    + "from " + billDscFrom + " a ,tblreasonmaster b "
		    + "where  a.strDiscReasonCode=b.strReasonCode "
		    + "and a.strBillNo='" + billNo + "' and date(a.dteBillDate)='" + billDate + "'";
	    ResultSet rsDisc = clsGlobalVarClass.dbMysql.executeResultSet(sql);

	    boolean flag = true;
	    while (rsDisc.next())
	    {
		if (flag)
		{
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName("Discount");
		    listOfDiscountDtl.add(objBillDtl);
		    flag = false;
		}
		double dbl = Double.parseDouble(rsDisc.getString("dblDiscPer"));
		String discText = String.format("%.1f", dbl) + "%" + " On " + rsDisc.getString("strDiscOnValue") + "";
		if (discText.length() > 30)
		{
		    discText = discText.substring(0, 30);
		}
		else
		{
		    discText = String.format("%-30s", discText);
		}

		String discountOnItem = objUtility.funPrintTextWithAlignment(rsDisc.getString("dblDiscAmt"), 8, "Right");
		hm.put("Discount", discText + " " + discountOnItem);
		objBillDtl = new clsBillDtl();
		objBillDtl.setStrItemName(discText);
		objBillDtl.setDblAmount(rsDisc.getDouble("dblDiscAmt"));
		listOfDiscountDtl.add(objBillDtl);

		objBillDtl = new clsBillDtl();
		objBillDtl.setStrItemName("Reason :" + " " + rsDisc.getString("strReasonName"));
		listOfDiscountDtl.add(objBillDtl);

		objBillDtl = new clsBillDtl();
		objBillDtl.setStrItemName("Remark :" + " " + rsDisc.getString("strDiscRemarks"));
		listOfDiscountDtl.add(objBillDtl);
	    }

	    List<clsBillDtl> listOfTaxDetail = new ArrayList<>();
	    String sql_Tax = "select b.strTaxDesc,sum(a.dblTaxAmount),b.strBillNote "
		    + " from " + billtaxdtl + " a,tbltaxhd b "
		    + " where a.strBillNo='" + billNo + "' and a.strTaxCode=b.strTaxCode "
		    + " group by a.strTaxCode";
	    ResultSet rsTax = clsGlobalVarClass.dbMysql.executeResultSet(sql_Tax);

	    while (rsTax.next())
	    {
		if (flgComplimentaryBill)
		{
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setDblAmount(0);
		    objBillDtl.setStrItemName(rsTax.getString(1));
		    listOfTaxDetail.add(objBillDtl);
		    hm.put("GSTNo", rsTax.getString(3));
		}
		else
		{
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setDblAmount(rsTax.getDouble(2));
		    objBillDtl.setStrItemName(rsTax.getString(1));
		    listOfTaxDetail.add(objBillDtl);
		    hm.put("GSTNo", rsTax.getString(3));
		}
	    }
	    rsTax.close();

	    //add del charges
	    double delCharges = Double.parseDouble(deliveryCharge);
	    objBillDtl = new clsBillDtl();
	    objBillDtl.setDblAmount(delCharges);
	    objBillDtl.setStrItemName("Del. Charges");
	    if (delCharges > 0)
	    {
		listOfTaxDetail.add(objBillDtl);
	    }

	    List<clsBillDtl> listOfGrandTotalDtl = new ArrayList<>();
	    if (Double.parseDouble(grandTotal) > 0)
	    {
		objBillDtl = new clsBillDtl();
		objBillDtl.setDblAmount(Double.parseDouble(grandTotal));
		listOfGrandTotalDtl.add(objBillDtl);
	    }

	    List<clsBillDtl> listOfBillSeriesDtl = new ArrayList<>();

	    //print Grand total of other bill nos from bill series
	    if (clsGlobalVarClass.gEnableBillSeries)
	    {
		String sqlPrintGT = "select a.strPrintGTOfOtherBills,b.strDtlBillNos,b.dblGrandTotal "
			+ "from tblbillseries a,tblbillseriesbilldtl b "
			+ "where (a.strposCode=b.strposCode or a.strposCode='All') "
			+ "and a.strBillSeries=b.strBillSeries "
			+ "and b.strHdBillNo='" + billNo + "' "
			+ "and b.strposCode='" + posCode + "' "
			+ "and date(b.dteBillDate)='" + billDate + "' ";
		ResultSet rsPrintGTYN = clsGlobalVarClass.dbMysql.executeResultSet(sqlPrintGT);
		double dblOtherBillsGT = 0.00;
		if (rsPrintGTYN.next())
		{
		    if (rsPrintGTYN.getString(1).equalsIgnoreCase("Y"))
		    {
			String billSeriesDtlBillNos = rsPrintGTYN.getString(2);
			String[] dtlBillSeriesBillNo = billSeriesDtlBillNos.split(",");
			dblOtherBillsGT += rsPrintGTYN.getDouble(3);
			if (dtlBillSeriesBillNo.length > 0)
			{
			    for (int i = 0; i < dtlBillSeriesBillNo.length; i++)
			    {
				sqlPrintGT = "select a.strHdBillNo,a.dblGrandTotal "
					+ "from tblbillseriesbilldtl a "
					+ "where a.strHdBillNo='" + dtlBillSeriesBillNo[i] + "' "
					+ "and a.strposCode='" + posCode + "' "
					+ "and date(a.dteBillDate)='" + billDate + "' ";
				ResultSet rsPrintGT = clsGlobalVarClass.dbMysql.executeResultSet(sqlPrintGT);
				if (rsPrintGT.next())
				{

				    dblOtherBillsGT += rsPrintGT.getDouble(2);

				    clsBillDtl objBillSeriesDtl = new clsBillDtl();
				    objBillSeriesDtl.setStrItemName(dtlBillSeriesBillNo[i]);
				    objBillSeriesDtl.setDblAmount(rsPrintGT.getDouble(2));

				    listOfBillSeriesDtl.add(objBillSeriesDtl);
				}
			    }
			    clsBillDtl objBillSeriesDtl = new clsBillDtl();
			    objBillSeriesDtl.setStrItemName("GRAND TOTAL(ROUNDED)");
			    objBillSeriesDtl.setDblAmount(Double.parseDouble(gDecimalFormat.format(dblOtherBillsGT)));

			    listOfBillSeriesDtl.add(objBillSeriesDtl);
			}
		    }
		}
	    }

	    List<clsBillDtl> listOfSettlementDetail = new ArrayList<>();
	    //settlement breakup part
	    String sqlSettlementBreakup = "select a.dblSettlementAmt, b.strSettelmentDesc, b.strSettelmentType "
		    + " from " + billSettlementdtl + " a ,tblsettelmenthd b "
		    + "where a.strBillNo=? and a.strSettlementCode=b.strSettelmentCode"
		    + " and date(a.dteBillDate)=?";
	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlSettlementBreakup);
	    pst.setString(1, billNo);
	    pst.setString(2, billDate);
	    ResultSet rs_Bill_Settlement = pst.executeQuery();
	    while (rs_Bill_Settlement.next())
	    {
		if (flgComplimentaryBill)
		{
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName(rs_Bill_Settlement.getString(2));
		    objBillDtl.setDblAmount(0.00);
		    listOfSettlementDetail.add(objBillDtl);
		}
		else
		{
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName(rs_Bill_Settlement.getString(2));
		    objBillDtl.setDblAmount(rs_Bill_Settlement.getDouble(1));
		    listOfSettlementDetail.add(objBillDtl);
		}
	    }
	    rs_Bill_Settlement.close();

	    String sqlTenderAmt = "select sum(dblPaidAmt),sum(dblSettlementAmt),(sum(dblPaidAmt)-sum(dblSettlementAmt)) RefundAmt "
		    + " from " + billSettlementdtl + " where strBillNo='" + billNo + "' "
		    + " and date(dteBillDate)='" + billDate + "'"
		    + " group by strBillNo";
	    ResultSet rsTenderAmt = clsGlobalVarClass.dbMysql.executeResultSet(sqlTenderAmt);
	    if (rsTenderAmt.next())
	    {
		if (flgComplimentaryBill)
		{
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName("PAID AMT");
		    objBillDtl.setDblAmount(0.00);
		    listOfSettlementDetail.add(objBillDtl);
		}
		else
		{
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName("PAID AMT");
		    objBillDtl.setDblAmount(rsTenderAmt.getDouble(1));
		    listOfSettlementDetail.add(objBillDtl);
		    if (rsTenderAmt.getDouble(3) > 0)
		    {

			objBillDtl = new clsBillDtl();
			objBillDtl.setStrItemName("REFUND AMT");
			objBillDtl.setDblAmount(rsTenderAmt.getDouble(3));
			listOfSettlementDetail.add(objBillDtl);
		    }
		}
	    }
	    rsTenderAmt.close();

	    if (flag_isHomeDelvBill)
	    {
		String sql_count = "select count(*) from tblhomedelivery where strCustomerCode=?";
		pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_count);
		pst.setString(1, customerCode);
		ResultSet rs_Count = pst.executeQuery();
		rs_Count.next();
		hm.put("CUSTOMER_COUNT", rs_Count.getString(1));
	    }

	    List<clsBillDtl> listOfServiceVatDetail = objPrintingUtility.funPrintServiceVatNoForJasper(billNo, billDate, billtaxdtl);
	    List<clsBillDtl> listOfFooterDtl = new ArrayList<>();

	    String sqlFooter = " select a.strBillFooter from tblsetup a where (a.strPOSCode='" + posCode + "' or strPOSCode='All');";
	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlFooter);
	    ResultSet rsFooter = pst.executeQuery();
	    if (rsFooter.next())
	    {
		String billFooter = rsFooter.getString(1);

		String[] footers = billFooter.split("\n");
		for (int i = 0; i < footers.length; i++)
		{
		    objBillDtl = new clsBillDtl();
		    objBillDtl.setStrItemName(footers[i]);
		    listOfFooterDtl.add(objBillDtl);
		}
	    }
	    rsFooter.close();

	    hm.put("user", user);

	    if ("linux".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
	    {
		hm.put("ch", "V");
	    }
	    else if ("windows".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
	    {
		if ("Inbuild".equalsIgnoreCase(clsPosConfigFile.gPrinterType))
		{
		    hm.put("ch", "V");
		}
		else
		{
		    hm.put("ch", "m");
		}
	    }

	    hm.put("BillType", billType);
	    hm.put("listOfItemDtl", listOfBillDetail);
	    hm.put("listOfTaxDtl", listOfTaxDetail);
	    hm.put("listOfGrandTotalDtl", listOfGrandTotalDtl);
	    hm.put("listOfBillSeriesDtl", listOfBillSeriesDtl);
	    hm.put("listOfServiceVatDetail", listOfServiceVatDetail);
	    hm.put("listOfFooterDtl", listOfFooterDtl);
	    hm.put("listOfHomeDeliveryDtl", listOfHomeDeliveryDtl);
	    hm.put("listOfDiscountDtl", listOfDiscountDtl);
	    hm.put("listOfSettlementDetail", listOfSettlementDetail);

	    hm.put("decimalFormaterForDoubleValue", gDecimalFormatString);
	    hm.put("decimalFormaterForIntegerValue", "0");

	    String imagePath = System.getProperty("user.dir");
	    imagePath = imagePath + File.separator + "ReportImage";
	    imagePath = imagePath + File.separator + "imgBillLogo.jpg";
	    System.out.println("imgBillLogo=" + imagePath);
	    hm.put("imgBillLogo", imagePath);

	    List<List<clsBillDtl>> listData = new ArrayList<>();
	    listData.add(listOfBillDetail);

	    String reportName = "";

	    reportName = "com/POSGlobal/reports/rptBillFormat7JasperReport.jasper";

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
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    }

	    if (clsGlobalVarClass.gMultiBillPrint)
	    {
		for (int i = 0; i < 2; i++)
		{
		    new Thread()
		    {
			@Override
			public void run()
			{
			    objPrintingUtility.funPrintJasperExporterInThread(print);
			}
		    }.start();
		}
	    }
	    else
	    {
		new Thread()
		{
		    @Override
		    public void run()
		    {
			System.out.println("Form Name for jasper" + formName);
			if (formName.equals("sales report") || formName.equals("Reprint Document"))
			{

			}
			else
			{
			    objPrintingUtility.funPrintJasperExporterInThread(print);
			}

		    }
		}.start();

	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

}
