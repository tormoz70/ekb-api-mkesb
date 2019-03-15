with ddd as (
    select a.pu_num,
           round(sum(a.tckts)/count(distinct a.org_id)) as tckts,
           round(sum(a.summ)/count(distinct a.org_id), 2) as summ,
           round(sum(a.sess)/count(distinct a.org_id)) as sess,
           count(distinct a.org_id) orgs,
           count(distinct a.sroom_id) srooms
      from CUB4$SALES1 a
      where a.part_month between to_char(mob_fmt.decodeDate(:p_periodStart), 'YYYYMM') and to_char(mob_fmt.decodeDate(:p_periodEnd), 'YYYYMM')
        and a.show_date >= trunc(mob_fmt.decodeDate(:p_periodStart)) and a.show_date < trunc(mob_fmt.decodeDate(:p_periodEnd))+1
        and (:p_holdingId is null or a.holding_id = to_number(:p_holdingId))
        and (:p_orgId is null or a.org_id = to_number(:p_orgId))
        and (:p_pu_num is null or a.pu_num = to_number(:p_pu_num))
        and (
            (:p_userRole in ('6', '8'))
            or (
                (:p_userRole in ('7'))
                and (
                    exists (
                        select 1
                           from ORGM$FILMPERMWS e
                          where e.id_org = to_number(:p_userOrgId)
                            and e.pu_number = a.pu_num
                    )
                )
            )
        )
     group by a.pu_num
)
select
    a.pu_num,
    k.startdate as startdate_srt,
    formatDatetimeUpToClient(k.startdate) as startdate,
    k.name_rus,
    a.tckts,
    a.summ,
    a.sess,
    a.orgs,
    a.srooms
from ddd a
    inner join cub4$kinos k on k.pu_num = a.pu_num
