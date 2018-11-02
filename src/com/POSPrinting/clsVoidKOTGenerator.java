/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting;

import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSPrinting.Interfaces.clsVoidKOTFormat;
import com.POSPrinting.Jasper.KOT.clsJasperVoidKOT;
import com.POSPrinting.Text.KOT.clsTextVoidKOT;
import java.util.HashMap;

/**
 *
 * @author Ajim
 * @date Aug 30, 2017
 */
public class clsVoidKOTGenerator
{

    private clsVoidKOTFormat objVoidKOTFormat;
    
    /**
     *
     * @param KOT_TableNo
     * @param KotNo
     * @param text
     * @param costCenterCode
     * @param mapVoidedItem
     */
    public void funGenerateVoidKOT(String KOT_TableNo, String KotNo, String text, String costCenterCode, HashMap<String, String> mapVoidedItem,int costCenterWiseCopies,String reprint)
    {
        if (clsGlobalVarClass.gPrintType.equals("Jasper"))
        {
            objVoidKOTFormat=new clsJasperVoidKOT();
	    objVoidKOTFormat.funGenerateVoidKOT(KOT_TableNo, KotNo, text, costCenterCode, mapVoidedItem,costCenterWiseCopies,reprint);
	    
        }
        else//text
        {
            objVoidKOTFormat=new clsTextVoidKOT();
            objVoidKOTFormat.funGenerateVoidKOT(KOT_TableNo, KotNo, text, costCenterCode, mapVoidedItem,costCenterWiseCopies,"");
        }
    }
}
