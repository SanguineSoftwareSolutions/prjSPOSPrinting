/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting.Text.KOT.Audit;

import com.POSGlobal.controller.clsBillDtl;
import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsUtility;
import com.POSGlobal.controller.clsUtility2;
import com.POSPrinting.Interfaces.clsVoidBillForAuditGenerationFormat;
import com.POSPrinting.Interfaces.clsVoidKOTForAuditGenerationFormat;
import com.POSPrinting.Utility.clsPrintingUtility;
import java.awt.Dimension;
import java.io.InputStream;
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
import javax.print.attribute.standard.Copies;
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
 * @author Harry
 */
public class clsJasperFormatForVoidKOTAudit implements clsVoidKOTForAuditGenerationFormat
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
        HashMap hm = new HashMap();
        List<List<clsBillDtl>> listData = new ArrayList<>();

        String imagePath = System.getProperty("user.dir");
        imagePath = imagePath + "\\ReportImage";

        try
        {

            hm.put("imagePath", imagePath);
            hm.put("KOTorNC", "Void KOT");
            hm.put("KOTTitle", "KOT");

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
            hm.put("voidBillType",voidBillType);
            
            hm.put("KOT", KotNo);
            hm.put("tableNo", KOT_TableNo);

            String waiterNo = "select strWaiterNo from tblvoidkot where strKOTNo='" + KotNo + "'";
            ResultSet rsWaiterNo = clsGlobalVarClass.dbMysql.executeResultSet(waiterNo);
            if (rsWaiterNo.next())
            {
                if (!"null".equalsIgnoreCase(rsWaiterNo.getString(1)) && rsWaiterNo.getString(1).trim().length() > 0)
                {
                    String sqlVOIDKOT_waiterName = "select strWShortName from tblwaitermaster where strWaiterNo='" + rsWaiterNo.getString(1) + "'";
                    ResultSet rs_waiterName = clsGlobalVarClass.dbMysql.executeResultSet(sqlVOIDKOT_waiterName);
                    if (rs_waiterName.next())
                    {
                        hm.put("waiterName", rs_waiterName.getString(1));
                    }
                    rs_waiterName.close();
                }
            }
            rsWaiterNo.close();

            //Added by Jaichandra
            String sqlVOIDKOT_waiterName = "select date(dteDateCreated),time(dteDateCreated) from tblvoidkot where strKOTNo='" + KotNo + "'";
            ResultSet rs_Date = clsGlobalVarClass.dbMysql.executeResultSet(sqlVOIDKOT_waiterName);
            if (rs_Date.next())
            {
                hm.put("DATE_TIME", rs_Date.getString(1) + " " + rs_Date.getString(2));
            }
            rs_Date.close();

            String sqlVOIDKOT_reasonName = "select b.strReasonName "
                    + "from tblvoidkot a,tblreasonmaster b "
                    + "where a.strReasonCode=b.strReasonCode "
                    + "and a.strKOTNo='" + KotNo + "' "
                    + "group by a.strKOTNo";
            rs_Date = clsGlobalVarClass.dbMysql.executeResultSet(sqlVOIDKOT_reasonName);
            if (rs_Date.next())
            {
                hm.put("reason", rs_Date.getString(1));
            }
            rs_Date.close();

            List<clsBillDtl> listOfKOTDetail = new ArrayList<>();
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
            while (rs_VOIDKOT_Items.next())
            {

                primaryPrinterName = rs_VOIDKOT_Items.getString(4);
                secondaryPrinterName = rs_VOIDKOT_Items.getString(6);

                clsBillDtl objBillDtl = new clsBillDtl();
                objBillDtl.setDblQuantity(Double.parseDouble(rs_VOIDKOT_Items.getString(1)));
                objBillDtl.setStrItemName(rs_VOIDKOT_Items.getString(2).toUpperCase());
                listOfKOTDetail.add(objBillDtl);
            }
            rs_VOIDKOT_Items.close();

            hm.put("listOfItemDtl", listOfKOTDetail);
            listData.add(listOfKOTDetail);
            String reportName = "com/POSGlobal/reports/rptGenrateVoidKOTJasperReport.jasper";
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
                jf.setSize(new Dimension(850, 750));
                jf.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            }
            JRPrintServiceExporter exporter = new JRPrintServiceExporter();

            //--- Set print properties
            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            printRequestAttributeSet.add(MediaSizeName.ISO_A4);
            if (clsGlobalVarClass.gMultipleKOTPrint)
            {
                printRequestAttributeSet.add(new Copies(2));
            }

            //----------------------------------------------------     
            //printRequestAttributeSet.add(new Destination(new java.net.URI("file:d:/output/report.ps")));
            //----------------------------------------------------     
            PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();

            String kotPrinterName = primaryPrinterName;
            kotPrinterName = kotPrinterName.replaceAll("#", "\\\\");
            printServiceAttributeSet.add(new PrinterName(kotPrinterName, null));

            //--- Set print parameters      
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
            exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet);
            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
            exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

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
