package com.example;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class UploadObject {

    public static void main(String[] args) throws IOException {

        String stringObjKeyName = "TEST_NAB_PUT_NAB_TEST*";
        String fileObjKeyName = "jeanpaulNAB";
        String fileName = "*** Path to file to upload ***";

        String clientRegion = "ap-southeast-2";
        String roleARN = "arn:aws:iam::454792302557:role/reauser-role";
        String roleSessionName = "reauser-role";
        String bucketName = "ndisanzejp-bucket";
        InputStream is = null;


        try {
            // Creating the STS client is part of your trusted code. It has
            // the security credentials you use to obtain temporary security credentials.
            AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withRegion(clientRegion)
                    .build();

            // Obtain credentials for the IAM role. Note that you cannot assume the role of an AWS root account;
            // Amazon S3 will deny access. You must use credentials for an IAM user or an IAM role.
            System.out.println(stsClient.getSessionToken().toString());
            AssumeRoleRequest roleRequest = new AssumeRoleRequest()
                    .withRoleArn(roleARN)
                    .withRoleSessionName(roleSessionName);
            AssumeRoleResult roleResponse = stsClient.assumeRole(roleRequest);
            Credentials sessionCredentials = roleResponse.getCredentials();

            // Create a BasicSessionCredentials object that contains the credentials you just retrieved.
            BasicSessionCredentials awsCredentials = new BasicSessionCredentials(
                    sessionCredentials.getAccessKeyId(),
                    sessionCredentials.getSecretAccessKey(),
                    sessionCredentials.getSessionToken());
            //This code expects that you have AWS credentials set up per:
            // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(clientRegion)
                    .build();

            // Upload a text string as a new object.
            //s3Client.putObject(bucketName, stringObjKeyName, "Uploaded String Object JPPP");
            is = new ByteArrayInputStream(Constants.book.getBytes());



            // Upload a file as a new object with ContentType and title specified.
            //PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, new ClassPathResource("classpath:testjp.html").getFile());
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("xml/text");
            metadata.addUserMetadata("x-amz-meta-book", "book library");
            //request.setMetadata(metadata);
            s3Client.putObject(bucketName,"book_library_jp_NAB",is,metadata);
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
        finally {
            if(null != is) is =null;
        }

    }
}

