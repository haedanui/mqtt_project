package com.nhnacademy;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.eclipse.paho.client.mqttv3.util.Debug;

import com.nhnacademy.function.Postprocess;
import com.nhnacademy.function.Preprocess;
import com.nhnacademy.node.DebugNode;
import com.nhnacademy.node.FunctionNode;
import com.nhnacademy.node.MqttInNode;
import com.nhnacademy.node.MqttOutNode;

public class Main {
    private static void testProcess() {
        Wire inTopre = new Wire();
        Wire preToPost = new Wire();
        Wire postToOut = new Wire();
        Wire inToDebug = new Wire();
        Wire preToDebug = new Wire();
        Wire postToDebug = new Wire();

        MqttInNode inNode = new MqttInNode();
        FunctionNode preFunctionNode = new FunctionNode(new Preprocess(), "PreProcess");
        FunctionNode postFunctionNode = new FunctionNode(new Postprocess(), "PostProcess");
        MqttOutNode outNode = new MqttOutNode();
        DebugNode intoDebugNode = new DebugNode();
        DebugNode preToDebugNode = new DebugNode();
        DebugNode postToDebugNode = new DebugNode();

        // 정규 연결
        inNode.wireOut(inTopre);

        preFunctionNode.wireIn(inTopre);
        preFunctionNode.wireOut(preToPost);

        postFunctionNode.wireIn(preToPost);
        postFunctionNode.wireOut(postToOut);

        outNode.wireIn(postToOut);

        // 디버그 연결
        // MqttIn to debug
        // inNode.wireOut(inToDebug);
        // intoDebugNode.wireIn(inToDebug);

        // pre to debug
        preFunctionNode.wireOut(preToDebug);
        preToDebugNode.wireIn(preToDebug);

        // post to debug
        // postFunctionNode.wireOut(postToDebug);
        // postToDebugNode.wireIn(postToDebug);

        inNode.start();
        preFunctionNode.start();
        postFunctionNode.start();
        outNode.start();
        // intoDebugNode.start();
        preToDebugNode.start();
        // postToDebugNode.start();

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

        testProcess();
    }
}