import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import Carousel from 'react-material-ui-carousel';
import { Paper, styled } from '@mui/material';
import ArrowCircleUpRoundedIcon from '@mui/icons-material/ArrowCircleUpRounded';
import CheckCircleOutlineRoundedIcon from '@mui/icons-material/CheckCircleOutlineRounded';
import DeleteForeverRoundedIcon from '@mui/icons-material/DeleteForeverRounded';
import DriveFileRenameOutlineRoundedIcon from '@mui/icons-material/DriveFileRenameOutlineRounded';
import EditNoteRoundedIcon from '@mui/icons-material/EditNoteRounded';
import FavoriteRoundedIcon from '@mui/icons-material/FavoriteRounded';
import PetsRoundedIcon from '@mui/icons-material/PetsRounded';

import swal from 'sweetalert';
import { PropTypes, number, string, bool } from 'prop-types';
import { useRecoilState, useRecoilValue, useSetRecoilState } from 'recoil';
import userAtom from 'states/users';
import followAtom from 'states/follow';
import recommentAtom from 'states/recomment';
import recommentIdAtom from 'states/recommentid';
import updateimageAtom from 'states/updateimage';
import updatepreviewAtom from 'states/updatepreview';

import { formatDate } from 'utils/utils';
import useFetch from 'utils/fetch';
import {
  SocialComment,
  SocialCommentInput,
  DeleteConfirmation,
  UploadImage,
} from 'components';
import chatAtom from 'states/chat';

