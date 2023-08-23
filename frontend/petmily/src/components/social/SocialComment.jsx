import { useEffect, useState } from 'react';

import DeleteForeverRoundedIcon from '@mui/icons-material/DeleteForeverRounded';
import { styled } from '@mui/material';
import { PropTypes, func, number, string } from 'prop-types';
import { useRecoilValue } from 'recoil';
import userAtom from 'states/users';

import { formatDate, profiles } from 'utils/utils';
import DeleteConfirmation from './DeleteConfirmation';

function SocialComment({ comments, deleteComment, toggleRecommentInput }) {
  const StyledDeleteForeverRoundedIcon = styled(DeleteForeverRoundedIcon, {
    name: 'StyledDeleteForeverRoundedIcon',
    slot: 'Wrapper',
  })({
    color: '#A6A7AB',
    fontSize: 20,
    '&:hover': { color: '#f4245e' },
  });

  const [showDeleteConfirmation, setShowDeleteConfirmation] = useState();
  const [showEdit, setShowEdit] = useState(false);

  const userLogin = useRecoilValue(userAtom);

  const handleDelete = () => {
    setShowDeleteConfirmation(true);
  };

  const handleCancelDelete = () => {
    setShowDeleteConfirmation(false);
  };

  const handleConfirmDelete = () => {
    deleteComment(comments.commentId);
    setShowDeleteConfirmation(false);
  };

  useEffect(() => {
    if (userLogin.userEmail === comments.userEmail) {
      setShowEdit(true);
    }
  }, []);

  return (
    <div>
      <span className="h-[0.06rem] w-full bg-gray2 inline-block m-0 p-0" />

      <div className="relative flex items-start mt-3">
        <DeleteConfirmation
          page="댓글"
          show={showDeleteConfirmation}
          onCancel={handleCancelDelete}
          onConfirm={handleConfirmDelete}
        />
        <div
          className={`${
            comments.userRing && 'border-solid border-[5px]'
          } w-[2.5rem] h-[2.5rem] rounded-full`}
          style={{
            borderColor: `#${comments?.userRing ? comments?.userRing : ''}`,
          }}
        >
          <img
            className="w-[2.5rem] h-[2.5rem] object-cover rounded-full overflow-hidden"
            alt=""
            src={comments.userProfileImg ? comments.userProfileImg : profiles}
          />
        </div>
        <div className="flex flex-col gap-[0.6rem] mx-4 w-full">
          <div className="flex items-center justify-between text-slategray">
            <div className="flex items-center gap-[0.5rem]">
              <b className="text-gray text-semibold text-lg">
                {comments.userNickname}
              </b>
              <div className="font-medium text-sm">
                {`· `}
                {formatDate(comments.commentTime)}
              </div>
            </div>
            {showEdit && (
              <div className="flex items-center gap-[0.5rem]">
                <div
                  role="presentation"
                  className="gap-[0.5rem] rounded-full text-[1rem] w-fill h-[0.5rem] text-black flex items-center justify-center cursor-pointer"
                  onClick={handleDelete}
                >
                  <StyledDeleteForeverRoundedIcon />
                </div>
              </div>
            )}
          </div>
          <div className="w-full flex justify-between items-end">
            <div className="break-all mr-[4.5rem] font-pretendard text-gray">
              {comments.commentContent}
            </div>
            {toggleRecommentInput ? (
              <div
                role="presentation"
                className={`${
                  comments ? '' : 'bg-lightblue rounded-full'
                } border-solid border-dodgerblue cursor-pointer px-2 font-pretendard p-1 text-dodgerblue text-sm font-semibold whitespace-nowrap`}
                onClick={() => toggleRecommentInput(comments)}
              >
                답글 달기
              </div>
            ) : null}
          </div>
        </div>
      </div>
    </div>
  );
}

SocialComment.propTypes = {
  comments: PropTypes.shape({
    commentId: number,
    commentContent: string,
    commentTime: string,
    userEmail: string,
    userNickname: string,
    userProfileImg: string,
    boardId: number,
    parentId: number,
    userRing: string,
  }).isRequired,
  deleteComment: func,
  toggleRecommentInput: func,
};

export default SocialComment;
