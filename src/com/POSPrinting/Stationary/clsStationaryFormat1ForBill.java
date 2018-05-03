/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting.Stationary;

import com.POSPrinting.Utility.clsPrintingUtility;
import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsPosConfigFile;
import com.POSGlobal.controller.clsUtility;
import com.POSGlobal.controller.clsUtility2;
import com.POSPrinting.Interfaces.clsBillGenerationFormat;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author Ajim
 * @date Aug 26, 2017
 */
public class clsStationaryFormat1ForBill implements clsBillGenerationFormat
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
        int lineCount = 0;
        clsUtility objUtility = new clsUtility();
        clsUtility2 objuUtility2 = new clsUtility2();
        String Linefor5 = "--------------------------------------";
        try
        {
            String user = "";
            String billhd = null;
            String billdtl = null;
            String billModifierdtl = null;
            String billSettlementdtl = null;
            String billtaxdtl = null;
            String billDscFrom = null;
            String billPromoDtl = null;
            String advBookBillHd = null;
            String advBookBillDtl = null;
            String advBookBillCharDtl = null;
            String advReceiptHd = null;
            if (clsGlobalVarClass.gHOPOSType.equalsIgnoreCase("HOPOS"))
            {
                billhd = "tblqbillhd";
                billdtl = "tblqbilldtl";
                billModifierdtl = "tblqbillmodifierdtl";
                billSettlementdtl = "tblqbillsettlementdtl";
                billtaxdtl = "tblqbilltaxdtl";
                billDscFrom = "tblqbilldiscdtl";
                billPromoDtl = "tblqbillpromotiondtl";

                advBookBillHd = "tblqadvbookbillhd";
                advBookBillDtl = "tblqadvbookbilldtl";
                advBookBillCharDtl = "tblqadvbookbillchardtl";
                advReceiptHd = "tblqadvancereceipthd";
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
                    advBookBillHd = "tbladvbookbillhd";
                    advBookBillDtl = "tbladvbookbilldtl";
                    advBookBillCharDtl = "tbladvbookbillchardtl";
                    advReceiptHd = "tbladvancereceipthd";
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
                        advBookBillHd = "tblqadvbookbillhd";
                        advBookBillDtl = "tblqadvbookbilldtl";
                        advBookBillCharDtl = "tblqadvbookbillchardtl";
                        advReceiptHd = "tblqadvancereceipthd";
                    }
                    String sql = "select count(strBillNo) from tblbillhd where strBillNo='" + billNo + "' and strposCode='" + posCode + "' ";
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
                        advBookBillHd = "tblqadvbookbillhd";
                        advBookBillDtl = "tblqadvbookbilldtl";
                        advBookBillCharDtl = "tblqadvbookbillchardtl";
                        advReceiptHd = "tblqadvancereceipthd";
                    }
                }
                else
                {
                    billhd = "tblbillhd";
                    billdtl = "tblbilldtl";
                    billModifierdtl = "tblbillmodifierdtl";
                    billSettlementdtl = "tblbillsettlementdtl";
                    billtaxdtl = "tblbilltaxdtl";
                    billDscFrom = "tblbilldiscdtl";
                    billPromoDtl = "tblbillpromotiondtl";
                    advBookBillHd = "tbladvbookbillhd";
                    advBookBillDtl = "tbladvbookbilldtl";
                    advBookBillCharDtl = "tbladvbookbillchardtl";
                    advReceiptHd = "tbladvancereceipthd";
                }
            }
            PreparedStatement pst = null;
            objPrintingUtility.funCreateTempFolder();
            String filePath = System.getProperty("user.dir");
            File Text_Bill = new File(filePath + "/Temp/Temp_Bill.txt");
            String subTotal = "";
            String grandTotal = "";
            String advAmount = "";
            String deliveryCharge = "";
            String customerCode = "";
            String waiterName = "";
            String tblName = "";
            ResultSet rs_BillHD = null;
            boolean flgComplimentaryBill = false;
            StringBuilder sqlBillHeaderDtl = new StringBuilder();
            sqlBillHeaderDtl.append("select ifnull(a.strTableNo,''),ifnull(a.strWaiterNo,''),a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
                    + "ifnull(a.strCustomerCode,''),a.dblGrandTotal,a.dblTaxAmt,ifnull(a.strReasonCode,''),ifnull(a.strRemarks,''),a.strUserCreated "
                    + ",ifnull(dblDeliveryCharges,0.00),ifnull(i.dblAdvDeposite,0.00),a.dblDiscountPer,b.strPOSName,a.intPaxNo "
                    + ",ifnull(c.strTableName,''),ifnull(d.strWShortName,''),ifnull(d.strWFullName,''),ifnull(l.strSettelmentType,''),ifnull(j.strReasonName,'') as voidedReason, "
                    + "ifnull(g.strReasonName,''),ifnull(e.strCustomerName,''),ifnull(a.strAdvBookingNo,''),ifnull(h.strMessage,''),ifnull(h.strShape,''),ifnull(h.strNote,''),ifnull(a.dblTipAmount,0.00) "
                    + ",a.strOperationType,ifnull(a.strTakeAwayRemarks,''),ifnull(e.longMobileNo,'')  "
                    + "from " + billhd + " a "
                    + "left outer join tblposmaster b on a.strposCode=b.strPosCode  "
                    + "left outer join tbltablemaster c on a.strTableNo=c.strTableNo and a.strClientCode=c.strClientCode "
                    + "left outer join tblwaitermaster d on a.strWaiterNo=d.strWaiterNo and a.strClientCode=d.strClientCode "
                    + "left outer join tblcustomermaster e on a.strCustomerCode=e.strCustomerCode and a.strClientCode=e.strClientCode "
                    + "left outer join tbldebitcardmaster f on a.strCardNo=f.strCardNo "
                    + "left outer join tblreasonmaster g on a.strReasonCode=g.strReasonCode "
                    + "left outer join " + advBookBillHd + " h on a.strAdvBookingNo=h.strAdvBookingNo and a.strClientCode=h.strClientCode "
                    + "left outer join " + advReceiptHd + " i on h.strAdvBookingNo=i.strAdvBookingNo and a.strClientCode=i.strClientCode "
                    + "left outer join tblvoidbillhd j on a.strBillNo=j.strBillNo and a.strposCode=j.strPosCode and a.strClientCode=j.strClientCode "
                    + "left outer join " + billSettlementdtl + " k on a.strBillNo=k.strBillNo and a.strClientCode=k.strClientCode "
                    + "left outer join tblsettelmenthd l on k.strSettlementCode=l.strSettelmentCode "
                    + "where a.strBillNo=? and a.strposCode=? "
                    + "group by a.strBillNo; ");
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlBillHeaderDtl.toString());
            pst.setString(1, billNo);
            pst.setString(2, posCode);
            rs_BillHD = pst.executeQuery();
            rs_BillHD.next();
            if (rs_BillHD.getString(21).equals("Complementary"))
            {
                flgComplimentaryBill = true;
            }
            FileWriter fstream_bill = new FileWriter(Text_Bill);
            BufferedWriter BillOut = new BufferedWriter(fstream_bill);
            BillOut.newLine();
            lineCount++;
            boolean isReprint = false;
            if ("reprint".equalsIgnoreCase(reprint))
            {
                isReprint = true;
                objPrintingUtility.funPrintBlankSpace("[DUPLICATE]", BillOut);
                BillOut.write("[DUPLICATE]");
                BillOut.newLine();
                lineCount++;
            }
            if (transType.equals("Void"))
            {
                objPrintingUtility.funPrintBlankSpace("VOIDED BILL", BillOut);
                BillOut.write("VOIDED BILL");
                BillOut.newLine();
                lineCount++;
            }
            boolean flag_isHomeDelvBill = false;
            String SQL_HomeDelivery = "select strBillNo,strCustomerCode,strDPCode,tmeTime,strCustAddressLine1 "
                    + "from tblhomedelivery where strBillNo=? ;";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_HomeDelivery);
            pst.setString(1, billNo);
            ResultSet rs_HomeDelivery = pst.executeQuery();
            if (rs_HomeDelivery.next())
            {
                flag_isHomeDelvBill = true;
                customerCode = rs_HomeDelivery.getString(2);

                if (clsGlobalVarClass.gPrintHomeDeliveryYN)
                {
                    objPrintingUtility.funPrintBlankSpace("HOME DELIVERY", BillOut);
                    BillOut.write("HOME DELIVERY");
                    BillOut.newLine();
                    lineCount++;
                }

                String SQL_CustomerDtl = "";

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
                    BillOut.write("  DELV BOY  :" + strIN);
                    BillOut.newLine();
                    lineCount++;
                    rs_DeliveryBoyDtl.close();
                }
                //BillOut.write(Line);
                // BillOut.newLine();
            }

            rs_HomeDelivery.close();
            //print take away
            int billPrintSize = 4;
            if (rs_BillHD.getString(30).equals("TakeAway"))
            {
                objPrintingUtility.funPrintBlankSpace("Take Away", BillOut);
                BillOut.write("Take Away");
                BillOut.newLine();
                lineCount++;
            }
            if (clsGlobalVarClass.gPrintTaxInvoice.equalsIgnoreCase("Y"))
            {
                objPrintingUtility.funPrintBlankSpace("TAX INVOICE", BillOut);
                BillOut.write("TAX INVOICE");
                BillOut.newLine();
                lineCount++;
            }
