import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { useRecoilState, useRecoilValue, useSetRecoilState } from 'recoil';
import authAtom from 'states/auth';
import chatAtom from 'states/chat';
import chatmessagesAtom from 'states/chatmessages';
import chatroomAtom from 'states/chatroom';
import userAtom from 'states/users';
import useFetch from 'utils/fetch';
import { profiles } from 'utils/utils';

function ChatRoom() {
  const navigate = useNavigate();
  const fetchData = useFetch();

  const auth = useRecoilValue(authAtom);
  const messages = useRecoilValue(chatmessagesAtom);
  const [chatRoom, setChatRoom] = useState(chatroomAtom);
  const [userLogin, setUser] = useRecoilState(userAtom);
  const setChatId = useSetRecoilState(chatAtom);

  const readChatRoom = async () => {
    try {
      const response = await fetchData.get(`/chat/${userLogin.userEmail}`);
      setChatRoom(response);
    } catch (error) {
      throw new Error(error);
    }
  };

  const handleOpenChat = (
    chatEmail,
    chatRoomId,
    chatProfile,
    chatNickname,
    e,
  ) => {
    e.preventDefault();
    if (chatRoom && chatRoom.length > 0) {
      setChatId([chatEmail, chatRoomId, chatProfile, chatNickname]);
      readChatRoom();
    }
    navigate(`/social/chat/${chatRoomId}`);
  };

  useEffect(() => {
    if (!auth || !Object.keys(auth).length) {
      setUser(null);
      navigate('/login');
      return;
    }
    readChatRoom();
  }, [messages]);

  return (
    <div className="sticky top-[6.5rem] right-0 mx-4 basis-1/4 flex h-[700px] rounded-xl bg-white min-w-[20%] flex-col p-[1rem] items-start justify-start gap-[0.38rem] font-pretendard">
      <div className="flex w-full flex-col items-start justify-center gap-[1rem] text-[1.25rem]">
        <div className="ml-1 font-semibold">메세지 목록</div>
        <div className="bg-slate-200 w-full h-[1.5px]" />
      </div>
      <div className="overflow-scroll w-full">
        {userLogin &&
          Array.isArray(chatRoom) &&
          chatRoom?.map(room => {
            return (
              <div
                role="presentation"
                key={
                  room.participants[0].userEmail !== userLogin?.userEmail
                    ? room.participants[0].userId
                    : room.participants[1].userId
                }
                className="self-stretch flex flex-col w-full items-start justify-start gap-[0.63rem]"
                onClick={e => {
                  handleOpenChat(
                    room.participants[0].userEmail !== userLogin?.userEmail
                      ? room.participants[0].userEmail
                      : room.participants[1].userEmail,
                    room.roomId,
                    room.participants[0].userEmail !== userLogin?.userEmail
                      ? room.participants[0].userProfile || profiles
                      : room.participants[1].userProfile || profiles,
                    room.participants[0].userEmail !== userLogin?.userEmail
                      ? room.participants[0].userNickname
                      : room.participants[1].userNickname,
                    e,
                  );
                }}
              >
                <div className="w-full flex flex-row py-[0.75rem] px-[1rem] box-border items-center justify-between">
                  <div className="w-fill gap-4 flex flex-row items-center justify-between">
                    <div className="h-11 w-11 rounded-full">
                      <img
                        className="h-11 w-11 overflow-hidden rounded-full object-cover"
                        alt=""
                        src={
                          room.participants[0].userEmail !==
                          userLogin?.userEmail
                            ? room.participants[0].userProfile || profiles
                            : room.participants[1].userProfile || profiles
                        }
                      />
                    </div>
                    <div className="flex flex-col items-start w-full justify-start gap-[0.3rem]">
                      <b className="">
                        {room.participants[0].userEmail !== userLogin?.userEmail
                          ? room.participants[0].userNickname
                          : room.participants[1].userNickname}
                      </b>
                      <div className="text-[1rem] font-medium text-slategray">
                        {room.latestMessage.length > 10
                          ? `${room.latestMessage.slice(0, 10)}...`
                          : room.latestMessage}
                      </div>
                    </div>
                  </div>
                  {room.unreadMessageCount ? (
                    <div className="rounded-full bg-dodgerblue h-8 w-8 overflow-hidden whitespace-nowrap flex flex-row box-border items-center justify-center text-center text-sm text-white">
                      <b className="">{room.unreadMessageCount}</b>
                    </div>
                  ) : null}
                </div>
                <div className="bg-slate-100 w-full h-[1px]" />{' '}
              </div>
            );
          })}
        {chatRoom.length === 0 && (
          <div className="w-full text-center mt-10 font-bold text-slategray">
            아직 메세지가 없습니다.
          </div>
        )}
      </div>
    </div>
  );
}

export default ChatRoom;
