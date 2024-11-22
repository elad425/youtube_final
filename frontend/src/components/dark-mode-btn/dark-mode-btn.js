import './dark-mode-btn.css'
import React, { useState } from 'react';
import { ReactComponent as Dark_mode_icon } from './dark-mode-icon.svg'
function Dark_mode_btn() {
    const [isDarkModeEnabled, setIsDarkModeEnabled] = useState(false);

    const enableDarkMode = () => {
        setIsDarkModeEnabled(!isDarkModeEnabled);
        document.body.classList.toggle('dark-mode-enabled', !isDarkModeEnabled);
    };
    return (
        <div className="darkmode-container">
           <button className="darkmode-btn" onClick={enableDarkMode}>
            <Dark_mode_icon/>
           </button>
        </div>

    );
}
export default Dark_mode_btn