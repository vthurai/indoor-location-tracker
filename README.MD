Set up instructions

1. Install gcloud
Linux (Raspberry Pi):

sudo apt-get update && sudo apt-get install google-cloud-sdk
link:https://cloud.google.com/sdk/docs/downloads-apt-get

2. Create GOOGLE_APPLICATION_CREDENTIALS

Easiest solution is to authenicate through gcloud:
```
gcloud beta auth application-default login
```
Next configure the project
```
gcloud config set project <PROJECT>
```