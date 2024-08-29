package software.amazon.amplifyuibuilder.common;

import org.apache.http.HttpStatus;
import software.amazon.awssdk.awscore.AwsRequest;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.amplifyuibuilder.model.ResourceNotFoundException;
import software.amazon.awssdk.services.amplifyuibuilder.model.*;
import software.amazon.cloudformation.exceptions.*;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;

import java.util.function.Function;

public final class ClientWrapper {
  public static <RequestT extends AwsRequest, ResultT extends AwsResponse> AwsResponse execute(
      final AmazonWebServicesClientProxy clientProxy,
      final RequestT request,
      final Function<RequestT, ResultT> requestFunction,
      final String resourceTypeName,
      final Logger logger) {
    return execute(clientProxy, request, requestFunction, resourceTypeName, "", logger);
  }

  public static <RequestT extends AwsRequest, ResultT extends AwsResponse> AwsResponse execute(
      final AmazonWebServicesClientProxy clientProxy,
      final RequestT request,
      final Function<RequestT, ResultT> requestFunction,
      final String resourceTypeName,
      final String resourceTypeId,
      final Logger logger) {
    try {
      logger.log("Invoking with request: " + requestFunction + " | " + resourceTypeName + " | " + resourceTypeId);
      return clientProxy.injectCredentialsAndInvokeV2(request, requestFunction);
    } catch (ResourceNotFoundException e) {
      logger.log("ERROR: " + e.getMessage());
      throw new CfnNotFoundException(resourceTypeName, resourceTypeId);
    } catch (InternalServerException e) {
      logger.log("ERROR: " + e.getMessage());
      throw new CfnInternalFailureException(e);
    } catch (ServiceQuotaExceededException e) {
      logger.log("ERROR: " + e.getMessage());
      throw new CfnServiceLimitExceededException(resourceTypeName, e.getMessage());
    } catch (InvalidParameterException e) {
      logger.log("ERROR: " + e.getMessage());
      throw new CfnInvalidRequestException(e.getMessage(), e);
    } catch (AmplifyUiBuilderException e) {
      logger.log("ERROR: " + e.getMessage());
      if (e.statusCode() == HttpStatus.SC_NOT_FOUND) {
        throw new CfnNotFoundException(resourceTypeName, e.getMessage());
      }
      throw e;
    } catch (AwsServiceException e) {
      logger.log("ERROR: " + e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.log("GENERAL ERROR: " + e.getMessage());
      throw e;
    }
  }

  public static String getUIBuilderEndpoint(String region) {
    return String.format("https://amplifyuibuilder.%s.amazonaws.com", region != null ? region : "us-west-2");
  }
}
