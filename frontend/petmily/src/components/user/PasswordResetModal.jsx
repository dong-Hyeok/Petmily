import { useCallback, useRef, useState } from 'react';
import { CircularProgress } from '@mui/material';
import { func } from 'prop-types';

import swal from 'sweetalert';
import useFetch from 'utils/fetch';

function PasswordResetModal({ onClose }) {
  const emailRef = useRef(null);
  const [validEmail, setValidEmail] = useState('');
  const [validCode, setValidCode] = useState('');

  const [visibleValidEmailError, setVisibleEmailError] = useState(false);
  const [visibleValidCodeError, setVisibleValidCodeError] = useState(false);
  const [confirmation, setConfirmation] = useState({
    email: false,
    code: false,
  });
  const [isLoading, setIsLoading] = useState({
    email: false,
    code: false,
    reset: false,
  });
  const fetchPasswordReset = useFetch();

  const onChangeVailidEamil = useCallback(e => {
    setVisibleEmailError(false);
    setValidEmail(e.target.value.trim());
  }, []);
  const onChageValidCode = useCallback(e => {
    setVisibleValidCodeError(false);
    setValidCode(e.target.value.trim());
  }, []);

  const handleValidationEmail = useCallback(async () => {
    if (
      !/^(([^<>()\\[\]\\.,;:\s@\\"]+(\.[^<>()\\[\]\\.,;:\s@\\"]+)*)|(\\".+\\"))@(([^<>()[\]\\.,;:\s@\\"]+\.)+[^<>()[\]\\.,;:\s@\\"]{2,})$/.test(
        validEmail,
      )
    ) {
      setVisibleEmailError(true);
      return;
    }
    try {
      setIsLoading({ ...isLoading, email: true });
      const response = await fetchPasswordReset.post('/resetpassword/email', {
        userEmail: validEmail,
      });
      if (response) {
        setConfirmation({ ...confirmation, email: true });
        setIsLoading({ ...isLoading, email: false });
      }
    } catch (error) {
      setVisibleEmailError(true);
      setIsLoading({ ...isLoading, email: false });
    }
  }, [confirmation, validEmail]);
  const handleValidationCode = useCallback(async () => {
    try {
      setIsLoading({ ...isLoading, code: true });
      const response = await fetchPasswordReset.post('/email/verification', {
        userEmail: validEmail,
        code: validCode,
      });
      if (response.status === 200) {
        setIsLoading({ ...isLoading, code: false });
        setConfirmation({ ...confirmation, code: true });
      }
    } catch (error) {
      setIsLoading({ ...isLoading, code: false });
      setVisibleValidCodeError(true);
    }
  }, [confirmation, validCode, validEmail]);

  const handleReset = useCallback(async () => {
    try {
      setIsLoading({ ...isLoading, reset: true });
      const response = await fetchPasswordReset.put('/resetpassword/reset', {
        userEmail: validEmail,
      });
      if (response.status === 200) {
        setIsLoading(false);
        setIsLoading({ ...isLoading, reset: false });
      }
    } catch (error) {
      setIsLoading({ ...isLoading, reset: false });
      setVisibleValidCodeError(true);
    }
    swal('비밀번호 초기화 완료');
    onClose();
  }, [confirmation, onClose, validEmail]);

  return (
    <div className="relative rounded-[10px] bg-white w-[656px] h-[450px] max-w-full flex flex-col justify-center items-center max-h-full text-left text-xl text-darkgray font-pretendard">
      <div className="absolute flex flex-col items-start justify-center gap-[23px]">
        <div className="relative tracking-[0.01em] leading-[125%] text-gray">
          가입한 이메일 주소를 입력해주세요.
        </div>
        <div className="flex-1 rounded-xl w-full flex flex-row items-center justify-end relative text-5xl text-white">
          <input
            className="w-full focus:outline-none self-stretch rounded-3xs bg-white flex flex-row py-5 px-5
            items-center justify-start text-black border-[1.5px] border-solid border-darkgray 
            focus:border-dodgerblue focus:border-1.5 font-pretendard text-xl 
            hover:brightness-95 focus:brightness-100"
            ref={emailRef}
            placeholder="이메일"
            onChange={onChangeVailidEamil}
          />
          <span
            role="presentation"
            onClick={handleValidationEmail}
            className="absolute bg-dodgerblue px-5 py-3 rounded-3xs my-0 mx-[!important] right-5 text-white tracking-[0.01em] leading-[125%] flex items-center justify-center w-[45.03px] h-[20.62px] shrink-0 z-[1] cursor-pointer"
          >
            {isLoading.email ? (
              <CircularProgress color="inherit" size={30} />
            ) : (
              '확인'
            )}
          </span>
        </div>
        {visibleValidEmailError ? (
          <span className="text-red-500 text-base w-full text-red">
            {' '}
            유효한 이메일 주소를 입력해주세요.
          </span>
        ) : null}
        <div className="flex-1 rounded-xl w-full flex flex-row items-center justify-end relative text-5xl text-white">
          <input
            className="focus:outline-none self-stretch rounded-3xs bg-white w-full flex flex-row py-5 px-5
            items-center justify-start text-black border-[1.5px] border-solid border-darkgray 
            focus:border-dodgerblue focus:border-1.5 font-pretendard text-xl 
            hover:brightness-95 focus:brightness-100"
            // ref={loginEmail}
            placeholder="인증 코드"
            onChange={onChageValidCode}
          />
          <span
            role="presentation"
            className="absolute bg-dodgerblue px-5 py-3 rounded-3xs my-0 mx-[!important] right-5 text-white tracking-[0.01em] leading-[125%] flex items-center justify-center w-[45.03px] h-[20.62px] shrink-0 z-[0] cursor-pointer"
            onClick={handleValidationCode}
          >
            {isLoading.code ? (
              <CircularProgress color="inherit" size={30} />
            ) : (
              '인증'
            )}
          </span>
        </div>
        {visibleValidCodeError ? (
          <span className="text-red-500 text-base w-full">
            인증코드가 맞지 않습니다.
          </span>
        ) : null}
        <button
          type="button"
          className={`self-stretch rounded-31xl ${
            !(confirmation.code && confirmation.email)
              ? 'bg-darkgray'
              : 'bg-dodgerblue hover:brightness-110'
          } overflow-hidden flex flex-row py-[21px] px-[172px] items-center justify-center text-5xl text-white cursor-pointer`}
          onClick={handleReset}
          disabled={!(confirmation.code && confirmation.email)}
        >
          {isLoading.reset ? (
            <CircularProgress
              color="inherit"
              size={30}
              className="px-[4.6rem]"
            />
          ) : (
            <b
              type="button"
              className="relative tracking-[0.01em] leading-[125%]"
            >
              비밀번호 재설정
            </b>
          )}
        </button>
      </div>
    </div>
  );
}

PasswordResetModal.propTypes = {
  onClose: func,
};

export default PasswordResetModal;
