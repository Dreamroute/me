package com.github.dreamroute.me.server.config;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.github.dreamroute.me.server.entity.DatabaseInfo;
import com.github.dreamroute.me.server.entity.TableInfo;
import com.github.dreamroute.me.server.service.DatabaseInfoService;
import com.github.dreamroute.me.server.service.TableInfoService;
import com.vip.vjtools.vjkit.collection.type.ConcurrentHashSet;
/**
 * @author w.dehai
 */
@Component
public class InitConfig implements CommandLineRunner {
    
    @Autowired
    private DatabaseInfoService databaseInfoService;
    @Autowired
    private TableInfoService tableInfoService;

    @Override
    public void run(String... args) throws Exception {
        List<DatabaseInfo> allDatabase = databaseInfoService.listAll();
        if (allDatabase != null && !allDatabase.isEmpty()) {
            // 设置  库名 -> 平台id
            Map<String, List<DatabaseInfo>> dbName2Db = allDatabase.stream().collect(Collectors.groupingBy(DatabaseInfo::getDatabaseName));
            dbName2Db.forEach((dbName, dbInfo) -> ConfigStore.DATABASENAME_PLATFORMID.put(dbName, dbInfo.get(0).getPlatformId()));
        }
        
        List<TableInfo> allTable = tableInfoService.listAll();
        if (allTable != null && !allTable.isEmpty()) {
            Set<String> tableNames = allTable.stream().map(tableInfo -> tableInfo.getDatabaseName() + '.' + tableInfo.getTableName()).collect(Collectors.toCollection(ConcurrentHashSet::new));
            ConfigStore.TABLE_NAME.addAll(tableNames);
        }
    }

}
