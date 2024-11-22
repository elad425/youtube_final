const Comment = require('../models/commentModel')
const Video = require('../models/videoModel')
const User = require('../models/userModel');
const mongoose = require('mongoose')


const createComment = async(req,res)=>{
    const { commentMessage, user_id,video_id} = req.body;
    const date1 = new Date();
    const date = new Date(date1).toLocaleDateString();
    try {
        const comment = await Comment.create({commentMessage,date,user_id,video_id});
        await Video.findByIdAndUpdate(video_id, { $push: { comments: comment._id } });
        res.status(200).json(comment);
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
}


const getComments = async (req, res) => {
    const { id } = req.params;
    try {
        const video = await Video.findById(id)
            .populate({
                path: 'comments',
                populate: {
                    path: 'user_id',
                    select: 'username icon'
                }
            });
        res.status(200).json(video.comments);
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
};
const deleteComment = async (req,res)=>{
    const {id} = req.params
    if (!mongoose.Types.ObjectId.isValid(id)){
        return res.status(404).json({error: "No such comment exists"})
    }
    
    const comment = await Comment.findOneAndDelete({_id:id})
    
    if (!comment){
        return res.status(404).json({error: "No such comment exists"})
    }
    await Video.findByIdAndUpdate(comment.video_id, {
        $pull: { comments: id }
    });
    return res.status(200).json(comment)
}

const updateComment = async (req,res)=>{
    const {id} = req.params
    if (!mongoose.Types.ObjectId.isValid(id)){
        return res.status(404).json({error: "No such comment exists"})
    }
   
    const comment = await Comment.findOneAndUpdate({_id:id},{
        ...req.body
    })
    if (!comment){
        return res.status(404).json({error: "No such comment exists"})
    }

   
    return res.status(200).json(comment)
}


module.exports = {
   getComments,
   createComment,
   updateComment,
   deleteComment
}