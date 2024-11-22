import './upload_popup.css'
import React, { useState } from 'react';
import { ReactComponent as Upload_icon } from './upload_icon.svg'
import Video_upload_modal from './video_upload_modal/video_upload_modal';
import Video_details_modal from './video_details_modal/video_details_modal';


function Upload_popup({ closeModal,addVideo }) {
  const [popupScreen, setPopupScreen] = useState(false);
  const [file, setFile] = useState(null);

  const togglePopup = () => {
    setPopupScreen(!popupScreen);
  };

  const handleFileUpload = (selectedFile) => {
    setFile(selectedFile);
    setPopupScreen(true);
  };
  
  return (
    <div className="modal-container">
      <div className="modal fade show" id="exampleModalToggle" aria-hidden="true" aria-labelledby="exampleModalToggleLabel" tabindex="-1" style={{ display: 'block' }}>
        <div className="modal-dialog modal-dialog-centered modal-lg">
          <div className="modal-content">
          <div className="modal-header">
              <h1 className="modal-title fs-5" id="exampleModalToggleLabel">Upload video</h1>
              
              <button type="button" className="btn-close" onClick={closeModal} aria-label="Close"></button>
              
            </div>
            <div className="modal-body">
            {popupScreen ? <Video_details_modal togglePopup={togglePopup} closeModal={closeModal} addVideo={addVideo} video_file={file} /> : <Video_upload_modal togglePopup={togglePopup}  handleFileUpload={handleFileUpload}/>}
            </div>
          </div>
        </div>
      </div>

    </div>
  );
}
export default Upload_popup;

