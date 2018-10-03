/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting.Text.Bill;

import com.POSPrinting.Utility.clsPrintingUtility;
import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsPosConfigFile;
import com.POSGlobal.controller.clsUtility;
import com.POSGlobal.controller.clsUtility2;
import com.POSPrinting.Interfaces.clsBillGenerationFormat;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 *
 * @author Ajim
 * @date Aug 26, 2017
 */
public class clsTextFormat11ForBill implements clsBillGenerationFormat
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
     * @param billNo
     * @param reprint
     * @param formName
     * @param transType
     * @param billDate
     * @param posCode
     * @param viewORprint
     */
    @Override
    public void funGenerateBill(String billNo, String reprint, String formName, String transType, String billDate, String posCode, String viewORprint)
    {
        clsUtility objUtility = new clsUtility();
        String lineFor2Inch = "--------------------";
        try
        {
            String user = "";
            String customerCode = "";
            String billhd;
            String billdtl;
            String billModifierdtl;
            String billSettlementdtl;
            String billtaxdtl;
            String billDscFrom = "tblbilldiscdtl";
            String billPromoDtl;
            if (clsGlobalVarClass.gHOPOSType.equalsIgnoreCase("HOPOS"))
            {
                billhd = "tblqbillhd";
                billdtl = "tblqbilldtl";
                billModifierdtl = "tblqbillmodifierdtl";
                billSettlementdtl = "tblqbillsettlementdtl";
                billtaxdtl = "tblqbilltaxdtl";
                billDscFrom = "tblqbilldiscdtl";
                billPromoDtl = "tblqbillpromotiondtl";
            }
            else
            {
                if ("sales report".equalsIgnoreCase(formName))
                {
                    billhd = "tblbillhd";
                    billdtl = "tblbilldtl";
                    billModifierdtl = "tblbillmodifierdtl";
                    billSettlementdtl = "tblbillsettlementdtl";
                    billtaxdtl = "tblbilltaxdtl";
                    billDscFrom = "tblbilldiscdtl";
                    billPromoDtl = "tblbillpromotiondtl";

                    long dateDiff = new clsUtility().funCompareDate(billDate, objUtility.funGetPOSDateForTransaction());
                    if (dateDiff > 0)
                    {
                        billhd = "tblqbillhd";
                        billdtl = "tblqbilldtl";
                        billModifierdtl = "tblqbillmodifierdtl";
                        billSettlementdtl = "tblqbillsettlementdtl";
                        billtaxdtl = "tblqbilltaxdtl";
                        billDscFrom = "tblqbilldiscdtl";
                        billPromoDtl = "tblqbillpromotiondtl";
                    }
                }
                else
                {
                    billhd = "tblbillhd";
                    billdtl = "tblbilldtl";
                    billModifierdtl = "tblbillmodifierdtl";
                    billSettlementdtl = "tblbillsettlementdtl";
                    billtaxdtl = "tblbilltaxdtl";
                    billPromoDtl = "tblbillpromotiondtl";
                }
            }
            objPrintingUtility.funCreateTempFolder();
            String filePath = System.getProperty("user.dir");
            File fileName = new File(filePath + "/Temp/Temp_Bill.txt");
            String subTotal = "";
            String grandTotal = "";
            String advAmount = "";
            String deliveryCharge = "";
            FileWriter objFileWriter = new FileWriter(fileName);
            BufferedWriter objBillOut = new BufferedWriter(objFileWriter);
            boolean isReprint = false;
            if ("reprint".equalsIgnoreCase(reprint))
            {
                //funPrintBlankSpace("[DUPLICATE]", objBillOut);
                //objBillOut.write("[DUPLICATE]");
                isReprint = true;
                objBillOut.write(objUtility.funPrintTextWithAlignment("[DUPLICATE]", 20, "Center"));
                objBillOut.newLine();
            }

            if (transType.equals("Void"))
            {
                objBillOut.write(objUtility.funPrintTextWithAlignment("VOIDED BILL", 20, "Center"));
                objBillOut.newLine();
            }

            boolean flgHomeDeliveryBill = false;
            String sqlHomeDel = "select strBillNo,strCustomerCode,strDPCode,tmeTime "
                    + "from tblhomedelivery where strBillNo='" + billNo + "' ;";
            ResultSet rsHomeDelivery = clsGlobalVarClass.dbMysql.executeResultSet(sqlHomeDel);
            if (rsHomeDelivery.next())
            {
                flgHomeDeliveryBill = true;
                customerCode = rsHomeDelivery.getString(2);
                if (clsGlobalVarClass.gPrintHomeDeliveryYN)
                {
                    objBillOut.write(objUtility.funPrintTextWithAlignment("HOME DELIVERY", 20, "Center"));
                    objBillOut.newLine();
                }

                String sqlCustDtl = "select a.strCustomerName,a.strBuildingName"
                        + " ,a.strStreetName,a.strLandmark,a.strArea,a.strCity,a.longMobileNo,b.strAddress "
                        + " from tblcustomermaster a left outer join tblbuildingmaster b "
                        + " on a.strBuldingCode=b.strBuildingCode "
                        + " where a.strCustomerCode='" + customerCode + "' ;";
                ResultSet rsCustomerDtl = clsGlobalVarClass.dbMysql.executeResultSet(sqlCustDtl);
                while (rsCustomerDtl.next())
                {
                    objBillOut.write(objUtility.funPrintTextWithAlignment("NAME : ", 7, "Left"));
                    objBillOut.write(objUtility.funPrintTextWithAlignment(rsCustomerDtl.getString(1).toUpperCase(), 13, "Left"));
                    objBillOut.newLine();

                    if (null != rsCustomerDtl.getString(8) && rsCustomerDtl.getString(8).trim().length() > 0)
                    {
                        objBillOut.write(objUtility.funPrintTextWithAlignment("CUSTOMER ADDRESS", 20, "Center"));
                        objBillOut.newLine();

                        if (rsCustomerDtl.getString(8).length() > 20)
                        {
                            List listTextToPrint = objPrintingUtility.funGetTextWithSpecifiedSize(rsCustomerDtl.getString(8), 2);
                            for (int cnt = 0; cnt < listTextToPrint.size(); cnt++)
                            {
                                objBillOut.write(objUtility.funPrintTextWithAlignment(listTextToPrint.get(cnt).toString(), 20, "Center"));
                                objBillOut.newLine();
                            }
                        }
                        else
                        {
                            objBillOut.write(objUtility.funPrintTextWithAlignment(rsCustomerDtl.getString(8), 20, "Center"));
                            objBillOut.newLine();
                        }
                    }

                    if (rsCustomerDtl.getString(2).length() > 20)
                    {
                        List listTextToPrint = objPrintingUtility.funGetTextWithSpecifiedSize(rsCustomerDtl.getString(2), 2);
                        for (int cnt = 0; cnt < listTextToPrint.size(); cnt++)
                        {
                            objBillOut.write(objUtility.funPrintTextWithAlignment(listTextToPrint.get(cnt).toString(), 20, "Center"));
                            objBillOut.newLine();
                        }
                    }
                    else
                    {
                        objBillOut.write(objUtility.funPrintTextWithAlignment(rsCustomerDtl.getString(2), 20, "Center"));
                        objBillOut.newLine();
                    }

                    if (rsCustomerDtl.getString(3).length() > 20)
                    {
                        List listTextToPrint = objPrintingUtility.funGetTextWithSpecifiedSize(rsCustomerDtl.getString(3), 2);
                        for (int cnt = 0; cnt < listTextToPrint.size(); cnt++)
                        {
                            objBillOut.write(objUtility.funPrintTextWithAlignment(listTextToPrint.get(cnt).toString(), 20, "Center"));
                            objBillOut.newLine();
                        }
                    }
                    else
                    {
                        objBillOut.write(objUtility.funPrintTextWithAlignment(rsCustomerDtl.getString(3), 20, "Center"));
                        objBillOut.newLine();
                    }

                    if (rsCustomerDtl.getString(4).length() > 20)
                    {
                        List listTextToPrint = objPrintingUtility.funGetTextWithSpecifiedSize(rsCustomerDtl.getString(4), 2);
                        for (int cnt = 0; cnt < listTextToPrint.size(); cnt++)
                        {
                            objBillOut.write(objUtility.funPrintTextWithAlignment(listTextToPrint.get(cnt).toString(), 20, "Center"));
                            objBillOut.newLine();
                        }
                    }
                    else
                    {
                        objBillOut.write(objUtility.funPrintTextWithAlignment(rsCustomerDtl.getString(4), 20, "Center"));
                        objBillOut.newLine();
                    }

                    if (rsCustomerDtl.getString(6).length() > 20)
                    {
                        List listTextToPrint = objPrintingUtility.funGetTextWithSpecifiedSize(rsCustomerDtl.getString(6), 2);
                        for (int cnt = 0; cnt < listTextToPrint.size(); cnt++)
                        {
                            objBillOut.write(objUtility.funPrintTextWithAlignment(listTextToPrint.get(cnt).toString(), 20, "Center"));
                            objBillOut.newLine();
                        }
                    }
                    else
                    {
                        objBillOut.write(objUtility.funPrintTextWithAlignment(rsCustomerDtl.getString(6), 20, "Center"));
                        objBillOut.newLine();
                    }

                    objBillOut.write(objUtility.funPrintTextWithAlignment(rsCustomerDtl.getString(7), 20, "Center"));
                    objBillOut.newLine();
                }
                rsCustomerDtl.close();

                if (null != rsHomeDelivery.getString(3) && rsHomeDelivery.getString(3).trim().length() > 0)
                {
                    String sqlHomeDelBoy = "select strDPName from tbldeliverypersonmaster "
                            + "where strDPCode='" + rsHomeDelivery.getString(3) + "' ;";
                    ResultSet rsDeliveryBoyDtl = clsGlobalVarClass.dbMysql.executeResultSet(sqlHomeDelBoy);
                    if (rsDeliveryBoyDtl.next())
                    {
                        objBillOut.write(objUtility.funPrintTextWithAlignment("DELV BOY :", 10, "Left"));
                        objBillOut.write(objUtility.funPrintTextWithAlignment(rsDeliveryBoyDtl.getString(1), 10, "Left"));
                    }
                    objBillOut.newLine();
                    rsDeliveryBoyDtl.close();
                }

                objBillOut.write(lineFor2Inch);
                objBillOut.newLine();
            }
            rsHomeDelivery.close();

            objPrintingUtility.funPrintTakeAway(billhd, billNo, objBillOut, 2);
            if (clsGlobalVarClass.gPrintTaxInvoice.equalsIgnoreCase("Y"))
            {
                objBillOut.write(objUtility.funPrintTextWithAlignment("TAX INVOICE", 20, "Center"));
                objBillOut.newLine();
            }

            objBillOut.write(objUtility.funPrintTextWithAlignment(clsGlobalVarClass.gClientName, 20, "Center"));
            objBillOut.newLine();

            if (clsGlobalVarClass.gClientAddress1.length() > 20)
            {
                List listTextToPrint = objPrintingUtility.funGetTextWithSpecifiedSize(clsGlobalVarClass.gClientAddress1, 2);
                for (int cnt = 0; cnt < listTextToPrint.size(); cnt++)
                {
                    objBillOut.write(objUtility.funPrintTextWithAlignment(listTextToPrint.get(cnt).toString(), 20, "Center"));
                    objBillOut.newLine();
                }
            }
            else
            {
                objBillOut.write(objUtility.funPrintTextWithAlignment(clsGlobalVarClass.gClientAddress1, 20, "Center"));
                objBillOut.newLine();
            }

            if (clsGlobalVarClass.gClientAddress2.trim().length() > 0)
            {
                if (clsGlobalVarClass.gClientAddress1.length() > 20)
                {
                    List listTextToPrint = objPrintingUtility.funGetTextWithSpecifiedSize(clsGlobalVarClass.gClientAddress2, 2);
                    for (int cnt = 0; cnt < listTextToPrint.size(); cnt++)
                    {
                        objBillOut.write(objUtility.funPrintTextWithAlignment(listTextToPrint.get(cnt).toString(), 20, "Center"));
                        objBillOut.newLine();
                    }
                }
                else
                {
                    objBillOut.write(objUtility.funPrintTextWithAlignment(clsGlobalVarClass.gClientAddress2, 20, "Center"));
                    objBillOut.newLine();
                }
            }

            if (clsGlobalVarClass.gClientAddress3.trim().length() > 0)
            {
                if (clsGlobalVarClass.gClientAddress1.length() > 20)
                {
                    List listTextToPrint = objPrintingUtility.funGetTextWithSpecifiedSize(clsGlobalVarClass.gClientAddress3, 2);
                    for (int cnt = 0; cnt < listTextToPrint.size(); cnt++)
                    {
                        objBillOut.write(objUtility.funPrintTextWithAlignment(listTextToPrint.get(cnt).toString(), 20, "Center"));
                        objBillOut.newLine();
                    }
                }
                else
                {
                    objBillOut.write(objUtility.funPrintTextWithAlignment(clsGlobalVarClass.gClientAddress3, 20, "Center"));
                    objBillOut.newLine();
                }
            }

            if (clsGlobalVarClass.gCityName.trim().length() > 0)
            {
                objBillOut.write(objUtility.funPrintTextWithAlignment(clsGlobalVarClass.gCityName, 20, "Center"));
                objBillOut.newLine();
            }

            //objBillOut.write(objUtility.funPrintTextWithAlignment("TEL NO. : ", 10, "Left"));
            if (clsGlobalVarClass.gClientTelNo.contains(","))
            {
                String[] arrTelNo = clsGlobalVarClass.gClientTelNo.split(",");
                for (int cnt = 0; cnt < arrTelNo.length; cnt++)
                {
                    objBillOut.write(objUtility.funPrintTextWithAlignment(arrTelNo[cnt], 20, "Left"));
                    objBillOut.newLine();
                }
            }
            else if (clsGlobalVarClass.gClientTelNo.contains("/"))
            {
                String[] arrTelNo = clsGlobalVarClass.gClientTelNo.split("/");
                for (int cnt = 0; cnt < arrTelNo.length; cnt++)
                {
                    objBillOut.write(objUtility.funPrintTextWithAlignment(arrTelNo[cnt], 20, "Left"));
                    objBillOut.newLine();
                }
            }
            else
            {
                objBillOut.write(objUtility.funPrintTextWithAlignment(String.valueOf(clsGlobalVarClass.gClientTelNo), 20, "Left"));
                objBillOut.newLine();
            }

            //objBillOut.write(objUtility.funPrintTextWithAlignment("EMAIL   : ", 10, "Left"));
            objBillOut.write(objUtility.funPrintTextWithAlignment(String.valueOf(clsGlobalVarClass.gClientEmail), 20, "Left"));
            objBillOut.newLine();

            String query = "";
            String sqlBillHD = "";
            String waiterName = "";
            String waiterNo = "";
            String tblName = "";
            ResultSet rsBillHd = null;
            ResultSet rsTblName = null;
            String sqlTblName = "";
            String tabNo = "";
            double discPer = 0;
            double discAmt = 0;
            boolean flgDirectBillerBill = false;
            if (objPrintingUtility.funIsDirectBillerBill(billNo, billhd))
            {
                flgDirectBillerBill = true;
                sqlBillHD = "select a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
                        + "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated "
                        + ",ifnull(dblDeliveryCharges,0.00),ifnull(b.dblAdvDeposite,0.00),a.dblDiscountPer ,c.strCustomerName,c.longMobileNo"
                        + ",strTakeAwayRemarks "
                        + "from " + billhd + " a left outer join tbladvancereceipthd b on a.strAdvBookingNo=b.strAdvBookingNo "
                        + "left outer join tblcustomermaster c on c.strCustomerCode=a.strCustomerCode "
                        + "where a.strBillNo='" + billNo + "'  "
                        + " and date(a.dteBillDate)='"+billDate+"' ";
                flgDirectBillerBill = true;
                rsBillHd = clsGlobalVarClass.dbMysql.executeResultSet(sqlBillHD);
                rsBillHd.next();
                discAmt = rsBillHd.getDouble(3);
                discPer = rsBillHd.getDouble(13);;
            }
            else
            {
                sqlBillHD = "select a.strTableNo,a.strWaiterNo,a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
                        + "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated "
                        + ",dblDeliveryCharges,ifnull(c.dblAdvDeposite,0.00),a.dblDiscountPer "
                        + "from " + billhd + " a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
                        + "left outer join tbladvancereceipthd c on a.strAdvBookingNo=c.strAdvBookingNo "
                        + "where a.strBillNo='" + billNo + "' and b.strOperational='Y'  "
                        + " and date(a.dteBillDate)='"+billDate+"' ";
                rsBillHd = clsGlobalVarClass.dbMysql.executeResultSet(sqlBillHD);
                rsBillHd.next();
                tabNo = rsBillHd.getString(1);
                discAmt = rsBillHd.getDouble(5);
                discPer = rsBillHd.getDouble(15);;
                if (rsBillHd.getString(2).equalsIgnoreCase("null") || rsBillHd.getString(2).equalsIgnoreCase(""))
                {
                    waiterNo = "";
                }
                else
                {
                    waiterNo = rsBillHd.getString(2);
                    query = "select strWShortName from tblwaitermaster where strWaiterNo= '" + waiterNo + "';";
                    ResultSet rsWaiter = clsGlobalVarClass.dbMysql.executeResultSet(query);
                    if (rsWaiter.next())
                    {
                        waiterName = rsWaiter.getString(1);
                    }
                    rsWaiter.close();
                }

                sqlTblName = "select strTableName from tbltablemaster where strTableNo='" + tabNo + "' ;";
                rsTblName = clsGlobalVarClass.dbMysql.executeResultSet(sqlTblName);
                if (rsTblName.next())
                {
                    tblName = rsTblName.getString(1);
                }
                rsTblName.close();
            }

            if (flgDirectBillerBill)
            {
                objBillOut.write(lineFor2Inch);
                objBillOut.newLine();

                objBillOut.write(objUtility.funPrintTextWithAlignment("BILL NO : ", 10, "Left"));
                objBillOut.write(objUtility.funPrintTextWithAlignment(billNo, 10, "Left"));
                objBillOut.newLine();

                SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
                String date = ft.format(rsBillHd.getObject(1));
                objBillOut.write(objUtility.funPrintTextWithAlignment("DATE    : ", 10, "Left"));
                objBillOut.write(objUtility.funPrintTextWithAlignment(date, 10, "Left"));
                objBillOut.newLine();

                if (clsGlobalVarClass.gRemarksOnTakeAway)
                {
                    if (!rsBillHd.getObject(16).equals(""))
                    {
                        objBillOut.write(objUtility.funPrintTextWithAlignment("CUSTOMER: ", 10, "Left"));
                        objBillOut.write(objUtility.funPrintTextWithAlignment(rsBillHd.getString(16).toUpperCase(), 10, "Left"));
                        objBillOut.newLine();
                    }
                }
                objBillOut.newLine();

                subTotal = rsBillHd.getString(4);
                user = rsBillHd.getString(10);
                deliveryCharge = rsBillHd.getString(11);
                advAmount = rsBillHd.getString(12);
            }
            else
            {
                objBillOut.write(objUtility.funPrintTextWithAlignment("TABLE   : ", 10, "Left"));
                objBillOut.write(objUtility.funPrintTextWithAlignment(tblName, 10, "Left"));
                objBillOut.newLine();

                if (waiterName.trim().length() > 0)
                {
                    objBillOut.write(objUtility.funPrintTextWithAlignment("STEWARD : ", 10, "Left"));
                    objBillOut.write(objUtility.funPrintTextWithAlignment(waiterName, 10, "Left"));
                    objBillOut.newLine();
                }

                objBillOut.write(lineFor2Inch);
                objBillOut.newLine();

                objBillOut.write(objUtility.funPrintTextWithAlignment("BILL NO : ", 10, "Left"));
                objBillOut.write(objUtility.funPrintTextWithAlignment(billNo, 10, "Left"));
                objBillOut.newLine();

                SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
                String date = ft.format(rsBillHd.getObject(3));
                objBillOut.write(objUtility.funPrintTextWithAlignment("DATE    : ", 10, "Left"));
                objBillOut.write(objUtility.funPrintTextWithAlignment(date, 10, "Left"));
                objBillOut.newLine();

                subTotal = rsBillHd.getString(6);
                grandTotal = rsBillHd.getString(8);
                user = rsBillHd.getString(12);
                deliveryCharge = rsBillHd.getString(13);
                advAmount = rsBillHd.getString(14);
            }

            /*
             * if (clsGlobalVarClass.gCMSIntegrationYN) { sql = "select
             * b.strCustomerCode,b.strCustomerName from " + billhd + "
             * a,tblcustomermaster b " + " where
             * a.strCustomerCode=b.strCustomerCode and a.strBillNo='" + billNo +
             * "'"; rsBillPrint =
             * clsGlobalVarClass.dbMysql.executeResultSet(sql); if
             * (rsBillPrint.next()) { BillOut.write(" Member Code : ");
             * BillOut.write(rsBillPrint.getString(1)); BillOut.newLine();
             * BillOut.write(" Member Name : ");
             * BillOut.write(rsBillPrint.getString(2)); BillOut.newLine(); }
             * rsBillPrint.close(); } BillOut.write(Line);
             */

            /*
             * String sqlAdvOrder = "select
             * a.strAdvBookingNo,a.strMessage,a.strShape,a.strNote" + " from
             * tbladvbookbillhd a, " + billhd + " b where
             * a.strAdvBookingNo=b.strAdvBookingNo " + "and
             * b.strBillNo='"+billNo+"' "; ResultSet rsAdvOrder =
             * clsGlobalVarClass.dbMysql.executeResultSet(sqlAdvOrder); if
             * (rsAdvOrder.next()) {
             * objBillOut.write(objUtility.funPrintTextWithAlignment("ORDER
             * DETAIL", 20, "Left")); objBillOut.newLine();
             *
             * objBillOut.write(Line); objBillOut.newLine();
             *
             * String msg = rsAdvOrder.getString(2).toUpperCase(); String shape
             * = rsAdvOrder.getString(3).toUpperCase(); String note =
             * rsAdvOrder.getString(4).toUpperCase(); int strlenMsg =
             * msg.length(); if (strlenMsg > 0) { String msg1 = ""; if
             * (strlenMsg < 27) { msg1 = msg.substring(0, strlenMsg);
             * objBillOut.write(objUtility.funPrintTextWithAlignment("MESSAGE :
             * ", 10, "Left"));
             * objBillOut.write(objUtility.funPrintTextWithAlignment(msg1, 10,
             * "Left")); objBillOut.newLine(); } else { msg1 = msg.substring(0,
             * 27);
             * objBillOut.write(objUtility.funPrintTextWithAlignment("MESSAGE :
             * ", 10, "Left"));
             * objBillOut.write(objUtility.funPrintTextWithAlignment(msg1, 10,
             * "Left")); objBillOut.newLine(); BillOut.write(" MESSAGE :" +
             * msg1); BillOut.newLine(); } for (int i = 27; i <= strlenMsg; i++)
             * { int endmsg = 0; endmsg = i + 27; if (strlenMsg > endmsg) { msg1
             * = msg.substring(i, endmsg); i = endmsg; BillOut.write(" " +
             * msg1); BillOut.newLine(); } else { msg1 = msg.substring(i,
             * strlenMsg); BillOut.write(" " + msg1); BillOut.newLine(); i =
             * strlenMsg + 1; } } }
             *
             * int strlenShape = shape.length(); if (strlenShape > 0) {
             *
             * String shape1 = ""; if (strlenShape < 27) { shape1 =
             * shape.substring(0, strlenShape); BillOut.write(" SHAPE :" +
             * shape1); BillOut.newLine(); } else { shape1 = shape.substring(0,
             * 27); BillOut.write(" SHAPE :" + shape1); BillOut.newLine(); } for
             * (int j = 27; j <= strlenShape; j++) { int endShape = 0; endShape
             * = j + 27; if (strlenShape > endShape) { shape1 =
             * shape.substring(j, endShape); j = endShape; BillOut.write(" " +
             * shape1); BillOut.newLine(); } else { shape1 = shape.substring(j,
             * strlenShape); BillOut.write(" " + shape1); BillOut.newLine(); j =
             * strlenShape + 1; } } } int strlenNote = note.length(); if
             * (strlenNote > 0) {
             *
             * String note1 = ""; if (strlenNote < 27) { note1 =
             * note.substring(0, strlenNote); BillOut.write(" NOTE :" + note1);
             * BillOut.newLine(); } else { note1 = note.substring(0, 27);
             * BillOut.write(" NOTE :" + note1); BillOut.newLine(); } for (int i
             * = 27; i <= strlenNote; i++) { int endNote = 0; endNote = i + 27;
             * if (strlenNote > endNote) { note1 = note.substring(i, endNote); i
             * = endNote; BillOut.write(" " + note1); BillOut.newLine(); } else
             * { note1 = note.substring(i, strlenNote); BillOut.write(" " +
             * note1); BillOut.newLine(); i = strlenNote + 1; } } }
             * BillOut.newLine(); BillOut.write(Line); }
             */
            objBillOut.write(lineFor2Inch);
            objBillOut.newLine();
            objBillOut.write(objUtility.funPrintTextWithAlignment("ITEM NAME", 20, "Left"));
            objBillOut.newLine();
            objBillOut.write(objUtility.funPrintTextWithAlignment(" QTY   RATE   AMOUNT", 20, "Left"));
            objBillOut.newLine();
            objBillOut.write(lineFor2Inch);
            objBillOut.newLine();

            String sqlGroupName = "select b.strItemType "
                    + " from " + billdtl + " a "
                    + " left outer join tblitemmaster b on a.strItemCode=b.strItemCode "
                    + " where a.strBillNo='" + billNo + "' and a.tdhYN='N' and date(a.dteBillDate)='"+billDate+"' "
                    + " group by b.strItemType,a.strItemCode "
                    + " order by b.strItemType,a.strItemCode ";
            ResultSet rsItemType = clsGlobalVarClass.dbMysql.executeResultSet(sqlGroupName);
            String itemType = "";
            if (rsItemType.next())
            {
                itemType = rsItemType.getString(1);
            }
            String sqlBillDtl = "select SUM(a.dblQuantity),a.strItemName,SUM(a.dblAmount),a.strItemCode,a.strKOTNo "
                    + " ,b.strItemType,a.dblRate "
                    + " from " + billdtl + " a "
                    + " left outer join tblitemmaster b on a.strItemCode=b.strItemCode "
                    + " where a.strBillNo='" + billNo + "' and a.tdhYN='N' and date(a.dteBillDate)='"+billDate+"' "
                    + " group by b.strItemType,a.strItemCode "
                    + " order by b.strItemType,a.strItemCode ";
            ResultSet rsBillDtl = clsGlobalVarClass.dbMysql.executeResultSet(sqlBillDtl);
            itemType = "";
            while (rsBillDtl.next())
            {
                double amount = rsBillDtl.getDouble(3);
                amount = Double.parseDouble(stdDecimalFormat.format(amount));

                if (rsBillDtl.getString(2).toUpperCase().length() > 20)
                {
                    List listTextToPrint = objPrintingUtility.funGetTextWithSpecifiedSize(rsBillDtl.getString(2).toUpperCase(), 2);
                    for (int cnt = 0; cnt < listTextToPrint.size(); cnt++)
                    {
                        objBillOut.write(objUtility.funPrintTextWithAlignment(listTextToPrint.get(cnt).toString(), 20, "Left"));
                        objBillOut.newLine();
                    }
                }
                else
                {
                    objBillOut.write(objUtility.funPrintTextWithAlignment(rsBillDtl.getString(2).toUpperCase(), 20, "Left"));
                    objBillOut.newLine();
                }

                objBillOut.write(objUtility.funPrintTextWithAlignment(rsBillDtl.getString(1), 6, "Right"));
                objBillOut.write(objUtility.funPrintTextWithAlignment(rsBillDtl.getString(7), 7, "Right"));
                objBillOut.write(objUtility.funPrintTextWithAlignment(String.valueOf(amount), 7, "Right"));
                objBillOut.newLine();

                String sqlModifier = "select count(*) "
                        + " from " + billModifierdtl + " "
                        + " where strBillNo='" + billNo + "' and left(strItemCode,7)='" + rsBillDtl.getString(4) + "' "
                        + "and date(dteBillDate)='"+billDate+"' ";
                if (!clsGlobalVarClass.gPrintZeroAmtModifierOnBill)
                {
                    sqlModifier += " and  dblAmount !=0.00 ";
                }
                ResultSet rsModCount = clsGlobalVarClass.dbMysql.executeResultSet(sqlModifier);
                rsModCount.next();
                int cntRecord = rsModCount.getInt(1);
                rsModCount.close();
                if (cntRecord > 0)
                {

                    /*
                     * String sqlModifierDtl = " SELECT
                     * b.strModifierName,b.dblQuantity,b.dblAmount,a.strDefaultModifier"
                     * + ",b.strDefaultModifierDeselectedYN,b.dblRate " + " FROM
                     * " + billModifierdtl + " b,tblitemmodofier a " + " WHERE
                     * a.strItemCode=left(b.strItemCode,7) and
                     * a.strModifierCode=b.strModifierCode " + " and
                     * b.strBillNo='" + billNo + "' AND LEFT(b.strItemCode,7)='"
                     * + rsBillDtl.getString(4) + "' "; if
                     * (!clsGlobalVarClass.gPrintZeroAmtModifierOnBill) {
                     * sqlModifierDtl += " and b.dblAmount !=0.00 ;"; }
                     */
                    String SQL_ModifierDtl = "select strModifierName,dblQuantity,dblAmount "
                            + "from " + billModifierdtl + " where strBillNo=? and left(strItemCode,7)=? "
                            + "and date(dteBillDate)='"+billDate+"' ";
                    if (!clsGlobalVarClass.gPrintZeroAmtModifierOnBill)
                    {
                        SQL_ModifierDtl += " and  dblAmount !=0.00 ";
                    }

                    ResultSet rsModifierDtl = clsGlobalVarClass.dbMysql.executeResultSet(SQL_ModifierDtl);
                    while (rsModifierDtl.next())
                    {
                        if (rsModifierDtl.getString(4).equalsIgnoreCase("N"))
                        {
                            if (rsModifierDtl.getString(1).toUpperCase().length() > 20)
                            {
                                List listTextToPrint = objPrintingUtility.funGetTextWithSpecifiedSize(rsModifierDtl.getString(1).toUpperCase(), 2);
                                for (int cnt = 0; cnt < listTextToPrint.size(); cnt++)
                                {
                                    objBillOut.write(objUtility.funPrintTextWithAlignment(listTextToPrint.get(cnt).toString(), 20, "Left"));
                                    objBillOut.newLine();
                                }
                            }
                            else
                            {
                                objBillOut.write(objUtility.funPrintTextWithAlignment(rsModifierDtl.getString(1).toUpperCase(), 20, "Left"));
                                objBillOut.newLine();
                            }

                            objBillOut.write(objUtility.funPrintTextWithAlignment(rsModifierDtl.getString(2), 6, "Right"));
                            objBillOut.write(objUtility.funPrintTextWithAlignment(rsModifierDtl.getString(6), 7, "Right"));
                            objBillOut.write(objUtility.funPrintTextWithAlignment(rsModifierDtl.getString(3), 7, "Right"));
                            objBillOut.newLine();
                        }
                    }
                    rsModifierDtl.close();
                }
            }

            rsBillDtl.close();

            //funPrintPromoItemsInBill(billNo, objBillOut,2);  // Print Promotion Items in Bill for this billno.
            objBillOut.write(lineFor2Inch);
            objBillOut.newLine();

            /*
             * if (clsGlobalVarClass.gPointsOnBillPrint) { String sql_CRMPoints
             * = "select b.dblPoints from tblbillhd a, tblcrmpoints b " + "where
             * a.strBillNo=b.strBillNo and a.strBillNo='" + billNo + "'";
             * ResultSet rsCRMPoints =
             * clsGlobalVarClass.dbMysql.executeResultSet(sql_CRMPoints); if
             * (rsCRMPoints.next()) { funWriteTotal("POINTS ",
             * rsCRMPoints.getString(1), BillOut, "Format1"); }
             * rsCRMPoints.close(); BillOut.newLine(); }
             */
            if (discAmt > 0)
            {
                //funWriteTotal("DISCOUNT  " + discPer + "%",String.valueOf(discAmt), objBillOut, "Format11");
                objBillOut.write(String.format("%-12s", "DISC " + discPer + "%") + String.format("%8s", String.valueOf(discAmt)));
                objBillOut.newLine();

            }

            objBillOut.write(objUtility.funPrintTextWithAlignment("SUB TOTAL", 10, "Left"));
            objBillOut.write(objUtility.funPrintTextWithAlignment(subTotal, 10, "Right"));
            objBillOut.newLine();

            String sqlBillTaxDtl = "select b.strTaxDesc,sum(a.dblTaxAmount) "
                    + " from " + billtaxdtl + " a,tbltaxhd b "
                    + " where a.strBillNo='" + billNo + "' and a.strTaxCode=b.strTaxCode "
                    + " and b.strTaxCalculation='Forward' "
                    + " group by a.strTaxCode";
            ResultSet rsTax = clsGlobalVarClass.dbMysql.executeResultSet(sqlBillTaxDtl);
            while (rsTax.next())
            {
                String taxDesc = rsTax.getString(1);
                if (taxDesc.trim().length() > 10)
                {
                    taxDesc = taxDesc.trim().substring(0, 10);
                }
                objBillOut.write(objUtility.funPrintTextWithAlignment(taxDesc, 20, "Left"));
                objBillOut.newLine();
                objBillOut.write(objUtility.funPrintTextWithAlignment(rsTax.getString(2), 20, "Right"));
                objBillOut.newLine();
            }

            if (deliveryCharge != null && deliveryCharge.trim().length() > 0 && !"0.00".equalsIgnoreCase(deliveryCharge))
            {
                objBillOut.write(objUtility.funPrintTextWithAlignment("DEL CHARGE", 10, "Left"));
                objBillOut.write(objUtility.funPrintTextWithAlignment(deliveryCharge, 10, "Right"));
                objBillOut.newLine();
            }

            if (advAmount.trim().length() > 0 && !"0.00".equalsIgnoreCase(advAmount))
            {
                objBillOut.write(objUtility.funPrintTextWithAlignment("ADVANCE   ", 10, "Left"));
                objBillOut.write(objUtility.funPrintTextWithAlignment(advAmount, 10, "Right"));
                objBillOut.newLine();
            }

            objBillOut.write(lineFor2Inch);
            objBillOut.newLine();

            objBillOut.write(objUtility.funPrintTextWithAlignment("TOTAL     ", 10, "Left"));
            objBillOut.write(objUtility.funPrintTextWithAlignment(grandTotal, 10, "Right"));
            objBillOut.newLine();

            objBillOut.write(lineFor2Inch);
            objBillOut.newLine();

            //settlement breakup part
            String sqlBillSettlement = "select a.dblSettlementAmt ,b.strSettelmentDesc "
                    + " from " + billSettlementdtl + " a ,tblsettelmenthd b "
                    + " where a.strBillNo='" + billNo + "' and a.strSettlementCode=b.strSettelmentCode"
                    + " and date(a.dteBillDate)='"+billDate+"'";
            ResultSet rsBillSettlementDtl = clsGlobalVarClass.dbMysql.executeResultSet(sqlBillSettlement);
            while (rsBillSettlementDtl.next())
            {
                objBillOut.write(objUtility.funPrintTextWithAlignment(rsBillSettlementDtl.getString(2), 20, "Left"));
                objBillOut.newLine();
                objBillOut.write(objUtility.funPrintTextWithAlignment(rsBillSettlementDtl.getString(1), 20, "Right"));
                objBillOut.newLine();
            }
            rsBillSettlementDtl.close();

            if (flgHomeDeliveryBill)
            {
                objBillOut.write(lineFor2Inch);
                objBillOut.newLine();
                String sqlHomeDelCount = "select count(*) from tblhomedelivery where strCustomerCode='" + customerCode + "'";
                ResultSet rsCount = clsGlobalVarClass.dbMysql.executeResultSet(sqlHomeDelCount);
                if (rsCount.next())
                {
                    objBillOut.write(objUtility.funPrintTextWithAlignment("CUST COUNT : ", 13, "Left"));
                    objBillOut.newLine();
                    objBillOut.write(objUtility.funPrintTextWithAlignment(rsCount.getString(1), 7, "Right"));
                    objBillOut.newLine();
                    rsCount.close();
                }
            }

            //footer part
            if (clsGlobalVarClass.gPrintInclusiveOfAllTaxes.equalsIgnoreCase("Y"))
            {
                objBillOut.write(objUtility.funPrintTextWithAlignment("(INC. OF ALL TAXES)", 20, "Center"));
                objBillOut.newLine();
            }
            objBillOut.newLine();

            objPrintingUtility.funPrintServiceVatNo(objBillOut, 4, billNo, billDate, billtaxdtl);

            if (clsGlobalVarClass.gBillFooter.trim().length() > 20)
            {
                List listTextToPrint = objPrintingUtility.funGetTextWithSpecifiedSize(clsGlobalVarClass.gBillFooter.trim(), 2);
                for (int cnt = 0; cnt < listTextToPrint.size(); cnt++)
                {
                    objBillOut.write(objUtility.funPrintTextWithAlignment(listTextToPrint.get(cnt).toString(), 20, "Left"));
                    objBillOut.newLine();
                }
            }
            else
            {
                objBillOut.write(objUtility.funPrintTextWithAlignment(clsGlobalVarClass.gBillFooter.trim(), 20, "Left"));
                objBillOut.newLine();
            }
            /*
             * int num = clsGlobalVarClass.gBillFooter.trim().length() / 20; int
             * num1 = clsGlobalVarClass.gBillFooter.trim().length() % 20; int
             * cnt1 = 0; for (int cnt = 0; cnt < num; cnt++) { String footer =
             * clsGlobalVarClass.gBillFooter.trim().substring(cnt1, (cnt1 +
             * 20));
             * objBillOut.write(objUtility.funPrintTextWithAlignment(footer.trim(),
             * 20, "Left")); objBillOut.newLine(); cnt1 += 10; }
             * objBillOut.write(objUtility.funPrintTextWithAlignment(clsGlobalVarClass.gBillFooter.trim().substring(cnt1,
             * (cnt1 + num1)).trim(), 20, "Left")); objBillOut.newLine();
             */

            objBillOut.write(objUtility.funPrintTextWithAlignment(user, 20, "Left"));
            for (int i = 0; i < 5; i++)
            {
                objBillOut.newLine();
            }

            if ("linux".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
            {
                objBillOut.write(objUtility.funPrintTextWithAlignment("V", 20, "Left"));
            }
            else if ("windows".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
            {
                if ("Inbuild".equalsIgnoreCase(clsPosConfigFile.gPrinterType))
                {
                    objBillOut.write(objUtility.funPrintTextWithAlignment("V", 20, "Left"));
                }
                else if ("Star".equalsIgnoreCase(clsPosConfigFile.gPrinterType))
                {
                    objBillOut.write(objUtility.funPrintTextWithAlignment("P", 20, "Left"));
                }
                else
                {
                    objBillOut.write(objUtility.funPrintTextWithAlignment("m", 20, "Left"));
                }
            }
            // BillOut.write("V");//Linux
            rsBillHd.close();
            objBillOut.close();
            objFileWriter.close();

            if (formName.equalsIgnoreCase("sales report"))
            {
                objPrintingUtility.funShowTextFile(fileName, formName, "");
            }
            else
            {
                if (clsGlobalVarClass.gShowBill)
                {
                    objPrintingUtility.funShowTextFile(fileName, formName, "");
                }
            }
            if (!formName.equalsIgnoreCase("sales report"))
            {
                if (transType.equalsIgnoreCase("void"))
                {
                    if (clsGlobalVarClass.gPrintOnVoidBill)
                    {
                        if (!viewORprint.equalsIgnoreCase("view"))
                        {
                            objPrintingUtility.funPrintToPrinter(clsGlobalVarClass.gBillPrintPrinterPort, "", "bill", "N", isReprint,"");
                        }
                    }
                }
                else
                {
                    if (!viewORprint.equalsIgnoreCase("view"))
                    {
                        objPrintingUtility.funPrintToPrinter(clsGlobalVarClass.gBillPrintPrinterPort, "", "bill", "N", isReprint,"");
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
