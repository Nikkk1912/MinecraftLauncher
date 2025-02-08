import React, {useState, useEffect} from "react";
import axios from 'axios';
import './VersionTab.scss';

type MinecraftVersion = string;
type InstalledVersion = string;

interface VersionTabProps {
    onVersionSelect: (version: string, isInstalled: boolean) => void;
}

const VersionTab: React.FC<VersionTabProps> = ({ onVersionSelect }: VersionTabProps) => {
    const [versions, setVersions] = useState<MinecraftVersion[]>([]);
    const [installedVersions, setInstalledVersions] = useState<InstalledVersion[]>([]);
    const [expandedGroups, setExpandedGroups] = useState<Record<string, boolean>>({});
    const [error, setError] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(false);

    const fetchVersions = () => {
        setIsLoading(true);
        setError(null);

        axios.get("http://localhost:8000/api/version/installed")
            .then(async installedResponse => {
                const installed = installedResponse.data;

                const allResponse = await axios.get("http://localhost:8000/api/version/all");
                const allVersions = Array.from(new Set([...allResponse.data, ...installed]));
                setVersions(allVersions);
                setInstalledVersions(installed);
            })
            .catch(err => {
                console.error("Error fetching versions:", err);
                setError("Reload");
            })
            .finally(() => setIsLoading(false)); // Stop loading when done
    };

    useEffect(() => { fetchVersions(); }, []);

    if (isLoading) { return <div style={{textAlign: "center", padding: "20px"}}> Loading... </div>; }
    if (error) { fetchVersions(); }

    const sortVersions = (a: string, b: string) => {
        const parseVersion = (version: string) => version.split('.').map(Number);

        const aParts = parseVersion(a);
        const bParts = parseVersion(b);

        // Compare major versions first
        if (aParts[0] !== bParts[0]) return bParts[0] - aParts[0];

        // Compare minor versions next
        if (aParts[1] !== bParts[1]) return bParts[1] - aParts[1];

        return 0; // If they're identical, keep order
    };

    const groupVersions = () => {
        return versions.reduce((groups: Record<string, MinecraftVersion[]>, version) => {
            let groupKey = "Snapshots"; // Default to "Snapshots"

            if (version.toLowerCase().includes("forge")) {
                groupKey = "Forge";
            } else if (version.toLowerCase().includes("pre") || version.toLowerCase().includes("rc")) {
                // Handles "pre" and "rc" versions
                groupKey = "Others";
            } else if (/^\d+\.\d+$/.test(version)) {
                // Matches "1.21" (two numbers)
                groupKey = version;
            } else if (/^\d+\.\d+\.\d+$/.test(version)) {
                // Matches "1.21.1" (three numbers)
                const parts = version.split('.');
                groupKey = `${parts[0]}.${parts[1]}`; // Convert "1.21.1" → "1.21"
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
                                        className={`version-item ${isInstalled(version) ? 'installed' : 'not-installed'}`}
                                        onClick={() => onVersionSelect(version, isInstalled(version))}
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
