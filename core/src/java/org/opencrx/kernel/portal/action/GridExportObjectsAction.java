/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: GridExportObjectsAction
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2011, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.portal.action;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.opencrx.kernel.backend.Exporter;
import org.openmdx.base.accessor.jmi.cci.RefObject_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.log.SysLog;
import org.openmdx.portal.servlet.Action;
import org.openmdx.portal.servlet.ApplicationContext;
import org.openmdx.portal.servlet.ViewsCache;
import org.openmdx.portal.servlet.action.ActionPerformResult;
import org.openmdx.portal.servlet.action.BoundAction;
import org.openmdx.portal.servlet.attribute.ObjectReferenceValue;
import org.openmdx.portal.servlet.view.Grid;
import org.openmdx.portal.servlet.view.ObjectView;
import org.openmdx.portal.servlet.view.ReferencePane;
import org.openmdx.portal.servlet.view.ShowObjectView;

public abstract class GridExportObjectsAction extends BoundAction {

	//-----------------------------------------------------------------------
	public interface GridExporter {
		
		Object[] exportItem(
			RefObject_1_0 startFrom
		) throws ServiceException ;
		
	}
	
	//-----------------------------------------------------------------------
	static class DefaultGridExporter extends Exporter implements GridExporter {

		public DefaultGridExporter(
			Grid grid,
			List<Path> selectedObjectIdentities,
			String mimeType,
			String referenceFilter,
			int maxItems
		) {
	        this.grid = grid;
	        this.selectedObjectIdentities = selectedObjectIdentities;
	        this.mimeType = mimeType;
	        this.referenceFilter = referenceFilter;
	        this.maxItems = maxItems;
        }
		
		@Override
        public Object[] exportItem(
        	RefObject_1_0 startFrom 
        ) throws ServiceException {
			return super.exportItem(
				startFrom, 
				null, // exportProfile
				this.referenceFilter, 
				this.mimeType
			);
        }
		
		@Override
        protected Collection<?> getContent(
        	TraversedObject startingFrom, 
        	String referenceName
        ) {	
			PersistenceManager pm = JDOHelper.getPersistenceManager(startingFrom.getObject());
			List<Object> content = new ArrayList<Object>();
			// Export selected / displayed grid objects.
			if(
				this.grid.getGridControl().getObjectContainer().getReferenceName().equals(referenceName) &&
				this.maxItems < Integer.MAX_VALUE
			) {
				// Export selected objects only
				if(this.selectedObjectIdentities != null && !this.selectedObjectIdentities.isEmpty()) {
					for(Path identity: this.selectedObjectIdentities) {
						content.add(
							pm.getObjectById(identity)
						);
					}
				}
				// Export objects of current page
				else {
					Object[] rows = this.grid.getRows(pm);
					for(Object row: rows) {
						if(row instanceof Object[]) {
							Object[] cols = (Object[])row;
							if(cols != null && cols.length > 0) {
								content.add(
									((ObjectReferenceValue)cols[0]).getObject()
								);
							}
						}
					}
				}
			} else {
				Collection<?> objs = super.getContent(
		        	startingFrom, 
		        	referenceName
		        );
				int n = 0;
				for(Object obj: objs) {
					content.add(obj);
					n++;
					// Do not export more than maxItems objects
					if(n > this.maxItems) break;
				}
			}
			return content;
        }

		private final Grid grid;
		private final List<Path> selectedObjectIdentities;
		private final String mimeType;
		private final String referenceFilter;
		private final int maxItems;
	}
	
	//-----------------------------------------------------------------------
	protected abstract GridExporter getGridExporter(
		Grid grid,
		List<Path> selectedObjectIdentities,
		int maxItems
	) throws ServiceException;
	
	//-----------------------------------------------------------------------
	@Override
    public ActionPerformResult perform(
        ObjectView view,
        HttpServletRequest request,
        HttpServletResponse response,        
        String parameter,
        HttpSession session,
        Map<String,String[]> requestParameters,
        ViewsCache editViewsCache,
        ViewsCache showViewsCache      
    ) throws IOException, ServletException {
        ApplicationContext app = view.getApplicationContext();
        if(view instanceof ShowObjectView) {
            ShowObjectView currentView = (ShowObjectView)view;    	
        	PersistenceManager pm = app.getNewPmData();
	    	try {
	            int paneIndex = -1;
	            try { 
	            	paneIndex = Integer.parseInt(requestParameters.get(Action.PARAMETER_PANE)[0]);
	            } catch(Exception e) {}
	            int referenceIndex = -1;
	            try {
	            	referenceIndex = Integer.parseInt(requestParameters.get(Action.PARAMETER_REFERENCE)[0]);
	            } catch(Exception e) {}
	            ReferencePane[] referencePanes = currentView.getReferencePane();
	            if(paneIndex < referencePanes.length) {
	                currentView.selectReferencePane(paneIndex);
	                referencePanes[paneIndex].selectReference(referenceIndex);
	                Grid grid = referencePanes[paneIndex].getGrid();
	                if(grid != null) {
	                	List<Path> selectedObjectIdentities = new ArrayList<Path>();
	                    StringTokenizer tokenizer = new StringTokenizer(parameter, " ");
	                    while(tokenizer.hasMoreTokens()) {
	                    	try {
		                        selectedObjectIdentities.add(
		                        	new Path(Action.getParameter(tokenizer.nextToken(), Action.PARAMETER_OBJECTXRI))
		                        );
	                    	} catch(Exception e) {}
	                    }
	                    int maxItems = 500; // default maxItems
	                    try {
	                    	maxItems = Integer.parseInt(requestParameters.get(Action.PARAMETER_SIZE)[0]);
	                    } catch(Exception e) {}
			    		GridExporter exporter = this.getGridExporter(
			    			grid,
			    			selectedObjectIdentities,
			    			maxItems
			    		);
			    		String referenceName = grid.getGridControl().getObjectContainer().getReferenceName();
			    		Object[] item = exporter.exportItem(
			    			view.getRefObject()
			    		);
			    		if(item != null) {
					        response.setContentType((String)item[1]);
					        response.setHeader("Content-disposition", "attachment;filename=" + referenceName + "-" + item[0]);            
					        OutputStream os = response.getOutputStream();
					        byte[] bytes = (byte[])item[2];
					        for(int i = 0; i < bytes.length; i++) {
					            os.write(bytes[i]);
					        }
					        response.setContentLength(bytes.length);
					        os.close();
			    		}
	                }
	            }
	    	}
		    catch (Exception e) {
	            ServiceException e0 = new ServiceException(e);
	            SysLog.warning(e0.getMessage(), e0.getCause());
	            try {
	                pm.currentTransaction().rollback();
	            }
	            catch(Exception e1) {}
	        }	        
	        pm.close();
        }
        return new ActionPerformResult(
        	ActionPerformResult.StatusCode.DONE
        );
    }
	
}
