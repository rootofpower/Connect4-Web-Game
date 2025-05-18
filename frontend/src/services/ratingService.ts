import api from './api'; // Ваш існуючий API клієнт

export interface AverageRatingDTO {
    game: string;
    averageRating: number;
    ratingCount: number;
}

export interface MyRatingResponse {
    rating: number;
}

export interface SetRatingRequest {
    game: string;
    rating: number; 
}


export const getAverageRating = async (game: string): Promise<AverageRatingDTO> => {
    const response = await api.get<AverageRatingDTO>(`/rating/average`, {
        params: { game }
    });
    return response.data;
};

export const getMyRating = async (game: string): Promise<MyRatingResponse> => {
    const response = await api.get<MyRatingResponse>(`/rating/my-rating`, {
        params: { game }
    });
    return response.data; 
};

export const setRating = async (request: SetRatingRequest): Promise<AverageRatingDTO> => {
    const response = await api.post<AverageRatingDTO>('/rating', request);
    return response.data;
};


export const deleteUserRating = async (game: string): Promise<void> => {
    await api.delete(`/rating`, {
        params: { game }
    });
};