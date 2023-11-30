package com.nhnacademy;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.nhnacademy.node.DebugNode;
import com.nhnacademy.node.MqttInNode;
import com.nhnacademy.node.MqttOutNode;
import com.nhnacademy.node.PostprocessNode;
import com.nhnacademy.node.PreprocessNode;

public class Main {
    private static void run() {

        Wire aTob = new Wire();
        Wire aDebugWire = new Wire();
        Wire bToc = new Wire();
        Wire bDebugWire = new Wire();
        Wire cTod = new Wire();
        Wire cDebugWire = new Wire();

        var a = new MqttInNode();
        var b = new PreprocessNode();
        var c = new PostprocessNode();
        var d = new MqttOutNode();
        var Adebug = new DebugNode();
        var Bdebug = new DebugNode();
        var Cdebug = new DebugNode();
        // var Ddebug = new DebugNode();

        a.wireOut(aTob);
        b.wireIn(aTob);

        a.wireOut(aDebugWire);
        Adebug.wireIn(aDebugWire);

        b.wireOut(bToc);
        c.wireIn(bToc);

        b.wireOut(bDebugWire);
        Bdebug.wireIn(bDebugWire);

        c.wireIn(bToc);
        c.wireOut(cTod);

        c.wireOut(cDebugWire);
        Cdebug.wireIn(cDebugWire);

        d.wireIn(cTod);

        a.start();
        // Adebug.start();
        b.start();
        Bdebug.start();
        c.start();
        Cdebug.start();
        // d.start();
    }

    public static void main(String[] args) {
        Options options = new Options();

        // TODO options의 설명 추가하기, help문 추가, 옵션에 없는 값이 들어올 경우 문제 try문 넣기.
        options.addOption("c", false, "");
        options.addOption("an", true, "");
        options.addOption("s", true, "");

        CommandLineParser parser = new DefaultParser();

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