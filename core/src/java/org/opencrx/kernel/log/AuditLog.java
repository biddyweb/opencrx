/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: AuditLog.java,v 1.3 2005/05/11 23:38:30 wfro Exp $
 * Description: AuditLog
 * Revision:    $Revision: 1.3 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2005/05/11 23:38:30 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2004, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.log;

import java.util.Properties;

import org.openmdx.application.Dependencies;
import org.openmdx.kernel.exception.VersionMismatchException;
import org.openmdx.kernel.log.Config;
import org.openmdx.kernel.log.LogLevel;
import org.openmdx.kernel.log.SysLog;
import org.openmdx.kernel.log.impl.Log;

public class AuditLog extends Log {

    private AuditLog() {}

	/**
	 * Set the config name that the logs pertain to. Must be called
	 * before doing any logging.
	 *
	 * @param cfgName  a config name
	 */
	public static void setConfigName(
	    String cfgName
	) {
		if (!initialized) {
			if (cfgName == null) return;  // reject null strings
			cfgName = cfgName.trim();
			if (cfgName.length()==0) return; // reject empty strings
			AuditLog.configName = cfgName;
		}else{
			SysLog.error("A config name must be set before any logging " +
			             "takes place", new Exception());
		}
	}

	/**
	 * Set a log source object. The logging framework uses the toString() method
	 * to determine the log source for each log event.
	 * The object may be set at any time.
	 *
	 * <p>
	 * Dynamic log source example:
	 * <code>
	 *
	 * class LogSource
	 * {
	 *   public String toString()
	 *   {
	 * 		return "LogSource-" + System.currentTimeMillis();
	 * 	 }
	 * }
	 *
	 * LogSource logSource = new LogSource();
	 * AuditLog.setLogSource(logSource)
	 *
	 * </code>
	 *
	 * <p>
	 * Static log source example:
	 * <code>
	 *
	 * AuditLog.setLogSource("LogSource")
	 *
	 * </code>
	 *
	 * @param logSource a log source object
	 */
	public static void setLogSource(
	    Object logSource
	) {
		if (logSource == null) return;
		AuditLog.logSource = logSource;
	}

	/**
	 * Sets the log properties [Config-Level-3]. setLogProperties() must be
	 * called before doing any logging.
	 *
	 * @param properties  the log properties
	 */
	public static void setLogProperties(
	    Properties props
	) {
		// Setting log properties is only possible when the config
		// object does not exist and nothing has been logged yet.
		if (!initialized) {
			AuditLog.logProperties = props;
		} else{
			SysLog.error("Log properties must be set before any logging " +
			             "takes place", new Exception());
		}
	}

	/**
     * Returns the logger's configuration object
	 *
     * @return  the configuration object
     */
    public static Config getLogConfig(
    ) {
		if (!initialized) init();
        return new Config(singleton.getConfig());
    }

	/**
     * Checks if trace logging is active
     *
     * <p>
     * Applications can use this method before they call AuditLog.trace(...) if
     * the log string creation is very time consuming.
     *
     * <pre>
     *      AuitLog.trace("Customer created", "First=Mark, Last=Smith");
	 *
     *      if (AuditLog.isTraceOn()) {
     *	        summary = expensive_creation();
     *	        detail  = expensive_creation();
     *          AuditLog.trace(summary, detail);
     *    	}
     * </pre>
	 *
     * @return  true if trace is active
     */
    public static boolean isTraceOn(
    ) {
		if (!initialized) init();
        return (singleton.getLoggingLevel() >= LogLevel.LOG_LEVEL_TRACE);
    }

    /**
     * Logs a text string at CRITICAL_ERROR_LEVEL.
     *
     * @param logString   
     *         a concise summary message. The message must be single line and
     *         must therefore not contain any '\r' or '\n' characters. The '\r' 
     *         and '\n' characters are removed silently from the message.
     * @param logObj  
     *         a log object providing detail information. The log object is
     *         stringified using its <code>toString</code> method before getting
     *         logged. The log object may be a null object. If the log object
     *         is a <code>Throwable</code> it's message and stack trace is 
     *         logged.
     * @see #criticalError(String)
     * @see #criticalError(String, Object, int)
     */
    public static void criticalError(
		String logString,
		Object logObj
	) {
		if (!initialized) init();

		if (LogLevel.LOG_LEVEL_CRITICAL_ERROR <= singleton.getLoggingLevel()) {
        	singleton.logString(
        	    AuditLog.logSource,
        		logString,
        		logObj,
        		LogLevel.LOG_LEVEL_CRITICAL_ERROR,
        		0);
		}
    }

