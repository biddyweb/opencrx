/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Name:        $Id: WordToText.java,v 1.3 2007/10/25 12:30:02 wfro Exp $
 * Description: WordToText
 * Revision:    $Revision: 1.3 $
 * Owner:       CRIXP AG, Switzerland, http://www.crixp.com
 * Date:        $Date: 2007/10/25 12:30:02 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Copyright (c) 2007, CRIXP Corp., Switzerland
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
package org.opencrx.kernel.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hwpf.model.CHPBinTable;
import org.apache.poi.hwpf.model.CHPX;
import org.apache.poi.hwpf.model.ComplexFileTable;
import org.apache.poi.hwpf.model.TextPiece;
import org.apache.poi.hwpf.model.TextPieceTable;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.BitField;
import org.apache.poi.util.LittleEndian;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.kernel.exception.BasicException;

public class WordToText {

    //-----------------------------------------------------------------------    
    private static class SprmIterator {
        private byte[] _grpprl;

        int _offset;

        public SprmIterator(byte[] grpprl) {
            _grpprl = grpprl;
            _offset = 0;
        }

        public boolean hasNext() {
            return _offset < _grpprl.length;
        }

        public SprmOperation next() {
            SprmOperation op = new SprmOperation(_grpprl, _offset);
            _offset += op.size();
            return op;
        }

    }

    //-----------------------------------------------------------------------    
    private static class SprmOperation {
        
        public SprmOperation(
            byte[] grpprl, 
            int offset
        ) {
            short sprmStart = LittleEndian.getShort(grpprl, offset);
            offset += 2;

            _operation = OP_BITFIELD.getValue(sprmStart);
            _type = TYPE_BITFIELD.getValue(sprmStart);
            int sizeCode = SIZECODE_BITFIELD.getValue(sprmStart);

            switch (sizeCode) {
            case 0:
            case 1:
                _operand = LittleEndian.getUnsignedByte(grpprl, offset);
                _sizeNeeded = 3;
                break;
            case 2:
            case 4:
            case 5:
                _operand = LittleEndian.getShort(grpprl, offset);
                _sizeNeeded = 4;
                break;
            case 3:
                _operand = LittleEndian.getInt(grpprl, offset);
                _sizeNeeded = 6;
                break;
            case 6:
                _varOperand = new byte[grpprl[offset++]];
                System.arraycopy(grpprl, offset, _varOperand, 0,
                        _varOperand.length);
                _sizeNeeded = _varOperand.length + 3;
                break;
            case 7:
                byte threeByteInt[] = new byte[4];
                threeByteInt[0] = grpprl[offset];
                threeByteInt[1] = grpprl[offset + 1];
                threeByteInt[2] = grpprl[offset + 2];
                threeByteInt[3] = (byte) 0;
                _operand = LittleEndian.getInt(threeByteInt, 0);
                _sizeNeeded = 5;
                break;

            }
        }

        public int getType() {
            return _type;
        }

        public int getOperation() {
            return _operation;
        }

        public int getOperand() {
            return _operand;
        }

        public int size() {
            return _sizeNeeded;
        }
        
        final static private BitField OP_BITFIELD = new BitField(0x1ff);
        final static private BitField TYPE_BITFIELD = new BitField(0x1c00);
        final static private BitField SIZECODE_BITFIELD = new BitField(0xe000);

        private int _type;
        // private boolean _variableLen;
        private int _operation;
        private int _operand;
        private byte[] _varOperand;
        private int _sizeNeeded;
        
    }

