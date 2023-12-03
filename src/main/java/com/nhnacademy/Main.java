package com.nhnacademy;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.json.JSONObject;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;

import com.nhnacademy.function.Postprocess;
import com.nhnacademy.function.Preprocess;
import com.nhnacademy.function.SayHello;
import com.nhnacademy.node.DebugNode;
import com.nhnacademy.node.FunctionNode;
import com.nhnacademy.node.MqttInNode;
import com.nhnacademy.node.MqttOutNode;

import lombok.extern.slf4j.Slf4j;

import javax.script.*;

@Slf4j
public class Main {

    private static void runJavascript() {
        try (Engine engine = Engine.newBuilder()
        .option("engine.WarnInterpreterOnly", "false")
        .build()) {

            try (Context ctx = Context.newBuilder("js").engine(engine).build()) {
                ctx.eval("js", "print('hello world');");
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private static void test() {
        Wire w1 = new Wire();
        Wire w2 = new Wire();
        Wire w3 = new Wire();
        Wire w4 = new Wire();

        MqttInNode in = new MqttInNode("in");
        FunctionNode pre = new FunctionNode(new Preprocess(), "pre");
        FunctionNode post = new FunctionNode(new Postprocess(), "post");
        DebugNode debug = new DebugNode();
        MqttOutNode out = new MqttOutNode();

        in.wireOut(w1);
        pre.wireIn(w1);

        pre.wireOut(w2);
        post.wireIn(w2);

        post.wireOut(w3);
        debug.wireIn(w3);

        post.wireOut(w4);
        out.wireIn(w4);

        in.start();
        pre.start();
        post.start();
        debug.start();
        out.start();
    }

    public static void main(String[] args) {
        // Options options = new Options();

        // TODO options의 설명 추가하기, help문 추가, 옵션에 없는 값이 들어올 경우 문제 try문 넣기.
        // options.addOption("c", false, "");
        // options.addOption("an", true, "");
        // options.addOption("s", true, "");

        // CommandLineParser parser = new DefaultParser();

        // try {
        // CommandLine cmd = parser.parse(options, args);

        // if (!cmd.hasOption("c") && cmd.getOptions().length < 2) { // an, s 값을 만족하지 못할
        // 경우.
        // throw new IllegalArgumentException(); // TODO 인자가 모자를 경우 발생하는 익셉션 만들기.
        // }

        // if (cmd.hasOption("c")) {
        // Config.setCurrentConfig(new Config(cmd.getOptionValue("an"),
        // cmd.getOptionValue("s")));
        // }
        // } catch (Exception e) {
        // HelpFormatter formatter = new HelpFormatter();
        // formatter.printHelp("mqtt [option] ... ", options);
        // }

        test();
    }
}