	/**
     * Logs a text string at ERROR_LEVEL.
     *
     * @param logString   
     *         a concise summary message. The message must be single line and
     *         must therefore not contain any '\r' or '\n' characters. The '\r' 
     *         and '\n' characters are removed silently from the message.
     * @param logObj  
     *         a log object providing detail information. The log object is
     *         stringified using its <code>toString</code> method before getting
     *         logged. The log object may be a null object. If the log object
     *         is a <code>Throwable</code> it's message and stack trace is 
     *         logged.
     * @see #error(String)
     * @see #error(String, Object, int)
     */
    public static void error(
		String logString,
		Object logObj
    ) {
		if (!initialized) init();
		if (LogLevel.LOG_LEVEL_ERROR <= singleton.getLoggingLevel()) {
        	singleton.logString(
        	    AuditLog.logSource,
        		logString,
        		logObj,
        		LogLevel.LOG_LEVEL_ERROR,
        		0);
		}
    }

	/**
     * Logs a text string at ERROR_LEVEL.
     *
     * @param logString   
     *         a concise summary message. The message must be single line and
     *         must therefore not contain any '\r' or '\n' characters. The '\r' 
     *         and '\n' characters are removed silently from the message.
     * @see #error(String, Object)
     * @see #error(String, Object, int)
     */
    public static void error(
        String logString
    ) {
		if (!initialized) init();
		if (LogLevel.LOG_LEVEL_ERROR <= singleton.getLoggingLevel()) {
        	singleton.logString(
        	    AuditLog.logSource,
        		logString,
        		null,
        		LogLevel.LOG_LEVEL_ERROR,
        		0);
		}
    }

	/**
     * Logs a text string at ERROR_LEVEL.
     *
     * @param logString   
     *         a concise summary message. The message must be single line and
     *         must therefore not contain any '\r' or '\n' characters. The '\r' 
     *         and '\n' characters are removed silently from the message.
     * @param logObj  
     *         a log object providing detail information. The log object is
     *         stringified using its <code>toString</code> method before getting
     *         logged. The log object may be a null object. If the log object
     *         is a <code>Throwable</code> it's message and stack trace is 
     *         logged.
     * @param callStackOff  
     *         a call stack correction offset. The offset must be a positive 
     *         number: 0, 1, 2, 3, ...
     * @see #error(String)
     * @see #error(String, Object)
     */
    public static void error(
		String logString,
		Object logObj,
		int    callStackOff
    ) {
		if (!initialized) init();
		if (LogLevel.LOG_LEVEL_ERROR <= singleton.getLoggingLevel()) {
        	singleton.logString(
        	    AuditLog.logSource,
        		logString,
        		logObj,
        		LogLevel.LOG_LEVEL_ERROR,
        		callStackOff);
		}
    }

    /**
     * Logs a text string at WARNING_LEVEL.
     *
     * @param logString   
     *         a concise summary message. The message must be single line and
     *         must therefore not contain any '\r' or '\n' characters. The '\r' 
     *         and '\n' characters are removed silently from the message.
     * @param logObj  
     *         a log object providing detail information. The log object is
     *         stringified using its <code>toString</code> method before getting
     *         logged. The log object may be a null object. If the log object
     *         is a <code>Throwable</code> it's message and stack trace is 
     *         logged.
     * @see #warning(String)
     * @see #warning(String, Object, int)
     */
    public static void warning(
		String logString,
		Object logObj
    ) {
		if (!initialized) init();
		if (LogLevel.LOG_LEVEL_WARNING <= singleton.getLoggingLevel()) {
        	singleton.logString(
        	    AuditLog.logSource,
        		logString,
        		logObj,
        		LogLevel.LOG_LEVEL_WARNING,
        		0);
		}
    }

