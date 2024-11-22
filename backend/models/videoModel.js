const mongoose = require ('mongoose')
const Schema = mongoose.Schema

const videoSchema = new Schema(
    {
        title: {
            type: String,
            required: true
        },
        description: {
            type: String,
        },
        views: {
            type: Number,
            default: 0
        },
        date : {
            type: String,
        },
        video_src: {
            type: String,
            required : true
        },
        thumbnail :{
            type: String,
            required :true
        },
        userDetails: {
            type: Schema.Types.ObjectId,
            ref: 'User',
            required: true,
        },
        comments: [
            {
                type: Schema.Types.ObjectId,
                ref: 'Comment'
            }
        ],
        likes:[
            {
                type: Schema.Types.ObjectId,
                ref: 'User'
            }
        ],
        dislikes:[
            {
                type: Schema.Types.ObjectId,
                ref: 'User'
            }
        ]
        
        
    },{timestamps:true})

module.exports = mongoose.model('Video',videoSchema)
