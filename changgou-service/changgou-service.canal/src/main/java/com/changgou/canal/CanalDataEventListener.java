package com.changgou.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.*;

/**
 * @Author: HeWei·Yuan
 * @CreateTime: 2021-04-30 10:43
 * @Description:
 */
@CanalEventListener
public class CanalDataEventListener {
    /**
     * 新增监听
     * @param eventType
     *          操作类型
     * @param rowData
     *          新增行数据
     */
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println("新增列名：" + column.getName() + "-------新增后的数据：" + column.getValue());
        }
    }

    /***
     * 修改数据监听
     * @param rowData
     */
    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println("修改前列名：" + column.getName() + "-------修改前的数据：" + column.getValue());
        }

        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println("修改后列名：" + column.getName() + "-------修改后的数据：" + column.getValue());
        }
    }

    /***
     * 删除数据监听
     * @param eventType
     */
    @DeleteListenPoint
    public void onEventDelete(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println("删除前列名：" + column.getName() + "-------删除前的数据：" + column.getValue());
        }
    }

    /***
     * 自定义数据修改监听
     * @param eventType
     * @param rowData
     */
    @ListenPoint(destination = "example",
            schema = "changgou_content", // 数据库
            table = {"tb_content_category", "tb_content"}, // 监听那张表
            eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.DELETE}) // 监听类型
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println("自定义操作前列名：" + column.getName() + "-------自定义前的数据：" + column.getValue());
        }

        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println("自定义操作后列名：" + column.getName() + "-------自定义后的数据：" + column.getValue());
        }
    }
}

