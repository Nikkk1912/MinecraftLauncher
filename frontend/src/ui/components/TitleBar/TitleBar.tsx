import React from "react";
import "./Titlebar.scss";

import CloseIcon from '../../assets/close-icon.svg?react';
import MaximizeIcon from '../../assets/maximize-icon.svg?react';
import MinimizeIcon from '../../assets/minimize-icon.svg?react';

const TitleBar: React.FC = () => {
    return (
        <div className={"title-bar"}>
            <div className={"title"}>Shedevro Launcher</div>
            <div className={"window-controls"}>
                <button className={"btn minimize"} onClick={() => window.electronAPI.minimizeWindow()}>
                    <MinimizeIcon className={"icon"} />
                </button>
                <button className={"btn maximize"} onClick={() => window.electronAPI.maximizeWindow()}>
                    <MaximizeIcon className={"icon"} />
                </button>
                <button className={"btn close"} onClick={() => window.electronAPI.closeWindow()}>
                    <CloseIcon className={"icon"} />
                </button>
            </div>
        </div>
    );
};

export default TitleBar;