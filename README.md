##一个TCP/WebSocket服务端网络框架,性能高效,稳定,简单易上手
# 服务端使用说明
###创建服务端
<br>最简单的默认配置启动服务端
<br>NetBoxEngine boxEngine = new NetBoxEngine();
<br>boxEngine.start();
<br>
<br>自定义配置启动服务端
<br>NetBoxEngineSetting setting  = new NetBoxEngineSetting();
<br>setting.setBasePackage("com.iih5.server");
<br>setting.setPort(9230);
<br>setting.setProtocolCoder(new TcpForDefaultProtoEncoder(),new TcpForDefaultProtoDecoder());
<br>setting.setDebug(true);
<br>NetBoxEngine boxEngine = new NetBoxEngine();
<br>boxEngine.setSettings(setting);
<br>boxEngine.start();

###配置说明
<br>NetBoxEngineSetting 是 NetBoxEngine 的配置类，字段如下
<br>void setPort(int port) //监听端口
<br>void setBossThreadSize(int bossThreadSize)//设置监听线程数量
<br>void setWorkerThreadSize(int workerThreadSize)//设置框架工作线程数量
<br>void setBasePackage(String basePackage)//设置注解扫描路径
<br>void setPlayerThreadSize(int playerThreadSize)//设置用户操作线程数
<br>void setDebug(boolean debug)//设置调试模式，true为调试模式
<br>void setProtocolCoder(Object encode,Object decode)//设置编码/解码，不设置则这采用默认TCP的 ProtocolDecoder2/ProtocolEncoder2
<br>
##SmartNetBox 默认自带的TCP/WebSocket-Binary传输协议格式
<br>/**
<br> ** 包格式：包头(byte=1)+包长度(int=4)+消息码(short=2)+加密段(byte=1)+数据段(byte[])
<br> 1）包头        :表示数据包合法性
<br> 2）包长度      :表示整个数据的长度(包含用于表示长度本身的字节)
<br> 3）消息码      :表示数据包类型
<br> 4）加密段      :表示数据段是否加密,采用什么加密算法
<br> 5）数据段      :采用byte[]形式存放
<br> */
##SmartNetBox WebSocket 文本传输协议
<br>包格式:  "【协议ID】#【加密类型】#【内容】",比如 10001#0#hello world!
##消息接受处理
<br>创建一个类，然后接入@Request注解，标识这是个handler类，@Protocol()注解标识协议处理,如下：
<br>@Request
<br>public class TestHandler {
<br>	@Protocol(value=2001)
<br>	public void test(Message msg, ISession session) throws Exception {
<br>		Example.Message.Builder message = (Example.Message.Builder)msg.parseObject(Example.Message.newBuilder());
<br>		log.info("name====="+message.getText());
<br>		log.info("id="+message.getNumber());
<br>		session.send(msg);
<br>	}
<br>}
##处理连接和断开事件
<br>继承ConnectExtension基类，接入@InOut()注解，标识这是个断开或连接处理类，然后重写connect和disConnect方法
<br>@InOut("connect/disconnect callback")
<br>public class DisConnectExtension extends ConnectExtension {
<br>	public void connect(ISession session) {
<br>		log.info("连接成功");
<br>	}
<br>	public void disConnect(ISession session) {
<br>		log.info("断开连接");
<br>	}
<br>}
##Session管理
SessionManager是个全局的Session管理类，利用这个类管理Session的各个行为，比如 SessionManager.getInstance().getAllSessions()

# 客户端使用说明
客户端的使用就比较灵活些，客户端是java的可以直接使用SmartNetBox的 编码/解码器，可以自己定义，如果是其他语言，则需要自己根据
协议自己定义

##目前已经提供java/c#客户端API，后续后继续加上各个语言的客户端API,方便同学们使用
##例子在example模块下面

##API查看地址
http://doc.iih5.com/netbox/index.html


###1.2版本
<br>1）修改了底层传输协议，增加了协议编码2
<br>2）修复部分bug
<br>3)
<br>
<br>//传输格式--------------------------编码1-----------------------------------------------------
<br>/**
<br> ** 包格式：包长度(int=4)+消息码(short=2)+数据段(byte[])
<br> 1）包长度      :表示该整个数据的长度(包含用于表示长度本身的字节)
<br> 2）消息码      :表示该数据包类型
<br> 3）数据段      :采用byte[]形式存放
<br> */
<br>//传输格式-------------------------------------------------------------------------------
<br>
<br>//编码1说明：编码简单，基础包小
<br>
<br>
<br>//传输格式--------------------------编码2-----------------------------------------------------
<br>/**
<br> ** 包格式：包头(byte=1)+包长度(int=4)+消息码(short=2)+加密段(byte=1)+数据段(byte[])
<br> 1）包头        :表示数据包合法性
<br> 2）包长度      :表示整个数据的长度(包含用于表示长度本身的字节)
<br> 3）消息码      :表示数据包类型
<br> 4）加密段      :表示数据段是否加密,采用什么加密算法
<br> 5）数据段      :采用byte[]形式存放
<br> */
<br>//传输格式-------------------------------------------------------------------------------
<br>
<br>//编码2说明：编码相对复杂，基础包大，有包头校检，当包头错误时，主动断开连接，有效防止非法垃圾数据，有加密标识，方便加密