
library(VennDiagram)

ptcdocs <- as.character(read.delim('PTC_docs.txt')[, 'doc_id'])
atcdocs <- as.character(read.delim('ATC_docs.txt')[, 'doc_id'])
ftcdocs <- as.character(read.delim('FTC_docs.txt')[, 'doc_id'])
mtcdocs <- as.character(read.delim('MTC_docs.txt')[, 'doc_id'])

input <- list(ptcdocs, atcdocs, ftcdocs, mtcdocs)
names(input) <- c('PTC',  'ATC', 'FTC', 'MTC')

venn.diagram(input,"st_docs.venn_large.png",col = "transparent",fill = c("cornflowerblue", "green", "yellow", "darkorchid1"),alpha = 0.50,label.col = c("red", "black", "darkorchid4", "black", "black", "black", "black", "black", "darkblue", "black", "black", "black", "black", "darkgreen", "black"),cat.col = c("darkblue", "darkgreen", "red", "darkorchid4"), scaled=TRUE, euler.d=TRUE, cex = 1.5)

