/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting.Text.AdvReceipt;

import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsPosConfigFile;
import com.POSGlobal.controller.clsUtility;
import com.POSGlobal.controller.clsUtility2;
import com.POSPrinting.Utility.clsPrintingUtility;
import com.POSPrinting.Interfaces.clsAdvReceiptGenerationFormat;
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
 * @date Aug 28, 2017
 */
public class clsTextFormat1ForAdvReceipt implements clsAdvReceiptGenerationFormat
{

    private DecimalFormat decimalFormat = new DecimalFormat("#.###");
    private SimpleDateFormat ddMMyyyyAMPMDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
    private clsUtility objUtility = new clsUtility();
    private clsUtility2 objUtility2 = new clsUtility2();
    private clsPrintingUtility objPrintingUtility = new clsPrintingUtility();
    private final String dashedLineFor40Chars = "  --------------------------------------";

    /**
     *
     * @param advBookNo
     * @param receiptNo
     * @param posCode
     * @param Reprint
     * @param custName
     * @param orderDate
     * @param waiterName
     * @param formName
     */
    @Override
    public void funGenerateAdvReceipt(String advBookNo, String receiptNo, String posCode, String Reprint, String custName, String orderDate, String waiterName, String formName)
    {
        try
        {
            String advBookingHd = "tbladvbookbillhd";
            String advBookingDtl = "tbladvbookbilldtl";
            String advBookingModDtl = "tbladvordermodifierdtl";
            String advBookingCharDtl = "tbladvbookbillchardtl";
            String advReceiptHd = "tbladvancereceipthd";
            String advReceiptDtl = "tbladvancereceiptdtl";

            String sql = "select strAdvBookingNo from tblqadvbookbillhd "
                    + " where strAdvBookingNo='" + advBookNo + "' and strClientCode='" + clsGlobalVarClass.gClientCode + "' ";
            ResultSet rsAdvOrder = clsGlobalVarClass.dbMysql.executeResultSet(sql);
            if (rsAdvOrder.next())
            {
                advBookingHd = "tblqadvbookbillhd";
                advBookingDtl = "tblqadvbookbilldtl";
                advBookingModDtl = "tblqadvordermodifierdtl";
                advBookingCharDtl = "tblqadvbookbillchardtl";
                advReceiptHd = "tblqadvancereceipthd";
                advReceiptDtl = "tblqadvancereceiptdtl";
            }
            rsAdvOrder.close();

            PreparedStatement pst = null;
            objPrintingUtility.funCreateTempFolder();
            String filePath = System.getProperty("user.dir");
            File Text_Bill = new File(filePath + "/Temp/Temp_Bill.txt");
            FileWriter fstream_bill = new FileWriter(Text_Bill);
            BufferedWriter advOut = new BufferedWriter(fstream_bill);
            boolean isReprint = false;
            if ("Reprint".equalsIgnoreCase(Reprint))
            {
                isReprint = true;
                objPrintingUtility.funPrintBlankSpace("[DUPLICATE]", advOut);
                advOut.write("[DUPLICATE]");
                advOut.newLine();
            }
            objPrintingUtility.funPrintBlankSpace("ADVANCE RECEIPT", advOut);
            advOut.write("ADVANCE RECEIPT");
            advOut.newLine();
            if (clsGlobalVarClass.gClientCode.equals("092.001") || clsGlobalVarClass.gClientCode.equals("092.002") || clsGlobalVarClass.gClientCode.equals("092.003"))//Shree Sound Pvt. Ltd.
            {
                objPrintingUtility.funPrintBlankSpace("SSPL", advOut);
                advOut.write("SSPL");
                advOut.newLine();
            }
            else
            {
                objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gClientName, advOut);
                advOut.write(clsGlobalVarClass.gClientName.toUpperCase());
                advOut.newLine();
            }
            objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gClientAddress1, advOut);
            advOut.write(clsGlobalVarClass.gClientAddress1.toUpperCase());
            advOut.newLine();

