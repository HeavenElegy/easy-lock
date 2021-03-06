# 常用锁集合

## 基于zookeeper的重入锁

### 使用方法

#### 基于`Java`

```java
// 初始化代码(全局只需要一次)
ZooKeeperLockSupport.init(
        "127.0.0.1:2181", 
        20 * 1000, 
        new DefaultOwnerGenerator());
// 调用
ZooKeeperReentrantLock lock = new ZooKeeperReentrantLock("key");
lock.lock();
// to do something
lock.unlock();
```

#### 基于`spring-starter`

`yml`配置

```yaml
zookeeper:
  lock:
    connect-string: 127.0.0.1:2181
    session-time-out: 30s
    enabled: true
```

`Maven`配置

```xml
<dependency>
    <groupId>com.elegy.heave.lock.zookeeper</groupId>
    <artifactId>zookeeper-lock-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

调用

```java
ZooKeeperReentrantLock lock = new ZooKeeperReentrantLock("key");
lock.lock();
// to do something
lock.unlock();
```

### 自定义所有者信息`Owner`

框架在对锁的占用时，可能会比较占用者信息`Owner`。用户可以自行实现`OwnerGenerator`作为`Owner`生成器。
默认结构如下。自行实现时，`spring`注入到容器，其他情况手动调用`ZooKeeperLockSupport.init()`进行注入

```java
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
    }
}
```

### 待续

|功能|是否支持|计划|
|:-:|:-:|:-:|
|优化性能|-|进行中|
|结构优化|-|进行中|
|超时|×|待加入|
|中断|×|待加入|

## 基于zookeeper的读写锁(暂时还没写)

## 锁结构抽象(随便想想)