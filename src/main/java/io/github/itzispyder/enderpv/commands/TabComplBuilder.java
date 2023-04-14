package io.github.itzispyder.enderpv.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

public class TabComplBuilder {

    private Map<Integer,List<String>> entries = new HashMap<>();
    private final CommandSender sender;
    private final Command command;
    private final String alias;
    private final String[] args;

    public TabComplBuilder(CommandSender sender, Command command, String alias, String[] args) {
        this.sender = sender;
        this.command = command;
        this.alias = alias;
        this.args = args;
    }

    /**
     * Adds to the tab completion
     * @param atIndex should be a number above or equal to 1
     * @param entry string array
     * @param condition condition
     */
    public TabComplBuilder add(int atIndex, String[] entry, boolean condition) {
        if (condition) add(atIndex,entry);
        return this;
    }

    /**
     * Adds to the tab completion
     * @param atIndex should be a number above or equal to 1
     * @param entry string array
     */
    public TabComplBuilder add(int atIndex, String[] entry) {
        atIndex = Math.max(1,atIndex);
        entries.put(atIndex,Arrays.stream(entry).toList());
        return this;
    }

    public TabComplBuilder add(int atIndex, List<String> entry, boolean condition) {
        if (condition) add(atIndex,entry);
        return this;
    }

    public TabComplBuilder add(int atIndex, List<String> entry) {
        entries.put(atIndex,entry);
        return this;
    }

    public List<String> build() {
        List<String> list = new ArrayList<>(entries.get(args.length) != null ? entries.get(args.length) : new ArrayList<>());
        list.removeIf(s -> !s.toLowerCase().contains(args[args.length - 1].toLowerCase()));
        return list;
    }
}
