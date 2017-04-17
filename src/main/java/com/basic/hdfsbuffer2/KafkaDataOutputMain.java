package com.basic.hdfsbuffer2;

import com.basic.hdfsbuffer2.model.HdfsCachePool;
import com.basic.hdfsbuffer2.task.DataInputFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.mapreduce.InputSplit;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

/**
 * locate com.basic.hdfsbuffer2
 * Created by 79875 on 2017/4/11.
 * HDFS文件时序性读取 Version 1.1
 * java -Xmx4028m -Xms4028m -cp hadoopTest-1.0-SNAPSHOT.jar com.basic.hdfsbuffer2.KafkaDataOutputMain /user/root/wordcount/input/1.log 10  testTopic 9
 */
public class KafkaDataOutputMain {
    private static final Log LOG = LogFactory.getLog(KafkaDataOutputMain.class);
    private static List<InputSplit> splits;//输入文件分片的数据类型 InputSplit
    private static DataInputFormat dataInputFormat=new DataInputFormat();
    private static DataOutputKafka dataOutputKafka;

    public static void main(String[] args) throws IOException, InterruptedException {
        long startTimeSystemTime= System.currentTimeMillis();
        splits= dataInputFormat.getSplits(args[0]);
        int CachePoolBufferNum=Integer.valueOf(args[1]);//缓冲池缓存Block大小
        String kafkaTopic=args[2];//kafka的Topci
        int kafkaPartitionsNum=Integer.valueOf(args[3]); //kafkaTopic 的分区个数保证数据均匀分布在Kafka分区中
        HdfsCachePool hdfsCachePool=HdfsCachePool.getInstance(CachePoolBufferNum,splits);
        hdfsCachePool.runHDFSCachePool();

        dataOutputKafka=new DataOutputKafka(hdfsCachePool,kafkaPartitionsNum);
        dataOutputKafka.datoutputKafka(kafkaTopic);

        long endTimeSystemTime = System.currentTimeMillis();
        LOG.info("startTime:"+new Timestamp(startTimeSystemTime));
        LOG.info("endTime:"+new Timestamp(endTimeSystemTime));
        long timelong = (endTimeSystemTime-startTimeSystemTime) / 1000;
        LOG.info("totalTime:"+timelong+" s"+"------or------"+timelong/60+" min");
        System.exit(0);
    }

    public void TestKafkaDataOutput(String inputFile) throws IOException, InterruptedException {
        long startTimeSystemTime= System.currentTimeMillis();
        splits= dataInputFormat.getSplits(inputFile);
        HdfsCachePool hdfsCachePool=HdfsCachePool.getInstance(4,splits);
        hdfsCachePool.runHDFSCachePool();
        int kafkaPartitionsNum=6;
        dataOutputKafka=new DataOutputKafka(hdfsCachePool,kafkaPartitionsNum);
        dataOutputKafka.datoutputKafka("abc");

        long endTimeSystemTime = System.currentTimeMillis();
        LOG.info("startTime:"+new Timestamp(startTimeSystemTime));
        LOG.info("endTime:"+new Timestamp(endTimeSystemTime));
        long timelong = (endTimeSystemTime-startTimeSystemTime) / 1000;
        LOG.info("totalTime:"+timelong+" s"+"------or------"+timelong/60+" min");
        System.exit(0);
    }
}
