import './video_watch.css';
import React, { useEffect, useState } from 'react';
import UNDER_VIDEO_SECTION from '../under-video-bar/under-video-bar';
import VIDEO_SIDE_BAR from '../video_side_bar/video_side_bar';
import COMMENT_BOX from '../comment_box/comment_box';

function Video_watch({ id, videoList, toggleVideoView, user, setVideoList, setSearchVideoList, toggleHomeView, updateVideoInList,sideBarVideos }) {
    const [videoCommentsList, setVideoCommentsList] = useState([]);
    const [videoDetails, setVideoDetails] = useState(null);
    const [videoDesc, setVideoDesc] = useState("")
    const [editedTitle, setEditedTitle] = useState("");
    const [videoWatched, setVideoWatched] = useState(null)
   
    useEffect(() => {
        const getVideo = () => {
            const video = videoList.find(video => video._id === id);
            if (video) {
                setVideoDetails(video);
                setVideoWatched(video.video_src);
                setVideoDesc(video.description);
                setEditedTitle(video.title);
                getComments(video._id)
            }
        }

       
        const getComments = async (id) => {
            try {
                const response = await fetch(`api/videos/comment/${id}`)
                const json = await response.json()
                if (response.ok) {
                    setVideoCommentsList(json)
                }
            }
            catch (error) {
                console.log(error)
            }
        }
        getVideo()
    }, [id, videoList]);
    const addComment = async (commentMessage, userId) => {
        const token = localStorage.getItem('token');
        const data = {
            commentMessage: commentMessage,
            video_id: videoDetails._id,
            user_id: userId // Ensure the correct user ID is used
        };
        try {
            const response = await fetch(`api/videos/comment/`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(data),
            });
            const json = await response.json();
            if (response.ok) {
                // Ensure user details are included in the new comment
                const newComment = {
                    ...json,
                    user_id: {
                        _id: user._id,
                        username: user.username,
                        icon: user.icon
                    }
                };
                setVideoCommentsList(prevComments => [...prevComments, newComment]);
            }
        } catch (error) {
            console.log("Adding comments failed", error);
        }
    };
    const editComment = async (commentId, newCommentMessage) => {
        const token = localStorage.getItem('token');
        const data = {
            commentMessage: newCommentMessage
        };
        try {
            const response = await fetch(`/api/videos/comment/${commentId}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(data),
            });
            const json = await response.json();
            if (response.ok) {
                setVideoCommentsList(prevComments =>
                    prevComments.map(comment =>
                        comment._id === commentId ? json : comment
                    )
                );
            }
        } catch (error) {
            console.log("Editing comments failed", error);
        }
    };
    const editVideo = async (newDesc, newTitle) => {
        const token = localStorage.getItem('token');
        const data = {
            title: newTitle,
            description: newDesc
        };
        try {
            const response = await fetch(`/api/users/${videoDetails.userDetails._id}/videos/${videoDetails._id}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(data),
            });
            const json = await response.json();
            if (response.ok) {
                updateVideoInList(json);
                setVideoDesc(newDesc);
                setEditedTitle(newTitle);
            }
        } catch (error) {
            console.log("Editing video failed", error);
        }
    };
    const changeLikedBy = (num) => {
        if (videoDetails) {
            videoDetails.likedBy = num;
        }
    }
    const deleteVideo = async () => {
        const token = localStorage.getItem('token');
        try {
            const response = await fetch(`/api/users/${videoDetails.userDetails._id}/videos/${videoDetails._id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            if (response.ok) {
                const updatedVideoList = videoList.filter(video => video._id !== id);
                setVideoList(updatedVideoList);
                setSearchVideoList(updatedVideoList);
                toggleHomeView();
            }
        } catch (error) {
            console.log("Deleting video failed", error);
        }
    };
    const deleteComment = async (commentId) => {
        const token = localStorage.getItem('token');
        try {
            const response = await fetch(`/api/videos/comment/${commentId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            if (response.ok) {
                const prevCommentList = videoCommentsList.filter(comment => comment._id !== commentId)
                setVideoCommentsList(prevCommentList)
            }
        } catch (error) {
            console.log("Deleting comments failed", error);
        }
    };
    const resetComments = (id) => {
        //setVideoCommentsList([]);
        toggleVideoView(id);
    }
    if (!videoDetails) return null;

    return (
        <div className="video-watch-body">
            <div className="video-desc-comments-container">
                <div className="video-watch-container">
                    <video className="video-file" controls key={videoWatched} >
                        <source src={videoWatched} type="video/mp4" />
                        Your browser does not support the video tag.
                    </video>
                </div>
                <span className='video-watch-title'>{editedTitle}</span>
                <div className="video-watch-btn-container">
                    <UNDER_VIDEO_SECTION
                        video={videoDetails}
                        videoId={videoDetails._id}
                        description={videoDesc}
                        videoUserDetails={videoDetails.userDetails}
                        user={user}
                        views={videoDetails.views}
                        date={videoDetails.date}
                        addComment={addComment}
                        editVideo={editVideo}
                        deleteVideo={deleteVideo}
                        title={editedTitle}
                        setVideoList={setVideoList}
                        likes={videoDetails.likes}
                        dislikes={videoDetails.dislikes}
                        videoList={videoList} />
                </div>
                <div className="comments-container">
                    {videoCommentsList.map((comment) => (
                        <COMMENT_BOX
                            key={comment._id}
                            comment={comment}
                            user={user}
                            editComment={editComment}
                            deleteComment={deleteComment}
                            videoId={videoDetails._id}
                            setVideoList={setVideoList}
                        />
                    ))}
                </div>
            </div>
            <div className="video-side-bar">
                <VIDEO_SIDE_BAR videoList={sideBarVideos} toggleVideoView={resetComments} />
            </div>
        </div>
    );
}

export default Video_watch;