    /**
     * Logs a text string at WARNING_LEVEL.
     *
     * @param logString   
     *         a concise summary message. The message must be single line and
     *         must therefore not contain any '\r' or '\n' characters. The '\r' 
     *         and '\n' characters are removed silently from the message.
     * @see #warning(String, Object)
     * @see #warning(String, Object, int)
     */
    public static void warning(
        String logString
    ) {
		if (!initialized) init();
		if (LogLevel.LOG_LEVEL_WARNING <= singleton.getLoggingLevel()) {
        	singleton.logString(
        	    AuditLog.logSource,
        		logString,
        		null,
        		LogLevel.LOG_LEVEL_WARNING,
        		0);
		}
    }

    /**
     * Logs a text string at WARNING_LEVEL.
     *
     * @param logString   
     *         a concise summary message. The message must be single line and
     *         must therefore not contain any '\r' or '\n' characters. The '\r' 
     *         and '\n' characters are removed silently from the message.
     * @param logObj  
     *         a log object providing detail information. The log object is
     *         stringified using its <code>toString</code> method before getting
     *         logged. The log object may be a null object. If the log object
     *         is a <code>Throwable</code> it's message and stack trace is 
     *         logged.
     * @param callStackOff  
     *         a call stack correction offset. The offset must be a positive 
     *         number: 0, 1, 2, 3, ...
     * @see #warning(String)
     * @see #warning(String, Object)
     */
    public static void warning(
        String logString,
		Object logObj,
		int    callStackOff
    ) {
		if (!initialized) init();
		if (LogLevel.LOG_LEVEL_WARNING <= singleton.getLoggingLevel()) {
        	singleton.logString(
        	    AuditLog.logSource,
        		logString,
        		logObj,
        		LogLevel.LOG_LEVEL_WARNING,
        		callStackOff);
		}
    }

    /**
     * Logs a text string at INFO_LEVEL.
     *
     * @param logString   
     *         a concise summary message. The message must be single line and
     *         must therefore not contain any '\r' or '\n' characters. The '\r' 
     *         and '\n' characters are removed silently from the message.
     * @param logObj  
     *         a log object providing detail information. The log object is
     *         stringified using its <code>toString</code> method before getting
     *         logged. The log object may be a null object. If the log object
     *         is a <code>Throwable</code> it's message and stack trace is 
     *         logged.
     * @see #info(String)
     * @see #info(String, Object, int)
     */
    public static void info(
        String logString,
        Object logObj
    ) {
		if (!initialized) init();
		if (LogLevel.LOG_LEVEL_INFO <= singleton.getLoggingLevel()) {
        	singleton.logString(
        	    AuditLog.logSource,
        		logString,
        		logObj,
        		LogLevel.LOG_LEVEL_INFO,
        		0);
		}
    }

    /**
     * Logs a text string at INFO_LEVEL.
     *
     * @param logString   
     *         a concise summary message. The message must be single line and
     *         must therefore not contain any '\r' or '\n' characters. The '\r' 
     *         and '\n' characters are removed silently from the message.
     * @see #info(String, Object)
     * @see #info(String, Object, int)
     */
    public static void info(
        String logString
    ) {
		if (!initialized) init();
		if (LogLevel.LOG_LEVEL_INFO <= singleton.getLoggingLevel()) {
        	singleton.logString(
        	    AuditLog.logSource,
        		logString,
        		null,
        		LogLevel.LOG_LEVEL_INFO,
        		0);
		}
	}

    /**
     * Logs a text string at INFO_LEVEL.
     *
     * @param logString   
     *         a concise summary message. The message must be single line and
     *         must therefore not contain any '\r' or '\n' characters. The '\r' 
     *         and '\n' characters are removed silently from the message.
     * @param logObj  
     *         a log object providing detail information. The log object is
     *         stringified using its <code>toString</code> method before getting
     *         logged. The log object may be a null object. If the log object
     *         is a <code>Throwable</code> it's message and stack trace is 
     *         logged.
     * @param callStackOff  
     *         a call stack correction offset. The offset must be a positive 
     *         number: 0, 1, 2, 3, ...
     * @see #info(String)
     * @see #info(String, Object)
     */
    public static void info(
        String logString,
        Object logObj,
        int    callStackOff
    ) {
		if (!initialized) init();
		if (LogLevel.LOG_LEVEL_INFO <= singleton.getLoggingLevel()) {
        	singleton.logString(
        	    AuditLog.logSource,
        		logString,
        		logObj,
        		LogLevel.LOG_LEVEL_INFO,
        		callStackOff);
		}
    }