// Bill No   Date  Table No     waiter
            //      BillOut.write("     QTY ITEM NAME                  AMT");

            //  5 blank line and then .. Bill No line
            int blankLine = 0;
            if (lineCount < 7)
            {
                for (int i = 0; i < 7 - lineCount; i++)
                {
                    BillOut.newLine();
                    blankLine++;
                }
            }
            lineCount += blankLine;
            BillOut.write(" " + billNo + "   ");
            SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
            BillOut.write(ft.format(rs_BillHD.getObject(3)));
            tblName = rs_BillHD.getString(18);
            BillOut.write("   " + tblName + "   ");
            waiterName = rs_BillHD.getString(19);
            BillOut.write(waiterName);
            BillOut.newLine();
            lineCount++;

            subTotal = rs_BillHD.getString(6);
            grandTotal = rs_BillHD.getString(8);
            user = rs_BillHD.getString(12);
            deliveryCharge = rs_BillHD.getString(13);
            advAmount = rs_BillHD.getString(14);
            //print card available balance

            //print card available balance
            if (transType.equals("Void"))
            {
                BillOut.write("  Reason      :" + " " + rs_BillHD.getString(22));//voided reason
                BillOut.newLine();
                lineCount++;
            }
            else if (flgComplimentaryBill)
            {

                BillOut.write("  Reason      :" + " " + rs_BillHD.getString(23));
                BillOut.newLine();
                lineCount++;
                BillOut.write("  Remark      :" + " " + rs_BillHD.getString(11));
                BillOut.newLine();
                lineCount++;
            }
            if (clsGlobalVarClass.gCMSIntegrationYN)
            {
            }
            if (rs_BillHD.getString(25) != null && rs_BillHD.getString(25).length() > 0)
            {
                if (rs_BillHD.getString(26).length() > 0 || rs_BillHD.getString(27).length() > 0 || rs_BillHD.getString(28).length() > 0)
                {
//                    BillOut.newLine();
//                      objPrintingUtility.funPrintBlankSpace("ORDER DETAIL", BillOut);
//                    BillOut.write("ORDER DETAIL");
//                    BillOut.newLine();
//                    BillOut.write(Linefor5);
//                    BillOut.newLine();
                }
                StringBuilder strValue = new StringBuilder();
                strValue.setLength(0);
                if (rs_BillHD.getString(26).length() > 0)
                {
                    strValue.append(rs_BillHD.getString(26));
                }
                else
                {
                    strValue.append("");
                }
                int strlenMsg = strValue.length();
                strValue.setLength(0);
                int strlenNote = strValue.length();
                if (strlenNote > 0)
                {
                    String note1 = "";
                    if (strlenNote < 27)
                    {
                        note1 = strValue.substring(0, strlenNote);
                        BillOut.write("  NOTE        :" + note1);
                        BillOut.newLine();
                        lineCount++;
                    }
                    else
                    {
                        note1 = strValue.substring(0, 27);
//                        BillOut.write("  NOTE        :" + note1);
//                        BillOut.newLine();
                    }
                    for (int i = 27; i <= strlenNote; i++)
                    {
                        int endNote = 0;
                        endNote = i + 27;
                        if (strlenNote > endNote)
                        {
                            note1 = strValue.substring(i, endNote);
                            i = endNote;
//                            BillOut.write("               " + note1);
//                            BillOut.newLine();
                        }
                        else
                        {
                            note1 = strValue.substring(i, strlenNote);
//                            BillOut.write("               " + note1);
//                            BillOut.newLine();
                            i = strlenNote + 1;
                        }
                    }
                }

            }
