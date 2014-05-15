import MySQLdb
import sys
import random

'''
@Chengkun Wu
This script will find a more compact representation of genes detected
'''

db = MySQLdb.connect(host="gnode1.mib.man.ac.uk", # host
     user="wchknudt", # your username
      passwd="wchk0714", # your password
      db="prj_mjkijcw2") # name of the data base

cur = db.cursor()

#sql = "SELECT doc_id, entity_id, entity_start, entity_end, confidence, gnat, moara, entity_term from tc_genes_gnat_moara"
#sql = "SELECT doc_id, entity_id, entity_start, entity_end, confidence, gnat, moara, entity_term, Symbol from tc_genes_gnat_moara, gene_info where gene_info.GeneID = tc_genes_gnat_moara.entity_id "
sql = "SELECT doc_id, entity_id, entity_start, entity_end, confidence, gnat, moara, entity_term, Symbol from tc_genes_gnat_moara, gene_info where gene_info.GeneID = tc_genes_gnat_moara.entity_id and doc_id in (select doc_id from tc_st_score_tp_pred where rb_label like '%MTC%')"
cur.execute(sql)

numrows = cur.rowcount

gene_doc_matrix = {}
doc2genes = {}
gene2docs = {}
docGene2Freq = {}
gene2symbol = {}
count = 0

print numrows, "human gene records!"
for x in xrange(0,numrows):
	gene_record = cur.fetchone()

	doc_id = gene_record[0]

	if gene_record[1].find('|') > 0:
		#print list(gene_record[1].split('|'))
		gene_id = int(random.choice(list(gene_record[1].split('|'))))
	else:
		gene_id = int(gene_record[1])

	start = int(gene_record[2])
	end = int(gene_record[3])
	confidence = float(gene_record[4])
	gnat = int(gene_record[5])
	moara = int(gene_record[6])
	gene2symbol[gene_id]  = gene_record[8]

	'''
	if gnat != 1 or moara != 1:
		continue
	'''
	doc_attr_tuple = (doc_id, start, end)
	gene_attr_tuple = (gene_id, confidence, gnat, moara)
	doc_gene_tuple = (doc_id, gene_id)

	#Documents to genes map
	if doc_id in doc2genes.keys():
		doc2genes[doc_id].append(gene_id)
	else:
		doc2genes[doc_id] = [gene_id]

	#Genes to documents map
	if gene_id in gene2docs.keys():
		gene2docs[gene_id].append(doc_id)
	else:
		gene2docs[gene_id] = [doc_id]

	#Frequency counting
	if doc_gene_tuple in docGene2Freq.keys():
		docGene2Freq[doc_gene_tuple] = docGene2Freq[doc_gene_tuple] + 1
	else:
		docGene2Freq[doc_gene_tuple] = 0

	gene_doc_matrix[doc_attr_tuple] = gene_attr_tuple

	count = count + 1

	if count % 1000 == 0:
		print count, "records processed."

output = open("gene2docs_mtc.txt", "w")

#Question 1: How many times are genes mentioned?
for gene_id in gene2docs.keys():

	outstr = '%d\t%d\t%s' % (gene_id, len(set(gene2docs[gene_id])), gene2symbol[gene_id])
	print outstr
	output.write(outstr + '\n')

output2 = open("doc2genes_mtc.txt", "w")
#Question 2: What genes are presented in thyroid cancer documents? 
for doc_id in doc2genes.keys():
	for gene_id in set(doc2genes[doc_id]):
		outstr = '%s\t%s' % (doc_id, gene2symbol[gene_id])
		print outstr
		output2.write(outstr + '\n')

output.close()

