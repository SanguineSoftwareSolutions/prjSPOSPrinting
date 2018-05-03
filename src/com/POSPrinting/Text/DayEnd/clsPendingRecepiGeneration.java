/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting.Text.DayEnd;

import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsPosConfigFile;
import com.POSGlobal.controller.clsPostPOSItemSalesDataInPOS;
import com.POSPrinting.Interfaces.clsDayEndTextPrinting;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ajim
 */
public class clsPendingRecepiGeneration implements clsDayEndTextPrinting
{

    /**
     *
     * @param file
     * @param mapOfPendingRecipesData
     * @param posName
     * @param dteDate
     * @return
     */
    public int funGeneratePendingRecipesTextReport(File file, Map<String, List<clsPostPOSItemSalesDataInPOS>> mapOfPendingRecipesData, String posName, String dteDate)
    {
        int count = 0;

        try
        {

            PrintWriter pw = new PrintWriter(file);
            funPrintBlankLines(clsGlobalVarClass.gClientName, pw);
            funPrintBlankLines(clsGlobalVarClass.gClientAddress1, pw);
            if (clsGlobalVarClass.gClientAddress2.trim().length() > 0)
            {
                funPrintBlankLines(clsGlobalVarClass.gClientAddress2, pw);
            }
            if (clsGlobalVarClass.gClientAddress3.trim().length() > 0)
            {
                funPrintBlankLines(clsGlobalVarClass.gClientAddress3, pw);
            }
            funPrintBlankLines("Pending Recipes Report", pw);

            pw.println();
            pw.println("POS  :" + posName);
            pw.println("Date :" + dteDate);
//            pw.println("Cost Center Name  :" + costCenterName);
//            
            pw.println("---------------------------------------");
            pw.println("Item Code       Item Name       Cost Center Name        Remark");
            pw.println("--------------------------------------------------------------");

            for (Map.Entry<String, List<clsPostPOSItemSalesDataInPOS>> entry : mapOfPendingRecipesData.entrySet())
            {
                for (clsPostPOSItemSalesDataInPOS objBean : entry.getValue())
                {
                    pw.print(objBean.getStrPosItemCode() + "       " + objBean.getStrPosItemName() + "       " + objBean.getStrCostCenterName() + "        " + "Recipe Pending");
                }
            }

            if ("linux".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
            {
                pw.println("V");//Linux
            }
            else if ("windows".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
            {
                if ("Inbuild".equalsIgnoreCase(clsPosConfigFile.gPrinterType))
                {
                    pw.println("V");
                }
                else
                {
                    pw.println("m");//windows
                }
            }

            pw.flush();
            pw.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return count;
    }

    /**
     * Print blank Lines
     *
     * @param textToPrint
     * @param pw
     * @return
     */
    private int funPrintBlankLines(String textToPrint, PrintWriter pw)
    {
        pw.println();
        int len = 40 - textToPrint.length();
        len = len / 2;
        for (int cnt = 0; cnt < len; cnt++)
        {
            pw.print(" ");
        }
        pw.print(textToPrint);
        return len;
    }
}
