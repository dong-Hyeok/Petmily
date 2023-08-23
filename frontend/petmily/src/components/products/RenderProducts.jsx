import { useNavigate } from 'react-router-dom';

import ArrowForwardIosRoundedIcon from '@mui/icons-material/ArrowForwardIosRounded';
import { styled } from '@mui/material';
import { bool, string, arrayOf, shape } from 'prop-types';
import { v4 as uuidv4 } from 'uuid';
import { icons, priceToString } from 'utils/utils';
import bannerDog from 'static/images/bannerDog.png';

function RenderProducts({ category, showMore, renderData }) {
  const navigation = useNavigate();

  const StyledArrowForwardIosRoundedIcon = styled(ArrowForwardIosRoundedIcon, {
    name: 'StyledArrowForwardIosRoundedIcon',
    slot: 'Wrapper',
  })({});
  const handleShowMoreClick = clickedCategory => {
    navigation(`/product/category/${clickedCategory}`, {
      state: {
        productCategory: clickedCategory,
      },
    });
  };

  if (!renderData) {
    throw new Error('데이터 없음');
  }

  return (
    <div className="relative top-0 min-w-[1340px] max-w-full flex flex-col items-start justify-start gap-[2.25rem] mb-5 mt-5">
      <div className="self-stretch flex flex-row items-center justify-between">
        <div className="rounded-31xl bg-white text-dodgerblue border-solid border-2 border-dodgerblue overflow-hidden flex flex-row py-2.5 px-5 items-center justify-start gap-[0.5rem]">
          <img
            src={icons[category]}
            alt=""
            className="relative w-[38px] h-auto"
          />
          <div className="relative tracking-[0.01em] leading-[125%] font-extrabold px-2 rounded-xl text-xl">
            {category}
          </div>
        </div>
        {showMore ? (
          <div
            className="rounded-31xl overflow-hidden flex flex-row py-2.5 px-5 items-center bg-white gap-[12px] text-[1.25rem] text-dodgerblue border-[2px] border-solid cursor-pointer"
            role="presentation"
            onClick={() => {
              handleShowMoreClick(category);
            }}
          >
            <b className="relative tracking-[0.01em] leading-[125%] text-dodgerblue">
              더보기
            </b>
            <StyledArrowForwardIosRoundedIcon className="text-dodgerblue" />
          </div>
        ) : null}
      </div>
      <div className="min-w-[1340px] flex-wrap max-w-full w-full flex flex-row items-start justify-start gap-[24.96px] text-[1rem] text-gray">
        {showMore
          ? renderData?.slice(0, 5).map(ele => {
              return (
                <div
                  key={uuidv4()}
                  className="relative flex-1 max-w-[560px] min-w-[250px] h-[450px] rounded-11xl bg-white overflow-hidden flex flex-col pt-0 px-0 pb-6 items-center justify-center gap-[16px]"
                >
                  <a
                    href={ele.productUrl}
                    className="w-fit h-fit no-underline text-black"
                    target="_blank"
                    rel="noreferrer"
                  >
                    <img
                      className="relative w-full object-fill"
                      alt=""
                      src={ele.productImg || bannerDog}
                    />
                    <div className="flex flex-col items-start justify-center gap-[16px] p-4 w-fit">
                      <div className="flex flex-row items-center justify-center gap-[12px]">
                        <div className="relative tracking-[0.01em] leading-[125%] font-medium flex items-start">
                          {ele.productName.replace(/<\/?b>/g, '')}
                        </div>
                      </div>
                      <div className="relative text-3xl tracking-[0.01em] leading-[125%] text-dodgerblue flex items-center">
                        {priceToString(ele.productPrice)} 원
                      </div>
                    </div>
                  </a>
                </div>
              );
            })
          : renderData?.map(ele => (
              <div
                key={uuidv4()}
                className="relative flex-1 max-w-[350px] min-w-[250px] h-[450px] rounded-11xl bg-white overflow-hidden flex flex-col pt-0 px-0 pb-6 items-center justify-center gap-[16px]"
              >
                <a
                  href={ele.productUrl}
                  className="w-fit h-fit no-underline text-black"
                  target="_blank"
                  rel="noreferrer"
                >
                  <img
                    className="relative w-full object-fill"
                    alt=""
                    src={ele.productImg || bannerDog}
                  />
                  <div className="flex flex-col items-start justify-center gap-[16px] p-4 w-fit">
                    <div className="flex flex-row items-center justify-center gap-[12px]">
                      <div className="relative tracking-[0.01em] leading-[125%] font-medium flex items-start">
                        {ele.productName.replace(/<\/?b>/g, '')}
                      </div>
                    </div>
                    <div className="relative text-3xl tracking-[0.01em] leading-[125%] text-dodgerblue flex items-center">
                      {priceToString(ele.productPrice)} 원
                    </div>
                  </div>
                </a>
              </div>
            ))}
      </div>
    </div>
  );
}

const rederDataType = shape({
  productName: 'string',
  productPrice: 'string',
  productUrl: 'string',
  productCategory: 'string',
  productImg: 'string',
});

RenderProducts.propTypes = {
  category: string,
  showMore: bool,
  renderData: arrayOf(rederDataType),
};
export default RenderProducts;
