FROM centos:7
MAINTAINER Eric Martinez <emartinez@usgs.gov>
LABEL dockerfile_version="v0.3.0"

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

# Create a hazdev user to run application inside container
RUN useradd \
  -c 'Docker image user' \
  -m \
  -r \
  -s /sbin/nologin \
  -U \
  hazdev-user
