import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSetRecoilState } from 'recoil';
import { CircularProgress } from '@mui/material';
import useFetch from 'utils/fetch';
import authAtom from 'states/auth';
import userAtom from 'states/users';

function LoginKakaoCallback() {
  const navigation = useNavigate();
  const setUsers = useSetRecoilState(userAtom);
  const setAuth = useSetRecoilState(authAtom);

  const fetchKakao = useFetch();

  useEffect(() => {
    const params = new URL(document.location.toString()).searchParams;
    const code = params.get('code');

    const sendCodeToBackend = async () => {
      try {
        const response = await fetchKakao.get(`/oauth/kakao?code=${code}`);
        const { accessToken } = response.data;
        const {
          userEmail,
          userNickname,
          userLikePet,
          userProfileImg,
          userToken,
          userPoint,
          userBadge,
          userRing,
          userBackground,
        } = response.data.userLoginInfoDto;
        setAuth({ accessToken, userToken });
        setUsers({
          userEmail,
          userNickname,
          userLikePet,
          userProfileImg,
          accessToken,
          userPoint,
          userBadge,
          userRing,
          userBackground,
        });
        if (userNickname !== null) {
          navigation('/');
        } else {
          navigation('/userinfo');
        }
      } catch (error) {
        throw new Error(error);
      }
    };

    sendCodeToBackend();
  }, [navigation]);
  return (
    <div className="flex w-full h-full flex-col gap-10 justify-center items-center text-darkgray">
      <div>로그인 중입니다...</div>
      <CircularProgress color="inherit" size={70} />
    </div>
  );
}

export default LoginKakaoCallback;