    /**
     * Logs a text string at DETAIL_LEVEL.
     *
     * @param logString   
     *         a concise summary message. The message must be single line and
     *         must therefore not contain any '\r' or '\n' characters. The '\r' 
     *         and '\n' characters are removed silently from the message.
     * @param logObj  
     *         a log object providing detail information. The log object is
     *         stringified using its <code>toString</code> method before getting
     *         logged. The log object may be a null object. If the log object
     *         is a <code>Throwable</code> it's message and stack trace is 
     *         logged.
     * @see #detail(String)
     * @see #detail(String, Object, int)
     */
    public static void detail(
        String logString,
        Object logObj
    ) {
		if (!initialized) init();
		if (LogLevel.LOG_LEVEL_DETAIL <= singleton.getLoggingLevel()) {
        	singleton.logString(
        	    AuditLog.logSource,
        		logString,
        		logObj,
        		LogLevel.LOG_LEVEL_DETAIL,
        		0);
		}
    }

    /**
     * Logs a text string at DETAIL_LEVEL.
     *
     * @param logString   
     *         a concise summary message. The message must be single line and
     *         must therefore not contain any '\r' or '\n' characters. The '\r' 
     *         and '\n' characters are removed silently from the message.
     * @see #detail(String, Object)
     * @see #detail(String, Object, int)
     */
    public static void detail(
        String logString
    ) {
		if (!initialized) init();
		if (LogLevel.LOG_LEVEL_DETAIL <= singleton.getLoggingLevel()) {
        	singleton.logString(
        	    AuditLog.logSource,
        		logString,
        		null,
        		LogLevel.LOG_LEVEL_DETAIL,
        		0);
		}
    }

    /**
     * Logs a text string at DETAIL_LEVEL.
     *
     * @param logString   
     *         a concise summary message. The message must be single line and
     *         must therefore not contain any '\r' or '\n' characters. The '\r' 
     *         and '\n' characters are removed silently from the message.
     * @param logObj  
     *         a log object providing detail information. The log object is
     *         stringified using its <code>toString</code> method before getting
     *         logged. The log object may be a null object. If the log object
     *         is a <code>Throwable</code> it's message and stack trace is 
     *         logged.
     * @param callStackOff  
     *         a call stack correction offset. The offset must be a positive 
     *         number: 0, 1, 2, 3, ...
     * @see #detail(String)
     * @see #detail(String, Object)
     */
    public static void detail(
        String logString,
    	Object logObj,
        int    callStackOff
    ) {
		if (!initialized) init();
		if (LogLevel.LOG_LEVEL_DETAIL <= singleton.getLoggingLevel()) {
        	singleton.logString(
        	    AuditLog.logSource,
        		logString,
        		logObj,
        		LogLevel.LOG_LEVEL_DETAIL,
        		callStackOff);
		}
    }

    /**
     * Logs a text string at TRACE_LEVEL.
     *
     * @param logString   
     *         a concise summary message. The message must be single line and
     *         must therefore not contain any '\r' or '\n' characters. The '\r' 
     *         and '\n' characters are removed silently from the message.
     * @param logObj  
     *         a log object providing detail information. The log object is
     *         stringified using its <code>toString</code> method before getting
     *         logged. The log object may be a null object. If the log object
     *         is a <code>Throwable</code> it's message and stack trace is 
     *         logged.
     * @see #trace(String)
     * @see #trace(String, Object, int)
     */
    public static void trace(
        String logString,
    	Object logObj
    ) {
		if (!initialized) init();
		if (LogLevel.LOG_LEVEL_TRACE <= singleton.getLoggingLevel()) {
        	singleton.logString(
    	    	AuditLog.logSource,
	        	logString,
        		logObj,
        		LogLevel.LOG_LEVEL_TRACE,
        		0);
		}
    }

