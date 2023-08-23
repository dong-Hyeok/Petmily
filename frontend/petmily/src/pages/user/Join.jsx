import { useCallback, useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import ArrowDropDownOutlinedIcon from '@mui/icons-material/ArrowDropDownOutlined';
import ClearRoundedIcon from '@mui/icons-material/ClearRounded';
import { CircularProgress, styled } from '@mui/material';
import swal from 'sweetalert';
import { func, string } from 'prop-types';
import CONSTANTS from 'utils/constants';
import { isSameCheck, validateEmail, validatePassword } from 'utils/utils';
import useFetch from 'utils/fetch';

function EmailSelect({ addr, onChange }) {
  const StyledArrowDropDownOutlinedIcon = styled(ArrowDropDownOutlinedIcon, {
    name: 'StyledArrowDropDownOutlinedIcon',
    slot: 'Wrapper',
  })({
    color: '#a6a7ab',
    fontSize: 40,
    '&:hover': { color: '#1f90fe' },
  });
  return (
    <>
      <select
        className="appearance-none w-full h-[106%] px-4 pr-8 text-base font-pretendard-medium rounded-lg 
    focus:outline-none border-solid border-[1.5px] border-darkgray focus:border-dodgerblue focus:border-1.5"
        value={addr}
        onChange={onChange}
      >
        {CONSTANTS.SLELECT.ADDRESS.map((addressElement, idx) => (
          <option
            value={idx === 0 ? 'none' : addressElement}
            disabled={idx === 0}
            selected={idx === 0}
          >
            {addressElement}
          </option>
        ))}
      </select>
      <div className="absolute inset-y-0 right-0 flex items-center pr-2 pointer-events-none">
        <StyledArrowDropDownOutlinedIcon className="w-5 h-5 text-gray-400" />
      </div>
    </>
  );
}
function EmailInput({ value, onChange, setIsInput }) {
  return (
    <>
      <input
        type="text"
        className="focus:outline-none w-full h-full px-4
      rounded-3xs border-solid border-[1.5px] border-darkgray 
      focus:border-dodgerblue focus:border-1.5 font-pretendard text-base"
        placeholder={CONSTANTS.SLELECT.INPUT}
        value={value === CONSTANTS.SLELECT.ADDRESS.at(-1) ? null : value}
        onChange={onChange}
        required
      />
      <ClearRoundedIcon
        className="absolute right-3"
        onClick={() => {
          setIsInput(false);
        }}
      />
    </>
  );
}

function Join() {
  const navigate = useNavigate();
  const fetchJoin = useFetch();
  const [isLoading, setIsLoading] = useState({
    validateEmail: false,
    join: false,
  });
  const [selectedSuffix, setSelectedSuffix] = useState('');
  const [selectedAddr, setSelectedAddr] = useState('');
  const [isInput, setIsInput] = useState(false);
  const [password, setPassword] = useState('');
  const [auth, setAuth] = useState({ email: false, code: false });
  const [verifyCode, setVerifyCode] = useState('');
  const [checkPassword, setCheckPassword] = useState('');
  const [visibleError, setVisibleError] = useState({
    email: false,
    password: false,
    checkPassword: false,
    code: false,
  });
  useState(false);
  const emailInput = useRef(null);
  const passwordInput = useRef(null);
  const checkPasswordInput = useRef(null);
  const verifyRef = useRef(null);
  const authEmailButtonRef = useRef(null);
  const verifyCodeButton = useRef(null);

  const checkForm = () => {
    if (selectedAddr && selectedSuffix && password && checkPassword) {
      return true;
    }
    return false;
  };

  const handleJoin = async (email, inputPassword, inputCheckPassword) => {
    setIsLoading({ ...isLoading, join: true });
    try {
      if (validateEmail(email)) {
        throw new Error(String(validateEmail(email)));
      }
      if (validatePassword(inputCheckPassword)) {
        throw new Error(String(validatePassword(inputCheckPassword)));
      }
      if (isSameCheck(inputPassword, inputCheckPassword)) {
        throw new Error(String(isSameCheck(inputPassword, inputCheckPassword)));
      }
      await fetchJoin.post(
        '/signup',
        {
          userEmail: email,
          userPw: password,
        },
        '/join',
      );
      setIsLoading({ ...isLoading, join: false });
      swal(CONSTANTS.COMPLETE.JOIN);
      navigate('/login');
    } catch (error) {
      setIsLoading({ ...isLoading, join: false });
      if (error.message === CONSTANTS.VALIDATION.EMAIL) {
        setVisibleError({ ...visibleError, email: true });
        emailInput.current.focus();
      }
      if (error.message === CONSTANTS.VALIDATION.PASSWORD) {
        setVisibleError({ ...visibleError, password: true });
        passwordInput.current.focus();
      }
      if (error.message === CONSTANTS.VALIDATION.CHECK_PASSWORD) {
        setVisibleError({ ...visibleError, checkPassword: true });
        checkPasswordInput.current.focus();
      }
    }
  };
  const handleValidationCode = useCallback(async () => {
    try {
      const response = await fetchJoin.post(
        '/email/verification',
        {
          userEmail: `${selectedAddr}@${selectedSuffix}`,
          code: verifyCode,
        },
        '/join',
      );

      setIsLoading({ ...isLoading, validateEmail: false });

      if (response.status === 200) {
        verifyRef.current.disabled = true;
        authEmailButtonRef.current.disabled = true;
        verifyCodeButton.current.disabled = true;
        swal(CONSTANTS.COMPLETE.AUTHENTICATION);
      }
      setAuth({ ...auth, code: true });
    } catch (error) {
      setVisibleError({ ...visibleError, code: true });
      setIsLoading({ ...isLoading, validateEmail: false });
    }
  }, [auth, selectedAddr, selectedSuffix, verifyCode, visibleError]);

  const onChageValidCode = useCallback(
    e => {
      setVerifyCode(e.target.value.trim());
      setVisibleError({ ...visibleError, code: false });
    },
    [visibleError],
  );

  const handleEmailAuth = async email => {
    setIsLoading({ ...isLoading, validateEmail: true });
    try {
      const emailValidation = validateEmail(email);
      if (emailValidation.length > 0) throw new Error(emailValidation);
      const url = '/signup/email';
      const data = {
        userEmail: email,
      };

      await fetchJoin.post(url, data, 'join');
      setIsLoading({ ...isLoading, validateEmail: false });
      emailInput.current.disabled = true;
      setAuth({ ...auth, email: true });
    } catch (error) {
      const errorResponse = error.response;
      if (errorResponse.status === 401) {
        setVisibleError({ ...visibleError, email: true });
      }
      setIsLoading({ ...isLoading, validateEmail: false });
    }
  };

  return (
    <div className="joinComponent">
      <div className="absolute top-[15%] left-[calc(50%_-_324px)] rounded-[20px] bg-white flex flex-col h-fit px-10 box-border items-center justify-center gap-[42px]">
        <b className="self-stretch relative text-13xl tracking-[0.01em] leading-[125%]">
          {CONSTANTS.HEADER.JOIN}
        </b>
        <div className="flex flex-col items-start justify-start gap-[18px] text-darkgray">
          <b className="relative tracking-[0.01em] leading-[125%] text-gray">
            {CONSTANTS.STRINGS.EMAIL}
          </b>
          <div className="w-[567px] flex flex-row items-center justify-start gap-[13px] text-xl">
            <div className="flex-1 bg-white flex flex-row items-center justify-center">
              <input
                type="text"
                className={`focus:outline-none w-full h-full py-[21px] px-4 
                rounded-3xs border-solid border-[1.5px] border-darkgray 
                focus:border-dodgerblue focus:border-1.5 font-pretendard 
                text-base ${
                  auth.email
                    ? 'brightness-95'
                    : 'hover:brightness-95 focus:brightness-100'
                }`}
                placeholder={CONSTANTS.STRINGS.EMAIL}
                ref={emailInput}
                onChange={e => {
                  setVisibleError({ ...visibleError, email: false });
                  setSelectedAddr(e.target.value);
                }}
              />
            </div>
            <b className="relative text-13xl tracking-[0.01em] leading-[125%] text-darkgray">
              @
            </b>
            <div className="flex-1 rounded-3xs flex flex-col items-start justify-start relative gap-[10px]">
              <div
                className="relative rounded-3xs bg-white box-border w-[254px] 
              h-[60.07px] z-[0]  border-darkgray focus:outline-none"
              />
              <div
                className="my-0 mx-[!important] w-full h-full absolute flex 
              flex-row items-center justify-center z-[1] hover:brightness-95 focus:brightness-100"
              >
                {isInput ? (
                  <EmailInput
                    value={selectedSuffix}
                    onChange={e => setSelectedSuffix(e.target.value)}
                    setIsInput={setIsInput}
                  />
                ) : (
                  <EmailSelect
                    value={selectedAddr}
                    onChange={e => {
                      setSelectedSuffix(e.target.value);
                      if (e.target.value === CONSTANTS.SLELECT.ADDRESS.at(-1)) {
                        setIsInput(true);
                      }
                    }}
                  />
                )}
              </div>
            </div>
          </div>
          {auth.email ? (
            <div className="flex-1 rounded-xl w-full flex flex-row items-center justify-end relative text-5xl text-white">
              <input
                className="focus:outline-none self-stretch rounded-3xs bg-white w-full flex flex-row py-5 px-5
              items-center justify-start text-black border-[1.5px] border-solid border-darkgray 
              focus:border-dodgerblue focus:border-1.5 font-pretendard text-xl 
              hover:brightness-95 focus:brightness-100"
                placeholder={CONSTANTS.INPUTS.AUTH_CODE}
                onChange={onChageValidCode}
                ref={verifyRef}
                value={verifyCode}
              />
              <button
                type="button"
                className={`absolute px-10 py-5 whitespace-nowrap rounded-3xs my-0 mx-[!important] right-5 text-white tracking-[0.01em] leading-[125%] flex items-center justify-center w-[45.03px] h-[20.62px] shrink-0 z-[0] cursor-pointer ${
                  verifyCode ? 'bg-dodgerblue' : 'bg-darkgray'
                }`}
                onClick={handleValidationCode}
                ref={verifyCodeButton}
                disabled={auth.code}
              >
                <b className="relative tracking-[0.01em] leading-[125%] text-white text-xl">
                  {CONSTANTS.BUTTONS.AUTH}
                </b>
              </button>
            </div>
          ) : null}
          {visibleError.code ? (
            <span className="text-red">{CONSTANTS.VALIDATION.AUTH_CODE}</span>
          ) : null}

          <button
            type="button"
            ref={authEmailButtonRef}
            className={`self-stretch rounded-3xs ${
              selectedAddr && selectedSuffix
                ? 'bg-dodgerblue text-white'
                : 'bg-white'
            }
            overflow-hidden flex flex-row py-[21px] px-[199px] 
            items-center justify-center border-[1.5px] border-solid border-darkgray hover:brightness-90 cursor-pointer`}
            onClick={() => {
              handleEmailAuth(`${selectedAddr}@${selectedSuffix}`);
            }}
          >
            {isLoading.validateEmail ? (
              <CircularProgress color="inherit" size={26} />
            ) : (
              <b
                className={`relative tracking-[0.01em] leading-[125%] ${
                  selectedAddr && selectedSuffix
                    ? 'text-white'
                    : 'text-darkgray'
                } text-xl`}
              >
                {CONSTANTS.BUTTONS.AUTH_EMAIL}
              </b>
            )}
          </button>
          {visibleError.email ||
          `${selectedAddr}${setSelectedSuffix}`.length <= 0 ? (
            <span className="text-red">{CONSTANTS.VALIDATION.EMAIL}</span>
          ) : null}
        </div>
        <div className="w-[564px] flex flex-col items-start justify-start gap-[11px] text-xl">
          <b className="relative text-5xl tracking-[0.01em] leading-[125%]">
            {CONSTANTS.STRINGS.PASSWORD}
          </b>
          <div className="relative tracking-[0.01em] leading-[125%] font-medium text-lightslategray">
            {CONSTANTS.VALIDATION.PASSWORD}
          </div>
          <input
            type="password"
            className="focus:outline-none self-stretch rounded-3xs bg-white flex flex-row py-5 px-4 
          items-center justify-start text-black border-[1.5px] border-solid border-darkgray 
          focus:border-dodgerblue focus:border-1.5 font-pretendard text-base 
          hover:brightness-95 focus:brightness-100"
            ref={passwordInput}
            placeholder={CONSTANTS.STRINGS.PASSWORD}
            onChange={e => {
              setVisibleError({ ...visibleError, password: false });
              setPassword(e.target.value);
            }}
          />
          {visibleError.password ? (
            <span className="text-red">{CONSTANTS.VALIDATION.PASSWORD}</span>
          ) : null}
        </div>
        <div className="w-[564px] flex flex-col items-start justify-start gap-[12px]">
          <b className="relative tracking-[0.01em] leading-[125%]">
            {CONSTANTS.STRINGS.CHECK_PASSWORD}
          </b>
          <input
            type="password"
            className="focus:outline-none self-stretch rounded-3xs bg-white flex flex-row py-5 px-4 
          items-center justify-start text-black border-[1.5px] border-solid border-darkgray 
          focus:border-dodgerblue focus:border-1.5 font-pretendard text-base 
          hover:brightness-95 focus:brightness-100"
            placeholder={CONSTANTS.STRINGS.CHECK_PASSWORD}
            ref={checkPasswordInput}
            onChange={e => {
              setCheckPassword(e.target.value);
              setVisibleError({ ...visibleError, checkPassword: false });
            }}
          />
          {visibleError.checkPassword ? (
            <span className="text-red">
              {CONSTANTS.VALIDATION.CHECK_PASSWORD}
            </span>
          ) : null}
        </div>

        <button
          type="submit"
          className={`rounded-[50px] ${
            checkForm() && auth.code ? 'bg-dodgerblue' : 'bg-darkgray'
          } w-full h-[80px] flex flex-row py-[40px] px-[216px] items-center justify-center text-white hover:brightness-95 cursor-pointer`}
          onClick={() => {
            handleJoin(
              `${selectedAddr}@${selectedSuffix}`,
              password,
              checkPassword,
            );
          }}
          disabled={!(checkForm() && auth.code)}
        >
          {isLoading.join ? (
            <CircularProgress color="inherit" size={26} />
          ) : (
            <b className="relative tracking-[0.01em] leading-[125%] text-xl whitespace-nowrap">
              {CONSTANTS.BUTTONS.JOIN}
            </b>
          )}
        </button>
        <div className="self-stretch flex flex-row items-start justify-center gap-[10px] text-xl">
          <div className="relative tracking-[0.01em] leading-[125%]">
            이미 아이디가 있으신가요?
          </div>
          <Link
            to="/login"
            className="relative [text-decoration:underline] tracking-[0.01em] leading-[125%] font-bold text-dodgerblue hover:brightness-125"
          >
            {CONSTANTS.HEADER.LOGIN}
          </Link>
          <div className="h-20" />
        </div>
      </div>
    </div>
  );
}

EmailSelect.propTypes = {
  addr: string,
  onChange: func,
};
EmailInput.propTypes = {
  value: string,
  onChange: func,
  setIsInput: func,
};
export default Join;
