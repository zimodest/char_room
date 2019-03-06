package com.zimodest;

/*
    Socket编程：封装IP协议（通过IP协议实现）
    C/S架构
    B/S架构
    ip地址：在网络中找到一台设备
    端口号: 唯一标识本台机器中的一个应用程序
    ssh：22

    Socket通信：
        1、先建立连接：知道目标服务器的IP地址和端口号
        2、获取连接的输入和输出流
        3、通过IO进行数据的读取与写入
        4、关闭流

    服务端Socket类 ServerSocket 重点构造方法  public ServerSocket(int port) throws IOException
        1、建立服务端Socket等待客户端的连接，通过构造方法实现-建立基站
            ServerSocket server = new ServerSocket(port); 在本机的哪个端口创建基站
        2、等到客户端的连接
            accept(): 一直阻塞直到有客户端连接，返回客户端Socket
        3、取得输入和输出流
            getInputStream()
            getOutputStream()
        4、关闭流  将基站关闭
            server.close();

    客户端Socket类 Socket
        1、连接到指定服务器，通过构造方法实现
            Socket client = new Socket(ip,  port);
        2、取得输入和输出流
            getInputStream()
            getOutputStream()
        3、关闭流
            close()

        客户端连接请求的发起和客户端Socket的关闭都在客户端进行

        本机默认IP ：127.0.0.1
        本机默认域名 ： localhost

        单线程通信的问题
        1、容易造成双方卡死的现象
        类比电话占线

        2、发送一次数据后服务器与客户端均退出，不能持久通信

        3、不能同时进行数据的读取与数据的写入，顺序执行  -读写分离，作为两个不相关的线程

        4、服务器只能处理一个客户端的连接 --每当有一个客户端的连接进来就创建一个线程来处理此客户端的请求

        多线程通信：

        客户端：
        读写分离，读作为一个线程，写作为一个线程

        服务器实现 ： 存储所有连接的客户端 Map<userName, Socket>

        群聊实现：
            G:hello i am ..
        私聊实现：
            P:
        退出：UserName byebye

        Pattern:正则表达式类
        Matcher：过滤字符串
        线程池：Executors

 */
