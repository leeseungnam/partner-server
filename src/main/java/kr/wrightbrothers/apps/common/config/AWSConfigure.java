package kr.wrightbrothers.apps.common.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import kr.wrightbrothers.framework.util.WBAwsSns;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
@RequiredArgsConstructor
public class AWSConfigure {

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;
    @Value("${cloud.aws.ses.accessKey}")
    private String sesAccessKey;
    @Value("${cloud.aws.ses.secretKey}")
    private String sesSecretKey;
    @Value("${cloud.aws.sns.accessKey}")
    private String snsAccessKey;
    @Value("${cloud.aws.sns.secretKey}")
    private String snsSecretKey;

    @Value("${cloud.aws.region.static}")
    private String region;
    @Value("${cloud.aws.sqs.auto}")
    private Boolean isAuto;

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
                                        new BasicAWSCredentials(snsAccessKey, snsSecretKey)
                                )
                        )
                        .build()
        );
    }

    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSQSAsync) {
        SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setAmazonSqs(amazonSQSAsync);
        factory.setAutoStartup(isAuto);
        factory.setWaitTimeOut(1);
        factory.setMaxNumberOfMessages(1);
        factory.setTaskExecutor(createDefaultTaskExecutor());

        return factory;
    }

    protected AsyncTaskExecutor createDefaultTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix("SQSExecutor - ");
        threadPoolTaskExecutor.setCorePoolSize(100);
        threadPoolTaskExecutor.setMaxPoolSize(100);
        threadPoolTaskExecutor.setQueueCapacity(2);
        threadPoolTaskExecutor.afterPropertiesSet();
        return threadPoolTaskExecutor;
    }
}
