package kr.wrightbrothers.apps.common.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesAsyncClient;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
@RequiredArgsConstructor
public class AWSConfigure {

    @Value("${cloud.aws.ses.accessKey}")
    private String accessKey;
    @Value("${cloud.aws.ses.secretKey}")
    private String secretKey;

    @Bean
    public SesClient amazonSimpleEmailServiceAsync() {
        return SesClient.builder()
                .credentialsProvider(() -> AwsBasicCredentials.create(accessKey, secretKey))
                .region(Region.AP_NORTHEAST_2)
                .build();
    }

}
