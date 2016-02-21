# SmartNetBox 这是一个TCP/WebSocket服务端框架，简单易上手
# 服务端使用说明
##创建服务端
<br>最简单的启动服务端
<br>NetBoxEngine boxEngine = new NetBoxEngine();
<br>boxEngine.start();
<br>使用默认配置信息端口9230，TCP协议，二进制传输，如果需要定制，需要加入配置信息，如下
<br>NetBoxEngineSetting setting  = new NetBoxEngineSetting();
<br>setting.setBasePackage("example.server");//扫描Handler目录，解析注解信息
<br>setting.setPort(9230);
<br>setting.setMessageType(MessageType.PROTO_TYPE);
<br>setting.setTransformType(TransformType.TCP);
<br>
<br>NetBoxEngine boxEngine = new NetBoxEngine();
<br>boxEngine.setSettings(setting);
<br>boxEngine.start();
##配置说明
<br>NetBoxEngineSetting 是 NetBoxEngine 的配置类，字段如下
<br>int port=9230;//监听端口，默认9230,
<br>int bossThreadSize=1;//Boss线程数，默认1个
<br>int workerThreadSize=4;//Work线程数，默认4个
<br>int playerThreadSize=30;//玩家管理线程数，默认30个
<br>int transformType= TransformType.TCP;//底层传输类型，默认启动TCP
<br>int messageType= MessageType.BYTE_TYPE;/**数据协议类型,默认Byte*/
<br>String basePackage="com";//Handler类（即注解了Request的类）扫描路径，默认"com"
##SmartNetBox TCP传输协议
<br>包格式：包长度(4字节)+消息码(2字节)+数据段(byte[])
<br>	1）包长度      :表示该整个数据的长度(包含用于表示长度本身的字节)
<br>    2）消息码      :表示该数据包类型
<br>	3）数据段      :采用byte[]形式存放
##SmartNetBox WebSocket 二进制传输协议（和TCP一样）
<br>包格式：包长度(4字节)+消息码(2字节)+数据段(byte[])
<br>	1）包长度      :表示该整个数据的长度(包含用于表示长度本身的字节)
<br>    2）消息码      :表示该数据包类型
<br>	3）数据段      :采用byte[]形式存放
##SmartNetBox WebSocket 文本传输协议
<br>包格式: "id#content",比如 100231#hello world!
##消息接受处理
<br>创建一个类，然后接入@Request注解，标识这是个handler类，@Protocol()注解标识协议处理,如下：
<br>@Request
<br>public class TestHandler {
<br>
<br>	@Protocol(value=1001)
<br>	public void abc2(Message msg, ISession session) throws Exception {
<br>		Example.Message.Builder message = (Example.Message.Builder)msg.parseObject(Example.Message.newBuilder());
<br>		System.out.println("name====="+message.getText());
<br>		System.out.println("id="+message.getNumber());
<br>		session.send(msg);
<br>	}
<br>}
##处理连接和断开事件
<br>继承ConnectExtension基类，接入@InOut()注解，标识这是个断开或连接处理类，然后重写connect和disConnect方法
<br>@InOut("connect/disconnect callback")
<br>public class DisConnectExtension extends ConnectExtension {
<br>	public void connect(ISession session) {
<br>		System.err.println("n,,,,,,,,连接成功、、、、、、、、、、、...");
<br>	}
<br>	public void disConnect(ISession session) {
<br>		System.err.println(",,,,,,,,,断开连接、、、、、、、、、、、...");
<br>	}
<br>}
##Session管理
SessionManager是个全局的Session管理类，利用这个类管理Session的各个行为，比如 SessionManager.getInstance().getAllSessions()

# 客户端使用说明
客户端的使用就比较灵活些，客户端是java的可以直接使用SmartNetBox的 编码/解码器，可以自己定义，如果是其他语言，则需要自己根据
协议自己定义

##后续后继续加上各个语言的客户端API,方便同学们使用

