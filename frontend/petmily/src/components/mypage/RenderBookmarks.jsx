import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import BookmarkIcon from '@mui/icons-material/Bookmark';
import { styled } from '@mui/material';
import { useRecoilState } from 'recoil';
import { v4 as uuidv4 } from 'uuid';
import swal from 'sweetalert';

import userAtom from 'states/users';
import useFetch from 'utils/fetch';

function RenderBookmarks() {
  const StyledBookmarkIcon = styled(BookmarkIcon, {
    name: 'StyledBookmarkIcon',
    slot: 'Wrapper',
  })({
    fontSize: 35,
  });
  const navigation = useNavigate();
  const fetchData = useFetch();
  const [userInfo, setUserInfo] = useRecoilState(userAtom);
  const [userBookmarks, setUserBookmarks] = useState([]);
  const handleBookmark = useCallback(
    async curationId => {
      if (!userInfo || !Object.keys(userInfo).length) {
        navigation('/login');
        swal('로그인이 필요합니다.');
      }
      try {
        const message = window.confirm('북마크를 취소하시겠습니까?');
        if (message) {
          await fetchData.post('/curation/bookmarks', {
            userEmail: userInfo.userEmail,
            cid: curationId,
          });
          setUserBookmarks(prev => prev.filter(ele => ele.cid !== curationId));
          setUserInfo({
            ...userInfo,
            bookmarks: userInfo.bookmarks.filter(ele => ele !== curationId),
          });
        }
      } catch (error) {
        throw new Error(error);
      }
    },
    [fetchData, navigation, userInfo],
  );
  useEffect(() => {
    async function fetchBookmarks() {
      try {
        const bookmarks = await fetchData.get(
          `/curation/userbookmarksdetail?userEmail=${userInfo.userEmail}`,
        );
        if (bookmarks.length > 0) {
          setUserBookmarks(bookmarks);
        }
      } catch (error) {
        throw new Error(error);
      }
    }
    fetchBookmarks();
  }, []);
  return (
    <div className="w-full flex flex-col items-center bg-white gap-5 py-1">
      {userBookmarks.length > 0 ? (
        userBookmarks?.map(ele => {
          return (
            <div
              key={uuidv4()}
              className="relative flex-1 w-[95%] h-full p-5 rounded-11xl bg-slate-100 shadow-md overflow-hidden flex flex-col pt-0 px-0 pb-6 items-center justify-center gap-[16px]"
            >
              {/* 유저 북마크 정보랑 비교해서 큐레이션 카드 ID가 북마크 정보 리스트안에 있으면
     TRUE 아니면 FALSE */}
              <StyledBookmarkIcon
                className="absolute bottom-2 right-3 cursor-pointer z-10"
                color="primary"
                onClick={() => {
                  handleBookmark(ele.cid);
                }}
              />
              <a
                href={ele.curl}
                className="w-fit h-fit no-underline text-black"
                target="_blank"
                rel="noreferrer"
              >
                <img
                  className="relative w-full object-fill"
                  alt=""
                  src={ele.cimage}
                />
                <div className="flex flex-col items-start justify-center gap-[16px] p-4 w-fit">
                  <div className="flex flex-row items-center justify-center gap-[12px]">
                    <div className="relative tracking-[0.01em] leading-[125%] font-medium flex items-start">
                      {ele.ctitle}
                    </div>
                  </div>
                  <div className="relative text-[0.88rem] tracking-[0.01em] leading-[125%] text-darkgray flex items-center">
                    {ele.ccontent}
                  </div>
                </div>
              </a>
            </div>
          );
        })
      ) : (
        <div>
          <p className=" font-bold">북마크한 큐레이션이 없습니다.</p>
        </div>
      )}
    </div>
  );
}

export default RenderBookmarks;
