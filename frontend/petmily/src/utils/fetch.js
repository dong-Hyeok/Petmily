import { useRecoilState } from 'recoil';
import axios from 'axios';
import authAtom from 'states/auth';
import userAtom from 'states/users';

function useFetch() {
  const [auth, setAuth] = useRecoilState(authAtom);
  const [loginState, setLoginState] = useRecoilState(userAtom);
  function authHeader() {
    const token = auth?.accessToken;
    const isLoggedIn = !!token;
    if (isLoggedIn) {
      return { Authorization: `Bearer ${token}` };
    }
    return {};
  }

  async function handleResponse(response) {
    const { data } = response;

    if (response.status !== 200) {
      if (response.status === 401 || response.status === 403) {
        try {
          const newAccessTokenResponse = await axios.post(
            '/refreshAccessToken',
            { userEmail: loginState.userEmail, refreshToken: auth.userToken },
          );
          const newAccessToken = newAccessTokenResponse.data;
          setAuth(prevAuth => ({ ...prevAuth, accessToken: newAccessToken }));
          const newConfig = {
            ...response.config,
            headers: {
              ...response.config.headers,
              Authorization: `Bearer ${newAccessToken}`,
            },
          };
          try {
            const originalResponse = await axios.request(newConfig);
            setAuth({ ...auth, accessToken: newAccessToken });
            return originalResponse.data;
          } catch (error) {
            return Promise.reject(error);
          }
        } catch (error) {
          setAuth(null);
          setLoginState(null);

          return Promise.reject(error);
        }
      }
      if (response.status === 500) {
        return Promise.reject(response);
      }

      setAuth(null);
      setLoginState(null);
      const error = (data && data.message) || response.message;
      return Promise.reject(error);
    }
    return data || { status: 200 };
  }

  function request(method) {
    return async (url, body, page) => {
      const requestOptions = {
        method,
        headers: authHeader(url),
      };
      if (body) {
        requestOptions.headers['Content-Type'] =
          page === 'image' ? 'multipart/form-data' : 'application/json';
        requestOptions.data = body;
      } else {
        requestOptions.headers['Content-Type'] = 'application/json';
      }

      try {
        const response = await axios({
          url: `/api${url}`,
          method,
          headers: requestOptions.headers,
          data: body,
          timeout: 5000,
        });
        return handleResponse(response);
      } catch (error) {
        if (error.response.status === 403 || error.response.status === 401) {
          return handleResponse(error.response);
        }
        setAuth(null);
        setLoginState(null);
        return Promise.reject(error);
      }
    };
  }

  return {
    get: request('GET'),
    post: request('POST'),
    patch: request('PATCH'),
    put: request('PUT'),
    delete: request('DELETE'),
  };
}

export default useFetch;
