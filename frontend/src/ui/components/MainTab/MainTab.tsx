import React, { useState } from "react";
import axios from "axios";
import "./MainTab.scss";

type NickName = string;

interface MainTabProps {
    version?: string | null;
    isInstalled?: boolean | null;
}

const MainTab: React.FC<MainTabProps> = ({version, isInstalled}: MainTabProps) => {

    const fetchLaunch = () => {
        axios.post('http://localhost:8000/api/execute', {
            versionNum: version?.toString() || "",
            playerName: nickName || ""
        }, {
            headers: { "Content-Type": "application/json" }
        })
            .then(response => console.log(response.data))
            .catch(error => console.error("Error:", error));
    };


    const showMessageNotInstalled = () => {
      return (
          <div>
              <p>Not installed version.</p>
          </div>
      )
    }

    const [nickName, setNickName] = useState<NickName | null>(null);

    const handleNickInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setNickName(event.target.value);
    }

    return (
        <div className="MainTab">
            <p>Version: {version}</p>
            <p>Installed: {isInstalled?.toString() ?? "Loading..."}</p>
            <input
                type="text"
                placeholder="Enter search term"
                value={nickName ?? ""}
                onChange={handleNickInputChange} // Update state on input change
            />
            <button onClick={() => (isInstalled ? fetchLaunch() : showMessageNotInstalled())}>Launch</button>
        </div>
    )
};

export default MainTab;