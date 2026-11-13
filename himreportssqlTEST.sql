select row_number() over() as rn, * from(
       select
       nn.kust,
       nn.skv,
       nn.mest,
       nn.NGDU,
       nn.zakazchik,
       nn.Tehnika,
       nn.Gos_Nomer,
       nn.ceh,
       nn.date_b,
       nn.date_n,
       nn.Vid_HOS,
       nn.Metod_HOS,
       nn.Group_Metod_HOS,
       nn.Kolich_PZR,
       nn.Priznak,
       nn.Paker,
       nn.V_reag_kanistr,
       nn.V_reag_emk,
       nn.V_rastvor,
       nn.V_zhidkost,
       nn.V_rast_zhidk,
       nn.Plan_Priemist,--18
       nn.Fakt_Priemist,
       nn.Fakt_Priemist_after,
       nn.Fakt_Priemist_avg,
       nn.Pereezd_km,
       nn.Pereezd_bad_km,
       nn.Pereezd_good_km,
       nn.Pereezd_best_km,
       nn.Norma_Vip,
       nn.Fakt,
       (case when nn.Fakt = 0
         then 0
       when nn.Fakt is null
         then 0
       else
         round(nn.Norma_Vip/nn.Fakt*100, 2)
  		end) as Uroven_vip,
       round(nn.PZR_na_baze_norma,2) as PZR_na_baze_norma,
       round(nn.PrigKomp_norma,2) as PrigKomp_norma,
       round(nn.OzhReakKomp_norma,2) as OzhReakKomp_norma,
       round(nn.Proezd_norma,2) as Proezd_norma,
       round(nn.PR_na_skv_norma,2) as PR_na_skv_norma,
       round(nn.ZR_na_skv_norma,2) as ZR_na_skv_norma,
       round(nn.Prodavka_norma,2) as Prodavka_norma,
       round(nn.Prodavka_vodoi_norma,2) as Prodavka_vodoi_norma,
       round(nn.PZR_na_baze_norma,2) as PZR_na_baze_norma,
       round(nn.PZR_na_baze_fakt,2) as PZR_na_baze_fakt,
       round(nn.PZR_na_baze_Uroven_Vip,2) as PZR_na_baze_Uroven_Vip,
       round(nn.PrigKomp_norma,2) as PrigKomp_norma,
       round(nn.PrigKomp_fakt,2) as PrigKomp_fakt,
       round(PrigKomp_Uroven_Vip,2) as PrigKomp_Uroven_Vip,
       round(nn.OzhReakKomp_norma,2) as OzhReakKomp_norma,
       round(nn.OzhReakKomp_fakt,2) as OzhReakKomp_fakt,
       round(OzhReakKomp_Uroven_Vip,2) as OzhReakKomp_Uroven_Vip,
       round(nn.Proezd_norma,2) as Proezd_norma,
       round(nn.Proezd_fakt,2) as Proezd_fakt,
       round(nn.Proezd_Uroven_Vip,2) as Proezd_Uroven_Vip,
       round(nn.PR_na_skv_norma,2) as PR_na_skv_norma,
       round(nn.PR_na_skv_fakt,2) as PR_na_skv_fakt,
       round(nn.PR_na_skv_Uroven_Vip,2) as PR_na_skv_Uroven_Vip,
       round(nn.ZR_na_skv_norma,2) as ZR_na_skv_norma,
       round(nn.ZR_na_skv_fakt,2) as ZR_na_skv_fakt,
       round(nn.ZR_na_skv_Uroven_Vip,2) as ZR_na_skv_Uroven_Vip,
       round(nn.Prodavka_norma,2) as Prodavka_norma,
       round(nn.Prodavka_fakt,2) as Prodavka_fakt,
       round(nn.Prodavka_Uroven_Vip,2) as Prodavka_Uroven_Vip,
       round(nn.Prodavka_vodoi_norma,2) as Prodavka_vodoi_norma,
       round(nn.Prodavka_vodoi_fakt,2) as Prodavka_vodoi_fakt,
       round(nn.Prodavka_vodoi_Uroven_Vip,2) as Prodavka_vodoi_Uroven_Vip,

       (case when nn.Fakt_Priemist_avg = 0
         then 0
       else
         round( nn.V_rastvor/nn.Fakt_Priemist_avg*24, 2)
		end) as Rasch_Fact_Priem_Prod,

       round(nn.Prodavka_fakt,2) as Prodavka_fakt,
       case when coalesce(nn.Fakt_Priemist_avg,0)=0 or coalesce(nn.Prodavka_fakt,0)=0
        then 0
        else round(round(nn.V_rastvor/nn.Fakt_Priemist_avg*24,2)/round(nn.Prodavka_fakt,2)*100,2)
       end as Prodavka_Uroven_Vip_Rasch,

       (case when nn.Fakt_Priemist_after = 0
         then 0
       when nn.Fakt_Priemist_after is null
         then 0
       else
         round( nn.V_zhidkost/nn.Fakt_Priemist_after*24, 2 )
		end) as Rasch_Fact_Priem_Prodav_Vod,

       nn.Prodavka_vodoi_fakt,
       case when coalesce(nn.Fakt_Priemist_after,0)=0 or coalesce(nn.Prodavka_vodoi_fakt,0)=0
        then 0
        else round(round(nn.V_zhidkost/nn.Fakt_Priemist_after*24,2)/nn.Prodavka_vodoi_fakt*100,2)
       end as Prodavka_Vod_Uroven_Vip_Rasch,

       (case when nn.Fakt_Priemist_avg = 0
         then 0
       else
         round(coalesce(nn.V_rastvor, 0) / nn.Fakt_Priemist_avg*24, 2)
	   end) +
       (case when nn.Fakt_Priemist_after = 0
         then 0
       when nn.Fakt_Priemist_after is null
         then 0
       else
         round(coalesce(nn.V_zhidkost, 0) / nn.Fakt_Priemist_after*24, 2)
	   end) as Rasch_Fact_Priem_Prod_Prod_Vod,

       coalesce(nn.Prodavka_fakt,0)+coalesce(nn.Prodavka_vodoi_fakt,0) as Prod_and_Prod_Vod_Fact,
       case when coalesce(nn.Fakt_Priemist_avg,0)=0 or nn.Fakt_Priemist_after=0 or coalesce(nn.Prodavka_fakt,0)+coalesce(nn.Prodavka_vodoi_fakt,0)=0
        then 0
        else ROUND( (round(coalesce(nn.V_rastvor,0)/nn.Fakt_Priemist_avg*24,2)+round(coalesce(nn.V_zhidkost,0)/nn.Fakt_Priemist_after*24,2)) / (coalesce(nn.Prodavka_fakt,0)+coalesce(nn.Prodavka_vodoi_fakt,0)) ,2)*100
       end as Prod_and_Prod_Vod_Ur_Vip_Rasch,

       round(coalesce(nn.PZR_na_baze_norma,0),2)+
       round(coalesce(nn.Proezd_norma,0),2)+
       round(coalesce(nn.PR_na_skv_norma,0),2)+
       round(coalesce(nn.ZR_na_skv_norma,0),2)+
       round(coalesce(nn.PrigKomp_norma,0),2)+
       round(coalesce(nn.OzhReakKomp_norma,0),2)+
       (case when nn.Fakt_Priemist_avg = 0
         then 0
       else
         round(coalesce(nn.V_rastvor/nn.Fakt_Priemist_avg*24, 0), 2)
		end) +
       (case when nn.Fakt_Priemist_after = 0
         then 0
       else
         round(coalesce(nn.V_zhidkost/nn.Fakt_Priemist_after*24, 0), 2)
	    end) as Rasch_Vrem_Fact_Priem_Na_HOS,
       --coalesce(decode(nn.Fakt_Priemist,0,0,Null,0,nn.V_rastvor/nn.Fakt_Priemist*24),0)+
       --coalesce(decode(nn.Fakt_Priemist,0,0,Null,0,nn.V_zhidkost/nn.Fakt_Priemist*24),0) as Rasch_Vrem_Fact_Priem_Na_HOS,

       round(
       coalesce(nn.PZR_na_baze_fakt,0)+
       coalesce(nn.PrigKomp_fakt,0)+
       coalesce(nn.OzhReakKomp_fakt,0)+
       coalesce(nn.Proezd_fakt,0)+
       coalesce(nn.PR_na_skv_fakt,0)+
       coalesce(nn.ZR_na_skv_fakt,0)+
       coalesce(nn.Prodavka_fakt,0)+
       coalesce(nn.Prodavka_vodoi_fakt,0),2) as Fakt_HOS,--58(31+34+37+40+43+46)

       case when (coalesce(nn.PZR_na_baze_fakt,0)+
                 coalesce(nn.PrigKomp_fakt,0)+
                 coalesce(nn.OzhReakKomp_fakt,0)+
                 coalesce(nn.Proezd_fakt,0)+
                 coalesce(nn.PR_na_skv_fakt,0)+
                 coalesce(nn.ZR_na_skv_fakt,0)+
                 coalesce(nn.Prodavka_fakt,0)+
                 coalesce(nn.Prodavka_vodoi_fakt,0))=0 then 0
            else round(
                 (coalesce(nn.PZR_na_baze_norma,0)+
                 coalesce(nn.PrigKomp_norma,0)+
                 coalesce(nn.OzhReakKomp_norma,0)+
                 coalesce(nn.Proezd_norma,0)+
                 coalesce(nn.PR_na_skv_norma,0)+
                 coalesce(nn.ZR_na_skv_norma,0)+
                 coalesce((case when nn.Fakt_Priemist = 0
         							then 0
       							when nn.Fakt_Priemist is null
         							then 0
       							else
        							 nn.V_rastvor/nn.Fakt_Priemist*24
  							end),0)+
                 coalesce((case when nn.Fakt_Priemist = 0
						            then 0
						          when nn.Fakt_Priemist is null
						            then 0
						          else
						            nn.V_zhidkost/nn.Fakt_Priemist*24
						     end),0))/
                 (coalesce(nn.PZR_na_baze_fakt,0)+
                 coalesce(nn.PrigKomp_fakt,0)+
                 coalesce(nn.OzhReakKomp_fakt,0)+
                 coalesce(nn.Proezd_fakt,0)+
                 coalesce(nn.PR_na_skv_fakt,0)+
                 coalesce(nn.ZR_na_skv_fakt,0)+
                 coalesce(nn.Prodavka_fakt,0)+
                 coalesce(nn.Prodavka_vodoi_fakt,0))*100,2) end as Uroven_Vip_HOS,--59(57/58*101)
        round(
        (coalesce(nn.PZR_na_baze_norma,0)+
        coalesce(nn.Proezd_norma,0)+
        coalesce(nn.PR_na_skv_norma,0)+
        coalesce(nn.ZR_na_skv_norma,0)+
        coalesce((case when nn.Fakt_Priemist = 0
						 then 0
					   when nn.Fakt_Priemist is null
						 then 0
					   else
						 nn.V_rastvor/nn.Fakt_Priemist*24
				  end),0)+
        coalesce((case when nn.Fakt_Priemist = 0
						 then 0
					   when nn.Fakt_Priemist is null
						 then 0
					   else
						 nn.V_zhidkost/nn.Fakt_Priemist*24
				  end),0))
       -
       (coalesce(nn.PZR_na_baze_fakt,0)+
        coalesce(nn.Proezd_fakt,0)+
        coalesce(nn.PR_na_skv_fakt,0)+
        coalesce(nn.ZR_na_skv_fakt,0)+
        coalesce(nn.Prodavka_fakt,0)+
        coalesce(nn.Prodavka_vodoi_fakt,0)) ,2) as Otklonenie,--60(57-58)

       nn.Poteri_Rab_Vrem,--61
       Meteo,
       Proparka,
       Proparka_spec_tech,
       OzhPPUSkvOb,
       OzhPPUSpecTech,
       AZSiATZ,
       Chistka,
       Tech_Dezh,
       ProdavkaRostDav,
       PerehodSNasosaSIzmRezh,
       SoglParSIzmPokZak,
       OzhSharov,
       NaborTechZhidk,
       PrigKomp,
       UstrTechiLine,
       OzhNachObrPoZayavke,
       InstCDNGOpz,
       OzhOPZ,
       PogrRazgrSNPH,
       OzhPoluchHimReagSNPH,
       PogrRazgrBPTOIKO,
       OzhZavozHim,
       OzhZakazchik,
       OzhPodgSkv,
       OzhGotovPutei,
       InstCDNGMun,
       SoglParamZakaz,
       SoglParamPodr,
       PerehodSNasosaSNesootv,
       DostavkaTranspPoVineZakaz,
       DostavkaTranspPoVinePodr,
       DostavkaTranspPoVineSubPodr,
       OzhPolZad,
       OzhPodtvZayavki,
       OzhDokumZakaz,
       OzhDokumPodr,
       ProchieProstoiPodr,
       ProchieProstoiZakaz,
       RemKust,
       RemElectro,
       OzhZapChast,
       OzhKIPiA,
       PerehodSNasosaSVih,
       ProdavkaRemontObor,
       TechObsluzh,
       EzhesmTechObsluzh,
       HolProezdPoVineZakazChas,
       HolProezdPoVineSNPHChas,

       nn.Kolvo_Smen,
       nn.Operator_5,nn.Operator_3,nn.Motorist_6,nn.Mashinist_5,nn.Voditel,
       nn.Operator_5_1SM,nn.Operator_3_1SM,nn.Motorist_6_1SM,nn.Mashinist_5_1SM,nn.Voditel_1SM,
       nn.Operator_5_2SM,nn.Operator_3_2SM,nn.Motorist_6_2SM,nn.Mashinist_5_2SM,nn.Voditel_2SM,
       nn.Operator_5_3SM,nn.Operator_3_3SM,nn.Motorist_6_3SM,nn.Mashinist_5_3SM,nn.Voditel_3SM,
       nn.Operator_5_4SM,nn.Operator_3_4SM,nn.Motorist_6_4SM,nn.Mashinist_5_4SM,nn.Voditel_4SM,
       nn.Operator_5_5SM,nn.Operator_3_5SM,nn.Motorist_6_5SM,nn.Mashinist_5_5SM,nn.Voditel_5SM,
       nn.Operator_5_6SM,nn.Operator_3_6SM,nn.Motorist_6_6SM,nn.Mashinist_5_6SM,nn.Voditel_6SM,
       nn.Operator_5_7SM,nn.Operator_3_7SM,nn.Motorist_6_7SM,nn.Mashinist_5_7SM,nn.Voditel_7SM,
       nn.Operator_5_8SM,nn.Operator_3_8SM,nn.Motorist_6_8SM,nn.Mashinist_5_8SM,nn.Voditel_8SM,
       nn.Operator_5_9SM,nn.Operator_3_9SM,nn.Motorist_6_9SM,nn.Mashinist_5_9SM,nn.Voditel_9SM,
       nn.Operator_5_10SM,nn.Operator_3_10SM,nn.Motorist_6_10SM,nn.Mashinist_5_10SM,nn.Voditel_10SM,
       nn.Operator_5_11SM,nn.Operator_3_11SM,nn.Motorist_6_11SM,nn.Mashinist_5_11SM,nn.Voditel_11SM,
       nn.Operator_5_12SM,nn.Operator_3_12SM,nn.Motorist_6_12SM,nn.Mashinist_5_12SM,nn.Voditel_12SM,
       nn.Operator_5_13SM,nn.Operator_3_13SM,nn.Motorist_6_13SM,nn.Mashinist_5_13SM,nn.Voditel_13SM,
       nn.Operator_5_14SM,nn.Operator_3_14SM,nn.Motorist_6_14SM,nn.Mashinist_5_14SM,nn.Voditel_14SM,
       nn.Operator_5_15SM,nn.Operator_3_15SM,nn.Motorist_6_15SM,nn.Mashinist_5_15SM,nn.Voditel_15SM,
       nn.Operator_5_16SM,nn.Operator_3_16SM,nn.Motorist_6_16SM,nn.Mashinist_5_16SM,nn.Voditel_16SM,
       nn.Operator_5_17SM,nn.Operator_3_17SM,nn.Motorist_6_17SM,nn.Mashinist_5_17SM,nn.Voditel_17SM,
       nn.Operator_5_18SM,nn.Operator_3_18SM,nn.Motorist_6_18SM,nn.Mashinist_5_18SM,nn.Voditel_18SM,
       nn.Operator_5_19SM,nn.Operator_3_19SM,nn.Motorist_6_19SM,nn.Mashinist_5_19SM,nn.Voditel_19SM,
       nn.Operator_5_20SM,nn.Operator_3_20SM,nn.Motorist_6_20SM,nn.Mashinist_5_20SM,nn.Voditel_20SM,
       round(nn.Norma_chel_ch,2) as Norma_chel_ch,
       round(nn.Fact_chel_ch,2) as Fact_chel_ch
from (
select row_number() over() as rn,
	   d.kust as kust,
	   d.skv as skv,
	   m.nm as mest,
	   (select (select nm from hos.org_stru o where o.id = znval) from hos.znparams zn where zn.defnar = d.key and zn.parcode = 5) as NGDU,
	   o.nm as zakazchik,
	   (with recursive rec as (
	  		select t.* from public.tematic_razdel t where t.id = d.razdel
	  		union
		   select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242
	   )
	   select (case when rec.id = 1018281  then 'Р¦Рђ-320'
			   		when rec.id = 1019237  then 'KENWORTH'
			   		when rec.id = 1019540  then 'MAN'
			   		when rec.id = 1019385  then 'РЈРќРџ2'
			   		when rec.id = 1018865  then 'РЈРќР¦'
			   		else '' end) from rec where parent_id = 1000242) as Tehnika,
	   (select valstr from hos.znparams zn where zn.defnar = d.key and zn.parcode = 10000589) as Gos_Nomer,
	   z.nm as ceh,
	   to_char(dv.begdate, 'dd.mm.yyyy HH24:MI') as date_b,
	   to_char(dv.enddate, 'dd.mm.yyyy HH24:MI') as date_n,
	   (select t.nm from public.tematic_razdel t where t.id = d.razdel) as Vid_HOS,
	   mt.name as Metod_HOS,
	   mt.group_meth as Group_Metod_HOS,
	   (select val from hos.znparams zn where zn.defnar = d.key and zn.parcode = 10000595) as Kolich_PZR,
	   (case when v.priznak = 1 then 'РџРѕРґС…РѕРґРЅС‹Рµ С‚РµС…РЅРѕР»РѕРіРёРё' when v.priznak = 2 then 'Р‘РµСЃРїРѕРґС…РѕРґРЅС‹Рµ С‚РµС…РЅРѕР»РѕРіРёРё' else null end) as Priznak,
	   (select znd.znach from hos.znparams zn join public.zn_dparam znd on zn.znval = znd.key where zn.defnar = d.key and zn.parcode = 1874) as Paker,
	   (select sum(vp.znach_v) from hos.vipolnenie_param vp join hos.vipolnenie_oper vo on vp.vip_key = vo.key where vo.narkey = d.key and parcode = 10000135) as V_reag_kanistr,
	   (select sum(vp.znach_v) from hos.vipolnenie_param vp join hos.vipolnenie_oper vo on vp.vip_key = vo.key where vo.narkey = d.key and parcode = 10000176) as V_reag_emk,
	   (select sum(vp.znach_v) from hos.vipolnenie_param vp join hos.vipolnenie_oper vo on vp.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and parcode in (10000215,10000256,10000609,10000613,10000617) and o.typerab = 1007) as V_rastvor,
	   (select sum(vp.znach_v) from hos.vipolnenie_param vp join hos.vipolnenie_oper vo on vp.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and parcode in (10000215) and o.typerab = 1008) as V_zhidkost,
	   (select sum(vp.znach_v) from hos.vipolnenie_param vp join hos.vipolnenie_oper vo on vp.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and ((parcode in (10000215,10000256,10000609,10000613,10000617) and o.typerab = 1007) or (parcode in (10000215) and o.typerab = 1008)))  as V_rast_zhidk,
	   (select coalesce(zn.val,0) from hos.znparams zn where zn.defnar = d.key and zn.parcode = 10000591) as Plan_Priemist,
	   (select coalesce(zn.val,0) from hos.znparams zn where zn.defnar = d.key and zn.parcode = 10000469) as Fakt_Priemist,
	   (select coalesce(zn.val,0) from hos.znparams zn where zn.defnar = d.key and zn.parcode = 10000471) as Fakt_Priemist_after,
	   (select round(coalesce(sum(zn.val),0)/2,0) from hos.znparams zn where zn.defnar = d.key and zn.parcode in (10000469,10000471)) as Fakt_Priemist_avg,
	   (select sum(vp.znach_v) from hos.vipolnenie_param vp join hos.vipolnenie_oper vo on vp.vip_key = vo.key where vo.narkey = d.key and parcode in (10000349, 10000448, 10000389, 10000349)) as Pereezd_km,
	   (select sum(vp.znach_v) from hos.vipolnenie_param vp join hos.vipolnenie_oper vo on vp.vip_key = vo.key where vo.narkey = d.key and parcode = 10000448) as Pereezd_bad_km,
	   (select sum(vp.znach_v) from hos.vipolnenie_param vp join hos.vipolnenie_oper vo on vp.vip_key = vo.key where vo.narkey = d.key and parcode = 10000389) as Pereezd_good_km,
	   (select sum(vp.znach_v) from hos.vipolnenie_param vp join hos.vipolnenie_oper vo on vp.vip_key = vo.key where vo.narkey = d.key and parcode = 10000349) as Pereezd_best_km,
	   (select sum(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key where vo.narkey = d.key and vn.prnum = 1) as Norma_Vip,
	   (select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1) as Fakt,
	   (select max(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1004) as PZR_na_baze_norma,
	   (select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1004) as PZR_na_baze_fakt,
	   (select max(vn.norma_v)/nullif(sum(vn.fact),0)*100 from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1004) as PZR_na_baze_Uroven_Vip,
	   (select max(vn.norma_v) + hos.getprostoi_prigkomp(d.key) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1010) as PrigKomp_norma,
	   (select sum(vn.fact) + hos.getprostoi_prigkomp(d.key) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1010) as PrigKomp_fakt,
	   (select (max(vn.norma_v) + hos.getprostoi_prigkomp(d.key))/nullif((sum(vn.fact) + hos.getprostoi_prigkomp(d.key)),0)*100 from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1010) as PrigKomp_Uroven_Vip,
	   (select max(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1011) as OzhReakKomp_norma,
	   (select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1011) as OzhReakKomp_fakt,
	   (select max(vn.norma_v)/nullif(sum(vn.fact),0)*100 from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1011) as OzhReakKomp_Uroven_Vip,
	   (select sum(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1009) as Proezd_norma,
	   (select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1009) as Proezd_fakt,
	   (select sum(vn.norma_v)/nullif(sum(vn.fact),0)*100 from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1009) as Proezd_Uroven_Vip,
	   (select max(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1006) as PR_na_skv_norma,
	   (select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1006) as PR_na_skv_fakt,
	   (select max(vn.norma_v)/nullif(sum(vn.fact),0)*100 from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1006) as PR_na_skv_Uroven_Vip,
	   (select max(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1005) as ZR_na_skv_norma,
	   (select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1005) as ZR_na_skv_fakt,
	   (select max(vn.norma_v)/nullif(sum(vn.fact),0)*100 from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1005) as ZR_na_skv_Uroven_Vip,
	   (select sum(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1007) as Prodavka_norma,
	   (select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1007) as Prodavka_fakt,
	   (select sum(vn.norma_v)/nullif(sum(vn.fact),0)*100 from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1007) as Prodavka_Uroven_Vip,
	   (select sum(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1008) as Prodavka_vodoi_norma,
	   (select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1008) as Prodavka_vodoi_fakt,
	   (select sum(vn.norma_v)/nullif(sum(vn.fact),0)*100 from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1008) as Prodavka_vodoi_Uroven_Vip,
	   (select sum(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key) as Poteri_Rab_Vrem,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=1) as Meteo,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=2) as Proparka,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=27) as Proparka_spec_tech,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=10) as OzhPPUSkvOb,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=39) as OzhPPUSpecTech,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=3) as AZSiATZ,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=4) as Chistka,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=5) as Tech_Dezh,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=19) as ProdavkaRostDav,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=45) as PerehodSNasosaSIzmRezh,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=46) as SoglParSIzmPokZak,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=31) as OzhSharov,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=32) as NaborTechZhidk,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=24) as PrigKomp,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=35) as UstrTechiLine,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=38) as OzhNachObrPoZayavke,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=47) as InstCDNGOpz,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=6) as OzhOPZ,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=20) as PogrRazgrSNPH,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=25) as OzhPoluchHimReagSNPH,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=48) as PogrRazgrBPTOIKO,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=7) as OzhZavozHim,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=8) as OzhZakazchik,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=9) as OzhPodgSkv,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=49) as OzhGotovPutei,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=11) as InstCDNGMun,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=12) as SoglParamZakaz,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=50) as SoglParamPodr,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=40) as PerehodSNasosaSNesootv,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=13) as DostavkaTranspPoVineZakaz,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=51) as DostavkaTranspPoVinePodr,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=54) as DostavkaTranspPoVineSubPodr,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=33) as OzhPolZad,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=43) as OzhPodtvZayavki,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=34) as OzhDokumZakaz,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=52) as OzhDokumPodr,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=14) as ProchieProstoiPodr,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=53) as ProchieProstoiZakaz,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=15) as RemKust,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=29) as RemElectro,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=16) as OzhZapChast,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=28) as OzhKIPiA,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=26) as PerehodSNasosaSVih,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=36) as ProdavkaRemontObor,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=37) as TechObsluzh,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=44) as EzhesmTechObsluzh,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=18) as HolProezdPoVineZakazChas,
           (select max(pp.znach) from hos.prostoi_param pp where pp.keynar=d.key and pp.param=41) as HolProezdPoVineSNPHChas,
	   d.shiftcnt as Kolvo_Smen,
	   (select sum(volume) from (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 group by shift) as v) as Operator_5,
	   (select sum(volume) from (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 group by shift) as v) as Operator_3,
	   (select sum(volume) from (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 group by shift) as v) as Mashinist_5,
	   (select sum(volume) from (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 group by shift) as v) as Motorist_6,
	    null as Voditel,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 1) as Operator_5_1SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 1) as Operator_3_1SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 1) as Mashinist_5_1SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 1) as Motorist_6_1SM,
	    null as Voditel_1SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 2) as Operator_5_2SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 2) as Operator_3_2SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 2) as Mashinist_5_2SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 2) as Motorist_6_2SM,
	    null as Voditel_2SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 3) as Operator_5_3SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 3) as Operator_3_3SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 3) as Mashinist_5_3SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 3) as Motorist_6_3SM,
	    null as Voditel_3SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 4) as Operator_5_4SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 4) as Operator_3_4SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 4) as Mashinist_5_4SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 4) as Motorist_6_4SM,
	    null as Voditel_4SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 5) as Operator_5_5SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 5) as Operator_3_5SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 5) as Mashinist_5_5SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 5) as Motorist_6_5SM,
		null as Voditel_5SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 6) as Operator_5_6SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 6) as Operator_3_6SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 6) as Mashinist_5_6SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 6) as Motorist_6_6SM,
		null as Voditel_6SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 7) as Operator_5_7SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 7) as Operator_3_7SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 7) as Mashinist_5_7SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 7) as Motorist_6_7SM,
		null as Voditel_7SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 8) as Operator_5_8SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 8) as Operator_3_8SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 8) as Mashinist_5_8SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 8) as Motorist_6_8SM,
		null as Voditel_8SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 9) as Operator_5_9SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 9) as Operator_3_9SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 9) as Mashinist_5_9SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 9) as Motorist_6_9SM,
		null as Voditel_9SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 10) as Operator_5_10SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 10) as Operator_3_10SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 10) as Mashinist_5_10SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 10) as Motorist_6_10SM,
		null as Voditel_10SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 11) as Operator_5_11SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 11) as Operator_3_11SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 11) as Mashinist_5_11SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 11) as Motorist_6_11SM,
		null as Voditel_11SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 12) as Operator_5_12SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 12) as Operator_3_12SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 12) as Mashinist_5_12SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 12) as Motorist_6_12SM,
		null as Voditel_12SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 13) as Operator_5_13SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 13) as Operator_3_13SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 13) as Mashinist_5_13SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 13) as Motorist_6_13SM,
		null as Voditel_13SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 14) as Operator_5_14SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 14) as Operator_3_14SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 14) as Mashinist_5_14SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 14) as Motorist_6_14SM,
		null as Voditel_14SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 15) as Operator_5_15SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 15) as Operator_3_15SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 15) as Mashinist_5_15SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 15) as Motorist_6_15SM,
		null as Voditel_15SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 16) as Operator_5_16SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 16) as Operator_3_16SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 16) as Mashinist_5_16SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 16) as Motorist_6_16SM,
		null as Voditel_16SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 17) as Operator_5_17SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 17) as Operator_3_17SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 17) as Mashinist_5_17SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 17) as Motorist_6_17SM,
		null as Voditel_17SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 18) as Operator_5_18SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 18) as Operator_3_18SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 18) as Mashinist_5_18SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 18) as Motorist_6_18SM,
		null as Voditel_18SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 19) as Operator_5_19SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 19) as Operator_3_19SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 19) as Mashinist_5_19SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 19) as Motorist_6_19SM,
		null as Voditel_19SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001584 and shift = 20) as Operator_5_20SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001585 and shift = 20) as Operator_3_20SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001586 and shift = 20) as Mashinist_5_20SM,
	   (select max(volume_v) as volume from hos.vipolnenie_cks vc join hos.vipolnenie_oper vo on vc.vip_key = vo.key where vo.narkey = d.key and common_spr_id = 10001587 and shift = 20) as Motorist_6_20SM,
		null as Voditel_20SM,
	   (select sum(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 2) as Norma_chel_ch,
	   (select sum(vn.fact * (select sum(volume_v) from hos.vipolnenie_cks vc where vc.vip_key = vn.vip_key and vc.shift = vn.shift)) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1) as Fact_chel_ch,
	   d.key
	   from hos.defnar d
	   		left join hos.him_msr m on d.msr = m.key
			left join hos.org_stru o on o.id = d.zakazchik
			left join hos.zeh z on z.key = d.zeh
			join hos.defnarvip dv on d.key = dv.narkey
			join hos.vid_him_obrab v on v.id = d.razdel
			join (with recursive rec as (select m.*, null::text as group_meth from hos.him_metod_obrab m where parent_key is null union select m.*, rec.name::text from hos.him_metod_obrab m join rec on rec.key=m.parent_key) select * from rec) mt on mt.key = v.metod_id
	   where closed = 1
   and d.spr_org_id= :pdr
   and date_trunc('month', dv.enddate) >= date_trunc('month', to_date(:dateBegin, 'DD.MM.YYYY'))
   and date_trunc('month', dv.enddate) <= date_trunc('month', to_date(:dateEnd, 'DD.MM.YYYY'))

   and (:zeh = 0 or z.key = :zeh)
   and (:tip = 0 or (with recursive rec as (select t.* from public.tematic_razdel t where t.id = d.razdel union select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242 )
                        select rec.id from rec where parent_id = 1000242) =:tip)
 ) nn order by date_b) tbl
