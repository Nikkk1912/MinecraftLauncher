import './styles/App.scss'
import TitleBar from "./components/TitleBar/TitleBar.tsx";
import VersionTab from "./components/VersionTab/VersionTab.tsx";

function App() {
    return (
        <div>
            <TitleBar/>
            <div className="main-body">
                <div className={"version-tab"}>
                    <div style={{ display: "flex", justifyContent: "center", alignItems: "center" }}>
                        <h1>Versions:</h1>
                    </div>
                    <VersionTab />
                </div>
                <div className={"middle-tab"}>
                    test
                </div>
                <div className={"right-tab"}>
                    right
                </div>
            </div>
        </div>
    )
}

export default App
