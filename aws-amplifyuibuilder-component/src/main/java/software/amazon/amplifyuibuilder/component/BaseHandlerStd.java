package software.amazon.amplifyuibuilder.component;

import org.apache.http.HttpStatus;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.amplifyuibuilder.AmplifyUiBuilderClient;
import software.amazon.awssdk.services.amplifyuibuilder.model.*;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInternalFailureException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

// Functionality that could be shared across Create/Read/Update/Delete/List Handlers

public abstract class BaseHandlerStd extends BaseHandler<CallbackContext> {

  protected Logger logger;

  @Override
  public final ProgressEvent<ResourceModel, CallbackContext> handleRequest(
      final AmazonWebServicesClientProxy proxy,
      final ResourceHandlerRequest<ResourceModel> request,
      final CallbackContext callbackContext,
      final Logger logger
  ) {
    return handleRequest(
        proxy,
        request,
        callbackContext != null ? callbackContext : new CallbackContext(),
        proxy.newProxy(ClientBuilder::getClient),
        logger
    );
  }

  protected abstract ProgressEvent<ResourceModel, CallbackContext> handleRequest(
      final AmazonWebServicesClientProxy proxy,
      final ResourceHandlerRequest<ResourceModel> request,
      final CallbackContext callbackContext,
      final ProxyClient<AmplifyUiBuilderClient> proxyClient,
      final Logger logger
  );

  protected CreateComponentResponse createComponent(
      CreateComponentRequest request,
      ProxyClient<AmplifyUiBuilderClient> client
  ) {
    try {
      return client.injectCredentialsAndInvokeV2(
          request,
          client.client()::createComponent
      );
    } catch (ResourceNotFoundException e) {
      throw new CfnNotFoundException(ResourceModel.TYPE_NAME, e.getMessage());
    } catch (InternalServerException e) {
      throw new CfnInternalFailureException(e);
    } catch (InvalidParameterException e) {
      throw new CfnInvalidRequestException(e.getMessage(), e);
    } catch (final AmplifyUiBuilderException e) {
      if (e.statusCode() == HttpStatus.SC_NOT_FOUND || e.statusCode() == HttpStatus.SC_BAD_REQUEST) {
        throw new CfnNotFoundException(ResourceModel.TYPE_NAME, e.getMessage());
      }
      throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
    } catch (final AwsServiceException e) {
      throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
    }
  }

    protected DeleteComponentResponse deleteComponent (
        DeleteComponentRequest request,
        ProxyClient < AmplifyUiBuilderClient > client
  ){
      DeleteComponentResponse deleteResponse;
      try {
        deleteResponse = client.injectCredentialsAndInvokeV2(
            request,
            client.client()::deleteComponent
        );
        return deleteResponse;
      } catch (ResourceNotFoundException e) {
        throw new CfnNotFoundException(ResourceModel.TYPE_NAME, request.id());
      } catch (InternalServerException e) {
        throw new CfnInternalFailureException(e);
      } catch (InvalidParameterException e) {
        throw new CfnInvalidRequestException(e.getMessage(), e);
      } catch (final AmplifyUiBuilderException e) {
        if (e.statusCode() == HttpStatus.SC_NOT_FOUND || e.statusCode() == HttpStatus.SC_BAD_REQUEST) {
          throw new CfnNotFoundException(ResourceModel.TYPE_NAME, e.getMessage());
        }
        throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
      } catch (final AwsServiceException e) {
        throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
      }
    }

    protected UpdateComponentResponse updateComponent (
        UpdateComponentRequest request,
        ProxyClient < AmplifyUiBuilderClient > client
  ){
      UpdateComponentResponse updateResponse;
      try {
        updateResponse =
            client.injectCredentialsAndInvokeV2(
                request,
                client.client()::updateComponent
            );
        return updateResponse;
      } catch (ResourceNotFoundException e) {
        throw new CfnNotFoundException(ResourceModel.TYPE_NAME, request.id());
      } catch (InternalServerException e) {
        throw new CfnInternalFailureException(e);
      } catch (InvalidParameterException e) {
        throw new CfnInvalidRequestException(e.getMessage(), e);
      } catch (final AmplifyUiBuilderException e) {
        if (e.statusCode() == HttpStatus.SC_NOT_FOUND || e.statusCode() == HttpStatus.SC_BAD_REQUEST) {
          throw new CfnNotFoundException(ResourceModel.TYPE_NAME, e.getMessage());
        }
        throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
      } catch (final AwsServiceException e) {
        throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
      }
    }

    protected GetComponentResponse getComponent (
        GetComponentRequest request,
        ProxyClient < AmplifyUiBuilderClient > client
  ){
      GetComponentResponse readResponse;
      try {
        readResponse =
            client.injectCredentialsAndInvokeV2(
                request,
                client.client()::getComponent
            );
      } catch (ResourceNotFoundException e) {
        throw new CfnNotFoundException(ResourceModel.TYPE_NAME, request.id());
      } catch (InternalServerException e) {
        throw new CfnInternalFailureException(e);
      } catch (InvalidParameterException e) {
        throw new CfnInvalidRequestException(e.getMessage(), e);
      } catch (final AmplifyUiBuilderException e) {
        if (e.statusCode() == HttpStatus.SC_NOT_FOUND || e.statusCode() == HttpStatus.SC_BAD_REQUEST) {
          throw new CfnNotFoundException(ResourceModel.TYPE_NAME, e.getMessage());
        }
        throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
      } catch (final AwsServiceException e) {
        throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
      }
      return readResponse;
    }
  }
