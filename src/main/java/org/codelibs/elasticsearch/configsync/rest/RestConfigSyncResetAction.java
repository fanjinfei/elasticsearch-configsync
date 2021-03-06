package org.codelibs.elasticsearch.configsync.rest;

import static org.elasticsearch.action.ActionListener.wrap;

import java.io.IOException;

import org.codelibs.elasticsearch.configsync.service.ConfigSyncService;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;

public class RestConfigSyncResetAction extends RestConfigSyncAction {

    private final ConfigSyncService configSyncService;

    @Inject
    public RestConfigSyncResetAction(final Settings settings, final RestController controller,
            final ConfigSyncService configSyncService) {
        super(settings);
        this.configSyncService = configSyncService;

        controller.registerHandler(RestRequest.Method.POST, "/_configsync/reset", this);
    }

    @Override
    protected RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient client) throws IOException {
        try {
            switch (request.method()) {
            case POST:
                return channel -> configSyncService
                        .resetSync(wrap(response -> sendResponse(channel, null), e -> sendErrorResponse(channel, e)));
            default:
                return channel -> sendErrorResponse(channel, new ElasticsearchException("Unknown request type."));
            }
        } catch (final Exception e) {
            return channel -> sendErrorResponse(channel, e);
        }
    }

    @Override
    public String getName() {
        return "configsync_reset_action";
    }
}