            if (clsGlobalVarClass.gClientAddress2.trim().length() > 0)
            {
                objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gClientAddress2, advOut);
                advOut.write(clsGlobalVarClass.gClientAddress2.toUpperCase());
                advOut.newLine();
            }

            if (clsGlobalVarClass.gClientAddress3.trim().length() > 0)
            {
                objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gClientAddress3, advOut);
                advOut.write(clsGlobalVarClass.gClientAddress3.toUpperCase());
                advOut.newLine();
            }

            if (clsGlobalVarClass.gCityName.trim().length() > 0)
            {
                objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gCityName, advOut);
                advOut.write(clsGlobalVarClass.gCityName.toUpperCase());
                advOut.newLine();
            }

            advOut.write("  TEL NO.   :" + " ");
            advOut.write(String.valueOf(clsGlobalVarClass.gClientTelNo));
            advOut.newLine();
            advOut.write("  EMAIL ID  :" + " ");
            advOut.write(clsGlobalVarClass.gClientEmail);
            advOut.newLine();
            advOut.write(dashedLineFor40Chars);
            advOut.newLine();

            String sql_advOrder_SuTtotal = "select a.dblSubTotal,a.dblTaxAmt,b.dblAdvDeposite"
                    + ",a.strMessage,a.strShape,a.strNote,a.dblHomeDelCharges,a.strManualAdvOrderNo "
                    + "from " + advBookingHd + " a ," + advReceiptHd + " b "
                    + "where a.strAdvBookingNo=b.strAdvBookingNo and a.strAdvBookingNo=?";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_advOrder_SuTtotal);
            pst.setString(1, advBookNo);
            ResultSet rs_advOrder_Tot = pst.executeQuery();
            rs_advOrder_Tot.next();
            advOut.write("  ADV ORD NO  :" + advBookNo);
            advOut.newLine();
            if (clsGlobalVarClass.gPrintManualAdvOrderNoOnBill)
            {
                if (rs_advOrder_Tot.getString(8).trim().length() > 0)
                {
                    advOut.write("  MANUAL NO  :" + rs_advOrder_Tot.getString(8));
                    advOut.newLine();
                }
            }

            String sql_settle = "select DATE(b.dteInstallment) "
                    + " from " + advReceiptHd + " a," + advReceiptDtl + " b,tblsettelmenthd c," + advBookingHd + " d "
                    + " where a.strReceiptNo=b.strReceiptNo and b.strSettlementCode=c.strSettelmentCode "
                    + " and a.strAdvBookingNo=? and d.strAdvBookingNo=a.strAdvBookingNo ";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_settle);
            pst.setString(1, advBookNo);
            ResultSet rsAdvOrderDate = pst.executeQuery();
            if (rsAdvOrderDate.next())
            {
                advOut.write("  ORDER DATE    :" + objUtility.funGetDateInFormat(rsAdvOrderDate.getString(1), "dd-MM-yyyy"));
                advOut.newLine();
            }
            rsAdvOrderDate.close();

            String orderDateTime = objUtility.funGetDateInFormat(orderDate.split(" ")[0], "dd-MM-yyyy");
            orderDateTime += " " + orderDate.split(" ")[1];
            advOut.write("  ORDER FOR   :" + orderDateTime);
            advOut.newLine();
            if (waiterName.trim().length() > 0)
            {
                advOut.write("  ORDER BY  :" + waiterName.toUpperCase());
                advOut.newLine();
            }
            advOut.write("  CUST NAME   :" + custName.toUpperCase());
            advOut.newLine();

            sql = "select b.longMobileNo "
                    + " from " + advBookingHd + " a,tblcustomermaster b "
                    + " where a.strCustomerCode=b.strCustomerCode and a.strAdvBookingNo='" + advBookNo + "'";
            ResultSet rsCustMbNo = clsGlobalVarClass.dbMysql.executeResultSet(sql);
            if (rsCustMbNo.next())
            {
                advOut.write("  Mobile No   :" + rsCustMbNo.getString(1));
                advOut.newLine();
            }
            rsCustMbNo.close();

            advOut.write(dashedLineFor40Chars);
            advOut.newLine();
            objPrintingUtility.funPrintBlankSpace("ORDER DETAIL", advOut);
            advOut.write("ORDER DETAIL");
            advOut.newLine();
            advOut.write(dashedLineFor40Chars);
            advOut.newLine();
            String msg = rs_advOrder_Tot.getString(4).toUpperCase();
            String shape = rs_advOrder_Tot.getString(5).toUpperCase();
            String note = rs_advOrder_Tot.getString(6).toUpperCase();

            String[] arrSpMsg = msg.split("}");
            for (int cnt = 0; cnt < arrSpMsg.length; cnt++)
            {
                msg = arrSpMsg[cnt];
                int strlenMsg = msg.length();
                if (strlenMsg > 0)
                {
                    String msg1 = "";
                    if (strlenMsg < 27)
                    {
                        msg1 = msg.substring(0, strlenMsg);
                        advOut.write("  MESSAGE " + (cnt + 1) + "    :" + msg1);
                        advOut.newLine();
                    }
                    else
                    {
                        msg1 = msg.substring(0, 27);
                        //BillOut.write("  MESSAGE     :"+msg1);;
                        advOut.write("  MESSAGE " + (cnt + 1) + "    :" + msg1);;
                        advOut.newLine();
                    }
                    for (int i = 27; i <= strlenMsg; i++)
                    {
                        int endmsg = 0;
                        endmsg = i + 27;
                        if (strlenMsg > endmsg)
                        {
                            msg1 = msg.substring(i, endmsg);
                            i = endmsg;
                            advOut.write("               " + msg1);
                            advOut.newLine();
                        }
                        else
                        {
                            msg1 = msg.substring(i, strlenMsg);
                            advOut.write("               " + msg1);
                            advOut.newLine();
                            i = strlenMsg + 1;
                        }
                    }
                }
            }

            String[] arrSpShape = shape.split("}");
            for (int cnt = 0; cnt < arrSpShape.length; cnt++)
            {
                shape = arrSpShape[cnt];
                int strlenShape = shape.length();
                if (strlenShape > 0)
                {
                    String shape1 = "";
                    if (strlenShape < 27)
                    {
                        shape1 = shape.substring(0, strlenShape);
                        advOut.write("  SHAPE " + (cnt + 1) + "      :" + shape1);
                        advOut.newLine();
                    }
                    else
                    {
                        shape1 = shape.substring(0, 27);
                        advOut.write("  SHAPE " + (cnt + 1) + "      :" + shape1);
                        advOut.newLine();
                    }
                    for (int j = 27; j <= strlenShape; j++)
                    {
                        int endShape = 0;
                        endShape = j + 27;
                        if (strlenShape > endShape)
                        {
                            shape1 = shape.substring(j, endShape);
                            j = endShape;
                            advOut.write("               " + shape1);
                            advOut.newLine();
                        }
                        else
                        {
                            shape1 = shape.substring(j, strlenShape);
                            advOut.write("               " + shape1);
                            advOut.newLine();
                            j = strlenShape + 1;
                        }
                    }
                }
            }
            int strlenNote = note.length();
            if (strlenNote > 0)
            {

                String note1 = "";
                if (strlenNote < 27)
                {
                    note1 = note.substring(0, strlenNote);
                    advOut.write("  NOTE        :" + note1);
                    advOut.newLine();
                }
                else
                {
                    note1 = note.substring(0, 27);
                    advOut.write("  NOTE        :" + note1);;
                    advOut.newLine();
                }
                for (int i = 27; i <= strlenNote; i++)
                {
                    int endNote = 0;
                    endNote = i + 27;
                    if (strlenNote > endNote)
                    {
                        note1 = note.substring(i, endNote);
                        i = endNote;
                        advOut.write("               " + note1);
                        advOut.newLine();
                    }
                    else
                    {
                        note1 = note.substring(i, strlenNote);
                        advOut.write("               " + note1);
                        advOut.newLine();
                        i = strlenNote + 1;
                    }
                }
            }

            advOut.newLine();
            advOut.write(dashedLineFor40Chars);
            advOut.newLine();
            advOut.write("   QTY        ITEM NAME            AMT");
            advOut.newLine();
            advOut.write(dashedLineFor40Chars);
            advOut.newLine();

            String SQL_AdvOrderDtl = "select SUM(dblQuantity),strItemName,SUM(dblAmount),strItemCode,dblWeight "
                    + " from " + advBookingDtl + " where strAdvBookingNo=? group by strItemCode ;";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_AdvOrderDtl);
            pst.setString(1, advBookNo);
            ResultSet rs_AdvOrderDtl = pst.executeQuery();
            while (rs_AdvOrderDtl.next())
            {
                double weight = rs_AdvOrderDtl.getDouble(5);
                String qty = rs_AdvOrderDtl.getString(1);
                if (qty.contains("."))
                {
                    String decVal = qty.substring(qty.length() - 2, qty.length());
                    if (Double.parseDouble(decVal) == 0)
                    {
                        qty = qty.substring(0, qty.length() - 3);
                    }
                }

                //objPrintingUtility.funWriteToText(advOut, rs_AdvOrderDtl.getString(1), rs_AdvOrderDtl.getString(2).toUpperCase(), rs_AdvOrderDtl.getString(3), "Format1");
                qty = objUtility.funPrintTextWithAlignment(qty, 5, "Left");
                advOut.write("   " + qty);

                String itemName = objUtility.funPrintTextWithAlignment(rs_AdvOrderDtl.getString(2), 26, "Left");
                advOut.write(itemName.toUpperCase());
                String itemAmt = objUtility.funPrintTextWithAlignment(rs_AdvOrderDtl.getString(3), 8, "Left");
                advOut.write(itemAmt);
                advOut.newLine();
                String SQL_ModifierDtl_count = "select Count(*) from " + advBookingModDtl + " "
                        + "where strAdvOrderNo=? and strItemCode=? ";
                if (!clsGlobalVarClass.gPrintZeroAmtModifierOnBill)
                {
                    SQL_ModifierDtl_count += " and dblAmount !=0.00 ;";
                }
                pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_ModifierDtl_count);
                pst.setString(1, advBookNo);
                pst.setString(2, rs_AdvOrderDtl.getString(4));
                ResultSet rs_count = pst.executeQuery();
                rs_count.next();
                int cntRecord = rs_count.getInt(1);
                rs_count.close();
                if (cntRecord > 0)
                {
                    String SQL_ModifierDtl = "select strModifierName,dblQuantity,dblAmount from " + advBookingModDtl + " "
                            + "where strAdvOrderNo=? and strItemCode=? ";
                    if (!clsGlobalVarClass.gPrintZeroAmtModifierOnBill)
                    {
                        SQL_ModifierDtl += " and dblAmount !=0.00 ;";
                    }
                    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_ModifierDtl);
                    pst.setString(1, advBookNo);
                    pst.setString(2, rs_AdvOrderDtl.getString(4));
                    ResultSet rs_modifierRecord = pst.executeQuery();
                    while (rs_modifierRecord.next())
                    {
                        objPrintingUtility.funWriteToText(advOut, rs_modifierRecord.getString(2), rs_modifierRecord.getString(1).toUpperCase(), rs_modifierRecord.getString(3), "Format1");
                        advOut.newLine();
                    }
                    rs_modifierRecord.close();
                }

                if (weight > 0)
                {
                    advOut.write("     Weight");
                    advOut.write("     " + weight);
                    advOut.newLine();
                }

                sql = "select b.strCharName,a.strCharValues,a.strItemCode "
                        + " from " + advBookingCharDtl + " a,tblcharactersticsmaster b "
                        + " where a.strCharCode=b.strCharCode and a.strAdvBookingNo='" + advBookNo + "' "
                        + " and a.strItemCode='" + rs_AdvOrderDtl.getString(4) + "' ";
                ResultSet rsCharDtl = clsGlobalVarClass.dbMysql.executeResultSet(sql);
                while (rsCharDtl.next())
                {
                    String charName = objUtility.funPrintTextWithAlignment(rsCharDtl.getString(1), 12, "Left");
                    advOut.write("     " + charName);
                    String charVal = objUtility.funPrintTextWithAlignment(rsCharDtl.getString(2), 28, "Left");
                    advOut.write("     " + charVal);
                    advOut.newLine();
                }
                rsCharDtl.close();
            }
            rs_AdvOrderDtl.close();
            advOut.write(dashedLineFor40Chars);
            advOut.newLine();

            String subTotal = rs_advOrder_Tot.getString(1);
            String tax = rs_advOrder_Tot.getString(2);
            String advAmt = rs_advOrder_Tot.getString(3);
            String delCharges = rs_advOrder_Tot.getString(7);
            double bal = (Double.parseDouble(subTotal) + Double.parseDouble(tax) + Double.parseDouble(delCharges)) - Double.parseDouble(advAmt);

            objPrintingUtility.funWriteTotal("SUB TOTAL", subTotal, advOut, "Format1");
            advOut.newLine();
            objPrintingUtility.funWriteTotal("TAX", tax, advOut, "Format1");
            double subTotalWithDelCharges = Double.parseDouble(subTotal) + Double.parseDouble(delCharges);
