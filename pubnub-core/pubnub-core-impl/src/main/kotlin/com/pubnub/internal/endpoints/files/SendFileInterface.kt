package com.pubnub.internal.endpoints.files

import com.pubnub.api.endpoints.remoteaction.ExtendedRemoteAction
import com.pubnub.api.models.consumer.files.PNFileUploadResult

interface SendFileInterface : ExtendedRemoteAction<PNFileUploadResult>
