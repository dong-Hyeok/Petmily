import React, { useEffect, useState } from 'react';
import { useRecoilValue, useSetRecoilState } from 'recoil';
import { v4 as uuidv4 } from 'uuid';
import { ErrorBoundary } from 'react-error-boundary';
import { func, objectOf, string } from 'prop-types';

import { ProductCarousel, RenderProducts, SearchBar } from 'components';
import selectAtom from 'states/select';
import CustomSelect from 'components/commons/CustomSelect';
import searchAtom from 'states/search';
import productAtom from 'states/products';
import popularsAtom from 'states/populars';
import useFetch from 'utils/fetch';

const productCategories = ['식품', '미용', '건강', '기타'];

function ErrorFallback({ error, resetErrorBoundary }) {
  return (
    <div role="alert">
      <p>Something went wrong:</p>
      <pre style={{ color: 'red' }}>{error.message}</pre>
      <button type="button" onClick={resetErrorBoundary}>
        Try again
      </button>
    </div>
  );
}
function ProductPet() {
  const select = useRecoilValue(selectAtom);
  const altSelect = decodeURIComponent(
    window.location.pathname.split('/').at(-1),
  );
  const searchResult = useRecoilValue(searchAtom);
  const globalProduct = useRecoilValue(productAtom);
  const [isSearch, setIsSearch] = useState(false);
  const setPopularItems = useSetRecoilState(popularsAtom);
  const setGlobalProduct = useSetRecoilState(productAtom);
  const fetchData = useFetch();
  useEffect(() => {
    async function popularItems(productData) {
      try {
        const newPopularItems = [];
        productCategories.forEach(category => {
          newPopularItems.push(productData[category][0]);
        });
        setPopularItems(newPopularItems);
      } catch (error) {
        throw new Error();
      }
    }
    const fetchPetData = async selectPet => {
      try {
        const productData = await fetchData.get(
          `/product/getdata?species=${selectPet}`,
        );
        if (productData && productData['식품'].length > 0) {
          setGlobalProduct({
            식품: productData['식품'],
            건강: productData['건강'],
            미용: productData['미용'],
            기타: productData['기타'],
          });
        }
        popularItems(productData);
      } catch (error) {
        throw new Error(error);
      }
    };
    fetchPetData(altSelect);
  }, []);

  return (
    <div className="absolute min-w-[1340px] top-[60px] max-w-full flex flex-1 flex-col items-center justify-center text-left text-[1.13rem] text-darkgray font-pretendard">
      <div className="min-w-[1340px] max-w-full w-full relative text-[1.75rem] text-gray">
        <div className=" flex flex-col items-start justify-start text-[1.5rem] text-white">
          <ErrorBoundary FallbackComponent={ErrorFallback}>
            <ProductCarousel />
          </ErrorBoundary>
          <div className="h-10" />

          <div className="h-10" />
          <div className="p-8 w-[96%] min-w-[1340px] max-w-full">
            <div className="flex flex-row justify-around items-center w-full h-auto mt-20 mb-20">
              <div className="relative flex gap-5 justify-start flex-row w-full h-auto">
                <CustomSelect
                  component="product"
                  select={select.length > 0 ? select : altSelect}
                  options={['강아지', '고양이', '기타동물']}
                />
              </div>
              <SearchBar
                page="최저가"
                petCategory={select.length > 0 ? select : altSelect}
                setIsSearch={setIsSearch}
              />
            </div>
            {isSearch ? (
              <RenderProducts category="검색" renderData={searchResult} />
            ) : (
              globalProduct &&
              productCategories.map(category => {
                return (
                  <div key={uuidv4()}>
                    {globalProduct.category !== null ? (
                      <RenderProducts
                        category={category}
                        showMore
                        renderData={globalProduct[category]}
                      />
                    ) : (
                      <div>카테고리 데이터가 없습니다.</div>
                    )}
                  </div>
                );
              })
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

ErrorFallback.propTypes = {
  error: objectOf(string),
  resetErrorBoundary: func,
};

export default ProductPet;