// need 3  blank line to print quantity 
            BillOut.write(Linefor5);
            blankLine = 0;
            if (lineCount < 11)
            {
                for (int i = 0; i < 11 - lineCount; i++)
                {
                    BillOut.newLine();
                    blankLine++;
                }
            }
            lineCount += blankLine;
//here  total line count is 11

            // BillOut.write("     QTY ITEM NAME                  AMT");
            BillOut.newLine();
            lineCount++;
            String SQL_BillDtl = "select sum(a.dblQuantity),left(a.strItemName,22) as ItemLine1"
                    + " ,MID(a.strItemName,23,LENGTH(a.strItemName)) as ItemLine2"
                    + " ,sum(a.dblAmount),a.strItemCode,a.strKOTNo "
                    + " from " + billdtl + " a," + billhd + " b "
                    + " where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode and a.strBillNo=? and b.strposCode=?  ";
            if (!clsGlobalVarClass.gPrintTDHItemsInBill)
            {
                SQL_BillDtl += "and a.tdhYN='N' ";
            }
            if (!clsGlobalVarClass.gPrintOpenItemsOnBill)
            {
                SQL_BillDtl += "and a.dblAmount>0 ";
            }

            SQL_BillDtl += " group by a.strItemCode ";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_BillDtl);
            pst.setString(1, billNo);
            pst.setString(2, posCode);
            ResultSet rs_BillDtl = pst.executeQuery();
            while (rs_BillDtl.next())
            {
                double saleQty = Double.parseDouble(rs_BillDtl.getString(1));
                String sqlPromoBills = "select dblQuantity from " + billPromoDtl + " "
                        + " where strBillNo='" + billNo + "' and strItemCode='" + rs_BillDtl.getString(5) + "' "
                        + " and strPromoType='ItemWise' ";
                ResultSet rsPromoItems = clsGlobalVarClass.dbMysql.executeResultSet(sqlPromoBills);
                if (rsPromoItems.next())
                {
                    saleQty -= rsPromoItems.getDouble(1);
                }
                rsPromoItems.close();
                String qty = String.valueOf(decimalFormatFor3DecPoint.format(saleQty));
//                if (qty.contains("."))
//                {
//                    String decVal = qty.substring(qty.length() - 2, qty.length());
//                    if (Double.parseDouble(decVal) == 0)
//                    {
//                        qty = qty.substring(0, qty.length() - 2);
//                    }
//                }
                if (saleQty > 0)
                {
                    objuUtility2.funPrintContentWithSpace("Right", qty, 6, BillOut);//Qty Print
                    BillOut.write(" ");
                    objuUtility2.funPrintContentWithSpace("Left", rs_BillDtl.getString(2), 20, BillOut);//Item Name
                    if (flgComplimentaryBill)
                    {
                        objuUtility2.funPrintContentWithSpace("Right", "0.00", 7, BillOut);//Amount
                    }
                    else
                    {
                        objuUtility2.funPrintContentWithSpace("Right", rs_BillDtl.getString(4), 7, BillOut);//Amount
                    }
                    BillOut.newLine();
                    lineCount++;
                    if (rs_BillDtl.getString(3).trim().length() > 0)
                    {
                        BillOut.write("       " + rs_BillDtl.getString(3));
                        BillOut.newLine();
                        lineCount++;
                    }
                    String sqlModifier = "select count(*) "
                            + "from " + billModifierdtl + " where strBillNo=? and left(strItemCode,7)=? ";
                    if (!clsGlobalVarClass.gPrintZeroAmtModifierOnBill)
                    {
                        sqlModifier += " and  dblAmount !=0.00 ";
                    }
                    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlModifier);
                    pst.setString(1, billNo);
                    pst.setString(2, rs_BillDtl.getString(5));
                    ResultSet rs_count = pst.executeQuery();
                    rs_count.next();
                    int cntRecord = rs_count.getInt(1);
                    rs_count.close();
                    if (cntRecord > 0)
                    {
                        sqlModifier = "select strModifierName,dblQuantity,dblAmount "
                                + " from " + billModifierdtl + " "
                                + " where strBillNo=? and left(strItemCode,7)=? ";
                        if (!clsGlobalVarClass.gPrintZeroAmtModifierOnBill)
                        {
                            sqlModifier += " and  dblAmount !=0.00 ";
                        }
                        pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlModifier);
                        pst.setString(1, billNo);
                        pst.setString(2, rs_BillDtl.getString(5));
                        ResultSet rs_modifierRecord = pst.executeQuery();
                        while (rs_modifierRecord.next())
                        {
                            if (flgComplimentaryBill)
                            {
                                objuUtility2.funWriteToTextformat5(BillOut, "", rs_modifierRecord.getString(1).toUpperCase(), "0.00", "Format5");
                                BillOut.newLine();
                                lineCount++;
                            }
                            else
                            {
                                objuUtility2.funWriteToTextformat5(BillOut, "", rs_modifierRecord.getString(1).toUpperCase(), rs_modifierRecord.getString(3), "Format5");
                                BillOut.newLine();
                                lineCount++;
                            }
                        }
                        rs_modifierRecord.close();
                    }

                    String sql = "select b.strItemCode,b.dblWeight "
                            + " from " + billhd + " a," + advBookBillDtl + " b "
                            + " where a.strAdvBookingNo=b.strAdvBookingNo and a.strClientCode=b.strClientCode "
                            + " and a.strBillNo='" + billNo + "' and b.strItemCode='" + rs_BillDtl.getString(5) + "' "
                            + " and a.strposCode='" + posCode + "' ";
                    ResultSet rsWeight = clsGlobalVarClass.dbMysql.executeResultSet(sql);
                    while (rsWeight.next())
                    {
                        BillOut.write("   Weight");
                        BillOut.write("   " + rsWeight.getDouble(2));
                        BillOut.newLine();
                        lineCount++;
                    }
                    rsWeight.close();
                    sql = "select c.strCharName,b.strCharValues "
                            + " from " + billhd + " a," + advBookBillCharDtl + " b,tblcharactersticsmaster c "
                            + " where a.strAdvBookingNo=b.strAdvBookingNo and b.strCharCode=c.strCharCode "
                            + " and a.strBillNo='" + billNo + "' and b.strItemCode='" + rs_BillDtl.getString(5) + "' "
                            + " and a.strposCode='" + posCode + "' and a.strClientCode=b.strClientCode ";
                    ResultSet rsCharDtl = clsGlobalVarClass.dbMysql.executeResultSet(sql);
                    while (rsCharDtl.next())
                    {
                        String charName = objUtility.funPrintTextWithAlignment(rsCharDtl.getString(1), 10, "Left");
                        BillOut.write("   " + charName);
                        String charVal = objUtility.funPrintTextWithAlignment(rsCharDtl.getString(2), 26, "Left");
                        BillOut.write("   " + charVal);
                        BillOut.newLine();
                        lineCount++;
                    }
                    rsCharDtl.close();
                }

                //      here check no of items in bill.. if items greater than 7 ..then print next items on second page
                if (lineCount > 28)
                {
                    for (int i = 0; i < 19; i++)
                    {
                        BillOut.newLine();
                    }
                    lineCount = 11;
                }
            }
            rs_BillDtl.close();
            objPrintingUtility.funPrintPromoItemsInBill(billNo, BillOut, 4);  // Print Promotion Items in Bill for this billno.
            BillOut.write(Linefor5);
            BillOut.newLine();
            lineCount++;
            // currentLineCount-- current line number, totalLineCount -- total number of line in single page , topSpaceLineCount --no of line from actual item printing start,
            //totalLinesForNextPage -- required blank line to contiouse print on next page ,BufferedWriter out
            lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
            if (clsGlobalVarClass.gPointsOnBillPrint)
            {
                String sqlCRMPoints = "select b.dblPoints from " + billhd + " a, tblcrmpoints b "
                        + " where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
                        + " and a.strBillNo='" + billNo + "' and a.strposCode='" + posCode + "' ";
                ResultSet rsCRMPoints = clsGlobalVarClass.dbMysql.executeResultSet(sqlCRMPoints);
                if (rsCRMPoints.next())
                {
                    objPrintingUtility.funWriteTotalStationery("POINTS ", rsCRMPoints.getString(1), BillOut, "Format5", "");
                }
                rsCRMPoints.close();
                BillOut.newLine();
                lineCount++;
                lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
            }
