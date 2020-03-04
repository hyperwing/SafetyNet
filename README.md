# SafetyNet


**PREREQUISITES:** 
Python 3.7+

## Setting up the server
1. Open terminal (cmd for Windows or bash for macOS) and navigate to desired root directory

2. **If you haven't ever run the server before,** from desired root directory, create virtual environment by running the command below. 
```python
py -m venv env 
```
```
env\Scripts\activate 
```

3. Install Dependencies
```python
pip install flask 
pip install requests
```

4. Set environment variable for the FLASK_APP to be the flask server we made:
```python
set FLASK_APP=server.py
```

5. Run server
```
flask run
```