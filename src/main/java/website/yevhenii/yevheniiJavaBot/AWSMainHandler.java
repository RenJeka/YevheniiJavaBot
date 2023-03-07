package website.yevhenii.yevheniiJavaBot;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.springframework.cloud.function.adapter.aws.FunctionInvoker;
import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

//public class AWSMainHandler extends FunctionInvoker {
public class AWSMainHandler extends SpringBootRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
}
