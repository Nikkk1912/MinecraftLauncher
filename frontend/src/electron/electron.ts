import { app, BrowserWindow } from 'electron';

let mainWindow: BrowserWindow | null = null;

app.whenReady().then(() => {
    mainWindow = new BrowserWindow({
        width: 1280,
        height: 720,
        webPreferences: {
            nodeIntegration: true,
        },
    });

    mainWindow.loadURL('http://localhost:5173'); // Connects to React dev server

    app.on('window-all-closed', () => {
        if (process.platform !== 'darwin') app.quit();
    });
});
