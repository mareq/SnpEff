package org.snpeff.snpEffect.testCases.unity;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.snpeff.annotate.HaplotypeAnnotationDetector;
import org.snpeff.annotate.HaplotypeDetectorSameCodon;
import org.snpeff.fileIterator.VcfFileIterator;
import org.snpeff.interval.Variant;
import org.snpeff.snpEffect.VariantEffect;
import org.snpeff.snpEffect.VariantEffects;
import org.snpeff.util.Gpr;
import org.snpeff.vcf.VcfEntry;

/**
 * Test multiple variants affecting one codon
 *
 * Test transcript:
 * 1:374-925, strand: +, id:transcript_0, Protein
 * 		Exons:
 * 		1:374-422 'exon_0_0', rank: 1, frame: ., sequence: gcgggtacggcgcttagcgagtttccaggatctctttcccggttagttc
 * 		1:563-567 'exon_0_1', rank: 2, frame: ., sequence: atatc
 * 		1:824-925 'exon_0_2', rank: 3, frame: ., sequence: cgcgcaagtattcttcatagtgcccgtagcagcaagtggtatcactcccatcataggacttgacttcgtaaagtgtgctaccttactgttaccatagtgcaa
 * 		CDS     :	gcgggtacggcgcttagcgagtttccaggatctctttcccggttagttcatatccgcgcaagtattcttcatagtgcccgtagcagcaagtggtatcactcccatcataggacttgacttcgtaaagtgtgctaccttactgttaccatagtgcaa
 * 		Protein :	AGTALSEFPGSLSRLVHIRASILHSARSSKWYHSHHRT*LRKVCYLTVTIVQ
 *
 * @author pcingola
 */
public class TestCasesHaplotypeDetectionSameCodonVcf extends TestCasesBase {

	class DetectorAndVcfEntries {
		public HaplotypeAnnotationDetector hapDet;
		public List<VcfEntry> vcfEntries;

		public DetectorAndVcfEntries() {
			vcfEntries = new ArrayList<>();
		}
	}

	public static int N = 1000;

	public TestCasesHaplotypeDetectionSameCodonVcf() {
		super();
	}

	DetectorAndVcfEntries detectSameCodon(String vcfFileName, boolean hasAnn[], boolean isFree[]) {
		minExons = 3;
		initSnpEffPredictor();

		if (debug) Gpr.debug("Transcript:\n" + transcript);
		VcfFileIterator vcf = new VcfFileIterator(vcfFileName);
		HaplotypeDetectorSameCodon hapdet = new HaplotypeDetectorSameCodon();
		hapdet.setVerbose(verbose);

		// Annotate all variants and add them o the detector
		DetectorAndVcfEntries dv = new DetectorAndVcfEntries();
		dv.hapDet = hapdet;
		for (VcfEntry ve : vcf) {
			dv.vcfEntries.add(ve);
			for (Variant var : ve.variants()) {
				VariantEffects variantEffects = snpEffectPredictor.variantEffect(var);
				for (VariantEffect veff : variantEffects)
					hapdet.add(ve, var, veff);
			}
		}

		// Sanity check
		Assert.assertEquals("Length of 'hasAnn' differs with vcfEntries.size", dv.vcfEntries.size(), hasAnn.length);
		Assert.assertEquals("Length of 'isFree' differs with vcfEntries.size", dv.vcfEntries.size(), isFree.length);

		// Check assertions
		int i = 0;
		for (VcfEntry ve : dv.vcfEntries) {
			Assert.assertTrue("Variant should " + (hasAnn[i] ? "" : "NOT") + " be in same codon:" + ve, dv.hapDet.hasHaplotypeAnnotation(ve) == hasAnn[i]);
			Assert.assertTrue("Variant should " + (isFree[i] ? "" : "NOT") + " be free:" + ve, dv.hapDet.isFree(ve) == isFree[i]);
			i++;
		}

		return dv;
	}

	@Override
	protected void init() {
		super.init();
		randSeed = 20170331;
	}

	String keyVariant(String chr, int start, String ref, String alt) {
		return chr + ":" + start + "_" + ref + "/" + alt;
	}

	String keyVariant(Variant var) {
		return keyVariant(var.getChromosomeName(), var.getStart(), var.getReference(), var.getAlt());
	}

	/**
	 * Two SNPs affect same codon: Implicit phasing
	 */
	@Test
	public void test_01_implicit() {
		Gpr.debug("Test");
		String vcfFileName = "tests/test_haplotype_samecodon_vcf_01_phase_implicit.vcf";
		boolean hasAnn[] = { true, true, false };
		boolean isFree[] = { true, true, false };
		detectSameCodon(vcfFileName, hasAnn, isFree);
	}

	/**
	 * Two SNPs affect same codon: Phased
	 */
	@Test
	public void test_01_phased() {
		Gpr.debug("Test");
		String vcfFileName = "tests/test_haplotype_samecodon_vcf_01_phased.vcf";
		boolean hasAnn[] = { true, true, false };
		boolean isFree[] = { true, true, false };
		detectSameCodon(vcfFileName, hasAnn, isFree);
	}

	/**
	 * Two SNPs affect same codon: Phased using phase group
	 */
	@Test
	public void test_01_phasegroup() {
		Gpr.debug("Test");
		String vcfFileName = "tests/test_haplotype_samecodon_vcf_01_phasegroup.vcf";
		boolean hasAnn[] = { true, true, false };
		boolean isFree[] = { false, false, false };
		detectSameCodon(vcfFileName, hasAnn, isFree);
	}

	/**
	 * Two SNPs affect same codon: Exons edges, implicit phasing
	 */
	@Test
	public void test_02_implicit() {
		Gpr.debug("Test");
		String vcfFileName = "tests/test_haplotype_samecodon_vcf_02_phase_implicit.vcf";
		boolean hasAnn[] = { true, true, false };
		boolean isFree[] = { true, true, false };
		detectSameCodon(vcfFileName, hasAnn, isFree);
	}

	/**
	 * Two SNPs affect same codon: Exon edges, phased
	 */
	@Test
	public void test_02_phased() {
		Gpr.debug("Test");
		String vcfFileName = "tests/test_haplotype_samecodon_vcf_02_phased.vcf";
		boolean hasAnn[] = { true, true, false };
		boolean isFree[] = { true, true, false };
		detectSameCodon(vcfFileName, hasAnn, isFree);
	}

	/**
	 * Two SNPs affect same codon: Exon edges: Phase group
	 */
	@Test
	public void test_02_phasegroup() {
		Gpr.debug("Test");
		String vcfFileName = "tests/test_haplotype_samecodon_vcf_02_phasegroup.vcf";
		boolean hasAnn[] = { true, true, false };
		boolean isFree[] = { false, false, false };
		detectSameCodon(vcfFileName, hasAnn, isFree);
	}

	/**
	 * Two SNPs: Only one affects the coding part of the transcript
	 */
	@Test
	public void test_03() {
		Gpr.debug("Test");
		String vcfFileName = "tests/test_haplotype_samecodon_vcf_03.vcf";
		boolean hasAnn[] = { false, false };
		boolean isFree[] = { true, false };
		detectSameCodon(vcfFileName, hasAnn, isFree);
	}

	/**
	 * Two MNP affect same codon
	 */
	@Test
	public void test_04_MNP() {
		Gpr.debug("Test");
		String vcfFileName = "tests/test_haplotype_samecodon_vcf_04.vcf";
		boolean hasAnn[] = { true, true, true, false };
		boolean isFree[] = { true, true, true, false };
		detectSameCodon(vcfFileName, hasAnn, isFree);
	}

}