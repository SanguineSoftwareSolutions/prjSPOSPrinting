/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting.Text.KOT;

import com.POSGlobal.controller.clsBillDtl;
import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSGlobal.controller.clsPosConfigFile;
import com.POSGlobal.controller.clsUtility;
import com.POSGlobal.controller.clsUtility2;
import com.POSPrinting.Utility.clsPrintingUtility;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

/**
 *
 * @author Ajim
 * @date Aug 28, 2017
 */
public class clsCheckKOT
{

    private DecimalFormat decimalFormat = new DecimalFormat("#.###");
    private SimpleDateFormat ddMMyyyyAMPMDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a ");
    private clsUtility objUtility = new clsUtility();
    private clsUtility2 objUtility2 = new clsUtility2();
    private clsPrintingUtility objPrintingUtility = new clsPrintingUtility();
    private final String dashedLineFor40Chars = "  --------------------------------------";

    /**
     *
     * @param TableNo
     * @param WaiterName
     * @param printYN
     */
    public void funCkeckKotTextFile(String TableNo, String WaiterName, String printYN)
    {
	try
	{
	    objPrintingUtility.funCreateTempFolder();
	    String filePath = System.getProperty("user.dir");
	    File Text_Check_KOT = new File(filePath + "/Temp/Temp_KOT.txt");
	    FileWriter fstream = new FileWriter(Text_Check_KOT);

	    BufferedWriter checkKotOut = new BufferedWriter(fstream);

	    objPrintingUtility.funPrintBlankSpace("CHECK KOT", checkKotOut);
	    checkKotOut.write("CHECK KOT");
	    checkKotOut.newLine();
	    checkKotOut.write(dashedLineFor40Chars);
	    checkKotOut.newLine();

	    PreparedStatement pst = null;
	    String sql_CheckKot = "select a.strItemName, sum(a.dblItemQuantity),b.strTableName"
		    + " ,DATE_FORMAT(a.dteDateCreated,'%d-%m-%y %h:%i')dateANDtime "
		    + " from tblitemrtemp a,tbltablemaster b,tblitemmaster c "
		    + " where a.strTableNo=b.strTableNo and left(a.strItemCode,7)=c.strItemCode "
		    + " and a.strTableNo=? "
		    + " and c.strOpenItem='N' "
		    + " and a.strNCKotYN='N' "
		    + " group by a.strItemCode,a.strItemName "
		    + " order by a.strSerialNo";
	    System.out.println(sql_CheckKot);
	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_CheckKot);
	    pst.setString(1, TableNo);
	    ResultSet rs_checkKOT = pst.executeQuery();
	    boolean flag_first = true;
	    while (rs_checkKOT.next())
	    {
		if (flag_first)
		{
		    checkKotOut.write("  TABLE NO.: " + rs_checkKOT.getString(3));
		    if ("null".equalsIgnoreCase(WaiterName) || WaiterName.isEmpty())
		    {
		    }
		    else
		    {
			checkKotOut.newLine();
			checkKotOut.write("  WAITER NAME: " + WaiterName);
		    }

		    checkKotOut.newLine();
		    checkKotOut.write("  DATE TIME: " + rs_checkKOT.getString(4));
		    checkKotOut.newLine();
		    checkKotOut.write(dashedLineFor40Chars);
		    checkKotOut.newLine();
		    checkKotOut.write("     QTY          ITEM NAME");
		    checkKotOut.newLine();
		    checkKotOut.write(dashedLineFor40Chars);
		    checkKotOut.newLine();
		    String itemqty = rs_checkKOT.getString(2);

		    switch (itemqty.length())
		    {
			case 7:
			    if (rs_checkKOT.getString(1).startsWith("-->"))
			    {
				if (!clsGlobalVarClass.gPrintModQtyOnKOT)
				{
				    checkKotOut.write("      " + rs_checkKOT.getString(1).toUpperCase());
				}
				else
				{
				    checkKotOut.write("  " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
				}
			    }
			    else
			    {
				checkKotOut.write("  " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
			    }
			    break;

			case 6:
			    if (rs_checkKOT.getString(1).startsWith("-->"))
			    {
				if (!clsGlobalVarClass.gPrintModQtyOnKOT)
				{
				    checkKotOut.write("       " + rs_checkKOT.getString(1).toUpperCase());
				}
				else
				{
				    checkKotOut.write("   " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
				}
			    }
			    else
			    {
				checkKotOut.write("   " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
			    }
			    break;

			case 5:
			    if (rs_checkKOT.getString(1).startsWith("-->"))
			    {
				if (!clsGlobalVarClass.gPrintModQtyOnKOT)
				{
				    checkKotOut.write("        " + rs_checkKOT.getString(1).toUpperCase());
				}
				else
				{
				    checkKotOut.write("    " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
				}
			    }
			    else
			    {
				checkKotOut.write("    " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
			    }
			    break;

			case 4:
			    if (rs_checkKOT.getString(1).startsWith("-->"))
			    {
				if (!clsGlobalVarClass.gPrintModQtyOnKOT)
				{
				    checkKotOut.write("         " + rs_checkKOT.getString(1).toUpperCase());
				}
				else
				{
				    checkKotOut.write("     " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
				}
			    }
			    else
			    {
				checkKotOut.write("     " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
			    }
			    break;
		    }
		    flag_first = false;
		}
		else
		{
		    String itemqty = rs_checkKOT.getString(2);
		    switch (itemqty.length())
		    {
			case 7:
			    if (rs_checkKOT.getString(1).startsWith("-->"))
			    {
				if (!clsGlobalVarClass.gPrintModQtyOnKOT)
				{
				    checkKotOut.write("      " + rs_checkKOT.getString(1).toUpperCase());
				}
				else
				{
				    checkKotOut.write("  " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
				}
			    }
			    else
			    {
				checkKotOut.write("  " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
			    }
			    break;

			case 6:
			    if (rs_checkKOT.getString(1).startsWith("-->"))
			    {
				if (!clsGlobalVarClass.gPrintModQtyOnKOT)
				{
				    checkKotOut.write("       " + rs_checkKOT.getString(1).toUpperCase());
				}
				else
				{
				    checkKotOut.write("   " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
				}
			    }
			    else
			    {
				checkKotOut.write("   " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
			    }
			    break;

			case 5:
			    if (rs_checkKOT.getString(1).startsWith("-->"))
			    {
				if (!clsGlobalVarClass.gPrintModQtyOnKOT)
				{
				    checkKotOut.write("        " + rs_checkKOT.getString(1).toUpperCase());
				}
				else
				{
				    checkKotOut.write("    " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
				}
			    }
			    else
			    {
				checkKotOut.write("    " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
			    }
			    break;

			case 4:
			    if (rs_checkKOT.getString(1).startsWith("-->"))
			    {
				if (!clsGlobalVarClass.gPrintModQtyOnKOT)
				{
				    checkKotOut.write("         " + rs_checkKOT.getString(1).toUpperCase());
				}
				else
				{
				    checkKotOut.write("     " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
				}
			    }
			    else
			    {
				checkKotOut.write("     " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
			    }
			    break;
		    }
		}
		checkKotOut.newLine();
	    }

	    sql_CheckKot = "select a.strItemName, a.dblItemQuantity,b.strTableName"
		    + " ,DATE_FORMAT(a.dteDateCreated,'%d-%m-%y %h:%i')dateANDtime "
		    + " from tblitemrtemp a,tbltablemaster b,tblitemmaster c "
		    + " where a.strTableNo=b.strTableNo and left(a.strItemCode,7)=c.strItemCode "
		    + " and a.strTableNo=? and c.strOpenItem='Y' ";
	    //System.out.println(sql_CheckKot);
	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_CheckKot);
	    pst.setString(1, TableNo);
	    rs_checkKOT = pst.executeQuery();
	    while (rs_checkKOT.next())
	    {
		if (flag_first)
		{
		    checkKotOut.write("  TABLE NO.  : " + rs_checkKOT.getString(3));
		    if ("null".equalsIgnoreCase(WaiterName) || WaiterName.isEmpty())
		    {
		    }
		    else
		    {
			checkKotOut.newLine();
			checkKotOut.write("  WAITER NAME: " + WaiterName);
		    }

		    checkKotOut.newLine();
		    checkKotOut.write("  DATE & TIME: " + rs_checkKOT.getString(4));
		    checkKotOut.newLine();
		    checkKotOut.write(dashedLineFor40Chars);
		    checkKotOut.newLine();
		    checkKotOut.write("     QTY          ITEM NAME");
		    checkKotOut.newLine();
		    checkKotOut.write(dashedLineFor40Chars);
		    checkKotOut.newLine();
		    String itemqty = rs_checkKOT.getString(2);

		    switch (itemqty.length())
		    {
			case 7:
			    if (rs_checkKOT.getString(1).startsWith("-->"))
			    {
				if (!clsGlobalVarClass.gPrintModQtyOnKOT)
				{
				    checkKotOut.write("      " + rs_checkKOT.getString(1).toUpperCase());
				}
				else
				{
				    checkKotOut.write("  " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
				}
			    }
			    else
			    {
				checkKotOut.write("  " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
			    }
			    break;

			case 6:
			    if (rs_checkKOT.getString(1).startsWith("-->"))
			    {
				if (!clsGlobalVarClass.gPrintModQtyOnKOT)
				{
				    checkKotOut.write("       " + rs_checkKOT.getString(1).toUpperCase());
				}
				else
				{
				    checkKotOut.write("   " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
				}
			    }
			    else
			    {
				checkKotOut.write("   " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
			    }
			    break;

			case 5:
			    if (rs_checkKOT.getString(1).startsWith("-->"))
			    {
				if (!clsGlobalVarClass.gPrintModQtyOnKOT)
				{
				    checkKotOut.write("        " + rs_checkKOT.getString(1).toUpperCase());
				}
				else
				{
				    checkKotOut.write("    " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
				}
			    }
			    else
			    {
				checkKotOut.write("    " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
			    }
			    break;

			case 4:
			    if (rs_checkKOT.getString(1).startsWith("-->"))
			    {
				if (!clsGlobalVarClass.gPrintModQtyOnKOT)
				{
				    checkKotOut.write("         " + rs_checkKOT.getString(1).toUpperCase());
				}
				else
				{
				    checkKotOut.write("     " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
				}
			    }
			    else
			    {
				checkKotOut.write("     " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
			    }
			    break;
		    }
		    flag_first = false;
		}
		else
		{
		    String itemqty = rs_checkKOT.getString(2);
		    switch (itemqty.length())
		    {
			case 7:
			    if (rs_checkKOT.getString(1).startsWith("-->"))
			    {
				if (!clsGlobalVarClass.gPrintModQtyOnKOT)
				{
				    checkKotOut.write("      " + rs_checkKOT.getString(1).toUpperCase());
				}
				else
				{
				    checkKotOut.write("  " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
				}
			    }
			    else
			    {
				checkKotOut.write("  " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
			    }
			    break;

			case 6:
			    if (rs_checkKOT.getString(1).startsWith("-->"))
			    {
				if (!clsGlobalVarClass.gPrintModQtyOnKOT)
				{
				    checkKotOut.write("       " + rs_checkKOT.getString(1).toUpperCase());
				}
				else
				{
				    checkKotOut.write("   " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
				}
			    }
			    else
			    {
				checkKotOut.write("   " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
			    }
			    break;

			case 5:
			    if (rs_checkKOT.getString(1).startsWith("-->"))
			    {
				if (!clsGlobalVarClass.gPrintModQtyOnKOT)
				{
				    checkKotOut.write("        " + rs_checkKOT.getString(1).toUpperCase());
				}
				else
				{
				    checkKotOut.write("    " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
				}
			    }
			    else
			    {
				checkKotOut.write("    " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
			    }
			    break;

			case 4:
			    if (rs_checkKOT.getString(1).startsWith("-->"))
			    {
				if (!clsGlobalVarClass.gPrintModQtyOnKOT)
				{
				    checkKotOut.write("         " + rs_checkKOT.getString(1).toUpperCase());
				}
				else
				{
				    checkKotOut.write("     " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
				}
			    }
			    else
			    {
				checkKotOut.write("     " + rs_checkKOT.getString(2) + "    " + rs_checkKOT.getString(1).toUpperCase());
			    }
			    break;
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

	    objPrintingUtility.funShowTextFile(Text_Check_KOT, "", "");

	    if (printYN.equalsIgnoreCase("Y"))
	    {
		objPrintingUtility.funPrintToPrinter(clsGlobalVarClass.gBillPrintPrinterPort, "", "checkkot", "N", false);
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    /**
     *
     * @param TableNo
     * @param WaiterName
     * @param printYN
     */
    public void funCkeckKotForJasper(String TableNo, String WaiterName, String printYN)
    {
	HashMap hm = new HashMap();
	try
	{

	    hm.put("KOTType", "CHECK KOT");
	    List<clsBillDtl> listOfKOTDetail = new ArrayList<>();

	    PreparedStatement pst = null;
	    String sql_CheckKot = "select a.strItemName, sum(a.dblItemQuantity),b.strTableName "
		    + " ,DATE_FORMAT(a.dteDateCreated,'%d-%m-%y %h:%i')dateANDtime "
		    + " from tblitemrtemp a,tbltablemaster b,tblitemmaster c "
		    + " where a.strTableNo=b.strTableNo and left(a.strItemCode,7)=c.strItemCode "
		    + " and a.strTableNo=? "
		    + " and c.strOpenItem='N' "
		    + " and a.strNCKotYN='N' "
		    + " group by a.strItemCode,a.strItemName "
		    + " order by a.strSerialNo";
	    System.out.println(sql_CheckKot);
	    pst = clsGlobalVarClass.conPrepareStatement.prepareStatement(sql_CheckKot);
	    pst.setString(1, TableNo);
	    ResultSet rs_checkKOT = pst.executeQuery();
	    boolean flag_first = true;
	    while (rs_checkKOT.next())
	    {
		String itemName = rs_checkKOT.getString(1).toUpperCase();
		if (flag_first)
		{

		    hm.put("tableNo", rs_checkKOT.getString(3));
		    if ("null".equalsIgnoreCase(WaiterName) || WaiterName.isEmpty())
		    {
		    }
		    else
		    {
			hm.put("waiterName", WaiterName);
		    }

		    hm.put("DATE_TIME", rs_checkKOT.getString(4));

		    if (itemName.startsWith("-->"))
		    {
			if (clsGlobalVarClass.gPrintModQtyOnKOT)
			{
			    clsBillDtl objBillDtl = new clsBillDtl();
			    objBillDtl.setDblQuantity(rs_checkKOT.getDouble(2));
			    objBillDtl.setStrItemName(rs_checkKOT.getString(1).toUpperCase());
			    listOfKOTDetail.add(objBillDtl);
			}
			else
			{
			    clsBillDtl objBillDtl = new clsBillDtl();
			    objBillDtl.setDblQuantity(0);
			    objBillDtl.setStrItemName(rs_checkKOT.getString(1).toUpperCase());
			    listOfKOTDetail.add(objBillDtl);
			}
		    }
		    else
		    {
			clsBillDtl objBillDtl = new clsBillDtl();
			objBillDtl.setDblQuantity(rs_checkKOT.getDouble(2));
			objBillDtl.setStrItemName(rs_checkKOT.getString(1).toUpperCase());
			listOfKOTDetail.add(objBillDtl);
		    }

		    flag_first = false;
		}
		else
		{
		    if (itemName.startsWith("-->"))
		    {
			if (clsGlobalVarClass.gPrintModQtyOnKOT)
			{
			    clsBillDtl objBillDtl = new clsBillDtl();
			    objBillDtl.setDblQuantity(rs_checkKOT.getDouble(2));
			    objBillDtl.setStrItemName(rs_checkKOT.getString(1).toUpperCase());
			    listOfKOTDetail.add(objBillDtl);
			}
			else
			{
			    clsBillDtl objBillDtl = new clsBillDtl();
			    objBillDtl.setDblQuantity(0);
			    objBillDtl.setStrItemName(rs_checkKOT.getString(1).toUpperCase());
			    listOfKOTDetail.add(objBillDtl);
			}
		    }
		    else
		    {
			clsBillDtl objBillDtl = new clsBillDtl();
			objBillDtl.setDblQuantity(rs_checkKOT.getDouble(2));
			objBillDtl.setStrItemName(rs_checkKOT.getString(1).toUpperCase());
			listOfKOTDetail.add(objBillDtl);
		    }
		}
	    }
	    List<List<clsBillDtl>> listData = new ArrayList<>();
	    hm.put("listOfItemDtl", listOfKOTDetail);
	    listData.add(listOfKOTDetail);
	    String reportName = "com/POSGlobal/reports/rptCheckKOTForJasper.jasper";
	    JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(listData);
	    InputStream is = this.getClass().getClassLoader().getResourceAsStream(reportName);

	    JasperPrint print = JasperFillManager.fillReport(is, hm, beanColDataSource);
	    JRViewer viewer = new JRViewer(print);
	    JFrame jf = new JFrame();
	    jf.getContentPane().add(viewer);
	    jf.validate();
	    if (clsGlobalVarClass.gShowBill)
	    {
		jf.setVisible(true);
		jf.setSize(new Dimension(500, 900));
		jf.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    }

	    if (printYN.equalsIgnoreCase("Y"))
	    {
		//--- Print the document
		clsUtility2 objUtility2 = new clsUtility2();
		objUtility2.funPrintJasperKOT(clsGlobalVarClass.gBillPrintPrinterPort, print);
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }
}
