package top.mrxiaom.sweet.buildmobs;

import top.mrxiaom.pluginbase.func.language.Language;
import top.mrxiaom.pluginbase.func.language.Message;

import static top.mrxiaom.pluginbase.func.language.LanguageFieldAutoHolder.field;

@Language(prefix = "messages.")
public class Messages {
    @Language(prefix = "messages.command.")
    public static class Command {
        public static final Message reload = field("&a配置文件已重载");
        public static final Message select__start = field("&a已开始选取区域，请左键、右键点击方块，选择区域的对角点");
        public static final Message select__stop = field("&a选取区域已结束");
        public static final Message select__save__not_started = field("&e你需要先开始选取区域才能保存构筑");
        public static final Message select__save__not_selected = field("&e你需要先选择区域对角点才能保存构筑，请左键、右键点击方块，选择区域的对角点");
        public static final Message select__save__error = field("&e保存区域时出现错误:&b %error%");
        public static final Message select__save__success = field("&a已保存选取的区域到&e output.yml &a文件");
    }

    @Language(prefix = "messages.selection.")
    public static class Selection {
        public static final Message select_pos1 = field("&f已选择区域对角点1 &7(%x%, %y%, %z%)");
        public static final Message select_pos2 = field("&f已选择区域对角点2 &7(%x%, %y%, %z%)");
        public static final Message available = field("&f区域已选中 &7(大小 %size_x% x %size_y% x %size_z%)&f，请&e面向该构筑的正面&f，然后执行命令&e /buildmobs save &f以保存构筑到临时配置中");
    }
}
