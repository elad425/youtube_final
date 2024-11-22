import './under-video-bar.css'
import Share_button from '../share-button/share-button'
import Like_dislike_buttons from '../like_dislike_buttons/like_dislike_buttons';
import React, { useEffect, useState } from 'react';
import Add_comment_box from '../add_comment_box/add_comment_box';
import { ReactComponent as Dots_menu } from './dots-menu.svg';

function Under_video_bar({videoList,video, videoId, description, views, date, addComment, videoUserDetails, user, editVideo, deleteVideo, title, setVideoList }) {
    let rightChannel = false;
    if (user) {
        if (videoUserDetails.username == user.username) {
            rightChannel = true;
        }

    }
    useEffect(() => {
        setLikesCount(video.likes.length)
        setDislikesCount(video.dislikes.length)
        setEditedDesc(description)
        setTitleEdit(title)
        setEditMode(false)
        if (user) {
            if (video.likes.includes(user._id)) {
                setLikeStatus(1)
            }
            else if (video.dislikes.includes(user._id)) {
                setLikeStatus(2)
            } else {
                setLikeStatus(0)
            }
        }

    }, [description, title,videoId,video]);
    // const [activeButton, setActiveButton] = useState(null);

    // const handleButtonClick = (buttonType) => {
    //     setActiveButton(buttonType);
    // };

    const [editMode, setEditMode] = useState(false);
    const [editedDesc, setEditedDesc] = useState(description);
    const [titleEdit, setTitleEdit] = useState(title);
    const [likesCount, setLikesCount] = useState(video.likes.length)
    const [dislikesCount, setDislikesCount] = useState(video.dislikes.length)
    const [likeStatus,setLikeStatus] = useState(0)
  

    const handleEdit = (event) => {
        event.preventDefault();
        setEditMode(!editMode);
    }

    const handleDelete = (event) => {
        event.preventDefault();
        deleteVideo();
    };
    const handleTitleChange = (event) => {
        setTitleEdit(event.target.value);
    }
    const handleDescChange = (event) => {
        setEditedDesc(event.target.value);
    };

    const handleSave = () => {
        editVideo(editedDesc, titleEdit);
        setEditMode(false);
    };

    const handleLike = async () => {
        const data = {
            userId: user._id
        }
        try {
            const response = await fetch(`/api/likes/like/video/${videoId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify(data)
            });
            const result = await response.json();
            setLikesCount(result.likes.length);
            setDislikesCount(result.dislikes.length);
            setLikeStatus(result.status)
            setVideoList(prevVideoList => {
                return prevVideoList.map(video => {
                    if (video._id === videoId) {
                        return {
                            ...video,
                            likes: result.likes,
                            dislikes: result.dislikes
                        };
                    }
                    return video;
                });
            });
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
            const response = await fetch(`/api/likes/dislike/video/${videoId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify(data)

            });
            const result = await response.json();
            setLikesCount(result.likes.length);
            setDislikesCount(result.dislikes.length);
            setLikeStatus(result.status)
            setVideoList(prevVideoList => {
                return prevVideoList.map(video => {
                    if (video._id === videoId) {
                        return {
                            ...video,
                            likes: result.likes,
                            dislikes: result.dislikes
                        };
                    }
                    return video;
                });
            });
        } catch (error) {
            console.error('Error disliking video:', error);
        }
    };

    return (
        <div className="under-video-container">
            <div className="under-video-btn-container">
                <div className="channel-name-icon">
                    <a href="#" className="icon-container">
                        <img className="channel-icon" src={videoUserDetails.icon} />
                    </a>
                    <span>{videoUserDetails.username}</span>
                </div>

                <Like_dislike_buttons videoId={videoId} user={user} likesNum={likesCount} dislikesNum={dislikesCount} likeVideo={handleLike} dislikeVideo={handleDislike} likedState={likeStatus} />
                <Share_button />
            </div>
            <div className="under-video-details-container">
                <div className="views-date-container">
                    <span className='views-under-span'>{views} views</span>
                    <span>{date}</span>
                    {rightChannel && (
                        <>
                            <button className='dots-menu-btn dropdown-toggle' data-bs-toggle="dropdown" aria-expanded="false">
                                <Dots_menu />
                            </button>
                            <ul className="dropdown-menu">
                                <li><a className="dropdown-item" href="#" onClick={handleEdit}>Edit</a></li>
                                <li><a className="dropdown-item" href="#" onClick={handleDelete}>Delete</a></li>
                            </ul>
                        </>
                    )}
                </div>
                <div className="description-container">
                    {editMode && user ? (
                        <div className="edit-video-body">
                            <div className="edit-video-container">
                                <div className="edit-video-title-container edit-container">
                                    <span className="desc-edit-span edit-span">Edit Title:</span>
                                    <textarea className="title-edit-textarea edit-textarea" value={titleEdit} onChange={handleTitleChange} />
                                </div>
                                <div className="edit-video-desc-container edit-container">
                                    <span className="desc-edit-span edit-span">Edit Description:</span>
                                    <textarea className="desc-edit-textarea edit-textarea" value={editedDesc} onChange={handleDescChange} />
                                </div>
                            </div>
                            <div className="save-btn-container">
                                <button class="save-edit-btn btn btn-primary" onClick={handleSave}>Save</button>
                            </div>
                        </div>

                    ) : (
                        <p className='under-video-desc'>{description}</p>
                    )}

                </div>

            </div>
            <span className="comments-title">Comments</span>
            <Add_comment_box addComment={addComment} userDetails={user}></Add_comment_box>

        </div>
    );
}
export default Under_video_bar