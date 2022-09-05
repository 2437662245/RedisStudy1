package com.atguigu.jedis;

import redis.clients.jedis.Jedis;

import java.util.Random;

public class PhoneCode {
    public static void main(String[] args) {
//        verifyCode("13113113133");
//        getRedisCode();
    }

    // 3.验证码校验
    public static void getRedisCode(String phone, String code) {
        // 从redis中获取验证码
        Jedis jedis = new Jedis("localhost", 6379);
        // 验证码key
        String codeKey = "verityCode" + phone + "code";
        String redisCode = jedis.get(codeKey);

        if (redisCode.equals(code)) {
            System.out.println("成功");
        } else {
            System.out.println("验证码错误");
        }

    }

    // 2.每个手机每天只能发送3次验证码，验证码放入redis中 设置过期时间
    public static void verifyCode(String phone) {
        // 2.1 连接redis
        Jedis jedis = new Jedis("localhost", 6379);
        // 2.2 拼接key 手机发送次数的拼接
        String countKey = "verityCode" + phone + "count";
        // 2.3 验证码key
        String codeKey = "VerifyCode" + phone + "code";
        // 2.4 每个手机每天只能发送3次
        String count = jedis.get(countKey);
        if (count == null) {
            // 以前没有发送过 设置首次发送次数位 1
            jedis.setex(countKey, 24*60*60, "1");
        } else if (Integer.parseInt(count) <= 2) {
            // 发送次数+1
            jedis.incr(countKey);
        } else if (Integer.parseInt(count) > 2) {
            // 发送3次， 不能再发送
            System.out.println("发送次数上限");
            jedis.close();
            return;
        }

        // 发送的验证码要放到redis中
        String vCode = getCode();
        jedis.setex(codeKey, 120, vCode);
        jedis.close();
    }

    // 1.生成6位数字验证码
    public static String getCode() {
        Random random = new Random();
        String code = "";
        for (int i = 0; i < 6; i++) {
            int rand = random.nextInt(10);
            code += rand;
        }
        return code;
    }
}
