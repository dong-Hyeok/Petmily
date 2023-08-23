import { useEffect, useState } from 'react';

import RefreshRoundedIcon from '@mui/icons-material/RefreshRounded';
import { styled } from '@mui/material';

import { useSetRecoilState, useRecoilValue } from 'recoil';
import followAtom from 'states/follow';
import userAtom from 'states/users';
import useFetch from 'utils/fetch';
import { profiles } from 'utils/utils';

function FollowRecommend() {
  const StyledRefreshRoundedIcon = styled(RefreshRoundedIcon, {
    name: 'StyledRefreshRoundedIcon',
    slot: 'Wrapper',
  })({
    color: '#0F1419',
    fontSize: 26,
    '&:hover': { color: '#1f90fe' },
  });
  const fetchData = useFetch();

  const userLogin = useRecoilValue(userAtom);
  const [followRecommend, setFollowRecommend] = useState([]);
  const setFollowedUsers = useSetRecoilState(followAtom);

  const readFollowRecommend = async () => {
    try {
      const response = await fetchData.get(
        `/follow/recommend/${userLogin.userEmail}`,
      );
      setFollowRecommend(response);
    } catch (error) {
      throw new Error(error);
    }
  };

  const handleFollow = async (followEmail, actionFollow) => {
    setFollowedUsers(prevFollowedUsers => ({
      ...prevFollowedUsers,
      [followEmail]: !prevFollowedUsers[followEmail],
    }));

    const sendBE = {
      userEmail: userLogin.userEmail,
    };
    if (actionFollow === false) {
      try {
        await fetchData.post(`/follow/${followEmail}`, sendBE);
        setFollowRecommend(prevFollowRecommend => {
          return prevFollowRecommend.map(item => {
            if (item.userEmail === followEmail) {
              return {
                ...item,
                followedByCurrentUser: !item.followedByCurrentUser,
              };
            }
            return item;
          });
        });
      } catch (error) {
        throw new Error(error);
      }
    } else if (actionFollow === true) {
      try {
        await fetchData.delete(`/follow/${followEmail}`, sendBE);
        setFollowRecommend(prevFollowRecommend => {
          return prevFollowRecommend.map(item => {
            if (item.userEmail === followEmail) {
              return {
                ...item,
                followedByCurrentUser: !item.followedByCurrentUser,
              };
            }
            return item;
          });
        });
      } catch (error) {
        throw new Error(error);
      }
    }
  };

  useEffect(() => {
    readFollowRecommend();
  }, []);

  return (
    <div className="sticky top-[6.5rem] left-0 mx-4 basis-1/4 flex h-[700px] rounded-xl bg-white min-w-[20%] flex-col p-[1rem] items-start justify-start gap-[0.38rem] font-pretendard">
      <div className="flex w-full flex-col items-start justify-center gap-[1rem] text-[1.25rem]">
        <div className="flex justify-between w-full">
          <div className="font-semibold text-[1.25rem] mx-6">친구 추천</div>
          <div className="mx-6">
            <StyledRefreshRoundedIcon onClick={readFollowRecommend} />
          </div>
        </div>
        <div className="bg-slate-200 w-full h-[1.5px]" />
      </div>
      <div className="w-full overflow-scroll">
        {followRecommend.map(f => {
          return (
            <div
              key={f.userEmail}
              className="self-stretch flex flex-col items-start justify-start gap-[0.63rem]"
            >
              <div className="w-full flex flex-row py-[0.75rem] px-[1rem] box-border items-center justify-between">
                <div className="w-fill gap-4 flex flex-row items-center justify-between">
                  <div className="h-11 w-11 rounded-full overflow-hidden">
                    <img
                      className="h-11 w-11 overflow-hidden object-cover"
                      alt=""
                      src={f.userProfileImg ? f.userProfileImg : profiles}
                    />
                  </div>
                  <div className="flex flex-col items-start justify-start gap-[0.3rem]">
                    <b className="">{f.userNickname}</b>
                    <div className="text-[1rem] font-medium text-lightgray">
                      #{f.userLikePet}
                    </div>
                  </div>
                </div>
                <div
                  role="presentation"
                  className={`${
                    f.followedByCurrentUser
                      ? 'bg-white font-bold'
                      : 'bg-dodgerblue text-white'
                  } text-dodgerblue cursor-pointer border-dodgerblue border-[2px] border-solid ml-2 rounded-full h-7 w-fill px-2.5 overflow-hidden whitespace-nowrap flex flex-row box-border items-center justify-center text-center`}
                  onClick={() =>
                    handleFollow(f.userEmail, f.followedByCurrentUser)
                  }
                >
                  <p className="text-sm">팔로우</p>
                </div>
              </div>
              <div className="bg-slate-200 w-full h-[0.1px]" />
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default FollowRecommend;
