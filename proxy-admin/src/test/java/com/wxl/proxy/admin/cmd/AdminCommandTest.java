package com.wxl.proxy.admin.cmd;

import com.wxl.proxy.admin.cmd.impl.LsAmd;
import com.wxl.proxy.handler.ProxyChannelInitializer;
import com.wxl.proxy.server.ProxyConfig;
import com.wxl.proxy.server.ProxyServer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by wuxingle on 2019/10/26
 */
public class AdminCommandTest {


    @Test
    public void test() {
        Pattern pattern = Pattern.compile("(\".+?\")|('.+?')|([^\\s])+");
        Matcher matcher = pattern.matcher("ls -l \"aa gg cc\" 'a b c'\t\"i ma is 'wxl' haha\" 'i is \"gg\"' sdf df");
        while (matcher.find()) {
            System.out.println(matcher.group());
            System.out.println("---------------------");
        }
    }

    @Test
    public void test1() throws Exception {

        AmdDefinition definition = new AmdDefinition() {
            @Override
            public String description() {
                return "ls all server";
            }

            @Override
            public Class<? extends Amd> type() {
                return LsAmd.class;
            }

            @Override
            public boolean supportCmdline() {
                return true;
            }

            @Override
            public Supplier<Options> options() {
                return new LsAmd.LsOptions();
            }
        };

        AmdFormatter formatter = new DefaultAmdFormatter();

        DefaultParser defaultParser = new DefaultParser();
        CommandLine commandLine = defaultParser.parse(definition.options().get(), new String[]{
                "-l"
        });
        LsAmd ls = new LsAmd("ls",commandLine);


        AmdContext context = new AmdContext() {
            @Override
            public List<ProxyServer> proxyServers() {
                return Arrays.asList(new PS1(), new PS2());
            }

            @Override
            public AmdFormatter formatter() {
                return formatter;
            }
        };

        AmdResult result = ls.invoke(context);

        System.out.println(result);
    }


    private static class PS1 implements ProxyServer {
        @Override
        public String name() {
            return "ps2-proxy";
        }

        @Override
        public int bindPort() {
            return 8888;
        }

        @Override
        public ProxyConfig getConfig() {
            return new ProxyConfig.ProxyConfigBuilder()
                    .bindPort(9999)
                    .build();
        }

        @Override
        public void setServerHandlerInitializer(ProxyChannelInitializer initializer) {

        }

        @Override
        public void setFrontHandlerInitializer(ProxyChannelInitializer initializer) {

        }

        @Override
        public void setBackendHandlerInitializer(ProxyChannelInitializer initializer) {

        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public boolean isRunning() {
            return false;
        }
    }

    private static class PS2 implements ProxyServer {
        @Override
        public String name() {
            return "ps1-proxy";
        }

        @Override
        public int bindPort() {
            return 9999;
        }

        @Override
        public ProxyConfig getConfig() {
            return new ProxyConfig.ProxyConfigBuilder()
                    .bindPort(11111)
                    .build();
        }

        @Override
        public void setServerHandlerInitializer(ProxyChannelInitializer initializer) {

        }

        @Override
        public void setFrontHandlerInitializer(ProxyChannelInitializer initializer) {

        }

        @Override
        public void setBackendHandlerInitializer(ProxyChannelInitializer initializer) {

        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public boolean isRunning() {
            return false;
        }
    }
}