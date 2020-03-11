# Code written based on https://cloud.google.com/community/tutorials/building-flask-api-with-cloud-firestore-and-deploying-to-cloud-run and https://medium.com/firebase-developers/hosting-flask-servers-on-firebase-from-scratch-c97cfb204579

# Dockerfile
FROM python:3.7-stretch
RUN pip install pyrebase
RUN apt-get update -y
RUN apt-get install -y python-pip python-dev build-essential
COPY . /app
WORKDIR /app
RUN pip install -r requirements.txt
ENTRYPOINT ["python"]
CMD ["app.py"]
