SELECT 
    tc_pathway_mentions_clean.doc_id, pathway_mention, rb_label
FROM
    prj_mjkijcw2.tc_pathway_mentions_clean,
    tc_st_score_tp_pred
where
    tc_pathway_mentions_clean.doc_id = tc_st_score_tp_pred.doc_id
	and rb_label like '%PTC%'

SELECT 
    tc_pathway_mentions_clean.doc_id, pathway_mention, rb_label
FROM
    prj_mjkijcw2.tc_pathway_mentions_clean,
    tc_st_score_tp_pred
where
    tc_pathway_mentions_clean.doc_id = tc_st_score_tp_pred.doc_id
	and rb_label like '%PTC%'