//1
            if (flgComplimentaryBill)
            {
                objPrintingUtility.funWriteTotalStationery("SUB TOTAL", "0.00", BillOut, "Format5", "");
                BillOut.newLine();
                lineCount++;
                lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
            }
            else
            {
                objPrintingUtility.funWriteTotalStationery("SUB TOTAL", subTotal, BillOut, "Format5", "");
                BillOut.newLine();
                lineCount++;
                lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
            }
            String sql = "select a.dblDiscPer,a.dblDiscAmt,a.strDiscOnType,a.strDiscOnValue,b.strReasonName,a.strDiscRemarks "
                    + " from " + billDscFrom + " a ,tblreasonmaster b," + billhd + " c "
                    + " where  a.strDiscReasonCode=b.strReasonCode and a.strBillNo=c.strBillNo "
                    + " and a.strClientCode=c.strClientCode and a.strBillNo='" + billNo + "' and c.strposCode='" + posCode + "' ";
            ResultSet rsDisc = clsGlobalVarClass.dbMysql.executeResultSet(sql);
            boolean flag = true;
            while (rsDisc.next())
            {
                if (flag)
                {
                    flag = false;
                    BillOut.write("  DISCOUNT");
                    BillOut.newLine();
                    lineCount++;
                    lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
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
                BillOut.write("  " + discText);
                String discountOnItem = objUtility.funPrintTextWithAlignment(rsDisc.getString("dblDiscAmt"), 8, "Right");
                BillOut.write(discountOnItem);
                BillOut.newLine();
                lineCount++;
                lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                BillOut.write("  Reason  : ");
                String discReason = objUtility.funPrintTextWithAlignment(rsDisc.getString(5), 20, "Left");
                BillOut.write(discReason);
                BillOut.newLine();
                lineCount++;
                lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                BillOut.write("  Remarks : ");
                String discRemarks = objUtility.funPrintTextWithAlignment(rsDisc.getString(6), 20, "Left");
                BillOut.write(discRemarks);
                BillOut.newLine();
                lineCount++;
                lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
//5
            }
            String sql_Tax = "select b.strTaxDesc,sum(a.dblTaxAmount) "
                    + " from " + billtaxdtl + " a,tbltaxhd b," + billhd + " c "
                    + " where a.strBillNo='" + billNo + "' "
                    + " and a.strTaxCode=b.strTaxCode "
                    + " and a.strBillNo=c.strBillNo "
                    + " and a.strClientCode=c.strClientCode "
                    + " and c.strposCode='" + posCode + "' "
                    + " and b.strTaxCalculation='Forward' "
                    + " group by a.strTaxCode";
            ResultSet rsTax = clsGlobalVarClass.dbMysql.executeResultSet(sql_Tax);
            while (rsTax.next())
            {
                if (flgComplimentaryBill)
                {
                    objPrintingUtility.funWriteTotal(rsTax.getString(1), "0.00", BillOut, "Format5");
                }
                else
                {
                    objPrintingUtility.funWriteTotalStationery(rsTax.getString(1), rsTax.getString(2), BillOut, "Format5", "");
                }
                BillOut.newLine();
                lineCount++;
                lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
            }
            if (deliveryCharge != null && deliveryCharge.trim().length() > 0 && !"0.00".equalsIgnoreCase(deliveryCharge))
            {
                objPrintingUtility.funWriteTotalStationery("DELV. CHARGE", deliveryCharge, BillOut, "Format5", "");
                BillOut.newLine();
                lineCount++;
                lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
            }
            if (advAmount.trim().length() > 0 && !"0.00".equalsIgnoreCase(advAmount))
            {
                objPrintingUtility.funWriteTotalStationery("ADVANCE", advAmount, BillOut, "Format5", "");
                BillOut.newLine();
                lineCount++;
                lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
            }
            BillOut.write(Linefor5);
            BillOut.newLine();
            lineCount++;
            lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
            if (flgComplimentaryBill)
            {
                objPrintingUtility.funWriteTotalStationery("TOTAL(ROUNDED)", "0.00", BillOut, "Format5", "");
            }
            else
            {
                objPrintingUtility.funWriteTotalStationery("TOTAL(ROUNDED)", grandTotal, BillOut, "Format5", "");
            }
            BillOut.newLine();
            lineCount++;
            lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
            BillOut.write(Linefor5);
//10
            //print Grand total of other bill nos from bill series
            if (clsGlobalVarClass.gEnableBillSeries)
            {
                String sqlPrintGT = "select a.strPrintGTOfOtherBills,b.strDtlBillNos,b.dblGrandTotal "
                        + "from tblbillseries a,tblbillseriesbilldtl b "
                        + "where (a.strposCode=b.strposCode or a.strposCode='All') "
                        + "and a.strBillSeries=b.strBillSeries "
                        + "and b.strHdBillNo='" + billNo + "' and b.strposCode='" + posCode + "' ";
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
                                        + "where a.strHdBillNo='" + dtlBillSeriesBillNo[i] + "' and a.strposCode='" + posCode + "' ";
                                ResultSet rsPrintGT = clsGlobalVarClass.dbMysql.executeResultSet(sqlPrintGT);
                                if (rsPrintGT.next())
                                {
                                    BillOut.newLine();
                                    lineCount++;
                                    lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                                    objPrintingUtility.funWriteTotalStationery(dtlBillSeriesBillNo[i] + " TOTAL(ROUNDED)", rsPrintGT.getString(2), BillOut, "Format5", "");
                                    dblOtherBillsGT += rsPrintGT.getDouble(2);
                                    BillOut.newLine();
                                    lineCount++;
                                    lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                                }
                            }
                            BillOut.write(Linefor5);
                            BillOut.newLine();
                            lineCount++;
                            lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                            objPrintingUtility.funWriteTotalStationery("GRAND TOTAL(ROUNDED)", String.valueOf(dblOtherBillsGT), BillOut, "Format5", "");
                            BillOut.newLine();
                            lineCount++;
                            lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                            BillOut.write(Linefor5);
                            BillOut.newLine();
                            lineCount++;
                            lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                        }
                    }
                }
            }
