import { useLocation } from 'react-router';
import { useRecoilValue } from 'recoil';

import { RenderProducts } from 'components';
import productAtom from 'states/products';

function ProductCategory() {
  const location = useLocation();
  const { productCategory } = location.state;
  const product = useRecoilValue(productAtom);

  return (
    <div className="absolute top-20 min-w-[1340px] max-w-full flex flex-1 flex-col items-center justify-center text-left text-[1.13rem] text-darkgray font-pretendard">
      <div className="min-w-[1340px] max-w-full relative text-[1.75rem] text-gray">
        <div className=" flex p-[40px] flex-col items-start justify-start text-[1.5rem] text-white">
          <div className="h-10" />

          <RenderProducts
            category={productCategory}
            showMore={false}
            renderData={product[productCategory]}
          />
        </div>
      </div>
    </div>
  );
}

export default ProductCategory;
