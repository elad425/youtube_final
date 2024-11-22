import React, { useState, useEffect } from 'react';
import './comment_box.css';
import Like_dislike_buttons from '../like_dislike_buttons/like_dislike_buttons';
import { ReactComponent as Dots_menu } from './dots-menu.svg';

function Comment_box({ comment, user, editComment, deleteComment,videoId,setVideoList}) {

    const [commentLikesCount, setCommentLikesCount] = useState(comment.likes.length)
    const [commentDislikesCount, setCommentDislikesCount] = useState(comment.dislikes.length)
    const [commentLikeStatus,setCommentLikeStatus] = useState(0)
    const [editMode, setEditMode] = useState(false);
    const [editedComment, setEditedComment] = useState(comment.commentMessage);
    let rightChannel = false;

    if (user) {
        if (user._id == comment.user_id._id) {
            rightChannel = true;
        }
    }
    useEffect(() => {
        setCommentLikesCount(comment.likes.length)
        setCommentDislikesCount(comment.dislikes.length)
        if (user) {
            if (comment.likes.includes(user._id)) {
                setCommentLikeStatus(1)
            }
            else if (comment.dislikes.includes(user._id)) {
                setCommentLikeStatus(2)
            } else {
                setCommentLikeStatus(0)
            }
        }

    }, [videoId,comment]);

    const handleLike = async () => {
        const data = {
            userId: user._id
        }
        try {
            const response = await fetch(`/api/likes/like/comment/${comment._id}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify(data)
            });
            const result = await response.json();
            setCommentLikesCount(result.likes.length);
            setCommentDislikesCount(result.dislikes.length);
            setCommentLikeStatus(result.status)
            setVideoList(prevVideoList => {
                return prevVideoList.map(video => {
                    if (video._id === videoId) {
                        return {
                            ...video,
                            comments: video.comments.map(c => {
                                if (c._id === comment._id) {
                                    return {
                                        ...c,
                                        likes: result.likes,
                                        dislikes: result.dislikes
                                    };
                                }
                                return c;
                            })
                        };
                    }
                    return video;
                });
            });
            comment.likes=result.likes
            comment.dislikes=result.dislikes
            //changeLikedBy(video.likes.length - video.dislikes.length);
        } catch (error) {
            console.error('Error liking video:', error);
        }
    };

    const handleDislike = async () => {
        const data = {
            userId: user._id
        }
        try {
            const response = await fetch(`/api/likes/dislike/comment/${comment._id}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify(data)

            });
            const result = await response.json();
            setCommentLikesCount(result.likes.length);
            setCommentDislikesCount(result.dislikes.length);
            setCommentLikeStatus(result.status)
            setVideoList(prevVideoList => {
                return prevVideoList.map(video => {
                    if (video._id === videoId) {
                        return {
                            ...video,
                            comments: video.comments.map(c => {
                                if (c._id === comment._id) {
                                    return {
                                        ...c,
                                        likes: result.likes,
                                        dislikes: result.dislikes
                                    };
                                }
                                return c;
                            })
                        };
                    }
                    return video;
                });
            });
            comment.likes=result.likes
            comment.dislikes=result.dislikes
        } catch (error) {
            console.error('Error disliking video:', error);
        }
    };
    const handleEdit = (event) => {
        event.preventDefault();
        setEditMode(!editMode);
    };
    const handleCommentChange = (event) => {
        setEditedComment(event.target.value);
    };

    const handleSave = () => {
        editComment(comment._id, editedComment);
        setEditMode(false);
    };
    const handleDelete = (event) => {
        event.preventDefault();
        deleteComment(comment._id);
    };

    return (
        <div className="comment-box-container">
            <div className="comment-header">
                <img src={comment.user_id.icon} alt="channel icon" className="channel-icon" />
            </div>
            <div className="comment-content">
                <div className="comment-channel-date">
                    <span className="comment-channel-name">{comment.user_id.username}</span>
                    <span className="comment-date">{comment.date}</span>
                    {rightChannel && (
                        <>
                            <button className='dots-menu-btn ' data-bs-toggle="dropdown" aria-expanded="false">
                                <Dots_menu />
                            </button>
                            <ul className="dropdown-menu">
                                <li><a className="dropdown-item" href="#" onClick={handleEdit}>Edit</a></li>
                                <li><a className="dropdown-item" href="#" onClick={handleDelete}>Delete</a></li>
                            </ul>
                        </>
                    )
                    }
                </div>
                {editMode ? (
                    <textarea className="comment-edit-textarea" value={editedComment} onChange={handleCommentChange} />
                ) : (
                    <p className="comment-content-p">{editedComment}</p>
                )}
                <Like_dislike_buttons videoId={videoId} user={user} likesNum={commentLikesCount} dislikesNum={commentDislikesCount} likeVideo={handleLike} dislikeVideo={handleDislike} likedState={commentLikeStatus} />
                {editMode && <button onClick={handleSave}>Save</button>}
            </div>
        </div>
    );
}

export default Comment_box;
