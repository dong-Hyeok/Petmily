import { useEffect } from 'react';

import naverLoginButtonImage from 'static/images/naverLoginButton.png';

const { naver } = window;

function LoginNaver() {
  const initializeNaverLogin = () => {
    const naverLogin = new naver.LoginWithNaverId({
      clientId: `${process.env.REACT_APP_NAVER_CLIENT_ID}`,
      callbackUrl: `${process.env.REACT_APP_NAVER_CALLBACK_URL}`,
      clientSecret: `${process.env.REACT_APP_NAVER_STATE_STRING}`,
      isPopup: true,
      loginButton: {
        color: 'green',
        type: 1,
        height: '60',
      },
      callbackHandle: 'true',
    });
    naverLogin.init();
  };

  const handleCustomNaverLogin = () => {
    const naverLoginButton = document.querySelector('#naverIdLogin > a');
    if (naverLoginButton) {
      naverLoginButton.click();
    }
  };

  useEffect(() => {
    initializeNaverLogin();
  }, []);

  return (
    <div>
      <div
        role="presentation"
        className="cursor-pointer"
        onClick={handleCustomNaverLogin}
      >
        <img
          src={naverLoginButtonImage}
          alt="네이버로그인버튼"
          className="h-[4rem] w-[4rem] border-[1px] border-solid border-naver rounded-full"
        />
      </div>
      <div id="naverIdLogin" className="hidden" />
    </div>
  );
}

export default LoginNaver;
