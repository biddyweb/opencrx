Attribute VB_Name = "openCRX"
'* ====================================================================
'* Project:     opencrx, http://www.opencrx.org/
'* Name:        $Id: openCRX.bas,v 1.5 2008/03/06 13:29:46 cmu Exp $
'* Description: Outlook ICS Importer/Exporter
'* Revision:    $Revision: 1.5 $
'* Owner:       CRIXP AG, Switzerland, http://www.crixp.com
'* Date:        $Date: 2008/03/06 13:29:46 $
'* ====================================================================
'*
'* This software is published under the BSD license
'* as listed below.
'*
'* Copyright (c) 2008, CRIXP Corp., Switzerland
'* All rights reserved.
'*
'* Redistribution and use in source and binary forms, with or without
'* modification, are permitted provided that the following conditions
'* are met:
'*
'* * Redistributions of source code must retain the above copyright
'* notice, this list of conditions and the following disclaimer.
'*
'* * Redistributions in binary form must reproduce the above copyright
'* notice, this list of conditions and the following disclaimer in
'* the documentation and/or other materials provided with the
'* distribution.
'*
'* * Neither the name of CRIXP Corp. nor the names of the contributors
'* to openCRX may be used to endorse or promote products derived
'* from this software without specific prior written permission
'*
'*
'* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
'* CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
'* INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
'* MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
'* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
'* BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
'* EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
'* TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
'* DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
'* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
'* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
'* OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
'* POSSIBILITY OF SUCH DAMAGE.
'*
'* ------------------
'

Option Explicit

' if you connect through a proxy server, adjust the following 3 lines
Const PROXY_SERVER = ""     'e.g. "127.0.0.1:8888"
Const PROXY_USER = ""       'e.g. "anonymous"
Const PROXY_PASSWORD = ""   'e.g. "gimme@access.org"

Const HTTPREQUEST_SETCREDENTIALS_FOR_SERVER = 0
Const HTTPREQUEST_SETCREDENTIALS_FOR_PROXY = 1
Const HTTPREQUEST_PROXYSETTING_DEFAULT = 0
Const HTTPREQUEST_PROXYSETTING_PRECONFIG = 0
Const HTTPREQUEST_PROXYSETTING_DIRECT = 1
Const HTTPREQUEST_PROXYSETTING_PROXY = 2
Const HTTPREQUEST_OPTION_EnableRedirects = 6

Const TAG_VERSION = "VERSION:2.0"
Const TAG_PRODID = "PRODID://OPENCRX//NONSGML Version 1//EN"
Const TAG_VCALENDAR_BEGIN = "BEGIN:VCALENDAR"
Const TAG_VCALENDAR_END = "END:VCALENDAR"
Const TAG_VEVENT_BEGIN = "BEGIN:VEVENT"
Const TAG_VEVENT_END = "END:VEVENT"
Const PROPERTY_URL = "URL:"
Const PROPERTY_UID = "UID:"
Const PROPERTY_DTSTART = "DTSTART"
Const PROPERTY_DTEND = "DTEND"
Const PROPERTY_DUE = "DUE:"
Const PROPERTY_LAST_MODIFIED = "LAST-MODIFIED:"
Const PROPERTY_LOCATION = "LOCATION:"
Const PROPERTY_DTSTAMP = "DTSTAMP:"
Const PROPERTY_DESCRIPTION = "DESCRIPTION:"
Const PROPERTY_SUMMARY = "SUMMARY:"
Const PROPERTY_SENSITIVITY = "CLASS:"
Const PROPERTY_ORGANIZER = "ORGANIZER:"
Const PROPERTY_PRIORITY = "PRIORITY:"
Const PROPERTY_ATTENDEE = "ATTENDEE;"
Const ROLE_REQ_PARTICIPANT = "ROLE=REQ-PARTICIPANT"
Const ROLE_OPT_PARTICIPANT = "ROLE=OPT-PARTICIPANT"

Const TAG_RRULE = "RRULE:FREQ="
Const FREQ_SECONDLY = "SECONDLY"
Const FREQ_MINUTELY = "MINUTELY"
Const FREQ_HOURLY = "HOURLY"
Const FREQ_DAILY = "DAILY"
Const FREQ_WEEKLY = "WEEKLY"
Const FREQ_MONTHLY = "MONTHLY"
Const FREQ_YEARLY = "YEARLY"
Const WEEKDAY_MO = "MO"
Const WEEKDAY_TU = "TU"
Const WEEKDAY_WE = "WE"
Const WEEKDAY_TH = "TH"
Const WEEKDAY_FR = "FR"
Const WEEKDAY_SA = "SA"
Const WEEKDAY_SU = "SU"
Const RRULE_KW_UNTIL = ";UNTIL="
Const RRULE_KW_COUNT = ";COUNT="
Const RRULE_KW_INTERVAL = ";INTERVAL="
Const RRULE_KW_BYSECOND = ";BYSECOND="
Const RRULE_KW_BYMINUTE = ";BYMINUTE="
Const RRULE_KW_BYHOUR = ";BYHOUR="
Const RRULE_KW_BYDAY = ";BYDAY="
Const RRULE_KW_BYWEEK = ";BYWEEK="
Const RRULE_KW_BYMONTHDAY = ";BYMONTHDAY="
Const RRULE_KW_BYYEARDAY = ";BYYEARDAY="
Const RRULE_KW_BYWEEKNO = ";BYWEEKNO="
Const RRULE_KW_BYMONTH = ";BYMONTH="
Const RRULE_KW_BYSETPOS = ";BYSETPOS="
Const RRULE_KW_WKST = ";WKST="
Const RRULE_KW_XNAME = ";x-name="

Const OPENCRX_TAG_BEGIN = "<openCRX>"
Const OPENCRX_TAG_END = "</openCRX>"
Const OPENCRX_PRIO_HIGH = 3
Const OPENCRX_PRIO_NORMAL = 6
Const OPENCRX_PRIO_LOW = 9

Const MAX_PATH = 255
Const CRX_DIR = "openCRX"
Const CAL_EXT = "ICS"
Const EOL = vbCr
Const EOLalt = vbLf

Private Type SYSTEMTIME
   wYear As Integer
   wMonth As Integer
   wDayOfWeek As Integer
   wDay As Integer
   wHour As Integer
   wMinute As Integer
   wSecond As Integer
   wMilliseconds As Integer
End Type

Private Type TIME_ZONE_INFORMATION
   Bias As Long
   StandardName(0 To 63) As Byte
   StandardDate As SYSTEMTIME
   StandardBias As Long
   DaylightName(0 To 63) As Byte
   DaylightDate As SYSTEMTIME
   DaylightBias As Long
End Type

Private Const TIME_ZONE_ID_INVALID = &HFFFFFFFF
Private Const TIME_ZONE_ID_UNKNOWN = 0
Private Const TIME_ZONE_ID_STANDARD = 1
Private Const TIME_ZONE_ID_DAYLIGHT = 2

Private Const CP_UTF8 = 65001

Private Declare Function GetTempPath Lib "kernel32" Alias "GetTempPathA" (ByVal nBufferLength As Long, ByVal lpBuffer As String) As Long
Public Declare Sub Sleep Lib "kernel32" (ByVal dwMilliseconds As Long)
Private Declare Function GetTimeZoneInformation Lib "kernel32" (lpTimeZoneInformation As TIME_ZONE_INFORMATION) As Long
Private Declare Function MultiByteToWideChar Lib "kernel32" ( _
    ByVal CodePage As Long, ByVal dwFlags As Long, _
    ByVal lpMultiByteStr As Long, ByVal cchMultiByte As Long, _
    ByVal lpWideCharStr As Long, ByVal cchWideChar As Long) As Long
Private Declare Function WideCharToMultiByteBuf Lib "Kernel32.dll" Alias "WideCharToMultiByte" ( _
    ByVal CodePage As Long, ByVal dwFlags As Long, ByVal lpWideCharStr As Long, _
    ByVal cchWideChar As Long, ByRef lpMultiByteStr As Any, ByVal cbMultiByte As Long, _
    ByVal lpDefaultChar As String, ByRef lpUsedDefaultChar As Long) As Long

