import React from 'react';
import { useNavigate } from 'react-router';
import MaleRoundedIcon from '@mui/icons-material/MaleRounded';
import FemaleRoundedIcon from '@mui/icons-material/FemaleRounded';
import { styled } from '@mui/material';
import { shape, string, number } from 'prop-types';
import { profileImage } from 'utils/utils';

function MyPetDetail({ petDetail }) {
  const StyledMaleRoundedIcon = styled(MaleRoundedIcon, {
    name: 'StyledMaleRoundedIcon',
    slot: 'Wrapper',
  })({
    color: '#1f90fe',
  });
  const StyledFemaleRoundedIcon = styled(FemaleRoundedIcon, {
    name: 'StyledFemaleRoundedIcon',
    slot: 'Wrapper',
  })({
    color: '#F44336',
  });
  const { profileCat, profileDog } = profileImage;

  const navigate = useNavigate();
  const toModifyPetInfo = () => {
    navigate('/petinfo/edit', { state: petDetail.petId });
  };
  return (
    <div className="rounded-11xl w-full bg-white min-w-[20%] h-[768px] flex flex-col basis-1/4 box-border items-start justify-start gap-[0.75rem] text-[1.25rem]">
      <div className="relative w-full text-[1.31rem]">
        <div className="relative flex flex-row w-full items-end justify-between">
          <div className="relative rounded-[100px] box-border w-[98px] h-[95px] overflow-hidden border-[4px] border-solid border-gray">
            <div className="relative top-[0px] rounded-[100px] w-[90px] h-[90px] overflow-hidden">
              <img
                className="h-[5.5rem] w-[5.5rem] overflow-hidden object-cover"
                alt={
                  petDetail.speciesName === '강아지' ? profileDog : profileCat
                }
                src={
                  petDetail.petImg ||
                  (petDetail.speciesName === '강아지' ? profileDog : profileCat)
                }
              />
            </div>
          </div>
          <div
            role="presentation"
            onClick={toModifyPetInfo}
            className="relative right-0 top-[0.5px] rounded-[100px] box-border w-28 h-[39px] overflow-hidden flex flex-row py-[0.94rem] px-[0.19rem] items-center justify-center text-center text-[0.94rem] text-dodgerblue border-[1px] border-solid border-dodgerblue cursor-pointer"
          >
            <b className="flex-1 relative leading-[1.19rem]">정보 수정</b>
          </div>
        </div>
      </div>
      <div className="relative flex flex-col items-start justify-start gap-[0.31rem]">
        <b className="relative tracking-[-0.01em] z-[0]">{petDetail.petName}</b>
        <div className="flex flex-row items-center whitespace-nowrap gap-2 relative text-[1rem] tracking-[-0.01em] font-medium w-20 z-[1]">
          {`${new Date().getFullYear() - petDetail.petBirth.slice(0, 4)}살`}
          {petDetail.petGender === 1 ? (
            <StyledMaleRoundedIcon />
          ) : (
            <StyledFemaleRoundedIcon />
          )}
        </div>
      </div>
      <div className="rounded-lg border-solid border-[1px] border-lightgray p-3 bg-white w-full flex flex-col box-border items-start justify-start gap-[0.94rem] text-[0.94rem]">
        <div
          className="self-stretch flex flex-col items-start 
        justify-start gap-[0.38rem] text-[0.88rem] text-slategray"
        >
          {petDetail.petInfo}
        </div>
      </div>
    </div>
  );
}
const petType = {
  petId: number,
  petName: string,
  petImg: string,
  petGender: string,
  petInfo: string,
  petBirth: string,
  speciesName: string,
};
MyPetDetail.propTypes = {
  petDetail: shape(petType),
};
export default MyPetDetail;
