import { app, BrowserWindow, ipcMain } from 'electron';
import * as path from 'path';

let mainWindow: BrowserWindow | null = null;
const isDev = !app.isPackaged;

const createWindow = () => {
    mainWindow = new BrowserWindow({
        width: 1920,
        height: 1080,
        minWidth: 960,
        minHeight: 540,
        frame: false,
        backgroundColor: '#2d2d2d',
        webPreferences: {
            nodeIntegration: false,
            contextIsolation: true,
            preload: path.join(__dirname, 'preload.js')
        },
    });

    mainWindow.setMenuBarVisibility(false);

    if (isDev) {
        const VITE_DEV_SERVER_URL = 'http://localhost:5173';

        mainWindow.loadURL(VITE_DEV_SERVER_URL).catch((err) => {
            console.error('Failed to load Vite Dev Server:', err);
        });
    } else {
        mainWindow.loadFile(path.join(__dirname, '../dist/index.html'));
    }

    mainWindow.on('closed', () => {
        mainWindow = null;
    });
};

ipcMain.on("window-close", () => {
    if (mainWindow) mainWindow.close();
});

ipcMain.on("window-minimize", () => {
    if (mainWindow) mainWindow.minimize();
});

ipcMain.on("window-maximize", () => {
    if (mainWindow) {
        if (mainWindow.isMaximized()) {
            mainWindow.restore();
        } else {
            mainWindow.maximize();
        }
    }
});

app.whenReady().then(() => {
    createWindow();

    app.on('activate', () => {
        if (BrowserWindow.getAllWindows().length === 0) createWindow();
    });
});

app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') app.quit();
});
