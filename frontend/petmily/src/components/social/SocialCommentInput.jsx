import { useState } from 'react';

import AddCircleOutlineRoundedIcon from '@mui/icons-material/AddCircleOutlineRounded';
import { styled } from '@mui/material';
import { PropTypes, func, number, string } from 'prop-types';
import { useRecoilValue } from 'recoil';
import userAtom from 'states/users';
import recommentIdAtom from 'states/recommentid';
import { profiles } from 'utils/utils';

function SocialCommentInput({
  createComment,
  recomment,
  comments,
  toggleRecommentInput,
}) {
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

  const [commentTexts, setCommentTexts] = useState('');

  const userLogin = useRecoilValue(userAtom);
  const recommentId = useRecoilValue(recommentIdAtom);

  const handleCommentChange = e => {
    setCommentTexts(e.target.value);
  };

  const onSubmitNewComment = e => {
    e.preventDefault();
    if (commentTexts) {
      createComment(commentTexts);
      setCommentTexts('');
    }
  };

  return recomment ? (
    <div className="gap-[0.5rem] flex justify-start items-center h-full max-w-full ml-[3rem] my-3">
      <div className="relative w-full border-solid border-[1px] border-gray2 flex items-center justify-between rounded-11xl bg-white max-w-full h-[3rem]">
        <div className="absolute left-0 px-[0.6rem] flex gap-3 justify-center items-center">
          <div className="h-[2rem] w-[2rem] rounded-full overflow-hidden">
            <img
              className="rounded-full w-[2rem] h-[2rem] overflow-hidden object-cover"
              alt=""
              src={
                userLogin.userProfileImg ? userLogin.userProfileImg : profiles
              }
            />
          </div>
          <div className="overflow-hidden whitespace-nowrap max-w-[3rem] text-sm font-pretendard bg-lightblue text-dodgerblue font-bold flex justify-start items-center h-[1.5rem] px-2 rounded-full">
            <div className="overflow-ellipsis">@ {recommentId[2]}</div>
          </div>
        </div>
        <input
          className="focus:outline-none w-full h-auto py-[0.8rem] pl-[8rem] pr-[3.5rem] focus:outline-dodgerblue focus:border-1.5 font-pretendard text-base
lex items-center font-medium rounded-full"
          placeholder="답글을 입력하세요"
          onChange={e => handleCommentChange(e)}
          value={commentTexts}
          onKeyUp={e => {
            if (e.key === 'Enter') {
              onSubmitNewComment(e);
            }
          }}
        />
        <StyledAddCircleOutlineRoundedIcon
          className="cursor-pointer absolute right-0 px-[1rem]"
          onClick={e => onSubmitNewComment(e)}
        />
      </div>
    </div>
  ) : (
    <div className="gap-[0.5rem] flex justify-start items-center h-full w-full">
      <div className="relative w-full border-solid border-[1px] border-gray2 flex items-center justify-between rounded-11xl bg-white max-w-full h-[3rem]">
        <div className="absolute left-0 px-[0.6rem] h-[2rem] w-[2rem] rounded-full overflow-hidden">
          <img
            className="rounded-full w-[2rem] h-[2rem] overflow-hidden object-cover"
            alt=""
            src={userLogin.userProfileImg ? userLogin.userProfileImg : profiles}
          />
        </div>
        <input
          className="focus:outline-none w-full h-auto py-[0.8rem] px-[3.5rem] focus:outline-dodgerblue focus:border-1.5 font-pretendard text-base
  lex items-center font-medium rounded-full"
          placeholder="댓글을 입력하세요"
          onChange={e => handleCommentChange(e)}
          value={commentTexts}
          onClick={() => toggleRecommentInput(comments)}
          onKeyUp={e => {
            if (e.key === 'Enter') {
              onSubmitNewComment(e);
            }
          }}
        />
        <StyledAddCircleOutlineRoundedIcon
          className="cursor-pointer absolute right-0 px-[1rem]"
          onClick={e => onSubmitNewComment(e)}
        />
      </div>
    </div>
  );
}

SocialCommentInput.propTypes = {
  createComment: func.isRequired,
  recomment: string,
  comments: PropTypes.arrayOf(
    PropTypes.shape({
      boardId: number,
      commentContent: string,
      commentId: number,
      commentTime: string,
      parentId: number,
      userEmail: string,
    }),
  ),
  toggleRecommentInput: func,
};

export default SocialCommentInput;
