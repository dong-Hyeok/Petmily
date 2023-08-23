import { useState, useEffect } from 'react';
import { useRecoilValue } from 'recoil';

import { v4 as uuidv4 } from 'uuid';
import userAtom from 'states/users';
import chatAtom from 'states/chat';
import chatmessagesAtom from 'states/chatmessages';
import useFetch from 'utils/fetch';
import { formatDate, profiles } from 'utils/utils';

function ChatMessage() {
  const fetchData = useFetch();

  const userLogin = useRecoilValue(userAtom);
  const chatId = useRecoilValue(chatAtom);
  const [historyMessages, setHistoryMessages] = useState([]);
  const messages = useRecoilValue(chatmessagesAtom);

  const readChatHistory = async () => {
    const sendBE = {
      sender: userLogin.userEmail,
      receiver: chatId[0],
    };
    try {
      const response = await fetchData.post('/chat/history', sendBE);
      setHistoryMessages(response);
    } catch (error) {
      if (error.response.status === 400) {
        throw new Error(error.response.data);
      }
    }
  };

  useEffect(() => {
    readChatHistory();
  }, [messages, chatId]);

  return (
    <div>
      <div className="grow flex flex-col w-full my-2 overflow-scroll justify-between overflow-x-hidden">
        <div className="flex flex-col w-full">
          {Array.isArray(historyMessages) &&
            historyMessages?.map((msg, index) => {
              const lastMessage =
                index > 0 && historyMessages[index - 1].writer === msg.writer;
              return (
                <div className="w-full flex flex-col" key={uuidv4()}>
                  {msg.writer !== userLogin.userEmail && (
                    <div className="flex items-end gap-2">
                      <div className="flex items-start">
                        <div className="px-[0.8rem] h-[2.5rem] w-[2.5rem] mt-2 rounded-full overflow-hidden">
                          {!lastMessage && (
                            <img
                              src={chatId[2]}
                              className="h-full w-full rounded-full overflow-hidden"
                              alt=""
                            />
                          )}
                        </div>
                        <div className="px-4 py-2 bg-gray2 w-fit rounded-2xl my-2">
                          <div className="break-all max-w-[11rem] w-fit text-sm text-black">
                            {msg.message}
                          </div>
                        </div>
                      </div>
                      <div className="text-xs text-slategray my-2">
                        {formatDate(msg.createdAt)}
                      </div>
                    </div>
                  )}
                  {msg.writer === userLogin.userEmail && (
                    <div className="flex justify-end items-end gap-2">
                      <div className="text-xs text-slategray my-2">
                        {formatDate(msg.createdAt)}
                      </div>
                      <div className="flex items-start">
                        <div className="px-4 py-2 bg-gray2 w-fit rounded-2xl my-2">
                          <div className="break-all max-w-[11rem] w-fit text-sm text-black">
                            {msg.message}
                          </div>
                        </div>
                        <div className="px-[0.8rem] h-[2.5rem] w-[2.5rem] mt-2 rounded-full overflow-hidden">
                          {!lastMessage && (
                            <img
                              src={
                                userLogin.userProfileImg
                                  ? userLogin.userProfileImg
                                  : profiles
                              }
                              className="h-full w-full rounded-full overflow-hidden"
                              alt=""
                            />
                          )}
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              );
            })}
        </div>
      </div>
    </div>
  );
}

export default ChatMessage;