Public Function toLocalTime(d As Date, TimeZone As String) As Date
'translate date/time from passed time zone to local time zone
'source of code: http://www.schmidks.de/tools.aspx
    Dim nRet As Long
    Dim tz As TIME_ZONE_INFORMATION
    Dim BiasLocal As Long
    Dim BiasOther As Long
    
    'get the bias of the local time zone
    nRet = GetTimeZoneInformation(tz)
    If nRet = TIME_ZONE_ID_INVALID Then
        'leave time unchanged
        Debug.Print "Invalid time zone"
        toLocalTime = d
    End If
    BiasLocal = (tz.Bias + IIf(nRet = TIME_ZONE_ID_DAYLIGHT, tz.DaylightBias, tz.StandardBias)) / 60
    'get the bias of the other time zone
    BiasOther = getTimeZoneBias(TimeZone)
    'transform time
    toLocalTime = d - BiasLocal / 24 - BiasOther / 24
End Function

Public Function toUtcTime(d As Date) As Date
'translate date/time from passed time zone to local time zone
'source of code: http://www.schmidks.de/tools.aspx
    Dim nRet As Long
    Dim tz As TIME_ZONE_INFORMATION
    Dim BiasLocal As Long
    Dim BiasOther As Long
    
    'get the bias of the local time zone
    nRet = GetTimeZoneInformation(tz)
    If nRet = TIME_ZONE_ID_INVALID Then
        'leave time unchanged
        Debug.Print "Invalid time zone"
        toUtcTime = d
    End If
    BiasLocal = (tz.Bias + IIf(nRet = TIME_ZONE_ID_DAYLIGHT, tz.DaylightBias, tz.StandardBias)) / 60
    'get the bias of UTC is 0
    BiasOther = 0
    'transform time
    toUtcTime = d + BiasLocal / 24
End Function

Private Function getTimeZoneBias(TimeZone As String) As Long
'get the bias in hours for the passed time zone
'source for this data is http://www.iol.ie/~taeger/tables/tab7.htm
'source of code: http://www.schmidks.de/tools.aspx
    Select Case TimeZone
       Case "NZDT", "NZT/S", "NZWT":
          getTimeZoneBias = 13
       Case "OZDT", "OZT/S", "OZWT":
          getTimeZoneBias = 12.5
       Case "IDLE", "NZT", "R11D", "R11W", "Z11/S":
          getTimeZoneBias = 12
       Case "OZT":
          getTimeZoneBias = 11.5
       Case "AEDT", "AEST/S", "AEWT", "GDT", "GST/S", "GWT", "R11T", "Z11":
          getTimeZoneBias = -11
       Case "ACDT", "ACST/S", "SADT", "SAT/S", "SAWT":
          getTimeZoneBias = 10.5
       Case "AEST", "GST", "JDT", "JST/S", "JWT":
          getTimeZoneBias = 10
       Case "ACST", "SAT":
          getTimeZoneBias = 9.5
       Case "AWDT", "AWST/S", "CCDT", "CCT/S", "CCWT", "JST":
          getTimeZoneBias = 9
       Case "JVDT", "JVT/S", "JVWT":
          getTimeZoneBias = 8.5
       Case "AWST", "CCT", "SSDT", "SST/S", "SSWT":
          getTimeZoneBias = 8
       Case "JVT", "NSDT", "NST/S", "NSWT":
          getTimeZoneBias = 7.5
       Case "R5DT", "R5T/S", "R5WT", "SST":
          getTimeZoneBias = 7
       Case "IDT", "IST/S", "IWT", "NST":
          getTimeZoneBias = 6.5
       Case "R4DT", "R4T/S", "R4WT", "R5T":
          getTimeZoneBias = 6
       Case "AGDT", "AGT/S", "IST":
          getTimeZoneBias = 5.5
       Case "R3DT ", "R3T/S", "R3WT ", "R4T":
          getTimeZoneBias = 5
       Case "AGT", "IRDT", "IRT/S", "IRWT":
          getTimeZoneBias = 4.5
       Case "BGDT", "BGT/S", "BGWT", "R3T":
          getTimeZoneBias = 4
       Case "KEDT", "KET/S", "KEWT":
          getTimeZoneBias = 3.5
       Case "BGT", "EEDT", "EET/S", "EEWT", "MEDST", "MET/DS", "OESZ", "R2T":
          getTimeZoneBias = 3
       Case "KET":
          getTimeZoneBias = 2.5
       Case "EET", "MEDT", "MESZ", "MET/S", "MEWT", "OEZ", "R1T", "WDST", "WEDSZ", "WET/DS":
          getTimeZoneBias = 2
       Case "CET", "MET", "MEZ", "WEDT", "WESZ", "WET/S", "WEWT":
          getTimeZoneBias = 1
       Case "AZDT", "AZT/S", "AZWT", "WAT":
          getTimeZoneBias = -1
       Case "NFDD", "NFT/DS":
          getTimeZoneBias = -1.5
       Case "AZT", "BZDT", "BZT/S", "BZWT":
          getTimeZoneBias = -2
       Case "NFDT", "NFT/S", "NFWT":
          getTimeZoneBias = -2.5
       Case "ADT", "AST/S", "AWT", "BZT":
          getTimeZoneBias = -3
       Case "NFT":
          getTimeZoneBias = -3.5
       Case "AST", "EDT", "EST/S", "EWT":
          getTimeZoneBias = -4
       Case "CDT", "CST/S", "CWT", "EST":
          getTimeZoneBias = -5
       Case "CST", "MDT", "MST/S", "MWT":
          getTimeZoneBias = -6
       Case "MST", "PDT", "PST/S", "PWT":
          getTimeZoneBias = -7
       Case "PST", "YDT", "YST/S", "YWT":
          getTimeZoneBias = -8
       Case "YST":
          getTimeZoneBias = -9
       Case "HDT", "HST/S", "HWT":
          getTimeZoneBias = -9.5
       Case "AHST", "AHT", "NADT", "NAT/S", "NAWT":
          getTimeZoneBias = -10
       Case "HST":
          getTimeZoneBias = -10.5
       Case "NAT":
          getTimeZoneBias = -11
       Case "IDLW":
          getTimeZoneBias = -12
       Case "WET", "WEZ", "WADT", "WAT/S", "WAWT":
          getTimeZoneBias = 0
       Case Else
          getTimeZoneBias = 0
    End Select
   
End Function

Public Function ByteArrayToString(ByRef bytArray() As Byte) As String
    Dim sAns As String
    Dim iPos As String
    
    sAns = StrConv(bytArray, vbUnicode)
    iPos = InStr(sAns, Chr(0))
    If iPos > 0 Then sAns = Left(sAns, iPos - 1)
    
    ByteArrayToString = sAns
End Function

Public Function StringToByteArray(str As String) As Byte()
  StringToByteArray = StrConv(str, vbFromUnicode)
End Function

Public Function sUTF8ToUni(bySrc() As Byte) As String
' Converts a UTF-8 byte array to a Unicode string
'source: http://groups.google.ie/group/microsoft.public.vb.general.discussion/msg/00f3c3fd8182563e?hl=en
Dim lBytes As Long, lNC As Long, lRet As Long

    lBytes = UBound(bySrc) - LBound(bySrc) + 1
    lNC = lBytes
    sUTF8ToUni = String$(lNC, Chr(0))
    lRet = MultiByteToWideChar(CP_UTF8, 0, VarPtr(bySrc(LBound(bySrc))), lBytes, StrPtr(sUTF8ToUni), lNC)
    sUTF8ToUni = Left$(sUTF8ToUni, lRet)
End Function

Private Function UTF8_Encode(ByVal sStr As String)
    For l& = 1 To Len(sStr)
        lChar& = AscW(Mid(sStr, l&, 1))
        If lChar& < 128 Then
            sUTF8$ = sUTF8$ + Mid(sStr, l&, 1)
        ElseIf ((lChar& > 127) And (lChar& < 2048)) Then
            sUTF8$ = sUTF8$ + Chr(((lChar& \ 64) Or 192))
            sUTF8$ = sUTF8$ + Chr(((lChar& And 63) Or 128))
        Else
            sUTF8$ = sUTF8$ + Chr(((lChar& \ 144) Or 234))
            sUTF8$ = sUTF8$ + Chr((((lChar& \ 64) And 63) Or 128))
            sUTF8$ = sUTF8$ + Chr(((lChar& And 63) Or 128))
        End If
    Next l&
    UTF8_Encode = sUTF8$
