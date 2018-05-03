package com.POSPrinting.Jasper.KOT;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.POSPrinting.Utility.clsPrintingUtility;
import com.POSGlobal.controller.clsBillDtl;
import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsUtility;
import com.POSGlobal.controller.clsUtility2;
import java.awt.Dimension;
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
public class clsKOTJasperFileGenerationForDirectBiller
{

    private DecimalFormat decimalFormat = new DecimalFormat("#.###");
    private SimpleDateFormat ddMMyyyyAMPMDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
    private clsUtility objUtility = new clsUtility();
    private clsUtility2 objUtility2 = new clsUtility2();
    private clsPrintingUtility objPrintingUtility = new clsPrintingUtility();

    /**
     *
     * @param CostCenterCode
     * @param ShowKOT
     * @param AreaCode
     * @param BillNo
     * @param Reprint
     * @param primaryPrinterName
     * @param secondaryPrinterName
     * @param CostCenterName
     * @param labelOnKOT
     */
    public void funGenerateJasperForKOTDirectBiller(String CostCenterCode, String ShowKOT, String AreaCode, String BillNo, String Reprint, String primaryPrinterName, String secondaryPrinterName, String CostCenterName, String labelOnKOT)
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
            else
            {
                hm.put("dublicate", "");
            }
            String sql_PrintHomeDelivery = "select strOperationType,intOrderNo from tblbillhd where strBillNo=? ";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_PrintHomeDelivery);
            pst.setString(1, BillNo);
            ResultSet rs_PrintHomeDelivery = pst.executeQuery();
            String operationType = "";
            if (rs_PrintHomeDelivery.next())
            {
                operationType = rs_PrintHomeDelivery.getString(1);

                if (clsGlobalVarClass.gBillFormatType.equalsIgnoreCase("Text 19") || clsGlobalVarClass.gBillFormatType.equalsIgnoreCase("Jasper 4"))//for only "MING YANG", Raju Ki Chai("206.001","Gaurika Enterprises Pvt. Ltd.")
                {
                    hm.put("orderNo", "Your order no is " + rs_PrintHomeDelivery.getString(2));
                }
            }
            rs_PrintHomeDelivery.close();
            hm.put("Type", "");
            if (operationType.equalsIgnoreCase("HomeDelivery"))
            {
                if (clsGlobalVarClass.gPrintHomeDeliveryYN)
                {
                    hm.put("Type", "Home Delivery");
                }
            }
            else if (operationType.equalsIgnoreCase("TakeAway"))
            {
                hm.put("Type", "Take Away");
            }
            hm.put("KOT", labelOnKOT);
            hm.put("POS", clsGlobalVarClass.gPOSName);
            hm.put("CostCenter", CostCenterName);
            hm.put("DIRECT BILLER", "DIRECT BILLER");
            hm.put("BILL No", BillNo);

            hm.put("kotByUser", clsGlobalVarClass.gUserCode);

            String sql_DirectKOT_Date = "select dteBillDate from tblbilldtl where strBillNo=? ";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_DirectKOT_Date);
            pst.setString(1, BillNo);
            ResultSet rs_DirectKOT_Date = pst.executeQuery();
            if (rs_DirectKOT_Date.next())
            {
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
                hm.put("DATE & TIME", dateTimeFormat.format(rs_DirectKOT_Date.getObject(1)));
            }
            rs_DirectKOT_Date.close();

            String sql_DirectKOT_Items = "select a.strItemCode,a.strItemName,a.dblQuantity,d.strShortName "
                    + "from tblbilldtl a,tblmenuitempricingdtl b,tblprintersetup c,tblitemmaster d "
                    + "where  a.strBillNo=? and  b.strCostCenterCode=c.strCostCenterCode "
                    + "and a.strItemCode=d.strItemCode "
                    + "and b.strCostCenterCode=? and (b.strAreaCode=? or b.strAreaCode='" + clsGlobalVarClass.gAreaCodeForTrans + "') "
                    + "and a.strItemCode=b.strItemCode "
                    + "group by a.strItemCode "
                    + "ORDER BY a.strSequenceNo;;";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_DirectKOT_Items);
            pst.setString(1, BillNo);
            pst.setString(2, CostCenterCode);
            pst.setString(3, AreaCode);
            List<clsBillDtl> listOfKOTDetail = new ArrayList<>();
            ResultSet rs_DirectKOT_Items = pst.executeQuery();
            while (rs_DirectKOT_Items.next())
            {

                String itemName = rs_DirectKOT_Items.getString(2);
                if (clsGlobalVarClass.gPrintShortNameOnKOT && !rs_DirectKOT_Items.getString(4).trim().isEmpty())
                {
                    itemName = rs_DirectKOT_Items.getString(4);
                }

                clsBillDtl objBillDtl = new clsBillDtl();
                objBillDtl.setDblQuantity(Double.parseDouble(rs_DirectKOT_Items.getString(3)));
                objBillDtl.setStrItemName(itemName);
                listOfKOTDetail.add(objBillDtl);
                String sql_Modifier = " select a.strModifierName,a.dblQuantity,ifnull(b.strDefaultModifier,'N'),a.strDefaultModifierDeselectedYN "
                        + "from tblbillmodifierdtl a "
                        + "left outer join tblitemmodofier b on left(a.strItemCode,7)=if(b.strItemCode='',a.strItemCode,b.strItemCode) "
                        + "and a.strModifierCode=if(a.strModifierCode=null,'',b.strModifierCode) "
                        + "where a.strBillNo=? and left(a.strItemCode,7)=? ";
                //System.out.println(sql_Modifier);
                pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_Modifier);
                pst.setString(1, BillNo);
                pst.setString(2, rs_DirectKOT_Items.getString(1));
                ResultSet rs_Modifier = pst.executeQuery();
                while (rs_Modifier.next())
                {
                    objBillDtl = new clsBillDtl();
                    if (!clsGlobalVarClass.gPrintModQtyOnKOT)//dont't print modifier qty
                    {
                        if (rs_Modifier.getString(3).equalsIgnoreCase("Y") && rs_Modifier.getString(4).equalsIgnoreCase("Y"))
                        {
                            objBillDtl.setDblQuantity(0);
                            objBillDtl.setStrItemName("        " + "No " + rs_Modifier.getString(1));
                        }
                        else if (!rs_Modifier.getString(3).equalsIgnoreCase("Y"))
                        {
                            objBillDtl.setDblQuantity(0);
                            objBillDtl.setStrItemName(rs_Modifier.getString(1));
                        }
                    }
                    else
                    {
                        if (rs_Modifier.getString(3).equalsIgnoreCase("Y") && rs_Modifier.getString(4).equalsIgnoreCase("Y"))
                        {
                            objBillDtl.setDblQuantity(Double.parseDouble(rs_Modifier.getString(2)));
                            objBillDtl.setStrItemName("  " + rs_Modifier.getString(2) + "      " + "No " + rs_Modifier.getString(1));
                        }
                        else if (!rs_Modifier.getString(3).equalsIgnoreCase("Y"))
                        {
                            objBillDtl.setDblQuantity(Double.parseDouble(rs_Modifier.getString(2)));
                            objBillDtl.setStrItemName("  " + rs_Modifier.getString(2) + "      " + rs_Modifier.getString(1));
                        }
                    }
                    listOfKOTDetail.add(objBillDtl);
                }
                rs_Modifier.close();
            }
            rs_DirectKOT_Items.close();
            hm.put("listOfItemDtl", listOfKOTDetail);
            listData.add(listOfKOTDetail);
            String reportName = "com/POSGlobal/reports/rptGenrateKOTJasperReportForDirectBiller.jasper";
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

