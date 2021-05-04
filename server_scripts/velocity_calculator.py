import json
import matplotlib.pyplot as plt
import numpy as np
import sys
import os
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore



def upload_to_firebase(uid, value):
    cred = credentials.Certificate(
        "/home/saman/Desktop/video_data/scripts/dynaswayconcussion-firebase-adminsdk-asj9q-6cc86f0b5a.json")
    firebase_admin.initialize_app(cred)

    db = firestore.client()

    db.collection('test_results').document(uid).set({'value': value}, merge=True)


def moving_average(x, w):
    return np.convolve(x, np.ones(w), 'valid') / w


def get_slope(x):
    return np.mean(np.diff(x))


joints = ["Nose", "LEye", "REye", "LEar", "REar",
          "LShoulder", "RShoulder", "LElbow", "RElbow",
          "LWrist", "RWrist", "LHip", "RHip", "LKnee", "RKnee",
          "LAnkle", "RAnkle"]

kp_map = {}

for i, joint in enumerate(joints):
    kp_map[joint] = {'id': i, 'pos': [], 'pos_filtered': []}

confidences = []

folder_path = sys.argv[1]
folder_name = os.path.basename(folder_path)
uid = folder_name.split('_')[0]
print(folder_path)
print(folder_name)
print(uid)

with open(os.path.join(folder_path, 'alphapose-results.json')) as f:
    df = json.load(f)
    for frame in df:
        confidences.append(frame['score'])
        kp = frame['keypoints']
        for k, v in kp_map.items():
            v['pos'].append([kp[v['id'] * 3], kp[v['id'] * 3 + 1]])


confidences = np.array(confidences) / 6 * 100

joints_of_interest = ['Nose', 'RShoulder', 'LShoulder', 'RHip', 'LHip']

pixel_velocity = 1
upper_arm_length = 1

plt.subplot(211), plt.title('raw data')
for joint in joints_of_interest:
    x, y = zip(*kp_map[joint]['pos'])
    plt.plot(x, label=joint)

plt.ylabel('Position (px)')
plt.legend()

# plt.subplot(312), plt.title('confidence')
# plt.plot(confidences)

plt.subplot(212), plt.title('filtered data')
for joint in joints_of_interest:
    x, y = zip(*kp_map[joint]['pos'])
    smooth_data = moving_average(x, 11)
    plt.plot(smooth_data, label=joint)
    if joint == 'Nose':
        pixel_velocity = get_slope(smooth_data)

plt.xlabel('Frame (#)')
plt.ylabel('Position (px)')

plt.savefig(os.path.join(folder_path, 'position_chart.png'))
# plt.show()

limbs_of_interest = [['Shoulder', 'Elbow'], ['Elbow', 'Wrist'],
                     ['Hip', 'Knee'], ['Knee', 'Ankle']]

side_of_interest = 'R'

lengths_of_interest = []

for i in range(len(limbs_of_interest)):
    lengths_of_interest.append([])

for limb_ind, limb in enumerate(limbs_of_interest):
    x_1, y_1 = zip(*kp_map[side_of_interest + limb[0]]['pos'])
    x_2, y_2 = zip(*kp_map[side_of_interest + limb[1]]['pos'])

    joint_1_pos_x = moving_average(x_1, 21)
    joint_1_pos_y = moving_average(y_1, 21)

    joint_2_pos_x = moving_average(x_2, 21)
    joint_2_pos_y = moving_average(y_2, 21)

    for i in range(len(joint_1_pos_x)):
        length = np.sqrt((joint_1_pos_x[i] - joint_2_pos_x[i]) ** 2 +
                         (joint_1_pos_y[i] - joint_2_pos_y[i]) ** 2)

        lengths_of_interest[limb_ind].append(length)

upper_arm_length = np.mean(lengths_of_interest[0])
print(f'Perceived upper arm length: {upper_arm_length} px')
print('Actual upper arm length: 31cm')

final_velocity = pixel_velocity / upper_arm_length
print(f'Normalized velocity: {final_velocity} upper arm length per frame')

upload_to_firebase(uid, final_velocity)

print(f'Estimated actual velocity: {final_velocity * 28.75 * 31 / 100} m/s')

