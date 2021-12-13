import React from 'react';
import './css/App.css';
import './css/Post.css';
import './css/Home.css'
import { Switch, Route, Link } from 'react-router-dom';
import Home from './pages/Home';
import Post from './pages/Post';

const App = ({ws}) => {
  // todo, add more pages!
  return (
    <div>
      <nav>
        <Link to="/"> Home </Link>
        <Link to="/Post"> Post </Link>
      </nav>
      <Switch>
        <Route path="/Post">
          <Post ws={ws} />
        </Route>
        <Route path="/">
          <Home ws={ws} />
        </Route>
      </Switch>
    </div>
  );
};

export default App;