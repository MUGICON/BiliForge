package cn.charlotte.biliforge.wrapper.bilibili.live.danmaku.event;

import org.jetbrains.annotations.NotNull;

/**
 * 用于接收弹幕事件的监听器。
 * 强烈建议在通常情况下使用{@link DanmakuAdapter}。本类可能会改变，而使用Adapter可以一定程度上减少本类修改时对用户代码的破坏。<br>
 * 注意，必须在DanmakuReceiver的DispatcherManager中注册了特定Dispatcher才能接收到对应事件。<br>
 *
 * @author Charlie Jiang
 * @see DanmakuAdapter
 * @since rv1
 */
public interface DanmakuListener {
    void danmakuEvent(@NotNull DanmakuEvent event);

    void watcherCountEvent(@NotNull DanmakuEvent event);

    void errorEvent(@NotNull DanmakuEvent event);

    void startStopEvent(@NotNull DanmakuEvent event);

    void statusEvent(@NotNull DanmakuEvent event);

    void welcomeVipEvent(@NotNull DanmakuEvent event);

    void giveGiftEvent(@NotNull DanmakuEvent event);

    void globalGiftEvent(@NotNull DanmakuEvent event);

    void globalAnnounceEvent(@NotNull DanmakuEvent event);
}
