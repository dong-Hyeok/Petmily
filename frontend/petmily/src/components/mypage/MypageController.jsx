import { string } from 'prop-types';
import RenderBookmarks from 'components/mypage/RenderBookmarks';
import RenderPosts from 'components/mypage/RenderPosts';

const showItems = category => {
  switch (category) {
    case '게시글':
      return <RenderPosts page="my" />;
    case '북마크':
      return <RenderBookmarks />;

    case '좋아요':
      return <RenderPosts page="like" />;
    default:
      return null;
  }
};

function MypageController({ category }) {
  return <>{showItems(category)}</>;
}
MypageController.propTypes = {
  category: string,
};
export default MypageController;
