import React, { useState, useEffect, useCallback } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { StarIcon, Trash2Icon } from 'lucide-react'; // Assuming StarIcon is also used for page title
import { Button } from '@/components/ui/button';
import { useToast } from '@/components/ui/use-toast';
import {
    getAverageRating,
    setRating,
    getMyRating,
    deleteUserRating,
    AverageRatingDTO,
    SetRatingRequest
} from '@/services/ratingService';
import { useAuth } from '@/context/AuthContext';
import InteractiveStarRating from '@/components/InteractiveStarRating';

const GAME_NAME_FOR_RATINGS = "connect4"; 

const renderFractionalStarsDisplay = (
  rating: number,
  totalStars: number = 5,
  starSize: number = 20,
  activeStarColor: string = 'fill-yellow-400 text-yellow-400',
  inactiveStarColor: string = 'text-gray-400'
) => {
  const stars = [];
  for (let i = 0; i < totalStars; i++) {
      const fillPercentage = Math.max(0, Math.min(1, rating - i));
      stars.push(
          <div
              key={`display-star-${i}`}
              className="relative inline-block"
              style={{ width: starSize, height: starSize }}
          >
              <StarIcon
                  className={`absolute top-0 left-0 ${inactiveStarColor}`}
                  style={{ width: starSize, height: starSize }}
              />
              {fillPercentage > 0 && (
                  <div
                      className="absolute top-0 left-0 h-full overflow-hidden"
                      style={{ width: `${fillPercentage * 100}%` }}
                  >
                      <StarIcon
                          className={`block ${activeStarColor}`}
                          style={{ width: starSize, height: starSize }}
                      />
                  </div>
              )}
          </div>
      );
  }
  return stars;
};


