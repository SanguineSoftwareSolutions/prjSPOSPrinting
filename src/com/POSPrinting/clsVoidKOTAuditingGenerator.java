/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting;

import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSPrinting.Interfaces.clsVoidKOTForAuditGenerationFormat;
import com.POSPrinting.Interfaces.clsVoidKOTFormat;
import com.POSPrinting.Jasper.KOT.clsJasperVoidKOT;
import com.POSPrinting.Text.KOT.Audit.clsJasperFormatForVoidKOTAudit;
import com.POSPrinting.Text.KOT.Audit.clsTextFormatForVoidKOTAudit;
import com.POSPrinting.Text.KOT.clsTextVoidKOT;
import java.util.HashMap;

/**
 *
 * @author Harry
 */
public class clsVoidKOTAuditingGenerator 
{
   private clsVoidKOTForAuditGenerationFormat objVoidKOTForAuditGenerationFormat;
    
    /**
     *
     * @param KOT_TableNo
     * @param KotNo
     * @param text
     * @param costCenterCode
     * @param mapVoidedItem
     */
    public void funGenerateVoidKOT(String KOT_TableNo, String KotNo, String text, String costCenterCode, HashMap<String, String> mapVoidedItem)
    {
        if (clsGlobalVarClass.gPrintType.equals("Jasper"))
        {
            objVoidKOTForAuditGenerationFormat=new clsJasperFormatForVoidKOTAudit();
            objVoidKOTForAuditGenerationFormat.funGenerateVoidKOT(KOT_TableNo, KotNo, text, costCenterCode, mapVoidedItem);
        }
        else//text
        {
            objVoidKOTForAuditGenerationFormat=new clsTextFormatForVoidKOTAudit();
            objVoidKOTForAuditGenerationFormat.funGenerateVoidKOT(KOT_TableNo, KotNo, text, costCenterCode, mapVoidedItem);
        }
    }  
}
