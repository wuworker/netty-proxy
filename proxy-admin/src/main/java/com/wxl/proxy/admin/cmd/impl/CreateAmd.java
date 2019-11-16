package com.wxl.proxy.admin.cmd.impl;

import com.wxl.proxy.admin.cmd.*;
import com.wxl.proxy.admin.cmd.annotation.Aommand;
import com.wxl.proxy.admin.cmd.result.EmptyResult;
import com.wxl.proxy.admin.cmd.result.HelpResult;
import com.wxl.proxy.http.HttpLoopResource;
import com.wxl.proxy.http.HttpProxyConfig;
import com.wxl.proxy.http.HttpProxyServer;
import com.wxl.proxy.http.proxy.SecondProxyConfig;
import com.wxl.proxy.http.proxy.SecondProxyType;
import com.wxl.proxy.server.ProxyServer;
import com.wxl.proxy.tcp.TcpLoopResource;
import com.wxl.proxy.tcp.TcpProxyConfig;
import com.wxl.proxy.tcp.TcpProxyServer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.net.InetSocketAddress;
import java.time.Duration;

/**
 * Create by wuxingle on 2019/11/11
 * 创建命令
 */
@Aommand(name = "create", desc = "Create Proxy Server",
        optionsMethod = "getOptions", usage = "create -n name -p port -t type [-options]")
public class CreateAmd extends AbstractAmd {

    private static Option h = Option.builder("h")
            .longOpt("help")
            .desc("help message")
            .build();

    private static Option n = Option.builder("n")
            .longOpt("name")
            .desc("proxy name")
            .hasArg()
            .argName("proxy name")
            .build();

    private static Option p = Option.builder("p")
            .longOpt("port")
            .desc("bind port")
            .hasArg()
            .argName("port number")
            .type(Number.class)
            .build();

    private static Option t = Option.builder("t")
            .longOpt("type")
            .desc("proxy type")
            .hasArg()
            .argName("tcp/http")
            .build();

    private static Option connectTimeout = Option.builder("o")
            .longOpt("connectTimeout")
            .desc("connect timeout millis")
            .hasArg()
            .argName("timeout millis")
            .type(Number.class)
            .build();

    //tcp 必须
    private static Option remoteHost = Option.builder("H")
            .longOpt("remotehost")
            .desc("tcp remote host")
            .hasArg()
            .argName("remote host")
            .build();

    //tcp 必须
    private static Option remotePort = Option.builder("P")
            .longOpt("remoteport")
            .desc("tcp remote port")
            .hasArg()
            .argName("remote port number")
            .type(Number.class)
            .build();

    //http二级代理配置
    private static Option httpProxyType = Option.builder()
            .longOpt("secProxyType")
            .desc("second proxy type:[http/socks4/socks5]")
            .hasArg()
            .argName("second proxy type")
            .build();

    private static Option httpProxyHost = Option.builder()
            .longOpt("secProxyHost")
            .desc("second proxy host")
            .hasArg()
            .argName("second proxy host")
            .build();

    private static Option httpProxyPort = Option.builder()
            .longOpt("secProxyPort")
            .desc("second proxy port")
            .hasArg()
            .type(Number.class)
            .argName("second proxy port")
            .build();

    private static Option httpProxyUsername = Option.builder()
            .longOpt("secProxyUsername")
            .desc("second proxy username")
            .hasArg()
            .argName("second proxy username")
            .build();

    private static Option httpProxyPassword = Option.builder()
            .longOpt("secProxyPassword")
            .desc("second proxy password")
            .hasArg()
            .argName("second proxy password")
            .build();

    public CreateAmd() {
    }

    public CreateAmd(CommandLine cmdline) {
        super(cmdline);
    }

    /**
     * 命令选项
     */
    public static Options getOptions() {
        Options options = new Options();
        options.addOption(h)
                .addOption(n)
                .addOption(p)
                .addOption(t)
                .addOption(connectTimeout)
                .addOption(remoteHost)
                .addOption(remotePort)
                .addOption(httpProxyType)
                .addOption(httpProxyHost)
                .addOption(httpProxyPort)
                .addOption(httpProxyUsername)
                .addOption(httpProxyPassword);
        return options;
    }


