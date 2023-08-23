import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { styled } from '@mui/material';
import SendRoundedIcon from '@mui/icons-material/SendRounded';
import CloseRoundedIcon from '@mui/icons-material/CloseRounded';

import { Stomp } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

import { useRecoilState, useRecoilValue, useSetRecoilState } from 'recoil';
import userAtom from 'states/users';
import chatAtom from 'states/chat';
import authAtom from 'states/auth';
import chatmessagesAtom from 'states/chatmessages';
import { profiles } from 'utils/utils';
import ChatMessage from './ChatMessage';

function Chat() {
  const StyledSendRoundedIcon = styled(SendRoundedIcon, {
    name: 'StyledSendRoundedIcon',
    slot: 'Wrapper',
  })({
    color: '#A6A7AB',
    fontSize: 22,
    '&:hover': { color: '#1f90fe' },
  });
  const StyledCloseRoundedIcon = styled(CloseRoundedIcon, {
    name: 'StyledCloseRoundedIcon',
    slot: 'Wrapper',
  })({
    color: '#A6A7AB',
    fontSize: 22,
    '&:hover': { color: '#1f90fe' },
  });

  const navigate = useNavigate();

  const [messageTexts, setMessageTexts] = useState('');
  const [stompClient, setStompClient] = useState('');

  const auth = useRecoilValue(authAtom);
  const chatId = useRecoilValue(chatAtom);
  const [userLogin, setUser] = useRecoilState(userAtom);
  const setMessages = useSetRecoilState(chatmessagesAtom);

  const connectChat = async () => {
    const socket = new SockJS('http://i9d209.p.ssafy.io:8081/chatting');
    const client = Stomp.over(() => socket);

    client.onConnect = () => {
      client.subscribe(`/sub/room/${chatId[1]}`, message => {
        setMessages([]);
        const parsedMessage = JSON.parse(message.body);
        setMessages({
          id: chatId[1],
          writer: parsedMessage.writer,
          message: parsedMessage.message,
        });
      });
    };

    client.activate();
    setStompClient(client);
  };

  useEffect(() => {
    if (!auth || !Object.keys(auth).length) {
      setUser(null);
      navigate('/login');
    }
    connectChat();
    return () => {
      if (stompClient) {
        stompClient.deactivate();
      }
    };
  }, [chatId]);

  const handleMessageChange = e => {
    setMessageTexts(e.target.value);
  };

  const handleSendMessage = messageInput => {
    if (!messageInput.trim()) {
      return;
    }
    const sendBE = {
      roomId: chatId[1],
      writer: userLogin.userEmail,
      message: messageInput,
    };
    if (stompClient) {
      stompClient.send('/pub/message', {}, JSON.stringify(sendBE));
    }

    setMessageTexts('');
  };

  const handleCloseChat = () => {
    navigate('/social');
  };

  return (
    <div className="pb-6 pt-2 basis-1/2 min-w-[400px] h-[700px] rounded-xl bg-white flex flex-col justify-between text-black font-pretendard">
      <div className="w-full h-full text-base flex flex-col justify-center gap-4">
        <div className="mt-4 flex-none w-full flex flex-row items-center justify-between">
          <div className="ml-6 w-full flex items-center gap-4">
            <img
              className="h-10 w-10 rounded-full overflow-hidden object-cover"
              alt=""
              src={chatId[2]}
            />
            <div className="text-2lg font-bold">{chatId[3]}</div>
          </div>
          <StyledCloseRoundedIcon
            className="mr-6"
            role="presentation"
            onClick={handleCloseChat}
          />
        </div>
        <div className="my-2 mx-4 flex-none bg-slate-200 w-fill h-[1.5px]" />
        <div className="justify-end border-solid border-[1px] rounded-xl border-slate-300 mx-4 grow flex flex-col w-fill h-[300px] mb-2 overflow-scroll overflow-x-hidden">
          <div className="flex flex-col-reverse overflow-y-auto">
            <ChatMessage />
          </div>
        </div>
        <div className="mb-2 mx-8 flex-none gap-[0.5rem] flex justify-end items-center h-fit w-fill">
          <div className="relative w-full border-solid border-[1px] border-slate-300 flex items-center justify-between rounded-11xl bg-white max-w-full h-[3rem]">
            <div className="absolute left-0 px-[0.6rem] h-[2rem] w-[2rem] rounded-full overflow-hidden">
              <img
                src={userLogin ? userLogin.userProfileImg : profiles}
                className="h-full w-full rounded-full overflow-hidden"
                alt=""
              />
            </div>
            <input
              className="focus:outline-none w-full h-auto py-[0.8rem] px-[3.5rem] focus:outline-dodgerblue focus:border-1.5 font-pretendard text-base
    lex items-center font-medium rounded-full"
              placeholder="메세지를 입력하세요"
              onChange={e => handleMessageChange(e)}
              value={messageTexts}
              onKeyUp={e => {
                if (e.key === 'Enter') {
                  handleSendMessage(messageTexts);
                }
              }}
            />
            <StyledSendRoundedIcon
              className="absolute right-0 px-[1rem]"
              onClick={() => handleSendMessage(messageTexts)}
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default Chat;
