/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting.Jasper.AdvReceipt;

import com.POSGlobal.controller.clsBillDtl;
import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsUtility;
import com.POSGlobal.controller.clsUtility2;
import com.POSPrinting.Utility.clsPrintingUtility;
import com.POSPrinting.Interfaces.clsAdvReceiptGenerationFormat;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrinterName;
import javax.swing.JDialog;
import javax.swing.JFrame;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.swing.JRViewer;

/**
 *
 * @author Ajim
 * @date Aug 28, 2017
 */
public class clsJasperFormat1ForAdvReceipt implements clsAdvReceiptGenerationFormat
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
        HashMap hm = new HashMap();
        List<clsBillDtl> listHeaderDtl = new ArrayList<>();
        List<clsBillDtl> listTitleDtl = new ArrayList<>();
        List<clsBillDtl> listFooterAmountDtl = new ArrayList<>();
        List<clsBillDtl> listNormalOrderDtl = new ArrayList<>();
        clsBillDtl objBillDtl = new clsBillDtl();
        String Linefor5 = "  --------------------------------------";
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
                objBillDtl = new clsBillDtl();
                objBillDtl.setStrItemName("[DUPLICATE]");
                listTitleDtl.add(objBillDtl);
            }
            objBillDtl = new clsBillDtl();
            objBillDtl.setStrItemName("ADVANCE RECEIPT");
            listTitleDtl.add(objBillDtl);
            objBillDtl = new clsBillDtl();
            objBillDtl.setStrItemName(clsGlobalVarClass.gClientName);
            listTitleDtl.add(objBillDtl);
            objBillDtl = new clsBillDtl();
            objBillDtl.setStrItemName(clsGlobalVarClass.gClientAddress1);
            listTitleDtl.add(objBillDtl);
            objBillDtl = new clsBillDtl();
            objBillDtl.setStrItemName(clsGlobalVarClass.gClientAddress2);
            listTitleDtl.add(objBillDtl);
            objBillDtl = new clsBillDtl();
            objBillDtl.setStrItemName(clsGlobalVarClass.gClientAddress3);
            listTitleDtl.add(objBillDtl);
            if (clsGlobalVarClass.gCityName.trim().length() > 0)
            {
                objBillDtl = new clsBillDtl();
                objBillDtl.setStrItemName(clsGlobalVarClass.gCityName);
                listTitleDtl.add(objBillDtl);
            }
            objBillDtl = new clsBillDtl();
            objBillDtl.setStrItemCode("TEL NO           :");
            objBillDtl.setStrItemName(String.valueOf(clsGlobalVarClass.gClientTelNo));
            listHeaderDtl.add(objBillDtl);
            objBillDtl = new clsBillDtl();
            objBillDtl.setStrItemCode("EMAIL ID         :");
            objBillDtl.setStrItemName(clsGlobalVarClass.gClientEmail);
            listHeaderDtl.add(objBillDtl);

            String sql_advOrder_SuTtotal = "select a.dblSubTotal,a.dblTaxAmt,b.dblAdvDeposite"
                    + ",a.strMessage,a.strShape,a.strNote,a.dblHomeDelCharges,a.strManualAdvOrderNo "
                    + "from " + advBookingHd + " a ," + advReceiptHd + " b "
                    + "where a.strAdvBookingNo=b.strAdvBookingNo and a.strAdvBookingNo=?";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_advOrder_SuTtotal);
            pst.setString(1, advBookNo);
            ResultSet rs_advOrder_Tot = pst.executeQuery();
            rs_advOrder_Tot.next();
            objBillDtl = new clsBillDtl();
            objBillDtl.setStrItemCode("ADV ORD NO   :");
            objBillDtl.setStrItemName(advBookNo);
            listHeaderDtl.add(objBillDtl);

            if (clsGlobalVarClass.gPrintZeroAmtModifierOnBill)
            {
                if (rs_advOrder_Tot.getString(8).trim().length() > 0)
                {
                    objBillDtl = new clsBillDtl();
                    objBillDtl.setStrItemCode("MANUAL NO    :");
                    objBillDtl.setStrItemName(rs_advOrder_Tot.getString(8));
                    listHeaderDtl.add(objBillDtl);
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
                objBillDtl = new clsBillDtl();
                objBillDtl.setStrItemCode("ORDER DATE   :");
                objBillDtl.setStrItemName(objUtility.funGetDateInFormat(rsAdvOrderDate.getString(1), "dd-MM-yyyy"));
                listHeaderDtl.add(objBillDtl);
            }
            rsAdvOrderDate.close();

            String orderDateTime = objUtility.funGetDateInFormat(orderDate.split(" ")[0], "dd-MM-yyyy");
            orderDateTime += " " + orderDate.split(" ")[1];
            objBillDtl = new clsBillDtl();
            objBillDtl.setStrItemCode("ORDER FOR     :");
            objBillDtl.setStrItemName(orderDateTime);
            listHeaderDtl.add(objBillDtl);
            if (waiterName.trim().length() > 0)
            {
                objBillDtl = new clsBillDtl();
                objBillDtl.setStrItemCode("WAITER NAME  :");
                objBillDtl.setStrItemName(waiterName.toUpperCase());
                listHeaderDtl.add(objBillDtl);
            }
            objBillDtl = new clsBillDtl();
            objBillDtl.setStrItemCode("CUST NAME     :");
            objBillDtl.setStrItemName(custName.toUpperCase());
            listHeaderDtl.add(objBillDtl);

            sql = "select b.longMobileNo "
                    + " from " + advBookingHd + " a,tblcustomermaster b "
                    + " where a.strCustomerCode=b.strCustomerCode and a.strAdvBookingNo='" + advBookNo + "'";
            ResultSet rsCustMbNo = clsGlobalVarClass.dbMysql.executeResultSet(sql);
            if (rsCustMbNo.next())
            {
                objBillDtl = new clsBillDtl();
                objBillDtl.setStrItemCode("MOB NO          :");
                objBillDtl.setStrItemName(rsCustMbNo.getString(1));
                listHeaderDtl.add(objBillDtl);
            }
            rsCustMbNo.close();

            String msg = rs_advOrder_Tot.getString(4).toUpperCase();
            String shape = rs_advOrder_Tot.getString(5).toUpperCase();
            String note = rs_advOrder_Tot.getString(6).toUpperCase();

            if (!msg.isEmpty())
            {
                objBillDtl = new clsBillDtl();
                objBillDtl.setStrItemCode("Message");
                objBillDtl.setStrItemName(msg);
                listNormalOrderDtl.add(objBillDtl);
            }
            if (!shape.isEmpty())
            {
                objBillDtl = new clsBillDtl();
                objBillDtl.setStrItemCode("Shape");
                objBillDtl.setStrItemName(shape);
                listNormalOrderDtl.add(objBillDtl);
            }
            if (!note.isEmpty())
            {
                objBillDtl = new clsBillDtl();
                objBillDtl.setStrItemCode("Note");
                objBillDtl.setStrItemName(note);
                listNormalOrderDtl.add(objBillDtl);
            }

            List<clsBillDtl> listAdvanceOrderItemsDtl = new ArrayList<>();

            String SQL_AdvOrderDtl = "select SUM(dblQuantity),strItemName,SUM(dblAmount),strItemCode,dblWeight "
                    + " from " + advBookingDtl + " where strAdvBookingNo=? group by strItemCode ;";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_AdvOrderDtl);
            pst.setString(1, advBookNo);
            ResultSet rs_AdvOrderDtl = pst.executeQuery();
            while (rs_AdvOrderDtl.next())
            {
                double weight = rs_AdvOrderDtl.getDouble(5);
                String qty = rs_AdvOrderDtl.getString(1);

                objBillDtl = new clsBillDtl();
                objBillDtl.setDblQuantity(rs_AdvOrderDtl.getDouble(1));
                objBillDtl.setDblAmount(rs_AdvOrderDtl.getDouble(3));
                objBillDtl.setStrItemName(rs_AdvOrderDtl.getString(2).toUpperCase());
                listAdvanceOrderItemsDtl.add(objBillDtl);

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
                        objBillDtl = new clsBillDtl();
                        objBillDtl.setDblQuantity(rs_modifierRecord.getDouble(2));
                        objBillDtl.setDblAmount(rs_modifierRecord.getDouble(3));
                        objBillDtl.setStrItemName(rs_modifierRecord.getString(1).toUpperCase());
                        listAdvanceOrderItemsDtl.add(objBillDtl);
                    }
                    rs_modifierRecord.close();
                }

                if (weight > 0)
                {
                    objBillDtl = new clsBillDtl();
                    objBillDtl.setStrItemName("     Weight" + "          " + weight);
                    listAdvanceOrderItemsDtl.add(objBillDtl);
                }

                sql = "select b.strCharName,a.strCharValues,a.strItemCode "
                        + " from " + advBookingCharDtl + " a,tblcharactersticsmaster b "
                        + " where a.strCharCode=b.strCharCode and a.strAdvBookingNo='" + advBookNo + "' "
                        + " and a.strItemCode='" + rs_AdvOrderDtl.getString(4) + "' ";
                ResultSet rsCharDtl = clsGlobalVarClass.dbMysql.executeResultSet(sql);
                while (rsCharDtl.next())
                {
                    String charName = rsCharDtl.getString(1);
                    String charVal = rsCharDtl.getString(2);
                    objBillDtl = new clsBillDtl();
                    if (charName.length() == 5)
                    {
                        objBillDtl.setStrItemName("     " + charName + "           " + charVal);
                    }
                    else if (charName.length() == 6)
                    {
                        objBillDtl.setStrItemName("     " + charName + "        " + charVal);
                    }
                    else
                    {
                        objBillDtl.setStrItemName("     " + charName + "       " + charVal);
                    }
                    listAdvanceOrderItemsDtl.add(objBillDtl);
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

            objBillDtl = new clsBillDtl();
            objBillDtl.setStrItemName("Sub Total       :");
            objBillDtl.setDblAmount(Double.parseDouble(subTotal));
            listFooterAmountDtl.add(objBillDtl);
            objBillDtl = new clsBillDtl();
            objBillDtl.setStrItemName("Tax Amt         :");
            objBillDtl.setDblAmount(Double.parseDouble(tax));
            listFooterAmountDtl.add(objBillDtl);

            double subTotalWithDelCharges = Double.parseDouble(subTotal) + Double.parseDouble(delCharges);
            if (subTotalWithDelCharges < Double.parseDouble(advAmt))
            {
                advAmt = subTotal;
            }
            advOut.newLine();

            if (Double.parseDouble(delCharges) > 0)
            {
                objBillDtl = new clsBillDtl();
                objBillDtl.setStrItemName("Del Charges  :");
                objBillDtl.setDblAmount(Double.parseDouble(delCharges));
                listFooterAmountDtl.add(objBillDtl);
            }
            objBillDtl = new clsBillDtl();
            objBillDtl.setStrItemName("Adv Amount   :");
            objBillDtl.setDblAmount(Double.parseDouble(advAmt));
            listFooterAmountDtl.add(objBillDtl);

            advOut.write(dashedLineFor40Chars);
            advOut.newLine();
            if (bal <= 0)
            {
                bal = 0.00;
            }
            objBillDtl = new clsBillDtl();
            objBillDtl.setStrItemName("Balance          :");
            objBillDtl.setDblAmount(Double.parseDouble(String.valueOf(bal).concat("0")));
            listFooterAmountDtl.add(objBillDtl);

            List<clsBillDtl> listOfFooterDtl = new ArrayList<>();

            if (clsGlobalVarClass.gPrintInclusiveOfAllTaxes.equalsIgnoreCase("Y"))
            {
                objBillDtl = new clsBillDtl();
                objBillDtl.setStrItemName("(INCLUSIVE OF ALL TAXES)");
                listOfFooterDtl.add(objBillDtl);
            }
            objBillDtl = new clsBillDtl();
            objBillDtl.setStrItemName("    " + clsGlobalVarClass.gBillFooter);
            listOfFooterDtl.add(objBillDtl);

            hm.put("listOfHeaderDtl", listHeaderDtl);
            hm.put("listOfAdvanceOrderItemDtl", listAdvanceOrderItemsDtl);
            hm.put("listOfFooterDtl", listOfFooterDtl);
            hm.put("listOfTitleDtl", listTitleDtl);
            hm.put("listOfFooterAmountDtl", listFooterAmountDtl);
            hm.put("listOfNormalOrderDtl", listNormalOrderDtl);

            List<List<clsBillDtl>> listData = new ArrayList<>();
            listData.add(listAdvanceOrderItemsDtl);
            String reportName = "com/POSGlobal/reports/rptAdvanceOrderReceiptJasperPrint.jasper";

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
            JRPrintServiceExporter exporter = new JRPrintServiceExporter();

            //--- Set print properties
            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            printRequestAttributeSet.add(MediaSizeName.ISO_A4);

            //----------------------------------------------------     
            //printRequestAttributeSet.add(new Destination(new java.net.URI("file:d:/output/report.ps")));
            //----------------------------------------------------     
            PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();

            String billPrinterName = clsGlobalVarClass.gBillPrintPrinterPort;

            billPrinterName = billPrinterName.replaceAll("#", "\\\\");
            printServiceAttributeSet.add(new PrinterName(billPrinterName, null));

            //--- Set print parameters      
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.TRUE);
            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.TRUE);
            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet);

            //--- Print the document
            try
            {
                exporter.exportReport();
            }
            catch (JRException e)
            {
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
