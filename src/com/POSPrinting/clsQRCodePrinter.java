/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting;

import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSPrinting.Utility.clsPrintingUtility;
import java.awt.Dimension;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JDialog;
import javax.swing.JFrame;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

/**
 *
 * @author Ajim
 */
public class clsQRCodePrinter
{

    private clsPrintingUtility objPrintingUtility = new clsPrintingUtility();

    /**
     *
     */
    public void funPrintStaticQRCode()
    {
	try
	{

	    String reportName = "com/POSGlobal/reports/rptStaticQRCodeForBenow.jasper";
	     String filePath = System.getProperty("user.dir");
	    filePath = filePath + "/Benow/StaticQRCode.png";

	    HashMap hm = new HashMap();
	    hm.put("staticQRCode", filePath);

	    ArrayList list = new ArrayList();
	    list.add(1);

	    JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(list);
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
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    }

	    new Thread()
	    {
		@Override
		public void run()
		{
		    objPrintingUtility.funPrintJasperExporterInThread(print);
		}
	    }.start();

	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

}
