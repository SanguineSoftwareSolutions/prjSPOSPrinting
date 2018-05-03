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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author Ajim
 * @date Aug 26, 2017
 */
public class clsTextFormat8ForBill implements clsBillGenerationFormat
{

    private DecimalFormat decimalFormat = new DecimalFormat("#.###");
    private SimpleDateFormat ddMMyyyyAMPMDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
    private clsUtility objUtility = new clsUtility();
    private clsUtility2 objUtility2 = new clsUtility2();
    private clsPrintingUtility objPrintingUtility = new clsPrintingUtility();
    private final String dashedLineFor40Chars = "  --------------------------------------";

    //Bill Format For M/S Kaveri(SB)caterers

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

        String Linefor8 = "  --------------------------------------";
        try
        {

            String user = "";
            String billhd;
            String billdtl;
            String billModifierdtl;
            String billSettlementdtl;
            String billtaxdtl;
            String billPromoDtl;
            String billDscFrom;
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
            PreparedStatement pst = null;
            objPrintingUtility.funCreateTempFolder();
            String filePath = System.getProperty("user.dir");
            File Text_Bill = new File(filePath + "/Temp/Temp_Bill.txt");
            String subTotal = "";
            String discount = "";
            String tax = "";
            String grandTotal = "";
            String advAmount = "";
            String deliveryCharge = "";
            String customerCode = "";
            int discPer = 0;
            boolean flag_DirectBiller = false;
            FileWriter fstream_bill = new FileWriter(Text_Bill);
            BufferedWriter BillOut = new BufferedWriter(fstream_bill);
            boolean isReprint = false;
            if ("reprint".equalsIgnoreCase(reprint))
            {
                isReprint = true;
                objPrintingUtility.funPrintBlankSpace("[DUPLICATE]", BillOut);
                BillOut.write("[DUPLICATE]");
                BillOut.newLine();
            }
            if (transType.equals("Void"))
            {
                objPrintingUtility.funPrintBlankSpace("VOIDED BILL", BillOut);
                BillOut.write("VOIDED BILL");
                BillOut.newLine();
            }
            boolean flag_isHomeDelvBill = false;
            String SQL_HomeDelivery = "select strBillNo,strCustomerCode,strDPCode,tmeTime,strCustAddressLine1 "
                    + "from tblhomedelivery where strBillNo=? ;";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_HomeDelivery);
            pst.setString(1, billNo);
            ResultSet rs_HomeDelivery = pst.executeQuery();

