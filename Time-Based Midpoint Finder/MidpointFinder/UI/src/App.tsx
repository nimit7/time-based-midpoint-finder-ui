import styles from './App.module.scss';
import MapComponent from './components/MapComponent';

function App() {
  return (
    <div className={styles.App}>
      <header className="App-header">
        Time-Based Mid-Point Finder
      </header>
      <MapComponent />
    </div>
  );
}

export default App;
