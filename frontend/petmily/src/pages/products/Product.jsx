import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useSetRecoilState } from 'recoil';
import { v4 as uuidv4 } from 'uuid';
import { objectOf, func, string } from 'prop-types';

import selectAtom from 'states/select';
import ProductDog from 'static/images/productDog.svg';
import ProductCat from 'static/images/categoryCat.png';
import ProductEtc from 'static/images/productEtc.svg';

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
function Product() {
  const petCategories = [
    ['강아지', ProductDog],
    ['고양이', ProductCat],
    ['기타동물', ProductEtc],
  ];
  const navigation = useNavigate();
  const setSelect = useSetRecoilState(selectAtom);

  const handleShowItem = async category => {
    setSelect(category);
    navigation(`/product/${category}`);
  };

  return (
    <div
      className="absolute bg-whitesmoke flex flex-col justify-start min-w-[1340px] items-center w-full 
      text-left text-[1.75rem] text-gray font-pretendard"
    >
      <div className="relative h-fit w-[96%]">
        <div className="flex flex-col items-center gap-[7.5rem]">
          <div className="relative tracking-[0.05em] leading-[125%] font-semibold mt-10">
            반려동물 카테고리
          </div>
          <div className="flex flex-row items-start gap-[80px] w-full">
            {petCategories.map(ele => (
              <div
                key={uuidv4()}
                className="relative w-84 h-fit w-full hover:brightness-90"
              >
                <img
                  role="presentation"
                  className="relative left-[0%] rounded-11xl max-w-full w-full h-[350px] max-h-full object-cover cursor-pointer"
                  alt=""
                  src={ele[1]}
                  onClick={() => {
                    handleShowItem(ele[0]);
                  }}
                />
                <div className="relative h-[15%] w-[22%] right-[76%] bottom-[0%] left-[2%]">
                  <b className="relative top-[0%] left-[0%] tracking-[0.01em] leading-[200%] whitespace-nowrap">
                    {ele[0]}
                  </b>
                  <div
                    role="presentation"
                    className="whitespace-nowrap relative left-[0%] text-[1.25rem] tracking-[0.01em] leading-[125%] font-semibold text-dodgerblue cursor-pointer"
                    onClick={() => {
                      handleShowItem(ele[0]);
                    }}
                  >
                    용품 보러가기
                  </div>
                </div>
              </div>
            ))}
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

export default Product;
