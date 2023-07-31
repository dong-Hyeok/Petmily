import { styled } from '@mui/material';
import FavoriteRoundedIcon from '@mui/icons-material/FavoriteRounded';
import EditNoteRoundedIcon from '@mui/icons-material/EditNoteRounded';
import AddCircleOutlineRoundedIcon from '@mui/icons-material/AddCircleOutlineRounded';
import SocialComment from './SocialComment';
import { placeholderImage } from '../utils/utils';

function SocialPost() {
  const StyledFavoriteRoundedIcon = styled(FavoriteRoundedIcon, {
    name: 'StyledFavoriteRoundedIcon',
    slot: 'Wrapper',
  })({
    color: '#A6A7AB',
    fontSize: 28,
    '&:hover': { color: '#f4245e' },
  });
  const StyledEditNoteRoundedIcon = styled(EditNoteRoundedIcon, {
    name: 'StyledEditNoteRoundedIcon',
    slot: 'Wrapper',
  })({
    color: '#A6A7AB',
    fontSize: 30,
    '&:hover': { color: '#1f90fe' },
  });
  const StyledAddCircleOutlineRoundedIcon = styled(
    AddCircleOutlineRoundedIcon,
    {
      name: 'StyledAddCircleOutlineRoundedIcon',
      slot: 'Wrapper',
    },
  )({
    color: '#A6A7AB',
    fontSize: 26,
    '&:hover': { color: '#1f90fe' },
  });
  return (
    <div>
      <span className="mb-3 h-[0.06rem] w-full bg-gray2 inline-block" />

      <div className="flex flex-col px-[1rem] items-between justify-between">
        <div className="flex items-start">
          <div className="rounded-full overflow-hidden pr-2">
            <img
              className="rounded-full w-[3rem] h-[3rem] overflow-hidden object-cover"
              alt=""
              src={placeholderImage}
            />
          </div>
          <div className="flex flex-col w-full gap-[0.5rem] mx-4">
            <div className="flex items-center justify-start gap-[0.3rem] text-slategray">
              <b className="relative text-gray">Devon Lane</b>
              <div className="relative font-medium">@johndue</div>
              <div className="relative text-[0.94rem]">{`· `}</div>
              <div className="relative font-medium">{`23s `}</div>
            </div>
            <div className="flex-1 relative font-medium">
              우리집 강아지 커여웡
            </div>
            <div className="w-full">
              <img
                src={placeholderImage}
                className="h-full w-full rounded-lg overflow-hidden"
                alt=""
              />
            </div>
            <div className="flex justify-start h-full gap-[0.2rem]">
              <div
                role="presentation"
                className="gap-[0.5rem] rounded-full text-[1rem] w-fill h-[0.5rem] text-black flex p-[0.5rem] items-center justify-center"
              >
                <StyledFavoriteRoundedIcon className="mt-1" />
                <div>999</div>
              </div>
              <div
                role="presentation"
                className="gap-[0.5rem] rounded-full text-[1rem] w-fill h-[0.5rem] text-black flex p-[0.5rem] items-center justify-center"
              >
                <StyledEditNoteRoundedIcon className="mt-0.5" />
                <div>999</div>
              </div>
            </div>
            <SocialComment />
            <span className="mb-2 mx-2 h-[0.02rem] w-fill bg-gray2 inline-block" />
            <div className="gap-[0.5rem] flex justify-start items-center h-full w-full">
              <div className="w-full border-solid border-[1px] border-gray2 relative flex items-center justify-between rounded-11xl bg-white max-w-full h-[3rem]">
                <div className="absolute left-0 px-[0.6rem] h-[2rem] w-[2rem] rounded-full overflow-hidden">
                  <img
                    src={placeholderImage}
                    className="h-full w-full rounded-full overflow-hidden"
                    alt=""
                  />
                </div>
                <input
                  className=" focus:outline-none w-full h-auto focus:outline-dodgerblue py-[0.8rem] px-[3.5rem] focus:border-1.5 font-pretendard text-base
lex items-center font-medium rounded-full"
                  placeholder="검색어를 입력하세요"
                />
                <StyledAddCircleOutlineRoundedIcon
                  className="absolute right-0  px-[1rem]"
                  onClick={() => {
                    console.log('click');
                  }}
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default SocialPost;