//            if (subTotalWithDelCharges < Double.parseDouble(advAmt))
//            {
//                advAmt = subTotal;
//            }
            advOut.newLine();

            if (Double.parseDouble(delCharges) > 0)
            {
                objPrintingUtility.funWriteTotal("DEL CHARGES", delCharges, advOut, "Format1");
                advOut.newLine();
            }
            objPrintingUtility.funWriteTotal("ADVANCE AMOUNT", advAmt, advOut, "Format1");
            advOut.newLine();

            advOut.write(dashedLineFor40Chars);
            advOut.newLine();
            if (bal <= 0)
            {
                bal = 0.00;
            }

            objPrintingUtility.funWriteTotal("BALANCE", String.valueOf(bal).concat("0"), advOut, "Format1");
            advOut.newLine();

            //footer part
            advOut.write(dashedLineFor40Chars);
            advOut.newLine();
            if (clsGlobalVarClass.gPrintInclusiveOfAllTaxes.equalsIgnoreCase("Y"))
            {
                objPrintingUtility.funPrintBlankSpace("(INCLUSIVE OF ALL TAXES)", advOut);
                advOut.write("(INCLUSIVE OF ALL TAXES)");
                advOut.newLine();
            }
            advOut.write("    " + clsGlobalVarClass.gBillFooter);

            advOut.newLine();
            advOut.newLine();
            advOut.newLine();
            advOut.newLine();
            advOut.newLine();
            advOut.newLine();

            if (!clsGlobalVarClass.gOpenCashDrawerAfterBillPrintYN)
            {
                if ("linux".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
                {
                    advOut.write("V");//Linux
                }
                else if ("windows".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
                {
                    if ("Inbuild".equalsIgnoreCase(clsPosConfigFile.gPrinterType))
                    {
                        advOut.write("V");
                    }
                    else
                    {
                        advOut.write("m");//windows
                    }
                }
            }
            advOut.close();
            fstream_bill.close();
            pst.close();

            if (formName.equalsIgnoreCase("AdvanceOrderFlash"))
            {
                objPrintingUtility.funShowTextFile(Text_Bill, formName, clsGlobalVarClass.gBillPrintPrinterPort);
            }
            else
            {

                if (clsGlobalVarClass.gShowBill)
                {
                    objPrintingUtility.funShowTextFile(Text_Bill, "", "");
                }

                String printerName = clsGlobalVarClass.gAdvReceiptPrinterPort;
                int advCount = Integer.parseInt(clsGlobalVarClass.gAdvRecPrintCount);
                for (int i = 0; i < advCount; i++)
                {
                    objPrintingUtility.funPrintToPrinter(printerName, "", "Adv Receipt", "N", isReprint);
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
