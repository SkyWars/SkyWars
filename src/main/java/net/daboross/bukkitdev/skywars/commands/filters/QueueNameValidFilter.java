package net.daboross.bukkitdev.skywars.commands.filters;

import net.daboross.bukkitdev.commandexecutorbase.ArrayHelpers;
import net.daboross.bukkitdev.commandexecutorbase.CommandFilter;
import net.daboross.bukkitdev.commandexecutorbase.SubCommand;
import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class QueueNameValidFilter implements CommandFilter {

    private final SkyWars plugin;
    private final int subCommandArgNum;

    public QueueNameValidFilter(final SkyWars plugin, final int num) {
        this.plugin = plugin;
        subCommandArgNum = num;
    }

    @Override
    public boolean canContinue(final CommandSender sender, final Command baseCommand, final SubCommand subCommand, final String baseCommandLabel, final String subCommandLabel, final String[] subCommandArgs) {
        if (subCommandArgs.length <= subCommandArgNum) {
            return true;
        }
        String queueName = subCommandArgs[subCommandArgNum];
        return plugin.getGameQueue().isQueueNameValid(queueName);
    }

    @Override
    public String[] getDeniedMessage(final CommandSender sender, final Command baseCommand, final SubCommand subCommand, final String baseCommandLabel, final String subCommandLabel, final String[] subCommandArgs) {
        String deniedMessage = SkyTrans.get(TransKey.NOT_A_QUEUE_NAME, subCommandArgs[subCommandArgNum]);
        return new String[]{
                deniedMessage,
                subCommand.getHelpMessage(baseCommandLabel),
                SkyTrans.get(TransKey.GENERIC_QUEUE_LIST, ArrayHelpers.combinedWithSeperator(plugin.getConfiguration().getQueueNames(), SkyTrans.get(TransKey.GENERIC_QUEUE_LIST_COMMA))),
        };
    }
}
