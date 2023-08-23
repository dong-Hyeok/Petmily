import { useCallback, useEffect, useState } from 'react';
import { Link, Outlet, useNavigate } from 'react-router-dom';
import { useRecoilState, useRecoilValue } from 'recoil';

import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import { styled } from '@mui/material';
import swal from 'sweetalert';

import headerLogo from 'static/images/headerLogo.svg';
import CONSTANTS from 'utils/constants';
import authAtom from 'states/auth';
import userAtom from 'states/users';
import headerAtom from 'states/headers';

import useFetch from 'utils/fetch';
import PortalPopup from 'components/commons/PortalPopup';
import CustomSelect from 'components/commons/CustomSelect';
import Alarm from 'components/header/Alarm';

function Header() {
  const StyledEventAvailableIcon = styled(EventAvailableIcon, {
    name: 'StyledEventAvailableIcon',
    slot: 'Wrapper',
  })({
    fontSize: 50,
    paddingTop: 5,
  });
  const navigate = useNavigate();
  const auth = useRecoilValue(authAtom);
  const user = useRecoilValue(userAtom);
  const [clickedHeader, setClickedHeader] = useRecoilState(headerAtom);
  const [alarmDot, setAlarmDot] = useState(false);
  const [showAlarmModal, setShowAlarmModal] = useState(false);
  const fetchAttendance = useFetch();

  const readAlarmCheck = async () => {
    if (!user || Object.keys(user).length <= 0) {
      return;
    }
    try {
      const response = await fetchAttendance.get(
        `/noti/checked/${user.userEmail}`,
      );
      const alarmCheck = response.status !== 200;
      setAlarmDot(alarmCheck);
    } catch (error) {
      throw new Error(error);
    }
  };

  const onAlarmClick = () => {
    if (alarmDot) {
      setAlarmDot(false);
    }
    setShowAlarmModal(!showAlarmModal);
  };

  const closeAlarmModal = async () => {
    try {
      await fetchAttendance.get(`/noti/status/${user.userEmail}`);
    } catch (error) {
      throw new Error(error);
    }
    setShowAlarmModal(false);
  };

  useEffect(() => {
    if (showAlarmModal) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'auto';
    }
    return () => {
      document.body.style.overflow = 'auto';
    };
  }, [showAlarmModal]);

  const handleAttendance = useCallback(async () => {
    try {
      const data = await fetchAttendance.put('/attendance', {
        userEmail: user.userEmail,
      });
      if (data === true) {
        swal('출석 완료');
      } else {
        swal('이미 출석을 완료했습니다.');
      }
    } catch (error) {
      throw new Error(error);
    }
  }, []);

  useEffect(() => {
    readAlarmCheck();
  }, []);

  return (
    <>
      <div className="fixed top-0 bg-slate-100 w-full h-2 z-50" />
      <div
        className={`fixed top-2 z-50 w-[98%] px-5 flex items-center justify-between rounded-[20px] bg-white ${
          clickedHeader === '마이페이지' || clickedHeader === '상점'
            ? 'min-w-[1400px]'
            : 'min-w-[1280px]'
        } max-w-full h-[80px] text-dodgerblue font-pretendard`}
      >
        <div
          className="flex items-center cursor-pointer"
          role="presentation"
          onClick={() => {
            navigate('/curation');
          }}
        >
          <img
            className="w-[180px] h-auto object-cover"
            alt=""
            src={headerLogo}
          />
        </div>
        <div className="flex items-center text-xl text-darkgray">
          <Link
            to="/product"
            className={`no-underline px-5 font-semibold whitespace-nowrap ${
              clickedHeader === CONSTANTS.HEADER.PRODUCT
                ? 'text-dodgerblue'
                : 'text-darkgray'
            } hover:text-dodgerblue`}
            onClick={() => {
              setClickedHeader(CONSTANTS.HEADER.PRODUCT);
            }}
          >
            {CONSTANTS.HEADER.PRODUCT}
          </Link>
          <Link
            to="/curation"
            className={`no-underline px-5 font-semibold whitespace-nowrap ${
              clickedHeader === CONSTANTS.HEADER.CURATION ||
              clickedHeader === ''
                ? 'text-dodgerblue'
                : 'text-darkgray'
            } hover:text-dodgerblue`}
            onClick={() => {
              setClickedHeader(CONSTANTS.HEADER.CURATION);
            }}
          >
            {CONSTANTS.HEADER.CURATION}
          </Link>
          <Link
            to="/social"
            className={`no-underline px-5 font-semibold whitespace-nowrap ${
              clickedHeader === CONSTANTS.HEADER.SOCIAL
                ? 'text-dodgerblue'
                : 'text-darkgray'
            } hover:text-dodgerblue`}
            onClick={() => {
              setClickedHeader(CONSTANTS.HEADER.SOCIAL);
            }}
          >
            {CONSTANTS.HEADER.SOCIAL}
          </Link>
        </div>
        {!auth ? (
          <div className="flex items-center justify-between text-lg text-black relative">
            <div className="rounded-full flex items-center justify-center">
              <Link
                to="/login"
                className="no-underline whitespace-nowrap text-black hover:text-dodgerblue"
              >
                {CONSTANTS.HEADER.LOGIN}
              </Link>
            </div>
            <span className="h-[1.4rem] w-[2px] mx-3 bg-darkgray" />
            <div className="rounded-full flex items-center justify-center text-gray">
              <Link
                to="/join"
                className="no-underline whitespace-nowrap text-black hover:text-dodgerblue"
              >
                {CONSTANTS.HEADER.JOIN}
              </Link>
            </div>
          </div>
        ) : (
          <div className="flex items-center justify-between text-lg text-black relative gap-5">
            <div className="w-16 h-16 hover:bg-[#3c4043] hover:bg-opacity-[0.08] rounded-full flex justify-center items-center">
              <StyledEventAvailableIcon
                onClick={handleAttendance}
                className="cursor-pointer z-50"
              />
            </div>
            <div
              role="presentation"
              onClick={onAlarmClick}
              className={`${
                user.userRing && 'border-solid border-[5px]'
              } relative rounded-full flex items-center justify-center`}
              style={{
                borderColor: `#${user.userRing ? user.userRing.itemColor : ''}`,
              }}
            >
              <img
                src={user.userProfileImg}
                className="w-12 h-12 rounded-full"
                alt=""
              />
              {alarmDot ? (
                <div className="absolute top-0 -right-1 w-4 h-4 rounded-full bg-red" />
              ) : null}
            </div>
            <CustomSelect
              component="header"
              select={user.userNickname}
              options={['상점', '마이페이지', '로그아웃']}
            />
          </div>
        )}
      </div>
      <Outlet />
      {showAlarmModal && (
        <PortalPopup
          overlayColor="rgba(113, 113, 113, 0.4)"
          placement="Top right"
          onOutsideClick={closeAlarmModal}
          alarm="alarm"
        >
          <Alarm onClose={closeAlarmModal} />
        </PortalPopup>
      )}
    </>
  );
}
export default Header;
