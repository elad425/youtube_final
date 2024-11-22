const mongoose = require('mongoose');
const Video = require('../models/videoModel')
const User = require('../models/userModel');
const fs = require('fs');
const path = require('path');
const net = require('net');


const deleteFileFromServer = async (req, res) => {

    try {
        const { file } = req.body;

        if (!file) {
            console.error("File not specified in request body");
            return res.sendStatus(400);
        }

        const fPath = path.join(__dirname, '..', file);

        fs.unlink(fPath, (err) => {
            if (err) {
                console.error('Error deleting file:', err);
                return res.sendStatus(400);
            } else {
                return res.sendStatus(200);
            }
        });
    } catch (error) {
        console.error('Error caught in catch block:', error);
        res.sendStatus(500);
    }
};
const sendInitialData = async ()=>{
  const allVideos = await Video.find().populate('userDetails', 'username icon');
  // Prepare video data for sending to TCP server
  const videoData = allVideos.map(video => `${video._id}:${video.views}`);
  // Send video names and views to TCP server
  message = `INIT_VIDEO_DATA ${videoData.join(', ')}`;
  console.log("Sending video data to TCP server: ", message);
  await sendMessageToTcpServer(message);
  const allUsers = await User.find();
  const usersData = allUsers.map(user => user.username );
  message = `INIT_USER_DATA ${usersData.join(', ')}`;
  console.log("Sending user data to TCP server: ", message);
  await sendMessageToTcpServer(message);

}
const sendMessageToTcpServer = (message) => {
    return new Promise((resolve, reject) => {
      const client = new net.Socket();
      const serverHost = 'your ip';  // Replace with your TCP server's IP
      const serverPort = 5555;         // Replace with your TCP server's port
  
      client.connect(serverPort, serverHost, () => {
        console.log('Connected to TCP server');
        client.write(message);
      });
  
      client.on('data', (data) => {
        console.log('Received:', data.toString());
        client.destroy(); // Close the connection after receiving a response
        resolve(data.toString());
      });
  
      client.on('error', (err) => {
        console.error('TCP client error:', err);
        reject(err);
      });
  
      client.on('close', () => {
        console.log('Connection closed');
      });
    });
  };

module.exports = {
    deleteFileFromServer,
    sendMessageToTcpServer,
    sendInitialData
}
