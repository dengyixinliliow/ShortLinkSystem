package com.example.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.project.dao.entity.LinkAccessStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

public interface LinkAccessStatsMapper extends BaseMapper<LinkAccessStatsDO> {

    @Insert("""
            INSERT INTO t_link_access_stats
                (full_short_url, gid, `date`, pv, uv, uip, `hour`, weekday, create_time, update_time, del_flag)
            VALUES
                (#{fullShortUrl}, #{gid}, #{date}, 1, #{uvIncrement}, #{uipIncrement}, #{hour}, #{weekday}, NOW(), NOW(), 0)
            ON DUPLICATE KEY UPDATE
                pv = pv + 1,
                uv = uv + #{uvIncrement},
                uip = uip + #{uipIncrement},
                update_time = NOW(),
                del_flag = 0
            """)
    void incrementStats(@Param("fullShortUrl") String fullShortUrl,
                        @Param("gid") String gid,
                        @Param("date") LocalDate date,
                        @Param("hour") Integer hour,
                        @Param("weekday") Integer weekday,
                        @Param("uvIncrement") Integer uvIncrement,
                        @Param("uipIncrement") Integer uipIncrement);
}
