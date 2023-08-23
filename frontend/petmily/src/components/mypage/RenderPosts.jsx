import { useEffect, useState } from 'react';

import { string } from 'prop-types';
import swal from 'sweetalert';
import { useRecoilState, useRecoilValue, useSetRecoilState } from 'recoil';
import userAtom from 'states/users';
import updateimageAtom from 'states/updateimage';
import updatepreviewAtom from 'states/updatepreview';
import useFetch from 'utils/fetch';
import SocialPost from 'components/social/SocialPost';

function RenderPosts({ page }) {
  const fetchData = useFetch();
  const userLogin = useRecoilValue(userAtom);
  const [posts, setPosts] = useState([]);

  const setUpdateFilePreview = useSetRecoilState(updatepreviewAtom);
  const [updateUploadedImage, setUpdateUploadedImage] =
    useRecoilState(updateimageAtom);

  const readMyPosts = async () => {
    try {
      if (page === 'my') {
        const postsData = await fetchData.get(
          `/profile/${userLogin.userEmail}/writeboard?currentUser=${userLogin.userEmail}`,
        );
        setPosts(postsData);
      }
      if (page === 'like') {
        const postsData = await fetchData.get(
          `/profile/${userLogin.userEmail}/likeboard?currentUser=${userLogin.userEmail}`,
        );
        setPosts(postsData);
      }
    } catch (error) {
      throw new Error(error);
    }
  };

  const updatePost = async (post, currentText, currentHashTags) => {
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
      setPosts(prevPosts =>
        prevPosts.map(prevPost =>
          prevPost.boardId === post.boardId
            ? {
                ...prevPost,
                boardContent: currentText,
                hashTags: currentHashTags,
              }
            : prevPost,
        ),
      );
      swal('게시글이 수정되었습니다.');
      setUpdateUploadedImage([]);
      setUpdateFilePreview([]);
    } catch (error) {
      throw new Error(error);
    }
  };

  const deletePost = async currentPostId => {
    const sendBE = {
      userEmail: userLogin.userEmail,
    };
    try {
      await fetchData.delete(`/board/${currentPostId}`, sendBE);
      setPosts(prevPosts =>
        prevPosts.filter(post => post.boardId !== currentPostId),
      );
      swal('게시글이 삭제되었습니다.');
    } catch (error) {
      throw new Error(error);
    }
  };

  useEffect(() => {
    readMyPosts();
  }, [page]);

  return (
    <div className="here w-full h-full flex flex-col items-center bg-white gap-5 py-1">
      {posts.length > 0 ? (
        posts?.map(p => {
          return (
            <div key={p.boardId} className="w-full">
              <SocialPost
                key={p}
                post={p}
                updatePost={updatePost}
                deletePost={deletePost}
                setPosts={setPosts}
              />
            </div>
          );
        })
      ) : (
        <div>
          <p className=" font-bold">
            {page === 'my' ? '작성한' : '좋아요한'} 게시글이 없습니다.
          </p>
        </div>
      )}
    </div>
  );
}

RenderPosts.propTypes = {
  page: string,
};

export default RenderPosts;
