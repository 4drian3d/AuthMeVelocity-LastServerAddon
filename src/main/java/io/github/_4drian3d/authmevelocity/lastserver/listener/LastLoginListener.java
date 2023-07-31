package io.github._4drian3d.authmevelocity.lastserver.listener;

import com.velocitypowered.api.event.AwaitingEventExecutor;

public interface LastLoginListener<E> extends AwaitingEventExecutor<E> {
    void register();
}
