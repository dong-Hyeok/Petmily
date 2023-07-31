import RefreshRoundedIcon from '@mui/icons-material/RefreshRounded';
import CheckRoundedIcon from '@mui/icons-material/CheckRounded';
import { styled } from '@mui/material';
import { useState } from 'react';
import UploadProfileImage from 'components/UploadProfileImage';
import FollowRecommend from 'components/FollowRecommend';
import SocialPost from 'components/SocialPost';
import SearchBar from 'components/SearchBar';
import { placeholderImage } from 'utils/utils';

function Social() {
  const StyledRefreshRoundedIcon = styled(RefreshRoundedIcon, {
    name: 'StyledRefreshRoundedIcon',
    slot: 'Wrapper',
  })({
    color: '#0F1419',
    fontSize: 26,
    '&:hover': { color: '#1f90fe' },
  });
  const [uploadedImage, setUploadedImage] = useState([]);

  return (
    <div className="pb-[10rem] min-w-[1340px] max-w-full w-full absolute top-[6.5rem] flex justify-between">
      <div className="mx-4 basis-1/4 flex h-[100px] rounded-lg bg-white">d</div>
      <div className="basis-1/2 min-w-[400px] rounded-lg flex flex-col gap-4">
        <SearchBar page="소통하기" />
        <div className="rounded-xl bg-white w-full h-full flex flex-col items-center justify-center text-[1rem] text-black">
          <div className="flex flex-col gap-3 w-full my-4">
            <div className="flex justify-between w-full">
              <div className="font-semibold text-[1.25rem] mx-6">뉴 피드</div>
              <div className="mx-6">
                <StyledRefreshRoundedIcon />
              </div>
            </div>
            <span className="h-[0.06rem] w-full bg-gray2 inline-block" />
            <div className="relative flex pb-12 flex-col px-[1rem] items-between justify-between">
              <div className="flex items-start">
                <div className="w-[3rem] h-[3rem] pr-4 overflow-hidden">
                  <img
                    className="rounded-full w-[3rem] h-[3rem] overflow-hidden object-cover"
                    alt=""
                    src={placeholderImage}
                  />
                </div>
                <textarea
                  name=""
                  id=""
                  cols="80"
                  rows="5"
                  placeholder="자유롭게 이야기 해보세요!"
                  className="resize-none font-medium w-full text-black mx-4 rounded-xl p-4 border-solid border-[2px] border-gray2 focus:outline-none focus:border-dodgerblue font-pretendard text-base"
                />
              </div>
              <UploadProfileImage
                page="소통하기"
                uploadedImage={uploadedImage}
                setUploadedImage={setUploadedImage}
              />
              <div className="absolute right-4 bottom-0 cursor-pointer rounded-full text-[1rem] w-[1.2rem] h-[0rem] text-white bg-dodgerblue border-solid border-[2px] border-dodgerblue flex p-[1rem] ml-[0.4rem] mr-[1rem] mt-[0.6rem] items-center justify-center opacity-[1]">
                <CheckRoundedIcon />
              </div>
            </div>
            <SocialPost />
          </div>
        </div>
      </div>
      <FollowRecommend />
    </div>
  );
}

export default Social;