    @Override
    protected AmdResult invoke(CommandLine cmdline, AmdContext context) throws AmdInvokeException {
        if (cmdline == null) {
            throw new AmdInvokeException("'create' must has options");
        }
        if (cmdline.getOptions().length == 0) {
            AmdDefinition definition = AmdDefinitionBuilder.of(CreateAmd.class);
            AmdFormatter formatter = context.getAmdFormatter();
            return new HelpResult(definition, formatter);
        }
        if (!cmdline.hasOption("n") || !cmdline.hasOption("p") || !cmdline.hasOption("t")) {
            throw new AmdInvokeException("'create' must has options -n -p -t");
        }

        String name = cmdline.getOptionValue("n");
        int port = parseToNumber(cmdline, "p");
        String type = cmdline.getOptionValue("t");

        ProxyServer<?> server;
        switch (type) {
            case "tcp":
                server = createTcpProxyServer(name, port, cmdline, context);
                break;
            case "http":
                server = createHttpProxyServer(name, port, cmdline, context);
                break;
            default:
                throw new AmdInvokeException("'create' type must is [tcp/http]");
        }

        context.getProxyServerRegistry().registry(server);
        return new EmptyResult();
    }

    /**
     * 创建tcp服务
     */
    private TcpProxyServer createTcpProxyServer(String name, int port,
                                                CommandLine cmdline, AmdContext context)
            throws AmdInvokeException {
        if (!cmdline.hasOption("H") || !cmdline.hasOption("P")) {
            throw new AmdInvokeException("'create' tcp server must has options -H -P");
        }
        String remoteHost = cmdline.getOptionValue("H");
        int remotePort = parseToNumber(cmdline, "P");

        Integer timeout = null;
        if (cmdline.hasOption("o")) {
            timeout = parseToNumber(cmdline, "o");
        }

        TcpProxyConfig config = TcpProxyConfig.builder()
                .serverName(name)
                .bindPort(port)
                .remoteAddress(new InetSocketAddress(remoteHost, remotePort))
                .connectTimeout(timeout == null ? null : Duration.ofMillis(timeout))
                .build();

        TcpLoopResource loopResource = context.getBean(TcpLoopResource.class);

        return new TcpProxyServer(config, loopResource);
    }

    /**
     * 创建http服务
     */
    private HttpProxyServer createHttpProxyServer(String name, int port,
                                                  CommandLine cmdline, AmdContext context)
            throws AmdInvokeException {

        Integer timeout = null;
        if (cmdline.hasOption("o")) {
            timeout = parseToNumber(cmdline, "o");
        }

        SecondProxyConfig secondProxyConfig = null;
        // http二级代理
        if (cmdline.hasOption("secProxyType")) {
            SecondProxyType type = SecondProxyType.parse(cmdline.getOptionValue("secProxyType"));
            if (type == null) {
                throw new AmdInvokeException("unknown secProxyType:" + cmdline.getOptionValue("secProxyType"));
            }
            if (!cmdline.hasOption("secProxyHost") || !cmdline.hasOption("secProxyPort")) {
                throw new AmdInvokeException("http second proxy must has options --secProxyHost --secProxyPort");
            }
            String remoteHost = cmdline.getOptionValue("secProxyHost");
            int remotePort = parseToNumber(cmdline, "secProxyPort");

            secondProxyConfig = SecondProxyConfig.builder()
                    .type(type)
                    .address(new InetSocketAddress(remoteHost, remotePort))
                    .username(cmdline.getOptionValue("secProxyUsername"))
                    .password(cmdline.getOptionValue("secProxyPassword"))
                    .build();
        }

        HttpProxyConfig config = HttpProxyConfig.builder()
                .serverName(name)
                .bindPort(port)
                .secondProxy(secondProxyConfig)
                .connectTimeout(timeout == null ? null : Duration.ofMillis(timeout))
                .build();

        HttpLoopResource httpLoopResource = context.getBean(HttpLoopResource.class);
        return new HttpProxyServer(config, httpLoopResource);
    }


    private int parseToNumber(CommandLine cmdline, String opt) throws AmdInvokeException {
        try {
            return ((Number) cmdline.getParsedOptionValue(opt)).intValue();
        } catch (ParseException e) {
            throw new AmdInvokeException("'create' option '" + opt + "' must is number");
        }
    }

}