            if (rs_HomeDelivery.next())
            {
                flag_isHomeDelvBill = true;
                customerCode = rs_HomeDelivery.getString(2);
                if (clsGlobalVarClass.gPrintHomeDeliveryYN)
                {
                    objPrintingUtility.funPrintBlankSpace("HOME DELIVERY", BillOut);
                    BillOut.write("HOME DELIVERY");
                    BillOut.newLine();
                }

                String SQL_CustomerDtl = "";

                if (rs_HomeDelivery.getString(5).equals("Home"))
                {
                    SQL_CustomerDtl = "select a.strCustomerName,a.strBuildingName,a.strStreetName"
                            + " ,a.strLandmark,a.strArea,a.strCity,a.intPinCode,a.longMobileNo "
                            + " from tblcustomermaster a left outer join tblbuildingmaster b "
                            + " on a.strBuldingCode=b.strBuildingCode "
                            + " where a.strCustomerCode=? ;";
                }
                else
                {
                    SQL_CustomerDtl = "select a.strCustomerName,a.strOfficeBuildingName,a.strOfficeStreetName"
                            + ",a.strOfficeLandmark,a.strOfficeArea,a.strOfficeCity,a.strOfficePinCode,a.longMobileNo "
                            + " from tblcustomermaster a "
                            + " where a.strCustomerCode=? ";
                }
                pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_CustomerDtl);
                pst.setString(1, rs_HomeDelivery.getString(2));
                ResultSet rs_CustomerDtl = pst.executeQuery();
                while (rs_CustomerDtl.next())
                {
                    BillOut.write("  NAME      :" + rs_CustomerDtl.getString(1).toUpperCase());
                    BillOut.newLine();

                    // Building Name    
                    String add = rs_CustomerDtl.getString(2);
                    int strlen = add.length();
                    String add1 = "";
                    if (strlen < 28)
                    {
                        add1 = add.substring(0, strlen);
                        BillOut.write("  ADDRESS1   :" + add1.toUpperCase());
                        BillOut.newLine();
                    }
                    else
                    {
                        add1 = add.substring(0, 28);
                        BillOut.write("  ADDRESS1   :" + add1.toUpperCase());
                        BillOut.newLine();
                    }

                    for (int i = 28; i <= strlen; i++)
                    {
                        int end = 0;
                        end = i + 28;
                        if (strlen > end)
                        {
                            add1 = add.substring(i, end);
                            i = end;
                            BillOut.write("             " + add1.toUpperCase());
                            BillOut.newLine();
                        }
                        else
                        {
                            add1 = add.substring(i, strlen);
                            BillOut.write("             " + add1.toUpperCase());
                            BillOut.newLine();
                            i = strlen + 1;
                        }
                    }

                    // Street Name    
                    String street = rs_CustomerDtl.getString(3);
                    String street1;
                    int streetlen = street.length();
                    for (int i = 0; i <= streetlen; i++)
                    {
                        int end = 0;
                        end = i + 28;
                        if (streetlen > end)
                        {
                            street1 = street.substring(i, end);
                            BillOut.write("             " + street1.toUpperCase());
                            BillOut.newLine();
                            i = end;
                        }
                        else
                        {
                            street1 = street.substring(i, streetlen);
                            BillOut.write("             " + street1.toUpperCase());
                            BillOut.newLine();
                            i = streetlen + 1;
                        }
                    }

                    // Landmark Name    
                    if (rs_CustomerDtl.getString(4).trim().length() > 0)
                    {
                        BillOut.write("             " + rs_CustomerDtl.getString(4).toUpperCase());
                        BillOut.newLine();
                    }

                    // Area Name    
                    if (rs_CustomerDtl.getString(5).trim().length() > 0)
                    {
                        BillOut.write("             " + rs_CustomerDtl.getString(4).toUpperCase());
                        BillOut.newLine();
                    }

                    // City Name    
                    if (rs_CustomerDtl.getString(6).trim().length() > 0)
                    {
                        BillOut.write("             " + rs_CustomerDtl.getString(6).toUpperCase());
                        BillOut.newLine();
                    }

                    // Pin Code    
                    if (rs_CustomerDtl.getString(7).trim().length() > 0)
                    {
                        BillOut.write("             " + rs_CustomerDtl.getString(7).toUpperCase());
                        BillOut.newLine();
                    }

                    // Mobile No    
                    BillOut.write("  MOBILE NO :" + rs_CustomerDtl.getString(8));
                    BillOut.newLine();
                }
                rs_CustomerDtl.close();

                if (null != rs_HomeDelivery.getString(3) && rs_HomeDelivery.getString(3).trim().length() > 0)
                {
                    String SQL_DeliveryBoyDtl = "select strDPName from tbldeliverypersonmaster where strDPCode=? ;";
                    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_DeliveryBoyDtl);

