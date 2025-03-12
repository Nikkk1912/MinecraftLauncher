import React, {useState, useEffect} from "react";
import './VersionTab.scss';
import {getAllVersions, getInstalledVersions} from "../../axios/versionsService.ts";
import {getLastLaunchedVersion} from "../../axios/configService.ts";

type MinecraftVersion = string;
type InstalledVersion = string;

interface VersionTabProps {
    onVersionSelect: (version: string, isInstalled: boolean) => void;
    setRefreshFunction?: (refreshFn: () => void) => void;
}

const VersionTab: React.FC<VersionTabProps> = ({ onVersionSelect, setRefreshFunction }: VersionTabProps) => {
    const [versions, setVersions] = useState<MinecraftVersion[]>([]);
    const [selectedVersion, setSelectedVersion] = useState<MinecraftVersion | null>(null);
    const [installedVersions, setInstalledVersions] = useState<InstalledVersion[]>([]);
    const [expandedGroups, setExpandedGroups] = useState<Record<string, boolean>>({});
    const [error, setError] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(false);

    const fetchVersions = async () => {
        setIsLoading(true);
        setError(null);

        try {
            const installedResponse = await getInstalledVersions();
            const installed = installedResponse.data;

            const allResponse = await getAllVersions();
            const allVersions = Array.from(new Set([...allResponse.data, ...installed]));
            setVersions(allVersions);
            setInstalledVersions(installed);
        } catch (err) {
            console.error("Error fetching versions:", err);
            setError("Reload");
        } finally {
            setIsLoading(false);
        }
    };

    const fetchLastLaunchedVersion = async () => {
        try {
            const response = await getLastLaunchedVersion();
            const lastLaunchedVersion = response.data;
            if (installedVersions.includes(lastLaunchedVersion)) {
                setSelectedVersion(lastLaunchedVersion);
                onVersionSelect(lastLaunchedVersion, isInstalled(lastLaunchedVersion));
            }
        } catch (error) {
            console.error("Error fetching last launched version:", error);
        }
    };

    useEffect(() => {
        fetchVersions();
    }, []);

    useEffect(() => {
        if (setRefreshFunction) {
            setRefreshFunction(fetchVersions);
        }
    }, [setRefreshFunction]);

    useEffect(() => {
        if (installedVersions.length > 0) {
            fetchLastLaunchedVersion();
        }
    }, [installedVersions]);

    const handleVersionSelect = (version: MinecraftVersion) => {
        setSelectedVersion(version);
        onVersionSelect(version, isInstalled(version));
    };

    if (isLoading) { return <div style={{textAlign: "center", padding: "20px"}}> Loading... </div>; }
    if (error) { fetchVersions(); }

    const sortVersions = (a: string, b: string) => {
        const parseVersion = (version: string) => version.split('.').map(Number);

        const aParts = parseVersion(a);
        const bParts = parseVersion(b);

        if (aParts[0] !== bParts[0]) return bParts[0] - aParts[0];
        if (aParts[1] !== bParts[1]) return bParts[1] - aParts[1];

        return 0;
    };

    const groupVersions = () => {
        return versions.reduce((groups: Record<string, MinecraftVersion[]>, version) => {
            let groupKey = "Snapshots";

            if (version.toLowerCase().includes("forge")) {
                groupKey = "Forge";
            } else if (version.toLowerCase().includes("pre") || version.toLowerCase().includes("rc")) {
                groupKey = "Others";
            } else if (/^\d+\.\d+$/.test(version)) {
                groupKey = version;
            } else if (/^\d+\.\d+\.\d+$/.test(version)) {
                const parts = version.split('.');
                groupKey = `${parts[0]}.${parts[1]}`;
            }

            if (!groups[groupKey]) {
                groups[groupKey] = [];
            }
            groups[groupKey].push(version);

            return groups;
        }, {});
    };

    const groupedVersions = groupVersions();

    const sortedGroups = Object.keys(groupedVersions)
        .filter(key => key !== "Snapshots" && key !== "Forge")
        .sort(sortVersions);

    if (groupedVersions["Forge"]) sortedGroups.push("Forge");
    if (groupedVersions["Snapshots"]) sortedGroups.push("Snapshots");


    const isInstalled = (version: MinecraftVersion) => {
        return installedVersions.includes(version);
    };

    const toggleGroup = (majorVersion: string) => {
        setExpandedGroups(prev => ({
            ...prev,
            [majorVersion]: !prev[majorVersion]
        }));
    };

    return (
        <div className="scrollable-container">
            {sortedGroups
                .map(majorVersion => (
                    <div key={majorVersion} className="version-group-container">
                        <h3
                            className="group-title"
                            onClick={() => toggleGroup(majorVersion)}
                        >
                            {majorVersion === "Snapshots"
                                ? "Snapshots"
                                : majorVersion === "Forge"
                                    ? "Forge Versions"
                                    : `${majorVersion}`}
                            <span className="toggle-icon">
                        {expandedGroups[majorVersion] ? "▼" : "▶"}
                    </span>
                        </h3>
                        {expandedGroups[majorVersion] && (
                            <div className="version-group">
                                {groupedVersions[majorVersion].map(version => (
                                    <div
                                        key={version}
                                        className={`version-item ${isInstalled(version) ? 'installed' : 'not-installed'} ${selectedVersion === version ? 'selected' : ''}`}
                                        onClick={() => handleVersionSelect(version)}
                                    >
                                        {version}
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                ))}
        </div>
    );
};

export default VersionTab;