    //-----------------------------------------------------------------------    
    /**
     * Gets the text from a Word document.
     * 
     * @param in
     *            The InputStream representing the Word file.
     */
    public Reader parse(
        InputStream in
    ) throws ServiceException, IOException {
        POIFSFileSystem fsys = new POIFSFileSystem(in);
        DocumentEntry headerProps = (DocumentEntry) fsys.getRoot().getEntry("WordDocument");
        DocumentInputStream din = fsys.createDocumentInputStream("WordDocument");
        byte[] header = new byte[headerProps.getSize()];

        din.read(header);
        din.close();

        int info = LittleEndian.getShort(header, 0xa);
        if ((info & 0x4) != 0) {
            throw new ServiceException(
                BasicException.Code.DEFAULT_DOMAIN,
                BasicException.Code.NOT_SUPPORTED, null,
                "Fast-saved files are unsupported at this time"
            );
        }
        if ((info & 0x100) != 0) {
            throw new ServiceException(
                BasicException.Code.DEFAULT_DOMAIN,
                BasicException.Code.NOT_SUPPORTED, null,
                "This document is password protected"
            );
        }

        // Determine the version of Word this document came from.
        int nFib = LittleEndian.getShort(header, 0x2);
        switch (nFib) {
            case 101:
            case 102:
            case 103:
            case 104:
                throw new ServiceException(
                    BasicException.Code.DEFAULT_DOMAIN,
                    BasicException.Code.NOT_SUPPORTED, null,
                    "Word 6.0 format not supported"
                );
        }

        // Get the information we need from the header
        boolean useTable1 = (info & 0x200) != 0;

        // get the location of the piece table
        int complexOffset = LittleEndian.getInt(header, 0x1a2);

        // determine which table stream we must use.
        String tableName = null;
        if (useTable1) {
            tableName = "1Table";
        } else {
            tableName = "0Table";
        }

        DocumentEntry table = (DocumentEntry) fsys.getRoot().getEntry(tableName);
        byte[] tableStream = new byte[table.getSize()];

        din = fsys.createDocumentInputStream(tableName);

        din.read(tableStream);
        din.close();

        int chpOffset = LittleEndian.getInt(header, 0xfa);
        int chpSize = LittleEndian.getInt(header, 0xfe);
        int fcMin = LittleEndian.getInt(header, 0x18);
        CHPBinTable cbt = new CHPBinTable(
            header, 
            tableStream, 
            chpOffset,
            chpSize, 
            fcMin
        );

        // load our text pieces and our character runs
        ComplexFileTable cft = new ComplexFileTable(
            header, 
            tableStream,
            complexOffset, 
            fcMin
        );
        TextPieceTable tpt = cft.getTextPieceTable();
        List<TextPiece> textPieces = tpt.getTextPieces();

        // make the POIFS objects available for garbage collection
        din = null;
        fsys = null;
        table = null;
        headerProps = null;

        Iterator textIt = textPieces.iterator();

        TextPiece currentPiece = (TextPiece) textIt.next();
        int currentTextStart = currentPiece.getStart();
        int currentTextEnd = currentPiece.getEnd();

        StringBuilder text = new StringBuilder();

        // iterate through all text runs extract the text only if they haven't
        // been
        // deleted
        for (CHPX chpx : (List<CHPX>) cbt.getTextRuns()) {
            boolean deleted = isDeleted(chpx.getGrpprl());
            if (deleted) {
                continue;
            }

            int runStart = chpx.getStart();
            int runEnd = chpx.getEnd();

            while (runStart >= currentTextEnd) {
                currentPiece = (TextPiece) textIt.next();
                currentTextStart = currentPiece.getStart();
                currentTextEnd = currentPiece.getEnd();
            }

            if (runEnd < currentTextEnd) {
                String str = currentPiece.substring(
                    runStart - currentTextStart, 
                    runEnd - currentTextStart
                );
                text.append(str);
            } 
            else if (runEnd > currentTextEnd) {
                while (runEnd > currentTextEnd) {
                    String str = currentPiece.substring(
                        runStart - currentTextStart, 
                        currentTextEnd - currentTextStart
                    );
                    text.append(str);
                    if (textIt.hasNext()) {
                        currentPiece = (TextPiece) textIt.next();
                        currentTextStart = currentPiece.getStart();
                        runStart = currentTextStart;
                        currentTextEnd = currentPiece.getEnd();
                    } else {
                        return new StringReader(text.toString());
                    }
                }
                String str = currentPiece.substring(
                    0, 
                    runEnd - currentTextStart
                );
                text.append(str);
            } 
            else {
                String str = currentPiece.substring(
                    runStart - currentTextStart, 
                    runEnd - currentTextStart
                );
                if (textIt.hasNext()) {
                    currentPiece = (TextPiece) textIt.next();
                    currentTextStart = currentPiece.getStart();
                    currentTextEnd = currentPiece.getEnd();
                }
                text.append(str);
            }
        }
        return new StringReader(text.toString());
    }

    //-----------------------------------------------------------------------    
    /**
     * Used to determine if a run of text has been deleted.
     * 
     * @param grpprl
     *            The list of sprms for a particular run of text.
     * @return true if this run of text has been deleted.
     */
    private boolean isDeleted(
        byte[] grpprl
    ) {
        SprmIterator iterator = new SprmIterator(grpprl);
        while (iterator.hasNext()) {
            SprmOperation op = iterator.next();
            // 0 is the operation that signals a FDelRMark operation
            if (op.getOperation() == 0 && op.getOperand() != 0) {
                return true;
            }
        }
        return false;
    }

}
