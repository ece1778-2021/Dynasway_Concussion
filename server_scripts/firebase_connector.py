import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

cred = credentials.Certificate("./dynaswayconcussion-firebase-adminsdk-asj9q-6cc86f0b5a.json")
firebase_admin.initialize_app(cred)

db = firestore.client()

db.collection(u'test_results').document(u'2dmgFW2c8oS3CxZ76xoM').set({u'value': 5}, merge=True)
