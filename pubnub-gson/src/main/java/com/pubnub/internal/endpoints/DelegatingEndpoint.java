package com.pubnub.internal.endpoints;

import com.pubnub.api.PubNubException;
import com.pubnub.api.endpoints.Endpoint;
import com.pubnub.api.endpoints.remoteaction.ExtendedRemoteAction;
import com.pubnub.api.enums.PNOperationType;
import com.pubnub.api.v2.callbacks.Result;
import com.pubnub.internal.PubNubCore;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class DelegatingEndpoint<T> implements Endpoint<T> {
    protected final PubNubCore pubnub;
    private volatile ExtendedRemoteAction<T> remoteAction = null;

    public DelegatingEndpoint(PubNubCore pubnub) {
        this.pubnub = pubnub;
    }

    protected abstract ExtendedRemoteAction<T> createAction();

    public T sync() throws PubNubException {
        validateParams();
        return getRemoteAction().sync();
    }

    protected void validateParams() throws PubNubException {
    }

    protected ExtendedRemoteAction<T> getRemoteAction() {
        ExtendedRemoteAction<T> localRef = remoteAction;
        if (localRef == null) {
            synchronized (this) {
                localRef = remoteAction;
                if (localRef == null) {
                    localRef = createAction();
                    remoteAction = localRef;
                }
            }
        }
        return localRef;
    }

    @Override
    public void async(@NotNull Consumer<Result<T>> callback) {
        try {
            validateParams();
        } catch (PubNubException pubnubException) {
            callback.accept(Result.failure(pubnubException));
            return;
        }
        getRemoteAction().async(callback);
    }

    @Override
    public void retry() {
        getRemoteAction().retry();
    }

    @Override
    public void silentCancel() {
        getRemoteAction().silentCancel();
    }

    @NotNull
    @Override
    public PNOperationType operationType() {
        return getRemoteAction().operationType();
    }

    public PNOperationType getOperationType() {
        return operationType();
    }
}