    /**
     * Logs a text string at TRACE_LEVEL.
     *
     * @param logString   
     *         a concise summary message. The message must be single line and
     *         must therefore not contain any '\r' or '\n' characters. The '\r' 
     *         and '\n' characters are removed silently from the message.
     * @see #trace(String, Object)
     * @see #trace(String, Object, int)
     */
    public static void trace(
        String logString
    ) {
		if (!initialized) init();
		if (LogLevel.LOG_LEVEL_TRACE <= singleton.getLoggingLevel()) {
        	singleton.logString(
        	    AuditLog.logSource,
        		logString,
        		null,
        		LogLevel.LOG_LEVEL_TRACE,
        		0);
		}
    }

    /**
     * Logs a text string at TRACE_LEVEL.
     *
     * @param logString   
     *         a concise summary message. The message must be single line and
     *         must therefore not contain any '\r' or '\n' characters. The '\r' 
     *         and '\n' characters are removed silently from the message.
     * @param logObj  
     *         a log object providing detail information. The log object is
     *         stringified using its <code>toString</code> method before getting
     *         logged. The log object may be a null object. If the log object
     *         is a <code>Throwable</code> it's message and stack trace is 
     *         logged.
     * @param callStackOff  
     *         a call stack correction offset. The offset must be a positive 
     *         number: 0, 1, 2, 3, ...
     * @see #trace(String)
     * @see #trace(String, Object)
     */
    public static void trace(
        String logString,
        Object logObj,
        int    callStackOff
    ) {
		if (!initialized) init();
		if (LogLevel.LOG_LEVEL_TRACE <= singleton.getLoggingLevel()) {
        	singleton.logString(
        	    AuditLog.logSource,
        		logString,
        		logObj,
        		LogLevel.LOG_LEVEL_TRACE,
        		callStackOff);
		}
    }

	/**
	 * Set the application name string that the logs pertain to. Must be called
	 * before doing any logging.
	 *
	 * @param appName the application name
     * @deprecated use {@link AuditLog#setConfigName(String)} instead
	 */
	public static void setApplicationName(String appName)
	{
		setConfigName(appName);
	}

    /**
     * Set logging level
     *
     * <p>
     * This method should not be used by applications. The logging level is set
     * in the log property file.
     *
     * @param level  a new logging level
     * @deprecated use {@link Config#setLogLevel(int)} from the
     *                 {@link AuditLog#getLogConfig()} class
     */
	public static void setLogLevel(
	    int logLevel
	) {
		if (!initialized) init();
		singleton.setLoggingLevel(logLevel);
	}

    private static void  init(
    ) {
       	synchronized(singleton) {
            if (!initialized) {
        		initialized = true;
        
        		singleton.loadConfig(AuditLog.configName, AuditLog.logProperties);
        		singleton.loadMechanisms();
          
        		SysLog.register(AuditLog.getLogConfig());
                SysLog.trace("AuditLog (CfgName=" + AuditLog.configName + ") initialized");
        
                // openmdx jar version logging
                AuditLog.info("openMDX kernel version     : " 
                            + org.openmdx.kernel.Version.getImplementationVersion());
                            
                AuditLog.info("openMDX base version       : " 
                            + org.openmdx.base.Version.getImplementationVersion());
                
                AuditLog.info("openMDX application version: " 
                            + org.openmdx.application.Version.getImplementationVersion());
                
                // openmdx jar version dependeny check
                try {
                    Dependencies.checkDependencies();
                } 
                catch (VersionMismatchException exception) {
                    AuditLog.error("Dependency check failed", exception); 
                    throw exception;       
                }
            }
        }
    }

    /** Constants */
    final private static String LOGNAME = "AuditLog";
    final static String LOGSOURCE = "Audit";

    /**
     * Provides the class variable for the Singleton pattern,
     * to keep track of the one and only instance of this class.
     */
    private static volatile Log singleton = newLog("AuditLog", AuditLog.class);

    private static volatile boolean initialized = false;

	/** The application properties. These properties are optional */
	private static Properties  logProperties = null;

	/** The application name. Default is the log name */
    private static String configName = LOGNAME;

	/** The log source */
    private static Object logSource = LOGSOURCE;
}

//--- End of File -----------------------------------------------------------