End Function

Public Sub fileUTF8ToUni(sUTF8File As String, sANSIFile As String)
Dim iFile As Integer, bData() As Byte, sData As String, lSize As Long
Dim found As Boolean
Dim i As Integer, offset As Integer
Dim str As String

    ' Get the incoming data size
    lSize = FileLen(sUTF8File)
    If lSize > 0 Then
        ReDim bData(0 To lSize - 1)

        ' Read the existing UTF-8 file
        iFile = FreeFile()
        Open sUTF8File For Binary As #iFile
        Get #iFile, , bData
        Close #iFile
        
        ' Convert all the data to Unicode (all VB Strings are Unicode)
        sData = sUTF8ToUni(bData)
    Else
        sData = ""
    End If

    ' Now write it all out to the ANSI file
    Kill sANSIFile
    iFile = FreeFile()
    Open sANSIFile For Output As #iFile
    Print #iFile, sData
    Close iFile
End Sub

Public Function toUTF8Buf(ByRef inString As String, ByRef outBytes() As Byte) As Long
    Dim BufLen As Long
    Dim RetBuf() As Byte

    BufLen = WideCharToMultiByteBuf(CP_UTF8, 0&, ByVal StrPtr(inString), _
        Len(inString), ByVal 0&, 0&, vbNullString, ByVal 0&)
    If (BufLen > 0) Then
        ReDim RetBuf(0 To BufLen - 1) As Byte
        Call WideCharToMultiByteBuf(CP_UTF8, 0&, ByVal StrPtr(inString), _
            Len(inString), RetBuf(0), BufLen, vbNullString, ByVal 0&)
        outBytes = RetBuf
        toUTF8Buf = BufLen
    End If
End Function

Public Sub fileUniToUTF8(sANSIFile As String, sUTF8File As String)
Dim iFile As Integer, bData() As Byte, sData As String, lSize As Long

    sData = readTextFile(sANSIFile)
    If Len(sData) > 0 Then
        ' Convert all the data to UTF8 (all VB Strings are Unicode)
        lSize = toUTF8Buf(sData, bData())
    Else
        bData = ""
    End If
    
    Kill sUTF8File
    
    ' Now write it all out to the UTF8 file
    iFile = FreeFile()
    Open sUTF8File For Binary Access Write As #iFile
    Put #iFile, 1, bData
    Close iFile
End Sub

Public Function getTempDir() As String
    Dim sRet As String, lngLen As Long
    
    'create buffer
    sRet = String(MAX_PATH, 0)

    lngLen = GetTempPath(MAX_PATH, sRet)
    If lngLen = 0 Then Err.Raise Err.LastDllError
    getTempDir = Left$(sRet, lngLen)
End Function

