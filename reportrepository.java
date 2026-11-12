package com.example.core.repository;

import com.example.core.DTO.*;
import com.example.core.service.SqlQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReportRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SqlQueryService sqlQueryService;

    public List<DowntimeReportDto> findDowntimeReport(Integer zeh, Integer type, String dateBegin, String dateEnd) throws IOException {
        String sql = sqlQueryService.getQuery("DowntimeReportSql");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ceh", zeh)
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(DowntimeReportDto.class));
    }

    public List<NormZReportDto> findNormReport(Integer zeh, Integer type, Integer brigade, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql = sqlQueryService.getQuery("NormReportSql");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("zeh", zeh)
                .addValue("tip", type)
                .addValue("pdr", 200)
                .addValue("key_zveno", brigade)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(NormZReportDto.class));
    }

    public List<Object[]> findHimReport(Integer zeh, Integer type, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql = sqlQueryService.getQuery("HimReportSql");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("zeh", zeh)
                .addValue("tip", type)
                .addValue("pdr", 200)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd);
        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            int columnCount = rs.getMetaData().getColumnCount();
            Object[] row = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                row[i] = rs.getObject(i + 1);
            }
            return row;
        });
    }
    
    
    public List<MethodDto> getMethod(Integer type, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                " select distinct o.key as id, o.name as name" +
                "      from  hos.defnar d " +
                "            join hos.defnarvip dv on d.key = dv.narkey" +
                "    join hos.vid_him_obrab v on v.id = d.razdel" +
                "    join hos.him_metod_obrab o on o.key = v.metod_id" +
                "      where closed = 1" +
                "    and dv.enddate between :dateBegin and :dateEnd" +
                        "    and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "    and (:zeh = 0 or d.zeh = :zeh)" +
                        " and d.razdel in (select id from rec)" ;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(MethodDto.class));
    }

    public List<ObrabDto> getObrab(Integer type, Integer method, Integer obrab, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql = 
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                "select row_number() over() row_num, key_obrab,name_obrab,kol_vo_skv," +
                "priem_120_300," +
                "priem_301_480," +
                "priem_481_660," +
                "priem_661_840," +
                "priem_841," +
                "priem_0_180," +
                "priem_181_300," +
                "priem_301_450," +
                "priem_451_600," +
                "priem_601_750," +
                "priem_751" +
                " from " +
                "(select v.id as key_obrab, (select tr.nm from public.tematic_razdel tr where tr.id = d.razdel) as name_obrab, count(d.key) as kol_vo_skv," +
                "sum(case when zn.val>=120 and zn.val<301 then 1 else 0 end) priem_120_300," +
                "sum(case when zn.val>=301 and zn.val<481 then 1 else 0 end) priem_301_480," +
                "sum(case when zn.val>=481 and zn.val<661 then 1 else 0 end) priem_481_660," +
                "sum(case when zn.val>=661 and zn.val<841 then 1 else 0 end) priem_661_840," +
                "sum(case when zn.val>=841 then 1 else 0 end) priem_841," +
                "sum(case when zn.val>=0 and zn.val<181 then 1 else 0 end) priem_0_180," +
                "sum(case when zn.val>=181 and zn.val<301 then 1 else 0 end) priem_181_300," +
                "sum(case when zn.val>=301 and zn.val<451 then 1 else 0 end) priem_301_450," +
                "sum(case when zn.val>=451 and zn.val<601 then 1 else 0 end) priem_451_600," +
                "sum(case when zn.val>=601 and zn.val<751 then 1 else 0 end) priem_601_750," +
                "sum(case when zn.val>=751 then 1 else 0 end) priem_751" +
                " from hos.defnar d" +
                "  join hos.defnarvip dv on d.key = dv.narkey" +
                "  join hos.znparams zn on zn.defnar = d.key" +
                "  join hos.vid_him_obrab v on v.id = d.razdel" +
                "  join hos.him_metod_obrab o on o.key = v.metod_id" +
                " where dv.enddate between :dateBegin and :dateEnd" +
                "    and (:pdr = 0 or d.spr_org_id = :pdr)" +
                "    and zn.parcode = 10000469" +
                "    and (:method = 0 or o.key = :method)" +
                "    and (:obrab = 0 or v.id = :obrab) " +
                "           and closed=1" +
                "    and (:zeh = 0 or d.zeh = :zeh)" +
                " and d.razdel in (select id from rec)" +
                " group by key_obrab, name_obrab) tb";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("method", method)
                .addValue("obrab", obrab)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(ObrabDto.class));
    }
    public Integer getPZR(Integer type, Integer method, Integer obrab, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        "select coalesce(sum(zn.val), 0) as kol_vo_skv" +
                        " from hos.defnar d" +
                        "  join hos.defnarvip dv on d.key = dv.narkey" +
                        "  join hos.znparams zn on zn.defnar = d.key" +
                        "  join hos.vid_him_obrab v on v.id = d.razdel" +
                        "  join hos.him_metod_obrab o on o.key = v.metod_id" +
                        " where dv.enddate between  :dateBegin and :dateEnd" +
                        "   and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "   and zn.parcode = 10000595" +
                        "   and (:method = 0 or o.key = :method)" +
                        "   and v.id = :obrab" +
                        "           and closed=1" +
                        "    and (:zeh = 0 or d.zeh = :zeh)" +
                        "   and d.razdel in (select id from rec)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("method", method)
                .addValue("obrab", obrab)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new SingleColumnRowMapper<>(Integer.class)).stream().findFirst().orElse(null);
    }

    public DurationDto getDuration(Integer type, Integer method, Integer obrab, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        "select     round(sum(norma)/sum(sred),2) as norma,sum(norma) norma_sum,sum(sred) kol_vo_sum," +
                        "           (case when sum(sred_f) is null then 0 when sum(sred_f) = 0 then 0 else round(sum(norma_f)/sum(sred_f), 2) end) as norma_f,sum(norma_f) fact_sum,sum(sred_f) kol_vo_fact_sum," +
                        "           round(sum(TrudoemkCHELF),2) as TrudoemkCHELF  ," +
                        "           round(sum(TrudoemkSKV),2) as TrudoemkSKV " +
                        "    " +
                        " from " +
                        "(select round(sum((case when vn.prnum = 1 then vn.norma_v else 0 end)),2) norma," +
                        "   round(sum((case when vn.prnum = 1 then vn.fact else 0 end)),2) norma_f," +
                        "   round(sum((case when vn.prnum = 2 then vn.norma_v else 0 end)),2) TrudoemkCHELF, " +
                        "   round(sum((case when vn.prnum = 2 then vn.norma_v else 0 end)),2) TrudoemkSKV, " +
                        "   (case when round(sum((case when vn.prnum = 1 then vn.norma_v else 0 end)),2) = 0 then 0 else 1 end) sred," +
                        "   (case when round(sum((case when vn.prnum = 1 then vn.fact else 0 end)),2) = 0 then 0 else 1 end) sred_f," +
                        "   d.key" +
                        " from hos.defnar d" +
                        "  join hos.defnarvip dv on d.key = dv.narkey" +
                        "  join hos.vid_him_obrab v on v.id = d.razdel" +
                        "  join hos.him_metod_obrab o on o.key = v.metod_id" +
                        " join hos.vipolnenie_oper vo on vo.narkey = dv.narkey" +
                        " join hos.vipolnenie_norm vn on vn.vip_key = vo.key" +
                        " where dv.enddate between  :dateBegin and :dateEnd" +
                        "   and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "   and (:method = 0 or o.key = :method)" +
                        "   and v.id = :obrab" +
                        "           and closed=1" +
                        "   and d.razdel in (select id from rec)" +
                        "    and (:zeh = 0 or d.zeh = :zeh)" +
                        "   group by d.key) tb";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("method", method)
                .addValue("obrab", obrab)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(DurationDto.class)).stream().findFirst().orElse(null);
    }

    public List<ObrabDto> getObrabTotal(Integer type, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        " select kol_vo_skv," +
                        "priem_120_300," +
                        "priem_301_480," +
                        "priem_481_660," +
                        "priem_661_840," +
                        "priem_841," +
                        "priem_0_180," +
                        "priem_181_300," +
                        "priem_301_450," +
                        "priem_451_600," +
                        "priem_601_750," +
                        "priem_751" +
                        " from " +
                        " (select count(d.key) as kol_vo_skv," +
                        "sum(case when zn.val>=120 and zn.val<301 then 1 else 0 end) priem_120_300," +
                        "sum(case when zn.val>=301 and zn.val<481 then 1 else 0 end) priem_301_480," +
                        "sum(case when zn.val>=481 and zn.val<661 then 1 else 0 end) priem_481_660," +
                        "sum(case when zn.val>=661 and zn.val<841 then 1 else 0 end) priem_661_840," +
                        "sum(case when zn.val>=841 then 1 else 0 end) priem_841," +
                        "sum(case when zn.val>=0 and zn.val<181 then 1 else 0 end) priem_0_180," +
                        "sum(case when zn.val>=181 and zn.val<301 then 1 else 0 end) priem_181_300," +
                        "sum(case when zn.val>=301 and zn.val<451 then 1 else 0 end) priem_301_450," +
                        "sum(case when zn.val>=451 and zn.val<601 then 1 else 0 end) priem_451_600," +
                        "sum(case when zn.val>=601 and zn.val<751 then 1 else 0 end) priem_601_750," +
                        "sum(case when zn.val>=751 then 1 else 0 end) priem_751" +
                        " from hos.defnar d" +
                        "  join hos.defnarvip dv on d.key = dv.narkey" +
                        "  join hos.znparams zn on zn.defnar = d.key" +
                        "  join hos.vid_him_obrab v on v.id = d.razdel" +
                        "  join hos.him_metod_obrab o on o.key = v.metod_id" +
                        " where dv.enddate between :dateBegin and :dateEnd" +
                        "    and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "    and zn.parcode = 10000469" +
                        "           and closed=1" +
                        " and d.razdel in (select id from rec)" +
                        "    and (:zeh = 0 or d.zeh = :zeh)" +
                        ") tb";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(ObrabDto.class));
    }
    public Integer getPZRTotal(Integer type, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        " select coalesce(sum(zn.val), 0) as kol_vo_skv" +
                        " from hos.defnar d" +
                        "  join hos.defnarvip dv on d.key = dv.narkey" +
                        "  join hos.znparams zn on zn.defnar = d.key" +
                        "  join hos.vid_him_obrab v on v.id = d.razdel" +
                        "  join hos.him_metod_obrab o on o.key = v.metod_id" +
                        " where dv.enddate between  :dateBegin and :dateEnd" +
                        "   and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "   and zn.parcode = 10000595" +
                        "           and closed=1" +
                        "    and (:zeh = 0 or d.zeh = :zeh)" +
                        "   and d.razdel in (select id from rec)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new SingleColumnRowMapper<>(Integer.class)).stream().findFirst().orElse(null);
    }
    public DurationDto getDurationTotal(Integer type, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        "select     round(sum(norma)/sum(sred),2) as norma,sum(norma) norma_sum,sum(sred) kol_vo_sum," +
                        "           (case when sum(sred_f) is null then 0 when sum(sred_f) = 0 then 0 else round(sum(norma_f)/sum(sred_f), 2) end) as norma_f,sum(norma_f) fact_sum,sum(sred_f) kol_vo_fact_sum," +
                        "           round(sum(TrudoemkCHELF),2) as TrudoemkCHELF  ," +
                        "           round(sum(TrudoemkSKV),2) as TrudoemkSKV " +
                        "    " +
                        " from " +
                        "(select round(sum((case when vn.prnum = 1 then vn.norma_v else 0 end)),2) norma," +
                        "   round(sum((case when vn.prnum = 1 then vn.fact else 0 end)),2) norma_f," +
                        "   round(sum((case when vn.prnum = 2 then vn.norma_v else 0 end)),2) TrudoemkCHELF, " +
                        "   round(sum((case when vn.prnum = 2 then vn.norma_v else 0 end)),2) TrudoemkSKV, " +
                        "   (case when round(sum((case when vn.prnum = 1 then vn.norma_v else 0 end)),2) = 0 then 0 else 1 end) sred," +
                        "   (case when round(sum((case when vn.prnum = 1 then vn.fact else 0 end)),2) = 0 then 0 else 1 end) sred_f," +
                        "   d.key" +
                        " from hos.defnar d" +
                        "  join hos.defnarvip dv on d.key = dv.narkey" +
                        "  join hos.vid_him_obrab v on v.id = d.razdel" +
                        "  join hos.him_metod_obrab o on o.key = v.metod_id" +
                        " join hos.vipolnenie_oper vo on vo.narkey = dv.narkey" +
                        " join hos.vipolnenie_norm vn on vn.vip_key = vo.key" +
                        " where dv.enddate between  :dateBegin and :dateEnd" +
                        "   and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "           and closed=1" +
                        "   and d.razdel in (select id from rec)" +
                        "    and (:zeh = 0 or d.zeh = :zeh)" +
                        "   group by d.key) tb";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(DurationDto.class)).stream().findFirst().orElse(null);
    }

    public List<SelectDto> getZeh(Integer type, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        " select 0 as id, 'Всего по управлению' as nm" +
                        " union " +
                        " select distinct z.key as id, z.nm as nm " +
                        "from hos.defnar d " +
                        "   join hos.defnarvip dv on d.key = dv.narkey " +
                        "  join hos.zeh z on z.key = d.zeh " +
                        " where dv.enddate between :dateBegin and :dateEnd " +
                        "  and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "  and closed=1 " +
                        "  and d.razdel in (select id from rec)  " +
                        "order by id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("pdr", 200);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(SelectDto.class));
    }

    public NarInfo getNarInfo(Long nar) {
        String sql =
                "select d.key as nar, z.nm as zeh, " +
                        "    (select t.nm from public.tematic_razdel t where t.id = d.razdel) as vid_HOS, " +
                        "    (select name from hos.him_metod_obrab where key = v.metod_id) || ': ' || (with recursive tt as ( " +
                        "                      select '' as full_path,  aa.id, aa.parent_id, aa.nm from public.tematic_razdel aa where aa.id = 1019387 " +
                        "                      union  " +
                        "                      select  (aa.nm||'. '|| tt.full_path)::text as full_path, aa.id, aa.parent_id, aa.nm from public.tematic_razdel aa, tt where tt.parent_id = aa.id and aa.id <> 1000242 " +
                        "                     ) " +
                        "  select full_path from tt where parent_id = 1000242 " +
                        "  ) as metodobrab, " +
                        "  (select coalesce(round(zn.val,0),0) from hos.znparams zn where zn.defnar = d.key and zn.parcode = 10000469) as Fakt_Priemist, " +
                        "  (select sum(coalesce(norma_v,0)) from hos.vipolnenie_norm vn where vn.vip_key in (select key from hos.vipolnenie_oper vo where vo.narkey = d.key) and prnum = 1) as Norma_V, " +
                        "  (select sum(coalesce(fact, 0)) from hos.vipolnenie_norm vn where vn.vip_key in (select key from hos.vipolnenie_oper vo where vo.narkey = d.key) and prnum = 1) as Norma_F, " +
                        "  (select sum(coalesce(norma_v, 0)) from hos.vipolnenie_norm vn where vn.vip_key in (select key from hos.vipolnenie_oper vo where vo.narkey = d.key) and prnum = 2) as Norma_V_Chel, " +
                        "  (select sum(coalesce(fact, 0) *  coalesce((select sum(volume_v) from hos.vipolnenie_cks vc where vc.vip_key = vn.vip_key and vc.shift = vn.shift), 0))  " +
                        "    from hos.vipolnenie_norm vn where vn.vip_key in (select key from hos.vipolnenie_oper vo where vo.narkey = d.key) and prnum = 1) as Norma_F_Chel, " +
                        "  (select val from hos.znparams zn where zn.defnar = d.key and zn.parcode = 10000595) as KolPZR, " +
                        "  (select coalesce(zn.val,0) from hos.znparams zn where zn.defnar = d.key and zn.parcode = 10000469) as priem, " +
                        "  o.nm customer, " +
                        "  (select nbrg from hos.him_brg hb where hb.key = d.nbrg) as brg, " +
                        "  d.kust as kust,  " +
                        "  d.skv as skv, " +
                        "  m.nm as mest, " +
                        "  dv.begdate, " +
                        "  dv.enddate " +
                        "        from hos.defnar d  " +
                        "        join hos.vid_him_obrab v on d.razdel = v.id " +
                        "        left join hos.zeh z on z.key = d.zeh " +
                        "        left join hos.org_stru o on o.id = d.zakazchik " +
                        "        left join hos.him_msr m on d.msr = m.key " +
                        "        join hos.defnarvip dv on d.key = dv.narkey " +
                        "        where d.key = :nar";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nar", nar);
        return namedParameterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(NarInfo.class));
    }

    public List<NarBody> getNarBody(Long nar) {
        String sql =
                "with a as ( " +
                        "     select distinct vo.key, hos.vipolnenie_getoperist(vo.key) istnorm,  " +
                        "     s.nm,  " +
                        "     (select c.nm from public.common_spr c, public.operlife o where c.id = o.edizm_com and o.id = vo.operlifeid) ed_izm, " +
                        "     max(vn.norma_str) as norma_str, " +
                        "     coalesce(string_agg((case when vn.prnum=1 then (select string_agg((case when znach = 0 then '0' else replace(trim_scale(round(znach,2))::text, '.', ',') end),'/') from hos.vipolnenie_param vp where vp.vip_key = vo.key and vp.shift = vn.shift and  " +
                        "                        vp.parcode in (10000215,10000256,10000609,10000613,10000617,10000135,10000176,10000448,10000389,10000349,10000135,10000176)) else null end), ';'), '1') as ob, " +
                        "     (select sum(volume_v)::text from hos.vipolnenie_cks vc where vc.vip_key = vo.key and vc.shift = 1) as cks, " +
                        "     round(sum((case when vn.prnum=1 then vn.norma else null::numeric end)),2) norma, " +
                        "     round(sum((case when vn.prnum=2 then vn.norma else null::numeric end)),2) normaob, " +
                        "     coalesce(string_agg((case when vn.prnum=1 then (select string_agg((case when znach = 0 then '0' else replace(trim_scale(round(znach,2))::text, '.', ',') end),'/') from hos.vipolnenie_param vp where vp.vip_key = vo.key and vp.shift = vn.shift and  " +
                        "                        vp.parcode in (10000215,10000256,10000609,10000613,10000617,10000135,10000176,10000448,10000389,10000349,10000135,10000176)) else null end), ';'), '1') as ob_v, " +
                        "     string_agg((case when vn.prnum=1 then (select sum(volume_v)::text from hos.vipolnenie_cks vc where vc.vip_key = vo.key and vc.shift = vn.shift) else null end), '/') as cks_v, " +
                        "     round(sum((case when vn.prnum=1 then vn.norma_v else null::numeric end)),2) norma_v, " +
                        "     round(sum((case when vn.prnum=2 then vn.norma_v else null::numeric end)),2) normaob_v, " +
                        "     coalesce(round(sum((case when vn.prnum=1 then vn.fact else null::numeric end)),2), 0) fact, " +
                        "     coalesce(round(sum((case when vn.prnum=1 then vn.fact * (select sum(volume_v) from hos.vipolnenie_cks vc where vc.vip_key = vo.key and vc.shift = vn.shift) else null::numeric end)),2),0) factob " +
                        "     from hos.defnar d " +
                        "        join hos.vipolnenie_oper vo on d.key = vo.narkey   " +
                        "        join hos.vipolnenie_norm vn on vo.key = vn.vip_key " +
                        "        left join public.spr_oper s on vo.oper = s.key " +
                        "        where d.key = :nar " +
                        "        group by vo.key, s.nm, vo.operlifeid " +
                        "      )  " +
                        "      select * from a " +
                        "      union " +
                        "      select null, null, 'Итог', null, null, null, null, sum(norma), sum(normaob), null, null, sum(norma_v), sum(normaob_v), sum(fact), sum(factob) from a order by key ";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nar", nar);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(NarBody.class));
    }

    public List<CksBody> getCksBody(Long nar) {
        String sql = "select null as tehnm,   " +
                "   (select nm from public.common_spr s where s.id = vc.common_spr_id) as nm,   " +
                "   max(volume_v) as chisl,   " +
                "   round(sum((case when vn.prnum=2 and vc.shift = 1 then vn.norma_v else null::numeric end)),2) norma,  " +
                "   round(sum((case when vn.prnum=1 and vc.shift = 1  then vn.fact else null::numeric end)),2) fakt  " +
                "      from hos.defnar d   " +
                "     join hos.vipolnenie_oper vo on d.key = vo.narkey  " +
                "     join hos.vipolnenie_cks vc on vo.key = vc.vip_key  " +
                "     join hos.vipolnenie_norm vn on vo.key = vn.vip_key  " +
                "     where d.key = :nar   " +
                "     group by vc.common_spr_id  ";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nar", nar);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(CksBody.class));
    }

    public List<UserReportBody> getUsers() {
        String sql = "select distinct p.fio,  " +
                "u.dtout,   " +
                "u.active, " +
                "(case when u.active::text = '0' then 'Отключен' when u.active::text = '1' then 'Подключен' end)  " +
                "activenm, " +
                "u.note,  " +
                "u.ora_name username,  " +
                "u.dtenter as DateIn,   " +
                "r.nm User_Role,  " +
                "o.org_nm, " +
                "sd.nm doljn " +
                "from hos.karjera k, " +
                "hos.people p, " +
                "hos.users  u, " +
                "hos.spr_role r,   " +
                "hos.sprdoljnost sd, "+
                "(with recursive rec as (" +
                "select o.id, o.nm org_nm from hos.org_stru o where o.id = 0     " +
                "union     " +
                "select o.id, o.nm org_nm from hos.org_stru o, rec where rec.id = o.parent) " +
                "select * from rec) o   " +
                "where k.idpeople = p.id    " +
                "and k.org = o.id    " +
                "and p.id = u.people_id  " +
                "and u.role_id = r.role_id  " +
                "and k.doljnost = sd.key  " +
                "order by o.org_nm, p.fio ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(UserReportBody.class));
    }

    public List<SelectDto> getZehNew(Integer type, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385, 1019540, 1019237]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        " select 0 as id, '' as nm " +
                        " union " +
                        " select distinct z.key as id, z.nm as nm " +
                        "from hos.defnar d " +
                        "   join hos.defnarvip dv on d.key = dv.narkey " +
                        "  join hos.zeh z on z.key = d.zeh " +
                        " where dv.enddate between :dateBegin and :dateEnd " +
                        "  and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "  and closed=1 " +
                        "  and d.razdel in (select id from rec)  " +
                        "order by id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("pdr", 200);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(SelectDto.class));
    }


    public List<MethodDto> getMethodNew(Integer type, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385, 1019540, 1019237]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        " select distinct o.key as id, o.name as name, o.parent_key" +
                        "      from  hos.defnar d " +
                        "            join hos.defnarvip dv on d.key = dv.narkey" +
                        "    join hos.vid_him_obrab v on v.id = d.razdel" +
                        "    join hos.him_metod_obrab o on o.key = v.metod_id" +
                        "      where closed = 1" +
                        "    and dv.enddate between :dateBegin and :dateEnd" +
                        "    and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "    and (:zeh = 0 or d.zeh = :zeh)" +
                        " and d.razdel in (select id from rec) " +
                        " order by o.parent_key, o.key" ;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(MethodDto.class));
    }

    public List<ObrabDto> getObrabNew(Integer type, Integer method, String obrab, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385, 1019540, 1019237]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        "select row_number() over() row_num, key_obrab,name_obrab,kol_vo_skv," +
                        "priem_120_300," +
                        "priem_301_480," +
                        "priem_481_660," +
                        "priem_661_840," +
                        "priem_841," +
                        "priem_0_180," +
                        "priem_181_300," +
                        "priem_301_450," +
                        "priem_451_600," +
                        "priem_601_750," +
                        "priem_751" +
                        " from " +
                        "(select string_agg(distinct v.id::text, ', ') as key_obrab, (select tr.nm from public.tematic_razdel tr where tr.id = d.razdel) as name_obrab, count(d.key) as kol_vo_skv," +
                        "sum(case when zn.val<301 then 1 else 0 end) priem_120_300," +
                        "sum(case when zn.val>=301 and zn.val<481 then 1 else 0 end) priem_301_480," +
                        "sum(case when zn.val>=481 and zn.val<661 then 1 else 0 end) priem_481_660," +
                        "sum(case when zn.val>=661 and zn.val<841 then 1 else 0 end) priem_661_840," +
                        "sum(case when zn.val>=841 then 1 else 0 end) priem_841," +
                        "sum(case when zn.val>=0 and zn.val<181 then 1 else 0 end) priem_0_180," +
                        "sum(case when zn.val>=181 and zn.val<301 then 1 else 0 end) priem_181_300," +
                        "sum(case when zn.val>=301 and zn.val<451 then 1 else 0 end) priem_301_450," +
                        "sum(case when zn.val>=451 and zn.val<601 then 1 else 0 end) priem_451_600," +
                        "sum(case when zn.val>=601 and zn.val<751 then 1 else 0 end) priem_601_750," +
                        "sum(case when zn.val>=751 then 1 else 0 end) priem_751" +
                        " from hos.defnar d" +
                        "  join hos.defnarvip dv on d.key = dv.narkey" +
                        "  join hos.znparams zn on zn.defnar = d.key" +
                        "  join hos.vid_him_obrab v on v.id = d.razdel" +
                        "  join hos.him_metod_obrab o on o.key = v.metod_id" +
                        " where dv.enddate between :dateBegin and :dateEnd" +
                        "    and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "    and zn.parcode = 10000469" +
                        "    and (:method = 0 or o.key = :method)" +
                        "    and (:obrab is null or trim(:obrab) = '' or v.id = ANY(string_to_array(:obrab, ',')::integer[])) " +
                        "           and closed=1" +
                        "    and (:zeh = 0 or d.zeh = :zeh)" +
                        " and d.razdel in (select id from rec)" +
                        " group by name_obrab) tb";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("method", method)
                .addValue("obrab", obrab)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(ObrabDto.class));
    }

    public List<ObrabDto> getObrabTotalNew(Integer type, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385, 1019540, 1019237]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        " select kol_vo_skv," +
                        "priem_120_300," +
                        "priem_301_480," +
                        "priem_481_660," +
                        "priem_661_840," +
                        "priem_841," +
                        "priem_0_180," +
                        "priem_181_300," +
                        "priem_301_450," +
                        "priem_451_600," +
                        "priem_601_750," +
                        "priem_751" +
                        " from " +
                        " (select count(d.key) as kol_vo_skv," +
                        "sum(case when zn.val<301 then 1 else 0 end) priem_120_300," +
                        "sum(case when zn.val>=301 and zn.val<481 then 1 else 0 end) priem_301_480," +
                        "sum(case when zn.val>=481 and zn.val<661 then 1 else 0 end) priem_481_660," +
                        "sum(case when zn.val>=661 and zn.val<841 then 1 else 0 end) priem_661_840," +
                        "sum(case when zn.val>=841 then 1 else 0 end) priem_841," +
                        "sum(case when zn.val>=0 and zn.val<181 then 1 else 0 end) priem_0_180," +
                        "sum(case when zn.val>=181 and zn.val<301 then 1 else 0 end) priem_181_300," +
                        "sum(case when zn.val>=301 and zn.val<451 then 1 else 0 end) priem_301_450," +
                        "sum(case when zn.val>=451 and zn.val<601 then 1 else 0 end) priem_451_600," +
                        "sum(case when zn.val>=601 and zn.val<751 then 1 else 0 end) priem_601_750," +
                        "sum(case when zn.val>=751 then 1 else 0 end) priem_751" +
                        " from hos.defnar d" +
                        "  join hos.defnarvip dv on d.key = dv.narkey" +
                        "  join hos.znparams zn on zn.defnar = d.key" +
                        "  join hos.vid_him_obrab v on v.id = d.razdel" +
                        "  join hos.him_metod_obrab o on o.key = v.metod_id" +
                        " where dv.enddate between :dateBegin and :dateEnd" +
                        "    and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "    and zn.parcode = 10000469" +
                        "           and closed=1" +
                        " and d.razdel in (select id from rec)" +
                        "    and (:zeh = 0 or d.zeh = :zeh)" +
                        ") tb";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(ObrabDto.class));
    }

    public PriemDto getPriem(Integer type, Integer method, String obrab, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385, 1019540, 1019237]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        "select round(avg(case when zn.parcode = 10000591 then coalesce(zn.val, 0) else null end)) as plan, " +
                        "round(avg(case when zn.parcode in (10000469,10000471) then coalesce(zn.val, 0) else null end)) as avg " +
                        " from hos.defnar d" +
                        "  join hos.defnarvip dv on d.key = dv.narkey" +
                        "  join hos.znparams zn on zn.defnar = d.key" +
                        "  join hos.vid_him_obrab v on v.id = d.razdel" +
                        "  join hos.him_metod_obrab o on o.key = v.metod_id" +
                        " where dv.enddate between  :dateBegin and :dateEnd" +
                        "   and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "   and (:method = 0 or o.key = :method)" +
                        "   and zn.parcode in (10000469,10000471,10000591)" +
                        "   and v.id = any(string_to_array(:obrab, ',')::integer[])" +
                        "           and closed=1" +
                        "    and (:zeh = 0 or d.zeh = :zeh)" +
                        "   and d.razdel in (select id from rec)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("method", method)
                .addValue("obrab", obrab)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params,  new BeanPropertyRowMapper<>(PriemDto.class)).stream().findFirst().orElse(null);
    }

    public PriemDto getPriemTotal(Integer type, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385, 1019540, 1019237]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        "select round(avg(case when zn.parcode = 10000591 then coalesce(zn.val, 0) else null end)) as plan, " +
                        "round(avg(case when zn.parcode in (10000469,10000471) then coalesce(zn.val, 0) else null end)) as avg " +
                        " from hos.defnar d" +
                        "  join hos.defnarvip dv on d.key = dv.narkey" +
                        "  join hos.znparams zn on zn.defnar = d.key" +
                        "  join hos.vid_him_obrab v on v.id = d.razdel" +
                        "  join hos.him_metod_obrab o on o.key = v.metod_id" +
                        " where dv.enddate between  :dateBegin and :dateEnd" +
                        "   and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "   and zn.parcode in (10000469,10000471,10000591)" +
                        "           and closed=1" +
                        "    and (:zeh = 0 or d.zeh = :zeh)" +
                        "   and d.razdel in (select id from rec)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params,  new BeanPropertyRowMapper<>(PriemDto.class)).stream().findFirst().orElse(null);
    }

    public PZRDto getPZRNew(Integer type, Integer method, String obrab, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385, 1019540, 1019237]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        "select sum(coalesce(zn.val, 0)) as skv, " +
                        " sum((select case when max(vn.norma_v) > 0 then 1 else 0 end from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1004)) as base " +
                        " from hos.defnar d" +
                        "  join hos.defnarvip dv on d.key = dv.narkey" +
                        "  join hos.znparams zn on zn.defnar = d.key" +
                        "  join hos.vid_him_obrab v on v.id = d.razdel" +
                        "  join hos.him_metod_obrab o on o.key = v.metod_id" +
                        " where dv.enddate between  :dateBegin and :dateEnd" +
                        "   and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "   and zn.parcode = 10000595" +
                        "   and (:method = 0 or o.key = :method)" +
                        "   and v.id = any(string_to_array(:obrab, ',')::integer[])" +
                        "           and closed=1" +
                        "    and (:zeh = 0 or d.zeh = :zeh)" +
                        "   and d.razdel in (select id from rec)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("method", method)
                .addValue("obrab", obrab)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(PZRDto.class)).stream().findFirst().orElse(null);
    }

    public PZRDto getPZRTotalNew(Integer type, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385, 1019540, 1019237]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        " select sum(coalesce(zn.val, 0)) as skv, " +
                        " sum((select case when max(vn.norma_v) > 0 then 1 else 0 end from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1004)) as base " +
                        " from hos.defnar d" +
                        "  join hos.defnarvip dv on d.key = dv.narkey" +
                        "  join hos.znparams zn on zn.defnar = d.key" +
                        "  join hos.vid_him_obrab v on v.id = d.razdel" +
                        "  join hos.him_metod_obrab o on o.key = v.metod_id" +
                        " where dv.enddate between  :dateBegin and :dateEnd" +
                        "   and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "   and zn.parcode = 10000595" +
                        "           and closed=1" +
                        "    and (:zeh = 0 or d.zeh = :zeh)" +
                        "   and d.razdel in (select id from rec)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(PZRDto.class)).stream().findFirst().orElse(null);
    }

    public DurationDto getDurationNew(Integer type, Integer method, String obrab, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385, 1019540, 1019237]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        "select     round(sum(norma)/sum(sred),2) as norma, sum(norma) normaSum,sum(sred) kol_vo_sum," +
                        "           (case when sum(sred_f) is null then 0 when sum(sred_f) = 0 then 0 else round(sum(norma_f)/sum(sred_f), 2) end) as norma_f,sum(norma_f) factSum,sum(sred_f) kol_vo_fact_sum," +
                        "           round(sum(TrudoemkCHELF),2) as TrudoemkCHELF  ," +
                        "           round(sum(TrudoemkCHELN),2) as TrudoemkCHELN " +
                        "    " +
                        " from " +
                        "(select round(sum((case when vn.prnum = 1 then vn.norma_v else 0 end)),2) norma," +
                        "   round(sum((case when vn.prnum = 1 then vn.fact else 0 end)),2) norma_f," +
                        "   round(sum((select sum(volume_v) from hos.vipolnenie_cks vc where vc.vip_key = vn.vip_key and vc.shift = vn.shift ) * (case when vn.prnum = 1 then vn.fact else 0 end)),2) TrudoemkCHELF, " +
                        "   round(sum((case when vn.prnum = 2 then vn.norma_v else 0 end)),2) TrudoemkCHELN, " +
                        "   (case when round(sum((case when vn.prnum = 1 then vn.norma_v else 0 end)),2) = 0 then 0 else 1 end) sred," +
                        "   (case when round(sum((case when vn.prnum = 1 then vn.fact else 0 end)),2) = 0 then 0 else 1 end) sred_f," +
                        "   d.key" +
                        " from hos.defnar d" +
                        "  join hos.defnarvip dv on d.key = dv.narkey" +
                        "  join hos.vid_him_obrab v on v.id = d.razdel" +
                        "  join hos.him_metod_obrab o on o.key = v.metod_id" +
                        " join hos.vipolnenie_oper vo on vo.narkey = dv.narkey" +
                        " join hos.vipolnenie_norm vn on vn.vip_key = vo.key" +
                        " where dv.enddate between  :dateBegin and :dateEnd" +
                        "   and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "   and (:method = 0 or o.key = :method)" +
                        "   and v.id = any(string_to_array(:obrab, ',')::integer[])" +
                        "           and closed=1" +
                        "   and d.razdel in (select id from rec)" +
                        "    and (:zeh = 0 or d.zeh = :zeh)" +
                        "   group by d.key) tb";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("method", method)
                .addValue("obrab", obrab)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(DurationDto.class)).stream().findFirst().orElse(null);
    }

    public DurationDto getDurationTotalNew(Integer type, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385, 1019540, 1019237]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        "select     round(sum(norma)/sum(sred),2) as norma,sum(norma) norma_sum,sum(sred) kol_vo_sum," +
                        "           (case when sum(sred_f) is null then 0 when sum(sred_f) = 0 then 0 else round(sum(norma_f)/sum(sred_f), 2) end) as norma_f,sum(norma_f) fact_sum,sum(sred_f) kol_vo_fact_sum," +
                        "           round(sum(TrudoemkCHELF),2) as TrudoemkCHELF  ," +
                        "           round(sum(TrudoemkCHELN),2) as TrudoemkCHELN " +
                        "    " +
                        " from " +
                        "(select round(sum((case when vn.prnum = 1 then vn.norma_v else 0 end)),2) norma," +
                        "   round(sum((case when vn.prnum = 1 then vn.fact else 0 end)),2) norma_f," +
                        "   round(sum((select sum(volume_v) from hos.vipolnenie_cks vc where vc.vip_key = vn.vip_key and vc.shift = vn.shift ) * (case when vn.prnum = 1 then vn.fact else 0 end)),2) TrudoemkCHELF, " +
                        "   round(sum((case when vn.prnum = 2 then vn.norma_v else 0 end)),2) TrudoemkCHELN, " +
                        "   (case when round(sum((case when vn.prnum = 1 then vn.norma_v else 0 end)),2) = 0 then 0 else 1 end) sred," +
                        "   (case when round(sum((case when vn.prnum = 1 then vn.fact else 0 end)),2) = 0 then 0 else 1 end) sred_f," +
                        "   d.key" +
                        " from hos.defnar d" +
                        "  join hos.defnarvip dv on d.key = dv.narkey" +
                        "  join hos.vid_him_obrab v on v.id = d.razdel" +
                        "  join hos.him_metod_obrab o on o.key = v.metod_id" +
                        " join hos.vipolnenie_oper vo on vo.narkey = dv.narkey" +
                        " join hos.vipolnenie_norm vn on vn.vip_key = vo.key" +
                        " where dv.enddate between  :dateBegin and :dateEnd" +
                        "   and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "           and closed=1" +
                        "   and d.razdel in (select id from rec)" +
                        "    and (:zeh = 0 or d.zeh = :zeh)" +
                        "   group by d.key) tb";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(DurationDto.class)).stream().findFirst().orElse(null);
    }

    public Double getWaterVolume(Integer type, Integer method, String obrab, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385, 1019540, 1019237]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        " select coalesce(sum(vp.znach_v),0) " +
                        " from hos.defnar d" +
                        "  join hos.defnarvip dv on d.key = dv.narkey" +
                        "  join hos.vid_him_obrab v on v.id = d.razdel" +
                        "  join hos.him_metod_obrab o on o.key = v.metod_id" +
                        " join hos.vipolnenie_oper vo on vo.narkey = dv.narkey" +
                        " join hos.vipolnenie_param vp on vp.vip_key = vo.key" +
                        " join public.operlife op on vo.operlifeid = op.id" +
                        " where dv.enddate between  :dateBegin and :dateEnd" +
                        "   and ((parcode in (10000215,10000256,10000609,10000613,10000617) and op.typerab = 1007) or (parcode in (10000215) and op.typerab = 1008))" +
                        "   and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "   and (:method = 0 or o.key = :method)" +
                        "   and v.id = any(string_to_array(:obrab, ',')::integer[])" +
                        "           and closed=1" +
                        "   and d.razdel in (select id from rec)" +
                        "    and (:zeh = 0 or d.zeh = :zeh)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("method", method)
                .addValue("obrab", obrab)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new SingleColumnRowMapper<>(Double.class)).stream().findFirst().orElse(null);
    }

    public Double getWaterVolumeTotal(Integer type, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385, 1019540, 1019237]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        " select coalesce(sum(vp.znach_v),0) " +
                        " from hos.defnar d" +
                        "  join hos.defnarvip dv on d.key = dv.narkey" +
                        "  join hos.vid_him_obrab v on v.id = d.razdel" +
                        "  join hos.him_metod_obrab o on o.key = v.metod_id" +
                        " join hos.vipolnenie_oper vo on vo.narkey = dv.narkey" +
                        " join hos.vipolnenie_param vp on vp.vip_key = vo.key" +
                        " join public.operlife op on vo.operlifeid = op.id" +
                        " where dv.enddate between  :dateBegin and :dateEnd" +
                        "   and ((parcode in (10000215,10000256,10000609,10000613,10000617) and op.typerab = 1007) or (parcode in (10000215) and op.typerab = 1008))" +
                        "   and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "           and closed=1" +
                        "   and d.razdel in (select id from rec)" +
                        "    and (:zeh = 0 or d.zeh = :zeh)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new SingleColumnRowMapper<>(Double.class)).stream().findFirst().orElse(null);
    }

    public SeparateDurationDto getSeparateDuration(Integer type, Long zeh,  LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385, 1019540, 1019237]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        " select " +
                        " sum((select max(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1004)) as base, " +
                        " sum((select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1004)) as baseFact, " +
                        " sum((select sum(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1009))  as transfer," +
                        " sum((select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1009)) as transferFact," +
                        " sum((select max(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1006)) as pr," +
                        " sum((select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1006)) as prFact," +
                        " sum((select max(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1005)) as zr," +
                        " sum((select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1005)) as zrFact," +
                        " sum((select sum(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab in (1007, 1008))) as injection," +
                        " sum((select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab in (1007, 1008))) as injectionFact" +
                        " from hos.defnar d" +
                        "  join hos.defnarvip dv on d.key = dv.narkey" +
                        " where dv.enddate between  :dateBegin and :dateEnd" +
                        "   and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "           and closed=1" +
                        "    and (:zeh = 0 or d.zeh = :zeh)" +
                        "   and d.razdel in (select id from rec)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(SeparateDurationDto.class)).stream().findFirst().orElse(null);
    }

    public List<SeparateDowntimeDto> getSeparateDowntime(Integer type, Long zeh, LocalDateTime dateBegin, LocalDateTime dateEnd) {
        String sql =
                "with recursive roots as (" +
                        "    select id from (" +
                        "        select unnest(array[1018281, 1018865, 1019385, 1019540, 1019237]) as id where :tip = 0" +
                        "        union all" +
                        "        select :tip as id where :tip != 0" +
                        "    ) tmp" +
                        ")," +
                        " rec AS (" +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    where t.id in (select id from roots)" +
                        "    " +
                        "    union" +
                        "    " +
                        "    select t.id, t.parent_id" +
                        "    from public.tematic_razdel t" +
                        "    join rec on t.parent_id = rec.id" +
                        ") " +
                        " select sum(case when pp.znach > 0 then 1 else 0 end) as count, sum(pp.znach) as sum, sp.nm as name, sp.id as id " +
                        " from hos.defnar d" +
                        "  join hos.defnarvip dv on d.key = dv.narkey" +
                        "  join hos.prostoi_param pp on d.key = pp.keynar" +
                        "  join hos.spr_prostoi sp on pp.param = sp.id" +
                        " where dv.enddate between  :dateBegin and :dateEnd" +
                        "   and (:pdr = 0 or d.spr_org_id = :pdr)" +
                        "           and closed=1" +
                        "    and (:zeh = 0 or d.zeh = :zeh)" +
                        "   and d.razdel in (select id from rec)" +
                        "  group by sp.nm, sp.id" +
                        "  order by sp.id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("pdr", 200)
                .addValue("zeh", zeh);
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(SeparateDowntimeDto.class));
    }

    public  List<UrVipDto> getUrVip(Integer type, LocalDateTime dateBegin, LocalDateTime dateEnd){
        String sql = "    with recursive roots as (" +
                "        select id from ( " +
                "        select unnest(array[1018281, 1018865, 1019385, 1019540, 1019237]) as id where :tip = 0 " +
                "        union all " +
                "        select :tip as id where :tip != 0 " +
                "        ) tmp " +
                "    ), " +
                "    rec AS ( " +
                "        select t.id, t.parent_id " +
                "        from public.tematic_razdel t " +
                "        where t.id in (select id from roots) " +
                " " +
                "        union " +
                " " +
                "        select t.id, t.parent_id " +
                "        from public.tematic_razdel t " +
                "        join rec on t.parent_id = rec.id " +
                "        ) " +
                "        select  " +
                "        count (*), " +
                "        sum((select max(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1004)) as base, " +
                "        sum((select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1004)) as baseFact, " +
                "        sum((select max(vn.norma_v) + hos.getprostoi_prigkomp(d.key) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1010 and vo.oper = 101970)) as prigKomp, " +
                "        sum((select sum(vn.fact) + hos.getprostoi_prigkomp(d.key) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1010 and vo.oper = 101970)) as prigKompFact, " +
                "        sum((select max(vn.norma_v) + hos.getprostoi_prigkomp(d.key) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1010 and vo.oper = 105049)) as prigRastv, " +
                "        sum((select sum(vn.fact) + hos.getprostoi_prigkomp(d.key) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1010 and vo.oper = 105049)) as prigRastvFact, " +
                "        sum((select sum(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1009))  as transfer, " +
                "        sum((select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1009)) as transferFact, " +
                "        sum((select sum(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab in (1005, 1006))) as pzr, " +
                "        sum((select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab in (1005, 1006))) as pzrFact," +
                "        sum((select sum(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab in (1007, 1008))) as injection, " +
                "        sum((select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab in (1007, 1008))) as injectionFact, " +
                "        sum((select sum(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab in (1007))) as inj, " +
                "        sum((select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab in (1007))) as injFact, " +
                "        sum((select sum(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab in (1008))) as prod, " +
                "        sum((select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab in (1008))) as prodFact," +
                "        sum((select max(vn.norma_v) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1011)) as ozhReact, " +
                "        sum((select coalesce(sum(vn.fact),0) from hos.vipolnenie_norm vn join hos.vipolnenie_oper vo on vn.vip_key = vo.key join public.operlife o on vo.operlifeid = o.id where vo.narkey = d.key and vn.prnum = 1 and o.typerab = 1011)) as ozhReactFact,         " +
                "        sum((select sum(pp.znach) from hos.prostoi_param pp where pp.keynar = d.key)) as downtime, " +
                "                case d.zakazchik " +
                "            when '1' then 1 " +
                "            when '2' then 2 " +
                "            when '6' then 3  " +
                "            when '3' then 4 " +
                "            when '4' then 5 " +
                "            when '5' then 6 " +
                "            when '7' then 7 " +
                "            when '8' then 8 " +
                "            when '9' then 9 " +
                "            when '10' then 10 " +
                "            when '11' then 11 " +
                "            when '12' then 12   " +
                "            else 13           " +
                "        end as zakazchik " +
                "        from hos.defnar d " +
                "        join hos.defnarvip dv on d.key = dv.narkey " +
                "        where dv.enddate between :dateBegin and :dateEnd " +
                "        and (:pdr = 0 or d.spr_org_id = :pdr) " +
                "        and closed=1 " +
                "        and d.razdel in (select id from rec) " +
                "        group by rollup (d.zakazchik) ";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("pdr", 200);

        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(UrVipDto.class));
    }

    public List<Integer> getTypeTech(Integer type, LocalDateTime dateBegin, LocalDateTime dateEnd){
        String sql  = "with recursive ids as (select distinct t.id " +
                "       from hos.defnar d " +
                "            join hos.defnarvip dv on d.key = dv.narkey " +
                "            join public.tematic_razdel t on d.razdel = t.id " +
                "       where dv.enddate between  :dateBegin and :dateEnd" +
                "       and (:pdr = 0 or d.spr_org_id = :pdr) and closed=1), " +
                " rec as (" +
                " select t.* from public.tematic_razdel t where t.id in (select id from ids)" +
                " union " +
                " select t.* from public.tematic_razdel t join rec on t.id = rec.parent_id and rec.id <> 1000242 )" +
                " select id from rec where parent_id = 1000242 and (:tip = 0 or id = :tip) order by ord desc";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("tip", type)
                .addValue("dateBegin", dateBegin)
                .addValue("dateEnd", dateEnd)
                .addValue("pdr", 200);
        return namedParameterJdbcTemplate.query(sql, params, new SingleColumnRowMapper<>(Integer.class));
    }

}
