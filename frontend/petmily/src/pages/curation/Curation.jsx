import { useEffect, useState } from 'react';
import { useRecoilState, useSetRecoilState } from 'recoil';
import { CircularProgress } from '@mui/material';

import useFetch from 'utils/fetch';
import { RenderCuration } from 'components';
import headerAtom from 'states/headers';
import userAtom from 'states/users';

function Curation() {
  const fetchData = useFetch();
  const [curationDatas, setCurationDatas] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const setClickedHeader = useSetRecoilState(headerAtom);
  const [userInfo, setUserInfo] = useRecoilState(userAtom);

  useEffect(() => {
    setIsLoading(true);
    setClickedHeader('큐레이션');
    const fetchCuration = async () => {
      try {
        const curationData = await fetchData.get(
          '/curation/getNewsData?species=all',
        );
        if (curationData) {
          setCurationDatas(curationData);
          setIsLoading(false);
        }
      } catch (error) {
        throw new Error(error);
      }
    };
    fetchCuration();
  }, []);

  useEffect(() => {
    async function fetchBookmarks() {
      if (!userInfo || Object.keys(userInfo).length <= 0) {
        return;
      }
      try {
        const bookmarks = await fetchData.get(
          `/curation/userbookmarks?userEmail=${userInfo.userEmail}`,
        );
        if (bookmarks.length > 0) {
          setUserInfo({ ...userInfo, bookmarks });
        }
      } catch (error) {
        throw new Error(error);
      }
    }
    fetchBookmarks();
  }, []);

  return (
    <div className="absolute top-0 min-w-[1340px] max-w-full flex flex-1 flex-col items-center justify-center text-left text-[1.13rem] text-darkgray font-pretendard">
      <div className="min-w-[1340px] w-[95%] py-2 px-10 relative text-[1.75rem] text-gray">
        <div className="flex flex-col items-start justify-start text-[1.5rem] text-white">
          <div className="h-48" />
          <b className="text-center self-stretch relative text-13xl tracking-[0.01em] leading-[125%] text-black">
            CURATION
          </b>
          <div className="h-10" />
          {!isLoading ? (
            <>
              <RenderCuration
                category="강아지"
                renderData={curationDatas['강아지']}
              />
              <RenderCuration
                category="고양이"
                renderData={curationDatas['고양이']}
              />
              <RenderCuration
                category="기타동물"
                renderData={curationDatas['기타동물']}
              />
            </>
          ) : (
            <div className="flex w-full mt-20 flex-row justify-center items-center text-darkgray">
              <CircularProgress color="inherit" size={70} />
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default Curation;
