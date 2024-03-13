package com.ktk.workhuservice.service.microsoft;

import com.ktk.workhuservice.config.MicrosoftConfig;
import com.ktk.workhuservice.data.Activity;
import com.ktk.workhuservice.enums.TransactionType;
import com.ktk.workhuservice.service.ActivityItemService;
import com.ktk.workhuservice.service.Utils;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.graph.models.Drive;
import com.microsoft.graph.models.FieldValueSet;
import com.microsoft.graph.models.ListItem;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.kiota.RequestInformation;
import com.microsoft.kiota.authentication.AuthenticationProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class MicrosoftService {

    private static ConfidentialClientApplication app;
    private final MicrosoftConfig config;
    private ActivityItemService activityItemService;

    public MicrosoftService(MicrosoftConfig config, ActivityItemService activityItemService) {
        this.config = config;
        this.activityItemService = activityItemService;
    }

    public void sendActivityToSharePointListItem(Activity activity, byte[] activityWorkbook) throws Exception {
        var sumHours = activityItemService.sumHoursByActivity(activity.getId());
        BuildConfidentialClientObject();
        IAuthenticationResult accessTokenResult = getAccessTokenByClientCredentialGrant();

        var authProvider = new AuthenticationProvider() {
            @Override
            public void authenticateRequest(RequestInformation request, Map<String, Object> additionalAuthenticationContext) {
                request.headers.add("Authorization", "Bearer " + accessTokenResult.accessToken());
                additionalAuthenticationContext.put("Authorization", "Bearer " + accessTokenResult.accessToken());
            }
        };

        GraphServiceClient graphClient = new GraphServiceClient(authProvider);

        ListItem listItem = new ListItem();
        FieldValueSet fields = new FieldValueSet();
        HashMap<String, Object> additionalData = new HashMap<String, Object>();
        if (activity.getTransactionType().equals(TransactionType.HOURS)) {
            createPaidListItem(activity, additionalData, graphClient, sumHours);
            fields.setAdditionalData(additionalData);
            listItem.setFields(fields);
            ListItem result = graphClient.sites().bySiteId(config.getSiteId()).lists().byListId(config.getPaidJobsId()).items().post(listItem);
            sendXlsxToSharepointFolder(graphClient, activity, sumHours);
        } else if (activity.getTransactionType().equals(TransactionType.DUKA_MUNKA)) {
            createUnpaidListItem(activity, additionalData, graphClient, sumHours);
            fields.setAdditionalData(additionalData);
            listItem.setFields(fields);
            ListItem result = graphClient.sites().bySiteId(config.getSiteId()).lists().byListId(config.getUnpaidJobsId()).items().post(listItem);
            sendXlsxToSharepointFolder(graphClient, activity, sumHours);
        }

    }

    private Drive getDriveId(GraphServiceClient graphServiceClient) {
        return graphServiceClient.sites().bySiteId(config.getSiteId()).drive().get();
    }

    private void sendXlsxToSharepointFolder(GraphServiceClient graphServiceClient, Activity activity, double sumHours) throws IOException {
        Drive drive = getDriveId(graphServiceClient);
        var items = activityItemService.findByActivity(activity.getId());
        var path = Utils.createXlsxFromActivity(activity, items, sumHours, new ClassPathResource("imports/docs/munkalap_sablon_uj.xlsx").getInputStream());
        graphServiceClient.drives().byDriveId(drive.getId()).root().content().withUrl(buildUri(drive.getId(), buildFilename(activity)))
                .put(new FileInputStream(path));
    }

    private String buildFilename(Activity activity) {
        return buildYearString(activity.getActivityDateTime()) + "_" + Utils.changeSpecChars(activity.getDescription()) + ".xlsx";
    }

    private String buildYearString(LocalDateTime dateTime) {
        return String.valueOf(dateTime.getYear()) + dateTime.getMonthValue() + dateTime.getDayOfMonth();
    }

    private String buildUri(String driveId, String filename) {
        return "https://graph.microsoft.com/v1.0/sites/" + config.getSiteId() +
                "/drives/" +
                driveId +
                "/root:/WORK/Munkalapok/" + filename + ":/content";
    }

    private void createUnpaidListItem(Activity activity, HashMap<String, Object> additionalData, GraphServiceClient graphClient, double sumHours) {
        var teamsLookUpId = getUserTeamsListId(graphClient, activity.getResponsible().getId());
        additionalData.put("Title", activity.getDescription());
        additionalData.put("ResponsibleLookupId", teamsLookUpId);
        additionalData.put("Activitydate", activity.getActivityDateTime().toString());
        additionalData.put("Hours", String.valueOf(sumHours));
        additionalData.put("Credits", String.valueOf(sumHours * 1000));
        additionalData.put("Appactivityid", String.valueOf(activity.getId()));
    }

    private void createPaidListItem(Activity activity, HashMap<String, Object> additionalData, GraphServiceClient graphClient, double sumHours) {
        var teamsLookUpId = getUserTeamsListId(graphClient, activity.getEmployer().getId());
        additionalData.put("Title", activity.getDescription());
        additionalData.put("EmployerLookupId", teamsLookUpId);
        additionalData.put("Activitydate", activity.getActivityDateTime().toString());
        additionalData.put("Hours", String.valueOf(sumHours));
        additionalData.put("MySharecredits", String.valueOf(sumHours * 2000));
        additionalData.put("WorkId", String.valueOf(activity.getId()));
    }

    private String getUserTeamsListId(GraphServiceClient graphClient, Long id) {
        var result = graphClient.sites().bySiteId(config.getSiteId()).lists().byListId(config.getWorkUsersId()).items().get(requestOptions -> {
            requestOptions.queryParameters.expand = new String[]{"fields"};
            requestOptions.queryParameters.filter = "fields/Title eq " + id;
            requestOptions.headers.add("Prefer", "HonorNonIndexedQueriesWarningMayFailRandomly");
        });

        return result.getValue().get(0).getFields().getId();
    }

    private void BuildConfidentialClientObject() throws Exception {
        app = ConfidentialClientApplication.builder(
                config.getClientId(),
                ClientCredentialFactory.createFromSecret(config.getClientSecret()))
                .authority(config.getRealm())
                .build();
    }

    private IAuthenticationResult getAccessTokenByClientCredentialGrant() throws Exception {
        ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
                Collections.singleton(config.getScope()))
                .build();

        CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);
        return future.get();
    }

}