//15
            //settlement breakup part
            String sqlSettlementBreakup = "select a.dblSettlementAmt, b.strSettelmentDesc, b.strSettelmentType "
                    + " from " + billSettlementdtl + " a ,tblsettelmenthd b," + billhd + " c "
                    + " where a.strBillNo=? and a.strBillNo=c.strBillNo and a.strClientCode=c.strClientCode "
                    + " and a.strSettlementCode=b.strSettelmentCode and c.strposCode=? ";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlSettlementBreakup);
            pst.setString(1, billNo);
            pst.setString(2, posCode);
            boolean flgSettlement = false;
            boolean creditSettlement = false;
            ResultSet rsBillSettlement = pst.executeQuery();
            while (rsBillSettlement.next())
            {
                if (flgComplimentaryBill)
                {
                    BillOut.newLine();
                    objPrintingUtility.funWriteTotalStationery(rsBillSettlement.getString(2), "0.00", BillOut, "Format5", "");
                }
                else
                {
                    BillOut.newLine();
                    objPrintingUtility.funWriteTotalStationery(rsBillSettlement.getString(2), rsBillSettlement.getString(1), BillOut, "Format5", "");
                }
                lineCount++;
                lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                flgSettlement = true;
                if (rsBillSettlement.getString(3).equals("Credit"))
                {
                    creditSettlement = true;
                }
            }
            rsBillSettlement.close();

            if (flgSettlement)
            {
                if (creditSettlement)
                {
                    BillOut.newLine();
                    lineCount++;
                    lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                    objPrintingUtility.funWriteTotalStationery("Credit Remarks ", rs_BillHD.getString(11), BillOut, "Format5", "");
                    BillOut.newLine();
                    lineCount++;
                    lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                    String custName = rs_BillHD.getString(24);
                    if (!custName.isEmpty())
                    {
                        objPrintingUtility.funWriteTotalStationery("Customer " + custName, "", BillOut, "Format5", "");
                    }
                    BillOut.newLine();
                    lineCount++;
                    lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                    BillOut.write(Linefor5);
                }
            }
