import { useEffect } from 'react';

import axios from 'axios';

function LoginNaverCallback() {
  const url = new URL(window.location.href).href;

  const params = new URLSearchParams(url.split('#')[1]);
  const code = params.get('access_token');

  useEffect(() => {
    const naver = async () => {
      try {
        await axios.post(
          '/login/naver',
          {},
          {
            headers: {
              Authorization: `Bearer ${code}}`,
              'Content-type': 'application/x-www-form-urlencoded;charset=utf-8',
            },
          },
        );
      } catch (error) {
        throw new Error(error);
      }
    };
    if (code) {
      naver();
    }
  });
  return <div>NaverCallback</div>;
}

export default LoginNaverCallback;
