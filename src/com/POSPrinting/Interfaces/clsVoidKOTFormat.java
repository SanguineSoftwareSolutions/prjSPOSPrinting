/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting.Interfaces;

import java.util.HashMap;

/**
 *
 * @author Sanguine
 */
public interface clsVoidKOTFormat
{

    /**
     *
     * @param KOT_TableNo
     * @param KotNo
     * @param text
     * @param costCenterCode
     * @param mapVoidedItem
     */
    public void funGenerateVoidKOT(String KOT_TableNo, String KotNo, String text, String costCenterCode, HashMap<String, String> mapVoidedItem);
}