//19
            String sqlTenderAmt = "select sum(a.dblPaidAmt),sum(a.dblSettlementAmt),(sum(a.dblPaidAmt)-sum(a.dblSettlementAmt)) RefundAmt "
                    + " from " + billSettlementdtl + " a," + billhd + " b "
                    + " where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
                    + " and b.strBillNo='" + billNo + "' and b.strposCode='" + posCode + "' "
                    + " group by a.strBillNo";
            ResultSet rsTenderAmt = clsGlobalVarClass.dbMysql.executeResultSet(sqlTenderAmt);
            if (rsTenderAmt.next())
            {
                BillOut.newLine();
                lineCount++;
                lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                if (flgComplimentaryBill)
                {
                    objPrintingUtility.funWriteTotalStationery("PAID AMT", "0.00", BillOut, "Format5", "");
                    //BillOut.newLine();
                }
                else
                {
                    objPrintingUtility.funWriteTotalStationery("PAID AMT", rsTenderAmt.getString(1), BillOut, "Format5", "");
                    BillOut.newLine();
                    lineCount++;
                    lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                    if (rsTenderAmt.getDouble(3) > 0)
                    {
                        objPrintingUtility.funWriteTotalStationery("REFUND AMT", rsTenderAmt.getString(3), BillOut, "Format5", "");
                        //BillOut.newLine();
                    }
                }
                BillOut.newLine();
                lineCount++;
                lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                BillOut.write(Linefor5);
            }
            rsTenderAmt.close();

            if (rs_BillHD.getDouble(29) > 0)
            {
                BillOut.newLine();
                lineCount++;
                lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                objPrintingUtility.funWriteTotalStationery("TIP AMT", rs_BillHD.getString(29), BillOut, "Format5", "");
                //BillOut.newLine();
            }
            if (flag_isHomeDelvBill)
            {
                BillOut.newLine();
                lineCount++;
                lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                String sql_count = "select count(*) from tblhomedelivery where strCustomerCode=?";
                pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_count);
                pst.setString(1, customerCode);
                ResultSet rs_Count = pst.executeQuery();
                rs_Count.next();
                BillOut.write("CUSTOMER COUNT : " + rs_Count.getString(1));
                rs_Count.close();
                BillOut.newLine();
                lineCount++;
                lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                BillOut.write(Linefor5);
            }
            BillOut.newLine();
            lineCount++;
            lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);

            objPrintingUtility.funPrintServiceVatNo(BillOut, 4, billNo, billDate, billtaxdtl);

            if (clsGlobalVarClass.gEnableBillSeries)
            {
                sql = "select b.strPrintInclusiveOfTaxOnBill "
                        + " from tblbillseriesbilldtl a,tblbillseries b "
                        + " where a.strBillSeries=b.strBillSeries and a.strHdBillNo='" + billNo + "' and a.strClientCode=b.strClientCode";
                ResultSet rsBillSeries = clsGlobalVarClass.dbMysql.executeResultSet(sql);
                if (rsBillSeries.next())
                {
                    if (rsBillSeries.getString(1).equals("Y"))
                    {
                        BillOut.write(Linefor5);
                        BillOut.newLine();
                        lineCount++;
                        lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                        objPrintingUtility.funPrintBlankSpace("(INCLUSIVE OF ALL TAXES)", BillOut);
                        BillOut.write("(INCLUSIVE OF ALL TAXES)");
                        BillOut.newLine();
                        lineCount++;
                        lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                    }
                }
                rsBillSeries.close();
            }
            else
            {
                if (clsGlobalVarClass.gPrintInclusiveOfAllTaxes.equalsIgnoreCase("Y"))
                {
                    BillOut.write(Linefor5);
                    BillOut.newLine();
                    lineCount++;
                    lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                    objPrintingUtility.funPrintBlankSpace("(INCLUSIVE OF ALL TAXES)", BillOut);
                    BillOut.write("(INCLUSIVE OF ALL TAXES)");
                    BillOut.newLine();
                    lineCount++;
                    lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                }
            }

            int num = clsGlobalVarClass.gBillFooter.trim().length() / 30;
            int num1 = clsGlobalVarClass.gBillFooter.trim().length() % 30;
            int cnt1 = 0;
            for (int cnt = 0; cnt < num; cnt++)
            {
                String footer = clsGlobalVarClass.gBillFooter.trim().substring(cnt1, (cnt1 + 30));
                footer = footer.replaceAll("\n", "");
                BillOut.write("   " + footer.trim());
                BillOut.newLine();
                lineCount++;
                lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
                cnt1 += 30;
            }
            BillOut.write("   " + clsGlobalVarClass.gBillFooter.trim().substring(cnt1, (cnt1 + num1)).trim());
            BillOut.newLine();
            lineCount++;
            lineCount = objPrintingUtility.funWriteBlankLines(lineCount, 28, 11, 19, BillOut);
            objPrintingUtility.funPrintBlankSpace(user, BillOut);
            BillOut.write(user);
            if (lineCount < 29)
            {
                for (int i = 0; i < 29 - lineCount; i++)
                {
                    BillOut.newLine();
                }
            }
            if (!clsGlobalVarClass.gOpenCashDrawerAfterBillPrintYN)
            {
                if ("linux".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
                {
                    BillOut.write("V");//Linux
                }
                else if ("windows".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
                {
                    if ("Inbuild".equalsIgnoreCase(clsPosConfigFile.gPrinterType))
                    {
                        BillOut.write("V");
                    }
                    else
                    {
                        BillOut.write("m");//windows
                    }
                }
            }
            rs_BillHD.close();
            BillOut.close();
            fstream_bill.close();
            pst.close();

            if (formName.equalsIgnoreCase("sales report"))
            {
                objPrintingUtility.funShowTextFile(Text_Bill, formName, clsGlobalVarClass.gBillPrintPrinterPort);
            }
            else
            {
                if (clsGlobalVarClass.gShowBill)
                {
                    objPrintingUtility.funShowTextFile(Text_Bill, formName, clsGlobalVarClass.gBillPrintPrinterPort);
                }
            }

            if (!formName.equalsIgnoreCase("sales report"))
            {
                if (transType.equalsIgnoreCase("void"))
                {
                    if (clsGlobalVarClass.gPrintOnVoidBill)
                    {
                        if (!viewORprint.equalsIgnoreCase("view"))
                        {
                            objPrintingUtility.funPrintToPrinter(clsGlobalVarClass.gBillPrintPrinterPort, "", "bill", "N", isReprint);
                        }
                    }
                }
                else
                {
                    if (!clsGlobalVarClass.flgReprintView)
                    {
                        if (!viewORprint.equalsIgnoreCase("view"))
                        {
                            objPrintingUtility.funPrintToPrinter(clsGlobalVarClass.gBillPrintPrinterPort, "", "bill", "N", isReprint);
                        }
                    }
                    else
                    {
                        clsGlobalVarClass.flgReprintView = false;
                    }
                }
            }
            //if (formName.equalsIgnoreCase("sales report"))

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
