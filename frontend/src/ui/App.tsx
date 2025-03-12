import './styles/App.scss'

import {useRef, useState} from "react";
import TitleBar from "./components/TitleBar/TitleBar.tsx";
import VersionTab from "./components/VersionTab/VersionTab.tsx";
import MainTab from "./components/MainTab/MainTab.tsx";
import SettingsTab from "./components/SettingsTab/SettingsTab.tsx";

function App() {
    const [selectedVersion, setSelectedVersion] = useState<string | null>(null);
    const [isVersionInstalled, setIsVersionInstalled] = useState<boolean | null>(false);

    // Reference to store the fetchVersions function from VersionTab
    const refreshVersionsRef = useRef<() => void>(() => {});

    // Function to set the refresh function from VersionTab
    const setVersionsRefreshFunction = (refreshFn: () => void) => {
        refreshVersionsRef.current = refreshFn;
    };

    // Function to trigger version refresh from MainTab
    const triggerVersionsRefresh = () => {
        refreshVersionsRef.current();
    };

    const handleVersionSelect = (version: string, isInstalled: boolean ) => {
        setSelectedVersion(version);
        setIsVersionInstalled(isInstalled);
    }

    // Function to update installation status
    const updateInstallationStatus = (isInstalled: boolean) => {
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
                    <VersionTab
                        onVersionSelect={handleVersionSelect}
                        setRefreshFunction={setVersionsRefreshFunction}
                    />
                </div>
                <div className={"middle-tab"}>
                    <MainTab
                        version={selectedVersion}
                        isInstalled={isVersionInstalled}
                        onTriggerVersionsRefresh={triggerVersionsRefresh}
                        updateInstallationStatus={updateInstallationStatus}
                    />
                </div>
                <div className={"right-tab"}>
                    <SettingsTab />
                </div>
            </div>
        </div>
    )
}

export default App;
