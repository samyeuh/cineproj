import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Dashboard from './page/Dashboard/Dashboard';
import FilmDetails from './page/FilmDetails/FilmDetails';
import Sign from './page/Sign/Sign';
import { UserProvider } from './context/UserContext';
import CinemaDetails from './page/CinemaDetails/CinemaDetails';
import CinemaOwner from './page/CinemaOwner/CinemaOwner';
import AdminPanel from './page/AdminPanel/AdminPanel';


function App() {

  return (
    <UserProvider>
      <Router>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/films/:id" element={<FilmDetails />} />
          <Route path="/login" element={<Sign />} />
          <Route path="/cinemas" element={<CinemaOwner />} />
          <Route path="/cinemas/:id" element={<CinemaDetails />} />
          <Route path="/admin" element={<AdminPanel />} />
        </Routes>
      </Router>
    </UserProvider>
  );
}

export default App;
