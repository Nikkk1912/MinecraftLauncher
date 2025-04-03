import "./SettingsTab.scss";
import React, { useState, useEffect } from "react";
import {
    getLauncherFolderPath,
    getMaxRamValue,
    getMinRamValue,
    saveSetting
} from "../../axios/configService.ts";

interface SettingsData {
    minecraftFolderPath: string;
    minRam: string;
    maxRam: string;
}

const SettingsTab: React.FC = () => {
    const [isExpanded, setIsExpanded] = useState<boolean>(false);
    const [settings, setSettings] = useState<SettingsData>({
        minecraftFolderPath: "",
        minRam: "",
        maxRam: ""
    });
    const [originalSettings, setOriginalSettings] = useState<SettingsData>({
        minecraftFolderPath: "",
        minRam: "",
        maxRam: ""
    });
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [isSaving, setIsSaving] = useState<boolean>(false);

    useEffect(() => {
        fetchSettings();
    }, []);

    const fetchSettings = async () => {
        setIsLoading(true);
        try {
            const minRamResponse = await getMinRamValue();
            const maxRamResponse = await getMaxRamValue();
            const folderPathResponse = await getLauncherFolderPath();

            const loadedSettings = {
                minecraftFolderPath: folderPathResponse.data,
                minRam: minRamResponse.data,
                maxRam: maxRamResponse.data
            };

            setSettings(loadedSettings);
            setOriginalSettings(loadedSettings);
        } catch (error) {
            console.error("Failed to fetch settings:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setSettings(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const isFieldChanged = (fieldName: keyof SettingsData): boolean => {
        return settings[fieldName] !== originalSettings[fieldName];
    };

    const areSettingsChanged = (): boolean => {
        return (
            isFieldChanged("minecraftFolderPath") ||
            isFieldChanged("minRam") ||
            isFieldChanged("maxRam")
        );
    };

    const saveSettings = async () => {
        setIsSaving(true);
        try {
            const savePromises = [];

            if (isFieldChanged("minecraftFolderPath")) {
                savePromises.push(saveSetting("minecraft_home", settings.minecraftFolderPath));
            }

            if (isFieldChanged("minRam")) {
                savePromises.push(saveSetting("minRam", settings.minRam));
            }

            if (isFieldChanged("maxRam")) {
                savePromises.push(saveSetting("maxRam", settings.maxRam));
            }

            await Promise.all(savePromises);

            setOriginalSettings({...settings});
        } catch (error) {
            console.error("Failed to save settings:", error);
        } finally {
            setIsSaving(false);
        }
    };

    const toggleExpand = () => {
        setIsExpanded(!isExpanded);
    };

    if (isLoading) {
        return <div className="settings-tab">Loading settings...</div>;
    }

    return (
        <div className="settings-tab">
            <div className="settings-header" onClick={toggleExpand}>
                <h2>Settings</h2>
                <div className={`arrow ${isExpanded ? "expanded" : ""}`}>
                    {isExpanded ? "▼" : "►"}
                </div>
            </div>

            {isExpanded && (
                <div className="settings-content">
                    <div className="setting-field">
                        <label htmlFor="minecraftFolderPath">Minecraft Folder Path</label>
                        <input
                            type="text"
                            id="minecraftFolderPath"
                            name="minecraftFolderPath"
                            className={`path-input ${isFieldChanged("minecraftFolderPath") ? "changed" : ""}`}
                            value={settings.minecraftFolderPath}
                            onChange={handleInputChange}
                        />
                        {isFieldChanged("minecraftFolderPath") && (
                            <div className="unsaved-changes">Changes not saved</div>
                        )}
                    </div>

                    <div className="setting-field">
                        <label htmlFor="minRam">Minimum RAM</label>
                        <input
                            type="text"
                            id="minRam"
                            name="minRam"
                            className={`nick-input ${isFieldChanged("minRam") ? "changed" : ""}`}
                            value={settings.minRam}
                            onChange={handleInputChange}
                        />
                        {isFieldChanged("minRam") && (
                            <div className="unsaved-changes">Changes not saved</div>
                        )}
                    </div>

                    <div className="setting-field">
                        <label htmlFor="maxRam">Maximum RAM</label>
                        <input
                            type="text"
                            id="maxRam"
                            name="maxRam"
                            className={`nick-input ${isFieldChanged("maxRam") ? "changed" : ""}`}
                            value={settings.maxRam}
                            onChange={handleInputChange}
                        />
                        {isFieldChanged("maxRam") && (
                            <div className="unsaved-changes">Changes not saved</div>
                        )}
                    </div>

                    <button
                        className="save-button"
                        onClick={saveSettings}
                        disabled={!areSettingsChanged() || isSaving}
                    >
                        {isSaving ? "Saving..." : "Save Changes"}
                    </button>
                </div>
            )}
        </div>
    );
};

export default SettingsTab;