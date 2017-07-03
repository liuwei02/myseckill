package com.cn.winter.dao.cache;

import com.cn.winter.entity.Seckill;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by Administrator on 2017/7/3.
 */
public class RedisDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JedisPool jedisPool;

    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public RedisDao(String ip, int port){
        jedisPool = new JedisPool(ip, port);
    }

    public Seckill getSeckill(long seckillId){
        //redis操作逻辑om

        try {
            //获取jedis
            Jedis jedis = jedisPool.getResource();

            try{
                String key = "seckill:"+seckillId;
                //并没有实现内部序列化的操作
                //get->byte[] ->反序列化 ->Object(Seckill)
                //采用自定义序列化
                //protostuff:pojo(对象必须是普通的java对象（get、set)
                byte[] bytes = jedis.get(key.getBytes());
                if(bytes != null){
                    //创建一个空的对象
                    Seckill seckill = schema.newMessage();
                    ProtobufIOUtil.mergeFrom(bytes,seckill,schema);
                    //seckill 被反序列化
                    return  seckill;
                }

            }finally {
                //关闭jedis
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    public String putSeckill(Seckill seckill){
        //set Object(Seckill) ->序列化 ->byte[]

        try{
            Jedis jedis = jedisPool.getResource();

            try{
                String key = "seckill:" + seckill.getSeckillId();
                byte[] bytes = ProtobufIOUtil.toByteArray(seckill, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //超时缓存
                int timeout = 60*60;//一小时
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;
            }finally {
                jedis.close();
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
