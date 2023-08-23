import { useEffect, useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';

import swal from 'sweetalert';
import { string } from 'prop-types';
import { useRecoilState, useRecoilValue } from 'recoil';
import { styled } from '@mui/material';
import CloseRoundedIcon from '@mui/icons-material/CloseRounded';

import userAtom from 'states/users';
import authAtom from 'states/auth';
import createImageAtom from 'states/createimage';
import useFetch from 'utils/fetch';

import { UploadImage } from 'components';
import logo from 'static/images/logo.svg';
import CONSTANTS from 'utils/constants';
import { profiles, validatePassword } from 'utils/utils';

function UserInfo({ page }) {
  const navigate = useNavigate();
  const fetchData = useFetch();

  const [userName, setUserName] = useState('');
  const [userLike, setUserLike] = useState('');
  const [visibleUserNameError, setVisibleUserNameError] = useState(false);
  const [userNameError, setUserNameError] = useState('');
  const [userNameSuccess, setUserNameSuccess] = useState(false);
  const [isButtonDisabled, setIsButtonDisabled] = useState(true);
  const [trySubmit, setTrySubmit] = useState(0);

  const oldPasswordInput = useRef(null);
  const passwordInput = useRef(null);
  const [oldPassword, setOldPassword] = useState('');
  const [password, setPassword] = useState('');

  const auth = useRecoilValue(authAtom);
  const [userLogin, setUser] = useRecoilState(userAtom);
  const [uploadedImage, setUploadedImage] = useRecoilState(createImageAtom);
  const [isChangePassword, setIsChangePassword] = useState(false);

  function passwordChange() {
    const StyledCloseRoundedIcon = styled(CloseRoundedIcon, {
      name: 'StyledCloseRoundedIcon',
      slot: 'Wrapper',
    })({
      color: '#A6A7AB',
      fontSize: 30,
      '&:hover': { color: 'red' },
    });
    return isChangePassword ? (
      <div className="w-[36rem] flex flex-col items-start justify-start gap-[1rem]">
        <div className="w-full flex flex-row justify-between">
          <b className="relative text-[1.4rem]">비밀번호 변경</b>
          <StyledCloseRoundedIcon
            className="relative cursor-pointer"
            role="presentation"
            onClick={() => {
              setIsChangePassword(false);
            }}
          />
        </div>
        <b className="relative flex text-slategray items-center shrink-0">
          기존 비밀번호와 새 비밀번호를 입력해주세요.
        </b>
        <div className="relative self-stretch flex flex-row items-center justify-center gap-[1rem] text-darkgray">
          <input
            className="flex-1 rounded-3xs box-border h-[3rem] flex flex-row px-[1rem] items-center justify-start border-[1px] border-solid border-darkgray focus:outline-none w-full 
      focus:border-dodgerblue focus:border-1.5 font-pretendard text-base"
            type="password"
            ref={oldPasswordInput}
            placeholder="기존 비밀번호"
            onChange={e => {
              setOldPassword(e.target.value);
            }}
          />
        </div>
        <div className="relative self-stretch flex flex-row items-center justify-center gap-[1rem] text-darkgray">
          <input
            className="flex-1 rounded-3xs box-border h-[3rem] flex flex-row px-[1rem] items-center justify-start border-[1px] border-solid border-darkgray focus:outline-none w-full 
      focus:border-dodgerblue focus:border-1.5 font-pretendard text-base"
            type="password"
            ref={passwordInput}
            placeholder={CONSTANTS.STRINGS.PASSWORD}
            onChange={e => {
              setPassword(e.target.value);
            }}
          />
        </div>
      </div>
    ) : (
      <div className="w-[36rem] h-[4.5rem] cursor-pointer mt-5">
        <button
          type="submit"
          className="bg-white border-solid border-2 border-dodgerblue rounded-full w-full h-[4.5rem] hover:brightness-95 cursor-pointer"
          onClick={() => {
            setIsChangePassword(true);
          }}
        >
          <b className="flex justify-center items-center text-[1.5rem] text-dodgerblue cursor-pointer">
            비밀번호 변경
          </b>
        </button>
      </div>
    );
  }

  const checkForm = () => {
    return userName && userLike && userNameSuccess;
  };

  const onChangeUserName = e => {
    const newUserName = e.target.value;
    if (newUserName.length > 6) {
      setUserNameError('닉네임은 6글자 이하로 입력해주세요.');
    } else {
      setUserName(newUserName);
      setUserNameError('');
    }
    setIsButtonDisabled(newUserName.length < 1 || newUserName.length > 6);
    setVisibleUserNameError(true);
    setUserNameSuccess(false);
  };

  const handleDoubleCheck = async (currentUserName, e) => {
    e.preventDefault();

    const sendBE = {
      userNickname: currentUserName,
    };
    try {
      fetchData.post('/nickname/check', sendBE);
      setUserNameSuccess(true);
    } catch (error) {
      setUserNameError('중복된 닉네임입니다.');
      setVisibleUserNameError(true);
      throw new Error(error);
    }
  };

  const onChangeUserLike = e => {
    setUserLike(e.target.value);
  };

  const handleUserInfo = async (
    currentUserImage,
    currentUserName,
    currentUserLike,
    e,
  ) => {
    e.preventDefault();

    if (trySubmit !== 1 && uploadedImage?.length === 0) {
      swal('프로필 이미지를 선택해주세요!');
      setTrySubmit(1);
      return;
    }
    if (password.length > 0) {
      const message = validatePassword(password);
      if (message.length > 0) {
        swal(message);
        return;
      }
    }

    const userInfoEditDto = {
      userEmail: userLogin.userEmail,
      userNickname: currentUserName,
      userLikePet: currentUserLike,
    };

    const formData = new FormData();

    formData.append(
      'userInfoEditDto',
      new Blob([JSON.stringify(userInfoEditDto)], {
        type: 'application/json',
      }),
    );

    formData.append('file', currentUserImage);

    try {
      const response = await fetchData.post('/mypage/edit', formData, 'image');
      setUser({
        ...userLogin,
        userNickname: currentUserName,
        userLikePet: currentUserLike,
        userProfileImg: response.imageUrl ? response.imageUrl : profiles,
      });
      if (page && isChangePassword) {
        await fetchData.put(`/changepassword`, {
          userEmail: userLogin.userEmail,
          oldPassword,
          newPassword: password,
        });
      }
      if (page) {
        navigate('/mypage');
        swal(`사용자 정보 수정에 성공하였습니다.`);
      } else {
        navigate('/');
        swal(`사용자 정보 등록에 성공하였습니다.`);
      }
    } catch (error) {
      throw new Error(error);
    }
  };

  useEffect(() => {
    if (page && userLogin) {
      setUserName(userLogin.userNickname);
      setUserLike(userLogin.userLikePet);
      setUploadedImage([]);
    }
  }, [page, userLogin]);

  useEffect(() => {
    if (!auth || !Object.keys(auth).length) {
      setUser(null);
      navigate('/login');
    }
  }, []);

  return (
    <div
      className={`${
        page ? 'rounded-lg max-h-screen h-[100vh]' : 'h-[100vh]'
      } flex justify-center items-center bg-white w-full touch-none text-left text-[1rem] text-gray font-pretendard`}
    >
      <div
        className={`${
          page
            ? 'rounded-lg max-h-fit min-h-[500px] top-24 py-10'
            : 'top-0 py-[3rem]'
        } relative flex flex-col box-border items-center justify-center bg-white w-screen gap-[2rem]`}
      >
        {page ? null : (
          <div className="flex justify-center items-start w-[8rem] pb-3">
            <img className="w-[8rem]" alt="" src={logo} />
          </div>
        )}
        <b className="w-[36rem] text-[1.6rem]">
          {page ? '개인 정보 수정' : '개인 정보 설정'}
        </b>
        <UploadImage page="사용자정보" />

        <div className="w-[36rem] flex flex-col items-start justify-start gap-[1rem]">
          <b className="relative text-[1.4rem]">닉네임</b>
          <b className="relative flex text-slategray items-center shrink-0">
            사용할 닉네임을 입력해주세요
          </b>
          <div className="relative self-stretch flex flex-row items-center justify-center gap-[1rem] text-darkgray">
            <input
              className="flex-1 box-border h-[3rem] flex flex-row px-[1rem] items-center justify-start focus:outline-none w-full
              rounded-3xs border-solid border-[1px] border-darkgray 
              focus:border-dodgerblue focus:border-1.5 font-pretendard text-base"
              type="text"
              placeholder="6글자 이하 닉네임을 사용할 수 있어요"
              onChange={e => {
                setVisibleUserNameError(false);
                onChangeUserName(e);
              }}
              value={userName}
            />
            <button
              type="button"
              onClick={e => {
                handleDoubleCheck(userName, e);
              }}
              className={`rounded-31xl text-white font-semibold bg-dodgerblue overflow-hidden flex flex-row py-3 px-4 items-center justify-center text-lg ${
                isButtonDisabled
                  ? 'opacity-50 cursor-not-allowed'
                  : 'cursor-pointer'
              }`}
              disabled={isButtonDisabled}
            >
              중복 확인
            </button>
          </div>
          <div>
            {userNameSuccess && (
              <span className="text-dodgerblue text-base w-full">
                사용할 수 있는 닉네임입니다.
              </span>
            )}
            {visibleUserNameError && (
              <span className="text-red text-base w-full">{userNameError}</span>
            )}
          </div>
        </div>

        <div className="w-[36rem] flex flex-col items-start justify-start gap-[1rem]">
          <b className="relative text-[1.4rem]">선호하는 반려동물</b>
          <b className="relative flex text-slategray items-center shrink-0">
            함께하고 싶은 반려동물이 있나요?
          </b>
          <div className="relative self-stretch flex flex-row items-center justify-center gap-[1rem] text-darkgray">
            <input
              className="flex-1 rounded-3xs box-border h-[3rem] flex flex-row px-[1rem] items-center justify-start border-[1px] border-solid border-darkgray focus:outline-none w-full 
              focus:border-dodgerblue focus:border-1.5 font-pretendard text-base"
              type="text"
              placeholder="ex) 고양이"
              onChange={e => {
                onChangeUserLike(e);
              }}
              value={userLike}
            />
          </div>
        </div>
        {!page ? null : passwordChange()}

        <div className="w-[36rem] h-[4.5rem] mt-10">
          <button
            type="submit"
            className={`${
              checkForm() ? ' bg-dodgerblue' : 'bg-darkgray'
            } rounded-full w-full h-[4.5rem] cursor-pointer`}
            onClick={e => {
              handleUserInfo(uploadedImage, userName, userLike, e);
            }}
            disabled={!checkForm()}
          >
            <b className="flex justify-center items-center text-[1.5rem] text-white">
              {page ? '수정 완료' : '작성 완료'}
            </b>
          </button>
        </div>
      </div>
    </div>
  );
}

UserInfo.propTypes = {
  page: string,
};

export default UserInfo;
