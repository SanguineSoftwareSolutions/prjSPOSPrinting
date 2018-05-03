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
public class clsTextFormat2ForAdvReceipt implements clsAdvReceiptGenerationFormat
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
            if (clsGlobalVarClass.gClientCode.equals("092.001") || clsGlobalVarClass.gClientCode.equals("092.002") || clsGlobalVarClass.gClientCode.equals("092.003"))//Shree Sound Pvt. Ltd.(Waters)
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
            //

            advOut.write("  ADV ORD NO  :" + advBookNo);
            advOut.newLine();
            advOut.write("  ORDER FOR   :" + orderDate);
            advOut.newLine();
            if (waiterName.trim().length() > 0)
            {
                advOut.write("  ORDER BY  :" + waiterName.toUpperCase());
                advOut.newLine();
            }
            advOut.write("  CUST NAME   :" + custName.toUpperCase());
            advOut.newLine();
            advOut.write(dashedLineFor40Chars);
            advOut.newLine();

            String sql_advOrder_SuTtotal = "select a.dblSubTotal,a.dblTaxAmt,b.dblAdvDeposite"
                    + ",a.strMessage,a.strShape,a.strNote,a.dblHomeDelCharges "
                    + "from tbladvbookbillhd a ,tbladvancereceipthd b "
                    + "where a.strAdvBookingNo=b.strAdvBookingNo and a.strAdvBookingNo=?";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_advOrder_SuTtotal);
            pst.setString(1, advBookNo);
            ResultSet rs_advOrder_Tot = pst.executeQuery();
            rs_advOrder_Tot.next();
            objPrintingUtility.funPrintBlankSpace("ORDER DETAIL", advOut);
            advOut.write("ORDER DETAIL");
            advOut.newLine();
            advOut.write(dashedLineFor40Chars);
            advOut.newLine();
            String msg = rs_advOrder_Tot.getString(4).toUpperCase();
            String shape = rs_advOrder_Tot.getString(5).toUpperCase();
            String note = rs_advOrder_Tot.getString(6).toUpperCase();

            int strlenMsg = msg.length();
            if (strlenMsg > 0)
            {
                String msg1 = "";
                if (strlenMsg < 27)
                {
                    msg1 = msg.substring(0, strlenMsg);
                    advOut.write("  MESSAGE     :" + msg1);
                    advOut.newLine();
                }
                else
                {
                    msg1 = msg.substring(0, 27);
                    advOut.write("  MESSAGE     :" + msg1);;
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
            //////////////////////////////////////////////////

            int strlenShape = shape.length();
            if (strlenShape > 0)
            {

                String shape1 = "";
                if (strlenShape < 27)
                {
                    shape1 = shape.substring(0, strlenShape);
                    advOut.write("  SHAPE       :" + shape1);
                    advOut.newLine();
                }
                else
                {
                    shape1 = shape.substring(0, 27);
                    advOut.write("  SHAPE       :" + shape1);;
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
            }//////////////////////////////////////////////

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

            //
            advOut.newLine();
            advOut.write(dashedLineFor40Chars);
            advOut.newLine();
            advOut.write("     QTY      ITEM NAME            AMT");
            advOut.newLine();
            advOut.write(dashedLineFor40Chars);
            advOut.newLine();

            String SQL_AdvOrderDtl = "select SUM(dblQuantity),strItemName,SUM(dblAmount),strItemCode from tbladvbookbilldtl where strAdvBookingNo=? group by strItemCode ;";

            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_AdvOrderDtl);
            pst.setString(1, advBookNo);
            ResultSet rs_AdvOrderDtl = pst.executeQuery();
            while (rs_AdvOrderDtl.next())
            {
                objPrintingUtility.funWriteToText(advOut, rs_AdvOrderDtl.getString(1), rs_AdvOrderDtl.getString(2).toUpperCase(), rs_AdvOrderDtl.getString(3), "Format1");
                advOut.newLine();
                String SQL_ModifierDtl_count = "select Count(*) from tbladvordermodifierdtl where strAdvOrderNo=? and strItemCode=? and  dblAmount !=0.00 ;";
                pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_ModifierDtl_count);
                pst.setString(1, advBookNo);
                pst.setString(2, rs_AdvOrderDtl.getString(4));
                ResultSet rs_count = pst.executeQuery();
                rs_count.next();
                int cntRecord = rs_count.getInt(1);
                rs_count.close();
                if (cntRecord > 0)
                {
                    String SQL_ModifierDtl = "select strModifierName,dblQuantity,dblAmount from tbladvordermodifierdtl where strAdvOrderNo=? and strItemCode=? and dblAmount !=0.00 ;";
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
            if (subTotalWithDelCharges < Double.parseDouble(advAmt))
            {
                advAmt = subTotal;
            }
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
            //Settlement bkp part
            String sql_settle = "select a.dblAdvDeposite,b.strSettlementCode,date(b.dteInstallment),b.dblAdvDepositesettleAmt,"
                    + "(d.dblSubTotal+d.dblTaxAmt) from tbladvancereceipthd a,tbladvancereceiptdtl b,tblsettelmenthd c,"
                    + "tbladvbookbillhd d where a.strReceiptNo=b.strReceiptNo and b.strSettlementCode=c.strSettelmentCode "
                    + "and a.strAdvBookingNo=? and d.strAdvBookingNo=a.strAdvBookingNo ";
            //+ "group by b.strSettlementCode";

            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_settle);
            pst.setString(1, advBookNo);

            ResultSet rs_advOrder_settel = pst.executeQuery();
            while (rs_advOrder_settel.next())
            {
                double depositeAmt = Double.parseDouble(advAmt);
                if (Double.parseDouble(rs_advOrder_settel.getString(4)) > Double.parseDouble(advAmt))
                {
                    objPrintingUtility.funWriteTotal(rs_advOrder_settel.getString(3), String.valueOf(depositeAmt), advOut, "Format1");
                    advOut.newLine();
                }
                else
                {
                    objPrintingUtility.funWriteTotal(rs_advOrder_settel.getString(3), rs_advOrder_settel.getString(4), advOut, "Format1");
                    advOut.newLine();
                }
            }
            rs_advOrder_settel.close();
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
            advOut.write("    " + clsGlobalVarClass.gBillFooter);

            advOut.newLine();

            advOut.newLine();
            advOut.newLine();
            //BillOut.write("m");
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
            // BillOut.write("V");//Linux

            advOut.close();
            fstream_bill.close();
            pst.close();
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
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
