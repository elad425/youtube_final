import './video_upload_modal.css'
import React, { useState, useRef } from 'react';

import { ReactComponent as Upload_icon } from '../upload_icon.svg'

function Video_upload_modal({ togglePopup, handleFileUpload }) {
    const [file, setFile] = useState(null);
    const [error, setError] = useState('');
    const fileInputRef = useRef(null);

    const validVideoExtensions = ['mp4', 'avi', 'mkv', 'mov', 'wmv', 'webm'];

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (selectedFile) {
            const fileExtension = selectedFile.name.split('.').pop().toLowerCase();
            if (validVideoExtensions.includes(fileExtension)) {
                setFile(selectedFile);
                setError('');
            } else {
                setFile(null);
                setError('Invalid file type. Please select a video file.');
                if (fileInputRef.current) {
                    fileInputRef.current.value = null; // Clear the file input
                }
            }
        } else {
            setFile(null);
            setError('No file chosen. Please select a file to upload.');
        }
    };
    const handleUploadClick = async () => {
        if (!file) {
            setError('No file chosen or invalid file type. Please select a valid video file to upload.');
            return;
        }
        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await fetch(`/upload`, {
                method: 'POST',
                body: formData,
            });
            const json = await response.json();
            handleFileUpload(json.path); // Assuming handleFileUpload updates the video list
            togglePopup();
        } catch (error) {
            console.log("Video upload failed", error);
            setError('Video upload failed. Please try again.');
        }
      
    };
    return (
        <div className="upload-modal-body">
            <div className="popup-body">
                <div className="upload-icon">
                    <Upload_icon className="upload-icon-1" />
                </div>
                <span className='select-span' >Select video to upload</span>
                <div className="m-3">
                    <input
                        className="form-control"
                        type="file"
                        id="formFile"
                        onChange={handleFileChange}
                        ref={fileInputRef}
                    />
                </div>
                {error && <span className='error-message'>{error}</span>}
                <button className='btn btn-primary select-btn' onClick={handleUploadClick}>UPLOAD</button>
                <div className="tos-div">
                    <span className='popup-span'>By submitting your videos to YouTube, you acknowledge that you agree to YouTube's Terms of Service and Community Guidelines.</span>
                    <span className='popup-span'>Please be sure not to violate others' copyright or privacy rights. Learn more</span>
                </div>
            </div>
        </div>
    );
}
export default Video_upload_modal;

