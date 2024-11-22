import './like_dislike_buttons.css';
import { ReactComponent as Like_icon } from './like-icon.svg';
import { ReactComponent as Dislike_icon } from './dislike-icon.svg';
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';


function Like_dislike_buttons({videoId, user, dislikesNum, likesNum, likedState, dislikeVideo, likeVideo }) {
    const [activeButton, setActiveButton] = useState(null);
    const [likes,setLikes]= useState (likesNum)
    const [dislikes,setDislikes] = useState(dislikesNum)
    useEffect(() => {
        console.log("bane",likedState)
        setLikes(likesNum)
        setDislikes(dislikesNum)
        if (likedState === 1) {
            setActiveButton('like');
        } else if (likedState === 2) {
            setActiveButton('dislike');
        } else {
            setActiveButton(null);
        }
    }, [videoId, likedState, likesNum, dislikesNum]);



    const handleButtonClick = async (buttonType) => {
        if (user) {
            if (buttonType === 'like') {
                if (activeButton === 'like') {
                    setActiveButton(null);
                    await likeVideo(); // Remove like
                } else {
                    if (activeButton === 'dislike') {
                        setActiveButton('like');
                        await dislikeVideo(); // Remove dislike
                    }
                    setActiveButton('like');
                    await likeVideo(); // Add like
                }
            } else if (buttonType === 'dislike') {
                if (activeButton === 'dislike') {
                    setActiveButton(null);
                    await dislikeVideo(); // Remove dislike
                } else {
                    if (activeButton === 'like') {
                        setActiveButton('dislike');
                        await likeVideo(); // Remove like
                    }
                    setActiveButton('dislike');
                    await dislikeVideo(); // Add dislike
                }
            }
        }
    };
    const likeDislike_content = (

        <div className="btn-group" role="group" aria-label="Basic outlined example">
            <button
                className={`btn btn-outline-primary under-btn ${activeButton === 'like' ? 'active-btn' : ''}`}
                id="like"
                onClick={() => handleButtonClick('like')}
            >
                <Like_icon />
                <span className='like-count'>{likes}</span>

            </button>
            <button
                className={`btn btn-outline-primary under-btn ${activeButton === 'dislike' ? 'active-btn' : ''}`}
                onClick={() => handleButtonClick('dislike')}
            >
                <Dislike_icon />
                <span className='like-count'>{dislikes}</span>
            </button>

        </div>

    )

    return user ? (
        likeDislike_content
    ) : (
        <Link to="/login" className="no-style-link">
            {likeDislike_content}
        </Link>
    );
}

export default Like_dislike_buttons;
