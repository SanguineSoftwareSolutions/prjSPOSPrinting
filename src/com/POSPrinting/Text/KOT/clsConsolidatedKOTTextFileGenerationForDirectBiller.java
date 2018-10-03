/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting.Text.KOT;

import com.POSPrinting.Utility.clsPrintingUtility;
import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsPosConfigFile;
import com.POSGlobal.controller.clsUtility;
import com.POSGlobal.controller.clsUtility2;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author Ajim
 * @date Aug 26, 2017
 */
public class clsConsolidatedKOTTextFileGenerationForDirectBiller
{

    private DecimalFormat decimalFormat = new DecimalFormat("#.###");
    private SimpleDateFormat ddMMyyyyAMPMDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
    private clsUtility objUtility = new clsUtility();
    private clsUtility2 objUtility2 = new clsUtility2();
    private clsPrintingUtility objPrintingUtility = new clsPrintingUtility();
    private final String dashedLineFor40Chars = "  --------------------------------------";

    /**
     *
     * @param billNo
     */
    public void funConsolidatedKOTForDirectBillerTextFileGeneration(String billNo)
    {
        try
        {
            PreparedStatement pst = null;
            DecimalFormat decimalFormat = new DecimalFormat("#.###");

            objPrintingUtility.funCreateTempFolder();
            String filePath = System.getProperty("user.dir");
            File Text_KOT = new File(filePath + "/Temp/Temp_KOT.txt");
            FileWriter fstream = new FileWriter(Text_KOT);
            //BufferedWriter KotOut = new BufferedWriter(fstream);
            BufferedWriter KotOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Text_KOT), "UTF8"));
            boolean isReprint = false;

            isReprint = true;
            objPrintingUtility.funPrintBlankSpace("CONSOLIDATED KOT", KotOut);
            KotOut.write("CONSOLIDATED KOT");
            KotOut.newLine();

            String sql_PrintHomeDelivery = "select strOperationType from tblbillhd where strBillNo=? ";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_PrintHomeDelivery);
            pst.setString(1, billNo);
            ResultSet rs_PrintHomeDelivery = pst.executeQuery();
            String operationType = "";
            if (rs_PrintHomeDelivery.next())
            {
                operationType = rs_PrintHomeDelivery.getString(1);
            }
            rs_PrintHomeDelivery.close();
            if (operationType.equalsIgnoreCase("HomeDelivery"))
            {
                if (clsGlobalVarClass.gPrintHomeDeliveryYN)
                {
                    objPrintingUtility.funPrintBlankSpace("Home Delivery", KotOut);
                    KotOut.write("Home Delivery");
                    KotOut.newLine();
                }

            }
            else if (operationType.equalsIgnoreCase("TakeAway"))
            {
                objPrintingUtility.funPrintBlankSpace("Take Away", KotOut);
                KotOut.write("Take Away");
                KotOut.newLine();
            }

            //KotOut.newLine();
            objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gPOSName, KotOut);
            KotOut.write(clsGlobalVarClass.gPOSName);
            KotOut.newLine();

            // KotOut.newLine();
            objPrintingUtility.funPrintBlankSpace("DIRECT BILLER", KotOut);
            KotOut.write("DIRECT BILLER");
            KotOut.newLine();
            KotOut.write(dashedLineFor40Chars);
            KotOut.newLine();
            KotOut.write("  BILL No: " + billNo);
            KotOut.newLine();
            KotOut.write(dashedLineFor40Chars);

            String sql_DirectKOT_Date = "select dteBillDate from tblbilldtl where strBillNo=? ";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_DirectKOT_Date);
            pst.setString(1, billNo);
            ResultSet rs_DirectKOT_Date = pst.executeQuery();
            if (rs_DirectKOT_Date.next())
            {
                KotOut.newLine();
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
                KotOut.write("  DATE & TIME: " + dateTimeFormat.format(rs_DirectKOT_Date.getObject(1)));
            }
            rs_DirectKOT_Date.close();

            KotOut.newLine();
            KotOut.write(dashedLineFor40Chars);
            KotOut.newLine();
            KotOut.write("  QTY        ITEM NAME  ");
            KotOut.newLine();
            KotOut.write(dashedLineFor40Chars);

            String itemName = "a.strItemName";
            if (clsGlobalVarClass.gPrintShortNameOnKOT)
            {
                itemName = "d.strShortName";
            }

            String sql_DirectKOT_Items = "SELECT a.strItemCode,a.strItemName, SUM(a.dblQuantity) "
                    + "FROM tblbilldtl a "
                    + "WHERE a.strBillNo=?  "
                    + "GROUP BY a.strItemCode "
                    + "ORDER BY a.strSequenceNo;";
            //System.out.println(sql_DirectKOT_Items);
            String areaCode = clsGlobalVarClass.gDineInAreaForDirectBiller;

            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_DirectKOT_Items);
            pst.setString(1, billNo);
            ResultSet rs_DirectKOT_Items = pst.executeQuery();
            while (rs_DirectKOT_Items.next())
            {
                String kotItemName = rs_DirectKOT_Items.getString(2).toUpperCase();
                KotOut.newLine();

                String itemQty = String.valueOf(decimalFormat.format(rs_DirectKOT_Items.getDouble(3)));

                KotOut.write("  " + itemQty + "      ");
                if (kotItemName.length() <= 25)
                {
                    KotOut.write(kotItemName);
                }
                else
                {
                    KotOut.write(kotItemName.substring(0, 25));
                    KotOut.newLine();
                    KotOut.write("            " + kotItemName.substring(25, kotItemName.length()));
                }
                //following code called for modifier

                String sql_Modifier = " select a.strModifierName,a.dblQuantity,ifnull(b.strDefaultModifier,'N'),a.strDefaultModifierDeselectedYN "
                        + "from tblbillmodifierdtl a "
                        + "left outer join tblitemmodofier b on left(a.strItemCode,7)=if(b.strItemCode='',a.strItemCode,b.strItemCode) "
                        + "and a.strModifierCode=if(a.strModifierCode=null,'',b.strModifierCode) "
                        + "where a.strBillNo=? and left(a.strItemCode,7)=? ";
                //System.out.println(sql_Modifier);

                pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_Modifier);
                pst.setString(1, billNo);
                pst.setString(2, rs_DirectKOT_Items.getString(1));
                ResultSet rs_Modifier = pst.executeQuery();
                while (rs_Modifier.next())
                {
                    String modiQty = String.valueOf(decimalFormat.format(rs_Modifier.getDouble(2)));

                    if (!clsGlobalVarClass.gPrintModQtyOnKOT)//dont't print modifier qty
                    {
                        if (rs_Modifier.getString(3).equalsIgnoreCase("Y") && rs_Modifier.getString(4).equalsIgnoreCase("Y"))
                        {
                            KotOut.newLine();
                            KotOut.write("        " + "No " + rs_Modifier.getString(1).toUpperCase());
                        }
                        else if (!rs_Modifier.getString(3).equalsIgnoreCase("Y"))
                        {
                            KotOut.newLine();
                            KotOut.write("        " + rs_Modifier.getString(1).toUpperCase());
                        }
                    }
                    else
                    {
                        if (rs_Modifier.getString(3).equalsIgnoreCase("Y") && rs_Modifier.getString(4).equalsIgnoreCase("Y"))
                        {
                            KotOut.newLine();
                            KotOut.write("  " + modiQty + "      " + "No " + rs_Modifier.getString(1).toUpperCase());
                        }
                        else if (!rs_Modifier.getString(3).equalsIgnoreCase("Y"))
                        {
                            KotOut.newLine();
                            KotOut.write("  " + modiQty + "      " + rs_Modifier.getString(1).toUpperCase());
                        }
                    }
                }
                rs_Modifier.close();
            }

            rs_DirectKOT_Items.close();
            KotOut.newLine();
            KotOut.write(dashedLineFor40Chars);
            KotOut.newLine();
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
                if ("Inbuild".equalsIgnoreCase(clsPosConfigFile.gPrinterType))
                {
                    KotOut.write("V");
                }
                else
                {
                    KotOut.write("m");//windows
                }
            }
            // KotOut.write("m");
            KotOut.close();
            fstream.close();
            pst.close();

//            if (clsGlobalVarClass.gShowBill)
//            {
//                funShowTextFile(Text_KOT, "", "Printer Info!2");
//            }           
            if (clsGlobalVarClass.gConsolidatedKOTPrinterPort.length() > 0)
            {
                objPrintingUtility.funPrintToPrinter(clsGlobalVarClass.gConsolidatedKOTPrinterPort, "", "ConsolidatedKOT", "N", false,"");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
