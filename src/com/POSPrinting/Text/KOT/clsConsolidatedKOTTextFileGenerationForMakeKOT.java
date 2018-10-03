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
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author Ajim
 * @date Aug 24, 2017
 */
public class clsConsolidatedKOTTextFileGenerationForMakeKOT
{

    private DecimalFormat decimalFormat = new DecimalFormat("#.###");
    private SimpleDateFormat ddMMyyyyAMPMDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
    private clsUtility objUtility = new clsUtility();
    private clsUtility2 objUtility2 = new clsUtility2();
    private clsPrintingUtility objPrintingUtility = new clsPrintingUtility();
    private final String dashedLineFor40Chars = "  --------------------------------------";

    /**
     *
     * @param tableNo
     * @param kotNo
     */
    public void funConsolidatedKOTForMakeKOTTextFileGeneration(String tableNo, String kotNo)
    {
        try
        {
            objPrintingUtility.funCreateTempFolder();
            String filePath = System.getProperty("user.dir");
            File Text_Check_KOT = new File(filePath + "/Temp/Temp_KOT.txt");
            FileWriter fstream = new FileWriter(Text_Check_KOT);

            DecimalFormat decimalFormat = new DecimalFormat("#.###");

            BufferedWriter checkKotOut = new BufferedWriter(fstream);

            objPrintingUtility.funPrintBlankSpace("Consolidated KOT", checkKotOut);
            checkKotOut.write("Consolidated KOT");
            checkKotOut.newLine();
            checkKotOut.write(dashedLineFor40Chars);
            checkKotOut.newLine();

            PreparedStatement pst = null;
            String sql_CheckKot = "select a.strItemName, sum(a.dblItemQuantity),b.strTableName, "
                    + "TIME_FORMAT(time(a.dteDateCreated),'%h:%i'),ifnull(a.strWaiterNo,'') ,ifnull(d.strWFullName,'') "
                    + "from tblitemrtemp a "
                    + "join tbltablemaster b on a.strTableNo=b.strTableNo  "
                    + "join tblitemmaster c on left(a.strItemCode,7)=c.strItemCode "
                    + "left outer join tblwaitermaster d on a.strWaiterNo=d.strWaiterNo "
                    + "where a.strTableNo=? "
                    + "and a.strKOTNo=? "
                    + "and a.strNCKotYN='N'  "
                    + "group by a.strItemCode,a.strItemName  "
                    + "order by a.strSerialNo ";
            System.out.println(sql_CheckKot);
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_CheckKot);
            pst.setString(1, tableNo);
            pst.setString(2, kotNo);
            ResultSet rs_checkKOT = pst.executeQuery();
            boolean flag_first = true;
            String waiterName = "";
            while (rs_checkKOT.next())
            {
                if (flag_first)
                {
                    checkKotOut.write("  TABLE NAME : " + rs_checkKOT.getString(3));
                    if ("null".equalsIgnoreCase(rs_checkKOT.getString(5)))
                    {
                    }
                    else
                    {
                        if (rs_checkKOT.getString(5).length() > 0)
                        {
                            waiterName = rs_checkKOT.getString(6);
                        }
                        checkKotOut.newLine();
                        checkKotOut.write("  WAITER NAME: " + waiterName);
                    }

                    checkKotOut.newLine();
                    checkKotOut.write("  TIME: " + rs_checkKOT.getString(4));
                    checkKotOut.newLine();
                    checkKotOut.write(dashedLineFor40Chars);
                    checkKotOut.newLine();
                    checkKotOut.write("   QTY          ITEM NAME");
                    checkKotOut.newLine();
                    checkKotOut.write(dashedLineFor40Chars);
                    checkKotOut.newLine();

                    String itemqty = decimalFormat.format(rs_checkKOT.getDouble(2));
                    String kotItemName = rs_checkKOT.getString(1).trim().toUpperCase();

                    int noOfCharsToBePrinted = 30;
                    int noOfCharsToBePrintedX2 = noOfCharsToBePrinted * 2;
                    int qtyWidth = 6;

                    if (rs_checkKOT.getString(1).startsWith("-->"))
                    {
                        if (!clsGlobalVarClass.gPrintModQtyOnKOT)
                        {
                            if (kotItemName.length() <= noOfCharsToBePrinted)
                            {
                                checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + kotItemName);
                            }
                            else
                            {
                                if (kotItemName.length() >= noOfCharsToBePrintedX2)
                                {
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + kotItemName.substring(0, noOfCharsToBePrinted));
                                    checkKotOut.newLine();
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrinted, noOfCharsToBePrintedX2).trim());

                                    checkKotOut.newLine();
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrintedX2, kotItemName.length()).trim());
                                }
                                else
                                {
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + kotItemName.substring(0, noOfCharsToBePrinted));
                                    checkKotOut.newLine();
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrinted, kotItemName.length()).trim());
                                }
                            }
                        }
                        else
                        {
                            if (kotItemName.length() <= noOfCharsToBePrinted)
                            {
                                checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", itemqty) + kotItemName);
                            }
                            else
                            {
                                if (kotItemName.length() >= noOfCharsToBePrintedX2)
                                {
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", itemqty) + kotItemName.substring(0, noOfCharsToBePrinted));
                                    checkKotOut.newLine();
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrinted, noOfCharsToBePrintedX2).trim());

                                    checkKotOut.newLine();
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrintedX2, kotItemName.length()).trim());
                                }
                                else
                                {
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", itemqty) + kotItemName.substring(0, noOfCharsToBePrinted));
                                    checkKotOut.newLine();
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrinted, kotItemName.length()).trim());
                                }
                            }
                        }

                    }
                    else
                    {
                        if (kotItemName.length() <= noOfCharsToBePrinted)
                        {
                            checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", itemqty) + kotItemName);
                        }
                        else
                        {
                            if (kotItemName.length() >= noOfCharsToBePrintedX2)
                            {
                                checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", itemqty) + kotItemName.substring(0, noOfCharsToBePrinted));
                                checkKotOut.newLine();
                                checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrinted, noOfCharsToBePrintedX2).trim());

                                checkKotOut.newLine();
                                checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrintedX2, kotItemName.length()).trim());
                            }
                            else
                            {
                                checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", itemqty) + kotItemName.substring(0, noOfCharsToBePrinted));
                                checkKotOut.newLine();
                                checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrinted, kotItemName.length()).trim());
                            }
                        }
                    }

                    flag_first = false;
                }
                else
                {
                    String itemqty = decimalFormat.format(rs_checkKOT.getDouble(2));
                    String kotItemName = rs_checkKOT.getString(1).trim().toUpperCase();

                    int noOfCharsToBePrinted = 30;
                    int noOfCharsToBePrintedX2 = noOfCharsToBePrinted * 2;
                    int qtyWidth = 6;

                    if (rs_checkKOT.getString(1).startsWith("-->"))
                    {
                        if (!clsGlobalVarClass.gPrintModQtyOnKOT)
                        {
                            if (kotItemName.length() <= noOfCharsToBePrinted)
                            {
                                checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + kotItemName);
                            }
                            else
                            {
                                if (kotItemName.length() >= noOfCharsToBePrintedX2)
                                {
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + kotItemName.substring(0, noOfCharsToBePrinted));
                                    checkKotOut.newLine();
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrinted, noOfCharsToBePrintedX2).trim());

                                    checkKotOut.newLine();
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrintedX2, kotItemName.length()).trim());
                                }
                                else
                                {
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + kotItemName.substring(0, noOfCharsToBePrinted));
                                    checkKotOut.newLine();
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrinted, kotItemName.length()).trim());
                                }
                            }
                        }
                        else
                        {
                            if (kotItemName.length() <= noOfCharsToBePrinted)
                            {
                                checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", itemqty) + kotItemName);
                            }
                            else
                            {
                                if (kotItemName.length() >= noOfCharsToBePrintedX2)
                                {
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", itemqty) + kotItemName.substring(0, noOfCharsToBePrinted));
                                    checkKotOut.newLine();
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrinted, noOfCharsToBePrintedX2).trim());

                                    checkKotOut.newLine();
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrintedX2, kotItemName.length()).trim());
                                }
                                else
                                {
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", itemqty) + kotItemName.substring(0, noOfCharsToBePrinted));
                                    checkKotOut.newLine();
                                    checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrinted, kotItemName.length()).trim());
                                }
                            }
                        }

                    }
                    else
                    {
                        if (kotItemName.length() <= noOfCharsToBePrinted)
                        {
                            checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", itemqty) + kotItemName);
                        }
                        else
                        {
                            if (kotItemName.length() >= noOfCharsToBePrintedX2)
                            {
                                checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", itemqty) + kotItemName.substring(0, noOfCharsToBePrinted));
                                checkKotOut.newLine();
                                checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrinted, noOfCharsToBePrintedX2).trim());

                                checkKotOut.newLine();
                                checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrintedX2, kotItemName.length()).trim());
                            }
                            else
                            {
                                checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", itemqty) + kotItemName.substring(0, noOfCharsToBePrinted));
                                checkKotOut.newLine();
                                checkKotOut.write("   " + String.format("%-" + qtyWidth + "s", "") + "" + kotItemName.substring(noOfCharsToBePrinted, kotItemName.length()).trim());
                            }
                        }
                    }
                }
                checkKotOut.newLine();
            }

            checkKotOut.newLine();
            checkKotOut.newLine();
            checkKotOut.newLine();
            checkKotOut.newLine();
            checkKotOut.newLine();
            //checkKotOut.write("m"); //windows

            if ("linux".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
            {
                checkKotOut.write("V");//Linux
            }
            else if ("windows".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
            {
                if ("Inbuild".equalsIgnoreCase(clsPosConfigFile.gPrinterType))
                {
                    checkKotOut.write("V");
                }
                else
                {
                    checkKotOut.write("m");//windows
                }
            }
            rs_checkKOT.close();
            checkKotOut.close();
            fstream.close();

//            if (clsGlobalVarClass.gShowBill)
//            {
//                funShowTextFile(Text_Check_KOT, "", "");
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
