import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import CheckRoundedIcon from '@mui/icons-material/CheckRounded';
import RefreshRoundedIcon from '@mui/icons-material/RefreshRounded';
import { styled } from '@mui/material';
import { useRecoilState, useRecoilValue, useSetRecoilState } from 'recoil';

import swal from 'sweetalert';
import postsAtom from 'states/posts';
import userAtom from 'states/users';
import authAtom from 'states/auth';
import createimageAtom from 'states/createimage';
import updateimageAtom from 'states/updateimage';
import createpreviewAtom from 'states/createpreview';
import updatepreviewAtom from 'states/updatepreview';
import searchhashtagAtom from 'states/searchhashtag';

import { SearchBar, UploadImage } from 'components';
import useFetch from 'utils/fetch';
import SocialPost from 'components/social/SocialPost';
import searchpostsAtom from 'states/searchposts';
import { profiles } from 'utils/utils';

function SocialFeed() {
  const StyledRefreshRoundedIcon = styled(RefreshRoundedIcon, {
    name: 'StyledRefreshRoundedIcon',
    slot: 'Wrapper',
  })({
    color: '#0F1419',
    fontSize: 26,
    '&:hover': { color: '#1f90fe' },
  });
  const StyledCheckRoundedIcon = styled(CheckRoundedIcon, {
    name: 'StyledCheckRoundedIcon',
    slot: 'Wrapper',
  })({
    color: '#ffffff',
    fontSize: 26,
    '&:hover': { color: '#1f90fe' },
  });

  const fetchData = useFetch();
  const navigate = useNavigate();

  const [postText, setPostText] = useState('');
  const [hashTag, setHashTag] = useState('');
  const [hashTags, setHashTags] = useState([]);
  const [isFetching, setIsFetching] = useState(false);
  const [isLast, setIsLast] = useState(false);
  const [page, setPage] = useState(999999);

  const auth = useRecoilValue(authAtom);
  const updateFilePreview = useRecoilValue(updatepreviewAtom);
  const [userLogin, setUser] = useRecoilState(userAtom);
  const [posts, setPosts] = useRecoilState(postsAtom);
  const setCreateFilePreview = useSetRecoilState(createpreviewAtom);
  const setUpdateFilePreview = useSetRecoilState(updatepreviewAtom);
  const [searchPosts, setSearchPosts] = useRecoilState(searchpostsAtom);
  const [searchSocialData, setSearchSocialData] =
    useRecoilState(searchhashtagAtom);
  const [createUploadedImage, setCreateUploadedImage] =
    useRecoilState(createimageAtom);
  const [updateUploadedImage, setUpdateUploadedImage] =
    useRecoilState(updateimageAtom);

  useEffect(() => {
    if (!auth || !Object.keys(auth).length) {
      setUser(null);
      navigate('/login');
      return;
    }
    setSearchSocialData([]);
    setSearchPosts([]);
  }, []);

  const onPostTextChange = e => {
    setPostText(e.currentTarget.value);
  };
  const handleRefresh = () => {
    window.location.reload();
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

  const readPosts = useCallback(async () => {
    try {
      const response = await fetchData.get(
        `/board/all/inf?currentUserEmail=${
          userLogin.userEmail
        }&lastPostId=${page}&size=${5}`,
      );
      const { boards, last } = response;
      setPage(boards[boards.length - 1].boardId);
      setIsLast(last);
      setIsFetching(false);
      setPosts([...posts, ...boards]);
    } catch (error) {
      throw new Error(error);
    }
  }, [page]);

  const createPost = async createPostText => {
    if (
      !createPostText.trim() &&
      hashTags?.length === 0 &&
      createUploadedImage?.length === 0
    ) {
      swal('게시글 내용을 써주세요!');
      return;
    }
    const boardRequestDto = {
      userEmail: userLogin.userEmail,
      boardContent: createPostText,
    };

    const hashTagRequestDto = {
      hashTagNames: hashTags,
    };

    const formData = new FormData();

    formData.append(
      'boardRequestDto',
      new Blob([JSON.stringify(boardRequestDto)], {
        type: 'application/json',
      }),
    );

    formData.append(
      'hashTagRequestDto',
      new Blob([JSON.stringify(hashTagRequestDto)], {
        type: 'application/json',
      }),
    );

    if (Array.isArray(createUploadedImage)) {
      createUploadedImage?.forEach(image => {
        formData.append('file', image);
      });
    }

    try {
      const response = await fetchData.post('/board/save', formData, 'image');
      response.comments = response.comments || [];
      setPosts(prevPosts => [response, ...prevPosts]);
      setPostText('');
      setCreateUploadedImage([]);
      setCreateFilePreview([]);
      setHashTag('');
      setHashTags([]);
    } catch (error) {
      throw new Error(error);
    }
  };

  const updatePosts = (prevPosts, post, currentText, currentHashTags) => {
    return prevPosts.map(prevPost =>
      prevPost.boardId === post.boardId
        ? {
            ...prevPost,
            boardContent: currentText,
            hashTags: currentHashTags,
            photoUrls: [...prevPost.photoUrls, ...updateFilePreview],
          }
        : prevPost,
    );
  };

  const updatePost = async (post, currentText, currentHashTags, search) => {
    const boardRequestDto = {
      userEmail: userLogin.userEmail,
      boardContent: currentText,
    };

    const hashTagRequestDto = {
      hashTagNames: currentHashTags,
    };

    const formData = new FormData();

    formData.append(
      'boardRequestDto',
      new Blob([JSON.stringify(boardRequestDto)], {
        type: 'application/json',
      }),
    );
    formData.append(
      'hashTagRequestDto',
      new Blob([JSON.stringify(hashTagRequestDto)], {
        type: 'application/json',
      }),
    );
    updateUploadedImage.forEach(image => {
      formData.append('file', image);
    });

    try {
      await fetchData.post(`/board/${post.boardId}`, formData, 'image');

      if (search !== 'search') {
        setPosts(prevPosts =>
          updatePosts(prevPosts, post, currentText, currentHashTags),
        );
      } else {
        setSearchPosts(prevPosts =>
          updatePosts(prevPosts, post, currentText, currentHashTags),
        );
      }

      swal('게시글이 수정되었습니다.');
      setUpdateUploadedImage([]);
      setUpdateFilePreview([]);
    } catch (error) {
      throw new Error(error);
    }
  };

  const deletePost = async (currentPostId, search) => {
    const sendBE = {
      userEmail: userLogin.userEmail,
    };
    try {
      await fetchData.delete(`/board/${currentPostId}`, sendBE);
      if (search !== 'search') {
        setPosts(prevPosts =>
          prevPosts.filter(post => post.boardId !== currentPostId),
        );
      } else {
        setSearchPosts(prevPosts =>
          prevPosts.filter(post => post.boardId !== currentPostId),
        );
      }
      swal('게시글이 삭제되었습니다.');
    } catch (error) {
      throw new Error(error);
    }
  };

  const onSubmitNewPost = e => {
    e.preventDefault();
    createPost(postText);
  };

  useEffect(() => {
    if (!auth || !Object.keys(auth).length) {
      setUser(null);
      navigate('/login');
    }
    const handleScroll = () => {
      const { scrollTop, offsetHeight } = document.documentElement;
      if (
        scrollTop + offsetHeight >=
        document.documentElement.scrollHeight - 500
      ) {
        setIsFetching(true);
      }
    };
    setIsFetching(true);
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  useEffect(() => {
    if (isFetching && !isLast) {
      readPosts();
    } else if (isLast) {
      setIsFetching(false);
    }
  }, [isFetching]);

  return (
    <div className="basis-1/2 min-w-[400px] rounded-lg flex flex-col gap-4">
      <SearchBar page="소통하기" />
      {searchSocialData[0] && (
        <div className="rounded-xl bg-white w-full h-fill flex flex-col items-center justify-center text-[1rem] text-black">
          <div className="flex flex-col gap-5 w-full my-4">
            <div className="flex justify-between w-full">
              <div className="font-semibold text-[1.25rem] mx-6">
                {`' ${searchSocialData[1]} ' 검색결과`}
              </div>
              <div className="mx-6">
                <div>{searchPosts.length}개</div>
              </div>
            </div>
            {searchPosts.map(s => (
              <SocialPost
                key={s.boardId}
                post={s}
                updatePost={updatePost}
                deletePost={deletePost}
                setPosts={setSearchPosts}
                search="search"
              />
            ))}
          </div>
        </div>
      )}

      <div className="rounded-xl bg-white w-full h-fill flex flex-col items-center justify-center text-[1rem] text-black">
        <div className="flex flex-col gap-5 w-full my-4">
          <div className="flex justify-between w-full">
            <div className="font-semibold text-[1.25rem] mx-6">뉴 피드</div>
            <div className="mx-6">
              <StyledRefreshRoundedIcon onClick={handleRefresh} />
            </div>
          </div>
          <span className="h-[0.06rem] w-full bg-gray2 inline-block" />
          <form
            encType="multipart/form-data"
            onSubmit={onSubmitNewPost}
            className="relative flex pb-12 flex-col px-[1rem] items-between justify-between"
          >
            <div className="flex items-start space-between">
              <div
                className={`${
                  userLogin?.userRing?.itemColor && 'border-solid border-[4px]'
                } w-[3rem] h-[3rem] overflow-hidden pr-2 rounded-full`}
                style={{
                  borderColor: `#${
                    userLogin?.userRing?.itemColor
                      ? userLogin?.userRing?.itemColor
                      : ''
                  }`,
                }}
              >
                <img
                  className="rounded-full w-[3rem] h-[3rem] overflow-hidden object-cover"
                  alt=""
                  src={userLogin ? userLogin?.userProfileImg : profiles}
                />
              </div>
              <div className="w-full flex flex-col mr-[4rem] gap-2 justify-between">
                <textarea
                  onChange={onPostTextChange}
                  value={postText}
                  name="post"
                  cols="80"
                  rows="5"
                  placeholder="자유롭게 이야기 해보세요!"
                  className="resize-none font-medium w-full text-black mx-4 rounded-xl p-4 border-solid border-[2px] border-gray2 focus:outline-none focus:border-dodgerblue font-pretendard text-base"
                  onKeyDown={e => {
                    if (e.key === 'Enter') {
                      e.preventDefault();
                    }
                  }}
                />
                <div className="w-full">
                  <input
                    onChange={onHashTagChange}
                    value={hashTag}
                    name="hashTag"
                    placeholder="해시태그 입력 후 스페이스 바를 누르세요"
                    className="font-medium w-full text-black mx-4 rounded-xl p-4 border-solid border-[2px] border-gray2 focus:outline-none focus:border-dodgerblue font-pretendard text-base"
                    onKeyDown={e => {
                      if (e.key === 'Enter') {
                        e.preventDefault();
                      }
                    }}
                  />
                  <div className="flex gap-2 ml-4 py-2 max-w-[46rem] w-full flex-wrap">
                    {hashTags?.map(tag => (
                      <div
                        role="presentation"
                        key={tag}
                        onClick={() => removeHashTag(tag)}
                        className="text-sm cursor-pointer px-3 py-2 w-fit bg-gray2 rounded-xl whitespace-nowrap"
                      >
                        # {tag}
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
            <UploadImage page="소통하기" />
            <button
              type="submit"
              className="hover:bg-lightblue absolute right-4 bottom-0 cursor-pointer rounded-full text-[1rem] w-[1.2rem] h-[0rem] text-white bg-dodgerblue border-solid border-[2px] border-dodgerblue flex py-[1rem] px-[1.4rem] ml-[0.4rem] mr-[1rem] items-center justify-center opacity-[1]"
            >
              <StyledCheckRoundedIcon />
            </button>
          </form>
          {posts?.map(p => {
            return (
              <div key={p.boardId}>
                <SocialPost
                  post={p}
                  updatePost={updatePost}
                  deletePost={deletePost}
                  setPosts={setPosts}
                />
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

export default SocialFeed;
