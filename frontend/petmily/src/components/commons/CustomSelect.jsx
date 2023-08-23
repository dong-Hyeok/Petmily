import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import ExpandMoreRoundedIcon from '@mui/icons-material/ExpandMoreRounded';
import PetsIcon from '@mui/icons-material/Pets';
import { styled } from '@mui/material';
import { arrayOf, string } from 'prop-types';
import { useSetRecoilState } from 'recoil';

import headerAtom from 'states/headers';
import selectAtom from 'states/select';
import userAtom from 'states/users';
import authAtom from 'states/auth';

function CustomSelect({ component, select = '', options = [] }) {
  const navigation = useNavigate();
  const [currentValue, setCurrentValue] = useState(select);
  const setCategory = useSetRecoilState(selectAtom);
  const setHeader = useSetRecoilState(headerAtom);
  const setUsers = useSetRecoilState(userAtom);
  const setAuth = useSetRecoilState(authAtom);
  const [isShowOptions, setShowOptions] = useState(false);
  const StyledPetsIcon = styled(PetsIcon, {
    name: 'StyledArrowForwardIosRoundedIcon',
    slot: 'Wrapper',
  })({
    width: '30px',
    height: 'auto',
  });
  const handleClick = option => {
    switch (option) {
      case '상점':
        setHeader('상점');
        navigation('/shop');
        break;
      case '마이페이지':
        setHeader('마이페이지');
        navigation('/mypage');
        break;
      case '로그아웃':
        setUsers(null);
        setAuth(null);
        navigation('/login');
        // 로그아웃 메서드
        break;
      default:
        break;
    }
  };

  return (
    <div
      className={`relative rounded-31xl ${
        component === 'header'
          ? 'bg-white text-black border-solid border-2 border-dodgerblue py-[0.8rem]'
          : 'bg-dodgerblue before:text-white py-[1rem]'
      } flex flex-row w-fit px-[2rem] items-center justify-end cursor-pointer z-[10] 
     `}
      role="presentation"
      onClick={() => {
        setShowOptions(prev => !prev);
      }}
    >
      <ExpandMoreRoundedIcon
        style={{
          position: 'absolute',
          right: '4px',
          color: `${component === 'header' ? 'dodgerblue' : 'white'}`,
          fontSize: '45px',
          cursor: 'pointer',
        }}
      />
      {component === 'header' ? null : (
        <StyledPetsIcon className="absolute top-[0.75rem] left-4" />
      )}
      <label
        htmlFor="id"
        className={`left-12bg-transparent w-[120px] ${
          component === 'header' ? 'text-black' : 'text-white ml-8'
        } appearance-none font-semibold text-2lg whitespace-nowrap cursor-pointer`}
      >
        {currentValue}
      </label>
      <ul
        className={`absolute ${
          component === 'header'
            ? 'top-[3rem] left-0 bg-dodgerblue'
            : 'top-[1.4rem] left-0 bg-white'
        } w-full h-fit ${
          isShowOptions
            ? 'max-h-none border-solid border-2 border-dodgerblue'
            : 'max-h-0'
        } p-0 rounded-[15px] list-none overflow-hidden z-[1]
        }`}
      >
        {options.map(ele => (
          <li
            key={ele}
            role="presentation"
            className={`text-xl ${
              component === 'header'
                ? 'text-white bg-dodgerblue'
                : 'text-dodgerblue bg-white'
            } px-2 py-2 hover hover:brightness-90 transition duration-200 ease-in`}
            onClick={() => {
              if (component === 'curationMore') {
                setCurrentValue(ele);
                setCategory(ele);
              }
              if (component === 'header') {
                handleClick(ele);
              }
              if (component === 'product') {
                setCurrentValue(ele);
                window.location.href = `/product/${ele}`;
              }
            }}
          >
            {ele}
          </li>
        ))}
      </ul>
    </div>
  );
}

CustomSelect.propTypes = {
  component: string,
  select: string,
  options: arrayOf(string),
};

export default CustomSelect;
