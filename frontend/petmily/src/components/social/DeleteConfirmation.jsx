import { bool, PropTypes, string } from 'prop-types';

function DeleteConfirmation({ page, show, onCancel, onConfirm }) {
  if (!show) return null;

  return page !== '댓글' ? (
    <div className="z-10 rounded-lg absolute top-0 w-full h-[10rem] mt-1">
      <div className="bg-blue50 [backdrop-filter:blur(70px)] gap-3 flex flex-col justify-center items-center w-fill h-full m-2 rounded-xl">
        <div className="text-xl font-pretendard font-bold">
          이 게시물을 삭제하시겠습니까?
        </div>
        <div className="flex gap-3">
          <button
            type="submit"
            onClick={onCancel}
            className="cursor-pointer rounded-full whitespace-nowrap w-[1.2rem] h-[0rem] text-dodgerblue bg-white border-solid border-[2px] border-dodgerblue hover:bg-dodgerblue hover:text-white flex py-[0.8rem] px-[1.4rem] items-center justify-center"
          >
            <div className="font-base font-pretendard font-bold">취소</div>
          </button>
          <button
            type="submit"
            onClick={onConfirm}
            className="cursor-pointer rounded-full whitespace-nowrap w-[1.2rem] h-[0rem] text-red bg-white border-solid border-[2px] border-red hover:bg-red hover:text-white flex py-[0.8rem] px-[1.4rem] items-center justify-center"
          >
            <div className="font-base font-pretendard font-bold">삭제</div>
          </button>
        </div>
      </div>
    </div>
  ) : (
    <div className="rounded-lg absolute top-0 w-full h-full">
      <div className="bg-blue50 [backdrop-filter:blur(40px)] gap-3 flex flex-row justify-between items-center w-fill h-full px-5 rounded-xl">
        <div className="text-lh font-pretendard font-bold">
          이 댓글을 삭제하시겠습니까?
        </div>
        <div className="flex gap-3">
          <button
            type="submit"
            onClick={onCancel}
            className="cursor-pointer rounded-full whitespace-nowrap w-[1.2rem] h-[0rem] text-dodgerblue bg-white border-solid border-[2px] border-dodgerblue hover:bg-dodgerblue hover:text-white flex py-[0.8rem] px-[1.4rem] items-center justify-center"
          >
            <div className="font-base font-pretendard font-bold">취소</div>
          </button>
          <button
            type="submit"
            onClick={onConfirm}
            className="cursor-pointer rounded-full whitespace-nowrap w-[1.2rem] h-[0rem] text-red bg-white border-solid border-[2px] border-red hover:bg-red hover:text-white flex py-[0.8rem] px-[1.4rem] items-center justify-center"
          >
            <div className="font-base font-pretendard font-bold">삭제</div>
          </button>
        </div>
      </div>
    </div>
  );
}

DeleteConfirmation.propTypes = {
  page: string,
  show: bool,
  onCancel: PropTypes.func.isRequired,
  onConfirm: PropTypes.func.isRequired,
};

export default DeleteConfirmation;
