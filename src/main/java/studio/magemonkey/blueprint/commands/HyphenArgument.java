package studio.magemonkey.blueprint.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HyphenArgument {
    private final String       name;
    private final List<String> arguments;

    public HyphenArgument(String name, String... arguments) {
        this.name = '-' + name;
        List<String> argumentList = new ArrayList<>(arguments.length);
        argumentList.addAll(Arrays.asList(arguments));
        this.arguments = Collections.unmodifiableList(argumentList);
    }

    public String getName() {return name;}

    public List<String> getArguments() {return arguments;}
}
