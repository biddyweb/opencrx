/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: ChartValue.java,v 1.4 2009/01/14 00:03:29 wfro Exp $
 * Description: ChartValue class
 * Revision:    $Revision: 1.4 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/01/14 00:03:29 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004-2008, CRIXP Corp., Switzerland
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 * 
 * * Neither the name of CRIXP Corp. nor the names of the contributors
 * to openCRX may be used to endorse or promote products derived
 * from this software without specific prior written permission
 * 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * ------------------
 * 
 * This product includes software developed by the Apache Software
 * Foundation (http://www.apache.org/).
 * 
 * This product includes software developed by contributors to
 * openMDX (http://www.openmdx.org/)
 */
package org.opencrx.kernel.portal;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.openmdx.application.log.AppLog;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.text.conversion.HtmlEncoder;
import org.openmdx.portal.servlet.Action;
import org.openmdx.portal.servlet.ApplicationContext;
import org.openmdx.portal.servlet.HtmlPage;
import org.openmdx.portal.servlet.attribute.Attribute;
import org.openmdx.portal.servlet.attribute.BinaryValue;
import org.openmdx.portal.servlet.attribute.FieldDef;

public class ChartValue 
    extends BinaryValue
    implements Serializable {

    //-------------------------------------------------------------------------
    public ChartValue(
        Object object, 
        FieldDef fieldDef,
        ApplicationContext application
    ) throws ServiceException {
        super(
            object, 
            fieldDef,
            application
        );
    }

    //-------------------------------------------------------------------------
    public void paint(
        Attribute attribute,
        HtmlPage p,
        String id,
        String label,
        RefObject_1_0 lookupObject,
        int nCols,
        int tabIndex,
        String gapModifier,
        String styleModifier,
        String widthModifier,
        String rowSpanModifier,
        String readonlyModifier,
        String disabledModifier,
        String lockedModifier,
        String stringifiedValue,
        boolean forEditing
    ) throws ServiceException { 
        int currentChartIndex = 
            p.getProperty(HtmlPage.PROPERTY_CHART_ID) != null
                ? ((Integer)p.getProperty(HtmlPage.PROPERTY_CHART_ID)).intValue()
                : 0;
        String currentChartId = Integer.toString(currentChartIndex);
        try {
	        Action binaryValueAction = (Action)this.getValue(false);
	        if(binaryValueAction != null) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                try {
                    this.getBinaryValue(os);
                }
                catch(ServiceException e) {
                    AppLog.warning(e.getMessage(), e.getCause());
                }
		        BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new ByteArrayInputStream(os.toByteArray()))
                );
		        Map data = new HashMap();
		        String line = null;
		        while((line = reader.readLine()) != null) {
		          if(line.indexOf(":") >= 0) {
		            data.put(
		              line.substring(0, line.indexOf(":")),
		              line.substring(line.indexOf(":") + 1)
		            );
		          }
		        }
		
		        // single-valued BinaryValue in place -->
		        p.write(gapModifier);
		        p.write("<td class=\"label\"><span class=\"nw\">", attribute.getLabel(), "</span></td>");
		        p.write("<td ", rowSpanModifier, " class=\"valueL\" ", widthModifier, ">");
                p.write("<span style=\"color:#FFFFFF;\">.</span>");
		
		        // get chart data
		        int nCharts = new Integer(data.get("COUNT").toString().trim()).intValue();
		        for(int chartIndex = 0; chartIndex < nCharts; chartIndex++) {
		            String chartName = "CHART[" + chartIndex + "]";
		            String chartType = data.get(chartName + ".TYPE") + "";
		            
		            // TYPE HORIZBAR
		            if("HORIZBAR".equals(chartType.trim())) {
		                String chartLabel = data.get(chartName + ".LABEL") + "";
		                String xMinValue = data.get(chartName + ".MINVALUE") + "";
		                String xMaxValue = data.get(chartName + ".MAXVALUE") + "";
		                String scaleXTitle = data.get(chartName + ".SCALEXTITLE") + "";
		                String scaleYTitle = data.get(chartName + ".SCALEYTITLE") + "";
		                String yLabels = "";
		                String xValues = "";
                        String xValues2 = "";
		                String borderColors = "";
		                String fillColors = "";
                        String fillColors2 = "";
		                int itemCount = data.get(chartName + ".COUNT") == null
			                ? 0
			                : new Integer(data.get(chartName + ".COUNT").toString().trim()).intValue();
                        styleModifier = attribute.getSpanRow() > 1
                            ? "style=\"width:100%;height:" + (1.2 + (attribute.getSpanRow()-1) * 1.5) + "em;\""
                            : "style=\"width:100%;height:" + (6.0 + itemCount * 1.2) + "em;\"";
		                for(
                            int count = 0; 
                            count < itemCount; 
                            count++
                        ) {
			                yLabels += yLabels.length() == 0 ? "" : ", ";
			                yLabels += "\"" + data.get(chartName + ".LABEL[" + count + "]") + "\"";
			                xValues += xValues.length() == 0 ? "" : ", ";
			                xValues += "\"" + data.get(chartName + ".VAL[" + count + "]") + "\"";
			                borderColors += borderColors.length() == 0 ? "" : ", ";
			                borderColors += "\"" + data.get(chartName + ".BORDER[" + count + "]") + "\"";
			                fillColors += fillColors.length() == 0 ? "" : ", ";
			                fillColors += "\"" + data.get(chartName + ".FILL[" + count + "]") + "\"";
                            // VAL2
                            if(data.get(chartName + ".VAL2[" + count + "]") != null) {
                                xValues2 += xValues2.length() == 0 ? "" : ", ";
                                xValues2 += "\"" + data.get(chartName + ".VAL2[" + count + "]") + "\"";
                            }
                            // FILL2
                            if(data.get(chartName + ".FILL2[" + count + "]") != null) {
                                fillColors2 += fillColors2.length() == 0 ? "" : ", ";
                                fillColors2 += "\"" + data.get(chartName + ".FILL2[" + count + "]") + "\"";                                
                            }
		                }
		                p.write("<div><iframe id=\"Chart", currentChartId, "\" src=\"blank.html\" name=\"Chart", currentChartId, "\" frameborder=0 scrolling=\"no\" ", styleModifier, "></iframe></div>");
                        if(!forEditing) {
    		                p.write("<script language=\"javascript\" type=\"text/javascript\">");
    		                p.write("  function displayChart", currentChartId, "() {");
                            if((xValues.length() > 0) && (fillColors2.length() > 0)) {
                                p.write("    calcChartTypeHorizontalBarsOverlay('Chart", currentChartId, "', '", HtmlEncoder.encode(chartLabel, false), "', '", scaleXTitle, "', '", scaleYTitle, "', Array(", xValues + "), ", xMinValue, ", ", xMaxValue, ", Array(", yLabels, "), Array(", borderColors, "), Array(", fillColors, "), Array(", xValues2, "), Array(", fillColors2, "));");
                            }
                            else {
                                p.write("    calcChartTypeHorizontalBars('Chart", currentChartId, "', '", HtmlEncoder.encode(chartLabel, false), "', '", scaleXTitle, "', '", scaleYTitle, "', Array(", xValues, "), ", xMinValue, ", ", xMaxValue, ", Array(", yLabels, "), Array(", borderColors, "), Array(", fillColors, "));");                            
                            }
    		                p.write("  }");
    		                p.write("</script>");
                        }
		            }
		
		            // TYPE VERTBAR
		            else if("VERTBAR".equals(chartType.trim())) {
		                String chartLabel = data.get(chartName + ".LABEL") + "";
		                String yMinValue = data.get(chartName + ".MINVALUE") + "";
		                String yMaxValue = data.get(chartName + ".MAXVALUE") + "";
		                String scaleXTitle = data.get(chartName + ".SCALEXTITLE") + "";
		                String scaleYTitle = data.get(chartName + ".SCALEYTITLE") + "";
		                String xLabels = "";
		                String yValues = "";
		                String borderColors = "";
		                String fillColors = "";
		                int itemCount = data.get(chartName + ".COUNT") == null
		                    ? 0
		                    : new Integer(data.get(chartName + ".COUNT").toString().trim()).intValue();
                        styleModifier = 
                            "style=\"height: " + 
                            (attribute.getSpanRow() > 1 
                                ? (1.2 + (attribute.getSpanRow()-1) * 1.5) + "em"
                                : "15em") + 
                            "\"";
		                for(int count = 0; count < itemCount; count++) {
			                xLabels += xLabels.length() == 0 ? "" : ", ";
			                xLabels += "\"" + data.get(chartName + ".LABEL[" + count + "]") + "\"";
			                yValues += yValues.length() == 0 ? "" : ", ";
			                yValues += data.get(chartName + ".VAL[" + count + "]");
			                borderColors += borderColors.length() == 0 ? "" : ", ";
			                borderColors += "\"" + data.get(chartName + ".BORDER[" + count + "]") + "\"";
			                fillColors += fillColors.length() == 0 ? "" : ", ";
			                fillColors += "\"" + data.get(chartName + ".FILL[" + count + "]") + "\"";
		                }
		                p.write("<div><iframe id=\"Chart", currentChartId, "\" src=\"blank.html\" name=\"Chart", currentChartId, "\" frameborder=0 scrolling=\"no\" width=\"100%\" ", styleModifier, "></iframe></div>");
                        if(!forEditing) {
    		                p.write("<script language=\"javascript\" type=\"text/javascript\">");
    		                p.write("  function displayChart", currentChartId, "() {");
    		                p.write("   calcChartTypeVerticalBars('Chart", currentChartId, "', '", HtmlEncoder.encode(chartLabel, false), "', '", scaleXTitle, "', '", scaleYTitle, "', Array(", yValues, "), ", yMinValue, ", ", yMaxValue, ", Array(", xLabels, "), Array(", borderColors, "), Array(", fillColors, "));");
    		                p.write("  }");
    		                p.write("</script>");
                        }
		            }
		        }
                p.write("</td>");		        
	        }
        }
        catch(IOException e) {}
        if(!forEditing) {
            p.setProperty(
                HtmlPage.PROPERTY_CHART_ID,
                new Integer(currentChartIndex + 1)
            );
        }
    }    
    
    //-------------------------------------------------------------------------
    // Members
    //-------------------------------------------------------------------------
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3258689922910336053L;

}

//--- End of File -----------------------------------------------------------
