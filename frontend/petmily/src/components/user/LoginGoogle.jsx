import { useGoogleLogin } from '@react-oauth/google';
import axios from 'axios';
import googleLoginButtonImage from 'static/images/googleLoginButton.png';

function LoginGoogle() {
  const responseGoogle = async response => {
    try {
      await axios.post(
        '/login/google',
        {},
        {
          headers: {
            Authorization: `Bearer ${response.access_token}`,
            'Content-type': 'application/x-www-form-urlencoded;charset=utf-8',
          },
        },
      );
    } catch (error) {
      throw new Error(error);
    }
  };

  const googleLoginButton = useGoogleLogin({
    onSuccess: responseGoogle,
  });

  return (
    <div
      role="presentation"
      className="flex cursor-pointer"
      onClick={() => googleLoginButton()}
    >
      <img
        src={googleLoginButtonImage}
        alt="구글로그인버튼"
        className="h-[4rem] w-[4rem] border-[1px] bg-white border-solid border-[#dadce0] rounded-full"
      />
    </div>
  );
}

export default LoginGoogle;
