package com.pubnub.api.integration.objects;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.PubNubException;
import com.pubnub.api.UserId;
import com.pubnub.api.enums.PNLogVerbosity;
import com.pubnub.api.integration.util.ITTestConfig;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;

import java.util.UUID;

import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assume.assumeThat;

public abstract class ObjectsApiBaseIT {
    //See README.md in integrationTest directory for more info on running integration tests
    private ITTestConfig itTestConfig = ConfigFactory.create(ITTestConfig.class, System.getenv());

    protected final PubNub pubNubUnderTest = pubNub();

    private PubNub pubNub() {
        PNConfiguration pnConfiguration;
        try {
            pnConfiguration = new PNConfiguration(new UserId("pn-" + UUID.randomUUID()));
        } catch (PubNubException e) {
            throw new RuntimeException(e);
        }
        pnConfiguration.setSubscribeKey(itTestConfig.subscribeKey());
        pnConfiguration.setLogVerbosity(PNLogVerbosity.BODY);

        return PubNub.create(pnConfiguration);
    }

    @Before
    public void assumeTestsAreConfiguredProperly() {
        assumeThat("Subscription key must be set in test.properties", itTestConfig.subscribeKey(), not(isEmptyOrNullString()));
    }
}
