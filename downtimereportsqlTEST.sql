select * from (
select 1 as prnum,'1' Rep_PP_NUM,'Внутрисменные простои, всего' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 2 as prnum,'' Rep_PP_NUM,'удел. вес в общем количестве' nm,
       '%' as edizm,
       100 VSEGO,
       round(sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SNPH,
       round(sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SN,
       round(sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) FN,
       round(sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) LN,
       round(sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) BN,
       round(sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) KN,
       round(sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) NSN,
       round(sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUBR_1,
       round(sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUBR_2,
       round(sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUBR_3,
       round(sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUPNP,
       round(sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) FUPNP,
       round(sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 3 as prnum,'-' Rep_PP_NUM,'уд. на 1 скв.-опер.' nm,
       'ч' as edizm,
       round(sum(sum_znach)/sum(sum_cnt),2) VSEGO,
       sum(case when coalesce(zakazchik,200)=200 then val_udelno else 0 end) SNPH,
       sum(case when coalesce(zakazchik,200)=1 then val_udelno else 0 end) SN,
       sum(case when coalesce(zakazchik,200)=2 then val_udelno else 0 end) FN,
       sum(case when coalesce(zakazchik,200)=6 then val_udelno else 0 end) LN,
       sum(case when coalesce(zakazchik,200)=3 then val_udelno else 0 end) BN,
       sum(case when coalesce(zakazchik,200)=4 then val_udelno else 0 end) KN,
       sum(case when coalesce(zakazchik,200)=5 then val_udelno else 0 end) NSN,
       sum(case when coalesce(zakazchik,200)=10 then val_udelno else 0 end) SUBR_1,
       sum(case when coalesce(zakazchik,200)=11 then val_udelno else 0 end) SUBR_2,
       sum(case when coalesce(zakazchik,200)=12 then val_udelno else 0 end) SUBR_3,
       sum(case when coalesce(zakazchik,200)=7 then val_udelno else 0 end) SUPNP,
       sum(case when coalesce(zakazchik,200)=8 then val_udelno else 0 end) FUPNP,
       sum(case when coalesce(zakazchik,200)=9 then val_udelno else 0 end) UPNP
  from (select n.zakazchik,round(sum(np.znach)/count(distinct n.key),2) val_udelno, sum(np.znach) sum_znach,count(distinct n.key) sum_cnt
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
   group by n.zakazchik) k

UNION ALL
select 4 as prnum,'2' Rep_PP_NUM,'Метео' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 1
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 5 as prnum,'' Rep_PP_NUM,'удел. вес в общем количестве' nm,
       '%' as edizm,
       100 VSEGO,
       round(sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SNPH,
       round(sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SN,
       round(sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) FN,
       round(sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) LN,
       round(sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) BN,
       round(sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) KN,
       round(sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) NSN,
       round(sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUBR_1,
       round(sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUBR_2,
       round(sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUBR_3,
       round(sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUPNP,
       round(sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) FUPNP,
       round(sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 1
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 6 as prnum,'3' Rep_PP_NUM,'Технологические простои' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id in (2,27,10,39,3,4,5,19,45,46,31,32,24,35,38,47)
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 7 as prnum,'' Rep_PP_NUM,'удел. вес в общем количестве' nm,
       '%' as edizm,
       100 VSEGO,
       round(sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SNPH,
       round(sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SN,
       round(sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) FN,
       round(sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) LN,
       round(sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) BN,
       round(sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) KN,
       round(sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) NSN,
       round(sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUBR_1,
       round(sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUBR_2,
       round(sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUBR_3,
       round(sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUPNP,
       round(sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) FUPNP,
       round(sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id in (2,27,10,39,3,4,5,19,45,46,31,32,24,35,38,47)
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 8 as prnum,'3.1' Rep_PP_NUM,'Пропарка скважинного оборудования' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 2
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 9 as prnum,'3.2' Rep_PP_NUM,'Пропарка специальной техники' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 27
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 10 as prnum,'3.3' Rep_PP_NUM,'Ожидание ППУ для пропарки скважинного оборудования' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 10
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 11 as prnum,'3.4' Rep_PP_NUM,'Ожидание ППУ для пропарки специальной техники' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 39
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 12 as prnum,'3.5' Rep_PP_NUM,'АЗС и АТЗ' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 3
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 13 as prnum,'3.6' Rep_PP_NUM,'Чистка оборудования' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 4
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 14 as prnum,'3.7' Rep_PP_NUM,'Технологическое дежурство' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 5
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 15 as prnum,'3.8' Rep_PP_NUM,'Продавка в связи с ростом давления' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 19
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 16 as prnum,'3.9' Rep_PP_NUM,'Переход с насоса на насос в связи с изменением режима работы скважины в процессе закачки' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 45
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 17 as prnum,'3.10' Rep_PP_NUM,'Согласование параметров закачки в связи с измениением показателей в процессе закачки' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 46
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 18 as prnum,'3.11' Rep_PP_NUM,'Ожидание растворения шаров-активаторов' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 31
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 19 as prnum,'3.12' Rep_PP_NUM,'Набор технологической жидкости' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 32
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 20 as prnum,'3.13' Rep_PP_NUM,'Приготовление рабочего раствора (кислотной композиции)' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 24
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 21 as prnum,'3.14' Rep_PP_NUM,'Устранение течи нагнетательной и водоподающей линии' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 35
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 22 as prnum,'3.15' Rep_PP_NUM,'Ожидание начала обработки по заявке (ранний приезд Подрядчика)' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 38
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 23 as prnum,'3.16' Rep_PP_NUM,'Проведение инструктажа ЦДНГ(ОПЗ)' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 47
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 24 as prnum,'4' Rep_PP_NUM,'Организационные простои, всего' nm,
	   'ч' as edizm,
	   sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id in (6,20,25,48,7,8,9,49,11,12,50,40,13,51,33,43,34,52,14,53,54)
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 25 as prnum,'' Rep_PP_NUM,'удел. вес в общем количестве' nm,
       '%' as edizm,
       100 VSEGO,
       round(sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SNPH,
       round(sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SN,
       round(sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) FN,
       round(sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) LN,
       round(sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) BN,
       round(sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) KN,
       round(sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) NSN,
       round(sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUBR_1,
       round(sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUBR_2,
       round(sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUBR_3,
       round(sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUPNP,
       round(sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) FUPNP,
       round(sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id in (6,20,25,48,7,8,9,49,11,12,50,40,13,51,33,43,34,52,14,53,54)
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 26 as prnum,'4.1' Rep_PP_NUM,'Ожидание проведения ОПЗ перед МУН' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 6
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 27 as prnum,'4.2' Rep_PP_NUM,'Погрузо-разгрузочные работы на базе СНПХ' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 20
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 28 as prnum,'4.3' Rep_PP_NUM,'Ожидание получения химреагентов со склада СНПХ' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 25
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 29 as prnum,'4.4' Rep_PP_NUM,'Погрузо-разгрузочные работы на базе БПТОИКО' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 48
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 30 as prnum,'4.5' Rep_PP_NUM,'Ожидание завоза химреагентов' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 7
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 31 as prnum,'4.6' Rep_PP_NUM,'Ожидание представителя заказчика' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 8
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 32 as prnum,'4.7' Rep_PP_NUM,'Ожидание подготовки скважины' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 9
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 33 as prnum,'4.8' Rep_PP_NUM,'Ожидание готовности подъездных путей и расчистка кустовой площадки' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 49
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 34 as prnum,'4.9' Rep_PP_NUM,'Проведение инструктажа ЦДНГ(МУН)' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 11
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 35 as prnum,'4.10' Rep_PP_NUM,'Согласование параметров закачки по вине Заказчика' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 12
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 36 as prnum,'4.11' Rep_PP_NUM,'Согласование параметров закачки по вине Подрядчика' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 50
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 37 as prnum,'4.12' Rep_PP_NUM,'Переход с насоса на насос в связи с несоответствием фактической приемистости плану работ' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 40
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 38 as prnum,'4.13' Rep_PP_NUM,'Доставка оперативным транспортом (позднее прибытие вахты) по вине Заказчика' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 13
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 39 as prnum,'4.14' Rep_PP_NUM,'Доставка оперативным транспортом (позднее прибытие вахты) по вине Подрядчика' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 51
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 40 as prnum,'4.15' Rep_PP_NUM,'Доставка оперативным транспортом (позднее прибытие вахты) по вине Субподрядчика' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 54
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 41 as prnum,'4.16' Rep_PP_NUM,'Ожидание получения задания' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 33
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 42 as prnum,'4.17' Rep_PP_NUM,'Ожидание подтверждения заявки' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 43
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 43 as prnum,'4.18' Rep_PP_NUM,'Ожидание представления документации при ХОС Заказчиком' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 34
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 44 as prnum,'4.19' Rep_PP_NUM,'Ожидание представления документации при ХОС Подрядчиком' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 52
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 45 as prnum,'4.20' Rep_PP_NUM,'Прочие внутрисменные простои по вине Подрядчика' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 14
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 46 as prnum,'4.21' Rep_PP_NUM,'Прочие внутрисменные простои по вине Заказчика' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 53
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 47 as prnum,'5' Rep_PP_NUM,'Ремонт на кустовой площадке' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id in (15,29,16,28,26,36,37,44)
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 48 as prnum,'' Rep_PP_NUM,'удел. вес в общем количестве' nm,
       '%' as edizm,
       100 VSEGO,
       round(sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SNPH,
       round(sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SN,
       round(sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) FN,
       round(sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) LN,
       round(sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) BN,
       round(sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) KN,
       round(sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) NSN,
       round(sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUBR_1,
       round(sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUBR_2,
       round(sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUBR_3,
       round(sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) SUPNP,
       round(sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) FUPNP,
       round(sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end)/(case when sum(coalesce(np.znach, 0)) =0 then 1 else sum(coalesce(np.znach,0))end)*100, 2) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id in (15,29,16,28,26,36,37,44)
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 49 as prnum,'5.1' Rep_PP_NUM,'Ремонт верхнего оборудования, шасси' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 15
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 50 as prnum,'5.2' Rep_PP_NUM,'Ремонт электрооборудования' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 29
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 51 as prnum,'5.3' Rep_PP_NUM,'Ожидание запасных частей' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 16
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 52 as prnum,'5.4' Rep_PP_NUM,'Ожидание персонала КИПиА' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 28
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 53 as prnum,'5.5' Rep_PP_NUM,'Переход с насоса на насос в связи с выходом из строя' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) BN,
       sum(case when n.zakazchik=4 then coalesce(np.znach,0) else 0 end) KN,
       sum(case when n.zakazchik=5 then coalesce(np.znach,0) else 0 end) NSN,
       sum(case when n.zakazchik=10 then coalesce(np.znach,0) else 0 end) SUBR_1,
       sum(case when n.zakazchik=11 then coalesce(np.znach,0) else 0 end) SUBR_2,
       sum(case when n.zakazchik=12 then coalesce(np.znach,0) else 0 end) SUBR_3,
       sum(case when n.zakazchik=7 then coalesce(np.znach,0) else 0 end) SUPNP,
       sum(case when n.zakazchik=8 then coalesce(np.znach,0) else 0 end) FUPNP,
       sum(case when n.zakazchik=9 then coalesce(np.znach,0) else 0 end) UPNP
  from hos.defnar n
  	   join hos.defnarvip dv on n.key = dv.narkey
  	   join hos.prostoi_param np on n.key = np.keynar
	   join hos.spr_prostoi p on p.id=np.param
 where n.spr_org_id = 200
   and n.closed = 1
   and coalesce(n.zeh, 0) = (case when :ceh = 0 then coalesce(n.zeh, 0) else :ceh end)
   and ((with recursive rec as (
						 	select t.* from public.tematic_razdel t where t.id = n.razdel
						 	union
						 	select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
				    	)select id from rec where parent_id = 1000242) = (case when :tip = 0 then (with recursive rec as (
																			  select t.* from public.tematic_razdel t where t.id = n.razdel
																			  union
																			  select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
																		  )select id from rec where parent_id = 1000242) else :tip end))
   and p.id = 26
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))
UNION ALL
select 54 as prnum,'5.6' Rep_PP_NUM,'Продавка водой в связи с ремонтом оборудования' nm,
       'ч' as edizm,
       sum(coalesce(np.znach,0)) VSEGO,
       sum(case when coalesce(n.zakazchik,200)=200 then coalesce(np.znach,0) else 0 end) SNPH,
       sum(case when n.zakazchik=1 then coalesce(np.znach,0) else 0 end) SN,
       sum(case when n.zakazchik=2 then coalesce(np.znach,0) else 0 end) FN,
       sum(case when n.zakazchik=6 then coalesce(np.znach,0) else 0 end) LN,
       sum(case when n.zakazchik=3 then coalesce(np.znach,0) else 0 end) B
