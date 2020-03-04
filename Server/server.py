from flask import Flask, request
import requests
import json 

app = Flask(__name__)

users = {"user1":"password1", "user2":"password2", "user3":"password1"}
contacts = {"user1": [1], "user1": [0]}
messages = {"user1":[], "user2":[]}


def build_help_json(user, desc, loc):
    help_request = {}
    help_request['user'] = user
    help_request['desc'] = desc
    help_request['loc'] = loc
    return json.dumps(help_request)


@app.route('/help')
def send_help_request():
    data = request.headers

    recip = data.get("contacts").split(",")
    print(recip)

    help_request = build_help_json(data.get("user"), data.get("desc"), data.get("loc"))

    print("adding messages to table")
    for user in recip:
        cur_messages = messages[user]
        cur_messages.append(help_request)
        messages[user] = cur_messages
        print("new inbox:" + str(cur_messages))
    print("added messages")

    return "calling for help"

@app.route('/check')
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

    app.run()