import React, { useState } from 'react';
import { useAuth } from "@/context/AuthContext";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import { useToast } from "@/components/ui/use-toast";
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { getComments, addComment, type CommentDTO, type AddCommentRequest } from '@/services/commentService';
import { Loader2 } from 'lucide-react';
const GAME_NAME = 'connect4';

const Comments = () => {
    const { isAuthenticated, user } = useAuth();
    const { toast } = useToast();
    const queryClient = useQueryClient();
    const [commentText, setCommentText] = useState('');

    const {
        data: comments,
        isLoading: isLoadingComments,
        isError: isErrorComments,
        error: commentsError,
    } = useQuery<CommentDTO[], Error>({
        queryKey: ['comments', GAME_NAME],
        queryFn: () => getComments(GAME_NAME),
    });

    const addCommentMutation = useMutation<CommentDTO, Error, AddCommentRequest>({
        mutationFn: addComment,
        onSuccess: (newComment) => {
            toast({ title: "Success", description: "Comment added!" });
            setCommentText('');
            queryClient.invalidateQueries({ queryKey: ['comments', GAME_NAME] });
        },
        onError: (error) => {
            toast({
                variant: "destructive",
                title: "Error adding comment",
                description: error.message || "Something went wrong",
            });
        },
    });

    const handleAddComment = () => {
        if (!commentText.trim()) {
            toast({ variant: "destructive", title: "Error", description: "Comment cannot be empty." });
            return;
        }
        if (!user?.username) {
            toast({ variant: "destructive", title: "Error", description: "User not found. Please log in again." });
            return;
        }

        const newCommentData: AddCommentRequest = {
            game: GAME_NAME,
            username: user.username,
            comment: commentText.trim(),
        };

        addCommentMutation.mutate(newCommentData);
    };

    return (
        <>
            <div className="container mx-auto p-4">
                <h1 className="text-3xl font-bold text-white mb-8">Comments for {GAME_NAME}</h1>

                {isAuthenticated && user ? (
                    <Card className="bg-connect4-blue border-gray-700 mb-8">
                        <CardHeader>
                            <CardTitle className="text-white">Add a Comment</CardTitle>
                            <CardDescription className="text-gray-400">Share your thoughts, {user.username}!</CardDescription>
                        </CardHeader>
                        <CardContent className="space-y-2">
                             <Textarea
                                 placeholder="Type your comment..."
                                 value={commentText}
                                 onChange={(e) => setCommentText(e.target.value)}
                                 className="bg-gray-800 border-gray-700 text-white"
                                 rows={3}
                                 disabled={addCommentMutation.isPending}
                             />
                             <Button
                                onClick={handleAddComment}
                                className="bg-connect4-yellow text-connect4-dark hover:bg-yellow-500"
                                disabled={addCommentMutation.isPending}
                             >
                                {addCommentMutation.isPending ? (
                                    <><Loader2 className="mr-2 h-4 w-4 animate-spin" /> Posting...</>
                                ) : (
                                    'Post Comment'
                                )}
                             </Button>
                        </CardContent>
                    </Card>
                ) : (
                    <p className="text-gray-400 mb-8">Log in to add comments.</p>
                )}

                 <div className="space-y-4">
                    <h2 className="text-xl text-white font-semibold">Existing Comments:</h2>
                    {isLoadingComments && (
                        <div className="flex justify-center items-center p-8">
                            <Loader2 className="h-8 w-8 animate-spin text-white"/>
                        </div>
                    )}
                    {isErrorComments && (
                        <p className="text-red-500">Error loading comments: {commentsError?.message}</p>
                    )}
                    {!isLoadingComments && !isErrorComments && (!comments || comments.length === 0) && (
                        <p className="text-gray-500">No comments yet.</p>
                    )}
                    {!isLoadingComments && !isErrorComments && comments && comments.length > 0 && (
                         comments.map(comment => (
                             <Card key={comment.id} className="bg-gray-800 border-gray-700 text-white p-4">
                                 <p className="font-bold">{comment.username}</p>
                                 <p className="text-sm text-gray-300 mb-2">{new Date(comment.commentedAt).toLocaleString()}</p>
                                 <p className="whitespace-pre-wrap">{comment.comment}</p>
                             </Card>
                         ))
                    )}
                </div>
            </div>
        </>
    );
};

export default Comments;