import java.util.ArrayList;

import moara.bio.entities.Organism;
import moara.mention.MentionConstant;
import moara.mention.entities.GeneMention;
import moara.mention.functions.GeneRecognition;
import moara.normalization.entities.GenePrediction;
import moara.normalization.functions.ExactMatchingNormalization;
import moara.util.Constant;
import moara.util.EnvironmentVariable;

public class TestMoara {

	public static void main(String[] args) {
		
		// Abstract Pubmed Id 8076837
		/*
		String text = "Biological significance of aminopeptidase N/CD13 in thyroid carcinomas. "
				+ "Aminopeptidase N (APN)/CD13 is a transmembrane ectopeptidase expressed "
				+ "on a wide variety of cells. However, the precise function of APN/CD13 "
				+ "in tumor cells and the relationship of APN/CD13 to thyroid cancer "
				+ "remain unclear. In our study, we quantified the expression of APN/CD13 "
				+ "and additionally dipeptidyl peptidase IV (DPIV)/CD26 in "
				+ "thyroid carcinoma cell lines and in tissues of "
				+ "patients with thyroid carcinomas. "
				+ "Undifferentiated anaplastic thyroid carcinomas expressed more APN/CD13 "
				+ "than differentiated thyroid carcinomas. "
				+ "DPIV/CD26 showed an opposite expression pattern. "
				+ "We detected higher levels of DPIV/CD26 in "
				+ "follicular thyroid carcinomas (FTCs) and papillary thyroid carcinomas "
				+ "than in undifferentiated anaplastic thyroid carcinomas. "
				+ "In the undifferentiated thyroid carcinoma cell line 1736, "
				+ "APN/CD13 mRNA expression could be increased by epidermal growth factor, "
				+ "basic fibroblast growth factor, interleukin-6, "
				+ "and tumor necrosis factor alpha. FTC-133 cells stably transfected with "
				+ "an expression vector for APN-enhanced green fluorescent protein "
				+ "showed a higher migration rate than FTC-133 cells transfected with "
				+ "the enhanced green fluorescent protein-control plasmid. "
				+ "Overexpression of APN/CD13 in stably transfected cells is associated "
				+ "with down-regulation of N-myc down-regulated gene (NDRG)-1, "
				+ "melanoma-associated antigen ME491/CD63, and DPIV/CD26 gene expression. Inhibition of APN/CD13 mRNA expression by small interfering RNA induced NDRG-1, ME491/CD63, and DPIV/CD26 mRNA expression in cells of the undifferentiated thyroid carcinoma cell line C643. We conclude that APN/CD13-associated down-regulation of NDRG-1, ME491/CD63, and DPIV/CD26 in thyroid carcinoma cells is an important step of tumor progression to more malignant phenotypes, and we underline the important role of APN/CD13 as mediator in a multimolecular process regulating cell migration.";
				*/
		/*String text = "A gene (pkt1) was isolated from the filamentous fungus Trichoderma " +
				"reesei, which exhibits high homology with the yeast YPK1 and YKR2 (YPK2) " +
				"genes. It contains a 2123-bp ORF that is interrupted by two introns, and it " +
				"encodes a 662-amino-acid protein with a calculated M(r) of 72,820. During " +
				"active growth, pkt1 is expressed as two mRNAs of 3.1 and 2.8 kb which differ " +
				"in the 3' untranslated region due to the use of two different polyadenylation " +
				"sites. ";
				*/
		/*String text = "Transforming growth factor-beta1 (TGF-beta1) and activin A are "
				+ "the most important growth inhibitors of benign follicular epithelial cells of the human thyroid.";
				*/
		String text = "\"undifferentiated's thyroid carcinomas were carried out with antisera against calcitonin, calcitonin-gene related peptide (CGRP), somatostatin, and also thyroglobulin, using the PAP method. ";
		text = text.replace('\"', ' ');
		
		StringUtil su = new StringUtil();
		
		// Extracting...
		String text2 = new String(text);
		EnvironmentVariable.setMoaraHome("./");
		GeneRecognition gr = new GeneRecognition();
		ArrayList<GeneMention> gms = gr.extract(MentionConstant.MODEL_BC2,text);
		// Listing mentions...
		System.out.println("Start\tEnd\tMention");
		for (int i=0; i<gms.size(); i++) {
			GeneMention gm = gms.get(i);
			System.out.println(gm.Start() + "\t" + gm.End() + "\t" + gm.Text());
			//System.out.println(text2.substring(gm.Start(), gm.End()));
			System.out.println(su.getTokenByPosition(text,gm.Start(),gm.End()).trim());
		}
		if (true){
			return;
		}
		// Normalizing mentions...
		Organism yeast = new Organism(Constant.ORGANISM_YEAST);
		Organism human = new Organism(Constant.ORGANISM_HUMAN);
		ExactMatchingNormalization gn = new ExactMatchingNormalization(human);
		gms = gn.normalize(text,gms);
		// Listing normalized identifiers...
		
		System.out.println("\nStart\tEnd\t#Pred\tMention");
		for (int i=0; i<gms.size(); i++) {
			GeneMention gm = gms.get(i);
			if (gm.GeneIds().size()>0) {
				System.out.println(gm.Start() + "\t" + gm.End() + "\t" + gm.GeneIds().size() + 
					"\t" + su.getTokenByPosition(text,gm.Start(),gm.End()).trim());
				for (int j=0; j<gm.GeneIds().size(); j++) {
					GenePrediction gp = gm.GeneIds().get(j);
					System.out.print("\t" + gp.GeneId() + " " + gp.OriginalSynonym() + " " + gp.ScoreDisambig());
					System.out.println((gm.GeneId().GeneId().equals(gp.GeneId())?" (*)":""));
				}
			}
		}
	}
	
}
