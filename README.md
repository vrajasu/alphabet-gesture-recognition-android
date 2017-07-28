# alphabet-gesture-recognition-android
This project involves testing the feasibility of using Dynamic Time Warping (DTW) instead of traditional ML approaches for comparing training samples and  testing samples for alphabet gesture recognition. This experiment is conducted only for four alphabets A,B,C and D

The application uses accelerometer data in the X and Y direction to record the samples at a sampling rate of 100Hz. 

The application is divided into two main parts which are data collection/training and testing. 

### Data Collection/Training Phase
1. The application first checks if there is a training file present, if the training file is found then the user is given the option to either move to the testing stage or record new samples.
<p align="center">
  <img src="https://github.com/vrajasu/alphabet-gesture-recognition-android/blob/master/screenshots/file_found.png" width="250"/>
</p>

2. If the user chooses to record samples then we collect 5 samples each for A,B,C and D. The Alphabet on the screen indicates the sample being recorded. The user is required to hold down a button and move the phone in an attempt to draw one of the alphabets in the air.
<p align="center">
  <img src="https://github.com/vrajasu/alphabet-gesture-recognition-android/blob/master/screenshots/training_phase.png" width="250"/>
</p>

3. As mentioned above the application uses accelerometer data and stores it in a List of 1x2 float arrays, with array[0] being the acceleration in the X direction and array[1] being the acceleration in the Y direction.

### Testing Phase with DTW
1. The implementation of calculating DTW distance between two samples is adopted from the simple algorithm provided on [Wikipedia](https://en.wikipedia.org/wiki/Dynamic_time_warping)

2. Once a user records the test case, its DTW distance is calculated with each of the training samples. An empiricial threshold is determined and based on voting on lowest DTW distance an alphabet is predicted.
<p align="center">
  <img src="https://github.com/vrajasu/alphabet-gesture-recognition-android/blob/master/screenshots/testing_phase.png" width="250"/>
</p>

### Results
The results depend on one very important factor which is how one draws the alphabet in the air, is it slowly/fast/smoothly. Steadily drawn samples and tests yield a very high accuracy rate. 

### Future Work
Future work would involve refining data recorded by sensor fusion. Sensor fusion between accelerometer, gyroscope and compass provide a very accurate reading of the device's pitch, yaw and roll. A more accurate data collection process will only result in higher accuracies during the testing process

Do not use unless you have obtained permission

Copyright 2017

@authors
- Vraj Delhivala mailto: vdelhiva@asu.edu
