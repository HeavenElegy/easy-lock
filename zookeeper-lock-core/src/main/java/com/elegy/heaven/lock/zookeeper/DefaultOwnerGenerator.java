package com.elegy.heaven.lock.zookeeper;

import java.util.UUID;

/**
 * 默认的拥有者生成器
 * <p>所生成的<i>拥有者</i>使用如下信息进行比较</p>
 * <ol>
 *     <li>全局uuid(作用域为当前服务)</li>
 *     <li>线程id</li>
 *     <li>线程名</li>
 * </ol>
 *
 * @author lixiaoxi_v
 * @date 2020-11-23 11:24:14
 */
public class DefaultOwnerGenerator implements OwnerGenerator<DefaultOwnerGenerator.DefaultOwner> {

    private static final String GLOBAL_UUID = UUID.randomUUID().toString();

    @Override
    public DefaultOwnerGenerator.DefaultOwner getOwner() {
        return new DefaultOwner(
                GLOBAL_UUID,
                String.valueOf(Thread.currentThread().getId()),
                Thread.currentThread().getName()
        );
    }

    public static class DefaultOwner extends OwnerGenerator.Owner {

        private String uuid;
        private String threadId;
        private String threadName;

        public DefaultOwner(String uuid, String threadId, String threadName) {
            this.uuid = uuid;
            this.threadId = threadId;
            this.threadName = threadName;
        }


        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof DefaultOwner)) {
                return false;
            }
            DefaultOwner in = (DefaultOwner) obj;
            return in.uuid.equals(this.uuid)
                    && in.threadId.equals(this.threadId)
                    && in.threadName.equals(this.threadName);
        }

        @Override
        public String
        toString() {
            return "DefaultOwner{" +
                    "uuid='" + uuid + '\'' +
                    ", threadId='" + threadId + '\'' +
                    ", threadName='" + threadName + '\'' +
                    '}';
        }
    }
}
