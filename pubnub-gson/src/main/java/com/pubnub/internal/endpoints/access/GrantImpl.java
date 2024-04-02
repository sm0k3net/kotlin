package com.pubnub.internal.endpoints.access;

import com.pubnub.api.endpoints.access.Grant;
import com.pubnub.api.endpoints.remoteaction.ExtendedRemoteAction;
import com.pubnub.api.endpoints.remoteaction.MappingRemoteAction;
import com.pubnub.api.models.consumer.access_manager.PNAccessManagerGrantResult;
import com.pubnub.internal.PubNubCore;
import com.pubnub.internal.endpoints.DelegatingEndpoint;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Setter
@Accessors(chain = true, fluent = true)
public class GrantImpl extends DelegatingEndpoint<PNAccessManagerGrantResult> implements Grant {

    private boolean read;
    private boolean write;
    private boolean manage;
    private boolean delete;
    private boolean get;
    private boolean update;
    private boolean join;
    private int ttl = -1;

    private List<String> authKeys = new ArrayList<>();
    private List<String> channels = new ArrayList<>();
    private List<String> channelGroups = new ArrayList<>();
    private List<String> uuids = Collections.emptyList();

    public GrantImpl(PubNubCore pubnub) {
        super(pubnub);
    }

    @Override
    protected ExtendedRemoteAction<PNAccessManagerGrantResult> createAction() {
        return new MappingRemoteAction<>(
                pubnub.grant(
                        read,
                        write,
                        manage,
                        delete,
                        get,
                        update,
                        join,
                        ttl,
                        authKeys,
                        channels,
                        channelGroups,
                        uuids
                ), PNAccessManagerGrantResult::from
        );
    }
}