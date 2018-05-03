/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting.Text.KOT.Audit;

import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsPosConfigFile;
import com.POSGlobal.controller.clsUtility;
import com.POSGlobal.controller.clsUtility2;
import com.POSPrinting.Interfaces.clsVoidKOTForAuditGenerationFormat;
import com.POSPrinting.Utility.clsPrintingUtility;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 *
 * @author Harry
 */
public class clsTextFormatForVoidKOTAudit implements clsVoidKOTForAuditGenerationFormat
{
    private DecimalFormat decimalFormat = new DecimalFormat("#.###");
    private SimpleDateFormat ddMMyyyyAMPMDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
    private clsUtility objUtility = new clsUtility();
    private clsUtility2 objUtility2 = new clsUtility2();
    private clsPrintingUtility objPrintingUtility = new clsPrintingUtility();
    private DecimalFormat stdDecimalFormat = new DecimalFormat("######.##");
    private DecimalFormat decimalFormatFor2DecPoint = new DecimalFormat("0.00");
    private DecimalFormat decimalFormatFor3DecPoint = new DecimalFormat("0.000");
    private final String dashedLineFor40Chars = "  --------------------------------------";

    /**
     *
     * @param KOT_TableNo
     * @param KotNo
     * @param text
     * @param costCenterCode
     * @param mapVoidedItem
     */
    @Override
    public void funGenerateVoidKOT(String KOT_TableNo, String KotNo, String text, String costCenterCode, HashMap<String, String> mapVoidedItem)
    {
        String sqlVOIDKOT_waiterName = "";

        DecimalFormat decimalFormat = new DecimalFormat("#.###");

        try
        {

            objPrintingUtility.funCreateTempFolder();
            String filePath = System.getProperty("user.dir");
            File Text_KOT = new File(filePath + "/Temp/Temp_KOT.txt");
            FileWriter fstream = new FileWriter(Text_KOT);
            BufferedWriter KotOut = new BufferedWriter(fstream);

            KotOut.newLine();
            KotOut.newLine();
            objPrintingUtility.funPrintBlankSpace("VOID KOT", KotOut);
            KotOut.write("VOID KOT");
            KotOut.newLine();

            objPrintingUtility.funPrintBlankSpace("KOT", KotOut);
            KotOut.write("KOT");

            //item will pickup from tblvoidkot
            String sqlVOIDKOT_Items = "select a.dblItemQuantity,a.strItemName"
                    + ",c.strCostCenterCode,c.strPrinterPort,a.strItemCode,c.strSecondaryPrinterPort,a.strVoidBillType "
                    + "from tblvoidkot a,tblmenuitempricingdtl b,tblcostcentermaster c "
                    + "where left(a.strItemCode,7)=b.strItemCode and b.strCostCenterCode=c.strCostCenterCode "
                    + "and a.strKOTNo='" + KotNo + "' ";
            if (!text.equals("Reprint"))
            {
                sqlVOIDKOT_Items += " and a.strPrintKOT='N' ";
            }
            sqlVOIDKOT_Items += " and b.strCostCenterCode='" + costCenterCode + "' group by a.strItemName";

            ResultSet rs_VOIDKOT_Items = clsGlobalVarClass.dbMysql.executeResultSet(sqlVOIDKOT_Items);
            String primaryPrinterName = "", secondaryPrinterName = "";
            rs_VOIDKOT_Items.next();
            String voidBillType = rs_VOIDKOT_Items.getString(7);
            KotOut.newLine();
            objPrintingUtility.funPrintBlankSpace(voidBillType, KotOut);
            KotOut.write(voidBillType);
            
            KotOut.newLine();
            KotOut.write(dashedLineFor40Chars);
            KotOut.newLine();
            KotOut.write("  KOT NO        :");
            KotOut.write("  " + KotNo + "  ");
            KotOut.newLine();
            KotOut.write("  TABLE NAME    :");
            KotOut.write("  " + KOT_TableNo + "     ");

            KotOut.newLine();
            String waiterNo = "select strWaiterNo from tblvoidkot where strKOTNo='" + KotNo + "'";
            ResultSet rsWaiterNo = clsGlobalVarClass.dbMysql.executeResultSet(waiterNo);
            if (rsWaiterNo.next())
            {
                if (!"null".equalsIgnoreCase(rsWaiterNo.getString(1)) && rsWaiterNo.getString(1).trim().length() > 0)
                {
                    sqlVOIDKOT_waiterName = "select strWShortName from tblwaitermaster where strWaiterNo='" + rsWaiterNo.getString(1) + "'";
                    ResultSet rs_waiterName = clsGlobalVarClass.dbMysql.executeResultSet(sqlVOIDKOT_waiterName);
                    if (rs_waiterName.next())
                    {
                        KotOut.write("  WAITER NAME   :" + "  " + rs_waiterName.getString(1));
                        KotOut.newLine();
                    }
                    rs_waiterName.close();
                }
            }
            rsWaiterNo.close();

            //Added by Jaichandra
            sqlVOIDKOT_waiterName = "select date(dteDateCreated),time(dteDateCreated) from tblvoidkot where strKOTNo='" + KotNo + "'";
            ResultSet rs_Date = clsGlobalVarClass.dbMysql.executeResultSet(sqlVOIDKOT_waiterName);
            if (rs_Date.next())
            {
                KotOut.write("  DATE & TIME   :" + " " + rs_Date.getString(1) + " " + rs_Date.getString(2));
            }
            rs_Date.close();
            KotOut.newLine();

            String sqlVOIDKOT_reasonName = "select b.strReasonName "
                    + "from tblvoidkot a,tblreasonmaster b "
                    + "where a.strReasonCode=b.strReasonCode "
                    + "and a.strKOTNo='" + KotNo + "' "
                    + "group by a.strKOTNo";
            rs_Date = clsGlobalVarClass.dbMysql.executeResultSet(sqlVOIDKOT_reasonName);
            if (rs_Date.next())
            {
                KotOut.write("  Reason        :" + " " + rs_Date.getString(1));
            }
            rs_Date.close();
            KotOut.newLine();

            KotOut.write(dashedLineFor40Chars);
            KotOut.newLine();
            KotOut.write("  QTY         ITEM NAME  ");
            KotOut.newLine();
            KotOut.write(dashedLineFor40Chars);

            int qtyWidth = 6;
            sqlVOIDKOT_Items = "select a.dblItemQuantity,a.strItemName"
                + ",c.strCostCenterCode,c.strPrinterPort,a.strItemCode,c.strSecondaryPrinterPort,a.strVoidBillType "
                + "from tblvoidkot a,tblmenuitempricingdtl b,tblcostcentermaster c "
                + "where left(a.strItemCode,7)=b.strItemCode and b.strCostCenterCode=c.strCostCenterCode "
                + "and a.strKOTNo='" + KotNo + "' ";
            if (!text.equals("Reprint"))
            {
                sqlVOIDKOT_Items += " and a.strPrintKOT='N' ";
            }
            sqlVOIDKOT_Items += " and b.strCostCenterCode='" + costCenterCode + "' group by a.strItemName";

            ResultSet rs_VOIDKOT_ItemsDtl = clsGlobalVarClass.dbMysql.executeResultSet(sqlVOIDKOT_Items);
            while (rs_VOIDKOT_ItemsDtl.next())
            {
                KotOut.newLine();
                String itemqty = decimalFormat.format(rs_VOIDKOT_ItemsDtl.getDouble(1));

                primaryPrinterName = rs_VOIDKOT_ItemsDtl.getString(4);
                secondaryPrinterName = rs_VOIDKOT_ItemsDtl.getString(6);

                KotOut.write("   " + String.format("%-" + qtyWidth + "s", itemqty) + "       " + rs_VOIDKOT_ItemsDtl.getString(2).toUpperCase());

            }
            rs_VOIDKOT_ItemsDtl.close();
            KotOut.newLine();
            KotOut.write(dashedLineFor40Chars);
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
                KotOut.write("m");//windows
            }
            // KotOut.write("m");
            KotOut.close();
            fstream.close();

            if (clsGlobalVarClass.gShowBill)
            {
                objPrintingUtility.funShowTextFile(Text_KOT, "", "Printer Info!2");
            }
            objPrintingUtility.funPrintToPrinter(primaryPrinterName, secondaryPrinterName, "kot", "N", false);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
