// import MaleRoundedIcon from '@mui/icons-material/MaleRounded';
// import { useNavigate } from 'react-router';
// import { styled } from '@mui/material';

import { useEffect, useState } from 'react';
import { useRecoilValue } from 'recoil';
import { useNavigate } from 'react-router';
import ArrowForwardIosRoundedIcon from '@mui/icons-material/ArrowForwardIosRounded';
import ArrowBackRoundedIcon from '@mui/icons-material/ArrowBackRounded';
import { styled } from '@mui/material';

import profileDog from 'static/images/profiledog.png';
import profileCat from 'static/images/profilecat.png';
import petAtom from 'states/pets';
import { formatLocaleDate } from 'utils/utils';
import MyPetDetail from './MyPetDetail';

function MyPetInfo() {
  const navigate = useNavigate();
  const [openPetDetail, setOpenPetDetail] = useState(false);
  const petInfos = useRecoilValue(petAtom);
  const [clickedPet, setClickedPet] = useState({});
  const StyledArrowForwardIosRoundedIcon = styled(ArrowForwardIosRoundedIcon, {
    name: 'StyledArrowForwardIosRoundedIcon',
    slot: 'Wrapper',
  })({});
  const StyleBackRoundedIcon = styled(ArrowBackRoundedIcon, {
    name: 'StyleBackRoundedIcon',
    slot: 'Wrapper',
  })({
    color: '#1f90fe',
  });

  const handleGoBack = () => {
    setOpenPetDetail(false);
  };

  function showPetDetails() {
    if (!openPetDetail) {
      return petInfos.length > 0 ? (
        <>
          {petInfos.map(ele => {
            return (
              <div
                role="presentation"
                key={ele.petName}
                className="self-stretch flex flex-col items-start justify-start gap-[0.63rem]"
                onClick={() => {
                  setOpenPetDetail(true);
                  setClickedPet(ele);
                }}
              >
                <div className="w-full flex flex-row py-[0.75rem] px-[1rem] box-border items-center justify-between cursor-pointer">
                  <div className="w-fill gap-4 flex flex-row items-center justify-between">
                    <div className="h-11 w-11 rounded-full overflow-hidden">
                      <img
                        className="h-11 w-11 overflow-hidden object-cover"
                        alt={
                          ele.speciesName === '강아지' ? profileDog : profileCat
                        }
                        src={
                          ele.petImg ||
                          (ele.speciesName === '강아지'
                            ? profileDog
                            : profileCat)
                        }
                      />
                    </div>
                    <div className="flex flex-col items-start justify-start gap-[0.3rem]">
                      <b className="">{ele.petName}</b>
                      <div className="text-[1rem] font-medium text-slategray">
                        {formatLocaleDate(ele.petBirth)}
                      </div>
                    </div>
                  </div>
                  <div className="rounded-xl bg-dodgerblue h-8 w-20 overflow-hidden whitespace-nowrap flex flex-row box-border items-center justify-center text-center text-sm text-white">
                    <b className="">{ele.speciesName}</b>
                  </div>
                </div>
                <div className="bg-slate-200 w-full h-[1px]" />
              </div>
            );
          })}
          <div
            role="presentation"
            className="flex flex-row items-center justify-center gap-5 w-full py-2 cursor-pointer"
            onClick={() => {
              navigate('/petinfo');
            }}
          >
            <span>반려동물 정보 추가하기</span>
            <span className="font-bold text-5xl text-dodgerblue">+</span>
          </div>
        </>
      ) : (
        <div
          role="presentation"
          className="flex flex-row items-center justify-center gap-5 w-full py-2 cursor-pointer"
          onClick={() => {
            navigate('/petinfo');
          }}
        >
          <span>반려동물 정보 등록하러 가기</span>
          <StyledArrowForwardIosRoundedIcon className="text-dodgerblue" />
        </div>
      );
    }

    return <MyPetDetail petDetail={clickedPet} />;
  }
  useEffect(() => {
    showPetDetails();
  }, [openPetDetail]);
  return (
    // eslint-disable-next-line react/jsx-no-useless-fragment
    <>
      <div className="mx-4 basis-1/4 flex h-fit rounded-xl bg-white min-w-[20%] flex-col p-[1rem] items-start justify-start gap-[0.38rem] font-pretendard">
        <div className="flex w-full flex-col items-start justify-center gap-[1rem] text-[1.25rem]">
          {openPetDetail ? (
            <div
              role="presentation"
              className="flex flex-col py-[0.5rem] items-start justify-start cursor-pointer"
              onClick={handleGoBack}
            >
              <StyleBackRoundedIcon />
            </div>
          ) : (
            <>
              <div className="ml-1 font-semibold">내 반려동물</div>
              <div className="bg-slate-200 w-full h-[1.5px]" />
            </>
          )}
        </div>
        {showPetDetails()}
      </div>
    </>
  );
}

export default MyPetInfo;
