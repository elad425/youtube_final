import React, { useState, useEffect } from 'react';
import './home_button.css';

function Home_button({ returnToHome,resetVideos}) {
  const [imageSrc, setImageSrc] = useState('yt_logo_rgb_light.png');

  const returnHome = () => {
    resetVideos();
    returnToHome();
  };
  

  useEffect(() => {
    const updateImageSrc = () => {
      if (document.body.classList.contains('dark-mode-enabled')) {
        setImageSrc('yt_logo_rgb_dark.png');
      } else {
        setImageSrc('yt_logo_rgb_light.png');
      }
    };
    updateImageSrc();
    const observer = new MutationObserver(updateImageSrc);
    observer.observe(document.body, { attributes: true, attributeFilter: ['class'] });
    return () => observer.disconnect();
  }, []);

  return (
    <div className="home_button_div" onClick={returnHome}>
      <button className="home_button">
        <img src={imageSrc} className="img-fluid yt-image" alt="Responsive image" />
      </button>
    </div>
  );
}

export default Home_button;
