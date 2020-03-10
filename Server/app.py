import os
from flask import Flask, request
import pyrebase
from firebase_admin import credentials, firestore, initialize_app
import requests
import json 

const firebaseConfig = {
  apiKey: "AIzaSyBOyX3_1WceWPpb4KTduGU2GKs_o5pXNok",
  authDomain: "safetynet-74e22.firebaseapp.com",
  databaseURL: "https://safetynet-74e22.firebaseio.com",
  projectId: "safetynet-74e22",
  storageBucket: "safetynet-74e22.appspot.com",
  messagingSenderId: "234550163455",
  appId: "1:234550163455:web:f3a0c7c66dc06dc31859ca",
  measurementId: "G-KVJY32QXKD"
};

firebase = pyrebase.initialize_app(firebaseConfig)

db = firebase.database()

# Hosting URL: https://safetynet-74e22.firebaseapp.com
# Project console: https://console.firebase.google.com/project/safetynet-74e22/overview
# Server url: https://todo-ngcg7jl7oa-uk.a.run.app/


app = Flask(__name__)

# Initialize Firestore DB
# cred = credentials.Certificate('key.json')
# default_app = initialize_app(cred)
# db = firestore.client()
# todo_ref = db.collection('todos')

users = {"user1":"password1", "user2":"password2", "user3":"password1"}
contacts = {"user1": [1], "user1": [0]}
messages = {"user1":[], "user2":[], "user3":[]}


def build_help_json(user, desc, loc):
    help_request = {}
    help_request['user'] = user
    help_request['desc'] = desc
    help_request['loc'] = loc
    return json.dumps(help_request)


@app.route('/help', methods=['POST'])
def send_help_request():
    data = request.headers

    recip = data.get("contacts").split(",")

    help_request = build_help_json(data.get("user"), data.get("desc"), data.get("loc"))

    print("adding messages to table")
    for user in recip:
        cur_messages = messages[user]
        cur_messages.append(help_request)
        messages[user] = cur_messages
        print("new inbox:" + str(cur_messages))
    print("added messages")

    return "calling for help"

@app.route('/check', methods=['GET'])
def check_messages():
    data = request.headers
    print("data: "+ str(data))

    user = data.get("user")

    print(user)

    print(messages[user])
    return str(messages[user])

@app.route("/")
def hello():
    return "Hello, World!"

if __name__ == '__main__':
    # Source: https://firebase.google.com/docs/hosting/cloud-run
    app.run(debug=True,host='0.0.0.0',port=int(os.environ.get('PORT', 8080)))