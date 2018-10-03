/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting.Text.Bill.Audit;

import com.POSPrinting.Text.Bill.*;
import com.POSPrinting.Utility.clsPrintingUtility;
import com.POSGlobal.controller.clsBillDtl;
import com.POSGlobal.controller.clsGlobalVarClass;  
import com.POSGlobal.controller.clsPosConfigFile;
import com.POSGlobal.controller.clsUtility;
import com.POSGlobal.controller.clsUtility2;
import com.POSPrinting.Interfaces.clsBillGenerationFormat;
import com.POSPrinting.Interfaces.clsVoidBillForAuditGenerationFormat;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import javafx.scene.shape.Line;

/**
 *
 * @author Ajim
 * @date Aug 26, 2017
 */
public class clsTextFormat5ForVoidBillAudit implements clsVoidBillForAuditGenerationFormat
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
        String Linefor5 = "  --------------------------------------";
        try
        {
            String user = "";
            String billhd = null;
            String billdtl = null;
            String billModifierdtl = null;
            String billSettlementdtl = null;
            String billtaxdtl = null;
            String billDscFrom = null;
            String billPromoDtl = null;
            String billComplDtl = null;
            String advBookBillHd = null;
            String advBookBillDtl = null;
            String advBookBillCharDtl = null;
            String advReceiptHd = null;
            if (clsGlobalVarClass.gHOPOSType.equalsIgnoreCase("HOPOS"))
            {
                billhd = "tblqbillhd";
                billdtl = "tblqbilldtl";
                billModifierdtl = "tblqbillmodifierdtl";
                billSettlementdtl = "tblqbillsettlementdtl";
                billtaxdtl = "tblqbilltaxdtl";
                billDscFrom = "tblqbilldiscdtl";
                billPromoDtl = "tblqbillpromotiondtl";
                billComplDtl = "tblqbillcomplementrydtl";

                advBookBillHd = "tblqadvbookbillhd";
                advBookBillDtl = "tblqadvbookbilldtl";
                advBookBillCharDtl = "tblqadvbookbillchardtl";
                advReceiptHd = "tblqadvancereceipthd";
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
                    billComplDtl = "tblbillcomplementrydtl";
                    advBookBillHd = "tbladvbookbillhd";
                    advBookBillDtl = "tbladvbookbilldtl";
                    advBookBillCharDtl = "tbladvbookbillchardtl";
                    advReceiptHd = "tbladvancereceipthd";
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
                        billComplDtl = "tblqbillcomplementrydtl";
                        advBookBillHd = "tblqadvbookbillhd";
                        advBookBillDtl = "tblqadvbookbilldtl";
                        advBookBillCharDtl = "tblqadvbookbillchardtl";
                        advReceiptHd = "tblqadvancereceipthd";
                    }
                    String sql = "select count(strBillNo) from tblbillhd where strBillNo='" + billNo + "' and strposCode='" + posCode + "' and date(dteBillDate)='" + billDate + "' ";
                    ResultSet rsBillTable = clsGlobalVarClass.dbMysql.executeResultSet(sql);
                    rsBillTable.next();
                    int billCnt = rsBillTable.getInt(1);
                    if (billCnt == 0)
                    {
                        billhd = "tblqbillhd";
                        billdtl = "tblqbilldtl";
                        billModifierdtl = "tblqbillmodifierdtl";
                        billSettlementdtl = "tblqbillsettlementdtl";
                        billtaxdtl = "tblqbilltaxdtl";
                        billDscFrom = "tblqbilldiscdtl";
                        billPromoDtl = "tblqbillpromotiondtl";
                        billComplDtl = "tblqbillcomplementrydtl";
                        advBookBillHd = "tblqadvbookbillhd";
                        advBookBillDtl = "tblqadvbookbilldtl";
                        advBookBillCharDtl = "tblqadvbookbillchardtl";
                        advReceiptHd = "tblqadvancereceipthd";
                    }
                }
                else
                {
                    billhd = "tblbillhd";
                    billdtl = "tblbilldtl";
                    billModifierdtl = "tblbillmodifierdtl";
                    billSettlementdtl = "tblbillsettlementdtl";
                    billtaxdtl = "tblbilltaxdtl";
                    billDscFrom = "tblbilldiscdtl";
                    billPromoDtl = "tblbillpromotiondtl";
                    billComplDtl = "tblbillcomplementrydtl";
                    advBookBillHd = "tbladvbookbillhd";
                    advBookBillDtl = "tbladvbookbilldtl";
                    advBookBillCharDtl = "tbladvbookbillchardtl";
                    advReceiptHd = "tbladvancereceipthd";
                }
            }
            PreparedStatement pst = null;
            objPrintingUtility.funCreateTempFolder();
            String filePath = System.getProperty("user.dir");
            File Text_Bill = new File(filePath + "/Temp/Temp_Bill.txt");
            String subTotal="",taxAmount="";
            String grandTotal = "";
            String advAmount = "";
            String deliveryCharge = "";
            String customerCode = "";
            String waiterName = "";
            String tblName = "";
            ResultSet rs_BillHD = null;
            boolean flgComplimentaryBill = false;

            billDate = billDate.split(" ")[0];

            StringBuilder sqlBillHeaderDtl = new StringBuilder();
            
            String strTansType="";
            String transactionType = "select a.strTransType "
                                + "from tblvoidbillhd a where a.strBillNo='"+billNo+"'";
            ResultSet rsTransactionType = clsGlobalVarClass.dbMysql.executeResultSet(transactionType);
            if(rsTransactionType.next())
            {
              strTansType = rsTransactionType.getString(1);
            }
            
            sqlBillHeaderDtl.append("SELECT IFNULL(a.strTableNo,''), IFNULL(a.strWaiterNo,''),a.dteBillDate, TIME(a.dteBillDate),ifNULL(a.strReasonCode,''), IFNULL(a.strRemark,'') " 
                    + ",b.strPOSName, IFNULL(c.strTableName,''), IFNULL(d.strWShortName,''), IFNULL(d.strWFullName,''), IFNULL(l.strSettelmentType,'') " 
                    + ",a.strVoidBillType,a.dteModifyVoidBill,a.strReasonName,a.strRemark,a.strUserCreated,a.strUserEdited " 
                    + "FROM tblvoidbillhd  a " 
                    + "LEFT OUTER" 
                    + " JOIN tblposmaster b ON a.strposCode=b.strPosCode " 
                    + "LEFT OUTER" 
                    + " JOIN tbltablemaster c ON a.strTableNo=c.strTableNo AND a.strClientCode=c.strClientCode " 
                    + "LEFT OUTER" 
                    + " JOIN tblwaitermaster d ON a.strWaiterNo=d.strWaiterNo AND a.strClientCode=d.strClientCode " 
                    + "LEFT OUTER" 
                    + " JOIN tblreasonmaster g ON a.strReasonCode=g.strReasonCode "  
                    + "LEFT OUTER" 
                    + " JOIN tblqbillsettlementdtl k ON a.strBillNo=k.strBillNo AND a.strClientCode=k.strClientCode AND DATE(a.dteBillDate)= DATE(k.dteBillDate) " 
                    + "LEFT OUTER" 
                    + " JOIN tblsettelmenthd l ON k.strSettlementCode=l.strSettelmentCode " 
                    + "WHERE a.strBillNo=? " 
                    + "AND a.strposCode=? " 
                    + "AND DATE(a.dteBillDate)=? " 
                    + "and a.strTransType=? " 
                    + "GROUP BY a.strBillNo");
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlBillHeaderDtl.toString());
            pst.setString(1, billNo);
            pst.setString(2, posCode);
            pst.setString(3, billDate);
            pst.setString(4, strTansType);
            rs_BillHD = pst.executeQuery();
            rs_BillHD.next();