const Ratings: React.FC = () => {
    const { toast } = useToast();
    const { isAuthenticated: isUserLoggedIn } = useAuth();

    const [averageRatingInfo, setAverageRatingInfo] = useState<AverageRatingDTO | null>(null);
    const [userRating, setUserRating] = useState<number | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

    const fetchAverageRatingData = useCallback(async () => {
        try {
            const avgData = await getAverageRating(GAME_NAME_FOR_RATINGS);
            setAverageRatingInfo(avgData);
        } catch (error) {
            console.error("Error fetching average rating:", error);
            toast({
                title: "Error",
                description: "Could not load the average rating.",
                variant: "destructive",
            });
        }
    }, [toast]);

    const fetchUserRatingData = useCallback(async () => {
        if (!isUserLoggedIn) return;
        try {
            const myRatingData = await getMyRating(GAME_NAME_FOR_RATINGS);
            setUserRating(myRatingData.rating);
        } catch (error: any) {
            if (error.response && error.response.status === 404) {
                setUserRating(null);
            } else {
                console.error("Error fetching user rating:", error);
                toast({
                    title: "Error",
                    description: "Could not load your rating.",
                    variant: "destructive",
                });
            }
        }
    }, [isUserLoggedIn, toast]);

    useEffect(() => {
        setIsLoading(true);
        const fetchData = async () => {
            await fetchAverageRatingData();
            if (isUserLoggedIn) {
                await fetchUserRatingData();
            }
            setIsLoading(false);
        };
        fetchData();
    }, [fetchAverageRatingData, fetchUserRatingData, isUserLoggedIn]);

    const handleSetOrUpdateRating = async (ratingValue: number) => {
        if (!isUserLoggedIn) {
            toast({
                title: "Authentication Required",
                description: "Please log in to submit your rating.",
                variant: "default",
            });
            return;
        }
        if (ratingValue < 0.01 || ratingValue > 5) { // precision 0.01 allows very small values
            toast({
                title: "Invalid Rating",
                description: "Rating must be between 0.01 and 5 stars.",
                variant: "destructive",
            });
            return;
        }

        setIsSubmitting(true);
        try {
            const request: SetRatingRequest = { game: GAME_NAME_FOR_RATINGS, rating: ratingValue };
            const updatedAverageInfo = await setRating(request);
            setUserRating(ratingValue);
            setAverageRatingInfo(updatedAverageInfo);
            toast({
                title: "Rating Submitted",
                description: `You rated ${ratingValue.toFixed(2)} out of 5 stars.`,
            });
        } catch (error) {
            console.error("Error setting rating:", error);
            toast({
                title: "Error",
                description: "Could not submit your rating.",
                variant: "destructive",
            });
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleDeleteUserRating = async () => {
        if (!isUserLoggedIn || userRating === null) return;
        setIsSubmitting(true);
        try {
            await deleteUserRating(GAME_NAME_FOR_RATINGS);
            setUserRating(null);
            await fetchAverageRatingData();
            toast({
                title: "Rating Deleted",
                description: "Your rating has been successfully removed.",
            });
        } catch (error) {
            console.error("Error deleting rating:", error);
            toast({
                title: "Error",
                description: "Could not delete your rating.",
                variant: "destructive",
            });
        } finally {
            setIsSubmitting(false);
        }
    };

    if (isLoading) {
        return (
            <div className="min-h-[calc(100vh-5rem)] p-4 bg-gray-900 text-white flex justify-center items-center">
                Loading ratings...
            </div>
        );
    }

    return (
        <div className="min-h-[calc(100vh-5rem)] p-4 text-white">
            <div className="connect4-container max-w-lg mx-auto"> 
                
                <div className="flex flex-col md:flex-row justify-between items-center mb-6 md:mb-8">
                    <h1 className="text-3xl font-bold text-white flex items-center">
                        <StarIcon className="mr-3 h-8 w-8 text-yellow-400 fill-yellow-400" /> 
                        Game Ratings
                    </h1>
                </div>

                {/* Card styled similarly to Leaderboard's card */}
                <Card className="bg-сonnect4-blue border-gray-700 shadow-xl overflow-hidden"> 
                    <CardHeader className="pb-4"> 
                        <CardTitle className="text-xl font-semibold text-white">
                            Share Your Opinion on "{GAME_NAME_FOR_RATINGS}"
                        </CardTitle>
                        <CardDescription className="text-gray-400 pt-1"> 
                            {averageRatingInfo && averageRatingInfo.ratingCount > 0
                                ? `Community average based on ${averageRatingInfo.ratingCount} ratings.`
                                : 'Be the first to rate this game!'}
                        </CardDescription>
                    </CardHeader>
                    <CardContent className="pt-4 space-y-6"> 
                        <div>
                            <h3 className="text-lg font-medium mb-2 text-gray-300">Average Community Rating:</h3> 
                            {averageRatingInfo && averageRatingInfo.ratingCount > 0 ? (
                                <div className="flex items-center space-x-1">
                                    {renderFractionalStarsDisplay(averageRatingInfo.averageRating, 5, 22)} 
                                    <span className="ml-2 text-white font-semibold text-lg"> 
                                        {averageRatingInfo.averageRating.toFixed(2)}
                                    </span>
                                    <span className="ml-1.5 text-gray-400 text-sm">
                                        ({averageRatingInfo.ratingCount} {averageRatingInfo.ratingCount === 1 ? 'rating' : 'ratings'})
                                    </span>
                                </div>
                            ) : (
                                <p className="text-gray-500 italic">No average rating data available yet.</p>
                            )}
                        </div>

                        <hr className="border-gray-600" />

                        {isUserLoggedIn ? (
                            <div>
                                <h3 className="text-lg font-medium mb-3 text-gray-300">
                                    {userRating !== null ? 'Your Current Rating:' : 'Rate This Game:'}
                                </h3>
                                {userRating !== null && (
                                     <div className="flex items-center space-x-1 mb-4"> 
                                        {renderFractionalStarsDisplay(userRating, 5, 26)} 
                                        <span className="ml-2 text-white font-semibold text-lg">{userRating.toFixed(2)}</span>
                                    </div>
                                )}
                                <InteractiveStarRating
                                    currentRating={userRating}
                                    onRatingChange={handleSetOrUpdateRating}
                                    precision={0.01} 
                                    starSize={36}
                                    disabled={isSubmitting}
                                    minRating={1} 
                                    // activeStarColor="fill-yellow-400 text-yellow-400"
                                    // inactiveStarColor="text-gray-500"
                                />
                                
                                {userRating !== null && (
                                    <Button
                                        variant="ghost"
                                        size="sm"
                                        onClick={handleDeleteUserRating}
                                        disabled={isSubmitting}
                                        className="mt-5 text-red-500 hover:bg-red-500 hover:text-white flex items-center px-3 py-2" 
                                    >
                                        <Trash2Icon className="h-4 w-4 mr-2" />
                                        Delete My Rating
                                    </Button>
                                )}
                                {isSubmitting && <p className="text-sm text-yellow-300 mt-3">Processing...</p>}
                            </div>
                        ) : (
                            <div className="p-6 border border-dashed border-gray-600 rounded-lg bg-slate-700/30 text-center"> 
                                <p className="text-gray-300">
                                    Please{' '}
                                    <a href="/login" className="font-semibold text-yellow-400 hover:text-yellow-300 underline">
                                        log in
                                    </a>
                                    {' '}to submit or edit your rating.
                                </p>
                            </div>
                        )}
                    </CardContent>
                </Card>
            </div>
        </div>
    );
};

export default Ratings;
