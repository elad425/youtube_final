import './video_card.css'
//import React, { useState } from 'react';

function Video_card({ video ,toggleView,filterUserVids}) {

    //const [videoState, setVideoState] = useState(null);
    const switchView = () => {
        toggleView(video);
    }
    const filterVideos = () => {
        filterUserVids(video.userDetails._id);
    }
   
    return (
        <div className="video-card-container"  >
            <a href="#" className="thumbnail" onClick={switchView}>
                <img className="thumbnail-image" src={video.thumbnail} />
            </a>

            <div className="video-bottom-section">
                <a href="#" className="icon-container">
                    <img className="channel-icon" onClick={filterVideos} src={video.userDetails.icon} />
                </a>
                <div className="video-details">
                    <a href="#" className="video-title" onClick={switchView}>{video.title}</a>
                    <a href="#" className="video-channel-name" onClick={filterVideos}>{video.userDetails.username}</a>
                    <div className="video-metadata">
                        <span className="views">{video.views} views</span>
                        •
                        <span className="date">{video.date}</span>
                    </div>
                </div>
              
            </div>
        </div>

    );
}
export default Video_card