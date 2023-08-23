import React from 'react';
import ReactDOM from 'react-dom/client';
import { RecoilRoot } from 'recoil';
import { BrowserRouter } from 'react-router-dom';

import ScrollToTop from 'utils/ScrollTop';
import App from './App';
import './main.css';
import reportWebVitals from './reportWebVitals';

const root = ReactDOM.createRoot(document.getElementById('root'));

root.render(
  <React.StrictMode>
    <RecoilRoot>
      <BrowserRouter>
        <ScrollToTop />
        <App />
      </BrowserRouter>
    </RecoilRoot>
  </React.StrictMode>,
);

reportWebVitals();
