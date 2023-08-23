import { useCallback, useState } from 'react';

import SearchIcon from '@mui/icons-material/Search';
import { styled } from '@mui/material';
import { string, func } from 'prop-types';
import { useRecoilValue, useSetRecoilState } from 'recoil';
import useFetch from 'utils/fetch';
import searchAtom from 'states/search';
import searchhashtagAtom from 'states/searchhashtag';
import searchpostsAtom from 'states/searchposts';
import userAtom from 'states/users';

function SearchBar({ page, petCategory, setIsSearch }) {
  const [inputSearch, setInputSearch] = useState('');
  const userLogin = useRecoilValue(userAtom);
  const setSearchData = useSetRecoilState(searchAtom);
  const setSearchSocialData = useSetRecoilState(searchhashtagAtom);
  const setSearchPosts = useSetRecoilState(searchpostsAtom);
  const StyledSearchIcon = styled(SearchIcon, {
    name: 'StyledSearchIcon',
    slot: 'Wrapper',
  })({
    color: '#5b7083',
    fontSize: 26,
    '&:hover': { color: '#1f90fe' },
  });
  const fetchSearchResult = useFetch();
  const handleSearch = useCallback(async () => {
    try {
      const fetchData = await fetchSearchResult.get(
        `/product/search/${petCategory} ${inputSearch}`,
      );
      setIsSearch(true);
      fetchData.sort((a, b) => a.productPrice - b.productPrice);
      setSearchData(fetchData);
    } catch (error) {
      throw new Error(error);
    }
  }, [fetchSearchResult, inputSearch, petCategory, setIsSearch, setSearchData]);

  const handleSearchHashTag = useCallback(async () => {
    if (!inputSearch.trim()) {
      return;
    }
    try {
      const fetchData = await fetchSearchResult.get(
        `/board/search/${inputSearch}?currentUser=${userLogin.userEmail}`,
      );
      setSearchSocialData([true, inputSearch]);
      setSearchPosts(fetchData);
    } catch (error) {
      throw new Error(error);
    }
  }, [inputSearch, setSearchSocialData, setSearchPosts]);

  return page !== '소통하기' ? (
    <div
      className={`relative flex flex-row justify-between items-center ${
        page === '최저가' ?? 'top-[100px]'
      } rounded-11xl bg-white w-[760px] h-[60px]`}
    >
      <input
        className="outline-none w-full rounded-[100px] h-auto py-5 px-4 border-[1.5px] border-solid border-darkgray 
        focus:border-dodgerblue focus:border-[1.5px] font-pretendard text-base
        relative tracking-[0.01em] leading-[125%]"
        placeholder="검색어를 입력하세요"
        value={inputSearch}
        onChange={e => setInputSearch(e.target.value)}
        onKeyUp={e => {
          if (e.key === 'Enter') {
            handleSearch();
          }
        }}
      />
      <SearchIcon
        className="absolute right-4 z-[1] cursor-pointer text-darkgray"
        fontSize="large"
        onClick={() => {
          handleSearch();
        }}
      />
    </div>
  ) : (
    <div className="relative flex items-center justify-between rounded-11xl bg-white max-w-full h-[60px]">
      <input
        className=" focus:outline-none w-full h-auto focus:outline-dodgerblue py-[1rem] pl-[2rem] pr-[5rem] focus:border-1.5 font-pretendard text-base
        lex items-center font-medium rounded-full"
        placeholder="해시태그를 검색하세요"
        value={inputSearch}
        onChange={e => setInputSearch(e.target.value)}
        onKeyUp={e => {
          if (e.key === 'Enter') {
            handleSearchHashTag();
          }
        }}
      />
      <StyledSearchIcon
        className="absolute right-0  px-[1.5rem]"
        onClick={() => {
          handleSearchHashTag();
        }}
      />
    </div>
  );
}
SearchBar.propTypes = {
  page: string,
  petCategory: string,
  setIsSearch: func,
};
export default SearchBar;
