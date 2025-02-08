import './styles/App.scss'

import { useState} from "react";
import TitleBar from "./components/TitleBar/TitleBar.tsx";
import VersionTab from "./components/VersionTab/VersionTab.tsx";

function App() {
    const [selectedVersion, setSelectedVersion] = useState<string | null> (null);
    const [isVersionInstalled, setIsVersionInstalled] = useState<boolean | null> (false);

    const handleVersionSelect = (version: string, isInstalled: boolean ) => {
        setSelectedVersion(version);
        setIsVersionInstalled(isInstalled);
    }

    return (
        <div>
            <TitleBar/>
            <div className="main-body">
                <div className={"version-tab"}>
                    <div style={{ display: "flex", justifyContent: "center", alignItems: "center" }}>
                        <h1>Versions:</h1>
                    </div>
                    <VersionTab onVersionSelect={handleVersionSelect} />
                </div>
                <div className={"middle-tab"}>
                    {selectedVersion} , {isVersionInstalled}
                </div>
                <div className={"right-tab"}>
                    right
                </div>
            </div>
        </div>
    )
}

export default App
