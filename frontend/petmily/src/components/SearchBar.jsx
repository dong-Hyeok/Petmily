import { useCallback, useState } from 'react';

import SearchIcon from '@mui/icons-material/Search';
import { styled } from '@mui/material';
import { string, func } from 'prop-types';
import { useSetRecoilState } from 'recoil';
import useFetch from 'utils/fetch';
import searchAtom from 'states/search';

function SearchBar({ page, petCategory, setIsSearch }) {
  const [inputSearch, setInputSearch] = useState('');
  const setSearchData = useSetRecoilState(searchAtom);
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
        `search/${petCategory} ${inputSearch}`,
      );
      setIsSearch(true);
      console.log('searchBar', fetchData);
      fetchData.sort((a, b) => a.productPrice - b.productPrice);
      setSearchData(fetchData);
    } catch (error) {
      console.log(error);
    }
  }, [fetchSearchResult, inputSearch, petCategory, setIsSearch, setSearchData]);
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
      />
      <SearchIcon
        className="absolute right-4 z-[1] cursor-pointer text-darkgray"
        fontSize="large"
        onClick={() => {
          console.log('click');
          handleSearch();
        }}
      />
    </div>
  ) : (
    <div className="relative flex items-center justify-between rounded-11xl bg-white max-w-full h-[60px]">
      <input
        className=" focus:outline-none w-full h-auto focus:outline-dodgerblue py-[1rem] pl-[2rem] pr-[5rem] focus:border-1.5 font-pretendard text-base
        lex items-center font-medium rounded-full"
        placeholder="검색어를 입력하세요"
        value={inputSearch}
        onChange={e => setInputSearch(e.target.value)}
      />
      <StyledSearchIcon
        className="absolute right-0  px-[1.5rem]"
        onClick={() => {
          handleSearch();
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
