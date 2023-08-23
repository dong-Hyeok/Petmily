import kakaoLoginButton from 'static/images/kakaoLoginButton.png';
import { kakaoURL } from 'utils/utils';

function LoginKakao() {
  const handleKakaoLogin = () => {
    window.location.href = kakaoURL;
  };

  return (
    <button
      type="button"
      aria-label="카카오 로그인"
      className="cursor-pointer bg-transparent h-[4rem] w-[4rem] flex justify-center items-center p-0"
      onClick={handleKakaoLogin}
    >
      <img
        alt="카카오 로그인"
        src={kakaoLoginButton}
        className="h-full w-full p-0 m-0 bg-kakao border-[1px] border-solid border-[#f7de27] rounded-full"
      />
    </button>
  );
}

export default LoginKakao;