/*
    集合框架 --JDK1.2（解决数组定长问题，动态数组）

    Collection-保存单个元素的父接口
        -Iterator（集合遍历迭代器接口）  list.iterator()
        -List（允许数据重复）
            -常用方法：add() remove() get(int index) set(int index, Object newValue)
            -常用子类
            -ArrayList

            -Vector

            -LinkedList
        -Set（不允许数据重复）
            -HashSet

            -TreeSet 插入时进行比较，要有比较机制：插入对象的类实现Comparable 或者 创建TreeSet对象时，传入Comparator类对象
        -Queue
            -Deque
            -BlockingQueue（juc下的阻塞线程-线程池）
        面试题：
            1、对比ArrayList Vector LinkedList - 总分总
                总：都是Collection接口的实现类
                初始化策略、扩容机制、是否线程安全，底层实现
            2、fail-fast 与 fail-safe
                fail-fast：java.util.concurrentModifiedException
                如何产生：在迭代遍历集合时同时修改集合结构（add、remove）
                本质：执行修改时 modCount 增加   取得迭代器时 exceptedModCount = modCount
                        遍历时 判断exceptedModCount与modeCount
                为何抛出此异常：
                    尽量保证在多线程场景下数据不会产生脏读，即若有线程修改集合结构时，向同时遍历此集合的线程发出一个异常，
                    通知其数据集合已经修改
                抛出此异常的类：
                    ArrayList Vector LinkedList    HashMap
                fail-safe：juc包下的类 不抛出此异常
                CopyOnWriteArrayList  ConcurrentHashMap

           3、TreeSet有序，指的是什么？
                要使用TreeSet，必须满足以下两者之一：
                I、作为TreeSet集合的类，实现Comparable接口（内部排序）
                II、通过构造方法出入Comparator接口对象（外部排序）
                优先使用外部排序
            4、HashSet中元素判重的依据：
                hashCode --
                equals



        Map接口-保存一对对象的接口
        资源文件（Properties）的使用：
        InputStream in = Test.Class.getClassLoader().getResourcesAsStream("资源文件名");
        Properties pro = new Properties(in);
        pro.load();

        操作方法：
        put(key, value)
        get(key)
        KeySet() :Set<Key>  取得所有key值
        values():Collection<value>  取得所有value值
        remove(key):

        entrySet(): Set<Map.Entry<k, v>> 将Map变为Set集合用于迭代
        getKey()
        getValue()

        源码剖析
        -HashMap
            1、成员变量、树化阈值
                默认初始化容量：16（桶个数）
                负载因子loadFactor：0.75F
                树化阈值：8 链表元素个数
                树化阈值：64 桶中元素个数（链表数）  树化要求的最少哈希表元素个数
                解除树化阈值：6 （resize阶段）

                树化总结：
                    当桶中链表个数超过8并且哈希表中所有元素个数超过64，此时，会将桶中链表转为红黑树结构
                    否则（只是链表元素个数超过8），只是简单的进行扩容操作而已
                为啥树化？
                    当桶中链表长度太长会大大影响查找速度，因此树化来提高查找指定节点的速度。

            2、初始化策略 - 看构造方法
                HashMap采用lazy_load策略 (当第一次使用put()时，才会将哈希表初始化) 初始化容量16
                无参构造
                public HashMap(){
                    this.loadFactor = DEFAULT_LOAD_FACTOR;
                }

                有参构造
                public HashMap(int initialCapacity){
                    this(initialCapacity,DEFAULT_LOAD_FACTOR);
                }
                问题：要求初始化容量必须为2的次方，若通过构造方法传入一个非2的次方 的值，HashMap会在内部调用tableSizeFor返回一个
                距离最近的2的次方值，eg：



            3、put、get
                put方法流程：
                    1、若HashMap还未初始化，调用resize进行初始化操作
                    II、对key值Hash取得要存储的桶下标
                        若桶中为空，将节点直接作为桶的头结点保存
                        2、若桶不为空
                            a.若树化，使用树的方式添加新节点
                            b、将新节点以链表形式尾插到最后
                                -添加元素后，链表的个数binCount>=树化阈值-1，尝试进行树化操作
                        3、若桶中存在相同的key节点，替换value值
                    III、添加元素后计算整个哈希表大小，若超过threshold（容量*负载因子），进行resize扩容操作
            get方法流程
                I、若表已经初始化并且桶的首节点不为空
                    1、查找的节点key值恰好等于首节点，直接返回首节点
                    2、进行桶元素的遍历，查找指定节点
                        a、若树化，按照树的方式查找

                        b、按照链表查找
                II、哈希表为空或桶的首节点为null，返回null




            4、哈希算法、扩容机制、性能
                哈希算法：
                    返回高低16位共同参与运算的一个Hash值
                    static final int hash(Object key){
                    int h;
                    return (key == null)? 0: () ^ (h>>>16)
                    }
                    为何不直接采用Object的hashcode()返回 桶下标
                    桶容量大，哈希冲突的概率太小


                    （n - 1）& hash：使用为运算代替数学的取模运算提高分桶速度
                     若 n 恰好是2的次方 （n-1)%hash
                扩容机制：resize
                    I、判断哈希表是否初始化，若还未初始化，根据InitialCapacity值（必须是2的次方）进行初始化操作
                    II、若表已经初始化，将哈希表按照2倍方式扩容
                    III、扩容后进行原表的移动
                        1、若桶中节点已经树化，调用树的方式移动元素
                            （若在移动过程中发现红黑树节点<=6，会将红黑树解除树化，还原成链表
                        2、若还未树化，调用链表的方式来移动元素
                    性能问题：
                        1、多线程场景下由于条件竞争，很容易造成死锁（使用ConcurrentHashMap）
                        2、rehash是一个比较耗时的过程
                            （在能预估存储的元素个数的前提下，尽量自定义初始化容量，尽量减少resize过程）
    HashTable：基于哈希表存储元素
        早期版本的Map实现
        初始化策略：当产生HashTable对象时就将哈希表初始化
        线程安全：在put remove get方法上使用内建锁将整个哈希表上锁
        使用串行化操作整表，性能较低

        如何优化HashTable的性能？
            使用分段锁，将锁细粒度化，将整锁拆成多个锁进行优化




    -ConcurrentHashMap
        JDK1.7实现
            基于分段锁Segment来实现，每个Segment实际上是ReentrantLock的子类
            1、结构：将哈希表拆分为16个Segment，每个Segment下又是一个小的哈希表
            2、关于锁：将原先整表的一把锁细粒度化为每个Segment一把锁，并且不同Segment之间互不干扰
                每个Segment实际上是ReentrantLock的子类
            3、扩容机制：初始化后无法扩容（默认初始化16），扩容实际上是Segment对应的小的哈希表，
                并且不同Segment之间的扩容完全隔离
        JDK1.8实现
            1、结构：与JDK1.8的HashMap如出一辙，也是哈希表+红黑树的底层结构，
                原先的Segment保留，没有实际含义，仅仅用作序列化
            2、锁：将原先锁一片区域再次细粒度化为只锁桶中的头结点
                使用CAS+同步代码块（Synchronized）
        对比JDK7和JDK8的ConcurrentHashMap：
        1、结构上的变化：
            JDK7是基于分段锁的Segment
            JDK8 哈希表+红黑树
        2、锁的使用
            JDK7使用ReentrantLock将Segment上锁
            JDK8使用CAS+Synchronized代码块
        小tips：
        为何JDK8中又重新使用Synchronized内建锁？
            在现版本的JDK中，内建锁与lock的性能上基本差不多，甚至在低竞争场景下还会优于lock
                lock锁的对象必须都加到同步队列中
                synchronized只是将头结点加入同步队列
            使用synchronized可以节省大量内存空间，这是相较于ReentrantLock最大的优势



    juc包的梳理：java.util.concurrent
    四个并发工具
        CountDownLatch-闭锁
            CountDownLatch对象在计数器减为0之后无法使用，只能使用一次，值无法还原
        CyclicBarrier-循环栅栏
            对象在计数器减为0后可以循环使用
        Semaphore-信号量
            Semaphore通过控制同时访问的线程个数，通过acquire()方法来尝试获取资源，若没有资源就等待。
            通过release()释放一个资源
        Exchanger-交换器
            用于两个线程之间交换数据


































 */
