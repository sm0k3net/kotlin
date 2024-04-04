package com.pubnub.internal.endpoints.presence;

import com.pubnub.api.endpoints.presence.GetState;
import com.pubnub.api.endpoints.remoteaction.ExtendedRemoteAction;
import com.pubnub.api.models.consumer.presence.PNGetStateResult;
import com.pubnub.internal.PubNubCore;
import com.pubnub.internal.endpoints.DelegatingEndpoint;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Setter
@Accessors(chain = true, fluent = true)
public class GetStateImpl extends DelegatingEndpoint<PNGetStateResult> implements GetState {

    private List<String> channels = new ArrayList<>();
    private List<String> channelGroups = new ArrayList<>();
    private String uuid;

    public GetStateImpl(PubNubCore pubnub) {
        super(pubnub);
        uuid = pubnub.getConfiguration().getUuid();
    }

    @Override
    protected ExtendedRemoteAction<PNGetStateResult> createAction() {
        return pubnub.getPresenceState(
                channels,
                channelGroups,
                uuid
        );
    }
}