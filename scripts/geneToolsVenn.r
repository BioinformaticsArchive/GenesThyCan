
library(VennDiagram)

gnat <- as.character(read.delim('gene2docs_gnat.txt')[, 1])
moara <- as.character(read.delim('gene2docs_moara.txt')[, 1])
# union <- as.character(read.delim('gene2docs_union.txt')[, 1])
# intersect <- as.character(read.delim('gene2docs_intersect.txt')[, 1])


input <- list(gnat, moara)
names(input) <- c('GNAT',  'MOARA')

venn.diagram(input,"genes_tools.venn.png", col = "transparent",fill = c("cornflowerblue", "green"),alpha = 0.50)

# venn.diagram(input,"genes_tools.venn.png",col = "transparent",fill = c("cornflowerblue", "green", "yellow", "darkorchid1"),alpha = 0.50,label.col = c("red", "black", "darkorchid4", "black", "black", "black", "black", "black", "darkblue", "black", "black", "black", "black", "darkgreen", "black"),cat.col = c("darkblue", "darkgreen", "red", "darkorchid4"), scaled=TRUE, euler.d=TRUE)
