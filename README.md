Centos
======

Project based on official Centos image. Applies necessary steps to get images
to run on the DOI network, namely:

- Add DOIRootCA2.pem to SSL certificate chain for system.
  - Necessary to work on DOI network with SSL Visibility program in place


Build Image
-----------

A Dockerfile is provided to simplify image building. Most basically one may
use the default image like so...

```
$ cd <PROJECT_ROOT>
$ docker build -t <TAG_NAME> .
```

> Here `<PROJECT_ROOT>` and `<TAG_NAME>` should be replaced with values appropriate
> to ones local environment and desired generated tag.

The Dockerfile also supports other base images, so if one desired to build from
a different CentOS-like image, one may do as as follows:

```
$ cd <PROJECT_ROOT>
$ docker build \
  --build-arg FROM_IMAGE=<OTHER_BASE_IMAGE> \
  -t <TAG_NAME> \
  .

```