                    pst.setString(1, rs_HomeDelivery.getString(3));
                    ResultSet rs_DeliveryBoyDtl = pst.executeQuery();
                    if (rs_DeliveryBoyDtl.next())
                    {
                        BillOut.write("  DELV BOY  :" + rs_DeliveryBoyDtl.getString(1).toUpperCase());
                    }
                    BillOut.newLine();
                    rs_DeliveryBoyDtl.close();
                }
                /*
                 * BillOut.write(" DELV TIME :" +
                 * rs_HomeDelivery.getString(4).toUpperCase());
                 * BillOut.newLine();
                 */
                BillOut.write(dashedLineFor40Chars);
                BillOut.newLine();
            }
            rs_HomeDelivery.close();

            objPrintingUtility.funPrintTakeAway(billhd, billNo, BillOut, 4);
            if (clsGlobalVarClass.gPrintTaxInvoice.equalsIgnoreCase("Y"))
            {
                objPrintingUtility.funPrintBlankSpace("TAX INVOICE", BillOut);
                BillOut.write("TAX INVOICE");
                BillOut.newLine();
            }
            if (clsGlobalVarClass.gClientCode.equals("074.001"))
            {
                if (clsGlobalVarClass.gPOSCode.equals("P18") || clsGlobalVarClass.gPOSCode.equals("P19") || clsGlobalVarClass.gPOSCode.equals("P20") || clsGlobalVarClass.gPOSCode.equals("P21") || clsGlobalVarClass.gPOSCode.equals("P22") || clsGlobalVarClass.gPOSCode.equals("P23"))
                {
                    objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gPOSName, BillOut);
                    BillOut.write(clsGlobalVarClass.gPOSName);
                    BillOut.newLine();
                    BillOut.write(Linefor8);
                }
            }
            else if (clsGlobalVarClass.gClientCode.equals("092.001") || clsGlobalVarClass.gClientCode.equals("092.002") || clsGlobalVarClass.gClientCode.equals("092.003"))//Shree Sound Pvt. Ltd.(Waters)
            {
                objPrintingUtility.funPrintBlankSpace("SSPL", BillOut);
                BillOut.write("SSPL");
                BillOut.newLine();

                objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gClientAddress1, BillOut);
                BillOut.write(clsGlobalVarClass.gClientAddress1.toUpperCase());
                BillOut.newLine();

                if (clsGlobalVarClass.gClientAddress2.trim().length() > 0)
                {
                    objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gClientAddress2, BillOut);
                    BillOut.write(clsGlobalVarClass.gClientAddress2.toUpperCase());
                    BillOut.newLine();
                }

                if (clsGlobalVarClass.gClientAddress3.trim().length() > 0)
                {
                    objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gClientAddress3, BillOut);
                    BillOut.write(clsGlobalVarClass.gClientAddress3.toUpperCase());
                    BillOut.newLine();
                }

                if (clsGlobalVarClass.gCityName.trim().length() > 0)
                {
                    objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gCityName, BillOut);
                    BillOut.write(clsGlobalVarClass.gCityName.toUpperCase());
                    BillOut.newLine();
                }
            }
            else
            {
                objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gClientName, BillOut);
                BillOut.write(clsGlobalVarClass.gClientName.toUpperCase());
                BillOut.newLine();

                objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gClientAddress1, BillOut);
                BillOut.write(clsGlobalVarClass.gClientAddress1.toUpperCase());
                BillOut.newLine();

                if (clsGlobalVarClass.gClientAddress2.trim().length() > 0)
                {
                    objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gClientAddress2, BillOut);
                    BillOut.write(clsGlobalVarClass.gClientAddress2.toUpperCase());
                    BillOut.newLine();
                }

                if (clsGlobalVarClass.gClientAddress3.trim().length() > 0)
                {
                    objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gClientAddress3, BillOut);
                    BillOut.write(clsGlobalVarClass.gClientAddress3.toUpperCase());
                    BillOut.newLine();
                }

                if (clsGlobalVarClass.gCityName.trim().length() > 0)
                {
                    objPrintingUtility.funPrintBlankSpace(clsGlobalVarClass.gCityName, BillOut);
                    BillOut.write(clsGlobalVarClass.gCityName.toUpperCase());
                    BillOut.newLine();
                }
            }

            BillOut.write("  TEL NO.   :" + " ");
            BillOut.write(String.valueOf(clsGlobalVarClass.gClientTelNo));
            BillOut.newLine();
            BillOut.write("  EMAIL ID  :" + " ");
            BillOut.write(clsGlobalVarClass.gClientEmail);
            BillOut.newLine();
            String query = "";
            String SQL_BillHD = "";
            String waiterName = "";
            String waiterNo = "";
            String tblName = "";
            ResultSet rsQuery = null;
            ResultSet rs_BillHD = null;
            ResultSet rsTblName = null;
            String sqlTblName = "";
            String tabNo = "";
            boolean flag_DirectBillerBlill = false;
            if (objPrintingUtility.funIsDirectBillerBill(billNo, billhd))
            {
                flag_DirectBillerBlill = true;
                SQL_BillHD = "select a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
                        + "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
                        + ",ifnull(dblDeliveryCharges,0.00),ifnull(b.dblAdvDeposite,0.00),a.dblDiscountPer,c.strPOSName "
                        + "from " + billhd + " a left outer join tbladvancereceipthd b on a.strAdvBookingNo=b.strAdvBookingNo "
                        + "left outer join tblposmaster c on a.strPOSCode=c.strPOSCode "
                        + "where a.strBillNo=?  "
                        + " and date(a.dteBillDate)=? ";
                flag_DirectBiller = true;
                pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_BillHD);
                pst.setString(1, billNo);
                pst.setString(2, billDate);
                rs_BillHD = pst.executeQuery();
                rs_BillHD.next();
            }
            else
            {
                SQL_BillHD = "select a.strTableNo,a.strWaiterNo,a.dteBillDate,time(a.dteBillDate),a.dblDiscountAmt,a.dblSubTotal,"
                        + "a.strCustomerCode,a.dblGrandTotal,a.dblTaxAmt,a.strReasonCode,a.strRemarks,a.strUserCreated"
                        + ",dblDeliveryCharges,ifnull(c.dblAdvDeposite,0.00),a.dblDiscountPer,d.strPOSName,a.intPaxNo "
                        + "from " + billhd + " a left outer join tbltablemaster b on a.strTableNo=b.strTableNo "
                        + "left outer join tbladvancereceipthd c on a.strAdvBookingNo=c.strAdvBookingNo "
                        + "left outer join tblposmaster d on a.strPOSCode=d.strPOSCode "
                        + "where a.strBillNo=?  "
                        + " and date(a.dteBillDate)=? ";
                pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_BillHD);
                pst.setString(1, billNo);
                pst.setString(2, billDate);
                rs_BillHD = pst.executeQuery();
                rs_BillHD.next();
                tabNo = rs_BillHD.getString(1);

                if (rs_BillHD.getString(2).equalsIgnoreCase("null") || rs_BillHD.getString(2).equalsIgnoreCase(""))
                {
                    waiterNo = "";
                }
                else
                {
                    waiterNo = rs_BillHD.getString(2);
                    query = "select strWShortName from tblwaitermaster where strWaiterNo=? ;";
                    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(query);
                    pst.setString(1, waiterNo);
                    rsQuery = pst.executeQuery();
                    if(rsQuery.next())
                    {
                    waiterName = rsQuery.getString(1);
                    }
                    rsQuery.close();
                    pst.close();
                }

                sqlTblName = "select strTableName from tbltablemaster where strTableNo=? ;";
                pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlTblName);
                pst.setString(1, tabNo);
                rsTblName = pst.executeQuery();
                if(rsTblName.next())
                {    
                tblName = rsTblName.getString(1);
                }
                rsTblName.close();
                pst.close();
            }
           //funPrintTakeAway(billhd, billNo, BillOut);

            // Code to Print KOT No Series.            
            String KOTNoSeries = "";
            ResultSet rsKOTNo = clsGlobalVarClass.dbMysql.executeResultSet("select distinct(strKOTNo) from " + billdtl + " where strBillNo='" + billNo + "'");
            if (rsKOTNo.next())
            {
                KOTNoSeries += "," + rsKOTNo.getString(1);
            }
            rsKOTNo.close();
            if (!KOTNoSeries.isEmpty())
            {
                KOTNoSeries = KOTNoSeries.substring(1, KOTNoSeries.length());
                BillOut.write(Linefor8);
                BillOut.newLine();
            }

            if (flag_DirectBillerBlill)
            {
                BillOut.write(Linefor8);
                BillOut.newLine();
                BillOut.write("  POS         : ");
                BillOut.write(rs_BillHD.getString(14));
                BillOut.newLine();

                BillOut.write("  BILL NO.    : ");
                BillOut.write(billNo);
                BillOut.newLine();

                BillOut.write("  DATE & TIME : ");
                SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
                BillOut.write(ft.format(rs_BillHD.getObject(1)));
                BillOut.newLine();

                BillOut.newLine();
                subTotal = rs_BillHD.getString(4);
                discount = rs_BillHD.getString(3);
                tax = rs_BillHD.getString(7);
                grandTotal = rs_BillHD.getString(6);
                user = rs_BillHD.getString(10);
                deliveryCharge = rs_BillHD.getString(11);
                advAmount = rs_BillHD.getString(12);
                discPer = rs_BillHD.getInt(13);
            }
            else
            {
                BillOut.write("  TABLE NAME:" + "  ");
                BillOut.write(tblName);
                BillOut.newLine();
                if (waiterName.trim().length() > 0)
                {
                    BillOut.write("  STEWARD   :" + "  ");
                    BillOut.write(waiterName);
                    BillOut.newLine();
                }
                BillOut.write(Linefor8);

                BillOut.newLine();
                BillOut.write("  POS         : ");
                BillOut.write(rs_BillHD.getString(16));
                BillOut.newLine();

                BillOut.write("  BILL NO.    : ");
                BillOut.write(billNo);
                BillOut.newLine();

                BillOut.write("  PAX NO.     : ");
                BillOut.write(rs_BillHD.getString(17));
                BillOut.newLine();

                BillOut.write("  DATE & TIME : ");
                SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
                BillOut.write(ft.format(rs_BillHD.getObject(3)));
                BillOut.newLine();
                BillOut.write(Linefor8);
                BillOut.newLine();
                subTotal = rs_BillHD.getString(6);
                discount = rs_BillHD.getString(5);
                tax = rs_BillHD.getString(9);
                grandTotal = rs_BillHD.getString(8);
                user = rs_BillHD.getString(12);
                deliveryCharge = rs_BillHD.getString(13);
                advAmount = rs_BillHD.getString(14);
                discPer = rs_BillHD.getInt(15);
            }
            //print card available balance
            String isSttled = "select strBillNo from " + billSettlementdtl + " where strBillNo='" + billNo + "' "
                    + " and date(dteBillDate)='" + billDate + "' ";
            ResultSet rsIsSettled = clsGlobalVarClass.dbMysql.executeResultSet(isSttled);
            if (rsIsSettled.next())
            {
                rsIsSettled.close();
                String availBal = "select a.strCardNo,(b.dblRedeemAmt)"
                        + "from " + billhd + " a inner join tbldebitcardmaster b on a.strCardNo=b.strCardNo "
                        + "where a.strBillNo='" + billNo + "' "
                        + " and date(a.dteBillDate)='" + billDate + "' ";
                ResultSet rsAvailBal = clsGlobalVarClass.dbMysql.executeResultSet(availBal);
                if (rsAvailBal.next())
                {
                    BillOut.write("  Available Balance(" + rsAvailBal.getString(1) + "):" + rsAvailBal.getString(2));
                    BillOut.newLine();
                }
            }
            else
            {
                String availBal = "select a.strCardNo,(b.dblRedeemAmt-a.dblGrandTotal)"
                        + "from " + billhd + " a inner join tbldebitcardmaster b on a.strCardNo=b.strCardNo "
                        + "where a.strBillNo='" + billNo + "' "
                        + " and date(a.dteBillDate)='" + billDate + "' ";
                ResultSet rsAvailBal = clsGlobalVarClass.dbMysql.executeResultSet(availBal);
                if (rsAvailBal.next())
                {
                    BillOut.write("  Available Balance(" + rsAvailBal.getString(1) + "):" + rsAvailBal.getString(2));
                    BillOut.newLine();
                }
            }
            //print card available balance
            if (transType.equals("Void"))
            {
                String reasonName = "select a.strReasonName "
                        + "from tblvoidbillhd a "
                        + "where a.strBillNo='" + billNo + "' "
                        + " and date(a.dteBillDate)='" + billDate + "' ";
                ResultSet rsReason = clsGlobalVarClass.dbMysql.executeResultSet(reasonName);
                if (rsReason.next())
                {
                    BillOut.write("  Reason      :" + " " + rsReason.getString(1));
                    BillOut.newLine();
                }
                rsReason.close();
            }

            ////////////////////////llllllllll/////////////////////////////
            String sql_advOrder_SuTtotal = "select a.strAdvBookingNo,a.strMessage"
                    + ",a.strShape,a.strNote"
                    + " from tbladvbookbillhd a, " + billhd + " b "
                    + " where a.strAdvBookingNo=b.strAdvBookingNo "
                    + "and b.strBillNo=? and date(b.dteBillDate)=? ";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_advOrder_SuTtotal);

            pst.setString(1, billNo);
            pst.setString(2, billDate);
            ResultSet rs_advOrder_Tot = pst.executeQuery();
            if (rs_advOrder_Tot.next())
            {
                objPrintingUtility.funPrintBlankSpace("ORDER DETAIL", BillOut);
                BillOut.write("ORDER DETAIL");
                BillOut.newLine();
                BillOut.write(Linefor8);
                BillOut.newLine();
                String msg = rs_advOrder_Tot.getString(2).toUpperCase();
                String shape = rs_advOrder_Tot.getString(3).toUpperCase();
                String note = rs_advOrder_Tot.getString(4).toUpperCase();
                int strlenMsg = msg.length();
                if (strlenMsg > 0)
                {
                    String msg1 = "";
                    if (strlenMsg < 27)
                    {
                        msg1 = msg.substring(0, strlenMsg);
                        BillOut.write("  MESSAGE     :" + msg1);
                        BillOut.newLine();
                    }
                    else
                    {
                        msg1 = msg.substring(0, 27);
                        BillOut.write("  MESSAGE     :" + msg1);;
                        BillOut.newLine();
                    }
                    for (int i = 27; i <= strlenMsg; i++)
                    {
                        int endmsg = 0;
                        endmsg = i + 27;
                        if (strlenMsg > endmsg)
                        {
                            msg1 = msg.substring(i, endmsg);
                            i = endmsg;
                            BillOut.write("               " + msg1);
                            BillOut.newLine();
                        }
                        else
                        {
                            msg1 = msg.substring(i, strlenMsg);
                            BillOut.write("               " + msg1);
                            BillOut.newLine();
                            i = strlenMsg + 1;
                        }
                    }
                }
                //////////////////////////////////////////////////

                int strlenShape = shape.length();
                if (strlenShape > 0)
                {
                    String shape1 = "";
                    if (strlenShape < 27)
                    {
                        shape1 = shape.substring(0, strlenShape);
                        BillOut.write("  SHAPE       :" + shape1);
                        BillOut.newLine();
                    }
                    else
                    {
                        shape1 = shape.substring(0, 27);
                        BillOut.write("  SHAPE       :" + shape1);
                        BillOut.newLine();
                    }
                    for (int j = 27; j <= strlenShape; j++)
                    {
                        int endShape = 0;
                        endShape = j + 27;
                        if (strlenShape > endShape)
                        {
                            shape1 = shape.substring(j, endShape);
                            j = endShape;
                            BillOut.write("               " + shape1);
                            BillOut.newLine();
                        }
                        else
                        {
                            shape1 = shape.substring(j, strlenShape);
                            BillOut.write("               " + shape1);
                            BillOut.newLine();
                            j = strlenShape + 1;
                        }
                    }
                }
                int strlenNote = note.length();
                if (strlenNote > 0)
                {

                    String note1 = "";
                    if (strlenNote < 27)
                    {
                        note1 = note.substring(0, strlenNote);
                        BillOut.write("  NOTE        :" + note1);
                        BillOut.newLine();
                    }
                    else
                    {
                        note1 = note.substring(0, 27);
                        BillOut.write("  NOTE        :" + note1);
                        BillOut.newLine();
                    }
                    for (int i = 27; i <= strlenNote; i++)
                    {
                        int endNote = 0;
                        endNote = i + 27;
                        if (strlenNote > endNote)
                        {
                            note1 = note.substring(i, endNote);
                            i = endNote;
                            BillOut.write("               " + note1);
                            BillOut.newLine();
                        }
                        else
                        {
                            note1 = note.substring(i, strlenNote);
                            BillOut.write("               " + note1);
                            BillOut.newLine();
                            i = strlenNote + 1;
                        }
                    }
                }

                BillOut.write(Linefor8);
            }
            /////////////////////////lllllllll////////////////////////////

            BillOut.newLine();
            BillOut.write("     QTY ITEM NAME                  AMT");
            BillOut.newLine();
            BillOut.write(Linefor8);
            BillOut.newLine();
            String SQL_BillDtl = "select SUM(dblQuantity),left(strItemName,22) as ItemLine1"
                    + " ,MID(strItemName,23,LENGTH(strItemName)) as ItemLine2"
                    + " ,sum(dblAmount),strItemCode,strKOTNo "
                    + " from " + billdtl + " where strBillNo=? and tdhYN='N' and date(dteBillDate)=? "
                    + " group by strItemCode ;";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_BillDtl);

            pst.setString(1, billNo);
            pst.setString(2, billDate);
            ResultSet rs_BillDtl = pst.executeQuery();
            while (rs_BillDtl.next())
            {
                objPrintingUtility.funPrintContentWithSpace("Right", rs_BillDtl.getString(1), 8, BillOut);//Qty Print
                BillOut.write(" ");
                objPrintingUtility.funPrintContentWithSpace("Left", rs_BillDtl.getString(2), 22, BillOut);//Amount                
                objPrintingUtility.funPrintContentWithSpace("Right", rs_BillDtl.getString(4), 9, BillOut);//Amount

                BillOut.newLine();
                if (rs_BillDtl.getString(3).trim().length() > 0)
                {
                    BillOut.write("         " + rs_BillDtl.getString(3));
                    BillOut.newLine();
                }

                String sqlModifier = "select count(*) "
                        + "from " + billModifierdtl + " where strBillNo=? and left(strItemCode,7)=? and date(dteBillDate)=?";
                if (!clsGlobalVarClass.gPrintZeroAmtModifierOnBill)
                {
                    sqlModifier += " and  dblAmount !=0.00 ";
                }
                pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlModifier);

                System.out.println("Item Code=" + rs_BillDtl.getString(5));
                pst.setString(1, billNo);
                pst.setString(2, rs_BillDtl.getString(5));
                pst.setString(3, billDate);
                ResultSet rs_count = pst.executeQuery();
                rs_count.next();
                int cntRecord = rs_count.getInt(1);
                rs_count.close();
                if (cntRecord > 0)
                {
                    sqlModifier = "select strModifierName,dblQuantity,dblAmount "
                            + "from " + billModifierdtl + " where strBillNo=? and left(strItemCode,7)=? and date(dteBillDate)=? ";
                    if (!clsGlobalVarClass.gPrintZeroAmtModifierOnBill)
                    {
                        sqlModifier += " and  dblAmount !=0.00 ";
                    }
                    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlModifier);

                    pst.setString(1, billNo);
                    pst.setString(2, rs_BillDtl.getString(5));
                    pst.setString(3, billDate);
                    ResultSet rs_modifierRecord = pst.executeQuery();
                    while (rs_modifierRecord.next())
                    {
                        objPrintingUtility.funWriteToTextformat5(BillOut, "", rs_modifierRecord.getString(1).toUpperCase(), rs_modifierRecord.getString(3), "Format5");
                        BillOut.newLine();
                    }
                    rs_modifierRecord.close();
                }
            }
            rs_BillDtl.close();

            //funPrintPromoItemsInBill(billNo, BillOut,4);  // Print Promotion Items in Bill for this billno.
            BillOut.write(Linefor8);
            BillOut.newLine();

            if (clsGlobalVarClass.gPointsOnBillPrint)
            {
                String sql_CRMPoints = "select b.dblPoints from tblbillhd a, tblcrmpoints b "
                        + "where a.strBillNo=b.strBillNo and a.strBillNo='" + billNo + "'";
                ResultSet rsCRMPoints = clsGlobalVarClass.dbMysql.executeResultSet(sql_CRMPoints);
                if (rsCRMPoints.next())
                {
                    objPrintingUtility.funWriteTotal("POINTS ", rsCRMPoints.getString(1), BillOut, "Format5");
                }
                rsCRMPoints.close();
                BillOut.newLine();
            }

            String sql_Tax = "select b.strTaxDesc,sum(a.dblTaxAmount) "
                    + " from " + billtaxdtl + " a,tbltaxhd b "
                    + " where a.strBillNo='" + billNo + "' and a.strTaxCode=b.strTaxCode "
                    + " group by a.strTaxCode";
            ResultSet rsTax = clsGlobalVarClass.dbMysql.executeResultSet(sql_Tax);
            objPrintingUtility.funWriteTotal("SUB TOTAL", subTotal, BillOut, "Format5");
            BillOut.newLine();

            if (discount.trim().length() > 0 && !"0.00".equalsIgnoreCase(discount))
            {
                objPrintingUtility.funWriteTotal("DISCOUNT  " + discPer + "%", discount, BillOut, "Format5");
                BillOut.newLine();
            }

            while (rsTax.next())
            {
                objPrintingUtility.funWriteTotal(rsTax.getString(1), rsTax.getString(2), BillOut, "Format5");
                BillOut.newLine();
            }
            if (deliveryCharge != null && deliveryCharge.trim().length() > 0 && !"0.00".equalsIgnoreCase(deliveryCharge))
            {
                objPrintingUtility.funWriteTotal("DELV. CHARGE", deliveryCharge, BillOut, "Format5");
                BillOut.newLine();
            }

            if (advAmount.trim().length() > 0 && !"0.00".equalsIgnoreCase(advAmount))
            {
                objPrintingUtility.funWriteTotal("ADVANCE", advAmount, BillOut, "Format5");
                BillOut.newLine();
            }

            BillOut.write(Linefor8);
            BillOut.newLine();
            objPrintingUtility.funWriteTotal("TOTAL(ROUNDED)", grandTotal, BillOut, "Format5");
            BillOut.newLine();
            BillOut.write(Linefor8);
            BillOut.newLine();
            //settlement breakup part
            String SQL_Settlement_Breakup = "select a.dblSettlementAmt ,b.strSettelmentDesc "
                    + " from " + billSettlementdtl + " a ,tblsettelmenthd b "
                    + "where a.strBillNo=? and a.strSettlementCode=b.strSettelmentCode and date(a.dteBillDate)=? ";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_Settlement_Breakup);

            pst.setString(1, billNo);
            pst.setString(2, billDate);
            boolean flgSettlement = false;
            ResultSet rs_Bill_Settlement = pst.executeQuery();
            while (rs_Bill_Settlement.next())
            {
                objPrintingUtility.funWriteTotal(rs_Bill_Settlement.getString(2), rs_Bill_Settlement.getString(1), BillOut, "Format5");
                BillOut.newLine();
                flgSettlement = true;
            }
            rs_Bill_Settlement.close();
            if (flgSettlement)
            {
                BillOut.write(Linefor8);
            }
            if (flag_isHomeDelvBill)
            {
                BillOut.write(Linefor8);
                BillOut.newLine();
                String sql_count = "select count(*) from tblhomedelivery where strCustomerCode=?";
                pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_count);
                pst.setString(1, customerCode);
                ResultSet rs_Count = pst.executeQuery();
                rs_Count.next();
                BillOut.write("  CUSTOMER COUNT : " + rs_Count.getString(1));
                rs_Count.close();
                BillOut.newLine();
            }
            BillOut.newLine();
            BillOut.write("Location : ");
            BillOut.write(clsGlobalVarClass.gClientName);
            BillOut.newLine();

            objPrintingUtility.funPrintServiceVatNo(BillOut, 4, billNo, billDate, billtaxdtl);

            BillOut.write(Linefor8);
            BillOut.newLine();

            String cashCard = "N";
            String sql = "select a.dblGrandTotal,b.strCustomerCode,d.strCashCard,c.strCardNo,c.dblRedeemAmt "
                    + "from tblbillhd a,tblcustomermaster b,tbldebitcardmaster c,tbldebitcardtype d  "
                    + "where a.strCustomerCode=b.strCustomerCode "
                    + "and b.strCustomerCode=c.strCustomerCode "
                    + "and c.strCardTypeCode=d.strCardTypeCode "
                    + "and a.strBillNo='" + billhd + "' and date(a.dteBillDate)='"+billDate+"' ";
            ResultSet rsCashCard = clsGlobalVarClass.dbMysql.executeResultSet(sql);
            if (rsCashCard.next())
            {
                cashCard = rsCashCard.getString(3);
                if (cashCard.equals("Y"))
                {
                    BillOut.write("Debit Card No: ");
                    BillOut.write(rsCashCard.getString(4));
                    BillOut.newLine();

                    BillOut.write("Holder Name: ");
                    BillOut.write(rsCashCard.getString(2));
                    BillOut.newLine();

                    BillOut.write("Opening Bal: ");
                    BillOut.write(rsCashCard.getString(5));
                    BillOut.newLine();

                    double cardBal = rsCashCard.getDouble(5) - rsCashCard.getDouble(1);
                    BillOut.write("Card Bal: ");
                    BillOut.write(cardBal + "");
                    BillOut.newLine();
                }
            }
            rsCashCard.close();

            if (cashCard.equals("N"))
            {
                if (clsGlobalVarClass.gCMSIntegrationYN)
                {
                    sql = "select b.strCustomerCode,b.strCustomerName "
                            + " from " + billhd + " a,tblcustomermaster b "
                            + " where a.strCustomerCode=b.strCustomerCode "
                            + " and a.strBillNo='" + billNo + "' and date(a.dteBillDate)='"+billDate+"' ";
                    ResultSet rsBillPrint = clsGlobalVarClass.dbMysql.executeResultSet(sql);
                    if (rsBillPrint.next())
                    {
                        BillOut.write("  Member Code : ");
                        BillOut.write(rsBillPrint.getString(1));
                        BillOut.newLine();
                        BillOut.write("  Member Name : ");
                        objPrintingUtility.funWriteToTextMemberNameForFormat5(BillOut, rsBillPrint.getString(2), "Format5");
                        BillOut.newLine();
                    }
                    rsBillPrint.close();
                }
            }

            int num = clsGlobalVarClass.gBillFooter.trim().length() / 30;
            int num1 = clsGlobalVarClass.gBillFooter.trim().length() % 30;
            int cnt1 = 0;
            for (int cnt = 0; cnt < num; cnt++)
            {
                String footer = clsGlobalVarClass.gBillFooter.trim().substring(cnt1, (cnt1 + 30));
                BillOut.write("     " + footer.trim());
                BillOut.newLine();
                cnt1 += 30;
            }
            BillOut.write("     " + clsGlobalVarClass.gBillFooter.trim().substring(cnt1, (cnt1 + num1)).trim());
            BillOut.newLine();

            objPrintingUtility.funPrintBlankSpace(user, BillOut);
            BillOut.write(user);
            BillOut.newLine();

            BillOut.newLine();
            BillOut.newLine();

            BillOut.newLine();
            BillOut.newLine();

            //BillOut.write("m");
            if ("linux".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
            {
                BillOut.write("V");//Linux
            }
            else if ("windows".equalsIgnoreCase(clsPosConfigFile.gPrintOS))
            {
                if ("Inbuild".equalsIgnoreCase(clsPosConfigFile.gPrinterType))
                {
                    BillOut.write("V");
                }
                else
                {
                    BillOut.write("m");//windows
                }
            }
            // BillOut.write("V");//Linux
            rs_BillHD.close();
            BillOut.close();
            fstream_bill.close();
            pst.close();

            if (formName.equalsIgnoreCase("sales report"))
            {
                objPrintingUtility.funShowTextFile(Text_Bill, formName, clsGlobalVarClass.gBillPrintPrinterPort);
            }
            else
            {
                if (clsGlobalVarClass.gShowBill)
                {
                    objPrintingUtility.funShowTextFile(Text_Bill, formName, clsGlobalVarClass.gBillPrintPrinterPort);
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
                            objPrintingUtility.funPrintToPrinter(clsGlobalVarClass.gBillPrintPrinterPort, "", "bill", "N", isReprint);
                        }
                    }
                }
                else
                {
                    if (!viewORprint.equalsIgnoreCase("view"))
                    {
                        objPrintingUtility.funPrintToPrinter(clsGlobalVarClass.gBillPrintPrinterPort, "", "bill", "N", isReprint);
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
