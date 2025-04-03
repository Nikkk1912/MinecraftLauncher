import React, { useEffect, useState } from "react";
import "./MainTab.scss";
import { getPlayerName } from "../../axios/configService.ts";
import { installVersion } from "../../axios/installService.ts";
import { launchVersion } from "../../axios/launchService.ts";

type NickName = string;

interface MainTabProps {
    version?: string | null;
    isInstalled?: boolean | null;
    onTriggerVersionsRefresh: () => void;
    updateInstallationStatus: (isInstalled: boolean) => void;
}

const MainTab: React.FC<MainTabProps> = ({
                                             version,
                                             isInstalled,
                                             onTriggerVersionsRefresh,
                                             updateInstallationStatus,
                                         }) => {
    const [nickName, setNickName] = useState<NickName | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false); // NEW LOADING STATE

    const fetchSavedNickName = async () => {
        try {
            const response = await getPlayerName();
            setNickName(response.data);
        } catch (error) {
            console.error("Error fetching saved nickname:", error);
        }
    };

    const fetchInstall = async () => {
        if (!version) {
            console.error("Error: No version selected");
            return;
        }

        setLoading(true);
        try {
            const response = await installVersion(version);
            console.log(response.data);
            updateInstallationStatus(true);
            onTriggerVersionsRefresh();
        } catch (error) {
            console.error("Error:", error);
        } finally {
            setLoading(false);
        }
    };

    const fetchLaunch = async () => {
        if (!version) {
            console.error("Error: No version selected");
            return;
        }

        if (!nickName) {
            setError("Please enter a player name.");
            return;
        }

        setError(null);
        setLoading(true);

        try {
            const response = await launchVersion(version, nickName, true);
            console.log(response.data);
            updateInstallationStatus(true);
            onTriggerVersionsRefresh();
        } catch (error) {
            console.error("Error:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchSavedNickName();
    }, []);

    const handleNickInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (error) {
            setError(null);
        }
        setNickName(event.target.value);
    };

    return (
        <div className="MainTab">
            <p className="version-text">{version || "No version selected"}</p>
            <p className="version-status">{isInstalled ? "Installed" : "Not installed"}</p>

            <input
                className={`nick-input ${!nickName ? "error-border" : ""}`}
                type="text"
                placeholder="Enter nickname"
                value={nickName ?? ""}
                onChange={handleNickInputChange}
                disabled={loading}
            />

            {error && <div className="error-message" style={{ color: "red" }}>{error}</div>}

            <button
                className={`button ${isInstalled ? "launch-button" : "install-button"}`}
                onClick={() => (isInstalled ? fetchLaunch() : fetchInstall())}
                disabled={!version || loading}
            >
                {loading ? "Loading..." : isInstalled ? "Launch Game" : "Install Game"}
            </button>

            {loading && <div className="loading-spinner"></div>} {}
        </div>
    );
};

export default MainTab;
