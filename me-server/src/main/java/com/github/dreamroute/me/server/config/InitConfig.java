package com.github.dreamroute.me.server.config;

import com.github.dreamroute.me.server.entity.DatabaseInfo;
import com.github.dreamroute.me.server.entity.TableInfo;
import com.github.dreamroute.me.server.service.DatabaseInfoService;
import com.github.dreamroute.me.server.service.TableInfoService;
import com.vip.vjtools.vjkit.collection.type.ConcurrentHashSet;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <ol>
 *     <li>数据库结构：3张表：分别是平台 -> 数据库 -> 表，一次是一对多关系</li>
 *     <li>应用启动时初始化数据</li>
 * </ol>
 *
 * @author w.dehai
 */
@Order(1)
@Component
@AllArgsConstructor
public class InitConfig implements CommandLineRunner {

    private DatabaseInfoService databaseInfoService;
    private TableInfoService tableInfoService;

    @Override
    public void run(String... args) {

        // 缓存Map<库名, 平台id>
        cacheDb2platformId();

        // 缓存所有表名(database.table)
        cacheAllTables();
    }

    private void cacheAllTables() {
        List<TableInfo> allTable = tableInfoService.listAll();
        if (allTable != null && !allTable.isEmpty()) {
            Set<String> tableNames = allTable.stream().map(tableInfo -> tableInfo.getDatabaseName() + '.' + tableInfo.getTableName()).collect(Collectors.toCollection(ConcurrentHashSet::new));
            ConfigStore.TABLE_NAME.addAll(tableNames);
        }
    }

    private void cacheDb2platformId() {
        List<DatabaseInfo> allDbs = databaseInfoService.listAll();
        if (allDbs != null && !allDbs.isEmpty()) {
            Map<String, List<DatabaseInfo>> dbName2Db = allDbs.stream().collect(Collectors.groupingBy(DatabaseInfo::getDatabaseName));
            dbName2Db.forEach((dbName, dbInfo) -> ConfigStore.DATABASENAME_PLATFORMID.put(dbName, dbInfo.get(0).getPlatformId()));
        }
    }

}
