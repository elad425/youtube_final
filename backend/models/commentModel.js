const mongoose = require('mongoose')
const Schema = mongoose.Schema

const commentSchema = new Schema(
    {
        commentMessage: {
            type: String,
            required: true
        },
        date: {
            type: String
        },
        user_id: {
            type: Schema.Types.ObjectId,
            ref: 'User',
            required: true
        },
        video_id: {
            type: String,
            required: true
        },
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

    }, { timestamps: true })

module.exports = mongoose.model('Comment', commentSchema)
