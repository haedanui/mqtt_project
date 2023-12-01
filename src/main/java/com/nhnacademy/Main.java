package com.nhnacademy;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.eclipse.paho.client.mqttv3.util.Debug;

import com.nhnacademy.function.Preprocess;
import com.nhnacademy.node.DebugNode;
import com.nhnacademy.node.FunctionNode;
import com.nhnacademy.node.MqttInNode;

public class Main {
    private static void run() {
        Wire w1 = new Wire();
        Wire w2 = new Wire();

        var n1 = new MqttInNode("MqttInNode");
        var n2 = new FunctionNode(new Preprocess(), "Preprocess");
        // var n3 = new DebugNode();

        n1.wireOut(w1);
        n2.wireIn(w1);

        // n2.wireOut(w2);
        // n3.wireIn(w2);

        n1.start();
        n2.start();
        // n3.start();
    }

    public static void main(String[] args) {
        Options options = new Options();

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

        run();
    }
}