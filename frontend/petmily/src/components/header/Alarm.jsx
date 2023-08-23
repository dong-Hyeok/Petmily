import { useEffect, useState } from 'react';
import { useRecoilValue } from 'recoil';
import userAtom from 'states/users';
import useFetch from 'utils/fetch';
import { formatDate, profiles } from 'utils/utils';

function Alarm() {
  const fetchData = useFetch();

  const [alarm, setAlarm] = useState([]);

  const userLogin = useRecoilValue(userAtom);

  const readAlarm = async () => {
    try {
      const response = await fetchData.get(`/noti/${userLogin.userEmail}`);
      setAlarm(response);
    } catch (error) {
      throw new Error(error);
    }
  };

  useEffect(() => {
    readAlarm();
  }, []);

  return (
    <div className="font-pretendard text-base p-5 flex flex-col gap-4 w-[22rem] h-[35rem] rounded-xl bg-dodgerblue absolute top-[105px] right-[220px]">
      <div className="text-xl text-white">알림</div>
      <div className="overflow-scroll flex flex-col gap-4">
        {alarm?.map(a => {
          return (
            <div
              key={a.id}
              className="relative py-4 flex justify-start items-start gap-4 bg-white rounded-xl w-full h-fill"
            >
              <div className="pl-4">
                <img
                  className="h-[2.5rem] w-[2.5rem] rounded-full object-cover"
                  src={a.fromUserProfileImg ? a.fromUserProfileImg : profiles}
                  alt=""
                />
              </div>
              <div className="pr-4 whitespace-normal text-sm break-all flex flex-col gap-2">
                <div className="flex whitespace-nowrap gap-1 items-center">
                  <div className="text-black font-semibold">
                    {a.fromUserNickname}
                  </div>
                  <div className="text-darkgray font-medium">
                    님이 보낸 알림이 있습니다.
                  </div>
                </div>
                <div className="flex whitespace-wrap gap-1 items-center">
                  <div className="text-slategray font-semibold">
                    {formatDate(a.createDate)}
                  </div>
                  <div className="text-darkgray font-medium">
                    에 새로운 {a.notiType === 'LIKE' && '좋아요가'}
                    {a.notiType === 'COMMENT' && '댓글이'}
                    {a.notiType === 'FOLLOW' && '팔로우가'} 생겼습니다.
                  </div>
                </div>
              </div>
              {!a.checked && (
                <div className="absolute top-0">
                  <div className="bg-red h-3.5 w-3.5 rounded-full" />
                </div>
              )}
            </div>
          );
        })}
      </div>
      {alarm.length === 0 && (
        <div className="text-white w-fulls mt-[10rem] text-center">
          알림이 없습니다.
        </div>
      )}
    </div>
  );
}

export default Alarm;
