{
  "AWSEBDockerrunVersion": "1",
  "Authentication": {
    "bucket": "dev1-classify-elasticbeanstalk",
    "key": "github-dockercfg"
  },
  "Image": {
    "Name": "docker.pkg.github.com/classifyventure/classify-api-app/release:futurestate-handoff",
    "Update": "true"
  },
  "Ports": [
    {
      "ContainerPort": "3000",
      "HostPort": "80"
    }
  ]
}
