/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting.Text.DayEnd;

import com.POSGlobal.controller.clsBillDtl;
import com.POSGlobal.controller.clsBillItemDtl;
import com.POSGlobal.controller.clsBillItemDtlBean;
import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsGroupSubGroupWiseSales;
import com.POSGlobal.controller.clsPosConfigFile;
import com.POSGlobal.controller.clsUtility;
import com.POSGlobal.controller.clsUtility2;
import com.POSPrinting.Utility.clsPrintingUtility;
import com.POSReport.controller.comparator.clsCreditBillReportComparator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ajim
 * @date Aug 26, 2017
 */
public class clsDayEndTextReport
{

    private clsUtility objUtility = new clsUtility();
    private clsUtility2 objUtility2 = new clsUtility2();
    private clsPrintingUtility objPrintingUtility = new clsPrintingUtility();
    private final String Line = "  --------------------------------------";
    private ResultSet rsBillPrint;
    private DecimalFormat decimalFormat = new DecimalFormat("######.##");

    private String KOTType, sql;

    private final DecimalFormat gDecimalFormat = clsGlobalVarClass.funGetGlobalDecimalFormatter();
    private final DecimalFormat decimalFormatForInt = new DecimalFormat("0");
    String dashLinesFor42Chars = "  ----------------------------------------";

