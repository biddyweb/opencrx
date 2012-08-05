/* This software is published under the BSD license                          */
/* as listed below.                                                          */
/*                                                                           */
/* Copyright (c) 2004-2008, CRIXP Corp., Switzerland                         */
/* All rights reserved.                                                      */
/*                                                                           */
/* Redistribution and use in source and binary forms, with or without        */
/* modification, are permitted provided that the following conditions        */
/* are met:                                                                  */
/*                                                                           */
/* * Redistributions of source code must retain the above copyright          */
/* notice, this list of conditions and the following disclaimer.             */
/*                                                                           */
/* * Redistributions in binary form must reproduce the above copyright       */
/* notice, this list of conditions and the following disclaimer in           */
/* the documentation and/or other materials provided with the                */
/* distribution.                                                             */
/*                                                                           */
/* * Neither the name of CRIXP Corp. nor the names of the contributors       */
/* to openCRX may be used to endorse or promote products derived             */
/* from this software without specific prior written permission              */
/*                                                                           */
/* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND                    */
/* CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,               */
/* INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF                  */
/* MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE                  */
/* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS         */
/* BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,                  */
/* EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED           */
/* TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,             */
/* DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON         */
/* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,           */
/* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY            */
/* OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE                   */
/* POSSIBILITY OF SUCH DAMAGE.                                               */
/*                                                                           */
/* ------------------                                                        */
/*                                                                           */
/* This product includes software developed by the Apache Software           */
/* Foundation (http://www.apache.org/).                                      */
/*                                                                           */
/* This product includes software developed by contributors to               */
/* openMDX (http://www.openmdx.org/)                                         */
/*                                                                           */
DELETE FROM OOCKE1_PRODUCT_ WHERE OBJECT_ID IN (SELECT OBJECT_ID FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:product1:ComplexProductBundleType') ;
DELETE FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:product1:ComplexProductBundleType' ;
DELETE FROM OOCKE1_PRODUCT_ WHERE OBJECT_ID IN (SELECT OBJECT_ID FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:product1:ProductOffering') ;
DELETE FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:product1:ProductOffering' ;
DELETE FROM OOCKE1_PRODUCT_ WHERE OBJECT_ID IN (SELECT OBJECT_ID FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:product1:BundledProduct') ;
DELETE FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:product1:BundledProduct' ;
DELETE FROM OOCKE1_PRODUCT_ WHERE OBJECT_ID IN (SELECT OBJECT_ID FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:product1:BundledProductType') ;
DELETE FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:product1:BundledProductType' ;
DELETE FROM OOCKE1_PRODUCT_ WHERE OBJECT_ID IN (SELECT OBJECT_ID FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:product1:ProductBundle') ;
DELETE FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:product1:ProductBundle' ;
DELETE FROM OOCKE1_PRODUCT_ WHERE OBJECT_ID IN (SELECT OBJECT_ID FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:product1:ProductBundleType') ;
DELETE FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:product1:ProductBundleType' ;
DELETE FROM OOCKE1_PRODUCT_ WHERE OBJECT_ID IN (SELECT OBJECT_ID FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:product1:ComplexProductBundle') ;
DELETE FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:product1:ComplexProductBundle' ;
DELETE FROM OOCKE1_CONTRACTPOSITION_ WHERE OBJECT_ID IN (SELECT OBJECT_ID FROM OOCKE1_PRODUCT WHERE DTYPE LIKE 'org:opencrx:kernel:contract1:%BundledProductPosition') ;
DELETE FROM OOCKE1_CONTRACTPOSITION WHERE DTYPE LIKE 'org:opencrx:kernel:contract1:%BundledProductPosition' ;
DELETE FROM OOCKE1_CONTRACTPOSITION_ WHERE OBJECT_ID IN (SELECT OBJECT_ID FROM OOCKE1_PRODUCT WHERE DTYPE LIKE 'org:opencrx:kernel:contract1:%ProductBundlePosition') ;
DELETE FROM OOCKE1_CONTRACTPOSITION WHERE DTYPE LIKE 'org:opencrx:kernel:contract1:%ProductBundlePosition' ;
DELETE FROM OOCKE1_CONTRACTPOSITION_ WHERE OBJECT_ID IN (SELECT OBJECT_ID FROM OOCKE1_PRODUCT WHERE DTYPE LIKE 'org:opencrx:kernel:contract1:%ProductOfferingPosition') ;
DELETE FROM OOCKE1_CONTRACTPOSITION WHERE DTYPE LIKE 'org:opencrx:kernel:contract1:%ProductOfferingPosition' ;
DELETE FROM OOCKE1_CONTRACTPOSITION_ WHERE OBJECT_ID IN (SELECT OBJECT_ID FROM OOCKE1_PRODUCT WHERE DTYPE LIKE 'org:opencrx:kernel:contract1:%ComplexProductBundlePosition') ;
DELETE FROM OOCKE1_CONTRACTPOSITION WHERE DTYPE LIKE 'org:opencrx:kernel:contract1:%ComplexProductBundlePosition' ;
DELETE FROM OOCKE1_DEPOTPOSITION_ WHERE OBJECT_ID IN (SELECT OBJECT_ID FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:depot1:BundledProductDepotPosition') ;
DELETE FROM OOCKE1_DEPOTPOSITION WHERE DTYPE = 'org:opencrx:kernel:depot1:BundledProductDepotPosition' ;
DELETE FROM OOCKE1_DEPOTPOSITION_ WHERE OBJECT_ID IN (SELECT OBJECT_ID FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:depot1:ComplexProductBundleDepotPosition') ;
DELETE FROM OOCKE1_DEPOTPOSITION WHERE DTYPE = 'org:opencrx:kernel:depot1:ComplexProductBundleDepotPosition' ;
DELETE FROM OOCKE1_DEPOTPOSITION_ WHERE OBJECT_ID IN (SELECT OBJECT_ID FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:depot1:ProductBundleDepotPosition') ;
DELETE FROM OOCKE1_DEPOTPOSITION WHERE DTYPE = 'org:opencrx:kernel:depot1:ProductBundleDepotPosition' ;
DELETE FROM OOCKE1_DEPOTPOSITION_ WHERE OBJECT_ID IN (SELECT OBJECT_ID FROM OOCKE1_PRODUCT WHERE DTYPE = 'org:opencrx:kernel:depot1:ProductOfferingDepotPosition') ;
DELETE FROM OOCKE1_DEPOTPOSITION WHERE DTYPE = 'org:opencrx:kernel:depot1:ProductOfferingDepotPosition' ;
