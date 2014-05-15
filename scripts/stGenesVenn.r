
library(VennDiagram)

ptcGenes <- as.character(read.delim('geneScores_ptc.txt')[, 1])
atcGenes <- as.character(read.delim('geneScores_atc.txt')[, 1])
ftcGenes <- as.character(read.delim('geneScores_ftc.txt')[, 1])
mtcGenes <- as.character(read.delim('geneScores_mtc.txt')[, 1])
allGenes <- as.character(read.delim('gene2docs_union.txt')[, 1])

input <- list(ptcGenes, atcGenes, ftcGenes, mtcGenes)
names(input) <- c('PTC',  'ATC', 'FTC', 'MTC')

venn.diagram(input,"st_genes.venn_large.png",col = "transparent",fill = c("cornflowerblue", "green", "yellow", "darkorchid1"),alpha = 0.50,label.col = c("red", "black", "darkorchid4", "black", "black", "black", "black", "black", "darkblue", "black", "black", "black", "black", "darkgreen", "black"),cat.col = c("darkblue", "darkgreen", "red", "darkorchid4"), scaled=TRUE, euler.d=TRUE, cex = 1.5)



tcgdb_all <- as.character(read.delim('tcgdb_all.txt')[, 1])
tcgdb_ptc <- as.character(read.delim('tcgdb_ptc.txt')[, 1])
tcgdb_atc <- as.character(read.delim('tcgdb_atc.txt')[, 1])
tcgdb_ftc <- as.character(read.delim('tcgdb_ftc.txt')[, 1])
tcgdb_mtc <- as.character(read.delim('tcgdb_mtc.txt')[, 1])

all_diff <- setdiff(tcgdb_all, allGenes)
ptc_diff <- setdiff(tcgdb_ptc, ptcGenes)
atc_diff <- setdiff(tcgdb_atc, atcGenes)
ftc_diff <- setdiff(tcgdb_ftc, ftcGenes)
mtc_diff <- setdiff(tcgdb_mtc, mtcGenes)

comp_list <- c(length(tcgdb_all), length(tcgdb_ptc), length(tcgdb_atc), length(tcgdb_ftc), length(tcgdb_mtc))
diff_list <- c(length(all_diff), length(ptc_diff), length(atc_diff), length(ftc_diff), length(mtc_diff))