    /**
     *
     * @param posCode
     * @param billDate
     * @param reprint
     * @param shiftNo
     * @param printYN
     */
    public void funGenerateTextDayEndReport(String posCode, String billDate, String reprint, int shiftNo, String printYN)
    {
	try
	{

	    String billHd = "tblqbillhd";
	    String billDtl = "tblqbilldtl";
	    String billSettlementDtl = "tblqbillsettlementdtl";
	    String billTaxDtl = "tblqbilltaxdtl";
	    String billComplementaryDtl = "tblqbillcomplementrydtl";
	    objPrintingUtility.funCreateTempFolder();
	    String filePath = System.getProperty("user.dir");
	    File Text_DayEndReport = new File(filePath + "/Temp/Temp_DayEndReport.txt");
	    FileWriter fstream_Report = new FileWriter(Text_DayEndReport);
	    BufferedWriter bufferedWriter = new BufferedWriter(fstream_Report);
	    boolean isReprint = false;
	    if ("reprint".equalsIgnoreCase(reprint))
	    {
		isReprint = true;
		objPrintingUtility.funPrintBlankSpace("[DUPLICATE]", bufferedWriter);
		bufferedWriter.write("[DUPLICATE]");
		bufferedWriter.newLine();
		billHd = "tblqbillhd";
		billDtl = "tblqbilldtl";
		billSettlementDtl = "tblqbillsettlementdtl";
		billTaxDtl = "tblqbilltaxdtl";
		billComplementaryDtl = "tblqbillcomplementrydtl";
	    }
	    if (clsGlobalVarClass.gEnableShiftYN)
	    {
		objPrintingUtility.funPrintBlankSpace("SHIFT END REPORT", bufferedWriter);
		bufferedWriter.write("SHIFT END REPORT");
	    }
	    else
	    {
		objPrintingUtility.funPrintBlankSpace("DAY END REPORT", bufferedWriter);
		bufferedWriter.write("DAY END REPORT");
	    }
	    bufferedWriter.newLine();
	    bufferedWriter.newLine();
	    String sqlDayEnd = "";
	    ResultSet rsDayend;

	    double grandTotalSales = 0;
	    if (posCode.equals("All"))
	    {
		sqlDayEnd = "select  'All' as POSCode,'All' as POSName,date(a.dtePOSDate),time(a.dteDayEndDateTime),sum(a.dblTotalSale), "
			+ " sum(a.dblFloat),sum(a.dblCash),sum(a.dblAdvance),  sum(a.dblTransferIn),sum(a.dblTotalReceipt),sum(a.dblPayments), "
			+ " sum(a.dblWithDrawal),sum(a.dblTransferOut),sum(a.dblTotalPay),  sum(a.dblCashInHand),sum(a.dblHDAmt), "
			+ " sum(a.dblDiningAmt),sum(a.dblTakeAway),sum(a.dblNoOfBill),sum(a.dblNoOfVoidedBill), "
			+ " sum(a.dblNoOfModifyBill),sum(a.dblRefund)  ,sum(a.dblTotalDiscount), "
			+ " sum(a.intTotalPax),sum(a.intNoOfTakeAway),sum(a.intNoOfHomeDelivery),  "
			+ " sum(a.strUserCreated),sum(a.strUserEdited), sum(a.intNoOfNCKOT),sum(a.intNoOfComplimentaryKOT), "
			+ " sum(a.intNoOfVoidKOT),sum(dblUsedDebitCardBalance),sum(dblUnusedDebitCardBalance),sum(a.dblTipAmt)"
			+ " ,sum(a.dblNoOfDiscountedBill) "
			+ " from tbldayendprocess a  "
			+ " where date(a.dtePOSDate)=? ";
		PreparedStatement pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlDayEnd);
		pst.setString(1, billDate);
		rsDayend = pst.executeQuery();
	    }
	    else
	    {
		sqlDayEnd = "SELECT a.strPOSCode,b.strPosName, DATE(a.dtePOSDate), TIME(a.dteDayEndDateTime),sum(a.dblTotalSale),\n"
			+ " sum(a.dblFloat),sum(a.dblCash),sum(a.dblAdvance),sum( a.dblTransferIn),sum(a.dblTotalReceipt),sum(a.dblPayments),\n"
			+ " sum(a.dblWithDrawal),sum(a.dblTransferOut),sum(a.dblTotalPay),sum(a.dblCashInHand),sum(a.dblHDAmt),\n"
			+ " sum(a.dblDiningAmt),sum(a.dblTakeAway),sum(a.dblNoOfBill),sum(a.dblNoOfVoidedBill),\n"
			+ " sum(a.dblNoOfModifyBill),sum(a.dblRefund),sum(a.dblTotalDiscount),\n"
			+ " sum(a.intTotalPax),sum(a.intNoOfTakeAway),sum(a.intNoOfHomeDelivery),\n"
			+ " a.strUserCreated,a.strUserEdited, sum(a.intNoOfNCKOT),sum(a.intNoOfComplimentaryKOT)\n"
			+ " ,sum(a.intNoOfVoidKOT),sum(a.dblUsedDebitCardBalance),sum(a.dblUnusedDebitCardBalance),sum(a.dblTipAmt),sum(a.dblNoOfDiscountedBill)\n"
			+ "FROM tbldayendprocess a,tblposmaster b "
			+ " where b.strPosCode=a.strPosCode "
			+ " and a.strPOSCode=? "
			+ " and date(a.dtePOSDate)=? "
			+ " and a.intShiftCode=? ";
		PreparedStatement pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlDayEnd);
		pst.setString(1, posCode);
		pst.setString(2, billDate);
		pst.setString(3, String.valueOf(shiftNo));
		rsDayend = pst.executeQuery();
	    }
	    if (rsDayend.next())
	    {
		//Header Part
		bufferedWriter.write("  POS Code    :");
		bufferedWriter.write(rsDayend.getString(1));
		bufferedWriter.newLine();

		bufferedWriter.write("  POS Name    :");
		bufferedWriter.write(rsDayend.getString(2));
		bufferedWriter.newLine();

		if (clsGlobalVarClass.gEnableShiftYN)
		{
		    bufferedWriter.write("  SHIFT No.    :");
		    bufferedWriter.write(String.valueOf(shiftNo));
		    bufferedWriter.newLine();
		}

		bufferedWriter.write("  POS Date    :");
		bufferedWriter.write(rsDayend.getString(3));
		bufferedWriter.write(" " + rsDayend.getString(4));
		bufferedWriter.newLine();

		if (clsGlobalVarClass.gEnableShiftYN)
		{
		    bufferedWriter.write("  SHIFT End By  :");
		}
		else
		{
		    bufferedWriter.write("  Day End By  :");
		}

		bufferedWriter.write(rsDayend.getString(28));
		bufferedWriter.newLine();
		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();
		// End Of Header Part

		//Start of Detail Part
		objPrintingUtility.funWriteTotal(" 1. HOME DELIVERY", gDecimalFormat.format(rsDayend.getDouble(16)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal(" 2. DINING", gDecimalFormat.format(rsDayend.getDouble(17)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal(" 3. TAKE AWAY", gDecimalFormat.format(rsDayend.getDouble(18)), bufferedWriter, "");
		bufferedWriter.newLine();

		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();
		objPrintingUtility.funWriteTotal(" 4. TOTAL", gDecimalFormat.format(rsDayend.getDouble(5)), bufferedWriter, "");
		grandTotalSales = rsDayend.getDouble(5);
		bufferedWriter.newLine();
		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal(" 5. DISCOUNT", gDecimalFormat.format(rsDayend.getDouble(23)), bufferedWriter, "");
		bufferedWriter.newLine();

		String floatLabel = "FLOAT";
		if (clsGlobalVarClass.gClientCode.equalsIgnoreCase("240.001"))
		{
		    floatLabel = "PETTY CASH";
		}
		objPrintingUtility.funWriteTotal(" 6. " + floatLabel, gDecimalFormat.format(rsDayend.getDouble(6)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal(" 7. CASH", gDecimalFormat.format(rsDayend.getDouble(7)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal(" 8. ADVANCE", gDecimalFormat.format(rsDayend.getDouble(8)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal(" 9. TRANSFER IN", gDecimalFormat.format(rsDayend.getDouble(9)), bufferedWriter, "");
		bufferedWriter.newLine();

		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();
		objPrintingUtility.funWriteTotal("10. TOTAL RECEIPT", gDecimalFormat.format(rsDayend.getDouble(10)), bufferedWriter, "");
		bufferedWriter.newLine();
		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal("11. PAYMENT", gDecimalFormat.format(rsDayend.getDouble(11)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal("12. WITHDRAWAL", gDecimalFormat.format(rsDayend.getDouble(12)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal("13. TRANSFER OUT", gDecimalFormat.format(rsDayend.getDouble(13)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal("14. REFUND", gDecimalFormat.format(rsDayend.getDouble(22)), bufferedWriter, "");
		bufferedWriter.newLine();

		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();
		objPrintingUtility.funWriteTotal("15. TOTAL PAYMENTS", gDecimalFormat.format(rsDayend.getDouble(14)), bufferedWriter, "");
		bufferedWriter.newLine();

		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();
		objPrintingUtility.funWriteTotal("16. CASH IN HAND", gDecimalFormat.format(rsDayend.getDouble(15)), bufferedWriter, "");
		bufferedWriter.newLine();
		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal("17. No. OF BILLS", decimalFormatForInt.format(rsDayend.getDouble(19)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal("18. No. OF VOIDED BILLS", decimalFormatForInt.format(rsDayend.getDouble(20)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal("19. No. OF MODIFIED BILLS", decimalFormatForInt.format(rsDayend.getDouble(21)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal("20. NO. OF PAX", decimalFormatForInt.format(rsDayend.getDouble(24)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal("21. No. OF HOME DEL", decimalFormatForInt.format(rsDayend.getDouble(26)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal("22. No. OF TAKE AWAY", decimalFormatForInt.format(rsDayend.getDouble(25)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal("23. No. OF NC KOT", decimalFormatForInt.format(rsDayend.getDouble(29)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal("24. No. OF COMPLIMENTARY BILLS", decimalFormatForInt.format(rsDayend.getDouble(30)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal("25. No. OF DISCOUNTED BILLS", decimalFormatForInt.format(rsDayend.getDouble(35)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal("26. No. OF VOID KOT", decimalFormatForInt.format(rsDayend.getDouble(31)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal("27. Used Card Balance", gDecimalFormat.format(rsDayend.getDouble(32)), bufferedWriter, "");
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal("28. Unused Card Balance", gDecimalFormat.format(rsDayend.getDouble(33)), bufferedWriter, "");
		bufferedWriter.newLine();
		//End of Detail Part

		//<< tip amount
		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();
		objPrintingUtility.funWriteTotal("29. Total Tip Amount", gDecimalFormat.format(rsDayend.getDouble(34)), bufferedWriter, "");
		bufferedWriter.newLine();
		//>> tip amount

		//Start of Settlement Brkup
		double totalAmt = 0.00;
		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();
		objPrintingUtility.funPrintBlankSpace("BILLING SETTLEMENT BREAK UP", bufferedWriter);
		bufferedWriter.write("BILLING SETTLEMENT BREAK UP");
		bufferedWriter.newLine();
		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();

		StringBuilder sbSqlLive = new StringBuilder();
		StringBuilder sbSqlQFile = new StringBuilder();
		StringBuilder sqlFilter = new StringBuilder();

		String fromDate = billDate;
		String toDate = billDate;

		sbSqlLive.append("select ifnull(c.strPosCode,'All'),a.strSettelmentDesc, ifnull(SUM(b.dblSettlementAmt),0.00) "
			+ ",ifnull(d.strposname,'All'), if(c.strPOSCode is null,0,COUNT(*)) "
			+ "from tblsettelmenthd a "
			+ "left outer join tblbillsettlementdtl b on a.strSettelmentCode=b.strSettlementCode and date(b.dteBillDate) BETWEEN '" + billDate + "' AND '" + billDate + "' "
			+ "left outer join tblbillhd c on b.strBillNo=c.strBillNo and date(b.dteBillDate)=date(c.dteBillDate) "
			+ "left outer join tblposmaster d on c.strPOSCode=d.strPosCode ");

		sbSqlQFile.append("select ifnull(c.strPosCode,'All'),a.strSettelmentDesc, ifnull(SUM(b.dblSettlementAmt),0.00) "
			+ ",ifnull(d.strposname,'All'), if(c.strPOSCode is null,0,COUNT(*)) "
			+ "from tblsettelmenthd a "
			+ "left outer join tblqbillsettlementdtl b on a.strSettelmentCode=b.strSettlementCode and date(b.dteBillDate) BETWEEN '" + fromDate + "' AND '" + toDate + "' "
			+ "left outer join tblqbillhd c on b.strBillNo=c.strBillNo and date(b.dteBillDate)=date(c.dteBillDate) "
			+ "left outer join tblposmaster d on c.strPOSCode=d.strPosCode ");

		sqlFilter.append(" where a.strSettelmentType!='Complementary' "
			+ "and a.strApplicable='Yes' ");

		if (!"All".equalsIgnoreCase(posCode))
		{
		    sqlFilter.append("and  c.strPosCode='" + posCode + "' ");
		}
		if (clsGlobalVarClass.gEnableShiftYN)
		{
		    if (clsGlobalVarClass.gEnableShiftYN && (!String.valueOf(shiftNo).equalsIgnoreCase("All")))
		    {
			sqlFilter.append(" and c.intShiftCode = '" + shiftNo + "' ");
		    }
		}

		sqlFilter.append("group by a.strSettelmentCode "
			+ "order by b.dblSettlementAmt desc ");

		sbSqlLive.append(sqlFilter);
		sbSqlQFile.append(sqlFilter);

		ResultSet rsData = clsGlobalVarClass.dbMysql.executeResultSet(sbSqlLive.toString());

		Map<String, clsBillItemDtlBean> mapSettlementModes = new HashMap<>();

		double grossRevenue = 0;
		while (rsData.next())
		{
		    String settlementName = rsData.getString(2);
		    if (mapSettlementModes.containsKey(settlementName))
		    {
			clsBillItemDtlBean obj = mapSettlementModes.get(settlementName);

			obj.setDblSettlementAmt(obj.getDblSettlementAmt() + rsData.getDouble(3));
			obj.setNoOfBills(obj.getNoOfBills() + rsData.getInt(5));

		    }
		    else
		    {
			clsBillItemDtlBean obj = new clsBillItemDtlBean();
			obj.setStrPosCode(rsData.getString(1));
			obj.setStrSettelmentMode(settlementName);
			obj.setDblSettlementAmt(rsData.getDouble(3));
			obj.setStrPosName(rsData.getString(4));
			obj.setNoOfBills(rsData.getInt(5));

			mapSettlementModes.put(settlementName, obj);

		    }

		    grossRevenue += rsData.getDouble(3);

		}

		rsData = clsGlobalVarClass.dbMysql.executeResultSet(sbSqlQFile.toString());
		while (rsData.next())
		{
		    String settlementName = rsData.getString(2);
		    if (mapSettlementModes.containsKey(settlementName))
		    {
			clsBillItemDtlBean obj = mapSettlementModes.get(settlementName);

			obj.setDblSettlementAmt(obj.getDblSettlementAmt() + rsData.getDouble(3));
			obj.setNoOfBills(obj.getNoOfBills() + rsData.getInt(5));

		    }
		    else
		    {
			clsBillItemDtlBean obj = new clsBillItemDtlBean();
			obj.setStrPosCode(rsData.getString(1));
			obj.setStrSettelmentMode(settlementName);
			obj.setDblSettlementAmt(rsData.getDouble(3));
			obj.setStrPosName(rsData.getString(4));
			obj.setNoOfBills(rsData.getInt(5));

			mapSettlementModes.put(settlementName, obj);

		    }

		    grossRevenue += rsData.getDouble(3);
		}

		List<clsBillItemDtlBean> listOfSettlementData = new ArrayList<clsBillItemDtlBean>();

		for (clsBillItemDtlBean objDtlBean : mapSettlementModes.values())
		{
		    listOfSettlementData.add(objDtlBean);
		}
		Comparator<clsBillItemDtlBean> amtComparator = new Comparator<clsBillItemDtlBean>()
		{

		    @Override
		    public int compare(clsBillItemDtlBean o1, clsBillItemDtlBean o2)
		    {
			if (o1.getDblSettlementAmt() == o2.getDblSettlementAmt())
			{
			    return 0;
			}
			else if (o1.getDblSettlementAmt() > o2.getDblSettlementAmt())
			{
			    return -1;
			}
			else
			{
			    return 1;
			}
		    }
		};

		Collections.sort(listOfSettlementData, amtComparator);

		for (clsBillItemDtlBean billItemDtlBean : listOfSettlementData)
		{
		    totalAmt += billItemDtlBean.getDblSettlementAmt();
		    objPrintingUtility.funWriteTotal(billItemDtlBean.getStrSettelmentMode(), gDecimalFormat.format(billItemDtlBean.getDblSettlementAmt()), bufferedWriter, "");
		    bufferedWriter.newLine();
		}

		//for complementary sales
		String sqlComplementarySales = "";
		PreparedStatement psComplementarySales = null;
		if (posCode.equalsIgnoreCase("All"))
		{

		    sqlComplementarySales = "select d.strSettelmentDesc, SUM(b.dblAmount) "
			    + "from  " + billHd + " a," + billComplementaryDtl + " b," + billSettlementDtl + " c, tblsettelmenthd d "
			    + "where a.strBillNo = b.strBillNo "
			    + "and b.strBillNo=c.strBillNo "
			    + "and date(a.dteBillDate)=date(b.dteBillDate) "
			    + "and date(b.dteBillDate)=date(c.dteBillDate) "
			    + "and c.strSettlementCode = d.strSettelmentCode "
			    + "and date(a.dteBillDate) = ? "
			    + "and d.strSettelmentType='Complementary' "
			    + "GROUP BY d.strSettelmentDesc";

		    psComplementarySales = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlComplementarySales);
		    psComplementarySales.setString(1, billDate);

		}
		else
		{

		    sqlComplementarySales = "select d.strSettelmentDesc, SUM(b.dblAmount) "
			    + "from  " + billHd + " a," + billComplementaryDtl + " b," + billSettlementDtl + " c, tblsettelmenthd d "
			    + "where a.strBillNo = b.strBillNo "
			    + "and b.strBillNo=c.strBillNo "
			    + "and date(a.dteBillDate)=date(b.dteBillDate) "
			    + "and date(b.dteBillDate)=date(c.dteBillDate) "
			    + "and c.strSettlementCode = d.strSettelmentCode "
			    + "and a.strPOSCode=?  "
			    + "and date(a.dteBillDate) = ? "
			    + "and d.strSettelmentType='Complementary' "
			    + "GROUP BY d.strSettelmentDesc";
		    psComplementarySales = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlComplementarySales);
		    psComplementarySales.setString(1, posCode);
		    psComplementarySales.setString(2, billDate);

		}
		ResultSet rsComplementarySales = psComplementarySales.executeQuery();
		boolean complementarySales = true;
		while (rsComplementarySales.next())
		{
		    if (complementarySales)
		    {
			objPrintingUtility.funWriteTotal("COMPLEMENTARY SALES", "", bufferedWriter, "");
			bufferedWriter.newLine();
			objPrintingUtility.funWriteTotal("-------------------", "", bufferedWriter, "");
			bufferedWriter.newLine();
		    }
		    complementarySales = false;
		    objPrintingUtility.funWriteTotal(rsComplementarySales.getString(1), gDecimalFormat.format(rsComplementarySales.getDouble(2)), bufferedWriter, "");
		    bufferedWriter.newLine();
		}
		rsComplementarySales.close();

		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTotal("   TOTAL", gDecimalFormat.format(totalAmt), bufferedWriter, "");
		bufferedWriter.newLine();
		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();
		bufferedWriter.write("   TAX Des             Taxable   Tax Amt   ");
		bufferedWriter.newLine();
		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();
		double totalTableAmt = 0.00, totalTaxAmt = 0.00;

		StringBuilder sqlTaxBuilder = new StringBuilder();
		sqlTaxBuilder.append("SELECT b.strTaxCode,c.strTaxDesc,sum(b.dblTaxableAmount) as dblTaxableAmount,sum(b.dblTaxAmount) as dblTaxAmount "
			+ "FROM " + billHd + " a "
			+ "INNER JOIN " + billTaxDtl + " b ON a.strBillNo = b.strBillNo and date(a.dteBillDate)=date(b.dteBillDate) "
			+ "INNER JOIN tblTaxHd c ON b.strTaxCode = c.strTaxCode "
			+ "LEFT OUTER JOIN tblposmaster d ON a.strposcode=d.strposcode "
			+ "where date(a.dteBillDate) between '" + billDate + "' and '" + billDate + "' ");
		if (!posCode.equalsIgnoreCase("All"))
		{
		    sqlTaxBuilder.append("and a.strPOSCode='" + posCode + "'  ");
		}
		if (clsGlobalVarClass.gEnableShiftYN && (!String.valueOf(shiftNo).equalsIgnoreCase("All")))
		{
		    sqlTaxBuilder.append("and a.intShiftCode='" + shiftNo + "' ");
		}
		sqlTaxBuilder.append("group by c.strTaxCode,c.strTaxDesc ");
		ResultSet rsTaxDtl = clsGlobalVarClass.dbMysql.executeResultSet(sqlTaxBuilder.toString());

		while (rsTaxDtl.next())
		{
		    objPrintingUtility.funWriteTextWithBlankLines("  " + rsTaxDtl.getString(2), 21, bufferedWriter);
		    objPrintingUtility.funWriteTextWithBlankLines(gDecimalFormat.format(rsTaxDtl.getDouble(3)), 10, bufferedWriter);
		    objPrintingUtility.funWriteTextWithBlankLines(gDecimalFormat.format(rsTaxDtl.getDouble(4)), 9, bufferedWriter);
		    bufferedWriter.newLine();

		    totalTableAmt += rsTaxDtl.getDouble(3);
		    totalTaxAmt += rsTaxDtl.getDouble(4);
		}
		rsTaxDtl.close();

		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();

		objPrintingUtility.funWriteTextWithBlankLines("  " + "Total Taxation", 21, bufferedWriter);
		objPrintingUtility.funWriteTextWithBlankLines("", 10, bufferedWriter);
		objPrintingUtility.funWriteTextWithBlankLines(gDecimalFormat.format(totalTaxAmt), 9, bufferedWriter);
		bufferedWriter.newLine();

		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();
	    }
	    rsDayend.close();

	    /**
	     * print round off
	     */
	    double dblRoundOff = 0;
	    StringBuilder sqlRoundOffBuilder = new StringBuilder();
	    sqlRoundOffBuilder.append("SELECT sum(a.dblRoundOff) "
		    + "FROM " + billHd + " a "
		    + "where date(a.dteBillDate) between '" + billDate + "' and '" + billDate + "' ");
	    if (!posCode.equalsIgnoreCase("All"))
	    {
		sqlRoundOffBuilder.append("and a.strPOSCode='" + posCode + "'  ");
	    }
	    if (clsGlobalVarClass.gEnableShiftYN && (!String.valueOf(shiftNo).equalsIgnoreCase("All")))
	    {
		sqlRoundOffBuilder.append("and a.intShiftCode='" + shiftNo + "' ");
	    }
	    sqlRoundOffBuilder.append(" ");
	    ResultSet rsRoundOff = clsGlobalVarClass.dbMysql.executeResultSet(sqlRoundOffBuilder.toString());
	    while (rsRoundOff.next())
	    {
		dblRoundOff += rsRoundOff.getDouble(1);
	    }
	    rsRoundOff.close();

	    bufferedWriter.write(dashLinesFor42Chars);
	    bufferedWriter.newLine();

	    bufferedWriter.write(objUtility.funPrintTextWithAlignment("  " + "Total Round Off", 22, "left"));
	    bufferedWriter.write(objUtility.funPrintTextWithAlignment("  " + gDecimalFormat.format(dblRoundOff), 19, "right"));
	    bufferedWriter.newLine();

	    bufferedWriter.write(dashLinesFor42Chars);
	    bufferedWriter.newLine();

	    //group wise subtotal
	    StringBuilder sqlBuilder = new StringBuilder();

	    Map<String, clsGroupSubGroupWiseSales> mapGroupWiseData = new HashMap<>();

	    //live group data
	    sqlBuilder.setLength(0);
	    sqlBuilder.append("SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscountAmt),f.strPosName "
		    + ", '" + clsGlobalVarClass.gUserCode + "',b.dblRate, SUM(b.dblAmount), SUM(b.dblDiscountAmt),a.strPOSCode "
		    + ", SUM(b.dblAmount)- SUM(b.dblDiscountAmt)+ SUM(b.dblTaxAmount),SUM(b.dblAmount)- SUM(b.dblDiscountAmt)netTotal "
		    + "FROM tblbillhd a,tblbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
		    + "WHERE a.strBillNo=b.strBillNo "
		    + " and date(a.dteBillDate)=date(b.dteBillDate) "
		    + "AND a.strPOSCode=f.strPOSCode "
		    + "AND a.strClientCode=b.strClientCode "
		    + "AND b.strItemCode=e.strItemCode "
		    + "AND c.strGroupCode=d.strGroupCode "
		    + "AND d.strSubGroupCode=e.strSubGroupCode "
		    + "AND a.strPOSCode = '" + posCode + "' "
		    + "AND DATE(a.dteBillDate)='" + billDate + "'"
		    + "GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode;");
	    ResultSet rsGroupData = clsGlobalVarClass.dbMysql.executeResultSet(sqlBuilder.toString());
	    while (rsGroupData.next())
	    {
		String groupCode = rsGroupData.getString(1);//groupCode
		String groupName = rsGroupData.getString(2);//groupCode
		double netTotalPlusTax = rsGroupData.getDouble(11);//subTotal-disc+tax
		double netTotal = rsGroupData.getDouble(12);//subTotal-disc

		if (mapGroupWiseData.containsKey(groupCode))
		{
		    clsGroupSubGroupWiseSales objGroupWiseSales = mapGroupWiseData.get(groupCode);
		    objGroupWiseSales.setDblNetTotalPlusTax(objGroupWiseSales.getDblNetTotalPlusTax() + netTotalPlusTax);
		    objGroupWiseSales.setDblNetTotal(objGroupWiseSales.getDblNetTotal() + netTotal);
		}
		else
		{
		    clsGroupSubGroupWiseSales objGroupWiseSales = new clsGroupSubGroupWiseSales();
		    objGroupWiseSales.setGroupCode(groupCode);
		    objGroupWiseSales.setGroupName(groupName);
		    objGroupWiseSales.setDblNetTotalPlusTax(netTotalPlusTax);
		    objGroupWiseSales.setDblNetTotal(netTotal);

		    mapGroupWiseData.put(groupCode, objGroupWiseSales);
		}
	    }
	    rsGroupData.close();
	    //live modifier group data
	    sqlBuilder.setLength(0);
	    sqlBuilder.append("SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscAmt),f.strPOSName "
		    + ",'" + clsGlobalVarClass.gUserCode + "','0', SUM(b.dblAmount), SUM(b.dblDiscAmt),a.strPOSCode, SUM(b.dblAmount)- SUM(b.dblDiscAmt) "
		    + "FROM tblbillmodifierdtl b,tblbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c "
		    + "WHERE a.strBillNo=b.strBillNo "
		    + " and date(a.dteBillDate)=date(b.dteBillDate) "
		    + "AND a.strPOSCode=f.strPosCode "
		    + "AND a.strClientCode=b.strClientCode  "
		    + "AND LEFT(b.strItemCode,7)=d.strItemCode  "
		    + "AND d.strSubGroupCode=e.strSubGroupCode "
		    + "AND e.strGroupCode=c.strGroupCode  "
		    + "AND b.dblamount>0 "
		    + "AND a.strPOSCode = '" + posCode + "' "
		    + "AND DATE(a.dteBillDate) = '" + billDate + "' "
		    + "GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode;");
	    rsGroupData = clsGlobalVarClass.dbMysql.executeResultSet(sqlBuilder.toString());
	    while (rsGroupData.next())
	    {
		String groupCode = rsGroupData.getString(1);//groupCode
		String groupName = rsGroupData.getString(2);//groupCode
		double netTotalPlusTax = rsGroupData.getDouble(11);//subTotal-disc+tax
		double netTotal = rsGroupData.getDouble(11);//subTotal-disc

		if (mapGroupWiseData.containsKey(groupCode))
		{
		    clsGroupSubGroupWiseSales objGroupWiseSales = mapGroupWiseData.get(groupCode);
		    objGroupWiseSales.setDblNetTotalPlusTax(objGroupWiseSales.getDblNetTotalPlusTax() + netTotalPlusTax);
		    objGroupWiseSales.setDblNetTotal(objGroupWiseSales.getDblNetTotal() + netTotal);
		}
		else
		{
		    clsGroupSubGroupWiseSales objGroupWiseSales = new clsGroupSubGroupWiseSales();
		    objGroupWiseSales.setGroupCode(groupCode);
		    objGroupWiseSales.setGroupName(groupName);
		    objGroupWiseSales.setDblNetTotalPlusTax(netTotalPlusTax);
		    objGroupWiseSales.setDblNetTotal(netTotal);

		    mapGroupWiseData.put(groupCode, objGroupWiseSales);
		}
	    }
	    rsGroupData.close();

	    //QFile group data
	    sqlBuilder.setLength(0);
	    sqlBuilder.append("SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscountAmt),f.strPosName "
		    + ", '" + clsGlobalVarClass.gUserCode + "',b.dblRate, SUM(b.dblAmount), SUM(b.dblDiscountAmt),a.strPOSCode "
		    + ", SUM(b.dblAmount)- SUM(b.dblDiscountAmt)+ SUM(b.dblTaxAmount),SUM(b.dblAmount)- SUM(b.dblDiscountAmt)netTotal "
		    + "FROM tblqbillhd a,tblqbilldtl b,tblgrouphd c,tblsubgrouphd d,tblitemmaster e,tblposmaster f "
		    + "WHERE a.strBillNo=b.strBillNo "
		    + "and date(a.dteBillDate)=date(b.dteBillDate) "
		    + "AND a.strPOSCode=f.strPOSCode "
		    + "AND a.strClientCode=b.strClientCode "
		    + "AND b.strItemCode=e.strItemCode "
		    + "AND c.strGroupCode=d.strGroupCode "
		    + "AND d.strSubGroupCode=e.strSubGroupCode "
		    + "AND a.strPOSCode = '" + posCode + "' "
		    + "AND DATE(a.dteBillDate)='" + billDate + "'"
		    + "GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode;");
	    rsGroupData = clsGlobalVarClass.dbMysql.executeResultSet(sqlBuilder.toString());
	    while (rsGroupData.next())
	    {
		String groupCode = rsGroupData.getString(1);//groupCode
		String groupName = rsGroupData.getString(2);//groupCode
		double netTotalPlusTax = rsGroupData.getDouble(11);//subTotal-disc+tax
		double netTotal = rsGroupData.getDouble(12);//subTotal-disc

		if (mapGroupWiseData.containsKey(groupCode))
		{
		    clsGroupSubGroupWiseSales objGroupWiseSales = mapGroupWiseData.get(groupCode);
		    objGroupWiseSales.setDblNetTotalPlusTax(objGroupWiseSales.getDblNetTotalPlusTax() + netTotalPlusTax);
		    objGroupWiseSales.setDblNetTotal(objGroupWiseSales.getDblNetTotal() + netTotal);
		}
		else
		{
		    clsGroupSubGroupWiseSales objGroupWiseSales = new clsGroupSubGroupWiseSales();
		    objGroupWiseSales.setGroupCode(groupCode);
		    objGroupWiseSales.setGroupName(groupName);
		    objGroupWiseSales.setDblNetTotalPlusTax(netTotalPlusTax);
		    objGroupWiseSales.setDblNetTotal(netTotal);

		    mapGroupWiseData.put(groupCode, objGroupWiseSales);
		}
	    }
	    rsGroupData.close();
	    //QFile modifier group data
	    sqlBuilder.setLength(0);
	    sqlBuilder.append("SELECT c.strGroupCode,c.strGroupName, SUM(b.dblQuantity), SUM(b.dblAmount)- SUM(b.dblDiscAmt),f.strPOSName "
		    + ",'" + clsGlobalVarClass.gUserCode + "','0', SUM(b.dblAmount), SUM(b.dblDiscAmt),a.strPOSCode, SUM(b.dblAmount)- SUM(b.dblDiscAmt) "
		    + "FROM tblqbillmodifierdtl b,tblqbillhd a,tblposmaster f,tblitemmaster d,tblsubgrouphd e,tblgrouphd c "
		    + "WHERE a.strBillNo=b.strBillNo "
		    + " and date(a.dteBillDate)=date(b.dteBillDate) "
		    + "AND a.strPOSCode=f.strPosCode "
		    + "AND a.strClientCode=b.strClientCode  "
		    + "AND LEFT(b.strItemCode,7)=d.strItemCode  "
		    + "AND d.strSubGroupCode=e.strSubGroupCode "
		    + "AND e.strGroupCode=c.strGroupCode  "
		    + "AND b.dblamount>0 "
		    + "AND a.strPOSCode = '" + posCode + "' "
		    + "AND DATE(a.dteBillDate) = '" + billDate + "' "
		    + "GROUP BY c.strGroupCode, c.strGroupName, a.strPoscode;");
	    rsGroupData = clsGlobalVarClass.dbMysql.executeResultSet(sqlBuilder.toString());
	    while (rsGroupData.next())
	    {
		String groupCode = rsGroupData.getString(1);//groupCode
		String groupName = rsGroupData.getString(2);//groupCode
		double netTotalPlusTax = rsGroupData.getDouble(11);//subTotal-disc+tax
		double netTotal = rsGroupData.getDouble(11);//subTotal-disc

		if (mapGroupWiseData.containsKey(groupCode))
		{
		    clsGroupSubGroupWiseSales objGroupWiseSales = mapGroupWiseData.get(groupCode);
		    objGroupWiseSales.setDblNetTotalPlusTax(objGroupWiseSales.getDblNetTotalPlusTax() + netTotalPlusTax);
		    objGroupWiseSales.setDblNetTotal(objGroupWiseSales.getDblNetTotal() + netTotal);
		}
		else
		{
		    clsGroupSubGroupWiseSales objGroupWiseSales = new clsGroupSubGroupWiseSales();
		    objGroupWiseSales.setGroupCode(groupCode);
		    objGroupWiseSales.setGroupName(groupName);
		    objGroupWiseSales.setDblNetTotalPlusTax(netTotalPlusTax);
		    objGroupWiseSales.setDblNetTotal(netTotal);

		    mapGroupWiseData.put(groupCode, objGroupWiseSales);
		}
	    }
	    rsGroupData.close();

	    if (mapGroupWiseData.size() > 0)
	    {
		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();
		int netTotalWidth = 12;
		if (clsGlobalVarClass.gClientCode.equals("240.001"))
		{
		    netTotalWidth = 23;
		}
		bufferedWriter.write(objUtility.funPrintTextWithAlignment("  Group", 16, "left"));
		bufferedWriter.write(objUtility.funPrintTextWithAlignment("  Net Total", netTotalWidth, "right"));
		if (!clsGlobalVarClass.gClientCode.equals("240.001"))
		{
		    bufferedWriter.write(objUtility.funPrintTextWithAlignment("  Gross Total", 12, "right"));
		}
		bufferedWriter.newLine();
		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();

		double totalNetTotal = 0, totalGrossTotal = 0;
		for (clsGroupSubGroupWiseSales objGroupWiseSales : mapGroupWiseData.values())
		{
		    bufferedWriter.write(objUtility.funPrintTextWithAlignment("  " + objGroupWiseSales.getGroupName(), 16, "left"));
		    bufferedWriter.write(objUtility.funPrintTextWithAlignment("  " + gDecimalFormat.format(objGroupWiseSales.getDblNetTotal()), netTotalWidth, "right"));
		    if (!clsGlobalVarClass.gClientCode.equals("240.001"))
		    {
			bufferedWriter.write(objUtility.funPrintTextWithAlignment("  " + gDecimalFormat.format(objGroupWiseSales.getDblNetTotalPlusTax()), 12, "right"));
		    }
		    bufferedWriter.newLine();

		    totalNetTotal += objGroupWiseSales.getDblNetTotal();
		    totalGrossTotal += objGroupWiseSales.getDblNetTotalPlusTax();
		}
		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();

		bufferedWriter.write(objUtility.funPrintTextWithAlignment("  " + "Total", 16, "left"));
		bufferedWriter.write(objUtility.funPrintTextWithAlignment("  " + gDecimalFormat.format(totalNetTotal), netTotalWidth, "right"));
		if (!clsGlobalVarClass.gClientCode.equals("240.001"))
		{
		    bufferedWriter.write(objUtility.funPrintTextWithAlignment("  " + gDecimalFormat.format(grandTotalSales), 12, "right"));
		}

		bufferedWriter.newLine();
		bufferedWriter.write(dashLinesFor42Chars);
		bufferedWriter.newLine();

	    }

	    /**
	     * Credit bill payment received
	     */
	    List<clsBillDtl> listOfCreditBillReport = new ArrayList<clsBillDtl>();

	    DateFormat dteDate = new SimpleDateFormat("dd-MMM-yyyy");
	    String sbSqlFilters = "", sbSqlFilters1 = "";

	    String sbSqlLive = "SELECT b.strReceiptNo as receiptNo, DATE_FORMAT(DATE(b.dteReceiptDate),'%d-%b-%Y')dteReceiptDate,b.strSettlementName as settlement,  "
		    + "SUM(b.dblReceiptAmt) as ReceivedAmt,c.strCustomerName,a.strCustomerCode "
		    + "FROM tblbillhd a,tblcreditbillreceipthd b,tblcustomermaster c "
		    + "WHERE a.strBillNo=b.strBillNo AND DATE(a.dteBillDate)= DATE(b.dteBillDate)  "
		    + "AND a.strClientCode=b.strClientCode AND a.strCustomerCode=c.strCustomerCode  "
		    + "AND DATE(b.dteReceiptDate) BETWEEN '" + billDate + "' AND '" + billDate + "' "
		    + " and a.strPOSCode='" + posCode + "'  "
		    + "GROUP BY c.strCustomerCode,b.strSettlementName ";

	    String sbSqlQFile = "SELECT b.strReceiptNo as receiptNo, DATE_FORMAT(DATE(b.dteReceiptDate),'%d-%b-%Y')dteReceiptDate,b.strSettlementName as settlement,  "
		    + "SUM(b.dblReceiptAmt) as ReceivedAmt,c.strCustomerName,a.strCustomerCode "
		    + "FROM tblqbillhd a,tblqcreditbillreceipthd b,tblcustomermaster c "
		    + "WHERE a.strBillNo=b.strBillNo AND DATE(a.dteBillDate)= DATE(b.dteBillDate)  "
		    + "AND a.strClientCode=b.strClientCode AND a.strCustomerCode=c.strCustomerCode  "
		    + "AND DATE(b.dteReceiptDate) BETWEEN '" + billDate + "' AND '" + billDate + "' "
		    + " and a.strPOSCode='" + posCode + "'  "
		    + "GROUP BY c.strCustomerCode,b.strSettlementName ";

	    sbSqlFilters1 = sbSqlFilters1 + " ";

	    sbSqlLive += " " + sbSqlFilters1;
	    sbSqlQFile += " " + sbSqlFilters1;

	    ResultSet rsLiveData = clsGlobalVarClass.dbMysql.executeResultSet(sbSqlLive);
	    while (rsLiveData.next())
	    {

		clsBillDtl objBean = new clsBillDtl();

		objBean.setStrCustomerName(rsLiveData.getString(5));
		objBean.setDblAmount(rsLiveData.getDouble(4));   //Receipt Amount
		objBean.setStrReceiptNo(rsLiveData.getString(1));  //Receipt No
		objBean.setDteReceiptDate(rsLiveData.getString(2));
		objBean.setStrSettlementName(rsLiveData.getString(3));

		listOfCreditBillReport.add(objBean);

	    }
	    rsLiveData.close();

	    ResultSet rsQfileModData = clsGlobalVarClass.dbMysql.executeResultSet(sbSqlQFile);
	    while (rsQfileModData.next())
	    {
		clsBillDtl objBean = new clsBillDtl();

		objBean.setStrCustomerName(rsQfileModData.getString(5));
		objBean.setDblAmount(rsQfileModData.getDouble(4));   //Receipt Amount
		objBean.setStrReceiptNo(rsQfileModData.getString(1));  //Receipt No
		objBean.setDteReceiptDate(rsQfileModData.getString(2));
		objBean.setStrSettlementName(rsQfileModData.getString(3));

		listOfCreditBillReport.add(objBean);
	    }
	    rsQfileModData.close();

	    Comparator<clsBillDtl> dateComparator = new Comparator<clsBillDtl>()
	    {

		@Override
		public int compare(clsBillDtl o1, clsBillDtl o2)
		{
		    return o1.getDteReceiptDate().compareToIgnoreCase(o2.getDteReceiptDate());
		}
	    };

	    Collections.sort(listOfCreditBillReport, new clsCreditBillReportComparator(dateComparator));

	    bufferedWriter.newLine();
	    bufferedWriter.write(objUtility.funPrintTextWithAlignment("  Pending Bill Received", 18, "left"));

	    bufferedWriter.newLine();
	    bufferedWriter.write(dashLinesFor42Chars);

	    bufferedWriter.newLine();
	    bufferedWriter.write(objUtility.funPrintTextWithAlignment("  Customer", 18, "left"));
	    bufferedWriter.write(objUtility.funPrintTextWithAlignment("Rec. Amt", 10, "right"));
	    bufferedWriter.write(objUtility.funPrintTextWithAlignment("  Pay Mode", 10, "left"));

	    bufferedWriter.newLine();
	    bufferedWriter.write(dashLinesFor42Chars);

	    for (clsBillDtl objCredirReceipt : listOfCreditBillReport)
	    {
		bufferedWriter.newLine();
		bufferedWriter.write(objUtility.funPrintTextWithAlignment("  " + objCredirReceipt.getStrCustomerName(), 18, "left"));
		bufferedWriter.write(objUtility.funPrintTextWithAlignment(gDecimalFormat.format(objCredirReceipt.getDblAmount()), 10, "right"));
		bufferedWriter.write(objUtility.funPrintTextWithAlignment("  " + objCredirReceipt.getStrSettlementName(), 10, "left"));
	    }
	    bufferedWriter.newLine();
	    bufferedWriter.write(dashLinesFor42Chars);

	    /**
	     * credit bill Outstaing bills for UNWIND
	     *
	     */
	    if (clsGlobalVarClass.gClientCode.equalsIgnoreCase("178.001"))//"178.001" UNWIND
	    {
		listOfCreditBillReport = new ArrayList<clsBillDtl>();

		sbSqlLive = "SELECT a.strCustomerCode, a.strCustomerName,if(CreditAmt is null,DebitAmt,DebitAmt - CreditAmt) Outstanding\n"
			+ "FROM \n"
			+ "(\n"
			+ "SELECT a.strPOSCode,a.strCustomerCode,d.strCustomerName, SUM(b.dblSettlementAmt) DebitAmt\n"
			+ "FROM tblbillhd a,tblbillsettlementdtl b,tblsettelmenthd c,tblcustomermaster d\n"
			+ "WHERE a.strBillNo=b.strBillNo AND b.strSettlementCode=c.strSettelmentCode \n"
			+ "AND DATE(a.dtebilldate)= DATE(b.dtebilldate) AND a.strClientCode=b.strClientCode \n"
			+ "AND c.strSettelmentType='Credit' AND a.strCustomerCode=d.strCustomerCode \n"
			+ "AND DATE(a.dteBillDate) <= '" + billDate + "' "
			+ "and a.strPOSCode='" + posCode + "' "
			+ "GROUP BY a.strCustomerCode) AS a\n"
			+ "left outer join  \n"
			+ "(\n"
			+ "SELECT c.strCustomerCode, ifnull(SUM(b.dblReceiptAmt),0) CreditAmt\n"
			+ "FROM tblbillhd a,tblqcreditbillreceipthd b,tblcustomermaster c\n"
			+ "WHERE a.strBillNo=b.strBillNo AND DATE(a.dteBillDate)= DATE(b.dteBillDate)\n"
			+ " AND a.strClientCode=b.strClientCode AND a.strCustomerCode=c.strCustomerCode \n"
			+ " AND DATE(a.dteBillDate) <= '" + billDate + "' "
			+ "and a.strPOSCode='" + posCode + "' "
			+ "GROUP BY c.strCustomerCode) AS b\n"
			+ "on a.strCustomerCode = b.strCustomerCode\n";

		sbSqlQFile = "SELECT a.strCustomerCode, a.strCustomerName,if(CreditAmt is null,DebitAmt,DebitAmt - CreditAmt) Outstanding\n"
			+ "FROM \n"
			+ "(\n"
			+ "SELECT a.strPOSCode,a.strCustomerCode,d.strCustomerName, SUM(b.dblSettlementAmt) DebitAmt\n"
			+ "FROM tblqbillhd a,tblqbillsettlementdtl b,tblsettelmenthd c,tblcustomermaster d\n"
			+ "WHERE a.strBillNo=b.strBillNo AND b.strSettlementCode=c.strSettelmentCode \n"
			+ "AND DATE(a.dtebilldate)= DATE(b.dtebilldate) AND a.strClientCode=b.strClientCode \n"
			+ "AND c.strSettelmentType='Credit' AND a.strCustomerCode=d.strCustomerCode \n"
			+ "AND DATE(a.dteBillDate) <= '" + billDate + "' "
			+ "and a.strPOSCode='" + posCode + "' "
			+ "GROUP BY a.strCustomerCode) AS a\n"
			+ "left outer join  \n"
			+ "(\n"
			+ "SELECT c.strCustomerCode, ifnull(SUM(b.dblReceiptAmt),0) CreditAmt\n"
			+ "FROM tblqbillhd a,tblqcreditbillreceipthd b,tblcustomermaster c\n"
			+ "WHERE a.strBillNo=b.strBillNo AND DATE(a.dteBillDate)= DATE(b.dteBillDate)\n"
			+ " AND a.strClientCode=b.strClientCode AND a.strCustomerCode=c.strCustomerCode \n"
			+ " AND DATE(a.dteBillDate) <= '" + billDate + "' "
			+ "and a.strPOSCode='" + posCode + "' "
			+ "GROUP BY c.strCustomerCode) AS b\n"
			+ "on a.strCustomerCode = b.strCustomerCode\n";
		sbSqlFilters = " Order by a.strCustomerName";

		sbSqlLive += " " + sbSqlFilters;
		sbSqlQFile += " " + sbSqlFilters;
		Map<String, clsBillDtl> hmCustomerDtl = new HashMap<String, clsBillDtl>();
		clsBillDtl objBean = null;
		rsLiveData = clsGlobalVarClass.dbMysql.executeResultSet(sbSqlLive);
		while (rsLiveData.next())
		{
		    if (hmCustomerDtl.containsKey(rsLiveData.getString(1)))
		    {
			objBean = hmCustomerDtl.get(rsLiveData.getString(1));
			objBean.setDblBalanceAmt(objBean.getDblBalanceAmt() + Double.parseDouble(rsLiveData.getString(3)));

		    }
		    else
		    {
			objBean = new clsBillDtl();
			objBean.setStrCustomerCode(rsLiveData.getString(1));
			objBean.setStrCustomerName(rsLiveData.getString(2));   //Customer Name
			objBean.setDblBalanceAmt(Double.parseDouble(rsLiveData.getString(3)));

			hmCustomerDtl.put(rsLiveData.getString(1), objBean);
		    }

		}
		rsLiveData.close();

		ResultSet rsQfileData = clsGlobalVarClass.dbMysql.executeResultSet(sbSqlQFile);
		while (rsQfileData.next())
		{
		    if (hmCustomerDtl.containsKey(rsQfileData.getString(1)))
		    {
			objBean = hmCustomerDtl.get(rsQfileData.getString(1));
			objBean.setDblBalanceAmt(objBean.getDblBalanceAmt() + Double.parseDouble(rsQfileData.getString(3)));

		    }
		    else
		    {
			objBean = new clsBillDtl();
			objBean.setStrCustomerCode(rsQfileData.getString(1));
			objBean.setStrCustomerName(rsQfileData.getString(2));   //Customer Name
			objBean.setDblBalanceAmt(Double.parseDouble(rsQfileData.getString(3)));

			hmCustomerDtl.put(rsQfileData.getString(1), objBean);
		    }

		}
		rsQfileData.close();

		for (Map.Entry<String, clsBillDtl> entryOp : hmCustomerDtl.entrySet())
		{
		    clsBillDtl objBillDtl = entryOp.getValue();
		    listOfCreditBillReport.add(objBillDtl);
		}

		Comparator<clsBillDtl> customerComparator = new Comparator<clsBillDtl>()
		{

		    @Override
		    public int compare(clsBillDtl o1, clsBillDtl o2)
		    {
			return o1.getStrCustomerCode().compareToIgnoreCase(o2.getStrCustomerCode());
		    }
		};

		Collections.sort(listOfCreditBillReport, new clsCreditBillReportComparator(customerComparator));

		bufferedWriter.newLine();
		bufferedWriter.write(objUtility.funPrintTextWithAlignment("  Customer Outstanding", 18, "left"));

		bufferedWriter.newLine();
		bufferedWriter.write(dashLinesFor42Chars);

		bufferedWriter.newLine();
		bufferedWriter.write(objUtility.funPrintTextWithAlignment("  Customer", 22, "left"));
		bufferedWriter.write(objUtility.funPrintTextWithAlignment("Balance", 19, "right"));

		bufferedWriter.newLine();
		bufferedWriter.write(dashLinesFor42Chars);

		for (clsBillDtl objCredirReceipt : listOfCreditBillReport)
		{
		    bufferedWriter.newLine();
		    bufferedWriter.write(objUtility.funPrintTextWithAlignment("  " + objCredirReceipt.getStrCustomerName(), 22, "left"));
		    bufferedWriter.write(objUtility.funPrintTextWithAlignment(gDecimalFormat.format(objCredirReceipt.getDblBalanceAmt()), 19, "right"));
		}
		bufferedWriter.newLine();
		bufferedWriter.write(dashLinesFor42Chars);

	    }

	    funTotalItemIncentive(bufferedWriter, billDate, posCode, shiftNo);

	    //        
	    bufferedWriter.newLine();
	    bufferedWriter.newLine();
	    bufferedWriter.newLine();
	    bufferedWriter.newLine();
	    if ("linux".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
	    {
		bufferedWriter.write("V");//Linux
	    }
	    else if ("windows".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
	    {
		if ("Inbuild".equalsIgnoreCase(clsPosConfigFile.gPrinterType))
		{
		    bufferedWriter.write("V");
		}
		else
		{
		    bufferedWriter.write("m");//windows
		}
	    }

	    bufferedWriter.close();
	    fstream_Report.close();

	    if (clsGlobalVarClass.gShowBill)
	    {
		objPrintingUtility.funShowTextFile(Text_DayEndReport, "", clsGlobalVarClass.gBillPrintPrinterPort);
	    }
	    if (printYN.equalsIgnoreCase("Y"))
	    {
		objPrintingUtility.funPrintToPrinter(clsGlobalVarClass.gBillPrintPrinterPort, "", "dayend", "N", isReprint);
	    }

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    private void funTotalItemIncentive(BufferedWriter bufferedWriter, String billDate, String posCode, int shiftNo)
    {
	try
	{
	    StringBuilder sqlBuilder = new StringBuilder();
	    List<clsBillDtl> listOfItemWiseIncentives = new ArrayList<>();
	    Map<String, clsBillDtl> mapItem = new HashMap<>();

	    double totalIncentiveAmt = 0.00;

	    String type = "Item Wise";

	    String waiterShortName = " '' ";
	    if (!type.equalsIgnoreCase("Item Wise"))
	    {
		waiterShortName = " d.strWShortName ";
	    }
	    String waiterShortNo = " '' ";
	    if (!type.equalsIgnoreCase("Item Wise"))
	    {
		waiterShortNo = " d.strWaiterNo ";
	    }

	    //Live Data
	    sqlBuilder.setLength(0);
	    sqlBuilder.append("SELECT " + waiterShortName + ",b.strItemName,sum(b.dblAmount),c.dblIncentiveValue "
		    + " ,IF(c.strIncentiveType='Amt', (c.dblIncentiveValue)*sum(b.dblQuantity), (c.dblIncentiveValue/100)*sum(b.dblAmount)) as amount, "
		    + " e.strPosName,e.strPosCode,b.strItemCode," + waiterShortNo + ",c.strIncentiveType,sum(b.dblQuantity)  "
		    + " FROM tblbillhd a,tblbilldtl b,tblposwiseitemwiseincentives c ");
	    if (!type.equalsIgnoreCase("Item Wise"))
	    {
		sqlBuilder.append(",tblwaitermaster d ");
	    }
	    sqlBuilder.append(",tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
		    + " where a.strBillNo=b.strBillNo "
		    + " and b.strItemCode=c.strItemCode ");
	    if (!type.equalsIgnoreCase("Item Wise"))
	    {
		sqlBuilder.append(" and b.strWaiterNo=d.strWaiterNo ");
	    }
	    sqlBuilder.append(" and a.strPOSCode=e.strPosCode "
		    + " and a.strPOSCode=c.strPOSCode "
		    + " and c.dblIncentiveValue>0 "
		    + " and b.strItemCode=f.strItemCode "
		    + " and f.strSubGroupCode=g.strSubGroupCode "
		    + " and g.strGroupCode=h.strGroupCode "
		    + " and date(a.dteBillDate) between '" + billDate + "' and '" + billDate + "' ");
	    if (!posCode.equalsIgnoreCase("All"))
	    {
		sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
	    }
	    if (clsGlobalVarClass.gEnableShiftYN && (!String.valueOf(shiftNo).equalsIgnoreCase("All")))
	    {
		sqlBuilder.append("and a.intShiftCode='" + shiftNo + "' ");
	    }

	    sqlBuilder.append("and a.strBillNo not in (select u.strBillNo "
		    + " from tblbillhd v,tblbillsettlementdtl u,tblsettelmenthd w "
		    + " where v.strBillNo=u.strBillNo and u.strSettlementCode=w.strSettelmentCode "
		    + " and w.strSettelmentType='Complementary' and date(v.dteBillDate) between '" + billDate + "' and '" + billDate + "')");
	    if (type.equalsIgnoreCase("Item Wise"))
	    {
		sqlBuilder.append(" group by b.strItemCode ");
		sqlBuilder.append(" order by b.strItemName ");
	    }
	    else if (type.equalsIgnoreCase("Summary"))
	    {
		sqlBuilder.append(" group by b.strWaiterNo");
		sqlBuilder.append(" order by d.strWShortName ");
	    }
	    else
	    {
		sqlBuilder.append(" group by b.strWaiterNo,c.strPOSCode,b.strItemCode ");
		sqlBuilder.append(" order by e.strPosName,d.strWShortName,b.strItemName ");
	    }
	    ResultSet rsWaiterWiseItemSales = clsGlobalVarClass.dbMysql.executeResultSet(sqlBuilder.toString());
	    while (rsWaiterWiseItemSales.next())
	    {
		String itemCode = rsWaiterWiseItemSales.getString(8);
		totalIncentiveAmt += rsWaiterWiseItemSales.getDouble(5);

		if (mapItem.containsKey(itemCode))
		{
		    clsBillDtl obj = mapItem.get(itemCode);

		    obj.setDblQuantity(obj.getDblQuantity() + rsWaiterWiseItemSales.getDouble(11));
		    obj.setDblAmount(obj.getDblAmount() + rsWaiterWiseItemSales.getDouble(3));

		    obj.setDblIncentive(obj.getDblIncentive() + rsWaiterWiseItemSales.getDouble(5));
		}
		else
		{
		    clsBillDtl obj = new clsBillDtl();

		    obj.setStrWShortName(rsWaiterWiseItemSales.getString(1));
		    obj.setStrItemName(rsWaiterWiseItemSales.getString(2));
		    obj.setStrItemCode(rsWaiterWiseItemSales.getString(8));
		    obj.setDblAmount(rsWaiterWiseItemSales.getDouble(3));
		    obj.setDblIncentivePer(rsWaiterWiseItemSales.getDouble(4));
		    obj.setDblIncentive(rsWaiterWiseItemSales.getDouble(5));
		    obj.setStrPosName(rsWaiterWiseItemSales.getString(6));
		    obj.setStrPOSCode(rsWaiterWiseItemSales.getString(7));
		    obj.setStrWaiterNo(rsWaiterWiseItemSales.getString(9));
		    obj.setStrRemarks(rsWaiterWiseItemSales.getString(10));
		    obj.setDblQuantity(rsWaiterWiseItemSales.getDouble(11));

		    mapItem.put(itemCode, obj);
		}
	    }
	    rsWaiterWiseItemSales.close();

	    //Q Data
	    sqlBuilder.setLength(0);
	    sqlBuilder.append("SELECT " + waiterShortName + ",b.strItemName,sum(b.dblAmount),c.dblIncentiveValue "
		    + " ,IF(c.strIncentiveType='Amt', (c.dblIncentiveValue)*sum(b.dblQuantity), (c.dblIncentiveValue/100)*sum(b.dblAmount)) as amount, "
		    + " e.strPosName,e.strPosCode,b.strItemCode," + waiterShortNo + ",c.strIncentiveType,sum(b.dblQuantity)  "
		    + " FROM tblqbillhd a,tblqbilldtl b,tblposwiseitemwiseincentives c ");
	    if (!type.equalsIgnoreCase("Item Wise"))
	    {
		sqlBuilder.append(",tblwaitermaster d ");
	    }
	    sqlBuilder.append(",tblposmaster e,tblitemmaster f,tblsubgrouphd g,tblgrouphd h "
		    + " where a.strBillNo=b.strBillNo "
		    + " and b.strItemCode=c.strItemCode ");
	    if (!type.equalsIgnoreCase("Item Wise"))
	    {
		sqlBuilder.append(" and b.strWaiterNo=d.strWaiterNo ");
	    }
	    sqlBuilder.append(" and a.strPOSCode=e.strPosCode "
		    + " and a.strPOSCode=c.strPOSCode "
		    + " and c.dblIncentiveValue>0 "
		    + " and b.strItemCode=f.strItemCode "
		    + " and f.strSubGroupCode=g.strSubGroupCode "
		    + " and g.strGroupCode=h.strGroupCode "
		    + " and date(a.dteBillDate) between '" + billDate + "' and '" + billDate + "' ");
	    if (!posCode.equalsIgnoreCase("All"))
	    {
		sqlBuilder.append("and a.strPOSCode='" + posCode + "' ");
	    }
	    if (clsGlobalVarClass.gEnableShiftYN && (!String.valueOf(shiftNo).equalsIgnoreCase("All")))
	    {
		sqlBuilder.append("and a.intShiftCode='" + shiftNo + "' ");
	    }

	    sqlBuilder.append("and a.strBillNo not in (select u.strBillNo "
		    + " from tblqbillhd v,tblqbillsettlementdtl u,tblsettelmenthd w "
		    + " where v.strBillNo=u.strBillNo and u.strSettlementCode=w.strSettelmentCode "
		    + " and w.strSettelmentType='Complementary' and date(v.dteBillDate) between '" + billDate + "' and '" + billDate + "')");
	    if (type.equalsIgnoreCase("Item Wise"))
	    {
		sqlBuilder.append(" group by b.strItemCode ");
		sqlBuilder.append(" order by b.strItemName ");
	    }
	    else if (type.equalsIgnoreCase("Summary"))
	    {
		sqlBuilder.append(" group by b.strWaiterNo");
		sqlBuilder.append(" order by d.strWShortName ");
	    }
	    else
	    {
		sqlBuilder.append(" group by b.strWaiterNo,c.strPOSCode,b.strItemCode ");
		sqlBuilder.append(" order by e.strPosName,d.strWShortName,b.strItemName ");
	    }
	    rsWaiterWiseItemSales = clsGlobalVarClass.dbMysql.executeResultSet(sqlBuilder.toString());
	    while (rsWaiterWiseItemSales.next())
	    {
		String itemCode = rsWaiterWiseItemSales.getString(8);

		totalIncentiveAmt += rsWaiterWiseItemSales.getDouble(5);

		if (mapItem.containsKey(itemCode))
		{
		    clsBillDtl obj = mapItem.get(itemCode);

		    obj.setDblQuantity(obj.getDblQuantity() + rsWaiterWiseItemSales.getDouble(11));
		    obj.setDblAmount(obj.getDblAmount() + rsWaiterWiseItemSales.getDouble(3));

		    obj.setDblIncentive(obj.getDblIncentive() + rsWaiterWiseItemSales.getDouble(5));
		}
		else
		{
		    clsBillDtl obj = new clsBillDtl();

		    obj.setStrWShortName(rsWaiterWiseItemSales.getString(1));
		    obj.setStrItemName(rsWaiterWiseItemSales.getString(2));
		    obj.setStrItemCode(rsWaiterWiseItemSales.getString(8));
		    obj.setDblAmount(rsWaiterWiseItemSales.getDouble(3));
		    obj.setDblIncentivePer(rsWaiterWiseItemSales.getDouble(4));
		    obj.setDblIncentive(rsWaiterWiseItemSales.getDouble(5));
		    obj.setStrPosName(rsWaiterWiseItemSales.getString(6));
		    obj.setStrPOSCode(rsWaiterWiseItemSales.getString(7));
		    obj.setStrWaiterNo(rsWaiterWiseItemSales.getString(9));
		    obj.setStrRemarks(rsWaiterWiseItemSales.getString(10));
		    obj.setDblQuantity(rsWaiterWiseItemSales.getDouble(11));

		    mapItem.put(itemCode, obj);
		}
	    }
	    rsWaiterWiseItemSales.close();

	    bufferedWriter.newLine();
	    bufferedWriter.write(dashLinesFor42Chars);

	    bufferedWriter.newLine();
	    bufferedWriter.write(objUtility.funPrintTextWithAlignment("  Total Incentive Amount",22, "left"));	   
	    bufferedWriter.write(objUtility.funPrintTextWithAlignment("  " + gDecimalFormat.format(totalIncentiveAmt),17, "right"));

	    bufferedWriter.newLine();
	    bufferedWriter.write(dashLinesFor42Chars);

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}

    }
}
