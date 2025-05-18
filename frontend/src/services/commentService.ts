import api from './api';

export interface CommentDTO{
    id: number;
    game: string;
    username: string;
    comment: string;
    commentedAt: string;
}


export interface AddCommentRequest {
    game: string;
    username: string;
    comment: string;
}

export const addComment = async (comment: AddCommentRequest): Promise<CommentDTO> => {
    //post request to /api/connect4/comment
    const response = await api.post<CommentDTO>('/comment', comment);
    return response.data;
}
export const getComments = async (game: string): Promise<CommentDTO[]> => {
    //get request to /api/connect4/comment
    const response = await api.get<CommentDTO[]>(`/comment`,{
        params: { game }
    }); 
    return response.data;
}