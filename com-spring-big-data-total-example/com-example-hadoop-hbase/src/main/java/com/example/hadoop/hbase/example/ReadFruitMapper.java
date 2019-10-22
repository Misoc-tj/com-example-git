package com.example.hadoop.hbase.example;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class ReadFruitMapper extends TableMapper<ImmutableBytesWritable, Put> {

    //key 是rowKey  result是一行数据
    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        //rowKey
        Put put = new Put(key.get());

        for (Cell cell : value.rawCells()) {
            if ("info".equals(Bytes.toString(CellUtil.cloneFamily(cell)))) {
                if ("name".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    put.add(cell);
                } else if ("color".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    put.add(cell);
                }
            }
        }
        context.write(key, put);
    }
}
