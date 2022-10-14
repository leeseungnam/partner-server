package kr.wrightbrothers.apps.common.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import kr.wrightbrothers.framework.util.WBAwsSns;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
@RequiredArgsConstructor
public class AWSConfigure {

    @Value("${cloud.aws.ses.accessKey}")
    private String sesAccessKey;
    @Value("${cloud.aws.ses.secretKey}")
    private String sesSecretKey;
    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;
    @Value("${cloud.aws.sqs.auto}")
    private Boolean use;

    @Bean
    public SesClient amazonSimpleEmailServiceAsync() {
        return SesClient.builder()
                .credentialsProvider(() -> AwsBasicCredentials.create(sesAccessKey, sesSecretKey))
                .region(Region.AP_NORTHEAST_2)
                .build();
    }

    @Bean
    public WBAwsSns amazonSimpleNotificationService() {
        return new WBAwsSns(
                AmazonSNSClientBuilder
                        .standard().withRegion(region)
                        .withCredentials(
                                new AWSStaticCredentialsProvider(
                                        new BasicAWSCredentials(accessKey, secretKey)
                                )
                        )
                        .build()
        );
    }

}
