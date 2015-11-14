#!/bin/sh

VCF="$HOME/snpEff/db/GRCh37/1kg/1kg.vcf"
VCF="$HOME/snpEff/db/GRCh37/1kg/1kg_head.vcf"

REF=GRCh37.75
REF=hg19

VCFOUT_BASE=`dirname $VCF`/`basename $VCF .vcf`
VCFOUT_ANN=$VCFOUT_BASE.ann.vcf
VCFOUT_DBSNP=$VCFOUT_BASE.ann.dbSNp.vcf
VCFOUT_CLINVAR=$VCFOUT_BASE.ann.dbSNp.clinvar.vcf
VCFOUT_DBNSFP=$VCFOUT_BASE.ann.dbSNp..clinvar.dbNSFP.vcf

# Path to databases
DBDIR="$HOME/snpEff/db/GRCh37/"
DBSNP="$DBDIR/dbSnp/dbSnp.vcf"
DBCLINVAR="$DBDIR/clinvar/clinvar.vcf"
DBNSFP="$DBDIR/dbNSFP/dbNSFP2.9.txt.gz"

#---
# Annotate
#---

#time java -Xmx8G -jar snpEff.jar ann -v -stats $VCF.html $REF $VCF > $VCFOUT_ANN
#
#time java -Xmx8G -jar SnpSift.jar ann -v $DBSNP $VCFOUT_ANN > $VCFOUT_DBSNP
#
#time java -Xmx8G -jar SnpSift.jar ann -v $DBCLINVAR $VCFOUT_DBSNP > $VCFOUT_CLINVAR

time java -Xmx8G -jar SnpSift.jar dbnsfp -v -d -db $DBNSFP $VCFOUT_CLINVAR > $VCFOUT_DBNSFP

