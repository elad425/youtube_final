import React, { useState, useEffect } from 'react';
import HOME_BUTTON from '../components/home_button/home_button';
import SEARCH_BAR from '../components/search_bar/search_bar';
import SIGN_IN_BUTTON from '../components/sign_in_button/sign_in_button';
import BURGER_MENU from '../components/burger_menu/burger_menu';
import CREATE_BUTTON from '../components/create_video_button/create_video_button';
import SIDE_BAR from '../components/side_bar/side_bar';
import CLOSED_SIDE_BAR from '../components/closed_side_bar/closed_side_bar';
import VIDEO_FILTER_BUTTONS from '../components/video_filter_buttons/video_filter_buttons';
import UPLOAD_POPUP from '../components/upload_popup/upload_popup';
import VIDEO from '../components/video_card/video_card';
import VIDEO_WATCH from '../components/video_watch/video_watch';
import DARK_MODE_BTN from '../components/dark-mode-btn/dark-mode-btn';
import USER_LOGO from '../components/user_logo/user_logo';
import './home_screen.css';

function Home_screen({ user, isUserLoggedIn, setUser, setIsUserLoggedIn }) {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(true); // Add loading state
  const [isVideoWatched, setIsVideoWatched] = useState(false);
  const [videoBeingWatched, setVideoBeingWatched] = useState('');
  const [videoList, setVideoList] = useState([]);
  const [searchVideoList, setSearchVideoList] = useState([]);
  const [sideBarVideos,setSidebarVideos] = useState([]);

  useEffect(() => {
    const fetchVideos = async () => {
      const response = await fetch('api/videos')
      const json = await response.json()
      if (response.ok) {
        setVideoList(json)
        setSearchVideoList(json); // Ensure searchVideoList is also populated
        setIsLoading(false); // Set loading to false after fetch

      }
    }

    fetchVideos()
  }, [])


  const updateVideoInList = (updatedVideo) => {
    setVideoList(prevVideoList =>
      prevVideoList.map(video =>
        video._id === updatedVideo._id ? updatedVideo : video
      )
    );
    setSearchVideoList(prevSearchVideoList =>
      prevSearchVideoList.map(video =>
        video._id === updatedVideo._id ? updatedVideo : video
      )
    );
  };
  const updateViews = async (views, videoId) => {
    const data = {
        views: views + 1
    };
    let username;

    if (user === null) {
        username = 0;
    } else {
        username = user._id;
    }

    try {
        const response = await fetch(`api/videos/${videoId}/views/${username}`, {
            method: 'PATCH',
            headers: {
                "Content-Type": 'application/json'
            },
            body: JSON.stringify(data)
        });

        const { video, recommendations } = await response.json();  

        updateVideoInList(video);

        const list = recommendations;
        console.log("Recommendations list:", list);
        updateVideoSideBar(list);
        return list;

    } catch (error) {
        console.log("Updating views failed");
    }
  };
  const updateVideoSideBar = (recommendations) => {
    const recommendedIds = recommendations.split(',');

    // Map through the recommendedIds to preserve the order
    const orderedVideos = recommendedIds.map(id => 
        videoList.find(video => video._id === id)
    ).filter(video => video !== undefined); // Remove any undefined videos in case of missing IDs

    // Set the sidebar videos with the ordered list
    setSidebarVideos(orderedVideos);
};
  const clearSearch = () => {
    setVideoList(searchVideoList);
  };
  const addComment = (videoId, comment) => {
    setVideoList(prevVideoList =>
      prevVideoList.map(video =>
        video.id === videoId ? { ...video, comments: [...video.comments, comment] } : video
      )
    );
    setSearchVideoList(videoList)
  };

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const doSearch = (q) => {
    const lowerCaseQuery = q.toLowerCase();
    setVideoList(
      searchVideoList.filter((video) =>
        video.title.toLowerCase().includes(lowerCaseQuery)
      )
    );
  };
  const resetVideos =()=>{
    setVideoList(searchVideoList)
  }

  const toggleModal = () => {
    setIsModalOpen(!isModalOpen);
  };
  const filterUserVids = (id)=>{
    setVideoList(
      searchVideoList.filter((video) =>
        video.userDetails._id == id
      )
    );
  }
  const addVideo = async (title, description, thumbnail, video_src) => {
    const data = {
      title: title,
      description: description,
      thumbnail: thumbnail,
      channelName: user.username,
      video_src: video_src,
      userDetails: user._id
    }
    try {
      const response = await fetch(`api/users/${user._id}/videos`, {
        method: 'post',
        headers: {
          "content-Type": 'application/json'
        },
        body: JSON.stringify(data)
      })
      const json = await response.json()
      setVideoList([...videoList, json]);
      setSearchVideoList([...searchVideoList, json]);

      //setUserList([...usersList, json])
    }
    catch (error) {
      console.log("Video creation failed")
    }
  };
  const toggleHomeView = () => {
    setIsVideoWatched(false)
  }
  const toggleVideoView = async (video) => {
    setVideoBeingWatched(video._id)
    updateViews(video.views, video._id)
    setIsVideoWatched(true);
  };

  const returnToHome = () => {
    setIsVideoWatched(false);
  };

  return (
    <div className="site_container">
      <div className="top_header">
        <div className="menu-and-button">
          <BURGER_MENU toggleSidebar={toggleSidebar} />
          <HOME_BUTTON returnToHome={returnToHome} resetVideos={resetVideos}/>
        </div>
        <SEARCH_BAR doSearch={doSearch} clearSearch={clearSearch} />
        <CREATE_BUTTON onClick={toggleModal} user={user} />
        <DARK_MODE_BTN />
        {isUserLoggedIn ? <USER_LOGO user={user} setIsUserLoggedIn={setIsUserLoggedIn} setUser={setUser} returnToHome={returnToHome} resetVideos={resetVideos}/> : <SIGN_IN_BUTTON />}
      </div>
      <div className="page-container">
        {isModalOpen && <UPLOAD_POPUP closeModal={toggleModal} addVideo={addVideo} />}
        {isLoading ? ( // Show spinner if loading
          <div className="d-flex justify-content-center text-center ">
            <div className="spinner-border m-5" style={{ height: '10rem', width: '10rem' }} role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
          </div>
        ) : !isVideoWatched ? (
          <div className="body-container">
            {isSidebarOpen ? <SIDE_BAR isUserLoggedIn={isUserLoggedIn} /> : <CLOSED_SIDE_BAR />}
            <div className={isSidebarOpen ? 'video-page-container-reduced' : 'video-page-container-full'}>
              <VIDEO_FILTER_BUTTONS />
              <div className='videos-container-grid'>
                {videoList.map((video) => (
                  <VIDEO key={video._id} id={video._id} video={video} toggleView={toggleVideoView} filterUserVids = {filterUserVids} />
                ))}
              </div>
            </div>
          </div>
        ) : (
          <div className="watch-video-view-body">
            <VIDEO_WATCH
              id={videoBeingWatched}
              videoList={videoList}
              toggleVideoView={toggleVideoView}
              addComment={addComment}
              user={user}
              setVideoList={setVideoList}
              setSearchVideoList={setSearchVideoList}
              toggleHomeView={toggleHomeView}
              updateVideoInList={updateVideoInList}
              sideBarVideos = {sideBarVideos}
              
            />
          </div>
        )}
      </div>
    </div>
  );
}
export default Home_screen;
