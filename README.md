# Identity-admin

# Application configuration

Configuration files:
- Environment-specific configuration (`conf/<ENV>.conf`)
- Application configuration (`conf/application.conf`)
- System file with additional properties (`/etc/gu/identity-admin.conf`)
- Service account certificate (`/etc/gu/identity-admin-cert.json`)

# Setting up Identity Admin locally

## Hosts

Ensure you have the correct [identity-admin hosts](https://github.com/guardian/identity-admin/blob/master/nginx/hosts) included in the /etc/hosts file on your machine

## SSL Certificates & nginx setup

We have valid SSL certificates for thegulocal.com and the subdomains we use for local development.

The certificates for the local subdomain `useradmin.thegulocal.com` are stored in the AWS S3 Identity bucket and are set up as part of the `identity-admin.conf` for nginx.

Follow these installation steps to correctly setup nginx and valid SSL certificates locally:

* Make sure you are in the base `identity-admin` directory

* Make sure you have access to the S3 bucket identity-local-ssl and download them using the [AWS CLI utility](https://aws.amazon.com/cli/) (the following command will download them in your current directory using your Identity profile on AWS):

```bash
aws --profile identity s3 cp s3://identity-local-ssl/useradmin-thegulocal-com-exp2016-10-02-bundle.crt . 1>/dev/null
aws --profile identity s3 cp s3://identity-local-ssl/useradmin-thegulocal-com-exp2016-10-02.key . 1>/dev/null
```

* Find the configuration folder of nginx by running:

```bash
nginxHome=`nginx -V 2>&1 | grep "configure arguments:" | sed 's/[^*]*conf-path=\([^ ]*\)\/nginx\.conf.*/\1/g'`
```

`echo $nginxHome` should display the name of the folder.

* Create symbolic links for the certificates and identity-admin configuration file for nginx (note: this might require `sudo`)

```bash
sudo ln -fs `pwd`/useradmin-thegulocal-com-exp2016-10-02-bundle.crt $nginxHome/useradmin-thegulocal-com-exp2016-10-02-bundle.crt
sudo ln -fs `pwd`/useradmin-thegulocal-com-exp2016-10-02.key $nginxHome/useradmin-thegulocal-com-exp2016-10-02.key
sudo ln -fs `pwd`/nginx/identity-admin.conf $nginxHome/sites-enabled/identity-admin.conf
```

* Restart nginx:

```bash
sudo nginx -s stop
sudo nginx
```

* Optional - verify that your configuration is set up as expected

    - `ls -la $nginxHome` should show the certificates correctly symlinked to the **full pathname** of the downloaded certificates
    - `ls -la $nginxHome/sites-enabled` should show `identity-admin.conf`  correctly symlinked to the **full pathname** of `identity-admin/nginx/identity-admin.conf`

You should now be able to start the application (`sbt run`), go to [https://useradmin.thegulocal.com/management/healthcheck](https://useradmin.thegulocal.com/management/healthcheck) and see a green padlock for your local SSL certificate as well as a 200 response.

## Configuration

Install the local configuration file from s3:

```
aws s3 cp --profile identity s3://gu-identity-admin-private/DEV/identity-admin.conf /etc/gu
aws s3 cp --profile identity s3://gu-identity-admin-private/identity-admin-cert.json /etc/gu
```

## Running the application

```
sbt devrun
```

## Testing - Lines in the sand

- All tests should be included in this repo and executed before deployment. 
- Failing tests will block deployment.
- Prefer unit tests to integration/functional tests.
- Browser tests should only be written if the functionality cannot be tested any other way.
- Tests should always complete in under five minutes.
- Unstable tests are not acceptable.