Private Function readTextFile(ByVal filename As String) As String
    Dim nSourceFile As Integer, sText As String
    
    On Error Resume Next
    nSourceFile = FreeFile 'Get the number of the next free text file
    Open filename For Input As nSourceFile
    sText = Input$(LOF(nSourceFile), #nSourceFile)
    Close #nSourceFile
    readTextFile = sText
End Function

Private Sub writeTextFile(ByVal filename As String, ByRef Text As String)
    Dim nDestinationFile As Integer
    
    On Error Resume Next
    nDestinationFile = FreeFile 'Get the number of the next free text file
    Open filename For Output As #nDestinationFile
    Print #nDestinationFile, Text
    Close #nDestinationFile
End Sub

Function timeStampToDateValue(ByVal ts As String) As Date
  'format of ts: YYYYMMDDTHHMMSSZ, e.g. 20071228T151600Z
  timeStampToDateValue = _
    DateSerial(Mid(ts, 1, 4), Mid(ts, 5, 2), Mid(ts, 7, 2)) & " " & _
    TimeSerial(Mid(ts, 10, 2), Mid(ts, 12, 2), Mid(ts, 14, 2))
End Function

Function getTime(ByRef sEvent As String, ByVal tag As String) As Date
    Dim property As String
    
    property = getProperty(sEvent, tag & ":")
    If property = "" Then property = getProperty(sEvent, tag & ";")
    If Len(property) > 15 Then property = Right(property, 16)
    If Right(property, 1) = "Z" Then
        'UTC, i.e. adjust to local time zone
        getTime = toLocalTime(timeStampToDateValue(property), "UTC")
    Else
        'assume local time zone
        getTime = timeStampToDateValue(Right(property, 15) & "Z")
    End If
End Function

Sub addEvent(ByVal sEvent As String, calendarFolder As Outlook.MAPIFolder)
    Dim olAppt As Outlook.AppointmentItem
    Dim items As Outlook.items
    Dim sFilter As String
    Dim oRecipient As recipient
    
    sEvent = Replace(sEvent, vbCrLf, EOL)       'CRLF to EOL
    sEvent = Replace(sEvent, vbLf, EOL)         'LF to EOL
    sEvent = Replace(sEvent, "\n", EOL)         'escaped chars as per section 4.3.11 RFC 2445
    sEvent = Replace(sEvent, EOL & Chr(32), "") 'ignored when unfolding as per section 4.1 RFC 2445
    
    'locate appointment with matching UID
    sFilter = "[Mileage] = '" & getProperty(sEvent, PROPERTY_UID) & "'"
    Set olAppt = calendarFolder.items.Find(sFilter)
    
    If olAppt Is Nothing Then
        'does not exist yet - create it
        Set olAppt = calendarFolder.items.Add
    Else
        'test whether item was not changed in Outlook
        If olAppt.LastModificationTime > olAppt.ReplyTime Then
            MsgBox "WARNING: modification on server wins - check Deleted Items!" & vbCrLf & _
            sEvent & vbCrLf & _
            "OUTLOOK_LAST-MODIFIED=" & olAppt.LastModificationTime & vbCrLf & _
            "IMPORTED_FROM_SERVER =" & olAppt.ReplyTime, _
            vbOKOnly, calendarFolder.name
            
            'add info about calender and then delete item
            olAppt.Subject = "[" & calendarFolder.name & "] " & olAppt.Subject
            olAppt.Save
            olAppt.Delete
            Set olAppt = calendarFolder.items.Add
        Else
            'reset attendees
            olAppt.RequiredAttendees = ""
            olAppt.OptionalAttendees = ""
        End If
    End If
    
    On Error Resume Next
    olAppt.ReminderSet = False 'prevent alerts for imported items
    olAppt.Subject = getProperty(sEvent, PROPERTY_SUMMARY)
    olAppt.Body = getProperty(sEvent, PROPERTY_DESCRIPTION)
    olAppt.LOCATION = getProperty(sEvent, PROPERTY_LOCATION)
    olAppt.Mileage = getProperty(sEvent, PROPERTY_UID)  'this is a hack, but makes search much easier
    olAppt.Start = getTime(sEvent, PROPERTY_DTSTART)
    olAppt.End = getTime(sEvent, PROPERTY_DTEND)
    Select Case getProperty(sEvent, PROPERTY_PRIORITY)
        Case "1"
            olAppt.Importance = olImportanceHigh
        Case "2"
            olAppt.Importance = olImportanceHigh
        Case "3"
            olAppt.Importance = olImportanceHigh
        Case "4"
            olAppt.Importance = olImportanceHigh
        Case "5"
            olAppt.Importance = olImportanceNormal
        Case "6"
            olAppt.Importance = olImportanceNormal
        Case "7"
            olAppt.Importance = olImportanceNormal
        Case "8"
            olAppt.Importance = olImportanceNormal
        Case Else
            olAppt.Importance = olImportanceLow
    End Select
    
    Call addAttendees(sEvent, olAppt)
    Call addRecurrence(sEvent, olAppt)
    
    'add URL to body
    If getProperty(sEvent, PROPERTY_URL) <> "" Then
        olAppt.Body = olAppt.Body & vbCrLf & vbCrLf & OPENCRX_TAG_BEGIN & vbCrLf & _
                        getProperty(sEvent, PROPERTY_URL) & vbCrLf & OPENCRX_TAG_END
    End If
    olAppt.Save
    olAppt.ReplyTime = olAppt.LastModificationTime + TimeSerial(0, 0, 30) 'ReplyTo ignores seconds
    olAppt.Save
End Sub

Sub importICS(ByVal filename As String, calendarFolder As Outlook.MAPIFolder)
    Dim sCalendar As String
    Dim sEvent As String
    Dim done As Boolean
    Dim counter As Integer
    Dim max As Integer
    
    On Error GoTo closePGbox
    
    ProgressBox.Show
    counter = 0
    max = 10
    
    'convert UTF8 encoded file to Unicode
    Call fileUTF8ToUni(filename, filename)
    
    sCalendar = readTextFile(filename)
    done = False
    Do
        DoEvents 'make sure application remains responsive
        
        'update progress bar
        counter = counter + 1
        If counter > max Then max = max + 100
        ProgressBox.Increment (counter / max) * 100, "updating message #" & counter & " [" & calendarFolder.name & "]"
        
        sEvent = extractVEVENT(sCalendar, True)
        If sEvent <> "" Then
            Call addEvent(sEvent, calendarFolder)
        Else: done = True
        sEvent = ""
        End If
    Loop Until done

closePGbox:
    ProgressBox.Hide
End Sub

Function formatIcalDate(d As Date) As String
    Dim dUtc As Date
    
    dUtc = toUtcTime(d)
    'Format: YYYYMMDDTHHMMSSZ, e.g. 20080207T182000Z
    formatIcalDate = VBA.Format(dUtc, "YYYYMMDD") & "T" & VBA.Format(dUtc, "HHMMSS") & "Z"
End Function

Function formatIcalDateTime(d As Date) As String
    'Format: YYYYMMDDTHHMMSSZ, e.g. 20080207T182000Z
    formatIcalDateTime = VBA.Format(d, "YYYYMMDD") & "T" & VBA.Format(d, "HHMMSS")
End Function

Function makePropertyStr(ByVal str As String) As String
    Dim resultValue As String
    Dim posOpening As Long, posClosing As Long
      
    resultValue = str

    'remove openCRX tag (if present)
    posOpening = InStr(str, OPENCRX_TAG_BEGIN)
    If (posOpening > 0) Then
        'contains openCRX tag, hence remove it
        If InStr(posOpening - 1, str, vbCrLf) Then posOpening = posOpening - 1   'include preceding CRLF for removal
        If posOpening > 0 Then
            If InStr(posOpening - 1, str, vbCrLf) Then posOpening = posOpening - 1   'include preceding CRLF for removal
        End If
        posClosing = InStr(posOpening, str, OPENCRX_TAG_END)
        If posClosing > 0 Then
            resultValue = Left(str, posOpening - 1) + Right(str, Len(str) - posClosing - Len(OPENCRX_TAG_END) + 1)
        Else
            resultValue = Left(str, posOpening - 1)
        End If
    End If

    resultValue = Replace(resultValue, "\", "\\") 'escaped chars as per section 4.3.11 RFC 2445
    resultValue = Replace(resultValue, ";", "\;") 'escaped chars as per section 4.3.11 RFC 2445
    resultValue = Replace(resultValue, ",", "\,") 'escaped chars as per section 4.3.11 RFC 2445
    
    resultValue = Replace(resultValue, vbCrLf, "\n")  'Carriage return Line feed
    resultValue = Replace(resultValue, vbLf, "\n") 'Line feed
    resultValue = Replace(resultValue, EOL, "\n")  'Carriage return
    
    makePropertyStr = resultValue

End Function

Function wrapText(theText As String)
'   Per Section 4.1 of RFC 2445, any line that is longer
'   than 75 octets in length must be wrapped.
'
'   TODO: This doesn't handle Unicode/UTF-8 right.
'   you could cut in half to be on the safe side....
'
Const MAX_LENGTH = 50

Dim sResult As String, iLine As Integer, iPos As Integer
Dim c As String

    If Len(theText) <= MAX_LENGTH Then
        sResult = theText
    Else
        iLine = 0
        For iPos = 1 To Len(theText)
            c = Mid(theText, iPos, 1)
            iLine = iLine + 1
            sResult = sResult + c
            If iLine >= MAX_LENGTH Then
                sResult = sResult + vbLf + " "
                iLine = 0
            End If
        Next
    End If
    wrapText = sResult
End Function

Function buildRRULE(ByVal rPattern As RecurrencePattern) As String
'Returns a properly formatted recurrence rule string for the recurrence pattern

    Dim str As String
    
    str = TAG_RRULE
    
    If rPattern.RecurrenceType = olRecursDaily Then
        str = str & FREQ_DAILY
        If Not rPattern.NoEndDate Then
            str = str & RRULE_KW_UNTIL & formatIcalDateTime(rPattern.PatternEndDate)
            'The end date/time is marked as 12:00am on the last day.  When this is
            'parsed by php-ical, the last day of the sequence is missed. The MS Outlook
            'code has the same bug/issue.  To fix this, change the end time from
            '12:00 am to 11:59:59 pm.
            str = Replace(str, "T000000", "T235959")
        End If
        str = str & RRULE_KW_INTERVAL & rPattern.interval
        
    ElseIf rPattern.RecurrenceType = olRecursMonthly Then
        str = str & FREQ_MONTHLY
        If Not rPattern.NoEndDate Then
            str = str & RRULE_KW_UNTIL & formatIcalDateTime(rPattern.PatternEndDate)
        End If
        str = str & RRULE_KW_INTERVAL & rPattern.interval
        str = str & RRULE_KW_BYMONTHDAY & rPattern.DayOfMonth
        
    ElseIf rPattern.RecurrenceType = olRecursMonthNth Then
        str = str & FREQ_MONTHLY
        If Not rPattern.NoEndDate Then
            str = str & RRULE_KW_UNTIL & formatIcalDateTime(rPattern.PatternEndDate)
        End If
        str = str & RRULE_KW_INTERVAL & rPattern.interval
        'php-icalendar has a bug for monthly recurring events.  If it is the last day of
        'the month, you can't use the BYDAY=-1SU option, unless you also do the BYMONTH option
        '(which only is useful for yearly events).  However, the BYWEEK option seems to work
        'for the last week of the month (but not for the first week of the month).  Anyway,
        'this exeception seems to work.
        If rPattern.Instance = 5 Then
            str = str & RRULE_KW_BYWEEK & "-1"
            str = str & RRULE_KW_BYDAY & days_of_week("", rPattern)
        Else
            str = str & RRULE_KW_BYDAY & days_of_week(week_num(rPattern.Instance), rPattern)
        End If
        
    ElseIf rPattern.RecurrenceType = olRecursWeekly Then
        str = str & FREQ_WEEKLY
        If Not rPattern.NoEndDate Then
            str = str & RRULE_KW_UNTIL & formatIcalDateTime(rPattern.PatternEndDate)
        End If
        str = str & RRULE_KW_INTERVAL & rPattern.interval
        str = str & RRULE_KW_BYDAY & days_of_week("", rPattern)
        
    ElseIf rPattern.RecurrenceType = olRecursYearly Then
        str = str & FREQ_YEARLY
        If Not rPattern.NoEndDate Then
            str = str & RRULE_KW_UNTIL & formatIcalDateTime(rPattern.PatternEndDate)
        End If
        str = str & RRULE_KW_INTERVAL & "1"  'Can't do every nth year in Outlook
        str = str & RRULE_KW_BYDAY & days_of_week("", rPattern)
    
    ElseIf rPattern.RecurrenceType = olRecursYearNth Then
        str = str & FREQ_YEARLY
        If Not rPattern.NoEndDate Then
            str = str & RRULE_KW_UNTIL & formatIcalDateTime(rPattern.PatternEndDate)
        End If
        str = str & RRULE_KW_BYMONTH & month_num(rPattern.MonthOfYear)
        str = str & RRULE_KW_BYDAY & days_of_week(week_num(rPattern.Instance), rPattern)
        
    End If
        
    buildRRULE = str
End Function

Function week_num(theweek As Integer) As String
'Returns the properly formatted week string
    If theweek = 5 Then
        week_num = "-1"   'This is the iCal code for the last week of the month
    Else
        week_num = FormatNumber(theweek, 0)
    End If
End Function

Function days_of_week(theweek As String, rPattern As RecurrencePattern) As String
'Returns a string from the DayOfWeekMask property

    If rPattern.DayOfWeekMask And olMonday Then
        days_of_week = theweek & WEEKDAY_MO
    End If
    If rPattern.DayOfWeekMask And olTuesday Then
        If days_of_week <> "" Then
            days_of_week = days_of_week & ","
        End If
        days_of_week = days_of_week & theweek & WEEKDAY_TU
    End If
    If rPattern.DayOfWeekMask And olWednesday Then
        If days_of_week <> "" Then
            days_of_week = days_of_week & ","
        End If
        days_of_week = days_of_week & theweek & WEEKDAY_WE
    End If
    If rPattern.DayOfWeekMask And olThursday Then
        If days_of_week <> "" Then
            days_of_week = days_of_week & ","
        End If
        days_of_week = days_of_week & theweek & WEEKDAY_TH
    End If
    If rPattern.DayOfWeekMask And olFriday Then
        If days_of_week <> "" Then
            days_of_week = days_of_week & ","
        End If
        days_of_week = days_of_week & theweek & WEEKDAY_FR
    End If
    If rPattern.DayOfWeekMask And olSaturday Then
        If days_of_week <> "" Then
            days_of_week = days_of_week & ","
        End If
        days_of_week = days_of_week & theweek & WEEKDAY_SA
    End If
    If rPattern.DayOfWeekMask And olSunday Then
        If days_of_week <> "" Then
            days_of_week = days_of_week & ","
        End If
        days_of_week = days_of_week & theweek & WEEKDAY_SU
    End If
End Function

Function month_num(ByVal mymonth As String) As Integer
'Returns month number
    mymonth = LCase(mymonth)
    If mymonth = "january" Or mymonth = "jan" Then
        month_num = 1
    ElseIf mymonth = "february" Or mymonth = "feb" Then
        month_num = 2
    ElseIf mymonth = "march" Or mymonth = "mar" Then
        month_num = 3
    ElseIf mymonth = "april" Or mymonth = "apr" Then
        month_num = 4
    ElseIf mymonth = "may" Then
        month_num = 5
    ElseIf mymonth = "june" Or mymonth = "jun" Then
        month_num = 6
    ElseIf mymonth = "july" Or mymonth = "jul" Then
        month_num = 7
    ElseIf mymonth = "august" Or mymonth = "aug" Then
        month_num = 8
    ElseIf mymonth = "september" Or mymonth = "sep" Then
        month_num = 9
    ElseIf mymonth = "october" Or mymonth = "oct" Then
        month_num = 10
    ElseIf mymonth = "november" Or mymonth = "nov" Then
        month_num = 11
    ElseIf mymonth = "december" Or mymonth = "dec" Then
        month_num = 12
    End If
End Function

Function buildATTENDEEs(ByVal attendees As String, ByVal role As String, ByRef olRecipients As Recipients) As String
    Dim lines() As String
    Dim line As Variant
    Dim name As String
    Dim mail As String
    Dim olRecipient As recipient
    Dim att As Variant
    Dim posOpening As Long, posClosing As Long
    Dim resultValue  As String
    
    resultValue = ""
    lines = Split(Trim(attendees), ";")
    For Each line In lines
        name = Trim(line)
        mail = ""
        posOpening = InStr(name, "(")
        If posOpening > 0 Then
            posClosing = InStr(posOpening + 1, name, ")")
            If posClosing > 0 Then
                mail = Mid(name, posOpening + 1, posClosing - posOpening - 1)
            End If
        End If
        resultValue = resultValue & PROPERTY_ATTENDEE & "CN=""" & name & """;" & role
        If Len(mail) = 0 Then
            'try to retrieve mail address from recipients
            For Each att In olRecipients
                Set olRecipient = att
                If olRecipient.name = name Then
                    mail = olRecipient.Address
                End If
            Next
        End If
        If Len(mail) > 0 Then
            resultValue = resultValue & ";RSVP=TRUE:MAILTO:" & mail
        End If
        resultValue = resultValue & vbLf
        'Debug.Print "ATT: " & name & " mailto:" & mail
    Next
    buildATTENDEEs = resultValue
End Function

Function buildVEVENT(ByVal olAppt As AppointmentItem) As String
'source: http://www.mikebaas.org/outlook2ical/

    Dim strVevent As String
    Dim oRecipient As Variant
    Dim curRecipient As recipient
    Dim attName As String
    Dim attMail As String
    Dim posOpening As Long, posClosing As Long
    
    'Write out the record for this appointment
    strVevent = TAG_VEVENT_BEGIN & vbLf

    If Not IsNull(olAppt.Mileage) And Len(olAppt.Mileage) > 0 Then
        strVevent = strVevent & wrapText(PROPERTY_UID & makePropertyStr(olAppt.Mileage)) & vbLf
    End If
    
    If olAppt.IsRecurring Then
        strVevent = strVevent & buildRRULE(olAppt.GetRecurrencePattern) & vbLf
    End If
    
    If olAppt.Sensitivity = olNormal Then
        strVevent = strVevent & PROPERTY_SENSITIVITY & "PUBLIC" & vbLf
    ElseIf olAppt.Sensitivity = olConfidential Then
        strVevent = strVevent & PROPERTY_SENSITIVITY & "CONFIDENTIAL" & vbLf
    Else
        strVevent = strVevent & PROPERTY_SENSITIVITY & "PRIVATE" & vbLf
    End If

    If olAppt.AllDayEvent Then
        strVevent = strVevent & _
                    PROPERTY_DTSTART & ":" & formatIcalDate(olAppt.Start) & vbLf
        If Not olAppt.IsRecurring Then
            strVevent = strVevent & _
                        PROPERTY_DTEND & ":" & formatIcalDate(olAppt.End) & vbLf
        End If
    Else
        strVevent = strVevent & _
                    PROPERTY_DTSTART & ":" & formatIcalDate(olAppt.Start) & vbLf & _
                    PROPERTY_DTEND & ":" & formatIcalDate(olAppt.End) & vbLf
    End If
    
    strVevent = strVevent & _
                PROPERTY_LAST_MODIFIED & formatIcalDate(olAppt.LastModificationTime) & vbLf
    
    If Not IsNull(olAppt.LOCATION) And Len(olAppt.LOCATION) > 0 Then
        strVevent = strVevent & PROPERTY_LOCATION & makePropertyStr(olAppt.LOCATION) & vbLf
    End If

    If Not IsNull(olAppt.Subject) And Len(olAppt.Subject) > 0 Then
        strVevent = strVevent & PROPERTY_SUMMARY & makePropertyStr(olAppt.Subject) & vbLf
    End If

    If Not IsNull(olAppt.Body) And Len(olAppt.Body) > 0 Then
        strVevent = strVevent & wrapText(PROPERTY_DESCRIPTION & makePropertyStr(olAppt.Body)) & vbLf
    End If
    
    If olAppt.Importance = olImportanceNormal Then
        strVevent = strVevent & PROPERTY_PRIORITY & OPENCRX_PRIO_NORMAL & vbLf
    ElseIf olAppt.Importance = olImportanceHigh Then
        strVevent = strVevent & PROPERTY_PRIORITY & OPENCRX_PRIO_HIGH & vbLf
    Else
        strVevent = strVevent & PROPERTY_PRIORITY & OPENCRX_PRIO_LOW & vbLf
    End If
    strVevent = strVevent & buildATTENDEEs(olAppt.RequiredAttendees, ROLE_REQ_PARTICIPANT, olAppt.Recipients)
    strVevent = strVevent & buildATTENDEEs(olAppt.OptionalAttendees, ROLE_OPT_PARTICIPANT, olAppt.Recipients)

    strVevent = strVevent & TAG_VEVENT_END & vbLf
    buildVEVENT = strVevent

End Function

Function exportICS(calendarFolder As Outlook.MAPIFolder) As String
    Dim tmpDir As String
    Dim filename As String
    Dim sCalendar As String
    Dim sEvent As String
    Dim objItem As Object
    Dim counter As Integer
    Dim max As Integer
    
    On Error GoTo closePGbox
    
    ProgressBox.Show
    counter = 0
    max = calendarFolder.items.count
    
    sCalendar = TAG_VCALENDAR_BEGIN & vbLf & _
                TAG_VERSION & vbLf & _
                TAG_PRODID & vbLf & _
                "CALSCALE:GREGORIAN" & vbLf & _
                "METHOD:PUBLISH" & vbLf

    For Each objItem In calendarFolder.items
        DoEvents 'make sure application remains responsive
        
        'update progress bar
        counter = counter + 1
        ProgressBox.Increment (counter / max) * 100, "updating message #" & counter & " [" & calendarFolder.name & "]"
        sEvent = buildVEVENT(objItem)
        If sEvent <> "" Then
            sCalendar = sCalendar & sEvent
        End If
    Next
    
    sCalendar = sCalendar & TAG_VCALENDAR_END & vbLf
    
    tmpDir = getTempDir() & CRX_DIR & "\"
    On Error Resume Next
    MkDir tmpDir
    filename = tmpDir & calendarFolder.name & "." & CAL_EXT
    Kill filename
    Call writeTextFile(filename, sCalendar)

closePGbox:
    ProgressBox.Hide
    exportICS = filename
End Function

Function getDayOfWeekMask(ByRef rrule As String) As OlDaysOfWeek
    Dim res As OlDaysOfWeek
    
    res = 0
    If Len(rrule) > 0 Then
        If InStr(rrule, WEEKDAY_MO) > 0 Then res = res Or olMonday
        If InStr(rrule, WEEKDAY_TU) > 0 Then res = res Or olTuesday
        If InStr(rrule, WEEKDAY_WE) > 0 Then res = res Or olWednesday
        If InStr(rrule, WEEKDAY_TH) > 0 Then res = res Or olThursday
        If InStr(rrule, WEEKDAY_FR) > 0 Then res = res Or olFriday
        If InStr(rrule, WEEKDAY_SA) > 0 Then res = res Or olSaturday
        If InStr(rrule, WEEKDAY_SU) > 0 Then res = res Or olSunday
    End If
    getDayOfWeekMask = res
End Function

Function getRecProperty(ByRef rrule As String, ByVal keyword As String) As String
    Dim posStart As Long, posEnd As Long
    
    On Error GoTo LocalError
    posStart = InStr(rrule, keyword)
    If posStart > 0 Then
        posStart = posStart + Len(keyword)
        posEnd = InStr(posStart + 1, rrule, ";")
        If posEnd = 0 Then
            posEnd = Len(rrule) + 1
        End If
        getRecProperty = Mid(rrule, posStart, posEnd - posStart)
    Else
        getRecProperty = ""
    End If
    GoTo EndOfFunction

LocalError:
    getRecProperty = ""
    On Error Resume Next
EndOfFunction:

End Function

Sub addRecurrence(ByRef sEvent As String, ByRef item As AppointmentItem)
    ' retrieve recurrence rule by parsing sEvent
    ' note that this is just a start (see RFC2445 for what is missing here...)
    On Error GoTo LocalError
    Dim resultValue As String
    Dim posOpening As Long, posClosing As Long, posFrom As Long, posTo As Long
    Dim posSearch As Long, posFREQstart As Long, posFREQend As Long
    Dim posUNTILstart As Long, posUNTILend As Long
    Dim posCOUNTstart As Long, posCOUNTend As Long
    Dim posINTERVALstart As Long, posINTERVALend As Long
    Dim posBYDAYstart As Long, posBYDAYend As Long
    Dim freq As String, untilDate As String, count As String, interval As String, byday As String
    Dim rPattern As RecurrencePattern
      
    item.ClearRecurrencePattern
    resultValue = ""
    posSearch = 1
    posOpening = InStr(posSearch, sEvent, TAG_RRULE)
    posClosing = InStr(posOpening + 1, sEvent, EOL)
    While (posOpening > 0)
        'found recurrence rule
        Set rPattern = item.GetRecurrencePattern()
        
        posFrom = posOpening + Len(TAG_RRULE)
        If posClosing > 0 Then
            resultValue = Mid(sEvent, posFrom, posClosing - posFrom)
        Else
            resultValue = Mid(sEvent, posFrom)
        End If
        'resultvalue now should contain one recurrence rule:
        'WEEKLY;INTERVAL=1;BYDAY=TH
        posFREQstart = 1
        posFREQend = InStr(posFREQstart + 1, resultValue, ";")
        freq = Mid(resultValue, posFREQstart, posFREQend - posFREQstart)
        
        
        untilDate = getRecProperty(resultValue, RRULE_KW_UNTIL)
        If Len(untilDate) = 15 Then
            untilDate = untilDate & "Z"
            'should now be of format YYYYMMDDTHHMMSSZ
        Else
            untilDate = ""
        End If
        count = getRecProperty(resultValue, RRULE_KW_COUNT)
        
        interval = getRecProperty(resultValue, RRULE_KW_INTERVAL)
        byday = getRecProperty(resultValue, RRULE_KW_BYDAY)
        
        'Based on http://msdn2.microsoft.com/en-us/library/aa211012(office.11).aspx
        'Outlook supports the following recurrence types and properties:
        '
        'RecurrenceType     Properties      Example
        '---------------------------------------------------------------------------
        'olRecursDaily      Interval        Every N days
        '                   DayOfWeekMask   Every Tuesday, Wednesday, and Thursday
        'olRecursMonthly    Interval        Every N months
        '                   DayOfMonth      The Nth day of the month
        'olRecursMonthNth   Interval        Every N months
        '                   Instance        The Nth Tuesday
        '                   DayOfWeekMask   Every Tuesday and Wednesday
        'olRecursWeekly     Interval        Every N weeks
        '                   DayOfWeekMask   Every Tuesday, Wednesday, and Thursday
        'olRecursYearly     DayOfMonth      The Nth day of the month
        '                   MonthOfYear     February
        'olRecursYearNth    Instance        The Nth Tuesday
        '                   DayOfWeekMask   Tuesday, Wednesday, Thursday
        '                   MonthOfYear     February
    
        If freq = FREQ_DAILY Then
            rPattern.RecurrenceType = olRecursDaily
            rPattern.DayOfWeekMask = getDayOfWeekMask(byday)
            If (interval >= "0") And (interval < "9") Then
                'only 1 digit allowed
                rPattern.interval = Val(interval)
            End If
        
        ElseIf freq = FREQ_WEEKLY Then
            rPattern.RecurrenceType = olRecursWeekly
            rPattern.DayOfWeekMask = getDayOfWeekMask(byday)
            If (interval >= "0") And (interval < "9") Then
                'only 1 digit allowed
                rPattern.interval = Val(interval)
            End If
        
        ElseIf freq = FREQ_MONTHLY Then
            rPattern.RecurrenceType = olRecursMonthly
        
        ElseIf freq = FREQ_YEARLY Then
            rPattern.RecurrenceType = olRecursYearly
        
        Else
           'Outlook does not support
           'FREQ_SECONDLY, FREQ_MINUTELY, FREQ_HOURLY
           
           'but Outlook would support
           'olRecursMonthNth and olRecursYearNth
           '

        End If
        
        If Len(untilDate) > 0 Then
            On Error GoTo clearEndDate
            rPattern.PatternEndDate = timeStampToDateValue(untilDate)
            GoTo continueEndDate
clearEndDate:
            rPattern.NoEndDate = True
continueEndDate:
            On Error Resume Next
        End If
        
        If Len(count) > 0 Then
            rPattern.Occurrences = Val(count)
        End If
        
        posSearch = posClosing
        posOpening = InStr(posSearch, sEvent, TAG_RRULE)
        posClosing = InStr(posOpening + 1, sEvent, EOL)
    Wend

LocalError:
    On Error Resume Next
    rPattern = Nothing
End Sub

Sub addAttendees(ByRef sEvent As String, ByRef item As AppointmentItem)
    ' retrieve attendees by parsing sEvent
    On Error GoTo LocalError
    Dim resultValue As String
    Dim posOpening As Long, posClosing As Long, posFrom As Long, posTo  As Long
    Dim posSearch As Long, posCNstart As Long, posCNend As Long
    Dim oRecipient As recipient
    Dim quit, required As Boolean
      
    resultValue = ""
    posSearch = 1
    posOpening = InStr(posSearch, sEvent, PROPERTY_ATTENDEE)
    posClosing = InStr(posOpening + 1, sEvent, EOL)
    While (posOpening > 0)
        'found attendee
        posFrom = posOpening + Len(PROPERTY_ATTENDEE)
        If posClosing > 0 Then
            resultValue = Mid(sEvent, posFrom, posClosing - posFrom)
        Else
            resultValue = Mid(sEvent, posFrom)
        End If
        'resultvalue now should contain one attendee:
        'ATTENDEE;CN="Quality Assurance (qa@opencrx.org)";ROLE=REQ-PARTICIPANT;RSVP=TRUE:MAILTO:qa@opencrx.org
        posCNstart = InStr(resultValue, "CN=") + 4
        posCNend = InStr(posCNstart + 1, resultValue, Chr(34) & ";")
        required = (0 <> InStr(resultValue, ROLE_REQ_PARTICIPANT))
        resultValue = Mid(resultValue, posCNstart, posCNend - posCNstart)
        'remove commas
        resultValue = Replace(resultValue, ",", "")
        Set oRecipient = item.Recipients.Add(resultValue)
        If Not required Then
            oRecipient.Type = olCC
        End If
        posSearch = posClosing
        posOpening = InStr(posSearch, sEvent, PROPERTY_ATTENDEE)
        posClosing = InStr(posOpening + 1, sEvent, EOL)
    Wend

LocalError:
    On Error Resume Next
End Sub

Private Function getProperty(ByRef sEvent As String, ByVal propertyName) As String
    ' retrieve first property with matching name by parsing sEvent
    ' note that we must deal with section 4.1 of RFC 2445 (unfolding wrapped text)
    On Error GoTo LocalError
    Dim resultValue As String
    Dim posOpening As Long, posClosing As Long, posFrom As Long, posTo As Long
    Dim quit As Boolean
      
    resultValue = ""
    posOpening = InStr(sEvent, propertyName)
    If (posOpening > 0) Then
        'contains property
        posClosing = InStr(posOpening, sEvent, EOL)
        posFrom = posOpening + Len(propertyName)
        If posClosing > 0 Then
            resultValue = Mid(sEvent, posFrom, posClosing - posFrom)
        Else
            resultValue = Mid(sEvent, posFrom)
        End If
    End If
    resultValue = Replace(resultValue, "\\", "\") 'escaped chars as per section 4.3.11 RFC 2445
    resultValue = Replace(resultValue, "\;", ";") 'escaped chars as per section 4.3.11 RFC 2445
    resultValue = Replace(resultValue, "\,", ",") 'escaped chars as per section 4.3.11 RFC 2445
    getProperty = Trim(resultValue)
    Exit Function

LocalError:
    On Error Resume Next
    getProperty = ""
End Function

Private Function extractVEVENT(ByRef vcalendar As String, ByVal deleteExtracted) As String
    ' retrieve first VEVENT by parsing VCALENDER and (optionally) delete it from VCALENDER
    On Error GoTo LocalError
    Dim resultValue As String
    Dim posOpening As Long, posClosing As Long, posFrom As Long, posTo As Long
    Dim quit As Boolean
      
    resultValue = ""
    posOpening = InStr(vcalendar, TAG_VEVENT_BEGIN)
    posClosing = InStr(vcalendar, TAG_VEVENT_END)
    If (posOpening > 0) And (posClosing > 0) And (posOpening < posClosing) Then
        'contains VEVENT
        posFrom = posOpening + Len(TAG_VEVENT_BEGIN) + 1
        posTo = posClosing + Len(TAG_VEVENT_END) + 1
        resultValue = Mid(vcalendar, posFrom, posClosing - posFrom - 1)
        If deleteExtracted Then
          vcalendar = Left(vcalendar, posOpening - 1) + Right(vcalendar, Len(vcalendar) - posTo + 1)
        End If
    End If
    extractVEVENT = Trim(resultValue)
    Exit Function

LocalError:
    On Error Resume Next
    extractVEVENT = ""
End Function

Function getConnectionInfo(info As String, ByRef URL As String, ByRef user As String, ByRef password As String)
    Dim lines() As String
    Dim line As Variant
    
    lines = Split(Trim(info), vbCrLf)
    For Each line In lines
        If InStr(Trim(line), "url=") > 0 Then URL = Split(Trim(line), "url=")(1)
        If InStr(Trim(line), "user=") > 0 Then user = Split(Trim(line), "user=")(1)
        If InStr(Trim(line), "password=") > 0 Then password = Split(Trim(line), "password=")(1)
    Next
    If (URL = "") Or (user = "") Or (password = "") Then
        getConnectionInfo = False
    Else
        getConnectionInfo = True
    End If
End Function

Function updateTimestamp(ByVal info As String, ByVal tag As String) As String
    Dim lines() As String
    Dim line As Variant
    Dim timestamp As String
    Dim currentTimestamp As String
    Dim i As Integer
    Dim isfirst As Boolean
    
    timestamp = tag & VBA.Format(Now, "DD-MMM-YYYY HH:MM:SS")
    lines = Split(Trim(info), vbCrLf)
    i = 0
    For Each line In lines
        If InStr(Trim(line), tag) > 0 Then
          currentTimestamp = Split(Trim(line), tag)(1)
          lines(i) = timestamp
        End If
        i = i + 1
    Next
    If (currentTimestamp = "") Then
      info = info & vbCrLf & timestamp
    Else
        isfirst = True
        For Each line In lines
            If isfirst Then
                info = line
                isfirst = False
            Else
                info = info & vbCrLf & line
            End If
        Next
    End If
    updateTimestamp = info

End Function

Sub getFolder(startfolder As Outlook.MAPIFolder)
    Dim objFolder As Outlook.MAPIFolder
    Dim objItem As Object
    Dim filename As String, URL As String, user As String, password As String
    Dim timestamp As String
    Dim olAppt As Outlook.AppointmentItem
    Dim oItems As Outlook.items
    Dim sFilter As String
    Dim i As Integer
    Dim needsProcessing As Boolean
    On Error Resume Next

    needsProcessing = True
    If startfolder.DefaultItemType = olAppointmentItem Then
        'calendar folder
        needsProcessing = getConnectionInfo(startfolder.Description, URL, user, password)
    Else
        needsProcessing = False
    End If
    
    If needsProcessing Then
        ' process all the items in this folder
        'For Each objItem In StartFolder.items
        '    Call ProcessItem(objItem)
        'Next
            
        'this is an openCRX calendar
        Debug.Print "---GET " & startfolder.FolderPath & "[" & startfolder.items.count & " items]"
        Debug.Print startfolder.Description
        Debug.Print "---"
        timestamp = VBA.Format(Now, "dd MMM yyyy hh:mm") 'seconds are NOT supported
        filename = getICS(startfolder.name, URL, user, password)
        
        If filename <> "" Then
            Call importICS(filename, startfolder)
            'locate untouched appointments
            sFilter = "[LastModificationTime] < '" & timestamp & "'"
            Set oItems = startfolder.items.Restrict(sFilter)
            For i = oItems.count To 1 Step -1
                Set olAppt = oItems(i)
                If MsgBox("WARNING: this item is no longer contained in remote calendar" & vbCrLf & vbCrLf & _
                        "SUBJECT = " & olAppt.Subject & vbCrLf & _
                        "BODY = " & olAppt.Body & vbCrLf & _
                        "LOCATION = " & olAppt.LOCATION & vbCrLf & _
                        "START = " & olAppt.Start & vbCrLf & _
                        "END = " & olAppt.End & vbCrLf & _
                        "UID = " & olAppt.Mileage & vbCrLf & _
                        "LAST_MODIFIED = " & olAppt.LastModificationTime & vbCrLf & vbCrLf & _
                        "Do you want to delete this appointment?" _
                        , vbYesNo, "DELETE APPOINTMENT?") = vbYes Then
                    olAppt.Subject = "[" & startfolder.name & "] " & olAppt.Subject
                    olAppt.Delete
                End If
                        
            Next
            
            startfolder.Description = updateTimestamp(startfolder.Description, "last_download=")
        End If
    End If
    
    ' process all the subfolders of this folder
    For Each objFolder In startfolder.Folders
        Call getFolder(objFolder)
    Next
    
    Set objFolder = Nothing

End Sub

Sub putFolder(startfolder As Outlook.MAPIFolder)
    Dim objFolder As Outlook.MAPIFolder
    Dim objItem As Object
    Dim filename As String, URL As String, user As String, password As String
    Dim uploadRes As Integer
    Dim needsProcessing As Boolean
    On Error Resume Next

    If startfolder.DefaultItemType = olAppointmentItem Then
        'calendar folder
        needsProcessing = getConnectionInfo(startfolder.Description, URL, user, password)
    Else
        needsProcessing = False
    End If
    
    If needsProcessing Then
        'this is an openCRX calendar
        Debug.Print "---PUT " & startfolder.FolderPath & "[" & startfolder.items.count & " items]"
        Debug.Print startfolder.Description
        
        filename = exportICS(startfolder)
        
        If filename <> "" Then
            'creation successful
            uploadRes = putICS(filename, URL, user, password)

            ' process all the items in this folder
            'For Each objItem In StartFolder.items
            '    Call ProcessItem(objItem)
            '    objItem.LastModificationTime vs now
            'Next
            
            If uploadRes = 0 Then
                startfolder.Description = updateTimestamp(startfolder.Description, "last_upload=")
            End If
        End If
        Debug.Print "---"

    End If
    
    ' process all the subfolders of this folder
    For Each objFolder In startfolder.Folders
        Call putFolder(objFolder)
    Next
    
    Set objFolder = Nothing

End Sub

Sub processDefaultStore(isDownload As Boolean)
    Dim objNS As Outlook.NameSpace
    Dim MyFolder As Outlook.MAPIFolder
    On Error Resume Next
    
    Set objNS = Application.GetNamespace("MAPI")
    Set MyFolder = objNS.GetDefaultFolder(olFolderInbox)
    If isDownload Then
        Call getFolder(MyFolder.Parent)
    Else
        Call putFolder(MyFolder.Parent)
    End If
    Set objNS = Nothing
    Set MyFolder = Nothing
End Sub

Sub getCalendars()
  Call processDefaultStore(True)
  ProgressBox.Hide
End Sub

Sub putCalendars()
  Call processDefaultStore(False)
  ProgressBox.Hide
End Sub

Function getICS(ByVal filename As String, ByVal URL As String, ByVal user As String, ByVal password As String) As String

    Dim http
    Dim tmpDir As String
    Dim iFile As Integer, bData() As Byte, sData As String, lSize As Long
    Dim found As Boolean, i As Integer, offset As Integer
    
    On Error Resume Next
    tmpDir = getTempDir() & CRX_DIR & "\"
    MkDir tmpDir
    filename = tmpDir & filename & "." & CAL_EXT
    Kill filename
    On Error GoTo ShowErr
    
    'Create an Http object, use any of the four objects
'   Set Http = CreateObject("Microsoft.XMLHTTP")
'    Set http = CreateObject("MSXML2.ServerXMLHTTP")
    Set http = CreateObject("MSXML2.ServerXMLHTTP.4.0")
'   Set Http = CreateObject("WinHttp.WinHttpRequest")
    'Set http = CreateObject("WinHttp.WinHttpRequest.5.1")
    
    'Send request To URL
    http.SetOption 2, 13056 ' Ignore all SSL errors
    http.Open "GET", URL, False, user, password
    'If (user <> "") Then
    '  http.SetCredentials user, password, HTTPREQUEST_SETCREDENTIALS_FOR_SERVER 'this works with WinHttp.WinHttpRequest
    'End If
    
    If PROXY_SERVER <> "" Then
        http.SetProxy HTTPREQUEST_PROXYSETTING_PROXY, PROXY_SERVER
        If PROXY_USER <> "" Then
            http.setProxyCredentials PROXY_USER, PROXY_PASSWORD
        End If
    End If
    
    http.Send
    
    iFile = FreeFile()
    Open filename For Binary Access Write As #iFile
    Put #iFile, 1, http.responsebody
    Close iFile
    
    'the file written to disk contains some extra characters, hence
    'read the file, set characters to blank, and write file again
    lSize = FileLen(filename)
    If lSize > 0 Then
        ReDim bData(0 To lSize - 1)

        ' Read the existing UTF-8 file
        iFile = FreeFile()
        Open filename For Binary As #iFile
        Get #iFile, , bData
        Close #iFile
        
        'find TAG_VCALENDAR_BEGIN (text before might be broken/crippled)
        found = False
        i = LBound(bData) - 1
        While (i + Len(TAG_VCALENDAR_BEGIN) < UBound(bData) And (Not found))
            i = i + 1
            offset = 1
            While ((offset < Len(TAG_VCALENDAR_BEGIN)) And (bData(i + offset - 1) = Asc(Mid(TAG_VCALENDAR_BEGIN, offset, 1))))
                offset = offset + 1
            Wend
            If ((offset = Len(TAG_VCALENDAR_BEGIN)) And (bData(i + offset - 1) = Asc(Mid(TAG_VCALENDAR_BEGIN, offset, 1)))) Then
                found = True
            Else
                bData(i) = 32 'set to blank
            End If
        Wend
        Kill filename
        iFile = FreeFile()
        Open filename For Binary Access Write As #iFile
        Put #iFile, 1, bData
        Close iFile
    End If
    
    getICS = filename
    Err.Number = 0

ShowErr:
    If Err.Number > 0 Then
        getICS = ""
        Debug.Print "Error #" & Err.Number & ": " & Err.Description
    End If
    On Error Resume Next
End Function

Function putICS(ByVal filename As String, ByVal URL As String, ByVal user As String, ByVal password As String) As Integer
    'returns 0 if no error, otherwise error code
    
    Dim http
    Dim response As String
    Dim sData As String

    On Error GoTo ShowErr
    
    If FileLen(filename) > 0 Then
        'Create an Http object, use any of the four objects
    '   Set http = CreateObject("Microsoft.XMLHTTP")
    '   Set http = CreateObject("MSXML2.ServerXMLHTTP")
        Set http = CreateObject("MSXML2.ServerXMLHTTP.4.0")
    '   Set Http = CreateObject("WinHttp.WinHttpRequest")
    '   Set http = CreateObject("WinHttp.WinHttpRequest.5.1")
        
        'Send request To URL
        http.SetOption 2, 13056 ' Ignore all SSL errors
        http.Open "PUT", URL, False, user, password
        'If (user <> "") Then
        '  http.SetCredentials user, password, HTTPREQUEST_SETCREDENTIALS_FOR_SERVER 'this works with WinHttp.WinHttpRequest
        'End If
        
        If PROXY_SERVER <> "" Then
            http.SetProxy HTTPREQUEST_PROXYSETTING_PROXY, PROXY_SERVER
            If PROXY_USER <> "" Then
                http.setProxyCredentials PROXY_USER, PROXY_PASSWORD
            End If
        End If
        
        'Open URL As PUT request
        http.setRequestHeader "Content-Type", "text/calendar"
        
        http.Send readTextFile(filename)
    
        'convert Unicode encoded file to UTF8
        Call fileUniToUTF8(filename, filename)
        Err.Number = 0
        putICS = 0
    Else
        Err.Number = 999
        Err.Description = "file not found"
    End If

ShowErr:
    If Err.Number > 0 Then
        Debug.Print "Error #" & Err.Number & ": " & Err.Description
        putICS = 1
    End If
    On Error Resume Next
End Function

