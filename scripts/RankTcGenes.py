import MySQLdb
import sys
import random
import csv

'''
@Chengkun Wu
This script will find a more compact representation of genes detected
'''

db = MySQLdb.connect(host="gnode1.mib.man.ac.uk", # host
     user="wchknudt", # your username
      passwd="wchk0714", # your password
      db="prj_mjkijcw2") # name of the data base

cur = db.cursor()

gene2scores = {}
gene2name = {}

count = 0

with open('gene2docs_mtc.txt','rb') as tsvin:
    tsvin = csv.reader(tsvin, delimiter='\t')

    for row in tsvin:
		
		gene_id = row[0]
		freq = row[1]
		gene_name = row[2]

		gene2name[gene_id] = gene_name

		sql = "select doc_id, TC, PTC, ATC, FTC, MTC, DTC from tc_st_score_tp_pred where rb_label like %s and doc_id in (SELECT distinct(doc_id) FROM tc_genes_gnat_moara where entity_id = %s);"
		cur.execute(sql, ("%" + "MTC" + "%", gene_id))

		docs = set()

		tc_score = 0
		ptc_score = 0
		atc_score = 0
		ftc_score = 0
		mtc_score = 0

		for score_record in cur.fetchall():
			docs.add(score_record[0]) 

			tc_score = tc_score + float(score_record[1])
			ptc_score = ptc_score + float(score_record[2]) + float(score_record[6])
			atc_score = atc_score + float(score_record[3])
			ftc_score = ftc_score + float(score_record[4]) + float(score_record[6])
			mtc_score = mtc_score + float(score_record[5])

		st_score_vector = [tc_score, ptc_score, atc_score, ftc_score, mtc_score]

		gene2scores[gene_id] = st_score_vector

		count = count + 1
		if count % 10 == 0:
			print count, 'genes processed!'


output = open("geneScores_mtc.txt", "w")
for gene_id in set(gene2scores.keys()):
	tc_score = gene2scores[gene_id][0]
	ptc_score = gene2scores[gene_id][1]
	atc_score = gene2scores[gene_id][2]
	ftc_score = gene2scores[gene_id][3]
	mtc_score = gene2scores[gene_id][4]

	outstr = '%s\t%s\t%f\t%f\t%f\t%f\t%f' %(gene_id, gene2name[gene_id], tc_score, ptc_score, atc_score, ftc_score, mtc_score )
	output.write(outstr + '\n')

output.close()
print 'File successfully written!'
