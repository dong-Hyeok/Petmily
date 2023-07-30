import { GoogleOAuthProvider } from '@react-oauth/google';
import {
  Header,
  GoogleLoginPage as LoginGoogle,
  KakaoCallback as LoginKakaoCallBack,
  LoginNaverCallback,
} from 'components';
import {
  Curation,
  CurationCategory,
  CurationPet,
  Join,
  Login,
  MyPage,
  PetInfo,
  Product,
  Social,
  UserInfo,
} from 'pages';
import { useEffect, useState } from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';

function App() {
  const [isLoggedIn, setisLoggedIn] = useState(false);
  const handleRoute = () => {
    // 로그인이 되었을 때
    setisLoggedIn(true);
    // 안되었을 때
  };
  useEffect(() => {
    return () => {
      handleRoute();
    };
  }, [isLoggedIn]);

  return (
    <div className="App">
      <GoogleOAuthProvider clientId={process.env.REACT_APP_GOOGLE_API_TOKEN}>
        <BrowserRouter>
          <Routes>
            <Route element={<Header />}>
              <Route path="*" element={<Curation />} />
              <Route path="curation" element={<Curation />} />
              <Route path="product" element={<Product />} />
              <Route path="social" element={<Social />} />
              <Route path="mypage" element={<MyPage />} />
              <Route path="/pet/*" element={<CurationPet />} />
              <Route path="/category/*" element={<CurationCategory />} />
            </Route>
            <Route path="/join" element={<Join />} />
            <Route path="/userinfo" element={<UserInfo />} />
            <Route path="/petinfo" element={<PetInfo />} />
            <Route path="/login" element={<Login />} component={LoginGoogle} />
            <Route
              path="login/oauth2/code/kakao"
              element={<LoginKakaoCallBack />}
            />
            <Route
              path="login/oauth2/code/naver"
              element={<LoginNaverCallback />}
            />
          </Routes>
        </BrowserRouter>
      </GoogleOAuthProvider>
    </div>
  );
}

export default App;
