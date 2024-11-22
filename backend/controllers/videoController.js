const Video = require('../models/videoModel')
const User = require('../models/userModel');
const fs = require('fs');
const path = require('path');
const net = require('net');
const { sendMessageToTcpServer } = require('./miscellaneousController');


const mongoose = require('mongoose')


const createVideo = async (req, res, userDetails) => {
    const { title, description, channelName, video_src, thumbnail } = req.body;
    const date1 = new Date();
    const date = new Date(date1).toLocaleDateString();
    console.log("CREATING VIDEO WOW")
    try {
        const video = await Video.create({ userDetails, title, description, channelName, date, video_src, thumbnail });
        await video.populate('userDetails','username icon')
        const message = `ADD_VIDEO ${video._id}`
        await sendMessageToTcpServer(message)
        res.status(200).json(video);
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
}

const getVideos = async (req, res) => {
    try {
        const topViewedVideos = await Video.find().sort({ views: -1 }).limit(10).populate('userDetails', 'username icon');
        const topViewedVideoIds = topViewedVideos.map(video => video._id);
        const randomVideos = await Video.aggregate([
            { $match: { _id: { $nin: topViewedVideoIds } } },
            { $sample: { size: 10 } }
        ]).exec();
        const populatedRandomVideos = await Video.populate(randomVideos, { path: 'userDetails', select: 'username icon' });
        const combinedVideos = [...topViewedVideos, ...populatedRandomVideos];
        for (let i = combinedVideos.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [combinedVideos[i], combinedVideos[j]] = [combinedVideos[j], combinedVideos[i]];
        }
    
        res.status(200).json(combinedVideos);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

const updateViews = async (req, res) => {
    console.log("UPDATING VIEWS NODEJS")
    const pid  = req.params.pid;
    console.log("pid is",pid)
    const id = req.params.id;
    console.log("id isGDAGA",id)
    try {
        const video = await Video.findById(pid).populate('userDetails', 'username icon');
        if (!video) {
            return res.sendStatus(404);
        }
        video.views += 1;
        await video.save();
        const message = `GET_RECOMMENDATIONS ${id} ${pid}`;
        console.log("message is ",message)
        const tcpResponse = await sendMessageToTcpServer(message);
        console.log("tcp res is",tcpResponse)
        res.status(200).json({video,recommendations: tcpResponse });
    } catch (error) {
        res.sendStatus(500);
    }
};



const getVideo = async (req, res) => {
    const { pid } = req.params
    if (!mongoose.Types.ObjectId.isValid(pid)) {
        return res.status(404).json({ error: "No such video exists" })
    }
    const video = await Video.findById(pid).populate('userDetails', 'username icon')
    if (!video) {
        return res.status(404).json({ error: "No such video exists" })
    }
    res.status(200).json(video)
}

const deleteVideo = async (req, res) => {

    
    const { pid } = req.params
    if (!mongoose.Types.ObjectId.isValid(pid)) {
        return res.status(404).json({ error: "No such video exists" })
    }
    const video = await Video.findOneAndDelete({ _id: pid })
    if (!video) {
        return res.status(404).json({ error: "No such video exists" })
    }

    const videoPath = path.join(__dirname, '..', video.video_src);
    const thumbnailPath = path.join(__dirname, '..', video.thumbnail);

    fs.unlink(videoPath, (err) => {
        if (err) {
            console.error('Error deleting video file:', err);
        }
    });

    // Delete the thumbnail file
    fs.unlink(thumbnailPath, (err) => {
        if (err) {
            console.error('Error deleting thumbnail file:', err);
        }
    });
    const message = `DELETE_VIDEO ${pid}`;
    await sendMessageToTcpServer(message);
    return res.status(200).json(video)
}

const updateVideo = async (req, res) => {
    const { pid } = req.params
    if (!mongoose.Types.ObjectId.isValid(pid)) {
        return res.status(404).json({ error: "No such video exists" })
    }
    const video = await Video.findOneAndUpdate({ _id: pid }, {
        ...req.body
    }).populate('userDetails', 'username icon')
    if (!video) {
        return res.status(404).json({ error: "No such video exists" })
    }
    return res.status(200).json(video)
}

module.exports = {
    getVideo,
    getVideos,
    createVideo,
    deleteVideo,
    updateVideo,
    updateViews
}