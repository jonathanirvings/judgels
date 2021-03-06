import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import { PutCourseChapter, DelCourseChapter } from './courseChapterReducer';

export const courseChapterActions = {
  getChapters: (courseJid: string) => {
    return async (dispatch, getState, { courseChapterAPI }) => {
      const token = selectToken(getState());
      return await courseChapterAPI.getChapters(token, courseJid);
    };
  },

  getChapter: (courseJid: string, chapterAlias: string) => {
    return async (dispatch, getState, { courseChapterAPI }) => {
      const token = selectToken(getState());
      const chapter = await courseChapterAPI.getChapter(token, courseJid, chapterAlias);
      dispatch(
        PutCourseChapter.create({
          value: { alias: chapterAlias, chapterJid: chapter.jid },
          name: chapter.name,
        })
      );
      return chapter;
    };
  },

  clearChapter: DelCourseChapter.create,
};
