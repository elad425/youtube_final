const Video = require('../models/videoModel')
const Comment = require('../models/commentModel')

const likeVideo = async(req,res)=>{
    const { id } = req.params
    const { userId } = req.body

    try {
        const video = await Video.findById(id);
        let status = 0 
        if (!video) {
            return res.status(404).json({ error: "No such video exists" });
        }

        if (video.likes.includes(userId)) {
            video.likes.pull(userId);
        } else {
            video.likes.push(userId);
            video.dislikes.pull(userId);
            status=1
        }
        await video.save();
        const likes = video.likes;
        const dislikes = video.dislikes;
        res.status(200).json({ likes, dislikes,status });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }

}   

const dislikeVideo = async(req,res)=>{
    const { id } = req.params
    const { userId } = req.body
    let status = 0
    try {
        const video = await Video.findById(id);
        if (!video) {
            return res.status(404).json({ error: "No such video exists" });
        }

        if (video.dislikes.includes(userId)) {
            video.dislikes.pull(userId);
        } else {
            video.dislikes.push(userId);
            video.likes.pull(userId);
            status = 2
        }
        await video.save();
        const likes = video.likes;
        const dislikes = video.dislikes;
        res.status(200).json({ likes, dislikes,status});
    } catch (error) {
        res.status(500).json({ error: error.message });
    }

}   

const dislikeComment = async(req,res)=>{
    const { id } = req.params
    const { userId } = req.body
    let status = 0
    try {
        const comment = await Comment.findById(id);
        if (!comment) {
            return res.status(404).json({ error: "No such comment exists" });
        }

        if (comment.dislikes.includes(userId)) {
            comment.dislikes.pull(userId);
        } else {
            comment.dislikes.push(userId);
            comment.likes.pull(userId);
            status = 2
        }
        await comment.save();
        const likes = comment.likes;
        const dislikes = comment.dislikes;
        res.status(200).json({ likes, dislikes,status});
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
}   

const likeComment = async(req,res)=>{
    const { id } = req.params
    const { userId } = req.body

    try {
        const comment = await Comment.findById(id);
        let status = 0 
        if (!comment) {
            return res.status(404).json({ error: "No such comment exists" });
        }

        if (comment.likes.includes(userId)) {
            comment.likes.pull(userId);
        } else {
            comment.likes.push(userId);
            comment.dislikes.pull(userId);
            status=1
        }
        await comment.save();
        const likes = comment.likes;
        const dislikes = comment.dislikes;
        res.status(200).json({ likes, dislikes,status });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
}   


const getVideoLikeStatus = async (req,res)=>{
    const { id } = req.params
    const {userId} = req.body
    try {
        let status = 0
        const video = Video.findById(id)
        if (!video){
            return res.status(404).json({error:"No such video exists"})
        }
        if (video.likes.includes(userId)){
            status = 1
            return res.status(200).json(status)
        }
        if (video.dislikes.includes(userId)){
            status = 2
            return res.status(200).json(status)
        }
        else {
            return res.status(200).json(status)
        }
    } 
    catch(error){
        res.status(500).json({ error: error.message });

    }
}
module.exports = {
    likeVideo,
    dislikeVideo,
    likeComment,
    dislikeComment,
    getVideoLikeStatus
}