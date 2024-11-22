const Video = require('../models/videoModel')
const User = require('../models/userModel');
const Comment = require('../models/commentModel')
const fs = require('fs');
const path = require('path');
const jwt = require('jsonwebtoken');
const mongoose = require('mongoose');
const { createVideo } = require('./videoController');


const createUser = async (req, res) => {
    const { username, icon, email, password } = req.body;
    try {
        const user = await User.create({ username, icon, email, password });
        const token = jwt.sign({ userId: user._id }, process.env.ACCESS_TOKEN_SECRET);
        res.status(200).json({ token, user });
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
}

const loginUser = async (req, res) => {
    const { email, password } = req.body;
    try {
        const user = await User.findOne({ email });
        if (!user) return res.status(400).json({ error: 'User not found' });
        if (user.password != password) return res.status(400).json({ error: 'Invalid credentials' });
        const token = jwt.sign({ userId: user._id }, process.env.ACCESS_TOKEN_SECRET);
        res.status(200).json({ token, user });
    } catch (error) {
        res.status(400).json({ error: 'Login failed' });
    }
};

const checkEmailExists = async (req, res) => {
    const { email } = req.body;
    try {
        const user = await User.findOne({ email });
        console.log("123321",user)
        if (user) {
            return res.status(200).json({ exists: true });
        } else {
            return res.status(200).json({ exists: false });
        }
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
};

const getUser = async (req, res) => {
    const { id } = req.params
    if (!mongoose.Types.ObjectId.isValid(id)) {
        return res.status(404).json({ error: "No such user exists" })
    }
    const user = await User.findById(id)
    if (!user) {
        return res.status(404).json({ error: "No such user exists" })
    }
    res.status(200).json(user)
}

const deleteUser = async (req, res) => {
    const { id } = req.params
    if (!mongoose.Types.ObjectId.isValid(id)) {
        return res.status(404).json({ error: "No such user exists" })
    }
    try {
        const user = await User.findOneAndDelete({ _id: id })
        await Video.deleteMany({ userDetails: id });
        await Comment.deleteMany({ user_id: id })

        if (!user) {
            return res.status(404).json({ error: "No such user exists" })
        }
        const iconPath = path.join(__dirname, '..', user.icon);

        fs.unlink(iconPath, (err) => {
            if (err) {
                console.error('Error deleting user icon file:', err);
            }
        });
        return res.status(200).json(user)
    }
    catch (error) {
        return res.status(500).json({ error: error.message });
    }
}
const getAllUsers = async (req, res) => {
    try {
        const users = await User.find()
        return res.status(200).json(users);
    } catch (error) {
        return res.status(500).json({ error: error.message });
    }
}
const updateUsername = async (req, res) => {
    const { id } = req.params;

    if (!mongoose.Types.ObjectId.isValid(id)) {
        return res.status(404).json({ error: "No such user exists" });
    }

    const { username, email, password, icon } = req.body;
    let updatedFields = {  };
    if (username) updatedFields.username = username;
    if (email) updatedFields.email = email;
    if (password) updatedFields.password = password;
    if (icon) updatedFields.icon = icon;
    // Ensure to hash the password before saving

    // Handle file upload for icon if provided

    try {
        const user = await User.findOneAndUpdate({ _id: id }, updatedFields, { new: true });
        console.log(user)
        if (!user) {
            return res.status(404).json({ error: "No such user exists" });
        }
        return res.status(200).json(user);
    } catch (error) {
        return res.status(500).json({ error: error.message });
    }
};

const getUserVideos = async (req, res) => {
    const { id } = req.params
    if (!mongoose.Types.ObjectId.isValid(id)) {
        return res.status(404).json({ error: "No such user exists" })
    }
    try {
        const videos = await Video.find({ userDetails: id });
        if (!videos) {
            return res.status(404).json({ error: "User has no videos" })
        }
        return res.status(200).json(videos)
    }
    catch (error) {
        return res.status(500).json({ error: error.message });
    }
}

const createUserVideo = async (req, res) => {
    const { id } = req.params
    if (!mongoose.Types.ObjectId.isValid(id)) {
        return res.status(404).json({ error: "No such user exists" })
    }
    try {
        const user = await User.findById(id)
        if (!user) {
            return res.status(404).json({ error: "No such user exists" })
        }
        return createVideo(req, res, id)
    }
    catch (error) {
        return res.status(500).json({ error: error.message });
    }
}




module.exports = {
    createUser,
    getUser,
    updateUsername,
    deleteUser,
    getUserVideos,
    createUserVideo,
    getAllUsers,
    loginUser,
    checkEmailExists

}