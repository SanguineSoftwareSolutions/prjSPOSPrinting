package com.POSPrinting;

import com.POSPrinting.Interfaces.clsBillGenerationFormat;
import com.POSPrinting.Stationary.clsStockTransferNoteFormatForBill;
import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsUtility2;
import com.POSPrinting.Jasper.Bill.clsJasperFormat10ForBill;
import com.POSPrinting.Jasper.Bill.clsJasperFormat1ForBill;
import com.POSPrinting.Jasper.Bill.clsJasperFormat2ForBill;
import com.POSPrinting.Jasper.Bill.clsJasperFormat3ForBill;
import com.POSPrinting.Jasper.Bill.clsJasperFormat4ForBill;
import com.POSPrinting.Jasper.Bill.clsJasperFormat5ForBill;
import com.POSPrinting.Jasper.Bill.clsJasperFormat6ForBill;
import com.POSPrinting.Jasper.Bill.clsJasperFormat7ForBill;
import com.POSPrinting.Jasper.Bill.clsJasperFormat8ForBill;
import com.POSPrinting.Jasper.Bill.clsJasperFormat9ForBill;
import com.POSPrinting.Stationary.clsStationaryFormat1ForBill;
import com.POSPrinting.Stationary.clsStationaryFormat2ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat10ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat11ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat12ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat13ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat14ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat15ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat16ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat17ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat18ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat19ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat1ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat20ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat2ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat3ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat4ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat5ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat6ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat7ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat8ForBill;
import com.POSPrinting.Text.Bill.clsTextFormat9ForBill;
import com.POSPrinting.Text.Bill.clsTextFormatForPlayZone;
import com.POSPrinting.Text.Bill.clsTextFormatSaloonForBill;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates and open the template
 * in the editor.
 */
/**
 *
 * @author Ajim
 * @date Aug 11, 2017
 */
public class clsBillGeneration
{

    private clsBillGenerationFormat objBillGenerationFormat;
    private clsUtility2 objUtility2 = new clsUtility2();

    /**
     *
     * @param billno
     * @param reprint
     * @param formName
     * @param transType
     * @param billDate
     * @param posCode
     * @param viewORPrint
     */
    public void funGenerateBill(String billno, String reprint, String formName, String transType, String billDate, String posCode, String viewORPrint)
    {

	switch (clsGlobalVarClass.gBillFormatType)
	{
	    case "Text 1":
		objBillGenerationFormat = new clsTextFormat1ForBill();
		break;

	    case "Text 2":
		objBillGenerationFormat = new clsTextFormat2ForBill();
		break;

	    case "Text 3":
		objBillGenerationFormat = new clsTextFormat3ForBill();
		break;

	    case "Text 4":
		objBillGenerationFormat = new clsTextFormat4ForBill();
		break;

	    case "Text 5":
		objBillGenerationFormat = new clsTextFormat5ForBill();
		break;

	    case "Text 6":
		objBillGenerationFormat = new clsTextFormat6ForBill();
		break;

	    case "Text 7":
		objBillGenerationFormat = new clsTextFormat7ForBill();
		break;

	    case "Text 8":
		objBillGenerationFormat = new clsTextFormat8ForBill();
		break;

	    case "Text 9":
		objBillGenerationFormat = new clsTextFormat9ForBill();
		break;

	    case "Text 10":
		objBillGenerationFormat = new clsTextFormat10ForBill();
		break;

	    case "Text 11":
		objBillGenerationFormat = new clsTextFormat11ForBill();
		break;

	    case "Text 12":
		objBillGenerationFormat = new clsTextFormat12ForBill();
		break;

	    case "Text 13":
		objBillGenerationFormat = new clsTextFormat13ForBill();
		break;

	    case "Text 14":
		objBillGenerationFormat = new clsTextFormat14ForBill();
		break;

	    case "Text 15":
		objBillGenerationFormat = new clsTextFormat15ForBill();
		break;

	    case "Text 16":
		objBillGenerationFormat = new clsTextFormat16ForBill();
		break;

	    case "Text 17":
		objBillGenerationFormat = new clsTextFormat17ForBill();
		break;

	    case "Text 18":
		objBillGenerationFormat = new clsTextFormat18ForBill();
		break;

	    case "Text 19":
		objBillGenerationFormat = new clsTextFormat19ForBill();//for order No on bill
		break;
	    case "Text 20":
		objBillGenerationFormat = new clsTextFormat20ForBill();//for HSN No on bill
		break;
	    case "Text PlayZone":
		objBillGenerationFormat = new clsTextFormatForPlayZone();//for PlayZone and HSN No on bill
		break;

	    case "Jasper 1":
		objBillGenerationFormat = new clsJasperFormat1ForBill();
		break;

	    case "Jasper 2":
		objBillGenerationFormat = new clsJasperFormat2ForBill();
		break;

	    case "Jasper 3":
		objBillGenerationFormat = new clsJasperFormat3ForBill();
		break;

	    case "Jasper 4":
		objBillGenerationFormat = new clsJasperFormat4ForBill();
		break;

	    case "Jasper 5":
		objBillGenerationFormat = new clsJasperFormat5ForBill();
		break;

	    case "Jasper 6":
		objBillGenerationFormat = new clsJasperFormat6ForBill();
		break;

	    case "Jasper 7":
		objBillGenerationFormat = new clsJasperFormat7ForBill();
		break;

	    case "Jasper 8":
		objBillGenerationFormat = new clsJasperFormat8ForBill();
		break;

	    case "Jasper 9":
		objBillGenerationFormat = new clsJasperFormat9ForBill();
		break;

	    case "Jasper 10":
		objBillGenerationFormat = new clsJasperFormat10ForBill();
		break;

	    case "Stationery 1":
		objBillGenerationFormat = new clsStationaryFormat1ForBill();
		break;

	    case "Stationery 2":
		objBillGenerationFormat = new clsStationaryFormat2ForBill();
		break;

	    case "Stock Transfer Note 1":
		objBillGenerationFormat = new clsStockTransferNoteFormatForBill();
		break;

	    case "Saloon 1":
		objBillGenerationFormat = new clsTextFormatSaloonForBill();//for Saloon
		break;
	}

	if (objBillGenerationFormat == null)
	{
	    objBillGenerationFormat = new clsTextFormat5ForBill();
	}

	objBillGenerationFormat.funGenerateBill(billno, reprint, formName, transType, billDate, posCode, viewORPrint);

    }

}
