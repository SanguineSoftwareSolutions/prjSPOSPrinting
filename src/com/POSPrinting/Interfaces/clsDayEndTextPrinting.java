/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting.Interfaces;

import com.POSGlobal.controller.clsPostPOSItemSalesDataInPOS;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Harry
 */
public interface clsDayEndTextPrinting
{

    /**
     *
     * @param file
     * @param mapOfPendingRecipesData
     * @param posName
     * @param dteDate
     * @return
     */
    public int funGeneratePendingRecipesTextReport(File file, Map<String, List<clsPostPOSItemSalesDataInPOS>> mapOfPendingRecipesData, String posName, String dteDate);
}
