import './add_comment_box.css';
import React, { useState } from 'react';
import { Link } from 'react-router-dom';

function Add_comment_box({addComment, userDetails }) {
    const [commentContent, setCommentContent] = useState('');
    let isConnected= true
    if (!userDetails){
        userDetails={
            icon:"/no-user-icon.jpg",
        }
        isConnected=false
    }
    const handleInputChange = (event) => {
        setCommentContent(event.target.value);
    };

    const sendCommentDetails = () => {
        addComment(commentContent, userDetails._id);
        setCommentContent(''); // Reset the input field
    };

    const commentBoxContent = (
        <div className="add-comment-container">
            <div className="add-comment-icon-content">
                <a href="#" className="icon-container">
                    <img className="channel-icon" src={userDetails.icon} alt="channel icon" />
                </a>
                <input
                    className="form-control comment-input"
                    type="text"
                    placeholder="Add a comment..."
                    value={commentContent}
                    onChange={handleInputChange}
                />
            </div>
            <div className="add-comment-button-container" onClick={sendCommentDetails}>
                <button className="comment-button">Comment</button>
            </div>
        </div>
    );

    return isConnected ? (
        commentBoxContent
    ) : (
        <Link to="/login" className="no-style-link">
            {commentBoxContent}
        </Link>
    );
}

export default Add_comment_box;