//            if (rs_BillHD.getString(21).equals("Complementary"))
//            {
//                flgComplimentaryBill = true;
//            }
            FileWriter fstream_bill = new FileWriter(Text_Bill);
            BufferedWriter BillOut = new BufferedWriter(fstream_bill);
            if (clsGlobalVarClass.gClientCode.equals("117.001"))
            {
                if (posCode.equals("P01"))
                {
                    objPrintingUtility.funPrintBlankSpace("THE PREM'S HOTEL", BillOut);
                    BillOut.write("THE PREM'S HOTEL");
                    BillOut.newLine();
                }
                else if (posCode.equals("P02"))
                {
                    objPrintingUtility.funPrintBlankSpace("SWIG", BillOut);
                    BillOut.write("SWIG");
                    BillOut.newLine();
                }
            }

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

            //item void or full void      
            String voidType = rs_BillHD.getString(12).toUpperCase();
            objPrintingUtility.funPrintBlankSpace(voidType, BillOut);
            BillOut.write(voidType);
            BillOut.newLine();

//            boolean flag_isHomeDelvBill = false;
//            String SQL_HomeDelivery = "select strBillNo,strCustomerCode,strDPCode,tmeTime,strCustAddressLine1 "
//                    + "from tblhomedelivery "
//                    + "where strBillNo=? "
//                    + "and date(dteDate)=? ;";
//            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_HomeDelivery);
//            pst.setString(1, billNo);
//            pst.setString(2, billDate);
//            ResultSet rs_HomeDelivery = pst.executeQuery();
//            if (rs_HomeDelivery.next())
//            {
//                flag_isHomeDelvBill = true;
//                customerCode = rs_HomeDelivery.getString(2);
//                if (clsGlobalVarClass.gPrintHomeDeliveryYN)
//                {
//                    objPrintingUtility.funPrintBlankSpace("HOME DELIVERY", BillOut);
//                    BillOut.write("HOME DELIVERY");
//                    BillOut.newLine();
//                }
//
//                String SQL_CustomerDtl = "";
//                if (rs_HomeDelivery.getString(5).equals("Temporary"))
//                {
//                    SQL_CustomerDtl = "select a.strCustomerName,a.strTempAddress,a.strTempStreet"
//                            + " ,a.strTempLandmark,a.strBuildingName,a.strCity,a.intPinCode,a.longMobileNo "
//                            + " from tblcustomermaster a left outer join tblbuildingmaster b "
//                            + " on a.strBuldingCode=b.strBuildingCode "
//                            + " where a.strCustomerCode=? ;";
//                }
//                else if (rs_HomeDelivery.getString(5).equals("Office"))
//                {
//                    SQL_CustomerDtl = "select a.strCustomerName,a.strOfficeBuildingName,a.strOfficeStreetName"
//                            + ",a.strOfficeLandmark,a.strOfficeArea,a.strOfficeCity,a.strOfficePinCode,a.longMobileNo "
//                            + " from tblcustomermaster a "
//                            + " where a.strCustomerCode=? ";
//                }
//                else
//                {
//                    SQL_CustomerDtl = "select a.strCustomerName,a.strCustAddress,a.strStreetName"
//                            + " ,a.strLandmark,a.strBuildingName,a.strCity,a.intPinCode,a.longMobileNo "
//                            + " from tblcustomermaster a left outer join tblbuildingmaster b "
//                            + " on a.strBuldingCode=b.strBuildingCode "
//                            + " where a.strCustomerCode=? ;";
//                }
//                pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_CustomerDtl);
//                pst.setString(1, rs_HomeDelivery.getString(2));
//                ResultSet rs_CustomerDtl = pst.executeQuery();
//                while (rs_CustomerDtl.next())
//                {
//                    BillOut.write("  NAME      :" + rs_CustomerDtl.getString(1).toUpperCase());
//                    BillOut.newLine();
//                    // Building Name    
//                    String add = rs_CustomerDtl.getString(2);
//                    int strlen = add.length();
//                    String add1 = "";
//                    if (strlen < 28)
//                    {
//                        add1 = add.substring(0, strlen);
//                        BillOut.write("  ADDRESS1  :" + add1.toUpperCase().replaceAll("\n", " "));
//                        BillOut.newLine();
//                    }
//                    else
//                    {
//                        add1 = add.substring(0, 28);
//                        BillOut.write("  ADDRESS1  :" + add1.toUpperCase().replaceAll("\n", " "));
//                        BillOut.newLine();
//                    }
//                    for (int i = 28; i <= strlen;)
//                    {
//                        int end = 0;
//                        end = i + 28;
//                        if (strlen > end)
//                        {
//                            add1 = add.substring(i, end);
//                            i = end;
//                            BillOut.write("             " + add1.toUpperCase().replaceAll("\n", " "));
//                            BillOut.newLine();
//                        }
//                        else
//                        {
//                            add1 = add.substring(i, strlen);
//                            BillOut.write("             " + add1.toUpperCase().replaceAll("\n", " "));
//                            BillOut.newLine();
//                            i = strlen + 1;
//                        }
//                    }
//                    // Street Name    
//                    String street = rs_CustomerDtl.getString(3);
//                    String street1;
//                    int streetlen = street.length();
//                    for (int i = 0; i <= streetlen;)
//                    {
//                        int end = 0;
//                        end = i + 28;
//                        if (streetlen > end)
//                        {
//                            street1 = street.substring(i, end);
//                            BillOut.write("             " + street1.toUpperCase());
//                            BillOut.newLine();
//                            i = end;
//                        }
//                        else
//                        {
//                            street1 = street.substring(i, streetlen);
//                            BillOut.write("             " + street1.toUpperCase());
//                            BillOut.newLine();
//                            i = streetlen + 1;
//                        }
//                    }
//                    // Landmark Name    
//                    if (rs_CustomerDtl.getString(4).trim().length() > 0)
//                    {
//                        BillOut.write("             " + rs_CustomerDtl.getString(4).toUpperCase());
//                        BillOut.newLine();
//                    }
//                    // Area Name    
//                    if (rs_CustomerDtl.getString(5).trim().length() > 0)
//                    {
//                        BillOut.write("             " + rs_CustomerDtl.getString(5).toUpperCase());
//                        BillOut.newLine();
//                    }
//                    // City Name    
//                    if (rs_CustomerDtl.getString(6).trim().length() > 0)
//                    {
//                        BillOut.write("             " + rs_CustomerDtl.getString(6).toUpperCase());
//                        BillOut.newLine();
//                    }
//                    // Pin Code    
//                    if (rs_CustomerDtl.getString(7).trim().length() > 0)
//                    {
//                        BillOut.write("             " + rs_CustomerDtl.getString(7).toUpperCase());
//                        BillOut.newLine();
//                    }
//                    // Mobile No    
//                    BillOut.write("  MOBILE NO :" + rs_CustomerDtl.getString(8));
//                    BillOut.newLine();
//                }
//                rs_CustomerDtl.close();
//                if (null != rs_HomeDelivery.getString(3) && rs_HomeDelivery.getString(3).trim().length() > 0)
//                {
//                    String[] delBoys = rs_HomeDelivery.getString(3).split(",");
//                    StringBuilder strIN = new StringBuilder("(");
//                    for (int i = 0; i < delBoys.length; i++)
//                    {
//                        if (i == 0)
//                        {
//                            strIN.append("'" + delBoys[i] + "'");
//                        }
//                        else
//                        {
//                            strIN.append(",'" + delBoys[i] + "'");
//                        }
//                    }
//                    strIN.append(")");
//                    String SQL_DeliveryBoyDtl = "select strDPName from tbldeliverypersonmaster where strDPCode IN " + strIN + " ;";
//                    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_DeliveryBoyDtl);
//                    ResultSet rs_DeliveryBoyDtl = pst.executeQuery();
//                    strIN.setLength(0);
//                    for (int i = 0; rs_DeliveryBoyDtl.next(); i++)
//                    {
//                        if (i == 0)
//                        {
//                            strIN.append(rs_DeliveryBoyDtl.getString(1).toUpperCase());
//                        }
//                        else
//                        {
//                            strIN.append("," + rs_DeliveryBoyDtl.getString(1).toUpperCase());
//                        }
//                    }
//                    BillOut.write("  DELV BOY  :" + strIN);
//                    BillOut.newLine();
//                    rs_DeliveryBoyDtl.close();
//                }
//                BillOut.write(dashedLineFor40Chars);
//                BillOut.newLine();
//            }
//            else
//            {
//                String customerType = rs_BillHD.getString(33);
//
//                if (rs_BillHD.getString(7).length() > 0 && !(customerType.equalsIgnoreCase("Liqour") || customerType.equalsIgnoreCase("Liquor")))//customerCode
//                {
//                    BillOut.write("  NAME      :" + rs_BillHD.getString(24).toUpperCase());
//                    BillOut.newLine();
//                    // Mobile No    
//                    BillOut.write("  MOBILE NO :" + rs_BillHD.getString(32));
//                    BillOut.newLine();
//                }
//            }
//            rs_HomeDelivery.close();
            //print take away
            int billPrintSize = 4;