function SocialPost({ post, readPosts, updatePost, deletePost }) {
  const [heart, setHeart] = useState(post.heartCount);
  const [actionHeart, setActionHeart] = useState(post.likedByCurrentUser);
  const [followedUsers, setFollowedUsers] = useRecoilState(followAtom);
  const [actionFollow, setActionFollow] = useState(post.followedByCurrentUser);

  const StyledFavoriteRoundedIcon = styled(FavoriteRoundedIcon, {
    name: 'StyledFavoriteRoundedIcon',
    slot: 'Wrapper',
  })({
    color: actionHeart ? '#f4245e' : '#A6A7AB',
    fontSize: 28,
    '&:hover': { color: '#f4245e' },
  });
  const StyledPetsRoundedIcon = styled(PetsRoundedIcon, {
    name: 'StyledPetsRoundedIcon',
    slot: 'Wrapper',
  })({
    color: '#ffffff',
    fontSize: actionFollow ? 15 : 18,
    '&:hover': { color: '#1f90fe' },
  });
  const StyledEditNoteRoundedIcon = styled(EditNoteRoundedIcon, {
    name: 'StyledEditNoteRoundedIcon',
    slot: 'Wrapper',
  })({
    color: '#A6A7AB',
    fontSize: 30,
    '&:hover': { color: '#1f90fe' },
  });
  const StyledDeleteForeverRoundedIcon = styled(DeleteForeverRoundedIcon, {
    name: 'StyledDeleteForeverRoundedIcon',
    slot: 'Wrapper',
  })({
    color: '#A6A7AB',
    fontSize: 26,
    '&:hover': { color: '#f4245e' },
  });
  const StyledArrowCircleUpRoundedIcon = styled(ArrowCircleUpRoundedIcon, {
    name: 'StyledArrowCircleUpRoundedIcon',
    slot: 'Wrapper',
  })({
    color: '#A6A7AB',
    fontSize: 26,
    transform: 'rotate(-90deg)',
    '&:hover': { color: '#f4245e' },
  });
  const StyledDriveFileRenameOutlineRoundedIcon = styled(
    DriveFileRenameOutlineRoundedIcon,
    {
      name: 'StyledDriveFileRenameOutlineRoundedIcon',
      slot: 'Wrapper',
    },
  )({
    color: '#A6A7AB',
    fontSize: 26,
    '&:hover': { color: '#1f90fe' },
  });
  const StyledCheckCircleOutlineRoundedIcon = styled(
    CheckCircleOutlineRoundedIcon,
    {
      name: 'StyledCheckCircleOutlineRoundedIcon',
      slot: 'Wrapper',
    },
  )({
    color: '#A6A7AB',
    fontSize: 26,
    '&:hover': { color: '#1f90fe' },
  });

  const navigate = useNavigate();
  const fetchData = useFetch();

  const [comments, setComments] = useState(post.comments);
  const [editMode, setEditMode] = useState(false);
  const [showEdit, setShowEdit] = useState(false);
  const [editedText, setEditedText] = useState(post.boardContent);
  const [hashTag, setHashTag] = useState('');
  const [hashTags, setHashTags] = useState(post.hashTags);
  const [heartCount, setHeartCount] = useState();
  const [commentsCount, setCommentsCount] = useState();
  const [showDeleteConfirmation, setShowDeleteConfirmation] = useState(false);
  const [isHovered, setIsHovered] = useState(false);
  const showNextButton = post.photoUrls?.length >= 2;
  const isFollowed = followedUsers[post.userEmail];

  const userLogin = useRecoilValue(userAtom);
  const setChatId = useSetRecoilState(chatAtom);
  const setUpdateFilePreview = useSetRecoilState(updatepreviewAtom);
  const [recommentId, setRecommentId] = useRecoilState(recommentIdAtom);
  const [recommentInputMap, setRecommentInputMap] =
    useRecoilState(recommentAtom);
  const [updateUploadedImage, setUpdateUploadedImage] =
    useRecoilState(updateimageAtom);

  const toggleRecommentInput = comment => {
    setRecommentInputMap(prevState => ({
      [comment.commentId]: !prevState[comment.commentId],
    }));
    setRecommentId([comment.boardId, comment.commentId, comment.userNickname]);
  };

  const toggleEditMode = () => {
    setUpdateFilePreview([]);
    setUpdateUploadedImage([...updateUploadedImage]);
    setEditedText(post.boardContent);
    setEditMode(prevEditMode => !prevEditMode);
  };

  const onHashTagChange = e => {
    const input = e.currentTarget.value;
    setHashTag(input);

    if (input.endsWith(' ')) {
      const newTag = input.trim();
      if (hashTags.includes(newTag)) {
        swal('중복된 해시태그는 생성 블가합니다!');
        setHashTag('');
      }
      if (newTag !== '' && !hashTags.includes(newTag)) {
        setHashTags([...hashTags, newTag]);
        setHashTag('');
      }
    }
  };

  const removeHashTag = removedTag => {
    const updatedTags = hashTags.filter(tag => tag !== removedTag);
    setHashTags(updatedTags);
  };

  const handleTextChange = e => {
    setEditedText(e.target.value);
  };

  const handleUpdate = () => {
    updatePost(post, editedText, hashTags);
    setEditMode(false);
  };

  const handleCancel = () => {
    setEditMode(false);
  };

  const handleDelete = () => {
    setShowDeleteConfirmation(true);
  };

  const handleCancelDelete = () => {
    setShowDeleteConfirmation(false);
  };

  const handleConfirmDelete = () => {
    deletePost(post.boardId);
    setShowDeleteConfirmation(false);
  };

  const readComments = async boardId => {
    try {
      const response = await fetchData.get(
        `board/${boardId}?currentUserEmail=${userLogin.userEmail}`,
      );
      setComments(response.comments);
      console.log('여기는 댓글 읽기 삭제 후 댓글 불러오기', comments);
    } catch (error) {
      console.log(error);
    }
  };

  const createComment = async createCommentText => {
    const sendBE = {
      userEmail: userLogin.userEmail,
      boardId:
        (recommentInputMap[recommentId[1]] && recommentId[0]) || post.boardId,
      commentContent: createCommentText,
      parentId: (recommentInputMap[recommentId[1]] && recommentId[1]) || false,
    };
    console.log(sendBE);
    try {
      const response = await fetchData.post('comment/save', sendBE);
      console.log('여기댓글생성응답', response);

      readComments(post.boardId);
    } catch (error) {
      console.log(error);
    }
  };

  const deleteComment = async currentCommentId => {
    const response = await fetchData.delete(`comment/${currentCommentId}`);
    console.log('댓글 삭제', response);
    readComments(post.boardId);
  };

  const handleHeart = async () => {
    const sendBE = {
      userEmail: userLogin.userEmail,
      boardId: post.boardId,
    };

    if (actionHeart === false) {
      try {
        const response = await fetchData.post('board/heart', sendBE);
        console.log('좋아요 응답 성공', response);
        setActionHeart(true);
        setHeart(prev => prev + 1);
      } catch (error) {
        console.log(error);
      }
    } else if (actionHeart === true) {
      try {
        const response = await fetchData.delete('board/heart', sendBE);
        console.log('좋아요 취소 응답 성공', response);
        setActionHeart(false);
        setHeart(prev => prev - 1);
      } catch (error) {
        console.log(error);
      }
    }
  };

  const handleFollow = async () => {
    setFollowedUsers(prevFollowedUsers => ({
      ...prevFollowedUsers,
      [post.userEmail]: !prevFollowedUsers[post.userEmail],
    }));

    const sendBE = {
      userEmail: userLogin.userEmail,
    };
    if (actionFollow === false) {
      try {
        const response = await fetchData.post(
          `follow/${post.userEmail}`,
          sendBE,
        );
        console.log('팔로우 응답 성공', response);
        setActionFollow(true);
      } catch (error) {
        console.log(error);
      }
    } else if (actionFollow === true) {
      try {
        const response = await fetchData.delete(
          `follow/${post.userEmail}`,
          sendBE,
        );
        console.log('팔로우 취소 응답 성공', response);
        setActionFollow(false);
      } catch (error) {
        console.log(error);
      }
    }
  };

  useEffect(() => {
    readPosts();
    if (userLogin.userEmail === post.userEmail) {
      setShowEdit(true);
    }
  }, [comments]);

  useEffect(() => {
    if (heart === 0) {
      setHeartCount('Likes');
    } else {
      setHeartCount(heart);
    }
    if (Array.isArray(post.comments)) {
      if (post.comments?.length === 0) {
        setCommentsCount('Comments');
      } else {
        setCommentsCount(post.comments?.length);
      }
      if (post.comments.commentId) {
        toggleRecommentInput();
      }
    }
  }, [heart, comments, toggleRecommentInput]);

  const createChatRoom = async (receieverEmail, e) => {
    e.preventDefault();
    const sendBE = {
      sender: userLogin.userEmail,
      receiver: receieverEmail,
    };
    try {
      const response = await fetchData.post('chat/start', sendBE);
      console.log('채팅방 생성 id', response);
      setChatId([receieverEmail, response, post.userProfileImageUrl]);
      navigate(`/social/chat/${response}`);
    } catch (error) {
      console.log(error);
    }
  };

  if (!post || !userLogin || !userLogin.userEmail) {
    return <div>Loading...</div>;
  }
  return (
    <div className="relative w-full">
      <span className="mb-3 h-[0.06rem] w-full bg-gray2 inline-block" />
      <DeleteConfirmation
        show={showDeleteConfirmation}
        onCancel={handleCancelDelete}
        onConfirm={handleConfirmDelete}
      />
      <div className="flex flex-col px-[1rem] items-between justify-between">
        <div className="flex items-start">
          <div className="relativerounded-full overflow-hidden pr-2">
            <img
              className="rounded-full w-[3rem] h-[3rem] overflow-hidden object-cover"
              alt=""
              src={post ? post.userProfileImageUrl : ''}
            />
          </div>
          {userLogin.userEmail !== post?.userEmail && (
            <div
              role="presentation"
              onClick={handleFollow}
              className="transition-colors duration-300 text-white bg-dodgerblue hover:bg-lightblue hover:text-dodgerblue cursor-pointer absolute left-[2.8rem] flex justify-center items-center rounded-full font-bold w-[1.5rem] h-[1.5rem]"
            >
              {isFollowed ? <StyledPetsRoundedIcon /> : <div>+</div>}
            </div>
          )}
          <div className="flex flex-col w-full mx-4">
            <div className="flex items-center justify-between text-slategray">
              <div className="whitespace-nowrap flex gap-[0.5rem] items-center justify-between">
                <b className="text-gray text-lg">{post.userNickname}</b>
                {userLogin.userEmail !== post?.userEmail && (
                  <div
                    className="transition-colors duration-300 hover:bg-lightblue cursor-pointer border-solid border-[1.5px] border-dodgerblue px-2 py-1 rounded-full"
                    role="presentation"
                    onClick={e => {
                      createChatRoom(post?.userEmail, e);
                    }}
                  >
                    <div className="text-dodgerblue text-xs font-bold">
                      메세지
                    </div>
                  </div>
                )}
                <div className="font-medium text-sm">
                  {` · `}
                  {formatDate(post.boardUploadTime)}
                </div>
              </div>

              <div className="flex items-center justify-center">
                {showEdit && !editMode && (
                  <>
                    <div
                      role="presentation"
                      className="cursor-pointer gap-[0.5rem] rounded-full text-[1rem] w-fill h-[0.5rem] text-black flex p-[0.5rem] items-center justify-center"
                      onClick={toggleEditMode}
                    >
                      <StyledDriveFileRenameOutlineRoundedIcon className="mt-0.5" />
                    </div>
                    <div
                      role="presentation"
                      className="cursor-pointer gap-[0.5rem] rounded-full text-[1rem] w-fill h-[0.5rem] text-black flex p-[0.5rem] items-center justify-center"
                      onClick={handleDelete}
                    >
                      <StyledDeleteForeverRoundedIcon className="mt-0.5" />
                    </div>
                  </>
                )}
                {editMode && (
                  <>
                    <div
                      role="presentation"
                      className="cursor-pointer gap-[0.5rem] rounded-full text-[1rem] w-fill h-[0.5rem] text-black flex p-[0.5rem] items-center justify-center"
                      onClick={handleCancel}
                    >
                      <StyledArrowCircleUpRoundedIcon className="mt-0.5" />
                    </div>
                    <div
                      type="submit"
                      role="presentation"
                      className="cursor-pointer gap-[0.5rem] rounded-full text-[1rem] w-fill h-[0.5rem] text-black flex p-[0.5rem] items-center justify-center"
                      onClick={handleUpdate}
                    >
                      <StyledCheckCircleOutlineRoundedIcon className="mt-0.5" />
                    </div>
                  </>
                )}
              </div>
            </div>
            {editMode ? (
              <div className="h-full w-fill">
                <div className="w-fill mr-[2.3rem]">
                  <textarea
                    rows="5"
                    value={editedText}
                    onChange={handleTextChange}
                    className="resize-none mt-5 w-full h-full text-black rounded-xl p-4 border-solid border-[2px] border-dodgerblue focus:outline-none font-pretendard text-base"
                  />
                </div>
                <UploadImage page="소통하기수정" />
              </div>
            ) : (
              post.boardContent && (
                <div className="break-all font-pretendard text-base mt-5 font-base w-fill text-black rounded-xl p-4 border-solid border-[2px] border-gray2 focus:outline-none focus:border-dodgerblue">
                  {post.boardContent}
                </div>
              )
            )}
            {editMode ? (
              <div className="w-full">
                <div className="flex gap-2 pb-2 w-full flex-wrap">
                  <input
                    onChange={onHashTagChange}
                    value={hashTag}
                    name="hasgTag"
                    placeholder="해시태그 입력 후 스페이스 바를 누르세요"
                    className="font-medium w-full text-black rounded-xl p-4 border-solid border-[2px] border-gray2 focus:outline-none focus:border-dodgerblue font-pretendard text-base"
                  />
                  {hashTags?.map(tag => (
                    // eslint-disable-next-line jsx-a11y/click-events-have-key-events, jsx-a11y/no-static-element-interactions
                    <div
                      key={tag}
                      onClick={() => removeHashTag(tag)}
                      className="text-sm cursor-pointer px-3 py-2 w-fit bg-gray2 rounded-xl whitespace-nowrap"
                    >
                      # {tag}
                    </div>
                  ))}
                </div>
              </div>
            ) : (
              <div className="flex gap-2 py-2 max-w-[46rem] w-full flex-wrap">
                {post.hashTags?.map(tag => (
                  // eslint-disable-next-line jsx-a11y/click-events-have-key-events, jsx-a11y/no-static-element-interactions
                  <div
                    key={tag}
                    onClick={() => removeHashTag(tag)}
                    className="text-sm cursor-pointer px-3 py-2 w-fit bg-gray2 rounded-xl whitespace-nowrap"
                  >
                    # {tag}
                  </div>
                ))}
              </div>
            )}

            {post.photoUrls?.length > 0 ? (
              <Carousel
                className={`z-0 w-full h-[40rem] ${
                  post.boardContent ? 'mt-3' : 'mt-0'
                } rounded-xl overflow-hidden object-cover`}
                autoPlay={false}
                animation="slide"
                fullHeightHover
                indicatorContainerProps={{
                  style: {
                    zIndex: 1,
                    marginTop: '-2rem',
                    position: 'relative',
                    opacity: post.photoUrls?.length === 1 ? 0 : 0.3,
                  },
                }}
                navButtonsAlwaysVisible={showNextButton}
                navButtonsAlwaysInvisible={!showNextButton}
                navButtonsProps={{
                  style: {
                    opacity: isHovered ? 1 : 0.3,
                  },
                }}
                cycleNavigation={false}
                onMouseEnter={() => {
                  setIsHovered(true);
                }}
                onMouseLeave={() => {
                  setIsHovered(false);
                }}
              >
                {post.photoUrls?.map(photos => {
                  return (
                    <Paper key={photos}>
                      <div className="w-full h-[40rem] flex rounded-xl overflow-hidden object-cover justify-center items-center">
                        <img
                          src={photos}
                          className="w-full h-[40rem] rounded-xl overflow-hidden object-cover"
                          alt=""
                        />
                      </div>
                    </Paper>
                  );
                })}
              </Carousel>
            ) : (
              false
            )}
            <div className="flex justify-start h-full mt-3 gap-[0.2rem]">
              <div
                role="presentation"
                className="gap-[0.5rem] rounded-full text-sm font-semibold w-fill h-[0.5rem] text-black flex p-[0.5rem] items-center justify-center"
                onClick={handleHeart}
              >
                <StyledFavoriteRoundedIcon className="" />
                <div>{heartCount}</div>
              </div>
              <div
                role="presentation"
                className="gap-[0.5rem] rounded-full text-sm font-semibold w-fill h-[0.5rem] text-black flex p-[0.5rem] items-center justify-center"
              >
                <StyledEditNoteRoundedIcon className="" />
                <div>{commentsCount}</div>
              </div>
            </div>
            <span className="m-3 h-[0.02rem] w-fill bg-gray2 inline-block" />
            <SocialCommentInput
              createComment={createComment}
              comments={post.comments}
              toggleRecommentInput={toggleRecommentInput}
            />
            {post.comments?.map(c => {
              if (!c.parentId) {
                return (
                  <div key={c.commentId}>
                    <SocialComment
                      comments={c}
                      deleteComment={deleteComment}
                      toggleRecommentInput={toggleRecommentInput}
                    />
                    {post.comments?.map(co => {
                      if (co.parentId === c.commentId) {
                        return (
                          <div key={co.commentId} className="ml-[3.5rem]">
                            <SocialComment
                              comments={co}
                              deleteComment={deleteComment}
                            />
                          </div>
                        );
                      }
                      return false;
                    })}
                    {recommentInputMap[c.commentId] && (
                      <SocialCommentInput
                        createComment={createComment}
                        recomment="recomment"
                      />
                    )}
                  </div>
                );
              }
              return false;
            })}
            <span className="m-3 h-[0.02rem] w-fill bg-gray2 inline-block" />
          </div>
        </div>
      </div>
    </div>
  );
}

SocialPost.propTypes = {
  post: PropTypes.shape({
    boardContent: string,
    boardId: number,
    boardUploadTime: string,
    comments: PropTypes.arrayOf(
      PropTypes.shape({
        boardId: number,
        commentContent: string,
        commentId: number,
        commentTime: string,
        parentId: number,
        userEmail: string,
      }),
    ).isRequired,
    hashTags: PropTypes.arrayOf(string).isRequired,
    heartCount: number,
    heartdByCurrentUser: bool,
    photoUrls: PropTypes.arrayOf(string).isRequired,
    userEmail: string,
    userNickname: string,
    userProfileImageUrl: string,
    likedByCurrentUser: bool,
    followedByCurrentUser: bool,
  }).isRequired,
  readPosts: PropTypes.func.isRequired,
  updatePost: PropTypes.func.isRequired,
  deletePost: PropTypes.func.isRequired,
};

export default SocialPost;
