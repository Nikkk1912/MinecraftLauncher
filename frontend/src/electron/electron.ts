import { app, BrowserWindow } from 'electron';
import * as path from 'path';

let mainWindow: BrowserWindow | null = null;
const isDev = !app.isPackaged;

const createWindow = () => {
    mainWindow = new BrowserWindow({
        width: 1920,
        height: 720,
        webPreferences: {
            nodeIntegration: true,
        },
    });

    if (isDev) {
        // Load Vite + React app (wait for server)
        const VITE_DEV_SERVER_URL = 'http://localhost:5173';

        mainWindow.loadURL(VITE_DEV_SERVER_URL).catch((err) => {
            console.error('Failed to load Vite Dev Server:', err);
        });
    } else {
        // Load built React files from dist folder
        mainWindow.loadFile(path.join(__dirname, '../dist/index.html'));
    }

    mainWindow.on('closed', () => {
        mainWindow = null;
    });
};

app.whenReady().then(() => {
    createWindow();

    app.on('activate', () => {
        if (BrowserWindow.getAllWindows().length === 0) createWindow();
    });
});

app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') app.quit();
});