//            if (rs_BillHD.getString(30).equals("TakeAway"))
//            {
//                objPrintingUtility.funPrintBlankSpace("Take Away", BillOut);
//                BillOut.write("Take Away");
//                BillOut.newLine();
//            }
            if (clsGlobalVarClass.gPrintTaxInvoice.equalsIgnoreCase("Y"))
            {
                objPrintingUtility.funPrintBlankSpace("TAX INVOICE", BillOut);
                BillOut.write("TAX INVOICE");
                BillOut.newLine();
            }
            if (clsGlobalVarClass.gClientCode.equals("047.001") && posCode.equals("P03"))
            {
                objPrintingUtility.funPrintBlankSpace("SHRI SHAM CATERERS", BillOut);
                BillOut.write("SHRI SHAM CATERERS");
                BillOut.newLine();
                String cAddr1 = "Flat No.7, Mon Amour,";
                objPrintingUtility.funPrintBlankSpace(cAddr1, BillOut);
                BillOut.write(cAddr1.toUpperCase());
                BillOut.newLine();
                String cAddr2 = "Thorat Colony,Prabhat Road,";
                objPrintingUtility.funPrintBlankSpace(cAddr2, BillOut);
                BillOut.write(cAddr2.toUpperCase());
                BillOut.newLine();
                String cAddr3 = " Erandwane, Pune 411 004.";
                objPrintingUtility.funPrintBlankSpace(cAddr3, BillOut);
                BillOut.write(cAddr3.toUpperCase());
                BillOut.newLine();
                String cAddr4 = "Approved Caterers of";
                objPrintingUtility.funPrintBlankSpace(cAddr4, BillOut);
                BillOut.write(cAddr4.toUpperCase());
                BillOut.newLine();
                String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
                objPrintingUtility.funPrintBlankSpace(cAddr5, BillOut);
                BillOut.write(cAddr5.toUpperCase());
                BillOut.newLine();
            }
            else if (clsGlobalVarClass.gClientCode.equals("047.001") && posCode.equals("P02"))
            {
                objPrintingUtility.funPrintBlankSpace("SHRI SHAM CATERERS", BillOut);
                BillOut.write("SHRI SHAM CATERERS");
                BillOut.newLine();
                String cAddr1 = "Flat No.7, Mon Amour,";
                objPrintingUtility.funPrintBlankSpace(cAddr1, BillOut);
                BillOut.write(cAddr1.toUpperCase());
                BillOut.newLine();
                String cAddr2 = "Thorat Colony,Prabhat Road,";
                objPrintingUtility.funPrintBlankSpace(cAddr2, BillOut);
                BillOut.write(cAddr2.toUpperCase());
                BillOut.newLine();
                String cAddr3 = " Erandwane, Pune 411 004.";
                objPrintingUtility.funPrintBlankSpace(cAddr3, BillOut);
                BillOut.write(cAddr3.toUpperCase());
                BillOut.newLine();
                String cAddr4 = "Approved Caterers of";
                objPrintingUtility.funPrintBlankSpace(cAddr4, BillOut);
                BillOut.write(cAddr4.toUpperCase());
                BillOut.newLine();
                String cAddr5 = "ROYAL CONNAUGHT BOAT CLUB";
                objPrintingUtility.funPrintBlankSpace(cAddr5, BillOut);
                BillOut.write(cAddr5.toUpperCase());
                BillOut.newLine();
            }
            else if (clsGlobalVarClass.gClientCode.equals("092.001") || clsGlobalVarClass.gClientCode.equals("092.002") || clsGlobalVarClass.gClientCode.equals("092.003"))//Shree Sound Pvt. Ltd.
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
            else if (clsGlobalVarClass.gClientCode.equals("092.001") || clsGlobalVarClass.gClientCode.equals("092.002") || clsGlobalVarClass.gClientCode.equals("092.003"))//Shree Sound Pvt. Ltd.
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
            else if (clsGlobalVarClass.gClientCode.equals("190.001"))//"190.001", "SQUARE ONE HOSPITALITY LLP" (Quarter House)
            {
                String licenseName = clsGlobalVarClass.gClientName;
                if (billNo.startsWith("L"))//liqour bill license name
                {
                    licenseName = "STEP-IN-AGENCIES & ESTATE PVT LTD";
                }
                else
                {
                    licenseName = clsGlobalVarClass.gClientName;
                }
                objPrintingUtility.funPrintBlankSpace(licenseName, BillOut);

                BillOut.write(licenseName);
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
                if (clsGlobalVarClass.gClientCode.equals("124.001"))
                {
                    BillOut.write(clsGlobalVarClass.gClientName);
                }
                else
                {
                    BillOut.write(clsGlobalVarClass.gClientName.toUpperCase());
                }
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
            tblName = rs_BillHD.getString(8);
            if (tblName.length() > 0)
            {
                if (clsGlobalVarClass.gClientCode.equalsIgnoreCase("136.001"))//KINKI
                {
                    BillOut.write("  TABLE No   :");
                }
                else
                {
                    BillOut.write("  TABLE NAME:" + "  ");
                }
                BillOut.write(tblName);
                BillOut.newLine();
            }
            waiterName = rs_BillHD.getString(9);
            if (waiterName.trim().length() > 0)
            {
                BillOut.write("  STEWARD   :" + "  ");
                BillOut.write(waiterName);
                BillOut.newLine();
            }
            BillOut.write(Linefor5);
            BillOut.newLine();
            BillOut.write("  POS         : ");
            BillOut.write(rs_BillHD.getString(7));
            BillOut.newLine();
            BillOut.write("  BILL NO.    : ");
            BillOut.write(billNo);
            BillOut.newLine();

            SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
            if (clsGlobalVarClass.gPrintTimeOnBillYN)
            {
                BillOut.write("  DATE & TIME : ");
                BillOut.write(ft.format(rs_BillHD.getObject(3)));
                BillOut.newLine();
            }
            else
            {
                ft = new SimpleDateFormat("dd-MM-yyyy");

                BillOut.write("  DATE        : ");
                BillOut.write(ft.format(rs_BillHD.getObject(3)));
                BillOut.newLine();
            }
            //void detail
            BillOut.write(Linefor5);
            BillOut.newLine();
            if (clsGlobalVarClass.gPrintTimeOnBillYN)
            {
                BillOut.write("  VOID DATE & TIME : ");
                BillOut.write(ft.format(rs_BillHD.getObject(13)));
                BillOut.newLine();

            }
            else
            {
                ft = new SimpleDateFormat("dd-MM-yyyy");

                BillOut.write("  VOID DATE   : ");
                BillOut.write(ft.format(rs_BillHD.getObject(13)));
                BillOut.newLine();
            }

            BillOut.write("  REASON  : ");
            BillOut.write(rs_BillHD.getString(14));
            BillOut.newLine();

            BillOut.write("  REMARKS : ");
            BillOut.write(rs_BillHD.getString(15));
            BillOut.newLine();

            BillOut.write("  VOIDED USER : ");
            BillOut.write(rs_BillHD.getString(17));
            BillOut.newLine();

            user = rs_BillHD.getString(16);

            String isSttled = "select a.strBillNo from " + billSettlementdtl + " a," + billhd + " b "
                    + "where a.strBillNo=b.strBillNo "
                    + "and a.strClientCode=b.strClientCode "
                    + "and a.strBillNo='" + billNo + "' "
                    + "and b.strposCode='" + posCode + "' "
                    + "and date(a.dteBillDate)=date(b.dteBillDate) ";
            ResultSet rsIsSettled = clsGlobalVarClass.dbMysql.executeResultSet(isSttled);
            if (rsIsSettled.next())
            {
                rsIsSettled.close();
                String availBal = "select a.strCardNo,(b.dblRedeemAmt)"
                        + "from " + billhd + " a inner join tbldebitcardmaster b on a.strCardNo=b.strCardNo "
                        + "where a.strBillNo='" + billNo + "' "
                        + "and a.strposCode='" + posCode + "' "
                        + "and date(a.dteBillDate)='" + billDate + "' ";
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
                        + "and a.strposCode='" + posCode + "' "
                        + "and date(a.dteBillDate)='" + billDate + "' ";
                ResultSet rsAvailBal = clsGlobalVarClass.dbMysql.executeResultSet(availBal);
                if (rsAvailBal.next())
                {
                    BillOut.write("  Available Balance(" + rsAvailBal.getString(1) + "):" + rsAvailBal.getString(2));
                    BillOut.newLine();
                }
            }

            if (flgComplimentaryBill)
            {

                BillOut.write("  Reason      :" + " " + rs_BillHD.getString(14));
                BillOut.newLine();
                BillOut.write("  Remark      :" + " " + rs_BillHD.getString(15));
                BillOut.newLine();
            }

            Map<String, clsBillDtl> hmComplBillItemDtl = objUtility.funGetComplimetaryItems(billNo, billComplDtl, billhd, posCode, billDate);
            StringBuilder sbZeroAmtItems = new StringBuilder();

            double subTotVal = 0.00;
            double subTot=0.00,taxAmt=0.00;
            double discAmt=0.00;
            String discAmount="";
            BillOut.write(Linefor5);
            BillOut.newLine();
            BillOut.write("     QTY ITEM NAME                  AMT");
            BillOut.newLine();
            BillOut.write(Linefor5);
            BillOut.newLine();
            String SQL_BillDtl = "select sum(a.intQuantity),left(a.strItemName,22) as ItemLine1"
                    + " ,MID(a.strItemName,23,LENGTH(a.strItemName)) as ItemLine2"
                    + " ,sum(a.dblAmount),a.strItemCode,a.strKOTNo,a.dblTaxAmount "
                    + " from tblvoidbilldtl a,tblvoidbillhd b,tblitemmaster c "
                    + " where a.strBillNo=b.strBillNo and a.strClientCode=b.strClientCode "
                    + " and a.strItemCode=c.strItemCode and date(a.dteBillDate)=date(b.dteBillDate) "
                    + " and a.strBillNo=? "
                    + " and b.strposCode=?  "
                    + " and date(b.dteBillDate)=? ";
            
            SQL_BillDtl += " group by a.strItemCode ";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(SQL_BillDtl);
            pst.setString(1, billNo);
            pst.setString(2, posCode);
            pst.setString(3, billDate);
            ResultSet rs_BillDtl = pst.executeQuery();
            while (rs_BillDtl.next())
            {
                double saleQty = rs_BillDtl.getDouble(1);
                subTotVal = rs_BillDtl.getDouble(4);
                subTot += subTotVal;
                subTotal = String.valueOf(subTot);
                
               
                String sqlPromoBills = "select dblQuantity from " + billPromoDtl + " "
                        + " where strBillNo='" + billNo + "' "
                        + " and strItemCode='" + rs_BillDtl.getString(5) + "' "
                        + " and strPromoType='ItemWise' "
                        + " and date(dteBillDate)='" + billDate + "' ";
                ResultSet rsPromoItems = clsGlobalVarClass.dbMysql.executeResultSet(sqlPromoBills);
                if (rsPromoItems.next())
                {
                    saleQty -= rsPromoItems.getDouble(1);
                }
                rsPromoItems.close();
                String qty = String.valueOf(saleQty);
                if (qty.contains("."))
                {
                    String decVal = qty.substring(qty.length() - 2, qty.length());
                    if (Double.parseDouble(decVal) == 0)
                    {
                        qty = qty.substring(0, qty.length() - 2);
                    }
                }
                if (saleQty > 0)
                {
                    if (hmComplBillItemDtl.containsKey(rs_BillDtl.getString(5)))
                    {
//                        double chargedQty = hmComplBillItemDtl.get(rs_BillDtl.getString(5)).getDblQuantity() - saleQty;
//                        if (chargedQty > 0)
//                        {
//                            qty = String.valueOf(chargedQty);
//                        }
                        if (rs_BillDtl.getDouble(4) == 0)
                        {
                            sbZeroAmtItems.append(",'" + rs_BillDtl.getString(5) + "'");
                        }
                    }
                    objPrintingUtility.funPrintContentWithSpace("Right", qty, 8, BillOut);//Qty Print
                    BillOut.write(" ");
                    objPrintingUtility.funPrintContentWithSpace("Left", rs_BillDtl.getString(2), 22, BillOut);//Item Name
                    if (flgComplimentaryBill)
                    {
                        objPrintingUtility.funPrintContentWithSpace("Right", "0.00", 9, BillOut);//Amount
                    }
                    else
                    {
                        objPrintingUtility.funPrintContentWithSpace("Right", rs_BillDtl.getString(4), 9, BillOut);//Amount
                    }
                    BillOut.newLine();

                    if (rs_BillDtl.getString(3).trim().length() > 0)
                    {
                        String line = rs_BillDtl.getString(3);
                        if (line.length() > 22)
                        {
                            BillOut.write("         " + line.substring(0, 22));
                            BillOut.newLine();

                            BillOut.write("         " + line.substring(22, line.length()));
                            BillOut.newLine();
                        }
                        else
                        {
                            BillOut.write("         " + line);
                            BillOut.newLine();
                        }
                    }
                    String sqlModifier = "select count(*) "
                            + "from " + billModifierdtl + " "
                            + "where strBillNo=? "
                            + "and left(strItemCode,7)=? "
                            + "and date(dteBillDate)=? ";
                    if (!clsGlobalVarClass.gPrintZeroAmtModifierOnBill)
                    {
                        sqlModifier += " and  dblAmount !=0.00 ";
                    }
                    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlModifier);
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
                                + " from " + billModifierdtl + " "
                                + " where strBillNo=? "
                                + " and left(strItemCode,7)=? "
                                + " and date(dteBillDate)=? ";
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
                            if (flgComplimentaryBill)
                            {
                                objPrintingUtility.funWriteToTextformat5(BillOut, "", rs_modifierRecord.getString(1).toUpperCase(), "0.00", "Format5");
                                BillOut.newLine();
                            }
                            else
                            {
                                objPrintingUtility.funWriteToTextformat5(BillOut, "", rs_modifierRecord.getString(1).toUpperCase(), rs_modifierRecord.getString(3), "Format5");
                                BillOut.newLine();
                            }
                        }
                        rs_modifierRecord.close();
                    }

                }
            }
            rs_BillDtl.close();

            objPrintingUtility.funPrintComplimentaryItemsInBill(billNo, BillOut, 4, posCode, billDate, sbZeroAmtItems,billhd,billComplDtl);

            objPrintingUtility.funPrintPromoItemsInBill(billNo, BillOut, 4, billPromoDtl);  // Print Promotion Items in Bill for this billno.

            BillOut.write(Linefor5);
            BillOut.newLine();

            if (flgComplimentaryBill)
            {
                objPrintingUtility.funWriteTotal("SUB TOTAL", "0.00", BillOut, "Format5");
                BillOut.newLine();
            }
            else
            {
                objPrintingUtility.funWriteTotal("SUB TOTAL", subTotal, BillOut, "Format5");
                BillOut.newLine();   
            }
             DecimalFormat two = new DecimalFormat("#.##");
               
             String sql = "select a.dblDiscPer,a.dblDiscAmt,a.strDiscOnType,a.strDiscOnValue "
                    + "from tblbilldiscdtl a "
                    + "where a.strBillNo='" + billNo + "' ";
            ResultSet rsDisc = clsGlobalVarClass.dbMysql.executeResultSet(sql);
            boolean flag = true;
            double totalDisc = 0.00;
            String discText="";
            while (rsDisc.next())
            {
                if (flag)
                {
                    flag = false;
                    BillOut.write("  DISCOUNT");
                    BillOut.newLine();
                }
                double dbl = Double.parseDouble(rsDisc.getString("dblDiscPer"));
                discText = String.format("%.1f", dbl) + "%" + " On " + rsDisc.getString("strDiscOnValue") + "";
                if (discText.length() > 30)
                {
                    discText = discText.substring(0, 30);
                }
                else
                {
                    discText = String.format("%-30s", discText);
                }
            }
              sql = "select a.dblDiscAmt from tblvoidbilldiscdtl a " 
                      +"where a.strBillNo='"+billNo+"' and a.strTransType='VB'";
                ResultSet rsDiscDtl = clsGlobalVarClass.dbMysql.executeResultSet(sql);
                while(rsDiscDtl.next())
                {
                    double discAmountVal = Double.parseDouble(rsDiscDtl.getString(1));
                    discAmt +=discAmountVal;
                    discAmount = String.valueOf(discAmt);
                    if (flgComplimentaryBill)
                    {
                        objPrintingUtility.funWriteTotal(discText, "0.00", BillOut, "Format5");
                        BillOut.newLine();
                    }
                    else
                    {
                        objPrintingUtility.funWriteTotal(discText, discAmount, BillOut, "Format5");
                         BillOut.newLine();
                    }
                }
                rsDiscDtl.close();
                
                double taxAmountAdd=0.00;
                String taxName="";
                 sql = "select a.dblTaxAmount,b.strTaxDesc from tblvoidbilltaxdtl a,tbltaxhd b " 
                            + "where a.strTaxCode=b.strTaxCode and a.strBillNo='"+billNo+"' and a.strTransType='VB'";
                ResultSet rsTaxDtl = clsGlobalVarClass.dbMysql.executeResultSet(sql);
                while(rsTaxDtl.next())
                {
                    double taxAmountVal=0.00;
                    taxName = rsTaxDtl.getString(2);
                    taxAmountVal = rsTaxDtl.getDouble(1);
                    taxAmt =taxAmountVal;
                    taxAmount = String.valueOf(two.format(taxAmt));
                    taxAmountAdd += taxAmountVal;
                    
                    if (flgComplimentaryBill)
                    {
                        objPrintingUtility.funWriteTotal(taxName, "0.00", BillOut, "Format5");
                        BillOut.newLine();
                    }
                    else
                    {
                        objPrintingUtility.funWriteTotal(taxName, taxAmount, BillOut, "Format5");
                         BillOut.newLine();
                    }
                }
                rsTaxDtl.close();

                
                 
                double finalGrandTot = (taxAmountAdd+subTot)-discAmt; 
                grandTotal = String.valueOf(finalGrandTot);


            BillOut.write(Linefor5);
            BillOut.newLine();
            if (flgComplimentaryBill)
            {
                objPrintingUtility.funWriteTotal("TOTAL(ROUNDED)", "0.00", BillOut, "Format5");
                BillOut.newLine();
                BillOut.write(Linefor5);
            }
            else
            {
                objPrintingUtility.funWriteTotal("TOTAL(ROUNDED)", grandTotal, BillOut, "Format5");
                BillOut.newLine();
                BillOut.write(Linefor5);
            }

            //print Grand total of other bill nos from bill series
            if (clsGlobalVarClass.gEnableBillSeries)
            {
                String sqlPrintGT = "select a.strPrintGTOfOtherBills,b.strDtlBillNos,b.dblGrandTotal "
                        + "from tblbillseries a,tblbillseriesbilldtl b "
                        + "where (a.strposCode=b.strposCode or a.strposCode='All') "
                        + "and a.strBillSeries=b.strBillSeries "
                        + "and b.strHdBillNo='" + billNo + "' "
                        + "and b.strposCode='" + posCode + "' "
                        + "and date(b.dteBillDate)='" + billDate + "' ";
                ResultSet rsPrintGTYN = clsGlobalVarClass.dbMysql.executeResultSet(sqlPrintGT);
                double dblOtherBillsGT = 0.00;
                if (rsPrintGTYN.next())
                {
                    if (rsPrintGTYN.getString(1).equalsIgnoreCase("Y"))
                    {
                        String billSeriesDtlBillNos = rsPrintGTYN.getString(2);
                        String[] dtlBillSeriesBillNo = billSeriesDtlBillNos.split(",");
                        dblOtherBillsGT += rsPrintGTYN.getDouble(3);
                        if (dtlBillSeriesBillNo.length > 0)
                        {
                            for (int i = 0; i < dtlBillSeriesBillNo.length; i++)
                            {
                                sqlPrintGT = "select a.strHdBillNo,a.dblGrandTotal "
                                        + "from tblbillseriesbilldtl a "
                                        + "where a.strHdBillNo='" + dtlBillSeriesBillNo[i] + "' "
                                        + "and a.strposCode='" + posCode + "' "
                                        + "and date(a.dteBillDate)='" + billDate + "' ";
                                ResultSet rsPrintGT = clsGlobalVarClass.dbMysql.executeResultSet(sqlPrintGT);
                                if (rsPrintGT.next())
                                {
                                    BillOut.newLine();
                                    objPrintingUtility.funWriteTotal(dtlBillSeriesBillNo[i] + " TOTAL(ROUNDED)", rsPrintGT.getString(2), BillOut, "Format5");
                                    dblOtherBillsGT += rsPrintGT.getDouble(2);
                                    BillOut.newLine();
                                }
                            }
                            BillOut.newLine();
                            BillOut.write(Linefor5);
                            BillOut.newLine();
                            objPrintingUtility.funWriteTotal("GRAND TOTAL(ROUNDED)", String.valueOf(dblOtherBillsGT), BillOut, "Format5");
                            BillOut.newLine();
                            BillOut.write(Linefor5);
                            BillOut.newLine();
                        }
                    }
                }
            }

            //settlement breakup part
            String sqlSettlementBreakup = "select a.dblSettlementAmt, b.strSettelmentDesc, b.strSettelmentType "
                    + " from " + billSettlementdtl + " a ,tblsettelmenthd b," + billhd + " c "
                    + " where a.strBillNo=? "
                    + " and a.strBillNo=c.strBillNo "
                    + " and a.strClientCode=c.strClientCode "
                    + " and a.strSettlementCode=b.strSettelmentCode "
                    + " AND date(a.dteBillDate)=date(c.dteBillDate) "
                    + " and c.strposCode=? "
                    + " and date(c.dteBillDate)=? ";
            pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sqlSettlementBreakup);
            pst.setString(1, billNo);
            pst.setString(2, posCode);
            pst.setString(3, billDate);
            boolean flgSettlement = false;
            boolean creditSettlement = false;
            ResultSet rsBillSettlement = pst.executeQuery();
            while (rsBillSettlement.next())
            {
                if (flgComplimentaryBill)
                {
                    BillOut.newLine();
                    objPrintingUtility.funWriteTotal(rsBillSettlement.getString(2), "0.00", BillOut, "Format5");
                }
                else
                {
                    BillOut.newLine();
                    objPrintingUtility.funWriteTotal(rsBillSettlement.getString(2), rsBillSettlement.getString(1), BillOut, "Format5");
                }
                flgSettlement = true;
                if (rsBillSettlement.getString(3).equals("Credit"))
                {
                    creditSettlement = true;
                }
            }
            rsBillSettlement.close();

            if (flgSettlement)
            {
                BillOut.newLine();
                if (creditSettlement)
                {
                    objPrintingUtility.funWriteTotal("Credit Remarks ", rs_BillHD.getString(11), BillOut, "Format5");
                    BillOut.newLine();
                    String custName = rs_BillHD.getString(24);
                    if (!custName.isEmpty())
                    {
                        objPrintingUtility.funWriteTotal("Customer " + custName, "", BillOut, "Format5");
                    }
                    BillOut.newLine();
                    BillOut.write(Linefor5);
                }
            }

            String sqlTenderAmt = "select sum(a.dblPaidAmt),sum(a.dblSettlementAmt),(sum(a.dblPaidAmt)-sum(a.dblSettlementAmt)) RefundAmt "
                    + " from " + billSettlementdtl + " a," + billhd + " b "
                    + " where a.strBillNo=b.strBillNo "
                    + " and a.strClientCode=b.strClientCode "
                    + " AND date(a.dteBillDate)=date(b.dteBillDate) "
                    + " and b.strBillNo='" + billNo + "' "
                    + " and b.strposCode='" + posCode + "' "
                    + " and date(b.dteBillDate)='" + billDate + "' "
                    + " group by a.strBillNo";
            ResultSet rsTenderAmt = clsGlobalVarClass.dbMysql.executeResultSet(sqlTenderAmt);
            if (rsTenderAmt.next())
            {
                BillOut.newLine();
                if (flgComplimentaryBill)
                {
                    objPrintingUtility.funWriteTotal("PAID AMT", "0.00", BillOut, "Format5");
                    BillOut.newLine();
                }
                else
                {
                    objPrintingUtility.funWriteTotal("PAID AMT", rsTenderAmt.getString(1), BillOut, "Format5");
                    BillOut.newLine();
                    if (rsTenderAmt.getDouble(3) > 0)
                    {
                        objPrintingUtility.funWriteTotal("REFUND AMT", rsTenderAmt.getString(3), BillOut, "Format5");
                        BillOut.newLine();
                    }
                }
                BillOut.write(Linefor5);
            }
            rsTenderAmt.close();



            /**
             * print Tax Nos
             */
            objPrintingUtility.funPrintServiceVatNo(BillOut, 4, billNo, billDate, billtaxdtl);

            if (clsGlobalVarClass.gEnableBillSeries)
            {
               sql = "select b.strPrintInclusiveOfTaxOnBill "
                        + " from tblbillseriesbilldtl a,tblbillseries b "
                        + " where a.strBillSeries=b.strBillSeries "
                        + " and a.strHdBillNo='" + billNo + "' "
                        + " and a.strClientCode=b.strClientCode "
                        + " and date(a.dteBillDate)='" + billDate + "' ";
                ResultSet rsBillSeries = clsGlobalVarClass.dbMysql.executeResultSet(sql);
                if (rsBillSeries.next())
                {
                    if (rsBillSeries.getString(1).equals("Y"))
                    {
                        BillOut.write(dashedLineFor40Chars);
                        BillOut.newLine();
                        objPrintingUtility.funPrintBlankSpace("(INCLUSIVE OF ALL TAXES)", BillOut);
                        BillOut.write("(INCLUSIVE OF ALL TAXES)");
                        BillOut.newLine();
                    }
                }
                rsBillSeries.close();
            }
            else
            {
                if (clsGlobalVarClass.gPrintInclusiveOfAllTaxes.equalsIgnoreCase("Y"))
                {
                    BillOut.write(dashedLineFor40Chars);
                    BillOut.newLine();
                    objPrintingUtility.funPrintBlankSpace("(INCLUSIVE OF ALL TAXES)", BillOut);
                    BillOut.write("(INCLUSIVE OF ALL TAXES)");
                    BillOut.newLine();
                }
            }

            int num = clsGlobalVarClass.gBillFooter.trim().length() / 30;
            int num1 = clsGlobalVarClass.gBillFooter.trim().length() % 30;
            int cnt1 = 0;
            for (int cnt = 0; cnt < num; cnt++)
            {
                String footer = clsGlobalVarClass.gBillFooter.trim().substring(cnt1, (cnt1 + 30));
                footer = footer.replaceAll("\n", "");
                BillOut.write("     " + footer.trim());
                BillOut.newLine();
                cnt1 += 30;
            }
            BillOut.write("     " + clsGlobalVarClass.gBillFooter.trim().substring(cnt1, (cnt1 + num1)).trim());
            BillOut.newLine();
            objPrintingUtility.funPrintBlankSpace(user, BillOut);
            BillOut.write("Created By:"+user);
            BillOut.newLine();
            BillOut.newLine();
            BillOut.newLine();
            BillOut.newLine();
            BillOut.newLine();

            if (!clsGlobalVarClass.gOpenCashDrawerAfterBillPrintYN)
            {
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
            }
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
                            objPrintingUtility.funPrintToPrinter(clsGlobalVarClass.gBillPrintPrinterPort, "", "bill", "N", isReprint,"");
                        }
                    }
                }
                else
                {
                    if (!clsGlobalVarClass.flgReprintView)
                    {
                        if (!viewORprint.equalsIgnoreCase("view"))
                        {
                            objPrintingUtility.funPrintToPrinter(clsGlobalVarClass.gBillPrintPrinterPort, "", "bill", "N", isReprint,"");
                        }
                    }
                    else
                    {
                        clsGlobalVarClass.flgReprintView = false;
                    }
                }
            }
            //if (formName.equalsIgnoreCase("sales report"))

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
