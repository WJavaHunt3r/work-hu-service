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
import com.microsoft.graph.models.*;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody;
import com.microsoft.kiota.RequestInformation;
import com.microsoft.kiota.authentication.AuthenticationProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
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

    public void sendActivityToSharePointListItem(Activity activity) throws Exception {
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
        HashMap<String, Object> additionalData = new HashMap<>();

        var items = activityItemService.findByActivity(activity.getId());
        String xlsx = Utils.createXlsxFromActivity(activity, items, sumHours, new ClassPathResource("imports/docs/munkalap_sablon_uj.xlsx").getInputStream());

        if (activity.getTransactionType().equals(TransactionType.HOURS)) {
            createPaidListItem(activity, additionalData, graphClient, sumHours);
            fields.setAdditionalData(additionalData);
            listItem.setFields(fields);
            graphClient.sites().bySiteId(config.getSiteId()).lists().byListId(config.getPaidJobsId()).items().post(listItem);
            sendXlsxToSharepointFolder(graphClient, activity, xlsx);
            sendMailToEmployer(graphClient, activity, sumHours, xlsx);
        } else if (activity.getTransactionType().equals(TransactionType.DUKA_MUNKA)) {
            createUnpaidListItem(activity, additionalData, graphClient, sumHours);
            fields.setAdditionalData(additionalData);
            listItem.setFields(fields);
            graphClient.sites().bySiteId(config.getSiteId()).lists().byListId(config.getUnpaidJobsId()).items().post(listItem);
            sendXlsxToSharepointFolder(graphClient, activity, xlsx);
        }

    }

    private void sendMailToEmployer(GraphServiceClient graphClient, Activity activity, double sumHours, String xlsx) throws IOException {

        Message message = new Message();
        ItemBody body = new ItemBody();
        body.setContentType(BodyType.Text);

        body.setContent(createContent(activity, sumHours));
        message.setBody(body);
        message.setSubject(String.format("Befizetés:  %s", activity.getDescription()));

        LinkedList<Recipient> toRecipientsList = new LinkedList<>();
        Recipient toRecipients = new Recipient();
        EmailAddress emailAddress1 = new EmailAddress();
        emailAddress1.setAddress(activity.getEmployer().getEmail());
        toRecipients.setEmailAddress(emailAddress1);
        toRecipientsList.add(toRecipients);
        message.setToRecipients(toRecipientsList);

        FileAttachment attachment = new FileAttachment();
        attachment.setName(buildFilename(activity));
        attachment.setContentType("text/plain");
        attachment.setContentBytes(new FileInputStream(xlsx).readAllBytes());

        ArrayList<Attachment> attachmentList = new ArrayList<>();
        attachmentList.add(attachment);
        message.setAttachments(attachmentList);

        SendMailPostRequestBody request = new SendMailPostRequestBody();
        request.setMessage(message);
        request.setSaveToSentItems(false);

        graphClient.users().byUserId(config.getMyshareMail()).sendMail().post(request);

    }

    private String createContent(Activity activity, double sumHours) {
        String date = Utils.formatDate(activity.getActivityDateTime().toLocalDate());
        return "Kedves " + activity.getEmployer().getFullName() + "!\n\n" + date +
                " dátummal egy munka került rögzítésre rendszerünkbe. A munka részleteit a csatolt munkalapon láthatod.\n\nAz elvégzett munka utáni befizetendő összeg: " + (int) (sumHours * 2000) +
                "Ft\n\nKérjük, a befizetendő összeget a MyShare számlára utald el!\nSzámlaszám: 10700323-43750203-52000001\nKözlemény: " + date.replace(".", "") + " " + activity.getDescription()
                + "\n\nHa már utaltál, akkor tekintsd tárgytalannak az emailt. Kérdés esetén erre az email-re válaszolva veheted fel velünk a kapcsolatot. \n\nÜdvözlettel, \nMyShare csapat";

    }

    private Drive getDriveId(GraphServiceClient graphServiceClient) {
        return graphServiceClient.sites().bySiteId(config.getSiteId()).drive().get();
    }

    private void sendXlsxToSharepointFolder(GraphServiceClient graphServiceClient, Activity activity, String xlsx) throws IOException {
        Drive drive = getDriveId(graphServiceClient);
        graphServiceClient.drives().byDriveId(drive.getId()).root().content().withUrl(buildUri(drive.getId(), buildFilename(activity)))
                .put(new FileInputStream(xlsx));
    }

    private String buildFilename(Activity activity) {
        return Utils.formatDate(activity.getActivityDateTime().toLocalDate()).replace(".", "") + "_" + Utils.changeSpecChars(activity.getDescription().trim()) + ".xlsx";
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
