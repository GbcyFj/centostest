ARG FROM_IMAGE=centos:6
FROM $FROM_IMAGE

LABEL maintainer="Eric Martinez <emartinez@usgs.gov>"\
      dockerfile_version="v1.0.0"

# Allow builds within DOI network
COPY DOIRootCA2.crt /etc/pki/ca-trust/source/anchors/DOIRootCA2.crt

# Update current system packages and certificates
RUN yum upgrade -y && \
    yum updateinfo -y && \
    yum install -y \
      ca-certificates \
      && \
    yum clean all && \
    update-ca-trust enable && \
    update-ca-trust extract

# location of ssl certificate chain
ENV SSL_CERT_FILE /etc/pki/tls/certs/ca-bundle.crt

# Create a hazdev user to run application inside container
RUN useradd \
  -c 'Docker image user' \
  -m \
  -r \
  -s /sbin/nologin \
  -U \
  hazdev-user