//            JRPrintServiceExporter exporter = new JRPrintServiceExporter();
//            //--- Set print properties
//            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
//            printRequestAttributeSet.add(MediaSizeName.ISO_A4);
//            if (clsGlobalVarClass.gMultipleKOTPrint)
//            {
//                printRequestAttributeSet.add(new Copies(2));
//            }
//
//            //----------------------------------------------------     
//            //printRequestAttributeSet.add(new Destination(new java.net.URI("file:d:/output/report.ps")));
//            //----------------------------------------------------     
//            PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
//
//            String billPrinterName = primaryPrinterName;
//
//            billPrinterName = billPrinterName.replaceAll("#", "\\\\");
//            printServiceAttributeSet.add(new PrinterName(billPrinterName, null));
//
//            //--- Set print parameters      
//            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
//            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
//            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet);
//            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
//            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
//
//            //--- Print the document
//            try
//            {
//                exporter.exportReport();
//            }
//            catch (JRException e)
//            {
//                e.printStackTrace();
//            }
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
                    if (clsGlobalVarClass.gMultipleKOTPrint)
                    {
                        objUtility2.funPrintJasperKOT(primary, print);
                        objUtility2.funPrintJasperKOT(primary, print);

                        objUtility2.funPrintJasperKOT(secondary, print);
                        objUtility2.funPrintJasperKOT(secondary, print);
                    }
                    else
                    {
                        objUtility2.funPrintJasperKOT(primary, print);
                        objUtility2.funPrintJasperKOT(secondary, print);
                    }
                }
                else
                {

                    if (clsGlobalVarClass.gMultipleKOTPrint)
                    {
                        if (!objUtility2.funPrintJasperKOT(primary, print))
                        {
                            objUtility2.funPrintJasperKOT(secondary, print);
                        }
                        if (!objUtility2.funPrintJasperKOT(primary, print))
                        {
                            objUtility2.funPrintJasperKOT(secondary, print);
                        }
                    }
                    else
                    {
                        if (!objUtility2.funPrintJasperKOT(primary, print))
                        {
                            objUtility2.funPrintJasperKOT(secondary, print);
                        }
                    }
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
