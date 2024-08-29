## My Project

TODO: Fill this README out!

1. Create a new S3 bucket in your account to store your test artifacts. The step functions that will be executing tests

```shell
aws s3api create-bucket --bucket {bucketName} --region {bucketRegion} --create-bucket-configuration LocationConstraint={bucketRegion}
```

## Security

See [CONTRIBUTING](CONTRIBUTING.md#security-issue-notifications) for more information.

## License

This project is licensed under the Apache-2.0 License.
